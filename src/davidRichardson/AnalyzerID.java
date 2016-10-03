package davidRichardson;

public class AnalyzerID 
{
	private int  m_ID = 0;  // Assign an ID to each result created.

	AnalyzerID(Integer static_ID)
	{
		synchronized(static_ID)
		{
			static_ID++;
			m_ID = static_ID;
		}
	}

	/**
	 * @return the m_ID
	 */
	public synchronized int getM_ID() {
		return m_ID;
	}
}
