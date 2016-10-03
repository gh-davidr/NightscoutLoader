package davidRichardson;

import java.sql.ResultSet;
import java.sql.SQLException;
//import java.util.logging.Logger;

public class DBResultRoche extends DBResult  
{
//	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());	

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

	public DBResultRoche()
	{
		super();
	}
}
