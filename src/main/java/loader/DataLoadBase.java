package loader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import control.MyLogger;
import control.PrefsNightScoutLoader;
import entity.DBResult;
import entity.DBResultEntry;
import entity.ResultFromDBComparator;
import utils.CommonUtils;

import java.net.UnknownHostException;

public abstract class DataLoadBase 
{

	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	// Loaded from raw device such as meter, pump DB or file
	protected ArrayList <DBResult> rawResultsFromDB = new ArrayList<DBResult>();

	// Merge results into treatments ready for comparison/loading to Nightscout
	protected ArrayList <DBResult> resultTreatments = new ArrayList<DBResult>();

	protected HashMap<Long, DBResult> resultTreatmentHashMap = new HashMap<Long, DBResult>();

	// Loaded from raw device such as meter, pump DB or file
	protected ArrayList <DBResultEntry> rawEntryResultsFromDB = new ArrayList<DBResultEntry>();

	protected HashMap<Long, DBResultEntry> rawEntryResultHashMap = new HashMap<Long, DBResultEntry>();

	public void clearLists()
	{
		rawResultsFromDB.clear();
		resultTreatments.clear();
		resultTreatmentHashMap.clear();
		rawEntryResultsFromDB.clear();
		rawEntryResultHashMap.clear();
	}

	public DataLoadBase()
	{
		;
	}


	/**
	 * @return the rawResultsFromDB
	 */
	public synchronized ArrayList<DBResult> getRawResultsFromDB() {
		return rawResultsFromDB;
	}

	/**
	 * @return the resultTreatments
	 */
	public synchronized ArrayList<DBResult> getResultTreatments() {
		return resultTreatments;
	}

	/**
	 * @return the resultTreatmentHashMap
	 */
	public synchronized HashMap<Long, DBResult> getResultTreatmentHashMap() {
		return resultTreatmentHashMap;
	}

	/**
	 * @return the rawEntryResultHashMap
	 */
	public synchronized HashMap<Long, DBResultEntry> getRawEntryResultHashMap() {
		return rawEntryResultHashMap;
	}

	/**
	 * @return the rawEntryResultsFromDB
	 */
	public synchronized ArrayList<DBResultEntry> getRawEntryResultsFromDB() {
		return rawEntryResultsFromDB;
	}
	
	

	// Derived classes must return a String that gets used in storing treatments.
	protected abstract String getDevice();

	// Derived classes must be able to support a load.
	// This is used in the thread class
	public abstract void loadDBResults() throws UnknownHostException, SQLException, ClassNotFoundException, IOException;


	protected void sortDBResults()
	{
		// Sort the Mongo Results
		Collections.sort(rawResultsFromDB, new ResultFromDBComparator(false));
	}

	protected void sortDBResultEntries()
	{
		// Sort the CGM Results
		Collections.sort(rawEntryResultsFromDB, new ResultFromDBComparator(false));
	}

	// Provide a common means of inferring basals where needed
	protected void locateTempBasals()
	{
		// Iterate over the raw results looking for basal rates that have changed.
		// Assume the list is ordered in time.
		//		boolean  tempStarted   = false;
		if (PrefsNightScoutLoader.getInstance().isM_InferTempBasals())
		{
			DBResult tempBasalStart = null;
			DBResult lastHourChange = null;
			Double lastHourChangeRate = null;

			for (DBResult res : rawResultsFromDB)
			{
				// Only do this for Basal Rates
				String resType = res.getM_ResultType();

				if (resType == "Basal")
				{
					// Different approach.
					// If the basal rate change was on the hour, then assume that it's a basal rate
					// If the basal rate change was off the hour, then assume it's a temp basal.
					Date basalTime = new Date(res.getM_EpochMillies());
					int minutes = CommonUtils.getMinutesFromDate(basalTime);

					if (minutes == 0)
					{
						lastHourChange = res;
						lastHourChangeRate = Double.parseDouble(lastHourChange.getM_Result());
					}
					else
					{
						// If there's a last hour change and we're not in the middle of a temp basal,
						// and the last hour change basal rate was not 0, and the rate is not 100% then start one
						if (lastHourChange != null && tempBasalStart == null && 
								lastHourChangeRate != null && lastHourChangeRate != 0.0)
						{
							m_Logger.log(Level.FINE, 
									"Creating Temp Basal from : " + res.rawToString());

							Double resRate = Double.parseDouble(res.getM_Result());
							Double basRate = lastHourChangeRate;
							Double percent = Math.round((resRate / basRate) * 100.0) * 1.0;

							if (java.lang.Math.abs(percent - 100.0) > 0.1)
							{
								tempBasalStart = new DBResult(res, getDevice());
								tempBasalStart.setM_CP_Percent(percent);
							}
						}

						// If there's a last hour change and we're  in the middle of a temp basal,
						// Then end one
						else if (lastHourChange != null && tempBasalStart != null)
						{
							// Merge the basal rate change
							//							tempBasalStart.merge(res);

							Date tempBasalEndTime = new Date(tempBasalStart.getM_EpochMillies());			
							Double mins = (double)CommonUtils.timeDiffInMinutes(basalTime, tempBasalEndTime);
							tempBasalStart.setM_CP_Duration(mins);

							addTreatment(tempBasalStart);

							tempBasalStart = null;
						}
					}

				}

			}
		}

		// Now remove all the basal entries from rawResultsFromDB
		Iterator<DBResult> it = rawResultsFromDB.iterator();
		while (it.hasNext()) 
		{
			String resType = it.next().getM_ResultType();
			if (resType == "Basal")
			{
				it.remove();
				// If you know it's unique, you could `break;` here
			}
		}
	}


	// Provide a common way of converting 
	protected void convertDBResultsToTreatments()
	{
		// Iterate over the raw results and convert results into NightScout Treatments
		// according to this specification:
		//
		// BG, Carbs etc in close proximity 

		DBResult pendingRes = new DBResult();
		boolean readyForNextRes = true;
		boolean pendingResAdded = false;

		// Assume the list is ordered in time.
		for (DBResult res : rawResultsFromDB)
		{
			m_Logger.log(Level.FINEST, "<"+this.getClass().getName()+">" + "--------------------------------------------");
			m_Logger.log(Level.FINEST, "<"+this.getClass().getName()+">" + "Pending Res " + pendingRes.rawToString());
			m_Logger.log(Level.FINEST, "<"+this.getClass().getName()+">" + "Res " + res.rawToString());

			pendingResAdded = false;

			if (readyForNextRes)
			{
				pendingRes = new DBResult(res, getDevice());
				readyForNextRes = false;				
			}
			else
			{
				readyForNextRes = false;

				if (!pendingRes.discard())
				{
					if (pendingRes.pending())
					{
						DBResult.MergeResult merged = pendingRes.merge(res, getDevice());

						// Is the merge complete?
						if (merged == DBResult.MergeResult.Merged && !pendingRes.pending())
						{
							// Store current pending item
							addTreatment(pendingRes);
							readyForNextRes = true;
							pendingResAdded = true;
						}

						else if (merged == DBResult.MergeResult.TooDistant)
						{
							// David 14 Oct 2016
							// Store current pending item
							addTreatment(pendingRes);
							// David 14 Oct 2016

							// Res is potentially of interest so keep it & check
							pendingRes = new DBResult(res, getDevice());
							readyForNextRes = false;
						}

						// New case.  FOr example Medtronic Temp Basal in middle of bg carb & ins
						else if (merged == DBResult.MergeResult.CantMerge)
						{
							// Create a CP result and store it.
							//							addTreatment(new DBResult(res, getDevice()));

							// 11 Feb 2017
							// Developing for Tandem
							addTreatment(pendingRes);
							pendingRes = new DBResult(res, getDevice());
							readyForNextRes = false;
						}

						else if (merged == DBResult.MergeResult.Duplicate)
						{
							; // Don't convert the raw result and allow to slip away quietly...
						}
					}
					else if (pendingRes != null && pendingResAdded == false)
					{
						addTreatment(pendingRes);
						readyForNextRes = true;
						pendingResAdded = true;
					}					
				}
				else
				{
					// Discard it and get the next result
					pendingRes = new DBResult(res, getDevice());
					readyForNextRes = false;					
				}
			}
		}

		// Store last result
		if (pendingRes != null && pendingResAdded == false && resultTreatments.size() > 0)
		{
			// BG & Insulin is a correction!
			if (pendingRes.getM_CP_EventType().equals("BG Check") && pendingRes.getM_CP_Insulin() != null && pendingRes.getM_CP_Insulin() > 0.0)
			{
				pendingRes.setM_CP_EventType("Correction Bolus");
			}
			addTreatment(pendingRes);
		}

	}
	
	private void addTreatment(DBResult entry)
	{
		resultTreatments.add(entry);
		resultTreatmentHashMap.put(entry.getM_EpochMillies(), entry);
	}
	
	protected void addEntry(DBResultEntry entry)
	{
		rawEntryResultsFromDB.add(entry);
		Long epochMillies = entry.getM_EpochMillies();
		rawEntryResultHashMap.put(epochMillies, entry);
	}

	protected void inferTrendsFromCGMResultEntries()
	{
		DBResultEntry prev = null;
		for (DBResultEntry res : rawEntryResultsFromDB)
		{
			String direction = getDirection(
					(prev != null ? prev.getM_EpochMillies() : res.getM_EpochMillies()), res.getM_EpochMillies(),
					(prev != null ? prev.getM_SGV() : res.getM_SGV()), res.getM_SGV());

			res.setM_Direction(direction);
			res.setM_Unfiltered(0.0);

			prev = res;
		}
	}

	private String getDirection(long startMillies, long endMillies, double startMgDL, double endMgDL)
	{
		String result = new String("NOT COMPUTABLE");
		double d_DoubleDown    = -3.5;
		double d_SingleDown    = -2.0;
		double d_FortyFiveDown = -1.0;
		double d_Flat          =  1.0;
		double d_FortyFiveUp   =  2.0;
		double d_SingleUp      =  3.5;
		double d_DoubleUp      = 40.0;

		// Distinct direction values ...

		/*
		 * FortyFiveDown
		 * Flat
		 * FortyFiveUp
		 * SingleDown
		 * SingleUp
		 * DoubleUp
		 * DoubleDown
		 * NOT COMPUTABLE
		 */

		if (endMillies - startMillies > 0)
		{
			// Scale this up to change per minute
			double slope = (endMgDL - startMgDL) / (endMillies - startMillies) * 1000 * 60;

			if (slope <= (d_DoubleDown))
			{
				result = "DoubleDown";
			} 
			else if (slope <= (d_SingleDown))
			{
				result = "SingleDown";
			} 
			else if (slope <= (d_FortyFiveDown)) 
			{
				result = "FortyFiveDown";
			} 
			else if (slope <= (d_Flat)) 
			{
				result = "Flat";
			} 
			else if (slope <= (d_FortyFiveUp))
			{
				result = "FortyFiveUp";
			} 
			else if (slope <= (d_SingleUp)) 
			{
				result = "SingleUp";
			} 
			else if (slope <= (d_DoubleUp))
			{
				result = "DoubleUp";
			}

		}
		return result;
	}


}
