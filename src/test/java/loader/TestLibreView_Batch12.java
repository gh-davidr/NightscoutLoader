package loader;

import java.text.SimpleDateFormat;

import org.junit.jupiter.api.Test;

public class TestLibreView_Batch12 extends BaseTestEntry
{
	private static final String RESOURCE_FILE_NAME_STRING = "Sample_Libreview.csv";
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	private static Object[][] EXPECTED_ENTRY_RESULTS = 
		{
				//    Date of Reading     BG    DUPLICATE?
				// THIS COLUMN NOW USED
				{"04/09/2021 00:03", 2.9, "YES"},
				{"04/09/2021 08:54", 3.4, "NO"},
				{"04/09/2021 11:43", 6.1, "NO"},
				{"04/09/2021 15:46", 7.8, "NO"},
				{"04/09/2021 20:13", 6.9, "NO"},
				{"05/09/2021 11:59", 4.1, "YES"},
				{"05/09/2021 18:14", 13.3, "NO"},
				{"05/09/2021 22:08", 9.3, "NO"},
				{"06/09/2021 02:55", 13.6, "YES"},
				{"06/09/2021 04:22", 14.7, "NO"},
				{"06/09/2021 15:34", 8.4, "NO"},
				{"06/09/2021 17:21", 14.9, "YES"},
				{"06/09/2021 19:53", 2.9, "NO"},
				{"07/09/2021 00:33", 2.9, "YES"},
				{"07/09/2021 11:07", 10.2, "NO"},
				{"07/09/2021 16:15", 7.9, "NO"},
				{"07/09/2021 17:23", 15.6, "YES"},
				{"07/09/2021 20:04", 14.2, "NO"},
				{"08/09/2021 02:19", 12.9, "NO"},
				{"08/09/2021 11:11", 7.8, "NO"},
				{"08/09/2021 12:39", 9.7, "YES"},
				{"08/09/2021 13:34", 7.8, "NO"},
				{"08/09/2021 15:33", 11.6, "NO"},
				{"08/09/2021 16:50", 12.1, "YES"},
				{"08/09/2021 17:19", 10.2, "NO"},
				{"08/09/2021 18:15", 14.3, "YES"},
				{"08/09/2021 18:51", 10.3, "NO"},
				{"08/09/2021 19:40", 8.7, "YES"},
				{"08/09/2021 20:03", 9.5, "NO"},
				{"08/09/2021 20:27", 11.6, "YES"},
				{"08/09/2021 22:05", 23.5, "NO"},
				{"08/09/2021 22:57", 20.3, "YES"},
				{"08/09/2021 23:33", 13.2, "NO"},
				{"09/09/2021 04:47", 18.8, "YES"},
				{"09/09/2021 09:26", 18.8, "NO"},
				{"09/09/2021 11:07", 10.3, "YES"},
				{"09/09/2021 14:42", 21.1, "NO"},
				{"09/09/2021 20:06", 9.9, "NO"},
				{"09/09/2021 23:03", 9.9, "NO"},
				{"10/09/2021 00:50", 7.1, "NO"},
				{"10/09/2021 01:51", 8.3, "NO"},
				{"10/09/2021 15:22", 17.2, "YES"},
				{"10/09/2021 18:32", 7.3, "NO"},
				{"10/09/2021 20:57", 12.7, "YES"},
				{"10/09/2021 22:24", 15.7, "NO"},
				{"11/09/2021 03:57", 17.6, "YES"},
				{"11/09/2021 06:59", 12.2, "NO"},
				{"11/09/2021 12:48", 14.1, "YES"},
				{"11/09/2021 13:31", 16.9, "NO"},
				{"11/09/2021 21:11", 5.4, "NO"},
				{"11/09/2021 22:36", 18.4, "YES"},
				{"12/09/2021 03:48", 24.6, "NO"},
				{"12/09/2021 08:21", 22.0, "YES"},
				{"12/09/2021 13:42", 12.2, "NO"},
				{"12/09/2021 14:42", 10.7, "NO"},
				{"12/09/2021 15:57", 19.2, "YES"},
				{"12/09/2021 19:19", 23.1, "NO"},
				{"12/09/2021 20:41", 21.9, "YES"},
				{"13/09/2021 00:46", 23.1, "NO"},
				{"13/09/2021 03:21", 14.9, "YES"},
				{"13/09/2021 11:17", 5.6, "NO"},
				{"13/09/2021 16:07", 15.5, "YES"},
				{"13/09/2021 18:52", 15.4, "NO"},
				{"13/09/2021 20:00", 12.7, "NO"},
				{"13/09/2021 22:53", 12.1, "NO"},
				{"14/09/2021 00:04", 20.4, "YES"},
				{"14/09/2021 00:50", 16.8, "NO"},
				{"14/09/2021 13:58", 15.1, "YES"},
				{"14/09/2021 15:35", 14.7, "NO"},
				{"14/09/2021 18:12", 15.9, "YES"},
				{"14/09/2021 20:24", 21.1, "NO"},
				{"15/09/2021 01:05", 27.8, "YES"},
				{"15/09/2021 02:19", 18.2, "NO"},
				{"15/09/2021 11:30", 16.9, "YES"},
				{"15/09/2021 13:16", 21.2, "NO"},
				{"15/09/2021 13:50", 17.7, "YES"},
				{"15/09/2021 14:45", 8.6, "NO"},
				{"15/09/2021 16:54", 21.7, "YES"},
				{"15/09/2021 19:26", 22.3, "NO"},
				{"15/09/2021 19:50", 22.3, "YES"},
				{"15/09/2021 23:44", 14.9, "NO"},
				{"16/09/2021 00:10", 14.1, "YES"},
				{"16/09/2021 02:40", 5.8, "NO"},
				{"16/09/2021 08:14", 3.9, "YES"},
				{"16/09/2021 10:14", 5.7, "NO"},
				{"16/09/2021 12:39", 3.7, "YES"},
				{"16/09/2021 14:08", 6.2, "NO"},
				{"16/09/2021 16:44", 12.3, "NO"},
				{"16/09/2021 18:56", 14.3, "YES"},
				{"16/09/2021 20:35", 14.1, "NO"},
				{"16/09/2021 23:24", 11.1, "NO"},
				{"17/09/2021 00:49", 4.7, "YES"},
				{"17/09/2021 07:59", 14.1, "NO"},
				{"17/09/2021 12:39", 13.0, "NO"},
				{"17/09/2021 13:29", 16.3, "YES"},
				{"17/09/2021 14:30", 24.0, "NO"},
				{"17/09/2021 19:14", 23.7, "YES"},
				{"18/09/2021 02:49", 26.4, "NO"},
				{"18/09/2021 06:21", 21.1, "YES"},
				{"18/09/2021 13:56", 14.8, "NO"},
				{"18/09/2021 16:36", 26.2, "YES"},
				{"18/09/2021 20:50", 14.5, "NO"},
				{"18/09/2021 23:15", 12.7, "NO"},
				{"19/09/2021 02:54", 13.7, "YES"},
				{"19/09/2021 13:56", 8.9, "NO"},
				{"19/09/2021 15:10", 15.9, "YES"},
				{"19/09/2021 18:34", 16.7, "NO"},
				{"19/09/2021 22:08", 16.8, "YES"},
				{"20/09/2021 00:54", 15.1, "NO"},
				{"20/09/2021 03:39", 16.9, "YES"},
				{"20/09/2021 13:00", 10.1, "NO"},
				{"20/09/2021 17:27", 15.7, "YES"},
				{"20/09/2021 21:16", 7.8, "NO"},
				{"21/09/2021 00:48", 20.3, "YES"},
				{"21/09/2021 02:41", 19.8, "NO"},
				{"21/09/2021 15:02", 8.8, "NO"},
				{"21/09/2021 17:42", 17.0, "YES"},
				{"22/09/2021 00:16", 10.7, "NO"},
				{"22/09/2021 02:04", 18.8, "YES"},
				{"22/09/2021 09:07", 2.9, "NO"},
				{"22/09/2021 12:17", 8.9, "NO"},
				{"22/09/2021 13:33", 10.8, "NO"},
				{"22/09/2021 16:40", 4.9, "YES"},
				{"22/09/2021 18:29", 6.3, "NO"},
				{"22/09/2021 19:24", 12.8, "NO"},
				{"22/09/2021 22:56", 10.8, "YES"},
				{"23/09/2021 01:54", 15.4, "NO"},
				{"23/09/2021 12:35", 4.3, "YES"},
				{"23/09/2021 17:13", 6.4, "NO"},
				{"23/09/2021 20:56", 7.2, "NO"},
				{"23/09/2021 23:40", 12.1, "YES"},
				{"24/09/2021 00:47", 14.8, "NO"},
				{"24/09/2021 01:41", 19.4, "YES"},
				{"24/09/2021 02:10", 20.5, "NO"},
				{"24/09/2021 05:10", 13.7, "YES"},
				{"24/09/2021 13:40", 3.6, "NO"},
				{"24/09/2021 20:42", 17.8, "YES"},
				{"24/09/2021 22:41", 6.4, "NO"},
				{"25/09/2021 01:46", 17.9, "YES"},
				{"25/09/2021 04:57", 12.6, "NO"},
				{"25/09/2021 05:37", 14.8, "YES"},
				{"25/09/2021 14:02", 3.2, "NO"},
				{"25/09/2021 15:50", 11.4, "NO"},
				{"25/09/2021 18:20", 10.1, "NO"},
				{"25/09/2021 20:01", 14.9, "YES"},
				{"25/09/2021 22:27", 10.8, "NO"},
				{"25/09/2021 23:04", 10.2, "NO"},
				{"26/09/2021 00:59", 18.8, "YES"},
				{"26/09/2021 03:04", 11.6, "NO"},
				{"26/09/2021 03:05", 11.6, "NO"},
				{"26/09/2021 03:49", 12.0, "NO"},
				{"26/09/2021 15:05", 4.3, "YES"},
				{"26/09/2021 17:16", 20.2, "NO"},
				{"26/09/2021 19:23", 4.7, "NO"},
				{"26/09/2021 21:48", 18.7, "YES"},
				{"27/09/2021 00:18", 17.3, "NO"},
				{"27/09/2021 04:13", 24.4, "YES"},
				{"27/09/2021 12:23", 17.1, "NO"},
				{"27/09/2021 15:34", 18.2, "YES"},
				{"27/09/2021 17:20", 18.8, "NO"},
				{"27/09/2021 21:34", 25.1, "YES"},
				{"28/09/2021 00:23", 14.2, "NO"},
				{"28/09/2021 09:08", 10.2, "NO"},
				{"28/09/2021 16:47", 11.4, "NO"},
				{"28/09/2021 17:27", 11.5, "NO"},
				{"28/09/2021 21:36", 14.2, "YES"},
				{"29/09/2021 10:40", 17.3, "NO"},
				{"29/09/2021 13:16", 15.4, "YES"},
				{"29/09/2021 18:02", 25.2, "NO"},
				{"29/09/2021 20:06", 15.7, "YES"},
				{"29/09/2021 22:17", 14.0, "NO"},
				{"29/09/2021 23:41", 17.7, "YES"},
				{"30/09/2021 01:47", 7.4, "NO"},
				{"30/09/2021 10:56", 15.4, "YES"},
				{"30/09/2021 13:33", 6.9, "NO"},
				{"30/09/2021 14:35", 5.9, "NO"},
				{"30/09/2021 15:53", 12.1, "YES"},
				{"30/09/2021 20:25", 13.7, "NO"},
				{"30/09/2021 23:48", 17.3, "YES"},
				{"01/10/2021 01:47", 17.6, "NO"},
				{"01/10/2021 10:55", 6.7, "NO"},
				{"01/10/2021 12:18", 7.2, "NO"},
				{"01/10/2021 15:15", 16.4, "YES"},
				{"01/10/2021 17:50", 4.7, "NO"},
				{"01/10/2021 23:52", 12.0, "NO"},
				{"02/10/2021 01:14", 15.5, "YES"},
				{"02/10/2021 04:50", 22.1, "NO"},
				{"02/10/2021 12:13", 5.2, "NO"},
				{"02/10/2021 16:15", 9.0, "NO"},
				{"02/10/2021 17:15", 9.5, "NO"},
				{"02/10/2021 18:11", 6.3, "NO"},
				{"02/10/2021 21:38", 14.4, "YES"},
				{"03/10/2021 01:39", 15.5, "NO"},
				{"03/10/2021 02:57", 12.8, "NO"},
				{"03/10/2021 12:40", 13.2, "NO"},
				{"04/10/2021 16:46", 14.4, "YES"},
				{"04/10/2021 18:00", 8.7, "NO"},
				{"04/10/2021 18:40", 6.3, "YES"},
				{"04/10/2021 19:16", 5.0, "NO"},
				{"05/10/2021 01:21", 14.3, "YES"},
				{"05/10/2021 11:43", 9.6, "NO"},
				{"05/10/2021 21:27", 8.7, "NO"},
				{"05/10/2021 22:36", 15.2, "YES"},
				{"06/10/2021 00:59", 12.6, "NO"},
				{"06/10/2021 11:56", 7.2, "NO"},
				{"06/10/2021 17:46", 10.5, "NO"},
				{"06/10/2021 18:51", 16.0, "YES"},
				{"06/10/2021 20:47", 17.1, "NO"},
				{"07/10/2021 00:10", 14.7, "YES"},
				{"07/10/2021 12:09", 10.1, "NO"},
				{"07/10/2021 12:36", 9.1, "NO"},
				{"07/10/2021 17:12", 6.4, "NO"},
				{"07/10/2021 19:36", 14.0, "YES"},
				{"07/10/2021 22:10", 13.7, "NO"},
				{"08/10/2021 00:43", 14.1, "YES"},
				{"08/10/2021 02:28", 16.4, "NO"},
				{"08/10/2021 04:46", 17.3, "YES"},
				{"08/10/2021 05:31", 15.4, "NO"},
				{"08/10/2021 13:37", 2.9, "YES"},
				{"08/10/2021 17:37", 7.8, "NO"},
				{"08/10/2021 18:42", 4.1, "YES"},
				{"08/10/2021 20:14", 7.8, "NO"},
				{"08/10/2021 22:48", 20.7, "YES"},
				{"09/10/2021 01:34", 17.7, "NO"},
				{"09/10/2021 02:14", 20.0, "YES"},
				{"09/10/2021 12:21", 9.7, "NO"},
				{"09/10/2021 15:55", 8.2, "NO"},
				{"09/10/2021 16:47", 11.2, "NO"},
				{"09/10/2021 17:09", 14.2, "YES"},
				{"09/10/2021 18:55", 17.9, "NO"},
				{"09/10/2021 20:10", 11.3, "NO"},
				{"09/10/2021 20:50", 10.7, "NO"},
				{"09/10/2021 21:22", 14.3, "YES"},
				{"09/10/2021 22:21", 17.9, "NO"},
				{"09/10/2021 23:07", 18.1, "YES"},
				{"10/10/2021 00:03", 14.2, "NO"},
				{"10/10/2021 00:48", 16.3, "YES"},
				{"10/10/2021 03:17", 16.5, "NO"},
				{"10/10/2021 11:09", 3.3, "YES"},
				{"10/10/2021 19:20", 5.8, "NO"},
				{"10/10/2021 21:11", 13.9, "YES"},
				{"11/10/2021 00:55", 9.9, "NO"},
				{"11/10/2021 07:59", 3.3, "YES"},
				{"11/10/2021 09:57", 17.3, "NO"},
				{"11/10/2021 16:52", 14.1, "YES"},
				{"11/10/2021 20:16", 18.8, "NO"},
				{"12/10/2021 00:42", 16.5, "YES"},
				{"12/10/2021 12:19", 7.6, "NO"},
				{"12/10/2021 16:43", 3.7, "YES"},
				{"12/10/2021 18:28", 14.9, "NO"},
				{"12/10/2021 20:40", 9.0, "NO"},
				{"12/10/2021 22:08", 13.6, "YES"},
				{"13/10/2021 02:13", 17.6, "NO"},
				{"13/10/2021 09:02", 14.1, "YES"},
				{"13/10/2021 12:21", 12.2, "NO"},
				{"13/10/2021 15:38", 9.7, "NO"},
				{"13/10/2021 18:33", 11.0, "NO"},
				{"13/10/2021 19:49", 15.3, "YES"},
				{"13/10/2021 21:01", 12.6, "NO"},
				{"13/10/2021 22:09", 14.2, "YES"},
				{"13/10/2021 23:33", 14.8, "NO"},
				{"14/10/2021 02:36", 16.1, "YES"},
				{"14/10/2021 10:22", 4.2, "NO"},
				{"14/10/2021 15:01", 11.3, "NO"},
				{"14/10/2021 18:12", 7.0, "NO"},
				{"14/10/2021 19:29", 17.1, "YES"},
				{"14/10/2021 21:38", 10.3, "NO"},
				{"14/10/2021 21:59", 11.7, "YES"},
				{"14/10/2021 23:12", 12.6, "NO"},
				{"15/10/2021 00:43", 16.8, "YES"},
				{"15/10/2021 03:06", 15.3, "NO"},
				{"15/10/2021 13:51", 4.7, "YES"},
				{"15/10/2021 18:27", 15.2, "NO"},
				{"15/10/2021 19:04", 16.4, "YES"},
				{"15/10/2021 20:20", 5.7, "NO"},
				{"15/10/2021 21:39", 9.6, "NO"},
				{"15/10/2021 22:32", 10.9, "NO"},
				{"16/10/2021 00:01", 8.2, "NO"},
				{"16/10/2021 00:46", 8.7, "NO"},
				{"16/10/2021 02:10", 10.1, "NO"},
				{"16/10/2021 09:06", 13.2, "NO"},
				{"16/10/2021 13:25", 6.6, "YES"},
				{"16/10/2021 15:08", 12.6, "NO"},
				{"16/10/2021 16:41", 8.0, "YES"},
				{"16/10/2021 17:30", 4.2, "NO"},
				{"16/10/2021 18:51", 9.7, "NO"},
				{"16/10/2021 20:31", 14.4, "YES"},
				{"16/10/2021 22:10", 9.9, "NO"},
				{"17/10/2021 00:15", 15.4, "YES"},
				{"17/10/2021 00:29", 17.3, "NO"},
				{"17/10/2021 00:29", 17.3, "YES"},
				{"17/10/2021 02:23", 16.0, "NO"},
				{"17/10/2021 08:48", 16.0, "YES"},
				{"17/10/2021 12:24", 5.7, "NO"},
				{"17/10/2021 14:34", 13.8, "YES"},
				{"17/10/2021 16:49", 8.3, "NO"},
				{"17/10/2021 17:11", 10.8, "NO"},
				{"17/10/2021 20:30", 14.8, "YES"},
				{"17/10/2021 22:04", 14.1, "NO"},
				{"17/10/2021 23:48", 6.9, "NO"},
				{"18/10/2021 08:37", 7.1, "NO"},
				{"18/10/2021 10:41", 17.2, "YES"},
				{"18/10/2021 11:20", 14.8, "NO"},
				{"18/10/2021 14:08", 5.1, "NO"},
				{"18/10/2021 18:03", 16.1, "YES"},
				{"18/10/2021 22:15", 16.3, "NO"},
				{"19/10/2021 02:17", 10.2, "NO"},
				{"19/10/2021 13:40", 3.3, "YES"},
				{"19/10/2021 15:53", 8.3, "NO"},
				{"19/10/2021 17:06", 7.8, "YES"},
				{"19/10/2021 18:49", 8.3, "NO"},
				{"19/10/2021 22:58", 17.8, "YES"},
				{"20/10/2021 01:27", 15.9, "NO"},
				{"20/10/2021 02:00", 17.1, "YES"},
				{"20/10/2021 15:33", 16.8, "NO"},
				{"20/10/2021 21:29", 12.8, "YES"},
				

		};

	DataLoadFile dataLoadFile = new DataLoadLibreView();

	@Override
	protected Object[][] getExpectedTestResults() {
		return EXPECTED_ENTRY_RESULTS;
	}

	@Override
	protected String getResourceFileName() {
		return RESOURCE_FILE_NAME_STRING;
	}

	@Override
	protected DataLoadFile getDataLoadFile() {
		return dataLoadFile;
	}

	@Test
	public void doTestLoad()
	{
		performTestLoad();
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
