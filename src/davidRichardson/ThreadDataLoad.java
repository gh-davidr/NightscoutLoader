package davidRichardson;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ThreadDataLoad implements Runnable 
{
	private static final Logger m_Logger = Logger.getLogger( MyLogger.class.getName() );
	
	// Separate thread for data loads
	private Thread                   m_LoadThread;

	/**
	 * @return the m_LoadThread
	 */
	public synchronized Thread getM_LoadThread() {
		return m_LoadThread;
	}

	/**
	 * @param m_LoadThread the m_LoadThread to set
	 */
	public synchronized void setM_LoadThread(Thread m_LoadThread) {
		this.m_LoadThread = m_LoadThread;
	}

	// Data Loader used by thread
	private DataLoadBase             m_DataLoader;
	
	private Boolean                  m_LoadRunning;
//	static Object                    m_Lock = new Object();
	private Object                   m_Lock;
	
	// Thread Synchronization
	public void waitUntilFree()
	{
		synchronized(m_Lock)
		{
			while (m_LoadRunning)
			{
				try
				{
					m_Logger.log( Level.FINE, "ThreadDataLoad Wait - Running & about to try lock: " + this );
					m_Lock.wait();
					m_Logger.log( Level.FINE, "ThreadDataLoad Wait - Running & notified : " + this );
				} 
				catch (InterruptedException e) 
				{					
					m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "ThreadDataLoad Wait - EXCEPTION CAUGHT.", e);
				}
			}
			m_Logger.log( Level.FINE, "ThreadDataLoad Wait - No longer running: " + this);
		}
	}
	// Handler to notify when load completes
	DataLoadCompleteHandler           m_CompleteHandler;
	
	// Thread Handler for resynchronization
	public static abstract class DataLoadCompleteHandler 
	{
		private Object m_Object;
		public DataLoadCompleteHandler(Object obj)
		{
			m_Object = obj;
		}
		public abstract void dataLoadComplete(Object obj, String message);
		public abstract void exceptionRaised(String message);
		
		public Object getM_Object()
		{
			return m_Object;
		}
	}
	
	public ThreadDataLoad(DataLoadBase dataLoader)
	{
		m_LoadRunning     = true;  // Initialise the thread in running state
		m_LoadThread      = new Thread(this);
		m_DataLoader      = dataLoader;
		m_CompleteHandler = null;
		
		// Thread synchronization
		m_Lock            = new Object();
	}
	
	public void loadDBResults(DataLoadCompleteHandler completeHandler)
	{
		m_CompleteHandler = completeHandler;
		m_LoadThread.start();
	}
	
	public void run() 
	{
		m_LoadRunning = true;		
		synchronized(m_Lock)
		{
			// Launch the data load read method
			try 
			{
				m_DataLoader.loadDBResults();
			}

			catch (ClassNotFoundException | SQLException | IOException e) 
			{
				m_CompleteHandler.exceptionRaised("Thread Exception: " + e.getLocalizedMessage());
			}
			finally
			{
				m_CompleteHandler.dataLoadComplete(m_CompleteHandler.getM_Object(), "");
			}
			m_LoadRunning = false;
			m_Lock.notifyAll();
		}
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

	/**
	 * @return the m_DataLoader
	 */
	public synchronized DataLoadBase getM_DataLoader() {
		return m_DataLoader;
	}

	/**
	 * @param m_DataLoader the m_DataLoader to set
	 */
	public synchronized void setM_DataLoader(DataLoadBase m_DataLoader) {
		this.m_DataLoader = m_DataLoader;
	}
}
