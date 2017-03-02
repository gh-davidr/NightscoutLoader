package davidRichardson;

public class ThreadAutotuneRun extends ThreadAutotune 
{

	public ThreadAutotuneRun(RemoteLinuxServer autoTuner) 
	{
		super(autoTuner);
	}

	@Override
	protected void doAutotuneTask() 
	{
		// TODO Auto-generated method stub
		m_Autotuner.runAutotune();
	}
}

