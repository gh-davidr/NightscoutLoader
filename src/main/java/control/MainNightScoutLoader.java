package control;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import win.WinNightScoutLoader;

public class MainNightScoutLoader implements MainControlInterface
{	
	private static final Logger   m_Logger   = Logger.getLogger(MyLogger.class.getName());
	//	private static final MyLogger m_MyLogger = new MyLogger(true);
	
	private CommandLineHandlerSingleThreaded commandLineHandler;
//	private CommandLineHandlerMultiThreaded  commandLineHandler;
	
	public MainNightScoutLoader(String[] args)
	{
		commandLineHandler = new CommandLineHandlerSingleThreaded(args, this);
//		commandLineHandler = new CommandLineHandlerMultiThreaded(args, this);
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		// Initialise the Logging mechanism.
		try 
		{
			Boolean consolLoggingBoolean = args == null || args.length == 0 ? false : true;
			
			MyLogger.setup(consolLoggingBoolean);
		}
		catch (IOException e1) 
		{
			// Can;t use the logging mechanism, so simply output to SYSTEM
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		MainNightScoutLoader mainNightScoutLoader = new MainNightScoutLoader(args);
		
		if (mainNightScoutLoader.commandLineHandler.launchWindow())
		{
			mainNightScoutLoader.launchWindow();
		}
		else 
		{
			mainNightScoutLoader.launchCommandLineLoader();
		}
	}

	private void launchWindow()
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					//http://stackoverflow.com/questions/7612592/jframe-and-nimbus-look-and-feel
					//					JFrame.setDefaultLookAndFeelDecorated(true); //before creating JFrames

					WinNightScoutLoader mainWin;
					mainWin = new WinNightScoutLoader();
					mainWin.setVisible(true);
				} 
				catch (Exception e) 
				{
					m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "Main: unhandled exception " + e.getLocalizedMessage());
					System.out.println("SEVERE ERROR : " + e.getLocalizedMessage());
				}
			}
		});
	}

	private void launchCommandLineLoader()
	{
		commandLineHandler.runWithoutWindow();
	}

	@Override
	public void shutdown()
	{
		m_Logger.log(Level.INFO, "Operation complete.  Shutting down");

		Runtime rtRuntime = Runtime.getRuntime();
		rtRuntime.exit(0);
	}

}
