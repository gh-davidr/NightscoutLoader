package davidRichardson;

import java.util.logging.Level;
import java.util.logging.Logger;

abstract
public class ThreadAutotune implements Runnable 
{
	protected static final Logger m_Logger = Logger.getLogger( MyLogger.class.getName() );
	
	// Separate thread for RemoteLinuxServer
	protected Thread                   m_AututuneThread;

	// Data Loader used by thread
	protected RemoteLinuxServer                 m_Autotuner;
	
	protected Boolean                  m_LoadRunning;
//	static Object                    m_Lock = new Object();
	protected Object                   m_Lock;
	
	// Sub classes must override this
	abstract protected void doAutotuneTask();
	
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
	AutotuneCompleteHandler           m_CompleteHandler;
	
	// Thread Handler for resynchronization
	public static abstract class AutotuneCompleteHandler 
	{
		private Object m_Object;
		public AutotuneCompleteHandler(Object obj)
		{
			m_Object = obj;
		}
		public abstract void runAutotuneComplete(Object obj, String message);
		public abstract void exceptionRaised(String message);
		
		public Object getM_Object()
		{
			return m_Object;
		}
	}
	
	public ThreadAutotune(RemoteLinuxServer autoTuner)
	{
		m_LoadRunning     = true;  // Initialise the thread in running state
		m_AututuneThread  = new Thread(this);
		m_Autotuner       = autoTuner;
		m_CompleteHandler = null;
		
		// Thread synchronization
		m_Lock            = new Object();
	}
	
	public void runThreadCommand(AutotuneCompleteHandler completeHandler)
	{
		m_CompleteHandler = completeHandler;
		m_AututuneThread.start();
	}
	
	public void run() 
	{
		m_LoadRunning = true;		
		synchronized(m_Lock)
		{
			// Launch the data load read method
			try 
			{
				doAutotuneTask();
			}

			catch (Exception e) 
			{
				m_CompleteHandler.exceptionRaised("Thread Exception: " + e.getLocalizedMessage());
			}
			finally
			{
				m_CompleteHandler.runAutotuneComplete(m_CompleteHandler.getM_Object(), "");
			}
			m_LoadRunning = false;
			m_Lock.notifyAll();
		}
	}

	/**
	 * @return the m_AututuneThread
	 */
	public synchronized Thread getM_AututuneThread() {
		return m_AututuneThread;
	}

	/**
	 * @param m_AututuneThread the m_AututuneThread to set
	 */
	public synchronized void setM_AututuneThread(Thread m_AututuneThread) {
		this.m_AututuneThread = m_AututuneThread;
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
	 * @return the m_Autotuner
	 */
	public synchronized RemoteLinuxServer getM_Autotuner() {
		return m_Autotuner;
	}

	/**
	 * @param m_Autotuner the m_Autotuner to set
	 */
	public synchronized void setM_Autotuner(RemoteLinuxServer m_Autotuner) {
		this.m_Autotuner = m_Autotuner;
	}
}
