package davidRichardson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	static boolean isDateTheSame(Date d1, Date d2)
	{
		boolean result = false;
		final DateFormat df  = new SimpleDateFormat("dd/MM/yyyy");
		String d1Str = new String(df.format(d1));
		String d2Str = new String(df.format(d2));

		result = d1Str.equals(d2Str);

		return result;
	}


	static long timeDiffInMinutes(Date d1, Date d2)
	{
		long result = 0;

		long diffMillies = d1.getTime() - d2.getTime();

		result = diffMillies / (1000 * 60);

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

	public static String convertNSZDateString(Date date) throws ParseException
	{ 
		String result = convertDateString(date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return result;
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
	
	public static String convertDateString(Date date, String format) throws ParseException
	{
		String result = new String("");
		final DateFormat nsformat   = new SimpleDateFormat(format, Locale.ENGLISH);
		result = nsformat.format(date);

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

}