package davidRichardson;

import java.util.ArrayList;

public class AnalyzerTrendResultEntryAggregate 
{
	// David -- getting tired
	// Group these into a single class for easier access.
	// 16 Dec 2016

	private static Integer      m_Static_ID = 0;  // Assign an ID to each result created.
	private int                 m_ID        = 0;  // Assign an ID to each result created.
	
	private AnalyzerTrendResultAggregateGroup m_OffsetMinusTwoAggregateGroup;
	private AnalyzerTrendResultAggregateGroup m_OffsetMinusOneAggregateGroup;
	private AnalyzerTrendResultAggregateGroup m_NoOffsetAggregateGroup;
	private AnalyzerTrendResultAggregateGroup m_OffsetPlusOneAggregateGroup;
	private AnalyzerTrendResultAggregateGroup m_OffsetPlusTwoAggregateGroup;

	AnalyzerTrendResultEntryAggregate()
	{
		m_Static_ID++;
		m_ID   = m_Static_ID;

		m_OffsetMinusTwoAggregateGroup = new AnalyzerTrendResultAggregateGroup();
		m_OffsetMinusOneAggregateGroup = new AnalyzerTrendResultAggregateGroup();
		m_NoOffsetAggregateGroup       = new AnalyzerTrendResultAggregateGroup();
		m_OffsetPlusOneAggregateGroup  = new AnalyzerTrendResultAggregateGroup();
		m_OffsetPlusTwoAggregateGroup  = new AnalyzerTrendResultAggregateGroup();
	}

	public void initialize(ArrayList<AnalyzerTrendResultEntry> trendResultEntries,
			ArrayList<AnalyzerTrendResultEntry>        trendResultEntriesPlusOne,
			ArrayList<AnalyzerTrendResultEntry>        trendResultEntriesPlusTwo,
			ArrayList<AnalyzerTrendResultEntry>        trendResultEntriesMinusOne,
			ArrayList<AnalyzerTrendResultEntry>        trendResultEntriesMinusTwo)
	{
		m_OffsetMinusTwoAggregateGroup.initialize(trendResultEntriesMinusTwo);
		m_OffsetMinusOneAggregateGroup.initialize(trendResultEntriesMinusOne);
		m_NoOffsetAggregateGroup.initialize(trendResultEntries);
		m_OffsetPlusOneAggregateGroup.initialize(trendResultEntriesPlusOne);
		m_OffsetPlusTwoAggregateGroup.initialize(trendResultEntriesPlusTwo);
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
	 * @return the m_OffsetMinusTwoAggregateGroup
	 */
	public synchronized AnalyzerTrendResultAggregateGroup getM_OffsetMinusTwoAggregateGroup() {
		return m_OffsetMinusTwoAggregateGroup;
	}

	/**
	 * @return the m_OffsetMinusOneAggregateGroup
	 */
	public synchronized AnalyzerTrendResultAggregateGroup getM_OffsetMinusOneAggregateGroup() {
		return m_OffsetMinusOneAggregateGroup;
	}

	/**
	 * @return the m_NoOffsetAggregateGroup
	 */
	public synchronized AnalyzerTrendResultAggregateGroup getM_NoOffsetAggregateGroup() {
		return m_NoOffsetAggregateGroup;
	}

	/**
	 * @return the m_OffsetPlusOneAggregateGroup
	 */
	public synchronized AnalyzerTrendResultAggregateGroup getM_OffsetPlusOneAggregateGroup() {
		return m_OffsetPlusOneAggregateGroup;
	}

	/**
	 * @return the m_OffsetPlusTwoAggregateGroup
	 */
	public synchronized AnalyzerTrendResultAggregateGroup getM_OffsetPlusTwoAggregateGroup() {
		return m_OffsetPlusTwoAggregateGroup;
	}

}
