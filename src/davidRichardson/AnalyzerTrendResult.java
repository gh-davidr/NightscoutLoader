package davidRichardson;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import davidRichardson.Analyzer.AnalyzerTrendResultTypeEnum;
import davidRichardson.Analyzer.L0AnalyzerSingleResultEnum;
import davidRichardson.Analyzer.L1AnalyzerTrendResultEnum;
import davidRichardson.Analyzer.L2AnalyzerTrendResultEnum;



// 27 May 
// THoughts on this:
//  Have start and end time for bedtime start
//  Have start and end time for bedtime end
//  Get trends between these
//  ALso extend trend result to include counts of hypos or tests in between.
//  Interesting stuff is hypo or hypers at night too

class AnalyzerTrendResult
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());
	private static Integer      m_Static_ID = 0;  // Assign an ID to each result created.
	private int                 m_ID        = 0;  // Assign an ID to each result created.

	private AnalyzerTrendResultTypeEnum        m_AnalyzerTrendResultTypeEnum;

	private AnalyzerSingleResult               m_AnalyzerSingleResult1;
	private AnalyzerSingleResult               m_AnalyzerSingleResult2;
	private Analyzer.L1AnalyzerTrendResultEnum m_AnalyzerTrendResultEnum;
	private Analyzer.L2AnalyzerTrendResultEnum m_L2TrendResultEnum;

	private double                             m_ChangeRatio;
	private long                               m_RelevanceScore;  // Range from 0 (not relevant) to 10 (very relevant)
	private String                             m_Commentary;
	private boolean                            m_TempBasalActive;

	ArrayList<AnalyzerSingleResult>            m_SkippedResults;
	// For overnight results, skipped results are results between start and end
	ArrayList<AnalyzerSingleResult>            m_InterveningResults;

	// More granular aspects of the trend result
	private DBResult.TimeSlot                  m_StartTimeSlot;
	private String                             m_StartDayName;  // Might be useful for some analysis
	private L0AnalyzerSingleResultEnum         m_StartSingleResultEnum;
	private L0AnalyzerSingleResultEnum         m_EndSingleResultEnum;
	private boolean                            m_StartIncludesMeal;
	private boolean                            m_StartIncludesCorrection;  // Insulin to come down
	private boolean                            m_StartIncludesCarbs;       // Carbs to go up

	// New for where m_StartTimeSlot is overnight
	// If all these are false, then means there's no intervention during
	// two overnight results
	private boolean                            m_CarbsAfterStartTime;
	private boolean                            m_CorrectionAfterStartTime;
	private boolean                            m_HigherResultAfterStartTime;
	private boolean                            m_LowerResultAfterStartTime;

	//		private Analyzer.Result   m_Result1;
	//		private Analyzer.Result   m_Result2;
	//		private Analyzer.Trend    m_AnalyzerTrend;
	//		private DBResult          m_DBResult1;
	//		private DBResult          m_DBResult2;
	//		private DBResult.TimeSlot m_TimeSlot1;
	//		private DBResult.TimeSlot m_TimeSlot2;
	//		private String            m_DayName;  // Might be useful for some analysis

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

	// Typically, we have no idea until see second result after creating the trend.
	public AnalyzerTrendResult(AnalyzerSingleResult singleResult1, AnalyzerTrendResultTypeEnum type)
	{
		m_Static_ID++;
		m_ID = m_Static_ID;

		m_Logger.log(Level.FINE, "Just built trend result " + m_ID + " @" + 
				singleResult1.toString());  // Next thing... add this logging to where we store as  recurring trend

		m_AnalyzerTrendResultTypeEnum = type;
		setM_AnalyzerSingleResult1(singleResult1);

		m_AnalyzerSingleResult2     = null;
		m_AnalyzerTrendResultEnum   = L1AnalyzerTrendResultEnum.flat;
		m_L2TrendResultEnum         = L2AnalyzerTrendResultEnum.in_range_to_in_range;

		m_ChangeRatio       = 0.0;
		m_RelevanceScore    = 0;
		m_Commentary        = new String("");
		m_TempBasalActive   = false;
		m_SkippedResults    = new ArrayList<AnalyzerSingleResult>();
		m_InterveningResults = new ArrayList<AnalyzerSingleResult>();

		m_StartTimeSlot           = getM_AnalyzerSingleResult1().getM_TimeSlot();
		m_StartDayName            = getM_AnalyzerSingleResult1().getM_DayName();
		m_StartSingleResultEnum   = getM_AnalyzerSingleResult1().getM_AnalyzerSingleResultEnum();

		m_Logger.log(Level.FINE, "<"+this.getClass().getName()+"> Just associated SR " + singleResult1.getM_ID() + " with TR " + getM_ID());
	}

	public String getTrendResultEnumSring()
	{
		String result = null;

		final String flat     = "Flat";
		final String rising   = "Rising";
		final String falling  = "Falling";

		switch (m_AnalyzerTrendResultEnum)
		{
		case flat:     result = flat; break;
		case rising:   result = rising; break;
		case falling:  result = falling;  break;
		}
		return result;

	}

	public String toString()
	{
		String result = new String();

		result = "AnalyzerTrendResult: " + "ID:"+getM_ID() + " " +getTrendResultEnumSring() + 
				" From " + 
				(m_AnalyzerSingleResult1 == null ? "Res1 is null" : m_AnalyzerSingleResult1.toString()) + " To " + 
				(m_AnalyzerSingleResult2 == null ? "Res2 is null" : m_AnalyzerSingleResult2.toString());

		return result;
	}
	
	public boolean isWanted()
	{
		boolean result = true;
		
		
		
		return result;
	}


	public boolean isRecurringTrendResult(AnalyzerTrendResult res)
	{
		boolean result = false;

		// We consider this is a recurring trend if following are true
		if (res != null && 
				m_L2TrendResultEnum == res.getM_L2TrendResultEnum() &&
				getM_AnalyzerSingleResult1().getM_TimeSlot() == res.getM_AnalyzerSingleResult1().getM_TimeSlot() &&
				getM_AnalyzerSingleResult2() != null && res.getM_AnalyzerSingleResult2() != null)
		{
			result = true;
		}

		return result;
	}

	public boolean isRecurringTrendResult(AnalyzerRecurringTrendResult res)
	{
		boolean result = false;

		// We consider this is a recurring trend if following are true
		if (res != null && res.getM_TrendResultList().size() > 0 &&
				m_L2TrendResultEnum == res.getM_TrendResultList().get(0).getM_L2TrendResultEnum() &&
				getM_AnalyzerSingleResult1().getM_TimeSlot() == res.getM_TrendResultList().get(0).getM_AnalyzerSingleResult1().getM_TimeSlot() &&
				getM_AnalyzerSingleResult2() != null)
		{
			result = true;
		}

		return result;
	}


	public String get_CategoryCode()
	{
		return get_CategoryCode(getM_L2TrendResultEnum());
	}

	public boolean isNoCarbsTrend()
	{
		boolean result = false;

		switch (m_L2TrendResultEnum)
		{
		// New - no carbs entries
		case fall_into_range_no_carbs:         result = true; break;
		case fall_out_of_range_no_carbs:       result = true; break;
		case fall_into_hypo_no_carbs:          result = true; break;

		// New - no carbs entries
		case rise_into_range_no_carbs:         result = true; break;
		case rise_out_of_range_no_carbs:       result = true; break;	
		case rise_in_to_out_of_range_no_carbs: result = true; break;

		default: result = false; break;
		}

		return result;
	}

	public static String get_CategoryCode(Analyzer.L2AnalyzerTrendResultEnum resEnum)
	{
		String result = new String();

		switch (resEnum)
		{
		case in_range_to_in_range:                     result = "IN_RNG_2_IN_RNG";           break;
		case hypo_after_after_presumed_carbs:          result = "HYPO_AFTER_CARB";           break;
		case fall_into_range_after_meal:               result = "FALL_IN_RNG_POST_MEAL";     break;	
		case fall_out_of_range_after_meal:             result = "FALL_OUT_RNG_POST_MEAL";    break;	
		case fall_into_hypo_after_meal:                result = "FALL_IN_HYPO_POST_MEAL";    break;
		case fall_into_range_after_correction:         result = "FALL_IN_RNG_POST_CORRECT";  break;
		case fall_out_of_range_after_correction:       result = "FALL_OUT_RNG_POST_CORRECT"; break;
		case fall_into_hypo_after_correction:          result = "FALL_IN_HYPO_POST_CORRECT"; break;

		// New - no carbs entries
		case fall_into_range_no_carbs:                  result = "FALL_IN_RNG_NO_CARBS";       break;
		case fall_out_of_range_no_carbs:                result = "FALL_OUT_RNG_NO_CARBS";      break;
		case fall_into_hypo_no_carbs:                   result = "FALL_IN_HYPO_NO_CARBS";      break;



		case rise_into_range_after_meal:               result = "RISE_IN_RNG_POST_MEAL";     break;
		case rise_out_of_range_after_meal:             result = "RISE_OUT_RNG_POST_MEAL";    break;	
		case rise_in_to_out_of_range_after_meal:       result = "RISE_IN_TO_OUT_RNG_POST_MEAL"; break;
		case rise_into_range_after_correction:         result = "RISE_IN_RNG_POST_CORRECT";     break;
		case rise_out_of_range_after_correction:       result = "RISE_OUT_RNG_POST_CORRECT"; break;		
		case rise_in_to_out_of_range_after_correction: result = "RISE_IN_TO_OUT_RNG_POST_CORRECT"; break;	
		case rise_into_range_after_presumed_carbs:     result = "RISE_IN_RNG_POST_CARBS";    break;		
		case rise_out_of_range_after_presumed_carbs:   result = "RISE_OUT_RNG_POST_CARBS";   break;	

		// New - no carbs entries
		case rise_into_range_no_carbs:                  result = "RISE_IN_RNG_NO_CARBS";       break;
		case rise_out_of_range_no_carbs:                result = "RISE_OUT_RNG_NO_CARBS";      break;	
		case rise_in_to_out_of_range_no_carbs:          result = "RISE_IN_TO_OUT_RNG_NO_CARBS"; break;


		// Special ones for overnight
		case rise_overnight_out_of_range_no_intervention:        result = "OVERNIGHT_RISE_OUT_RNG"; break;
		case rise_overnight_out_of_range_but_hypo_first:         result = "OVERNIGHT_RISE_OUT_RNG_HYPO_FIRST"; break;
		case rise_overnight_out_of_range_but_corrected_first:    result = "OVERNIGHT_RISE_OUT_RNG_CORR_FIRST"; break;

		case rise_overnight_into_range_no_intervention:          result = "OVERNIGHT_RISE_IN_RNG"; break;
		case rise_overnight_into_range_but_hypo_first:           result = "OVERNIGHT_RISE_IN_RNG_HYPO_FIRST"; break;
		case rise_overnight_into_range_but_corrected_first:      result = "OVERNIGHT_RISE_IN_RNG_CORR_FIRST"; break;

		case fall_overnight_out_of_range_no_intervention:        result = "OVERNIGHT_FALL_OUT_RNG"; break;
		case fall_overnight_out_of_range_but_hypo_first:         result = "OVERNIGHT_FALL_OUT_RNG_HYPO_FIRST"; break;
		case fall_overnight_out_of_range_but_corrected_first:    result = "OVERNIGHT_FALL_OUT_RNG_CORR_FIRST"; break;

		case fall_overnight_into_range_no_intervention:          result = "OVERNIGHT_FALL_IN_RNG"; break;
		case fall_overnight_into_range_but_hypo_first:           result = "OVERNIGHT_FALL_IN_RNG_HYPO_FIRST"; break;
		case fall_overnight_into_range_but_corrected_first:      result = "OVERNIGHT_FALL_IN_RNG_CORR_FIRST"; break;

		case fall_overnight_into_hypo_no_intervention:           result = "OVERNIGHT_FALL_HYPO"; break;
		case fall_overnight_into_hypo_but_hypo_first:            result = "OVERNIGHT_FALL_HYPO_HYPO_FIRST"; break;
		case fall_overnight_into_hypo_but_corrected_first:       result = "OVERNIGHT_FALL_HYPO_CORR_FIRST"; break;


		case overnight_in_range_to_in_range_no_intervention:     result = "OVERNIGHT_IN_RNG_2_IN_RNG"; break;
		case overnight_in_range_to_in_range_but_hypo_first:      result = "OVERNIGHT_IN_RNG_2_IN_RNG_HYPO_FIRST"; break;
		case overnight_in_range_to_in_range_but_corrected_first: result = "OVERNIGHT_IN_RNG_2_IN_RNG_CORR_FIRST"; break;

		}

		return result;
	}


	public void skipResult(AnalyzerSingleResult res)
	{
		res.setM_ReasonForDiscard("Having been stored, about to be discarded in favour of a later better result");
		m_SkippedResults.add(res);
	}

	public void addInterveningResult(AnalyzerSingleResult res)
	{
		res.setM_ReasonForDiscard("Adding to intervening list");
		m_InterveningResults.add(res);
	}

	public boolean checkForAlternateEndResult(AnalyzerSingleResult analyzerSingleResult2,
			int                  minDiffMins,
			double               trendRatio)
	{
		boolean result = false; // We have not replaced result 2
//		AnalyzerSingleResult currAnalyzerSingleResult = getM_AnalyzerSingleResult2();

		// We have a trend already but the next single result might qualify for replacing the end of the existing trend
		// What reasons would we want to do this?
		//
		// Actually, I can;t think of a good reason to do it.
		// Keep the method here as a stub

		//		if (currAnalyzerSingleResult != null)
		//		{
		//			// Does this single result have carbs?
		//			Double singleResCarbs = analyzerSingleResult2.getM_DBResult().getM_CP_Carbs();
		//			Double lastResCarbs   = getM_AnalyzerSingleResult2().getM_DBResult().getM_CP_Carbs();
		//
		//			if (singleResCarbs != null && lastResCarbs == null)
		//			{
		//				addInterveningResult(currAnalyzerSingleResult);
		//				this.setM_AnalyzerSingleResult2(null);
		//				currAnalyzerSingleResult.setM_ReasonForDiscard(currAnalyzerSingleResult.getM_ReasonForDiscard() + " Found later result with Carbs");
		//
		//				result = checkForResult(analyzerSingleResult2, minDiffMins, trendRatio);
		//			}
		//		}

		return result;
	}

	public boolean checkForResult(AnalyzerSingleResult analyzerSingleResult2,
			int                  minDiffMins,
			double               trendRatio)
	{
		boolean result = false; // We have not captured result 2

		//		double  analyzerHighThreshold                = PrefsNightScoutLoader.getInstance().getM_AnalyzerHighThreshold();
		//		double  analyzerHighThresholdRelevanceFactor = PrefsNightScoutLoader.getInstance().getM_AnalyzerHighThresholdRelevanceFactor();
		//		double  analyzerLowThreshold                 = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowThreshold();
		//		double  analyzerLowThresholdRelevanceFactor  = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowThresholdRelevanceFactor();
		//		double  analyzerIndividualTrendRatio         = PrefsNightScoutLoader.getInstance().getM_AnalyzerIndividualTrendRatio();

		double  analyzerLowRangeThreshold            = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowRangeThreshold();
		double  analyzerHighRangeThreshold           = PrefsNightScoutLoader.getInstance().getM_AnalyzerHighRangeThreshold();

		String  analyzerBedTrendStartStartTime       = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendStartStartTime();
		//		String  analyzerBedTrendStartEndTime         = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendStartEndTime();
		String  analyzerBedTrendEndStartTime         = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendEndStartTime();
		String  analyzerBedTrendEndEndTime           = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendEndEndTime();

		// See whether this is a temp basal for some extra colour on analysis
		if (analyzerSingleResult2.getM_DBResult().getM_CP_BasalValue() != null)
		{
			// However, this is not accurate for skipped results though
			setM_TempBasalActive(true);
		}

		// Used in conjunction with categorizeResult2
		// Basically, delegate much of the decision making to TrendResult

		// This result completes a trend

		Date dt1 = new Date(0);
		Date dt2 = new Date(0);

		try 
		{
			dt1 = CommonUtils.convertDateString(getM_AnalyzerSingleResult1().getM_DBResult().getM_CP_EventTime(), DBResult.getCP_EventTimeFormat());
			dt2 = CommonUtils.convertDateString(analyzerSingleResult2.getM_DBResult().getM_CP_EventTime(), DBResult.getCP_EventTimeFormat());

			//m_Logger.log(Level.FINE, "<"+this.getClass().getName()+"> checkForResult " + " Comparing dt1 " + dt1 + " with dt2 " + dt2);

		} 
		catch (ParseException e) 
		{	
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+"> checkForResult " + "Exception converting Date. " + e.getMessage());
		}

		// Get difference in minutes between the 1st trend value and this
		long diffMins = CommonUtils.timeDiffInMinutes(dt2, dt1);
		Double bg1  = getM_AnalyzerSingleResult1().getM_DBResult().getM_CP_Glucose();

		// If time difference greater than minimum (& less than 2x this) 
		// OR we've got a hypo then 15mins, store this as other side of trend
		/*
		if ( (diffMins > minDiffMins && diffMins < 2 * minDiffMins) ||
				(bg1 != null && bg1 < analyzerLowRangeThreshold && diffMins >= 15 && diffMins < minDiffMins) )*/

		// We call this twice once for regular trends second time looking for hypos, so use same parameters here

		boolean endBedTime = false;
		if (getM_StartTimeSlot() == DBResult.TimeSlot.BedTime)
		{
			try {
				// Does this result land between the configured start and end times that end an overnight result?
				if (CommonUtils.isTimeBetween(analyzerBedTrendEndStartTime, 
						analyzerBedTrendEndEndTime,
						analyzerSingleResult2.getM_DBResult().getM_Time()))
				{
					endBedTime = true;
				}

				//				// In fact, if this result is actually later than the end time, just take it to close out the overnight
				//				if (CommonUtils.isTimeAfter(analyzerBedTrendEndEndTime, analyzerSingleResult2.getM_DBResult().getM_Time()))
				//				{
				//					endBedTime = true;
				//				}
			} 
			catch (ParseException e) 
			{
				m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+"> checkForResult " + "Exception checking Date ranges. " + e.getMessage());
			}
		}

		// Use enumeraor defined for meal, hypo & overnight.
		// Addd to addInterveningOvernightResult 

		if ( ((m_AnalyzerTrendResultTypeEnum != AnalyzerTrendResultTypeEnum.overnightTrendType) &&

				// David - this was fairly arbitrary and knocks out a number of single results from becoming trends
				//(diffMins > minDiffMins && diffMins < 2 * minDiffMins))
				(diffMins > minDiffMins))
				|| 
				(endBedTime) )
			//			((m_AnalyzerTrendResultTypeEnum == AnalyzerTrendResultTypeEnum.overnightTrendType) && endBedTime) )
			//				((m_AnalyzerTrendResultTypeEnum == AnalyzerTrendResultTypeEnum.overnightTrendType) || endBedTime) )
		{				
			setM_AnalyzerSingleResult2(analyzerSingleResult2);
			m_EndSingleResultEnum   = getM_AnalyzerSingleResult2().getM_AnalyzerSingleResultEnum();
			result = true;  // We have acquired second result

			Double bg2  = getM_AnalyzerSingleResult2().getM_DBResult().getM_CP_Glucose();
			Double ins1 = getM_AnalyzerSingleResult1().getM_DBResult().getM_CP_Insulin();
			double chgRatio  = 0.0;
			long relevance = 0;
			double A       = 0.0;
			//			double B       = 2.0 * trendRatio;  David changed 12 Jun 2016
			double B       = trendRatio;

			if (bg1 != bg2)
			{
				chgRatio = Math.abs((bg2 - bg1) / bg1);  // Get absolute value
				if (chgRatio > B)
				{
					relevance = 10;
				}
				else
				{
					// Normalize ratio to number between 1 & 10 
					// http://mathforum.org/library/drmath/view/60433.html
					relevance =  (long)(1 + (chgRatio - A) * (10 - 1)/(B - A));

					// Sanitize
					//   1 - if both BGs are in range then set relevance to 1
					if ( (bg1 >= analyzerLowRangeThreshold && bg1 < analyzerHighRangeThreshold) && (bg2 >=analyzerLowRangeThreshold && bg2 <= analyzerHighRangeThreshold) )
					{
						relevance = 1;
					}
					//   2 - if bg1 is high, bg2 is in range and bg1 included insulin then also set to 1
					if ( bg1 > analyzerHighRangeThreshold && (bg2 >=analyzerLowRangeThreshold && bg2 <= analyzerHighRangeThreshold) && ins1 != null )
					{
						relevance = 1;
					}
					//   3 - if bg1 is low and bg2 is in range then set to 1 
					//       (don't check for carbs since not always stored with hypos)
					if ( bg1 < analyzerLowRangeThreshold && (bg2 >=analyzerLowRangeThreshold && bg2 <= analyzerHighRangeThreshold) )
					{
						relevance = 1;
					}
				}
				setM_TrendResultEnum(bg1 > bg2 ? L1AnalyzerTrendResultEnum.falling : L1AnalyzerTrendResultEnum.rising);
			}
			setM_RelevanceScore(relevance);

			inferTrendFromResults();

		}

		// The next result is too close to the previous one.
		// We will replace it under certain circumstances.
		// We may also decide to discard it under other circumstances.
		else
		{

			// If we are in overnight and this result is also in overnight range, then skip original
			// and take this result.
			boolean alternateBedStart = false;
			if (m_AnalyzerTrendResultTypeEnum == AnalyzerTrendResultTypeEnum.overnightTrendType)
			{
				try {
					if (CommonUtils.isTimeBetween(analyzerBedTrendStartStartTime, 
							analyzerBedTrendStartStartTime,
							analyzerSingleResult2.getM_DBResult().getM_Time()))
					{
						alternateBedStart = true;
					}
				}
				catch (ParseException e) 
				{
					m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+"> checkForResult " + "Exception checking Date ranges. " + e.getMessage());
				}
				if (alternateBedStart == true)
				{
					// David 21 Jul 2016
					// Now consider close results as intervening results too
					addInterveningResult(analyzerSingleResult2);
					analyzerSingleResult2.setM_ReasonForDiscard(analyzerSingleResult2.getM_ReasonForDiscard() + " Quite close to trend start.");
					m_Logger.log(Level.FINE, "Add intervening for " + m_ID + " @" + 
							analyzerSingleResult2.toString());

					//					skipResult(getM_AnalyzerSingleResult1());
					//					analyzerSingleResult2.setM_ReasonForDiscard("Swapping previous #2 to a #1 for Bed Start");
					//					setM_AnalyzerSingleResult1(analyzerSingleResult2);					
				}

				// We are in overnight and this result is something in between.  Keep track of it
				else
				{
					addInterveningResult(analyzerSingleResult2);
					m_Logger.log(Level.FINE, "Add intervening for " + m_ID + " @" + 
							analyzerSingleResult2.toString());					
				}
			}

			// Deal with meal events
			else
			{
				// Does the first BG include Carbs & Insulin?
				Double carbs1    = getM_AnalyzerSingleResult1().getM_DBResult().getM_CP_Carbs();
				Double insulin1  = getM_AnalyzerSingleResult1().getM_DBResult().getM_CP_Insulin();
				Double bgResult1 = getM_AnalyzerSingleResult1().getM_DBResult().getM_CP_Glucose();

				Double carbs    = analyzerSingleResult2.getM_DBResult().getM_CP_Carbs();
				Double insulin  = analyzerSingleResult2.getM_DBResult().getM_CP_Insulin();
				Double bgResult = analyzerSingleResult2.getM_DBResult().getM_CP_Glucose();


				// Why would we ever swap?
				// Perhaps 1st has BG only?
				if ( (carbs != null && insulin != null && bgResult != null) &&
						(carbs1 == null || insulin1 == null || bgResult1 == null) )
				{
					skipResult(getM_AnalyzerSingleResult1());
					setM_AnalyzerSingleResult1(analyzerSingleResult2);
					m_Logger.log(Level.FINE, "Replaced Start result for " + m_ID + " @" + 
							analyzerSingleResult2.toString());
				}

				//				// So we'll replace the current result1 for one that's in range with either no carbs and no insulin
				//				// or with both carbs and insulin - that is a meal event
				//				if ( bgResult != null &&
				//					(bgResult >= analyzerLowRangeThreshold && bgResult <= analyzerHighRangeThreshold) &&
				//					((carbs == null && insulin == null) || (carbs != null && insulin != null)) )
				//				{
				//					skipResult(getM_AnalyzerSingleResult1());
				//					analyzerSingleResult2.setM_ReasonForDiscard("Swapping previous #2 to a #1");
				//					setM_AnalyzerSingleResult1(analyzerSingleResult2);
				//				}

				// If this result is a correction or out of range, then it invalidates the trend we were trying
				// to establish.  Need to discard this entry.
				else
				{
					// David 20 Jul - stop doing this and see effect
					// setM_AnalyzerSingleResult1(null);
					// David 21 Jul 2016
					// Now store in the general intervening list
					addInterveningResult(analyzerSingleResult2);
					m_Logger.log(Level.FINE, "Add intervening for " + m_ID + " @" + 
							analyzerSingleResult2.toString());

				}
			}

			//DAVID think about bedtime result here.  Do we discard in favour of a later one still in time range?
		}

		return result;
	}

	private void inferTrendFromResults()
	{
		//		double  analyzerHighThreshold                = PrefsNightScoutLoader.getInstance().getM_AnalyzerHighThreshold();
		//		double  analyzerHighThresholdRelevanceFactor = PrefsNightScoutLoader.getInstance().getM_AnalyzerHighThresholdRelevanceFactor();
		//		double  analyzerLowThreshold                 = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowThreshold();
		//		double  analyzerLowThresholdRelevanceFactor  = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowThresholdRelevanceFactor();
		//		double  analyzerIndividualTrendRatio         = PrefsNightScoutLoader.getInstance().getM_AnalyzerIndividualTrendRatio();

		double  analyzerLowRangeThreshold            = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowRangeThreshold();
		double  analyzerHighRangeThreshold           = PrefsNightScoutLoader.getInstance().getM_AnalyzerHighRangeThreshold();

		//		String  analyzerBedTrendStartStartTime       = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendStartStartTime();
		//		String  analyzerBedTrendStartEndTime         = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendStartEndTime();
		//		String  analyzerBedTrendEndStartTime         = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendEndStartTime();
		//		String  analyzerBedTrendEndEndTime           = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendEndEndTime();

		boolean analyzerCompressMealTrends           = PrefsNightScoutLoader.getInstance().isM_AnalyzerCompressMealTrends();

//		AnalyzerTrendCounts analyzerTrendCounts = AnalyzerTrendCounts.getInstance();

		Double bg1  = getM_AnalyzerSingleResult1().getM_DBResult().getM_CP_Glucose();
		Double bg2  = getM_AnalyzerSingleResult2().getM_DBResult().getM_CP_Glucose();

		// Does the first BG include Carbs & Insulin?
		Double carbs   = getM_AnalyzerSingleResult1().getM_DBResult().getM_CP_Carbs();
		Double insulin = getM_AnalyzerSingleResult1().getM_DBResult().getM_CP_Insulin();

		// Set the granular flags
		setM_StartIncludesMeal( (carbs != null && insulin != null) ? true : false );
		setM_StartIncludesCorrection( (carbs == null && insulin != null) ? true : false );
		setM_StartIncludesCarbs( (bg1 < analyzerLowRangeThreshold) ? true : false );

/*
 * 	David 9 Sep 2016
 *    Replaced trend counts with an improved method of analysing trend results once colleted
 *    
 * 		// Set the trend counts at top level based purely on trend from BG1 to BG2 and timeslot

		// Rising
		if (getM_TrendResultEnum() == L1AnalyzerTrendResultEnum.rising)
		{
			if (m_AnalyzerTrendResultTypeEnum == AnalyzerTrendResultTypeEnum.overnightTrendType)
			{
				analyzerTrendCounts.incrM_OvernightRises();
			}

			// Only increment counts for Carbs and Insulin events
			else if (carbs != null & insulin != null)
			{
				if (getM_AnalyzerSingleResult1().getM_TimeSlot() == DBResult.TimeSlot.BreakfastTime)
				{
					analyzerTrendCounts.incrM_BreakfastRises();
				}
				else if (getM_AnalyzerSingleResult1().getM_TimeSlot() == DBResult.TimeSlot.LunchTime)
				{
					analyzerTrendCounts.incrM_LunchRises();
				}
				else if (getM_AnalyzerSingleResult1().getM_TimeSlot() == DBResult.TimeSlot.DinnerTime)
				{
					analyzerTrendCounts.incrM_DinnerRises();
				}
			}
		}
		// Falling
		else if (getM_TrendResultEnum() == L1AnalyzerTrendResultEnum.falling)
		{
			if (m_AnalyzerTrendResultTypeEnum == AnalyzerTrendResultTypeEnum.overnightTrendType)
			{
				analyzerTrendCounts.incrM_OvernightFalls();
			}
			// Only increment counts for Carbs and Insulin events
			else if (carbs != null & insulin != null)
			{
				if (getM_AnalyzerSingleResult1().getM_TimeSlot() == DBResult.TimeSlot.BreakfastTime)
				{
					analyzerTrendCounts.incrM_BreakfastFalls();
				}
				else if (getM_AnalyzerSingleResult1().getM_TimeSlot() == DBResult.TimeSlot.LunchTime)
				{
					analyzerTrendCounts.incrM_LunchFalls();
				}
				else if (getM_AnalyzerSingleResult1().getM_TimeSlot() == DBResult.TimeSlot.DinnerTime)
				{
					analyzerTrendCounts.incrM_DinnerFalls();
				}
			}

		}
		// Flats
		else
		{
			if (m_AnalyzerTrendResultTypeEnum == AnalyzerTrendResultTypeEnum.overnightTrendType)
			{
				analyzerTrendCounts.incrM_OvernightFlats();
			}
			else if (getM_AnalyzerSingleResult1().getM_TimeSlot() == DBResult.TimeSlot.BreakfastTime)
			{
				analyzerTrendCounts.incrM_BreakfastFlats();
			}
			else if (getM_AnalyzerSingleResult1().getM_TimeSlot() == DBResult.TimeSlot.LunchTime)
			{
				analyzerTrendCounts.incrM_LunchFlats();
			}
			else if (getM_AnalyzerSingleResult1().getM_TimeSlot() == DBResult.TimeSlot.DinnerTime)
			{
				analyzerTrendCounts.incrM_DinnerFlats();
			}
		}*/


		// Need some options in here for overnight

		// Now set the single enum ... for now ...
		// Add note in if bg1 was hypo and bg2 is still hypo

		// First off -- is this overnight or a meal?
		if (m_AnalyzerTrendResultTypeEnum == AnalyzerTrendResultTypeEnum.overnightTrendType)
		{
			boolean hypo      = false;
			boolean corrected = false;

			for (AnalyzerSingleResult c : getM_InterveningResults())
			{
				hypo      = (hypo == true ? true : (
						(c.getM_AnalyzerSingleResultEnum() == L0AnalyzerSingleResultEnum.tooLow 
						|| c.getM_AnalyzerSingleResultEnum() == L0AnalyzerSingleResultEnum.belowRange )? true : false));
				corrected = (corrected == true ? true : (c.getM_DBResult().getM_CP_Insulin() != null ? true : false));
			}

			// I N   R A N G E   T O   I N   R A N G E
			if (this.getM_AnalyzerSingleResult1().getM_AnalyzerSingleResultEnum() == L0AnalyzerSingleResultEnum.inRange &&
					this.getM_AnalyzerSingleResult2().getM_AnalyzerSingleResultEnum() == L0AnalyzerSingleResultEnum.inRange)
			{

				setM_L2TrendResultEnum((this.getM_InterveningResults().size() == 0) ? 
						Analyzer.L2AnalyzerTrendResultEnum.overnight_in_range_to_in_range_no_intervention :
							getM_L2TrendResultEnum());							
				setM_L2TrendResultEnum((hypo == true) ? 
						Analyzer.L2AnalyzerTrendResultEnum.overnight_in_range_to_in_range_but_hypo_first :
							getM_L2TrendResultEnum());							
				setM_L2TrendResultEnum((corrected == true) ? 
						Analyzer.L2AnalyzerTrendResultEnum.overnight_in_range_to_in_range_but_corrected_first :
							getM_L2TrendResultEnum());		

				//				analyzerTrendCounts.incrM_OvernightFlats();
			}

			// R I S I N G
			else if (getM_TrendResultEnum() == L1AnalyzerTrendResultEnum.rising)
			{
				if ((this.getM_AnalyzerSingleResult2().getM_AnalyzerSingleResultEnum() == L0AnalyzerSingleResultEnum.tooHigh)
						|| (this.getM_AnalyzerSingleResult2().getM_AnalyzerSingleResultEnum() == L0AnalyzerSingleResultEnum.aboveRange))
				{
					setM_L2TrendResultEnum((this.getM_InterveningResults().size() == 0) ? 
							Analyzer.L2AnalyzerTrendResultEnum.rise_overnight_out_of_range_no_intervention :
								getM_L2TrendResultEnum());							
					setM_L2TrendResultEnum((hypo == true) ? 
							Analyzer.L2AnalyzerTrendResultEnum.rise_overnight_out_of_range_but_hypo_first :
								getM_L2TrendResultEnum());							
					setM_L2TrendResultEnum((corrected == true) ? 
							Analyzer.L2AnalyzerTrendResultEnum.rise_overnight_out_of_range_but_corrected_first :
								getM_L2TrendResultEnum());							
				}

				else if (this.getM_AnalyzerSingleResult2().getM_AnalyzerSingleResultEnum() == L0AnalyzerSingleResultEnum.inRange)
				{
					setM_L2TrendResultEnum((this.getM_InterveningResults().size() == 0) ? 
							Analyzer.L2AnalyzerTrendResultEnum.rise_overnight_into_range_no_intervention :
								getM_L2TrendResultEnum());							
					setM_L2TrendResultEnum((hypo == true) ? 
							Analyzer.L2AnalyzerTrendResultEnum.rise_overnight_into_range_but_hypo_first :
								getM_L2TrendResultEnum());							
					setM_L2TrendResultEnum((corrected == true) ? 
							Analyzer.L2AnalyzerTrendResultEnum.rise_overnight_into_range_but_corrected_first :
								getM_L2TrendResultEnum());												
				}

				//				analyzerTrendCounts.incrM_OvernightRises();
			}

			// F A L L I N G
			else if (getM_TrendResultEnum() == L1AnalyzerTrendResultEnum.falling)
			{
				if ((this.getM_AnalyzerSingleResult2().getM_AnalyzerSingleResultEnum() == L0AnalyzerSingleResultEnum.tooHigh)
						|| (this.getM_AnalyzerSingleResult2().getM_AnalyzerSingleResultEnum() == L0AnalyzerSingleResultEnum.aboveRange))
				{
					setM_L2TrendResultEnum((this.getM_InterveningResults().size() == 0) ? 
							Analyzer.L2AnalyzerTrendResultEnum.fall_overnight_out_of_range_no_intervention :
								getM_L2TrendResultEnum());							
					setM_L2TrendResultEnum((hypo == true) ? 
							Analyzer.L2AnalyzerTrendResultEnum.fall_overnight_out_of_range_but_hypo_first :
								getM_L2TrendResultEnum());							
					setM_L2TrendResultEnum((corrected == true) ? 
							Analyzer.L2AnalyzerTrendResultEnum.fall_overnight_out_of_range_but_corrected_first :
								getM_L2TrendResultEnum());							
				}

				else if (this.getM_AnalyzerSingleResult2().getM_AnalyzerSingleResultEnum() == L0AnalyzerSingleResultEnum.inRange)
				{
					setM_L2TrendResultEnum((this.getM_InterveningResults().size() == 0) ? 
							Analyzer.L2AnalyzerTrendResultEnum.fall_overnight_into_range_no_intervention :
								getM_L2TrendResultEnum());							
					setM_L2TrendResultEnum((hypo == true) ? 
							Analyzer.L2AnalyzerTrendResultEnum.fall_overnight_into_range_but_hypo_first :
								getM_L2TrendResultEnum());							
					setM_L2TrendResultEnum((corrected == true) ? 
							Analyzer.L2AnalyzerTrendResultEnum.fall_overnight_into_range_but_corrected_first :
								getM_L2TrendResultEnum());												
				}
				else if ((this.getM_AnalyzerSingleResult2().getM_AnalyzerSingleResultEnum() == L0AnalyzerSingleResultEnum.tooLow)
						|| (this.getM_AnalyzerSingleResult2().getM_AnalyzerSingleResultEnum() == L0AnalyzerSingleResultEnum.belowRange))
				{
					setM_L2TrendResultEnum((this.getM_InterveningResults().size() == 0) ? 
							Analyzer.L2AnalyzerTrendResultEnum.fall_overnight_into_hypo_no_intervention :
								getM_L2TrendResultEnum());							
					setM_L2TrendResultEnum((hypo == true) ? 
							Analyzer.L2AnalyzerTrendResultEnum.fall_overnight_into_hypo_but_hypo_first :
								getM_L2TrendResultEnum());							
					setM_L2TrendResultEnum((corrected == true) ? 
							Analyzer.L2AnalyzerTrendResultEnum.fall_overnight_into_hypo_but_corrected_first :
								getM_L2TrendResultEnum());												
				}

				//				analyzerTrendCounts.incrM_OvernightFalls();

			}			

		}

		// If starting hypo, then 3 possible outcomes
		//   1 still hypo
		//   2 rise out of range
		//   3 rise into range
		else if (m_AnalyzerTrendResultTypeEnum == AnalyzerTrendResultTypeEnum.hypoTrendType)
		{
			if (bg1 < analyzerLowRangeThreshold && bg2 < analyzerLowRangeThreshold)
			{
				setM_L2TrendResultEnum(Analyzer.L2AnalyzerTrendResultEnum.hypo_after_after_presumed_carbs);
			}
			// No CARBS & No Insulin ==> Carb Correction
			else if (bg1 < analyzerLowRangeThreshold && insulin == null)
			{
				setM_L2TrendResultEnum(bg2 > analyzerHighRangeThreshold ? 
						Analyzer.L2AnalyzerTrendResultEnum.rise_out_of_range_after_presumed_carbs : 
							Analyzer.L2AnalyzerTrendResultEnum.rise_into_range_after_presumed_carbs);
			}				
		}

		else
		{

			if (bg1 < analyzerLowRangeThreshold && bg2 < analyzerLowRangeThreshold)
			{
				setM_L2TrendResultEnum(Analyzer.L2AnalyzerTrendResultEnum.hypo_after_after_presumed_carbs);
			}

			// 	Put some extra details in based on what we have...
			else if (getM_TrendResultEnum() == L1AnalyzerTrendResultEnum.falling)
			{
				// CARBS ==> Meal
				if (carbs != null & insulin != null)
				{
					if (bg2 >= analyzerLowRangeThreshold && bg2 <= analyzerHighRangeThreshold)
					{
						setM_L2TrendResultEnum(Analyzer.L2AnalyzerTrendResultEnum.fall_into_range_after_meal);						
					}
					else if (bg2 < analyzerLowRangeThreshold)
					{
						setM_L2TrendResultEnum(Analyzer.L2AnalyzerTrendResultEnum.fall_into_hypo_after_meal);						
					}
					else
					{
						setM_L2TrendResultEnum(Analyzer.L2AnalyzerTrendResultEnum.fall_out_of_range_after_meal);
					}

					//					if (this.getM_AnalyzerSingleResult1().getM_TimeSlot() == DBResult.TimeSlot.BreakfastTime)
					//					{
					//						analyzerTrendCounts.incrM_BreakfastFalls();
					//					}
					//					else if (this.getM_AnalyzerSingleResult1().getM_TimeSlot() == DBResult.TimeSlot.LunchTime)
					//					{
					//						analyzerTrendCounts.incrM_LunchFalls();
					//					}
					//					else if (this.getM_AnalyzerSingleResult1().getM_TimeSlot() == DBResult.TimeSlot.DinnerTime)
					//					{
					//						analyzerTrendCounts.incrM_DinnerFalls();
					//					}

				}

				// No CARBS ==> Correction
				else if (carbs == null && insulin != null)
				{
					if (bg2 >= analyzerLowRangeThreshold && bg2 <= analyzerHighRangeThreshold)
					{
						setM_L2TrendResultEnum(Analyzer.L2AnalyzerTrendResultEnum.fall_into_range_after_correction);						
					}
					else if (bg2 < analyzerLowRangeThreshold)
					{
						setM_L2TrendResultEnum(Analyzer.L2AnalyzerTrendResultEnum.fall_into_hypo_after_correction);						
					}
					else
					{
						setM_L2TrendResultEnum(Analyzer.L2AnalyzerTrendResultEnum.fall_out_of_range_after_correction);
					}
				}

				// No Carbs and No Insulin
				else if (carbs == null && insulin == null)
				{
					if (bg2 >= analyzerLowRangeThreshold && bg2 <= analyzerHighRangeThreshold)
					{
						setM_L2TrendResultEnum(Analyzer.L2AnalyzerTrendResultEnum.fall_into_range_no_carbs);						
					}
					else if (bg2 < analyzerLowRangeThreshold)
					{
						setM_L2TrendResultEnum(Analyzer.L2AnalyzerTrendResultEnum.fall_into_hypo_no_carbs);						
					}
					else
					{
						setM_L2TrendResultEnum(Analyzer.L2AnalyzerTrendResultEnum.fall_out_of_range_no_carbs);
					}

				}
			}

			// R I S I N G
			else
			{
				// CARBS ==> Meal
				if (carbs != null & insulin != null)
				{
					// Did we start off in range?
					if (bg1 > analyzerLowRangeThreshold && bg1 < analyzerHighRangeThreshold)
					{
						setM_L2TrendResultEnum(bg2 > analyzerHighRangeThreshold ? 
								(analyzerCompressMealTrends ? 
										Analyzer.L2AnalyzerTrendResultEnum.rise_out_of_range_after_meal : 
											Analyzer.L2AnalyzerTrendResultEnum.rise_in_to_out_of_range_after_meal) :
												//								Analyzer.L2AnalyzerTrendResultEnum.rise_in_to_out_of_range_after_meal : 
												Analyzer.L2AnalyzerTrendResultEnum.rise_into_range_after_meal);
					}
					else
					{
						setM_L2TrendResultEnum(bg2 > analyzerHighRangeThreshold ? 
								Analyzer.L2AnalyzerTrendResultEnum.rise_out_of_range_after_meal : 
									Analyzer.L2AnalyzerTrendResultEnum.rise_into_range_after_meal);
					}

					//					if (this.getM_AnalyzerSingleResult1().getM_TimeSlot() == DBResult.TimeSlot.BreakfastTime)
					//					{
					//						analyzerTrendCounts.incrM_BreakfastRises();
					//					}
					//					else if (this.getM_AnalyzerSingleResult1().getM_TimeSlot() == DBResult.TimeSlot.LunchTime)
					//					{
					//						analyzerTrendCounts.incrM_LunchRises();
					//					}
					//					else if (this.getM_AnalyzerSingleResult1().getM_TimeSlot() == DBResult.TimeSlot.DinnerTime)
					//					{
					//						analyzerTrendCounts.incrM_DinnerRises();
					//					}

				}


				// No CARBS ==> Correction
				else if (carbs == null && insulin != null)
				{
					// Did we start off in range?
					if (bg1 > analyzerLowRangeThreshold && bg1 < analyzerHighRangeThreshold)
					{
						setM_L2TrendResultEnum(bg2 > analyzerHighRangeThreshold ? 
								(analyzerCompressMealTrends ? 
										Analyzer.L2AnalyzerTrendResultEnum.rise_out_of_range_after_correction : 
											Analyzer.L2AnalyzerTrendResultEnum.rise_in_to_out_of_range_after_correction) :
												///								Analyzer.L2AnalyzerTrendResultEnum.rise_in_to_out_of_range_after_correction : 
												Analyzer.L2AnalyzerTrendResultEnum.rise_into_range_after_correction);
					}
					else
					{
						setM_L2TrendResultEnum(bg2 > analyzerHighRangeThreshold ? 
								Analyzer.L2AnalyzerTrendResultEnum.rise_out_of_range_after_correction : 
									Analyzer.L2AnalyzerTrendResultEnum.rise_into_range_after_correction);
					}							
				}

				// No CARBS & No Insulin ==> Carb Correction
				else if (bg1 < analyzerLowRangeThreshold && insulin == null)
				{
					setM_L2TrendResultEnum(bg2 > analyzerHighRangeThreshold ? 
							Analyzer.L2AnalyzerTrendResultEnum.rise_out_of_range_after_presumed_carbs : 
								Analyzer.L2AnalyzerTrendResultEnum.rise_into_range_after_presumed_carbs);
				}	

				// No CARBS & No Insulin ==> No Meal
				if (carbs == null & insulin == null)
				{
					// Did we start off in range?
					if (bg1 > analyzerLowRangeThreshold && bg1 < analyzerHighRangeThreshold)
					{
						setM_L2TrendResultEnum(bg2 > analyzerHighRangeThreshold ? 
								(analyzerCompressMealTrends ? 
										Analyzer.L2AnalyzerTrendResultEnum.rise_out_of_range_no_carbs : 
											Analyzer.L2AnalyzerTrendResultEnum.rise_in_to_out_of_range_no_carbs) :
												//								Analyzer.L2AnalyzerTrendResultEnum.rise_in_to_out_of_range_after_meal : 
												Analyzer.L2AnalyzerTrendResultEnum.rise_into_range_no_carbs);
					}
					else
					{
						setM_L2TrendResultEnum(bg2 > analyzerHighRangeThreshold ? 
								Analyzer.L2AnalyzerTrendResultEnum.rise_out_of_range_no_carbs : 
									Analyzer.L2AnalyzerTrendResultEnum.rise_into_range_no_carbs);
					}
				}

			}
		}

		// Now add commentary
		addCommentaryToTrendResult(this);

	}

	public static String generateCommentary(Analyzer.L2AnalyzerTrendResultEnum resEnum)
	{
		String commentary = new String();

		switch (resEnum)
		{
		case in_range_to_in_range: 
			commentary = "BG held nicely in target range"; break;

		case hypo_after_after_presumed_carbs:
			commentary = "Presumed Carb correction and following result still hypo.  Should repeat & give 15g carbs."; break;

		case fall_into_range_after_meal:
			commentary = "fall after meal bolus into ideal target range." + " Carb Ratio and Sensitivity look about right."; break;
		case fall_out_of_range_after_meal:
			commentary = "fall after meal bolus." + " Outside target range still, so was correction sufficient?"; break;
		case fall_into_hypo_after_meal:
			commentary = "fall after meal bolus into hypo." + " Was insulin correction too great?  Is Carb/Insulin Ratio too low?"; break;

		case fall_into_range_after_correction:
			commentary = "fall after correction bolus into ideal target range." + " Sensitivity look about right."; break;
		case fall_out_of_range_after_correction:
			commentary = "fall after correction bolus." + " Outside target range still, so was correction sufficient?"; break;
		case fall_into_hypo_after_correction:
			commentary = "fall after correction bolus into hypo." + " Was correction too much?"; break;

		case fall_into_range_no_carbs:
			commentary = "fall after no carbs into ideal target range." + " Was there an earlier correction?  Is Basal rate too high?"; break;

		case fall_out_of_range_no_carbs:
			commentary = "fall after no carbs but still out of range." + " Was there an earlier insufficient correction?  Is Basal rate too high?"; break;

		case fall_into_hypo_no_carbs:
			commentary = "fall after no carbs into hypo." + " Is Basal rate too high?"; break;


		case rise_into_range_after_meal:
			commentary = "rise after meal bolus into ideal target range." + " Carb Ratio looks about right."; break;
		case rise_out_of_range_after_meal:
			commentary = "rise after meal bolus." + " Outside target range, so was too little insulin given for carbs?  Is Carb/Insulin Ratio too high?"; break;
		case rise_in_to_out_of_range_after_meal:
			commentary = "rise from in range to out of range after meal bolus." + " Outside target range, so was too little insulin given for carbs?  Is Carb/Insulin Ratio too high?"; break;


		case rise_into_range_after_correction:
			commentary = "rise after correction bolus into ideal target range."; break;
		case rise_out_of_range_after_correction:
			commentary = "rise after correction bolus.  Possible Canula failure/absorption issue."; break;
		case rise_in_to_out_of_range_after_correction:
			commentary = "rise from in range to out of range after correction bolus.  Unusual to bolus with no carbs like this.  Possible Canula failure/absorption issue."; break;

		case rise_into_range_after_presumed_carbs:
			commentary = "rise after possible Carb correction into ideal target range." + " Carb treatment was just about right."; break;
		case rise_out_of_range_after_presumed_carbs:
			commentary = "rise after possible Carb correction." + " Outside target range, so were too many carbs given to treat hypo?"; break;

		case rise_into_range_no_carbs:
			commentary = "rise from hypo to in range after no carbs.  Is Basal rate too low?"; break;

		case rise_out_of_range_no_carbs:
			commentary = "rise from out of range to higher value after no carbs.  Is Basal rate too low?"; break;

		case rise_in_to_out_of_range_no_carbs:
			commentary = "rise from in range to out of range after no carbs.  Is Basal rate too low?"; break;

		case rise_overnight_out_of_range_no_intervention:
			commentary = "rise overnight with no intervention."  + " Check Basal rate for overnight."; break;

		case rise_overnight_out_of_range_but_hypo_first:       // Means Carbs were given overnight
			commentary = "rise overnight but hypo first." + " Was too much correction given?  Is overnight basal right?"; break;

		case rise_overnight_out_of_range_but_corrected_first:   // Means Insulin was given overnight
			commentary = "rise overnight despite insulin given." + " Check Basal overnight but also possible Canula failure/absorption issue?"; break;


		case rise_overnight_into_range_no_intervention:
			commentary = "rise overnight into ideal target range." + " Basal rate may be just about right."; break;

		case rise_overnight_into_range_but_hypo_first:       // Means Carbs were given overnight
			commentary = "rise overnight into ideal target range but hypo first.";

		case rise_overnight_into_range_but_corrected_first:  // Means Insulin was given overnight
			commentary = "rise overnight into ideal target range but higher & correction needed first." + " Check Basal overnight.";

		case fall_overnight_out_of_range_no_intervention:
			commentary = "fall overnight into hypo no intervention." + " Check Basal rate"; break;

		case fall_overnight_out_of_range_but_hypo_first:
			commentary = "fall overnight into hypo and other hypos too." + " Definitely check Basal rate as insulin may be too high."; break;


		case fall_overnight_out_of_range_but_corrected_first:
			commentary = "fall overnight into hypo but higher before and correction first." + " Was correction too much?"; break;


		case fall_overnight_into_range_no_intervention:
			commentary = "fall overnight into ideal target range no intervention." + " Basal rate may be just about right."; break;
		case fall_overnight_into_range_but_hypo_first:
			commentary = "fall overnight into ideal target range but hypo first." + " Check Basal rate to avoid needing carb correction."; break;
		case fall_overnight_into_range_but_corrected_first:
			commentary = "fall overnight into ideal target range but higher before and correction first." + " Check Basal rate to avoid needing carb correction."; break;

		case fall_overnight_into_hypo_no_intervention:
			commentary = "fall overnight into hypo no intervention." + " Check Basal rate"; break;
		case fall_overnight_into_hypo_but_hypo_first:
			commentary = "fall overnight into hypo and other hypos too." + " Definitely check Basal rate as insulin may be too high."; break;
		case fall_overnight_into_hypo_but_corrected_first:
			commentary = "fall overnight into hypo but higher before and correction first." + " Was correction too much?"; break;

		case overnight_in_range_to_in_range_no_intervention:
			commentary = "overnight start in range and end in range." + " Perfect overnight result."; break;
		case overnight_in_range_to_in_range_but_hypo_first:
			commentary = "overnight start in range and end in range, but hypo first." + " Check basal rates for early night or Dinner ratio."; break;
		case overnight_in_range_to_in_range_but_corrected_first:
			commentary = "overnight start in range and end in range, but higher before and correction first." + " Check basal rates for early night or Dinner ratio."; break;

		default:
			break;
		}

		return commentary;
	}

	private void addCommentaryToTrendResult(AnalyzerTrendResult trendResult)
	{
		if (trendResult != null)
		{
			long relevance = trendResult.getM_RelevanceScore();

			//			Double  lowThreshold  = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowRangeThreshold();
			Double  highThreshold = PrefsNightScoutLoader.getInstance().getM_AnalyzerHighRangeThreshold();

			// Does the first BG include Carbs & Insulin?
			//			Double carbs   = trendResult.getM_AnalyzerSingleResult1().getM_DBResult().getM_CP_Carbs();
			//			Double insulin = trendResult.getM_AnalyzerSingleResult1().getM_DBResult().getM_CP_Insulin();
			String adjective = new String("");
			String tempBasal = new String("");

			String commentary = new String("");

			if (relevance > highThreshold)
			{
				adjective = "Very significant ";
			}
			else if (relevance > 5)
			{
				adjective = "Significant ";
			}
			else if (relevance > 3)
			{
				adjective = "Moderate ";
			}
			else
			{
				adjective = "See ";
			}

			if (trendResult.isM_TempBasalActive())
			{
				tempBasal = "\nReview effect of Temp Basal too.";
			}

			commentary = generateCommentary(trendResult.getM_L2TrendResultEnum());

			trendResult.setM_Commentary(trendResult.getM_Commentary() + "\n" + 
					adjective + commentary + tempBasal);
		}
	}

	/**
	 * @return the m_AnalyzerSingleResult1
	 */
	public synchronized AnalyzerSingleResult getM_AnalyzerSingleResult1() {
		return m_AnalyzerSingleResult1;
	}

	/**
	 * @param m_AnalyzerSingleResult1 the m_AnalyzerSingleResult1 to set
	 */
	private synchronized void setM_AnalyzerSingleResult1(AnalyzerSingleResult m_AnalyzerSingleResult1) 
	{
		if (this.m_AnalyzerSingleResult1 != null)
		{
			this.m_AnalyzerSingleResult1.setM_TrendStartResultID(0);
		}
		if (m_AnalyzerSingleResult1 != null)
		{
			// Set the reference to this object
			m_AnalyzerSingleResult1.setM_TrendStartResultID(getM_ID());
		}
		this.m_AnalyzerSingleResult1 = m_AnalyzerSingleResult1;
	}

	/**
	 * @return the m_AnalyzerSingleResult2
	 */
	public synchronized AnalyzerSingleResult getM_AnalyzerSingleResult2() {
		return m_AnalyzerSingleResult2;
	}

	/**
	 * @param m_AnalyzerSingleResult2 the m_AnalyzerSingleResult2 to set
	 */
	private synchronized void setM_AnalyzerSingleResult2(AnalyzerSingleResult m_AnalyzerSingleResult2) 
	{
		if (this.m_AnalyzerSingleResult2 != null)
		{
//			AnalyzerTrendCounts analyzerTrendCounts = AnalyzerTrendCounts.getInstance();

			this.m_AnalyzerSingleResult2.setM_TrendEndResultID(0);
//			analyzerTrendCounts.reverseOutLastUpdate();
			m_Logger.log(Level.FINE, "Reversed out last end result " + m_ID);
		}
		if (m_AnalyzerSingleResult2 != null)
		{
			// Set the reference to this object
			m_AnalyzerSingleResult2.setM_TrendEndResultID(getM_ID());
			m_Logger.log(Level.FINE, "Complete now for " + m_ID + " with:" + 
					m_AnalyzerSingleResult2.toString());
		}
		this.m_AnalyzerSingleResult2 = m_AnalyzerSingleResult2;
	}

	/**
	 * @return the m_AnalyzerTrendResultEnum
	 */
	public synchronized Analyzer.L1AnalyzerTrendResultEnum getM_TrendResultEnum() {
		return m_AnalyzerTrendResultEnum;
	}

	/**
	 * @param m_AnalyzerTrendResultEnum the m_AnalyzerTrendResultEnum to set
	 */
	private synchronized void setM_TrendResultEnum(Analyzer.L1AnalyzerTrendResultEnum m_AnalyzerTrendResultEnum) {
		this.m_AnalyzerTrendResultEnum = m_AnalyzerTrendResultEnum;
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
	private synchronized void setM_L2TrendResultEnum(Analyzer.L2AnalyzerTrendResultEnum m_L2TrendResultEnum) {
		this.m_L2TrendResultEnum = m_L2TrendResultEnum;
	}

	/**
	 * @return the m_ChangeRatio
	 */
	public synchronized double getM_ChangeRatio() {
		return m_ChangeRatio;
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
	 * @return the m_Commentary
	 */
	public synchronized String getM_Commentary() {
		return m_Commentary;
	}

	/**
	 * @param m_Commentary the m_Commentary to set
	 */
	public synchronized void setM_Commentary(String m_Commentary) {
		this.m_Commentary = m_Commentary;
	}

	/**
	 * @return the m_TempBasalActive
	 */
	public synchronized boolean isM_TempBasalActive() {
		return m_TempBasalActive;
	}

	/**
	 * @param m_TempBasalActive the m_TempBasalActive to set
	 */
	public synchronized void setM_TempBasalActive(boolean m_TempBasalActive) {
		this.m_TempBasalActive = m_TempBasalActive;
	}

	/**
	 * @return the m_SkippedResults
	 */
	public synchronized ArrayList<AnalyzerSingleResult> getM_SkippedResults() {
		return m_SkippedResults;
	}

	/**
	 * @param m_SkippedResults the m_SkippedResults to set
	 */
	public synchronized void setM_SkippedResults(ArrayList<AnalyzerSingleResult> m_SkippedResults) {
		this.m_SkippedResults = m_SkippedResults;
	}

	/**
	 * @return the m_InterveningResults
	 */
	public synchronized ArrayList<AnalyzerSingleResult> getM_InterveningResults() {
		return m_InterveningResults;
	}

	/**
	 * @param m_InterveningResults the m_InterveningResults to set
	 */
	public synchronized void setM_InterveningResults(
			ArrayList<AnalyzerSingleResult> m_InterveningOvernightResults) {
		this.m_InterveningResults = m_InterveningOvernightResults;
	}

	//	/**
	//	 * @return the m_AnalyzerSingleResult1
	//	 */
	//	public synchronized AnalyzerSingleResult getM_AnalyzerSingleResult1() {
	//		return m_AnalyzerSingleResult1;
	//	}

	//	/**
	//	 * @param m_AnalyzerSingleResult1 the m_AnalyzerSingleResult1 to set
	//	 */
	//	public synchronized void setM_AnalyzerSingleResult1(AnalyzerSingleResult m_AnalyzerSingleResult1) {
	//		this.m_AnalyzerSingleResult1 = m_AnalyzerSingleResult1;
	//	}

	//	/**
	//	 * @return the m_AnalyzerSingleResult2
	//	 */
	//	public synchronized AnalyzerSingleResult getM_AnalyzerSingleResult2() {
	//		return m_AnalyzerSingleResult2;
	//	}
	//
	//	/**
	//	 * @param m_AnalyzerSingleResult2 the m_AnalyzerSingleResult2 to set
	//	 */
	//	public synchronized void setM_AnalyzerSingleResult2(AnalyzerSingleResult m_AnalyzerSingleResult2) {
	//		this.m_AnalyzerSingleResult2 = m_AnalyzerSingleResult2;
	//	}

	/**
	 * @return the m_AnalyzerTrendResultEnum
	 */
	public synchronized Analyzer.L1AnalyzerTrendResultEnum getM_AnalyzerTrendResultEnum() {
		return m_AnalyzerTrendResultEnum;
	}

	/**
	 * @param m_AnalyzerTrendResultEnum the m_AnalyzerTrendResultEnum to set
	 */
	public synchronized void setM_AnalyzerTrendResultEnum(Analyzer.L1AnalyzerTrendResultEnum m_AnalyzerTrendResultEnum) {
		this.m_AnalyzerTrendResultEnum = m_AnalyzerTrendResultEnum;
	}

	/**
	 * @return the m_StartTimeSlot
	 */
	public synchronized DBResult.TimeSlot getM_StartTimeSlot() {
		return m_StartTimeSlot;
	}

	/**
	 * @param m_StartTimeSlot the m_StartTimeSlot to set
	 */
	public synchronized void setM_StartTimeSlot(DBResult.TimeSlot m_StartTimeSlot) {
		this.m_StartTimeSlot = m_StartTimeSlot;
	}

	/**
	 * @param m_StartDayName the m_StartDayName to set
	 */
	public synchronized void setM_StartDayName(String m_StartDayName) {
		this.m_StartDayName = m_StartDayName;
	}

	/**
	 * @return the m_StartSingleResultEnum
	 */
	public synchronized L0AnalyzerSingleResultEnum getM_StartSingleResultEnum() {
		return m_StartSingleResultEnum;
	}

	/**
	 * @param m_StartSingleResultEnum the m_StartSingleResultEnum to set
	 */
	public synchronized void setM_StartSingleResultEnum(L0AnalyzerSingleResultEnum m_StartSingleResultEnum) {
		this.m_StartSingleResultEnum = m_StartSingleResultEnum;
	}

	/**
	 * @return the m_EndSingleResultEnum
	 */
	public synchronized L0AnalyzerSingleResultEnum getM_EndSingleResultEnum() {
		return m_EndSingleResultEnum;
	}

	/**
	 * @param m_EndSingleResultEnum the m_EndSingleResultEnum to set
	 */
	public synchronized void setM_EndSingleResultEnum(L0AnalyzerSingleResultEnum m_EndSingleResultEnum) {
		this.m_EndSingleResultEnum = m_EndSingleResultEnum;
	}

	/**
	 * @return the m_StartIncludesMeal
	 */
	public synchronized boolean isM_StartIncludesMeal() {
		return m_StartIncludesMeal;
	}

	/**
	 * @param m_StartIncludesMeal the m_StartIncludesMeal to set
	 */
	public synchronized void setM_StartIncludesMeal(boolean m_StartIncludesMeal) {
		this.m_StartIncludesMeal = m_StartIncludesMeal;
	}

	/**
	 * @return the m_StartIncludesCorrection
	 */
	public synchronized boolean isM_StartIncludesCorrection() {
		return m_StartIncludesCorrection;
	}

	/**
	 * @param m_StartIncludesCorrection the m_StartIncludesCorrection to set
	 */
	public synchronized void setM_StartIncludesCorrection(boolean m_StartIncludesCorrection) {
		this.m_StartIncludesCorrection = m_StartIncludesCorrection;
	}

	/**
	 * @return the m_StartIncludesCarbs
	 */
	public synchronized boolean isM_StartIncludesCarbs() {
		return m_StartIncludesCarbs;
	}

	/**
	 * @param m_StartIncludesCarbs the m_StartIncludesCarbs to set
	 */
	public synchronized void setM_StartIncludesCarbs(boolean m_StartIncludesCarbs) {
		this.m_StartIncludesCarbs = m_StartIncludesCarbs;
	}

	/**
	 * @return the m_CarbsAfterStartTime
	 */
	public synchronized boolean isM_CarbsAfterStartTime() {
		return m_CarbsAfterStartTime;
	}

	/**
	 * @param m_CarbsAfterStartTime the m_CarbsAfterStartTime to set
	 */
	public synchronized void setM_CarbsAfterStartTime(boolean m_CarbsAfterStartTime) {
		this.m_CarbsAfterStartTime = m_CarbsAfterStartTime;
	}

	/**
	 * @return the m_CorrectionAfterStartTime
	 */
	public synchronized boolean isM_CorrectionAfterStartTime() {
		return m_CorrectionAfterStartTime;
	}

	/**
	 * @param m_CorrectionAfterStartTime the m_CorrectionAfterStartTime to set
	 */
	public synchronized void setM_CorrectionAfterStartTime(boolean m_CorrectionAfterStartTime) {
		this.m_CorrectionAfterStartTime = m_CorrectionAfterStartTime;
	}

	/**
	 * @return the m_HigherResultAfterStartTime
	 */
	public synchronized boolean isM_HigherResultAfterStartTime() {
		return m_HigherResultAfterStartTime;
	}

	/**
	 * @param m_HigherResultAfterStartTime the m_HigherResultAfterStartTime to set
	 */
	public synchronized void setM_HigherResultAfterStartTime(boolean m_HigherResultAfterStartTime) {
		this.m_HigherResultAfterStartTime = m_HigherResultAfterStartTime;
	}

	/**
	 * @return the m_LowerResultAfterStartTime
	 */
	public synchronized boolean isM_LowerResultAfterStartTime() {
		return m_LowerResultAfterStartTime;
	}

	/**
	 * @param m_LowerResultAfterStartTime the m_LowerResultAfterStartTime to set
	 */
	public synchronized void setM_LowerResultAfterStartTime(boolean m_LowerResultAfterStartTime) {
		this.m_LowerResultAfterStartTime = m_LowerResultAfterStartTime;
	}

	/**
	 * @return the m_StartDayName
	 */
	public synchronized String getM_StartDayName() {
		return m_StartDayName;
	}

}
