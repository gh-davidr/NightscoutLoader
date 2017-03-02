package davidRichardson;

public class ThreadAutotuneListBackups extends ThreadAutotune
{
	public ThreadAutotuneListBackups(RemoteLinuxServer autoTuner) 
	{
		super(autoTuner);
	}

	@Override
	protected void doAutotuneTask() 
	{
		// TODO Auto-generated method stub
		m_Autotuner.listBackupDirectory();
	}
}
