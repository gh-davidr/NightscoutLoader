package davidRichardson;

import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
//import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import davidRichardson.ThreadAnalyzer.AnalyzerCompleteHander;

// Core class with logic to handle data loading, synchronisation and analaysis
public class CoreNightScoutLoader 
{
	// Implements the Singleton Design Pattern
	private static CoreNightScoutLoader m_Instance=null;

	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	public static CoreNightScoutLoader getInstance()
	{
		if (m_Instance == null)
		{
			m_Instance = new CoreNightScoutLoader();
		}
		return m_Instance;
	}

	// Main Data Loaders
	private DataLoadRoche               m_DataLoadRoche;
	private DataLoadRocheCSV            m_DataLoadRocheCSV;
	private DataLoadMedtronic           m_DataLoadMedtronic;
	private DataLoadNightScoutTreatments          m_DataLoadNightScout;
	private DataLoadNightScoutEntries   m_DataLoadNightScoutEntries;
	private DataLoadDiasend             m_DataLoadDiasend;
	private DataLoadOmniPod             m_DataLoadOmniPod;
	private DataLoadTandem              m_DataLoadTandem;

	// Hold the list of objects back from SQL Server
	private ArrayList <DBResult>      m_MeterArrayListDBResults;
	private ArrayList <DBResult>      m_NightScoutArrayListDBResults;
	private ArrayList <DBResultEntry> m_NightScoutArrayListDBResultEntries;
	private Set<DBResult>             m_MeterArrayListDBResultsSet;
	private Set<DBResult>             m_NightScoutArrayListDBResultsSet;

	// Result Analyzer that requires more work :-)
	// In this class so we can track messages back to core.
	//	private Analyzer             m_Analyzer;

	// Introduced Sep 2016 to allow for comparative trends
	// So as analyzer runs for a recent period, it can now compare results
	// across date range all the way back for even more insights
	private Analyzer             m_FullHistoryAnalyzer = null;


	// String for status updates controlled by Core
	private String               m_StatusText; 

	// Ooerr - try some multi-threading!!
	private ThreadDataLoad       m_ThreadDataLoadRoche;
	private ThreadDataLoad       m_ThreadDataLoadRocheCSV;
	private ThreadDataLoad       m_ThreadDataLoadMedtronic;
	private ThreadDataLoad       m_ThreadDataLoadDiasend;
	private ThreadDataLoad       m_ThreadDataLoadOmniPod;
	private ThreadDataLoad       m_ThreadDataLoadTandem;
	private ThreadDataLoad       m_ThreadDataLoadNightScout;
	private ThreadDataLoad       m_ThreadDataLoadNightScoutEntries;
	private ThreadDataLoad       m_ThreadDataMeterLoad; // Which thread is actually in use
	private boolean              m_MeterPumpLoadOnly = false;

	private ThreadDetermineSaveDifferences m_ThreadDetermineSaveDifferences;

	// Normal and Summarised share a thread analyzer
	private ThreadAnalyzer       m_ThreadAnalyzer;

	// Full range has its own thread
	private ThreadAnalyzer       m_FullHistoryThreadAnalyzer;

	// Keep track of these as threads are invoked so determinsavediff can be informed
	private String               m_DeviceUsed;
	private String               m_FileName;
	private String               m_DateRange;

	private ThreadDataLoad.DataLoadCompleteHandler m_ThreadHandlerRoche;
	private ThreadDataLoad.DataLoadCompleteHandler m_ThreadHandlerRocheCSV;
	private ThreadDataLoad.DataLoadCompleteHandler m_ThreadHandlerMedtronic;
	private ThreadDataLoad.DataLoadCompleteHandler m_ThreadHandlerDiasend;
	private ThreadDataLoad.DataLoadCompleteHandler m_ThreadHandlerOmniPod;
	private ThreadDataLoad.DataLoadCompleteHandler m_ThreadHandlerTandem;
	private ThreadDataLoad.DataLoadCompleteHandler m_ThreadHandlerNightscout;
	private ThreadDataLoad.DataLoadCompleteHandler m_ThreadHandlerNightscoutEntries;
	private ThreadDetermineSaveDifferences.DataLoadCompleteHander m_ThreadHandlerDetermineSaveDifferences;
	private ThreadAnalyzer.AnalyzerCompleteHander m_ThreadHandlerAnalyzer;
	private ThreadAnalyzer.AnalyzerCompleteHander m_FullHistoryThreadHandlerAnalyzer;

	// A couple extra threads that run all the time
	private ThreadMongoDBAlerterEntries    m_EntriesAlerter;
	private ThreadMongoDBAlerterTreatments m_TreatmentsAlerter;

	//	private String                         m_AnalyzerMessage;

	private Object                         m_Lock;

	// Prevent instantiation
	private CoreNightScoutLoader()
	{
		m_DataLoadRoche             = new DataLoadRoche();
		m_DataLoadRocheCSV          = new DataLoadRocheCSV();
		m_DataLoadMedtronic         = new DataLoadMedtronic();
		m_DataLoadNightScout        = new DataLoadNightScoutTreatments();
		m_DataLoadNightScoutEntries = new DataLoadNightScoutEntries();
		m_DataLoadDiasend           = new DataLoadDiasend();   
		m_DataLoadOmniPod           = new DataLoadOmniPod();
		m_DataLoadTandem            = new DataLoadTandem();

		m_MeterArrayListDBResults    = m_DataLoadRoche.getResultsTreatments();
		m_NightScoutArrayListDBResults       = m_DataLoadNightScout.getResultsFromDB();
		m_NightScoutArrayListDBResultEntries = m_DataLoadNightScoutEntries.getResultsFromDB();

		m_MeterArrayListDBResultsSet = new HashSet<DBResult>();
		m_NightScoutArrayListDBResultsSet   = new HashSet<DBResult>();

		//		m_Analyzer = new Analyzer(m_NightScoutArrayListDBResults);


		m_StatusText = new String();

		// Use Synchronized methods
		this.setM_ThreadDataLoadMedtronic(null);
		this.setM_ThreadDataLoadNightScout(null);
		this.setM_ThreadDataLoadNightScoutEntries(null);
		this.setM_ThreadDataMeterLoad(null);
		this.setM_ThreadDataLoadRoche(null);
		this.setM_ThreadDetermineSaveDifferences(null);
		this.setM_ThreadAnalyzer(null);
		this.setM_FullHistoryThreadAnalyzer(null);

		m_DeviceUsed   = new String();
		m_FileName     = new String();
		m_DateRange    = new String();

		m_ThreadHandlerRoche      = null;
		m_ThreadHandlerRocheCSV   = null;
		m_ThreadHandlerMedtronic  = null;
		m_ThreadHandlerDiasend    = null;
		m_ThreadHandlerOmniPod    = null;
		m_ThreadHandlerTandem     = null;
		m_ThreadHandlerNightscout = null;
		m_ThreadHandlerNightscoutEntries = null;
		m_ThreadHandlerDetermineSaveDifferences = null;
		m_ThreadHandlerAnalyzer   = null;
		m_FullHistoryThreadHandlerAnalyzer = null;

		m_EntriesAlerter    = new ThreadMongoDBAlerterEntries();
		m_TreatmentsAlerter = new ThreadMongoDBAlerterTreatments();

		// Thread synchronization
		m_Lock            = new Object();

		// Start them both
		m_EntriesAlerter.startThread();
		m_TreatmentsAlerter.startThread();

		//		m_AnalyzerMessage = new String();
	}

	// David - 2 Dec 2016
	// Sensor load and analysis
	public ArrayList <DBResultEntry> loadSensorResults()
	{
		ArrayList <DBResultEntry> result = null;

		try 
		{
			m_DataLoadNightScoutEntries.loadDBResults();

			result = m_DataLoadNightScoutEntries.getResultsFromDB();
		} 
		catch (UnknownHostException e) 
		{
			m_Logger.log(Level.SEVERE, "Exception caught loadSensorResults(): " + e.getMessage());
		}

		return result;
	}

	// Multi-threaded Nightscout Loader
	public synchronized void threadDetermineSaveDifferences(ThreadDetermineSaveDifferences.DataLoadCompleteHander handler)
	{
		if (this.getM_ThreadDetermineSaveDifferences() != null)
		{
			// Need better way than this!
			addErrorText("threadDetermineSaveDifferences Thread Already Running!!");
		}
		else
		{
			setM_ThreadDetermineSaveDifferences(
					new ThreadDetermineSaveDifferences(
							m_DataLoadNightScout,
							m_MeterArrayListDBResults,
							m_NightScoutArrayListDBResults,
							m_ThreadDataLoadNightScout,
							m_ThreadDataMeterLoad,
							m_DeviceUsed,
							m_FileName,
							m_DateRange));


			// Store supplied handler
			m_ThreadHandlerDetermineSaveDifferences = handler;

			// Install our own
			// m_ThreadDataLoadNightScout.loadDBResults(handler);

			m_ThreadDetermineSaveDifferences.determineSaveDifferences(
					new ThreadDetermineSaveDifferences.DataLoadCompleteHander(handler.getM_Object()) 
					{
						int entriesLoaded      = 0; // m_ThreadDetermineSaveDifferences.getM_CountMeterEntriesLoaded();
						int meterEntriesDuplicated  = 0; // m_ThreadDetermineSaveDifferences.getM_CountMeterEntriesDuplicated();
						int entriesAdded       = 0; // m_ThreadDetermineSaveDifferences.getM_CountMeterEntriesAdded();
						//					int nsEntriesBefore    = 0; // m_ThreadDetermineSaveDifferences.getM_CountNightScoutEntriesBefore();
						int nsEntriesAfter     = 0; // m_ThreadDetermineSaveDifferences.getM_CountNightScoutEntriesAfter();
						int meterProximityEntries   = 0;
						int nsProximityEntries   = 0;

						//		@Override
						public void exceptionRaised(String message) 
						{
							setM_ThreadDetermineSaveDifferences(null);
						}

						//		@Override
						public void operationComplete(Object obj, String message) 
						{
							entriesLoaded      = m_ThreadDetermineSaveDifferences.getM_CountMeterEntriesLoaded();
							entriesAdded       = m_ThreadDetermineSaveDifferences.getM_CountMeterEntriesAdded();
							//						nsEntriesBefore    = m_ThreadDetermineSaveDifferences.getM_CountNightScoutEntriesBefore();
							nsEntriesAfter     = m_ThreadDetermineSaveDifferences.getM_CountNightScoutEntriesAfter();
							meterProximityEntries   = m_ThreadDetermineSaveDifferences.getM_CountProximityMeterEntries();
							nsProximityEntries   = m_ThreadDetermineSaveDifferences.getM_CountProximityNightScoutEntries();

							String statusText = new String();

							statusText = String.format("Synchronize %s\n%5d meter/pump entries read.%4d new entries added.%4d ignored.%5d Treatments in NightScout. ", 
									m_DeviceUsed, entriesLoaded, entriesAdded, meterEntriesDuplicated, nsEntriesAfter);
							if (meterProximityEntries > 0)
							{
								statusText += "\n\n";
								statusText += String.format("Note that %d possible duplicate " 
										+ (meterProximityEntries == 1 ? "entry was loaded. This is marked orange" 
												: "entries were loaded. These are marked orange"), meterProximityEntries);
							}
							else if (nsProximityEntries > 0)
							{
								statusText += "\n\n";
								statusText += String.format("Note that %d possible duplicate " 
										+ (nsProximityEntries == 1 ? "entry was found already loaded. This is marked orange" 
												: "entries were found already loaded. These are marked orange"), nsProximityEntries);
							}

							// We want to do this UI change in the main thread, and not the DB worker thread that's just
							// notified back
							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									String statusText = new String();

									statusText = String.format("Synchronize %s\n%5d meter/pump entries read.%4d new entries added.%4d ignored.%5d Treatments in NightScout. ", 
											m_DeviceUsed, entriesLoaded, entriesAdded, meterEntriesDuplicated, nsEntriesAfter);

									//									statusText = String.format("%5d meter/pump entries read.%4d new entries added.%4d ignored.%5d Treatments in NightScout. ", 
									//											entriesLoaded, entriesAdded, meterEntriesDuplicated, nsEntriesAfter);
									// Add some commentary to the text pane
									addStatusTextWithTime(statusText);
								}
							});

							// Invoke our stored handler
							m_ThreadHandlerDetermineSaveDifferences.operationComplete(obj, statusText);

							// Now clear the threads							
							setM_ThreadDetermineSaveDifferences(null);
							setM_ThreadDataLoadNightScout(null);
							setM_ThreadDataMeterLoad(null);
						}
					});	
		}
	}

	// Multi-threaded Nightscout Loader
	public synchronized void threadLoadRocheMeterPump(Date startDate, Date endDate,
			ThreadDataLoad.DataLoadCompleteHandler handler)
	{
		if (this.getM_ThreadDataMeterLoad() != null)
		{
			// Need better way than this!
			addErrorText("threadLoadRocheMeterPump Thread Already Running!!");
		}
		else
		{
			m_DeviceUsed   = "Roche Meter/Pump";
			m_FileName     = "";
			m_DateRange    = startDate.toString() + " to " + endDate.toString();

			// Initialize the Roche loader with supplied dates
			m_DataLoadRoche.initialize(startDate, endDate);

			this.setM_ThreadDataLoadRoche(new ThreadDataLoad(m_DataLoadRoche));
			this.setM_ThreadDataMeterLoad(this.getM_ThreadDataLoadRoche());

			// Store supplied handler
			m_ThreadHandlerRoche = handler;

			// Install our own
			// m_ThreadDataLoadNightScout.loadDBResults(handler);

			m_ThreadDataLoadRoche.loadDBResults(
					new ThreadDataLoad.DataLoadCompleteHandler(handler.getM_Object()) 
					{
						//		@Override
						public void exceptionRaised(String message) 
						{
							// Now clear the thread
							setM_ThreadDataLoadRoche(null);
							setM_ThreadDataMeterLoad(null);
						}

						//		@Override
						public void dataLoadComplete(Object obj, String message) 
						{
							m_MeterArrayListDBResults = m_DataLoadRoche.getResultsTreatments();

							// Sort the Mongo Results
							Collections.sort(m_MeterArrayListDBResults, new ResultFromDBComparator());

							String statusText = new String();

							statusText = String.format("Load %s\n%5d meter/pump entries read. ", 
									m_DeviceUsed, m_MeterArrayListDBResults.size());

							// We want to do this UI change in the main thread, and not the DB worker thread that's just
							// notified back
							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									// Add some commentary to the text pane
									//									addStatusText("Loaded " + m_MeterArrayListDBResults.size() + " entries from Meter.");
								}
							});

							// Check whether this meter/pump load does not include Nightscout load too
							checkMeterPumpOnlyLoad();

							// Invoke our stored handler
							m_ThreadHandlerRoche.dataLoadComplete(obj, statusText);

							// Now clear the thread
							setM_ThreadDataLoadRoche(null);
							setM_ThreadDataMeterLoad(null);
						}
					});	
		}
	}


	// Multi-threaded Roche File Loader
	public synchronized void threadLoadRocheMeterPump(String filename,
			ThreadDataLoad.DataLoadCompleteHandler handler)
	{
		if (this.getM_ThreadDataMeterLoad() != null)
		{
			// Need better way than this!
			addErrorText("threadLoadRocheMeterPump Thread Already Running!!");
		}
		else
		{

			m_DeviceUsed   = "Roche Meter/Pump";
			m_FileName     = filename;
			m_DateRange    = "";

			this.setM_ThreadDataLoadRocheCSV(new ThreadDataLoad(m_DataLoadRocheCSV));
			this.setM_ThreadDataMeterLoad(this.getM_ThreadDataLoadRocheCSV());

			// Store supplied handler
			m_ThreadHandlerRocheCSV = handler;

			// Initialize the Medtronic loader with supplied dates
			m_DataLoadRocheCSV.initialize(filename);

			// Install our own
			// m_ThreadDataLoadNightScout.loadDBResults(handler);

			m_ThreadDataLoadRocheCSV.loadDBResults(
					new ThreadDataLoad.DataLoadCompleteHandler(handler.getM_Object()) 
					{
						//		@Override
						public void exceptionRaised(String message) 
						{
							setM_ThreadDataLoadRocheCSV(null);
							setM_ThreadDataMeterLoad(null);
						}

						//		@Override
						public void dataLoadComplete(Object obj, String message) 
						{
							m_MeterArrayListDBResults = new ArrayList<DBResult>(m_DataLoadRocheCSV.getResultsTreatments());

							// Sort the Mongo Results
							Collections.sort(m_MeterArrayListDBResults, new ResultFromDBComparator());

							String statusText = new String("");
							statusText = String.format("Load %s\n%5d meter/pump entries read. ", 
									m_DeviceUsed, m_MeterArrayListDBResults.size());

							// We want to do this UI change in the main thread, and not the DB worker thread that's just
							// notified back
							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									// Add some commentary to the text pane
									//									addStatusText("Loaded " + m_MeterArrayListDBResults.size() + " entries from Meter.");
								}
							});

							// Check whether this meter/pump load does not include Nightscout load too
							checkMeterPumpOnlyLoad();

							// Invoke our stored handler
							m_ThreadHandlerRocheCSV.dataLoadComplete(obj, statusText);

							// Now clear the thread
							setM_ThreadDataLoadRocheCSV(null);
							setM_ThreadDataMeterLoad(null);
						}
					});	
		}
	}



	private void checkMeterPumpOnlyLoad()
	{
		if (this.isM_MeterPumpLoadOnly() == true)
		{
			// We need to clone the meter results to make it seem like Nightscout load has completed also
			this.m_NightScoutArrayListDBResults = new ArrayList<DBResult>(m_MeterArrayListDBResults);

			this.m_DataLoadNightScout.cloneMeterPumpOnlyResults(m_MeterArrayListDBResults);
		}
	}

	public Boolean isLoadOrDiffThreadRunning()
	{
		Boolean result = false; // Assume no to begin with

		// Slight hit checking all, but no great shakes
		result = (this.getM_ThreadDataLoadMedtronic()         != null) ? true : result;
		result = (this.getM_ThreadDataLoadDiasend()           != null) ? true : result;
		result = (this.getM_ThreadDataLoadNightScout()        != null) ? true : result;
		result = (this.getM_ThreadDataLoadRoche()             != null) ? true : result;
		result = (this.getM_ThreadDataLoadOmniPod()           != null) ? true : result;
		result = (this.getM_ThreadDataLoadTandem()            != null) ? true : result;
		result = (this.getM_ThreadDataLoadNightScoutEntries() != null) ? true : result;
		result = (this.getM_ThreadDetermineSaveDifferences()  != null) ? true : result;

		return result;
	}

	public Boolean isAnalyzeThreadRunning()
	{
		Boolean result = false;

		result = (this.getM_ThreadAnalyzer() != null) ? true : result;

		return result;
	}

	public Boolean isFullHistoryAnalyzeThreadRunning()
	{
		Boolean result = false;

		result = (this.getM_FullHistoryThreadAnalyzer() != null) ? true : result;

		return result;
	}


	// Multi-threaded Nightscout Loader
	public synchronized void threadLoadMedtronicMeterPump(String filename,
			ThreadDataLoad.DataLoadCompleteHandler handler)
	{
		if (this.getM_ThreadDataMeterLoad() != null)
		{
			// Need better way than this!
			addErrorText("threadLoadMedtronicMeterPump Thread Already Running!!");
		}
		else
		{
			m_DeviceUsed   = "Medtronic Meter/Pump";
			m_FileName     = filename;
			m_DateRange    = "";

			// Initialize the Medtronic loader with supplied dates
			m_DataLoadMedtronic.initialize(filename);

			this.setM_ThreadDataLoadMedtronic(new ThreadDataLoad(m_DataLoadMedtronic));
			this.setM_ThreadDataMeterLoad(this.getM_ThreadDataLoadMedtronic());

			// Store supplied handler
			m_ThreadHandlerMedtronic = handler;

			// Install our own
			// m_ThreadDataLoadNightScout.loadDBResults(handler);

			m_ThreadDataLoadMedtronic.loadDBResults(
					new ThreadDataLoad.DataLoadCompleteHandler(handler.getM_Object()) 
					{
						//		@Override
						public void exceptionRaised(String message) 
						{
							// Now clear the thread
							setM_ThreadDataLoadMedtronic(null);
							setM_ThreadDataMeterLoad(null);
						}

						//		@Override
						public void dataLoadComplete(Object obj, String message) 
						{
							m_MeterArrayListDBResults = m_DataLoadMedtronic.getResultsTreatments();

							// Sort the Mongo Results
							Collections.sort(m_MeterArrayListDBResults, new ResultFromDBComparator());

							String statusText = new String("");
							statusText = String.format("Load %s\n%5d meter/pump entries read. ", 
									m_DeviceUsed, m_MeterArrayListDBResults.size());

							// We want to do this UI change in the main thread, and not the DB worker thread that's just
							// notified back
							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									// Add some commentary to the text pane
									//									addStatusText("Loaded " + m_MeterArrayListDBResults.size() + " entries from Meter.");
								}
							});

							// Check whether this meter/pump load does not include Nightscout load too
							checkMeterPumpOnlyLoad();

							// Invoke our stored handler
							m_ThreadHandlerMedtronic.dataLoadComplete(obj, statusText);

							// Now clear the thread
							setM_ThreadDataLoadMedtronic(null);
							setM_ThreadDataMeterLoad(null);
						}
					});	
		}
	}

	// Multi-threaded Diasend Loader
	public synchronized void threadLoadDiasendMeterPump(String filename,
			ThreadDataLoad.DataLoadCompleteHandler handler)
	{
		if (this.getM_ThreadDataMeterLoad() != null)
		{
			// Need better way than this!
			addErrorText("threadLoadDiasendMeterPump Thread Already Running!!");
		}
		else
		{
			m_DeviceUsed   = "Diasend Download File";
			m_FileName     = filename;
			m_DateRange    = "";

			// Initialize the Diasend loader with supplied dates
			m_DataLoadDiasend.initialize(filename);

			this.setM_ThreadDataLoadDiasend(new ThreadDataLoad(m_DataLoadDiasend));
			this.setM_ThreadDataMeterLoad(this.getM_ThreadDataLoadDiasend());

			// Store supplied handler
			m_ThreadHandlerDiasend = handler;

			// Install our own
			// m_ThreadDataLoadNightScout.loadDBResults(handler);

			m_ThreadDataLoadDiasend.loadDBResults(
					new ThreadDataLoad.DataLoadCompleteHandler(handler.getM_Object()) 
					{
						//		@Override
						public void exceptionRaised(String message) 
						{
							setM_ThreadDataLoadDiasend(null);
							setM_ThreadDataMeterLoad(null);
						}

						//		@Override
						public void dataLoadComplete(Object obj, String message) 
						{
							m_MeterArrayListDBResults = new ArrayList<DBResult>(m_DataLoadDiasend.getResultsTreatments());

							// Sort the Mongo Results
							Collections.sort(m_MeterArrayListDBResults, new ResultFromDBComparator());

							String statusText = new String("");
							statusText = String.format("Load %s\n%5d meter/pump entries read. ", 
									m_DeviceUsed, m_MeterArrayListDBResults.size());

							// We want to do this UI change in the main thread, and not the DB worker thread that's just
							// notified back
							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									// Add some commentary to the text pane
									//									addStatusText("Loaded " + m_MeterArrayListDBResults.size() + " entries from Meter.");
								}
							});

							// Check whether this meter/pump load does not include Nightscout load too
							checkMeterPumpOnlyLoad();

							// Invoke our stored handler
							m_ThreadHandlerDiasend.dataLoadComplete(obj, statusText);

							// Now clear the thread
							setM_ThreadDataLoadDiasend(null);
							setM_ThreadDataMeterLoad(null);
						}
					});	
		}
	}

	// Multi-threaded OmniPod Loader
	public synchronized void threadLoadOmniPodMeterPump(String filename,
			ThreadDataLoad.DataLoadCompleteHandler handler)
	{
		if (this.getM_ThreadDataMeterLoad() != null)
		{
			// Need better way than this!
			addErrorText("threadLoadOmniPodMeterPump Thread Already Running!!");
		}
		else
		{
			m_DeviceUsed   = "OmniPod Download File";
			m_FileName     = filename;
			m_DateRange    = "";

			// Initialize the OmniPod loader with supplied dates
			m_DataLoadOmniPod.initialize(filename);

			this.setM_ThreadDataLoadOmniPod(new ThreadDataLoad(m_DataLoadOmniPod));
			this.setM_ThreadDataMeterLoad(this.getM_ThreadDataLoadOmniPod());

			// Store supplied handler
			m_ThreadHandlerOmniPod = handler;

			// Install our own
			// m_ThreadDataLoadNightScout.loadDBResults(handler);

			m_ThreadDataLoadOmniPod.loadDBResults(
					new ThreadDataLoad.DataLoadCompleteHandler(handler.getM_Object()) 
					{
						//		@Override
						public void exceptionRaised(String message) 
						{
							setM_ThreadDataLoadOmniPod(null);
							setM_ThreadDataMeterLoad(null);
						}

						//		@Override
						public void dataLoadComplete(Object obj, String message) 
						{
							m_MeterArrayListDBResults = new ArrayList<DBResult>(m_DataLoadOmniPod.getResultsTreatments());

							// Sort the Mongo Results
							Collections.sort(m_MeterArrayListDBResults, new ResultFromDBComparator());

							String statusText = new String("");
							statusText = String.format("Load %s\n%5d meter/pump entries read. ", 
									m_DeviceUsed, m_MeterArrayListDBResults.size());

							// We want to do this UI change in the main thread, and not the DB worker thread that's just
							// notified back
							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									// Add some commentary to the text pane
									//									addStatusText("Loaded " + m_MeterArrayListDBResults.size() + " entries from Meter.");
								}
							});

							// Check whether this meter/pump load does not include Nightscout load too
							checkMeterPumpOnlyLoad();

							// Invoke our stored handler
							m_ThreadHandlerOmniPod.dataLoadComplete(obj, statusText);

							// Now clear the thread
							setM_ThreadDataLoadOmniPod(null);
							setM_ThreadDataMeterLoad(null);
						}
					});	
		}
	}


	// Multi-threaded Tandem Loader
	public synchronized void threadLoadTandemMeterPump(String filename,
			ThreadDataLoad.DataLoadCompleteHandler handler)
	{
		if (this.getM_ThreadDataMeterLoad() != null)
		{
			// Need better way than this!
			addErrorText("threadLoadTandemMeterPump Thread Already Running!!");
		}
		else
		{
			m_DeviceUsed   = "Tandem Download File";
			m_FileName     = filename;
			m_DateRange    = "";

			// Initialize the Tandem loader with supplied dates
			m_DataLoadTandem.initialize(filename);

			this.setM_ThreadDataLoadTandem(new ThreadDataLoad(m_DataLoadTandem));
			this.setM_ThreadDataMeterLoad(this.getM_ThreadDataLoadTandem());

			// Store supplied handler
			m_ThreadHandlerTandem = handler;

			// Install our own
			// m_ThreadDataLoadNightScout.loadDBResults(handler);

			m_ThreadDataLoadTandem.loadDBResults(
					new ThreadDataLoad.DataLoadCompleteHandler(handler.getM_Object()) 
					{
						//		@Override
						public void exceptionRaised(String message) 
						{
							setM_ThreadDataLoadTandem(null);
							setM_ThreadDataMeterLoad(null);
						}

						//		@Override
						public void dataLoadComplete(Object obj, String message) 
						{
							m_MeterArrayListDBResults = new ArrayList<DBResult>(m_DataLoadTandem.getResultsTreatments());

							// Sort the Mongo Results
							Collections.sort(m_MeterArrayListDBResults, new ResultFromDBComparator());

							String statusText = new String("");
							statusText = String.format("Load %s\n%5d meter/pump entries read. ", 
									m_DeviceUsed, m_MeterArrayListDBResults.size());

							// We want to do this UI change in the main thread, and not the DB worker thread that's just
							// notified back
							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									// Add some commentary to the text pane
									//									addStatusText("Loaded " + m_MeterArrayListDBResults.size() + " entries from Meter.");
								}
							});

							// Check whether this meter/pump load does not include Nightscout load too
							checkMeterPumpOnlyLoad();

							// Invoke our stored handler
							m_ThreadHandlerTandem.dataLoadComplete(obj, statusText);

							// Now clear the thread
							setM_ThreadDataLoadTandem(null);
							setM_ThreadDataMeterLoad(null);
						}
					});	
		}
	}


	public void loadRocheMeterPump(Date startDate, Date endDate)
	{
		try
		{
			m_DataLoadRoche.loadDBResults(
					PrefsNightScoutLoader.getInstance().getM_SQLDBServerHost(),
					PrefsNightScoutLoader.getInstance().getM_SQLDBServerInstance(), 
					PrefsNightScoutLoader.getInstance().getM_SQLDBName(), 
					PrefsNightScoutLoader.getInstance().getM_SQLFile(), 
					startDate, endDate);

			m_MeterArrayListDBResults = m_DataLoadRoche.getResultsTreatments();

			// Sort the Meter Results
			Collections.sort(m_MeterArrayListDBResults, new ResultFromDBComparator());

			// Add some commentary to the text pane
			addStatusText("Loaded " + m_MeterArrayListDBResults.size() + " entries from SQL Server.");	    
		}
		catch (Exception e)
		{
			addErrorText("ERROR - Load Roche. Caught Exception " + e.getMessage() + "-" + e.getLocalizedMessage());
		}
	}

	// The return value is earliest date found in the file
	public void loadMedtronicMeterPump(String filename)
	{
		try
		{
			m_DataLoadMedtronic.loadDBResults(filename);
			m_MeterArrayListDBResults = m_DataLoadMedtronic.getResultsTreatments();

			// Sort the Meter Results
			Collections.sort(m_MeterArrayListDBResults, new ResultFromDBComparator());

			// Add some commentary to the text pane
			addStatusText("Loaded " + m_MeterArrayListDBResults.size() + " entries from Medtronic File.");

			// Now set the dates based on what came in on the file
			// Set start & end dates for SQL Query

		}
		catch (Exception e)
		{
			addErrorText("ERROR - Load Medtronic. Caught Exception " + e.getMessage() + "-" + e.getLocalizedMessage());
		}

	}

	// The return value is earliest date found in the file
	public Date loadDiasendMeterPump(String filename)
	{
		Date result = new Date(0);
		try
		{
			// Need to write this class
			// Done :-)
			m_DataLoadDiasend.loadDBResults(filename);
			m_MeterArrayListDBResults = m_DataLoadDiasend.getResultsTreatments();

			// Sort the Meter Results
			Collections.sort(m_MeterArrayListDBResults, new ResultFromDBComparator());

			// Add some commentary to the text pane
			addStatusText("Loaded " + m_MeterArrayListDBResults.size() + " entries from Diasend File.");
		}
		catch (Exception e)
		{
			addErrorText("ERROR - Load Diasend. Caught Exception " + e.getMessage() + "-" + e.getLocalizedMessage());
		}

		return result;
	}


	// Multi-threaded Nightscout Loader
	public synchronized void threadLoadNightScout(ThreadDataLoad.DataLoadCompleteHandler handler)
	{
		if (this.getM_ThreadDataLoadNightScout() != null)
		{
			// Need better way than this!
			addErrorText("threadLoadNightScout Thread Already Running!!");
		}
		else
		{
			// Add some detail on the current timezone
			addStatusText("Current Local Timezone is: " + CommonUtils.locTZ.getDisplayName());

			this.setM_ThreadDataLoadNightScout(new ThreadDataLoad(m_DataLoadNightScout));

			// Store supplied handler
			m_ThreadHandlerNightscout = handler;

			// Install our own
			// m_ThreadDataLoadNightScout.loadDBResults(handler);

			m_ThreadDataLoadNightScout.loadDBResults(
					new ThreadDataLoad.DataLoadCompleteHandler(handler.getM_Object()) 
					{
						//		@Override
						public void exceptionRaised(String message) 
						{
							setM_ThreadDataLoadNightScout(null);
						}

						//		@Override
						public void dataLoadComplete(Object obj, String message) 
						{
							m_NightScoutArrayListDBResults = m_DataLoadNightScout.getResultsFromDB();

							// Sort the Mongo Results
							Collections.sort(m_NightScoutArrayListDBResults, new ResultFromDBComparator());

							String statusText = new String("");
							statusText = String.format("Load %s\n%5d meter/pump entries read. ", 
									m_DeviceUsed, m_NightScoutArrayListDBResults.size());

							// We want to do this UI change in the main thread, and not the DB worker thread that's just
							// notified back
							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									// Add some commentary to the text pane
									//									addStatusText("Loaded " + m_NightScoutArrayListDBResults.size() + " entries from Night Scout.");
								}
							});

							// Invoke our stored handler
							m_ThreadHandlerNightscout.dataLoadComplete(obj, statusText);

							// Now clear the thread
							setM_ThreadDataLoadNightScout(null);
						}
					});	

		}
	}

	// Multi-threaded Nightscout Loader
	public synchronized void threadLoadNightScoutEntries(ThreadDataLoad.DataLoadCompleteHandler handler)
	{
		if (this.getM_ThreadDataLoadNightScoutEntries() != null)
		{
			// Need better way than this!
			addErrorText("threadLoadNightScoutEntries Thread Already Running!!");
		}
		else
		{
			// Add some detail on the current timezone
			addStatusText("Current Local Timezone is: " + CommonUtils.locTZ.getDisplayName());

			this.setM_ThreadDataLoadNightScoutEntries(new ThreadDataLoad(m_DataLoadNightScoutEntries));

			// Store supplied handler
			m_ThreadHandlerNightscoutEntries = handler;

			// Install our own
			// m_ThreadDataLoadNightScout.loadDBResults(handler);

			m_ThreadDataLoadNightScoutEntries.loadDBResults(
					new ThreadDataLoad.DataLoadCompleteHandler(handler.getM_Object()) 
					{
						//		@Override
						public void exceptionRaised(String message) 
						{
							setM_ThreadDataLoadNightScout(null);
						}

						//		@Override
						public void dataLoadComplete(Object obj, String message) 
						{
							m_NightScoutArrayListDBResultEntries = m_DataLoadNightScoutEntries.getResultsFromDB();

							//							synchronized(m_Lock)
							//							{
							//								// Tell the Analyzer this is done
							//								m_ThreadAnalyzer.getM_Analyzer().initialize(m_NightScoutArrayListDBResultEntries);
							//							}

							// Sort the Mongo Results
							Collections.sort(m_NightScoutArrayListDBResultEntries, new ResultFromDBComparator());

							String statusText = new String("");
							statusText = String.format("Load CGM\n%7d entries read. ", 
									m_NightScoutArrayListDBResultEntries.size());

							// We want to do this UI change in the main thread, and not the DB worker thread that's just
							// notified back
							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									// Add some commentary to the text pane
									//									addStatusText("Loaded " + m_NightScoutArrayListDBResults.size() + " entries from Night Scout.");
								}
							});

							// Invoke our stored handler
							m_ThreadHandlerNightscoutEntries.dataLoadComplete(obj, statusText);

							// Now clear the thread
							setM_ThreadDataLoadNightScoutEntries(null);
						}
					});	

		}
	}

	public void loadNightScout(/*Date startDate, Date endDate*/)
	{
		//		Calendar startCal = new GregorianCalendar();
		//		Calendar endCal   = new GregorianCalendar();
		//		startCal.setTime(startDate);
		//		endCal.setTime(endDate);
		//	
		//		// Want midnight so we get all the results
		//		startCal.set(Calendar.HOUR_OF_DAY, 0);
		//		startCal.set(Calendar.MINUTE, 0);
		//		startCal.set(Calendar.SECOND, 0);
		//		startCal.set(Calendar.MILLISECOND, 0);
		//
		//		// Want midnight so we get all the results
		//		endCal.set(Calendar.HOUR_OF_DAY, 0);
		//		endCal.set(Calendar.MINUTE, 0);
		//		endCal.set(Calendar.SECOND, 0);
		//		endCal.set(Calendar.MILLISECOND, 0);
		//		
		//		Date startDateMidNight = new Date();
		//		startDateMidNight = startCal.getTime();
		//		
		//		Date endDateMidNight = new Date();
		//		endDateMidNight = endCal.getTime();
		//		
		try
		{
			// m_DataLoaderMongoDB.loadDBResults(startDate, endDate, false);

			// Instead of using the date ranges as above, load all results in...
			m_DataLoadNightScout.loadDBResults();
			m_Logger.log( Level.FINE, "Successfully Queried the Night Scout DB");

			m_NightScoutArrayListDBResults = m_DataLoadNightScout.getResultsFromDB();

			// Sort the Mongo Results
			Collections.sort(m_NightScoutArrayListDBResults, new ResultFromDBComparator());

			// Add some commentary to the text pane
			//			addStatusText("Loaded " + m_NightScoutArrayListDBResults.size() + " entries from Night Scout.");
		}
		catch(Exception e)
		{
			addErrorText("ERROR - Load NightScout. Caught Exception " + e.getMessage() + "-" + e.getLocalizedMessage());
		}

	}

	public void determineDifferences()
	{
		// All results from Meter/Pump
		m_MeterArrayListDBResultsSet.addAll(m_MeterArrayListDBResults);
		// All results from NightScout
		m_NightScoutArrayListDBResultsSet.addAll(m_NightScoutArrayListDBResults);

		// Create a copy of Meter/Pump results for intersection
		Set<DBResult> intersection = new HashSet<DBResult>(m_MeterArrayListDBResultsSet);
		intersection.retainAll(m_NightScoutArrayListDBResultsSet);

		m_Logger.log( Level.FINE, "determineDifferences : m_MeterArrayListDBResultsSet has " + m_MeterArrayListDBResultsSet.size() + " entries");
		m_Logger.log( Level.FINE, "determineDifferences : m_NightScoutArrayListDBResultsSet has " + m_NightScoutArrayListDBResultsSet.size() + " entries");

		// Now remove all Mongo results from SQL Server already there.
		m_MeterArrayListDBResultsSet.removeAll(m_NightScoutArrayListDBResultsSet);

		m_Logger.log( Level.FINE, "determineDifferences : After removing existing Nightscout entries, m_MeterArrayListDBResultsSet has " + m_MeterArrayListDBResultsSet.size() + " entries");
		intersection.retainAll(m_NightScoutArrayListDBResultsSet);
		m_Logger.log( Level.FINE, "determineDifferences : After retaining duplicate entries, intersection has " + intersection.size() + " entries");

		// This is where we move the additional meter DB Results to Nightscout.
		// Next operation would be to add the same DBResults
		for (DBResult c : m_MeterArrayListDBResultsSet)
		{
			m_NightScoutArrayListDBResults.add(c);
			m_Logger.log( Level.FINEST, "Difference detected: " + c.toString());
		}

		int cnt=0;
		// List out times before and after...
		for (DBResult c : m_NightScoutArrayListDBResults)
		{
			m_Logger.log( Level.FINEST, "BEFORE Epoch Millies at: " + cnt++ + " is " + c.getM_EpochMillies() + " full " + c.toString());
		}

		// Sort the Mongo Results
		Collections.sort(m_NightScoutArrayListDBResults, new ResultFromDBComparator());
		m_Logger.log( Level.FINE, "m_NightScoutArrayListDBResults now sorted");

		cnt=0;
		// List out times before and after...
		for (DBResult c : m_NightScoutArrayListDBResults)
		{
			m_Logger.log( Level.FINEST, "AFTER Epoch Millies at: " + cnt++ + " is " + c.getM_EpochMillies() + " full " + c.toString());
		}

		addStatusText("Found " + m_MeterArrayListDBResultsSet.size() + " new meter/pump entries.");
		addStatusText("Found " + intersection.size() + " existing meter/pump entries.");
		// Check the intersection too
	}

	public void saveDifferences()
	{
		if (m_MeterArrayListDBResultsSet.size() > 0)
		{
			try
			{
				m_DataLoadNightScout.storeResultsFromDB(m_MeterArrayListDBResultsSet);
			}
			catch(Exception e)
			{
				addErrorText("saveDifferences: Just Caught Exception " + e.getMessage() + "-" + e.getLocalizedMessage());
			}
		}
	}

	//	public void analyseResults()
	//	{
	//		try
	//		{
	//			m_Analyzer.scanForGaps(m_NightScoutArrayListDBResults);
	//		} 
	//		catch (ParseException e) 
	//		{
	//			addErrorText("analyseResults: Just Caught Exception " + e.getMessage() + "-" + e.getLocalizedMessage());
	//		}
	//	}

	public void resetAnalyzeDateRange()
	{
		Date endDate = Analyzer.getLastDateFromDBResults(this.getM_ResultsMongoDB());
		PrefsNightScoutLoader.getInstance().resetAnalyzeDateRange(endDate);
	}

	public void doThreadAnalyzeResults(WinTextWin autotunerWin, String excelFilename, AnalyzerCompleteHander handler)
	{
		if (this.getM_ThreadAnalyzer() != null)
		{
			// Need better way than this!
			addErrorText("doThreadAnalyzeResults Thread Already Running!!");
		}
		else
		{
			// David 27 Jul
			// Noticed issues on 2nd analyze 
			// Not sure why need to reuse same analyer so get thread to create one
			//			setM_ThreadAnalyzer(new ThreadAnalyzer(m_Analyzer));
			synchronized(m_Lock)
			{
				setM_ThreadAnalyzer(new ThreadAnalyzer(this.getM_ResultsMongoDB(), this.m_NightScoutArrayListDBResultEntries));
			}

			// Store supplied handler
			m_ThreadHandlerAnalyzer = handler;

			m_Logger.log(Level.FINE, "<"+this.getClass().getName()+"> Core.doThreadAnalyzeResults BEFORE Sort First Entry is " + 
					(getM_ResultsMongoDB().size() > 0 ? getM_ResultsMongoDB().get(0).toString() : "EMPTY LIST"));
			m_ThreadAnalyzer.setM_DBResultList(getM_ResultsMongoDB());
			m_Logger.log(Level.FINE, "<"+this.getClass().getName()+"> Core.doThreadAnalyzeResults AFTER Sort First Entry is " + 
					(getM_ResultsMongoDB().size() > 0 ? getM_ResultsMongoDB().get(0).toString() : "EMPTY LIST"));

			m_ThreadAnalyzer.setM_ExcelFilename(excelFilename);
			m_ThreadAnalyzer.setM_AutotunerWin(autotunerWin);

			m_ThreadAnalyzer.analyzeResults(
					new AnalyzerCompleteHander(handler.getM_Object())
					{
						//		@Override
						public void exceptionRaised(String message) 
						{
							// Display popup ... not working for some reason
							JOptionPane.showMessageDialog(null, message);							
							setM_ThreadAnalyzer(null);

							// Invoke our stored handler
							m_ThreadHandlerAnalyzer.exceptionRaised(message);
						}

						//		@Override
						public void analyzeResultsComplete(Object obj) 
						{
							// We want to do this UI change in the main thread, and not the DB worker thread that's just
							// notified back
							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									String statusText = new String();

									statusText = String.format("Analysis complete");
									// Add some commentary to the text pane
									addStatusTextWithTime(statusText);
								}
							});

							// Invoke our stored handler
							m_ThreadHandlerAnalyzer.analyzeResultsComplete(obj);

							// Now clear the thread							
							setM_ThreadAnalyzer(null);
						}
					}
					);
		}
	}


	public void doSummarisedThreadAnalyzeResults(AnalyzerCompleteHander handler)
	{
		if (this.getM_ThreadAnalyzer() != null)
		{
			// Need better way than this!
			addErrorText("doSummarisedThreadAnalyzeResults Thread Already Running!!");
		}
		else
		{
			// David 27 Jul
			// Noticed issues on 2nd analyze 
			// Not sure why need to reuse same analyer so get thread to create one
			//			setM_ThreadAnalyzer(new ThreadAnalyzer(m_Analyzer));
			//			setM_ThreadAnalyzer(new ThreadAnalyzer(this.getM_ResultsMongoDB(), true));
			setM_ThreadAnalyzer(new ThreadAnalyzer(this.getM_ResultsMongoDB(), 
					this.m_NightScoutArrayListDBResultEntries, Analyzer.AnalyzerMode.summaryOnly));

			// Store supplied handler
			m_ThreadHandlerAnalyzer = handler;

			m_Logger.log(Level.FINE, "<"+this.getClass().getName()+"> Core.doThreadAnalyzeResults BEFORE Sort First Entry is " + 
					(getM_ResultsMongoDB().size() > 0 ? getM_ResultsMongoDB().get(0).toString() : "EMPTY LIST"));
			m_ThreadAnalyzer.setM_DBResultList(getM_ResultsMongoDB());
			m_Logger.log(Level.FINE, "<"+this.getClass().getName()+"> Core.doThreadAnalyzeResults AFTER Sort First Entry is " + 
					(getM_ResultsMongoDB().size() > 0 ? getM_ResultsMongoDB().get(0).toString() : "EMPTY LIST"));

			m_ThreadAnalyzer.analyzeResults(
					new AnalyzerCompleteHander(handler.getM_Object())
					{
						//		@Override
						public void exceptionRaised(String message) 
						{
							setM_ThreadAnalyzer(null);
						}

						//		@Override
						public void analyzeResultsComplete(Object obj) 
						{
							// We want to do this UI change in the main thread, and not the DB worker thread that's just
							// notified back
							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									;
								}
							});

							// Invoke our stored handler
							m_ThreadHandlerAnalyzer.analyzeResultsComplete(obj);

							// Now clear the thread							
							setM_ThreadAnalyzer(null);
						}
					}
					);
		}
	}


	public void doFullHistoryThreadAnalyzeResults(AnalyzerCompleteHander handler)
	{
		if (this.getM_FullHistoryThreadAnalyzer() != null)
		{
			// Need better way than this!
			addErrorText("doFullHistoryThreadAnalyzeResults Thread Already Running!!");
		}
		else
		{
			// Reset any previous history analyzer to null in case a manual analysis runs.  We don't include full history
			this.setM_FullHistoryAnalyzer(null);

			// David 27 Jul
			// Noticed issues on 2nd analyze 
			// Not sure why need to reuse same analyer so get thread to create one
			//			setM_ThreadAnalyzer(new ThreadAnalyzer(m_Analyzer));
			setM_FullHistoryThreadAnalyzer(new ThreadAnalyzer(this.getM_ResultsMongoDB(), 
					this.m_NightScoutArrayListDBResultEntries, Analyzer.AnalyzerMode.fullHistory));

			// Store supplied handler
			m_FullHistoryThreadHandlerAnalyzer = handler;

			m_Logger.log(Level.FINE, "<"+this.getClass().getName()+"> Core.doFullHistoryThreadAnalyzeResults BEFORE Sort First Entry is " + 
					(getM_ResultsMongoDB().size() > 0 ? getM_ResultsMongoDB().get(0).toString() : "EMPTY LIST"));
			m_FullHistoryThreadAnalyzer.setM_DBResultList(getM_ResultsMongoDB());
			m_Logger.log(Level.FINE, "<"+this.getClass().getName()+"> Core.doFullHistoryThreadAnalyzeResults AFTER Sort First Entry is " + 
					(getM_ResultsMongoDB().size() > 0 ? getM_ResultsMongoDB().get(0).toString() : "EMPTY LIST"));

			m_FullHistoryThreadAnalyzer.analyzeResults(
					new AnalyzerCompleteHander(handler.getM_Object())
					{
						//		@Override
						public void exceptionRaised(String message) 
						{
							setM_FullHistoryThreadAnalyzer(null);
						}

						//		@Override
						public void analyzeResultsComplete(Object obj) 
						{

							// Keep a record of the Analyzer so that future analyzers can make use of it
							setM_FullHistoryAnalyzer(m_FullHistoryThreadAnalyzer.getM_Analyzer());

							// We want to do this UI change in the main thread, and not the DB worker thread that's just
							// notified back
							EventQueue.invokeLater(new 
									Runnable()
							{
								public void run()
								{ 
									;
								}
							});

							// Invoke our stored handler
							m_FullHistoryThreadHandlerAnalyzer.analyzeResultsComplete(obj);

							// Now clear the thread							
							setM_FullHistoryThreadAnalyzer(null);
						}
					}
					);
		}
	}


	//	public void analyseResults(String excelFileName)
	//	{
	////		int daysBack = PrefsNightScoutLoader.getInstance().getM_AnalyzerDaysBack();
	////		m_Analyzer.analyzeResults(m_NightScoutArrayListDBResults, daysBack, excelFileName);	
	//		m_Analyzer.analyzeResults(m_NightScoutArrayListDBResults, excelFileName);	
	//	}


	public int countNightScoutTreatments()
	{
		int result = 0;

		for (DBResult x : getM_DataLoadNightScout().getResultsFromDB())
		{
			String enteredByPrefix = AuditHistory.getInstance().getM_UploadIDPrefix();

			String enteredBy = x.getM_CP_EnteredBy();

			if (enteredBy.length() > enteredByPrefix.length())
			{
				String subStr = enteredBy.substring(0, enteredByPrefix.length());
				if (subStr.equals("Nightscout Loader"))
				{
					result++;
				}
			}
		}

		return result;
	}

	public void deleteLoadedTreatments()
	{
		try
		{
			// m_DataLoaderMongoDB.loadDBResults(startDate, endDate, false);
			int beforeEntryCount = getM_DataLoadNightScout().getResultsFromDB().size();

			// Instead of using the date ranges as above, load all results in...
			m_DataLoadNightScout.deleteLoadedTreatments();

			// Update all success entries as deleted and store an audit Log entry for the deletion
			Date now = new Date();

			try 
			{
				// Mark all as deleted by this upload id
				AuditHistory.getInstance().markAllAuditHistoryDeleted();

				// Add an entry for the delete itself
				AuditHistory.getInstance().storeAuditHistory(AuditLog.m_Delete, now, 
						m_DeviceUsed, m_FileName, m_DateRange, 0, 
						beforeEntryCount,	beforeEntryCount, 0, 0);

				// Reload to get latest id
				AuditHistory.getInstance().loadAuditHistory();
			} 
			catch (UnknownHostException e) 
			{
				m_Logger.log(Level.SEVERE, "Unknown Host Exception caught. " + e.getMessage());
			}


			// Delete the alerter threads and restart them
			m_EntriesAlerter.interrupThread();
			m_TreatmentsAlerter.interrupThread();

			// Now create new threads and allow garbage collector to do its thing
			m_EntriesAlerter    = new ThreadMongoDBAlerterEntries();
			m_TreatmentsAlerter = new ThreadMongoDBAlerterTreatments();

			// Start them both
			m_EntriesAlerter.startThread();
			m_TreatmentsAlerter.startThread();

			// Add some commentary to the text pane
			String statusText = new String();

			statusText = String.format("%5d treatment entries before deleting. ", 
					beforeEntryCount);
			// Add some commentary to the text pane
			addStatusTextWithTime(statusText);			
		}
		catch(Exception e)
		{
			addErrorText("ERROR - Delete Treatment NightScout. Caught Exception " + e.getMessage() + "-" + e.getLocalizedMessage());
		}

	}

	public void deleteLoadedProximityTreatment(AuditLog entry)
	{
		try
		{
			// m_DataLoaderMongoDB.loadDBResults(startDate, endDate, false);
			int beforeEntryCount = getM_DataLoadNightScout().getResultsFromDB().size();

			// Instead of using the date ranges as above, load all results in...
			m_DataLoadNightScout.deleteLoadedTreatment(entry, true);


			// Update all success entries as deleted and store an audit Log entry for the deletion
			Date now = new Date();

			try 
			{
				// Mark all as deleted by this upload id
				AuditHistory.getInstance().markAuditHistoryDeleted(entry, true);

				// Add an entry for the delete itself
				AuditHistory.getInstance().storeAuditHistory(AuditLog.m_Delete, now, 
						m_DeviceUsed, m_FileName, m_DateRange, 0, 
						beforeEntryCount,	beforeEntryCount, 0, 0);

				// Reload to get latest id
				AuditHistory.getInstance().loadAuditHistory();

				// Could also tell the Audit Window to refresh too
				// However we're in Core and some distance away ...
			} 
			catch (UnknownHostException e) 
			{
				m_Logger.log(Level.SEVERE, "Unknown Host Exception caught. " + e.getMessage());
			}


			// Delete the alerter threads and restart them
			m_EntriesAlerter.interrupThread();
			m_TreatmentsAlerter.interrupThread();

			// Now create new threads and allow garbage collector to do its thing
			m_EntriesAlerter    = new ThreadMongoDBAlerterEntries();
			m_TreatmentsAlerter = new ThreadMongoDBAlerterTreatments();

			// Start them both
			m_EntriesAlerter.startThread();
			m_TreatmentsAlerter.startThread();

			// Add some commentary to the text pane
			String statusText = new String();

			statusText = String.format("%5d treatment entries before deleting. ", 
					beforeEntryCount);
			// Add some commentary to the text pane
			addStatusTextWithTime(statusText);			
		}
		catch(Exception e)
		{
			addErrorText("ERROR - Delete Treatment NightScout. Caught Exception " + e.getMessage() + "-" + e.getLocalizedMessage());
		}
	}



	public void deleteLoadedTreatment(DBResult res)
	{
		try
		{
			m_DataLoadNightScout.deleteLoadedTreatment(res);

			// Delete the alerter threads and restart them
			m_EntriesAlerter.interrupThread();
			m_TreatmentsAlerter.interrupThread();

			// Now create new threads and allow garbage collector to do its thing
			m_EntriesAlerter    = new ThreadMongoDBAlerterEntries();
			m_TreatmentsAlerter = new ThreadMongoDBAlerterTreatments();

			// Start them both
			m_EntriesAlerter.startThread();
			m_TreatmentsAlerter.startThread();

			// Add some commentary to the text pane
			String statusText = new String();

			statusText = "Entry deleted.";
			// Add some commentary to the text pane
			addStatusTextWithTime(statusText);			
		}
		catch(Exception e)
		{
			addErrorText("ERROR - Delete Treatment NightScout. Caught Exception " + e.getMessage() + "-" + e.getLocalizedMessage());
		}

	}


	public void deleteLoadedTreatment(AuditLog entry)
	{
		try
		{
			// m_DataLoaderMongoDB.loadDBResults(startDate, endDate, false);
			int beforeEntryCount = getM_DataLoadNightScout().getResultsFromDB().size();

			// Instead of using the date ranges as above, load all results in...
			m_DataLoadNightScout.deleteLoadedTreatment(entry);

			// Check if there are any proximity as need to delete those too ...
			if (entry.getM_ProximityMeterEntries() > 0 || entry.getM_ProximityNSEntries() > 0)
			{
				m_DataLoadNightScout.deleteLoadedTreatment(entry, true);
			}

			// Update all success entries as deleted and store an audit Log entry for the deletion
			Date now = new Date();

			try 
			{
				// Mark all as deleted by this upload id
				AuditHistory.getInstance().markAuditHistoryDeleted(entry);

				// Add an entry for the delete itself
				AuditHistory.getInstance().storeAuditHistory(AuditLog.m_Delete, now, 
						m_DeviceUsed, m_FileName, m_DateRange, 0, 
						beforeEntryCount,	beforeEntryCount, 0, 0);

				// Reload to get latest id
				AuditHistory.getInstance().loadAuditHistory();
			} 
			catch (UnknownHostException e) 
			{
				m_Logger.log(Level.SEVERE, "Unknown Host Exception caught. " + e.getMessage());
			}


			// Delete the alerter threads and restart them
			m_EntriesAlerter.interrupThread();
			m_TreatmentsAlerter.interrupThread();

			// Now create new threads and allow garbage collector to do its thing
			m_EntriesAlerter    = new ThreadMongoDBAlerterEntries();
			m_TreatmentsAlerter = new ThreadMongoDBAlerterTreatments();

			// Start them both
			m_EntriesAlerter.startThread();
			m_TreatmentsAlerter.startThread();

			// Add some commentary to the text pane
			String statusText = new String();

			statusText = String.format("%5d treatment entries before deleting. ", 
					beforeEntryCount);
			// Add some commentary to the text pane
			addStatusTextWithTime(statusText);			
		}
		catch(Exception e)
		{
			addErrorText("ERROR - Delete Treatment NightScout. Caught Exception " + e.getMessage() + "-" + e.getLocalizedMessage());
		}
	}


	public void exportResults(DefaultTableModel model, String fileName) throws FileNotFoundException, IOException 
	{
		DataExportExcel exporter = new DataExportExcel();
		exporter.exportToExcel(model, fileName);

		addStatusText("Exported " + model.getRowCount() + " records to file " + fileName);
	}

	public void downloadTreamentJSON(String fileName) throws IOException
	{

		// One way of doing it, but relies on having mongoexport installed.
		//	Runtime.getRuntime().exec("mongoexport --host host_name --port port_number --db myDatabase --collection Page --out Page.json");

		int recCount = m_DataLoadNightScout.downloadTreamentJSON(fileName);
		addStatusText("Downloaded " + recCount + " treatment entries to file " + fileName);
	}

	public void downloadSensorJSON(String fileName) throws IOException
	{

		// One way of doing it, but relies on having mongoexport installed.
		//	Runtime.getRuntime().exec("mongoexport --host host_name --port port_number --db myDatabase --collection Page --out Page.json");

		int recCount = m_DataLoadNightScout.downloadSensorJSON(fileName);
		addStatusText("Downloaded " + recCount + " sensor entries to file " + fileName);
	}

	public void addStatusLine()
	{
		m_Logger.log( Level.INFO, "----------------------------------------------------------------" );
		//m_StatusText += "----------------------------------------------------------------\n";
	}

	public void addStatusText(String text)
	{
		addStatusText(text, false);
	}

	public void addStatusTextWithTime(String text)
	{
		// Keep everything at level INFO without the standard date time Logger provides
		// but sometimes we might want the time - eg for Sync
		final DateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
		Date now = new Date();

		StringBuffer sb = new StringBuffer();
		sb.append(df.format(now));
		sb.append(" " + text);

		addStatusText(sb.toString(), false);
	}

	public void popupADialog(String text)
	{

	}

	public void addErrorText(String text)
	{
		addStatusText(text, true);
	}

	public void addStatusText(String text, boolean err)
	{
		if (err == true)
		{
			m_Logger.log( Level.WARNING, text );
		}
		else
		{
			m_Logger.log( Level.INFO, text );
		}
	}

	/**
	 * @return the m_DataLoadSQLServer
	 */
	public DataLoadRoche getM_DataLoadSQLServer() {
		return m_DataLoadRoche;
	}


	/**
	 * @param m_DataLoadSQLServer the m_DataLoadSQLServer to set
	 */
	public void setM_DataLoadSQLServer(DataLoadRoche m_DataLoadSQLServer) {
		this.m_DataLoadRoche = m_DataLoadSQLServer;
	}


	/**
	 * @return the m_DataLoadMongoDB
	 */
	public DataLoadNightScoutTreatments getM_DataLoadNightScout() {
		return m_DataLoadNightScout;
	}


	/**
	 * @param m_DataLoadMongoDB the m_DataLoadMongoDB to set
	 */
	public void setM_DataLoadNightScout(DataLoadNightScoutTreatments m_DataLoadMongoDB) {
		this.m_DataLoadNightScout = m_DataLoadMongoDB;
	}


	/**
	 * @return the m_DataLoadNightScoutEntries
	 */
	public synchronized DataLoadNightScoutEntries getM_DataLoadNightScoutEntries() {
		return m_DataLoadNightScoutEntries;
	}

	/**
	 * @param m_DataLoadNightScoutEntries the m_DataLoadNightScoutEntries to set
	 */
	public synchronized void setM_DataLoadNightScoutEntries(DataLoadNightScoutEntries m_DataLoadNightScoutEntries) {
		this.m_DataLoadNightScoutEntries = m_DataLoadNightScoutEntries;
	}

	/**
	 * @return the m_DataLoadMedtronic
	 */
	public DataLoadMedtronic getM_DataLoadMedtronic() {
		return m_DataLoadMedtronic;
	}


	/**
	 * @param m_DataLoadMedtronic the m_DataLoadMedtronic to set
	 */
	public void setM_DataLoadMedtronic(DataLoadMedtronic m_DataLoadMedtronic) {
		this.m_DataLoadMedtronic = m_DataLoadMedtronic;
	}


	/**
	 * @return the m_ResultsSQLServer
	 */
	public ArrayList<DBResult> getM_ResultsSQLServer() {
		return m_MeterArrayListDBResults;
	}


	/**
	 * @param m_ResultsSQLServer the m_ResultsSQLServer to set
	 */
	public void setM_ResultsSQLServer(ArrayList<DBResult> m_ResultsSQLServer) {
		this.m_MeterArrayListDBResults = m_ResultsSQLServer;
	}


	/**
	 * @return the m_ResultsMongoDB
	 */
	public ArrayList<DBResult> getM_ResultsMongoDB() {
		return m_NightScoutArrayListDBResults;
	}


	/**
	 * @param m_ResultsMongoDB the m_ResultsMongoDB to set
	 */
	public void setM_ResultsMongoDB(ArrayList<DBResult> m_ResultsMongoDB) {
		this.m_NightScoutArrayListDBResults = m_ResultsMongoDB;
	}


	/**
	 * @return the m_StatusText
	 */
	public String getM_StatusText() {
		return m_StatusText;
	}


	/**
	 * @param m_StatusText the m_StatusText to set
	 */
	public void setM_StatusText(String m_StatusText) {
		this.m_StatusText = m_StatusText;
	}

	/**
	 * @return the m_ThreadDataLoadRoche
	 */
	public synchronized ThreadDataLoad getM_ThreadDataLoadRoche() {
		return m_ThreadDataLoadRoche;
	}

	/**
	 * @param m_ThreadDataLoadRoche the m_ThreadDataLoadRoche to set
	 */
	public synchronized void setM_ThreadDataLoadRoche(ThreadDataLoad m_ThreadDataLoadRoche) {
		this.m_ThreadDataLoadRoche = m_ThreadDataLoadRoche;
	}

	/**
	 * @return the m_ThreadDataLoadRocheCSV
	 */
	public synchronized ThreadDataLoad getM_ThreadDataLoadRocheCSV() {
		return m_ThreadDataLoadRocheCSV;
	}

	/**
	 * @param m_ThreadDataLoadRocheCSV the m_ThreadDataLoadRocheCSV to set
	 */
	public synchronized void setM_ThreadDataLoadRocheCSV(ThreadDataLoad m_ThreadDataLoadRocheCSV) {
		this.m_ThreadDataLoadRocheCSV = m_ThreadDataLoadRocheCSV;
	}

	/**
	 * @return the m_ThreadDataLoadMedtronic
	 */
	public synchronized ThreadDataLoad getM_ThreadDataLoadMedtronic() {
		return m_ThreadDataLoadMedtronic;
	}

	/**
	 * @param m_ThreadDataLoadMedtronic the m_ThreadDataLoadMedtronic to set
	 */
	public synchronized void setM_ThreadDataLoadMedtronic(ThreadDataLoad m_ThreadDataLoadMedtronic) {
		this.m_ThreadDataLoadMedtronic = m_ThreadDataLoadMedtronic;
	}

	/**
	 * @return the m_ThreadDataLoadDiasend
	 */
	public synchronized ThreadDataLoad getM_ThreadDataLoadDiasend() {
		return m_ThreadDataLoadDiasend;
	}


	/**
	 * @param m_ThreadDataLoadDiasend the m_ThreadDataLoadDiasend to set
	 */
	public synchronized void setM_ThreadDataLoadDiasend(ThreadDataLoad m_ThreadDataLoadDiasend) {
		this.m_ThreadDataLoadDiasend = m_ThreadDataLoadDiasend;
	}


	/**
	 * @return the m_ThreadDataLoadOmniPod
	 */
	public synchronized ThreadDataLoad getM_ThreadDataLoadOmniPod() {
		return m_ThreadDataLoadOmniPod;
	}


	/**
	 * @param m_ThreadDataLoadOmniPod the m_ThreadDataLoadOmniPod to set
	 */
	public synchronized void setM_ThreadDataLoadOmniPod(ThreadDataLoad m_ThreadDataLoadOmniPod) {
		this.m_ThreadDataLoadOmniPod = m_ThreadDataLoadOmniPod;
	}


	/**
	 * @return the m_ThreadDataLoadTandem
	 */
	public synchronized ThreadDataLoad getM_ThreadDataLoadTandem() {
		return m_ThreadDataLoadTandem;
	}

	/**
	 * @param m_ThreadDataLoadTandem the m_ThreadDataLoadTandem to set
	 */
	public synchronized void setM_ThreadDataLoadTandem(ThreadDataLoad m_ThreadDataLoadTandem) {
		this.m_ThreadDataLoadTandem = m_ThreadDataLoadTandem;
	}

	/**
	 * @return the m_ThreadDataLoadNightScout
	 */
	public synchronized ThreadDataLoad getM_ThreadDataLoadNightScout() {
		return m_ThreadDataLoadNightScout;
	}

	/**
	 * @param m_ThreadDataLoadNightScout the m_ThreadDataLoadNightScout to set
	 */
	public synchronized void setM_ThreadDataLoadNightScout(ThreadDataLoad m_ThreadDataLoadNightScout) {
		this.m_ThreadDataLoadNightScout = m_ThreadDataLoadNightScout;
	}

	/**
	 * @return the m_ThreadDataMeterLoad
	 */
	public synchronized ThreadDataLoad getM_ThreadDataMeterLoad() {
		return m_ThreadDataMeterLoad;
	}


	/**
	 * @return the m_ThreadDataLoadNightScoutEntries
	 */
	public synchronized ThreadDataLoad getM_ThreadDataLoadNightScoutEntries() {
		return m_ThreadDataLoadNightScoutEntries;
	}

	/**
	 * @param m_ThreadDataLoadNightScoutEntries the m_ThreadDataLoadNightScoutEntries to set
	 */
	public synchronized void setM_ThreadDataLoadNightScoutEntries(ThreadDataLoad m_ThreadDataLoadNightScoutEntries) {
		this.m_ThreadDataLoadNightScoutEntries = m_ThreadDataLoadNightScoutEntries;
	}

	/**
	 * @param m_ThreadDataMeterLoad the m_ThreadDataMeterLoad to set
	 */
	public synchronized void setM_ThreadDataMeterLoad(ThreadDataLoad m_ThreadDataMeterLoad) {
		this.m_ThreadDataMeterLoad = m_ThreadDataMeterLoad;
	}


	/**
	 * @return the m_MeterPumpLoadOnly
	 */
	public synchronized boolean isM_MeterPumpLoadOnly() {
		return m_MeterPumpLoadOnly;
	}


	/**
	 * @param m_MeterPumpLoadOnly the m_MeterPumpLoadOnly to set
	 */
	public synchronized void setM_MeterPumpLoadOnly(boolean m_MeterPumpLoadOnly) {
		this.m_MeterPumpLoadOnly = m_MeterPumpLoadOnly;
	}


	/**
	 * @return the m_ThreadDetermineSaveDifferences
	 */
	public synchronized ThreadDetermineSaveDifferences getM_ThreadDetermineSaveDifferences() {
		return m_ThreadDetermineSaveDifferences;
	}


	/**
	 * @param m_ThreadDetermineSaveDifferences the m_ThreadDetermineSaveDifferences to set
	 */
	public synchronized void setM_ThreadDetermineSaveDifferences(
			ThreadDetermineSaveDifferences m_ThreadDetermineSaveDifferences) {
		this.m_ThreadDetermineSaveDifferences = m_ThreadDetermineSaveDifferences;
	}


	/**
	 * @return the m_ThreadAnalyzer
	 */
	public synchronized ThreadAnalyzer getM_ThreadAnalyzer() {
		return m_ThreadAnalyzer;
	}


	/**
	 * @param m_ThreadAnalyzer the m_ThreadAnalyzer to set
	 */
	public synchronized void setM_ThreadAnalyzer(ThreadAnalyzer m_ThreadAnalyzer) {
		this.m_ThreadAnalyzer = m_ThreadAnalyzer;
	}


	/**
	 * @return the m_FullHistoryThreadAnalyzer
	 */
	public synchronized ThreadAnalyzer getM_FullHistoryThreadAnalyzer() {
		return m_FullHistoryThreadAnalyzer;
	}


	/**
	 * @param m_FullHistoryThreadAnalyzer the m_FullHistoryThreadAnalyzer to set
	 */
	public synchronized void setM_FullHistoryThreadAnalyzer(ThreadAnalyzer m_FullHistoryThreadAnalyzer) {
		this.m_FullHistoryThreadAnalyzer = m_FullHistoryThreadAnalyzer;
	}


	/**
	 * @return the m_FullHistoryAnalyzer
	 */
	public synchronized Analyzer getM_FullHistoryAnalyzer() {
		return m_FullHistoryAnalyzer;
	}


	/**
	 * @param m_FullHistoryAnalyzer the m_FullHistoryAnalyzer to set
	 */
	public synchronized void setM_FullHistoryAnalyzer(Analyzer m_FullHistoryAnalyzer) {
		this.m_FullHistoryAnalyzer = m_FullHistoryAnalyzer;
	}

	/**
	 * @return the m_NightScoutArrayListDBResultEntries
	 */
	public synchronized ArrayList<DBResultEntry> getM_NightScoutArrayListDBResultEntries() {
		return m_NightScoutArrayListDBResultEntries;
	}

}
