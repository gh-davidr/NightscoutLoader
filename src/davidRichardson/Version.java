package davidRichardson;


public class Version 
{
	private String m_Version;
	private String m_AboutText;
	private String m_Author;
	private String m_ContactUs;
	private String m_GoogleDriveHelpURI;

	private static Version m_Instance=null;

	public static Version getInstance()
	{
		if (m_Instance == null)
		{
			m_Instance = new Version();
		}
		return m_Instance;
	}

	private Version()
	{
		m_Version    = new String();
		m_AboutText  = new String();
		m_Author     = new String();
		m_ContactUs  = new String();
		m_GoogleDriveHelpURI = new String();

		m_Version    = "V3.3";
		m_Author     = "David Richardson (+ other contributors)";
		m_ContactUs  = "NightscoutLoader@gmail.com";
		//		m_GoogleDriveHelpURI = "https://drive.google.com/open?id=0BxlKJmCnE32_cEZMbmk4TTlZRUk";
		
		// This is a static URL.
		// m_GoogleDriveHelpURI = "https://drive.google.com/open?id=0BxlKJmCnE32_MTc4LThSamk2dzA";
		// m_GoogleDriveHelpURI = "https://drive.google.com/open?id=0BxlKJmCnE32_RnZUTmxleFkyWlU"; // V2.6
		// m_GoogleDriveHelpURI = "https://drive.google.com/open?id=0BxlKJmCnE32_d0tpQmtGZXptT28"; // V2.7
		// m_GoogleDriveHelpURI = "https://drive.google.com/open?id=1FjCzKM4G8xWrChXico9uvblHQhAft4G-"; // V3.0
		// m_GoogleDriveHelpURI = "https://1drv.ms/b/s!At9qWUeNOZ5nbs-PYVnZ8ziZ8ew";  // V3.0 OneDrive

		m_GoogleDriveHelpURI = "https://1drv.ms/b/s!At9qWUeNOZ5nedye6_g5eSSAqlE";  // V3.3 OneDrive logged as Dyyyd@kxxxxxxe.org
		
		m_AboutText += "Nightscout Loader Version" + m_Version + "\r\n\r\n";
		m_AboutText += "Nightscout Loader\r\n\r\nThree main functions offered:\r\n\r\n";
		m_AboutText += "  (1) Load Care Portal data from meter/pump database and upload into Nightscout for display on viewer\r\n";
		m_AboutText += "  (2) Load CGM data from supported sources (Diasend) and upload into Nightscout for display on viewer\r\n";
		m_AboutText += "  (3) View Nightscout treatment records and support edits\r\n";
		m_AboutText += "  (4) Perform Analysis on the Care Portal AND CGM results\r\n";
		m_AboutText += "  (5) Minor features as below\r\n";
		m_AboutText += "    (a) Download of Care Portal to Excel\r\n";
		m_AboutText += "    (b) Exports of Care Portal and CGMS from Mongo as JSON\r\n";
		m_AboutText += "    (c) Full featured audit history for control of Care Portal loads\r\n";
		m_AboutText += "    (d) Heartbeat checks on Nightscout Mongo alerting of independent updates\r\n";
		m_AboutText += "\r\n\r\n";
		m_AboutText += "Change Log\r\n\r\n";
		m_AboutText += " 1.1  - Fix CarbTime issue from Andy\r\n";
		m_AboutText += "      - Fix Medtronic Date load issue from Andy\r\n";
		m_AboutText += " 2.0  - Add audit log features to track & backout data uploaded\r\n";
		m_AboutText += "      - Add sophisticated meal and overnight trend analysis stored as Excel\r\n";
		m_AboutText += " 2.1  - Fix to Medtronic load.  All previous trial data was January 2016\r\n";
		m_AboutText += "        which masked an issue in month parsing for date handling.  \r\n";
		m_AboutText += " 2.11 - Fix that conforms with storage of UTC times in Mongo.  \r\n";
		m_AboutText += " 2.2  - Introduce controls for timezone (in case of travel) and file input date formats. \r\n";
		m_AboutText += "        Also included Feedback menu & Options tab in the rich analyzer Excel output.  \r\n";
		m_AboutText += " 2.3  - Added some improvements to the Analytics engine:\r\n";
		m_AboutText += "           Trends now include BG results that span a meal with no carbs\r\n";
		m_AboutText += "           Additional tab that focuses on the skipped meal trends\r\n";
		m_AboutText += "           Added a guidance tab that provides some highlevel directions\r\n";
		m_AboutText += "        Fix to temp basals - stored as wrong event type in Night scout.  \r\n";
		m_AboutText += " 2.3.1- (Never released - more added to create 2.4)\r\n";
		m_AboutText += "           Medtronic files can have multiple meters.\r\n";
		m_AboutText += "           Also restored use of CSV for medtronic files (was xls)\r\n";
		m_AboutText += " 2.4  - Several enhancements across application:\r\n";
		m_AboutText += "           On startup, after sync and loads an Analyzer runs to provide short summary in text window\r\n";
		m_AboutText += "           Full history analysis performed in separate thread for comparison with date range analysis\r\n";
		m_AboutText += "           Analyzer now makes use of percentages to report recurring trends\r\n";
		m_AboutText += "           Duplicate entry detection integrated with audit log and analyzer\r\n";
		m_AboutText += "           IDs in Analyzer Excel Tabs only appear in Advanced Option mode\r\n";
		m_AboutText += " 2.4.1a- Temporary early release for Paul Cooper\r\n";
		m_AboutText += " 2.5  - Stable release extending formally including the following\r\n";
		m_AboutText += "           Introduced Roche SQL File extract for installations that are having issues connecting to SQL Server for Roche meters\r\n";
		m_AboutText += "           Minor enhancements to proximity/duplicate checking\r\n";
		m_AboutText += "           A refined merge algorithm based on seeing more data sets.  Less chance some results are overlooked.\r\n";
		m_AboutText += "           Enhanced the Treatment Details screen to include up down navigation and also a Find feature.\r\n";
		m_AboutText += "           On request, included the option of disabling certain meal times from analysis.\r\n";
		m_AboutText += "           Can now successfully read back HAPP APP entries that use a different time convention\r\n";
		m_AboutText += "           Improvement to Diasend temp basal inference\r\n";
		m_AboutText += "           CGM Loading for Analysis\r\n";
		m_AboutText += "           Extended tabs in Analytical output to include CGM analysis where dates overlap\r\n";

		m_AboutText += "           Introduced Roche SQL File extract for installations that are having issues connecting to SQL Server for Roche meters\r\n";
		m_AboutText += "           Minor enhancements to proximity/duplicate checking.  Specifically, Nightscout Loader for the first time can now identify existing manually entered Treatment Entries that match meter/pump data.  For the first time, Nightscout Loader now offers the possibility of deleting manually loaded data, and not just data that’s under its responsibility (data from meter/pump).  This is based on a request from a user that manually loads data in Care Portal but wants the data from meter/pump to supersede it.\r\n";
		m_AboutText += "           A refined merge algorithm based on seeing more data sets.  Less chance some results are overlooked.\r\n";
		m_AboutText += "           Enhanced the Treatment Details screen to include up down navigation and also a Find feature.\r\n";
		m_AboutText += "           On request, included the option of disabling certain meal times from analysis.\r\n";
		m_AboutText += "           Inclusion of CGM Loading\r\n";
		m_AboutText += "           Inclusion of CGM Analysis result tabs to Excel analysis output\r\n";
		m_AboutText += "           Option of variable level of detail in Excel results workbook\r\n";
		m_AboutText += "           Disabling incomplete features – Diasend temporary basals and OmniPod binary file reader\r\n";
		m_AboutText += "           Having added a feature to disable Diasend temp basals, introduced a much better algorithm to infer them more accurately\r\n";
		m_AboutText += "           Fix to issue where last value read from file is duplicated\r\n";
		m_AboutText += "           Reference to Online help – link to PDF on Google Drive\r\n";
		m_AboutText += " 2.6     - Release introducing the following\r\n";
		m_AboutText += "           Support for T:Slim exports\r\n";
		m_AboutText += "           Fixes to MongoDB Reads as reported by Mátyási Péter\r\n";
		m_AboutText += " 2.7     - Release introducing the following\r\n";
		m_AboutText += "           Fix to Find / Details screen down nav at bottom of list\r\n";
		m_AboutText += "           Slightly different new Medtronic file format seen in the field now supported \r\n";
		m_AboutText += "           Proper support for Medtronic Set Change\r\n";
		m_AboutText += "           Support for running Autotune\r\n";
		m_AboutText += "              Inclusion of Autotune output in Excel Analysis results file\r\n";
		m_AboutText += "              Quick Run autotune with text output based on most recent CGM\r\n";
		m_AboutText += "              Autotune Profile Editor with automatic backup remotely to Nightscout Loader Backup directory\r\n";
		m_AboutText += "              Autotune Profile Editor can read settings directly from Diasend\r\n";
		m_AboutText += " 2.7.1   - Patch fix to Medtronic reader\r\n";
		m_AboutText += " 2.7.2   - Improved Skip Meal categorization - look for carbs between BGs\r\n";
		m_AboutText += " 3.0     - Addition of CGM upload from Diasend into Nightscout\r\n";
		m_AboutText += "           Glooko/Diasend seems to generate files with varying date formats.  Now pre-scan to try and figure out format\r\n";
		m_AboutText += "           Fixed connection issue for Autotune Server that broke in refactoring prior to V2.7\r\n";
		m_AboutText += "           Added convenience function to drive an autotune run from a Diasend file (sets basal rates and dates)\r\n";
		m_AboutText += "           Also some minor improvements to thread management\r\n";
		m_AboutText += " 3.01      Minor revision - bug fix to Analyzer which broke due to a change in 3.0\r\n";
		m_AboutText += " 3.02      Couple of patch fixes by skuzzer (Adam Wale) - \r\n";
		m_AboutText += "               Allow upload of Diasend file if any tab has data\r\n";
		m_AboutText += "               Allow Diasend files with only CGM data present\r\n";
		m_AboutText += " 3.1       Major Pull Request (PR10) contribution by kskandispersonal - \r\n";
		m_AboutText += "               Correct interpretation of combo boluses from Diasend\r\n";
		m_AboutText += "               Improved temporary basal inferrence from Diasend\r\n";
		m_AboutText += " 3.2       Included support for CellNovo (previously added as custom patch to 3.02 for one user) - \r\n";
		m_AboutText += " 3.3       Major Pull Request (PR10) contribution by kskandispersonal - \r\n";
		m_AboutText += "               Address Diasend change to export file\r\n";
		m_AboutText += "               Now support alternate Medtronic Carelink file format as reported May 2019\r\n";
		
		m_AboutText += "\r\n\r\n";
		m_AboutText += "Written by " + m_Author + " - January 2016 through July 2019\r\n";
	}

	/**
	 * @return the m_Version
	 */
	public synchronized String getM_Version() {
		return m_Version;
	}


	/**
	 * @return the m_AboutText
	 */
	public synchronized String getM_AboutText() {
		return m_AboutText;
	}

	/**
	 * @return the m_Author
	 */
	public synchronized String getM_Author() {
		return m_Author;
	}


	/**
	 * @return the m_ContactUs
	 */
	public synchronized String getM_ContactUs() {
		return m_ContactUs;
	}


	/**
	 * @return the m_GoogleDriveHelpURI
	 */
	public synchronized String getM_GoogleDriveHelpURI() {
		return m_GoogleDriveHelpURI;
	}

}
