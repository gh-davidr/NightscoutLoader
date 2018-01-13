package davidRichardson;

//import java.io.IOException;
import java.net.UnknownHostException;
//import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
//import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ThreadDetermineSaveDifferences implements Runnable 
{
	private static final Logger m_Logger = Logger.getLogger( MyLogger.class.getName() );

	// Separate thread for running this.
	private Thread                   m_LoadThread;

	// We want to join the two data load threads though ...
	private ThreadDataLoad           m_NightscoutLoadThread;
	private ThreadDataLoad           m_MeterLoadThread;

	// Data Loader used by thread
//	private DataLoadBase             m_DataLoader;

	// Keep track of these as threads are invoked so determinsavediff can be informed
	private String                   m_DeviceUsed;
	private String                   m_FileName;
	private String                   m_DateRange;
	private boolean                  m_CGMSyncPossible;

	Boolean                          m_LoadRunning;

	// Handler to notify when load completes
	DataLoadCompleteHander           m_CompleteHandler;

	// Main Data Loaders
	private DataLoadNightScoutTreatments   m_DataLoadNightScout;

	// Hold the list of objects back from SQL Server
	private ArrayList <DBResult>      m_MeterArrayListDBResults;
	private ArrayList <DBResult>      m_NightScoutArrayListDBResults;
	private ArrayList <DBResultEntry> m_MeterArrayListDBResultEntries;
	private ArrayList <DBResultEntry> m_NightScoutArrayListDBResultEntries;
	
	private Set<DBResult>             m_MeterArrayListDBResultsSet;
	private Set<DBResult>             m_MeterArrayListProximityDBResultsSet;
	private Set<DBResult>             m_ExistingNightScoutProximityDBResultsSet;
	private Set<DBResult>             m_NightScoutArrayListDBResultsSet;
	
	private Set<DBResultEntry>        m_MeterArrayListDBResultEntriesSet;
	private Set<DBResultEntry>        m_MeterArrayListProximityDBResultEntriesSet;
	private Set<DBResultEntry>        m_ExistingNightScoutProximityDBResultEntriesSet;
	private Set<DBResultEntry>        m_NightScoutArrayListDBResultEntriesSet;
	
	

//	private MyLogger               m_LoggerClass;

	// Collect some metrics for the message at the end
	private int                       m_CountMeterEntriesLoaded         = 0;
	private int                       m_CountMeterEntriesDuplicated     = 0;
	private int                       m_CountMeterEntriesAdded          = 0;
	private int                       m_CountNightScoutEntriesBefore    = 0;
	private int                       m_CountNightScoutEntriesAfter     = 0;
	private int                       m_CountNightScoutLoaderEntries    = 0;
	private int                       m_CountProximityMeterEntries      = 0;
	private int                       m_CountProximityNightScoutEntries = 0;
	
	// Proximity Checking
	private Set<DBResult>             m_FirstPassIntersection           = null;
	private Set<DBResult>             m_SecondPassIntersection          = null;

	private Set<DBResultEntry>        m_FirstPassEntryIntersection      = null;
	private Set<DBResultEntry>        m_SecondPassEntryIntersection     = null;

	
	// Collect some metrics for the message at the end
	private int                       m_CountMeterCGMEntriesLoaded         = 0;
	private int                       m_CountMeterCGMEntriesDuplicated     = 0;
	private int                       m_CountMeterCGMEntriesAdded          = 0;
	private int                       m_CountNightScoutCGMEntriesBefore    = 0;
	private int                       m_CountNightScoutCGMEntriesAfter     = 0;
	private int                       m_CountNightScoutLoaderCGMEntries    = 0;
	private int                       m_CountProximityMeterCGMEntries      = 0;
	private int                       m_CountProximityNightScoutCGMEntries = 0;


	/**
	 * @return the m_CountMeterEntriesLoaded
	 */
	public synchronized int getM_CountMeterEntriesLoaded() {
		return m_CountMeterEntriesLoaded;
	}

	/**
	 * @param m_CountMeterEntriesLoaded the m_CountMeterEntriesLoaded to set
	 */
	public synchronized void setM_CountMeterEntriesLoaded(int m_CountMeterEntriesLoaded) {
		this.m_CountMeterEntriesLoaded = m_CountMeterEntriesLoaded;
	}

	/**
	 * @return the m_CountMeterEntriesDuplicated
	 */
	public synchronized int getM_CountMeterEntriesDuplicated() {
		return m_CountMeterEntriesDuplicated;
	}

	/**
	 * @param m_CountMeterEntriesDuplicated the m_CountMeterEntriesDuplicated to set
	 */
	public synchronized void setM_CountMeterEntriesDuplicated(int m_CountMeterEntriesDuplicated) {
		this.m_CountMeterEntriesDuplicated = m_CountMeterEntriesDuplicated;
	}

	/**
	 * @return the m_CountMeterEntriesAdded
	 */
	public synchronized int getM_CountMeterEntriesAdded() {
		return m_CountMeterEntriesAdded;
	}

	/**
	 * @param m_CountMeterEntriesAdded the m_CountMeterEntriesAdded to set
	 */
	public synchronized void setM_CountMeterEntriesAdded(int m_CountMeterEntriesAdded) {
		this.m_CountMeterEntriesAdded = m_CountMeterEntriesAdded;
	}

	/**
	 * @return the m_CountNightScoutEntriesBefore
	 */
	public synchronized int getM_CountNightScoutEntriesBefore() {
		return m_CountNightScoutEntriesBefore;
	}

	/**
	 * @param m_CountNightScoutEntriesBefore the m_CountNightScoutEntriesBefore to set
	 */
	public synchronized void setM_CountNightScoutEntriesBefore(int m_CountNightScoutEntriesBefore) {
		this.m_CountNightScoutEntriesBefore = m_CountNightScoutEntriesBefore;
	}

	/**
	 * @return the m_CountNightScoutEntriesAfter
	 */
	public synchronized int getM_CountNightScoutEntriesAfter() {
		return m_CountNightScoutEntriesAfter;
	}

	/**
	 * @param m_CountNightScoutEntriesAfter the m_CountNightScoutEntriesAfter to set
	 */
	public synchronized void setM_CountNightScoutEntriesAfter(int m_CountNightScoutEntriesAfter) {
		this.m_CountNightScoutEntriesAfter = m_CountNightScoutEntriesAfter;
	}

//	public class ResultFromDBComparator implements Comparator<DBResult> 
//	{		
//		public int compare(DBResult p1, DBResult p2) 
//		{
//			int  result     = 0;
//			long p1_millies = p1.getM_EpochMillies();
//			long p2_millies = p2.getM_EpochMillies();
//			long diff       = p1_millies - p2_millies;
//
//			// Subtraction is too big for int result
//			if ((diff) > 0)
//			{
//				result = -1; // 1 for ascending order
//			}
//			else if ((diff) < 0)
//			{
//				result = 1;  // -1 for ascending order
//			}
//			return result;
//		}
//	}

	/**
	 * @return the m_CountNightScoutLoaderEntries
	 */
	public synchronized int getM_CountNightScoutLoaderEntries() {
		return m_CountNightScoutLoaderEntries;
	}

	/**
	 * @param m_CountNightScoutLoaderEntries the m_CountNightScoutLoaderEntries to set
	 */
	public synchronized void setM_CountNightScoutLoaderEntries(int m_CountNightScoutLoaderEntries) {
		this.m_CountNightScoutLoaderEntries = m_CountNightScoutLoaderEntries;
	}

	/**
	 * @return the m_CountProximityMeterEntries
	 */
	public synchronized int getM_CountProximityMeterEntries() {
		return m_CountProximityMeterEntries;
	}

	/**
	 * @param m_CountProximityMeterEntries the m_CountProximityMeterEntries to set
	 */
	public synchronized void setM_CountProximityMeterEntries(int m_CountProximityMeterEntries) {
		this.m_CountProximityMeterEntries = m_CountProximityMeterEntries;
	}

	/**
	 * @return the m_CountProximityNightScoutEntries
	 */
	public synchronized int getM_CountProximityNightScoutEntries() {
		return m_CountProximityNightScoutEntries;
	}

	/**
	 * @param m_CountProximityNightScoutEntries the m_CountProximityNightScoutEntries to set
	 */
	public synchronized void setM_CountProximityNightScoutEntries(int m_CountProximityNightScoutEntries) {
		this.m_CountProximityNightScoutEntries = m_CountProximityNightScoutEntries;
	}

	/**
	 * @return the m_CountMeterCGMEntriesLoaded
	 */
	public synchronized int getM_CountMeterCGMEntriesLoaded() {
		return m_CountMeterCGMEntriesLoaded;
	}

	/**
	 * @param m_CountMeterCGMEntriesLoaded the m_CountMeterCGMEntriesLoaded to set
	 */
	public synchronized void setM_CountMeterCGMEntriesLoaded(int m_CountMeterCGMEntriesLoaded) {
		this.m_CountMeterCGMEntriesLoaded = m_CountMeterCGMEntriesLoaded;
	}

	/**
	 * @return the m_CountMeterCGMEntriesDuplicated
	 */
	public synchronized int getM_CountMeterCGMEntriesDuplicated() {
		return m_CountMeterCGMEntriesDuplicated;
	}

	/**
	 * @param m_CountMeterCGMEntriesDuplicated the m_CountMeterCGMEntriesDuplicated to set
	 */
	public synchronized void setM_CountMeterCGMEntriesDuplicated(int m_CountMeterCGMEntriesDuplicated) {
		this.m_CountMeterCGMEntriesDuplicated = m_CountMeterCGMEntriesDuplicated;
	}

	/**
	 * @return the m_CountMeterCGMEntriesAdded
	 */
	public synchronized int getM_CountMeterCGMEntriesAdded() {
		return m_CountMeterCGMEntriesAdded;
	}

	/**
	 * @param m_CountMeterCGMEntriesAdded the m_CountMeterCGMEntriesAdded to set
	 */
	public synchronized void setM_CountMeterCGMEntriesAdded(int m_CountMeterCGMEntriesAdded) {
		this.m_CountMeterCGMEntriesAdded = m_CountMeterCGMEntriesAdded;
	}

	/**
	 * @return the m_CountNightScoutCGMEntriesBefore
	 */
	public synchronized int getM_CountNightScoutCGMEntriesBefore() {
		return m_CountNightScoutCGMEntriesBefore;
	}

	/**
	 * @param m_CountNightScoutCGMEntriesBefore the m_CountNightScoutCGMEntriesBefore to set
	 */
	public synchronized void setM_CountNightScoutCGMEntriesBefore(int m_CountNightScoutCGMEntriesBefore) {
		this.m_CountNightScoutCGMEntriesBefore = m_CountNightScoutCGMEntriesBefore;
	}

	/**
	 * @return the m_CountNightScoutCGMEntriesAfter
	 */
	public synchronized int getM_CountNightScoutCGMEntriesAfter() {
		return m_CountNightScoutCGMEntriesAfter;
	}

	/**
	 * @param m_CountNightScoutCGMEntriesAfter the m_CountNightScoutCGMEntriesAfter to set
	 */
	public synchronized void setM_CountNightScoutCGMEntriesAfter(int m_CountNightScoutCGMEntriesAfter) {
		this.m_CountNightScoutCGMEntriesAfter = m_CountNightScoutCGMEntriesAfter;
	}

	/**
	 * @return the m_CountNightScoutLoaderCGMEntries
	 */
	public synchronized int getM_CountNightScoutLoaderCGMEntries() {
		return m_CountNightScoutLoaderCGMEntries;
	}

	/**
	 * @param m_CountNightScoutLoaderCGMEntries the m_CountNightScoutLoaderCGMEntries to set
	 */
	public synchronized void setM_CountNightScoutLoaderCGMEntries(int m_CountNightScoutLoaderCGMEntries) {
		this.m_CountNightScoutLoaderCGMEntries = m_CountNightScoutLoaderCGMEntries;
	}

	/**
	 * @return the m_CountProximityMeterCGMEntries
	 */
	public synchronized int getM_CountProximityMeterCGMEntries() {
		return m_CountProximityMeterCGMEntries;
	}

	/**
	 * @param m_CountProximityMeterCGMEntries the m_CountProximityMeterCGMEntries to set
	 */
	public synchronized void setM_CountProximityMeterCGMEntries(int m_CountProximityMeterCGMEntries) {
		this.m_CountProximityMeterCGMEntries = m_CountProximityMeterCGMEntries;
	}

	/**
	 * @return the m_CountProximityNightScoutCGMEntries
	 */
	public synchronized int getM_CountProximityNightScoutCGMEntries() {
		return m_CountProximityNightScoutCGMEntries;
	}

	/**
	 * @param m_CountProximityNightScoutCGMEntries the m_CountProximityNightScoutCGMEntries to set
	 */
	public synchronized void setM_CountProximityNightScoutCGMEntries(int m_CountProximityNightScoutCGMEntries) {
		this.m_CountProximityNightScoutCGMEntries = m_CountProximityNightScoutCGMEntries;
	}

	// Thread Handler for resynchronization
	public static abstract class DataLoadCompleteHander 
	{
		private Object m_Object;
		public DataLoadCompleteHander(Object obj)
		{
			m_Object = obj;
		}
		public abstract void operationComplete(Object obj, String message);
		public abstract void exceptionRaised(String message);

		public Object getM_Object()
		{
			return m_Object;
		}
	}

	public ThreadDetermineSaveDifferences(DataLoadNightScoutTreatments   dataLoadNightScout,
			ArrayList <DBResult> meterArrayListDBResults,
			ArrayList <DBResult> nightScoutArrayListDBResults,
			ArrayList <DBResultEntry> meterArrayListDBResultEntries,
			ArrayList <DBResultEntry> nightScoutArrayListDBResultEntries,
			ThreadDataLoad nightscoutLoadThread,
			ThreadDataLoad meterLoadThread,
			String deviceUsed,
			String fileName,
			String dateRange)
	{
		m_LoadRunning = false;
		m_LoadThread  = new Thread(this);
		m_DataLoadNightScout                 = dataLoadNightScout;
		m_MeterArrayListDBResults            = meterArrayListDBResults;
		m_NightScoutArrayListDBResults       = nightScoutArrayListDBResults;
		m_MeterArrayListDBResultEntries      = meterArrayListDBResultEntries;
		m_NightScoutArrayListDBResultEntries = nightScoutArrayListDBResultEntries;

		m_MeterArrayListDBResultsSet                    = new HashSet<DBResult>();
		m_MeterArrayListProximityDBResultsSet           = new HashSet<DBResult>();
		m_NightScoutArrayListDBResultsSet               = new HashSet<DBResult>();
		m_ExistingNightScoutProximityDBResultsSet       = new HashSet<DBResult>();
		
		m_MeterArrayListDBResultEntriesSet              = new HashSet<DBResultEntry>();
		m_MeterArrayListProximityDBResultEntriesSet     = new HashSet<DBResultEntry>();
		m_NightScoutArrayListDBResultEntriesSet         = new HashSet<DBResultEntry>();
		m_ExistingNightScoutProximityDBResultEntriesSet = new HashSet<DBResultEntry>();
		
		// We want to join the two data load threads though ...
		m_NightscoutLoadThread              = nightscoutLoadThread;
		m_MeterLoadThread                   = meterLoadThread;

//		m_LoggerClass = new MyLogger(true);

		m_DeviceUsed   = new String(deviceUsed);
		m_FileName     = new String(fileName);
		m_DateRange    = new String(dateRange);
		m_CGMSyncPossible = false;;

	}

	public void determineSaveDifferences(DataLoadCompleteHander completeHandler)
	{
		m_CompleteHandler = completeHandler;
		m_LoadThread.start();
	}

	public void run() 
	{
		// Launch the thread to determine differences and save them back to MongoDB
		m_LoadRunning = true;
		

		// Before this thread can run, we need to join the other two
		m_Logger.log(Level.FINE, "ThreadDetermineSaveDifferences - Wait for meter thread - start ");
		this.m_MeterLoadThread.waitUntilFree();
		m_Logger.log(Level.FINE, "ThreadDetermineSaveDifferences - Wait for meter thread - done ");

		m_Logger.log(Level.FINE, "ThreadDetermineSaveDifferences - Wait for Night Scout thread - start ");
		this.m_NightscoutLoadThread.waitUntilFree();
		m_Logger.log(Level.FINE, "ThreadDetermineSaveDifferences - Wait for Night Scout thread - done ");

		m_Logger.log(Level.FINE, "ThreadDetermineSaveDifferences - All Dependent Threads complete ");

		// Get latest collections from the two threads
		m_NightScoutArrayListDBResults = m_DataLoadNightScout.getResultsFromDB();
		m_MeterArrayListDBResultEntries = this.m_MeterLoadThread.getM_DataLoader().getRawEntryResultsFromDB();
		m_MeterArrayListDBResults = this.m_MeterLoadThread.getM_DataLoader().getResultsTreatments();

		m_CGMSyncPossible = false;
		
		if (m_MeterArrayListDBResultEntries != null && 
				PrefsNightScoutLoader.getInstance().isM_SyncCGM() && m_MeterArrayListDBResultEntries.size() > 2)
		{
			// We already load CGM entries from Mongo, so see whether the start CGM loaded is before
			// Mongo entry load
			long startCGMLoadDateMillies   = m_MeterArrayListDBResultEntries.get(0).getM_EpochMillies();		
			long startMongoLoadDateMillies = DataLoadNightScoutEntries.getEntryLoadStartDate().getTime();
			
			// If Mongo start date is before the earliest CGM date, then we're good
			if (startMongoLoadDateMillies < startCGMLoadDateMillies)
			{
				m_CGMSyncPossible = true;
			}
		}
		
		// Meter / Pump results
		determineDifferences();
		saveDifferences();
				
		// CGM results if enabled
		if (m_CGMSyncPossible)
		{
			determineCGMDifferences();
			saveCGMDifferences();
		}
		else
		{
			m_CountMeterCGMEntriesLoaded = m_MeterArrayListDBResultEntries.size();
		}

		updateAuditHistory();
		loadAuditHistory();

		//			Thread.sleep(5000);
		m_LoadRunning = false;
		m_CompleteHandler.operationComplete(m_CompleteHandler.getM_Object(), "");
	}
	
	private void checkForDuplicates()
	{
		// Determine the type of duplicate action
		// 		cb_DuplicateCheckType.setModel(new DefaultComboBoxModel(new String[] {"No Duplicate Checking", "Mark Existing as Dupe", "Mark New as Dupe"}));

		int     checkType           = PrefsNightScoutLoader.getInstance().getM_ProximityCheckType();

		if (checkType == 0)
		{
			; // No dupe checking
		}
		// -----------------------------------------------------
		// Give preference to new entries in from meter.
		// If duplicates are detected, existing CP entries
		// get marked and could be removed from audit en masse.
		// -----------------------------------------------------
		else if (checkType == 1)
		{
			m_ExistingNightScoutProximityDBResultsSet = checkListsForDuplicates(m_MeterArrayListDBResultsSet, m_NightScoutArrayListDBResultsSet);
			m_CountProximityNightScoutEntries = m_ExistingNightScoutProximityDBResultsSet.size();
		}
		// -----------------------------------------------------
		// Give preference to existing CP entries.
		// If duplicates are detected, new meter entries
		// get marked and could be removed from audit en masse.
		// -----------------------------------------------------
		else
		{
			// Ignore the return as it contains new dupe meter entries
			m_MeterArrayListProximityDBResultsSet = checkListsForDuplicates(m_NightScoutArrayListDBResultsSet, m_MeterArrayListDBResultsSet);
			m_CountProximityMeterEntries = m_MeterArrayListProximityDBResultsSet.size();
		}
	}
	
	private void checkEntryForDuplicates()
	{
		// Determine the type of duplicate action
		// 		cb_DuplicateCheckType.setModel(new DefaultComboBoxModel(new String[] {"No Duplicate Checking", "Mark Existing as Dupe", "Mark New as Dupe"}));

		int     checkType           = PrefsNightScoutLoader.getInstance().getM_ProximityCheckType();

		if (checkType == 0)
		{
			; // No dupe checking
		}
		// -----------------------------------------------------
		// Give preference to new entries in from meter.
		// If duplicates are detected, existing CP entries
		// get marked and could be removed from audit en masse.
		// -----------------------------------------------------
		else if (checkType == 1)
		{
			m_ExistingNightScoutProximityDBResultEntriesSet = checkEntryListsForDuplicates(m_MeterArrayListDBResultEntriesSet,
					m_NightScoutArrayListDBResultEntriesSet);
			m_CountProximityNightScoutCGMEntries = m_ExistingNightScoutProximityDBResultEntriesSet.size();
		}
		// -----------------------------------------------------
		// Give preference to existing CP entries.
		// If duplicates are detected, new meter entries
		// get marked and could be removed from audit en masse.
		// -----------------------------------------------------
		else
		{
			// Ignore the return as it contains new dupe meter entries
			m_MeterArrayListProximityDBResultEntriesSet = checkEntryListsForDuplicates(m_NightScoutArrayListDBResultEntriesSet, 
					m_MeterArrayListDBResultEntriesSet);
			m_CountProximityMeterCGMEntries = m_MeterArrayListProximityDBResultEntriesSet.size();
		}
	}

	
	private Set<DBResult> checkListsForDuplicates(Set<DBResult> referenceList, Set<DBResult> compareList)
	{
		Set<DBResult> result = null;
		
		// ----------------------------------------------------------------------------------------
		
		DBResult.setM_ProximityCheck(true);
		DBResult.setM_ProximityCheckSecondPass(false);

		// Create a hash map of the Meter loaded results using modified ID
		Set<DBResult> nsFirstPassHashSet = new HashSet<DBResult>(referenceList);
		// Create a hash map of Nightscout results for intersection, this time using proximity checking
		m_FirstPassIntersection          = new HashSet<DBResult>(compareList);
		// Now elinate all NS results that differ to the meter/pump results leaving possible duplicates only.
		m_FirstPassIntersection.retainAll(nsFirstPassHashSet);
		
		
		// For Debug
		// 09 Oct 2016
		for (DBResult a : m_FirstPassIntersection)
		{
			System.out.println(a.toString());
			System.out.println(a.getIdentity());
		}
		
		
		// Clear both
		DBResult.setM_ProximityCheckSecondPass(false);
		DBResult.setM_ProximityCheck(false);

		// ----------------------------------------------------------------------------------------

		// ----------------------------------------------------------------------------------------

		// Now set second pass to true and repeat again
		DBResult.setM_ProximityCheck(true);
		DBResult.setM_ProximityCheckSecondPass(true);
		
		// Create a hash map of the NightScout loaded results using modified ID
		Set<DBResult> nsSecondPassHashSet = new HashSet<DBResult>(referenceList);
		// Create a hash map of Meter/Pump results for intersection, this time using proximity checking
		m_SecondPassIntersection          = new HashSet<DBResult>(compareList);
		// Now elinate all NS results that differ to the meter/pump results leaving possible duplicates only.
		m_SecondPassIntersection.retainAll(nsSecondPassHashSet);

		
		// Finally, merge the two pass intersection results to get the complete list of possible duplicates
		Set<DBResult> unionIntersection   = new HashSet<DBResult>();
		
		unionIntersection.addAll(m_FirstPassIntersection);
		unionIntersection.addAll(m_SecondPassIntersection);
				
		
		for (DBResult c : unionIntersection)
		{
			c.setM_ProximityPossibleDuplicate(true);
		}
		
		m_Logger.log(Level.FINE, "Proximity Count from firstPass is "  + m_FirstPassIntersection.size());
		m_Logger.log(Level.FINE, "Proximity Count from secondPass is "  + m_SecondPassIntersection.size());
		m_Logger.log(Level.FINE, "Proximity Count from combined passes is "  + unionIntersection.size());

			
		// Clear both
		DBResult.setM_ProximityCheckSecondPass(false);
		DBResult.setM_ProximityCheck(false);
		
		// ----------------------------------------------------------------------------------------

		result = unionIntersection;
		
		return result;
	}
	
	private Set<DBResultEntry> checkEntryListsForDuplicates(Set<DBResultEntry> referenceList, Set<DBResultEntry> compareList)
	{
		Set<DBResultEntry> result = null;
		
		// ----------------------------------------------------------------------------------------
		
		DBResultEntry.setM_ProximityCheck(true);
		DBResultEntry.setM_ProximityCheckSecondPass(false);

		// Create a hash map of the Meter loaded results using modified ID
		Set<DBResultEntry> nsFirstPassHashSet = new HashSet<DBResultEntry>(referenceList);
		// Create a hash map of Nightscout results for intersection, this time using proximity checking
		m_FirstPassEntryIntersection          = new HashSet<DBResultEntry>(compareList);
		// Now elinate all NS results that differ to the meter/pump results leaving possible duplicates only.
		m_FirstPassEntryIntersection.retainAll(nsFirstPassHashSet);
		
		
		// Clear both
		DBResult.setM_ProximityCheckSecondPass(false);
		DBResult.setM_ProximityCheck(false);

		// ----------------------------------------------------------------------------------------

		// ----------------------------------------------------------------------------------------

		// Now set second pass to true and repeat again
		DBResult.setM_ProximityCheck(true);
		DBResult.setM_ProximityCheckSecondPass(true);
		
		// Create a hash map of the NightScout loaded results using modified ID
		Set<DBResultEntry> nsSecondPassHashSet = new HashSet<DBResultEntry>(referenceList);
		// Create a hash map of Meter/Pump results for intersection, this time using proximity checking
		m_SecondPassEntryIntersection          = new HashSet<DBResultEntry>(compareList);
		// Now elinate all NS results that differ to the meter/pump results leaving possible duplicates only.
		m_SecondPassEntryIntersection.retainAll(nsSecondPassHashSet);

		
		// Finally, merge the two pass intersection results to get the complete list of possible duplicates
		Set<DBResultEntry> unionIntersection   = new HashSet<DBResultEntry>();
		
		unionIntersection.addAll(m_FirstPassEntryIntersection);
		unionIntersection.addAll(m_SecondPassEntryIntersection);
				
		
		for (DBResultEntry c : unionIntersection)
		{
			c.setM_ProximityPossibleDuplicate(true);
		}
		
		m_Logger.log(Level.FINE, "Proximity Count from firstPass is "  + m_FirstPassIntersection.size());
		m_Logger.log(Level.FINE, "Proximity Count from secondPass is "  + m_SecondPassIntersection.size());
		m_Logger.log(Level.FINE, "Proximity Count from combined passes is "  + unionIntersection.size());

			
		// Clear both
		DBResult.setM_ProximityCheckSecondPass(false);
		DBResult.setM_ProximityCheck(false);
		
		// ----------------------------------------------------------------------------------------

		result = unionIntersection;
		
		return result;
	}

	
	
	public void determineDifferences()
	{
		// All results from Meter/Pump
		m_MeterArrayListDBResultsSet.addAll(m_MeterArrayListDBResults);
		m_CountMeterEntriesLoaded = m_MeterArrayListDBResultsSet.size();

		// All results from NightScout
		m_NightScoutArrayListDBResultsSet.addAll(m_NightScoutArrayListDBResults);
		m_CountNightScoutEntriesBefore = m_NightScoutArrayListDBResultsSet.size();

		// Create a copy of Meter/Pump results for intersection
		Set<DBResult> existingNSEntriesFromMeterResults = new HashSet<DBResult>(m_MeterArrayListDBResultsSet);
		existingNSEntriesFromMeterResults.retainAll(m_NightScoutArrayListDBResultsSet);
		m_CountMeterEntriesDuplicated = existingNSEntriesFromMeterResults.size();

		// Now remove all Mongo results from Meter/Pump already there.
		m_MeterArrayListDBResultsSet.removeAll(m_NightScoutArrayListDBResultsSet);
		m_CountMeterEntriesAdded = m_MeterArrayListDBResultsSet.size();

		// Andy Sherwood request - Sep 2016
		checkForDuplicates();
		
		// This is where we move the additional meter DB Results to Nightscout.
		// Next operation would be to add the same DBResults
		for (DBResult c : m_MeterArrayListDBResultsSet)
		{
			m_NightScoutArrayListDBResults.add(c);
			m_Logger.log(Level.FINER, "Difference detected: " + c.toString());
		}

		m_CountNightScoutEntriesAfter = m_NightScoutArrayListDBResultsSet.size();

		int cnt=0;
		// List out times before and after...
		for (DBResult c : m_NightScoutArrayListDBResults)
		{
			m_Logger.log(Level.FINEST, "BEFORE Epoch Millies at: " + cnt++ + " is " + c.getM_EpochMillies() + " full " + c.toString());
		}

		// Sort the Mongo Results
		Collections.sort(m_NightScoutArrayListDBResults, new ResultFromDBComparator());

		m_CountNightScoutLoaderEntries = 0;
		String enteredByPrefix = AuditHistory.getInstance().getM_UploadIDPrefix();
		int    length = enteredByPrefix.length();
		// List out times before and after...
		for (DBResult c : m_NightScoutArrayListDBResults)
		{
			m_Logger.log(Level.FINEST, "AFTER Epoch Millies at: " + cnt++ + " is " + c.getM_EpochMillies() + " full " + c.toString());

			String enteredBy = c.getM_CP_EnteredBy(); 
			if (enteredBy.length() >= length)
			{
				String subStr    = enteredBy.substring(0, length);

				if (subStr.equals(enteredByPrefix))
				{
					m_CountNightScoutLoaderEntries++;
				}
			}
		}

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
				m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + " just caught an error line 748: " + e.getMessage() + " - " + e.getLocalizedMessage());
			}
		}
		
		if (this.m_ExistingNightScoutProximityDBResultsSet.size() > 0)
		{
			try
			{
				// DAVID CHECK THIS .. MIGHT NEED A DIFFERENT CALL TO MONGO DB TO DO AN UPDATE
				m_DataLoadNightScout.storeResultEntriesFromDB(m_ExistingNightScoutProximityDBResultEntriesSet);
			}
			catch(Exception e)
			{
				m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + " just caught an error: " + e.getMessage() + " - " + e.getLocalizedMessage());
			}
		}
	}
	
	public void determineCGMDifferences()
	{
		// All results from Meter/Pump
		m_MeterArrayListDBResultEntriesSet.addAll(m_MeterArrayListDBResultEntries);
		m_CountMeterCGMEntriesLoaded = m_MeterArrayListDBResultEntriesSet.size();

		// All results from NightScout
		m_NightScoutArrayListDBResultEntriesSet.addAll(m_NightScoutArrayListDBResultEntries);
		m_CountNightScoutCGMEntriesBefore = m_NightScoutArrayListDBResultEntriesSet.size();

		// Create a copy of Meter/Pump results for intersection
		Set<DBResultEntry> existingNSEntriesFromMeterResults = new HashSet<DBResultEntry>(m_MeterArrayListDBResultEntriesSet);
		existingNSEntriesFromMeterResults.retainAll(m_NightScoutArrayListDBResultEntriesSet);
		m_CountMeterCGMEntriesDuplicated = existingNSEntriesFromMeterResults.size();

		// Now remove all Mongo results from Meter/Pump already there.
		m_MeterArrayListDBResultEntriesSet.removeAll(m_NightScoutArrayListDBResultEntriesSet);
		m_CountMeterCGMEntriesAdded = m_MeterArrayListDBResultEntriesSet.size();

		// Andy Sherwood request - Sep 2016
		checkEntryForDuplicates();
		
		// This is where we move the additional meter DB Results to Nightscout.
		// Next operation would be to add the same DBResults
		for (DBResultEntry c : m_MeterArrayListDBResultEntriesSet)
		{
			m_NightScoutArrayListDBResultEntries.add(c);
			m_Logger.log(Level.FINER, "Difference detected: " + c.toString());
		}

		m_CountNightScoutCGMEntriesAfter = m_NightScoutArrayListDBResultEntriesSet.size();

		int cnt=0;
		// List out times before and after...
		for (DBResultEntry c : m_NightScoutArrayListDBResultEntries)
		{
			m_Logger.log(Level.FINEST, "BEFORE Epoch Millies at: " + cnt++ + " is " + c.getM_EpochMillies() + " full " + c.toString());
		}

		// Sort the Mongo Results
		Collections.sort(m_NightScoutArrayListDBResultEntries, new ResultFromDBComparator());

		m_CountNightScoutLoaderCGMEntries = 0;
		String enteredByPrefix = AuditHistory.getInstance().getM_UploadIDPrefix();
		int    length = enteredByPrefix.length();
		// List out times before and after...
		for (DBResultEntry c : m_NightScoutArrayListDBResultEntries)
		{
			m_Logger.log(Level.FINEST, "AFTER Epoch Millies at: " + cnt++ + " is " + c.getM_EpochMillies() + " full " + c.toString());

			String device = c.getM_Device(); 
			if (device.length() >= length)
			{
				String subStr    = device.substring(0, length);

				if (subStr.equals(enteredByPrefix))
				{
					m_CountNightScoutLoaderCGMEntries++;
				}
			}
		}
	}

	public void saveCGMDifferences()
	{
		if (m_MeterArrayListDBResultEntriesSet.size() > 0)
		{
			try
			{
				m_DataLoadNightScout.storeResultEntriesFromDB(m_MeterArrayListDBResultEntriesSet);
			}
			catch(Exception e)
			{
				m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + " just caught an error - line 839: " + e.getMessage() + " - " + e.getLocalizedMessage());
			}
		}
		
		if (this.m_ExistingNightScoutProximityDBResultEntriesSet.size() > 0)
		{
			try
			{
				// DAVID CHECK THIS .. MIGHT NEED A DIFFERENT CALL TO MONGO DB TO DO AN UPDATE
				m_DataLoadNightScout.storeResultsFromDB(m_ExistingNightScoutProximityDBResultsSet);
			}
			catch(Exception e)
			{
				m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + " just caught an error: " + e.getMessage() + " - " + e.getLocalizedMessage());
			}
		}

	}

	private void updateAuditHistory()
	{
		// Need to know all parameters to create an AuditLog
		// Then use AuditHistory to store result

		Date now = new Date();

		try {
			AuditHistory.getInstance().storeAuditHistory(AuditLog.m_Success, now, 
					m_DeviceUsed, m_FileName, m_DateRange, m_CountMeterEntriesAdded, 
					m_CountNightScoutEntriesBefore,	m_CountNightScoutLoaderEntries, 
					m_CountProximityMeterEntries, m_CountProximityNightScoutEntries, 
					m_CountMeterCGMEntriesAdded);
		}
		catch (UnknownHostException e) 
		{
			m_Logger.severe("Exception caught storing audit entry. " + e.getMessage());
		}
	}

	private void loadAuditHistory()
	{
		try 
		{
			AuditHistory.getInstance().loadAuditHistory();
		} 
		catch (UnknownHostException e) 
		{
			m_Logger.severe("Exception caught loading Audit history. " + e.getMessage());
		}
	}

	/**
	 * @return the m_CGMSyncPossible
	 */
	public synchronized boolean isM_CGMSyncPossible() {
		return m_CGMSyncPossible;
	}

	/**
	 * @param m_CGMSyncPossible the m_CGMSyncPossible to set
	 */
	public synchronized void setM_CGMSyncPossible(boolean m_CGMSyncPossible) {
		this.m_CGMSyncPossible = m_CGMSyncPossible;
	}

	/**
	 * @return the m_LoadRunning
	 */
	public Boolean getM_LoadRunning() {
		return m_LoadRunning;
	}

	/**
	 * @param m_LoadRunning the m_LoadRunning to set
	 */
	public void setM_LoadRunning(Boolean m_LoadRunning) {
		this.m_LoadRunning = m_LoadRunning;
	}

}
