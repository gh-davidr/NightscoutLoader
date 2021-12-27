package control;



import java.io.IOException;

// Single threaded version of CLI

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import loader.DataLoadBase;
import loader.DataLoadCellNovo;
import loader.DataLoadDiasend;
import loader.DataLoadFile;
import loader.DataLoadLibreView;
import loader.DataLoadMedtronic;
import loader.DataLoadNightScoutEntries;
import loader.DataLoadNightScoutTreatments;
import loader.DataLoadTandem;
import miscellaneous.FileChecker;
import win.WinNightScoutLoader;

public class CommandLineHandlerSingleThreaded
{
	private static final Logger m_Logger = Logger.getLogger(CommandLineHandlerSingleThreaded.class.getName());

	private Options m_Options = new Options();

	private WinNightScoutLoader.SupportedMeters m_SelectedMeters = WinNightScoutLoader.SupportedMeters.Unknown;
	private FileChecker.FileCheckType m_FileCheckType = FileChecker.FileCheckType.INVALID;
	private String  m_FilePathString;
	private String  m_ErrorMessageString;
	private String  m_MongoServer;
	private String  m_MongoDB;
	private Integer m_WeeksToLoadLong;
	private DataLoadMedtronic            m_DataLoadMedtronic;
	private DataLoadNightScoutTreatments m_DataLoadNightScout;
	private DataLoadNightScoutEntries    m_DataLoadNightScoutEntries;
	private DataLoadDiasend              m_DataLoadDiasend;
	private DataLoadLibreView            m_DataLoadLibreView;
	private DataLoadTandem               m_DataLoadTandem;
	private DataLoadBase                 m_DataLoadMeter;

	private DataLoadCellNovo             m_DataLoadCellNovo;

	public CommandLineHandlerSingleThreaded(String[] args, MainControlInterface m_MainControlInterface) {
		super();

		m_DataLoadMedtronic         = new DataLoadMedtronic();
		m_DataLoadNightScout        = new DataLoadNightScoutTreatments();
		m_DataLoadNightScoutEntries = new DataLoadNightScoutEntries();
		m_DataLoadDiasend           = new DataLoadDiasend();   
		m_DataLoadLibreView         = new DataLoadLibreView();   
		m_DataLoadTandem            = new DataLoadTandem();
		m_DataLoadCellNovo          = new DataLoadCellNovo();
		m_DataLoadMeter             = null;

		initialiseOptions();
		parseOptions(args);
	}

	public Boolean launchWindow()
	{
		Boolean resultBoolean = m_SelectedMeters == WinNightScoutLoader.SupportedMeters.Unknown && m_FilePathString == null;
		return resultBoolean;
	}

	public void runWithoutWindow() {
		setMongoLogLevel();
		setMongoDB();
		loadDataInSingleThread();
	}

	private void initialiseOptions()
	{
		Option meterType = Option.builder("m")
				.argName("Meter")
				.hasArg()
				.required(false)
				.desc("Selected Meter")
				.build();
		Option fileOption = Option.builder("f")
				.argName("File")
				.hasArg()
				.required(false)
				.desc("File with meter data")
				.build();
		Option serverOption = Option.builder("s")
				.argName("MongoServer")
				.hasArg()
				.required(false)
				.desc("Nightscout Server URL")
				.build();
		Option dbOption = Option.builder("d")
				.argName("MongoDB")
				.hasArg()
				.required(false)
				.desc("Nightscout Server DB")
				.build();
		Option weeksOption = Option.builder("w")
				.argName("WeeksToLoad")
				.hasArg()
				.required(false)
				.desc("Number of weeks to load")
				.build();

		m_Options.addOption(meterType);
		m_Options.addOption(fileOption);
		m_Options.addOption(serverOption);
		m_Options.addOption(dbOption);
		m_Options.addOption(weeksOption);
	}

	private void parseOptions(String[] args)
	{
		CommandLine cmd;
		CommandLineParser parser = new DefaultParser();
		HelpFormatter helper = new HelpFormatter();

		try {
			cmd = parser.parse(m_Options, args);
			if(cmd.hasOption("m")) {
				setMeter(cmd.getOptionValue("m"));
			}

			if (cmd.hasOption("f")) {
				m_FilePathString = cmd.getOptionValue("f");
			}
			
			if (cmd.hasOption("s")) {
				m_MongoServer = cmd.getOptionValue("s");
			}
			if (cmd.hasOption("d")) {
				m_MongoDB = cmd.getOptionValue("d");
			}
			if (cmd.hasOption("w")) {
				String weeks = cmd.getOptionValue("w");
				m_WeeksToLoadLong = Integer.parseInt(weeks);
			}
		} catch (ParseException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pwPrintWriter = new PrintWriter(sw);
			helper.printUsage(pwPrintWriter, 80, "NightScoutLoader", m_Options);

			m_ErrorMessageString = sw.toString();
		}

	}

	private void setMeter(String meterString) {

		// Apply some logic to workout what meter is actually selected.
		String sanitisedMeterString = meterString.replaceAll(" ", "").toUpperCase();

		if (sanitisedMeterString.contains("DIASEND")) 
		{
			m_SelectedMeters = WinNightScoutLoader.SupportedMeters.Diasend;
			m_FileCheckType =  FileChecker.FileCheckType.Diasend;
		}
		else if (sanitisedMeterString.contains("FREE") || sanitisedMeterString.contains("LIBRE"))
		{
			m_SelectedMeters = WinNightScoutLoader.SupportedMeters.LibreView;
			m_FileCheckType =  FileChecker.FileCheckType.LibreView;
		}
		else if (sanitisedMeterString.contains("MED")) 
		{
			m_SelectedMeters = WinNightScoutLoader.SupportedMeters.Medtronic;
			m_FileCheckType =  FileChecker.FileCheckType.Medtronic;
		}
		else if (sanitisedMeterString.contains("CELL")) 
		{
			m_SelectedMeters = WinNightScoutLoader.SupportedMeters.CellNovo;
			m_FileCheckType =  FileChecker.FileCheckType.CellNovo;
		}
		else if (sanitisedMeterString.contains("TAND")) 
		{
			m_SelectedMeters = WinNightScoutLoader.SupportedMeters.Tandem;
			m_FileCheckType =  FileChecker.FileCheckType.Tandem;
		}

		else m_ErrorMessageString = "Unknown supplied meter '" + meterString + "'";
	}

	private void loadDataInSingleThread()
	{
		loadData(m_DataLoadNightScout);
		loadData(m_DataLoadNightScoutEntries);
		loadData(m_DataLoadDiasend, m_FileCheckType == FileChecker.FileCheckType.Diasend);
		loadData(m_DataLoadLibreView, m_FileCheckType == FileChecker.FileCheckType.LibreView);
		loadData(m_DataLoadTandem, m_FileCheckType == FileChecker.FileCheckType.Tandem);
		loadData(m_DataLoadCellNovo, m_FileCheckType == FileChecker.FileCheckType.CellNovo);
		loadData(m_DataLoadMedtronic, m_FileCheckType == FileChecker.FileCheckType.Medtronic);

		m_DataLoadMeter = m_FileCheckType == FileChecker.FileCheckType.Diasend ? m_DataLoadDiasend : m_DataLoadMeter;
		m_DataLoadMeter = m_FileCheckType == FileChecker.FileCheckType.LibreView ? m_DataLoadLibreView : m_DataLoadMeter;
		m_DataLoadMeter = m_FileCheckType == FileChecker.FileCheckType.Tandem ? m_DataLoadTandem : m_DataLoadMeter;
		m_DataLoadMeter = m_FileCheckType == FileChecker.FileCheckType.CellNovo ? m_DataLoadCellNovo : m_DataLoadMeter;
		m_DataLoadMeter = m_FileCheckType == FileChecker.FileCheckType.Medtronic ? m_DataLoadMedtronic : m_DataLoadMeter;

		ThreadDetermineSaveDifferences threadDetermineSaveDifferences = 
				new ThreadDetermineSaveDifferences(
						m_DataLoadNightScout,
						m_DataLoadMeter.getResultTreatments()	/*m_MeterArrayListDBResults*/,
						m_DataLoadNightScout.getResultsFromDB() /*m_NightScoutArrayListDBResults*/,
						m_DataLoadMeter.getRawEntryResultsFromDB()/*m_MeterArrayListDBResultEntries*/,
						m_DataLoadNightScoutEntries.getResultsFromDB()/*m_NightScoutArrayListDBResultEntries*/,

						null /*m_ThreadDataLoadNightScout*/,
						null /*m_ThreadDataLoadNightScoutEntries*/,
						null /*m_ThreadDataMeterLoad*/,
						"" /*m_DeviceUsed*/,
						m_FilePathString /*m_FileName*/,
						"" /*m_DateRange*/);
		threadDetermineSaveDifferences.run();
	}


	private void loadData(DataLoadBase loader)
	{
		try 
		{
			loader.loadDBResults();
			m_Logger.log(Level.INFO, loader.getClass().getName() + " has loaded: " + loader.getResultTreatments().size());
		} 			
		catch (ClassNotFoundException | SQLException | IOException e) 
		{
			m_Logger.log(Level.SEVERE, "<"+loader.getClass().getName()+">" + 
					"Just caught an exception" + e.getLocalizedMessage());

		}
	}

	private void loadData(DataLoadFile loader, Boolean onlyIfTrue)
	{
		if (onlyIfTrue)
		{
			try 
			{
				loader.loadDBResults(m_FilePathString);
			} 			
			catch (ClassNotFoundException | SQLException | IOException e) 
			{
				m_Logger.log(Level.SEVERE, "<"+loader.getClass().getName()+">" + 
						"Just caught an exception" + e.getLocalizedMessage());

			}
		}
	}

	private void setMongoLogLevel()
	{
//		 Logger mongoLogger = Logger.getLogger( "com.mongodb.diagnostics.logging.JULLogger" ); 
//		 Logger mongoLogger = Logger.getLogger("com.mongodb"); 
//		 Logger mongoLogger = Logger.getLogger("loader.DataLoadNightScout");
//		 Logger mongoLogger = Logger.getLogger( "cluster" ); 
		 setMongoLogLevel("org.mongodb.driver.cluster", Level.SEVERE);
		 setMongoLogLevel("org.mongodb.driver.connection", Level.SEVERE);
	}
	
	private void setMongoDB()
	{
		PrefsNightScoutLoader.getInstance().setM_NightscoutMongoServer(m_MongoServer);
		PrefsNightScoutLoader.getInstance().setM_NightscoutMongoDB(m_MongoDB);
		
		// Set back to 
		PrefsNightScoutLoader.getInstance().setM_WeeksBackToLoadEntries(m_WeeksToLoadLong);
	}
	
	private void setMongoLogLevel(String className, Level level)
	{
		 Logger logger = Logger.getLogger( className ); 
		 logger.setLevel(level);
		 
		 m_Logger.log(Level.INFO, "Set " + className + " log level to: " + level.toString());
	}
	
	/**
	 * @return the m_SelectedMeters
	 */
	public synchronized WinNightScoutLoader.SupportedMeters getM_SelectedMeters() {
		return m_SelectedMeters;
	}

	/**
	 * @return the m_FilePathString
	 */
	public synchronized String getM_FilePathString() {
		return m_FilePathString;
	}

	/**
	 * @return the m_ErrorMessageString
	 */
	public synchronized String getM_ErrorMessageString() {
		return m_ErrorMessageString;
	}

}
