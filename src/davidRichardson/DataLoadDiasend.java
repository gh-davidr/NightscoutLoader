package davidRichardson;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

// Diasend knows how to read the Excel file generated by Diasend Corp

public class DataLoadDiasend extends DataLoadBase
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	private String   m_initFilename;  // File with Diasend results in it.

	final static String m_GlucoseTabName  = new String("Name and glucose");
	final static String m_InsulinTabName  = new String("Insulin use and carbs");
	final static String m_SettingsTabName = new String("Insulin pump settings");

	/*	final static int m_GlucoseTab  = 0; // Excel tab position with glucose data
	final static int m_InsulinTab  = 0; // Excel tab position with insulin & use data
	final static int m_SettingsTab = 0;
	 */
	final static int m_GlucoseRowDateRange   = 4;
	final static int m_GlucoseRowDataHeaders = 5;
	final static int m_GlucoseRowDataStart   = 6;

	final static int m_InsulinRowDataHeaders = 1;
	final static int m_InsulinRowDataStart   = 2;

	final static String m_SettingsBasalStartString  = "Basal profiles";
	final static String m_SettingsIntervalString    = "Interval";
	final static String m_SettingsActiveBasalString = "Active basal program";
	final static String m_SettingsBasalsEndString   = "I:C ratio settings";

	POIFSFileSystem m_ExcelFileSystem        = null;
	HSSFWorkbook    m_ExcelWorkBook          = null;

	// Loaded from raw device such as meter, pump DB or file
	protected ArrayList <DBResultDiasendBasalSetting> m_BasalSettings;

	protected ArrayList <DBResultDiasendBasalSetting> m_BasalSettings1;
	protected ArrayList <DBResultDiasendBasalSetting> m_BasalSettings2;
	protected ArrayList <DBResultDiasendBasalSetting> m_BasalSettings3;
	protected ArrayList <DBResultDiasendBasalSetting> m_BasalSettings4;
	protected ArrayList <DBResultDiasendBasalSetting> m_BasalSettings5;
	protected ArrayList <DBResultDiasendBasalSetting> m_BasalSettings6;
	protected ArrayList <DBResultDiasendBasalSetting> m_BasalSettings7;

	protected String                                  m_ActiveBasal;

	@Override
	protected String getDevice() 
	{
		return "Diasend";
	}

	public void loadDBResults(String fileName) throws UnknownHostException, SQLException, ClassNotFoundException, IOException 
	{
		m_BasalSettings  = null;

		m_BasalSettings1 = null;
		m_BasalSettings2 = null;
		m_BasalSettings3 = null;
		m_BasalSettings4 = null;
		m_BasalSettings5 = null;
		m_BasalSettings6 = null;
		m_BasalSettings7 = null;

		m_ActiveBasal    = null;

		initialize(fileName);
		loadDBResults();
	}

	@Override
	public void loadDBResults() throws UnknownHostException, SQLException, ClassNotFoundException, IOException 
	{
		// Expect initialize to have been called first

		loadInsulinPumpSettings();     // Load Settings first
		loadDBResultsFromGlucoseTab();
		loadDBResultsFromInsulinTab();

		// For debug, drop a list of all results so far
		m_Logger.log(Level.FINE, "<"+this.getClass().getName()+">" + "Summary of Diasend Results BEFORE load & Sort");
		for (DBResult res : rawResultsFromDB)
		{
			m_Logger.log(Level.FINE, "<"+this.getClass().getName()+">" + "  " + res.rawToString());
		}


		sortDBResults();

		// For debug, drop a list of all results so far
		m_Logger.log(Level.FINE, "<"+this.getClass().getName()+">" + "Summary of Diasend Results AFTER load & Sort");
		for (DBResult res : rawResultsFromDB)
		{
			m_Logger.log(Level.FINE, "<"+this.getClass().getName()+">" + "  " + res.rawToString());
		}

		locateTempBasals();


		// For debug, drop a list of all results so far
		m_Logger.log(Level.FINE, "<"+this.getClass().getName()+">" + "Summary of Diasend Results AFTER Locating Temp Basals");
		for (DBResult res : rawResultsFromDB)
		{
			m_Logger.log(Level.FINE, "<"+this.getClass().getName()+">" + "  " + res.rawToString());
		}


		convertDBResultsToTreatments();

		// For debug, drop a list of all results so far
		m_Logger.log(Level.FINE, "<"+this.getClass().getName()+">" + "Summary of Diasend Results AFTER conversion");
		for (DBResult res : resultTreatments)
		{
			m_Logger.log(Level.FINE, "<"+this.getClass().getName()+">" + "  " + res.toString());
		}

		// Now close the workbook
		m_ExcelWorkBook.close();
	}

	// New initialize routine
	public void initialize(String filename)
	{
		m_initFilename = filename;
		try 
		{
			m_ExcelFileSystem = new POIFSFileSystem(new FileInputStream(m_initFilename));
			m_ExcelWorkBook   = new HSSFWorkbook(m_ExcelFileSystem);
		} 
		catch(Exception ioe) 
		{
			ioe.printStackTrace();
		}
		// Also clear out any stored results
		clearLists();
	}

	protected HSSFSheet getNamedWorksheet(String workSheetName)
	{
		HSSFSheet result = null;

		int numSheets   = m_ExcelWorkBook.getNumberOfSheets();
		HSSFSheet sheet = null; 

		// Doesn't appear to work :-(
		// result = m_ExcelWorkBook.getSheet(workSheetName);

		for (int i = 0; i < numSheets && result == null; i++)
		{
			sheet = m_ExcelWorkBook.getSheetAt(i);
			if (workSheetName.equals(sheet.getSheetName()))
			{
				result = sheet;
			}
		}

		return result;
	}

	enum BasalRateLineGroup
	{
		Outside,

		Basals_Start,
		Program_Name,
		Interval,
		Rates,
		FirstGap,
		Total,
		SecondGap
	}

	private ArrayList <DBResultDiasendBasalSetting> loadBasalRate(int programNum, HSSFSheet sheet)
	{
		ArrayList <DBResultDiasendBasalSetting> result = null;

		try 
		{
			if (sheet != null)
			{
				HSSFRow row;

				int rows; // No of rows
				rows = sheet.getPhysicalNumberOfRows();

				int cols = 0; // No of columns
				int tmp = 0;

				// Interested in loading the basal profiles.
				// At this stage, not sure how data looks when device has multiple basal profiles setup
				// Will need to see

				// This trick ensures that we get the data properly even if it doesn't start from first few rows
				for(int i = 0; i < 10 || i < rows; i++) 
				{
					row = sheet.getRow(i);
					if(row != null) 
					{
						tmp = sheet.getRow(i).getPhysicalNumberOfCells();
						if(tmp > cols) cols = tmp;
					}
				}

				String profileName              = new String("Program: " + programNum);

				boolean foundProfileName        = false;
				boolean inProfileSection        = false;
				boolean completedProfileSection = false;

				for(int r = 0; r <= rows && !completedProfileSection; r++) 
				{
					row = sheet.getRow(r);

					if (row == null && inProfileSection)
					{
						completedProfileSection = true;
					}
					else if (row != null)
					{
						String cell1 = DBResultDiasendBasalSetting.getCellAsString(row, 0);

						if (cell1.equals(profileName))
						{
							foundProfileName = true;
							result = new ArrayList <DBResultDiasendBasalSetting>();
						}
						else if (foundProfileName && cell1.equals(m_SettingsIntervalString))
						{
							inProfileSection = true;
						}
						else if (foundProfileName && inProfileSection)
						{
							DBResultDiasendBasalSetting basal = new DBResultDiasendBasalSetting(profileName, row);
							result.add(basal);
						}
					}
				}
			}
		} 
		catch(Exception ioe) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + " Exception loading Basal Rate " + programNum + " "+ ioe.getMessage());
		}

		return result;
	}

	private String getActiveBasalRate(HSSFSheet sheet)
	{
		String result = null;

		try 
		{
			if (sheet != null)
			{
				HSSFRow row;

				int rows; // No of rows
				rows = sheet.getPhysicalNumberOfRows();

				int cols = 0; // No of columns
				int tmp = 0;

				// Interested in loading the basal profiles.
				// At this stage, not sure how data looks when device has multiple basal profiles setup
				// Will need to see

				// This trick ensures that we get the data properly even if it doesn't start from first few rows
				for(int i = 0; i < 10 || i < rows; i++) 
				{
					row = sheet.getRow(i);
					if(row != null) 
					{
						tmp = sheet.getRow(i).getPhysicalNumberOfCells();
						if(tmp > cols) cols = tmp;
					}
				}

				for(int r = 0; r <= rows && result == null; r++) 
				{
					row = sheet.getRow(r);

					if (row != null)
					{
						String cell1 = DBResultDiasendBasalSetting.getCellAsString(row, 0);
						if (cell1.equals(m_SettingsActiveBasalString))
						{
							result = "Program: " + DBResultDiasendBasalSetting.getCellAsString(row, 1);  // Result is next cell
						}
					}
				}
			} 
		}
		catch(Exception ioe) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + " Exception locating active Basal Rate " + ioe.getMessage());
		}

		return result;
	}


	private void loadInsulinPumpSettings()
	{
		HSSFSheet sheet = getNamedWorksheet(m_SettingsTabName); // m_ExcelWorkBook.getSheet(m_SettingsTabName);

		if (sheet != null)
		{
			m_ActiveBasal    = getActiveBasalRate(sheet);

			m_BasalSettings1 = loadBasalRate(1, sheet);
			m_BasalSettings2 = loadBasalRate(2, sheet);
			m_BasalSettings3 = loadBasalRate(3, sheet);
			m_BasalSettings4 = loadBasalRate(4, sheet);
			m_BasalSettings5 = loadBasalRate(5, sheet);
			m_BasalSettings6 = loadBasalRate(6, sheet);
			m_BasalSettings7 = loadBasalRate(7, sheet);

			if (m_ActiveBasal != null)
			{
				m_BasalSettings = (m_ActiveBasal.equals("Program: 1")) ? m_BasalSettings1 : m_BasalSettings;
				m_BasalSettings = (m_ActiveBasal.equals("Program: 2")) ? m_BasalSettings2 : m_BasalSettings;
				m_BasalSettings = (m_ActiveBasal.equals("Program: 3")) ? m_BasalSettings3 : m_BasalSettings;
				m_BasalSettings = (m_ActiveBasal.equals("Program: 4")) ? m_BasalSettings4 : m_BasalSettings;
				m_BasalSettings = (m_ActiveBasal.equals("Program: 5")) ? m_BasalSettings5 : m_BasalSettings;
				m_BasalSettings = (m_ActiveBasal.equals("Program: 6")) ? m_BasalSettings6 : m_BasalSettings;
				m_BasalSettings = (m_ActiveBasal.equals("Program: 7")) ? m_BasalSettings7 : m_BasalSettings;
			}
		}
	}


	private void loadDBResultsFromGlucoseTab()
	{
		try 
		{			
			HSSFSheet sheet = getNamedWorksheet(m_GlucoseTabName);  // m_ExcelWorkBook.getSheet(m_GlucoseTabName);
			HSSFRow row;

			int rows; // No of rows
			rows = sheet.getPhysicalNumberOfRows();

			int cols = 0; // No of columns
			int tmp = 0;

			// This trick ensures that we get the data properly even if it doesn't start from first few rows
			for(int i = 0; i < 10 || i < rows; i++) 
			{
				row = sheet.getRow(i);
				if(row != null) 
				{
					tmp = sheet.getRow(i).getPhysicalNumberOfCells();
					if(tmp > cols) cols = tmp;
				}
			}

			for(int r = 0; r <= rows; r++) 
			{
				row = sheet.getRow(r);
				if (r+1 == m_GlucoseRowDateRange)
				{
					DBResultDiasend.initializeGlucoseDateRange(row);
				}
				else if (r+1 == m_GlucoseRowDataHeaders)
				{
					DBResultDiasend.initializeGlucoseHeaders(row);
				}
				else if (r+1 >= m_GlucoseRowDataStart)
				{
					DBResultDiasend res = new DBResultDiasend(row, false);
					if (res.isValid())
					{
						rawResultsFromDB.add(res);
						m_Logger.log(Level.FINEST, "<"+this.getClass().getName()+">" + "Result added for " + res.toString());
					}

				}
			}
		} 
		catch(Exception ioe) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + " Exception loading Glucose tab " + ioe.getMessage());
		}

	}

	private void loadDBResultsFromInsulinTab()
	{
		try 
		{
			HSSFSheet sheet = getNamedWorksheet(m_InsulinTabName); // m_ExcelWorkBook.getSheet(m_InsulinTabName);
			HSSFRow row;

			int rows = sheet.getPhysicalNumberOfRows(); // No of rows

			int cols = 0; // No of columns
			int tmp = 0;

			// This trick ensures that we get the data properly even if it doesn't start from first few rows
			for(int i = 0; i < 10 || i < rows; i++) 
			{
				row = sheet.getRow(i);
				if(row != null) 
				{
					tmp = sheet.getRow(i).getPhysicalNumberOfCells();
					if(tmp > cols) cols = tmp;
				}
			}

			for(int r = 0; r < rows; r++) 
			{
				row = sheet.getRow(r);

				if (r+1 == m_InsulinRowDataHeaders)
				{
					DBResultDiasend.initializeInsulinHeaders(row);
				}
				else if (r+1 >= m_InsulinRowDataStart)
				{
					DBResultDiasend res = new DBResultDiasend(row, true);
					if (res.isValid())
					{
						// Check if this is a Basal rate
						if (res.getM_ResultType().equals("Basal"))
						{
							/// Do a look up on basal rates from settings
							// If this is a temp basal then we do keep it.
						}
						rawResultsFromDB.add(res);
						m_Logger.log(Level.FINEST, "<"+this.getClass().getName()+">" + "Result added for " + res.toString());
					}

				}
			}
		} 
		catch(Exception ioe) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + " Exception loading Insulin tab " + ioe.getMessage());
		}
	}

	private void sortDBResults()
	{
		// Sort the Mongo Results
		Collections.sort(rawResultsFromDB, new ResultFromDBComparator(false));
	}

	private DBResultDiasendBasalSetting getPrevalentBasalRate(DBResult basalResult)
	{
		DBResultDiasendBasalSetting result = null;
		DBResultDiasendBasalSetting prev   = null;
		if (m_BasalSettings != null)
		{
			for (DBResultDiasendBasalSetting c : m_BasalSettings)
			{
				try {
					if (result == null && CommonUtils.isTimeAfter(c.getM_Time(), basalResult.getM_Time()))
					{
						result = prev;
						break;
					}
					else
					{
						prev = c;
					}
				} 
				catch (ParseException e) 
				{
					m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + 
							"getPrevalentBasalRate - Unexpected error identifying basal rate: " + basalResult.toString());
				}
			}
		}

		return result;
	}

	private void locateTempBasals()
	{
		// Iterate over the raw results looking for basal rates that have changed.
		// Assume the list is ordered in time.
		//		boolean  tempStarted   = false;
		if (PrefsNightScoutLoader.getInstance().isM_InferDiasendTempBasals())
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
							m_Logger.log(Level.INFO, 
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

							resultTreatments.add(tempBasalStart);

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
	
	public static boolean isDiasend(String fileName)
	{
		boolean result = false;

		try 
		{
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(fileName));
			HSSFWorkbook wb = new HSSFWorkbook(fs);

			HSSFSheet glucoseSheet = wb.getSheet(m_GlucoseTabName);
			HSSFSheet insulinSheet = wb.getSheet(m_InsulinTabName);

			if ( (glucoseSheet != null && glucoseSheet.getPhysicalNumberOfRows() > 0) // Glucose tab is there with rows
					&&   (insulinSheet != null && insulinSheet.getPhysicalNumberOfRows() > 0))// Insulin tab is also there with rows
			{
				result = true;
			}
			wb.close();

		} 
		catch(Exception ioe) 
		{
			m_Logger.log(Level.SEVERE, "isDiasend: IOException closing file. File " + fileName + " Error " + ioe.getMessage());

			ioe.printStackTrace();
		}

		return result;
	}
}