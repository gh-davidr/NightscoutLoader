package davidRichardson;

import java.util.Comparator;
import java.util.Date;

public class AuditLogComparator implements Comparator<AuditLog> 
{		
	private boolean m_DescendingSort = true;
	AuditLogComparator()
	{
		m_DescendingSort = true;
	}
	AuditLogComparator(boolean descendingSort)
	{
		m_DescendingSort = descendingSort;
	}
	
	public int compare(AuditLog p1, AuditLog p2) 
	{
		int  result     = 0;
		
		Date alD1 = p1.getM_UploadDate();
		Date alD2 = p2.getM_UploadDate();
		
		long p1_millies = alD1.getTime();
		long p2_millies = alD2.getTime();
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
