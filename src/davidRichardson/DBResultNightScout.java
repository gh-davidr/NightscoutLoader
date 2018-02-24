package davidRichardson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.Date;
import davidRichardson.CommonUtils;


import com.mongodb.DBObject;

public class DBResultNightScout extends DBResult 
{
	public DBResultNightScout()
	{
		;
	}


	public  DBResultNightScout(DBObject rs, boolean rawData)
	{
		final DateFormat format     = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.ENGLISH);
		//		final DateFormat nsformat   = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
		final DateFormat dayformat  = new SimpleDateFormat("EEEE", Locale.ENGLISH);
		final DateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
		final DateFormat timeformat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

		// Put all into a try-catch block since format.parse can raise exception
		try
		{
			if(rawData)
			{
				m_Year       = CommonUtils.getFieldInt(rs, "Year");
				m_Month      = CommonUtils.getFieldInt(rs, "Month");
				m_Day        = CommonUtils.getFieldInt(rs, "Day");
				m_DayName    = CommonUtils.getFieldStr(rs, "DayName");
				//			time       = (Date)rs.get("Time");
				m_Time       = format.parse(CommonUtils.getFieldStr(rs, "Time"));
				m_TimeSlot   = CommonUtils.getFieldStr(rs, "TimeSlot");
				m_Result     = CommonUtils.getFieldStr(rs, "Result");
				m_ResultType = CommonUtils.getFieldStr(rs, "ResultType");
				m_MealType   = CommonUtils.getFieldStr(rs, "MealType");
				m_Duration   = CommonUtils.getFieldStr(rs, "Duration");

				// Date time    = (Date)rs.get("Time");

				// David 14 Apr 2016
				String timeStr  = CommonUtils.getFieldStr(rs, "Time");
				Date time       = CommonUtils.convertNSDateString(timeStr);

				m_EpochMillies = time.getTime();
			}
			// This is a treatment load
			// Need to check these field values below though
			else
			{
				m_ID            = CommonUtils.getIDStr(rs, "_id");
				m_CP_EventType  = CommonUtils.getFieldStr(rs, "eventType");
				m_CP_Glucose    = CommonUtils.getFieldDouble(rs, "glucose");
				m_CP_Carbs      = CommonUtils.getFieldDouble(rs, "carbs");
				m_CP_Insulin    = CommonUtils.getFieldDouble(rs, "insulin");
				m_CP_CarbsTime  = CommonUtils.getFieldDouble(rs, "preBolus");
				m_CP_Duration   = CommonUtils.getFieldDouble(rs, "duration");    // Temp Basal
				m_CP_Percent    = CommonUtils.getFieldDouble(rs, "percent");     // Temp Basal
				m_CP_BasalValue = CommonUtils.getFieldDouble(rs, "profile");  // Temp Basal
				m_CP_Notes      = CommonUtils.getFieldStr(rs, "notes");
				m_CP_EnteredBy  = CommonUtils.getFieldStr(rs, "enteredBy");

				// Nightscout times are in UTC.
				// Need to convert them to local time.
				Date utcTime    = CommonUtils.convertNSZDateString(CommonUtils.getFieldStr(rs, "created_at"));
				Date time       = new Date(CommonUtils.toLocalTime(utcTime.getTime(), CommonUtils.locTZ));
				m_Time          = time;
				m_CP_EventTime  = CommonUtils.convertNSZDateString(m_Time);

				//				m_CP_EventTime  = CommonUtils.getFieldStr(rs, "created_at");
				//
				//				// David 14 Apr 2016
				//				Date time          = convertNSZDateString(m_CP_EventTime);
				//				m_Time             = time;

				m_EpochMillies     = time.getTime();
				m_TreatmentDayName = dayformat.format(time);
				m_TreatmentDate    = dateformat.format(time);
				m_TreatmentTime    = timeformat.format(time);
			}
		}
		catch (ParseException e) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DBResultNightScout Caught Exception in MongoDB load "+e.toString());
		}
	}
}
