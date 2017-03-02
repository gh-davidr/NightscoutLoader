package davidRichardson;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadHelpLauncher implements Runnable 
{
	private static final Logger m_Logger = Logger.getLogger( MyLogger.class.getName() );

	// Separate thread for Analyzer to run
	private Thread                   m_HelpLauncherThread;

	private ArrayList <String>       m_RequestList  = new ArrayList<String>();

	//	static Object                    m_Lock = new Object();
	private Object                   m_Lock;

	// Thread Synchronization
	public void waitUntilFree()
	{
		synchronized(m_Lock)
		{
			while (m_RequestList.isEmpty())
			{
				try
				{
					m_Logger.log( Level.FINE, "ThreadHelpLauncher Wait - Running & about to try lock: " + this );
					m_Lock.wait();
					m_Logger.log( Level.FINE, "ThreadHelpLauncher Wait - Running & notified : " + this );
				} 
				catch (InterruptedException e) 
				{					
					m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "ThreadHelpLauncher Wait - EXCEPTION CAUGHT.", e);
				}
			}
			m_Logger.log( Level.FINE, "ThreadHelpLauncher Wait - No longer running: " + this);
		}
	}

	public ThreadHelpLauncher()
	{
		// Start thread immediately on creation
		m_HelpLauncherThread  = new Thread(this);
		m_HelpLauncherThread.start();

		// Thread synchronization
		m_Lock            = new Object();
	}


	public synchronized void addHelpRequest(String resName) 
	{
		this.m_RequestList.add(resName);
	}

	public void run() 
	{
		// Endless loop for this thread
		while(true)
		{
			waitUntilFree();

			synchronized(m_Lock)
			{
				// Launch the analyze method
				if (!m_RequestList.isEmpty())
				{
					String file = m_RequestList.get(0);
					m_RequestList.remove(0);

					openPdf(file);

					m_Lock.notifyAll();
				}
			}
		}
	}

	public void openPdf(String pdf)
	{
		if (Desktop.isDesktopSupported())   
		{   
			m_Logger.log(Level.INFO, "Opening the help file.  Will take a few seconds ...");

			//            InputStream jarPdf = getClass().getClassLoader().getResourceAsStream(pdf);
			InputStream jarPdf = MainNightScoutLoader.class.getResourceAsStream(pdf);

			try 
			{
				File pdfTemp = new File("NightscoutLoader_Temp.pdf");
				// Extraction du PDF qui se situe dans l'archive
				FileOutputStream fos = new FileOutputStream(pdfTemp);
				while (jarPdf.available() > 0) 
				{
					fos.write(jarPdf.read());
				}   // while (pdfInJar.available() > 0)
				fos.close();
				// Ouverture du PDF
				Desktop.getDesktop().open(pdfTemp);
			}   // try

			catch (IOException e)
			{
				m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + " openPdf Exception caught opening " + pdf, e);

			}   // catch (IOException e)

			m_Logger.log(Level.INFO, "Help file opened");
		}
		
		else
		{
			m_Logger.log(Level.WARNING, "<"+this.getClass().getName()+">" + " Desktop Not Supported ");
		}

	}

}
