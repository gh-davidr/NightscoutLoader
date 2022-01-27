package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
//import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.bson.Document;

//import javax.swing.JFormattedTextField.AbstractFormatter;

import com.mongodb.DBObject;
import control.MyLogger;
import control.PrefsNightScoutLoader;

public class CommonUtils 
{
	protected static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	public static final TimeZone utcTZ = TimeZone.getTimeZone("UTC");
	public static final TimeZone locTZ = TimeZone.getDefault();

	// Used for thread synchronisation and preventing concurrentnodification of iterator for date formats
	private static Object  m_LockInitialization = new Object();

	// Simple Date Format is not thread safe.  Rather than switching to better date class, simply use this
	// to synchronise calls
	private static Object  m_LockSDF = new Object();



	// Idea 30 Aug 2021
	// Build a hashmap of different timeformats to save creating them multiple times when needed during loads
	//
	private static HashMap<String, SimpleDateFormat> DATE_FORMAT_HASHMAP = new HashMap<String, SimpleDateFormat>();

	// Similar idea but for inferring from dates supplied
	//	private static HashMap<Pattern, String> REGEXP_FORMAT_HASHMAP = new HashMap<Pattern, String>();


	// 2-d Array holding Regexp Patterns as a key and corresponding SimpleDateFormat formats as value
	// These are all the most common formats seen coming out from Nightscout
	private static String m_RegexpDatePatternArray[][] = {
			{"^([0-9]){4,4}-([0-9]){2,2}-([0-9]){2,2}T([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}.([0-9]){3,3}Z$",  "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"},
			{"^([0-9]){4,4}-([0-9]){2,2}-([0-9]){2,2}T([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}.([0-9]){2,2}Z$",  "yyyy-MM-dd'T'HH:mm:ss.SS'Z'"},
			{"^([0-9]){4,4}-([0-9]){2,2}-([0-9]){2,2}T([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}.([0-9]){1,1}Z$",  "yyyy-MM-dd'T'HH:mm:ss.S'Z'"},
			{"^([0-9]){2,2}/([a-zA-Z]){3,3}/([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",             "dd/MMM/yyyy HH:mm:ss"},
			{"^([0-9]){2,2}-([a-zA-Z]){3,3}-([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",             "dd-MMM-yyyy HH:mm:ss"},
			{"^([0-9]){2,2}/([0-9]){2,2}/([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",                "dd/MM/yyyy HH:mm:ss"},
			{"^([0-9]){2,2}-([0-9]){2,2}-([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",                "dd-MM-yyyy HH:mm:ss"},
			{"^([0-9]){2,2}/([0-9]){2,2}/([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}$",                             "dd/MM/yyyy HH:mm"},
			{"^([0-9]){2,2}-([0-9]){2,2}-([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}$",                             "dd-MM-yyyy HH:mm"},
			{"^([0-9]){4,4}-([0-9]){2,2}-([0-9]){2,2}T([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",                "yyyy-MM-dd HH:mm:ss"},
			{"^([0-9]){4,4}/([0-9]){2,2}/([0-9]){2,2}T([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",                "yyyy/MM/dd HH:mm:ss"},
	};


	private static ArrayList<DatePatternFormat> m_DatePatternFormatList = new ArrayList<DatePatternFormat>();
	private static DatePatternFormat[]          m_DatePatternFormatArray = new DatePatternFormat[m_RegexpDatePatternArray.length];
	private static DatePatternFormat            m_LastUsedPatternFormat = null;	
	private static Boolean                      m_DatePatternFormatArrayInitializedBoolean = false;

	// Try and optimise performance during date format inference and reduce some iterations when same date format gets used.
	//	private static Pattern lAST_USED_PATTERN = null;
	//	private static String  lAST_USED_PATTERN_FORMAT = null;


	// Used for some tests
	//	public static final TimeZone locTZ = TimeZone.getTimeZone("Australia/Darwin");
	//	public static final TimeZone locTZ = TimeZone.getTimeZone("Etc/GMT-10");

	public static long toLocalTime(long time, TimeZone to) 
	{
		//		return convertTime(time, utcTZ, to);
		return convertTime(time, utcTZ, getTimezoneFromPrefs());
	}

	public static long toUTC(long time) 
	{
		//		return convertTime(time, locTZ, utcTZ);
		return convertTime(time, getTimezoneFromPrefs(), utcTZ);
	}

	public static TimeZone getTimezoneFromPrefs()
	{
		TimeZone result = null;
		String prefTimeZone = PrefsNightScoutLoader.getInstance().getM_Timezone();

		// If default, then use local
		if (prefTimeZone.equals(PrefsNightScoutLoader.getInstance().getDef_M_Timezone()))
		{
			result = locTZ;
		}

		// Take what's set and use directly
		else
		{
			Pattern positiveAdj = Pattern.compile("GMT \\+([0-9]*)");
			Pattern negativeAdj = Pattern.compile("GMT \\-([0-9]*)");
			Matcher positiveMatcher = positiveAdj.matcher(prefTimeZone);
			Matcher negativeMatcher = negativeAdj.matcher(prefTimeZone);

			int offset = 0;

			if (positiveMatcher.find())
			{
				String adj = positiveMatcher.group(1);
				offset = 1000 * 3600 * Integer.parseInt(adj);
			}
			else if (negativeMatcher.find())
			{
				String adj = negativeMatcher.group(1);
				offset = 1000 * 3600 * Integer.parseInt(adj) * (-1);
			}

			result = TimeZone.getTimeZone("UTC");
			result.setRawOffset(offset);
		}

		return result;
	}


	public static long toUTC(long time, TimeZone from) 
	{
		return convertTime(time, from, utcTZ);
	}

	public static long convertTime(long time, TimeZone from, TimeZone to) 
	{
		return time + getTimeZoneOffset(time, from, to);
	}

	private static long getTimeZoneOffset(long time, TimeZone from, TimeZone to) 
	{
		int fromOffset = from.getOffset(time);
		int toOffset = to.getOffset(time);

		return getTimeZoneOffset(time, fromOffset, toOffset);
	}

	private static long getTimeZoneOffset(long time, int fromOffset, int toOffset) 
	{
		int diff = 0;

		if (fromOffset >= 0)
		{
			if (toOffset > 0)
			{
				toOffset = -1*toOffset;
			} 
			else 
			{
				toOffset = Math.abs(toOffset);
			}
			diff = (fromOffset+toOffset)*-1;
		}
		else 
		{
			if (toOffset <= 0)
			{
				toOffset = -1*Math.abs(toOffset);
			}
			diff = (Math.abs(fromOffset)+toOffset);
		}
		return diff;
	}


	// Used for Diasend temp basal identifications
	public static boolean isTimeAfter(String time, Date dateTime) throws ParseException
	{
		boolean result = false;
		Date compDate = applyTimeToDate(time, dateTime);

		result = compDate.after(dateTime) ? true : false;

		return result;
	}


	public static Date applyTimeToDate(String time, Date date)
	{
		Date resultDate = null;

		final DateFormat df  = CommonUtils.getSimpleDateFormat("dd/MM/yyyy");  // new SimpleDateFormat("dd/MM/yyyy");
		String timeOnDate  = new String(df.format(date)) +  " " + time;

		try {
			resultDate = convertNSZDateString(timeOnDate);
		} catch (ParseException e) {
			m_Logger.log(Level.WARNING, "applyTimeToDate Caught exception parsing date " + timeOnDate);
		}

		return resultDate;
	}

	// Used for Diasend temp basal identifications
	public static boolean isTimeAfter_old(String time, Date dateTime) throws ParseException
	{
		boolean result = false;

		// Convert dateTime into a known string
		final DateFormat df  = CommonUtils.getSimpleDateFormat("dd/MM/yyyy");  // new SimpleDateFormat("dd/MM/yyyy");
		final DateFormat dtf = CommonUtils.getSimpleDateFormat("dd/MM/yyyy HH:mm");  // new SimpleDateFormat("dd/MM/yyyy HH:mm");

		String dateTimeStr = new String(dtf.format(dateTime));
		// Now append space & time ;-)
		String timeOnDate  = new String(df.format(dateTime)) +  " " + time;

		Date baseDate = convertDateString(dateTimeStr, "dd/MM/yyyy HH:mm");
		Date compDate = convertDateString(timeOnDate,  "dd/MM/yyyy HH:mm");

		result = compDate.after(baseDate) ? true : false;

		return result;
	}

	// Used for Diasend temp basal identifications
	public static boolean isTimeBefore(String time, Date dateTime) throws ParseException
	{
		boolean result = isTimeAfter(time, dateTime) ? false : true;

		return result;
	}

	// Used for Analyzer bedtime checks
	// Between means start <= time < end
	public static boolean isTimeBetween(String startTime, String endTime, Date dateTime) throws ParseException
	{
		boolean result = false;

		// Convert dateTime into a known string
		final DateFormat df  = CommonUtils.getSimpleDateFormat("dd/MM/yyyy");  // new SimpleDateFormat("dd/MM/yyyy");
		String startTimeOnDate  = new String(df.format(dateTime)) +  " " + startTime;
		String endTimeOnDate    = new String(df.format(dateTime)) +  " " + endTime;

		Date startCompDate = null;
		Date endCompDate   = null;

		try {
			startCompDate = convertNSZDateString(startTimeOnDate);
		} catch (ParseException e) {
			m_Logger.log(Level.WARNING, "applyTimeToDate Caught exception parsing date " + startTimeOnDate);
		}
		try {
			endCompDate = convertNSZDateString(endTimeOnDate);
		} catch (ParseException e) {
			m_Logger.log(Level.WARNING, "applyTimeToDate Caught exception parsing date " + endTimeOnDate);
		}


		result = ((startCompDate.before(dateTime) || startCompDate.equals(dateTime)) &&
				(endCompDate.after(dateTime)))
				? true : false;

		return result;
	}


	// Used for Analyzer bedtime checks
	// Between means start <= time < end
	public static boolean isTimeBetween_old(String startTime, String endTime, Date dateTime) throws ParseException
	{
		boolean result = false;

		// Convert dateTime into a known string
		final DateFormat df  = CommonUtils.getSimpleDateFormat("dd/MM/yyyy");  // new SimpleDateFormat("dd/MM/yyyy");
		final DateFormat dtf = CommonUtils.getSimpleDateFormat("dd/MM/yyyy HH:mm");  // new SimpleDateFormat("dd/MM/yyyy HH:mm");

		String dateTimeStr = new String(dtf.format(dateTime));
		// Now append space & time ;-)
		String startTimeOnDate  = new String(df.format(dateTime)) +  " " + startTime;
		String endTimeOnDate    = new String(df.format(dateTime)) +  " " + endTime;

		Date baseDate = convertDateString(dateTimeStr, "dd/MM/yyyy HH:mm");
		Date startCompDate = convertDateString(startTimeOnDate,  "dd/MM/yyyy HH:mm");
		Date endCompDate   = convertDateString(endTimeOnDate,  "dd/MM/yyyy HH:mm");

		result = ((startCompDate.before(baseDate) || startCompDate.equals(baseDate)) &&
				(endCompDate.after(baseDate)))
				? true : false;

		return result;
	}


	public static String safeIfNull(String par)
	{
		String result = new String( par == null ? "" : par );
		return result;
	}

	public static Double safeIfNull(Double par)
	{
		Double result = Double.valueOf( par == null ? 0.0 : par );
		return result;
	}

	public static Integer safeIfNull(Integer par)
	{
		Integer result = Integer.valueOf( par == null ? 0 : par );
		return result;
	}

	// Used for Analyzer bedtime checks
	// Between means start <= time < end
	public static boolean isTimeBetween(Date startTime, Date endTime, Date dateTime)
	{
		boolean result = false;

		result = ((startTime.before(dateTime) || startTime.equals(dateTime)) &&
				(endTime.after(dateTime)))
				? true : false;

		return result;
	}


	public static boolean isDateTheSame(Date d1, Date d2)
	{
		boolean result = false;
		final DateFormat df  = CommonUtils.getSimpleDateFormat("dd/MM/yyyy");  // new SimpleDateFormat("dd/MM/yyyy");
		String d1Str = new String(df.format(d1));
		String d2Str = new String(df.format(d2));

		result = d1Str.equals(d2Str);

		return result;
	}

	public static Date addDaysToDate(Date dt, int numDays)
	{
		Date result = new Date(0);

		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.DATE, numDays); // Add one day
		result = c.getTime();

		return result;
	}

	public static Date addMinsToDate(Date dt, int numMins)
	{
		Date result = new Date(0);

		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.MINUTE, numMins); // Add one day
		result = c.getTime();

		return result;
	}

	public static int get24Hour(Date dt)
	{
		int result = 0;

		Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
		calendar.setTime(dt);   // assigns calendar to given date 
		result = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
		//		calendar.get(Calendar.HOUR);        // gets hour in 12h format
		//		calendar.get(Calendar.MONTH);       // gets month number, NOTE this is zero based!

		return result;
	}

	public static Date setDateToParticularHour(Date dt, int hour)
	{
		Date result = new Date(0);

		Calendar cal = Calendar.getInstance();       // get calendar instance
		cal.setTime(dt);                             // set cal to date
		cal.set(Calendar.HOUR_OF_DAY, hour);         // set hour to midnight
		cal.set(Calendar.MINUTE, 0);                 // set minute in hour
		cal.set(Calendar.SECOND, 0);                 // set second in minute
		cal.set(Calendar.MILLISECOND, 0);            // set millis in second
		result = cal.getTime();                      // actually computes the new Date

		return result;
	}

	public static int getMinutesFromDate(Date dt)
	{
		int result = 0;

		Calendar cal = Calendar.getInstance();       // get calendar instance
		cal.setTime(dt);                             // set cal to date

		result = cal.get(Calendar.MINUTE);
		return result;
	}

	public static long timeDiffInMinutes(Date d1, Date d2)
	{
		long result = 0;

		long diffMillies = d1.getTime() - d2.getTime();

		result = diffMillies / (1000 * 60);

		return result;
	}

	// Copied from DBResultNightscout 01 Dec 2016

	/*	
	public static Date convertNSZDateString(String dateStr) throws ParseException
	{ 
		final String z     = new String("Z");
		final String t     = new String("T");
		final String pls   = new String("+");
		final String am    = new String("am");
		final String pm    = new String("pm");
		final String slash = new String("/");
		final String dash  = new String("-");
		if (dateStr.contains(z))
		{
			Date result = convertDateString(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			return result;
		}
		else if (dateStr.contains(pls))
		{
			Date result = convertDateString(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'+000'");
			return result;
		}
		else if (dateStr.contains(slash) && (dateStr.contains(am) || dateStr.contains(pm)))
		{
			Date result = convertDateString(dateStr, "dd/MM/yyyy HH:mm:ss aa");
			return result;			
		}
		else if (dateStr.contains(t))
		{
			Date result = convertDateString(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS");
			return result;
		}
		else if (dateStr.contains(dash))
		{
			Date result = convertDateString(dateStr, "dd-MM-YY HH:mm:ss");
			return result;
		}
		else
		{
			Date result = new Date(0);
			return result;
		}

	}

	 */
	public static String convertNSZDateString(Date date) throws ParseException
	{ 
		String result = convertDateString(date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return result;
	}
	/*	
	public static Date convertNSDateString(String dateStr) throws ParseException
	{
		Date result = convertDateString(dateStr, "yyyy-MM-dd HH:mm:ss.S");
		return result;
	}

	public static Date convertDateString(String dateStr, String format) throws ParseException
	{
		Date result = new Date(0);
		final DateFormat nsformat   = new SimpleDateFormat(format, Locale.ENGLISH);
		if (dateStr.length() > 0)
		{
			result = nsformat.parse(dateStr);  
		}

		return result;
	}

	 */
	public static String convertDateString(Date date, String format) throws ParseException
	{
		String result = new String("");
		final DateFormat nsformat   = CommonUtils.getSimpleDateFormat(format);  // new SimpleDateFormat(format, Locale.ENGLISH);
		result = nsformat.format(date);

		return result;
	}

	public static Date getFieldDate(DBObject rs, String fieldName)
	{
		Date result = new Date(0);
		if (rs.containsField(fieldName))
		{
			String str = new String((String)rs.get(fieldName));
			try
			{
				result = convertNSZDateString(str);
			} 
			catch (ParseException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;		
	}

	public static String truncatedFileName(String fullPath)
	{
		String result = new String();

		// Return filename in form ".../xxxx.yyy" for convenient display

		// http://stackoverflow.com/questions/11769555/java-regular-expression-to-match-a-backslash-followed-by-a-quote
		Pattern pattern = Pattern.compile(".*\\\\([^\\\\]*)");
		Matcher matcher = pattern.matcher(fullPath);

		if (matcher.find())
		{
			String fileName = matcher.group(1);

			result = "...\\" + fileName;
		}

		return result;
	}


	public static String getIDStr(DBObject rs, String fieldName)
	{
		String result = new String();
		if (rs.containsField(fieldName))
		{
			result = rs.get(fieldName).toString();
		}
		return result;		
	}


	public static String getFieldStr(DBObject rs, String fieldName)
	{
		String result = new String();
		if (rs.containsField(fieldName))
		{
			result = (String)rs.get(fieldName);
		}
		return result;		
	}

	public static Double getFieldDouble(DBObject rs, String fieldName)
	{
		Double result = null;
		if (rs.containsField(fieldName) && rs.get(fieldName).toString().length() > 0)
		{
			try
			{
				result = Double.valueOf(((Number)rs.get(fieldName)).doubleValue());
			}
			catch(Exception e)
			{
				m_Logger.log(Level.WARNING, "DBResultNightScout Caught exception parsing field " + fieldName + " number "+e.toString() + rs.toString());
			}
		}
		return result;		
	}

	public static int getFieldInt(DBObject rs, String fieldName)
	{
		int result = 0;
		if (rs.containsField(fieldName) && rs.get(fieldName).toString().length() > 0)
		{
			result = (int)rs.get(fieldName);
		}
		return result;		
	}

	public static long getFieldLong(DBObject rs, String fieldName)
	{
		long result = 0;
		if (rs.containsField(fieldName) && rs.get(fieldName).toString().length() > 0)
		{
			result = (long)rs.get(fieldName);
		}
		return result;		
	}


	public static String getIDStr(Document rs, String fieldName)
	{
		String result = new String();
		if (rs.containsKey(fieldName))
		{
			result = rs.get(fieldName).toString();
		}
		return result;
	}

	public static String getFieldStr(Document rs, String fieldName)
	{
		String result = new String();
		if (rs.containsKey(fieldName))
		{
			result = (String)rs.get(fieldName);
		}
		return result;		
	}

	public static Double getFieldDouble(Document rs, String fieldName)
	{
		return getFieldDouble(rs, fieldName, true);
	}

	public static int getFieldInt(Document rs, String fieldName)
	{
		return getFieldInt(rs, fieldName, true);
	}

	public static long getFieldLong(Document rs, String fieldName)
	{
		return getFieldLong(rs, fieldName, true);
	}

	public static Double getFieldDouble(Document rs, String fieldName, Boolean warn)
	{
		Double result = null;
		if (rs.containsKey(fieldName) && rs.get(fieldName).toString().length() > 0)
		{
			try
			{
				result = Double.valueOf(((Number)rs.get(fieldName)).doubleValue());
			}
			catch(Exception e)
			{
				if (warn)
					m_Logger.log(Level.WARNING, "DBResultNightScout Caught exception parsing Double field " + fieldName + " number "+e.toString() + rs.toString());
			}
		}
		return result;		
	}

	public static int getFieldInt(Document rs, String fieldName, Boolean warn)
	{
		int result = 0;
		if (rs.containsKey(fieldName) && rs.get(fieldName).toString().length() > 0)
		{	
			try
			{
				Integer resInteger = Integer.valueOf(((Number)rs.get(fieldName)).intValue());
				result = resInteger.intValue();
			}
			catch(Exception e)
			{
				if (warn)
					m_Logger.log(Level.WARNING, "DBResultNightScout Caught exception parsing Integer field " + fieldName + " number "+e.toString() + rs.toString());
			}
		}
		return result;		
	}

	public static long getFieldLong(Document rs, String fieldName, Boolean warn)
	{
		long result = 0;
		if (rs.containsKey(fieldName) && rs.get(fieldName).toString().length() > 0)
		{
			try
			{
				Long resLong = Long.valueOf(((Number)rs.get(fieldName)).longValue());
				result = resLong.longValue();
			}
			catch(Exception e)
			{
				if (warn)
					m_Logger.log(Level.WARNING, "DBResultNightScout Caught exception parsing Long field " + fieldName + " number "+e.toString() + rs.toString());
			}
		}
		return result;		
	}


	public static Document getFieldDocument(Document rs, String fieldName)
	{
		Document result = null;
		if (rs.containsKey(fieldName) && rs.get(fieldName).toString().length() > 0)
		{
			result = (Document)rs.get(fieldName);
		}
		return result;		
	}


	public static Date convertNSZDateString(String dateStr) throws ParseException
	{ 
		Date result = convertDateString(dateStr, getDateFormat(dateStr));

		return result;
	}
	public static LocalDateTime convertNSZDateTimeString(String dateStr) throws ParseException
	{ 
		LocalDateTime result = convertDateTimeString(dateStr, getDateFormat(dateStr));

		return result;
	}

	public static Date convertNSZDateString_b4Aug2021(String dateStr) throws ParseException
	{ 
		final String z     = new String("Z");
		final String t     = new String("T");
		final String pls   = new String("+");
		final String am    = new String("am");
		final String pm    = new String("pm");
		final String slash = new String("/");
		final String dot   = new String(".");

		final String happ  = new String("0000");

		if (dateStr.contains(z) && dateStr.contains(t) && dateStr.contains(dot))
		{
			Date result = convertDateString(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			return result;
		}
		// Some Nightscout dates are missing the milliseconds
		else if (dateStr.contains(z) && dateStr.contains(t))
		{
			Date result = convertDateString(dateStr, "yyyy-MM-dd'T'HH:mm:ss'Z'");
			return result;
		}
		else if (dateStr.contains(pls))
		{
			// HappApp seems to use a different notation here

			/*			Pattern happAppPattern = Pattern.compile("([0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]T[0-9][0-9]:[0-9][0-9]+0000)");
			Matcher happAppMatcher = happAppPattern.matcher(dateStr);

			if (happAppMatcher.find())*/
			if (dateStr.contains(happ) && !dateStr.contains(dot))
			{
				Date result = convertDateString(dateStr, "yyyy-MM-dd'T'HH:mm'+0000'");
				return result;
			}
			else
			{
				Date result = convertDateString(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'+000'");
				return result;
			}
		}
		else if (dateStr.contains(slash) && (dateStr.contains(am) || dateStr.contains(pm)))
		{
			Date result = convertDateString(dateStr, "dd/MM/yyyy HH:mm:ss aa");
			return result;			
		}
		else
		{
			Date result = convertDateString(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS");
			return result;
		}
	}


	public static Date convertNSDateString(String dateStr) throws ParseException
	{
		Date result = convertDateString(dateStr, "yyyy-MM-dd HH:mm:ss.S");
		return result;
	}

	public static Date convertDateString(String dateStr, String format) throws ParseException
	{
		Date result = new Date(0);

		// Need to keep this thread safe
		synchronized(m_LockSDF)
		{
			//			final DateFormat nsformat   = CommonUtils.getSimpleDateFormat(format);  // new SimpleDateFormat(format, Locale.ENGLISH);
			if (dateStr.length() > 0)
			{
				//			System.out.println("David DateStr(" + dateStr + ") Format(" + format + ")");

				try
				{

					// ---------------------------------------------------------------------------------------
					// 29 Oct 2021
					//
					// Discovered a bug in SimpleDateFormat today :-(
					// While unit testing LibreView extract the following date / times all advance one hour:
					//
					// 28/03/2021 01:17  becomes 28/03/2021 02:17
					// 28/03/2021 01:32  becomes 28/03/2021 02:32
					// 28/03/2021 01:47  becomes 28/03/2021 02:47
					//
					// This is last Sunday of March, date that DST starts in UK
					// However the following date / times are not adjusted
					//
					// 28/03/2021 02:02
					// 28/03/2021 02:17
					// 28/03/2021 02:32
					// 28/03/2021 02:48
					//
					// This only got picked up because the 28/03/2021 01:17 change to 28/03/2021 02:17
					// now conflicts with a real BG result at 02:17
					//
					// Now is the time and move to a better way of parsing dates
					//
					// ---------------------------------------------------------------------------------------

					//					result = nsformat.parse(dateStr.toUpperCase()); 


					// This seems to offer better results...
					//					DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
					DateTimeFormatter formatter = new DateTimeFormatterBuilder()
							// case insensitive to parse JAN and FEB
							.parseCaseInsensitive()
							// add pattern
							.appendPattern(format)
							// create formatter (use English Locale to parse month names)
							.toFormatter(Locale.ENGLISH);
					LocalDateTime parseDate = LocalDateTime.parse(dateStr, formatter);
					result = Date.from(parseDate.atZone(ZoneId.systemDefault()).toInstant());


				}
				catch (NumberFormatException e) {
					m_Logger.log(Level.SEVERE, "Caught Number format exception : " + e + " for dateStr: " + dateStr + " using format: " + format);
				}
				catch (DateTimeParseException e) {
					m_Logger.log(Level.SEVERE, "Caught DateTimeParse exception : " + e + " for dateStr: " + dateStr + " using format: " + format);
				}
			}
			m_LockSDF.notifyAll();
		}

		return result;
	}

	public static LocalDateTime convertDateTimeString(String dateStr, String format) throws ParseException
	{
		LocalDateTime result = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		result = LocalDateTime.parse(dateStr, formatter);
		return result;
	}

	//	public static Date parseFileDateTime(String date)
	//	{
	//		Date result = new Date(0);
	//		// Combined Date Time
	//
	//		// Need to keep this thread safe
	//		synchronized(m_LockSDF)
	//		{
	//
	//			final String defSlashFormat = new String("dd/MM/yy HH:mm");
	//			String prefDateFormat       = PrefsNightScoutLoader.getInstance().getM_InputDateFormat();
	//			final DateFormat slashformat = CommonUtils.getSimpleDateFormat((prefDateFormat.contains("/")  ?  prefDateFormat : defSlashFormat));
	//			// DateFormat slashformat      = new SimpleDateFormat((prefDateFormat.contains("/")  ?  prefDateFormat : defSlashFormat), Locale.ENGLISH);
	//
	//			try
	//			{
	//				result = slashformat.parse(date);
	//			}
	//			catch (ParseException e) 
	//			{
	//				m_Logger.log(Level.SEVERE, "<CommonUtils>" + "parseFileDate - Unexpected error parsing date: " + date);
	//			}
	//			m_LockSDF.notifyAll();
	//		}
	//
	//		return result;
	//	}
	//
	//	public  static Date parseDate(String date)
	//	{
	//		Date result = new Date(0);
	//
	//		// Combined Date Time
	//		final DateFormat slashformat = CommonUtils.getSimpleDateFormat("dd/MM/yyyy");
	//		// DateFormat slashformat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
	//
	//		try
	//		{
	//			result = slashformat.parse(date);
	//		}
	//		catch (ParseException e) 
	//		{
	//			m_Logger.log(Level.SEVERE, "<CommonUtils>" + "parseDate - Unexpected error parsing date: " + date);
	//		}
	//
	//
	//		return result;
	//	}
	//
	//	public static Date parseFromDate(String field)
	//	{
	//		Date result = new Date(0);
	//
	//		// field is in the form 2dd/mm/yyyy to dd/mm/yyyy"
	//		Pattern fromDatePattern = Pattern.compile("([0-9/]*) to");
	//		Matcher fromDateMatcher = fromDatePattern.matcher(field);
	//
	//		if (fromDateMatcher.find())
	//		{
	//			String matchedString = fromDateMatcher.group(0);
	//			String date = new String(matchedString.substring(0, matchedString.length() - 3));
	//			result = parseDate(date);
	//		}
	//
	//		return result;
	//	}
	//	public static Date parseToDate(String field)
	//	{
	//		Date result = new Date(0);
	//
	//		// field is in the form 2dd/mm/yyyy to dd/mm/yyyy"
	//		Pattern fromDatePattern = Pattern.compile("to ([0-9/]*)");
	//		Matcher fromDateMatcher = fromDatePattern.matcher(field);
	//
	//		if (fromDateMatcher.find())
	//		{
	//			String matchedString = fromDateMatcher.group(0);
	//			String date = new String(matchedString.substring(3, matchedString.length()));
	//			result = parseDate(date);
	//		}
	//
	//		return result;
	//	}

	public static String  getStringCellValue(HSSFRow row, int index)
	{
		String result = null;
		HSSFCell cell = row.getCell(index);
		if (cell != null && (cell.getCellType() != HSSFCell.CELL_TYPE_BLANK))
		{
			// David 27 Apr 2016
			// Get an exception just with diasend when updating the grid.
			// think might be related to fact that string values actually
			// owned by the POI Excel reader.
			// Therefore clone at the lowest level here.
			//result = cell.getStringCellValue();
			result = new String(cell.getStringCellValue());
		}
		else
		{
			// Ensure result gets allocated even if cell is blank
			result = new String("");
		}
		return result;
	}

	public static Double getDoubleCellValue(HSSFRow row, int index)
	{
		Double result = null;
		HSSFCell cell = row.getCell(index);
		if (cell != null && (cell.getCellType() != HSSFCell.CELL_TYPE_BLANK))
		{
			// David 27 Apr 2016
			// Get an exception just with diasend when updating the grid.
			// think might be related to fact that string values actually
			// owned by the POI Excel reader.
			// Therefore clone at the lowest level here.
			//result = cell.getNumericCellValue();
			result = Double.valueOf(cell.getNumericCellValue());
		}
		else
		{
			// Ensure result gets allocated even if cell is blank
			result = Double.valueOf(0);
		}
		return result;
	}

	public static String stripDoubleQuotes(String str)
	{
		String result = str.replace("\"", "");
		return result;
	}


	private static SimpleDateFormat getSimpleDateFormat(String format)
	{
		SimpleDateFormat resultDateFormat = null;

		// Need to keep this thread safe
		synchronized(m_LockInitialization)
		{
			if (format != null)
			{
				resultDateFormat = DATE_FORMAT_HASHMAP.get(format);
				if (resultDateFormat == null)
				{
					resultDateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
					DATE_FORMAT_HASHMAP.put(format, resultDateFormat);
				}
			}

			m_LockInitialization.notifyAll();
		}
		return resultDateFormat;
	}

	private static String getDateFormat(String dateString)
	{
		String resultString = null;

		// Need to keep this thread safe
		synchronized(m_LockInitialization)
		{
			if (m_DatePatternFormatArrayInitializedBoolean == false)
			{
				initRegexpHashMap();
			}
			if (m_LastUsedPatternFormat != null) 
			{
				Matcher matcher = m_LastUsedPatternFormat.getM_Pattern().matcher(dateString);
				resultString = matcher.find() ? m_LastUsedPatternFormat.getM_FormatString() : resultString;
			}

			if (resultString == null) {

				for (DatePatternFormat dpFormat : m_DatePatternFormatArray /*m_DatePatternFormatList*/)
				{
					if (resultString == null)
					{
						Pattern keyPattern = dpFormat.getM_Pattern();
						Matcher matcher = keyPattern.matcher(dateString);
						resultString = matcher.find() ? dpFormat.getM_FormatString() : resultString;

						m_LastUsedPatternFormat = matcher.find() ? dpFormat : m_LastUsedPatternFormat;
					}
				}

				//				Iterator<Pattern> keyIterator = REGEXP_FORMAT_HASHMAP.keySet().iterator();
				//
				//				while (resultString == null && keyIterator.hasNext())
				//				{
				//					Pattern keyPattern = keyIterator.next();
				//
				//					Matcher matcher = keyPattern.matcher(dateString);
				//					resultString = matcher.find() ? REGEXP_FORMAT_HASHMAP.get(keyPattern) : resultString;
				//
				//					lAST_USED_PATTERN = matcher.find() ? keyPattern : lAST_USED_PATTERN;
				//				}

			}

			m_LockInitialization.notifyAll();
		}

		return resultString;
	}

	private static void initRegexpHashMap()
	{
		if (m_DatePatternFormatArrayInitializedBoolean == false)
		{
			m_DatePatternFormatArrayInitializedBoolean = true;
			int i = 0;
			for (String[] pStrings : m_RegexpDatePatternArray) {
				m_DatePatternFormatList.add(new DatePatternFormat(pStrings[0], pStrings[1]));
				m_DatePatternFormatArray[i++] = new DatePatternFormat(pStrings[0], pStrings[1]);
			}
		}

	}


}