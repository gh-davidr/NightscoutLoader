package control;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import miscellaneous.FileChecker;
import win.WinNightScoutLoader;

public class CommandLineHandlerMultiThreaded
{
	private static final Logger m_Logger = Logger.getLogger(CommandLineHandlerMultiThreaded.class.getName());

	private Options m_Options = new Options();

	private WinNightScoutLoader.SupportedMeters m_SelectedMeters = WinNightScoutLoader.SupportedMeters.Unknown;
	private FileChecker.FileCheckType m_FileCheckType = FileChecker.FileCheckType.INVALID;
	private String m_FilePathString;
	private String m_ErrorMessageString;
	private MainControlInterface m_MainControlInterface;

	public CommandLineHandlerMultiThreaded(String[] args, MainControlInterface m_MainControlInterface) {
		super();

		this.m_MainControlInterface = m_MainControlInterface;

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
		
		doThreadLoadNightScout(true);
		doThreadLoadNightScoutEntries(true);
		doThreadLoadFile();
		doThreadDetermineSaveDifferences();
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

		m_Options.addOption(meterType);
		m_Options.addOption(fileOption);
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


	private void doThreadLoadNightScout(Boolean initialRun)
	{
		try
		{
			Object obj = Boolean.valueOf(initialRun);
			CoreNightScoutLoader m_NightScoutLoaderCore = CoreNightScoutLoader.getInstance();

			m_NightScoutLoaderCore.threadLoadNightScout(
					new ThreadDataLoad.DataLoadCompleteHandler(obj) 
					{
						//		@Override
						public void exceptionRaised(String message) 
						{
							m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "doThreadLoadMeterPump: Just caught an exception" + message);
						}

						//		@Override
						public void dataLoadComplete(Object obj, String message) 
						{
							m_Logger.log(Level.INFO, "Loaded treatments from Nightscout");	
						}
					});
		}

		catch(Exception e)
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + " doThreadLoadNightScout: just caught an error: " + e.getMessage() + "-" + e.getLocalizedMessage());		
		}
	}

	private void doThreadLoadNightScoutEntries(Boolean initialRun)
	{
		// We only attempt a CGM load if the preferences are enabled
		if (PrefsNightScoutLoader.getInstance().getM_LoadNightscoutEntries() == true)
		{
			try
			{
				Object obj = Boolean.valueOf(initialRun);
				CoreNightScoutLoader m_NightScoutLoaderCore = CoreNightScoutLoader.getInstance();

				m_NightScoutLoaderCore.threadLoadNightScoutEntries(
						new ThreadDataLoad.DataLoadCompleteHandler(obj) 
						{

							//		@Override
							public void exceptionRaised(String message) 
							{
								m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "doThreadLoadMeterPump: Just caught an exception" + message);
							}

							//		@Override
							public void dataLoadComplete(Object obj, String message) 
							{
								m_Logger.log(Level.INFO, "Loaded CGM entries from Nightscout");									
							}
						});	
			}

			catch(Exception e)
			{
				m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + " doThreadLoadNightScout: just caught an error: " + e.getMessage() + "-" + e.getLocalizedMessage());		
			}
		}
	}

	private void doThreadLoadFile()
	{
		try
		{
			CoreNightScoutLoader m_NightScoutLoaderCore = CoreNightScoutLoader.getInstance();
			if (m_FileCheckType == FileChecker.FileCheckType.Medtronic)
			{
				/*
				m_NightScoutLoaderCore.loadMedtronicMeterPump(m_FileNameTxtFld.getText());
				changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
				 */

				// Threaded load instead
				m_NightScoutLoaderCore.threadLoadMedtronicMeterPump(m_FilePathString,
						new ThreadDataLoad.DataLoadCompleteHandler(null) 
				{
					public void exceptionRaised(String message) { }
					public void dataLoadComplete(Object obj, String message) 
					{
						m_Logger.log(Level.INFO, "Loaded readings from Medtronic File");	
					}
				});
			}
			else if (m_FileCheckType == FileChecker.FileCheckType.Diasend)
			{
				/*
					m_NightScoutLoaderCore.loadDiasendMeterPump(m_FilePathString);
					changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
				 */

				// Threaded load instead
				m_NightScoutLoaderCore.threadLoadDiasendMeterPump(m_FilePathString,
						new ThreadDataLoad.DataLoadCompleteHandler(null) 
				{
					public void exceptionRaised(String message) { }
					public void dataLoadComplete(Object obj, String message) 
					{ 
						m_Logger.log(Level.INFO, "Loaded readings from Diasend File");
					}
				});
			}

			else if (m_FileCheckType == FileChecker.FileCheckType.LibreView)
			{
				/*
					m_NightScoutLoaderCore.loadDiasendMeterPump(m_FilePathString);
					changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
				 */

				// Threaded load instead
				m_NightScoutLoaderCore.threadLoadLibreView(m_FilePathString,
						new ThreadDataLoad.DataLoadCompleteHandler(null) 
				{
					public void exceptionRaised(String message) { }
					public void dataLoadComplete(Object obj, String message) 
					{ 
						m_Logger.log(Level.INFO, "Loaded readings from LibreView File");
					}
				});
			}

			else if (m_FileCheckType == FileChecker.FileCheckType.Tandem)
			{
				m_NightScoutLoaderCore.threadLoadTandemMeterPump(m_FilePathString,
						new ThreadDataLoad.DataLoadCompleteHandler(null) 
				{
					public void exceptionRaised(String message) { }
					public void dataLoadComplete(Object obj, String message) 
					{
						m_Logger.log(Level.INFO, "Loaded readings from Tandem File");

					}
				});	
			}

			else if (m_FileCheckType == FileChecker.FileCheckType.CellNovo)
			{
				m_NightScoutLoaderCore.threadLoadCellNovoMeterPump(m_FilePathString,
						new ThreadDataLoad.DataLoadCompleteHandler(null) 
				{
					public void exceptionRaised(String message) { }
					public void dataLoadComplete(Object obj, String message) 
					{
						m_Logger.log(Level.INFO, "Loaded readings from CellNovo File");

					}
				});	
			}

		}
		catch (Exception e)
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "doThreadFile: Just caught an exception" + e.getMessage() + "-" + e.getLocalizedMessage());		
		}

	}

	private void doThreadDetermineSaveDifferences()
	{
		try
		{

			CoreNightScoutLoader m_NightScoutLoaderCore = CoreNightScoutLoader.getInstance();
			m_NightScoutLoaderCore.threadDetermineSaveDifferences(
					new ThreadDetermineSaveDifferences.DataLoadCompleteHander(m_MainControlInterface) 
					{

						//		@Override
						public void exceptionRaised(String message) 
						{
							m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "doThreadDetermineSaveDifferences: Just caught an exception" + message);
							
							new Exception().printStackTrace();

						}

						//		@Override
						public void operationComplete(Object obj, String message)
						{
							MainControlInterface mainControlInterface = (MainControlInterface)obj;

							m_Logger.log(Level.INFO, "Differences all saved");
							mainControlInterface.shutdown();
						}
					});	
		}
		catch(Exception e)
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "doThreadDetermineSaveDifferences: Just caught an exception" + e.getMessage() + "-" + e.getLocalizedMessage());		
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
