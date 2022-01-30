package control;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import entity.DBResult;
import entity.DBResultEntry;
import loader.BaseTestEntry;
import loader.BaseTestTreatment;
import loader.DataLoadNightScoutTreatments;
import loader.TestDiasendTreatmentBGMergedToInsCarb;
import loader.TestDiasendTreatmentInsCarbNotMerged;
import loader.TestLibreView_Batch1;
import loader.TestLibreView_Batch10;
import loader.TestLibreView_Batch11;
import loader.TestLibreView_Batch12;
import loader.TestLibreView_Batch2;
import loader.TestLibreView_Batch3;
import loader.TestLibreView_Batch4;
import loader.TestLibreView_Batch5;
import loader.TestLibreView_Batch6;
import loader.TestLibreView_Batch7;
import loader.TestLibreView_Batch8;
import loader.TestLibreView_Batch9;
import utils.CommonUtils;

// But there ARE "save" tests
public class TestThreadDetermineSaveDifferencesMockitoMongo
{
	private DataLoadNightScoutTreatments m_DataLoadNightScoutTreatments = mock(DataLoadNightScoutTreatments.class);

	private ArrayList <DBResult>         m_MeterResultTreatmentsRemoved = new ArrayList <DBResult>();
	private ArrayList <DBResultEntry>    m_MeterResultEntriesRemoved = new ArrayList <DBResultEntry>();

	private HashMap<Long, DBResultEntry> m_MeterResultEntriesChanged = new HashMap<Long, DBResultEntry>();
	private HashMap<Long, DBResultEntry> m_NSResultEntriesChanged = new HashMap<Long, DBResultEntry>();

	private HashMap<Long, DBResult>      m_MeterResultTreatmentsChanged = new HashMap<Long, DBResult>();
	private HashMap<Long, DBResult>      m_NSResultTreatmentsChanged = new HashMap<Long, DBResult>();


	private int  m_HowManyResultTreatmentsToBeNew      = 1;
	private int  m_HowManyResultTreatmentsToBePossDupe = 1;
	private int  m_HowManyResultEntriesToBeNew         = 1;
	private int  m_HowManyResultEntriesToBePossDupe    = 1;
	private int  m_PossDupeMinAdjustment               = 3;   // 3 Minute change to Possible Dupe changes.

	public TestThreadDetermineSaveDifferencesMockitoMongo() {
		setup();
	}

	@Test
	public void testReplicateTreatments()
	{
		String nameofCurrMethod = new Throwable()
				.getStackTrace()[0]
						.getMethodName();
				System.out.println("[INFO] *** TEST *** " + this.getClass().getSimpleName() + "." + nameofCurrMethod);

		TestDiasendTreatmentBGMergedToInsCarb testTestDiasendTreatmentBGMergedToInsCarb = new TestDiasendTreatmentBGMergedToInsCarb();
		testTestDiasendTreatmentBGMergedToInsCarb.doDataLoad();
		initializeMock(testTestDiasendTreatmentBGMergedToInsCarb);

		ArrayList<DBResult> result = replicateNSTreatments(testTestDiasendTreatmentBGMergedToInsCarb.getDBResultArrayList());
		Assertions.assertTrue(result != null);
		Assertions.assertEquals(m_MeterResultTreatmentsRemoved.size() + result.size(), testTestDiasendTreatmentBGMergedToInsCarb.getDBResultArrayList().size());
	}

	@Test
	public void testReplicateEntries()
	{
		String nameofCurrMethod = new Throwable()
				.getStackTrace()[0]
						.getMethodName();
				System.out.println("[INFO] *** TEST *** " + this.getClass().getSimpleName() + "." + nameofCurrMethod);

		TestLibreView_Batch1                  testTestLibreView_Batch1 = new TestLibreView_Batch1();
		testTestLibreView_Batch1.doDataLoad();
		ArrayList<DBResultEntry> result = replicateNSEntries(testTestLibreView_Batch1.getDBResultEntryArrayList());
		Assertions.assertTrue(result != null);
		Assertions.assertEquals(m_MeterResultEntriesRemoved.size() + result.size(), testTestLibreView_Batch1.getDBResultEntryArrayList().size());
	}


	@Test
	public void testBGMergedToInsCarbTreatment()
	{
		String nameofCurrMethod = new Throwable()
				.getStackTrace()[0]
						.getMethodName();
				System.out.println("[INFO] *** TEST *** " + this.getClass().getSimpleName() + "." + nameofCurrMethod);
		runTreatmentComparison(new TestDiasendTreatmentBGMergedToInsCarb());
	}

	@Test
	public void testInsCarbNotMergedTreatment()
	{
		String nameofCurrMethod = new Throwable()
				.getStackTrace()[0]
						.getMethodName();
				System.out.println("[INFO] *** TEST *** " + this.getClass().getSimpleName() + "." + nameofCurrMethod);
		runTreatmentComparison(new TestDiasendTreatmentInsCarbNotMerged());
	}

	@Test
	public void testLibreEntries_Batch1()
	{
		String nameofCurrMethod = new Throwable()
				.getStackTrace()[0]
						.getMethodName();
				System.out.println("[INFO] *** TEST *** " + this.getClass().getSimpleName() + "." + nameofCurrMethod);
		runEntriesComparison(new TestLibreView_Batch1());
	}

	@Test
	public void testLibreEntries_Batch2()
	{
		String nameofCurrMethod = new Throwable()
				.getStackTrace()[0]
						.getMethodName();
				System.out.println("[INFO] *** TEST *** " + this.getClass().getSimpleName() + "." + nameofCurrMethod);
		runEntriesComparison(new TestLibreView_Batch2());
	}

	@Test
	public void testLibreEntries_Batch3()
	{
		String nameofCurrMethod = new Throwable()
				.getStackTrace()[0]
						.getMethodName();
				System.out.println("[INFO] *** TEST *** " + this.getClass().getSimpleName() + "." + nameofCurrMethod);
		runEntriesComparison(new TestLibreView_Batch3());
	}

	@Test
	public void testLibreEntries_Batch4()
	{
		String nameofCurrMethod = new Throwable()
				.getStackTrace()[0]
						.getMethodName();
				System.out.println("[INFO] *** TEST *** " + this.getClass().getSimpleName() + "." + nameofCurrMethod);
		runEntriesComparison(new TestLibreView_Batch4());
	}

	@Test
	public void testLibreEntries_Batch5()
	{
		String nameofCurrMethod = new Throwable()
				.getStackTrace()[0]
						.getMethodName();
				System.out.println("[INFO] *** TEST *** " + this.getClass().getSimpleName() + "." + nameofCurrMethod);
		runEntriesComparison(new TestLibreView_Batch5());
	}


	@Test
	public void testLibreEntries_Batch6()
	{
		String nameofCurrMethod = new Throwable()
				.getStackTrace()[0]
						.getMethodName();
				System.out.println("[INFO] *** TEST *** " + this.getClass().getSimpleName() + "." + nameofCurrMethod);
		runEntriesComparison(new TestLibreView_Batch6());
	}


	@Test
	public void testLibreEntries_Batch7()
	{
		String nameofCurrMethod = new Throwable()
				.getStackTrace()[0]
						.getMethodName();
				System.out.println("[INFO] *** TEST *** " + this.getClass().getSimpleName() + "." + nameofCurrMethod);
		runEntriesComparison(new TestLibreView_Batch7());
	}


	@Test
	public void testLibreEntries_Batch8()
	{
		String nameofCurrMethod = new Throwable()
				.getStackTrace()[0]
						.getMethodName();
				System.out.println("[INFO] *** TEST *** " + this.getClass().getSimpleName() + "." + nameofCurrMethod);
		runEntriesComparison(new TestLibreView_Batch8());
	}


	@Test
	public void testLibreEntries_Batch9()
	{
		String nameofCurrMethod = new Throwable()
				.getStackTrace()[0]
						.getMethodName();
				System.out.println("[INFO] *** TEST *** " + this.getClass().getSimpleName() + "." + nameofCurrMethod);
		runEntriesComparison(new TestLibreView_Batch9());
	}


	@Test
	public void testLibreEntries_Batch10()
	{
		String nameofCurrMethod = new Throwable()
				.getStackTrace()[0]
						.getMethodName();
				System.out.println("[INFO] *** TEST *** " + this.getClass().getSimpleName() + "." + nameofCurrMethod);
		runEntriesComparison(new TestLibreView_Batch10());
	}


	@Test
	public void testLibreEntries_Batch11()
	{
		String nameofCurrMethod = new Throwable()
				.getStackTrace()[0]
						.getMethodName();
				System.out.println("[INFO] *** TEST *** " + this.getClass().getSimpleName() + "." + nameofCurrMethod);
		runEntriesComparison(new TestLibreView_Batch11());
	}


	@Test
	public void testLibreEntries_Batch12()
	{
		String nameofCurrMethod = new Throwable()
				.getStackTrace()[0]
						.getMethodName();
				System.out.println("[INFO] *** TEST *** " + this.getClass().getSimpleName() + "." + nameofCurrMethod);
		runEntriesComparison(new TestLibreView_Batch12());
	}

	private void runTreatmentComparison(BaseTestTreatment baseTreatment)
	{
		runTreatmentComparison(baseTreatment, 0, 5);

		runTreatmentComparison(baseTreatment, 1, 5);
		runTreatmentComparison(baseTreatment, 2, 5);

		runTreatmentComparison(baseTreatment, 1, 15);
		runTreatmentComparison(baseTreatment, 2, 15);		
	}


	private void runTreatmentComparison(BaseTestTreatment baseTreatment, int proximityCheckType, int proximityMinutes)
	{
		baseTreatment.doDataLoad();
		initializeMock(baseTreatment);

		ArrayList<DBResult> simulatedNSTreatmentsArrayList = replicateNSTreatments(baseTreatment.getDBResultArrayList());
		ArrayList<DBResultEntry> simulatedNSEntriesArrayList = null;  // ArrayList<DBResultEntry>();

		HashMap<Long, DBResult> simulatedNSTreatmentsHashMap = new HashMap<Long, DBResult>();

		simulatedNSTreatmentsArrayList.forEach(r -> simulatedNSTreatmentsHashMap.put(r.getM_EpochMillies(), r));


		// Set up proximity check with preference on meter values
		PrefsNightScoutLoader.getInstance().setM_ProximityCheckType(proximityCheckType);
		PrefsNightScoutLoader.getInstance().setM_ProximityMinutes(proximityMinutes);


		ThreadDetermineSaveDifferences threadDetermineSaveDifferences = 
				new ThreadDetermineSaveDifferences(
						m_DataLoadNightScoutTreatments
						, baseTreatment.getDBResultArrayList()
						, simulatedNSTreatmentsArrayList
						, simulatedNSEntriesArrayList
						, simulatedNSEntriesArrayList

						, null /*m_ThreadDataLoadNightScout*/
						, null /*m_ThreadDataLoadNightScoutEntries*/
						, null /*m_ThreadDataMeterLoad*/
						, "" /*m_DeviceUsed*/
						, "" /*m_FileName*/
						, "" /*m_DateRange*/);

		ThreadDetermineSaveDifferences spyDetermineSaveDifferences = spy(threadDetermineSaveDifferences);


		Mockito.doNothing().when(spyDetermineSaveDifferences).saveDifferences();
		Mockito.doNothing().when(spyDetermineSaveDifferences).saveCGMDifferences();
		Mockito.doNothing().when(spyDetermineSaveDifferences).updateAuditHistory();
		Mockito.doNothing().when(spyDetermineSaveDifferences).loadAuditHistory();


		spyDetermineSaveDifferences.run();

		Set<DBResult> differencesTreatmentsSet = spyDetermineSaveDifferences.getM_MeterArrayListDBResultsSet();
		Set<DBResult> nsProximalTreatmentsSet  = spyDetermineSaveDifferences.getM_ExistingNightScoutProximityDBResultsSet();
		Set<DBResult> mtProximalTreatmentsSet  = spyDetermineSaveDifferences.getM_MeterArrayListProximityDBResultsSet();

		String assertTextString = "(Proximity Check: " + proximityCheckType + ", Proximity Minutes: " + proximityMinutes + ")";


		if (differencesTreatmentsSet.size() != this.m_MeterResultTreatmentsRemoved.size() +
				this.m_MeterResultTreatmentsChanged.size())
		{
			summariseSet(differencesTreatmentsSet, "Differences returned back");
			summariseList(m_MeterResultTreatmentsRemoved, "Meter Treatments Removed");
			summariseMap(m_MeterResultTreatmentsChanged, "Meter Treatments Changed");
			summariseSet(nsProximalTreatmentsSet, "NS Proximal Treatments");
			//			summariseMap(simulatedNSTreatmentsHashMap, "NS Simulated Treatments");
		}

		//		Assertions.assertEquals(differencesTreatmentsSet.size(), this.m_MeterResultTreatmentsRemoved.size());
		Assertions.assertEquals(
				differencesTreatmentsSet.size(), 
				this.m_MeterResultTreatmentsRemoved.size() +
				this.m_MeterResultTreatmentsChanged.size(),
				"Expect differences in treatments = (number removed) + (number changed)"
				);


		if (nsProximalTreatmentsSet.size() > 0) Assertions.assertEquals(nsProximalTreatmentsSet.size(), this.m_MeterResultTreatmentsChanged.size(), assertTextString + " Changed=NS Proximal Count");
		if (mtProximalTreatmentsSet.size() > 0) Assertions.assertEquals(mtProximalTreatmentsSet.size(), this.m_MeterResultTreatmentsChanged.size(), assertTextString + " Changed=Meter Proximal Count");

		m_MeterResultTreatmentsRemoved.forEach(r -> Assertions.assertTrue(differencesTreatmentsSet.contains(r), assertTextString + " Differences include removed entry: " + r.toString()));
		//		if (nsProximalTreatmentsSet.size() > 0)  m_MeterResultTreatmentsChanged.entrySet().forEach(r -> Assertions.assertTrue(nsProximalTreatmentsSet.contains(r.getValue()), assertTextString + " Differences include NS Proximal entry: " + " ID: (" + r.getValue().getId() + ") " + r.getValue().toString()));
		if (mtProximalTreatmentsSet.size() > 0)  m_NSResultTreatmentsChanged.entrySet().forEach(r -> Assertions.assertTrue(simulatedNSTreatmentsHashMap.get(r.getKey()) != null, assertTextString + " Differences include Meter Proximal entry: " + " ID: (" + r.getValue().getId() + ") " + r.getValue().toString()));

	}


	private void summariseMap(HashMap<Long, DBResult> set, String text)
	{
		set.forEach((l, d) ->
		System.out.println(text + " ID: (" + d.getId() + ") " + d.toString()));
	}
	private void summariseSet(Set<DBResult> set, String text)
	{
		set.forEach(d ->
		System.out.println(text + " ID: (" + d.getId() + ") " + d.toString()));
	}
	private void summariseList(ArrayList<DBResult> set, String text)
	{
		set.forEach(d ->
		System.out.println(text + " ID: (" + d.getId() + ") " + d.toString()));
	}


	private void runEntriesComparison(BaseTestEntry testEntry)
	{
		testEntry.doDataLoad();
		runEntriesComparison(testEntry, 1, 5);
		runEntriesComparison(testEntry, 2, 5);

		runEntriesComparison(testEntry, 1, 15);
		runEntriesComparison(testEntry, 2, 15);		
	}

	private void runEntriesComparison(BaseTestEntry testEntry, int proximityCheckType, int proximityMinutes)
	{
		//		testEntry.doDataLoad();
		ArrayList<DBResult>      simulatedNSTreatmentsArrayList = null;
		ArrayList<DBResultEntry> simulatedNSEntriesArrayList = replicateNSEntries(testEntry.getDBResultEntryArrayList());

		HashMap<Long, DBResultEntry> simulatedNSEntriesHashMap = new HashMap<Long, DBResultEntry>();

		simulatedNSEntriesArrayList.forEach(r -> simulatedNSEntriesHashMap.put(r.getM_EpochMillies(), r));


		// Set up proximity check with preference on meter values
		PrefsNightScoutLoader.getInstance().setM_ProximityCheckType(proximityCheckType);
		PrefsNightScoutLoader.getInstance().setM_ProximityMinutes(proximityMinutes);

		ThreadDetermineSaveDifferences threadDetermineSaveDifferences = 
				new ThreadDetermineSaveDifferences(
						m_DataLoadNightScoutTreatments
						, simulatedNSTreatmentsArrayList
						, simulatedNSTreatmentsArrayList
						, testEntry.getDBResultEntryArrayList()
						, simulatedNSEntriesArrayList

						, null /*m_ThreadDataLoadNightScout*/
						, null /*m_ThreadDataLoadNightScoutEntries*/
						, null /*m_ThreadDataMeterLoad*/
						, "" /*m_DeviceUsed*/
						, "" /*m_FileName*/
						, "" /*m_DateRange*/);

		ThreadDetermineSaveDifferences spyDetermineSaveDifferences = spy(threadDetermineSaveDifferences);

		Mockito.doNothing().when(spyDetermineSaveDifferences).saveDifferences();
		Mockito.doNothing().when(spyDetermineSaveDifferences).saveCGMDifferences();
		Mockito.doNothing().when(spyDetermineSaveDifferences).updateAuditHistory();
		Mockito.doNothing().when(spyDetermineSaveDifferences).loadAuditHistory();


		spyDetermineSaveDifferences.run();

		Set<DBResultEntry> differencesEntriesSet = spyDetermineSaveDifferences.getM_MeterArrayListDBResultEntriesSet();
		Set<DBResultEntry> nsProximalEntriesSet = spyDetermineSaveDifferences.getM_ExistingNightScoutProximityDBResultEntriesSet();
		Set<DBResultEntry> mtProximalEntriesSet = spyDetermineSaveDifferences.getM_MeterArrayListProximityDBResultEntriesSet();

		String assertTextString = "(Proximity Check: " + proximityCheckType + ", Proximity Minutes: " + proximityMinutes + ")";

		//		Assertions.assertEquals(differencesEntriesSet.size(), this.m_MeterResultEntriesRemoved.size(), "");
		Assertions.assertEquals(
				differencesEntriesSet.size(), 
				this.m_MeterResultEntriesRemoved.size() +
				this.m_MeterResultEntriesChanged.size(),
				"Expect differences in entries = (number removed) + (number changed)"
				);

		if (nsProximalEntriesSet.size() > 0) Assertions.assertEquals(nsProximalEntriesSet.size(), this.m_MeterResultEntriesChanged.size(), assertTextString + " Changed=NS Proximal Count");
		if (mtProximalEntriesSet.size() > 0) Assertions.assertEquals(mtProximalEntriesSet.size(), this.m_MeterResultEntriesChanged.size(), assertTextString + " Changed=Meter Proximal Count");

		m_MeterResultEntriesRemoved.forEach(r -> Assertions.assertTrue(differencesEntriesSet.contains(r), assertTextString + " Differences include removed entry: " + r.toString()));
		//		if (nsProximalEntriesSet.size() > 0)  m_MeterResultEntriesChanged.entrySet().forEach(r -> Assertions.assertTrue(nsProximalEntriesSet.contains(r.getValue()), assertTextString + " Differences include NS Proximal entry: " + r.toString()));
		if (mtProximalEntriesSet.size() > 0)  m_NSResultEntriesChanged.entrySet().forEach(r -> Assertions.assertTrue(simulatedNSEntriesHashMap.get(r.getKey()) != null, assertTextString + " Differences include Meter Proximal entry: " + r.toString()));
	}

	private ArrayList<DBResult> replicateNSTreatments(ArrayList<DBResult> meterList)
	{
		ArrayList<DBResult> result = new ArrayList<DBResult>(meterList);

		// Remove entries based on the defined numbers above.
		int[] removeArray = new int[m_HowManyResultTreatmentsToBeNew];
		int[] updateArray = new int[m_HowManyResultTreatmentsToBePossDupe];

		if (meterList.size() > m_HowManyResultTreatmentsToBeNew)
		{
			int newIncr = m_HowManyResultTreatmentsToBeNew > 0 ? meterList.size() / m_HowManyResultTreatmentsToBeNew : 0;
			int updIncr = m_HowManyResultEntriesToBePossDupe > 0 ? (meterList.size() - m_HowManyResultTreatmentsToBeNew) / m_HowManyResultEntriesToBePossDupe : 0;

			int index = 0;
			for (int i = 0; i < removeArray.length; i++)
			{
				removeArray[i] = index - i;  
				index += newIncr;
			}
			index = 0;
			for (int i = 0; i < updateArray.length; i++)
			{
				updateArray[i] = index - i;  
				index += updIncr;
			}

			Boolean storeRemovalsBoolean = m_MeterResultTreatmentsRemoved.isEmpty();

			for (int i = 0; i < removeArray.length; i++)
			{
				DBResult res = result.remove(i);
				if (storeRemovalsBoolean)
					m_MeterResultTreatmentsRemoved.add(res);
			}


			// Adjust the epoch millies for this number of items ...
			Boolean storeUpdatesBoolean = m_MeterResultTreatmentsChanged.isEmpty();
			for (int i = 0; i < updateArray.length; i++)
			{	
				// Replace the current entry with one having a modified date.
				DBResult res = result.get(i);
				String origDateString = res.getM_CP_EventTime();
				String adjuDateString = getAdjustedDateString(origDateString);
				DBResult upd = DBResult.cloneDBResult(res, adjuDateString);
				result.set(i, upd);
				if (storeUpdatesBoolean)
				{
					Long origMilliesLong = res.getM_EpochMillies();					
					m_MeterResultTreatmentsChanged.put(origMilliesLong, res);

					m_NSResultTreatmentsChanged.put(upd.getM_EpochMillies(), upd);
				}
			}

			//			this.summariseList(m_MeterResultTreatmentsRemoved, "replicateNSTreatments - REMOVED");
			//			this.summariseMap(m_MeterResultTreatmentsChanged, "replicateNSTreatments - CHANGED - ORIGINAL MILLIS");
			//			this.summariseMap(m_NSResultTreatmentsChanged, "replicateNSTreatments - CHANGED - UPDATED MILLIS");
		}

		return result;
	}

	private ArrayList<DBResultEntry> replicateNSEntries(ArrayList<DBResultEntry> meterList)
	{
		ArrayList<DBResultEntry> result = new ArrayList<DBResultEntry>(meterList);

		// Remove entries based on the defined numbers above.
		int[] removeArray = new int[m_HowManyResultEntriesToBeNew];
		int[] updateArray = new int[m_HowManyResultEntriesToBePossDupe];

		if (meterList.size() > m_HowManyResultEntriesToBeNew)
		{
			int newIncr = m_HowManyResultEntriesToBeNew > 0 ? meterList.size() / m_HowManyResultEntriesToBeNew : 0;
			int updIncr = m_HowManyResultEntriesToBePossDupe > 0 ? (meterList.size() - m_HowManyResultEntriesToBeNew) / m_HowManyResultEntriesToBePossDupe : 0;

			int index = 0;
			for (int i = 0; i < removeArray.length; i++)
			{
				removeArray[i] = index - i;  
				index += newIncr;
			}
			index = 0;
			for (int i = 0; i < updateArray.length; i++)
			{
				updateArray[i] = index - i;  
				index += updIncr;
			}

			Boolean storeRemovalsBoolean = m_MeterResultEntriesRemoved.isEmpty();
			for (int i = 0; i < removeArray.length; i++)
			{
				DBResultEntry res = result.remove(i);
				if (storeRemovalsBoolean)
					m_MeterResultEntriesRemoved.add(res);
			}

			// Adjust the epoch millies for this number of items ...
			Boolean storeUpdatesBoolean = m_MeterResultEntriesChanged.isEmpty();
			for (int i = 0; i < updateArray.length; i++)
			{	
				// Replace the current entry with one having a modified date.
				DBResultEntry res = result.get(i);
				String origDateString = res.getM_DateString();
				String adjuDateString = getAdjustedDateString(origDateString);
				DBResultEntry upd = new DBResultEntry(res, adjuDateString);
				result.set(i, upd);
				if (storeUpdatesBoolean)
				{
					Long origMilliesLong = res.getM_EpochMillies();					
					m_MeterResultEntriesChanged.put(origMilliesLong, res);

					m_NSResultEntriesChanged.put(upd.getM_EpochMillies(), upd);
				}
			}
		}

		return result;
	}

	private String getAdjustedDateString(String origDateString)
	{
		String resultString = null;

		try 
		{
			Date date = CommonUtils.convertDateString(origDateString);
			date = CommonUtils.addMinsToDate(date, m_PossDupeMinAdjustment);
			resultString = CommonUtils.convertNSZDateString(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultString;
	}

	private void initializeMock(BaseTestTreatment baseTreatment)
	{
		when(m_DataLoadNightScoutTreatments.getResultsFromDB()).thenReturn(replicateNSTreatments(baseTreatment.getDBResultArrayList()));		
	}

	private void setup()
	{
		try {

			// Set log level to 10 (warning).  Other new option is 20 (severe)
			// This quietens down the output significantly
			PrefsNightScoutLoader.getInstance().setM_LogLevel(10);

			MyLogger.setup(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
