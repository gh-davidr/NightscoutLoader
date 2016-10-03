package davidRichardson;

import java.util.Comparator;
//import java.util.Date;

public class OmniPodBinaryFileSectionComparator implements Comparator<OmniPodBinaryFileSection> 
{

	private boolean m_DescendingSort = true;
	OmniPodBinaryFileSectionComparator()
	{
		m_DescendingSort = true;
	}
	OmniPodBinaryFileSectionComparator(boolean descendingSort)
	{
		m_DescendingSort = descendingSort;
	}

	public int compare(OmniPodBinaryFileSection p1, OmniPodBinaryFileSection p2) 
	{
		int  result     = 0;

		String alD1 = p1.getM_SectionHeader();
		String alD2 = p2.getM_SectionHeader();

		int hash1 = alD1.hashCode();
		int hash2 = alD2.hashCode();
		int  diff = hash1 - hash2;

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
