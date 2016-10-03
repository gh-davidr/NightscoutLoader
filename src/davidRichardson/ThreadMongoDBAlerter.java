package davidRichardson;

import java.io.IOException;
import java.util.Date;
//import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

// Purpose of this class is to alert the application on any updates to the underlying
// MongoDB
// Abstract class since there are two primary intended uses - one on treatments one on sensor results
public abstract class ThreadMongoDBAlerter implements Runnable 
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
	protected DataLoadNightScout     m_DataLoader;
	protected Date                   m_LastResultAt;
	protected Date                   m_CurrentResultAt;
	protected String                 m_CurrentResultBy;

	private Boolean                  m_LoadRunning;
	private Object                   m_Lock;

	private Boolean                  m_FirstCheckComplete;

	private int                      m_SleepInterval;


	// Abstract mmethod that must be overridden
	protected abstract void checkDBForUpdates() throws IOException, ParseException;

	protected abstract String whatIsChecked();

	protected void compareAndNotify()
	{
		final Date epoch = new Date(0);

		m_Logger.log(Level.FINE, "Thread check on " + whatIsChecked() + " on MongoDB. Most recent date/time "
				+ m_CurrentResultAt.toString() + " by '" +  m_CurrentResultBy + "'");

		if (!m_CurrentResultAt.equals(epoch) && m_CurrentResultAt.after(m_LastResultAt))
		{
			// Check if this is the first change detected
			// Could be start up or a delete
			if (m_FirstCheckComplete == true)
			{
				// Something has changed, generate an info message
				m_Logger.log(Level.INFO, "Update detected to " + whatIsChecked() + " on MongoDB. Update at "
						+ m_CurrentResultAt.toString() + " by '" +  m_CurrentResultBy + "'");
			}
			m_FirstCheckComplete = true;
		}
		m_LastResultAt = m_CurrentResultAt;
		// Reset "by"
		m_CurrentResultBy = "";
	}

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
	DataLoadCompleteHander           m_CompleteHandler;

	// Thread Handler for resynchronization
	public static abstract class DataLoadCompleteHander 
	{
		private Object m_Object;
		public DataLoadCompleteHander(Object obj)
		{
			m_Object = obj;
		}
		public abstract void dataLoadComplete(Object obj);
		public abstract void exceptionRaised(String message);

		public Object getM_Object()
		{
			return m_Object;
		}
	}

	public ThreadMongoDBAlerter()
	{
		m_LoadRunning     = true;  // Initialise the thread in running state
		m_LoadThread      = new Thread(this);
		m_DataLoader      = new DataLoadNightScout();
		m_LastResultAt    = new Date(0); // Initialized to epoch time
		m_CurrentResultAt = new Date(0); // Initialized to epoch time
		m_CurrentResultBy = new String();

		m_CompleteHandler = null;

		// Thread synchronization
		m_Lock            = new Object();

		m_FirstCheckComplete      = false; // Initialize to false until first check

		// Set for 10 minutes by default but now from preferences
		// m_SleepInterval   = 60 * 10;
		m_SleepInterval   = PrefsNightScoutLoader.getInstance().getM_MongoDBAlerterCheckInterval() * 60 * 10;
	}

	public void interrupThread()
	{
		m_LoadThread.interrupt();
	}

	public void startThread()
	{
		//		m_CompleteHandler = completeHandler;		

		m_LoadThread.start();
	}

	public void run() 
	{
		m_LoadRunning = true;		
		synchronized(m_Lock)
		{
			try 
			{
				// Now if preference is set to 0 in options and then changed, it will have no effect
				// the thread is effectively dead and never re-created.  THis is fine.  Expect user to
				// relaunch application to reenable alerting
				// Similarly, if value is changed, change only takes effect on next launch
				if (m_SleepInterval > 0)
				{
					// Go into an endless loop, checking for updates then sleeping in between
					while (!Thread.interrupted())
					{
						// Call MongoDB looking for any updates.
						// Then block
						// 
						// Looked at below, but it requires particular types of Documents.
						// http://tugdualgrall.blogspot.co.uk/2015/01/how-to-create-pubsub-application-with.html?m=1
						// Instead, simply get latest date from DataLoader, check against last date then notify

						checkDBForUpdates();

						compareAndNotify();

						// Sleep for a minute before checking again.
						Thread.sleep(m_SleepInterval * 1000);
					}
				}
			}

			catch (IOException | InterruptedException | ParseException e) 
			{
				if (m_CompleteHandler != null)
				{
					m_CompleteHandler.exceptionRaised("Thread Exception: " + e.getLocalizedMessage());
				}
			}
			finally
			{
				if (m_CompleteHandler != null)
				{
					m_CompleteHandler.dataLoadComplete(m_CompleteHandler.getM_Object());
				}
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

}
