package davidRichardson;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Calendar; 
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DBResultMedtronicOld extends DBResultMedtronic 
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());	

	private static final String m_DateFormat = "dd/MM/yy";
	private static final String m_TimeFormat = "dd/MM/yy H:m:s";

	static protected String m_ReportRange = "Report Range";

/*	private boolean  m_Valid;
	private boolean  m_ReportDateRange;
	private Date     m_StartDate;
	private Date     m_EndDate;
*/
	
	static private String[] m_FieldNames =
		{
				"Index",
				"Date",
				"Time",
				"Timestamp",
				"New Device Time",
				"BG Reading (mmol/L)",
				"Linked BG Meter ID",
				"Temp Basal Amount (U/h)",
				"Temp Basal Type",
				"Temp Basal Duration (hh:mm:ss)",
				"Bolus Type",
				"Bolus Volume Selected (U)",
				"Bolus Volume Delivered (U)",
				"Bolus Duration (hh:mm:ss)",
				"Prime Type",
				"Prime Volume Delivered (U)",
				"Suspend",
				"Rewind",
				"BWZ Estimate (U)",
				"BWZ Target High BG (mmol/L)",
				"BWZ Target Low BG (mmol/L)",
				"BWZ Carb Ratio (grams)",
				"BWZ Insulin Sensitivity (mmol/L)",
				"BWZ Carb Input (grams)",
				"BWZ BG Input (mmol/L)",
				"BWZ Correction Estimate (U)",
				"BWZ Food Estimate (U)",
				"BWZ Active Insulin (U)",
				"Alarm",
				"Sensor Calibration BG (mmol/L)",
				"Sensor Glucose (mmol/L)",
				"ISIG Value",
				"Daily Insulin Total (U)",
				"Raw-Type",
				"Raw-Values",
				"Raw-ID",
				"Raw-Upload ID",
				"Raw-Seq Num",
				"Raw-Device Type",
		};

	static private boolean m_indexesInitialized = false; 
	static private int m_DateIndex = 0;
	static private int m_TimeIndex = 0;
	static private int m_BolusTypeIndex = 0;
	static private int m_BGIndex = 0;
	static private int m_TempBasalAmountIndex = 0;
	static private int m_TempBasalDurationIndex = 0;
	static private int m_CarbAmountIndex = 0;
	static private int m_StandardBolusIndex = 0;
	static private int m_BolusDurationIndex = 0;
	static private int m_PrimeIndex = 0;

	@Override
	public boolean isValid()
	{
		return m_Valid;
	}

	@Override
	public boolean isReportRange()
	{
		return m_ReportDateRange;
	}

	public Date getStartReportRange()
	{
		return m_StartDate;
	}
	public Date getEndReportRange()
	{
		return m_EndDate;	
	}

	public DBResultMedtronicOld(String[] recordSet) 
	{
		super(recordSet);
	}

	@Override
	protected void initialize()
	{
		m_StartDate = new Date(0);
		m_EndDate   = new Date(0);

		m_Valid = true;
		m_ReportDateRange = false;

		m_RecordSet = new String[m_FieldNames.length];

		// Set values in underdlying ResultFromDB from record set
		if (m_indexesInitialized == false)
		{	
			m_DateIndex = fieldLocation("Date");
			m_TimeIndex = fieldLocation("Time");
			m_BolusTypeIndex = fieldLocation("Bolus Type");
			m_BGIndex = fieldLocation("BG Reading (mmol/L)");
			m_TempBasalAmountIndex = fieldLocation("Temp Basal Amount (U/h)");
			m_TempBasalDurationIndex = fieldLocation("Temp Basal Duration (hh:mm:ss)");
			m_CarbAmountIndex = fieldLocation("BWZ Carb Input (grams)");
			m_StandardBolusIndex = fieldLocation("Bolus Volume Delivered (U)");
			m_BolusDurationIndex = fieldLocation("Bolus Duration (hh:mm:ss)");
			//			m_Time = fieldLocation("Timestamp");
			m_PrimeIndex = fieldLocation("Prime Type");

			m_indexesInitialized = true;
		}
	}

	@Override
	protected void getDateRangeFromRecordSet(String[] recordSet)
	{
		// Example:
		// Peiro	JoanCarles		09/03/2019 00:00	31/05/2019 23:59	Serial Number	NG1623501H
		// So 3rd field is blank
		//    6th says serial number
		if (recordSet.length > 1)
		{
			m_Logger.log(Level.FINE, "DBResultMedtronic Comparing:<" + recordSet[0] + "> and <" + m_ReportRange + ">");
			if (recordSet[0].equals(m_ReportRange))
			{
				m_ReportDateRange = true;
				m_StartDate = parseFileDate(recordSet[1]);
				m_EndDate   = parseFileDate(recordSet[3]);
			}
		}
	}
	
	@Override
	protected String getDateFormat()
	{
		return m_DateFormat;
	}
	
	@Override
	protected String getTimeFormat()
	{
		return m_TimeFormat;
	}


	private int fieldLocation(String f)
	{
		int result = super.fieldLocation(f, m_FieldNames);
		return result;
	}

	@Override
	protected int getDateIndex() 
	{
		return m_DateIndex;
	}

	@Override
	protected int getTimeIndex() 
	{
		return m_TimeIndex;
	}

	@Override
	protected int getBolusTypeIndex() 
	{
		return m_BolusTypeIndex;
	}

	@Override
	protected int getBGIndex() 
	{
		return m_BGIndex;
	}

	@Override
	protected int getTempBasalAmountIndex() 
	{
		return m_TempBasalAmountIndex;
	}

	@Override
	protected int getTempBasalDurationIndex() 
	{
		return m_TempBasalDurationIndex;
	}

	@Override
	protected int getCarbAmountIndex()
	{
		return m_CarbAmountIndex;
	}

	@Override
	protected int getStandardBolusIndex() 
	{
		return m_StandardBolusIndex;
	}

	@Override
	protected int getBolusDurationIndex() 
	{
		return m_BolusDurationIndex;
	}

	@Override
	protected int getPrimeIndex() 
	{
		return m_PrimeIndex;
	}

}
