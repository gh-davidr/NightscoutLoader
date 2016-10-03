package davidRichardson;

//import java.util.logging.Logger;

class AnalyzerSingleResult
{
//	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());
	private static Integer    m_Static_ID = 0;  // Assign an ID to each result created.
	private int               m_ID        = 0;  // Assign an ID to each result created.
	private int               m_TrendStartResultID = 0;
	private int               m_TrendEndResultID   = 0;

	private Analyzer.L0AnalyzerSingleResultEnum   m_AnalyzerSingleResultEnum;
	private DBResult          m_DBResult;
	private DBResult.TimeSlot m_TimeSlot;
	private String            m_DayName;  // Might be useful for some analysis
	
	private String            m_ReasonForDiscard;  // Going to summarise the single results into a tab too

	private long              m_RelevanceScore;  // Range from 0 (not relevant) to 10 (very relevant)
	
	// Some convenience flags
	private boolean           m_isMeal = false;
	private boolean           m_isCorrection = false;
	private boolean           m_isCarbCorrection = false; // Have to infer this from a low

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
	 * @return the m_TrendStartResultID
	 */
	public synchronized int getM_TrendStartResultID() {
		return m_TrendStartResultID;
	}

	/**
	 * @param m_TrendStartResultID the m_TrendStartResultID to set
	 */
	public synchronized void setM_TrendStartResultID(int m_TrendResultID) {
		this.m_TrendStartResultID = m_TrendResultID;
	}

	/**
	 * @return the m_TrendEndResultID
	 */
	public synchronized int getM_TrendEndResultID() {
		return m_TrendEndResultID;
	}

	/**
	 * @param m_TrendEndResultID the m_TrendEndResultID to set
	 */
	public synchronized void setM_TrendEndResultID(int m_TrendEndResultID) {
		this.m_TrendEndResultID = m_TrendEndResultID;
	}

	public AnalyzerSingleResult(Analyzer.L0AnalyzerSingleResultEnum singleResultEnum, DBResult dbResult, DBResult.TimeSlot timeSlot)
	{
		m_Static_ID++;
		m_ID = m_Static_ID;

		m_AnalyzerSingleResultEnum    = singleResultEnum;
		m_DBResult  = dbResult;
		m_TimeSlot  = timeSlot;
		m_DayName   = dbResult.getM_TreatmentDayName();
		
		m_ReasonForDiscard = new String();

		Double carbs   = dbResult.getM_CP_Carbs();
		Double insulin = dbResult.getM_CP_Insulin();
		Double bg      = dbResult.getM_CP_Glucose();

		double  analyzerLowRangeThreshold            = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowRangeThreshold();

		// CARBS & INSULIN ==> Meal
		if (carbs != null && insulin != null)
		{
			m_isMeal = true;
		}
		// NO CARBS & INSULIN ==> Meal
		else if (carbs == null && insulin != null)
		{
			m_isCorrection = true;
		}
		// Hypo ==> Carb Correction
		else if (carbs == null && bg < analyzerLowRangeThreshold && insulin == null)
		{
			m_isCarbCorrection = true;
		}

	}

	// override the equal method
	@Override
	public boolean equals(Object obj) 
	{ 
		if (obj == this) 
		{ 
			return true; 
		} 
		if (obj == null)
		{
			return false; 
		} 
		AnalyzerSingleResult guest = (AnalyzerSingleResult) obj;
		boolean result = m_DBResult.equals(guest.getM_DBResult());
		return result; 
	}


	public String toString()
	{
		String result = new String();

		result = "AnalyzerSingleResult: " + "ID:"+getM_ID() + getAnalyzerSingleResultEnumSring() + 
				m_DBResult.getM_TimeSlot() + " " + m_DBResult.toString();

		return result;
	}


	public String getAnalyzerSingleResultEnumSring()
	{
		String result = null;

		final String tooLow      = "Too Low";
		final String belowRange  = "Below Target Range";
		final String inRange     = "In Target Range";
		final String aboveRange  = "Above Target Range";
		final String tooHigh     = "Too High";

		switch (m_AnalyzerSingleResultEnum)
		{
		case inRange:    result = inRange;    break;
		case tooHigh:    result = tooHigh;    break;
		case tooLow:     result = tooLow;     break;
		case aboveRange: result = aboveRange; break;
		case belowRange: result = belowRange; break;
		default:
			break;
		}
		return result;
	}

	/**
	 * @return the m_AnalyzerSingleResultEnum
	 */
	public synchronized Analyzer.L0AnalyzerSingleResultEnum getM_AnalyzerSingleResultEnum() {
		return m_AnalyzerSingleResultEnum;
	}

	/**
	 * @param m_AnalyzerSingleResultEnum the m_AnalyzerSingleResultEnum to set
	 */
	public synchronized void setM_SingleResultEnum(Analyzer.L0AnalyzerSingleResultEnum m_AnalyzerSingleResultEnum) {
		this.m_AnalyzerSingleResultEnum = m_AnalyzerSingleResultEnum;
	}

	/**
	 * @return the m_DBResult
	 */
	public synchronized DBResult getM_DBResult() {
		return m_DBResult;
	}

	/**
	 * @param m_DBResult the m_DBResult to set
	 */
	public synchronized void setM_DBResult(DBResult m_DBResult) {
		this.m_DBResult = m_DBResult;
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
	 * @return the m_ReasonForDiscard
	 */
	public synchronized String getM_ReasonForDiscard() {
		return m_ReasonForDiscard;
	}

	/**
	 * @param m_ReasonForDiscard the m_ReasonForDiscard to set
	 */
	public synchronized void setM_ReasonForDiscard(String m_ReasonForDiscard) {
		this.m_ReasonForDiscard = m_ReasonForDiscard;
	}

	/**
	 * @return the m_RelevanceScore
	 */
	public synchronized long getM_RelevanceScore() {
		return m_RelevanceScore;
	}

	/**
	 * @param m_RelevanceScore the m_RelevanceScore to set
	 */
	public synchronized void setM_RelevanceScore(long m_RelevanceScore) {
		this.m_RelevanceScore = m_RelevanceScore;
	}

	/**
	 * @return the m_isMeal
	 */
	public synchronized boolean isM_isMeal() {
		return m_isMeal;
	}

	/**
	 * @param m_isMeal the m_isMeal to set
	 */
	public synchronized void setM_isMeal(boolean m_isMeal) {
		this.m_isMeal = m_isMeal;
	}

	/**
	 * @return the m_isCorrection
	 */
	public synchronized boolean isM_isCorrection() {
		return m_isCorrection;
	}

	/**
	 * @param m_isCorrection the m_isCorrection to set
	 */
	public synchronized void setM_isCorrection(boolean m_isCorrection) {
		this.m_isCorrection = m_isCorrection;
	}

	/**
	 * @return the m_isCarbCorrection
	 */
	public synchronized boolean isM_isCarbCorrection() {
		return m_isCarbCorrection;
	}

	/**
	 * @param m_isCarbCorrection the m_isCarbCorrection to set
	 */
	public synchronized void setM_isCarbCorrection(boolean m_isCarbCorrection) {
		this.m_isCarbCorrection = m_isCarbCorrection;
	}
};
