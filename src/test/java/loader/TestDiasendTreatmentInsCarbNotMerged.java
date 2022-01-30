package loader;

import java.text.SimpleDateFormat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestDiasendTreatmentInsCarbNotMerged extends BaseTestTreatment {

	private static final String RESOURCE_FILE_NAME_STRING = "Sample_Diasend2.xls";


	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	private static Object[][] EXPECTED_TREATMENT_RESULTS = 
		{
				//      Date of Reading    BG     INS     CARBS  DUPE?
				{    "20/03/2021 19:01",  0.0,   6.80,   0.00,  "NO"},
				{    "28/03/2021 15:41",  0.0,   4.05,   45.00,  "YES"},
				{    "28/03/2021 15:41",  0.0,   4.05,   45.00,  "NO"},
				{    "06/04/2021 00:00",  0.0,   1.30,   0.00,  "NO"},
				{    "09/04/2021 00:00",  0.0,   1.60,   0.00,  "NO"},
				{    "13/04/2021 19:44",  0.0,   5.00,   50.00,  "YES"},
				{    "13/04/2021 19:44",  0.0,   5.00,   50.00,  "NO"},
				{    "15/04/2021 00:00",  0.0,   2.70,   0.00,  "NO"},
				{    "27/05/2021 00:00",  0.0,   0.95,   0.00,  "NO"},
				{    "12/06/2021 00:00",  0.0,   3.05,   0.00,  "NO"},
				{    "08/07/2021 20:44",  0.0,   4.00,   40.00,  "YES"},
				{    "08/07/2021 20:44",  0.0,   4.00,   40.00,  "NO"},
				{    "23/07/2021 00:00",  0.0,   0.35,   0.00,  "NO"},
				{    "10/09/2021 00:00",  0.0,   0.95,   0.00,  "NO"},
				{    "03/10/2021 17:45",  0.0,   3.15,   35.00,  "YES"},
				{    "03/10/2021 17:45",  0.0,   3.15,   35.00,  "NO"},
				{    "04/10/2021 11:52",  0.0,   4.05,   45.00,  "YES"},
				{    "04/10/2021 11:52",  0.0,   4.05,   45.00,  "NO"},

		};
	
	DataLoadFile dataLoadFile = new DataLoadDiasend();

	@Test
	public void doTestLoad()
	{
		String nameofCurrMethod = new Throwable()
				.getStackTrace()[0]
						.getMethodName();
		System.out.println("[INFO] *** TEST *** " + this.getClass().getSimpleName() + "." + nameofCurrMethod);
		performTestLoad();
	}

	@Test
	public void doTestCount()
	{
		String nameofCurrMethod = new Throwable()
				.getStackTrace()[0]
						.getMethodName();
				System.out.println("[INFO] *** TEST *** " + this.getClass().getSimpleName() + "." + nameofCurrMethod);
		doDataLoad();
		
		int dlEntries = this.countDataLoadEntries(false);
		int exEntries = this.countExpectedResults();
		
		Assertions.assertTrue( dlEntries == exEntries, "Expect record counts of entries without BG to match data set provided.  "
				+ dlEntries + " in data load, " 
				+ exEntries + " in data set provided.");
	}
	
	@Override
	protected Object[][] getExpectedTestResults() {
		return EXPECTED_TREATMENT_RESULTS;
	}

	@Override
	protected String getResourceFileName() {
		return RESOURCE_FILE_NAME_STRING;
	}

	@Override
	protected DataLoadFile getDataLoadFile() {
		return dataLoadFile;
	}

	@Override
	protected SimpleDateFormat getSimpleDateFormat() {
		return DATE_FORMAT;
	}

	@Override
	protected String getDateFormat() {
		return "dd/MM/yyyy HH:mm";
	}

}
