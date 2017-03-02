package davidRichardson;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadAutotuneRemoteSaveAs extends ThreadAutotune
{
	private static final Logger m_Logger = Logger.getLogger( MyLogger.class.getName() );

	private String m_JSON;
	private String m_LocalFile;
	
	public ThreadAutotuneRemoteSaveAs(RemoteLinuxServer autoTuner,
			String json, String localFile) 
	{
		super(autoTuner);
		m_JSON      = new String(json);
		m_LocalFile = new String(localFile);
	}

	@Override
	protected void doAutotuneTask() 
	{
		// TODO Auto-generated method stub
		try 
		{
			m_Autotuner.remoteSaveAs(m_JSON, m_LocalFile);
		} 
		catch (IOException e) 
		{
			m_Logger.log(Level.SEVERE, "ThreadAutotuneRemoteSaveAs.doAutotuneTask exception caught " + e.getMessage());
		}
	}
}
