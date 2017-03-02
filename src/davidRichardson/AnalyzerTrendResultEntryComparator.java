package davidRichardson;

import java.util.Comparator;

import davidRichardson.AnalyzerResultEntryInterval.DBResultEntryProfileDirection;

public class AnalyzerTrendResultEntryComparator implements Comparator<AnalyzerTrendResultEntry> 
{		
	private boolean m_DescendingSort = true;
	private AnalyzerTrendResultEntryComparatorType m_CompType = AnalyzerTrendResultEntryComparatorType.NumericSort;
	
	public enum AnalyzerTrendResultEntryComparatorType
	{
		NumericSort,
		TimeSort,
		TypeSort,
	};
	
	AnalyzerTrendResultEntryComparator()
	{
		m_DescendingSort = true;
	}
	AnalyzerTrendResultEntryComparator(boolean descendingSort)
	{
		m_DescendingSort = descendingSort;
	}
	AnalyzerTrendResultEntryComparator(boolean descendingSort,
			AnalyzerTrendResultEntryComparatorType compType)
	{
		m_DescendingSort = descendingSort;
		m_CompType       = compType;
	}

	public int compare(AnalyzerTrendResultEntry p1, AnalyzerTrendResultEntry p2) 
	{			
		int  result     = 0;

		int  p1_entries = p1.getM_ResultEntryIntervals().size();
		int  p2_entries = p2.getM_ResultEntryIntervals().size();
		
//		DBResultEntryProfileChange p1_profile = p1.getM_DBResultEntryProfileChange();
//		DBResultEntryProfileChange p2_profile = p2.getM_DBResultEntryProfileChange();
		
		DBResultEntryProfileDirection p1_direction = p1.getM_ProfileDirection();
		DBResultEntryProfileDirection p2_direction = p2.getM_ProfileDirection();
		

		// Subtraction is too big for int result
		long diff        = p1_entries - p2_entries;
		int  timeDiff    = p1.getM_StartHour() - p2.getM_StartHour();
		int  profileDiff = p1_direction.hashCode() - p2_direction.hashCode(); // this will do

		// Sort based on number of entries and then also by time
		if (    // Numeric Sort
				(m_CompType == AnalyzerTrendResultEntryComparatorType.NumericSort
				&& (diff > 0) || (diff == 0 && timeDiff < 0) ) 
				
				||
				// Time Sort
				((m_CompType == AnalyzerTrendResultEntryComparatorType.TimeSort
				&& timeDiff < 0))
				
				||
				// Type Sort
				((m_CompType == AnalyzerTrendResultEntryComparatorType.TypeSort
				&& profileDiff < 0))
		   )
		{
			//  1 to get results in ascending order
			// -1 to get results in descending order
			result = m_DescendingSort ? -1 : 1;
		}
//		else if ( (diff < 0) || (diff == 0 && timeDiff > 0) )
		else if (    // Numeric Sort
				(m_CompType == AnalyzerTrendResultEntryComparatorType.NumericSort
				&& (diff < 0) || (diff == 0 && timeDiff > 0) ) 
				
				||
				// Time Sort
				((m_CompType == AnalyzerTrendResultEntryComparatorType.TimeSort
				&& timeDiff > 0))
				
				||
				// Type Sort
				((m_CompType == AnalyzerTrendResultEntryComparatorType.TypeSort
				&& profileDiff < 0))
		   )

		{
			// -1 to get results in ascending order
			//  1 to get results in descending order
			result = m_DescendingSort ? 1 : -1;
		}
		
		return result;
	}
	
}
