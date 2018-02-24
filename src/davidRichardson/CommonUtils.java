package davidRichardson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
//import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

import com.mongodb.BasicDBList;

//import javax.swing.JFormattedTextField.AbstractFormatter;

import com.mongodb.DBObject;

public class CommonUtils 
{
	protected static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	public static final TimeZone utcTZ = TimeZone.getTimeZone("UTC");
	public static final TimeZone locTZ = TimeZone.getDefault();

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
	static boolean isTimeAfter(String time, Date dateTime) throws ParseException
	{
		boolean result = false;

		// Convert dateTime into a known string
		final DateFormat df  = new SimpleDateFormat("dd/MM/yyyy");
		final DateFormat dtf = new SimpleDateFormat("dd/MM/yyyy hh:mm");

		String dateTimeStr = new String(dtf.format(dateTime));
		// Now append space & time ;-)
		String timeOnDate  = new String(df.format(dateTime)) +  " " + time;

		Date baseDate = convertDateString(dateTimeStr, "dd/MM/yyyy hh:mm");
		Date compDate = convertDateString(timeOnDate,  "dd/MM/yyyy hh:mm");

		result = compDate.after(baseDate) ? true : false;

		return result;
	}

	// Used for Diasend temp basal identifications
	static boolean isTimeBefore(String time, Date dateTime) throws ParseException
	{
		boolean result = isTimeAfter(time, dateTime) ? false : true;

		return result;
	}

	// Used for Analyzer bedtime checks
	// Between means start <= time < end
	static boolean isTimeBetween(String startTime, String endTime, Date dateTime) throws ParseException
	{
		boolean result = false;

		// Convert dateTime into a known string
		final DateFormat df  = new SimpleDateFormat("dd/MM/yyyy");
		final DateFormat dtf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		String dateTimeStr = new String(dtf.format(dateTime));
		// Now append space & time ;-)
		String startTimeOnDate  = new String(df.format(dateTime)) +  " " + startTime;
		String endTimeOnDate    = new String(df.format(dateTime)) +  " " + endTime;

		Date baseDate = convertDateString(dateTimeStr, "dd/MM/yyyy hh:mm");
 		Date startCompDate = convertDateString(startTimeOnDate,  "dd/MM/yyyy hh:mm");
		Date endCompDate   = convertDateString(endTimeOnDate,  "dd/MM/yyyy hh:mm");

		result = ((startCompDate.before(baseDate) || startCompDate.equals(baseDate)) &&
				(endCompDate.after(baseDate)))
				? true : false;

		return result;
	}
	
	static String safeIfNull(String par)
	{
		String result = new String( par == null ? "" : par );
		return result;
	}

	static Double safeIfNull(Double par)
	{
		Double result = new Double( par == null ? 0.0 : par );
		return result;
	}
	
	static Integer safeIfNull(Integer par)
	{
		Integer result = new Integer( par == null ? 0 : par );
		return result;
	}

	// Used for Analyzer bedtime checks
	// Between means start <= time < end
	static boolean isTimeBetween(Date startTime, Date endTime, Date dateTime)
	{
		boolean result = false;

		result = ((startTime.before(dateTime) || startTime.equals(dateTime)) &&
				(endTime.after(dateTime)))
				? true : false;

		return result;
	}

	
	static boolean isDateTheSame(Date d1, Date d2)
	{
		boolean result = false;
		final DateFormat df  = new SimpleDateFormat("dd/MM/yyyy");
		String d1Str = new String(df.format(d1));
		String d2Str = new String(df.format(d2));

		result = d1Str.equals(d2Str);

		return result;
	}
	
	static Date addDaysToDate(Date dt, int numDays)
	{
		Date result = new Date(0);
		
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.DATE, numDays); // Add one day
		result = c.getTime();
		
		return result;
	}

	static int get24Hour(Date dt)
	{
		int result = 0;

		Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
		calendar.setTime(dt);   // assigns calendar to given date 
		result = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
//		calendar.get(Calendar.HOUR);        // gets hour in 12h format
//		calendar.get(Calendar.MONTH);       // gets month number, NOTE this is zero based!
		
		return result;
	}
	
	static Date setDateToParticularHour(Date dt, int hour)
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
	
	static int getMinutesFromDate(Date dt)
	{
		int result = 0;
		
		Calendar cal = Calendar.getInstance();       // get calendar instance
		cal.setTime(dt);                             // set cal to date

		result = cal.get(Calendar.MINUTE);
		return result;
	}

	static long timeDiffInMinutes(Date d1, Date d2)
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
		final DateFormat nsformat   = new SimpleDateFormat(format, Locale.ENGLISH);
		result = nsformat.format(date);

		return result;
	}
/*
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
		if (rs.containsField(fieldName))
		{
			try
			{
				result = new Double(((Number)rs.get(fieldName)).doubleValue());
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
		if (rs.containsField(fieldName))
		{
			result = (int)rs.get(fieldName);
		}
		return result;		
	}

*/	
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
				result = new Double(((Number)rs.get(fieldName)).doubleValue());
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
	
	public static DBObject getFieldDBObject(DBObject rs, String fieldName)
	{
		DBObject result = null;
		if (rs.containsField(fieldName) && rs.get(fieldName).toString().length() > 0)
		{
			result = (DBObject)rs.get(fieldName);
		}
		return result;		
	}

	public static BasicDBList getFieldBasicDBList(DBObject rs, String fieldName)
	{
		BasicDBList result = null;
		if (rs.containsField(fieldName) && rs.get(fieldName).toString().length() > 0)
		{
			result = (BasicDBList)rs.get(fieldName);
		}
		return result;		
	}

	public static Date convertNSZDateString(String dateStr) throws ParseException
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
		final DateFormat nsformat   = new SimpleDateFormat(format, Locale.ENGLISH);
		if (dateStr.length() > 0)
		{
			result = nsformat.parse(dateStr);  
		}

		return result;
	}
	
	public static Date parseFileDateTime(String date)
	{
		Date result = new Date(0);
		// Combined Date Time

		final String defSlashFormat = new String("dd/MM/yy HH:mm");
		String prefDateFormat       = PrefsNightScoutLoader.getInstance().getM_InputDateFormat();
		DateFormat slashformat      = new SimpleDateFormat((prefDateFormat.contains("/")  ?  prefDateFormat : defSlashFormat), Locale.ENGLISH);
		//		DateFormat slashformat      = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);

		try
		{
			result = slashformat.parse(date);
		}
		catch (ParseException e) 
		{
			m_Logger.log(Level.SEVERE, "<CommonUtils>" + "parseFileDate - Unexpected error parsing date: " + date);
		}

		return result;
	}

	public  static Date parseDate(String date)
	{
		Date result = new Date(0);
		// Combined Date Time
		DateFormat slashformat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

		try
		{
			result = slashformat.parse(date);
		}
		catch (ParseException e) 
		{
			m_Logger.log(Level.SEVERE, "<CommonUtils>" + "parseDate - Unexpected error parsing date: " + date);
		}

		return result;
	}

	public static Date parseFromDate(String field)
	{
		Date result = new Date(0);

		// field is in the form 2dd/mm/yyyy to dd/mm/yyyy"
		Pattern fromDatePattern = Pattern.compile("([0-9/]*) to");
		Matcher fromDateMatcher = fromDatePattern.matcher(field);

		if (fromDateMatcher.find())
		{
			String matchedString = fromDateMatcher.group(0);
			String date = new String(matchedString.substring(0, matchedString.length() - 3));
			result = parseDate(date);
		}

		return result;
	}
	public static Date parseToDate(String field)
	{
		Date result = new Date(0);

		// field is in the form 2dd/mm/yyyy to dd/mm/yyyy"
		Pattern fromDatePattern = Pattern.compile("to ([0-9/]*)");
		Matcher fromDateMatcher = fromDatePattern.matcher(field);

		if (fromDateMatcher.find())
		{
			String matchedString = fromDateMatcher.group(0);
			String date = new String(matchedString.substring(3, matchedString.length()));
			result = parseDate(date);
		}

		return result;
	}

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
			result = new Double(cell.getNumericCellValue());
		}
		else
		{
			// Ensure result gets allocated even if cell is blank
			result = new Double(0);
		}
		return result;
	}



}