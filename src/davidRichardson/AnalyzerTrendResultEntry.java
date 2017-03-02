package davidRichardson;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import davidRichardson.AnalyzerResultEntryInterval.DBResultEntryProfile;
import davidRichardson.AnalyzerResultEntryInterval.DBResultEntryProfileChange;
import davidRichardson.AnalyzerResultEntryInterval.DBResultEntryProfileDirection;

public class AnalyzerTrendResultEntry 
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());
	
	private static Integer      m_Static_ID = 0;  // Assign an ID to each result created.
	private int                 m_ID        = 0;  // Assign an ID to each result created.

	private ArrayList<AnalyzerResultEntryInterval>     m_ResultEntryIntervals;
	private Boolean                                    m_GoesHypo;
	private Boolean                                    m_GoesHyper;
	private DBResultEntryProfileChange                 m_DBResultEntryProfileChange;
	private DBResultEntryProfile                       m_StartProfile;
	private DBResultEntryProfile                       m_EndProfile;
	private DBResultEntryProfileDirection              m_ProfileDirection;
	
	private int                                        m_StartHour;
	private int                                        m_EndHour;
	private int                                        m_Offset;
	
	AnalyzerTrendResultEntry(boolean goesHypo, boolean goesHyper, 
			DBResultEntryProfile startProfile, DBResultEntryProfile endProfile, 
			DBResultEntryProfileDirection profileDirection,
			int startHour, int endHour, int offset)
	{
		m_Static_ID++;
		m_ID   = m_Static_ID;

		m_Logger.log(Level.FINE, "Just built AnalyzerEntriesCGMDay " + m_ID + " Profile Start: " + 
				startProfile.toString() + " Profile End: " + 
				endProfile.toString() + " Start Hour: " + startHour + " End Hour: " + endHour); 

		m_ResultEntryIntervals = new ArrayList<AnalyzerResultEntryInterval>();
		
		m_GoesHypo            = goesHypo;
		m_GoesHyper           = goesHyper;
		m_StartProfile        = startProfile;
		m_EndProfile          = endProfile;
		m_StartHour           = startHour;
		m_EndHour             = endHour;
		m_ProfileDirection    = profileDirection;
		m_Offset              = offset;
	}

	AnalyzerTrendResultEntry(DBResultEntryProfileChange profileChange, int startHour, int endHour, int offset)
	{
		m_Static_ID++;
		m_ID   = m_Static_ID;

		m_Logger.log(Level.FINE, "Just built AnalyzerEntriesCGMDay " + m_ID + " Profile: " + 
				profileChange.toString() + " Start Hour: " + startHour + " End Hour: " + endHour); 

		m_ResultEntryIntervals = new ArrayList<AnalyzerResultEntryInterval>();
		
		m_DBResultEntryProfileChange = profileChange;
		m_StartHour                  = startHour;
		m_EndHour                    = endHour;
		m_Offset                     = offset;
	}
	
	public boolean addSingleResultEntry(AnalyzerResultEntryInterval e)
	{
		boolean result = false;
		
		boolean profileDirection = e.getM_ProfileDirection() == m_ProfileDirection ? true : false;
/*		boolean goesHypo         = e.getM_GoesHypo() == m_GoesHypo ? true : false;
		boolean goesHyper        = e.getM_GoesHyper() == m_GoesHyper ? true : false;
		boolean startProfile     = e.getM_StartProfile() == m_StartProfile ? true : false;
		boolean endProfile       = e.getM_EndProfile() == m_EndProfile ? true : false;
		
		
		// We group results with different end profiles together
		boolean exactlySameProfile  = 
				(goesHypo == true && goesHyper == true && startProfile == true && 
				endProfile == true && profileDirection == true) ? true : false;*/
		
		boolean sameProfile  = (profileDirection == true) ? true : false;
		
		if ( (sameProfile) && // Same profile
				CommonUtils.get24Hour(e.getM_PeriodStart()) >= m_StartHour + m_Offset &&         // Start time
						CommonUtils.get24Hour(e.getM_PeriodEnd()) <= m_EndHour + m_Offset)
		{
			m_ResultEntryIntervals.add(e);
			e.setM_AnalyzerTrendResultEntry(this);
			result = true;
		}
		
		return result;
	}
	


	public synchronized static void resetStaticID()
	{
		m_Static_ID = 0;
	}
	
	/**
	 * @return the m_ID
	 */
	public synchronized int getM_ID() 
	{
		return m_ID;
	}

	/**
	 * @return the m_ResultEntryIntervals
	 */
	public synchronized ArrayList<AnalyzerResultEntryInterval> getM_ResultEntryIntervals() {
		return m_ResultEntryIntervals;
	}

	/**
	 * @return the m_DBResultEntryProfileChange
	 */
	public synchronized DBResultEntryProfileChange getM_DBResultEntryProfileChange() {
		return m_DBResultEntryProfileChange;
	}

	/**
	 * @return the m_StartHour
	 */
	public synchronized int getM_StartHour() {
		return m_StartHour;
	}

	/**
	 * @return the m_EndHour
	 */
	public synchronized int getM_EndHour() {
		return m_EndHour;
	}

	/**
	 * @return the m_Offset
	 */
	public synchronized int getM_Offset() {
		return m_Offset;
	}

	/**
	 * @return the m_GoesHypo
	 */
	public synchronized Boolean getM_GoesHypo() {
		return m_GoesHypo;
	}

	/**
	 * @return the m_GoesHyper
	 */
	public synchronized Boolean getM_GoesHyper() {
		return m_GoesHyper;
	}

	/**
	 * @return the m_StartProfile
	 */
	public synchronized DBResultEntryProfile getM_StartProfile() {
		return m_StartProfile;
	}

	/**
	 * @return the m_EndProfile
	 */
	public synchronized DBResultEntryProfile getM_EndProfile() {
		return m_EndProfile;
	}

	/**
	 * @return the m_ProfileDirection
	 */
	public synchronized DBResultEntryProfileDirection getM_ProfileDirection() {
		return m_ProfileDirection;
	}

	/**
	 * @return the m_ProfileDirection
	 */
	public synchronized String getM_ProfileDirectionStr(boolean mmol) 
	{
		String result = AnalyzerResultEntryInterval.getProfileDirectionStr(mmol, m_ProfileDirection);
		return result;
	}
	
}
