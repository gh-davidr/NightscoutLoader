package davidRichardson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class AnalyzerEntries 
{
	private ArrayList<DBResultEntry>                   m_DBResultEntries;
	private ArrayList<DBResult>                        m_DBResults;
	private ArrayList<DBResultEntry>                   m_InRangeDBResultEntries;  // Used for trend analysis - results that overlap with date range being used

	private Date                                       m_StartDate = null;
	private Date                                       m_EndDate   = null;

	private Date                                       m_TreatmentsStartDate = null;
	private Date                                       m_TreatmentsEndDate   = null;

	private ArrayList<AnalyzerEntriesCGMRange>         m_CGMRanges;
	private ArrayList<AnalyzerResultEntryInterval>     m_ResultEntryIntervals;

	// Trend Result Entries that align within same hour and also offset by +- 2 hours
	private ArrayList<AnalyzerTrendResultEntry>        m_TrendResultEntries;
	private ArrayList<AnalyzerTrendResultEntry>        m_TrendResultEntriesPlusOne;
	private ArrayList<AnalyzerTrendResultEntry>        m_TrendResultEntriesPlusTwo;
	private ArrayList<AnalyzerTrendResultEntry>        m_TrendResultEntriesMinusOne;
	private ArrayList<AnalyzerTrendResultEntry>        m_TrendResultEntriesMinusTwo;

	private AnalyzerTrendResultEntryAggregate          m_TrendResultEntriesAggregates;

	/*	public AnalyzerEntries(ArrayList<DBResultEntry> resultEntries) 
	{
		super();
		m_DBResultEntries            = new ArrayList<DBResultEntry>(resultEntries);
		m_CGMRanges                  = new ArrayList<AnalyzerEntriesCGMRange>();
		m_ResultEntryIntervals       = new ArrayList<AnalyzerResultEntryInterval>();
		m_TrendResultEntries         = new ArrayList<AnalyzerTrendResultEntry>();
		m_TrendResultEntriesPlusOne  = new ArrayList<AnalyzerTrendResultEntry>();
		m_TrendResultEntriesPlusTwo  = new ArrayList<AnalyzerTrendResultEntry>();
		m_TrendResultEntriesMinusOne = new ArrayList<AnalyzerTrendResultEntry>();
		m_TrendResultEntriesMinusTwo = new ArrayList<AnalyzerTrendResultEntry>();

		m_TrendResultEntriesAggregates = new AnalyzerTrendResultEntryAggregate();
	}*/

	public AnalyzerEntries(ArrayList<DBResultEntry> resultEntries,
			ArrayList<DBResult> results) 
	{
		super();
		m_DBResultEntries            = new ArrayList<DBResultEntry>(resultEntries);
		m_DBResults                  = new ArrayList<DBResult>(results);
		m_CGMRanges                  = new ArrayList<AnalyzerEntriesCGMRange>();
		m_ResultEntryIntervals       = new ArrayList<AnalyzerResultEntryInterval>();
		m_TrendResultEntries         = new ArrayList<AnalyzerTrendResultEntry>();
		m_TrendResultEntriesPlusOne  = new ArrayList<AnalyzerTrendResultEntry>();
		m_TrendResultEntriesPlusTwo  = new ArrayList<AnalyzerTrendResultEntry>();
		m_TrendResultEntriesMinusOne = new ArrayList<AnalyzerTrendResultEntry>();
		m_TrendResultEntriesMinusTwo = new ArrayList<AnalyzerTrendResultEntry>();

		m_TrendResultEntriesAggregates = new AnalyzerTrendResultEntryAggregate();

		if (m_DBResults.size() > 0)
		{
			m_TreatmentsStartDate = new Date(m_DBResults.get(0).getM_Time().getTime());
			m_TreatmentsEndDate   = new Date(m_DBResults.get(m_DBResults.size() - 1).getM_Time().getTime());
		}
	}
	public void initialize(ArrayList<DBResultEntry> resultEntries)
	{
		// Reset all the m_ID static counters this analyzer is responsible for
		AnalyzerEntriesCGMRange.resetStaticID();
		AnalyzerResultEntryInterval.resetStaticID();
		AnalyzerTrendResultEntry.resetStaticID();

		// Retain copy of Nightscout Results
		m_DBResultEntries           = new ArrayList<DBResultEntry>(resultEntries);
		m_InRangeDBResultEntries    = new ArrayList<DBResultEntry>();

		// Determine CGM Ranges at earliest opportunity.
		determineCGMRanges();
	}

	private void identifyInRangeDBResultEntries()
	{
		m_InRangeDBResultEntries = new ArrayList<DBResultEntry>();

		for (DBResultEntry e : m_DBResultEntries)
		{
			if (e.getM_SGV() != null && // Ensure there's a valid Sugar Value
					CommonUtils.isTimeBetween(m_StartDate, m_EndDate, e.getM_UTCDate()))
			{
				m_InRangeDBResultEntries.add(e);
			}
		}
	}

	public void analyzeResults(long startDateLong, long endDateLong)
	{
		m_StartDate = new Date(startDateLong);
		m_EndDate   = new Date(endDateLong);

		identifyInRangeDBResultEntries();

		groupResultEntriesIntoIntervals();
		determineTrendResultEntries();		
		determineTrendResultEntryAggregates();
	}

	private void determineCGMRanges()
	{
		final Date epochDate          = new Date(0);
		Date prevDate                 = new Date(0);
		AnalyzerEntriesCGMRange range = null;

		// First off, sort the results in ascending date order
		Collections.sort(m_DBResultEntries, new ResultFromDBComparator(false));

		// addDaysToDate

		// Use the full CGM results to do this...
		for (DBResultEntry c : m_DBResultEntries)
		{
			Date thisDate = c.getM_UTCDate();

			// Special handling for first entry
			if (range == null && prevDate.equals(epochDate))
			{
				// Only add valid dates...
				if (thisDate.getTime() > 0)
				{
					range = new AnalyzerEntriesCGMRange(thisDate, m_TreatmentsStartDate, m_TreatmentsEndDate);
					m_CGMRanges.add(range);
					prevDate = thisDate;
				}
			}
			else
			{
				// Only advance on valid dates...
				if (thisDate.getTime() > 0)
				{
					Date nextDate = CommonUtils.addDaysToDate(prevDate, 1);
					if (!CommonUtils.isDateTheSame(thisDate, prevDate)      // Not same date as before
							&& !CommonUtils.isDateTheSame(thisDate, nextDate))  // Not the next date
					{
						// Then we have a gap
						range.endRange(prevDate);
						range = new AnalyzerEntriesCGMRange(thisDate, m_TreatmentsStartDate, m_TreatmentsEndDate);
						m_CGMRanges.add(range);
					}
					prevDate = thisDate;
				}
			}
			if (range != null)
			{
				range.addDBResultEntry(c);
			}

		}
		if (range != null)
		{
			range.endRange(prevDate);
		}

	}


	// Iterate through the entire CGM Entry result set
	// Put them into Single Result Entries that group by the defined
	// interval
	private void groupResultEntriesIntoIntervals()
	{
		// Iterate through the DBResultEntries and create Single Result Entries based on time.

		// What's the interval in hours?
		int interval = PrefsNightScoutLoader.getInstance().getM_EntryAnalyzerIntervalHours();

		AnalyzerResultEntryInterval curr = null;		
		int endHour = 0;

		// Use the in-range entries here
		for (DBResultEntry c : m_InRangeDBResultEntries)
		{			
			// Now enter a loop, attempting to add entries until that fails
			while( (curr == null) || (curr != null && curr.addDBResultEntry(c) != true) )
			{
				// Close out the interval and only add if entries have been added
				if (curr != null && curr.getM_DBResultEntries().size() > 0)
				{
					curr.analyzeResults();
					this.m_ResultEntryIntervals.add(curr);
				}

				// Create next interval entry
				curr = new AnalyzerResultEntryInterval(
						CommonUtils.setDateToParticularHour(c.getM_UTCDate(), endHour),
						CommonUtils.setDateToParticularHour(c.getM_UTCDate(), endHour + interval));

				// Advance endHour on by Interval
				endHour += interval;

				// If we go too far, then reset back to midnight 
				// to start all over again on the next day
				if (endHour >= 24 )
				{
					endHour = 0;
				}
			}
		}
		if (curr != null)
		{
			curr.analyzeResults();
		}
	}

	//	private AnalyzerResultEntryInterval locateAnalyzerResultEntryInterval(int hour)
	//	{
	//		AnalyzerResultEntryInterval result = null;
	//
	//		for (AnalyzerResultEntryInterval e : m_ResultEntryIntervals)
	//		{
	//			if (hour >= CommonUtils.get24Hour(e.getM_PeriodStart()) && hour < CommonUtils.get24Hour(e.getM_PeriodEnd()))
	//			{
	//				result = e;
	//				break;
	//			}
	//		}
	//
	//		return result;
	//	}


	private void determineTrendResultEntries()
	{
		determineTrendResultEntries(m_TrendResultEntries,          0);
		// 
		// Let's not do the shifted analysis for now
		/*
		determineTrendResultEntries(m_TrendResultEntriesPlusOne,   1);
		determineTrendResultEntries(m_TrendResultEntriesPlusTwo,   2);
		determineTrendResultEntries(m_TrendResultEntriesMinusOne, -1);
		determineTrendResultEntries(m_TrendResultEntriesMinusTwo, -2);
		 */
	}

	// Iterate through the entire CGM Entry result set
	// Put them into Single Result Entries that group by the defined
	// interval
	private void determineTrendResultEntries(ArrayList<AnalyzerTrendResultEntry> trendResultList,
			int offset)
	{
		// Iterate through the DBResultEntries and create Single Result Entries based on time.
		for (AnalyzerResultEntryInterval c : m_ResultEntryIntervals)
		{
			AnalyzerTrendResultEntry curr = null;
			// Find matching entry for single result entry
			for (AnalyzerTrendResultEntry t : trendResultList)
			{
				if (t.addSingleResultEntry(c))
				{
					curr = t;
					break;
				}
			}

			// Create the first slot or an extra one if a match can't be found.
			if (curr == null)
			{
				curr = new AnalyzerTrendResultEntry(c.getM_GoesHypo(), c.getM_GoesHyper(), 
						c.getM_StartProfile(), c.getM_EndProfile(), 
						c.getM_ProfileDirection(),
						CommonUtils.get24Hour(c.getM_PeriodStart()), 
						CommonUtils.get24Hour(c.getM_PeriodEnd()), offset);

				// Need to add the entry - duhh!
				curr.addSingleResultEntry(c);

				trendResultList.add(curr);
			}
		}

		// Now sort the list
		Collections.sort(trendResultList, new AnalyzerTrendResultEntryComparator(true));

	}

	private void determineTrendResultEntryAggregates()
	{
		m_TrendResultEntriesAggregates.initialize(m_TrendResultEntries,
				m_TrendResultEntriesPlusOne,
				m_TrendResultEntriesPlusTwo,
				m_TrendResultEntriesMinusOne,
				m_TrendResultEntriesMinusTwo);
	}

	/**
	 * @return the m_DBResultEntries
	 */
	public synchronized ArrayList<DBResultEntry> getM_DBResultEntries() {
		return m_DBResultEntries;
	}

	/**
	 * @param m_DBResultEntries the m_DBResultEntries to set
	 */
	public synchronized void setM_DBResultEntries(ArrayList<DBResultEntry> m_DBResultEntries) {
		this.m_DBResultEntries = m_DBResultEntries;
	}

	/**
	 * @return the m_InRangeDBResultEntries
	 */
	public synchronized ArrayList<DBResultEntry> getM_InRangeDBResultEntries() {
		return m_InRangeDBResultEntries;
	}

	/**
	 * @return the m_CGMRanges
	 */
	public synchronized ArrayList<AnalyzerEntriesCGMRange> getM_CGMRanges() {
		return m_CGMRanges;
	}

	/**
	 * @param m_CGMRanges the m_CGMRanges to set
	 */
	public synchronized void setM_CGMRanges(ArrayList<AnalyzerEntriesCGMRange> m_CGMRanges) {
		this.m_CGMRanges = m_CGMRanges;
	}

	/**
	 * @return the m_ResultEntryIntervals
	 */
	public synchronized ArrayList<AnalyzerResultEntryInterval> getM_ResultEntryIntervals() {
		return m_ResultEntryIntervals;
	}

	/**
	 * @return the m_TrendResultEntries
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_TrendResultEntries() {
		return m_TrendResultEntries;
	}

	/**
	 * @return the m_TrendResultEntriesPlusOne
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_TrendResultEntriesPlusOne() {
		return m_TrendResultEntriesPlusOne;
	}

	/**
	 * @return the m_TrendResultEntriesPlusTwo
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_TrendResultEntriesPlusTwo() {
		return m_TrendResultEntriesPlusTwo;
	}

	/**
	 * @return the m_TrendResultEntriesMinusOne
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_TrendResultEntriesMinusOne() {
		return m_TrendResultEntriesMinusOne;
	}

	/**
	 * @return the m_TrendResultEntriesMinusTwo
	 */
	public synchronized ArrayList<AnalyzerTrendResultEntry> getM_TrendResultEntriesMinusTwo() {
		return m_TrendResultEntriesMinusTwo;
	}

	/**
	 * @return the m_TrendResultEntriesAggregates
	 */
	public synchronized AnalyzerTrendResultEntryAggregate getM_TrendResultEntriesAggregates() {
		return m_TrendResultEntriesAggregates;
	}

}
