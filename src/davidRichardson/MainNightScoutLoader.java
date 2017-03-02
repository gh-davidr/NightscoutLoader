package davidRichardson;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainNightScoutLoader
{	
	private static final Logger   m_Logger   = Logger.getLogger(MyLogger.class.getName());
//	private static final MyLogger m_MyLogger = new MyLogger(true);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		// Initialise the Logging mechanism.
		try 
		{
			MyLogger.setup();
		}
		catch (IOException e1) 
		{
			// Can;t use the logging mechanism, so simply output to SYSTEM
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
				}
			}
		});
	}

}
