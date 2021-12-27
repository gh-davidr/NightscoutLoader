package entity;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import control.MyLogger;

import java.util.Calendar; 
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public abstract class DBResultMedtronic extends DBResult 
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());	
	
	protected String[] m_RecordSet;
	protected boolean  m_Valid;
	protected boolean  m_ReportDateRange;
	protected Date     m_StartDate;
	protected Date     m_EndDate;
	
		
	protected abstract int getDateIndex();
	protected abstract int getTimeIndex();
	protected abstract int getBolusTypeIndex();
	protected abstract int getBGIndex();
	protected abstract int getTempBasalAmountIndex();
	protected abstract int getTempBasalDurationIndex();
	protected abstract int getCarbAmountIndex();
	protected abstract int getStandardBolusIndex();
	protected abstract int getBolusDurationIndex();
	protected abstract int getPrimeIndex();
	
	protected abstract void initialize();
	protected abstract void getDateRangeFromRecordSet(String[] recordSet);
	protected abstract String getDateFormat();
	protected abstract String getTimeFormat();
	
	

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
			getDateRangeFromRecordSet(recordSet);
		}

		if (m_Valid == true)
		{
			// Deep copy
			for (int i = 0; i<m_RecordSet.length; i++)
			{
				m_RecordSet[i] = new String(recordSet[i]);
			}
			
			Date d = new Date(0);
			d = parseFileDate(m_RecordSet[getDateIndex()]);
			if (d.getTime() == 0)
			{
				m_Valid=false;
			}
			
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
//			    DateFormat df = new SimpleDateFormat("dd/MM/yy");
			    DateFormat df = new SimpleDateFormat(getDateFormat());
			    String ds;
			    ds = df.format(d);
			    
			    // David 15-Jun-2016
			    // BUg from Australia!
//			    DateFormat tf = new SimpleDateFormat("dd/mm/yy H:m:s");
//			    DateFormat tf = new SimpleDateFormat("dd/MM/yy H:m:s");
			    DateFormat tf = new SimpleDateFormat(getTimeFormat());
			    Date l_time = new Date(0);
			    
			    try
			    {
			    	l_time = tf.parse(ds + " " + m_RecordSet[getTimeIndex()]);
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
			        if (m_RecordSet[getBGIndex()].length() > 0)
			    	{
			    		this.m_Result = m_RecordSet[getBGIndex()];
			    		this.m_ResultType = "BG";
			    	}

			    	// It's some sort of insulin dose ...
			        else if (m_RecordSet[getBolusTypeIndex()].length() > 0)
			    	{
			    		// Now get the values
			    		if (m_RecordSet[getStandardBolusIndex()].length() > 0)
			    		{
			    			this.m_Result = m_RecordSet[getStandardBolusIndex()];
			    		}

			    		// Standard Bolus types
			    		if (m_RecordSet[getBolusTypeIndex()].equals("Normal") ||
			    				// Can only see one example of this and seems to be a standard delivery
			    				m_RecordSet[getBolusTypeIndex()].equals("Dual/Normal"))
			    		{
			    			this.m_ResultType = "Standard Bolus";
			    			
			    			// We need a bolus amount.  Have seen for New Medtronic it's (presumably)
			    			// possible to dial up a bolus through wizard but not actually give it
			    			// In that case, the bolus amount is non-zero but the delivered is blank
			    			m_Valid = this.m_Result.isEmpty() == true ? false : m_Valid;
			    		}

			    		// Extended Bolus types
			    		else if (m_RecordSet[getBolusTypeIndex()].equals("Square") ||
			    			     m_RecordSet[getBolusTypeIndex()].equals("Dual/Square"))
			    		{
			    			this.m_ResultType = "Extended Bolus Start";
			    			// Store the insulin amount in extended - this is needed for Medtronic which has separate rows for NOW and EXTENDED amoutns
			    			this.m_ExtendedAmount = m_RecordSet[getStandardBolusIndex()];
			    			
			    			if (m_RecordSet[getBolusDurationIndex()].length() > 0)
			    			{			    				
				    			// Need to convert hh:mm:ss into minutes ...
				    			// field is in the form 2dd/mm/yyyy to dd/mm/yyyy"
				    			Pattern durationPattern = Pattern.compile("([0-9]*):([0-9][0-9]):([0-9][0-9])");
				    			Matcher durationMatcher = durationPattern.matcher(m_RecordSet[getBolusDurationIndex()]);

				    			if (durationMatcher.find())
				    			{
				    				int hours = Integer.parseInt(durationMatcher.group(1));
				    				int mins  = Integer.parseInt(durationMatcher.group(2));
				    				Integer totMins = (hours * 60) + mins;
				    				this.m_Duration = totMins.toString();
				    			}
				    			else
				    			{
				    	   	    	m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DBResultMedtronic - DIDN'T PATTERN MATCH " + m_RecordSet[getBolusDurationIndex()]);
				    			}
			    			}
			    			
			    			// We need a bolus amount.  Have seen for New Medtronic it's (presumably)
			    			// possible to dial up a bolus through wizard but not actually give it
			    			// In that case, the bolus amount is non-zero but the delivered is blank
			    			m_Valid = this.m_Result.isEmpty() == true ? false : m_Valid;
			    		}
			    	}
			        
			        // Temp Basal
			    	else if (m_RecordSet[getTempBasalAmountIndex()].length() > 0)
			    	{
			    		this.m_Result = m_RecordSet[getTempBasalAmountIndex()];
			    		this.m_Duration = m_RecordSet[getTempBasalDurationIndex()];
			    		this.m_ResultType = "Temp Basal";
			    	}
			    		
			    	// Carbs
			    	else if (m_RecordSet[getCarbAmountIndex()].length() > 0)
			    	{
			    		this.m_Result = m_RecordSet[getCarbAmountIndex()];
			    		this.m_ResultType = "Carbs";
 			    	}
				
				
			    	// Site Change
			    	else if ((m_RecordSet[getPrimeIndex()].length() > 0) && (m_RecordSet[getPrimeIndex()].equals("Fill Cannula")))
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
	
	
	protected int fieldLocation(String f, String[] fieldNames)
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
	
}
