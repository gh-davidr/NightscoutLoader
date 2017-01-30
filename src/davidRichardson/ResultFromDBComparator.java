package davidRichardson;

import java.util.Comparator;

public class ResultFromDBComparator implements Comparator<DBResultInterface> 
{		
	private boolean m_DescendingSort = true;
	ResultFromDBComparator()
	{
		m_DescendingSort = true;
	}
	ResultFromDBComparator(boolean descendingSort)
	{
		m_DescendingSort = descendingSort;
	}
	
	public int compare(DBResultInterface p1, DBResultInterface p2) 
	{
		int  result     = 0;
		long p1_millies = p1.getM_EpochMillies();
		long p2_millies = p2.getM_EpochMillies();
		long diff       = p1_millies - p2_millies;

		// Subtraction is too big for int result
		if ((diff) > 0)
		{
			//  1 to get results in ascending order
			// -1 to get results in descending order
			result = m_DescendingSort ? -1 : 1;
		}
		else if ((diff) < 0)
		{
			// -1 to get results in ascending order
			//  1 to get results in descending order
			result = m_DescendingSort ? 1 : -1;
		}
		return result;
	}
	
}
