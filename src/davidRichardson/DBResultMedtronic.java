package davidRichardson;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Calendar; 
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DBResultMedtronic extends DBResult 
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());	
	
	private String[] m_RecordSet;
	private boolean  m_Valid;
	private boolean  m_ReportDateRange;
	private Date     m_StartDate;
	private Date     m_EndDate;
	
	static private String m_ReportRange = "Report Range";
	
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
	
/*	private Date parseFileDate(String date)
	{
		Date result = new Date(0);
		// One of a couple of formats
		
		// 15 Jun 2016
		// Bug found by Melanie Cragg in Australia 14 Jun 2016
		// Medtronic dates were all being stored as January
		// Andy's original file masked this issue as his data was all January!!!
//		DateFormat dashformat  = new SimpleDateFormat("dd-mm-yyyy", Locale.ENGLISH);
//		DateFormat slashformat = new SimpleDateFormat("dd/mm/yyyy", Locale.ENGLISH);
		
		final String defDashFormat  = new String("dd-MM-yy");
		final String defSlashFormat = new String("dd/MM/yy");
		String prefDateFormat       = PrefsNightScoutLoader.getInstance().getM_InputDateFormat();
		DateFormat dashformat       = new SimpleDateFormat((prefDateFormat.contains("-")  ?  prefDateFormat : defDashFormat), Locale.ENGLISH);
		DateFormat slashformat      = new SimpleDateFormat((prefDateFormat.contains("/")  ?  prefDateFormat : defSlashFormat), Locale.ENGLISH);
		
		try
		{
			if (date.contains("/"))
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
*/	
	public DBResultMedtronic(String[] recordSet) 
	{
		super();
		
		m_StartDate = new Date(0);
		m_EndDate   = new Date(0);
		
		m_Valid = true;
		m_ReportDateRange = false;
		initialize();
		
		if (recordSet.length < m_RecordSet.length)
		{
			m_Valid = false;
		}
		
		if (m_Valid == false)
		{
			// Check for date range
	    	m_Logger.log(Level.FINE, "DBResultMedtronic Comparing:<" + recordSet[0] + "> and <" + m_ReportRange + ">");
			if (recordSet[0].equals(m_ReportRange))
			{
				m_ReportDateRange = true;
				
				m_StartDate = parseFileDate(recordSet[1]);
				m_EndDate   = parseFileDate(recordSet[3]);		
			}
		}

		if (m_Valid == true)
		{
			// Deep copy
			for (int i = 0; i<m_RecordSet.length; i++)
			{
				m_RecordSet[i] = new String(recordSet[i]);
			}
			
			Date d = new Date(0);
			d = parseFileDate(m_RecordSet[m_DateIndex]);
			if (d.getTime() == 0)
			{
				m_Valid=false;
			}
			
/*			// Now assign the base values
			DateFormat format = new SimpleDateFormat("dd-mm-yy", Locale.ENGLISH);
			Date d = new Date();
			
			try
			{
				d = format.parse(m_RecordSet[m_DateIndex]);
			}
	        catch (ParseException e) 
			{
	        	m_Valid=false;
	        }*/
			
			if (m_Valid == true)
			{
			    Calendar cal = Calendar.getInstance();
			    cal.setTime(d);
			    m_Year = cal.get(Calendar.YEAR);
			    m_Month = cal.get(Calendar.MONTH); // For Medtronic, the months are correct!!
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
			    
			    // Set other parameters ...
			    // Time
/*			    DateFormat tf = new SimpleDateFormat("dd/mm/yy H:m");
			    Date l_time = new Date(0);
*/			    
			    // Get the date column in a known date format
			    // So we can construct the real time of this event!
			    // David 15-Jun-2016
			    // BUg from Australia!
//			    DateFormat df = new SimpleDateFormat("dd/mm/yy");
			    DateFormat df = new SimpleDateFormat("dd/MM/yy");
			    String ds;
			    ds = df.format(d);
			    
			    // David 15-Jun-2016
			    // BUg from Australia!
//			    DateFormat tf = new SimpleDateFormat("dd/mm/yy H:m:s");
			    DateFormat tf = new SimpleDateFormat("dd/MM/yy H:m:s");
			    Date l_time = new Date(0);
			    
			    try
			    {
			    	l_time = tf.parse(ds + " " + m_RecordSet[m_TimeIndex]);
				    this.m_Time = l_time;
			    }
			    catch(Exception e) 
			    {
			    	this.m_Time = new Date(0);
			    }

			    // DAVID - 17 APr
			    // See if exception is here ...
			    try
			    {
			    	// BG?
			        if (m_RecordSet[m_BGIndex].length() > 0)
			    	{
			    		this.m_Result = m_RecordSet[m_BGIndex];
			    		this.m_ResultType = "BG";
			    	}

			    	// It's some sort of insulin dose ...
			        else if (m_RecordSet[m_BolusTypeIndex].length() > 0)
			    	{
			    		// Now get the values
			    		if (m_RecordSet[m_StandardBolusIndex].length() > 0)
			    		{
			    			this.m_Result = m_RecordSet[m_StandardBolusIndex];
			    		}

			    		// Standard Bolus types
			    		if (m_RecordSet[m_BolusTypeIndex].equals("Normal") ||
			    				// Can only see one example of this and seems to be a standard delivery
			    				m_RecordSet[m_BolusTypeIndex].equals("Dual/Normal"))
			    		{
			    			this.m_ResultType = "Standard Bolus";
			    		}

			    		// Extended Bolus types
			    		else if (m_RecordSet[m_BolusTypeIndex].equals("Square") ||
			    			     m_RecordSet[m_BolusTypeIndex].equals("Dual/Square"))
			    		{
			    			this.m_ResultType = "Extended Bolus Start";
			    			// Store the insulin amount in extended - this is needed for Medtronic which has separate rows for NOW and EXTENDED amoutns
			    			this.m_ExtendedAmount = m_RecordSet[m_StandardBolusIndex];
			    			
			    			if (m_RecordSet[m_BolusDurationIndex].length() > 0)
			    			{			    				
				    			// Need to convert hh:mm:ss into minutes ...
				    			// field is in the form 2dd/mm/yyyy to dd/mm/yyyy"
				    			Pattern durationPattern = Pattern.compile("([0-9]*):([0-9][0-9]):([0-9][0-9])");
				    			Matcher durationMatcher = durationPattern.matcher(m_RecordSet[m_BolusDurationIndex]);

				    			if (durationMatcher.find())
				    			{
				    				int hours = Integer.parseInt(durationMatcher.group(1));
				    				int mins  = Integer.parseInt(durationMatcher.group(2));
				    				Integer totMins = (hours * 60) + mins;
				    				this.m_Duration = totMins.toString();
				    			}
				    			else
				    			{
				    	   	    	m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DBResultMedtronic - DIDN'T PATTERN MATCH " + m_RecordSet[m_BolusDurationIndex]);
				    			}
			    			}
			    		}
			    	}
			        
			        // Temp Basal
			    	else if (m_RecordSet[m_TempBasalAmountIndex].length() > 0)
			    	{
			    		this.m_Result = m_RecordSet[m_TempBasalAmountIndex];
			    		this.m_Duration = m_RecordSet[m_TempBasalDurationIndex];
			    		this.m_ResultType = "Temp Basal";
			    	}
			    		
			    	// Carbs
			    	else if (m_RecordSet[m_CarbAmountIndex].length() > 0)
			    	{
			    		this.m_Result = m_RecordSet[m_CarbAmountIndex];
			    		this.m_ResultType = "Carbs";
 			    	}
				
				
			    	// Site Change
			    	else if ((m_RecordSet[m_PrimeIndex].length() > 0) && (m_RecordSet[m_PrimeIndex].equals("Fill Cannula")))
			    	{
			    		this.m_ResultType = "Site Change";
			    	}
			    	
			        // Not interested, so make invalid and it's discarded
			    	else
				    {
				    	m_Valid = false;
				    }
			    }
			    catch(Exception e) 
			    {
    	   	    	m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DBResultMedtronic - Exception caught" + e.getMessage());
			    }
			}
		}
	}
	
	private void initialize()
	{
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
	
	private int fieldLocation(String f)
	{
		int result=-1;
		for (int i=0; result < 0 && i < m_FieldNames.length; i++)
		{
			if (m_FieldNames[i] == f)
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
}
