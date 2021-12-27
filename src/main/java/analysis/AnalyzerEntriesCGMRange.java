package analysis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import control.MyLogger;
import entity.DBResultEntry;
import utils.CommonUtils;

//import davidRichardson.AnalyzerEntries.AnalyzerEntriesCGMDay;

public class AnalyzerEntriesCGMRange
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	private static Integer      m_Static_ID = 0;  // Assign an ID to each result created.
	private int                 m_ID        = 0;  // Assign an ID to each result created.

	public enum DateOverlap
	{
		InRange,
		StartsBeforeTreatments,
		EndsAfterTreatments,
		StartsAndEndsBeforeTreatments,
		StartsAndEndsAfterTreatments,
	};

	private Date                             m_StartDate;
	private Date                             m_EndDate;
	private long                             m_Duration;
	private int                              m_AverageDBResultEntries;
	private int                              m_NumHypos   = 0;
	private int                              m_NumHypers  = 0;
	private int                              m_NumInRange = 0;
	private ArrayList<AnalyzerEntriesCGMDay> m_AnalyzerEntriesCGMDayList;
	private Date                             m_StartDateForTreatments;
	private Date                             m_EndDateForTreatments;
	private DateOverlap                      m_DateOverlap;

	private static String[] m_ColNames = {
			"Start Date",
			"End Date",
			"Overlap with Treatments",
			"# Days",
			"# Hypos",
			"# Hypers",
			"# In Range",
			"Avg / Day",
	};
	private static int[] m_ColWidths = {200, 200, 450, 100, 100, 100, 100, 100, };

	private static Object[][] m_Initializer = {{"","","","","","","","",}};


/*	AnalyzerEntriesCGMRange(Date startDate)
	{
		m_Static_ID++;
		m_ID   = m_Static_ID;

		m_Logger.log(Level.FINE, "Just built AnalyzerEntriesCGMRange " + m_ID + " @" + 
				startDate.toString()); 

		m_StartDate              = new Date(startDate.getTime());
		m_EndDate                = new Date(0);
		m_Duration               = 0;
		m_AverageDBResultEntries = 0;

		m_AnalyzerEntriesCGMDayList = new ArrayList<AnalyzerEntriesCGMDay>();
	}
*/
	AnalyzerEntriesCGMRange(Date startDate,
			Date startDateForTreatments, Date endDateForTreatments)
	{
		m_Static_ID++;
		m_ID   = m_Static_ID;

		m_Logger.log(Level.FINE, "Just built AnalyzerEntriesCGMRange " + m_ID + " @" + 
				startDate.toString()); 

		m_StartDate              = new Date(startDate.getTime());
		m_EndDate                = new Date(0);
		m_Duration               = 0;
		m_AverageDBResultEntries = 0;
		m_StartDateForTreatments = new Date(startDateForTreatments.getTime());
		m_EndDateForTreatments   = new Date(endDateForTreatments.getTime());

		m_AnalyzerEntriesCGMDayList = new ArrayList<AnalyzerEntriesCGMDay>();
	}

	
	// DBResultEntries are assumed added in chronological order
	public void addDBResultEntry(DBResultEntry e)
	{
		Date currDate                    = new Date(0);
		AnalyzerEntriesCGMDay currCGMDay = null;
		if (m_AnalyzerEntriesCGMDayList.size() != 0)
		{
			currCGMDay = m_AnalyzerEntriesCGMDayList.get(m_AnalyzerEntriesCGMDayList.size() - 1);
			currDate   = currCGMDay.getM_Date();
		}

		if (currCGMDay != null && CommonUtils.isDateTheSame(currDate, e.getM_UTCDate()))
		{
			currCGMDay.addDBResultEntry(e);
		}
		else
		{
			currCGMDay = new AnalyzerEntriesCGMDay(e.getM_UTCDate());
			m_AnalyzerEntriesCGMDayList.add(currCGMDay);
		}

		if (e.getM_SGV() != null)
		{
			m_NumHypos   += e.getM_BG()  < 4.0 ? 1 : 0;
			m_NumHypers  += e.getM_BG() >= 14.0 ? 1 : 0;
			m_NumInRange += (e.getM_BG() >= 4.0 && e.getM_BG() < 14.0) ? 1 : 0;
		}
	}

	public void endRange(Date endDate)
	{
		m_EndDate = endDate;

		// Now need to set duration too
		long dateDiff = m_EndDate.getTime() - m_StartDate.getTime();
		m_Duration = TimeUnit.DAYS.convert(dateDiff, TimeUnit.MILLISECONDS) + 1;

		int total = 0;
		// Calculate average now
		for (AnalyzerEntriesCGMDay c : m_AnalyzerEntriesCGMDayList)
		{
			total += c.getM_DBResultEntries().size();
		}
		this.m_AverageDBResultEntries = total / m_AnalyzerEntriesCGMDayList.size();
		
		if (m_StartDate.getTime() >= m_StartDateForTreatments.getTime() && 
				m_EndDate.getTime() <= m_EndDateForTreatments.getTime())
		{
			m_DateOverlap = DateOverlap.InRange;
		}
		else if (m_StartDate.getTime() < m_StartDateForTreatments.getTime() && 
				m_EndDate.getTime() <= m_EndDateForTreatments.getTime())
		{
			m_DateOverlap = DateOverlap.StartsBeforeTreatments;
		}
		else if (m_StartDate.getTime() >= m_StartDateForTreatments.getTime() && 
				m_EndDate.getTime() > m_EndDateForTreatments.getTime())
		{
			m_DateOverlap = DateOverlap.EndsAfterTreatments;
		}
		else if (m_StartDate.getTime() < m_StartDateForTreatments.getTime() && 
				m_EndDate.getTime() < m_StartDateForTreatments.getTime())
		{
			m_DateOverlap = DateOverlap.StartsAndEndsBeforeTreatments;
		}
		else if (m_StartDate.getTime() > m_EndDateForTreatments.getTime() && 
				m_EndDate.getTime() > m_EndDateForTreatments.getTime())
		{
			m_DateOverlap = DateOverlap.StartsAndEndsBeforeTreatments;
		}

	}
	
	public String getM_DateOverlapStr()
	{
		String result = null;
		
		if (m_DateOverlap == DateOverlap.InRange)
		{
			result = "Within Treatment Range";
		}
		else if (m_DateOverlap == DateOverlap.StartsBeforeTreatments)
		{
			result = "Starts Before Treatment Range";
		}
		else if (m_DateOverlap == DateOverlap.EndsAfterTreatments)
		{
			result = "Ends After Treatment Range";
		}
		else if (m_DateOverlap == DateOverlap.StartsAndEndsBeforeTreatments)
		{
			result = "Starts and Ends Before Treatment Range";
		}
		else if (m_DateOverlap == DateOverlap.StartsAndEndsBeforeTreatments)
		{
			result = "Starts and Ends After Treatment Range";
		}
		
		return result;
	}

	// Used to populate a model which then can appear in a grid
	public String[] toArray()
	{
		int arrSize = 8;  

		String[] res = new String[arrSize];

		m_Logger.log(Level.FINEST, toString());

		int i = 0;

		res[i++] = getM_StartDateStr();
		res[i++] = getM_EndDateStr();
		res[i++] = getM_DateOverlapStr();
		res[i++] = Long.toString(getM_Duration());
		res[i++] = Integer.toString(getM_NumHypos());
		res[i++] = Integer.toString(getM_NumHypers());
		res[i++] = Integer.toString(getM_NumInRange());
		res[i++] = Integer.toString(getM_AverageDBResultEntries());

		return res;
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

	public synchronized String getM_StartDateStr()
	{
		String result = new String("");
		final DateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
		result = dateformat.format(m_StartDate);
		return result;
	}

	public synchronized String getM_EndDateStr()
	{
		String result = new String("");
		final DateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
		result = dateformat.format(m_EndDate);
		return result;
	}
	/**
	 * @return the m_StartDate
	 */
	public synchronized Date getM_StartDate() {
		return m_StartDate;
	}

	/**
	 * @return the m_EndDate
	 */
	public synchronized Date getM_EndDate() {
		return m_EndDate;
	}

	/**
	 * @return the m_Duration
	 */
	public synchronized long getM_Duration() {
		return m_Duration;
	}

	/**
	 * @return the m_AnalyzerEntriesCGMDayList
	 */
	public synchronized ArrayList<AnalyzerEntriesCGMDay> getM_AnalyzerEntriesCGMDayList() {
		return m_AnalyzerEntriesCGMDayList;
	}

	/**
	 * @return the m_AverageDBResultEntries
	 */
	public synchronized int getM_AverageDBResultEntries() {
		return m_AverageDBResultEntries;
	}

	/**
	 * @return the m_ColNames
	 */
	public static synchronized String[] getM_ColNames() {
		return m_ColNames;
	}

	/**
	 * @return the m_ColWidths
	 */
	public static synchronized int[] getM_ColWidths() {
		return m_ColWidths;
	}

	/**
	 * @return the m_Initializer
	 */
	public static synchronized Object[][] getM_Initializer() {
		return m_Initializer;
	}

	/**
	 * @return the m_NumHypos
	 */
	public synchronized int getM_NumHypos() {
		return m_NumHypos;
	}

	/**
	 * @return the m_NumHypers
	 */
	public synchronized int getM_NumHypers() {
		return m_NumHypers;
	}

	/**
	 * @return the m_NumInRange
	 */
	public synchronized int getM_NumInRange() {
		return m_NumInRange;
	}


	/**
	 * @return the m_StartDateForTreatments
	 */
	public synchronized Date getM_StartDateForTreatments() {
		return m_StartDateForTreatments;
	}


	/**
	 * @return the m_EndDateForTreatments
	 */
	public synchronized Date getM_EndDateForTreatments() {
		return m_EndDateForTreatments;
	}


	/**
	 * @return the m_DateOverlap
	 */
	public synchronized DateOverlap getM_DateOverlap() {
		return m_DateOverlap;
	}

}
