package entity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Level;

import org.bson.Document;

import java.util.Date;


import com.mongodb.DBObject;
import utils.CommonUtils;

public class DBResultNightScout extends DBResult 
{
	final static DateFormat SDF_DATE_FORMAT     = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.ENGLISH);
	//		final DateFormat nsformat   = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
	final static DateFormat SDF_DAY_DATE_FORMAT  = new SimpleDateFormat("EEEE", Locale.ENGLISH);
	final static DateFormat SDF_DATE_DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
	final static DateFormat SDF_TIME_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

	public DBResultNightScout()
	{
		;
	}

	
	public  DBResultNightScout(DBObject rs, boolean rawData)
	{
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
				m_Time       = SDF_DATE_FORMAT.parse(CommonUtils.getFieldStr(rs, "Time"));
				m_TimeSlot   = CommonUtils.getFieldStr(rs, "TimeSlot");
				m_Result     = CommonUtils.getFieldStr(rs, "Result");
				m_ResultType = CommonUtils.getFieldStr(rs, "ResultType");
				m_MealType   = CommonUtils.getFieldStr(rs, "MealType");
				m_Duration   = CommonUtils.getFieldStr(rs, "Duration");

				// Date time    = (Date)rs.get("Time");

				// David 14 Apr 2016
				String timeStr  = CommonUtils.getFieldStr(rs, "Time");
				Date time       = CommonUtils.convertDateString(timeStr);

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
				m_CP_BasalValue = CommonUtils.getFieldDouble(rs, "profile");  // Temp Basal //AndroidAPS appears to use this field for Profile Name, not BasalValue
				if (m_CP_EventType.equals("Temp Basal")) {
					Double absolute = CommonUtils.getFieldDouble(rs, "absolute");
					if (absolute != null)
						m_CP_BasalValue = CommonUtils.getFieldDouble(rs, "absolute");
				}
				m_CP_Notes      = CommonUtils.getFieldStr(rs, "notes");
				m_CP_EnteredBy  = CommonUtils.getFieldStr(rs, "enteredBy");
				if (m_CP_EventType.equals("Combo Bolus")) {
					if (m_CP_EnteredBy.equals("")) {//CP uses strings for these values
						m_CP_Enteredinsulin = Double.valueOf(Double.parseDouble(CommonUtils.getFieldStr(rs, "enteredinsulin")));//Combo Bolusv
						m_CP_SplitNow = Double.valueOf(Double.parseDouble(CommonUtils.getFieldStr(rs, "splitNow")));//Combo Bolus
						m_CP_SplitExt = Double.valueOf(Double.parseDouble(CommonUtils.getFieldStr(rs, "splitExt")));//Combo Bolus
						m_CP_Relative = CommonUtils.getFieldDouble(rs, "relative");//Combo Bolus
					} else { //NSCLIENT_ID (AndroidAPS), NS Loader uses Double
						m_CP_Enteredinsulin = CommonUtils.getFieldDouble(rs, "enteredinsulin");//Combo Bolus
						m_CP_SplitNow = CommonUtils.getFieldDouble(rs, "splitNow");//Combo Bolus
						m_CP_SplitExt = CommonUtils.getFieldDouble(rs, "splitExt");//Combo Bolus
						m_CP_Relative = CommonUtils.getFieldDouble(rs, "relative");//Combo Bolus
					}
				}

				// Nightscout times are in UTC.
				// Need to convert them to local time.
				Date utcTime    = CommonUtils.convertDateString(CommonUtils.getFieldStr(rs, "created_at"));
				Date time       = new Date(CommonUtils.toLocalTime(utcTime.getTime(), CommonUtils.locTZ));
				m_Time          = time;
				m_CP_EventTime  = CommonUtils.convertNSZDateString(m_Time);

				//				m_CP_EventTime  = CommonUtils.getFieldStr(rs, "created_at");
				//
				//				// David 14 Apr 2016
				//				Date time          = convertNSZDateString(m_CP_EventTime);
				//				m_Time             = time;

				m_EpochMillies     = time.getTime();
				m_TreatmentDayName = SDF_DAY_DATE_FORMAT.format(time);
				m_TreatmentDate    = SDF_DATE_DATE_FORMAT.format(time);
				m_TreatmentTime    = SDF_TIME_DATE_FORMAT.format(time);
			}
		}
		catch (ParseException e) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DBResultNightScout Caught Exception in MongoDB load "+e.toString());
		}
	}
	
	public  DBResultNightScout(Document rs, boolean rawData)
	{
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
				m_Time       = SDF_DATE_FORMAT.parse(CommonUtils.getFieldStr(rs, "Time"));
				m_TimeSlot   = CommonUtils.getFieldStr(rs, "TimeSlot");
				m_Result     = CommonUtils.getFieldStr(rs, "Result");
				m_ResultType = CommonUtils.getFieldStr(rs, "ResultType");
				m_MealType   = CommonUtils.getFieldStr(rs, "MealType");
				m_Duration   = CommonUtils.getFieldStr(rs, "Duration");

				// Date time    = (Date)rs.get("Time");

				// David 14 Apr 2016
				String timeStr  = CommonUtils.getFieldStr(rs, "Time");
				Date time       = CommonUtils.convertDateString(timeStr);

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
				m_CP_BasalValue = CommonUtils.getFieldDouble(rs, "profile", false);  // Temp Basal //AndroidAPS appears to use this field for Profile Name, not BasalValue
				if (m_CP_EventType.equals("Temp Basal")) {
					Double absolute = CommonUtils.getFieldDouble(rs, "absolute");
					if (absolute != null)
						m_CP_BasalValue = CommonUtils.getFieldDouble(rs, "absolute");
				}
				m_CP_Notes      = CommonUtils.getFieldStr(rs, "notes");
				m_CP_EnteredBy  = CommonUtils.getFieldStr(rs, "enteredBy");
				if (m_CP_EventType.equals("Combo Bolus")) {
					if (m_CP_EnteredBy.equals("")) {//CP uses strings for these values
						m_CP_Enteredinsulin = Double.valueOf(Double.parseDouble(CommonUtils.getFieldStr(rs, "enteredinsulin")));//Combo Bolusv
						m_CP_SplitNow = Double.valueOf(Double.parseDouble(CommonUtils.getFieldStr(rs, "splitNow")));//Combo Bolus
						m_CP_SplitExt = Double.valueOf(Double.parseDouble(CommonUtils.getFieldStr(rs, "splitExt")));//Combo Bolus
						m_CP_Relative = CommonUtils.getFieldDouble(rs, "relative");//Combo Bolus
					} else { //NSCLIENT_ID (AndroidAPS), NS Loader uses Double
						m_CP_Enteredinsulin = CommonUtils.getFieldDouble(rs, "enteredinsulin");//Combo Bolus
						m_CP_SplitNow = CommonUtils.getFieldDouble(rs, "splitNow");//Combo Bolus
						m_CP_SplitExt = CommonUtils.getFieldDouble(rs, "splitExt");//Combo Bolus
						m_CP_Relative = CommonUtils.getFieldDouble(rs, "relative");//Combo Bolus
					}
				}

				// Nightscout times are in UTC.
				// Need to convert them to local time.
				Date utcTime    = CommonUtils.convertDateString(CommonUtils.getFieldStr(rs, "created_at"));
				Date time       = new Date(CommonUtils.toLocalTime(utcTime.getTime(), CommonUtils.locTZ));
				m_Time          = time;
				m_CP_EventTime  = CommonUtils.convertNSZDateString(m_Time);

				//				m_CP_EventTime  = CommonUtils.getFieldStr(rs, "created_at");
				//
				//				// David 14 Apr 2016
				//				Date time          = convertNSZDateString(m_CP_EventTime);
				//				m_Time             = time;

				m_EpochMillies     = time.getTime();
				m_TreatmentDayName = SDF_DAY_DATE_FORMAT.format(time);
				m_TreatmentDate    = SDF_DATE_DATE_FORMAT.format(time);
				m_TreatmentTime    = SDF_TIME_DATE_FORMAT.format(time);
			}
		}
		catch (ParseException e) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DBResultNightScout Caught Exception in MongoDB load "+e.toString());
		}
	}
}
