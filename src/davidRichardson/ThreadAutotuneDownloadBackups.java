package davidRichardson;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadAutotuneDownloadBackups extends ThreadAutotune
{
	protected static final Logger m_Logger = Logger.getLogger( MyLogger.class.getName() );

	private String m_LocalDirectory;
	
	public ThreadAutotuneDownloadBackups(RemoteLinuxServer autoTuner, String localDirectory) 
	{
		super(autoTuner);
		m_LocalDirectory = new String(localDirectory);
	}

	@Override
	protected void doAutotuneTask() 
	{
		// TODO Auto-generated method stub
		m_Autotuner.downloadAllBackupProfileFiles(m_LocalDirectory);
		try 
		{
			Runtime.getRuntime().exec("explorer.exe /select," + m_LocalDirectory);
		} 
		catch (IOException e) 
		{
			m_Logger.log( Level.SEVERE, "ThreadAutotuneDownloadBackups caught exception " + e.getMessage() );
		}
	}
}
