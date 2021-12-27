package loader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import control.PrefsNightScoutLoader;
import entity.DBResult;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseTestTreatment {

	protected static Double DOUBLE_THRESHOLD = 0.001;

	protected abstract Object[][]       getExpectedTestResults();
	protected abstract String           getResourceFileName();
	protected abstract DataLoadFile     getDataLoadFile();
	protected abstract SimpleDateFormat getSimpleDateFormat();
	protected abstract String           getDateFormat();


	public void performTestLoad()
	{
		doDataLoad();
		DataLoadFile dataLoadDiasend = getDataLoadFile();
		Assertions.assertTrue(dataLoadDiasend != null);
		try {
			assertionTests(dataLoadDiasend);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doDataLoad()
	{
		// This disables any MongoDB connection attempts
		PrefsNightScoutLoader.getInstance().setM_NightscoutMongoServer("");

		// Disable inferring any temp basals
		PrefsNightScoutLoader.getInstance().setM_InferTempBasals(false);

//		long maxDiffBetweenSameMealEvent  = 
//				// Get Preference Value now
//				PrefsNightScoutLoader.getInstance().getM_MaxMinsBetweenSameMealEvent();
//		long maxDiffBetweenSameCorrection = 
//				// Get Preference Value now
//				PrefsNightScoutLoader.getInstance().getM_MaxMinsBetweenSameCorrectionEvent();

		URL url = this.getClass().getClassLoader().getResource(getResourceFileName());
		Assertions.assertTrue(url != null);
		DataLoadFile dataLoadDiasend = getDataLoadFile();
		try {
			dataLoadDiasend.initialize(new File(url.toURI()).getAbsolutePath());
			dataLoadDiasend.loadDBResults();
		} catch (URISyntaxException e) {
			Assertions.assertTrue(false, e.toString());
		} catch (UnknownHostException e) {
			Assertions.assertTrue(false, e.toString());
		} catch (ClassNotFoundException e) {
			Assertions.assertTrue(false, e.toString());
		} catch (SQLException e) {
			Assertions.assertTrue(false, e.toString());
		} catch (IOException e) {
			Assertions.assertTrue(false, e.toString());
		}

	}

	public int countDataLoadEntries(Boolean withBG)
	{
		int result = 0;
		
		// Iterate over the HashMap since exact duplicate results at same time
		// are filtered out.

		DataLoadFile dataLoadDiasend = getDataLoadFile();
//		for (DBResult res : dataLoadDiasend.getResultTreatments())
		HashMap<Long, DBResult> map = dataLoadDiasend.getResultTreatmentHashMap();
        Set<Map.Entry<Long, DBResult>> entrySet = map.entrySet();

        for(Map.Entry<Long, DBResult> entry : entrySet)
		{
        	DBResult res = entry.getValue();
			result += res.getM_CP_Glucose() == null 
					? withBG == false ? 1 : 0
							: withBG == true ? 1 : 0;
		}

		return result;
	}

	@SuppressWarnings("unused")
	public int countExpectedResults()
	{
		int result = 0;

		for (int c = 0; c < getExpectedTestResults().length; c++)
		{
			Object[] treatmentObject = getExpectedTestResults()[c];
			int i = 0;

			String dateString = (String) treatmentObject[i++];
			Double  bgDouble = (Double)treatmentObject[i++];
			Double  insDouble  = (Double)treatmentObject[i++];
			Double carbDouble = (Double)treatmentObject[i++];
			String dupeString = (String)treatmentObject[i++];

			result += dupeString.equals("NO") ? 1 : 0;
		}

		return result;
	}


	
	private void assertionTests(DataLoadFile dataLoadDiasend) throws ParseException
	{
		Assertions.assertTrue(dataLoadDiasend != null);

		for (int c = 0; c < getExpectedTestResults().length; c++)
		{
			Object[] treatmentObject = getExpectedTestResults()[c];
			int i = 0;


			//			Integer indexInteger = (Integer) treatmentObject[i++];
			String dateString = (String) treatmentObject[i++];
			Double  bgDouble = (Double)treatmentObject[i++];
			Double  insDouble  = (Double)treatmentObject[i++];
			Double carbDouble = (Double)treatmentObject[i++];
			String dupeString = (String)treatmentObject[i++];


			Date  date = getSimpleDateFormat().parse(dateString);

			// Better way of date parsing Java 8 -- but need to use this technique in loader
			//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getDateFormat());
			//			LocalDateTime parseDate = LocalDateTime.parse(dateString, formatter);
			//			long ldtEpoch = parseDate.toEpochSecond(ZoneOffset.UTC);

			DBResult result = dataLoadDiasend.getResultTreatmentHashMap().get(date.getTime());
			//			DBResult result = dataLoadDiasend.getResultTreatmentHashMap().get(ldtEpoch * 1000);
			if (result != null)
			{
				String messgString = " for Entry " + c + " Date " + dateString + " Treatment Id: " + result.getId();
				Assertions.assertTrue(result != null);

				if (dupeString.equals("NO"))
				{
					if (bgDouble.equals(0.0)) Assertions.assertTrue(result.getM_CP_Glucose() == null, "BG Differs (not null)" + messgString);
					else assertEquals(result.getM_CP_Glucose(), bgDouble, "BG Differs" + messgString);
				}

				if (insDouble.equals(0.0)) Assertions.assertTrue(result.getM_CP_Insulin() == null, "Insulin Differs (not null)" + messgString);
				else assertEquals(result.getM_CP_Insulin(), insDouble, "Insulin Differs" + messgString);

				if (carbDouble.equals(0.0)) Assertions.assertTrue(result.getM_CP_Carbs() == null, "Carbs Differs (not null)" + messgString);
				else assertEquals(result.getM_CP_Carbs(), carbDouble, "Carbs Differs" + messgString);

			}

			else {
				Boolean mergedBoolean = dupeString.equals("YES");
				
				System.out.println(
						(mergedBoolean ? "As expected, " : "As NOT expected, ")
						+ "unable to find " 
						+ " Date: " + dateString
						+ " BG: " + bgDouble
						+ " Ins: " + insDouble
						+ " Carbs: " + carbDouble
						+ " Dupe: " + dupeString);
				
				// Only allow dupes to be not found
				Assertions.assertTrue(mergedBoolean);
			}
			//			System.out.println("Checked row " + c + (result == null ? " Result Treatment not located for " + dateString  : ""));
		}

	}

	private void assertEquals(Double dbl1, Double dbl2, String messageString)
	{
		Assertions.assertTrue(Math.abs(dbl1 - dbl2) < DOUBLE_THRESHOLD, messageString);
	}

}
