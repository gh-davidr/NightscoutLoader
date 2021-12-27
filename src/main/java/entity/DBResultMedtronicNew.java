package entity;

import java.util.Date;
import utils.CommonUtils;

public class DBResultMedtronicNew extends DBResultMedtronic 
{
	private static final String m_DateFormat = "yyyy/MM/dd";
	private static final String m_TimeFormat = "yyyy/MM/dd HH:mm:ss";

	static protected String m_ReportRange = "Serial Number";

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
				"New Device Time",
				"BG Reading (mmol/L)",
				"Linked BG Meter ID",
				"Basal Rate (U/h)",
				"Temp Basal Amount",
				"Temp Basal Type",
				"Temp Basal Duration (h:mm:ss)",
				"Bolus Type",
				"Bolus Volume Selected (U)",
				"Bolus Volume Delivered (U)",
				"Bolus Duration (h:mm:ss)",
				"Prime Type",
				"Prime Volume Delivered (U)",
				"Alarm",
				"Suspend",
				"Rewind",
				"BWZ Estimate (U)",
				"BWZ Target High BG (mmol/L)",
				"BWZ Target Low BG (mmol/L)",
				"BWZ Carb Ratio (g/U)",
				"BWZ Insulin Sensitivity (mg/dL/U)",
				"BWZ Carb Input (grams)",
				"BWZ BG Input (mmol/L)",
				"BWZ Correction Estimate (U)",
				"BWZ Food Estimate (U)",
				"BWZ Active Insulin (U)",
				"Sensor Calibration BG (mmol/L)",
				"Sensor Glucose (mmol/L)",
				"ISIG Value",
				"Event Marker",
				"Bolus Number",
				"Bolus Cancellation Reason",
				"BWZ Unabsorbed Insulin Total (U)",
				"Final Bolus Estimate",
				"Scroll Step Size",
				"Insulin Action Curve Time",
				"Sensor Calibration Rejected Reason",
				"Preset Bolus",
				"Bolus Source",
				"Network Device Associated Reason",
				"Network Device Disassociated Reason",
				"Network Device Disconnected Reason",
				"Sensor Exception",
				"Preset Temp Basal Name",
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

	public DBResultMedtronicNew(String[] recordSet) 
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
			m_TempBasalAmountIndex = fieldLocation("Temp Basal Amount");
			m_TempBasalDurationIndex = fieldLocation("Temp Basal Duration (h:mm:ss)");
			m_CarbAmountIndex = fieldLocation("BWZ Carb Input (grams)");
			m_StandardBolusIndex = fieldLocation("Bolus Volume Delivered (U)");
			m_BolusDurationIndex = fieldLocation("Bolus Duration (h:mm:ss)");
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
		if (recordSet.length > 6)
		{
			if (CommonUtils.stripDoubleQuotes(recordSet[2]).isEmpty() && 
					CommonUtils.stripDoubleQuotes(recordSet[5]).equals(m_ReportRange))
			{
				m_ReportDateRange = true;

				// All fields in this record have double quotes around them
				String field3 = getDateFromLine2DateField(CommonUtils.stripDoubleQuotes(recordSet[3]));
				String field4 = getDateFromLine2DateField(CommonUtils.stripDoubleQuotes(recordSet[4]));

				m_StartDate = parseFileDate(field3);
				m_EndDate   = parseFileDate(field4);
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


	private String getDateFromLine2DateField(String val)
	{
		String result = "";
		
		// Split by space to lose time... not interested
		String[] tokens = val.split(" ");
		if (tokens.length > 0)
		{
			result = tokens[0];
			
			// Add a leading zero for those lazy dates
			if (result.length() == 7)
			{
				result = "0" + result;
			}
		}

		return result;
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
