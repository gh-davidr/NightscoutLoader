package davidRichardson;

import java.util.ArrayList;
//import java.util.logging.Logger;

//import davidRichardson.Analyzer.AnalyzerTrendResult;

class AnalyzerRecurringTrendResult
{
//	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());
	private static Integer    m_Static_ID = 0;  // Assign an ID to each result created.
	private int               m_ID        = 0;  // Assign an ID to each result created.

	ArrayList<AnalyzerTrendResult>     m_AnalyzerTrendResultList;
	private DBResult.TimeSlot          m_TimeSlot;
	private Analyzer.L2AnalyzerTrendResultEnum m_L2TrendResultEnum;
	private long                       m_AverageRelevance;
		
	private int                        m_SunCnt;
	private int                        m_MonCnt;
	private int                        m_TueCnt;
	private int                        m_WedCnt;
	private int                        m_ThuCnt;
	private int                        m_FriCnt;
	private int                        m_SatCnt;
	
	private int                        m_OppositeCnt;
	private int                        m_TimeSlotRiseCnt;
	private int                        m_TimeSlotFallCnt;
	private double                     m_Percentage;  // Percentage of all trends that recur

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
	
	AnalyzerRecurringTrendResult(AnalyzerTrendResult res1, AnalyzerTrendResult res2)
	{
		m_Static_ID++;
		m_ID = m_Static_ID;
		
		m_AnalyzerTrendResultList   = new ArrayList<AnalyzerTrendResult>();
		m_TimeSlot          = res1.getM_AnalyzerSingleResult1().getM_TimeSlot();
		m_L2TrendResultEnum = res1.getM_L2TrendResultEnum();

		m_SunCnt = 0;
		m_MonCnt = 0;
		m_TueCnt = 0;
		m_WedCnt = 0;
		m_ThuCnt = 0;
		m_FriCnt = 0;
		m_SatCnt = 0;

		m_OppositeCnt     = 0;
		m_TimeSlotRiseCnt = 0;
		m_TimeSlotFallCnt = 0;
		m_Percentage = 0.0;

		addTrendResult(res1);
		addTrendResult(res2);
	}

	public void addTrendResult(AnalyzerTrendResult res)
	{
		m_AnalyzerTrendResultList.add(res);

		String dayName = res.getM_AnalyzerSingleResult1().getM_DayName();
		m_SunCnt += dayName.equals("Sunday")    ? 1 : 0;
		m_MonCnt += dayName.equals("Monday")    ? 1 : 0;
		m_TueCnt += dayName.equals("Tuesday")   ? 1 : 0;
		m_WedCnt += dayName.equals("Wednesday") ? 1 : 0;
		m_ThuCnt += dayName.equals("Thursday")  ? 1 : 0;
		m_FriCnt += dayName.equals("Friday")    ? 1 : 0;
		m_SatCnt += dayName.equals("Saturday")  ? 1 : 0;

		long totRelevance = 0;

		// Recalculate relevance
		for (AnalyzerTrendResult r : m_AnalyzerTrendResultList)
		{
			totRelevance += r.getM_RelevanceScore();
		}

		m_AverageRelevance = totRelevance / m_AnalyzerTrendResultList.size();
	}
	
	public boolean isAnIssueTrend()
	{
		boolean result = true;
		
		switch (this.getM_L2TrendResultEnum())
		{
		case in_range_to_in_range:                     result = false;           break;
		case hypo_after_after_presumed_carbs:          result = true;           break;
		case fall_into_range_after_meal:               result = false;     break;	
		case fall_out_of_range_after_meal:             result = true;    break;	
		case fall_into_hypo_after_meal:                result = true;    break;
		case fall_into_range_after_correction:         result = false;  break;
		case fall_out_of_range_after_correction:       result = true; break;
		case fall_into_hypo_after_correction:          result = true; break;
		case rise_into_range_after_meal:               result = false;     break;
		case rise_out_of_range_after_meal:             result = true;    break;		
		case rise_in_to_out_of_range_after_meal:       result = true; break;
		case rise_into_range_after_correction:         result = false;     break;
		case rise_out_of_range_after_correction:       result = true; break;				
		case rise_in_to_out_of_range_after_correction: result = true; break;				
		case rise_into_range_after_presumed_carbs:     result = false;    break;		
		case rise_out_of_range_after_presumed_carbs:   result = true;   break;	
		
		
		
		// Special ones for overnight
		case rise_overnight_out_of_range_no_intervention:        result = true; break;
		case rise_overnight_out_of_range_but_hypo_first:         result = true; break;
		case rise_overnight_out_of_range_but_corrected_first:    result = true; break;

		case rise_overnight_into_range_no_intervention:          result = false; break;
		case rise_overnight_into_range_but_hypo_first:           result = true; break;
		case rise_overnight_into_range_but_corrected_first:      result = true; break;

		case fall_overnight_out_of_range_no_intervention:        result = true; break;
		case fall_overnight_out_of_range_but_hypo_first:         result = true; break;
		case fall_overnight_out_of_range_but_corrected_first:    result = true; break;
		
		case fall_overnight_into_range_no_intervention:          result = false; break;
		case fall_overnight_into_range_but_hypo_first:           result = true; break;
		case fall_overnight_into_range_but_corrected_first:      result = true; break;
		
		case fall_overnight_into_hypo_no_intervention:           result = true; break;
		case fall_overnight_into_hypo_but_hypo_first:            result = true; break;
		case fall_overnight_into_hypo_but_corrected_first:       result = true; break;

		case overnight_in_range_to_in_range_no_intervention:     result = false; break;
		case overnight_in_range_to_in_range_but_hypo_first:      result = true; break;
		case overnight_in_range_to_in_range_but_corrected_first: result = true; break;
		
		case fall_into_hypo_no_carbs: result = false;
			break;
		case fall_into_range_no_carbs: result = false;
			break;
		case fall_out_of_range_no_carbs: result = false;
			break;
		case rise_in_to_out_of_range_no_carbs: result = false;
			break;
		case rise_into_range_no_carbs: result = false;
			break;
		case rise_out_of_range_no_carbs: result = false;
			break;
		default:
			break;

		}

		return result;
		
	}


	AnalyzerRecurringTrendResult(DBResult.TimeSlot timeSlot, Analyzer.L2AnalyzerTrendResultEnum l2TrendResultEnum)
	{
		m_Static_ID++;
		m_ID = m_Static_ID;

		m_AnalyzerTrendResultList   = new ArrayList<AnalyzerTrendResult>();
		m_TimeSlot          = timeSlot;
		m_L2TrendResultEnum = l2TrendResultEnum;
	}

	public String toString()
	{
		String result = new String();

		result = "AnalyzerRecurringTrendResult: " + DBResult.getTimeSlotString(m_TimeSlot) + 
				" Trend Result " +
				Analyzer.getL2TrendResultString(m_L2TrendResultEnum) +
				" With entries: " + 
				m_AnalyzerTrendResultList.size();

		return result;
	}


	/**
	 * @return the m_AnalyzerTrendResultList
	 */
	public synchronized ArrayList<AnalyzerTrendResult> getM_TrendResultList() {
		return m_AnalyzerTrendResultList;
	}

	/**
	 * @param m_AnalyzerTrendResultList the m_AnalyzerTrendResultList to set
	 */
	public synchronized void setM_TrendResultList(ArrayList<AnalyzerTrendResult> m_AnalyzerTrendResultList) {
		this.m_AnalyzerTrendResultList = m_AnalyzerTrendResultList;
	}

	/**
	 * @return the m_TimeSlot
	 */
	public synchronized DBResult.TimeSlot getM_TimeSlot() {
		return m_TimeSlot;
	}

	/**
	 * @param m_TimeSlot the m_TimeSlot to set
	 */
	public synchronized void setM_TimeSlot(DBResult.TimeSlot m_TimeSlot) {
		this.m_TimeSlot = m_TimeSlot;
	}

	/**
	 * @return the m_L2TrendResultEnum
	 */
	public synchronized Analyzer.L2AnalyzerTrendResultEnum getM_L2TrendResultEnum() {
		return m_L2TrendResultEnum;
	}

	/**
	 * @param m_L2TrendResultEnum the m_L2TrendResultEnum to set
	 */
	public synchronized void setM_L2TrendResultEnum(Analyzer.L2AnalyzerTrendResultEnum m_L2TrendResultEnum) {
		this.m_L2TrendResultEnum = m_L2TrendResultEnum;
	}

	/**
	 * @return the m_AverageRelevance
	 */
	public synchronized long getM_AverageRelevance() {
		return m_AverageRelevance;
	}

	/**
	 * @param m_AverageRelevance the m_AverageRelevance to set
	 */
	public synchronized void setM_AverageRelevance(long m_AverageRelevance) {
		this.m_AverageRelevance = m_AverageRelevance;
	}

	/**
	 * @return the m_SunCnt
	 */
	public synchronized int getM_SunCnt() {
		return m_SunCnt;
	}

	/**
	 * @param m_SunCnt the m_SunCnt to set
	 */
	public synchronized void setM_SunCnt(int m_SunCnt) {
		this.m_SunCnt = m_SunCnt;
	}

	/**
	 * @return the m_MonCnt
	 */
	public synchronized int getM_MonCnt() {
		return m_MonCnt;
	}

	/**
	 * @param m_MonCnt the m_MonCnt to set
	 */
	public synchronized void setM_MonCnt(int m_MonCnt) {
		this.m_MonCnt = m_MonCnt;
	}

	/**
	 * @return the m_TueCnt
	 */
	public synchronized int getM_TueCnt() {
		return m_TueCnt;
	}

	/**
	 * @param m_TueCnt the m_TueCnt to set
	 */
	public synchronized void setM_TueCnt(int m_TueCnt) {
		this.m_TueCnt = m_TueCnt;
	}

	/**
	 * @return the m_WedCnt
	 */
	public synchronized int getM_WedCnt() {
		return m_WedCnt;
	}

	/**
	 * @param m_WedCnt the m_WedCnt to set
	 */
	public synchronized void setM_WedCnt(int m_WedCnt) {
		this.m_WedCnt = m_WedCnt;
	}

	/**
	 * @return the m_ThuCnt
	 */
	public synchronized int getM_ThuCnt() {
		return m_ThuCnt;
	}

	/**
	 * @param m_ThuCnt the m_ThuCnt to set
	 */
	public synchronized void setM_ThuCnt(int m_ThuCnt) {
		this.m_ThuCnt = m_ThuCnt;
	}

	/**
	 * @return the m_FriCnt
	 */
	public synchronized int getM_FriCnt() {
		return m_FriCnt;
	}

	/**
	 * @param m_FriCnt the m_FriCnt to set
	 */
	public synchronized void setM_FriCnt(int m_FriCnt) {
		this.m_FriCnt = m_FriCnt;
	}

	/**
	 * @return the m_SatCnt
	 */
	public synchronized int getM_SatCnt() {
		return m_SatCnt;
	}

	/**
	 * @param m_SatCnt the m_SatCnt to set
	 */
	public synchronized void setM_SatCnt(int m_SatCnt) {
		this.m_SatCnt = m_SatCnt;
	}

	/**
	 * @return the m_OppositeCnt
	 */
	public synchronized int getM_OppositeCnt() {
		return m_OppositeCnt;
	}

	/**
	 * @param m_OppositeCnt the m_OppositeCnt to set
	 */
	public synchronized void setM_OppositeCnt(int m_OppositeCnt) {
		this.m_OppositeCnt = m_OppositeCnt;
	}

	/**
	 * @return the m_TimeSlotRiseCnt
	 */
	public synchronized int getM_TimeSlotRiseCnt() {
		return m_TimeSlotRiseCnt;
	}

	/**
	 * @param m_TimeSlotRiseCnt the m_TimeSlotRiseCnt to set
	 */
	public synchronized void setM_TimeSlotRiseCnt(int M_TimeSlotRiseCnt) {
		this.m_TimeSlotRiseCnt = M_TimeSlotRiseCnt;
	}

	/**
	 * @return the m_TimeSlotFallCnt
	 */
	public synchronized int getM_TimeSlotFallCnt() {
		return m_TimeSlotFallCnt;
	}

	/**
	 * @param m_TimeSlotFallCnt the m_TimeSlotFallCnt to set
	 */
	public synchronized void setM_TimeSlotFallCnt(int M_TimeSlotFallCnt) {
		this.m_TimeSlotFallCnt = M_TimeSlotFallCnt;
	}

	/**
	 * @return the m_Percentage
	 */
	public synchronized double getM_Percentage() {
		return m_Percentage;
	}

	/**
	 * @param m_Percentage the m_Percentage to set
	 */
	public synchronized void setM_Percentage(double m_Percentage) {
		this.m_Percentage = m_Percentage;
	}

}
