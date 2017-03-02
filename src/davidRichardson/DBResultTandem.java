package davidRichardson;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Calendar; 
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DBResultTandem extends DBResult 
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());	

	private String[] m_RecordSet;
	private boolean  m_Valid;
	private boolean  m_ReportDateRange;

	public enum WhichSectionOfFile
	{
		Unknown,
		Meter_Vals,
		Basal_Vals,
		Pump_Vals,
	};

	static private String[] m_MeterFieldNames =
		{
				"DeviceType",
				"SerialNumber",
				"Description",
				"EventDateTime",
				"BG",
				"IOB",
				"Note",
		};

	static private String[] m_BasalFieldNames =
		{
				"Type",
				"EventDateTime",
				"BasalRate",
		};

	
	static private String[] m_PumpFieldNames =
		{
				"Type",
				"Description",
				"BG",
				"IOB",
				"BolusRequestID",
				"BolusCompletionID",
				"CompletionDateTime",
				"InsulinDelivered",
				"FoodDelivered",
				"CorrectionDelivered",
				"CompletionStatusID",
				"CompletionStatusDesc",
				"BolusIsComplete",
				"BolexCompletionID",
				"BolexSize",
				"BolexStartDateTime",
				"BolexCompletionDateTime",
				"BolexInsulinDelivered",
				"BolexIOB",
				"BolexCompletionStatusID",
				"BolexCompletionStatusDesc",
				"ExtendedBolusIsComplete",
				"EventDateTime",
				"RequestDateTime",
				"BolusType",
				"BolusRequestOptions",
				"StandardPercent",
				"Duration",
				"CarbSize",
				"UserOverride",
				"TargetBG",
				"CorrectionFactor",
				"FoodBolusSize",
				"CorrectionBolusSize",
				"ActualTotalBolusRequested",
				"IsQuickBolus",
				"EventHistoryReportEventDesc",
				"EventHistoryReportDetails",
				"NoteID",
				"IndexID",
				"Note",
		};


	static private WhichSectionOfFile m_WhichSectionOfFile = WhichSectionOfFile.Unknown;

	static private boolean m_MeterIndexesInitialized = false; 
	static private int m_MeterDateTimeIndex = 0;
	static private int m_MeterBGIndex = 0;

	static private boolean m_BasalIndexesInitialized = false; 
	static private int m_BasalDateTimeIndex = 0;
	static private int m_BasalBasalRateIndex = 0;

	
	static private boolean m_PumpIndexesInitialized = false; 
	static private int m_PumpDateTimeIndex = 0;
	static private int m_PumpBGIndex = 0;		
	static private int m_PumpBolusTypeIndex = 0;
	static private int m_PumpCarbAmountIndex = 0;
	static private int m_PumpStandardBolusIndex = 0;
	static private int m_PumpBolusDurationIndex = 0;

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

	@Override
	public long getM_EpochMillies() 
	{
		return m_Time.getTime();
	}
	
	public DBResultTandem(String[] recordSet, ResultType resType) 
	{
		super();

		if (m_WhichSectionOfFile == WhichSectionOfFile.Meter_Vals)
		{
			constructMeterValues(recordSet);
		}
		else if (m_WhichSectionOfFile == WhichSectionOfFile.Basal_Vals)
		{
			constructBasalValues(recordSet);
		}

		else if (m_WhichSectionOfFile == WhichSectionOfFile.Pump_Vals)
		{
			constructPumpValues(recordSet, resType);
		}

	}

	private void constructMeterValues(String[] recordSet) 
	{
		m_Valid = true;
		m_ReportDateRange = false;
		initialize();

		if (recordSet.length < m_RecordSet.length)
		{
			m_Valid = false;
		}

		if (m_Valid == true)
		{
			// Deep copy
			for (int i = 0; i<m_RecordSet.length; i++)
			{
				m_RecordSet[i] = new String(recordSet[i]);
			}

			Date d = new Date(0);
			d = parseFileDate(m_RecordSet[m_MeterDateTimeIndex]);
			if (d.getTime() == 0)
			{
				m_Valid=false;
			}

			if (m_Valid == true)
			{
				m_Time = d;
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(d);
				m_Year = cal.get(Calendar.YEAR);
				m_Month = cal.get(Calendar.MONTH);
				m_Day = cal.get(Calendar.DAY_OF_MONTH);

				DateFormat f = new SimpleDateFormat("EEEE");
				try 
				{
					m_DayName = f.format(d);
				}
				catch(Exception e) 
				{
					m_DayName = "";
				}

				// DAVID - 17 APr
				// See if exception is here ...
				try
				{
					// BG?
					if (m_RecordSet[m_MeterBGIndex].length() > 0)
					{
						this.m_Result = m_RecordSet[m_MeterBGIndex];
						this.m_ResultType = "BG";
					}

					// Not interested, so make invalid and it's discarded
					else
					{
						m_Valid = false;
					}
				}
				catch(Exception e) 
				{
					m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DBResultTandem - Exception caught" + e.getMessage());
				}
			}
		}
	}

	private void constructBasalValues(String[] recordSet) 
	{
		m_Valid = true;
		m_ReportDateRange = false;
		initialize();

		if (recordSet.length < m_RecordSet.length)
		{
			m_Valid = false;
		}

		if (m_Valid == true)
		{
			// Deep copy
			for (int i = 0; i<m_RecordSet.length; i++)
			{
				m_RecordSet[i] = new String(recordSet[i]);
			}

			Date d = new Date(0);
			d = parseFileDate(m_RecordSet[m_BasalDateTimeIndex]);
			if (d.getTime() == 0)
			{
				m_Valid=false;
			}

			if (m_Valid == true)
			{
				m_Time = d;
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(d);
				m_Year = cal.get(Calendar.YEAR);
				m_Month = cal.get(Calendar.MONTH);
				m_Day = cal.get(Calendar.DAY_OF_MONTH);

				DateFormat f = new SimpleDateFormat("EEEE");
				try 
				{
					m_DayName = f.format(d);
				}
				catch(Exception e) 
				{
					m_DayName = "";
				}

				try
				{
					// BASAL
					if (m_RecordSet[m_BasalBasalRateIndex].length() > 0)
					{
						this.m_Result = m_RecordSet[m_BasalBasalRateIndex];
						this.m_ResultType = "Basal";
					}

					// Not interested, so make invalid and it's discarded
					else
					{
						m_Valid = false;
					}
				}
				catch(Exception e) 
				{
					m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DBResultTandem - Exception caught" + e.getMessage());
				}
			}
		}
	}

	
	private void constructPumpValues(String[] recordSet, ResultType resType) 
	{
		m_Valid = true;
		m_ReportDateRange = false;
		initialize();

		if (recordSet.length < m_RecordSet.length)
		{
			m_Valid = false;
		}

		if (m_Valid == true)
		{
			// Deep copy
			for (int i = 0; i<m_RecordSet.length; i++)
			{
				m_RecordSet[i] = new String(recordSet[i]);
			}

			Date d = new Date(0);
			d = parseFileDate(m_RecordSet[m_PumpDateTimeIndex]);
			if (d.getTime() == 0)
			{
				m_Valid=false;
			}

			if (m_Valid == true)
			{
				m_Time = d;
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(d);
				m_Year = cal.get(Calendar.YEAR);
				m_Month = cal.get(Calendar.MONTH); // For Tandem, the months are correct!!
				m_Day = cal.get(Calendar.DAY_OF_MONTH);

				DateFormat f = new SimpleDateFormat("EEEE");
				try 
				{
					m_DayName = f.format(d);
				}
				catch(Exception e) 
				{
					m_DayName = "";
				}

				// DAVID - 17 APr
				// See if exception is here ...
				try
				{
					// BG?
					if (resType == DBResultTandem.ResultType.BG)
					{
						if (m_RecordSet[m_PumpBGIndex].length() > 0)
						{
							this.m_Result = m_RecordSet[m_PumpBGIndex];
							this.m_ResultType = "BG";
						}
					}

					else if (resType == DBResultTandem.ResultType.Insulin)
					{
						// It's some sort of insulin dose ...
						if (m_RecordSet[m_PumpBolusTypeIndex].length() > 0)
						{
							// Now get the values
							if (m_RecordSet[m_PumpStandardBolusIndex].length() > 0)
							{
								this.m_Result = m_RecordSet[m_PumpStandardBolusIndex];
							}

							// Standard Bolus types
							if (m_RecordSet[m_PumpBolusTypeIndex].equals("Standard")
									|| m_RecordSet[m_PumpBolusTypeIndex].equals("Standard/Correction")
									|| m_RecordSet[m_PumpBolusTypeIndex].equals("Quick")
									)
							{
								this.m_ResultType = "Standard Bolus";
							}

							// Extended Bolus types
							else if (m_RecordSet[m_PumpBolusTypeIndex].contains("Extended"))
							{
								this.m_ResultType = "Extended Bolus Start";
								// Store the insulin amount in extended - this is needed for Tandem which has separate rows for NOW and EXTENDED amoutns
								this.m_ExtendedAmount = m_RecordSet[m_PumpStandardBolusIndex];

								if (m_RecordSet[m_PumpBolusDurationIndex].length() > 0)
								{			    	
									// This field is already in minutes ...
									m_Duration = m_RecordSet[m_PumpBolusDurationIndex];
								}
							}
						}
					}

					// None for now - David 8 Feb 2017

					/*					// Temp Basal
					else if (m_RecordSet[m_PumpTempBasalAmountIndex].length() > 0)
					{
						this.m_Result = m_RecordSet[m_PumpTempBasalAmountIndex];
						this.m_Duration = m_RecordSet[m_PumpTempBasalDurationIndex];
						this.m_ResultType = "Temp Basal";
					}
					 */

					else if (resType == DBResultTandem.ResultType.Carbs)
					{
						// Carbs
						if (m_RecordSet[m_PumpCarbAmountIndex].length() > 0)
						{
							this.m_Result = m_RecordSet[m_PumpCarbAmountIndex];
							this.m_ResultType = "Carbs";
							
							// If the carb value is 0, then this means there weren't really any carbs at all
							// and more than likely it's just a correction.  So we invalidate the entry
							if (this.m_Result.equals("0"))
							{
								m_Valid = false;
							}
						}
					}

					// Not interested, so make invalid and it's discarded
					else
					{
						m_Valid = false;
					}
				}
				catch(Exception e) 
				{
					m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DBResultTandem - Exception caught" + e.getMessage());
				}
			}
		}
	}


	private void initialize()
	{
		if (m_WhichSectionOfFile == WhichSectionOfFile.Meter_Vals)
		{
			m_RecordSet = new String[m_MeterFieldNames.length];

			// Set values in underlying ResultFromDB from record set
			if (m_MeterIndexesInitialized == false)
			{	
				m_MeterDateTimeIndex = fieldLocation("EventDateTime", m_MeterFieldNames);
				m_MeterBGIndex       = fieldLocation("BG", m_MeterFieldNames);

				m_MeterIndexesInitialized = true;
			}
		}
		
		else if (m_WhichSectionOfFile == WhichSectionOfFile.Basal_Vals)
		{
			m_RecordSet = new String[m_BasalFieldNames.length];
			
			// Set values in underlying ResultFromDB from record set
			if (m_BasalIndexesInitialized == false)
			{	
				m_BasalDateTimeIndex  = fieldLocation("EventDateTime", m_BasalFieldNames);
				m_BasalBasalRateIndex = fieldLocation("BasalRate", m_BasalFieldNames);

				m_BasalIndexesInitialized = true;
			}
		}

		else if (m_WhichSectionOfFile == WhichSectionOfFile.Pump_Vals)
		{
			m_RecordSet = new String[m_PumpFieldNames.length];

			// Set values in underdlying ResultFromDB from record set
			if (m_PumpIndexesInitialized == false)
			{	
				m_PumpDateTimeIndex  = fieldLocation("EventDateTime", m_PumpFieldNames);
				m_PumpBolusTypeIndex = fieldLocation("Description", m_PumpFieldNames);
				m_PumpBGIndex = fieldLocation("BG", m_PumpFieldNames);

				//				m_TempBasalAmountIndex = fieldLocation("Temp Basal Amount (U/h)");
				//				m_TempBasalDurationIndex = fieldLocation("Temp Basal Duration (hh:mm:ss)");
				m_PumpCarbAmountIndex    = fieldLocation("CarbSize", m_PumpFieldNames);
				m_PumpStandardBolusIndex = fieldLocation("ActualTotalBolusRequested", m_PumpFieldNames);
				m_PumpBolusDurationIndex = fieldLocation("Duration", m_PumpFieldNames);
				//			m_Time = fieldLocation("Timestamp");

				m_PumpIndexesInitialized = true;
			}

		}

	}

	private int fieldLocation(String f, String[] fieldNames)
	{
		int result=-1;
		for (int i=0; result < 0 && i < fieldNames.length; i++)
		{
			if (fieldNames[i] == f)
			{
				result=i;
			}
		}
		return result;
	}

	public boolean validRecord()
	{
		boolean result=true;

		// There are 39 rows in this file
		if (m_RecordSet.length < 39)
		{
			result=false;
		}
		return result;
	}

	/**
	 * @return the m_WhichSectionOfFile
	 */
	public static synchronized WhichSectionOfFile getM_WhichSectionOfFile() {
		return m_WhichSectionOfFile;
	}

	/**
	 * @param m_WhichSectionOfFile the m_WhichSectionOfFile to set
	 */
	public static synchronized void setM_WhichSectionOfFile(WhichSectionOfFile m_WhichSectionOfFile) {
		DBResultTandem.m_WhichSectionOfFile = m_WhichSectionOfFile;
	}
}
