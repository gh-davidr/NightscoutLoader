package davidRichardson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
//import java.io.FileWriter;
//import java.io.PrintWriter;
import java.io.IOException;

import java.awt.EventQueue;

//import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
//import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import javax.swing.JTextArea;

/*
 * 
 * Useful resource on Logger:
 * 
 * http://tutorials.jenkov.com/java-logging/logger.html
 * 
 */

public class MyLogger 
{
	private static final Logger m_Logger = Logger.getLogger( MyLogger.class.getName() );

	// New TextAreaHandler for sending text to the TextArea
	public class TextAreaHandler extends StreamHandler 
	{
		JTextArea textArea = null;
		String    text = new String();
		ArrayList<String>     m_PendingMessages = new ArrayList<String>();

		public void setTextArea(JTextArea textArea) 
		{
			this.textArea = textArea;
		}

		@Override
		public void publish(LogRecord record) 
		{
			super.publish(record);
			flush();

			if (textArea != null) 
			{
				m_PendingMessages.add(new String(getFormatter().format(record)));				
				// Swing is not threadsafe, so add a request to update the grid onto the even queue
				// Found this technique here:
				// http://www.informit.com/articles/article.aspx?p=26326&seqNum=9
				EventQueue.invokeLater(new 
						Runnable()
				{ 
					public void run()
					{ 
						appendJTextArea();
					}
				});

			}
		}

		private void appendJTextArea()
		{
			// Take first item if there is one
			// This is a FIFO queue for messages
			if (textArea != null && m_PendingMessages.size() > 0)
			{
				String text = (String)m_PendingMessages.get(0);
				textArea.append(text);
				
				// Remove it too.
				m_PendingMessages.remove(0);
			}
		}

	}
	

	enum MyLevel
	{
		Critical,
		Major,
		Warning,
		Informational,
		Debug,
		VerboseDebug
	};

	static private FileHandler fileTxt;
	static private boolean    m_Initialized = false;

	public MyLogger(boolean var)
	{

	}

	public void initialize()
	{
		try {
			if (m_Initialized == false)
			{
				setup();
			}
		} catch (IOException e) 
		{
			// Cant do much else here!
			e.printStackTrace();
		}
	}

	// Separate initialize method that can throw exception
	public void release() throws IOException
	{

	}

	private static class MyCustomTextFormatter extends Formatter 
	{
		@Override
		public String format(LogRecord record) 
		{
			final DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
			Date now = new Date();
			
			StringBuffer sb = new StringBuffer();
			sb.append(df.format(now));
			sb.append(" : ");
			sb.append(record.getLevel().toString().substring(0, 3)); // First 3 characters
			
			if (record.getLevel() == Level.FINE)
			{
				sb.append("E");
			}
			else if (record.getLevel() == Level.FINER)
			{
				sb.append("R");
			}
			else if (record.getLevel() == Level.FINEST)
			{
				sb.append("T");
			}
			else if (record.getLevel() == Level.INFO)
			{
				sb.append("O");
			}
			else if (record.getLevel() == Level.SEVERE)
			{
				sb.append("R");
			}
			else if (record.getLevel() == Level.WARNING)
			{
				sb.append("G");
			}
// Want to get last character  !!			sb.append(record.getLevel().toString().charAt(record.getLevel().toString().)
			sb.append(" : ");
			sb.append(record.getMessage());
			sb.append("\n");
			return sb.toString();
		}
	}
	private static class MyCustomJTextFormatter extends Formatter 
	{
		// Absolutely no frills :-)
		@Override
		public String format(LogRecord record) 
		{
			final DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
			Date now = new Date();
			
			StringBuffer sb = new StringBuffer();
			
			// Include some detail on severity
			if (record.getLevel() == Level.FINE)
			{
				sb.append(df.format(now));
				sb.append(" : ");
				sb.append(record.getLevel().toString().substring(0, 3)); // First 3 characters
				sb.append("E : ");
			}
			else if (record.getLevel() == Level.FINER)
			{
				sb.append(df.format(now));
				sb.append(" : ");
				sb.append(record.getLevel().toString().substring(0, 3)); // First 3 characters
				sb.append("R : ");
			}
			else if (record.getLevel() == Level.FINEST)
			{
				sb.append(df.format(now));
				sb.append(" : ");
				sb.append(record.getLevel().toString().substring(0, 3)); // First 3 characters
				sb.append("T : ");
			}
			else if (record.getLevel() == Level.SEVERE)
			{
				sb.append(df.format(now));
				sb.append(" : ");
				sb.append(record.getLevel().toString().substring(0, 3)); // First 3 characters
				sb.append("R : ");
			}
			else if (record.getLevel() == Level.WARNING)
			{
				sb.append(df.format(now));
				sb.append(" : ");
				sb.append(record.getLevel().toString().substring(0, 3)); // First 3 characters
				sb.append("G : ");
			}

			sb.append(record.getMessage());
			sb.append("\n");
			return sb.toString();
		}
	}


	static public void setup() throws IOException 
	{
		m_Initialized = true;

		// get the global logger to configure it
//		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

		// suppress the logging output to the console
		Logger rootLogger = Logger.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		if (handlers[0] instanceof ConsoleHandler) 
		{
			rootLogger.removeHandler(handlers[0]);
		}
		
		// Set log level based on preference
		// Changed now to 
		//  Regular
		//  Detailed
		//  Most Detailed
		int logLevelIndex = PrefsNightScoutLoader.getInstance().getM_LogLevel();
		switch (logLevelIndex)
		{
		case 0 : m_Logger.setLevel(Level.INFO);   break;  // Regular
		case 1 : m_Logger.setLevel(Level.FINE);   break;  // Detailed
		case 2 : m_Logger.setLevel(Level.FINER);  break;  // More Detailed		
		case 3 : m_Logger.setLevel(Level.FINEST); break;  // Most Detailed
 
		default: m_Logger.setLevel(Level.INFO);   break;
		}

//		logger.setLevel(Level.INFO);
		fileTxt = new FileHandler(PrefsNightScoutLoader.getInstance().getM_LogFile(), true);

		// create a TXT formatter
//		formatterTxt = new SimpleFormatter();
//		fileTxt.setFormatter(formatterTxt);
		// Use our own formatter
		fileTxt.setFormatter(new MyCustomTextFormatter());

		m_Logger.addHandler(fileTxt);

	}

	// Allow the addition of JTextArea handlers
	public void addJtextAreaOutput(JTextArea textArea)
	{
		// get the global logger to configure it
//		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

//		m_LoggerClass.setLevel(Level.INFO);
		
		TextAreaHandler textAreaHander = new TextAreaHandler();
		textAreaHander.setTextArea(textArea);
		textAreaHander.setFormatter(new MyCustomJTextFormatter());
		m_Logger.addHandler(textAreaHander);
	}

	
	public synchronized void log(MyLevel logLevel, String mesg)
	{
//		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

		// Generate current time and add that to message if level is lower than preference
//		int setLevel = PrefsNightScoutLoader.getInstance().getM_LogLevel();
//		int level    = 0;

		switch (logLevel)
		{
		case Critical      : m_Logger.severe(mesg);  break;
		case Major         : m_Logger.warning(mesg); break;
		case Warning       : m_Logger.warning(mesg); break;
		case Informational : m_Logger.info(mesg);    break;
		case Debug         : m_Logger.fine(mesg);    break;
		case VerboseDebug  : m_Logger.finest(mesg);  break;
		default : m_Logger.info(mesg); break;
		}
	}

	//	
	//	/*
	//	 * Levels:
	//	 * 
	//	 *   0  -  Critical Error
	//	 *   1  -  Major Error
	//	 *   .
	//	 *   .
	//	 *   9  -  Warning
	//	 *   10 -  Verbose Message
	//	 */
	//	
	//	enum MyLevel
	//	{
	//		Critical,
	//		Major,
	//		Warning,
	//		Informational,
	//		Debug,
	//		VerboseDebug
	//	};
	//
	//	// Single shared FileWriter
	//	static private FileWriter m_FileWriter = null;
	//	
	//	// Instance driven flag to use stdio too
	//	private boolean m_UseSysOut = false;
	//	
	//
	//	public MyLogger(boolean useSysOut)
	//	{
	//		m_UseSysOut = useSysOut;
	//	}
	//	
	//	// Separate initialize method that can throw exception
	//	public void initialize() throws IOException
	//	{
	//		if (m_FileWriter == null)
	//		{
	//			m_FileWriter = new FileWriter(PrefsNightScoutLoader.getInstance().getM_LogFile(), true);
	//		}
	//	}
	//	
	//	// Separate initialize method that can throw exception
	//	public void release() throws IOException
	//	{
	//		// Close file & release resource in case something else tries logging again.
	//		if (m_FileWriter != null)
	//		{
	//			PrintWriter print_line = new PrintWriter(m_FileWriter);
	//			print_line.close();
	//			m_FileWriter = null;
	//		}
	//	}
	//
	//	// Handles all logging throughout the application
	////	public void log(String mesg, int level)
	//	public void log(MyLevel logLevel, String mesg)
	//	{
	//		// Generate current time and add that to message if level is lower than preference
	//		int setLevel = PrefsNightScoutLoader.getInstance().getM_LogLevel();
	//		int level    = 0;
	//
	//		switch (logLevel)
	//		{
	//			case Critical      : level = 0; break;
	//			case Major         : level = 2; break;
	//			case Warning       : level = 4; break;
	//			case Informational : level = 6; break;
	//			case Debug         : level = 8; break;
	//			case VerboseDebug  : level = 10; break;
	//			default : level = 5; break;
	//		}
	//
	//		if (m_FileWriter != null && level <= setLevel)
	//		{
	//			final DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
	//			Date now = new Date();			
	//			String logMessage = df.format(now) + " Lvl:" + level + " " + mesg;
	//			
	//			// To Do ..
	//			//
	//			//  1 hold a handle to the file
	//			//  2 Write contents to the file
	//			PrintWriter print_line = new PrintWriter(m_FileWriter);
	//			print_line.println(logMessage);
	//			
	//			if (m_UseSysOut || level <= 4)
	//			{
	//				System.out.println(logMessage);
	//			}
	//		}
	//	}
}
