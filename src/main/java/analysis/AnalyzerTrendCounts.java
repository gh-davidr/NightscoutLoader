package analysis;

import java.util.ArrayList;

import analysis.Analyzer.L2AnalyzerTrendResultEnum;

//import davidRichardson.Analyzer.L2AnalyzerTrendResultEnumWrapper;

// September 2016
// A completely different implementation :-)
// Takes an analyzer and infers counts & stats from it
// Will be useful for a comparative analysis of two analyzers

public class AnalyzerTrendCounts 
{
	class L2AnalyzerTrendResultEnumWrapper
	{
		public L2AnalyzerTrendResultEnum m_L2AnalyzerTrendResultEnum;
		
		L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum e)
		{
			m_L2AnalyzerTrendResultEnum = e;
		}
		
	}
	
	private ArrayList<L2AnalyzerTrendResultEnumWrapper> m_RiseEnumList = new ArrayList<L2AnalyzerTrendResultEnumWrapper>();
	private ArrayList<L2AnalyzerTrendResultEnumWrapper> m_FallEnumList = new ArrayList<L2AnalyzerTrendResultEnumWrapper>();
	private ArrayList<L2AnalyzerTrendResultEnumWrapper> m_FlatEnumList = new ArrayList<L2AnalyzerTrendResultEnumWrapper>();

	private int                        m_BreakfastTimeSlotRiseCnt   = 0;
	private int                        m_BreakfastTimeSlotFallCnt   = 0;
	private int                        m_BreakfastTimeSlotFlatCnt   = 0;
	private int                        m_LunchTimeSlotRiseCnt       = 0;
	private int                        m_LunchTimeSlotFallCnt       = 0;
	private int                        m_LunchTimeSlotFlatCnt       = 0;
	private int                        m_DinnerTimeSlotRiseCnt      = 0;
	private int                        m_DinnerTimeSlotFallCnt      = 0;
	private int                        m_DinnerTimeSlotFlatCnt      = 0;
	private int                        m_OvernightTimeSlotRiseCnt   = 0;
	private int                        m_OvernightTimeSlotFallCnt   = 0;
	private int                        m_OvernightTimeSlotFlatCnt   = 0;
	
	AnalyzerTrendCounts()
	{
		initialize();
	}
	
	private void initialize()
	{
		// falls of interest
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_into_range_after_meal));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_into_hypo_after_meal));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_into_hypo_after_correction));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_into_range_no_carbs));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_into_hypo_no_carbs));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_overnight_into_range_no_intervention));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_overnight_into_range_but_hypo_first));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_overnight_into_hypo_no_intervention));
		
		// rises of interest
		m_RiseEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.rise_out_of_range_after_meal));
		m_RiseEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.rise_in_to_out_of_range_after_meal));
		m_RiseEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.rise_into_range_after_correction));
		m_RiseEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.rise_out_of_range_after_correction));
		m_RiseEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.rise_in_to_out_of_range_after_correction));
		m_RiseEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.rise_out_of_range_after_presumed_carbs));
		m_RiseEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.rise_out_of_range_no_carbs));
		m_RiseEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.rise_in_to_out_of_range_no_carbs));
		m_RiseEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.rise_overnight_out_of_range_no_intervention));
		m_RiseEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.rise_overnight_out_of_range_but_hypo_first));
		m_RiseEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.rise_overnight_out_of_range_but_corrected_first));

		// flats of interest
		m_FlatEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.in_range_to_in_range));
		m_FlatEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.overnight_in_range_to_in_range_no_intervention));
		m_FlatEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.overnight_in_range_to_in_range_but_hypo_first));
		m_FlatEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.overnight_in_range_to_in_range_but_corrected_first));		

	}

	/**
	 * @return the m_BreakfastTimeSlotRiseCnt
	 */
	public synchronized int getM_BreakfastTimeSlotRiseCnt() {
		return m_BreakfastTimeSlotRiseCnt;
	}

	/**
	 * @param m_BreakfastTimeSlotRiseCnt the m_BreakfastTimeSlotRiseCnt to set
	 */
	public synchronized void incrM_BreakfastTimeSlotRiseCnt() {
		this.m_BreakfastTimeSlotRiseCnt++;
	}
	
	/**
	 * @param m_BreakfastTimeSlotRiseCnt the m_BreakfastTimeSlotRiseCnt to set
	 */
	public synchronized void decrM_BreakfastTimeSlotRiseCnt() {
		this.m_BreakfastTimeSlotRiseCnt--;
	}
	

	/**
	 * @return the m_BreakfastTimeSlotFallCnt
	 */
	public synchronized int getM_BreakfastTimeSlotFallCnt() {
		return m_BreakfastTimeSlotFallCnt;
	}

	/**
	 * @param m_BreakfastTimeSlotFallCnt the m_BreakfastTimeSlotFallCnt to set
	 */
	public synchronized void incrM_BreakfastTimeSlotFallCnt() {
		this.m_BreakfastTimeSlotFallCnt++;
	}

	/**
	 * @param m_BreakfastTimeSlotFallCnt the m_BreakfastTimeSlotFallCnt to set
	 */
	public synchronized void decrM_BreakfastTimeSlotFallCnt() {
		this.m_BreakfastTimeSlotFallCnt--;
	}

	
	/**
	 * @return the m_BreakfastTimeSlotFlatCnt
	 */
	public synchronized int getM_BreakfastTimeSlotFlatCnt() {
		return m_BreakfastTimeSlotFlatCnt;
	}

	/**
	 * @param m_BreakfastTimeSlotFlatCnt the m_BreakfastTimeSlotFlatCnt to set
	 */
	public synchronized void incrM_BreakfastTimeSlotFlatCnt() {
		this.m_BreakfastTimeSlotFlatCnt++;
	}

	/**
	 * @param m_BreakfastTimeSlotFlatCnt the m_BreakfastTimeSlotFlatCnt to set
	 */
	public synchronized void decrM_BreakfastTimeSlotFlatCnt() {
		this.m_BreakfastTimeSlotFlatCnt--;
	}

	/**
	 * @return the m_LunchTimeSlotRiseCnt
	 */
	public synchronized int getM_LunchTimeSlotRiseCnt() {
		return m_LunchTimeSlotRiseCnt;
	}

	/**
	 * @param m_LunchTimeSlotRiseCnt the m_LunchTimeSlotRiseCnt to set
	 */
	public synchronized void incrM_LunchTimeSlotRiseCnt() {
		this.m_LunchTimeSlotRiseCnt++;
	}

	/**
	 * @param m_LunchTimeSlotRiseCnt the m_LunchTimeSlotRiseCnt to set
	 */
	public synchronized void decrM_LunchTimeSlotRiseCnt() {
		this.m_LunchTimeSlotRiseCnt--;
	}

	
	/**
	 * @return the m_LunchTimeSlotFallCnt
	 */
	public synchronized int getM_LunchTimeSlotFallCnt() {
		return m_LunchTimeSlotFallCnt;
	}

	/**
	 * @param m_LunchTimeSlotFallCnt the m_LunchTimeSlotFallCnt to set
	 */
	public synchronized void incrM_LunchTimeSlotFallCnt() {
		this.m_LunchTimeSlotFallCnt++;
	}

	/**
	 * @param m_LunchTimeSlotFallCnt the m_LunchTimeSlotFallCnt to set
	 */
	public synchronized void decrM_LunchTimeSlotFallCnt() {
		this.m_LunchTimeSlotFallCnt--;
	}

	
	/**
	 * @return the m_LunchTimeSlotFlatCnt
	 */
	public synchronized int getM_LunchTimeSlotFlatCnt() {
		return m_LunchTimeSlotFlatCnt;
	}

	/**
	 * @param m_LunchTimeSlotFlatCnt the m_LunchTimeSlotFlatCnt to set
	 */
	public synchronized void incrM_LunchTimeSlotFlatCnt() {
		this.m_LunchTimeSlotFlatCnt++;
	}

	/**
	 * @param m_LunchTimeSlotFlatCnt the m_LunchTimeSlotFlatCnt to set
	 */
	public synchronized void decrM_LunchTimeSlotFlatCnt() {
		this.m_LunchTimeSlotFlatCnt--;
	}

	/**
	 * @return the m_DinnerTimeSlotRiseCnt
	 */
	public synchronized int getM_DinnerTimeSlotRiseCnt() {
		return m_DinnerTimeSlotRiseCnt;
	}

	/**
	 * @param m_DinnerTimeSlotRiseCnt the m_DinnerTimeSlotRiseCnt to set
	 */
	public synchronized void incrM_DinnerTimeSlotRiseCnt() {
		this.m_DinnerTimeSlotRiseCnt++;
	}

	/**
	 * @param m_DinnerTimeSlotRiseCnt the m_DinnerTimeSlotRiseCnt to set
	 */
	public synchronized void decrM_DinnerTimeSlotRiseCnt() {
		this.m_DinnerTimeSlotRiseCnt--;
	}

	/**
	 * @return the m_DinnerTimeSlotFallCnt
	 */
	public synchronized int getM_DinnerTimeSlotFallCnt() {
		return m_DinnerTimeSlotFallCnt;
	}

	/**
	 * @param m_DinnerTimeSlotFallCnt the m_DinnerTimeSlotFallCnt to set
	 */
	public synchronized void incrM_DinnerTimeSlotFallCnt() {
		this.m_DinnerTimeSlotFallCnt++;
	}
	
	/**
	 * @param m_DinnerTimeSlotFallCnt the m_DinnerTimeSlotFallCnt to set
	 */
	public synchronized void decrM_DinnerTimeSlotFallCnt() {
		this.m_DinnerTimeSlotFallCnt++;
	}

	/**
	 * @return the m_DinnerTimeSlotFlatCnt
	 */
	public synchronized int getM_DinnerTimeSlotFlatCnt() {
		return m_DinnerTimeSlotFlatCnt;
	}

	/**
	 * @param m_DinnerTimeSlotFlatCnt the m_DinnerTimeSlotFlatCnt to set
	 */
	public synchronized void incrM_DinnerTimeSlotFlatCnt() {
		this.m_DinnerTimeSlotFlatCnt++;
	}

	/**
	 * @param m_DinnerTimeSlotFlatCnt the m_DinnerTimeSlotFlatCnt to set
	 */
	public synchronized void decrM_DinnerTimeSlotFlatCnt() {
		this.m_DinnerTimeSlotFlatCnt--;
	}

	
	/**
	 * @return the m_OvernightTimeSlotRiseCnt
	 */
	public synchronized int getM_OvernightTimeSlotRiseCnt() {
		return m_OvernightTimeSlotRiseCnt;
	}

	/**
	 * @param m_OvernightTimeSlotRiseCnt the m_OvernightTimeSlotRiseCnt to set
	 */
	public synchronized void incrM_OvernightTimeSlotRiseCnt() {
		this.m_OvernightTimeSlotRiseCnt++;
	}

	/**
	 * @param m_OvernightTimeSlotRiseCnt the m_OvernightTimeSlotRiseCnt to set
	 */
	public synchronized void decrM_OvernightTimeSlotRiseCnt() {
		this.m_OvernightTimeSlotRiseCnt--;
	}

	/**
	 * @return the m_OvernightTimeSlotFallCnt
	 */
	public synchronized int getM_OvernightTimeSlotFallCnt() {
		return m_OvernightTimeSlotFallCnt;
	}

	/**
	 * @param m_OvernightTimeSlotFallCnt the m_OvernightTimeSlotFallCnt to set
	 */
	public synchronized void incrM_OvernightTimeSlotFallCnt() {
		this.m_OvernightTimeSlotFallCnt++;
	}

	/**
	 * @param m_OvernightTimeSlotFallCnt the m_OvernightTimeSlotFallCnt to set
	 */
	public synchronized void decrM_OvernightTimeSlotFallCnt() {
		this.m_OvernightTimeSlotFallCnt--;
	}

	/**
	 * @return the m_OvernightTimeSlotFlatCnt
	 */
	public synchronized int getM_OvernightTimeSlotFlatCnt() {
		return m_OvernightTimeSlotFlatCnt;
	}

	/**
	 * @param m_OvernightTimeSlotFlatCnt the m_OvernightTimeSlotFlatCnt to set
	 */
	public synchronized void incrM_OvernightTimeSlotFlatCnt() {
		this.m_OvernightTimeSlotFlatCnt++;
	}

	/**
	 * @param m_OvernightTimeSlotFlatCnt the m_OvernightTimeSlotFlatCnt to set
	 */
	public synchronized void decrM_OvernightTimeSlotFlatCnt() {
		this.m_OvernightTimeSlotFlatCnt--;
	}

}


// Implements the singleton interface
/*public class AnalyzerTrendCounts 
{
	private static AnalyzerTrendCounts m_AnalyzerTrendCounts = null;
	
	static AnalyzerTrendCounts getInstance()
	{
		if (m_AnalyzerTrendCounts == null)
		{
			m_AnalyzerTrendCounts = new AnalyzerTrendCounts();
		}
		return m_AnalyzerTrendCounts;
	}

	private enum AnalyzerTrendCountsUpdated
	{
		unknown,

		breakfastFlats,
		lunchFlats,
		dinnerFlats,
		overnightFlats,

		breakfastRises,
		lunchRises,
		dinnerRises,
		overnightRises,

		breakfastFalls,
		lunchFalls,
		dinnerFalls,
		overnightFalls,

	};


	// Some useful counters for different trend types
	private int m_BreakfastFlats = 0;
	private int m_LunchFlats = 0;
	private int m_DinnerFlats = 0;
	private int m_OvernightFlats = 0;

	private int m_BreakfastRises = 0;
	private int m_LunchRises = 0;
	private int m_DinnerRises = 0;
	private int m_OvernightRises = 0;

	private int m_BreakfastFalls = 0;
	private int m_LunchFalls = 0;
	private int m_DinnerFalls = 0;
	private int m_OvernightFalls = 0;

	private AnalyzerTrendCountsUpdated m_AnalyzerTrendCountsUpdated = AnalyzerTrendCountsUpdated.unknown; 

	private AnalyzerTrendCounts()
	{

	}

	public synchronized void reset()
	{
		m_BreakfastFlats = 0;
		m_LunchFlats = 0;
		m_DinnerFlats = 0;
		m_OvernightFlats = 0;

		m_BreakfastRises = 0;
		m_LunchRises = 0;
		m_DinnerRises = 0;
		m_OvernightRises = 0;

		m_BreakfastFalls = 0;
		m_LunchFalls = 0;
		m_DinnerFalls = 0;
		m_OvernightFalls = 0;
	}


	public synchronized void reverseOutLastUpdate()
	{
		switch (m_AnalyzerTrendCountsUpdated)
		{
		case 		breakfastFlats: m_BreakfastFlats--; break;
		case 		lunchFlats:     m_LunchFlats--; break;
		case 		dinnerFlats:    m_DinnerFlats--; break;
		case 		overnightFlats: m_OvernightFlats--; break;

		case 		breakfastRises: m_BreakfastRises--; break;
		case 		lunchRises:     m_LunchRises--; break;
		case 		dinnerRises:    m_DinnerRises--; break;
		case 		overnightRises: m_OvernightRises--; break;

		case 		breakfastFalls: m_BreakfastFalls--; break;
		case 		lunchFalls:     m_LunchFalls--; break;
		case 		dinnerFalls:    m_DinnerFalls--; break;
		case 		overnightFalls: m_OvernightFalls--; break;

		case        unknown:
		default:
		}

	}

	*//**
	 * @return the m_BreakfastFlats
	 *//*
	public synchronized int getM_BreakfastFlats() {
		return m_BreakfastFlats;
	}
	*//**
	 * @param m_BreakfastFlats the m_BreakfastFlats to set
	 *//*
	public synchronized void incrM_BreakfastFlats() {
		this.m_BreakfastFlats++;
		m_AnalyzerTrendCountsUpdated = AnalyzerTrendCountsUpdated.breakfastFlats;
	}
	*//**
	 * @return the m_LunchFlats
	 *//*
	public synchronized int getM_LunchFlats() {
		return m_LunchFlats;
	}
	*//**
	 * @param m_LunchFlats the m_LunchFlats to set
	 *//*
	public synchronized void incrM_LunchFlats() {
		this.m_LunchFlats++;
		m_AnalyzerTrendCountsUpdated = AnalyzerTrendCountsUpdated.lunchFlats;
	}
	*//**
	 * @return the m_DinnerFlats
	 *//*
	public synchronized int getM_DinnerFlats() {
		return m_DinnerFlats;
	}
	*//**
	 * @param m_DinnerFlats the m_DinnerFlats to set
	 *//*
	public synchronized void incrM_DinnerFlats() {
		this.m_DinnerFlats++;
		m_AnalyzerTrendCountsUpdated = AnalyzerTrendCountsUpdated.dinnerFlats;
	}
	*//**
	 * @return the m_OvernightFlats
	 *//*
	public synchronized int getM_OvernightFlats() {
		return m_OvernightFlats;
	}
	*//**
	 * @param m_OvernightFlats the m_OvernightFlats to set
	 *//*
	public synchronized void incrM_OvernightFlats() {
		this.m_OvernightFlats++;
		m_AnalyzerTrendCountsUpdated = AnalyzerTrendCountsUpdated.overnightFlats;			
	}



	*//**
	 * @return the m_BreakfastRises
	 *//*
	public synchronized int getM_BreakfastRises() {
		return m_BreakfastRises;
	}
	*//**
	 * @param m_BreakfastRises the m_BreakfastRises to set
	 *//*
	public synchronized void incrM_BreakfastRises() {
		this.m_BreakfastRises++;
		m_AnalyzerTrendCountsUpdated = AnalyzerTrendCountsUpdated.breakfastRises;
	}
	*//**
	 * @return the m_LunchRises
	 *//*
	public synchronized int getM_LunchRises() {
		return m_LunchRises;
	}
	*//**
	 * @param m_LunchRises the m_LunchRises to set
	 *//*
	public synchronized void incrM_LunchRises() {
		this.m_LunchRises++;
		m_AnalyzerTrendCountsUpdated = AnalyzerTrendCountsUpdated.lunchRises;
	}
	*//**
	 * @return the m_DinnerRises
	 *//*
	public synchronized int getM_DinnerRises() {
		return m_DinnerRises;
	}
	*//**
	 * @param m_DinnerRises the m_DinnerRises to set
	 *//*
	public synchronized void incrM_DinnerRises() {
		this.m_DinnerRises++;
		m_AnalyzerTrendCountsUpdated = AnalyzerTrendCountsUpdated.dinnerRises;			
	}
	*//**
	 * @return the m_OvernightRises
	 *//*
	public synchronized int getM_OvernightRises() {
		return m_OvernightRises;
	}
	*//**
	 * @param m_OvernightRises the m_OvernightRises to set
	 *//*
	public synchronized void incrM_OvernightRises() {
		this.m_OvernightRises++;
		m_AnalyzerTrendCountsUpdated = AnalyzerTrendCountsUpdated.overnightRises;			
	}

	*//**
	 * @return the m_BreakfastFalls
	 *//*
	public synchronized int getM_BreakfastFalls() {
		return m_BreakfastFalls;
	}
	*//**
	 * @param m_BreakfastFalls the m_BreakfastFalls to set
	 *//*
	public synchronized void incrM_BreakfastFalls() {
		this.m_BreakfastFalls++;
		m_AnalyzerTrendCountsUpdated = AnalyzerTrendCountsUpdated.breakfastFalls;			
	}
	*//**
	 * @return the m_LunchFalls
	 *//*
	public synchronized int getM_LunchFalls() {
		return m_LunchFalls;
	}
	*//**
	 * @param m_LunchFalls the m_LunchFalls to set
	 *//*
	public synchronized void incrM_LunchFalls() {
		this.m_LunchFalls++;
		m_AnalyzerTrendCountsUpdated = AnalyzerTrendCountsUpdated.lunchFalls;			
	}
	*//**
	 * @return the m_DinnerFalls
	 *//*
	public synchronized int getM_DinnerFalls() {
		return m_DinnerFalls;
	}
	*//**
	 * @param m_DinnerFalls the m_DinnerFalls to set
	 *//*
	public synchronized void incrM_DinnerFalls() {
		this.m_DinnerFalls++;
		m_AnalyzerTrendCountsUpdated = AnalyzerTrendCountsUpdated.dinnerFalls;			
	}
	*//**
	 * @return the m_OvernightFalls
	 *//*
	public synchronized int getM_OvernightFalls() {
		return m_OvernightFalls;
	}
	*//**
	 * @param m_OvernightFalls the m_OvernightFalls to set
	 *//*
	public synchronized void incrM_OvernightFalls() {
		this.m_OvernightFalls++;
		m_AnalyzerTrendCountsUpdated = AnalyzerTrendCountsUpdated.overnightFalls;			
	}
};
*/