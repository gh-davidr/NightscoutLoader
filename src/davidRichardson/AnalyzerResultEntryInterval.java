package davidRichardson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


// 11 Jan 2017
// David
//   Add a reference to AnalyzerResultEntryInterval in DBResultEntry
//   Then when generating the CGM Results tab, can show how the result is categorized :-)


public class AnalyzerResultEntryInterval 
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	private static Integer      m_Static_ID = 0;  // Assign an ID to each result created.
	private int                 m_ID        = 0;  // Assign an ID to each result created.

	// Want to record in this the following types of observations
	//   Goes Hypo
	//   Goes Hyper
	//   Steep Rise
	//   Steep Fall
	//   Turbulent (rise and fall)
	//   Good result (steady)
	//
	// This is all going to come from reading the CGM results

	// What shape do we have.
	// There are at most 4
	enum DBResultEntryProfileChange
	{
		Unknown,

		SteepRise,    // Steep to be decided somehow
		SteepFall,    // As above
		Turbulent,    // Turbulent means both a rise and a fall
		GoodResult,   // Basically, none of the above

		/*
		 * 
		 * Start Hypo
		 *      Falling
		 *      Flat
		 *      Rising
		 *      Turbulent
		 * 
		 * Start In Range
		 *      Falling
		 *      Flat
		 *      Rising
		 *      Turbulent
		 * 
		 * Start Above Range
		 *      Falling
		 *      Flat
		 *      Rising
		 *      Turbulent
		 * 
		 * Start Hyper
		 *      Falling
		 *      Flat
		 *      Rising
		 *      Turbulent
		 * 
		 */

	};

	enum DBResultEntryProfile_orig
	{
		Unknown,

		Hypo,
		InRange,
		AboveRange,
		Hyper
	};

	enum DBResultEntryProfile
	{
		Unknown,

		Hypo,
		From4to7,
		From7to10,
		From10to14,
		Hyper
	};

	
	enum DBResultEntryProfileDirection_orig
	{
		Unknown,

		Hypo,              // An option can allow these to override directions
		Hyper,             // 

		// Start value is between 4 and 10 mmol/L
		Falling4to10,            // Initial Profile is higher than end and no rises in between
		FallingTurbulence4to10,  // Initial Profile is higher than end and also rises in between
		Flat4to10,               // Within tolerances, initial profile is same as end and minimal fluctuations
		Rising4to10,             // Initial Profile is lower than end and no falls in between
		RisingTurbulence4to10,   // Initial Profile is lower than end and also falls in between

		// Start value is between 10 and 15 mmol/L
		Falling10to15,           // Initial Profile is higher than end and no rises in between
		FallingTurbulence10to15, // Initial Profile is higher than end and also rises in between
		Flat10to15,              // Within tolerances, initial profile is same as end and minimal fluctuations
		Rising10to15,            // Initial Profile is lower than end and no falls in between
		RisingTurbulence10to15,  // Initial Profile is lower than end and also falls in between

	};	
	
	enum DBResultEntryProfileDirection
	{
		Unknown,

		// Too Low
		Hypo,              // An option can allow these to override directions
		
		// In Good Range
		From4to7ThenDown,
		From4to7UpThenDown,
		From4to7ThenLevel,
		From4to7ThenUp,
		From4to7DownThenUp,

		// Just outside Good Range
		From7to10ThenDown,
		From7to10UpThenDown,
		From7to10ThenLevel,
		From7to10ThenUp,
		From7to10DownThenUp,
		
		// Further outside Good Range
		From10to14ThenDown,
		From10to14UpThenDown,
		From10to14ThenLevel,
		From10to14ThenUp,
		From10to14DownThenUp,

		// Too high
		Hyper,             // 

	};	


	// Periods are 0 - 24
	// This can be collected over single hours and also grouped together too
	private Date  m_PeriodStart;
	private Date  m_PeriodEnd;


	private Boolean                       m_GoesHypo;
	private Boolean                       m_GoesHyper;
	private DBResultEntryProfileChange    m_DBResultEntryProfileChange;
	private DBResultEntryProfile          m_StartProfile;
	private DBResultEntryProfile          m_EndProfile;
	private DBResultEntryProfileDirection m_ProfileDirection;
	private ArrayList<DBResultEntry>      m_DBResultEntries;
	private Double                        m_AverageBG;
	
	// THis class holds several intervals
	private AnalyzerTrendResultEntry      m_AnalyzerTrendResultEntry;

	public AnalyzerResultEntryInterval(Date periodStart, Date periodEnd)
	{
		m_Static_ID++;
		m_ID   = m_Static_ID;

		m_Logger.log(Level.FINE, "Just built AnalyzerEntriesCGMDay " + m_ID + " Start: " + 
				periodStart.toString() + " End: " + periodEnd.toString()); 

		m_PeriodStart                = periodStart;
		m_PeriodEnd                  = periodEnd;

		m_GoesHypo                   = false;
		m_GoesHyper                  = false;
		m_DBResultEntryProfileChange = DBResultEntryProfileChange.Unknown;
		m_StartProfile               = DBResultEntryProfile.Unknown;
		m_EndProfile                 = DBResultEntryProfile.Unknown;
		m_ProfileDirection           = DBResultEntryProfileDirection.Unknown;

		m_DBResultEntries            = new ArrayList<DBResultEntry>();
		m_AverageBG                  = 0.0;
	}

	// Responsibility for determining whether result is in this list falls here.
	// Calling class iterates over results in chronolgical sequence so if this returns
	// false, it then allocates a new interval
	public boolean addDBResultEntry(DBResultEntry e)
	{
		boolean result = false;
		Date eDate     = e.getM_UTCDate();

		result = ((m_PeriodStart.before(eDate) || m_PeriodStart.equals(eDate)) &&
				(m_PeriodEnd.after(eDate)))
				? true : false;

		if (result)
		{
			// Do some basic sanity checks before adding to our list.
			if ( (e.getM_SGV() != null) )
			{
				m_DBResultEntries.add(e);

				// Having added this DBResultEntry, tell it what's added it.
				e.setM_AnalyzerResultEntryInterval(this);
			}
		}
		return result;
	}

	public void analyzeResults()
	{
		// Having poplulated the arraylist, now iterate over it and 
		// determine the various results
		
		// First off, sort the list to ensure it's ordered in date/time order
		Collections.sort(m_DBResultEntries, new ResultFromDBComparator());

		if (m_DBResultEntries.size() > 0)
		{
			Double startSGV = m_DBResultEntries.get(0).getM_SGV();
			Double endSGV   = m_DBResultEntries.get(m_DBResultEntries.size()-1).getM_SGV();

			Double startBG  = m_DBResultEntries.get(0).getM_BG();
			Double endBG    = m_DBResultEntries.get(m_DBResultEntries.size()-1).getM_BG();
			
			Double hypo            = PrefsNightScoutLoader.getInstance().getM_AnalyzerLowRangeThreshold();
			Double hyper           = PrefsNightScoutLoader.getInstance().getM_AnalyzerHighThreshold();
			Double changeThreshold = PrefsNightScoutLoader.getInstance().getM_EntryAnalyzerSteepChange();
			boolean extremesOverrideDirection = PrefsNightScoutLoader.getInstance().isM_EntryExtremesOverrideDirection();

			// Set Start & End Profile
			m_StartProfile = getEntryProfile(startBG);
			m_EndProfile   = getEntryProfile(endBG);

			if (m_StartProfile == DBResultEntryProfile.Hypo)
			{
				m_ProfileDirection = DBResultEntryProfileDirection.Hypo;
			}
			else if (m_StartProfile == DBResultEntryProfile.Hyper)
			{
				m_ProfileDirection = DBResultEntryProfileDirection.Hyper;
			}
			
			// If difference > threshold then assume a steep rise or fall initially.
			else if (endBG - startBG > changeThreshold)
			{
				switch (m_StartProfile)
				{
				case From4to7:   m_ProfileDirection = DBResultEntryProfileDirection.From4to7ThenUp; break;
				case From7to10:  m_ProfileDirection = DBResultEntryProfileDirection.From7to10ThenUp; break;
				case From10to14: m_ProfileDirection = DBResultEntryProfileDirection.From10to14ThenUp; break;
				default: break;
				}
				m_DBResultEntryProfileChange = DBResultEntryProfileChange.SteepRise;
			}
			else if (startBG - endBG > changeThreshold)
			{
				switch (m_StartProfile)
				{
				case From4to7:   m_ProfileDirection = DBResultEntryProfileDirection.From4to7ThenDown; break;
				case From7to10:  m_ProfileDirection = DBResultEntryProfileDirection.From7to10ThenDown; break;
				case From10to14: m_ProfileDirection = DBResultEntryProfileDirection.From10to14ThenDown; break;
				default: break;
				}
				m_DBResultEntryProfileChange = DBResultEntryProfileChange.SteepFall;
			}

			Double totSGV = 0.0;

			for (DBResultEntry e : m_DBResultEntries)
			{
				Double sgv = e.getM_SGV();
				Double bg  = e.getM_BG();
				totSGV += sgv;

				// Rise       ==> startSGV  <  endSGV
				// Rise Turb  ==> startSGV  <  endSGV   && any mid sgv > endSGV  OR mid sgv < startSGV

				// Fall       ==> startSGV  >  endSGV
				// Fall Turb  ==> startSGV  >  endSGV   && any mid sgv < endSGV  OR mid sgv > startSGV

				if ( (m_DBResultEntryProfileChange    == DBResultEntryProfileChange.SteepRise && sgv < startSGV) || 
						(m_DBResultEntryProfileChange == DBResultEntryProfileChange.SteepRise && sgv > endSGV))
				{

					switch (m_StartProfile)
					{
					case From4to7:   m_ProfileDirection = DBResultEntryProfileDirection.From4to7DownThenUp; break;
					case From7to10:  m_ProfileDirection = DBResultEntryProfileDirection.From7to10DownThenUp; break;
					case From10to14: m_ProfileDirection = DBResultEntryProfileDirection.From10to14DownThenUp; break;
					default: break;
					}
					m_DBResultEntryProfileChange = DBResultEntryProfileChange.Turbulent;
				}
				if (( m_DBResultEntryProfileChange    == DBResultEntryProfileChange.SteepFall && sgv > startSGV) || 
						(m_DBResultEntryProfileChange == DBResultEntryProfileChange.SteepFall && sgv < endSGV))
				{
					switch (m_StartProfile)
					{
					case From4to7:   m_ProfileDirection = DBResultEntryProfileDirection.From4to7UpThenDown; break;
					case From7to10:  m_ProfileDirection = DBResultEntryProfileDirection.From7to10UpThenDown; break;
					case From10to14: m_ProfileDirection = DBResultEntryProfileDirection.From10to14UpThenDown; break;
					default: break;
					}
					
					m_DBResultEntryProfileChange = DBResultEntryProfileChange.Turbulent;
				}

				if (bg < hypo)
				{
					m_GoesHypo = true;
					if (extremesOverrideDirection == true)
					{
						m_ProfileDirection = DBResultEntryProfileDirection.Hypo;
						break;
					}
				}
				if (bg > hyper)
				{
					m_GoesHyper = true;
					if (extremesOverrideDirection == true)
					{
						m_ProfileDirection = DBResultEntryProfileDirection.Hyper;
						break;
					}
				}
			}

			// If at the end of all this, we are still unknown then must be fairly stable
			if (m_ProfileDirection == DBResultEntryProfileDirection.Unknown)
			{
				switch (m_StartProfile)
				{
				case From4to7:   m_ProfileDirection = DBResultEntryProfileDirection.From4to7ThenLevel; break;
				case From7to10:  m_ProfileDirection = DBResultEntryProfileDirection.From7to10ThenLevel; break;
				case From10to14: m_ProfileDirection = DBResultEntryProfileDirection.From10to14ThenLevel; break;
				default: break;
				}

				m_DBResultEntryProfileChange = DBResultEntryProfileChange.GoodResult;
			}

			// SGV always in mmol/dL
			// Convert as necessary
			m_AverageBG = (totSGV / (m_DBResultEntries.size() * 18.0));
		}
	}

	private DBResultEntryProfile getEntryProfile(Double bg)
	{
		DBResultEntryProfile result = DBResultEntryProfile.Unknown;

		// Set StartProfile
		if (bg < 4.0)
		{
			result = DBResultEntryProfile.Hypo;
		}
		else if (bg >= 4.0 && bg < 7.0)
		{
			result = DBResultEntryProfile.From4to7;
		}
		else if (bg >= 7.0 && bg < 10.0)
		{
			result = DBResultEntryProfile.From7to10;
		}
		else if (bg >= 10.0 && bg < 14.0)
		{
			result = DBResultEntryProfile.From10to14;
		}
		else if (bg >= 14.0)
		{
			result = DBResultEntryProfile.Hyper;
		}

		return result;
	}



	public class AnalyzerResultEntryIntervalComparator implements Comparator<AnalyzerResultEntryInterval> 
	{		
		private boolean m_DescendingSort = true;
		AnalyzerResultEntryIntervalComparator()
		{
			m_DescendingSort = true;
		}
		AnalyzerResultEntryIntervalComparator(boolean descendingSort)
		{
			m_DescendingSort = descendingSort;
		}

		public int compare(AnalyzerResultEntryInterval p1, AnalyzerResultEntryInterval p2) 
		{			
			int  result     = 0;

			int  p1_entries = p1.m_DBResultEntries.size();
			int  p2_entries = p2.m_DBResultEntries.size();

			// Subtraction is too big for int result
			long diff       = p1_entries - p2_entries;

			// First priority given to whether it's an issue or not
			if ( (p1.m_GoesHypo && !p2.m_GoesHypo) || (p1.m_GoesHyper && !p2.m_GoesHyper))
			{
				result = m_DescendingSort ? -1 : 1;
			}
			else if (diff > 0) 
			{
				//  1 to get results in ascending order
				// -1 to get results in descending order
				result = m_DescendingSort ? -1 : 1;
			}
			else if (diff < 0)
			{
				// -1 to get results in ascending order
				//  1 to get results in descending order
				result = m_DescendingSort ? 1 : -1;
			}
			return result;
		}
	}

	public synchronized static void resetStaticID()
	{
		m_Static_ID = 0;
	}

	/**
	 * @return the m_ID
	 */
	public synchronized int getM_ID() 
	{
		return m_ID;
	}

	/**
	 * @return the m_PeriodStart
	 */
	public synchronized Date getM_PeriodStart() 
	{
		return m_PeriodStart;
	}


	/**
	 * @return the m_PeriodEnd
	 */
	public synchronized Date getM_PeriodEnd() 
	{
		return m_PeriodEnd;
	}

	/**
	 * @return the m_GoesHypo
	 */
	public synchronized Boolean getM_GoesHypo() 
	{
		return m_GoesHypo;
	}

	/**
	 * @param m_GoesHypo the m_GoesHypo to set
	 */
	public synchronized void setM_GoesHypo(Boolean m_GoesHypo) 
	{
		this.m_GoesHypo = m_GoesHypo;
	}

	/**
	 * @return the m_GoesHyper
	 */
	public synchronized Boolean getM_GoesHyper() 
	{
		return m_GoesHyper;
	}

	/**
	 * @param m_GoesHyper the m_GoesHyper to set
	 */
	public synchronized void setM_GoesHyper(Boolean m_GoesHyper) 
	{
		this.m_GoesHyper = m_GoesHyper;
	}

	/**
	 * @return the m_DBResultEntryProfileChange
	 */
	public synchronized DBResultEntryProfileChange getM_DBResultEntryProfileChange() 
	{
		return m_DBResultEntryProfileChange;
	}

	/**
	 * @param m_DBResultEntryProfileChange the m_DBResultEntryProfileChange to set
	 */
	public synchronized void setM_DBResultEntryProfileChange(DBResultEntryProfileChange m_DBResultEntryProfileChange) 
	{
		this.m_DBResultEntryProfileChange = m_DBResultEntryProfileChange;
	}

	/**
	 * @return the m_DBResultEntries
	 */
	public synchronized ArrayList<DBResultEntry> getM_DBResultEntries() 
	{
		return m_DBResultEntries;
	}

	/**
	 * @return the m_AverageBG
	 */
	public synchronized Double getM_AverageBG() 
	{
		return m_AverageBG;
	}

	/**
	 * @return the m_StartProfile
	 */
	public synchronized DBResultEntryProfile getM_StartProfile() {
		return m_StartProfile;
	}

	/**
	 * @return the m_EndProfile
	 */
	public synchronized DBResultEntryProfile getM_EndProfile() {
		return m_EndProfile;
	}

	/**
	 * @return the m_ProfileDirection
	 */
	public synchronized DBResultEntryProfileDirection getM_ProfileDirection() {
		return m_ProfileDirection;
	}

	
	public static String getEntryProfileStr(DBResultEntryProfile profile)
	{
		String result = new String("");
		
		switch (profile)
		{
		case Unknown: result = "Unknown"; break;

		case Hypo:       result = "Hypo"; break;
		case From4to7:   result = "From 4-7"; break;
		case From7to10:  result = "From 7-10"; break;
		case From10to14: result = "From 10-14"; break;
		case Hyper: result = "Hyper"; break;
		
		default: result = "** NOT IN LIST **"; break;
		}
		
		return result;
	}
	
	public static String getProfileDirectionStr(boolean mmol, DBResultEntryProfileDirection profDir)
	{
		String result = new String("");

		// Values in mmol
		if (mmol == true)
		{
			switch (profDir)
			{
			case Unknown:	result = "Unknown"; break;

			case Hypo:	result = "Hypo"; break;
			
			// In Good Range
			case From4to7ThenDown:           result = "From 4-7 then down"; break;
			case From4to7UpThenDown:         result = "From 4-7 up then down"; break;
			case From4to7ThenLevel:          result = "From 4-7 then level"; break;
			case From4to7ThenUp:             result = "From 4-7 then up"; break;
			case From4to7DownThenUp:         result = "From 4-7 down then up"; break;

			// Just outside Good Range
			case From7to10ThenDown:          result = "From 7-10 then down"; break;
			case From7to10UpThenDown:        result = "From 7-10 up then down"; break;
			case From7to10ThenLevel:         result = "From 7-10 then level"; break;
			case From7to10ThenUp:            result = "From 7-10 then up"; break;
			case From7to10DownThenUp:        result = "From 7-10 down then up"; break;
			
			// Further outside Good Range
			case From10to14ThenDown:         result = "From 10-14 then down"; break;
			case From10to14UpThenDown:       result = "From 10-14 up then down"; break;
			case From10to14ThenLevel:        result = "From 10-14 then level"; break;
			case From10to14ThenUp:           result = "From 10-14 then up"; break;
			case From10to14DownThenUp:       result = "From 10-14 down then up"; break;

			// Too high
			case Hyper:	result = "Hyper"; break;
					
			default: result = "** NOT IN LIST **"; break;
			}
		}
		
		// values in mg/dL
		else
		{
			switch (profDir)
			{
			case Unknown:	result = "Unknown"; break;

			case Hypo:	result = "Hypo"; break;
			
			// In Good Range
			case From4to7ThenDown:           result = "From 72-126 then down"; break;
			case From4to7UpThenDown:         result = "From 72-126 up then down"; break;
			case From4to7ThenLevel:          result = "From 72-126 then level"; break;
			case From4to7ThenUp:             result = "From 72-126 then up"; break;
			case From4to7DownThenUp:         result = "From 72-126 down then up"; break;

			// Just outside Good Range
			case From7to10ThenDown:          result = "From 126-180 then down"; break;
			case From7to10UpThenDown:        result = "From 126-180 up then down"; break;
			case From7to10ThenLevel:         result = "From 126-180 then level"; break;
			case From7to10ThenUp:            result = "From 126-180 then up"; break;
			case From7to10DownThenUp:        result = "From 126-180 down then up"; break;
			
			// Further outside Good Range
			case From10to14ThenDown:         result = "From 180-252 then down"; break;
			case From10to14UpThenDown:       result = "From 180-252 up then down"; break;
			case From10to14ThenLevel:        result = "From 180-252 then level"; break;
			case From10to14ThenUp:           result = "From 180-252 then up"; break;
			case From10to14DownThenUp:       result = "From 180-252 down then up"; break;
			
			case Hyper:	result = "Hyper"; break;

			default: result = "** NOT IN LIST **"; break;
			}

		}
		return result;

	}
	
	public String getM_ProfileDirectionStr(boolean mmol) 
	{
		String result = getProfileDirectionStr(mmol, m_ProfileDirection);
		return result;
	}

	/**
	 * @return the m_AnalyzerTrendResultEntry
	 */
	public synchronized AnalyzerTrendResultEntry getM_AnalyzerTrendResultEntry() {
		return m_AnalyzerTrendResultEntry;
	}

	/**
	 * @param m_AnalyzerTrendResultEntry the m_AnalyzerTrendResultEntry to set
	 */
	public synchronized void setM_AnalyzerTrendResultEntry(AnalyzerTrendResultEntry m_AnalyzerTrendResultEntry) {
		this.m_AnalyzerTrendResultEntry = m_AnalyzerTrendResultEntry;
	}

}
