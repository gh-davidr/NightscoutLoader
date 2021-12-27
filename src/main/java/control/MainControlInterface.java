package control;

public interface MainControlInterface 
{
	// Simple interface that allows the command line mechanism to exit cleanly
	// once all threads are complete
	public void shutdown();
}
