package davidRichardson;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
//import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBResultRoche extends DBResult  
{
	//	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());	
	protected static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	private boolean  m_Valid = true;

	static private String[] m_FieldNames =
		{
				"Year",
				"Month",
				"Day",
				"DayName",
				"Time",
				"TimeSlot",
				"Result",
				"ResultType",
				"MealType",
				"Duration",
		};

	static private boolean m_indexesInitialized = false; 
	static private int m_YearIndex = 0;
	static private int m_MonthIndex = 0;
	static private int m_DayIndex = 0;
	static private int m_DayNameIndex = 0;
	static private int m_TimeIndex = 0;
	static private int m_TimeSlotIndex = 0;
	static private int m_ResultIndex = 0;
	static private int m_ResultTypeIndex = 0;
	static private int m_MealTypeIndex = 0;
	static private int m_DurationIndex = 0;




	public DBResultRoche(ResultSet rs) throws SQLException
	{
		super();

		// Get this error if try to check resultset:
		// We just caught an error: The requested operation is not supported on forward only result sets. - The requested operation is not supported on forward only result sets.

		m_Year       = rs.getInt("Year");
		m_Month      = rs.getInt("Month");
		m_Day        = rs.getInt("Day");
		m_DayName    = rs.getString("DayName");
		m_Time       = rs.getTimestamp("Time");
		m_TimeSlot   = rs.getString("TimeSlot");
		m_Result     = rs.getString("Result");
		m_ResultType = rs.getString("ResultType");
		m_MealType   = rs.getString("MealType");
		m_Duration   = rs.getString("Duration");		
	}

	public DBResultRoche(String[] recordSet)
	{
		super();

		if (m_indexesInitialized == false)
		{
			m_Valid = false;
		}

		initialize();
		
		// Check for a repeat load and seeing the header line again.
		if (recordSet[m_YearIndex].equals("Year"))

		{
			m_Valid = false;
		}	

		try
		{
			if (m_Valid == true)
			{
				m_Year       = Integer.parseInt(recordSet[m_YearIndex]);
				m_Month      = Integer.parseInt(recordSet[m_MonthIndex]);
				m_Day        = Integer.parseInt(recordSet[m_DayIndex]);
				m_DayName    = recordSet[m_DayNameIndex];
				//m_Time       = parseFileDate(recordSet[m_TimeIndex]); 
				m_Time       = CommonUtils.convertDateString(recordSet[m_TimeIndex], "yyyy-MM-dd HH:mm:ss");
				m_TimeSlot   = recordSet[m_TimeSlotIndex];
				m_Result     = recordSet[m_ResultIndex];
				m_ResultType = recordSet[m_ResultTypeIndex];
				m_MealType   = recordSet[m_MealTypeIndex];
				m_Duration   = recordSet[m_DurationIndex];

				// If meal type or duration == "null" then reset strings to empty
				if (m_MealType.equals("(null)"))
				{
					m_MealType = "";
				}
				if (m_Duration.equals("(null)"))
				{
					m_Duration = "";
				}

				String rawString = rawToString();
				m_Logger.log(Level.FINE, "Just processed: " + rawString);

			}
		}
		catch (ParseException e) 
		{
			m_Valid = false;
		}
	}

	public DBResultRoche()
	{
		super();
	}

	private void initialize()
	{
		// Set values in underdlying ResultFromDB from record set
		if (m_indexesInitialized == false)
		{
			m_YearIndex = fieldLocation("Year");
			m_MonthIndex = fieldLocation("Month");
			m_DayIndex = fieldLocation("Day");
			m_DayNameIndex = fieldLocation("DayName");
			m_TimeIndex = fieldLocation("Time");
			m_TimeSlotIndex = fieldLocation("TimeSlot");
			m_ResultIndex = fieldLocation("Result");
			m_ResultTypeIndex = fieldLocation("ResultType");
			m_MealTypeIndex = fieldLocation("MealType");
			m_DurationIndex = fieldLocation("Duration");

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

	@Override
	public boolean isValid()
	{
		return m_Valid;
	}
}
