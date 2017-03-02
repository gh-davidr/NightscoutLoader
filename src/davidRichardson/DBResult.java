package davidRichardson;
// This class holds values for a single row from DB

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mongodb.BasicDBObject;

import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DBResult implements DBResultInterface
{

	protected static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	// Sep 2016
	// Proximity checks are for where we have a new Meter/Pump entry coming into an
	// existing NightScout Care Portal data set, and there's a possible duplicate
	// among them.
	private static boolean m_ProximityCheck           = false;
	private static boolean m_ProximityCheckSecondPass = false;


	// Enumerator for Analysis
	enum TimeSlot
	{
		UnknownTime,
		BreakfastTime,
		LunchTime,
		DinnerTime,
		BedTime
	};

	// Need to return more that true/false back from merge
	enum MergeResult
	{
		Merged,     // Raw result can merge
		TooDistant, // Raw result is part of another result
		Duplicate,
		CantMerge   // Raw result is completely different
	};
	
	// Since the file already groups this together, we need to read each section separately
	// to utilize the grouping logic already established for other data sources.
	// So when reading the pump values, tell the constructor what to look out for.
	public enum ResultType
	{
		Unknown,
		BG,
		Insulin,
		Carbs,
		Basal,
	};

	// Work out which time the result falls into
	public TimeSlot getDBResultTimeSlot()
	{
		TimeSlot result = TimeSlot.UnknownTime;

		// Get time preferences for each timeslot
		String breakfastStartTime = PrefsNightScoutLoader.getInstance().getM_AnalyzerBreakfastTimeStart();
		String lunchStartTime     = PrefsNightScoutLoader.getInstance().getM_AnalyzerLunchTimeStart();
		String dinnerStartTime    = PrefsNightScoutLoader.getInstance().getM_AnalyzerDinnerTimeStart();
		String bedStartTime       = PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTimeStart();

		if (m_TreatmentDate.length() > 0)
		{
			if (isTimeSlotBetween(breakfastStartTime, lunchStartTime))
			{
				result = TimeSlot.BreakfastTime;
			}
			else if (isTimeSlotBetween(lunchStartTime, dinnerStartTime))
			{
				result = TimeSlot.LunchTime;
			}
			else if (isTimeSlotBetween(dinnerStartTime, bedStartTime))
			{
				result = TimeSlot.DinnerTime;
			}
			//bed first
			else if (isTimeSlotLater(bedStartTime) || isTimeSlotBefore(breakfastStartTime))
			{
				result = TimeSlot.BedTime;
			}
		}

		return result;
	}

	static public String getTimeSlotString(TimeSlot timeSlot)
	{
		String result = null;

		final String str_UnknownTime   = "Unknown Time";
		final String str_BreakfastTime = "Breakfast Time";
		final String str_LunchTime     = "Lunch Time";
		final String str_DinnerTime    = "Dinner Time";
		final String str_BedTime       = "Bed Time";

		switch (timeSlot)
		{
		case UnknownTime:   result = str_UnknownTime;    break;
		case BreakfastTime: result = str_BreakfastTime;  break;
		case LunchTime:     result = str_LunchTime;      break;
		case DinnerTime:    result = str_DinnerTime;     break;
		case BedTime:       result = str_BedTime;        break;
		}

		return result;

	}

	private boolean isTimeSlotLater(String mealSlotStartTime)
	{
		boolean result = false;
		String mealSlotStartDate = new String(m_TreatmentDate + " " + mealSlotStartTime);
		try {
			Date mealSlotStart = CommonUtils.convertDateString(mealSlotStartDate, "dd-MMM-yyyy HH:mm:ss");
			int startComp = mealSlotStart.compareTo(m_Time);

			// Check if start <= time
			if (startComp <= 0)
			{
				result = true;
			}
		} 
		catch (ParseException e) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+"> isTimeSlotLater " + " Exception coverting " + 
					mealSlotStartTime + " time to internal date time for comparison.  " + e.getMessage());
		}

		return result;
	}

	private boolean isTimeSlotBefore(String mealSlotEndTime)
	{
		boolean result = false;
		String mealSlotEndDate   = new String(m_TreatmentDate + " " + mealSlotEndTime);
		try {
			Date mealSlotEnd   = CommonUtils.convertDateString(mealSlotEndDate,   "dd-MMM-yyyy HH:mm:ss");

			int endComp   = mealSlotEnd.compareTo(m_Time);

			// Check if time < end
			if (endComp > 0)
			{
				result = true;
			}
		} 
		catch (ParseException e) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+"> isTimeSlotBefore " + " Exception coverting " + 
					mealSlotEndTime + " time to internal date time for comparison.  " + e.getMessage());
		}

		return result;
	}

	private boolean isTimeSlotBetween(String mealSlotStartTime, String mealSlotEndTime)
	{
		boolean result = false;
		String mealSlotStartDate = new String(m_TreatmentDate + " " + mealSlotStartTime);
		String mealSlotEndDate   = new String(m_TreatmentDate + " " + mealSlotEndTime);
		try {
			Date mealSlotStart = CommonUtils.convertDateString(mealSlotStartDate, "dd-MMM-yyyy HH:mm:ss");
			Date mealSlotEnd   = CommonUtils.convertDateString(mealSlotEndDate,   "dd-MMM-yyyy HH:mm:ss");

			int startComp = mealSlotStart.compareTo(m_Time);
			int endComp   = mealSlotEnd.compareTo(m_Time);

			// Check if start <= time < end
			if (startComp <= 0 && endComp > 0)
			{
				result = true;
			}

		} 
		catch (ParseException e) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+"> isTimeSlotBetween " + " Exception coverting " + 
					mealSlotStartTime + " and " + mealSlotEndTime + " time to internal date times for comparison.  " + e.getMessage());
		}

		return result;
	}



	// 21 Jan 2016
	// Realise that these fields need to be native types
	// that is integers & date
	// That way they will be stored in MongoDB better
	// & support querying better :-)
	/*	private String year;
	private String month;
	private String day;
	private String dayName;
	private String time;
	private String timeSlot;
	private String result;
	private String resultType;
	private String mealType;
	private String duration;
	 */
	// Raw Result Data
	protected int    m_Year;
	protected int    m_Month;
	protected int    m_Day;
	protected String m_DayName;
	protected Date   m_Time;
	protected String m_TimeSlot;
	protected String m_Result;
	protected String m_ExtendedAmount; // Separate field to hold extra insulin administered for dual insulin doses
	protected String m_ResultType;
	protected String m_MealType;
	protected String m_Duration;
	protected String m_Notes;   // Any underlying notes or notes generated from raw data processing

	// Treatment Data for Care Portal
	protected String m_ID;
	protected String m_CP_EventType;
	protected Double m_CP_Glucose;
	protected Double m_CP_Carbs;
	protected Double m_CP_Insulin;
	protected Double m_CP_CarbsTime;
	protected Double m_CP_Duration;    // Temp Basal
	protected Double m_CP_Percent;     // Temp Basal
	protected Double m_CP_BasalValue;  // Temp Basal
	protected String m_CP_Notes;
	protected String m_CP_EnteredBy;
	protected String m_CP_EventTime;
	// Useful value for comparator
	protected long   m_EpochMillies = 0;
	protected String m_DataSource;

	// Derived fields useful for display
	protected String m_TreatmentDayName;
	protected String m_TreatmentTime;
	protected String m_TreatmentDate;

	// Flags to keep track of what we've seen
	private boolean m_BG = false;
	private boolean m_Carb = false;
	private boolean m_Ins = false;
	private boolean m_Corr = false; // A correction will be a BG then Ins in that order :-)
	private boolean m_TmpBasal = false;

	// Proximity match with an existing record
	private boolean m_ProximityPossibleDuplicate = false;

	// For knowing how we store results in MongoDB
	protected static String  m_determinantField = "enteredBy";
	protected static String  m_determinantValue = "Nightscout Loader";

	/*
	 * BG Check
	 * 		Glucose, Notes, EnteredBy, Time
	 * Snack Bolus
	 * 		Glucose, Carbs, Insulin, CarbsTime, Notes, EnteredBy, Time
	 * Meal Bolus
	 * 		Glucose, Carbs, Insulin, CarbsTime, Notes, EnteredBy, Time
	 * Correction Bolus
	 * 		Glucose, Insulin, Notes, EnteredBy, Time
	 * Carb Correction
	 * 		Glucose, Carbs, Notes, EnteredBy, Time
	 * Announcement
	 * 		Glucose, Notes, EnteredBy, Time
	 * Note,
	 * 		Glucose, Notes, Duration, EnteredBy, Time
	 * Question
	 * 		Glucose, Notes, EnteredBy, Time
	 * Exercise
	 * 		Duration, Notes, EnteredBy, Time
	 * Pump Site Change
	 * 		Glucose, Insulin, Notes, EnteredBy, Time
	 * Dexcom Sensor Site Change
	 * 		Glucose, Notes, EnteredBy, Time
	 * Dexcom Sensor ...
	 * 		Glucose, Notes, EnteredBy, Time
	 * Insulin Cartridge Change
	 * 		Glucose, Insulin, Notes, EnteredBy, Time
	 * Temp Basal Start
	 * 		Glucose, Duration, Percent, Basal Value, Notes, EnteredBy, Time
	 * Temp Basal End
	 * 		Glucose, Notes, EnteredBy, Time
	 * D.A.D Alert
	 * 		Glucose, Notes, EnteredBy, Time
	 * 
	 *  'Combo Bolus' - This is from Gitter - on Dev branch currently
	 */


	/*
	 * Raw Columns
	 */
	/*
	static String[] colNames = {"Year", "Month", "Day", "Name of Day", "Time",
			"Time Slot", "Result", "Result Type", "Meal Type", "Duration"};
	static int[] colWidths = {250, 250, 250, 400, 700,
			450, 250, 700, 500, 250};
	static Object[][] initializer = {{"","","","","","","","","",""}};
	 */

	//	static String[] colNames = {
	//			 "Event Type", "Glucose Reading", "Carbs Given", "Insulin Given", "Carb Time",
	//			 "Duration", "Percent", "Basal Value", "Notes", "Entered By", "Event Time",
	//	};
	//	static int[] colWidths = {250, 250, 250, 400, 700,
	//			  			      450, 250, 700, 500, 250, 
	//			  			      250, };
	private static String[] colNames = {
			//			 "Event Time",
			"Date",
			"Time",
			"Day Name",
			"Event Type", 
			"Glucose Reading", 
			"Carbs Given", 
			"Insulin Given", 
			"Carb Time",
			"Duration",
			"Percent", 
			"Notes", 
			"Basal Value", 
			"Entered By", 
	};
	private static String[] colNamesWithID = {
			//			 "Event Time",
			"ID",
			"Date",
			"Time",
			"Day Name",
			"Event Type", 
			"Glucose Reading", 
			"Carbs Given", 
			"Insulin Given", 
			"Carb Time",
			"Duration",
			"Percent", 
			"Notes", 
			"Basal Value", 
			"Entered By", 
	};

	static int[] colWidths = {250, 250, 250, 250, 250, 400, 700,
			450, 250, 700, 500, 250, 
			250, };

	static Object[][] initializer = {{"","","","","",
		"","","","","",
		"", }};

	static String getCP_EventTimeFormat()
	{
		final String result =  "yyyy-MM-dd'T'HH:mm:ss.S'Z'";
		return result;
	}


	public String[] toArray(boolean rawData)
	{
		//		final DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S", Locale.ENGLISH);
		final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
		String timeString = new String(format.format(m_Time));
		// m_Time used to populate event time too

		int arrSize = 0;

		if (rawData)
		{
			arrSize = 12;
		}
		else
		{
			arrSize = 17;
		}

		String[] res = new String[arrSize];

		m_Logger.log(Level.FINEST, toString());

		if (rawData)
		{
			int i   = 0;
			res[i++]  = String.format("%d", m_Year);
			res[i++]  = String.format("%d", m_Month);
			res[i++]  = String.format("%d", m_Day);
			res[i++]  = m_DayName;
			res[i++]  = timeString;
			res[i++]  = m_TimeSlot;
			res[i++]  = m_Result;
			res[i++]  = m_ExtendedAmount;
			res[i++]  = m_ResultType;
			res[i++]  = m_MealType;
			res[i++]  = m_Duration;
			res[i++]  = m_Notes;
		}
		else
		{
			/*
			 * 
			 *
//			 "Event Time",
             "Date",
             "Time",          
			 "Day Name",
			 "Event Type", 
			 "Glucose Reading", 
			 "Carbs Given", 
			 "Insulin Given", 
			 "Carb Time",
			 "Duration",
			 "Percent", 
			 "Notes", 
			 "Basal Value", 
			 "Entered By", 
			 * 
			 */
			int i = 0;
			//			res[i++] = m_CP_EventTime;
			res[i++] = m_TreatmentDate;
			res[i++] = m_TreatmentTime;
			res[i++] = m_TreatmentDayName;
			res[i++] = m_CP_EventType;
			res[i++] = m_CP_Glucose    == null ? "" : getDoubleValue(m_CP_Glucose);    //String.valueOf(m_CP_Glucose);
			res[i++] = m_CP_Carbs      == null ? "" : getDoubleValue(m_CP_Carbs);      //String.valueOf(m_CP_Carbs);
			res[i++] = m_CP_Insulin    == null ? "" : getDoubleValue(m_CP_Insulin);    //String.valueOf(m_CP_Insulin);
			res[i++] = m_CP_CarbsTime  == null ? "" : getDoubleValue(m_CP_CarbsTime);
			res[i++] = m_CP_Duration   == null ? "" : getDoubleValue(m_CP_Duration);   //String.valueOf(m_CP_Duration);
			res[i++] = m_CP_Percent    == null ? "" : getDoubleValue(m_CP_Percent);    //String.valueOf(m_CP_Percent);
			res[i++] = m_CP_Notes;
			res[i++] = m_CP_BasalValue == null ? "" : getDoubleValue(m_CP_BasalValue); //String.valueOf(m_CP_BasalValue);
			res[i++] = m_CP_EnteredBy;
		}

		return res;
	}


	/**
	 * @return the colNames
	 */
	public static String[] getColNames() {
		return colNames;
	}

	/**
	 * @return the colNames
	 */
	public static String[] getColNamesWithID() {
		return colNamesWithID;
	}

	/**
	 * @return the colWidths
	 */
	public static int[] getColWidths() {
		return colWidths;
	}

	/**
	 * @return the initializer
	 */
	public static Object[][] getInitializer() {
		return initializer;
	}

	/**
	 * @param initializer the initializer to set
	 */
	public static void setInitializer(Object[][] initializer) {
		DBResult.initializer = initializer;
	}


	public DBResult()
	{
		super();

		m_DayName        = new String();
		m_Time           = new Date(0);
		m_TimeSlot       = new String();
		m_Result         = new String();
		m_ExtendedAmount = new String();
		m_ResultType     = new String();
		m_MealType       = new String();
		m_Duration       = new String();
		m_Notes          = new String();

		// Treatment Data
		m_ID             = new String();
		m_CP_EventType   = new String();
		m_CP_Notes       = new String();;
		m_CP_EnteredBy   = new String();
		m_CP_EventTime   = new String();

		m_TreatmentDayName = new String();	
		m_TreatmentTime    = new String();
		m_TreatmentDate    = new String();


		// Initialize all the numerics as null initially
		m_CP_Glucose    = null;
		m_CP_Carbs      = null;
		m_CP_Insulin    = null;
		m_CP_Duration   = null;
		m_CP_Percent    = null;
		m_CP_BasalValue = null;

		//		m_CP_Glucose    = new String();
		//		m_CP_Carbs      = new String();
		//		m_CP_Insulin    = new String();
		//		m_CP_CarbsTime  = new String();
		//	m_CP_Duration   = new String();    // Temp Basal
		//	m_CP_Percent    = new String();     // Temp Basal
		//		m_CP_BasalValue = new String();  // Temp Basal

		m_DataSource    = new String();
	}





	public String getId() 
	{
		return getIdentity();
	}

	public int hashcode() 
	{
		return this.getId().hashCode();
	}

	public String getIdentity()
	{
		String result = null;

		//		String result = new String(getM_CP_EventType() + this.getM_CP_EventTime());
		long   time = this.getM_EpochMillies();
		String details = new String("");

		if (m_ProximityCheck == true)
		{
			// How many minutes apart two entries can be before being considered proximity/duplicate
			int     proximityMinutes    = PrefsNightScoutLoader.getInstance().getM_ProximityMinutes();
			int     checkType           = PrefsNightScoutLoader.getInstance().getM_ProximityCheckType();

			//			boolean typeCheck           = PrefsNightScoutLoader.getInstance().isM_ProximityTypeCheck();
			boolean typeCheck           = checkType == 0 ? false : true;
			boolean checkBGValue        = PrefsNightScoutLoader.getInstance().isM_CompareBGInProximityCheck();
			boolean checkCarbValue      = PrefsNightScoutLoader.getInstance().isM_CompareCarbInProximityCheck();
			boolean checkInsulinValue   = PrefsNightScoutLoader.getInstance().isM_CompareInsulinInProximityCheck();

			int     checkBGValueDP      = PrefsNightScoutLoader.getInstance().getM_BGDecPlacesProximityCheck();
			int     checkCarbValueDP    = PrefsNightScoutLoader.getInstance().getM_CarbDecPlacesProximityCheck();
			int     checkInsulinValueDP = PrefsNightScoutLoader.getInstance().getM_InsulinDecPlacesProximityCheck();

			// Since we may have 2 adjacent readings either side of the mid point of proximityMinutes, we do a second
			// pass looking for proximity matches but this time slide the time forward by half the proximityMinutes
			if (m_ProximityCheckSecondPass == true)
			{
				long halfProximityMinutesMillis = proximityMinutes * 60 * 1000 / 2;

				time += halfProximityMinutesMillis;
			}

			long roundPeriodMins = proximityMinutes * 60 * 1000;
			// Adjust time by rounding up or down to nearest proximity minutes approximately.

			long timeUp   = time - (time % roundPeriodMins) + roundPeriodMins;
			long timeDown = time - (time % roundPeriodMins);

			// Are we closer to Up time or Down
			if ( (timeUp - time) > (time - timeDown) )
			{
				time = timeDown;
			}
			else
			{
				time = timeUp;
			}

			// Based on preferences, include the BG, Carb and Insulin formatted
			// again to preference decimal places for each parameter
			if (checkBGValue == true && this.getM_CP_Glucose() != null)
			{
				details += " :BG: " + String.format("%." + checkBGValueDP + "f", getM_CP_Glucose());
			}
			if (checkCarbValue == true && this.getM_CP_Carbs() != null)
			{
				details += " :CARBS: " + String.format("%." + checkCarbValueDP + "f", getM_CP_Carbs());
			}
			if (checkInsulinValue == true && this.getM_CP_Insulin() != null)
			{
				details += " :INSULIN: " + String.format("%." + checkInsulinValueDP + "f", getM_CP_Insulin());
			}

			result = new String((typeCheck == true ? getM_CP_EventType() : "" ) + String.format("%d", time) + details);
		}
		else
		{
			result = new String(getM_CP_EventType() + String.format("%d", time));
		}

		return result;
	}

	// http://stackoverflow.com/questions/919387/how-can-i-calculate-the-difference-between-two-arraylists
	// Looking at how I can compare lists of these guys...

	// override the equal method
	@Override
	public boolean equals(Object obj) 
	{ 
		if (obj == this) 
		{ 
			return true; 
		} 
		//		if (obj == null || obj.getClass() != this.getClass())
		if (obj == null/* || obj.getClass() != this.getClass()*/)
		{
			return false; 
		} 
		DBResult guest = (DBResult) obj;
		boolean result = false;
		/*
		 * THis was for testing raw values
		String id = m_Year + m_Month + m_Day + m_Time.toString() + m_ResultType + m_CP_EventType;
		String guest_id = guest.m_Year + guest.m_Month + guest.m_Day + guest.m_Time.toString() + guest.m_ResultType + guest.m_CP_EventType;
		 */
		// Comparisons are for treatments only.
		// String id       = this.getM_CP_EventType()  + this.getM_CP_EventTime();
		// String guest_id = guest.getM_CP_EventType() + guest.getM_CP_EventTime();
		String id       = getIdentity();
		String guest_id = guest.getIdentity();

		result = id.equals(guest_id);
		return result; 
	}

	@Override 
	public int hashCode() 
	{ 
		//		final int prime = 31; 
		int result = 1; 
		/*		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode()); 
		result = prime * result + id; 
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode()); 
		 */		
		/*
		 * THis was for testing raw values
		String id = m_Year + m_Month + m_Day + m_Time.toString() + m_ResultType + m_CP_EventType;
		 */
		// String id       = this.getM_CP_EventType()  + this.getM_CP_EventTime();
		String id       = getIdentity();

		result = id.hashCode();
		return result; 
	}

	protected Date parseFileDate(String date)
	{
		Date result = new Date(0);
		// One of a couple of formats

		// 15 Jun 2016
		// Bug found by Melanie Cragg in Australia 14 Jun 2016
		// Medtronic dates were all being stored as January
		// Andy's original file masked this issue as his data was all January!!!
		//		DateFormat dashformat  = new SimpleDateFormat("dd-mm-yyyy", Locale.ENGLISH);
		//		DateFormat slashformat = new SimpleDateFormat("dd/mm/yyyy", Locale.ENGLISH);

		final String defDashFormat    = new String("dd-MM-yy");
		final String defSlashFormat   = new String("dd/MM/yy");
		final String defISODashFormat = new String("yyyy-MM-dd'T'HH:mm:ss");
		String prefDateFormat       = PrefsNightScoutLoader.getInstance().getM_InputDateFormat();
		DateFormat dashformat       = new SimpleDateFormat((prefDateFormat.contains("-")  ?  prefDateFormat : defDashFormat), Locale.ENGLISH);
		DateFormat slashformat      = new SimpleDateFormat((prefDateFormat.contains("/")  ?  prefDateFormat : defSlashFormat), Locale.ENGLISH);
		DateFormat defISODashformat = new SimpleDateFormat(defISODashFormat);

		try
		{
			if (date.contains("T"))
			{
				result = defISODashformat.parse(date);
			}
			else if (date.contains("/"))
			{
				result = slashformat.parse(date);
			}
			else if (date.contains("-"))
			{
				result = dashformat.parse(date);
			}
		}
		catch (ParseException e) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "parseFileDate - Unexpected error parsing date: " + date);
		}

		return result;
	}


	//	Read more: http://javarevisited.blogspot.com/2011/02/how-to-write-equals-method-in-java.html#ixzz3xisun9v6	
	public String toString()
	{
		return String.format("EventType:%s Glucose:%f Carbs:%f Insulin:%f CarbsTime:%s Duration:%f Percent:%f BasalValue:%f Notes:%s EnteredBy:%s EnteredAt:%s", 
				m_CP_EventType, m_CP_Glucose, m_CP_Carbs, m_CP_Insulin, m_CP_CarbsTime, m_CP_Duration, m_CP_Percent, m_CP_BasalValue, m_CP_Notes, m_CP_EnteredBy, m_CP_EventTime);		
	}

	public String rawToString()
	{
		return String.format("ResultType:%s Result:%s ExtendedAmt:%s Year:%d Month:%d Day:%d epochMillies:%d", 
				m_ResultType, m_Result, m_ExtendedAmount, m_Year, m_Month, m_Day, m_EpochMillies);		
	}

	static public boolean doubleIsInteger(double val)
	{
		boolean result = false;  // Assume not initially

		if (val == Math.floor(val))
		{
			result = true;
		}
		return result;
	}

	private	String getDoubleValue(Double val)
	{	
		String result = new String();
		result = doubleIsInteger(val) ? String.format("%d", val.longValue()) : val.toString();
		return result;
	}

	public DBResult(ResultSet rs) throws SQLException
	{
		// Get this error if try to check resultset:
		// We just caught an error: The requested operation is not supported on forward only result sets. - The requested operation is not supported on forward only result sets.

		/*		if (!rs.isLast())
		{*/
		/*			year       = rs.getString("Year");
			month      = rs.getString("Month");
			day        = rs.getString("Day");
			dayName    = rs.getString("Name of Day");
			time       = rs.getString("ResultDate");
			timeSlot   = rs.getString("Time Slot");
			result     = rs.getString("Result");
			resultType = rs.getString("Result Type");
			mealType   = rs.getString("Meal Type");
			duration   = rs.getString("Duration");
		 */			
		super();

		m_Year       = rs.getInt("Year");
		m_Month      = rs.getInt("Month");
		m_Day        = rs.getInt("Day");
		m_DayName    = rs.getString("DayName");
		m_Time       = rs.getTimestamp("Time");
		m_TimeSlot   = rs.getString("TimeSlot");
		m_Result     = rs.getString("Result");
		m_ResultType = rs.getString("ResultType");
		m_MealType   = rs.getString("MealType");
		m_Duration   = rs.getString("Duration");	

		m_EpochMillies = m_Time.getTime();

		/*		}*/
	}


	public DBResult(DBResult res, String device)
	{
		super();

		// 17 Apr 
		// Try one big ... try

		try
		{
			// Treatment Data
			m_ID            = new String();
			m_CP_EventType  = new String();
			//		m_CP_Glucose    = new String();
			//		m_CP_Carbs      = new String();
			//		m_CP_Insulin    = new String();
			//		m_CP_CarbsTime  = new String();
			//		m_CP_Duration   = new String();    // Temp Basal
			//		m_CP_Percent    = new String();     // Temp Basal
			//		m_CP_BasalValue = new String();  // Temp Basal
			m_CP_Notes      = new String();;
			m_CP_EnteredBy  = new String();
			m_CP_EventTime  = new String();

			// Initialize all the numerics as null initially
			m_CP_Glucose    = null;
			m_CP_Carbs      = null;
			m_CP_Insulin    = null;
			m_CP_Duration   = null;
			m_CP_Percent    = null;
			m_CP_BasalValue = null;

			m_DataSource    = new String("Meter");

			// push these variables into the class here
			String resType = res.getM_ResultType();
			Date   resTime = res.getM_Time();

			final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'", Locale.ENGLISH);
			final DateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
			final DateFormat timeformat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

			// Store the time ... in both fields!
			m_CP_EventTime = format.format(resTime);
			m_Time         = resTime;
			m_EpochMillies = m_Time.getTime();

			// and the dayname
			m_TreatmentDayName = res.getM_DayName();
			m_TreatmentDate    = dateformat.format(resTime);
			m_TreatmentTime    = timeformat.format(resTime);

			// Complete some other fields too
			m_CP_EnteredBy = m_determinantValue; // "Nightscout Loader";

			// David 28 Apr 2016
			// Now hold details of upload id in enteredby field
			m_CP_EnteredBy = AuditHistory.getInstance().getM_NextUploadID();

			// Doing this check here is way too early!

			//			// If this result has proximity flag then something already stored was found in NS
			//			if (isM_ProximityMatch() == true)
			//			{
			//				m_CP_EnteredBy += "-PROXIMITY";
			//			}

			// Too noisy on the reports to include details in notes

			if (resType.equals("BG"))
			{
				m_BG = true;
				if (!m_Carb && m_Ins)
				{
					m_Corr = true; // Allow an Ins first then a BG
				}
				m_CP_EventType = "BG Check";

				m_CP_Glucose = new Double(Double.parseDouble(res.getM_Result()));
			}
			else if (resType.equals("Carbs"))
			{
				if (m_Corr)
				{
					m_Corr = false; // Can't be a correction with Carbs
				}
				m_Carb = true;
				m_CP_EventType = "Meal Bolus";
				new Double(m_CP_Carbs = Double.parseDouble(res.getM_Result()));	
			}
			else if (resType.equals("Standard Bolus"))
			{
				m_Ins = true;
				m_CP_EventType = "Correction Bolus";
				m_CP_Insulin = new Double(Double.parseDouble(res.getM_Result()));
			}
			else if (resType.equals("Pen Units"))
			{
				m_Ins = true;
				m_CP_EventType = "Correction Bolus";
				m_CP_Insulin = new Double(Double.parseDouble(res.getM_Result()));
				m_CP_Notes += "PenBolus";
			}
			else if (resType.equals("MultiWave") || resType.equals("Extended Bolus Start"))
			{
				m_Ins = true;
				m_CP_EventType = "Meal Bolus";
				m_CP_Insulin  = new Double(Double.parseDouble(res.getM_Result()));

				// I didn;t manage to correctly get duration for multi-wave for Roche
				// So protect this from nulls that come back.
				// Less important to me now that Dawn's on an omnipod
				if (res.getM_Duration() != null && !res.getM_Duration().equals(""))
				{
					m_CP_Duration = new Double(Double.parseDouble(res.getM_Duration()));
				}
				m_CP_Notes += "PumpMultiWave";
			}

			else if (resType.equals("rewind.piston.rod"))
			{
				m_CP_EventType = "Insulin Cartridge Change";
			}

			// Added for Medtronic
			else if (resType.equals("Site Change"))
			{
				m_CP_EventType = "Site Change";
			}

			// Temp Basal where the start & stop are identified for us
			else if (resType.equals("Tmp Basal Start"))
			{
				m_CP_EventType = "Temp Basal";
				m_CP_Duration = new Double(Double.parseDouble(res.getM_Duration()));
				m_CP_Percent  = new Double(Double.parseDouble(res.getM_Result()));     // Temp Basal
			}
			else if (resType.equals("Tmp Basal Stop"))
			{
				// This type is not interpreted by Nightscout clients
				m_CP_EventType = "Temp Basal End";
			}

			// Basal rate provided (Diasend) but we convert to a start basal
			// The merge will set the duration and percent is supplied externally.
			else if (resType.equals("Basal") && 
					// Only if we are inferring temp basals
					PrefsNightScoutLoader.getInstance().isM_InferTempBasals())
			{
				m_CP_EventType = "Temp Basal";
				m_CP_Duration = new Double(0);
				m_CP_Percent  = new Double(0);
				m_TmpBasal = true;
			}


			// Getting an error as resType is blank for Medtronic now :-(
			else if (resType.substring(0,5).equals("alarm") ||
					resType.substring(0,5).equals("error"))
			{
				// Just store all the other types
				m_CP_EventType = "Announcement";

				m_CP_Notes += resType;
			}

			// Finally, append any notes from Raw data
			m_CP_Notes += (m_CP_Notes.length() > 0 ? " : " : "") + res.getM_Notes();
		}
		catch (Exception e) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+"> DBResult " +
					" Exception caught creating DBResult for Care Portal from Raw data: " + res.toString());
		}
		finally
		{
		}
	}

	public boolean pending()
	{
		boolean result = true; // Assume we're pending first

		if (m_BG && m_Carb && m_Ins) // We have a Meal bolus (BG, Carbs & Insulin)
		{
			result = false;
		}
		else if (m_Corr)  // We have a correction (BG & Insulin)
		{
			result = false;
		}
		else if (!m_BG && !m_Carb && !m_Ins) // We have neither of the above
		{
			result = false;
		}

		return result;
	}

	public boolean discard()
	{
		boolean result = false;

		if (m_CP_EventType.isEmpty())  // If we've not mapped resultType to eventType, then not interested
		{
			result = true;
		}
		return result;
	}


	public MergeResult merge(DBResult res)
	{
		MergeResult result = MergeResult.Merged;  // Assume that the result cannot be merged

		// 17 Apr 016
		// Try a big try
		try
		{

			// Compare time of current object with the merge.
			// If less than a minute then it could be a merge
			Date thisTime = this.getM_Time();
			Date resTime  = res.getM_Time();

			/*		long maxDiffBetweenSameMealEvent  = 1000 * 60 * 30; // Allow up to half an hour between BG, Carbs & Ins
		long maxDiffBetweenSameCorrection = 1000 * 60 * 5;  // Allow up to 5 mins between BG & Ins
			 */
			long maxDiffBetweenSameMealEvent  = 1000 * 60 * 
					// Get Preference Value now
					PrefsNightScoutLoader.getInstance().getM_MaxMinsBetweenSameMealEvent();
			long maxDiffBetweenSameCorrection = 1000 * 60 * 
					// Get Preference Value now
					PrefsNightScoutLoader.getInstance().getM_MaxMinsBetweenSameCorrectionEvent();

			long diffMillies = resTime.getTime() - thisTime.getTime();

			if ((diffMillies <= maxDiffBetweenSameCorrection) || (diffMillies <= maxDiffBetweenSameMealEvent && m_Carb))
			{
				// Add the notes from the other item with optional separator
				m_CP_Notes += (m_CP_Notes.length() > 0 ? " : " : "") + res.getM_Notes();

				// First, assume it's a Carb Correction as we are merging two results.
				String resType = res.getM_ResultType();

				if (resType.equals("BG") && !m_BG) // Last check in case results out of sequence
				{
					m_BG = true;
					m_CP_Glucose = new Double(Double.parseDouble(res.m_Result));
				}
				else if (resType.equals("Carbs") && !m_Carb)
				{
					m_Carb = true;
					m_CP_Carbs = new Double(Double.parseDouble(res.m_Result));
					// However, if we see a Carb then it must be a Meal Bolus instead
					m_CP_EventType = "Meal Bolus";

					// We can work out the Carbs time in Mins by assuming meal began at the time of first event
					m_CP_CarbsTime = (double)(diffMillies / (1000 * 60));
				}
				else if (resType.equals("Standard Bolus") && !m_Ins)
				{
					m_Ins = true;
					m_CP_Insulin = Double.parseDouble(res.m_Result);
				}
				else if (resType.equals("Pen Units")  && !m_Ins)
				{
					m_Ins = true;
					if (!m_Carb && m_BG)
					{
						m_Corr = true; // Allow a BG first then Ins
					}
					m_CP_EventType = "Correction Bolus";
					m_CP_Insulin = Double.parseDouble(res.getM_Result());
					m_CP_Notes += (m_CP_Notes.length() > 0 ? " : " : "") + "PenBolus";
				}

				else if (resType.equals("MultiWave") && !m_Ins)
				{
					m_Ins = true;
					if (!m_Carb && m_BG)
					{
						m_Corr = true; // Allow a BG first then Ins
					}
					m_CP_Insulin = Double.parseDouble(res.m_Result);
					m_CP_Notes += (m_CP_Notes.length() > 0 ? " : " : "") + "PumpMultiWave";
				}
				/*
				 * Looks like extended is not supported
				 */
				/* But let's store it as a NOTE for now
				 */
				else if (resType.equals("Extended Bolus Start"))
				{
					m_CP_Notes += (m_CP_Notes.length() > 0 ? " : " : "") + "Extended Bolus " + res.getM_Duration() + " Mins";
					m_CP_Duration = Double.parseDouble(res.getM_Duration());

					// If we already have insulin amount, then add the amounts together.
					if (m_Ins)
					{
						Double totIns = this.getM_CP_Insulin() + Double.parseDouble(res.getM_Result());

						m_CP_Insulin = totIns;

						m_CP_Notes += "(Insulin Combined. " + res.getM_Result() + "u Extended)";
					}
					else
					{
						m_CP_Insulin = Double.parseDouble(res.m_Result);
					}

					m_Ins = true;

				}

				// Check for duplicate.  Sometimes see this with Medtronic results
				else if ((resType.equals("BG") && m_BG && m_CP_Glucose.equals(Double.parseDouble(res.m_Result)) ||
						(resType.equals("Carbs") && m_Carb && m_CP_Carbs.equals(Double.parseDouble(res.m_Result))) ||
						(resType.equals("Standard Bolus") || resType.equals("Pen Units") || 
								resType.equals("MultiWave") || resType.equals("Extended Bolus Start") ) && m_Ins && m_CP_Insulin.equals(Double.parseDouble(res.m_Result)))
						)
				{
					result = MergeResult.Duplicate;
				}

				// David 14 Oct 2016
				// So the results are in close proximity, but we've not really merged anything
				else
				{
					// Return false so the current mergee gets converted to a candidate
					result = MergeResult.CantMerge;					
				}
				// David 14 Oct 2016
			}

			// Could be either side of a long running temp basal
			else if (m_TmpBasal == true && res.getM_ResultType().equals("Basal"))
			{
				// Get duration from time differences
				Double diffMilliesDbl = new Double(diffMillies);

				// Round to nearest whole number
				m_CP_Duration = Math.round(diffMilliesDbl / ( 60000.0 )) * 1.0; // Convert duration to minutes.

				m_TmpBasal = false;
			}

			else
			{
				// Check if we need to change the type first ...

				// BG & Insulin is a correction!
				if (m_BG && m_Ins && !m_Carb)
				{
					m_CP_EventType = "Correction Bolus";
				}
				// Result is outside what we expect, so reset previous pending flags
				// so this gets stored.
				m_BG = false;
				m_Carb = false;
				m_Ins = false;

				// Return false so the current mergee gets converted to a candidate
				result = MergeResult.TooDistant;
			}

		}
		catch (Exception e) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+"> DBResult " +
					" Exception caught merging DBResult for Care Portal from Raw data: " + res.toString()
					+ res.rawToString() + " DURATION is " + res.getM_Duration());
		}
		finally
		{
		}

		return result;
	}


	//	public boolean merge(DBResult res)
	//	{
	//		boolean result = true;  // Assume that the result is in close proximity
	//
	//		// 17 Apr 016
	//		// Try a big try
	//		try
	//		{
	//
	//			// Compare time of current object with the merge.
	//			// If less than a minute then it could be a merge
	//			Date thisTime = this.getM_Time();
	//			Date resTime  = res.getM_Time();
	//
	//			/*		long maxDiffBetweenSameMealEvent  = 1000 * 60 * 30; // Allow up to half an hour between BG, Carbs & Ins
	//		long maxDiffBetweenSameCorrection = 1000 * 60 * 5;  // Allow up to 5 mins between BG & Ins
	//			 */
	//			long maxDiffBetweenSameMealEvent  = 1000 * 60 * 
	//					// Get Preference Value now
	//					PrefsNightScoutLoader.getInstance().getM_MaxMinsBetweenSameMealEvent();
	//			long maxDiffBetweenSameCorrection = 1000 * 60 * 
	//					// Get Preference Value now
	//					PrefsNightScoutLoader.getInstance().getM_MaxMinsBetweenSameCorrectionEvent();
	//
	//			long diffMillies = resTime.getTime() - thisTime.getTime();
	//
	//			if ((diffMillies <= maxDiffBetweenSameCorrection) || (diffMillies <= maxDiffBetweenSameMealEvent && m_Carb))
	//			{
	//				// Add the notes from the other item with optional separator
	//				m_CP_Notes += (m_CP_Notes.length() > 0 ? " : " : "") + res.getM_Notes();
	//
	//				// First, assume it's a Carb Correction as we are merging two results.
	//				String resType = res.getM_ResultType();
	//
	//				if (resType.equals("BG") && !m_BG) // Last check in case results out of sequence
	//				{
	//					m_BG = true;
	//					m_CP_Glucose = new Double(Double.parseDouble(res.m_Result));
	//				}
	//				else if (resType.equals("Carbs") && !m_Carb)
	//				{
	//					m_Carb = true;
	//					m_CP_Carbs = new Double(Double.parseDouble(res.m_Result));
	//					// However, if we see a Carb then it must be a Meal Bolus instead
	//					m_CP_EventType = "Meal Bolus";
	//
	//					// We can work out the Carbs time in Mins by assuming meal began at the time of first event
	//					m_CP_CarbsTime = (double)(diffMillies / (1000 * 60));
	//				}
	//				else if (resType.equals("Standard Bolus") && !m_Ins)
	//				{
	//					m_Ins = true;
	//					m_CP_Insulin = Double.parseDouble(res.m_Result);
	//				}
	//				else if (resType.equals("Pen Units")  && !m_Ins)
	//				{
	//					m_Ins = true;
	//					if (!m_Carb && m_BG)
	//					{
	//						m_Corr = true; // Allow a BG first then Ins
	//					}
	//					m_CP_EventType = "Correction Bolus";
	//					m_CP_Insulin = Double.parseDouble(res.getM_Result());
	//					m_CP_Notes += (m_CP_Notes.length() > 0 ? " : " : "") + "PenBolus";
	//				}
	//
	//				else if (resType.equals("MultiWave") && !m_Ins)
	//				{
	//					m_Ins = true;
	//					if (!m_Carb && m_BG)
	//					{
	//						m_Corr = true; // Allow a BG first then Ins
	//					}
	//					m_CP_Insulin = Double.parseDouble(res.m_Result);
	//					m_CP_Notes += (m_CP_Notes.length() > 0 ? " : " : "") + "PumpMultiWave";
	//				}
	//				/*
	//				 * Looks like extended is not supported
	//				 */
	//				/* But let's store it as a NOTE for now
	//				 */
	//				else if (resType.equals("Extended Bolus Start"))
	//				{
	//					m_CP_Notes += (m_CP_Notes.length() > 0 ? " : " : "") + "Extended Bolus " + res.getM_Duration() + " Mins";
	//					m_CP_Duration = Double.parseDouble(res.getM_Duration());
	//
	//					// If we already have insulin amount, then add the amounts together.
	//					if (m_Ins)
	//					{
	//						Double totIns = this.getM_CP_Insulin() + Double.parseDouble(res.getM_Result());
	//
	//						m_CP_Insulin = totIns;
	//
	//						m_CP_Notes += "(Insulin Combined. " + res.getM_Result() + "u Extended)";
	//					}
	//					else
	//					{
	//						m_CP_Insulin = Double.parseDouble(res.m_Result);
	//					}
	//
	//					m_Ins = true;
	//
	//				}
	//				
	//				// David 14 Oct 2016
	//				// So the results are in close proximity, but we've not really merged anything
	//				else
	//				{
	//					// Return false so the current mergee gets converted to a candidate
	//					result = false;					
	//				}
	//				// David 14 Oct 2016
	//			}
	//
	//			// Could be either side of a long running temp basal
	//			else if (m_TmpBasal == true && res.getM_ResultType().equals("Basal"))
	//			{
	//				// Get duration from time differences
	//				Double diffMilliesDbl = new Double(diffMillies);
	//
	//				// Round to nearest whole number
	//				m_CP_Duration = Math.round(diffMilliesDbl / ( 60000.0 )) * 1.0; // Convert duration to minutes.
	//
	//				m_TmpBasal = false;
	//			}
	//
	//			else
	//			{
	//				// Check if we need to change the type first ...
	//
	//				// BG & Insulin is a correction!
	//				if (m_BG && m_Ins && !m_Carb)
	//				{
	//					m_CP_EventType = "Correction Bolus";
	//				}
	//				// Result is outside what we expect, so reset previous pending flags
	//				// so this gets stored.
	//				m_BG = false;
	//				m_Carb = false;
	//				m_Ins = false;
	//
	//				// Return false so the current mergee gets converted to a candidate
	//				result = false;
	//			}
	//
	//		}
	//		catch (Exception e) 
	//		{
	//			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+"> DBResult " +
	//					" Exception caught merging DBResult for Care Portal from Raw data: " + res.toString()
	//					+ res.rawToString() + " DURATION is " + res.getM_Duration());
	//		}
	//		finally
	//		{
	//		}
	//
	//		return result;
	//	}


	public BasicDBObject createNightScoutObject()
	{
		BasicDBObject result = new BasicDBObject("eventType", m_CP_EventType);

		appendToDoc(result, "glucose", m_CP_Glucose);
		if (m_CP_Glucose != null)
		{
			appendToDoc(result, "glucoseType", "Finger");
			appendToDoc(result, "units", 
					(PrefsNightScoutLoader.getBGUnitMultiplier() == 1) ? "mmol" : "mgDl");
		}
		appendToDoc(result, "carbs", m_CP_Carbs);
		appendToDoc(result, "insulin", m_CP_Insulin);
		appendToDoc(result, "preBolus", m_CP_CarbsTime);
		appendToDoc(result, "duration", m_CP_Duration);
		appendToDoc(result, "percent", m_CP_Percent);
		appendToDoc(result, "profile", m_CP_BasalValue);
		appendToDoc(result, "notes", m_CP_Notes);

		appendToDoc(result, m_determinantField, m_CP_EnteredBy);
		//		appendToDoc(result, "enteredBy", m_CP_EnteredBy);

		Date utcTime = new Date(CommonUtils.toUTC(m_Time.getTime(), CommonUtils.locTZ));
		//		appendToDoc(result, "created_at", m_CP_EventTime);
		appendToDoc(result, "created_at", utcTime);  // Use the UTC time instead

		return result;
	}

	static public void appendToDoc(BasicDBObject doc, String label, String value)
	{
		if (value.length() > 0)
		{
			doc.append(label, value);
		}
	}

	static public void appendToDoc(BasicDBObject doc, String label, Double value)
	{
		if (value != null)
		{
			doc.append(label, doubleIsInteger(value) ? value.longValue() : value.doubleValue());
		}
	}

	static public void appendToDoc(BasicDBObject doc, String label, int value)
	{
		doc.append(label, value);
	}

	static public void appendToDoc(BasicDBObject doc, String label, Date value)
	{
		if (value != null)
		{
			// 16 Jun 2016
			// Feedback from Mel in Australia that times are shifted
			// Realise that I need to convert from local to UTC times!
			//			final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'", Locale.ENGLISH);
			//			final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH); // Try something different

			final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			//			Date utcValue = new Date(CommonUtils.toUTC(value.getTime(), CommonUtils.locTZ)); 
			//			String dVal = format.format(utcValue);

			String dVal = format.format(value);

			doc.append(label, dVal);
		}

	}

	public boolean isValid()
	{
		return true;
	}

	public boolean isReportRange()
	{
		return false;
	}


	/**
	 * @return the m_Year
	 */
	public int getM_Year() {
		return m_Year;
	}

	/**
	 * @param m_Year the m_Year to set
	 */
	public void setM_Year(int m_Year) {
		this.m_Year = m_Year;
	}

	/**
	 * @return the m_Month
	 */
	public int getM_Month() {
		return m_Month;
	}

	/**
	 * @param m_Month the m_Month to set
	 */
	public void setM_Month(int m_Month) {
		this.m_Month = m_Month;
	}

	/**
	 * @return the m_Day
	 */
	public int getM_Day() {
		return m_Day;
	}

	/**
	 * @param m_Day the m_Day to set
	 */
	public void setM_Day(int m_Day) {
		this.m_Day = m_Day;
	}

	/**
	 * @return the m_DayName
	 */
	public String getM_DayName() {
		return m_DayName;
	}

	/**
	 * @param m_DayName the m_DayName to set
	 */
	public void setM_DayName(String m_DayName) {
		this.m_DayName = m_DayName;
	}

	/**
	 * @return the m_Time
	 */
	public Date getM_Time() {
		return m_Time;
	}

	/**
	 * @param m_Time the m_Time to set
	 */
	public void setM_Time(Date m_Time) {
		this.m_Time = m_Time;
	}

	/**
	 * @return the m_TimeSlot
	 */
	public String getM_TimeSlot() {
		return m_TimeSlot;
	}

	/**
	 * @param m_TimeSlot the m_TimeSlot to set
	 */
	public void setM_TimeSlot(String m_TimeSlot) {
		this.m_TimeSlot = m_TimeSlot;
	}

	/**
	 * @return the m_Result
	 */
	public String getM_Result() {
		return m_Result;
	}

	/**
	 * @param m_Result the m_Result to set
	 */
	public void setM_Result(String m_Result) {
		this.m_Result = m_Result;
	}

	/**
	 * @return the m_ResultType
	 */
	public String getM_ResultType() {
		return m_ResultType;
	}

	/**
	 * @param m_ResultType the m_ResultType to set
	 */
	public void setM_ResultType(String m_ResultType) {
		this.m_ResultType = m_ResultType;
	}

	/**
	 * @return the m_MealType
	 */
	public String getM_MealType() {
		return m_MealType;
	}

	/**
	 * @param m_MealType the m_MealType to set
	 */
	public void setM_MealType(String m_MealType) {
		this.m_MealType = m_MealType;
	}

	/**
	 * @return the m_Duration
	 */
	public String getM_Duration() {
		return m_Duration;
	}

	/**
	 * @param m_Duration the m_Duration to set
	 */
	public void setM_Duration(String m_Duration) {
		this.m_Duration = m_Duration;
	}

	/**
	 * @return the m_Notes
	 */
	public synchronized String getM_Notes() {
		return m_Notes;
	}


	/**
	 * @param m_Notes the m_Notes to set
	 */
	public synchronized void setM_Notes(String m_Notes) {
		this.m_Notes = m_Notes;
	}


	/**
	 * @return the m_ID
	 */
	public String getM_ID() {
		return m_ID;
	}

	/**
	 * @param m_ID the m_ID to set
	 */
	public void setM_ID(String m_ID) {
		this.m_ID = m_ID;
	}

	/**
	 * @return the m_CP_EventType
	 */
	public String getM_CP_EventType() {
		return m_CP_EventType;
	}

	/**
	 * @param m_CP_EventType the m_CP_EventType to set
	 */
	public void setM_CP_EventType(String m_CP_EventType) {
		this.m_CP_EventType = m_CP_EventType;
	}

	/**
	 * @return the m_CP_Glucose
	 */
	public Double getM_CP_Glucose() {
		return m_CP_Glucose;
	}

	/**
	 * @param m_CP_Glucose the m_CP_Glucose to set
	 */
	public void setM_CP_Glucose(double m_CP_Glucose) {
		this.m_CP_Glucose = m_CP_Glucose;
	}

	/**
	 * @return the m_CP_Carbs
	 */
	public Double getM_CP_Carbs() {
		return m_CP_Carbs;
	}

	/**
	 * @param m_CP_Carbs the m_CP_Carbs to set
	 */
	public void setM_CP_Carbs(double m_CP_Carbs) {
		this.m_CP_Carbs = m_CP_Carbs;
	}

	/**
	 * @return the m_CP_Insulin
	 */
	public Double getM_CP_Insulin() {
		return m_CP_Insulin;
	}

	/**
	 * @param m_CP_Insulin the m_CP_Insulin to set
	 */
	public void setM_CP_Insulin(double m_CP_Insulin) {
		this.m_CP_Insulin = m_CP_Insulin;
	}

	/**
	 * @return the m_CP_CarbsTime
	 */
	public Double getM_CP_CarbsTime() {
		return m_CP_CarbsTime;
	}

	/**
	 * @param m_CP_CarbsTime the m_CP_CarbsTime to set
	 */
	public void setM_CP_CarbsTime(Double m_CP_CarbsTime) {
		this.m_CP_CarbsTime = m_CP_CarbsTime;
	}

	/**
	 * @return the m_CP_Duration
	 */
	public Double getM_CP_Duration() {
		return m_CP_Duration;
	}

	/**
	 * @param m_CP_Duration the m_CP_Duration to set
	 */
	public void setM_CP_Duration(double m_CP_Duration) {
		this.m_CP_Duration = m_CP_Duration;
	}

	/**
	 * @return the m_CP_Percent
	 */
	public Double getM_CP_Percent() {
		return m_CP_Percent;
	}

	/**
	 * @param m_CP_Percent the m_CP_Percent to set
	 */
	public void setM_CP_Percent(double m_CP_Percent) {
		this.m_CP_Percent = m_CP_Percent;
	}

	/**
	 * @return the m_CP_BasalValue
	 */
	public Double getM_CP_BasalValue() {
		return m_CP_BasalValue;
	}

	/**
	 * @param m_CP_BasalValue the m_CP_BasalValue to set
	 */
	public void setM_CP_BasalValue(double m_CP_BasalValue) {
		this.m_CP_BasalValue = m_CP_BasalValue;
	}

	/**
	 * @return the m_CP_Notes
	 */
	public String getM_CP_Notes() {
		return m_CP_Notes;
	}

	/**
	 * @param m_CP_Notes the m_CP_Notes to set
	 */
	public void setM_CP_Notes(String m_CP_Notes) {
		this.m_CP_Notes = m_CP_Notes;
	}

	/**
	 * @return the m_CP_EnteredBy
	 */
	public String getM_CP_EnteredBy() {
		return m_CP_EnteredBy;
	}

	/**
	 * @param m_CP_EnteredBy the m_CP_EnteredBy to set
	 */
	public void setM_CP_EnteredBy(String m_CP_EnteredBy) {
		this.m_CP_EnteredBy = m_CP_EnteredBy;
	}

	/**
	 * @return the m_CP_EventTime
	 */
	public String getM_CP_EventTime() {
		return m_CP_EventTime;
	}

	/**
	 * @param m_CP_EventTime the m_CP_EventTime to set
	 */
	public void setM_CP_EventTime(String m_CP_EventTime) {
		this.m_CP_EventTime = m_CP_EventTime;
	}

	/**
	 * @return the m_EpochMillies
	 */
	@Override
	public long getM_EpochMillies() {
		return m_EpochMillies;
	}

	/**
	 * @param m_EpochMillies the m_EpochMillies to set
	 */
	public void setM_EpochMillies(long m_EpochMillies) {
		this.m_EpochMillies = m_EpochMillies;
	}

	/**
	 * @return the m_DataSource
	 */
	public String getM_DataSource() {
		return m_DataSource;
	}

	/**
	 * @param m_DataSource the m_DataSource to set
	 */
	public void setM_DataSource(String m_DataSource) {
		this.m_DataSource = m_DataSource;
	}


	/**
	 * @return the m_determinantField
	 */
	public static String getM_determinantField() {
		return m_determinantField;
	}


	/**
	 * @return the m_determinantValue
	 */
	public static String getM_determinantValue() {
		return m_determinantValue;
	}


	/**
	 * @return the m_TreatmentDayName
	 */
	public synchronized String getM_TreatmentDayName() {
		return m_TreatmentDayName;
	}


	/**
	 * @param m_TreatmentDayName the m_TreatmentDayName to set
	 */
	public synchronized void setM_TreatmentDayName(String m_TreatmentDayName) {
		this.m_TreatmentDayName = m_TreatmentDayName;
	}


	/**
	 * @return the m_TreatmentTime
	 */
	public synchronized String getM_TreatmentTime() {
		return m_TreatmentTime;
	}


	/**
	 * @param m_TreatmentTime the m_TreatmentTime to set
	 */
	public synchronized void setM_TreatmentTime(String m_TreatmentTime) {
		this.m_TreatmentTime = m_TreatmentTime;
	}


	/**
	 * @return the m_TreatmentDate
	 */
	public synchronized String getM_TreatmentDate() {
		return m_TreatmentDate;
	}


	/**
	 * @param m_TreatmentDate the m_TreatmentDate to set
	 */
	public synchronized void setM_TreatmentDate(String m_TreatmentDate) {
		this.m_TreatmentDate = m_TreatmentDate;
	}


	/**
	 * @param m_CP_Glucose the m_CP_Glucose to set
	 */
	public synchronized void setM_CP_Glucose(Double m_CP_Glucose) {
		this.m_CP_Glucose = m_CP_Glucose;
	}


	/**
	 * @param m_CP_Carbs the m_CP_Carbs to set
	 */
	public synchronized void setM_CP_Carbs(Double m_CP_Carbs) {
		this.m_CP_Carbs = m_CP_Carbs;
	}


	/**
	 * @param m_CP_Insulin the m_CP_Insulin to set
	 */
	public synchronized void setM_CP_Insulin(Double m_CP_Insulin) {
		this.m_CP_Insulin = m_CP_Insulin;
	}


	/**
	 * @param m_CP_Duration the m_CP_Duration to set
	 */
	public synchronized void setM_CP_Duration(Double m_CP_Duration) {
		this.m_CP_Duration = m_CP_Duration;
	}


	/**
	 * @param m_CP_Percent the m_CP_Percent to set
	 */
	public synchronized void setM_CP_Percent(Double m_CP_Percent) {
		this.m_CP_Percent = m_CP_Percent;
	}


	/**
	 * @param m_CP_BasalValue the m_CP_BasalValue to set
	 */
	public synchronized void setM_CP_BasalValue(Double m_CP_BasalValue) {
		this.m_CP_BasalValue = m_CP_BasalValue;
	}

	/**
	 * @return the m_ProximityPossibleDuplicate
	 */
	public synchronized boolean isM_ProximityPossibleDuplicate() 
	{
		m_ProximityPossibleDuplicate = m_CP_EnteredBy.length() > 0 && m_CP_EnteredBy.contains("-PROXIMITY") ?
				true : false;
		return m_ProximityPossibleDuplicate;
	}

	/**
	 * @param m_ProximityPossibleDuplicate the m_ProximityPossibleDuplicate to set
	 */
	public synchronized void setM_ProximityPossibleDuplicate(boolean m_ProximityMatch) {
		this.m_ProximityPossibleDuplicate = m_ProximityMatch;

		if (m_CP_EnteredBy.length() > 0 &&
				!m_CP_EnteredBy.contains("-PROXIMITY"))
		{
			m_CP_EnteredBy += "-PROXIMITY";
		}
	}

	/**
	 * @return the m_ProximityCheck
	 */
	public static synchronized boolean isM_ProximityCheck() {
		return m_ProximityCheck;
	}

	/**
	 * @param m_ProximityCheck the m_ProximityCheck to set
	 */
	public static synchronized void setM_ProximityCheck(boolean m_ProximityCheck) {
		DBResult.m_ProximityCheck = m_ProximityCheck;
	}

	/**
	 * @return the m_ProximityCheckSecondPass
	 */
	public static synchronized boolean isM_ProximityCheckSecondPass() {
		return m_ProximityCheckSecondPass;
	}

	/**
	 * @param m_ProximityCheckSecondPass the m_ProximityCheckSecondPass to set
	 */
	public static synchronized void setM_ProximityCheckSecondPass(boolean m_ProximityCheckSecondPass) {
		DBResult.m_ProximityCheckSecondPass = m_ProximityCheckSecondPass;
	}
}
