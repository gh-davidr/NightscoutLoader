package davidRichardson;


public class Version 
{
	private String m_Version;
	private String m_AboutText;
	private String m_Author;
	private String m_ContactUs;

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
		
		m_Version    = "V2.4";
		m_Author     = "David Richardson";
		m_ContactUs  = "NightscoutLoader@gmail.com";

		m_AboutText += "Nightscout Loader Version" + m_Version + "\r\n\r\n";
		m_AboutText += "Nightscout Loader\r\n\r\nThree main functions offered:\r\n\r\n";
		m_AboutText += "  (1) Load data from meter/pump database and upload into Nightscout for display on viewer\r\n";
		m_AboutText += "  (2) View Nightscout treatment records and support edits\r\n";
		m_AboutText += "  (3) Perform Analysis on the results\r\n";
		m_AboutText += "  (4) Minor features as below\r\n";
		m_AboutText += "    (a) Download of CarePortal to Excel\r\n";
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

		m_AboutText += "\r\n\r\n";
		m_AboutText += "Written by " + m_Author + " - January through October 2016\r\n";
	}

	/**
	 * @return the m_Version
	 */
	public synchronized String getM_Version() {
		return m_Version;
	}

	/**
	 * @param m_Version the m_Version to set
	 */
	public synchronized void setM_Version(String m_Version) {
		this.m_Version = m_Version;
	}

	/**
	 * @return the m_AboutText
	 */
	public synchronized String getM_AboutText() {
		return m_AboutText;
	}

	/**
	 * @param m_AboutText the m_AboutText to set
	 */
	public synchronized void setM_AboutText(String m_AboutText) {
		this.m_AboutText = m_AboutText;
	}

	/**
	 * @return the m_Author
	 */
	public synchronized String getM_Author() {
		return m_Author;
	}

	/**
	 * @param m_Author the m_Author to set
	 */
	public synchronized void setM_Author(String m_Author) {
		this.m_Author = m_Author;
	}


	/**
	 * @return the m_ContactUs
	 */
	public synchronized String getM_ContactUs() {
		return m_ContactUs;
	}


	/**
	 * @param m_ContactUs the m_ContactUs to set
	 */
	public synchronized void setM_ContactUs(String m_ContactUs) {
		this.m_ContactUs = m_ContactUs;
	}

}
