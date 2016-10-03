package davidRichardson;

import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Calendar; 
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;


public class DBResultDiasend extends DBResult 
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());	
	
	private boolean  m_Valid;
	
	private boolean  m_ReportDateRange;
	static private Date     m_StartDate;
	static private Date     m_EndDate;

	static private String m_ReportRange = "Date";

	// Collection of items to handle the Glucose tab
	static private boolean m_GlucoseIndexesInitialized = false; 
	static private String[] m_GlucoseFieldNames =
		{
				"Time",
				"mmol/L",
		};
	static private int m_GlucoseTimeIndex = 0;
	static private int m_GlucoseBGIndex = 0;
	
	// Collection of items to handle the Insulin tab
	static private boolean m_InsulinIndexesInitialized = false; 
	static private String[] m_InsulinFieldNames =
		{
				"Time",
				"Basal Amount (U/h)",
				"Bolus Type",
				"Bolus Volume (U)",
				"Immediate Volume (U)",	
				"Extended Volume (U)",
				"Duration (min)",
				"Carbs(g)",
				"Notes",
		};
	static private int m_InsulinTimeIndex = 0;
	static private int m_InsulinBasalAmountIndex = 0;
	static private int m_InsulinBolusTypeIndex = 0;
	static private int m_InsulinBolusVolumeIndex = 0;
	static private int m_InsulinImmediateVolumeIndex = 0;
//	static private int m_InsulinExtendedVolumeIndex = 0;
	static private int m_InsulinDurationIndex = 0;
	static private int m_InsulinCarbsIndex = 0;
//	static private int m_InsulinNotesIndex = 0;

	
	public boolean isValid()
	{
		return m_Valid;
	}

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

	private Date parseFileDate(String date)
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
   	    	m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+"> " + "parseFileDate - Unexpected error parsing date: " + date);
		}

		return result;
	}

	private static Date parseDate(String date)
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
   	    	m_Logger.log(Level.SEVERE, "<DBResultDiasend>" + "parseDate - Unexpected error parsing date: " + date);
		}

		return result;
	}

	private static Date parseFromDate(String field)
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
	private static Date parseToDate(String field)
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
	
	public DBResultDiasend(HSSFRow row, boolean insulinTab) 
	{
		super();
		
		if (insulinTab == true)
		{
			// Handle an Insulin record
			loadRawInsulin(row);
		}
		else
		{
			// Handle aa Glucose record
			loadRawGlucose(row);
		}

	}
	
	String getStringCellValue(HSSFRow row, int index)
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
	
	Double getDoubleCellValue(HSSFRow row, int index)
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

	
	private void loadRawGlucose(HSSFRow row)
	{
		String timeStr  = getStringCellValue(row, m_GlucoseTimeIndex);
		Double bgDouble = getDoubleCellValue(row, m_GlucoseBGIndex);

		m_ResultType = "BG";
		if (bgDouble != null)
		{
			m_Result = bgDouble.toString();
		}

		if (timeStr != null)
		{
			Date d = new Date(0);
			d = parseFileDate(timeStr);
			if (d.getTime() == 0)
			{
				m_Valid=false;
			}
			else
			{
				m_Time = d;
				m_Valid = true;
				setDateFields();
			}
		}
	}


	private void loadRawInsulin(HSSFRow row)
	{
		String timeStr      = getStringCellValue(row, m_InsulinTimeIndex);
		Double basAmtDbl    = getDoubleCellValue(row, m_InsulinBasalAmountIndex);
		String bolTypeStr   = getStringCellValue(row, m_InsulinBolusTypeIndex);
		Double bolAmtDbl    = getDoubleCellValue(row, m_InsulinBolusVolumeIndex);
		Double bolImmAmtDbl = getDoubleCellValue(row, m_InsulinImmediateVolumeIndex);
//		Double bolExtAmtDbl = getDoubleCellValue(row, m_InsulinExtendedVolumeIndex);
		Double bolDurDbl    = getDoubleCellValue(row, m_InsulinDurationIndex);
		Double carbAmtDbl   = getDoubleCellValue(row, m_InsulinCarbsIndex);
//		String notesStr     = getStringCellValue(row, m_InsulinNotesIndex);

		// Not sure how data looks for square waves as yet ...

		
		if (timeStr != null && !timeStr.equals(""))
		{
			Date d = new Date(0);

			d = parseFileDate(timeStr);
			if (d.getTime() == 0)
			{
				m_Valid=false;
			}
			else
			{
				m_Time = d;
				m_Valid = true;
				setDateFields();
			}
		}

		// Bolus?
		if (bolTypeStr != null && bolTypeStr.length() > 0)
		{
			if (bolTypeStr.equals("Normal"))
			{
				this.m_ResultType = "Standard Bolus";
			}
			else if (bolTypeStr.equals("Combination"))
			{
				this.m_ResultType = "MultiWave";
				if (bolDurDbl != null)
				{
					this.m_Duration = bolDurDbl.toString();
					
					// Add the amount of immediate to notes
					if (bolImmAmtDbl != null)
					{
						this.m_Notes += "Immediate Volume = " + bolImmAmtDbl.toString();
					}
				}
			}
			// If bolus type specified, have to assume that value even if null can be used.
			if (bolAmtDbl != null)
			{
				this.m_Result = bolAmtDbl.toString();
			}
		}
		else if (carbAmtDbl != null && !carbAmtDbl.equals(0.0))
		{
			this.m_ResultType = "Carbs";
			this.m_Result = carbAmtDbl.toString();
		}
		
		// Now store the Basals and allow the controlling loader to decide whether it's of interest or not
		else if (basAmtDbl != null)
		{
			this.m_ResultType = "Basal";
			this.m_Result     = basAmtDbl.toString();
		}

		else 
		{
			m_Valid = false;
		}


	}
	
	private void setDateFields()
	{
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(m_Time);
	    m_Year = cal.get(Calendar.YEAR);
	    m_Month = cal.get(Calendar.MONTH) + 1;  // Seem to have to add 1 to month 
	    m_Day = cal.get(Calendar.DAY_OF_MONTH);
	    
	    DateFormat f = new SimpleDateFormat("EEEE");
	    try 
	    {
	    	m_DayName = f.format(m_Time);
	    }
	    catch(Exception e) 
	    {
	    	m_DayName = "";
	    }
	    
	    // Finally, set this critical field too
	    m_EpochMillies = m_Time.getTime();
	}
	
	public static void initializeGlucoseDateRange(HSSFRow row)
	{

		int maxColumns = row.getPhysicalNumberOfCells();
		if (maxColumns == m_GlucoseFieldNames.length)
		{
			String cell1 = row.getCell(0).getStringCellValue();
			String cell2 = row.getCell(1).getStringCellValue();

			if (cell1.equals(m_ReportRange))
			{
				// Data in cell2
				m_StartDate = parseFromDate(cell2);
				m_EndDate   = parseToDate(cell2);

			}
			else if (cell2.equals(m_ReportRange))
			{
				// Data in cell1
				m_StartDate = parseFromDate(cell1);
				m_EndDate   = parseToDate(cell1);
			}
		}
	}

	public static void initializeInsulinHeaders(HSSFRow row)
	{
		if (m_InsulinIndexesInitialized == false)
		{
			int maxColumns = row.getPhysicalNumberOfCells();
			if (maxColumns == m_InsulinFieldNames.length)
			{
				int c = 0;

				for (c=0; c<m_InsulinFieldNames.length; c++)
				{
					String cell = row.getCell(c).getStringCellValue();
					if (cell.equals(m_InsulinFieldNames[c]))
					{
						switch (c)
						{
						case 0 : m_InsulinTimeIndex            = c; break;
						case 1 : m_InsulinBasalAmountIndex     = c; break;
						case 2 : m_InsulinBolusTypeIndex       = c; break;
						case 3 : m_InsulinBolusVolumeIndex     = c; break;
						case 4 : m_InsulinImmediateVolumeIndex = c; break;
//						case 5 : m_InsulinExtendedVolumeIndex  = c; break;
						case 6 : m_InsulinDurationIndex        = c; break;
						case 7 : m_InsulinCarbsIndex           = c; break;
//						case 8 : m_InsulinNotesIndex           = c; break;
						default :                                  break;
						}
					}
				}
				m_InsulinIndexesInitialized = true;
			}
		}
	}

	public static void initializeGlucoseHeaders(HSSFRow row)
	{
		if (m_GlucoseIndexesInitialized == false)
		{
			int maxColumns = row.getPhysicalNumberOfCells();
			if (maxColumns == m_GlucoseFieldNames.length)
			{
				int c = 0;

				for (c=0; c<m_GlucoseFieldNames.length; c++)
				{
					String cell = row.getCell(c).getStringCellValue();
					if (cell.equals(m_GlucoseFieldNames[c]))
					{
						switch (c)
						{
						case 0 : m_GlucoseTimeIndex           = c; break;
						case 1 : m_GlucoseBGIndex             = c; break;
						default :                                  break;
						}
					}
				}
				m_GlucoseIndexesInitialized = true;
			}
		}

	}

}


