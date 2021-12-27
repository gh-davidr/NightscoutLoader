package loader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import control.PrefsNightScoutLoader;
import entity.DBResultEntry;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseTestEntry {

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
	

	private void assertionTests(DataLoadFile dataLoadDiasend) throws ParseException
	{
		Assertions.assertTrue(dataLoadDiasend != null);

		for (int c = 0; c < getExpectedTestResults().length; c++)
		{
			Object[] treatmentObject = getExpectedTestResults()[c];
			int i = 0;

			//			Integer indexInteger = (Integer) treatmentObject[i++];
			String dateString = (String) treatmentObject[i++];
//			Date  date = getSimpleDateFormat().parse(dateString);
			
			// Better way of date parsing Java 8
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getDateFormat());
			LocalDateTime parseDate = LocalDateTime.parse(dateString, formatter);
			long ldtEpoch = parseDate.toEpochSecond(ZoneOffset.UTC);

//			Date date = Date.from(parseDate.atZone(ZoneId.systemDefault()).toInstant());
//			
//	        ZonedDateTime zonedDateTime = parseDate.atZone(ZoneId.systemDefault());
//			Date date2 = Date.from(zonedDateTime.toInstant());
//
//			long d1Epoch  = date.getTime();
//			long d2Epoch  = date2.getTime();
//			
//			
//			LocalDateTime parseDate2 = LocalDateTime.parse("28/03/2021 02:17", formatter);
//			long ldt2Epoch = parseDate2.toEpochSecond(ZoneOffset.UTC);
//			Date date3 = Date.from(parseDate2.atZone(ZoneId.ofOffset("UTC", ZoneOffset.UTC)).toInstant());
//			long d3Epoch  = date3.getTime();
//
//			Date date_a1 = new Date(ldtEpoch * 1000);
//
//			Date date_a2 = new Date(ldt2Epoch * 1000);


			Double  bgDouble = (Double)treatmentObject[i++];
			String dupeString = (String)treatmentObject[i++];

	
			DBResultEntry result = dataLoadDiasend.getRawEntryResultHashMap().get(ldtEpoch * 1000);			
			if (result != null)
			{
				String messgString = " for Entry " + c + " Date " + dateString + " Treatment Id: " + result.getIdentity();
				Assertions.assertTrue(result != null);

				if (dupeString.equals("NO"))
					assertEquals(result.getM_BG(), bgDouble, "BG Differs" + messgString);
			}
//			System.out.println("Checked row " + c 
//					+ (result == null ? " CGM Result Entry not located for " + dateString  : "")
//					+ (dupeString.equals("YES") ? " Duplicate BG so first ones ignored" : "")
//					);
		}

	}

	protected void assertEquals(Double dbl1, Double dbl2, String messageString)
	{
		Assertions.assertTrue(Math.abs(dbl1 - dbl2) < DOUBLE_THRESHOLD, messageString);
	}

}
