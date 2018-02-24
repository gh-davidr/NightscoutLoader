package davidRichardson;

import java.util.Comparator;
import java.util.Date;

public class DBResultDiasendPumpSettingComparator implements Comparator<DBResultDiasendPumpSetting> 
{
	private boolean m_DescendingSort = true;
	DBResultDiasendPumpSettingComparator()
	{
		m_DescendingSort = true;
	}
	DBResultDiasendPumpSettingComparator(boolean descendingSort)
	{
		m_DescendingSort = descendingSort;
	}

	public int compare(DBResultDiasendPumpSetting p1, DBResultDiasendPumpSetting p2) 
	{
		int  result     = 0;

		// Times always in this format:
		// HH:MM

		// Get the hour value
		int p1hr = Integer.parseInt(p1.getM_Time().substring(0, 2));
		int p2hr = Integer.parseInt(p2.getM_Time().substring(0, 2));

		// Get the minutes value
		int p1min = Integer.parseInt(p1.getM_Time().substring(3, 5));
		int p2min = Integer.parseInt(p2.getM_Time().substring(3, 5));

		// Consider differences between Hour and Minute
		long diff       = (p1hr - p2hr) == 0 ? (p1min - p2min) : (p1hr - p2hr);

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
