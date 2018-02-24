package davidRichardson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class DBResultNightScoutProfile extends DBResultCore
{
	protected static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	private String                                       m_dia;
	private String                                       m_carbs_hr;
	private String                                       m_delay;
	private Date                                         m_startDate;
	private String                                       m_timezone;
	private ArrayList <DBResultDiasendISFSetting>        m_sens;
	private ArrayList <DBResultDiasendBasalSetting>      m_basal;
	private ArrayList <DBResultDiasendTargetSetting>     m_target_low;
	private ArrayList <DBResultDiasendTargetSetting>     m_target_high;
	private ArrayList <DBResultDiasendCarbRatioSetting>  m_CarbRatioList;
	private Date                                         m_created_at;
	private String                                       m_units;

	public  DBResultNightScoutProfile(DBObject rs)
	{
		final DateFormat format     = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.ENGLISH);
		final DateFormat nsformat   = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
		final DateFormat dayformat  = new SimpleDateFormat("EEEE", Locale.ENGLISH);
		final DateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
		final DateFormat timeformat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

		try
		{
			m_sens          = new ArrayList<DBResultDiasendISFSetting>();
			m_basal         = new ArrayList<DBResultDiasendBasalSetting>();
			m_target_low    = new ArrayList<DBResultDiasendTargetSetting>();
			m_target_high   = new ArrayList<DBResultDiasendTargetSetting>();
			m_CarbRatioList = new ArrayList<DBResultDiasendCarbRatioSetting>();

			m_dia        = CommonUtils.getFieldStr(rs, "dia");
			m_carbs_hr   = CommonUtils.getFieldStr(rs, "carbs_hr");
			m_delay      = CommonUtils.getFieldStr(rs, "delay");
			m_startDate  = nsformat.parse(CommonUtils.getFieldStr(rs, "startDate"));
			m_timezone   = CommonUtils.getFieldStr(rs, "timezone");
			m_created_at = nsformat.parse(CommonUtils.getFieldStr(rs, "created_at"));
			m_units      = CommonUtils.getFieldStr(rs, "units");

			BasicDBList carbratio   = CommonUtils.getFieldBasicDBList(rs, "carbratio");
			BasicDBList sens        = CommonUtils.getFieldBasicDBList(rs, "sens");
			BasicDBList basal       = CommonUtils.getFieldBasicDBList(rs, "basal");
			BasicDBList target_low  = CommonUtils.getFieldBasicDBList(rs, "target_low");
			BasicDBList target_high = CommonUtils.getFieldBasicDBList(rs, "target_high");
			
			// Build the lists
			BasicDBObject[] carbratio_list = carbratio.toArray(new BasicDBObject[0]);
			for (BasicDBObject rs2: carbratio_list)
			{		
				DBResultDiasendCarbRatioSetting crs = new DBResultDiasendCarbRatioSetting(rs2);
				m_CarbRatioList.add(crs);
			}
			BasicDBObject[] sens_list = sens.toArray(new BasicDBObject[0]);
			for (BasicDBObject rs2: sens_list)
			{		
				DBResultDiasendISFSetting crs = new DBResultDiasendISFSetting(rs2, m_units);
				m_sens.add(crs);
			}
			BasicDBObject[] basal_list = basal.toArray(new BasicDBObject[0]);
			for (BasicDBObject rs2: basal_list)
			{		
				DBResultDiasendBasalSetting crs = new DBResultDiasendBasalSetting(rs2);
				m_basal.add(crs);
			}
			BasicDBObject[] target_low_list = target_low.toArray(new BasicDBObject[0]);
			for (BasicDBObject rs2: target_low_list)
			{		
				DBResultDiasendTargetSetting crs = new DBResultDiasendTargetSetting(rs2);
				m_target_low.add(crs);
			}
			
			BasicDBObject[] target_high_list = target_high.toArray(new BasicDBObject[0]);
			for (BasicDBObject rs2: target_high_list)
			{		
				DBResultDiasendTargetSetting crs = new DBResultDiasendTargetSetting(rs2);
				m_target_high.add(crs);
			}
			
			// Sort the lists in ascending time order
			Collections.sort(m_sens, new DBResultDiasendPumpSettingComparator(false));
			Collections.sort(m_basal, new DBResultDiasendPumpSettingComparator(false));
			Collections.sort(m_target_low, new DBResultDiasendPumpSettingComparator(false));
			Collections.sort(m_target_high, new DBResultDiasendPumpSettingComparator(false));
			Collections.sort(m_CarbRatioList, new DBResultDiasendPumpSettingComparator(false));	
		}

		catch (ParseException e) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DBResultNightScout Caught Exception in MongoDB load "+e.toString());
		}

	}

	@Override
	public long getM_EpochMillies() 
	{
		// Use the start date for this
		long result = m_startDate.getTime();
		return result;
	}

	@Override
	public void setImpactOfProximity() 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void determineWhetherInProximity() 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public BasicDBObject createNightScoutObject() 
	{
		// TODO Auto-generated method stub
		return null;
	}

}
