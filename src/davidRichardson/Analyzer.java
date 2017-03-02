package davidRichardson;

import java.awt.EventQueue;
//import java.awt.Color;
//import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import davidRichardson.ThreadAutotune.AutotuneCompleteHandler;
import java.util.Date;


public class Analyzer extends DataExportExcel 
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	public 	enum AnalyzerMode
	{
		normal,
		summaryOnly,
		fullHistory,
	};

	public enum AnalyzerResult
	{
		analysisComplete,
		datesAreReversed,
		noDataToAnalyze,
	};

	// Lightweight class that can analyze results & provide commentary in text area	
	// Haha - used to be lightweight :-)


	ArrayList<AnalyzerSingleResult>   m_CombinedSingleResults;
	ArrayList<AnalyzerTrendResult>    m_CombinedTrendResults;

	// Trends where meals have been missed
	ArrayList<AnalyzerTrendResult>    m_SkippedBreakfastResults;
	ArrayList<AnalyzerTrendResult>    m_SkippedLunchResults;
	ArrayList<AnalyzerTrendResult>    m_SkippedDinnerResults;

	// Day Summary Results
	ArrayList<AnalyzerDaySummary>     m_AnalyzerDaySummaries;

	ArrayList <DBResult>              m_DBResults;
	ArrayList <DBResultEntry>         m_DBResultEntries;

	AnalyzerEntries                   m_AnalyzerEntries;

	Date                              m_StartDate;
	Date                              m_EndDate;

	// Convenient place holder when creating a trend result.  Reset to null once fully formed
	AnalyzerTrendResult               m_CurrentTrendResult     = null;
	// Convenient place holder when creating a hypo trend result.  Reset to null once fully formed
	AnalyzerTrendResult               m_CurrentHypoTrendResult = null;

	ArrayList<AnalyzerRecurringTrendResult>  m_AnalyzerRecurringTrendResultList;

	int                                      m_TotalRecurringTrends = 0;

	// New field (end July) to allow analyzer to run on first load or after synchronizing
	// That way we can provide encouragement to user to use analysis if there are some
	// interesting results
	boolean                           m_SummaryOnly;

	private Analyzer.AnalyzerMode     m_AnalyzerMode      = Analyzer.AnalyzerMode.normal;

	// Output file column names

	private String[]                  m_Analyzer_ColNames   = {"Category Name", "Importance Rank", "Opposite Name", "Rise, Fall or Flat", "Recommendations" };

	private String[]                  m_Parameter_ColNames  = {"Parameter", "Value", "Notes" };

	private String[]                  m_Highs_Lows_ColNames = {"Date", "Day Name", "TimeSlot", "Type", "Relevance", "BG", "Time"};

	private String[]                  m_Trends_ColNames     = {"ID", "Date", "Day Name", "Trend", "Start TimeSlot", "End TimeSlot", "Type", "Significance", "Start ID", "Start BG", "Start Time", "End ID", "End BG", "End Date", "End Time", "Multi-Carbs", "Intervening IDs", "Skipped IDs", "Category Code", "Commentary"};

	private String[]                  m_SingleRes_ColNames  = {"ID", "Start Trend ID", "End Trend ID", "DB Result ID", "Date", "Day Name", "TimeSlot", "Time", "Type", "BG", "Carbs", "Insulin", "ID Reason For Discard"};

	private String[]                  m_RecurringTrends_ColNames = {"ID", "Time Slot", "Trend", "Count", "Count %", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Significance", "TS Opposites", "TS Rises", "TS Falls", "Recommendation", "Further Insights" };

	private String[]                  m_Guidance_ColNames = {"Worksheet", "Observations", "Comment" };

	private String[]                  m_DaySummary_ColNames = {"ID", "Date", "Day Name", "Possible Duplicates", "Missing Meal BG", "Missing Meal Carbs", "Num Hypos", "Very Late Night BGs", "Very Late Night Corrections", "Very Late Night Hypos"};

	//	private String[]                  m_Skipped_Trends_ColNames     = {"Date", "Day Name", "Start TimeSlot", "End TimeSlot", "Type", "Significance", "Start BG", "Start Time", "End BG", "End Date", "End Time", "Skipped Entries", "Category Code", "Commentary"};

	private String[]                  m_Comparison_To_Full_History  = {"Time Slot", "Trend", "Full History Count", "Full History Count %", "This Analysis Count", "This Analysis Count %"};	

	//	private String[]                  m_CGM_Date_Ranges  = {"Start Date", "End Date", "Number of Days", "Average Readings per Day"};
	private String[]                  m_CGM_Date_Ranges  = {"Start Date", "End Date", "Overlap with Treatments", "Number of Days", "Number of Hypos", "Number of Hypers", "Number in Range", "Average Readings per Day"};

	//	private String[]                  m_CGM_Trends      = {"Start Hour", "End Hour", "Profile", "Offset", "Count"};
	//	private String[]                  m_CGM_Trends      = {"Start Hour", "End Hour", "Goes Hypo", "Goes Hyper", "Start Profile", "End Profile", "Profile Direction", "Offset", "Count"};

	private String[]                  m_CGM_Trends      = null;

	private String[]                  m_CGM_Results  = {"ID", "Date", "Trend Range", "Trend Profile Type", "Entry Interval ID", "Time", "CGM Value"};
	private String[]                  m_CGM_EntryIntervals  = {"ID", "Date", "Trend Range", "Trend Profile Type", "Goes Hypo", "Goes Hyper", "Start Profile", "End Profile", "Trend Result Entry ID", "Num CGM Entries"};
	private String[]                  m_CGM_TrendResultEntry  = {"ID", "Start Hour", "End Hour", "Trend Profile Type", "Goes Hypo", "Goes Hyper", "Start Profile", "End Profile", "Num CGM Intervals"};
	private String[]                  m_AutoTuneEntry_ColNames  = {"Parameter", "Current", "Autotune"};

	// CGM_Trends2 is dynamically generated :-)


	private ArrayList<L2AnalyzerTrendResultEnumWrapper> m_RiseEnumList = new ArrayList<L2AnalyzerTrendResultEnumWrapper>();
	private ArrayList<L2AnalyzerTrendResultEnumWrapper> m_FallEnumList = new ArrayList<L2AnalyzerTrendResultEnumWrapper>();
	private ArrayList<L2AnalyzerTrendResultEnumWrapper> m_FlatEnumList = new ArrayList<L2AnalyzerTrendResultEnumWrapper>();


	// The below are now all included in m_AnalyzerTrendCounts

	private int                        m_BreakfastTimeSlotRiseCnt;
	private int                        m_BreakfastTimeSlotFallCnt;
	//	private int                        m_BreakfastTimeSlotFlatCnt;
	private int                        m_LunchTimeSlotRiseCnt;
	private int                        m_LunchTimeSlotFallCnt;
	//	private int                        m_LunchTimeSlotFlatCnt;
	private int                        m_DinnerTimeSlotRiseCnt;
	private int                        m_DinnerTimeSlotFallCnt;
	//	private int                        m_DinnerTimeSlotFlatCnt;
	private int                        m_OvernightTimeSlotRiseCnt;
	private int                        m_OvernightTimeSlotFallCnt;
	//	private int                        m_OvernightTimeSlotFlatCnt;


	private Analyzer                   m_FullHistoryAnalyzer = null;     

	private ThreadAutotune             m_Autotune_Thread = null;
	private RemoteLinuxServer          m_Autotuner       = null;
	private WinTextWin                 m_AutotunerWin    = null;

	// To Do - future work.  Send analytical results to Excel for more detail
	Analyzer(ArrayList <DBResult> results, ArrayList<DBResultEntry> resultEntries)
	{
		initialize(results, resultEntries);
	}

	Analyzer(ArrayList <DBResult> results, ArrayList<DBResultEntry> resultEntries, boolean summaryOnly)
	{
		m_SummaryOnly = summaryOnly;
		initialize(results, resultEntries);
	}

	Analyzer(ArrayList <DBResult> results, ArrayList<DBResultEntry> resultEntries, Analyzer.AnalyzerMode mode)
	{
		m_AnalyzerMode = mode;
		initialize(results, resultEntries);
	}


	public void initialize(ArrayList<DBResultEntry> resultEntries)
	{
	}

	private void initialize(ArrayList <DBResult> results, ArrayList<DBResultEntry> resultEntries)
	{
		// Reset all the m_ID static counters
		AnalyzerSingleResult.resetStaticID();
		AnalyzerTrendResult.resetStaticID();
		AnalyzerDaySummary.resetStaticID();
		AnalyzerRecurringTrendResult.resetStaticID();
		AnalyzerEntriesCGMDay.resetStaticID();

		AnalyzerResultEntryInterval.resetStaticID();
		AnalyzerTrendResultAggregateGroup.resetStaticID();
		AnalyzerTrendResultEntry.resetStaticID();
		AnalyzerTrendResultEntryAggregate.resetStaticID();


		m_CombinedSingleResults = new ArrayList<AnalyzerSingleResult>();

		m_CombinedTrendResults      = new ArrayList<AnalyzerTrendResult>();

		m_SkippedBreakfastResults  = new ArrayList<AnalyzerTrendResult>();
		m_SkippedLunchResults      = new ArrayList<AnalyzerTrendResult>();
		m_SkippedDinnerResults     = new ArrayList<AnalyzerTrendResult>();

		m_AnalyzerDaySummaries      = new ArrayList<AnalyzerDaySummary>();

		// Retain copy of Nightscout Results
		m_DBResults                 = new ArrayList<DBResult>(results);
		m_DBResultEntries           = new ArrayList<DBResultEntry>(resultEntries);

		m_AnalyzerEntries           = new AnalyzerEntries(m_DBResultEntries, m_DBResults);
		m_AnalyzerEntries.initialize(resultEntries);

		m_StartDate                 = new Date(0);
		m_EndDate                   = new Date(0);

		m_AnalyzerRecurringTrendResultList  = new ArrayList<AnalyzerRecurringTrendResult>();

		// Sort the Mongo Results in ascending date order
		m_Logger.log(Level.FINE, "<"+this.getClass().getName()+"> Constructor BEFORE Sort First Entry is " + 
				(m_DBResults.size() > 0 ? m_DBResults.get(0).toString() : "EMPTY LIST"));

		Collections.sort(m_DBResults, new ResultFromDBComparator(false));

		m_Logger.log(Level.FINE, "<"+this.getClass().getName()+"> Constructor AFTER Sort First Entry is " + 
				(m_DBResults.size() > 0 ? m_DBResults.get(0).toString() : "EMPTY LIST"));


		// falls of interest
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_into_range_after_meal));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_into_hypo_after_meal));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_into_hypo_after_correction));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_into_range_no_carbs));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_into_hypo_no_carbs));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_overnight_into_range_no_intervention));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_overnight_into_range_but_hypo_first));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_overnight_into_hypo_no_intervention));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_out_of_range_after_meal));

		// Additional falls
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_overnight_into_hypo_but_hypo_first));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_overnight_into_hypo_but_corrected_first));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_overnight_out_of_range_but_hypo_first));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_out_of_range_no_carbs));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_out_of_range_after_correction));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_overnight_out_of_range_but_corrected_first));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_overnight_out_of_range_no_intervention));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_overnight_into_range_but_corrected_first));
		m_FallEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.fall_into_range_after_correction));

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
		m_RiseEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.rise_overnight_into_range_but_hypo_first));
		m_RiseEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.rise_overnight_into_range_no_intervention));
		m_RiseEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.rise_overnight_into_range_but_corrected_first));
		m_RiseEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.rise_into_range_no_carbs));
		m_RiseEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.rise_into_range_after_meal));
		m_RiseEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.rise_into_range_after_presumed_carbs));

		// flats of interest
		m_FlatEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.in_range_to_in_range));
		m_FlatEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.overnight_in_range_to_in_range_no_intervention));
		m_FlatEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.overnight_in_range_to_in_range_but_hypo_first));
		m_FlatEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.overnight_in_range_to_in_range_but_corrected_first));		
		m_FlatEnumList.add(new L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum.hypo_after_after_presumed_carbs));

		m_BreakfastTimeSlotRiseCnt = 0;
		m_BreakfastTimeSlotFallCnt = 0;
		//		m_BreakfastTimeSlotFlatCnt = 0;
		m_LunchTimeSlotRiseCnt     = 0; 
		m_LunchTimeSlotFallCnt     = 0;
		//		m_LunchTimeSlotFlatCnt     = 0;
		m_DinnerTimeSlotRiseCnt    = 0;
		m_DinnerTimeSlotFallCnt    = 0;
		//		m_DinnerTimeSlotFlatCnt    = 0;
		m_OvernightTimeSlotRiseCnt = 0;
		m_OvernightTimeSlotFallCnt = 0;
		//		m_OvernightTimeSlotFlatCnt = 0;

		// Initialize the full history analyzer from control
		m_FullHistoryAnalyzer = CoreNightScoutLoader.getInstance().getM_FullHistoryAnalyzer();
	}

	private static String[] getColumnNameArray(String[] stringArr)
	{
		String[] result = null;

		if (PrefsNightScoutLoader.getInstance().isM_AdvancedSettings())
		{
			result = stringArr.clone();
		}
		else
		{
			int len = stringArr.length;
			int ids = 0;
			int i   = 0;
			for (String c : stringArr)
			{
				if (c.contains("ID"))
				{
					ids++;
				}
			}

			result = new String[len - ids];
			for (String c : stringArr)
			{
				if (!c.contains("ID"))
				{
					result[i++] = new String(c);
				}
			}
		}	

		// See whether advanced Settings are set.
		// If so, then filter out the ID columns


		return result;
	}

	// Trend results are one of 3 types
	enum AnalyzerTrendResultTypeEnum
	{
		mealTrendType,      // Trend spans one meal to another.  Duration configured by num minutes
		hypoTrendType,      // Hypos can occur anywhere.  Duration is 15-30 minutes between results
		overnightTrendType  // An overnight trend checks trends overnight.  Start / End configured by time of day
	}


	// We'll review results over a period of time looking at trends
	enum L0AnalyzerSingleResultEnum
	{
		// Individual results
		tooLow,        // Typically        x <  3.1
		belowRange,    // Typically  3.1 < x <  4.0
		inRange,       // Typically  4.0 < x <  7.0
		aboveRange,    // Typically  7.0 < x < 14.0
		tooHigh,       // Typically 14.0 < x
	};


	// We'll review results over a period of time looking at trends
	enum L1AnalyzerTrendResultEnum
	{
		// Comparative results
		flat,
		rising,
		falling,
	};


	class L2AnalyzerTrendResultEnumWrapper
	{
		public L2AnalyzerTrendResultEnum m_L2AnalyzerTrendResultEnum;

		L2AnalyzerTrendResultEnumWrapper(L2AnalyzerTrendResultEnum e)
		{
			m_L2AnalyzerTrendResultEnum = e;
		}

	}


	// Much more sophisticated analysis based on context
	// Kind of clustering going on here.
	enum L2AnalyzerTrendResultEnum
	{		
		in_range_to_in_range,

		// Comparative results
		hypo_after_after_presumed_carbs,
		fall_into_range_after_meal,
		fall_out_of_range_after_meal,
		fall_into_hypo_after_meal,
		fall_into_range_after_correction,
		fall_out_of_range_after_correction,
		fall_into_hypo_after_correction,

		// New - no carbs entries
		fall_into_range_no_carbs,
		fall_out_of_range_no_carbs,
		fall_into_hypo_no_carbs,

		rise_into_range_after_meal,
		rise_out_of_range_after_meal,
		rise_in_to_out_of_range_after_meal, // New
		rise_into_range_after_correction,
		rise_out_of_range_after_correction,
		rise_in_to_out_of_range_after_correction, // New
		rise_into_range_after_presumed_carbs,
		rise_out_of_range_after_presumed_carbs,

		// New - no carbs entries
		rise_into_range_no_carbs,  // Used when compressing
		rise_out_of_range_no_carbs,  // Starts from in range
		rise_in_to_out_of_range_no_carbs,


		// Special ones for overnight
		rise_overnight_out_of_range_no_intervention,
		rise_overnight_out_of_range_but_hypo_first,       // Means Carbs were given overnight
		rise_overnight_out_of_range_but_corrected_first,  // Means Insulin was given overnight

		rise_overnight_into_range_no_intervention,
		rise_overnight_into_range_but_hypo_first,       // Means Carbs were given overnight
		rise_overnight_into_range_but_corrected_first,  // Means Insulin was given overnight

		fall_overnight_out_of_range_no_intervention,
		fall_overnight_out_of_range_but_hypo_first,
		fall_overnight_out_of_range_but_corrected_first,

		fall_overnight_into_range_no_intervention,
		fall_overnight_into_range_but_hypo_first,
		fall_overnight_into_range_but_corrected_first,

		fall_overnight_into_hypo_no_intervention,
		fall_overnight_into_hypo_but_hypo_first,
		fall_overnight_into_hypo_but_corrected_first,

		overnight_in_range_to_in_range_no_intervention,
		overnight_in_range_to_in_range_but_hypo_first,
		overnight_in_range_to_in_range_but_corrected_first,

	};




	public static String getL2TrendResultString(L2AnalyzerTrendResultEnum res)
	{
		String result = new String();

		switch (res)
		{
		case in_range_to_in_range:                     result = "In Range to In Range";           break;
		case hypo_after_after_presumed_carbs:          result = "Still Hypo after earlier Hypo";           break;
		case fall_into_range_after_meal:               result = "Fall into range after meal";     break;	
		case fall_out_of_range_after_meal:             result = "Fall but still outside range after meal";    break;	
		case fall_into_hypo_after_meal:                result = "Fall into Hypo after meal";    break;
		case fall_into_range_after_correction:         result = "Fall into range after correction";  break;
		case fall_out_of_range_after_correction:       result = "Fall but still outside range after correction"; break;
		case fall_into_hypo_after_correction:          result = "Fall into Hypo after correction"; break;

		case fall_into_range_no_carbs:                  result = "Fall into range no carbs"; break;
		case fall_out_of_range_no_carbs:                result = "Fall but still outside range no carbs"; break;
		case fall_into_hypo_no_carbs:                   result = "Fall into Hypo no carbs"; break;

		case rise_into_range_after_meal:               result = "Rise into range after meal";     break;
		case rise_out_of_range_after_meal:             result = "Rise out of range after meal";    break;		
		case rise_in_to_out_of_range_after_meal:       result = "Rise from in range to out of range after meal"; break;
		case rise_into_range_after_correction:         result = "Rise into range after correction";     break;
		case rise_out_of_range_after_correction:       result = "Rise out of range after correction"; break;				
		case rise_in_to_out_of_range_after_correction: result = "Rise from in range to out of range after correction"; break;				
		case rise_into_range_after_presumed_carbs:     result = "Rise into range after earlier Hypo";    break;		
		case rise_out_of_range_after_presumed_carbs:   result = "Rise out of range after earlier Hypo";   break;	

		case rise_into_range_no_carbs:                  result = "Rise into range after no Carbs";   break;	
		case rise_out_of_range_no_carbs:                result = "Rise out of range after no Carbs";   break;	
		case rise_in_to_out_of_range_no_carbs:          result = "Rise from in range to out of range after no Carbs";   break;

		// Special ones for overnight
		case rise_overnight_out_of_range_no_intervention:        result = "Overnight Rise Out of Range"; break;
		case rise_overnight_out_of_range_but_hypo_first:         result = "Overnight Rise Out of Range but Hypo first"; break;
		case rise_overnight_out_of_range_but_corrected_first:    result = "Overnight Rise Out of Range despite Correction"; break;

		case rise_overnight_into_range_no_intervention:          result = "Overnight Rise into Range"; break;
		case rise_overnight_into_range_but_hypo_first:           result = "Overnight Rise into Range but Hypo first"; break;
		case rise_overnight_into_range_but_corrected_first:      result = "Overnight Rise into Range with Correction"; break;

		case fall_overnight_out_of_range_no_intervention:        result = "Overnight Fall still outside Range"; break;
		case fall_overnight_out_of_range_but_hypo_first:         result = "Overnight Fall still outside Range but surprisingly Hypo first"; break;
		case fall_overnight_out_of_range_but_corrected_first:    result = "Overnight Fall still outside Range with a Correction first"; break;

		case fall_overnight_into_range_no_intervention:          result = "Overnight Fall into Range"; break;
		case fall_overnight_into_range_but_hypo_first:           result = "Overnight Fall into Range but Hypo first"; break;
		case fall_overnight_into_range_but_corrected_first:      result = "Overnight Fall into Range with a Correction first"; break;

		case fall_overnight_into_hypo_no_intervention:           result = "Overnight Fall into Hypo"; break;
		case fall_overnight_into_hypo_but_hypo_first:            result = "Overnight Fall into Hypo with earlier Hypo"; break;
		case fall_overnight_into_hypo_but_corrected_first:       result = "Overnight Fall into Hypo with a Correction first"; break;

		case overnight_in_range_to_in_range_no_intervention:     result = "Overnight In Range to In Range"; break;
		case overnight_in_range_to_in_range_but_hypo_first:      result = "Overnight In Range to In Range but Hypo first"; break;
		case overnight_in_range_to_in_range_but_corrected_first: result = "Overnight In Range to In Range with a Correction first"; break;

		}

		return result;
	}

	public static L2AnalyzerTrendResultEnum getL2TrendResultOpposite(L2AnalyzerTrendResultEnum res)
	{
		Analyzer.L2AnalyzerTrendResultEnum result = null;

		boolean analyzerCompressMealTrends           = PrefsNightScoutLoader.getInstance().isM_AnalyzerCompressMealTrends();

		// Only some results have a natural opposite
		switch (res)
		{
		//		case in_range_to_in_range:                     result = "In Range to In Range";           break;
		case hypo_after_after_presumed_carbs:          result = L2AnalyzerTrendResultEnum.rise_into_range_after_presumed_carbs;           break;
		case fall_into_range_after_meal:               result = (analyzerCompressMealTrends ? 
				L2AnalyzerTrendResultEnum.rise_out_of_range_after_meal : L2AnalyzerTrendResultEnum.rise_in_to_out_of_range_after_meal);     break;	
		case fall_out_of_range_after_meal:             result = L2AnalyzerTrendResultEnum.rise_out_of_range_after_meal;    break;	
		case fall_into_hypo_after_meal:                result = L2AnalyzerTrendResultEnum.rise_into_range_after_meal;    break;
		case fall_into_range_after_correction:         result = (analyzerCompressMealTrends ? 
				L2AnalyzerTrendResultEnum.rise_out_of_range_after_correction : L2AnalyzerTrendResultEnum.rise_in_to_out_of_range_after_correction);  break;
		case fall_out_of_range_after_correction:       result = L2AnalyzerTrendResultEnum.rise_out_of_range_after_correction; break;
		case fall_into_hypo_after_correction:          result = L2AnalyzerTrendResultEnum.rise_into_range_after_correction; break;

		case fall_into_range_no_carbs:                  result = L2AnalyzerTrendResultEnum.rise_into_range_no_carbs; break;
		case fall_out_of_range_no_carbs:                result = L2AnalyzerTrendResultEnum.rise_out_of_range_no_carbs; break;
		case fall_into_hypo_no_carbs:                   result = L2AnalyzerTrendResultEnum.rise_into_range_no_carbs; break;

		case rise_into_range_after_meal:               result = L2AnalyzerTrendResultEnum.fall_into_hypo_after_meal;     break;
		case rise_out_of_range_after_meal:             result = L2AnalyzerTrendResultEnum.fall_out_of_range_after_meal;    break;		
		case rise_in_to_out_of_range_after_meal:       result = L2AnalyzerTrendResultEnum.fall_into_range_after_meal; break;
		case rise_into_range_after_correction:         result = L2AnalyzerTrendResultEnum.fall_into_hypo_after_correction;     break;
		case rise_out_of_range_after_correction:       result = L2AnalyzerTrendResultEnum.fall_out_of_range_after_correction; break;				
		case rise_in_to_out_of_range_after_correction: result = L2AnalyzerTrendResultEnum.fall_into_range_after_correction; break;				
		case rise_into_range_after_presumed_carbs:     result = L2AnalyzerTrendResultEnum.hypo_after_after_presumed_carbs;    break;		
		case rise_out_of_range_after_presumed_carbs:   result = L2AnalyzerTrendResultEnum.hypo_after_after_presumed_carbs;   break;	

		case rise_into_range_no_carbs:                  result = L2AnalyzerTrendResultEnum.fall_into_range_no_carbs;   break;	
		case rise_out_of_range_no_carbs:                result = L2AnalyzerTrendResultEnum.fall_out_of_range_no_carbs;   break;	
		case rise_in_to_out_of_range_no_carbs:          result = L2AnalyzerTrendResultEnum.fall_into_range_no_carbs;   break;

		// Special ones for overnight
		case rise_overnight_out_of_range_no_intervention:        result = L2AnalyzerTrendResultEnum.fall_overnight_out_of_range_no_intervention; break;
		case rise_overnight_out_of_range_but_hypo_first:         result = L2AnalyzerTrendResultEnum.fall_overnight_out_of_range_but_hypo_first; break;
		case rise_overnight_out_of_range_but_corrected_first:    result = L2AnalyzerTrendResultEnum.fall_overnight_out_of_range_but_corrected_first; break;

		case rise_overnight_into_range_no_intervention:          result = L2AnalyzerTrendResultEnum.fall_overnight_into_range_no_intervention; break;
		case rise_overnight_into_range_but_hypo_first:           result = L2AnalyzerTrendResultEnum.fall_overnight_into_range_but_hypo_first; break;
		case rise_overnight_into_range_but_corrected_first:      result = L2AnalyzerTrendResultEnum.fall_overnight_into_range_but_corrected_first; break;

		case fall_overnight_out_of_range_no_intervention:        result = L2AnalyzerTrendResultEnum.rise_overnight_out_of_range_no_intervention; break;
		case fall_overnight_out_of_range_but_hypo_first:         result = L2AnalyzerTrendResultEnum.rise_overnight_out_of_range_but_hypo_first; break;
		case fall_overnight_out_of_range_but_corrected_first:    result = L2AnalyzerTrendResultEnum.rise_overnight_out_of_range_but_corrected_first; break;

		case fall_overnight_into_range_no_intervention:          result = L2AnalyzerTrendResultEnum.rise_overnight_into_range_no_intervention; break;
		case fall_overnight_into_range_but_hypo_first:           result = L2AnalyzerTrendResultEnum.rise_overnight_into_range_but_hypo_first; break;
		case fall_overnight_into_range_but_corrected_first:      result = L2AnalyzerTrendResultEnum.rise_overnight_into_range_but_corrected_first; break;

		case fall_overnight_into_hypo_no_intervention:           result = L2AnalyzerTrendResultEnum.rise_overnight_out_of_range_no_intervention; break;
		case fall_overnight_into_hypo_but_hypo_first:            result = L2AnalyzerTrendResultEnum.rise_overnight_out_of_range_but_hypo_first; break;
		case fall_overnight_into_hypo_but_corrected_first:       result = L2AnalyzerTrendResultEnum.rise_overnight_out_of_range_but_corrected_first; break;

		case overnight_in_range_to_in_range_no_intervention:     result = L2AnalyzerTrendResultEnum.overnight_in_range_to_in_range_no_intervention; break;
		case overnight_in_range_to_in_range_but_hypo_first:      result = L2AnalyzerTrendResultEnum.overnight_in_range_to_in_range_but_corrected_first; break;
		case overnight_in_range_to_in_range_but_corrected_first: result = L2AnalyzerTrendResultEnum.overnight_in_range_to_in_range_but_hypo_first; break;

		case in_range_to_in_range:                               result = L2AnalyzerTrendResultEnum.in_range_to_in_range; break;
		}

		return result;
	}

	public int countRecurringMatches(ArrayList<L2AnalyzerTrendResultEnumWrapper> oppList, DBResult.TimeSlot timeSlot)
	{
		int result = 0;

		// L2AnalyzerTrendResultEnum opposite = getL2TrendResultOpposite(res);
		for (AnalyzerTrendResult c : m_CombinedTrendResults)
		{
			for (L2AnalyzerTrendResultEnumWrapper opp : oppList)
			{
				if (c.getM_L2TrendResultEnum() == opp.m_L2AnalyzerTrendResultEnum &&
						c.getM_StartTimeSlot()      == timeSlot)
				{
					result++;
				}
			}
		}

		return result;
	}


	public int countRecurringMatches(L2AnalyzerTrendResultEnum opp, DBResult.TimeSlot timeSlot)
	{
		int result = 0;

		// L2AnalyzerTrendResultEnum opposite = getL2TrendResultOpposite(res);
		for (AnalyzerTrendResult c : m_CombinedTrendResults)
		{
			if (c.getM_L2TrendResultEnum() == opp &&
					c.getM_StartTimeSlot()     == timeSlot)
			{
				result++;
			}
		}

		return result;
	}

	public static String getL2TrendResultRecommendation(L2AnalyzerTrendResultEnum res)
	{
		return getL2TrendResultRecommendation(res, 0, 0, 0);	
	}

	public static String getL2TrendResultRecommendation(L2AnalyzerTrendResultEnum res,
			int oppCount, int rises, int falls)
	{
		String result = new String();

		switch (res)
		{
		case in_range_to_in_range:                     result = "Excellent result - no recommendation.";           break;
		case hypo_after_after_presumed_carbs:          result = "Presumed treatment of 15g carbs has failed.";           break;
		case fall_into_range_after_meal:               result = "Excellent result - no recommendation.";     break;	
		case fall_out_of_range_after_meal:             result = "Was sufficient correction given?  Is Carb Ratio correct for meal?" + (rises > 0 ? " (BEWARE - rises also detected.)" : "");    break;	
		case fall_into_hypo_after_meal:                result = "Is Carb Ratio too low for meal?"+ (rises > 0 ? " (BEWARE - rises also detected.)" : "");    break;	
		case fall_into_range_after_correction:         result = "Excellent result - no recommendation.";  break;
		case fall_out_of_range_after_correction:       result = "Was sufficient correction given?  Is sensitivity right for this time of day?" + (rises > 0 ? " (BEWARE - rises also detected.)" : "");    break;	
		case fall_into_hypo_after_correction:          result = "Was too much insulin given?  Is sensitivity right for this time of day?" + (rises > 0 ? " (BEWARE - rises also detected.)" : "");    break;	

		case fall_into_range_no_carbs:                  result = "Basal rate suggests a drop into range.  Was earlier correction given?  Is Basal rate too high?"; break;
		case fall_out_of_range_no_carbs:                result = "BG start value is too high.  Best address this (despite drop) before considering basal rate changes across meal."; break;
		case fall_into_hypo_no_carbs:                   result = "Basal rate suggests a drop into hypo.  Was earlier correction given?  Is Basal rate too high?"; break;

		case rise_into_range_after_meal:               result = "Excellent result - no recommendation.";     break;
		case rise_out_of_range_after_meal:             result = "Is Carb Ratio too high for meal?" + (falls > 0 ? " (BEWARE - falls also detected.)" : "");    break;			
		case rise_in_to_out_of_range_after_meal:       result = "Is Carb Ratio too high for meal?" + (falls > 0 ? " (BEWARE - falls also detected.)" : "");    break;
		case rise_into_range_after_correction:         result = "Excellent result - no recommendation.";     break;
		case rise_out_of_range_after_correction:       result = "Was sufficient correction given?  Is there an issue with set or site absorption?" + (falls > 0 ? " (BEWARE - falls also detected.)" : "");    break;
		case rise_in_to_out_of_range_after_correction: result = "Was sufficient correction given?  Is there an issue with set or site absorption?" + (falls > 0 ? " (BEWARE - falls also detected.)" : "");    break;				
		case rise_into_range_after_presumed_carbs:     result = "Excellent result - no recommendation.";    break;		
		case rise_out_of_range_after_presumed_carbs:   result = "Were too many grams of carbs given for hypo?";   break;

		case rise_into_range_no_carbs:                  result = "This may be a small fluctuation or the result of a previous hypo correction since Carbs are not recorded"; break;	
		case rise_out_of_range_no_carbs:                result = "BG start value is too high.  Best address this (despite rise) before considering basal rate changes across meal."; break;	
		case rise_in_to_out_of_range_no_carbs:          result = "Basal rate suggests a rise from in range into out of range.  Is Basal rate too low?";   break;


		// Special ones for overnight
		case rise_overnight_out_of_range_no_intervention:        result = "Consider increased Basal for overnight if pattern is strong" + (falls > 0 ? " (BEWARE - falls also detected.)" : "");    break;
		case rise_overnight_out_of_range_but_hypo_first:         result = "Was correction for Hypo too much?"; break;
		case rise_overnight_out_of_range_but_corrected_first:    result = "Intervening correction was not enough.  Was dose sufficient, is sensitivity correct, absorption issues?" + (falls > 0 ? " (BEWARE - falls also detected.)" : "");    break;

		case rise_overnight_into_range_no_intervention:          result = "Excellent ending up in range, but check for repeated hypos at bedtime"; break;
		case rise_overnight_into_range_but_hypo_first:           result = "Excellent carb correction intervention - no recommendation"; break;
		case rise_overnight_into_range_but_corrected_first:      result = "Excellent correction intervention - no recommendation"; break;

		case fall_overnight_out_of_range_no_intervention:        result = "Running high overnight could be issue with Dinner" + (rises > 0 ? " (BEWARE - rises also detected.)" : "");    break;
		case fall_overnight_out_of_range_but_hypo_first:         result = "High swings from high to low to high.  Was too much carb correction given?" + (rises > 0 ? " (BEWARE - rises also detected.)" : "");    break;
		case fall_overnight_out_of_range_but_corrected_first:    result = "Was earlier night time correction too great?" + (rises > 0 ? " (BEWARE - rises also detected.)" : "");    break;

		case fall_overnight_into_range_no_intervention:          result = "Excellent ending up in range, but check for repeated highs at bedtime"; break;
		case fall_overnight_into_range_but_hypo_first:           result = "Excellent ending up in range, but check for repeated hypos at during the night"; break;
		case fall_overnight_into_range_but_corrected_first:      result = "Excellent correction intervention - no recommendation"; break;

		case fall_overnight_into_hypo_no_intervention:           result = "Consider reduced Basal for overnight if pattern is strong" + (rises > 0 ? " (BEWARE - rises also detected.)" : "");    break;
		case fall_overnight_into_hypo_but_hypo_first:            result = "Consider reduced Basal for overnight if pattern is strong" + (rises > 0 ? " (BEWARE - rises also detected.)" : "");    break;
		case fall_overnight_into_hypo_but_corrected_first:       result = "Was earlier night time correction too great?" + (rises > 0 ? " (BEWARE - rises also detected.)" : "");    break;


		case overnight_in_range_to_in_range_no_intervention:     result = "Excellent result - no recommendation."; break;
		case overnight_in_range_to_in_range_but_hypo_first:      result = "Excellent result - Earlier Fall then carb correction."; break;
		case overnight_in_range_to_in_range_but_corrected_first: result = "Excellent result - Earlier High then corrected."; break;

		}

		return result;
	}

	public String furtherInsights(AnalyzerRecurringTrendResult res)
	{
		String result = new String();

		int sunCnt = res.getM_SunCnt();
		int monCnt = res.getM_MonCnt();
		int tueCnt = res.getM_TueCnt();
		int wedCnt = res.getM_WedCnt();
		int thuCnt = res.getM_ThuCnt();
		int friCnt = res.getM_FriCnt();
		int satCnt = res.getM_SatCnt();

		// Only add these insights if not excellent!
		if (!getL2TrendResultRecommendation(res.getM_L2TrendResultEnum()).contains("Excellent"))
		{
			// Is the significance high?
			if (res.getM_AverageRelevance() > 5)
			{
				result += "Signifiance ranges from 1 to 10, 10 represents highest changes.";
			}

			// Having obtained day counts include details in a string
			if (sunCnt > 1 || monCnt > 1 || tueCnt > 1 || wedCnt > 1 || thuCnt > 1 || friCnt > 1 || satCnt > 1)
			{
				if (res.getM_AverageRelevance() > 5)
				{
					result += "  Also there are Repeat incidents on same day. Consider common weekly patterns that might be influencing results. ";
				}
				else
				{
					result += "Repeat incidents on same day. Consider common weekly patterns that might be influencing results. ";
				}
			}

		}

		return result;
	}

	public class AnalyzerSingleResultComparator implements Comparator<AnalyzerSingleResult> 
	{
		private boolean m_DescendingSort = true;
		AnalyzerSingleResultComparator()
		{
			m_DescendingSort = true;
		}
		AnalyzerSingleResultComparator(boolean descendingSort)
		{
			m_DescendingSort = descendingSort;
		}

		public int compare(AnalyzerSingleResult p1, AnalyzerSingleResult p2) 
		{
			int  result     = 0;

			long p1Millies = p1.getM_DBResult().getM_EpochMillies();
			long p2Millies = p2.getM_DBResult().getM_EpochMillies();

			long diff       = (p1Millies - p2Millies);

			// Subtraction is too big for int result
			if ((diff) > 0)
			{
				//  1 to get results in ascending order
				// -1 to get results in descending order
				result = m_DescendingSort ? -1 : 1;
			}
			else if ((diff) < 0)
			{
				// -1 to get results in ascending order
				//  1 to get results in descending order
				result = m_DescendingSort ? 1 : -1;
			}
			return result;
		}
	}


	public class AnalyzerTrendResultComparator implements Comparator<AnalyzerTrendResult> 
	{		
		private boolean m_DescendingSort = true;
		private boolean m_DateSortOnly   = false;
		AnalyzerTrendResultComparator()
		{
			m_DescendingSort = true;
			m_DateSortOnly   = false;
		}
		AnalyzerTrendResultComparator(boolean descendingSort)
		{
			m_DescendingSort = descendingSort;
			m_DateSortOnly   = false;
		}
		AnalyzerTrendResultComparator(boolean descendingSort, boolean dateSortOnly)
		{
			m_DescendingSort = descendingSort;
			m_DateSortOnly   = dateSortOnly;
		}

		public int compare(AnalyzerTrendResult p1, AnalyzerTrendResult p2) 
		{
			int  result     = 0;

			long diff       = 0;
			if (m_DateSortOnly)
			{
				long p1Millies = p1.getM_AnalyzerSingleResult1().getM_DBResult().getM_EpochMillies();
				long p2Millies = p2.getM_AnalyzerSingleResult1().getM_DBResult().getM_EpochMillies();

				diff       = (p1Millies - p2Millies);
			}
			else
			{
				DBResult.TimeSlot p1_TimeSlot = p1.getM_AnalyzerSingleResult1().getM_TimeSlot();
				DBResult.TimeSlot p2_TimeSlot = p2.getM_AnalyzerSingleResult1().getM_TimeSlot();

				L2AnalyzerTrendResultEnum p1_L2TrendResultEnum = p1.getM_L2TrendResultEnum();
				L2AnalyzerTrendResultEnum p2_L2TrendResultEnum = p2.getM_L2TrendResultEnum();

				long p1_ts_num   = timeSlotToLong(p1_TimeSlot);
				long p2_ts_num   = timeSlotToLong(p2_TimeSlot);
				long p1_l2tr_num = l2TrendResultToLong(p1_L2TrendResultEnum);
				long p2_l2tr_num = l2TrendResultToLong(p2_L2TrendResultEnum);

				diff       = (p1_ts_num - p2_ts_num) + (p1_l2tr_num - p2_l2tr_num );
			}

			// Subtraction is too big for int result
			if ((diff) > 0)
			{
				//  1 to get results in ascending order
				// -1 to get results in descending order
				result = m_DescendingSort ? -1 : 1;
			}
			else if ((diff) < 0)
			{
				// -1 to get results in ascending order
				//  1 to get results in descending order
				result = m_DescendingSort ? 1 : -1;
			}
			return result;
		}
	}

	public class AnalyzerRecurringTrendResultComparator implements Comparator<AnalyzerRecurringTrendResult> 
	{		
		private boolean m_DescendingSort = true;
		AnalyzerRecurringTrendResultComparator()
		{
			m_DescendingSort = true;
		}
		AnalyzerRecurringTrendResultComparator(boolean descendingSort)
		{
			m_DescendingSort = descendingSort;
		}

		public int compare(AnalyzerRecurringTrendResult p1, AnalyzerRecurringTrendResult p2) 
		{
			int  result     = 0;

			int  p1_entries = p1.getM_TrendResultList().size();
			int  p2_entries = p2.getM_TrendResultList().size();

			boolean p1Issue = p1.isAnIssueTrend();
			boolean p2Issue = p2.isAnIssueTrend();

			// Subtraction is too big for int result
			long diff       = p1_entries - p2_entries;

			// First priority given to whether it's an issue or not
			if (p1Issue && !p2Issue)
			{
				result = m_DescendingSort ? -1 : 1;
			}
			else if (!p1Issue && p2Issue)
			{
				result = m_DescendingSort ? 1 : -1;
			}
			else if (diff > 0) 
			{
				//  1 to get results in ascending order
				// -1 to get results in descending order
				result = m_DescendingSort ? -1 : 1;
			}
			else if (diff < 0)
			{
				// -1 to get results in ascending order
				//  1 to get results in descending order
				result = m_DescendingSort ? 1 : -1;
			}
			return result;
		}
	}

	public class AnalyzerDaySummaryComparator implements Comparator<AnalyzerDaySummary> 
	{
		private boolean m_DescendingSort = true;
		AnalyzerDaySummaryComparator()
		{
			m_DescendingSort = true;
		}
		AnalyzerDaySummaryComparator(boolean descendingSort)
		{
			m_DescendingSort = descendingSort;
		}

		public int compare(AnalyzerDaySummary p1, AnalyzerDaySummary p2) 
		{
			int  result     = 0;

			long p1Millies = p1.getM_Date().getTime();
			long p2Millies = p2.getM_Date().getTime();

			long diff       = (p1Millies - p2Millies);

			// Subtraction is too big for int result
			if ((diff) > 0)
			{
				//  1 to get results in ascending order
				// -1 to get results in descending order
				result = m_DescendingSort ? -1 : 1;
			}
			else if ((diff) < 0)
			{
				// -1 to get results in ascending order
				//  1 to get results in descending order
				result = m_DescendingSort ? 1 : -1;
			}
			return result;
		}
	}


	private long timeSlotToLong(DBResult.TimeSlot timeSlot)
	{
		long result = 0;

		switch (timeSlot)
		{
		case UnknownTime   : result = 1; break;
		case BreakfastTime : result = 2; break;
		case LunchTime     : result = 3; break;
		case DinnerTime    : result = 4; break;
		case BedTime       : result = 5; break;
		}

		// Scale up
		result *= 1000;

		return result;
	}
	private long l2TrendResultToLong(L2AnalyzerTrendResultEnum l2TrendResult)
	{
		long result = 0;

		// In a preferred order

		switch (l2TrendResult)
		{
		// Hypos
		case hypo_after_after_presumed_carbs          : result = 1; break;
		case fall_into_hypo_after_meal                : result = 2; break;
		case fall_into_hypo_after_correction          : result = 3; break;

		case fall_into_range_no_carbs                  : result = 4; break;
		case fall_out_of_range_no_carbs                : result = 5; break;
		case fall_into_hypo_no_carbs                   : result = 6; break;

		case fall_overnight_into_hypo_but_hypo_first  : result = 7; break;
		case fall_overnight_into_hypo_no_intervention : result = 8; break;
		case fall_overnight_into_hypo_but_corrected_first : result = 9; break;

		// Interim overnight Hypos
		case rise_overnight_out_of_range_but_hypo_first : result = 10; break;
		case rise_overnight_into_range_but_hypo_first : result = 11; break;
		case fall_overnight_out_of_range_but_hypo_first : result = 12; break;
		case fall_overnight_into_range_but_hypo_first : result = 13; break;
		case overnight_in_range_to_in_range_but_hypo_first : result = 14; break;


		// Rises
		case rise_out_of_range_after_meal             : result = 15; break;
		case rise_in_to_out_of_range_after_meal       : result = 16; break;  // Group these together as similar analysis needed

		case rise_out_of_range_after_correction       : result = 17; break;
		case rise_in_to_out_of_range_after_correction : result = 18; break;
		case rise_out_of_range_after_presumed_carbs   : result = 19; break;


		case rise_into_range_no_carbs                  :	result = 19; break;
		case rise_out_of_range_no_carbs                :	result = 20; break;
		case rise_in_to_out_of_range_no_carbs          : result = 21; break;


		case rise_overnight_out_of_range_no_intervention   : result = 22; break;
		case rise_overnight_out_of_range_but_corrected_first   : result = 23; break;



		// Not enough of a fall
		case fall_out_of_range_after_meal             : result = 24; break;
		case fall_out_of_range_after_correction       : result = 25; break;
		case fall_overnight_out_of_range_no_intervention       : result = 26; break;
		case fall_overnight_out_of_range_but_corrected_first       : result = 27; break;


		// All the In Ranges
		case in_range_to_in_range                     : result = 28; break;
		case overnight_in_range_to_in_range_no_intervention : result = 29; break;
		case overnight_in_range_to_in_range_but_corrected_first : result = 30; break;
		case rise_overnight_into_range_no_intervention : result = 31; break;
		case rise_overnight_into_range_but_corrected_first : result = 32; break;
		case fall_overnight_into_range_no_intervention : result = 33; break;
		case fall_overnight_into_range_but_corrected_first : result = 34; break;

		// Comparative results
		case fall_into_range_after_meal               : result = 35; break;
		case fall_into_range_after_correction         : result = 36; break;

		case rise_into_range_after_meal               : result = 37; break;
		case rise_into_range_after_correction         : result = 38; break;
		case rise_into_range_after_presumed_carbs     : result = 39; break;

		}

		// Scale up
		result *= 10;

		return result;
	}

	public AnalyzerResult analyzeResults(ArrayList <DBResult> results, String excelFileName)
	{
		return analyzeResults(results, excelFileName, null);
	}
	
	public AnalyzerResult analyzeResults(ArrayList <DBResult> results, String excelFileName, WinTextWin autotunerWin)
	{
		AnalyzerResult result = AnalyzerResult.analysisComplete;

		//		boolean result = true;
		
		m_AutotunerWin = autotunerWin;
		
		
		if (m_SummaryOnly == true || m_AnalyzerMode == Analyzer.AnalyzerMode.summaryOnly)
		{
			Date lastDate = new Date(0);
			if (m_DBResults.size() > 0)
			{
				lastDate = m_DBResults.get(m_DBResults.size() - 1).getM_Time();
				long startDateLong = 0;
				long endDateLong   = lastDate.getTime();
				// Days back of 1 is actually 2 from WinAnalyzer
				// So we reproduce this for Summarised analysis
				long daysBack      = 14 + 1;

				// Do the calculation by hand to work out exactly 14 days earlier.
				startDateLong = endDateLong - daysBack * 24 * 60 * 60 * 1000; 

				m_Logger.log(Level.FINE, "** SUMMARY ANALYSIS ** " +
						" Start Date Long (" + startDateLong + "), End Date Long (" + endDateLong + ")" +
						" Last Date: " + lastDate);

				analyzeResults(results, daysBack, startDateLong, endDateLong, excelFileName);
			}
			else
			{
				result = AnalyzerResult.noDataToAnalyze;
			}
		}

		else if (m_AnalyzerMode == Analyzer.AnalyzerMode.fullHistory)
		{
			Date lastDate  = new Date(0);
			Date firstDate = new Date(0);
			if (m_DBResults.size() > 0)
			{
				firstDate = m_DBResults.get(0).getM_Time();
				lastDate  = m_DBResults.get(m_DBResults.size() - 1).getM_Time();
				long startDateLong = firstDate.getTime();
				long endDateLong   = lastDate.getTime();
				// Days back of 1 is actually 2 from WinAnalyzer
				// So we reproduce this for Summarised analysis
				long daysBack      = (endDateLong - startDateLong) / 24 * 60 * 60 * 1000;

				m_Logger.log(Level.FINE, "** FULL RANGE ANALYSIS ** " +
						" Start Date Long (" + startDateLong + "), End Date Long (" + endDateLong + ")" +
						" Last Date: " + lastDate);

				analyzeResults(results, daysBack, startDateLong, endDateLong, excelFileName);
			}
			else
			{
				result = AnalyzerResult.noDataToAnalyze;
			}
		}

		else
		{
			long daysBack      = PrefsNightScoutLoader.getInstance().getM_AnalyzerDaysBack();
			long startDateLong = PrefsNightScoutLoader.getInstance().getM_AnalyzerStartDateLong();
			long endDateLong   = PrefsNightScoutLoader.getInstance().getM_AnalyzerEndDateLong();

			// QUick fudge
			// We know JDatePicker uses times of midday, so add and subtract 12 hours accordingly.
			startDateLong -= 12 * 60 * 60 * 1000;
			endDateLong   += 12 * 60 * 60 * 1000;

			m_Logger.log(Level.FINE, "** REGULAR ANALYSIS ** " +
					" Start Date Long (" + startDateLong + "), End Date Long (" + endDateLong + ")" +	
					" First Date: " + new Date(startDateLong) +	
					" Last Date: " + new Date(endDateLong));	

			if (m_DBResults.size() == 0)
			{
				result = AnalyzerResult.noDataToAnalyze;
			}
			// Prevent non-sensical dates
			else if (startDateLong > endDateLong)
			{
				// Log as severe
				m_Logger.log(Level.SEVERE, "Unable to run analysis since start date is after end date.  Please review and try again.");
				result = AnalyzerResult.datesAreReversed;
			}
			else
			{
				// Analyze the CGM results too
				// Commented out for now
				// Have a choice ...
				//  Either make the initial load synchroized with entry load
				//  Or make a separate analyze method
				m_AnalyzerEntries.analyzeResults(startDateLong, endDateLong);

				// Then do the main analysis
				analyzeResults(results, daysBack, startDateLong, endDateLong, excelFileName);
			}

		}


		return result;
	}	

	public void analyzeResults(ArrayList <DBResult> results, long daysBack, 
			long startDateLong, long endDateLong, String excelFileName)
	{
		// Keep a record of start and end date / times
		this.m_StartDate = new Date(startDateLong);
		this.m_EndDate = new Date(endDateLong);

		// Take a physical copy of result list for our own sorting
		m_DBResults = new ArrayList<DBResult> (results);

		// Nullify all the other lists to ensure clean analysis
		m_CombinedSingleResults.clear();

		// Trends looking at comparative results
		m_CombinedTrendResults.clear();

		m_SkippedBreakfastResults.clear();
		m_SkippedLunchResults.clear();
		m_SkippedDinnerResults.clear();

		m_AnalyzerDaySummaries.clear();

		m_AnalyzerRecurringTrendResultList.clear();
		m_TotalRecurringTrends = 0;

		analyzeAutotune(startDateLong, endDateLong);

		analyzeResults(startDateLong, endDateLong);

		collectRecurringTrends();

		addAdditionalCounts();

		determinePercentages();

		//analyzeResults(100);
		try {
			if (m_SummaryOnly == false && m_AnalyzerMode == Analyzer.AnalyzerMode.normal)
			{
				writeResultsToExcel(excelFileName, startDateLong, endDateLong);
			}
			else if (m_SummaryOnly == true || m_AnalyzerMode == Analyzer.AnalyzerMode.summaryOnly)
			{
				summariseResultsToPanel(startDateLong, endDateLong);
			}
			else
			{
				m_Logger.log(Level.FINE, "Background Full History Analysis complete");
				// Full range is complete.
				// Decide what we summarise back to panel if anything
			}
		} 
		catch (FileNotFoundException e) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + ". Unable to save to file: " + excelFileName);
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + ".  Please close file in Excel if already open.");

			// Close the Autotune Window if open
			if (m_AutotunerWin != null && m_AutotunerWin.isEnabled())
			{
				// Swing is not threadsafe, so add a request to update the grid onto the even queue
				// Found this technique here:
				// http://www.informit.com/articles/article.aspx?p=26326&seqNum=9
				EventQueue.invokeLater(new 
						Runnable()
				{ 
					public void run()
					{ 
						m_AutotunerWin.setVisible(false);
					}
				});
			}
		}

		catch (IOException e)
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + ". Unexpected Exception saving Excel File for Analysis. " + e.getMessage());
		}
	}

	private void addAdditionalCounts()
	{
		// Determine rise & fall counts for each timeslot
		m_BreakfastTimeSlotRiseCnt  = countRecurringMatches(this.m_RiseEnumList, DBResult.TimeSlot.BreakfastTime);
		m_BreakfastTimeSlotFallCnt  = countRecurringMatches(this.m_FallEnumList, DBResult.TimeSlot.BreakfastTime);
		//		m_BreakfastTimeSlotFlatCnt  = countRecurringMatches(this.m_FlatEnumList, DBResult.TimeSlot.BreakfastTime);
		m_LunchTimeSlotRiseCnt      = countRecurringMatches(this.m_RiseEnumList, DBResult.TimeSlot.LunchTime);
		m_LunchTimeSlotFallCnt      = countRecurringMatches(this.m_FallEnumList, DBResult.TimeSlot.LunchTime);
		//		m_LunchTimeSlotFlatCnt      = countRecurringMatches(this.m_FlatEnumList, DBResult.TimeSlot.LunchTime);
		m_DinnerTimeSlotRiseCnt     = countRecurringMatches(this.m_RiseEnumList, DBResult.TimeSlot.DinnerTime);
		m_DinnerTimeSlotFallCnt     = countRecurringMatches(this.m_FallEnumList, DBResult.TimeSlot.DinnerTime);
		//		m_DinnerTimeSlotFlatCnt     = countRecurringMatches(this.m_FlatEnumList, DBResult.TimeSlot.DinnerTime);
		m_OvernightTimeSlotRiseCnt  = countRecurringMatches(this.m_RiseEnumList, DBResult.TimeSlot.BedTime);
		m_OvernightTimeSlotFallCnt  = countRecurringMatches(this.m_FallEnumList, DBResult.TimeSlot.BedTime);
		//		m_OvernightTimeSlotFlatCnt  = countRecurringMatches(this.m_FlatEnumList, DBResult.TimeSlot.BedTime);

		int totalTrends = this.m_CombinedTrendResults.size();

		// Now iterate over each recurring trend and set values
		for (AnalyzerRecurringTrendResult c:  m_AnalyzerRecurringTrendResultList)
		{
			L2AnalyzerTrendResultEnum oppositeL2TrendResult = getL2TrendResultOpposite(c.getM_L2TrendResultEnum());

			int oppCount = countRecurringMatches(oppositeL2TrendResult, c.getM_TimeSlot());
			c.setM_OppositeCnt(oppCount);
			c.setM_Percentage((double)c.getM_TrendResultList().size() / (double)totalTrends);
			switch (c.getM_TimeSlot())
			{
			case BreakfastTime:
				c.setM_TimeSlotFallCnt(m_BreakfastTimeSlotFallCnt);
				c.setM_TimeSlotRiseCnt(m_BreakfastTimeSlotRiseCnt);
				break;
			case LunchTime:
				c.setM_TimeSlotFallCnt(m_LunchTimeSlotFallCnt);
				c.setM_TimeSlotRiseCnt(m_LunchTimeSlotRiseCnt);
				break;
			case DinnerTime:
				c.setM_TimeSlotFallCnt(m_DinnerTimeSlotFallCnt);
				c.setM_TimeSlotRiseCnt(m_DinnerTimeSlotRiseCnt);
			case BedTime:
				c.setM_TimeSlotFallCnt(m_OvernightTimeSlotFallCnt);
				c.setM_TimeSlotRiseCnt(m_OvernightTimeSlotRiseCnt);
				break;
			default:
				break;
			}

			// Collect counts while we're here
			m_TotalRecurringTrends += c.getM_TrendResultList().size();
		}

	}

	void determinePercentages()
	{
		boolean totalRecurringTrendsOnly = PrefsNightScoutLoader.getInstance().isM_AnalyzerTotalRecurringTrendsOnly();
		int totalTrends = 0;

		if (totalRecurringTrendsOnly)
		{
			// Iterate over recurring trends and count totals
			// Now iterate over each recurring trend and set values
			for (AnalyzerRecurringTrendResult c:  m_AnalyzerRecurringTrendResultList)
			{
				totalTrends += c.getM_TrendResultList().size();
			}
		}
		else
		{
			totalTrends = this.m_CombinedTrendResults.size();
		}

		// Now iterate over each recurring trend and set values
		for (AnalyzerRecurringTrendResult c:  m_AnalyzerRecurringTrendResultList)
		{
			c.setM_Percentage((double)c.getM_TrendResultList().size() / (double)totalTrends);
		}
	}

	public static Date getLastDateFromDBResults(ArrayList<DBResult> resultList)
	{
		Date result = new Date(0);

		if (resultList.size() > 0)
		{
			// Sort the Mongo Results in descending date order initially
			Collections.sort(resultList, new ResultFromDBComparator(true));

			// First entry is most recent
			DBResult latest = resultList.get(0);

			try {
				result = CommonUtils.convertDateString(latest.getM_CP_EventTime(), DBResult.getCP_EventTimeFormat());
			} catch (ParseException e) 
			{
				m_Logger.log(Level.SEVERE, "<getLastDateFromDBResults>" + ". Unexpected Exception converting date. " + e.getMessage());
			}
		}

		return result;
	}

	public static Date getFirstDateFromDBResults(ArrayList<DBResult> resultList)
	{
		Date result = new Date(0);

		if (resultList.size() > 0)
		{
			// Sort the Mongo Results in descending date order initially
			Collections.sort(resultList, new ResultFromDBComparator(false));

			// First entry is most recent
			DBResult latest = resultList.get(0);

			try {
				result = CommonUtils.convertDateString(latest.getM_CP_EventTime(), DBResult.getCP_EventTimeFormat());
			} catch (ParseException e) 
			{
				m_Logger.log(Level.SEVERE, "<getFirstDateFromDBResults>" + ". Unexpected Exception converting date. " + e.getMessage());
			}
		}

		return result;
	}


	public static Date getDateOffsetBy(Date refDate, Long daysBack)
	{
		Integer  daysAdj = daysBack.intValue();
		Date result = getDateOffsetBy(refDate, daysAdj);

		return result;
	}

	public static Date getDateOffsetBy(Date refDate, Integer daysBack)
	{
		Date result = new Date(0);

		Calendar c = Calendar.getInstance();
		c.setTime(refDate);
		c.add(Calendar.DATE, (daysBack * (-1))); // Subtract daysBack
		result = c.getTime();

		return result;
	}

	private boolean settingsAreAllFine()
	{
		boolean result = true;  // All looks good, so proceed with analysis

		// Collect the include Settings and insist that at least one is set.

		boolean analyzeBreakfast                  = PrefsNightScoutLoader.getInstance().isM_AnalyzerIncludeBreakfast();
		boolean analyzeLunch                      = PrefsNightScoutLoader.getInstance().isM_AnalyzerIncludeLunch();
		boolean analyzeDinner                     = PrefsNightScoutLoader.getInstance().isM_AnalyzerIncludeDinner();
		boolean analyzeOvernight                  = PrefsNightScoutLoader.getInstance().isM_AnalyzerIncludeOvernight();

		result = (analyzeBreakfast || analyzeLunch || analyzeDinner || analyzeOvernight) ? true : false;

		return result;
	}

//	private void analyzeAutotune_triedButStillNotHappy(long startDateLong, long endDateLong)
//	{
//		// This had better not be summary or full history mode?	
//		// Is Autotune enabled to run?
//		// Then some additional basic checks ...
//		//   Is the server configured?
//		//   Is the NS URL defined?
//		if (m_AnalyzerMode != Analyzer.AnalyzerMode.summaryOnly &&
//				m_AnalyzerMode != Analyzer.AnalyzerMode.fullHistory &&
//				PrefsNightScoutLoader.getInstance().isM_AutoTuneInvoked() &&
//				!PrefsNightScoutLoader.getInstance().getM_AutoTuneServer().isEmpty() &&
//				!PrefsNightScoutLoader.getInstance().getM_AutoTuneNSURL().isEmpty())
//
//		{
//			Date startDate = new Date(startDateLong);
//			Date endDate   = new Date(endDateLong);
//			//			m_AutotunerWin = new WinTextWin("Nightscout Loader " + Version.getInstance().getM_Version() + " - Autotune (within Analysis)");
//			if (m_AutotunerWin != null)
//			{
//				/*
//				 * Below ok if we hold winremotelinuxserver rather than wintextwin
//				 * 
//				 * 
//				// Set dates
//				// We need to subtract one day from end Date since it's advanced
//				// to ensure we read treatment dates usually for regular analysis		
//				m_AutotunerWin.setDatesAndHandlerFromAnalyzer(startDate, 
//						CommonUtils.addDaysToDate(endDate, -1));	
//
//				m_AutotunerWin.runAutotune();
//				m_Autotune_Thread = m_AutotunerWin.getM_ATThread();
//				m_Autotuner = m_AutotunerWin.getM_Autotune();
//				*/
//				
//				
//				// Swing is not threadsafe, so add a request to update the grid onto the even queue
//				// Found this technique here:
//				// http://www.informit.com/articles/article.aspx?p=26326&seqNum=9
//				EventQueue.invokeLater(new 
//						Runnable()
//				{ 
//					public void run()
//					{ 
//						m_AutotunerWin.setVisible(true);
//					}
//				});
//			}
//		}
//		else
//		{
//			m_Autotune_Thread = null;
//		}
//	}

	private void analyzeAutotune(long startDateLong, long endDateLong)
	{
		// This had better not be summary or full history mode?	
		// Is Autotune enabled to run?
		// Then some additional basic checks ...
		//   Is the server configured?
		//   Is the NS URL defined?
		if (m_AnalyzerMode != Analyzer.AnalyzerMode.summaryOnly &&
				m_AnalyzerMode != Analyzer.AnalyzerMode.fullHistory &&
				PrefsNightScoutLoader.getInstance().isM_AutoTuneInvoked() &&
				!PrefsNightScoutLoader.getInstance().getM_AutoTuneServer().isEmpty() &&
				!PrefsNightScoutLoader.getInstance().getM_AutoTuneNSURL().isEmpty())

		{
			Date startDate = new Date(startDateLong);
			Date endDate   = new Date(endDateLong);
			//			m_AutotunerWin = new WinTextWin("Nightscout Loader " + Version.getInstance().getM_Version() + " - Autotune (within Analysis)");
			if (m_AutotunerWin != null)
			{
				// Swing is not threadsafe, so add a request to update the grid onto the even queue
				// Found this technique here:
				// http://www.informit.com/articles/article.aspx?p=26326&seqNum=9
				EventQueue.invokeLater(new 
						Runnable()
				{ 
					public void run()
					{ 
						m_AutotunerWin.setVisible(true);
					}
				});
			}

			// We need to subtract one day from end Date since it's advanced
			// to ensure we read treatment dates usually for regular analysis		
			m_Autotuner = new RemoteLinuxServer(startDate, CommonUtils.addDaysToDate(endDate, -1), m_AutotunerWin);

			m_Autotune_Thread = new ThreadAutotuneRun(m_Autotuner);
			m_Autotune_Thread.runThreadCommand(new AutotuneCompleteHandler(this) 
			{
				//		@Override
				public void exceptionRaised(String message) 
				{				
					// Set flag that main thread here will wait on...

				}

				//		@Override
				public void runAutotuneComplete(Object obj, String message) 
				{
					// m_Logger.log(Level.INFO, "Autotune Finished");				
					// Set flag that main thread here will wait on...
				}
			});
		}
		else
		{
			m_Autotune_Thread = null;
		}
	}

	
	public void analyzeResults(long startDateLong, long endDateLong)
	{
		if (settingsAreAllFine())
		{
			analyzeResultsNew(startDateLong, endDateLong);  // Issue with Excel file generation on highs & lows ...	
		}

		else
		{
			m_Logger.log(Level.INFO, "Analysis will not run since no meals are included.");
		}
	}

	public void analyzeResultsNew(long startDateLong, long endDateLong)
	{
		// Now sort the Mongo Results in ascending date order
		m_Logger.log(Level.FINE, "<"+this.getClass().getName()+"> analyzeResultsNew BEFORE Sort First Entry is " + 
				(m_DBResults.size() > 0 ? m_DBResults.get(0).toString() : "EMPTY LIST"));

		Collections.sort(m_DBResults, new ResultFromDBComparator(false));

		m_Logger.log(Level.FINE, "<"+this.getClass().getName()+"> analyzeResultsNew AFTER Sort First Entry is " + 
				(m_DBResults.size() > 0 ? m_DBResults.get(0).toString() : "EMPTY LIST"));

		// Reset the trend counter
		//	AnalyzerTrendCounts.getInstance().reset();

		Long     currDateLong = 0L;
		for (DBResult result : m_DBResults)
		{
			currDateLong = result.getM_Time().getTime();
			Double bg    = result.getM_CP_Glucose();

			if (bg != null && (startDateLong <= currDateLong) && (endDateLong >= currDateLong))
			{
				AnalyzerSingleResult singleResult = buildAnalyzerSingleResult(result);
				buildDaySummary(singleResult);

				categorizeResultNew(singleResult);				
				categorizeResultNew(singleResult);
			}
		}
	}

	private void collectRecurringTrends()
	{
		// This is the algorithm if we are interested in recurring trends only
		//  1 Sort the trend results using comparator
		//  2 Iterate through the trend results
		//  3 See what comes out.  Keep previous and compare with next
		//    if the same then create a recurring trend and store in list

		// 1
		ArrayList<AnalyzerTrendResult>  trendResultList = new ArrayList<AnalyzerTrendResult>(m_CombinedTrendResults);
		Collections.sort(trendResultList, new AnalyzerTrendResultComparator(false));

		AnalyzerTrendResult          prev        = null;
		AnalyzerRecurringTrendResult recTrendRes = null;

		// 2
		for (AnalyzerTrendResult curr : trendResultList)
		{			
			// 3
			if (prev != null)
			{
				// Only store a trend result if it's the same as the previous one
				if (prev.isRecurringTrendResult(curr))
				{
					// Create an entry for recurring events if not already, store in list too.
					// Simples ...

					if (recTrendRes != null && curr.isRecurringTrendResult(recTrendRes))
					{
						// This is a third entry added to the list - whooohooo!
						recTrendRes.addTrendResult(curr);

						// Check what's going on here!
						m_Logger.log(Level.FINE, "<"+this.getClass().getName()+">" + "Adding Entry to recurring Trend: "
								//						m_Logger.log(Level.INFO, "<"+this.getClass().getName()+">" + "Adding Entry to recurring Trend: "
								+ recTrendRes.toString() + " Adding: " + curr.toString());
					}
					else
					{
						recTrendRes = new AnalyzerRecurringTrendResult(prev, curr);
						m_AnalyzerRecurringTrendResultList.add(recTrendRes);
					}
				}
			}

			// Keep curr for next check
			prev = curr;


		}

		// Finally, sort the recurring trends into reverse size order
		Collections.sort(m_AnalyzerRecurringTrendResultList, new AnalyzerRecurringTrendResultComparator(true));

		// While here, also sort the other lists so dates are in descending order
		// Sort the Mongo Results in ascending date order
		m_Logger.log(Level.FINE, "<"+this.getClass().getName()+"> collectRecurringTrends BEFORE Sort First Entry is " + 
				(m_DBResults.size() > 0 ? m_DBResults.get(0).toString() : "EMPTY LIST"));
		Collections.sort(m_DBResults, new ResultFromDBComparator(true));
		m_Logger.log(Level.FINE, "<"+this.getClass().getName()+"> collectRecurringTrends AFTER Sort First Entry is " + 
				(m_DBResults.size() > 0 ? m_DBResults.get(0).toString() : "EMPTY LIST"));

		Collections.sort(m_CombinedTrendResults,  new AnalyzerTrendResultComparator(true, true));
		Collections.sort(m_AnalyzerDaySummaries,  new AnalyzerDaySummaryComparator(true));
		Collections.sort(m_CombinedSingleResults, new AnalyzerSingleResultComparator(true));

	}

	private void summariseResultsToPanel(long startDateLong, long endDateLong)
	{
		if (m_AnalyzerRecurringTrendResultList.size() > 0)
		{
			Date startDate = new Date(startDateLong);
			Date endDate   = new Date(endDateLong);

			AnalyzerRecurringTrendResult first  = m_AnalyzerRecurringTrendResultList.get(0);
			AnalyzerRecurringTrendResult second = (m_AnalyzerRecurringTrendResultList.size() > 1 ? 
					m_AnalyzerRecurringTrendResultList.get(1) : null);
			AnalyzerRecurringTrendResult third = (m_AnalyzerRecurringTrendResultList.size() > 2 ? 
					m_AnalyzerRecurringTrendResultList.get(2) : null);

			String commentary = new String("");

			commentary += "The most frequent trend is at " + DBResult.getTimeSlotString(first.getM_TimeSlot());
			commentary += (first.getM_TrendResultList().size() > 1 ? ", there are " + 
					first.getM_TrendResultList().size() + " entries showing " :
					", there is one entry showing ");
			commentary += getL2TrendResultString(first.getM_L2TrendResultEnum());

			if (second != null)
			{
				commentary += "\n";
				commentary += "(2nd @" + DBResult.getTimeSlotString(second.getM_TimeSlot());
				commentary += (second.getM_TrendResultList().size() > 1 ? ", has " + 
						second.getM_TrendResultList().size() + " entries: " :
						", one entry: ");
				commentary += getL2TrendResultString(second.getM_L2TrendResultEnum()) + ")";
			}
			if (third != null)
			{
				commentary += ", (3rd @" + DBResult.getTimeSlotString(third.getM_TimeSlot());
				commentary += (third.getM_TrendResultList().size() > 1 ? ", has " + 
						third.getM_TrendResultList().size() + " entries: " :
						", one entry: ");
				commentary += getL2TrendResultString(third.getM_L2TrendResultEnum())  + ")";
			}

			try {
				long days = (endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000);
				m_Logger.log(Level.INFO, days + " Days Background analysis complete on results between " + CommonUtils.convertDateString(startDate, "dd-MMM-YYYY") +
						" and " + CommonUtils.convertDateString(endDate, "dd-MMM-YYYY"));
			} catch (ParseException e) {
				m_Logger.log(Level.SEVERE, "Unexpected error handling date");
			}
			m_Logger.log(Level.INFO, commentary);
			m_Logger.log(Level.INFO, "See full results arranged in Excel from 'Action->Analyze Results'");
		}
	}

	private void writeResultsToExcel(String filename, long startDateLong, long endDateLong) 
			throws FileNotFoundException, IOException
	{

		// Only proceed if we have some results to look at
		if (m_DBResults.size() > 0)
		{
			if (m_Autotune_Thread != null)
			{
				m_Logger.log(Level.FINE, "writeResultsToExcel - Wait for meter thread - start ");
				m_Autotune_Thread.waitUntilFree();
				m_Logger.log(Level.FINE, "writeResultsToExcel - Wait for meter thread - done ");
				
				m_AutotunerWin = null;
			}

			HSSFWorkbook wb = new HSSFWorkbook();

			long diffMillies = endDateLong - startDateLong;
			int diffDays = (int)TimeUnit.DAYS.convert(diffMillies, TimeUnit.MILLISECONDS);

			// Generate Recommendations report
			writeGuidanceToExcel(wb);

			// Generate RemoteLinuxServer report
			writeAutoTuneToExcel(wb);

			// Generate Recurring Trend report based on number of days between start and end		
			writeRecurringTrendsToExcel(wb, diffDays);

			// Include a tab with a CGM trend heatmap - very nice
			writeCGMTrends(wb);

			// If there any CGM entries, then include a tab that summarises
			// date ranges that CGM was active.  Useful if not on CGM 24x7
			// Include CGM summary in Excel too
			writeCGMSummary(wb);

			// Generate Trends report
			writeTrendResultsToExcel(wb);

			// Identify skipped meals
			writeSkippedTrendResultsToExcel(wb);

			// Generate BGs Outside Range report
			writeOutsideRangeResultsToExcel(wb);

			// Generate summary of bad nights
			writeDaySummariesToExcel(wb);

			// Generate SIngle Results that are based on the raw data
			writeSingleResultsToExcel(wb);

			// Generate Raw data too
			writeRawDataToExcel(wb, startDateLong, endDateLong);

			// Summarise the intermediate CGM structures if enabled and data is there
			writeCGMTrendResultEntries(wb);
			writeCGMResultEntryIntervals(wb);
			writeCGMResults(wb);

			// Generate comparison to full history results
			writeFullHistoryTrendResultsToExcel(wb);

			// Generate comparison to full history results
			writeComparisonToFullHistory(wb);

			// Generate a summary of all analyzer parameters
			writeParametersToExcel(wb);

			// Generate a summary of all option parameters
			writeSettingsToExcel(wb);

			// Generate a summary of supported categories
			writeAnalyzerToExcel(wb);

			//			reorderExcelTabs(wb);

			
			// Now wait and generate RemoteLinuxServer report content details
			writeAutoTuneContentsToExcel(wb);
	
			
			wb.setActiveSheet(0);

			FileOutputStream out = new FileOutputStream(filename);
			wb.write(out);
			out.close();
			wb.close();
		}
	}

	private void writeAnalyzerToExcel(HSSFWorkbook wb)
	{
		String sheetName = new String("Trend Explanations");
		if (AnalyzerTabs.getInstance().isTabEnabled(sheetName))
		{
			Sheet sheet = wb.createSheet(sheetName);

			writeColumnHeaderRow(wb, sheet, m_Analyzer_ColNames);
			int rowNum = 1;
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.hypo_after_after_presumed_carbs, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.fall_into_hypo_no_carbs, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.fall_into_hypo_after_meal, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.fall_into_hypo_after_correction, sheet, rowNum);

			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.fall_overnight_into_hypo_but_hypo_first, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.fall_overnight_into_hypo_no_intervention, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.fall_overnight_into_hypo_but_corrected_first, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.rise_overnight_out_of_range_but_hypo_first, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.rise_overnight_into_range_but_hypo_first, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.fall_overnight_out_of_range_but_hypo_first, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.fall_overnight_into_range_but_hypo_first, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.overnight_in_range_to_in_range_but_hypo_first, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.rise_out_of_range_no_carbs, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.rise_out_of_range_after_meal, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.rise_in_to_out_of_range_no_carbs, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.rise_in_to_out_of_range_after_meal, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.rise_out_of_range_after_correction, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.rise_in_to_out_of_range_after_correction, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.rise_out_of_range_after_presumed_carbs, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.rise_overnight_out_of_range_no_intervention, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.rise_overnight_out_of_range_but_corrected_first, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.fall_out_of_range_no_carbs, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.fall_out_of_range_after_meal, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.fall_out_of_range_after_correction, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.fall_overnight_out_of_range_no_intervention, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.fall_overnight_out_of_range_but_corrected_first, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.in_range_to_in_range, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.overnight_in_range_to_in_range_no_intervention, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.overnight_in_range_to_in_range_but_corrected_first, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.rise_overnight_into_range_no_intervention, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.rise_overnight_into_range_but_corrected_first, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.fall_overnight_into_range_no_intervention, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.fall_overnight_into_range_but_corrected_first, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.fall_into_range_no_carbs, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.fall_into_range_after_meal, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.fall_into_range_after_correction, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.rise_into_range_no_carbs, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.rise_into_range_after_meal, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.rise_into_range_after_correction, sheet, rowNum);
			rowNum += addAnalyzerValues(L2AnalyzerTrendResultEnum.rise_into_range_after_presumed_carbs, sheet, rowNum);

			autoSizeColumns(sheet, m_Analyzer_ColNames);
		}
	}

	private void writeParametersToExcel(HSSFWorkbook wb)
	{
		String sheetName = new String("Parameters");
		if (AnalyzerTabs.getInstance().isTabEnabled(sheetName))
		{
			Sheet sheet = wb.createSheet(sheetName);

			writeColumnHeaderRow(wb, sheet, m_Parameter_ColNames);

			// Get Date as of now
			Date now = new Date();
			String date_time = new String("");
			final DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
			date_time = format.format(now);

			Date   start_date      = new Date(PrefsNightScoutLoader.getInstance().getM_AnalyzerStartDateLong());
			Date   end_date        = new Date(PrefsNightScoutLoader.getInstance().getM_AnalyzerEndDateLong());
			String start_date_time = new String("");
			String end_date_time   = new String("");
			start_date_time        = format.format(start_date);
			end_date_time          = format.format(end_date);

			// Now just add the rows one at a time...

			//	 m_Parameter_ColNames  = {"Parameter", "Value", "Notes" };
			int rowNum = 1;
			rowNum += addParameterValue("Date / Time run", date_time, "When the analysis was run", sheet, rowNum);
			rowNum += addParameterValue("BG UNITS", PrefsNightScoutLoader.getInstance().getM_BGUnits() == 0 ? "mmol/L" : "mg/dL", "KEY parameter in determining how your results are interpreted.  Please set accordingly!", sheet, rowNum);
			rowNum += addParameterValue("Excel Output Level", 
					PrefsNightScoutLoader.getInstance().getM_AnalyzerExcelOutputLevel() == 0 ? "Minimal Detail Excel Summary"  :
						PrefsNightScoutLoader.getInstance().getM_AnalyzerExcelOutputLevel() == 1 ? "Moderate Detail Excel Summary" : "Full Detail Excel Summary" , "Hides more complexity in output as needed", sheet, rowNum);
			rowNum += addParameterValue("Advanced Settings", PrefsNightScoutLoader.getInstance().isM_AdvancedSettings() ? "True" : "False", "Advanced Settings in the Settings tab.  If set to false, then all parameters are either default or unchanged from before", sheet, rowNum);
			rowNum += addParameterValue("Start Date", start_date_time, "Inclusive start date range for the analysis", sheet, rowNum);
			rowNum += addParameterValue("End Date", end_date_time, "Inclusive end date range for the analysis", sheet, rowNum);
			rowNum += addParameterValue("Breakfast Start Time", PrefsNightScoutLoader.getInstance().getM_AnalyzerBreakfastTimeStart(), "Defines start time for results in breakfast range", sheet, rowNum);
			rowNum += addParameterValue("Lunch Start Time", PrefsNightScoutLoader.getInstance().getM_AnalyzerLunchTimeStart(), "Defines start time for results in lunch range", sheet, rowNum);
			rowNum += addParameterValue("Dinner Start Time", PrefsNightScoutLoader.getInstance().getM_AnalyzerDinnerTimeStart(), "Defines start time for results in dinner range", sheet, rowNum);
			rowNum += addParameterValue("Bed Start Time", PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTimeStart(), "Defines start time for results in bed range", sheet, rowNum);
			rowNum += addParameterValue("BedTime Trend Start Start Time", PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendStartStartTime(), "Overnight trends start between this start time and end time", sheet, rowNum);
			rowNum += addParameterValue("BedTime Trend Start End Time", PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendStartEndTime(), "Overnight trends start between start time and this end time", sheet, rowNum);
			rowNum += addParameterValue("BedTime Trend End Start Time", PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendEndStartTime(), "Overnight trends end between this start time and end time", sheet, rowNum);
			rowNum += addParameterValue("BedTime Trend End End Time", PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendEndEndTime(), "Overnight trends end between start time and this end time", sheet, rowNum);	


			rowNum += addParameterValue("High Threshold", PrefsNightScoutLoader.getInstance().getM_AnalyzerHighThreshold(), "Values above this are considered 'too high'", sheet, rowNum);
			rowNum += addParameterValue("Low Threshold", PrefsNightScoutLoader.getInstance().getM_AnalyzerLowThreshold(), "Values below this are considered 'too low'", sheet, rowNum);
			rowNum += addParameterValue("High Threshold Relevance Factor", PrefsNightScoutLoader.getInstance().getM_AnalyzerHighThresholdRelevanceFactor(), "High BG Results are ranked from 1 to 10.  BGs outside range at this value and above represent 10", sheet, rowNum);
			rowNum += addParameterValue("Low Threshold Relevance Factor", PrefsNightScoutLoader.getInstance().getM_AnalyzerLowThresholdRelevanceFactor(), "Low BG results are ranked from 1 to 10.  BGs outside range at this value and below represent 10", sheet, rowNum);
			// not used rowNum += addParameterValue("Individual Trend Ratio", PrefsNightScoutLoader.getInstance().getM_AnalyzerIndividualTrendRatio(), "Low BG results are ranked from 1 to 10.  BGs outside range at this value and below represent 10", sheet, rowNum);
			rowNum += addParameterValue("Overnight Change Trend Ratio", PrefsNightScoutLoader.getInstance().getM_AnalyzerOvernightChangeTrendRatio(), "BG changes are ranked from 1 to 10.  BG change overnight at this value and above represent 10", sheet, rowNum);
			rowNum += addParameterValue("Breakfast Change Trend Ratio", PrefsNightScoutLoader.getInstance().getM_AnalyzerBreakfastChangeTrendRatio(), "BG changes are ranked from 1 to 10.  BG change from breakfast at this value and above represent 10", sheet, rowNum);
			rowNum += addParameterValue("Lunch Change Trend Ratio", PrefsNightScoutLoader.getInstance().getM_AnalyzerLunchChangeTrendRatio(), "BG changes are ranked from 1 to 10.  BG change from lunch at this value and above represent 10", sheet, rowNum);
			rowNum += addParameterValue("Dinner Change Trend Ratio", PrefsNightScoutLoader.getInstance().getM_AnalyzerDinnerChangeTrendRatio(), "BG changes are ranked from 1 to 10.  BG change from dinner at this value and above represent 10", sheet, rowNum);
			rowNum += addParameterValue("Minimum Minutes for Trend", (double)PrefsNightScoutLoader.getInstance().getM_AnalyzerMinMinsForTrendResults(), "To make a trend, two BG results must be at least this number of minutes apart", sheet, rowNum);

			rowNum += addParameterValue("Ratio of incidents / number of days as High Priority", PrefsNightScoutLoader.getInstance().getM_AnalyzerHighFrequencyPercentage(), "For recurring trends, if the raio of incidents exceeds this, then it's coloured RED", sheet, rowNum);
			rowNum += addParameterValue("Ratio of incidents / number of days as Medium Priority", PrefsNightScoutLoader.getInstance().getM_AnalyzerMediumFrequencyPercentage(), "For recurring trends, if the raio of incidents exceeds this, then it's coloured AMBER", sheet, rowNum);

			rowNum += addParameterValue("High Normal Range", PrefsNightScoutLoader.getInstance().getM_AnalyzerHighRangeThreshold(), "Upper range of 'normal' BG levels.  Typically 7 mmol/L", sheet, rowNum);
			rowNum += addParameterValue("Low Normal Range", PrefsNightScoutLoader.getInstance().getM_AnalyzerLowRangeThreshold(), "Lower range of 'normal' BG levels.  Typically 4 mmol/L", sheet, rowNum);
			rowNum += addParameterValue("Bad Night Start Time", PrefsNightScoutLoader.getInstance().getM_AnalyzerBadNightStartTime(), "Start time of 'silly o-clock' BG results.", sheet, rowNum);
			rowNum += addParameterValue("Bad Night End Time", PrefsNightScoutLoader.getInstance().getM_AnalyzerBadNightEndTime(), "End time of 'silly o-clock' BG results.", sheet, rowNum);

			rowNum += addParameterValue("CGM Trend Hour Intervals", PrefsNightScoutLoader.getInstance().getM_EntryAnalyzerIntervalHours(), "Either 1, 2 or 3 hour intervals CGM results are grouped into for trends.", sheet, rowNum);
			rowNum += addParameterValue("Compress Meal Trends", PrefsNightScoutLoader.getInstance().isM_AnalyzerCompressMealTrends() ? "Yes" : "No", "Rises into range and out of range treated same if enabled", sheet, rowNum);
			rowNum += addParameterValue("Total Recurring Trends Only", PrefsNightScoutLoader.getInstance().isM_AnalyzerTotalRecurringTrendsOnly() ? "Yes" : "No", "As example, if there are only 3 trend results and 2 recur, then if this is true 100% = 2 else 100% = 3", sheet, rowNum);
			rowNum += addParameterValue("CGM Trend Hour Intervals", PrefsNightScoutLoader.getInstance().getM_EntryAnalyzerIntervalHours(), "Either 1, 2 or 3 hourly intervals set on CGM Heat Map", sheet, rowNum);

			if (m_Autotune_Thread != null)
			{
				rowNum += addParameterValue("Autotune - Run Autotune", PrefsNightScoutLoader.getInstance().isM_AutoTuneInvoked() ? "True" : "False", "If True then Autotune output included in the spreadsheet as a separate tab", sheet, rowNum);
				rowNum += addParameterValue("Autotune - Nightscout URL", PrefsNightScoutLoader.getInstance().getM_AutoTuneNSURL(), "Nightscout URL used only for Autotune", sheet, rowNum);
				rowNum += addParameterValue("Autotune - Autotune Linux Server", PrefsNightScoutLoader.getInstance().getM_AutoTuneServer(), "Linux Server identified in form of user@host.domain", sheet, rowNum);
				rowNum += addParameterValue("Autotune - Autotune Key Auth File", PrefsNightScoutLoader.getInstance().getM_AutoTuneKeyFile(), "Path to Key Authentication file for Linux Server", sheet, rowNum);
			}

			autoSizeColumns(sheet, m_Parameter_ColNames);
		}
	}

	private void writeOutsideRangeResultsToExcel(HSSFWorkbook wb) 
	{
		String sheetName = new String("BGs Outside Range");
		if (AnalyzerTabs.getInstance().isTabEnabled(sheetName) &&
				m_CombinedSingleResults.size() > 0)
		{
			Sheet sheet = wb.createSheet(sheetName);

			writeColumnHeaderRow(wb, sheet, m_Highs_Lows_ColNames);

			int rowNum = 1;

			rowNum += addOutsideRangeRowResult(m_CombinedSingleResults, sheet, rowNum);

			autoSizeColumns(sheet, m_Highs_Lows_ColNames);
		}
	}

	private void writeRawDataToExcel(HSSFWorkbook wb, long startDateLong, long endDateLong)
	{
		String sheetName = new String("Treatment Data Analyzed");
		if (AnalyzerTabs.getInstance().isTabEnabled(sheetName) &&
				m_CombinedSingleResults.size() > 0)
		{
			Sheet sheet = wb.createSheet(sheetName);

			writeColumnHeaderRow(wb, sheet, getColumnNameArray(DBResult.getColNamesWithID()));

			Row row   = null;
			Cell cell = null;

			int      i = 1;

			Long     currDateLong = 0L;
			for (DBResult result : m_DBResults)
			{
				currDateLong = result.getM_Time().getTime();
				Double bg    = result.getM_CP_Glucose();

				if (bg != null && (startDateLong <= currDateLong) && (endDateLong >= currDateLong))
				{
					row = sheet.createRow(i++);

					int n = 0;

					if (PrefsNightScoutLoader.getInstance().isM_AdvancedSettings())
					{
						cell = row.createCell(n++);
						cell.setCellValue(result.getM_ID());	
					}

					String[] dbResArray = result.toArray(false);
					for (int j = 0; j < dbResArray.length; j++) 
					{
						cell = row.createCell(j + n);
						cell.setCellValue(dbResArray[j]);
					}
				}
			}	

			autoSizeColumns(sheet, getColumnNameArray(DBResult.getColNamesWithID()));
		}
	}

	private void writeSingleResultsToExcel(HSSFWorkbook wb)
	{
		String sheetName = new String("Single Results");
		if (AnalyzerTabs.getInstance().isTabEnabled(sheetName) &&
				m_CombinedSingleResults.size() > 0)
		{
			Sheet sheet = wb.createSheet(sheetName);

			writeColumnHeaderRow(wb, sheet, getColumnNameArray(m_SingleRes_ColNames));

			int rowNum = 1;

			rowNum += addSingleResult(m_CombinedSingleResults, wb, sheet, rowNum);

			autoSizeColumns(sheet, getColumnNameArray(m_SingleRes_ColNames));
		}
	}

	private void writeTrendResultsToExcel(HSSFWorkbook wb)
	{
		String sheetName = new String("Trends");
		if (AnalyzerTabs.getInstance().isTabEnabled(sheetName) &&
				m_CombinedTrendResults.size() > 0)
		{
			Sheet sheet = wb.createSheet(sheetName);

			writeColumnHeaderRow(wb, sheet, getColumnNameArray(m_Trends_ColNames));

			int rowNum = 1;

			rowNum += addTrendResult(m_CombinedTrendResults, wb, sheet, rowNum);

			autoSizeColumns(sheet, getColumnNameArray(m_Trends_ColNames));
		}
	}

	private void writeSkippedTrendResultsToExcel(HSSFWorkbook wb)
	{
		String sheetName = new String("Skipped Meal Trends");
		if (AnalyzerTabs.getInstance().isTabEnabled(sheetName) &&
				m_CombinedTrendResults.size() > 0)
		{
			Sheet sheet = wb.createSheet(sheetName);
			// This tab shares exactly the same structure as the Trends

			writeColumnHeaderRow(wb, sheet, getColumnNameArray(m_Trends_ColNames));

			int rowNum = 1;

			// Filter specifically for results that have no carbs only
			rowNum += addTrendResult(m_CombinedTrendResults, wb, sheet, rowNum, true, false);

			autoSizeColumns(sheet, getColumnNameArray(m_Trends_ColNames));
		}

	}

	private void writeRecurringTrendsToExcel(HSSFWorkbook wb, long daysBack)
	{
		String sheetName = new String("Recurring Trends");
		if (AnalyzerTabs.getInstance().isTabEnabled(sheetName) &&
				m_AnalyzerRecurringTrendResultList.size() > 0)
		{
			Sheet sheet = wb.createSheet(sheetName);

			writeColumnHeaderRow(wb, sheet, getColumnNameArray(m_RecurringTrends_ColNames));

			int rowNum = 1;
			rowNum += addRecurringTrendResult(m_AnalyzerRecurringTrendResultList, wb, sheet, rowNum, daysBack);	

			autoSizeColumns(sheet, getColumnNameArray(m_RecurringTrends_ColNames));
		}
	}
	//	private void reorderExcelTabs(HSSFWorkbook wb)
	//	{
	//		int i = 0;
	//
	//		wb.setSheetOrder("Guide to Tabs", i++);
	//		wb.setSheetOrder("Recurring Trends", i++);
	//		wb.setSheetOrder("Trends", i++);
	//		wb.setSheetOrder("Skipped Meal Trends", i++);
	//		wb.setSheetOrder("BGs Outside Range", i++);
	//		wb.setSheetOrder("Day Summaries", i++);
	//		wb.setSheetOrder("Single Results", i++);
	//		wb.setSheetOrder("Treatment Data Analyzed", i++);
	//		if (PrefsNightScoutLoader.getInstance().isM_AdvancedSettings())
	//		{
	//			wb.setSheetOrder("Comparison to Full History", i++);
	//			wb.setSheetOrder("Full History Trends", i++);
	//		}
	//		wb.setSheetOrder("Parameters", i++);
	//		wb.setSheetOrder("Settings", i++);
	//		wb.setSheetOrder("Trend Explanations", i++);
	//
	//		wb.setActiveSheet(0);
	//
	//	}


	// Experimental for now.
	// Not sure even higher level of aggregation adds much value
	private void writeGuidanceToExcel(HSSFWorkbook wb)
	{
		String sheetName = new String("Guide to Tabs");
		if (AnalyzerTabs.getInstance().isTabEnabled(sheetName))
		{
			Sheet sheet = wb.createSheet(sheetName);
			writeColumnHeaderRow(wb, sheet, m_Guidance_ColNames);

			int rowNum = 1;
			rowNum += addGuidance(wb, sheet, rowNum);	

			autoSizeColumns(sheet, m_Guidance_ColNames);
		}

	}

	private void writeAutoTuneToExcel(HSSFWorkbook wb)
	{
		// We pause the writing of Excel file to actually run Autotune if configured
		// This is a quick way of integrating its output in analysis ... for now
		if (m_Autotune_Thread != null)
		{			
			String sheetName = new String("Autotune");
			if (AnalyzerTabs.getInstance().isTabEnabled(sheetName))
			{
				Sheet sheet = wb.createSheet(sheetName);
				writeColumnHeaderRow(wb, sheet, m_AutoTuneEntry_ColNames);
			}
		}
	}
	
	private void writeAutoTuneContentsToExcel(HSSFWorkbook wb)
	{
		// We pause the writing of Excel file to actually run Autotune if configured
		// This is a quick way of integrating its output in analysis ... for now
		if (m_Autotune_Thread != null)
		{			
			m_Logger.log(Level.INFO, "Trend & CGM Analysis complete. Waiting on Aututune thread to finish." );
			// Should be free already, but just in case
			m_Autotune_Thread.waitUntilFree();
			m_Logger.log(Level.INFO, "Autotune finished.  Now finalising Excel sheet." );

			String sheetName = new String("Autotune");
			if (AnalyzerTabs.getInstance().isTabEnabled(sheetName))
			{
				Sheet sheet = wb.getSheet(sheetName);
				writeColumnHeaderRow(wb, sheet, m_AutoTuneEntry_ColNames);
				int rowNum = 1;

				rowNum += m_Autotuner.writeAutotuneOutputToExcel(wb, sheet, rowNum);
				autoSizeColumns(sheet, m_AutoTuneEntry_ColNames);
			}
		}
	}

	private void writeFullHistoryTrendResultsToExcel(HSSFWorkbook wb)
	{
		String sheetName = new String("Full History Trends");
		if (AnalyzerTabs.getInstance().isTabEnabled(sheetName) &&
				PrefsNightScoutLoader.getInstance().isM_AdvancedSettings() &&
				this.m_FullHistoryAnalyzer != null)
		{
			Sheet sheet = wb.createSheet(sheetName);

			writeColumnHeaderRow(wb, sheet, getColumnNameArray(m_Trends_ColNames));

			int rowNum = 1;

			rowNum += addTrendResult(m_FullHistoryAnalyzer.m_CombinedTrendResults, wb, sheet, rowNum);

			autoSizeColumns(sheet, getColumnNameArray(m_Trends_ColNames));
		}
	}

	private void writeComparisonToFullHistory(HSSFWorkbook wb)
	{
		String sheetName = new String("Comparison to Full History");
		if (AnalyzerTabs.getInstance().isTabEnabled(sheetName) && 
				PrefsNightScoutLoader.getInstance().isM_AdvancedSettings() &&
				this.m_FullHistoryAnalyzer != null)
		{
			HSSFSheet sheet = wb.createSheet(sheetName);

			//			Sheet sheet = wb.createSheet("Comparison to Full History");
			//			sheet.createFreezePane(0,1);

			writeColumnHeaderRow(wb, sheet, getColumnNameArray(m_Comparison_To_Full_History));

			int rowNum = 1;
			rowNum += addComparisonToFullHistory(this.m_FullHistoryAnalyzer.m_AnalyzerRecurringTrendResultList, wb, sheet, rowNum);	

			autoSizeColumns(sheet, getColumnNameArray(m_Comparison_To_Full_History));
		}

	}

	private void writeCGMSummary(HSSFWorkbook wb)
	{
		// We only do this if the full history is available and there are CGM results
		String sheetName = new String("CGM Summary");
		if (AnalyzerTabs.getInstance().isTabEnabled(sheetName) &&
				m_DBResultEntries.size() > 0)
		{
			HSSFSheet sheet = wb.createSheet(sheetName);
			//			Sheet sheet = wb.createSheet("Comparison to Full History");
			//			sheet.createFreezePane(0,1);

			writeColumnHeaderRow(wb, sheet, getColumnNameArray(m_CGM_Date_Ranges));
			int rowNum = 1;

			rowNum += addCGMSummary(this.m_AnalyzerEntries, wb, sheet, rowNum);	

			autoSizeColumns(sheet, getColumnNameArray(m_CGM_Date_Ranges));
		}

	}

	//	private void writeCGMTrends(HSSFWorkbook wb)
	//	{
	//	
	//		// We only do this if the full history is available and there are CGM results
	//		if (this.m_DBResultEntries.size() > 0)
	//		{
	//			//			Sheet sheet = wb.createSheet("Comparison to Full History");
	//			HSSFSheet sheet = wb.createSheet("CGM Trends");
	//			//			sheet.createFreezePane(0,1);
	//
	//			writeColumnHeaderRow(wb, sheet, getColumnNameArray(m_CGM_Trends));
	//			int rowNum = 1;
	//
	//			rowNum += addCGMTrends(this.m_AnalyzerEntries, wb, sheet, rowNum);	
	//
	//			autoSizeColumns(sheet, getColumnNameArray(m_CGM_Trends));
	//		}
	//	}

	private void writeCGMTrends(HSSFWorkbook wb)
	{
		String sheetName = new String("CGM Heat Map");
		if (AnalyzerTabs.getInstance().isTabEnabled(sheetName) &&
				m_AnalyzerEntries.getM_InRangeDBResultEntries().size() > 0)
		{

			int  interval = PrefsNightScoutLoader.getInstance().getM_EntryAnalyzerIntervalHours();
			int  blocks   = 24 / interval;

			// Allocate enough time slots
			m_CGM_Trends = new String[blocks + 1];

			m_CGM_Trends[0] = "Profile Type";

			//			// Headers are dynamically added based on the interval configured
			//			for (int i = 0; i < 24; i += interval)
			//			{
			//				m_CGM_Trends[i + 2] = new String( (i < 10 ? "0" : "") + i + "->");
			//			}

			// Headers are dynamically added based on the interval configured
			for (int i = 0; i < blocks; i++)
			{
				m_CGM_Trends[i + 1] = new String( ((i * interval) < 10 ? "0" : "") + (i * interval) + "->");
			}

			// We only do this if the full history is available and there are CGM results
			if (this.m_DBResultEntries.size() > 0)
			{
				//			Sheet sheet = wb.createSheet("Comparison to Full History");
				//			sheet.createFreezePane(0,1);
				HSSFSheet sheet = wb.createSheet(sheetName);

				writeColumnHeaderRow(wb, sheet, getColumnNameArray(m_CGM_Trends));
				int rowNum = 1;

				//			rowNum += addCGMTrends("Minus 1", this.m_AnalyzerEntries.getM_TrendResultEntriesAggregates().getM_OffsetMinusOneAggregateGroup(), 
				//					wb, sheet, rowNum);	
				rowNum += addCGMTrends("No Offset", this.m_AnalyzerEntries.getM_TrendResultEntriesAggregates().getM_NoOffsetAggregateGroup(), 
						wb, sheet, rowNum);	
				//			rowNum += addCGMTrends("Plus 1", this.m_AnalyzerEntries.getM_TrendResultEntriesAggregates().getM_OffsetPlusOneAggregateGroup(), 
				//					wb, sheet, rowNum);	

				autoSizeColumns(sheet, getColumnNameArray(m_CGM_Trends));
			}
		}
	}

	private void writeCGMResults(HSSFWorkbook wb)
	{
		String sheetName = new String("In Range CGM Results");
		if (AnalyzerTabs.getInstance().isTabEnabled(sheetName) &&
				m_AnalyzerEntries.getM_InRangeDBResultEntries().size() > 0)
		{
			HSSFSheet sheet = wb.createSheet(sheetName);

			//			Sheet sheet = wb.createSheet("Comparison to Full History");
			//			sheet.createFreezePane(0,1);

			writeColumnHeaderRow(wb, sheet, getColumnNameArray(m_CGM_Results));
			int rowNum = 1;

			rowNum += addCGMResults(wb, sheet, rowNum);	

			autoSizeColumns(sheet, getColumnNameArray(m_CGM_Results));
		}

	}

	// 13 jan
	// fix details so we can see the AnalyzerResultEntryIntervals 
	private void writeCGMResultEntryIntervals(HSSFWorkbook wb)
	{
		String sheetName = new String("In Range CGM Entry Intervals");
		if (AnalyzerTabs.getInstance().isTabEnabled(sheetName) &&
				m_AnalyzerEntries.getM_InRangeDBResultEntries().size() > 0)
		{
			HSSFSheet sheet = wb.createSheet(sheetName);

			//			Sheet sheet = wb.createSheet("Comparison to Full History");
			//			sheet.createFreezePane(0,1);

			writeColumnHeaderRow(wb, sheet, getColumnNameArray(m_CGM_EntryIntervals));
			int rowNum = 1;

			rowNum += addCGMEntryIntervals(wb, sheet, rowNum);	

			autoSizeColumns(sheet, getColumnNameArray(m_CGM_EntryIntervals));
		}

	}


	private void writeCGMTrendResultEntries(HSSFWorkbook wb)
	{
		String sheetName = new String("In Range CGM Trend Result Entries");
		if (AnalyzerTabs.getInstance().isTabEnabled(sheetName) &&
				m_AnalyzerEntries.getM_InRangeDBResultEntries().size() > 0)
		{
			HSSFSheet sheet = wb.createSheet(sheetName);


			//			Sheet sheet = wb.createSheet("Comparison to Full History");
			//			sheet.createFreezePane(0,1);

			writeColumnHeaderRow(wb, sheet, getColumnNameArray(m_CGM_TrendResultEntry));
			int rowNum = 1;

			rowNum += addCGMTrendResultEntries(wb, sheet, rowNum);	

			autoSizeColumns(sheet, getColumnNameArray(m_CGM_TrendResultEntry));
		}

	}

	private void writeDaySummariesToExcel(HSSFWorkbook wb)
	{
		String sheetName = new String("Day Summaries");
		if (AnalyzerTabs.getInstance().isTabEnabled(sheetName) &&
				m_AnalyzerDaySummaries.size() > 0)
		{
			Sheet sheet = wb.createSheet(sheetName);

			writeColumnHeaderRow(wb, sheet, getColumnNameArray(m_DaySummary_ColNames));

			int rowNum = 1;
			rowNum += addDaySummaries(m_AnalyzerDaySummaries, wb, sheet, rowNum);	

			autoSizeColumns(sheet, getColumnNameArray(m_DaySummary_ColNames));
		}
	}

	private int addAnalyzerValues(L2AnalyzerTrendResultEnum resEnum, Sheet sheet, int rowNum)
	{
		int result = 0;

		Row row = null;
		Cell cell = null;

		// Row always one more since we add the title
		row = sheet.createRow(result + rowNum);
		int j = 0;

		// 	String[]                  m_Analyzer_ColNames   = {"Category Code", "Category Name", "Recommendations" };
		// 	private String[]                  m_Analyzer_ColNames   = {"Category Code", "Importance Rank", "Category Name", "Opposite Name", "Rise, Fall or Flat", "Recommendations" };

		cell = row.createCell(j++);
		cell.setCellValue(getL2TrendResultString(resEnum));
		cell = row.createCell(j++);
		cell.setCellValue(l2TrendResultToLong(resEnum));

		cell = row.createCell(j++);
		cell.setCellValue(getL2TrendResultString(getL2TrendResultOpposite(resEnum)));

		String direction = new String("");

		for (L2AnalyzerTrendResultEnumWrapper c : m_RiseEnumList)
		{
			if (c.m_L2AnalyzerTrendResultEnum == resEnum)
			{
				direction = "Rise";
				break;
			}
		}
		for (L2AnalyzerTrendResultEnumWrapper c : m_FallEnumList)
		{
			if (c.m_L2AnalyzerTrendResultEnum == resEnum)
			{
				direction = "Fall";
				break;
			}
		}
		for (L2AnalyzerTrendResultEnumWrapper c : m_FlatEnumList)
		{
			if (c.m_L2AnalyzerTrendResultEnum == resEnum)
			{
				direction = "Flat";
				break;
			}
		}

		cell = row.createCell(j++);
		cell.setCellValue(direction);

		cell = row.createCell(j++);
		cell.setCellValue(AnalyzerTrendResult.generateCommentary(resEnum));

		result++;

		return result;
	}

	private int addOutsideRangeRowResult(ArrayList<AnalyzerSingleResult> singeResultList, Sheet sheet, int rowNum)
	{
		int i = 0;
		int result = 0;

		Row row = null;
		Cell cell = null;


		// Add results from each list
		for (i=0;i<singeResultList.size();i++) 
		{
			// Row always one more since we add the title
			row = sheet.createRow(result + rowNum);
			AnalyzerSingleResult res = singeResultList.get(i);

			// Only proceed if res is of interest
			if (res.getM_AnalyzerSingleResultEnum() != L0AnalyzerSingleResultEnum.inRange)
			{
				int j = 0;

				// This needs to be aggregated.  Write an alternate class to do this based on results....			`

				// 	 m_Highs_Lows_ColNames = {"Date", "Day Name", "TimeSlot", "Type", "Relevance", "BG", "Time"};

				cell = row.createCell(j++);
				cell.setCellValue(res.getM_DBResult().getM_TreatmentDate());
				cell = row.createCell(j++);
				cell.setCellValue(res.getM_DayName());
				cell = row.createCell(j++);
				cell.setCellValue(DBResult.getTimeSlotString(res.getM_TimeSlot()));
				cell = row.createCell(j++);
				cell.setCellValue(res.getAnalyzerSingleResultEnumSring());
				cell = row.createCell(j++);
				cell.setCellValue(res.getM_RelevanceScore());
				cell = row.createCell(j++);
				cell.setCellValue(res.getM_DBResult().getM_CP_Glucose());
				cell = row.createCell(j++);
				cell.setCellValue(res.getM_DBResult().getM_TreatmentTime());

				result++;
			}

		}

		return result; // How many actually added
	}

	private int addSingleResult(ArrayList<AnalyzerSingleResult> singleResultList, HSSFWorkbook wb, Sheet sheet, int rowNum)
	{
		int i = 0;
		int result = 0;
		Row row = null;
		Cell cell = null;

		//	private String[]                  m_SingleRes_ColNames  = {"Date", "Day Name", "TimeSlot", "Time", "Type", "BG", "Carbs", "Insulin", "Reason For Discard"};

		// Add results from each list
		for (i=0;i<singleResultList.size();i++) 
		{
			int j = 0;
			// Row always one more since we add the title
			row = sheet.createRow(result + rowNum);
			AnalyzerSingleResult res = singleResultList.get(i);

			if (PrefsNightScoutLoader.getInstance().isM_AdvancedSettings())
			{
				cell = row.createCell(j++);
				cell.setCellValue(res.getM_ID());


				cell = row.createCell(j++);
				cell.setCellValue(res.getM_TrendStartResultID());

				cell = row.createCell(j++);
				cell.setCellValue(res.getM_TrendEndResultID());

				cell = row.createCell(j++);
				cell.setCellValue(res.getM_DBResult().getM_ID());				
			}

			cell = row.createCell(j++);
			cell.setCellValue(res.getM_DBResult().getM_TreatmentDate());

			cell = row.createCell(j++);
			cell.setCellValue(res.getM_DayName());

			cell = row.createCell(j++);
			cell.setCellValue(DBResult.getTimeSlotString(res.getM_TimeSlot()));

			cell = row.createCell(j++);
			cell.setCellValue(res.getM_DBResult().getM_TreatmentTime());

			cell = row.createCell(j++);
			cell.setCellValue(res.getAnalyzerSingleResultEnumSring());

			cell = row.createCell(j++);
			String glucose = res.getM_DBResult().getM_CP_Glucose() == null ? "" : res.getM_DBResult().getM_CP_Glucose().toString();
			cell.setCellValue(glucose);

			cell = row.createCell(j++);
			String carbs = res.getM_DBResult().getM_CP_Carbs() == null ? "" : res.getM_DBResult().getM_CP_Carbs().toString();
			cell.setCellValue(carbs);

			cell = row.createCell(j++);
			String insulin = res.getM_DBResult().getM_CP_Insulin() == null ? "" : res.getM_DBResult().getM_CP_Insulin().toString();
			cell.setCellValue(insulin);

			if (PrefsNightScoutLoader.getInstance().isM_AdvancedSettings())
			{
				cell = row.createCell(j++);
				cell.setCellValue(res.getM_ReasonForDiscard());		
			}

			result++;
		}

		return result;
	}


	private int addTrendResult(ArrayList<AnalyzerTrendResult> trendResultList, HSSFWorkbook wb, Sheet sheet, int rowNum)
	{
		return addTrendResult(trendResultList, wb, sheet, rowNum, false, false);
	}

	private int addTrendResult(ArrayList<AnalyzerTrendResult> trendResultList, HSSFWorkbook wb, Sheet sheet, int rowNum, 
			boolean noCarbsOnly, boolean carbsOnly)
	{
		int i = 0;
		int result = 0;

		Row row = null;
		Cell cell = null;

		// All Good Colouring / Font
		HSSFCellStyle allGoodStyle = wb.createCellStyle();
		//		allGoodStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		//		allGoodStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		HSSFFont allGoodFont = wb.createFont();
		//		allGoodFont.setColor(HSSFColor.WHITE.index);
		allGoodFont.setColor(HSSFColor.GREY_50_PERCENT.index);
		allGoodFont.setItalic(true);
		allGoodStyle.setFont(allGoodFont);

		// No particular format
		HSSFCellStyle regularStyle = wb.createCellStyle();

		// Add results from each list
		for (i=0;i<trendResultList.size();i++) 
		{
			// Row always one more since we add the title
			row = sheet.createRow(result + rowNum);
			AnalyzerTrendResult res = trendResultList.get(i);

			boolean noCarbs = res.isNoCarbsTrend();

			// We exclude if asked to supply noCarbs only and this has carbs,
			// or if asked to supply carbs only and this has no carbs.
			boolean exclude = false;
			exclude = (noCarbsOnly == true && noCarbs == false) ? true : exclude;
			exclude = (carbsOnly   == true && noCarbs == true)  ? true : exclude;

			// It's possible we started a trend but never completed it
			// In such cases, result2 is null
			if ( (res.getM_AnalyzerSingleResult1() != null & res.getM_AnalyzerSingleResult2() != null) && (exclude == false) )
			{
				int j = 0;

				// 	 m_Highs_Lows_ColNames = {"Date", "Day Name", "TimeSlot", "Type", "Relevance", "BG", "Time"};

				//   m_Trends_ColNames     = {"Date", "Day Name", "Start TimeSlot", "End TimeSlot", "Type", "Relevance"};
				// 	 m_Trends_ColNames     = {"Date", "Day Name", "Start TimeSlot", "End TimeSlot", "Type", "Relevance", "Start BG", "Start Time", "End BG", "End Date", "End Time", "Commentary"};
				// 	 m_Trends_ColNames     = {"ID", "Date", "Day Name", "Trend", "Start TimeSlot", "End TimeSlot", "Type", "Significance", "Start ID", "Start BG", "Start Time", "End ID", "End BG", "End Date", "End Time", "Skipped Entries", "Category Code", "Commentary"};
				//   m_Trends_ColNames     = {"ID", "Date", "Day Name", "Trend", "Start TimeSlot", "End TimeSlot", "Type", "Significance", "Start ID", "Start BG", "Start Time", "End ID", "End BG", "End Date", "End Time", "Intervening IDs", "Skipped IDs", "Category Code", "Commentary"};
				//   m_Trends_ColNames     = {"ID", "Date", "Day Name", "Trend", "Start TimeSlot", "End TimeSlot", "Type", "Significance", "Start ID", "Start BG", "Start Time", "End ID", "End BG", "End Date", "End Time", "Multi-Carbs", "Intervening IDs", "Skipped IDs", "Category Code", "Commentary"};


				HSSFCellStyle style = null;

				if (getL2TrendResultRecommendation(res.getM_L2TrendResultEnum()).contains("Excellent"))
				{
					style = allGoodStyle;
				}
				else
				{
					style = regularStyle;
				}
				String interveningIDs = new String();
				String skippedIDs     = new String();
				int    singleResultCarbs = 0;

				// Count the number of times carbs were given during this trend
				// Start with #1
				if (res.getM_AnalyzerSingleResult1().getM_DBResult().getM_CP_Carbs() != null)
				{
					singleResultCarbs++;
				}

				for (AnalyzerSingleResult c : res.getM_InterveningResults())
				{
					interveningIDs += interveningIDs.length() > 0 ? "," + c.getM_ID() : c.getM_ID();
					if (c.getM_DBResult().getM_CP_Carbs() != null)
					{
						singleResultCarbs++;
					}
				}
				for (AnalyzerSingleResult c : res.getM_SkippedResults())
				{
					skippedIDs += skippedIDs.length() > 0 ? "," + c.getM_ID() : c.getM_ID();
				}

				if (PrefsNightScoutLoader.getInstance().isM_AdvancedSettings())
				{
					cell = row.createCell(j++);
					cell.setCellValue(res.getM_ID());
					cell.setCellStyle(style);
				}

				cell = row.createCell(j++);
				cell.setCellValue(res.getM_AnalyzerSingleResult1().getM_DBResult().getM_TreatmentDate());
				cell.setCellStyle(style);

				cell = row.createCell(j++);
				cell.setCellValue(res.getM_AnalyzerSingleResult1().getM_DayName());
				cell.setCellStyle(style);

				cell = row.createCell(j++);				
				cell.setCellValue(getL2TrendResultString(res.getM_L2TrendResultEnum()));
				cell.setCellStyle(style);

				cell = row.createCell(j++);			
				cell.setCellValue(DBResult.getTimeSlotString(res.getM_AnalyzerSingleResult1().getM_TimeSlot()));
				cell.setCellStyle(style);

				cell = row.createCell(j++);
				cell.setCellValue(DBResult.getTimeSlotString(res.getM_AnalyzerSingleResult2().getM_TimeSlot()));
				cell.setCellStyle(style);

				cell = row.createCell(j++);
				cell.setCellValue(res.getTrendResultEnumSring());
				cell.setCellStyle(style);

				cell = row.createCell(j++);
				cell.setCellValue(res.getM_RelevanceScore());
				if (PrefsNightScoutLoader.getInstance().isM_AdvancedSettings())
				{

					cell = row.createCell(j++);
					cell.setCellValue(res.getM_AnalyzerSingleResult1().getM_ID());
				}
				cell = row.createCell(j++);				
				cell.setCellValue(res.getM_AnalyzerSingleResult1().getM_DBResult().getM_CP_Glucose());
				cell = row.createCell(j++);
				cell.setCellValue(res.getM_AnalyzerSingleResult1().getM_DBResult().getM_TreatmentTime());
				if (PrefsNightScoutLoader.getInstance().isM_AdvancedSettings())
				{
					cell = row.createCell(j++);
					cell.setCellValue(res.getM_AnalyzerSingleResult2().getM_ID());
				}
				cell = row.createCell(j++);				
				cell.setCellValue(res.getM_AnalyzerSingleResult2().getM_DBResult().getM_CP_Glucose());
				cell = row.createCell(j++);
				cell.setCellValue(res.getM_AnalyzerSingleResult2().getM_DBResult().getM_TreatmentDate());
				cell = row.createCell(j++);
				cell.setCellValue(res.getM_AnalyzerSingleResult2().getM_DBResult().getM_TreatmentTime());		
				cell = row.createCell(j++);

				switch (singleResultCarbs)
				{
				case 0   : cell.setCellValue("None"); break;
				case 1   : cell.setCellValue("Once"); break;
				case 2   : cell.setCellValue("Twice"); break;
				default  : cell.setCellValue(singleResultCarbs + " Times"); break;
				}
				if (PrefsNightScoutLoader.getInstance().isM_AdvancedSettings())
				{
					cell = row.createCell(j++);
					cell.setCellValue(interveningIDs);
					cell = row.createCell(j++);
					cell.setCellValue(skippedIDs);
				}
				cell = row.createCell(j++);
				cell.setCellValue(res.get_CategoryCode());				
				cell = row.createCell(j++);
				cell.setCellValue(res.getM_Commentary());

				result++;
			}

		}

		return result; // How many actually added
	}

	private int addRecurringTrendResult(ArrayList<AnalyzerRecurringTrendResult> recurringTrendResultList, 
			HSSFWorkbook wb, Sheet sheet, int rowNum, long daysBack)
	{
		int i = 0;
		int result = 0;

		//AnalyzerTrendCounts analyzerTrendCounts = AnalyzerTrendCounts.getInstance();

		Row row = null;
		Cell cell = null;

		// Cell Colouring / Font for most important recommendations 
		HSSFCellStyle highPriorityStyle = wb.createCellStyle();
		highPriorityStyle.setFillForegroundColor(HSSFColor.RED.index);
		highPriorityStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		HSSFFont highPriorityFont = wb.createFont();
		highPriorityFont.setColor(HSSFColor.WHITE.index);
		highPriorityStyle.setFont(highPriorityFont);

		HSSFCellStyle highPriorityPercentStyle = wb.createCellStyle();
		highPriorityPercentStyle.setDataFormat(wb.createDataFormat().getFormat("0.0%"));		
		highPriorityPercentStyle.setFont(highPriorityFont);
		highPriorityPercentStyle.setFillForegroundColor(HSSFColor.RED.index);
		highPriorityPercentStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		// Cell Colouring / Font for important recommendations 
		HSSFCellStyle mediumPriorityStyle = wb.createCellStyle();
		mediumPriorityStyle.setFillForegroundColor(HSSFColor.ORANGE.index);
		mediumPriorityStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		HSSFFont mediumPriorityFont = wb.createFont();
		mediumPriorityFont.setColor(HSSFColor.WHITE.index);
		mediumPriorityStyle.setFont(mediumPriorityFont);

		HSSFCellStyle mediumPriorityPercentStyle = wb.createCellStyle();
		mediumPriorityPercentStyle.setDataFormat(wb.createDataFormat().getFormat("0.0%"));		
		mediumPriorityPercentStyle.setFont(mediumPriorityFont);
		mediumPriorityPercentStyle.setFillForegroundColor(HSSFColor.ORANGE.index);
		mediumPriorityPercentStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);


		// All Good Colouring / Font
		HSSFCellStyle allGoodStyle = wb.createCellStyle();

		HSSFFont allGoodFont = wb.createFont();

		allGoodFont.setColor(HSSFColor.GREY_50_PERCENT.index);
		allGoodFont.setItalic(true);
		allGoodStyle.setFont(allGoodFont);

		HSSFCellStyle allGoodPercentStyle = wb.createCellStyle();
		allGoodPercentStyle.setDataFormat(wb.createDataFormat().getFormat("0.0%"));		
		allGoodPercentStyle.setFont(allGoodFont);


		// Cell Colouring / Font for days with more than 1 incident
		HSSFCellStyle repeatDaysStyle = wb.createCellStyle();
		repeatDaysStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		repeatDaysStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		// No particular format
		HSSFCellStyle regularStyle = wb.createCellStyle();

		HSSFCellStyle regularPercentStyle = wb.createCellStyle();
		regularPercentStyle.setDataFormat(wb.createDataFormat().getFormat("0.0%"));		

		// These are now held as percentages from 1.0 to 100.0
		double highFrequencyPercentage   = PrefsNightScoutLoader.getInstance().getM_AnalyzerHighFrequencyPercentage()   / 100.0;
		double mediumFrequencyPercentage = PrefsNightScoutLoader.getInstance().getM_AnalyzerMediumFrequencyPercentage() / 100.0;

		// Add results from each list
		for (i=0;i<recurringTrendResultList.size();i++) 
		{
			// David 8 Sep
			// Call countRecurringMatches for this res using its opposite

			// Row always one more since we add the title
			row = sheet.createRow(result + rowNum);
			AnalyzerRecurringTrendResult res = recurringTrendResultList.get(i);

			HSSFCellStyle style = null;
			HSSFCellStyle percentStyle = null;

			int j = 0;
			//			double percentage = ((double)res.getM_TrendResultList().size()) / ((double)this.m_TotalRecurringTrends);
			double storedPercentage = res.getM_Percentage();

			// double frequencyRatio = ((double)res.getM_TrendResultList().size()) / ((double)daysBack);
			double frequencyRatio = res.getM_Percentage();

			boolean highPriority   = frequencyRatio > highFrequencyPercentage   ? true : false;
			boolean mediumPriority = frequencyRatio > mediumFrequencyPercentage ? true : false;

			if (getL2TrendResultRecommendation(res.getM_L2TrendResultEnum()).contains("Excellent"))
			{
				style = allGoodStyle;
				percentStyle = allGoodPercentStyle;
			}
			else if (highPriority)
			{
				style = highPriorityStyle;
				percentStyle = highPriorityPercentStyle;
			}
			else if (mediumPriority)
			{
				style = mediumPriorityStyle;
				percentStyle = mediumPriorityPercentStyle;
			}
			else
			{
				style = regularStyle;
				percentStyle = regularPercentStyle;
			}

			// 	 m_Highs_Lows_ColNames = {"Date", "Day Name", "TimeSlot", "Type", "Relevance", "BG", "Time"};

			//   m_Trends_ColNames     = {"Date", "Day Name", "Start TimeSlot", "End TimeSlot", "Type", "Relevance"};
			// 	 m_Trends_ColNames     = {"Date", "Day Name", "Start TimeSlot", "End TimeSlot", "Type", "Relevance", "Start BG", "Start Time", "End BG", "End Date", "End Time", "Commentary"};
			// 	 m_RecurringTrends_ColNames = {"Time Slot", "Trend", "Number of Times", "Recommendation" };
			// 	 m_RecurringTrends_ColNames = {"ID", "Time Slot", "Trend", "Number of Incidents", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Exact Opposites", "Rises at TimeSlot", "Falls at TimeSlot", "Significance", "Recommendation", "Further Insights" };

			if (PrefsNightScoutLoader.getInstance().isM_AdvancedSettings())
			{
				cell = row.createCell(j++);
				cell.setCellValue(res.getM_ID());
				cell.setCellStyle(style);
			}

			cell = row.createCell(j++);
			cell.setCellValue(DBResult.getTimeSlotString(res.getM_TimeSlot()));
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(getL2TrendResultString(res.getM_L2TrendResultEnum()));
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(res.getM_TrendResultList().size());
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(storedPercentage);
			cell.setCellStyle(percentStyle);

			cell = row.createCell(j++);
			cell.setCellValue(res.getM_SunCnt());
			cell.setCellStyle(res.getM_SunCnt() > 1 ? repeatDaysStyle : regularStyle);

			cell = row.createCell(j++);
			cell.setCellValue(res.getM_MonCnt());
			cell.setCellStyle(res.getM_MonCnt() > 1 ? repeatDaysStyle : regularStyle);

			cell = row.createCell(j++);
			cell.setCellValue(res.getM_TueCnt());
			cell.setCellStyle(res.getM_TueCnt() > 1 ? repeatDaysStyle : regularStyle);

			cell = row.createCell(j++);
			cell.setCellValue(res.getM_WedCnt());
			cell.setCellStyle(res.getM_WedCnt() > 1 ? repeatDaysStyle : regularStyle);

			cell = row.createCell(j++);
			cell.setCellValue(res.getM_ThuCnt());
			cell.setCellStyle(res.getM_ThuCnt() > 1 ? repeatDaysStyle : regularStyle);

			cell = row.createCell(j++);
			cell.setCellValue(res.getM_FriCnt());
			cell.setCellStyle(res.getM_FriCnt() > 1 ? repeatDaysStyle : regularStyle);

			cell = row.createCell(j++);
			cell.setCellValue(res.getM_SatCnt());
			cell.setCellStyle(res.getM_SatCnt() > 1 ? repeatDaysStyle : regularStyle);

			cell = row.createCell(j++);
			cell.setCellValue(res.getM_AverageRelevance());
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(res.getM_OppositeCnt());
			cell.setCellStyle(regularStyle);

			cell = row.createCell(j++);
			cell.setCellValue(res.getM_TimeSlotRiseCnt());
			cell.setCellStyle(regularStyle);		

			cell = row.createCell(j++);
			cell.setCellValue(res.getM_TimeSlotFallCnt());
			cell.setCellStyle(regularStyle);

			cell = row.createCell(j++);			
			cell.setCellValue(getL2TrendResultRecommendation(res.getM_L2TrendResultEnum(), res.getM_OppositeCnt(), 
					res.getM_TimeSlotRiseCnt(), res.getM_TimeSlotFallCnt()));

			cell = row.createCell(j++);
			cell.setCellValue(furtherInsights(res));

			result++;
		}

		return result; // How many actually added
	}


	private int addGuidance(HSSFWorkbook wb, Sheet sheet, int rowNum)
	{
		int	result = 0;

		rowNum += addGuidanceParameterValue("Guide to Tabs", "Provides a summary of worksheets generated by NightscoutLoader", "", sheet, rowNum);


		if (PrefsNightScoutLoader.getInstance().isM_AutoTuneInvoked())
		{
			rowNum += addGuidanceParameterValue("Autotune", "Autotune ran on " + PrefsNightScoutLoader.getInstance().getM_AutoTuneNSURL() , "Use this tab to see specific recommendations from Autotune", sheet, rowNum);
		}

		if (m_AnalyzerRecurringTrendResultList.size() > 0)
		{
			String topTrend    = new String();
			String topTimeSlot = new String();
			//AnalyzerTrendCounts analyzerTrendCounts = AnalyzerTrendCounts.getInstance();
			AnalyzerRecurringTrendResult topRecurringTrend = this.m_AnalyzerRecurringTrendResultList.get(0);

			topTimeSlot = DBResult.getTimeSlotString(topRecurringTrend.getM_TimeSlot());
			topTrend = getL2TrendResultString(topRecurringTrend.getM_L2TrendResultEnum());

			// Cell Colouring / Font for most important recommendations 
			HSSFCellStyle highPriorityStyle = wb.createCellStyle();
			highPriorityStyle.setFillForegroundColor(HSSFColor.RED.index);
			highPriorityStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

			HSSFFont highPriorityFont = wb.createFont();
			highPriorityFont.setColor(HSSFColor.WHITE.index);
			highPriorityStyle.setFont(highPriorityFont);

			int recurrRows = m_AnalyzerRecurringTrendResultList.size();
			int freq = m_AnalyzerRecurringTrendResultList.get(0).getM_TrendResultList().size();
			//		int trendRows = this.m_CombinedTrendResults.size();

			// No longer accurate - count them directly instead.
			//			int skippedRows = 0;
			//			for (AnalyzerTrendResult res : m_CombinedTrendResults)
			//			{
			//				skippedRows += (res.isNoCarbsTrend() == true ? 1 : 0);
			//			}
			//			int outsideRangeRows = 0;
			//			int daySummaryRows = this.m_AnalyzerDaySummaries.size();
			//			int singleRows = this.m_CombinedSingleResults.size();
			//			int totalDataSet = this.m_DBResults.size();

			//			for (AnalyzerSingleResult res : m_CombinedSingleResults)
			//			{
			//				outsideRangeRows += (res.getM_AnalyzerSingleResultEnum() != L0AnalyzerSingleResultEnum.inRange ? 1 : 0);
			//			}

			rowNum += addGuidanceParameterValue("Recurring Trends", "There are " + recurrRows + " separate recurring trends, and " + freq + " highest frequency" + (topTrend.equals("") ? "" : " (" + topTrend + " at " + topTimeSlot + ")"),
					"Use this tab to analyze recurring trends.  Top row" + (topTimeSlot.equals("") ? "" : " at " + topTimeSlot) + " needs most attention.", sheet, rowNum);
		}
		if (this.m_AnalyzerEntries.getM_InRangeDBResultEntries().size() > 0)
		{
			rowNum += addGuidanceParameterValue("CGM Heat Map", "CGM Results within selected analysis date range are analyzed over time blocks, categorized and trends counted." , "Use this tab to see a colour coded heatmap of where comparative trend counts and types of change by time.", sheet, rowNum);
		}
		if (this.m_AnalyzerEntries.getM_DBResultEntries().size() > 0)
		{
			int cgmBlocks = this.m_AnalyzerEntries.getM_CGMRanges().size();
			int cgmWeeksLoaded = PrefsNightScoutLoader.getInstance().getM_WeeksBackToLoadEntries();
			rowNum += addGuidanceParameterValue("CGM Summary", "There are " + cgmBlocks + " separate date ranges where CGM was active going back " + cgmWeeksLoaded + " weeks." , "Use this tab to determine when CGM was used and so narrow down analysis to where CGM data is also available.", sheet, rowNum);
		}			

		if (m_AnalyzerRecurringTrendResultList.size() > 0)
		{
			//			String topTrend    = new String();
			//			String topTimeSlot = new String();
			//AnalyzerTrendCounts analyzerTrendCounts = AnalyzerTrendCounts.getInstance();
			//			AnalyzerRecurringTrendResult topRecurringTrend = this.m_AnalyzerRecurringTrendResultList.get(0);

			//			topTimeSlot = DBResult.getTimeSlotString(topRecurringTrend.getM_TimeSlot());
			//			topTrend = getL2TrendResultString(topRecurringTrend.getM_L2TrendResultEnum());

			// Cell Colouring / Font for most important recommendations 
			HSSFCellStyle highPriorityStyle = wb.createCellStyle();
			highPriorityStyle.setFillForegroundColor(HSSFColor.RED.index);
			highPriorityStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

			HSSFFont highPriorityFont = wb.createFont();
			highPriorityFont.setColor(HSSFColor.WHITE.index);
			highPriorityStyle.setFont(highPriorityFont);

			//			int recurrRows = m_AnalyzerRecurringTrendResultList.size();
			//			int freq = m_AnalyzerRecurringTrendResultList.get(0).getM_TrendResultList().size();
			int trendRows = this.m_CombinedTrendResults.size();

			// No longer accurate - count them directly instead.
			int skippedRows = 0;
			for (AnalyzerTrendResult res : m_CombinedTrendResults)
			{
				skippedRows += (res.isNoCarbsTrend() == true ? 1 : 0);
			}
			int outsideRangeRows = 0;
			int daySummaryRows = this.m_AnalyzerDaySummaries.size();
			int singleRows = this.m_CombinedSingleResults.size();
			int totalDataSet = this.m_DBResults.size();

			for (AnalyzerSingleResult res : m_CombinedSingleResults)
			{
				outsideRangeRows += (res.getM_AnalyzerSingleResultEnum() != L0AnalyzerSingleResultEnum.inRange ? 1 : 0);
			}

			rowNum += addGuidanceParameterValue("Trends", "There are " + trendRows + " separate trends identified", "Use this tab to analyze the " + trendRows + " individual trends.  Sorted in date order. Filter as necessary to see actual dates.", sheet, rowNum);
			rowNum += addGuidanceParameterValue("Skipped Meal Trends", "There are " + skippedRows + " separate instances of skipped meals", "Use this tab to analyze the " + skippedRows + " trends across meal times when nothing was eaten.  Provides a useful way to see fasting results.", sheet, rowNum);
			rowNum += addGuidanceParameterValue("BGs Outside Range", "There are " + outsideRangeRows + " separate high or low events found", "Use this tab to specifically focus on the " + outsideRangeRows + " highs & lows.", sheet, rowNum);
			rowNum += addGuidanceParameterValue("Day Summaries", "There are " + daySummaryRows + " separate day summaries", "Use this tab to review the " + daySummaryRows + " days with exceptional events.", sheet, rowNum);
			rowNum += addGuidanceParameterValue("Single Results", "There are " + singleRows + " separate treatment rows analyzed out of a total of " + totalDataSet + " total results in MongoDB.", "Use this tab to see the " + singleRows + " rows of raw treatment data included in the analysis.", sheet, rowNum);
			rowNum += addGuidanceParameterValue("Treatment Data Analyzed", "There are " + singleRows + " identified treatment rows that qualify as potential trends.", "Use this tab to see the " + singleRows + " rows of single data that may or may not have been included in the analysis and why if not.", sheet, rowNum);
		}

		if (this.m_AnalyzerEntries.getM_InRangeDBResultEntries().size() > 0)
		{
			rowNum += addGuidanceParameterValue("In Range CGM Trend Result Entries", "This is the tabular data used to generate the 'CGM Heat Map' heatmap ", "Can be used to verify the heatmap contents, else heatmap provides a more convenient view", sheet, rowNum);
			rowNum += addGuidanceParameterValue("In Range CGM Entry Intervals", "This groups the CGM data within analysis date range into time blocks and then categorizes them for trend visualization.", "Can be used to verify the heatmap contents, else heatmap provides a more convenient view", sheet, rowNum);
			rowNum += addGuidanceParameterValue("In Range CGM Results", "This is the raw CGM data within analysis date range in tabular format.", "Graphs can be generated from with Excel.  Unfortunately, the JAVA libraries used have not allowed the developer to do this automatically.", sheet, rowNum);
		}
		if (PrefsNightScoutLoader.getInstance().isM_AdvancedSettings())
		{
			int fullHistoryTrendRows = this.m_FullHistoryAnalyzer.m_CombinedTrendResults.size();

			rowNum += addGuidanceParameterValue("", "", "", sheet, rowNum);
			rowNum += addGuidanceParameterValue("Full History Trends", "There are " + fullHistoryTrendRows + " separate full history trends identified", "Use this tab to see the full list of trend categories analyzed for comparison across the entire history of results.", sheet, rowNum);
			rowNum += addGuidanceParameterValue("Comparison to Full History", "", "Use this tab to see a comparison between the requested analysis and the full list of trend categories analyzed separately across the entire history of results.", sheet, rowNum);
			rowNum += addGuidanceParameterValue("", "", "", sheet, rowNum);

		}
		rowNum += addGuidanceParameterValue("Parameters", "", "Use this tab to see all parameter values used for analysis.", sheet, rowNum);
		rowNum += addGuidanceParameterValue("Settings", "", "Use this tab to see option values set at time of analysis.", sheet, rowNum);
		rowNum += addGuidanceParameterValue("Trend Explanations", "", "Use this tab to see the full list of trend categories.", sheet, rowNum);

		return result;
	}

	// Used by Analyzer
	protected int addGuidanceParameterValue(String sheetName, String parValue, String notes, Sheet sheet, int rowNum)
	{
		int result = sheetName.equals("") || AnalyzerTabs.getInstance().isTabEnabled(sheetName) ? 
				addParameterValue(sheetName, parValue, notes, sheet, rowNum) : 0;
				return result;
	}


	private int addComparisonToFullHistory(ArrayList<AnalyzerRecurringTrendResult> recurringTrendResultList, 
			HSSFWorkbook wb, Sheet sheet, int rowNum)
	{
		int	result = 0;

		// 	private String[]                  m_Comparison_To_Full_History  = {"Data Set", "Time Slot", "Trend", "Number of Incidents"};
		// 	private String[]                  m_Comparison_To_Full_History  = {"Time Slot", "Trend", "Full History Count", "Full History Percent", "This Analysis Count", "This Analysis Percent"};

		// Iterate over the current analysis and summarise out

		int i = 0;

		//AnalyzerTrendCounts analyzerTrendCounts = AnalyzerTrendCounts.getInstance();

		Row row = null;
		Cell cell = null;

		// No particular format
		HSSFCellStyle regularStyle = wb.createCellStyle();

		// Percent format
		HSSFCellStyle percentStyle = wb.createCellStyle();
		percentStyle.setDataFormat(wb.createDataFormat().getFormat("0.0%"));		

		// Add results from each list
		for (i=0;i<this.m_FullHistoryAnalyzer.m_AnalyzerRecurringTrendResultList.size();i++) 
		{

			// David 8 Sep
			// Call countRecurringMatches for this res using its opposite

			// Row always one more since we add the title
			row = sheet.createRow(result + rowNum);
			AnalyzerRecurringTrendResult res = recurringTrendResultList.get(i);

			double fullHistoryPercentage = res.getM_Percentage();

			//double fullHistoryPercentage = ((double)res.getM_TrendResultList().size()) / ((double)this.m_FullHistoryAnalyzer.m_TotalRecurringTrends);
			double analyzerPercentage = 0.0;

			HSSFCellStyle style = null;

			int j = 0;

			style = regularStyle;

			// 	 m_Highs_Lows_ColNames = {"Date", "Day Name", "TimeSlot", "Type", "Relevance", "BG", "Time"};

			//   m_Trends_ColNames     = {"Date", "Day Name", "Start TimeSlot", "End TimeSlot", "Type", "Relevance"};
			// 	 m_Trends_ColNames     = {"Date", "Day Name", "Start TimeSlot", "End TimeSlot", "Type", "Relevance", "Start BG", "Start Time", "End BG", "End Date", "End Time", "Commentary"};
			// 	 m_RecurringTrends_ColNames = {"Time Slot", "Trend", "Number of Times", "Recommendation" };
			// 	 m_RecurringTrends_ColNames = {"ID", "Time Slot", "Trend", "Number of Incidents", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Exact Opposites", "Rises at TimeSlot", "Falls at TimeSlot", "Significance", "Recommendation", "Further Insights" };

			cell = row.createCell(j++);
			cell.setCellValue(DBResult.getTimeSlotString(res.getM_TimeSlot()));
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(getL2TrendResultString(res.getM_L2TrendResultEnum()));
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(res.getM_TrendResultList().size());
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(fullHistoryPercentage);
			cell.setCellStyle(percentStyle);

			// Iterate over the analyzer looking for a match
			int thisCount = 0;
			for (AnalyzerRecurringTrendResult c : this.m_AnalyzerRecurringTrendResultList)
			{
				if (c.getM_L2TrendResultEnum() == res.getM_L2TrendResultEnum() &&
						c.getM_TimeSlot() == res.getM_TimeSlot())
				{
					analyzerPercentage = c.getM_Percentage();
					thisCount = c.getM_TrendResultList().size();
				}
				if (thisCount > 0)
					break;
			}

			if (thisCount > 0)
			{
				cell = row.createCell(j++);
				cell.setCellValue(thisCount);
				cell.setCellStyle(style);

				cell = row.createCell(j++);
				cell.setCellValue(analyzerPercentage);
				cell.setCellStyle(percentStyle);

			}

			result++;
		}

		return result; // How many actually added
	}

	private int addCGMSummary(AnalyzerEntries analyzer,	HSSFWorkbook wb, Sheet sheet, int rowNum)
	{
		int result = 0;

		//AnalyzerTrendCounts analyzerTrendCounts = AnalyzerTrendCounts.getInstance();

		Row row = null;
		Cell cell = null;

		// No particular format
		HSSFCellStyle regularStyle = wb.createCellStyle();

		// Add results from each list
		for (AnalyzerEntriesCGMRange c : this.m_AnalyzerEntries.getM_CGMRanges()) 
		{
			// Row always one more since we add the title
			row = sheet.createRow(result + rowNum);

			HSSFCellStyle style = null;

			int j = 0;

			style = regularStyle;

			// 	private String[]                  m_CGM_Date_Ranges  = {"Start Date", "End Date", "Number of Days", "Average Readings per Day"};
			// 	private String[]                  m_CGM_Date_Ranges  = 
			// {"Start Date", "End Date", "Overlap with Treatments", "Number of Days", 
			// "Number of Hypos", "Number of Hypers", "Number in Range", "Average Readings per Day", "Number of CGM Days"};


			cell = row.createCell(j++);
			cell.setCellValue(c.getM_StartDateStr());
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(c.getM_EndDateStr());
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(c.getM_DateOverlapStr());
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(c.getM_Duration());
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(c.getM_NumHypos());
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(c.getM_NumHypers());
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(c.getM_NumInRange());
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(c.getM_AverageDBResultEntries());
			cell.setCellStyle(style);

			result++;
		}

		return result; // How many actually added

	}

	private int addCGMResults(HSSFWorkbook wb, Sheet sheet, int rowNum)
	{
		int result = 0;

		//AnalyzerTrendCounts analyzerTrendCounts = AnalyzerTrendCounts.getInstance();

		Row row = null;
		Cell cell = null;

		// No particular format
		HSSFCellStyle regularStyle = wb.createCellStyle();

		HSSFCellStyle mmolStyle = wb.createCellStyle();
		mmolStyle.setDataFormat(wb.createDataFormat().getFormat("#.#"));

		//		ArrayList<DBResultEntry> resultEntryList = this.m_DBResultEntries;
		ArrayList<DBResultEntry> resultEntryList = this.m_AnalyzerEntries.getM_InRangeDBResultEntries();

		final String dtf = new String("dd/MM/yyyy");
		final String hrf = new String("HH");

		// Is preference for BG in mg/dL or mmol/L?

		boolean mmol = PrefsNightScoutLoader.getInstance().getM_BGUnits() == 0 ? true : false;

		// Add results from each list
		for (DBResultEntry c : resultEntryList) 
		{
			/*			if (c.getM_SGV() != null &&
					(CommonUtils.isTimeBetween(m_StartDate, m_EndDate, c.getM_UTCDate())))
			{
			 */	
			// Row always one more since we add the title
			row = sheet.createRow(result + rowNum);

			HSSFCellStyle style = null;
			String dateStr = null;
			String trendRange = null;
			Date   trendStartDate = c.getM_AnalyzerResultEntryInterval().getM_PeriodStart();
			Date   trendEndDate   = c.getM_AnalyzerResultEntryInterval().getM_PeriodEnd();

			int j = 0;

			style = regularStyle;

			//			private String[]                  m_CGM_Results  = {"Date", "Time", "CGM Value"};
			// 	private String[]                  m_CGM_Results  = {"Date", "Trend Range", "Time", "CGM Value"};
			// 	private String[]                  m_CGM_Results  = {"Date", "Trend Range", "Trend Profile Type", "Time", "CGM Value"};
			// 	private String[]                  m_CGM_Results  = {"ID", "Date", "Trend Range", "Trend Profile Type", "Trend ID", "Time", "CGM Value"};



			try 
			{
				dateStr = CommonUtils.convertDateString(c.getM_UTCDate(), dtf);

				trendRange = new String(CommonUtils.convertDateString(trendStartDate, hrf) +
						"H to " + CommonUtils.convertDateString(trendEndDate, hrf) + "H");

			} 
			catch (ParseException e) 
			{
				m_Logger.log(Level.SEVERE, "addCGMResults : " + e.getMessage());
			}

			if (PrefsNightScoutLoader.getInstance().isM_AdvancedSettings())
			{
				cell = row.createCell(j++);
				cell.setCellValue(c.getM_ID());
				cell.setCellStyle(style);
			}

			cell = row.createCell(j++);
			cell.setCellValue(dateStr);
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(trendRange);
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(c.getM_AnalyzerResultEntryInterval().getM_ProfileDirectionStr(mmol));
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(c.getM_AnalyzerResultEntryInterval().getM_ID());
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(c.getM_UTCDate().toString());
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(mmol == true ? c.getM_BG() : c.getM_SGV());
			cell.setCellStyle(mmol == true ? mmolStyle : style);

			result++;
		}
		//}

		return result; // How many actually added

	}

	private int addCGMEntryIntervals(HSSFWorkbook wb, Sheet sheet, int rowNum)
	{
		int result = 0;

		//AnalyzerTrendCounts analyzerTrendCounts = AnalyzerTrendCounts.getInstance();

		Row row = null;
		Cell cell = null;

		// No particular format
		HSSFCellStyle regularStyle = wb.createCellStyle();

		HSSFCellStyle mmolStyle = wb.createCellStyle();
		mmolStyle.setDataFormat(wb.createDataFormat().getFormat("#.#"));

		ArrayList<AnalyzerResultEntryInterval> resultEntryIntervalList = m_AnalyzerEntries.getM_ResultEntryIntervals();

		final String dtf = new String("dd/MM/yyyy");
		final String hrf = new String("HH");

		// Is preference for BG in mg/dL or mmol/L?

		boolean mmol = PrefsNightScoutLoader.getInstance().getM_BGUnits() == 0 ? true : false;

		// Add results from each list
		for (AnalyzerResultEntryInterval c : resultEntryIntervalList) 
		{
			// Row always one more since we add the title
			row = sheet.createRow(result + rowNum);

			HSSFCellStyle style = null;
			String dateStr = null;
			String trendRange = null;
			Date   trendStartDate = c.getM_PeriodStart();
			Date   trendEndDate   = c.getM_PeriodEnd();

			int j = 0;

			style = regularStyle;

			// 	private String[]                  m_CGM_EntryIntervals  = {"ID", "Date", "Trend Range", "Trend Profile Type", "Num CGM Entries"};
			//  private String[]                  m_CGM_EntryIntervals  = {"ID", "Date", "Trend Range", "Trend Profile Type", "Trend Result Entry ID", "Num CGM Entries"};
			//	private String[]                  m_CGM_EntryIntervals  = {"ID", "Date", "Trend Range", "Trend Profile Type", "Goes Hypo", "Goes Hyper", "Start Profile", "End Profile", "Trend Result Entry ID", "Num CGM Entries"};



			try 
			{
				dateStr = CommonUtils.convertDateString(c.getM_PeriodStart(), dtf);

				trendRange = new String(CommonUtils.convertDateString(trendStartDate, hrf) +
						"H to " + CommonUtils.convertDateString(trendEndDate, hrf) + "H");

			} 
			catch (ParseException e) 
			{
				m_Logger.log(Level.SEVERE, "addCGMEntryIntervals : " + e.getMessage());
			}

			if (PrefsNightScoutLoader.getInstance().isM_AdvancedSettings())
			{
				cell = row.createCell(j++);
				cell.setCellValue(c.getM_ID());
				cell.setCellStyle(style);
			}

			cell = row.createCell(j++);
			cell.setCellValue(dateStr);
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(trendRange);
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(c.getM_ProfileDirectionStr(mmol));
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(c.getM_GoesHypo() == true ? "Yes" : "No");
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(c.getM_GoesHyper() == true ? "Yes" : "No");
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(AnalyzerResultEntryInterval.getEntryProfileStr(c.getM_StartProfile()));
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(AnalyzerResultEntryInterval.getEntryProfileStr(c.getM_EndProfile()));
			cell.setCellStyle(style);


			if (PrefsNightScoutLoader.getInstance().isM_AdvancedSettings())
			{
				cell = row.createCell(j++);
				cell.setCellValue( (c.getM_AnalyzerTrendResultEntry() == null ?
						0 : c.getM_AnalyzerTrendResultEntry().getM_ID()) );
				cell.setCellStyle(style);
			}

			cell = row.createCell(j++);
			cell.setCellValue(c.getM_DBResultEntries().size());
			cell.setCellStyle(style);

			result++;

		}

		return result; // How many actually added
	}

	private int addCGMTrendResultEntries(HSSFWorkbook wb, Sheet sheet, int rowNum)
	{
		int result = 0;

		//AnalyzerTrendCounts analyzerTrendCounts = AnalyzerTrendCounts.getInstance();

		Row row = null;
		Cell cell = null;

		// No particular format
		HSSFCellStyle regularStyle = wb.createCellStyle();

		HSSFCellStyle mmolStyle = wb.createCellStyle();
		mmolStyle.setDataFormat(wb.createDataFormat().getFormat("#.#"));

		ArrayList<AnalyzerTrendResultEntry> trendResultList = m_AnalyzerEntries.getM_TrendResultEntries();

		// Is preference for BG in mg/dL or mmol/L?

		boolean mmol = PrefsNightScoutLoader.getInstance().getM_BGUnits() == 0 ? true : false;

		// Add results from each list
		for (AnalyzerTrendResultEntry c : trendResultList) 
		{
			// Row always one more since we add the title
			row = sheet.createRow(result + rowNum);

			HSSFCellStyle style = null;
			int   trendStartHour = c.getM_StartHour();
			int   trendEndHour   = c.getM_EndHour();

			int j = 0;

			style = regularStyle;

			// private String[]                  m_CGM_TrendResultEntry  = {"ID", "Start Hour", "End Hour", "Trend Profile Type", "Num CGM Intervals"};
			// private String[]                  m_CGM_TrendResultEntry  = {"ID", "Start Hour", "End Hour", "Trend Profile Type", "Goes Hypo", "Goes Hyper", "Start Profile", "End Profile", "Num CGM Intervals"};

			if (PrefsNightScoutLoader.getInstance().isM_AdvancedSettings())
			{
				cell = row.createCell(j++);
				cell.setCellValue(c.getM_ID());
				cell.setCellStyle(style);
			}

			cell = row.createCell(j++);
			cell.setCellValue(trendStartHour);
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(trendEndHour);
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(c.getM_ProfileDirectionStr(mmol));
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(c.getM_GoesHypo() == true ? "Yes" : "No");
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(c.getM_GoesHyper() == true ? "Yes" : "No");
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(AnalyzerResultEntryInterval.getEntryProfileStr(c.getM_StartProfile()));
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(AnalyzerResultEntryInterval.getEntryProfileStr(c.getM_EndProfile()));
			cell.setCellStyle(style);

			cell = row.createCell(j++);
			cell.setCellValue(c.getM_ResultEntryIntervals().size());
			cell.setCellStyle(style);

			result++;

		}

		return result; // How many actually added
	}


	private int addCGMTrends(String offset, AnalyzerTrendResultAggregateGroup group, 
			HSSFWorkbook wb, Sheet sheet, int rowNum)
	{
		int result = 0;

		boolean mmol = PrefsNightScoutLoader.getInstance().getM_BGUnits() == 0 ? true : false;

		short colhypo_1   = HSSFColor.DARK_RED.index;
		short colhypo_2   = HSSFColor.RED.index;
		short col4to7_1   = HSSFColor.GREEN.index;
		short col4to7_2   = HSSFColor.LIGHT_GREEN.index;
		short col7to10_1  = HSSFColor.YELLOW.index;
		short col7to10_2  = HSSFColor.LIGHT_YELLOW.index;
		short col10to14_1 = HSSFColor.ORANGE.index;
		short col10to14_2 = HSSFColor.LIGHT_ORANGE.index;
		short colhyper_1  = HSSFColor.DARK_RED.index;
		short colhyper_2  = HSSFColor.RED.index;

		// -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** 
		// Hypo Line
		// -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** 
		result += addCGMTrendsEntries(offset, 
				AnalyzerResultEntryInterval.getProfileDirectionStr(mmol, 
						AnalyzerResultEntryInterval.DBResultEntryProfileDirection.Hypo),    
				group.getM_Hypo(), group.getM_HypoMaxCount(), 
				colhypo_1, colhypo_2, wb, sheet, rowNum + result); 
		result += addBlankCGMTrendsEntries(sheet, rowNum + result);	


		// -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** 
		// In Range Lines
		// -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** 
		result += addCGMTrendsEntries(offset, 
				AnalyzerResultEntryInterval.getProfileDirectionStr(mmol, 
						AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From4to7ThenDown),
				group.getM_From4to7ThenDown(), group.getM_From4to7ThenDownMaxCount(), 
				col4to7_1, col4to7_2, wb, sheet, rowNum + result); 

		result += addCGMTrendsEntries(offset, 
				AnalyzerResultEntryInterval.getProfileDirectionStr(mmol, 
						AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From4to7UpThenDown),
				group.getM_From4to7UpThenDown(), group.getM_From4to7UpThenDownMaxCount(), 
				col4to7_1, col4to7_2, wb, sheet, rowNum + result); 

		result += addCGMTrendsEntries(offset, 
				AnalyzerResultEntryInterval.getProfileDirectionStr(mmol, 
						AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From4to7ThenLevel),
				group.getM_From4to7ThenLevel(), group.getM_From4to7ThenLevelMaxCount(), 
				col4to7_1, col4to7_2, wb, sheet, rowNum + result); 

		result += addCGMTrendsEntries(offset, 
				AnalyzerResultEntryInterval.getProfileDirectionStr(mmol, 
						AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From4to7ThenUp),
				group.getM_From4to7ThenUp(), group.getM_From4to7ThenUpMaxCount(), 
				col4to7_1, col4to7_2, wb, sheet, rowNum + result); 

		result += addCGMTrendsEntries(offset, 
				AnalyzerResultEntryInterval.getProfileDirectionStr(mmol, 
						AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From4to7DownThenUp),
				group.getM_From4to7DownThenUp(), group.getM_From4to7DownThenUpMaxCount(), 
				col4to7_1, col4to7_2, wb, sheet, rowNum + result); 

		result += addBlankCGMTrendsEntries(sheet, rowNum + result);	

		// -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** 
		// Just Outside Range Lines
		// -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** 
		result += addCGMTrendsEntries(offset, 
				AnalyzerResultEntryInterval.getProfileDirectionStr(mmol, 
						AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From7to10ThenDown),
				group.getM_From7to10ThenDown(), group.getM_From7to10ThenDownMaxCount(), 
				col7to10_1, col7to10_2, wb, sheet, rowNum + result); 

		result += addCGMTrendsEntries(offset, 
				AnalyzerResultEntryInterval.getProfileDirectionStr(mmol, 
						AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From7to10UpThenDown),
				group.getM_From7to10UpThenDown(), group.getM_From7to10UpThenDownMaxCount(), 
				col7to10_1, col7to10_2, wb, sheet, rowNum + result); 

		result += addCGMTrendsEntries(offset, 
				AnalyzerResultEntryInterval.getProfileDirectionStr(mmol, 
						AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From7to10ThenLevel),
				group.getM_From7to10ThenLevel(), group.getM_From7to10ThenLevelMaxCount(), 
				col7to10_1, col7to10_2, wb, sheet, rowNum + result); 

		result += addCGMTrendsEntries(offset, 
				AnalyzerResultEntryInterval.getProfileDirectionStr(mmol, 
						AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From7to10ThenUp),
				group.getM_From7to10ThenUp(), group.getM_From7to10ThenUpMaxCount(), 
				col7to10_1, col7to10_2, wb, sheet, rowNum + result); 

		result += addCGMTrendsEntries(offset, 
				AnalyzerResultEntryInterval.getProfileDirectionStr(mmol, 
						AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From7to10DownThenUp),
				group.getM_From7to10DownThenUp(), group.getM_From7to10DownThenUpMaxCount(), 
				col7to10_1, col7to10_2, wb, sheet, rowNum + result); 
		result += addBlankCGMTrendsEntries(sheet, rowNum + result);	

		// -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** 
		// Further Outside Range Lines
		// -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** 
		result += addCGMTrendsEntries(offset, 
				AnalyzerResultEntryInterval.getProfileDirectionStr(mmol, 
						AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From10to14ThenDown),
				group.getM_From10to14ThenDown(), group.getM_From10to14ThenDownMaxCount(), 
				col10to14_1, col10to14_2, wb, sheet, rowNum + result); 

		result += addCGMTrendsEntries(offset, 
				AnalyzerResultEntryInterval.getProfileDirectionStr(mmol, 
						AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From10to14UpThenDown),
				group.getM_From10to14UpThenDown(), group.getM_From10to14UpThenDownMaxCount(), 
				col10to14_1, col10to14_2, wb, sheet, rowNum + result); 

		result += addCGMTrendsEntries(offset, 
				AnalyzerResultEntryInterval.getProfileDirectionStr(mmol, 
						AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From10to14ThenLevel),
				group.getM_From10to14ThenLevel(), group.getM_From10to14ThenLevelMaxCount(), 
				col10to14_1, col10to14_2, wb, sheet, rowNum + result); 

		result += addCGMTrendsEntries(offset, 
				AnalyzerResultEntryInterval.getProfileDirectionStr(mmol, 
						AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From10to14ThenUp),
				group.getM_From10to14ThenUp(), group.getM_From10to14ThenUpMaxCount(), 
				col10to14_1, col10to14_2, wb, sheet, rowNum + result); 

		result += addCGMTrendsEntries(offset, 
				AnalyzerResultEntryInterval.getProfileDirectionStr(mmol, 
						AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From10to14DownThenUp),
				group.getM_From10to14DownThenUp(), group.getM_From10to14DownThenUpMaxCount(), 
				col10to14_1, col10to14_2, wb, sheet, rowNum + result); 
		result += addBlankCGMTrendsEntries(sheet, rowNum + result);	

		// -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** 
		// Hyper Line
		// -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** -- ** 
		result += addCGMTrendsEntries(offset, 
				AnalyzerResultEntryInterval.getProfileDirectionStr(mmol, 
						AnalyzerResultEntryInterval.DBResultEntryProfileDirection.Hyper),    
				group.getM_Hyper(), group.getM_HyperMaxCount(), 
				colhyper_1, colhyper_2, wb, sheet, rowNum + result); 

		return result;
	}

	private int addBlankCGMTrendsEntries(Sheet sheet, int rowNum)
	{
		int result = 0;		
		sheet.createRow(result + rowNum);
		// We've added one blank row
		result++;	

		return result;
	}

	private int addCGMTrendsEntries(String offset, String profile, 
			ArrayList<AnalyzerTrendResultEntry> entries, 
			int maxCount,
			short colourIndex_1,
			short colourIndex_2,
			HSSFWorkbook wb, Sheet sheet, int rowNum)
	{
		int result = 0;
		int interval = PrefsNightScoutLoader.getInstance().getM_EntryAnalyzerIntervalHours();

		Row row = null;
		Cell cell = null;

		row = sheet.createRow(result + rowNum);

		int j = 0;

		HSSFCellStyle style = null;
		HSSFCellStyle firstThirdStyle = wb.createCellStyle();
		HSSFCellStyle secondThirdStyle = wb.createCellStyle();
		HSSFCellStyle lastThirdStyle = wb.createCellStyle();

		HSSFFont styleFont = wb.createFont();
		HSSFFont lastStyleFont = wb.createFont();

		styleFont.setColor(HSSFColor.BLACK.index);
		styleFont.setBold(false);
		lastStyleFont.setColor(HSSFColor.WHITE.index);
		lastStyleFont.setBold(true);

		firstThirdStyle.setFont(styleFont);
		secondThirdStyle.setFont(styleFont);
		lastThirdStyle.setFont(lastStyleFont);


		//		firstThirdStyle.setFillForegroundColor(HSSFColor.RED.index);
		// Mild trend
		firstThirdStyle.setFillForegroundColor(colourIndex_2);
		//		firstThirdStyle.setFillPattern(HSSFCellStyle.SPARSE_DOTS);
		firstThirdStyle.setFillPattern(HSSFCellStyle.FINE_DOTS);
		//		firstThirdStyle.setFillPattern(HSSFCellStyle.DIAMONDS);

		// Moderate trend
		secondThirdStyle.setFillForegroundColor(colourIndex_2);
		//		secondThirdStyle.setFillPattern(HSSFCellStyle.FINE_DOTS);
		secondThirdStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		// Significant trend
		lastThirdStyle.setFillForegroundColor(colourIndex_1);
		lastThirdStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

		// Add Profile Entry
		cell = row.createCell(j++);
		cell.setCellValue(profile);
		cell.setCellStyle(style);

		// Headers are dynamically added based on the interval configured
		// Have 3 different shades based on whether the result is in
		//  First third up to maxCount
		//  Second third up to maxCount
		//  Last third up to maxCount

		for (int i = 0; i < 24; i += interval)
		{
			int count = 0;			

			// Carefully scan the list looking for a matching entry with same start time.
			for (AnalyzerTrendResultEntry e : entries)
			{
				if (e.getM_StartHour() == i)
				{
					count = e.getM_ResultEntryIntervals().size();
					break;
				}				
			}

			double frac = (double)count / (double)maxCount;

			boolean firstThird  = (count == 0)                                ? false : (frac <= 0.3333333 ? true : false);
			boolean secondThird = (count == 0 || firstThird)                  ? false : (frac <= 0.6666666 ? true : false);
			boolean lastThird   = (count == 0 || firstThird  ||  secondThird) ? false : (frac >  0.6666666 ? true : false);


			// Add Profile Count Entry
			cell = row.createCell(j++);
			cell.setCellValue(count);

			if (count == 0)
			{
				cell.setCellStyle(style);
			}
			else if (firstThird == true)
			{
				cell.setCellStyle(style);
				//				cell.setCellStyle(firstThirdStyle);
			}
			else if (secondThird == true)
			{
				cell.setCellStyle(style);
				cell.setCellStyle(secondThirdStyle);
			}
			else if (lastThird == true)
			{
				cell.setCellStyle(lastThirdStyle);
			}
		}

		// We've added one row
		result++;	

		return result;
	}



	//	private int addGuidanceUpdDownFlatSummary(HSSFWorkbook wb, Sheet sheet, int rowNum, 
	//			int flats, int rises, int falls, String eventType, HSSFCellStyle style)
	//	{
	//		int	result = 0;
	//		long daysBack      = PrefsNightScoutLoader.getInstance().getM_AnalyzerDaysBack();
	//
	//		String commentary1 = new String("");
	//		String commentaryDirection = new String("");
	//		String recommendation = new String("");
	//
	//		if ((rises > falls) && (rises > flats))
	//		{
	//			commentaryDirection = "of rise trends";
	//			if ((rises / daysBack) > 0.5)  // > 50% is significant
	//				//			if (Math.abs(rises - falls) > rises / 2)
	//			{
	//				commentary1 = "a very high recurrence (" + rises + " across " + daysBack + " days)";
	//				if (falls > 0)
	//				{
	//					recommendation = "Despite the very strong pattern, there are still both falls (" + falls + ") as well as rises (" + rises + ") seen for " + eventType +
	//							" use caution when considering any changes to basal or bolus as results might be inconclusive.";
	//				}
	//				else
	//				{
	//					recommendation = "Given there are no falls, this could be a strong pattern of rises (based on recurrence) and worth investigating changes for " + eventType;				
	//				}
	//
	//			}
	//			else if ((rises / daysBack) > 0.333)  // > 33% is significant
	//				//			else if (Math.abs(rises - falls) > falls / 2)
	//			{
	//				commentary1 = "a high recurrence (" + rises + " across " + daysBack + " days)";
	//				if (falls > 0)
	//				{
	//					recommendation = "Despite the strong pattern, there are still both falls (" + falls + ") as well as rises (" + rises + ") seen for " + eventType +
	//							" use caution when considering any changes to basal or bolus as results might be inconclusive.";
	//				}
	//				else
	//				{
	//					recommendation = "Given there are no falls, this could be a strong pattern of rises (based on recurrence) and worth investigating changes for " + eventType;				
	//				}
	//
	//			}
	//
	//			else
	//			{
	//				commentary1 = "a notable recurrence (" + rises + " across " + daysBack + " days)";
	//				recommendation = "Given there are still both falls (" + falls + ") as well as rises (" + rises + ") seen for " + eventType +
	//						" use caution when considering any changes to basal or bolus as results might be inconclusive.";
	//			}
	//
	//		}
	//		else if ((falls > rises) && (falls > flats))
	//		{
	//			commentaryDirection = "of fall trends" ;
	//			if ((falls / daysBack) > 0.5)  // > 50% is significant
	//			{
	//				commentary1 = "a very high recurrence (" + falls + " across " + daysBack + " days)";
	//				if (rises > 0)
	//				{
	//					recommendation = "Despite the very strong pattern, there are still both rises (" + rises + ") as well as falls (" + falls + ") seen for " + eventType +
	//							" use caution when considering any changes to basal or bolus as results might be inconclusive.";
	//				}
	//				else
	//				{
	//					recommendation = "Given there are no rises, this could be a strong pattern of rises (based on recurrence) and worth investigating changes for " + eventType;				
	//				}
	//
	//			}
	//			//			else if (Math.abs(falls - rises) > rises / 2)
	//			else if ((falls / daysBack) > 0.33)  // > 33% is significant			
	//			{
	//				commentary1 = "a high recurrence (" + falls + " across " + daysBack + " days)";
	//				if (rises > 0)
	//				{
	//					recommendation = "Despite the strong pattern, there are still both rises (" + rises + ") as well as falls (" + falls + ") seen for " + eventType +
	//							" use caution when considering any changes to basal or bolus as results might be inconclusive.";
	//				}
	//				else
	//				{
	//					recommendation = "Given there are no rises, this could be a strong pattern of rises (based on recurrence) and worth investigating changes for " + eventType;				
	//				}
	//
	//			}
	//
	//			else
	//			{
	//				commentary1 = "a notable recurrence (" + falls + " across " + daysBack + " days)";
	//				recommendation = "Given there are still both rises (" + rises + ") as well as falls (" + falls + ") seen for " + eventType +
	//						" use caution when considering any changes to basal or bolus as results might be inconclusive.";
	//			}
	//		}
	//		else if ((flats > rises) && (flats > falls))
	//		{
	//			commentaryDirection = "of flat trends";
	//			recommendation = "Flat results are good (as long as the start is is range!).";
	//		}
	//		else
	//		{
	//			commentaryDirection = "a very even distribution.  Same number of rise, fall and flat trends";
	//			recommendation = "Given the equal distribution, it's difficult to determine what if any change is needed here.";
	//		}
	//
	//
	//		rowNum += addParameterValue("High Level Analysis - " + eventType, "For all trend results starting at " + eventType + " there is " + commentary1 +
	//				" " + commentaryDirection, recommendation, sheet, rowNum, style);
	//
	//		result++;
	//
	//		return result;
	//	}

	private int addDaySummaries(ArrayList<AnalyzerDaySummary> daySummaryList, 
			HSSFWorkbook wb, Sheet sheet, int rowNum)
	{
		int i = 0;
		int result = 0;

		Row row = null;
		Cell cell = null;

		// Add results from each list
		for (i=0;i<daySummaryList.size();i++) 
		{
			// Row always one more since we add the title
			row = sheet.createRow(result + rowNum);
			AnalyzerDaySummary res = daySummaryList.get(i);

			// If the result is relevant only proceed
			if (res.isM_Relevant())
			{
				int j = 0;

				//	m_DaySummary_ColNames = {"Date", "Day Name", "Missing Meal BG", "Missing Meal Carbs", "Num Hypos", "Silly O'Clock BGs", "Silly O'Clock Corrections", "Silly O'Clock Hypos"};
				//	m_DaySummary_ColNames = {"Date", "Day Name", "Possible Duplicates", "Missing Meal BG", "Missing Meal Carbs", "Num Hypos", "Silly O'Clock BGs", "Silly O'Clock Corrections", "Silly O'Clock Hypos"};

				String missingMealBG = new String();
				String missingMealCarbs = new String();

				missingMealBG = (!res.isM_BreakfastBG() ? "Breakfast " : "") +
						(!res.isM_LunchBG()     ? "Lunch "     : "")     +
						(!res.isM_DinnerBG()    ? "Dinner "    : "");
				missingMealBG = (missingMealBG.equals("") ? "n/a" : missingMealBG);

				missingMealCarbs = (!res.isM_BreakfastCarbs() ? "Breakfast " : "") +
						(!res.isM_LunchCarbs()     ? "Lunch "     : "")     +
						(!res.isM_DinnerCarbs()    ? "Dinner "    : "");
				missingMealCarbs = (missingMealCarbs.equals("") ? "n/a" : missingMealCarbs);

				if (PrefsNightScoutLoader.getInstance().isM_AdvancedSettings())
				{
					cell = row.createCell(j++);
					cell.setCellValue(res.getM_ID());
				}
				cell = row.createCell(j++);
				cell.setCellValue(res.getM_DateString());
				cell = row.createCell(j++);
				cell.setCellValue(res.getM_DayName());
				cell = row.createCell(j++);
				cell.setCellValue(res.getM_PossibleDuplicates());
				cell = row.createCell(j++);
				cell.setCellValue(missingMealBG);
				cell = row.createCell(j++);
				cell.setCellValue(missingMealCarbs);
				cell = row.createCell(j++);
				cell.setCellValue(res.getM_NumHypos());
				cell = row.createCell(j++);
				cell.setCellValue(res.getM_NumNightTests());
				cell = row.createCell(j++);
				cell.setCellValue(res.getM_NumNightCorrections());
				cell = row.createCell(j++);
				cell.setCellValue(res.getM_NumNightHypos());

				result++;
			}

		}

		return result; // How many actually added
	}

	private void categorizeResultNew(AnalyzerSingleResult singleResult)
	{
		// 1 Get timeslot for result
		DBResult.TimeSlot timeSlot = singleResult.getM_TimeSlot();

		double  analyzerOvernightChangeTrendRatio = PrefsNightScoutLoader.getInstance().getM_AnalyzerOvernightChangeTrendRatio();
		double  analyzerBreakfastChangeTrendRatio = PrefsNightScoutLoader.getInstance().getM_AnalyzerBreakfastChangeTrendRatio();
		double  analyzerLunchChangeTrendRatio     = PrefsNightScoutLoader.getInstance().getM_AnalyzerLunchChangeTrendRatio();
		double  analyzerDinnerChangeTrendRatio    = PrefsNightScoutLoader.getInstance().getM_AnalyzerDinnerChangeTrendRatio();

		boolean analyzeBreakfast                  = PrefsNightScoutLoader.getInstance().isM_AnalyzerIncludeBreakfast();
		boolean analyzeLunch                      = PrefsNightScoutLoader.getInstance().isM_AnalyzerIncludeLunch();
		boolean analyzeDinner                     = PrefsNightScoutLoader.getInstance().isM_AnalyzerIncludeDinner();
		boolean analyzeOvernight                  = PrefsNightScoutLoader.getInstance().isM_AnalyzerIncludeOvernight();

		boolean proceed                           = true;

		ArrayList<AnalyzerSingleResult> applicableIndividualList = null;

		ArrayList<AnalyzerTrendResult>  trendResultList          = null;
		ArrayList<AnalyzerTrendResult>  skippedMealResultList    = null;

		double                  trendRatio               = 0.0;

		switch (timeSlot)
		{
		case BreakfastTime: 
			applicableIndividualList = m_CombinedSingleResults; // m_BreakfastResults;
			skippedMealResultList    = m_SkippedBreakfastResults;
			trendResultList          = m_CombinedTrendResults; // m_PreToPostBreakfastResults;
			trendRatio               = analyzerBreakfastChangeTrendRatio;
			proceed                  = analyzeBreakfast;
			break;
		case LunchTime:
			applicableIndividualList = m_CombinedSingleResults; // m_LunchResults;
			skippedMealResultList    = m_SkippedLunchResults;
			trendResultList          = m_CombinedTrendResults; // m_PreToPostLunchResults;
			trendRatio               = analyzerLunchChangeTrendRatio;
			proceed                  = analyzeLunch;
			break;
		case DinnerTime:
			applicableIndividualList = m_CombinedSingleResults; // m_DinnerResults;
			skippedMealResultList    = m_SkippedDinnerResults;
			trendResultList          = m_CombinedTrendResults; // m_PreToPostDinnerResults;
			trendRatio               = analyzerDinnerChangeTrendRatio;
			proceed                  = analyzeDinner;
			break;
		case BedTime:
			applicableIndividualList = m_CombinedSingleResults; // m_BedTimeResults;
			trendResultList          = m_CombinedTrendResults; // m_OvernightResults;
			trendRatio               = analyzerOvernightChangeTrendRatio;
			proceed                  = analyzeOvernight;
			break;

		default:

			break;
		}

		if (proceed)
		{
			categorizeResultNew(singleResult, timeSlot, applicableIndividualList, trendRatio, trendResultList, skippedMealResultList);
		}
		else
		{
			m_Logger.log(Level.FINE, "<"+this.getClass().getName()+">" + ". Analyzer not included for all meal times (see Settings).  Trend Results not being captured");
		}

	}


	//	private void categorizeResult(DBResult result)
	//	{
	//		// 1 Get timeslot for result
	//		DBResult.TimeSlot timeSlot = result.getDBResultTimeSlot();  // FAILED !!!
	//
	//		double  analyzerOvernightChangeTrendRatio = PrefsNightScoutLoader.getInstance().getM_AnalyzerOvernightChangeTrendRatio();
	//		double  analyzerBreakfastChangeTrendRatio = PrefsNightScoutLoader.getInstance().getM_AnalyzerBreakfastChangeTrendRatio();
	//		double  analyzerLunchChangeTrendRatio     = PrefsNightScoutLoader.getInstance().getM_AnalyzerLunchChangeTrendRatio();
	//		double  analyzerDinnerChangeTrendRatio    = PrefsNightScoutLoader.getInstance().getM_AnalyzerDinnerChangeTrendRatio();
	//
	//		ArrayList<AnalyzerSingleResult> applicableIndividualList = null;
	//
	//		ArrayList<AnalyzerTrendResult>  trendResultList          = null;
	//		ArrayList<AnalyzerTrendResult>  skippedMealResultList    = null;
	//
	//		double                  trendRatio               = 0.0;
	//
	//		switch (timeSlot)
	//		{
	//		case BreakfastTime: 
	//			applicableIndividualList = m_CombinedSingleResults; // m_BreakfastResults;
	//			skippedMealResultList    = m_SkippedBreakfastResults;
	//			trendResultList          = m_CombinedTrendResults; // m_PreToPostBreakfastResults;
	//			trendRatio               = analyzerBreakfastChangeTrendRatio;
	//			break;
	//		case LunchTime:
	//			applicableIndividualList = m_CombinedSingleResults; // m_LunchResults;
	//			skippedMealResultList    = m_SkippedLunchResults;
	//			trendResultList          = m_CombinedTrendResults; // m_PreToPostLunchResults;
	//			trendRatio               = analyzerLunchChangeTrendRatio;
	//			break;
	//		case DinnerTime:
	//			applicableIndividualList = m_CombinedSingleResults; // m_DinnerResults;
	//			skippedMealResultList    = m_SkippedDinnerResults;
	//			trendResultList          = m_CombinedTrendResults; // m_PreToPostDinnerResults;
	//			trendRatio               = analyzerDinnerChangeTrendRatio;
	//			break;
	//		case BedTime:
	//			applicableIndividualList = m_CombinedSingleResults; // m_BedTimeResults;
	//			trendResultList          = m_CombinedTrendResults; // m_OvernightResults;
	//			trendRatio               = analyzerOvernightChangeTrendRatio;
	//			break;
	//
	//		default:
	//
	//			break;
	//		}
	//
	//		categorizeResult(result, timeSlot, applicableIndividualList, trendRatio, trendResultList, skippedMealResultList);
	//	}

	private void categorizeResultNew(AnalyzerSingleResult singleResult, 
			DBResult.TimeSlot timeSlot,

			ArrayList<AnalyzerSingleResult> applicableIndividualList,
			double trendRatio,

			ArrayList<AnalyzerTrendResult> trendResultList,
			ArrayList<AnalyzerTrendResult> skippedMealResultList)
	{
		int minMins = PrefsNightScoutLoader.getInstance().getM_AnalyzerMinMinsForTrendResults();

		// If the trend exists and first part was hypo, then reduce this down
		if (m_CurrentTrendResult != null && 
				m_CurrentTrendResult.getM_AnalyzerSingleResult1().getM_DBResult().getM_CP_Glucose()  <= PrefsNightScoutLoader.getInstance().getM_AnalyzerLowRangeThreshold())
		{
			minMins = 15;
		}

		m_CurrentTrendResult     = categorizeResultNew(singleResult, 
				timeSlot, 
				applicableIndividualList, 
				trendRatio, 
				trendResultList, 
				minMins, 
				m_CurrentTrendResult);

		// If the trend is complete, we are in a meal and there's no carbs then proceed
		identifySkippedResult(m_CurrentTrendResult, skippedMealResultList);

	}


	//	private void categorizeResult(DBResult dbResult, 
	//			DBResult.TimeSlot timeSlot,
	//
	//			ArrayList<AnalyzerSingleResult> applicableIndividualList,
	//			double trendRatio,
	//
	//			ArrayList<AnalyzerTrendResult> trendResultList,
	//			ArrayList<AnalyzerTrendResult> skippedMealResultList)
	//	{
	//		// David 23 Jul 2016
	//		// Try something different.
	//		// If the first bg is hypo then reduce time accordingly. 
	//		int minMins = PrefsNightScoutLoader.getInstance().getM_AnalyzerMinMinsForTrendResults();
	//
	//		// If the trend exists and first part was hypo, then reduce this down
	//		if (m_CurrentTrendResult != null && 
	//				m_CurrentTrendResult.getM_AnalyzerSingleResult1().getM_DBResult().getM_CP_Glucose()  <= PrefsNightScoutLoader.getInstance().getM_AnalyzerLowRangeThreshold())
	//		{
	//			minMins = 15;
	//		}
	//
	//		m_CurrentTrendResult     = categorizeResult(dbResult, 
	//				timeSlot, 
	//				applicableIndividualList, 
	//				trendRatio, 
	//				trendResultList, 
	//				minMins, 
	//				m_CurrentTrendResult);
	//
	//		// If the trend is complete, we are in a meal and there's no carbs then proceed
	//		identifySkippedResult(m_CurrentTrendResult, skippedMealResultList);
	//	}

	private void identifySkippedResult(AnalyzerTrendResult trendResult, ArrayList<AnalyzerTrendResult>  skippedMealResultList)
	{
		// We are handling a meal (could be overnight)
		if (skippedMealResultList != null)
		{
			// There is a current trend result and it is complete
			if (trendResult != null &&
					trendResult.getM_AnalyzerSingleResult1() != null &&
					trendResult.getM_AnalyzerSingleResult2() != null)
			{
				// Finally, does the result1 have some carbs?
				if (trendResult.getM_AnalyzerSingleResult1().getM_DBResult().getM_CP_Carbs() == null)
				{
					skippedMealResultList.add(trendResult);
				}

				// It's possible that this is a meal and we stored a non-meal entry?
				// Need to verify as it's been months since I reviewed the algorithm 
				// that groups results together
				else
				{
					// Get the last result added.
					if (skippedMealResultList.size() > 0)
					{
						AnalyzerTrendResult lastSkipped = skippedMealResultList.get(skippedMealResultList.size() - 1);
						Date lastSkippedD = new Date(lastSkipped.getM_AnalyzerSingleResult1().getM_DBResult().getM_EpochMillies());
						Date thisD = new Date(trendResult.getM_AnalyzerSingleResult1().getM_DBResult().getM_EpochMillies());
						// Result is the same day, remove the previously stored skipped entry
						if (CommonUtils.timeDiffInMinutes(thisD, lastSkippedD) < 24 * 60)
						{
							skippedMealResultList.remove(skippedMealResultList.size() - 1);
						}
					}
				}
			}
		}
	}

	private AnalyzerTrendResult categorizeResultNew(AnalyzerSingleResult singleResult, 
			DBResult.TimeSlot timeSlot,

			ArrayList<AnalyzerSingleResult> applicableIndividualList,
			double trendRatio,

			ArrayList<AnalyzerTrendResult> trendResultList,
			int                            minDiffMins,
			AnalyzerTrendResult            trendResult)
	{	
		//AnalyzerTrendResult result = null;

		//		double  analyzerHighThreshold                = PrefsNightScoutLoader.getInstance().getM_AnalyzerHighThreshold();
		//		double  analyzerHighThresholdRelevanceFactor = PrefsNightScoutLoader.getInstance().getM_AnalyzerHighThresholdRelevanceFactor();
		//		double  analyzerLowThreshold                 = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowThreshold();
		//		double  analyzerLowThresholdRelevanceFactor  = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowThresholdRelevanceFactor();

		double  analyzerLowRangeThreshold            = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowRangeThreshold();
		//		double  analyzerHighRangeThreshold           = PrefsNightScoutLoader.getInstance().getM_AnalyzerHighRangeThreshold();

		String  analyzerBedTrendStartStartTime       = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendStartStartTime();
		String  analyzerBedTrendStartEndTime         = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendStartEndTime();
		//		String  analyzerBedTrendEndStartTime         = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendEndStartTime();
		//		String  analyzerBedTrendEndEndTime           = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendEndEndTime();


		if (singleResult != null)
		{
			Double bgResult = singleResult.getM_DBResult().getM_CP_Glucose();		

			// Check if list is empty or not already added
			if ((applicableIndividualList.size() == 0) ||
					(applicableIndividualList.size() > 0 && 
							applicableIndividualList.get(applicableIndividualList.size() - 1) != singleResult))
			{
				applicableIndividualList.add(singleResult);  // WIll happen multiple times
			}

			// A result can be either the start of a trend or the end of a trend, but not both.
			// Two trend results can span different time slot periods too.

			if (trendResult != null)
			{
				boolean stored = trendResult.checkForResult(singleResult, minDiffMins, trendRatio);
				if (!stored)
				{
					// Check whether bg1 was discarded too.  If so, then remove
					// this trend result and start again.
					if (trendResult.getM_AnalyzerSingleResult1() == null)
					{
						singleResult.setM_ReasonForDiscard("Trend Result - checkForResult - returned Not Stored");
						trendResultList.remove(trendResult);
						trendResult = null;
					}
				}
				else
				{
					// Reset to null to collect next result as a trend
					trendResult = null;
					singleResult.setM_ReasonForDiscard("Should have been Stored as #2");
				}
			}
			else
			{
				// We should really only store a trend result under the following circumstances:
				//  1 First result is a hypo - we're interested in what happens for hypo management
				//  2 First BG is within range - we're interested in excursions out of range


				// Originally
				//   1 First BG has no associated insulin or carbs
				//   2 First BG has associated insulin AND carbs
				//   3 BG is within range - we're interested in excursions out of range really.

				// Let's just store all of them!

				// David 20 Jul 2016
				// Thought of another situation
				//   We have a previous trend that looks like a skip meal
				//   However, this single result is same meal period and includes carbs
				//   Then we want to switch BG 2 out and replace with this and actually not create a new trend with this one
				//   Makes sense???  Good.
				//
				// Leading to other possibilities here:
				//   Revised Trend Detection Algorithm
				//    Currently - Take result, look at next.  If less than threshold then restart from 2nd result.
				//   Instead:
				//    Look at result then next.  If next is less than threshold, add to an internal Trend skipped result list and get next
				//    


				AnalyzerTrendResult prevAnalyzerTrendResult = trendResultList.size() > 1 ? trendResultList.get(trendResultList.size() - 1) : null;
				boolean usedAsAlternate = false;

				if (prevAnalyzerTrendResult != null)
				{
					usedAsAlternate = prevAnalyzerTrendResult.checkForAlternateEndResult(singleResult, minDiffMins, trendRatio);										
				}

				// However, only store bed results if between the start and end times configured.
				if (usedAsAlternate == false && timeSlot == DBResult.TimeSlot.BedTime)
				{
					try {
						if (CommonUtils.isTimeBetween(analyzerBedTrendStartStartTime, 
								analyzerBedTrendStartEndTime,
								singleResult.getM_DBResult().getM_Time()))
						{
							trendResult = new AnalyzerTrendResult(singleResult, AnalyzerTrendResultTypeEnum.overnightTrendType);
							trendResultList.add(trendResult);
							singleResult.setM_ReasonForDiscard("Should have been Stored as #1 - Overnight");
						}

						else
						{
							if (prevAnalyzerTrendResult != null)
							{
								prevAnalyzerTrendResult.addInterveningResult(singleResult);
								singleResult.setM_ReasonForDiscard(singleResult.getM_ReasonForDiscard() 
										+ " Overnight BG after overnight start time range");
							}
							else
							{
								singleResult.setM_ReasonForDiscard("No BG to start overnight trend.  This result is after overnight start time range");
							}
						}

						// Note that we can actually skip results here without keeping a record of them
						// as we do once we've got the 1st result

					} 
					catch (ParseException e) 
					{
						m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + ". Exception checking date ranges. " + e.getMessage());
					}
				}
				else if (usedAsAlternate == false)
				{
					singleResult.setM_ReasonForDiscard("Should have been Stored as #1 - Meal or hypo");
					trendResult = new AnalyzerTrendResult(singleResult, 
							bgResult < analyzerLowRangeThreshold ? AnalyzerTrendResultTypeEnum.hypoTrendType :
								AnalyzerTrendResultTypeEnum.mealTrendType);
					trendResultList.add(trendResult);
				}
			}
		}

		return trendResult;
	}


	//	private AnalyzerTrendResult categorizeResult(DBResult dbResult, 
	//			DBResult.TimeSlot timeSlot,
	//
	//			ArrayList<AnalyzerSingleResult> applicableIndividualList,
	//			double trendRatio,
	//
	//			ArrayList<AnalyzerTrendResult> trendResultList,
	//			int                            minDiffMins,
	//			AnalyzerTrendResult            trendResult)
	//	{	
	//		double  analyzerHighThreshold                = PrefsNightScoutLoader.getInstance().getM_AnalyzerHighThreshold();
	//		double  analyzerHighThresholdRelevanceFactor = PrefsNightScoutLoader.getInstance().getM_AnalyzerHighThresholdRelevanceFactor();
	//		double  analyzerLowThreshold                 = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowThreshold();
	//		double  analyzerLowThresholdRelevanceFactor  = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowThresholdRelevanceFactor();
	//
	//		double  analyzerLowRangeThreshold            = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowRangeThreshold();
	//		double  analyzerHighRangeThreshold           = PrefsNightScoutLoader.getInstance().getM_AnalyzerHighRangeThreshold();
	//
	//		String  analyzerBedTrendStartStartTime       = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendStartStartTime();
	//		String  analyzerBedTrendStartEndTime         = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendStartEndTime();
	//		//		String  analyzerBedTrendEndStartTime         = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendEndStartTime();
	//		//		String  analyzerBedTrendEndEndTime           = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendEndEndTime();
	//
	//
	//		// Construct a AnalyzerSingleResult and add to appropriate list
	//		Double  bgResult = dbResult.getM_CP_Glucose();
	//
	//		// See whether this is a temp basal for some extra colour on analysis
	//		//		Double basalResult = dbResult.getM_CP_BasalValue();
	//
	//		// THe result might not actually have a bg (example Temp Basal)
	//		// Therefore, see if mid way through a trend and add commentary accordingly
	//
	//		// Only really proceed if this is a BG result
	//
	//		if (bgResult != null)
	//		{
	//			L0AnalyzerSingleResultEnum singleResultEnum = L0AnalyzerSingleResultEnum.inRange;
	//			long relevance   = 0;
	//			double A         = 0.0;
	//			double B         = 0.0;
	//
	//			if (bgResult > analyzerHighThreshold)
	//			{
	//				singleResultEnum = L0AnalyzerSingleResultEnum.tooHigh;
	//			}
	//			else if (bgResult > analyzerHighRangeThreshold)
	//			{
	//				singleResultEnum = L0AnalyzerSingleResultEnum.aboveRange;
	//			}
	//			else if (bgResult < analyzerLowThreshold)
	//			{
	//				singleResultEnum = L0AnalyzerSingleResultEnum.tooLow;
	//			}
	//			else if (bgResult < analyzerLowRangeThreshold)
	//			{
	//				singleResultEnum = L0AnalyzerSingleResultEnum.belowRange;
	//			}
	//			else
	//			{
	//				singleResultEnum = L0AnalyzerSingleResultEnum.inRange;
	//				// In range?  always 1...
	//				relevance =  1;
	//			}
	//
	//
	//			if ((singleResultEnum == L0AnalyzerSingleResultEnum.tooHigh) ||
	//					(singleResultEnum == L0AnalyzerSingleResultEnum.aboveRange))
	//			{
	//				A         = analyzerHighRangeThreshold;  // Top of normal range
	//				B         = analyzerHighThresholdRelevanceFactor * analyzerHighThreshold;
	//
	//				// http://mathforum.org/library/drmath/view/60433.html
	//				relevance =  bgResult > B ? 10 : (long)(1 + (bgResult - A) * (10 - 1)/(B - A));
	//			}
	//			if ((singleResultEnum == L0AnalyzerSingleResultEnum.tooLow) ||
	//					(singleResultEnum == L0AnalyzerSingleResultEnum.belowRange))
	//			{
	//				A         = analyzerLowThresholdRelevanceFactor * analyzerLowThreshold;  // Top of normal range
	//				B         = analyzerLowRangeThreshold;  // Always 4.0
	//
	//				// http://mathforum.org/library/drmath/view/60433.html
	//				relevance =  bgResult < A ? 10 : 10 - ((long)(1 + (bgResult - A) * (10 - 1)/(B - A)));
	//			}
	//
	//
	//			AnalyzerSingleResult singleResult = new AnalyzerSingleResult(singleResultEnum, dbResult, timeSlot);
	//			singleResult.setM_ReasonForDiscard("Just Created");
	//
	//
	//			buildDaySummary(singleResult);
	//
	//			singleResult.setM_RelevanceScore(relevance);
	//			applicableIndividualList.add(singleResult);
	//
	//			// A result can be either the start of a trend or the end of a trend, but not both.
	//			// Two trend results can span different time slot periods too.
	//
	//			if (trendResult != null)
	//			{
	//				boolean stored = trendResult.checkForResult(singleResult, minDiffMins, trendRatio);
	//				if (!stored)
	//				{
	//					// Check whether bg1 was discarded too.  If so, then remove
	//					// this trend result and start again.
	//					if (trendResult.getM_AnalyzerSingleResult1() == null)
	//					{
	//						singleResult.setM_ReasonForDiscard("Trend Result - checkForResult - returned Not Stored");
	//						trendResultList.remove(trendResult);
	//						trendResult = null;
	//					}
	//
	//				}
	//				else
	//				{
	//					// Reset to null to collect next result as a trend
	//					trendResult = null;
	//					singleResult.setM_ReasonForDiscard("Should have been Stored as #2");
	//				}
	//			}
	//			else
	//			{
	//				// We should really only store a trend result under the following circumstances:
	//				//  1 First result is a hypo - we're interested in what happens for hypo management
	//				//  2 First BG is within range - we're interested in excursions out of range
	//
	//
	//				// Originally
	//				//   1 First BG has no associated insulin or carbs
	//				//   2 First BG has associated insulin AND carbs
	//				//   3 BG is within range - we're interested in excursions out of range really.
	//
	//				// Let's just store all of them!
	//
	//				// David 20 Jul 2016
	//				// Thought of another situation
	//				//   We have a previous trend that looks like a skip meal
	//				//   However, this single result is same meal period and includes carbs
	//				//   Then we want to switch BG 2 out and replace with this and actually not create a new trend with this one
	//				//   Makes sense???  Good.
	//				//
	//				// Leading to other possibilities here:
	//				//   Revised Trend Detection Algorithm
	//				//    Currently - Take result, look at next.  If less than threshold then restart from 2nd result.
	//				//   Instead:
	//				//    Look at result then next.  If next is less than threshold, add to an internal Trend skipped result list and get next
	//				//    
	//
	//
	//				AnalyzerTrendResult prevAnalyzerTrendResult = trendResultList.size() > 1 ? trendResultList.get(trendResultList.size() - 1) : null;
	//				boolean usedAsAlternate = false;
	//
	//				if (prevAnalyzerTrendResult != null)
	//				{
	//					usedAsAlternate = prevAnalyzerTrendResult.checkForAlternateEndResult(singleResult, minDiffMins, trendRatio);										
	//				}
	//
	//				// However, only store bed results if between the start and end times configured.
	//				if (usedAsAlternate == false && timeSlot == DBResult.TimeSlot.BedTime)
	//				{
	//					try {
	//						if (CommonUtils.isTimeBetween(analyzerBedTrendStartStartTime, 
	//								analyzerBedTrendStartEndTime,
	//								singleResult.getM_DBResult().getM_Time()))
	//						{
	//							trendResult = new AnalyzerTrendResult(singleResult, AnalyzerTrendResultTypeEnum.overnightTrendType);
	//							trendResultList.add(trendResult);
	//							singleResult.setM_ReasonForDiscard("Should have been Stored as #1 - Overnight");
	//						}
	//
	//						else
	//						{
	//							if (prevAnalyzerTrendResult != null)
	//							{
	//								prevAnalyzerTrendResult.addInterveningResult(singleResult);
	//								singleResult.setM_ReasonForDiscard(singleResult.getM_ReasonForDiscard() 
	//										+ " Overnight BG after overnight start time range");
	//							}
	//							else
	//							{
	//								singleResult.setM_ReasonForDiscard("No BG to start overnight trend.  This result is after overnight start time range");
	//							}
	//						}
	//
	//						// Note that we can actually skip results here without keeping a record of them
	//						// as we do once we've got the 1st result
	//
	//					} 
	//					catch (ParseException e) 
	//					{
	//						m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + ". Exception checking date ranges. " + e.getMessage());
	//					}
	//				}
	//				else if (usedAsAlternate == false)
	//				{
	//					singleResult.setM_ReasonForDiscard("Should have been Stored as #1 - Meal or hypo");
	//					trendResult = new AnalyzerTrendResult(singleResult, 
	//							bgResult < analyzerLowRangeThreshold ? AnalyzerTrendResultTypeEnum.hypoTrendType :
	//								AnalyzerTrendResultTypeEnum.mealTrendType);
	//					trendResultList.add(trendResult);
	//				}
	//			}
	//		}
	//
	//		return trendResult;
	//	}

	private AnalyzerSingleResult buildAnalyzerSingleResult(DBResult res)
	{
		AnalyzerSingleResult result = null;

		double  analyzerHighThreshold                = PrefsNightScoutLoader.getInstance().getM_AnalyzerHighThreshold();
		double  analyzerHighThresholdRelevanceFactor = PrefsNightScoutLoader.getInstance().getM_AnalyzerHighThresholdRelevanceFactor();
		double  analyzerLowThreshold                 = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowThreshold();
		double  analyzerLowThresholdRelevanceFactor  = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowThresholdRelevanceFactor();

		double  analyzerLowRangeThreshold            = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowRangeThreshold();
		double  analyzerHighRangeThreshold           = PrefsNightScoutLoader.getInstance().getM_AnalyzerHighRangeThreshold();

		//		String  analyzerBedTrendStartStartTime       = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendStartStartTime();
		//		String  analyzerBedTrendStartEndTime         = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendStartEndTime();
		//		String  analyzerBedTrendEndStartTime         = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendEndStartTime();
		//		String  analyzerBedTrendEndEndTime           = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendEndEndTime();


		// Construct a AnalyzerSingleResult and add to appropriate list
		Double  bgResult = res.getM_CP_Glucose();

		DBResult.TimeSlot timeSlot = res.getDBResultTimeSlot();  


		// See whether this is a temp basal for some extra colour on analysis
		//		Double basalResult = dbResult.getM_CP_BasalValue();

		// THe result might not actually have a bg (example Temp Basal)
		// Therefore, see if mid way through a trend and add commentary accordingly

		// Only really proceed if this is a BG result

		if (bgResult != null)
		{
			L0AnalyzerSingleResultEnum singleResultEnum = L0AnalyzerSingleResultEnum.inRange;
			long relevance   = 0;
			double A         = 0.0;
			double B         = 0.0;

			if (bgResult > analyzerHighThreshold)
			{
				singleResultEnum = L0AnalyzerSingleResultEnum.tooHigh;
			}
			else if (bgResult > analyzerHighRangeThreshold)
			{
				singleResultEnum = L0AnalyzerSingleResultEnum.aboveRange;
			}
			else if (bgResult < analyzerLowThreshold)
			{
				singleResultEnum = L0AnalyzerSingleResultEnum.tooLow;
			}
			else if (bgResult < analyzerLowRangeThreshold)
			{
				singleResultEnum = L0AnalyzerSingleResultEnum.belowRange;
			}
			else
			{
				singleResultEnum = L0AnalyzerSingleResultEnum.inRange;
				// In range?  always 1...
				relevance =  1;
			}


			if ((singleResultEnum == L0AnalyzerSingleResultEnum.tooHigh) ||
					(singleResultEnum == L0AnalyzerSingleResultEnum.aboveRange))
			{
				A         = analyzerHighRangeThreshold;  // Top of normal range
				B         = analyzerHighThresholdRelevanceFactor * analyzerHighThreshold;

				// http://mathforum.org/library/drmath/view/60433.html
				relevance =  bgResult > B ? 10 : (long)(1 + (bgResult - A) * (10 - 1)/(B - A));
			}
			if ((singleResultEnum == L0AnalyzerSingleResultEnum.tooLow) ||
					(singleResultEnum == L0AnalyzerSingleResultEnum.belowRange))
			{
				A         = analyzerLowThresholdRelevanceFactor * analyzerLowThreshold;  // Top of normal range
				B         = analyzerLowRangeThreshold;  // Always 4.0

				// http://mathforum.org/library/drmath/view/60433.html
				relevance =  bgResult < A ? 10 : 10 - ((long)(1 + (bgResult - A) * (10 - 1)/(B - A)));
			}

			result = new AnalyzerSingleResult(singleResultEnum, res, timeSlot);
			result.setM_RelevanceScore(relevance);
			result.setM_ReasonForDiscard("Just Created");

		}

		return result;
	}

	private void buildDaySummary(AnalyzerSingleResult singleResult)
	{
		// Get last day summary from list.
		// Check date against this result
		// If same, then process it
		// If not, then create a new entry, process it and add to the list.

		AnalyzerDaySummary lastSummary = null;
		Date singleResultDate  = singleResult.getM_DBResult().getM_Time();

		if (m_AnalyzerDaySummaries.size() > 0)
		{
			lastSummary = m_AnalyzerDaySummaries.get(m_AnalyzerDaySummaries.size() - 1);

			// Compare date.  If same fine, if not then reset
			Date lastDate = lastSummary.getM_Date();
			if (!CommonUtils.isDateTheSame(singleResultDate, lastDate))
			{
				if (lastSummary != null)
				{
					lastSummary.setRelevance();
				}
				lastSummary = null;
			}
		}

		if (lastSummary == null)
		{
			lastSummary = new AnalyzerDaySummary(singleResultDate);
			m_AnalyzerDaySummaries.add(lastSummary);
		}
		lastSummary.processSingleResult(singleResult);
	}


	// Look for days without results in the collection of results.
	public void scanForGaps(ArrayList <DBResult> results) throws ParseException
	{
		// Clear current results
		int missedDates = 0;

		final DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

		// Iterate through the array looking for current & next date
		String   firstDate   = null;
		String   lastDate    = null;

		String thisDateStr = new String();
		String currDateStr = new String();
		String nextDateStr = new String();

		for (DBResult r : results)
		{
			if (firstDate == null)
			{
				firstDate = r.getM_CP_EventTime();
			}

			String evTime = r.getM_CP_EventTime();
			if (evTime.length() >=10)
			{
				// Check this date against current date
				thisDateStr = evTime.substring(0, 10);
			}

			if ((currDateStr.length() > 0) && (!thisDateStr.equals(currDateStr)))
			{
				currDateStr = "";

				// Not good.  We've moved to another date instead.
				// Add some commentary on this one,
				if (!thisDateStr.equals(nextDateStr))
				{
					// Now just log directly
					m_Logger.log(Level.INFO, "<"+this.getClass().getName()+">" + ". No Results for date: " + nextDateStr);
					missedDates++;
				}
			}

			// Initialise Current date based on the current record time stamp & set next date one day later
			if (currDateStr.length() == 0)
			{
				currDateStr = thisDateStr;
				// Construct a Java date from this string
				Date currDate = new Date();
				currDate = format.parse(currDateStr);
				Calendar c = Calendar.getInstance();
				c.setTime(currDate);
				c.add(Calendar.DATE, 1); // Add one day
				nextDateStr = format.format(c.getTime());
			}

			lastDate = r.getM_CP_EventTime();
		}

		if (missedDates == 0)
		{
			m_Logger.log(Level.INFO, "<"+this.getClass().getName()+">" + ". Analysis complete.  No gaps found in sequence between " 
					+ firstDate + " and " + lastDate + "\n");
		}
		else
		{
			m_Logger.log(Level.INFO, "<"+this.getClass().getName()+">" + ". Analysis complete.  (" + missedDates + ") gaps found in sequence between " 
					+ firstDate + " and " + lastDate + "\n");
		}
	}

}
