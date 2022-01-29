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
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import control.MyLogger;
import control.PrefsNightScoutLoader;
import entity.DBResult;
import entity.DBResultEntry;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseTestEntry {

	protected static Double DOUBLE_THRESHOLD = 0.001;

	public BaseTestEntry() {
		try {
			MyLogger.setup(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected abstract Object[][]       getExpectedTestResults();
	protected abstract String           getResourceFileName();
	protected abstract DataLoadFile     getDataLoadFile();
	protected abstract SimpleDateFormat getSimpleDateFormat();
	protected abstract String           getDateFormat();
	
	public ArrayList <DBResult>         getDBResultArrayList()
	{
		return getDataLoadFile().getResultTreatments();
	}
	
	public ArrayList <DBResultEntry>    getDBResultEntryArrayList()
	{
		return getDataLoadFile().getRawEntryResultsFromDB();
	}

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
			
			// Better way of date parsing Java 8
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getDateFormat());
			LocalDateTime parseDate = LocalDateTime.parse(dateString, formatter);
			long ldtEpoch = parseDate.toEpochSecond(ZoneOffset.UTC);


			Double  bgDouble = (Double)treatmentObject[i++];
			String dupeString = (String)treatmentObject[i++];

	
			DBResultEntry result = dataLoadDiasend.getRawEntryResultHashMap().get(ldtEpoch * 1000);			
			if (result != null)
			{
				String messgString = " for Entry " + c + " Date " + dateString + " Treatment Id: " + result.getIdentity();
				Assertions.assertTrue(result != null);

				if (dupeString.equals("NO"))
					assertEquals(result.getM_BG(), bgDouble, 
							"BG Differs" + messgString
							+ " Result BG: " + result.getM_BG()
							+ " Treatment BG: " + bgDouble);
			}

		}

	}

	protected void assertEquals(Double dbl1, Double dbl2, String messageString)
	{
		Assertions.assertTrue(Math.abs(dbl1 - dbl2) < DOUBLE_THRESHOLD, messageString);
	}

}
