package davidRichardson;

import java.util.ArrayList;
import java.util.Collections;

import davidRichardson.AnalyzerTrendResultEntryComparator.AnalyzerTrendResultEntryComparatorType;

public class AnalyzerTrendResultAggregateGroup 
{
	private static Integer      m_Static_ID = 0;  // Assign an ID to each result created.
	private int                 m_ID        = 0;  // Assign an ID to each result created.

	// Groups TrendResultEntries that share the same result type and offset
	// The array list will have different times and be ordered to allow
	// easy display on spreadsheet.
	//
	
	// Too Low
	private ArrayList<AnalyzerTrendResultEntry> m_Hypo;

	// In Good Range
	private ArrayList<AnalyzerTrendResultEntry> m_From4to7ThenDown;
	private ArrayList<AnalyzerTrendResultEntry> m_From4to7UpThenDown;
	private ArrayList<AnalyzerTrendResultEntry> m_From4to7ThenLevel;
	private ArrayList<AnalyzerTrendResultEntry> m_From4to7ThenUp;
	private ArrayList<AnalyzerTrendResultEntry> m_From4to7DownThenUp;

	// Just outside Good Range
	private ArrayList<AnalyzerTrendResultEntry> m_From7to10ThenDown;
	private ArrayList<AnalyzerTrendResultEntry> m_From7to10UpThenDown;
	private ArrayList<AnalyzerTrendResultEntry> m_From7to10ThenLevel;
	private ArrayList<AnalyzerTrendResultEntry> m_From7to10ThenUp;
	private ArrayList<AnalyzerTrendResultEntry> m_From7to10DownThenUp;
	
	// Further outside Good Range
	private ArrayList<AnalyzerTrendResultEntry> m_From10to14ThenDown;
	private ArrayList<AnalyzerTrendResultEntry> m_From10to14UpThenDown;
	private ArrayList<AnalyzerTrendResultEntry> m_From10to14ThenLevel;
	private ArrayList<AnalyzerTrendResultEntry> m_From10to14ThenUp;
	private ArrayList<AnalyzerTrendResultEntry> m_From10to14DownThenUp;

	// Too high
	private ArrayList<AnalyzerTrendResultEntry> m_Hyper;


	
	private int m_HypoMaxCount = 0;              // An option can allow these to override directions
	
	// In Good Range
	private int m_From4to7ThenDownMaxCount = 0;
	private int m_From4to7UpThenDownMaxCount = 0;
	private int m_From4to7ThenLevelMaxCount = 0;
	private int m_From4to7ThenUpMaxCount = 0;
	private int m_From4to7DownThenUpMaxCount = 0;

	// Just outside Good Range
	private int m_From7to10ThenDownMaxCount = 0;
	private int m_From7to10UpThenDownMaxCount = 0;
	private int m_From7to10ThenLevelMaxCount = 0;
	private int m_From7to10ThenUpMaxCount = 0;
	private int m_From7to10DownThenUpMaxCount = 0;
	
	// Further outside Good Range
	private int m_From10to14ThenDownMaxCount = 0;
	private int m_From10to14UpThenDownMaxCount = 0;
	private int m_From10to14ThenLevelMaxCount = 0;
	private int m_From10to14ThenUpMaxCount = 0;
	private int m_From10to14DownThenUpMaxCount = 0;

	// Too high
	private int m_HyperMaxCount = 0;             // 

	AnalyzerTrendResultAggregateGroup()
	{
		m_Static_ID++;
		m_ID   = m_Static_ID;

		
		m_Hypo = new ArrayList<AnalyzerTrendResultEntry>();

		// In Good Range
		m_From4to7ThenDown = new ArrayList<AnalyzerTrendResultEntry>();
		m_From4to7UpThenDown = new ArrayList<AnalyzerTrendResultEntry>();
		m_From4to7ThenLevel = new ArrayList<AnalyzerTrendResultEntry>();
		m_From4to7ThenUp = new ArrayList<AnalyzerTrendResultEntry>();
		m_From4to7DownThenUp = new ArrayList<AnalyzerTrendResultEntry>();

		// Just outside Good Range
		m_From7to10ThenDown = new ArrayList<AnalyzerTrendResultEntry>();
		m_From7to10UpThenDown = new ArrayList<AnalyzerTrendResultEntry>();
		m_From7to10ThenLevel = new ArrayList<AnalyzerTrendResultEntry>();
		m_From7to10ThenUp = new ArrayList<AnalyzerTrendResultEntry>();
		m_From7to10DownThenUp = new ArrayList<AnalyzerTrendResultEntry>();
		
		// Further outside Good Range
		m_From10to14ThenDown = new ArrayList<AnalyzerTrendResultEntry>();
		m_From10to14UpThenDown = new ArrayList<AnalyzerTrendResultEntry>();
		m_From10to14ThenLevel = new ArrayList<AnalyzerTrendResultEntry>();
		m_From10to14ThenUp = new ArrayList<AnalyzerTrendResultEntry>();
		m_From10to14DownThenUp = new ArrayList<AnalyzerTrendResultEntry>();

		// Too high
		m_Hyper = new ArrayList<AnalyzerTrendResultEntry>();
	}
	
	public void initialize(ArrayList<AnalyzerTrendResultEntry> trendResultEntries)
	{		
		// Too high
		m_HypoMaxCount = buildList(trendResultEntries, 
				AnalyzerResultEntryInterval.DBResultEntryProfileDirection.Hypo,
				m_Hypo);
		
		// In Good Range
		m_From4to7ThenDownMaxCount = buildList(trendResultEntries, 
				AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From4to7ThenDown,
				m_From4to7ThenDown);
		m_From4to7UpThenDownMaxCount = buildList(trendResultEntries, 
				AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From4to7UpThenDown,
				m_From4to7UpThenDown);
		m_From4to7ThenLevelMaxCount = buildList(trendResultEntries, 
				AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From4to7ThenLevel,
				m_From4to7ThenLevel);
		m_From4to7ThenUpMaxCount = buildList(trendResultEntries, 
				AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From4to7ThenUp,
				m_From4to7ThenUp);
		m_From4to7DownThenUpMaxCount = buildList(trendResultEntries, 
				AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From4to7DownThenUp,
				m_From4to7DownThenUp);

		// Just outside Good Range
		m_From7to10ThenDownMaxCount = buildList(trendResultEntries, 
				AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From7to10ThenDown,
				m_From7to10ThenDown);
		m_From7to10UpThenDownMaxCount = buildList(trendResultEntries, 
				AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From7to10UpThenDown,
				m_From7to10UpThenDown);
		m_From7to10ThenLevelMaxCount = buildList(trendResultEntries, 
				AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From7to10ThenLevel,
				m_From7to10ThenLevel);
		m_From7to10ThenUpMaxCount = buildList(trendResultEntries, 
				AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From7to10ThenUp,
				m_From7to10ThenUp);
		m_From7to10DownThenUpMaxCount = buildList(trendResultEntries, 
				AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From7to10DownThenUp,
				m_From7to10DownThenUp);
		
		// Further outside Good Range
		m_From10to14ThenDownMaxCount = buildList(trendResultEntries, 
				AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From10to14ThenDown,
				m_From10to14ThenDown);
		m_From10to14UpThenDownMaxCount = buildList(trendResultEntries, 
				AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From10to14UpThenDown,
				m_From10to14UpThenDown);
		m_From10to14ThenLevelMaxCount = buildList(trendResultEntries, 
				AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From10to14ThenLevel,
				m_From10to14ThenLevel);
		m_From10to14ThenUpMaxCount = buildList(trendResultEntries, 
				AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From10to14ThenUp,
				m_From10to14ThenUp);
		m_From10to14DownThenUpMaxCount = buildList(trendResultEntries, 
				AnalyzerResultEntryInterval.DBResultEntryProfileDirection.From10to14DownThenUp,
				m_From10to14DownThenUp);

		// Too high
		m_HyperMaxCount = buildList(trendResultEntries, 
				AnalyzerResultEntryInterval.DBResultEntryProfileDirection.Hyper,
				m_Hyper);
	}
	
	private int getMaxCount(ArrayList<AnalyzerTrendResultEntry> list)
	{
		int result = 0;
		
		for (AnalyzerTrendResultEntry e : list)
		{
			int size = e.getM_ResultEntryIntervals().size();
			result   = size > result ? size : result;
		}

		return result;
	}
	
	public int getMaxCount()
	{
		int result = 0;
				
		
		
		result = m_HypoMaxCount > result ? m_HypoMaxCount : result;

		result = m_From4to7ThenDownMaxCount > result ? m_From4to7ThenDownMaxCount : result;
		result = m_From4to7UpThenDownMaxCount > result ? m_From4to7UpThenDownMaxCount : result;
		result = m_From4to7ThenLevelMaxCount > result ? m_From4to7ThenLevelMaxCount : result;
		result = m_From4to7ThenUpMaxCount > result ? m_From4to7ThenUpMaxCount : result;
		result = m_From4to7DownThenUpMaxCount > result ? m_From4to7DownThenUpMaxCount : result;

		result = m_From7to10ThenDownMaxCount > result ? m_From7to10ThenDownMaxCount : result;
		result = m_From7to10UpThenDownMaxCount > result ? m_From7to10UpThenDownMaxCount : result;
		result = m_From7to10ThenLevelMaxCount > result ? m_From7to10ThenLevelMaxCount : result;
		result = m_From7to10ThenUpMaxCount > result ? m_From7to10ThenUpMaxCount : result;
		result = m_From7to10DownThenUpMaxCount > result ? m_From7to10DownThenUpMaxCount : result;

		result = m_From10to14ThenDownMaxCount > result ? m_From10to14ThenDownMaxCount : result;
		result = m_From10to14UpThenDownMaxCount > result ? m_From10to14UpThenDownMaxCount : result;
		result = m_From10to14ThenLevelMaxCount > result ? m_From10to14ThenLevelMaxCount : result;
		result = m_From10to14ThenUpMaxCount > result ? m_From10to14ThenUpMaxCount : result;
		result = m_From10to14DownThenUpMaxCount > result ? m_From10to14DownThenUpMaxCount : result;

		result = m_HyperMaxCount > result ? m_HyperMaxCount : result;

		
		return result;
	}


	private int buildList(ArrayList<AnalyzerTrendResultEntry> src,
			AnalyzerResultEntryInterval.DBResultEntryProfileDirection profile,
			ArrayList<AnalyzerTrendResultEntry> target)
	{
		int result = 0;
		
		// Sort the source list by time
		Collections.sort(src, new AnalyzerTrendResultEntryComparator(true, 
				AnalyzerTrendResultEntryComparatorType.TimeSort));

		// Now iterate over the source list and add entries to Dest if they match
		for (AnalyzerTrendResultEntry c : src)
		{
			if (c.getM_ProfileDirection() == profile)
			{
				target.add(c);
			}
		}
		
		result = getMaxCount(target);
		
		return result;
	}

	public synchronized static void resetStaticID()
	{
		m_Static_ID = 0;
	}

	/**
	 * @return the m_ID
	 */
	public synchronized int getM_ID() {
		return m_ID;
	}

	/**
	 * @return the m_Hypo
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_Hypo() {
		return m_Hypo;
	}

	/**
	 * @return the m_From4to7ThenDown
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_From4to7ThenDown() {
		return m_From4to7ThenDown;
	}

	/**
	 * @return the m_From4to7UpThenDown
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_From4to7UpThenDown() {
		return m_From4to7UpThenDown;
	}

	/**
	 * @return the m_From4to7ThenLevel
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_From4to7ThenLevel() {
		return m_From4to7ThenLevel;
	}

	/**
	 * @return the m_From4to7ThenUp
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_From4to7ThenUp() {
		return m_From4to7ThenUp;
	}

	/**
	 * @return the m_From4to7DownThenUp
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_From4to7DownThenUp() {
		return m_From4to7DownThenUp;
	}

	/**
	 * @return the m_From7to10ThenDown
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_From7to10ThenDown() {
		return m_From7to10ThenDown;
	}

	/**
	 * @return the m_From7to10UpThenDown
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_From7to10UpThenDown() {
		return m_From7to10UpThenDown;
	}

	/**
	 * @return the m_From7to10ThenLevel
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_From7to10ThenLevel() {
		return m_From7to10ThenLevel;
	}

	/**
	 * @return the m_From7to10ThenUp
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_From7to10ThenUp() {
		return m_From7to10ThenUp;
	}

	/**
	 * @return the m_From7to10DownThenUp
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_From7to10DownThenUp() {
		return m_From7to10DownThenUp;
	}

	/**
	 * @return the m_From10to14ThenDown
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_From10to14ThenDown() {
		return m_From10to14ThenDown;
	}

	/**
	 * @return the m_From10to14UpThenDown
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_From10to14UpThenDown() {
		return m_From10to14UpThenDown;
	}

	/**
	 * @return the m_From10to14ThenLevel
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_From10to14ThenLevel() {
		return m_From10to14ThenLevel;
	}

	/**
	 * @return the m_From10to14ThenUp
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_From10to14ThenUp() {
		return m_From10to14ThenUp;
	}

	/**
	 * @return the m_From10to14DownThenUp
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_From10to14DownThenUp() {
		return m_From10to14DownThenUp;
	}

	/**
	 * @return the m_Hyper
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_Hyper() {
		return m_Hyper;
	}

	/**
	 * @return the m_HypoMaxCount
	 */
	public synchronized int getM_HypoMaxCount() {
		return m_HypoMaxCount;
	}

	/**
	 * @return the m_From4to7ThenDownMaxCount
	 */
	public synchronized int getM_From4to7ThenDownMaxCount() {
		return m_From4to7ThenDownMaxCount;
	}

	/**
	 * @return the m_From4to7UpThenDownMaxCount
	 */
	public synchronized int getM_From4to7UpThenDownMaxCount() {
		return m_From4to7UpThenDownMaxCount;
	}

	/**
	 * @return the m_From4to7ThenLevelMaxCount
	 */
	public synchronized int getM_From4to7ThenLevelMaxCount() {
		return m_From4to7ThenLevelMaxCount;
	}

	/**
	 * @return the m_From4to7ThenUpMaxCount
	 */
	public synchronized int getM_From4to7ThenUpMaxCount() {
		return m_From4to7ThenUpMaxCount;
	}

	/**
	 * @return the m_From4to7DownThenUpMaxCount
	 */
	public synchronized int getM_From4to7DownThenUpMaxCount() {
		return m_From4to7DownThenUpMaxCount;
	}

	/**
	 * @return the m_From7to10ThenDownMaxCount
	 */
	public synchronized int getM_From7to10ThenDownMaxCount() {
		return m_From7to10ThenDownMaxCount;
	}

	/**
	 * @return the m_From7to10UpThenDownMaxCount
	 */
	public synchronized int getM_From7to10UpThenDownMaxCount() {
		return m_From7to10UpThenDownMaxCount;
	}

	/**
	 * @return the m_From7to10ThenLevelMaxCount
	 */
	public synchronized int getM_From7to10ThenLevelMaxCount() {
		return m_From7to10ThenLevelMaxCount;
	}

	/**
	 * @return the m_From7to10ThenUpMaxCount
	 */
	public synchronized int getM_From7to10ThenUpMaxCount() {
		return m_From7to10ThenUpMaxCount;
	}

	/**
	 * @return the m_From7to10DownThenUpMaxCount
	 */
	public synchronized int getM_From7to10DownThenUpMaxCount() {
		return m_From7to10DownThenUpMaxCount;
	}

	/**
	 * @return the m_From10to14ThenDownMaxCount
	 */
	public synchronized int getM_From10to14ThenDownMaxCount() {
		return m_From10to14ThenDownMaxCount;
	}

	/**
	 * @return the m_From10to14UpThenDownMaxCount
	 */
	public synchronized int getM_From10to14UpThenDownMaxCount() {
		return m_From10to14UpThenDownMaxCount;
	}

	/**
	 * @return the m_From10to14ThenLevelMaxCount
	 */
	public synchronized int getM_From10to14ThenLevelMaxCount() {
		return m_From10to14ThenLevelMaxCount;
	}

	/**
	 * @return the m_From10to14ThenUpMaxCount
	 */
	public synchronized int getM_From10to14ThenUpMaxCount() {
		return m_From10to14ThenUpMaxCount;
	}

	/**
	 * @return the m_From10to14DownThenUpMaxCount
	 */
	public synchronized int getM_From10to14DownThenUpMaxCount() {
		return m_From10to14DownThenUpMaxCount;
	}

	/**
	 * @return the m_HyperMaxCount
	 */
	public synchronized int getM_HyperMaxCount() {
		return m_HyperMaxCount;
	}
	
}
