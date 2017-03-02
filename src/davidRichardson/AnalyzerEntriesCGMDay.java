package davidRichardson;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnalyzerEntriesCGMDay
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());
	
	private static Integer      m_Static_ID = 0;  // Assign an ID to each result created.
	private int                 m_ID        = 0;  // Assign an ID to each result created.

	private Date                           m_Date;
	private ArrayList<DBResultEntry>       m_DBResultEntries;
	
	AnalyzerEntriesCGMDay(Date date)
	{
		m_Static_ID++;
		m_ID   = m_Static_ID;

		m_Logger.log(Level.FINE, "Just built AnalyzerEntriesCGMDay " + m_ID + " @" + 
				date.toString()); 

		
		m_Date = new Date(date.getTime());
		m_DBResultEntries = new ArrayList<DBResultEntry>();
	}
	
	public void addDBResultEntry(DBResultEntry e)
	{
		m_DBResultEntries.add(e);
	}

	public synchronized static void resetStaticID()
	{
		m_Static_ID = 0;
	}
	
	/**
	 * @return the m_ID
	 */
	public synchronized int getM_ID() {
		return m_ID;
	}

	
	/**
	 * @return the m_Date
	 */
	public synchronized Date getM_Date() {
		return m_Date;
	}

	/**
	 * @return the m_DBResultEntries
	 */
	public synchronized ArrayList<DBResultEntry> getM_DBResultEntries() {
		return m_DBResultEntries;
	}
}
