package davidRichardson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import davidRichardson.DBResult.TimeSlot;

class AnalyzerDaySummary
{
	private static final Logger m_Logger    = Logger.getLogger(MyLogger.class.getName());
	private static Integer  m_Static_ID     = 0;  // Assign an ID to each result created.
	private int       m_ID                  = 0;  // Assign an ID to each result created.

	private Date      m_Date                = new Date(0);
	private String    m_DateString          = new String();
	private String    m_DayName             = new String();
	private boolean   m_BreakfastBG         = false;
	private boolean   m_LunchBG             = false;
	private boolean   m_DinnerBG            = false;
	private boolean   m_BreakfastCarbs      = false;
	private boolean   m_LunchCarbs          = false;
	private boolean   m_DinnerCarbs         = false;
	private int       m_NumHypos            = 0;
	private int       m_NumNightTests       = 0;
	private int       m_NumNightCorrections = 0;
	private int       m_NumNightHypos       = 0;
	
	private boolean   m_Relevant            = false; 
	private int       m_PossibleDuplicates  = 0;

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
	
	AnalyzerDaySummary(Date date)
	{			
		m_Static_ID++;
		m_ID = m_Static_ID;
		
		m_Date = date;
		
	    DateFormat dayformat  = new SimpleDateFormat("EEEE");
	    DateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy");

	    try 
	    {
	    	m_DayName    = dayformat.format(m_Date);
	    	m_DateString = dateformat.format(m_Date);
	    }
	    catch(Exception e) 
	    {
	    	m_DayName = "";
	    	m_DateString = "";
	    }
	}
	
	public void processSingleResult(AnalyzerSingleResult res)
	{
		// Look at the result and set flags accordingly
		TimeSlot timeslot          = res.getM_TimeSlot();
		Double   bg                = res.getM_DBResult().getM_CP_Glucose();
		Double   carbs             = res.getM_DBResult().getM_CP_Carbs();
		Double   insulin           = res.getM_DBResult().getM_CP_Insulin();
		String   badNightStartTime = PrefsNightScoutLoader.getInstance().getM_AnalyzerBadNightStartTime();
		String   badNightEndTime   = PrefsNightScoutLoader.getInstance().getM_AnalyzerBadNightEndTime();
		Double   hypoThreshold     = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowRangeThreshold();
		boolean  proximityResult   = res.getM_DBResult().isM_ProximityPossibleDuplicate();
		
		if (timeslot == TimeSlot.BreakfastTime)
		{
			m_BreakfastBG    = (bg != null ? true : m_BreakfastBG);
			m_BreakfastCarbs = (carbs != null ? true : m_BreakfastCarbs);
		}
		else if (timeslot == TimeSlot.LunchTime)
		{
			m_LunchBG    = (bg != null ? true : m_LunchBG);
			m_LunchCarbs = (carbs != null ? true : m_LunchCarbs);				
		}
		else if (timeslot == TimeSlot.DinnerTime)
		{
			m_DinnerBG    = (bg != null ? true : m_DinnerBG);
			m_DinnerCarbs = (carbs != null ? true : m_DinnerCarbs);				
		}
		else if (timeslot == TimeSlot.BedTime)
		{
			try {
				if (CommonUtils.isTimeBetween(badNightStartTime, 
						badNightEndTime,
						res.getM_DBResult().getM_Time()))
				{
					if (bg != null)
					{
						m_NumNightTests++;
						if (bg < hypoThreshold)
						{
							m_NumNightHypos++;
						}
					}
					if (insulin != null)
					{
						m_NumNightCorrections++;
					}
				}
			} 
			catch (ParseException e) 
			{
				m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+"> processSingleResult" + ". Exception caught. " + e.getMessage());
			}
		}

		if (bg != null && bg < hypoThreshold)
		{
			m_NumHypos++;
		}
		
		if (proximityResult == true)
		{
			this.m_PossibleDuplicates++;
		}
	}
	
	public void setRelevance()
	{
		// Set relevance to true if there are any bad night tests, missing BG or carbs or hypos
		// or possible duplicates
		if ( (m_NumNightTests > 0) || (m_NumHypos > 0) ||
			 (!m_BreakfastBG || !m_BreakfastCarbs || 
					 !m_LunchBG || !m_LunchCarbs || 
					 !m_DinnerBG || !m_DinnerCarbs  || m_PossibleDuplicates > 0))
		{
			m_Relevant = true;
		}
	}

	/**
	 * @return the m_Date
	 */
	public synchronized Date getM_Date() {
		return m_Date;
	}

	/**
	 * @param m_Date the m_Date to set
	 */
	public synchronized void setM_Date(Date m_Date) {
		this.m_Date = m_Date;
	}

	/**
	 * @return the m_Date
	 */
	public synchronized String getM_DateString() {
		return m_DateString;
	}

	/**
	 * @param m_Date the m_Date to set
	 */
	public synchronized void setM_DateString(String m_DateString) {
		this.m_DateString = m_DateString;
	}

	/**
	 * @return the m_DayName
	 */
	public synchronized String getM_DayName() {
		return m_DayName;
	}

	/**
	 * @param m_DayName the m_DayName to set
	 */
	public synchronized void setM_DayName(String m_DayName) {
		this.m_DayName = m_DayName;
	}

	/**
	 * @return the m_BreakfastBG
	 */
	public synchronized boolean isM_BreakfastBG() {
		return m_BreakfastBG;
	}

	/**
	 * @param m_BreakfastBG the m_BreakfastBG to set
	 */
	public synchronized void setM_BreakfastBG(boolean m_BreakfastBG) {
		this.m_BreakfastBG = m_BreakfastBG;
	}

	/**
	 * @return the m_LunchBG
	 */
	public synchronized boolean isM_LunchBG() {
		return m_LunchBG;
	}

	/**
	 * @param m_LunchBG the m_LunchBG to set
	 */
	public synchronized void setM_LunchBG(boolean m_LunchBG) {
		this.m_LunchBG = m_LunchBG;
	}

	/**
	 * @return the m_DinnerBG
	 */
	public synchronized boolean isM_DinnerBG() {
		return m_DinnerBG;
	}

	/**
	 * @param m_DinnerBG the m_DinnerBG to set
	 */
	public synchronized void setM_DinnerBG(boolean m_DinnerBG) {
		this.m_DinnerBG = m_DinnerBG;
	}

	/**
	 * @return the m_BreakfastCarbs
	 */
	public synchronized boolean isM_BreakfastCarbs() {
		return m_BreakfastCarbs;
	}

	/**
	 * @param m_BreakfastCarbs the m_BreakfastCarbs to set
	 */
	public synchronized void setM_BreakfastCarbs(boolean m_BreakfastCarbs) {
		this.m_BreakfastCarbs = m_BreakfastCarbs;
	}

	/**
	 * @return the m_LunchCarbs
	 */
	public synchronized boolean isM_LunchCarbs() {
		return m_LunchCarbs;
	}

	/**
	 * @param m_LunchCarbs the m_LunchCarbs to set
	 */
	public synchronized void setM_LunchCarbs(boolean m_LunchCarbs) {
		this.m_LunchCarbs = m_LunchCarbs;
	}

	/**
	 * @return the m_DinnerCarbs
	 */
	public synchronized boolean isM_DinnerCarbs() {
		return m_DinnerCarbs;
	}

	/**
	 * @param m_DinnerCarbs the m_DinnerCarbs to set
	 */
	public synchronized void setM_DinnerCarbs(boolean m_DinnerCarbs) {
		this.m_DinnerCarbs = m_DinnerCarbs;
	}

	/**
	 * @return the m_NumHypos
	 */
	public synchronized int getM_NumHypos() {
		return m_NumHypos;
	}

	/**
	 * @param m_NumHypos the m_NumHypos to set
	 */
	public synchronized void setM_NumHypos(int m_NumHypos) {
		this.m_NumHypos = m_NumHypos;
	}

	/**
	 * @return the m_NumNightTests
	 */
	public synchronized int getM_NumNightTests() {
		return m_NumNightTests;
	}

	/**
	 * @param m_NumNightTests the m_NumNightTests to set
	 */
	public synchronized void setM_NumNightTests(int m_NumNightTests) {
		this.m_NumNightTests = m_NumNightTests;
	}

	/**
	 * @return the m_NumNightCorrections
	 */
	public synchronized int getM_NumNightCorrections() {
		return m_NumNightCorrections;
	}

	/**
	 * @param m_NumNightCorrections the m_NumNightCorrections to set
	 */
	public synchronized void setM_NumNightCorrections(int m_NumNightCorrections) {
		this.m_NumNightCorrections = m_NumNightCorrections;
	}

	/**
	 * @return the m_NumNightHypos
	 */
	public synchronized int getM_NumNightHypos() {
		return m_NumNightHypos;
	}

	/**
	 * @param m_NumNightHypos the m_NumNightHypos to set
	 */
	public synchronized void setM_NumNightHypos(int m_NumNightHypos) {
		this.m_NumNightHypos = m_NumNightHypos;
	}

	/**
	 * @return the m_Relevant
	 */
	public synchronized boolean isM_Relevant() {
		return m_Relevant;
	}

	/**
	 * @param m_Relevant the m_Relevant to set
	 */
	public synchronized void setM_Relevant(boolean m_Relevant) {
		this.m_Relevant = m_Relevant;
	}

	/**
	 * @return the m_PossibleDuplicates
	 */
	public synchronized int getM_PossibleDuplicates() {
		return m_PossibleDuplicates;
	}

	/**
	 * @param m_PossibleDuplicates the m_PossibleDuplicates to set
	 */
	public synchronized void setM_PossibleDuplicates(int m_PossibleDuplicates) {
		this.m_PossibleDuplicates = m_PossibleDuplicates;
	}
}
