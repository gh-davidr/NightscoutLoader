package loader;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import control.MyLogger;
import control.PrefsNightScoutLoader;
import entity.DBResult;
import entity.DBResultDiasend;
import entity.DBResultPumpSettingBasal;
import entity.DBResultPumpSettingCarbRatio;
import entity.DBResultPumpSettingISF;
import entity.DBResultEntryDiasend;
import utils.CommonUtils;

// Diasend knows how to read the Excel file generated by Diasend Corp

//public class DataLoadDiasend extends DataLoadBase 
public class DataLoadDiasend extends DataLoadFile
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	private String m_initFilename; // File with Diasend results in it.
	private String m_dateFormatFound;

	final static String m_GlucoseTabName = new String("Name and glucose");
	final static String m_InsulinTabName = new String("Insulin use and carbs");
	final static String m_SettingsTabName = new String("Insulin pump settings");
	final static String m_CGMTabName = new String("CGM");

	/*
	 * final static int m_GlucoseTab = 0; // Excel tab position with glucose data
	 * final static int m_InsulinTab = 0; // Excel tab position with insulin & use
	 * data final static int m_SettingsTab = 0;
	 */
	final static int m_GlucoseRowDateRange = 4;
	final static int m_GlucoseRowDataHeaders = 5;
	final static int m_GlucoseRowDataStart = 6;

	final static int m_InsulinRowDataHeaders = 1;
	final static int m_InsulinRowDataStart = 2;

	final static int m_CGMRowDataHeaders = 5;
	final static int m_CGMRowDataStart = 6;

	final static String m_SettingsBasalStartString = "Basal profiles";
	final static String m_SettingsIntervalString = "Interval";
	final static String m_SettingsActiveBasalString = "Active basal program";
	final static String m_SettingsBGUnitString = "BG unit";
	final static String m_SettingsISFStartString = "ISF programs";
	final static String m_SettingsCarbRatioStartString = "I:C ratio settings";

	POIFSFileSystem m_ExcelFileSystem = null;
	HSSFWorkbook m_ExcelWorkBook = null;

	// Loaded from raw device such as meter, pump DB or file
	protected ArrayList<DBResultPumpSettingBasal> m_BasalSettings;
	protected ArrayList<DBResultPumpSettingBasal> m_BasalSettings1;
	protected ArrayList<DBResultPumpSettingBasal> m_BasalSettings2;
	protected ArrayList<DBResultPumpSettingBasal> m_BasalSettings3;
	protected ArrayList<DBResultPumpSettingBasal> m_BasalSettings4;
	protected ArrayList<DBResultPumpSettingBasal> m_BasalSettings5;
	protected ArrayList<DBResultPumpSettingBasal> m_BasalSettings6;
	protected ArrayList<DBResultPumpSettingBasal> m_BasalSettings7;
	protected String m_ActiveBasal;
	protected ArrayList<DBResultPumpSettingISF> m_ISFSettings;
	protected ArrayList<DBResultPumpSettingCarbRatio> m_CarbRatioSettings;

	protected Date m_StartDate = new Date(0);
	protected Date m_EndDate = new Date(0);

	@Override
	protected String getDevice() {
		return "Diasend";
	}

	public void loadDBResults(String fileName)
			throws UnknownHostException, SQLException, ClassNotFoundException, IOException {
		m_BasalSettings = null;

		m_BasalSettings1 = null;
		m_BasalSettings2 = null;
		m_BasalSettings3 = null;
		m_BasalSettings4 = null;
		m_BasalSettings5 = null;
		m_BasalSettings6 = null;
		m_BasalSettings7 = null;

		m_ActiveBasal = null;

		m_ISFSettings = null;

		initialize(fileName);
		loadDBResults();
	}

	public void loadPumpSettings(String fileName)
			throws UnknownHostException, SQLException, ClassNotFoundException, IOException {
		m_BasalSettings = null;

		m_BasalSettings1 = null;
		m_BasalSettings2 = null;
		m_BasalSettings3 = null;
		m_BasalSettings4 = null;
		m_BasalSettings5 = null;
		m_BasalSettings6 = null;
		m_BasalSettings7 = null;

		m_ActiveBasal = null;
		m_ISFSettings = null;

		initialize(fileName);
		loadInsulinPumpSettings(); // Load Settings first

		// Now close the workbook
		m_ExcelWorkBook.close();
	}

	@Override
	public void loadDBResults() throws UnknownHostException, SQLException, ClassNotFoundException, IOException {
		// Expect initialize to have been called first

		loadInsulinPumpSettings(); // Load Settings first
		loadDBResultsFromGlucoseTab();
		loadDBResultsFromInsulinTab();
		loadDBResultEntriesFromCGMTab(); // Always load this even if not being sync'd

		sortDBResults();
		sortDBResultEntries();

		inferTrendsFromCGMResultEntries();

		// For debug, drop a list of all results so far
		m_Logger.log(Level.FINE,
				"<" + this.getClass().getName() + ">" + "Summary of Diasend Results BEFORE load & Sort");
		for (DBResult res : rawResultsFromDB) {
			m_Logger.log(Level.FINE, "<" + this.getClass().getName() + ">" + "  " + res.rawToString());
		}

		// For debug, drop a list of all results so far
		m_Logger.log(Level.FINE,
				"<" + this.getClass().getName() + ">" + "Summary of Diasend Results AFTER load & Sort");
		for (DBResult res : rawResultsFromDB) {
			m_Logger.log(Level.FINE, "<" + this.getClass().getName() + ">" + "  " + res.rawToString());
		}

		locateTempBasals();

		// For debug, drop a list of all results so far
		m_Logger.log(Level.FINE,
				"<" + this.getClass().getName() + ">" + "Summary of Diasend Results AFTER Locating Temp Basals");
		for (DBResult res : rawResultsFromDB) {
			m_Logger.log(Level.FINE, "<" + this.getClass().getName() + ">" + "  " + res.rawToString());
		}

		convertDBResultsToTreatments();

		// For debug, drop a list of all results so far
		m_Logger.log(Level.FINE, "<" + this.getClass().getName() + ">" + "Summary of Diasend Results AFTER conversion");
		for (DBResult res : resultTreatments) {
			m_Logger.log(Level.FINE, "<" + this.getClass().getName() + ">" + "  " + res.toString());
		}

		// Now close the workbook
		m_ExcelWorkBook.close();
	}

	// New initialize routine
	public void initialize(String filename) {
		m_initFilename = filename;

		m_dateFormatFound = scanFileAndInferDateFormat(filename);
		String currentDateFormat = PrefsNightScoutLoader.getInstance().getM_DiasendDateFormat();
		if (!currentDateFormat.equals(m_dateFormatFound)) {
			m_Logger.log(Level.INFO, "Found a different date format to last Diasend file");
			PrefsNightScoutLoader.getInstance().setM_DiasendDateFormat(m_dateFormatFound);
		}

		try {
			m_ExcelFileSystem = new POIFSFileSystem(new FileInputStream(m_initFilename));
			m_ExcelWorkBook = new HSSFWorkbook(m_ExcelFileSystem);
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
		// Also clear out any stored results
		clearLists();
	}

	protected HSSFSheet getNamedWorksheet(String workSheetName) {
		HSSFSheet result = null;

		int numSheets = m_ExcelWorkBook.getNumberOfSheets();
		HSSFSheet sheet = null;

		// Doesn't appear to work :-(
		// result = m_ExcelWorkBook.getSheet(workSheetName);

		for (int i = 0; i < numSheets && result == null; i++) {
			sheet = m_ExcelWorkBook.getSheetAt(i);
			if (workSheetName.equals(sheet.getSheetName())) {
				result = sheet;
			}
		}

		return result;
	}

	enum BasalRateLineGroup {
		Outside,

		Basals_Start, Program_Name, Interval, Rates, FirstGap, Total, SecondGap
	}

	private ArrayList<DBResultPumpSettingBasal> loadBasalRate(int programNum, HSSFSheet sheet) {
		ArrayList<DBResultPumpSettingBasal> result = null;

		try {
			if (sheet != null) {
				HSSFRow row;

				int rows; // No of rows
				// rows = sheet.getPhysicalNumberOfRows();
				rows = sheet.getLastRowNum();

				int cols = 0; // No of columns
				int tmp = 0;

				// Interested in loading the basal profiles.
				// At this stage, not sure how data looks when device has multiple basal
				// profiles setup
				// Will need to see

				// This trick ensures that we get the data properly even if it doesn't start
				// from first few rows
				for (int i = 0; i < 10 || i < rows; i++) {
					row = sheet.getRow(i);
					if (row != null) {
						tmp = sheet.getRow(i).getPhysicalNumberOfCells();
						if (tmp > cols)
							cols = tmp;
					}
				}

				String profileName = new String("Program: " + programNum);

				boolean foundProfileName = false;
				boolean inProfileSection = false;
				boolean completedProfileSection = false;

				for (int r = 0; r <= rows && !completedProfileSection; r++) {
					row = sheet.getRow(r);

					if (row == null && inProfileSection) {
						completedProfileSection = true;
					} else if (row != null) {
						String cell1 = DBResultPumpSettingBasal.getCellAsString(row, 0);

						if (cell1.equals(profileName)) {
							foundProfileName = true;
							result = new ArrayList<DBResultPumpSettingBasal>();
						} else if (foundProfileName && cell1.equals(m_SettingsIntervalString)) {
							inProfileSection = true;
						} else if (foundProfileName && inProfileSection) {
							DBResultPumpSettingBasal basal = new DBResultPumpSettingBasal(profileName, row);
							result.add(basal);
						}
					}
				}
			}
		} catch (Exception ioe) {
			m_Logger.log(Level.SEVERE, "<" + this.getClass().getName() + ">" + " Exception loading Basal Rate "
					+ programNum + " " + ioe.getMessage());
		}

		return result;
	}

	private ArrayList<DBResultPumpSettingISF> loadISFSettings(HSSFSheet sheet) {
		ArrayList<DBResultPumpSettingISF> result = null;

		try {
			if (sheet != null) {
				HSSFRow row;

				int rows; // No of rows
				// rows = sheet.getPhysicalNumberOfRows();
				rows = sheet.getLastRowNum();

				int cols = 0; // No of columns
				int tmp = 0;

				// Interested in loading the ISF profiles.
				// At this stage, not sure how data looks when device has multiple ISF profiles
				// setup
				// Will need to see

				// This trick ensures that we get the data properly even if it doesn't start
				// from first few rows
				for (int i = 0; i < 10 || i < rows; i++) {
					row = sheet.getRow(i);
					if (row != null) {
						tmp = sheet.getRow(i).getPhysicalNumberOfCells();
						if (tmp > cols)
							cols = tmp;
					}
				}

				boolean foundISFSection = false;
				boolean inProfileSection = false;
				boolean completedProfileSection = false;

				String bgUnits = new String("mmol/L");

				for (int r = 0; r <= rows && !completedProfileSection; r++) {
					row = sheet.getRow(r);

					if (row == null && inProfileSection) {
						completedProfileSection = true;
					} else if (row != null) {
						String cell1 = DBResultPumpSettingISF.getCellAsString(row, 0);

						if (cell1.equals(m_SettingsBGUnitString)) {
							bgUnits = row.getCell(1).getStringCellValue();
						} else if (cell1.equals(m_SettingsISFStartString)) {
							foundISFSection = true;
							result = new ArrayList<DBResultPumpSettingISF>();
						} else if (foundISFSection && cell1.equals(m_SettingsIntervalString)) {
							inProfileSection = true;
						} else if (foundISFSection && inProfileSection) {
							DBResultPumpSettingISF basal = new DBResultPumpSettingISF(row, bgUnits);
							result.add(basal);
						}
					}
				}
			}
		} catch (Exception ioe) {
			m_Logger.log(Level.SEVERE,
					"<" + this.getClass().getName() + ">" + " Exception loading ISF " + ioe.getMessage());
		}

		return result;
	}

	private ArrayList<DBResultPumpSettingCarbRatio> loadCarbRatioSettings(HSSFSheet sheet) {
		ArrayList<DBResultPumpSettingCarbRatio> result = null;

		try {
			if (sheet != null) {
				HSSFRow row;

				int rows; // No of rows
				// rows = sheet.getPhysicalNumberOfRows();
				rows = sheet.getLastRowNum();

				int cols = 0; // No of columns
				int tmp = 0;

				// Interested in loading the CarbRatio profiles.
				// At this stage, not sure how data looks when device has multiple CarbRatio
				// profiles setup
				// Will need to see

				// This trick ensures that we get the data properly even if it doesn't start
				// from first few rows
				for (int i = 0; i < 10 || i < rows; i++) {
					row = sheet.getRow(i);
					if (row != null) {
						tmp = sheet.getRow(i).getPhysicalNumberOfCells();
						if (tmp > cols)
							cols = tmp;
					}
				}

				boolean foundCarbRatioSection = false;
				boolean inProfileSection = false;
				boolean completedProfileSection = false;

				for (int r = 0; r <= rows && !completedProfileSection; r++) {
					row = sheet.getRow(r);

					if (row == null && inProfileSection) {
						completedProfileSection = true;
					} else if (row != null) {
						String cell1 = DBResultPumpSettingCarbRatio.getCellAsString(row, 0);

						if (cell1.equals(m_SettingsCarbRatioStartString)) {
							foundCarbRatioSection = true;
							result = new ArrayList<DBResultPumpSettingCarbRatio>();
						} else if (foundCarbRatioSection && cell1.equals(m_SettingsIntervalString)) {
							inProfileSection = true;
						} else if (foundCarbRatioSection && inProfileSection) {
							DBResultPumpSettingCarbRatio basal = new DBResultPumpSettingCarbRatio(row);
							result.add(basal);
						}
					}
				}
			}
		} catch (Exception ioe) {
			m_Logger.log(Level.SEVERE,
					"<" + this.getClass().getName() + ">" + " Exception loading CarbRatio " + ioe.getMessage());
		}

		return result;
	}

	private String getActiveBasalRate(HSSFSheet sheet) {
		String result = null;

		try {
			if (sheet != null) {
				HSSFRow row;

				int rows; // No of rows
				// rows = sheet.getPhysicalNumberOfRows();
				rows = sheet.getLastRowNum();

				int cols = 0; // No of columns
				int tmp = 0;

				// Interested in loading the basal profiles.
				// At this stage, not sure how data looks when device has multiple basal
				// profiles setup
				// Will need to see

				// This trick ensures that we get the data properly even if it doesn't start
				// from first few rows
				for (int i = 0; i < 10 || i < rows; i++) {
					row = sheet.getRow(i);
					if (row != null) {
						tmp = sheet.getRow(i).getPhysicalNumberOfCells();
						if (tmp > cols)
							cols = tmp;
					}
				}

				for (int r = 0; r <= rows && result == null; r++) {
					row = sheet.getRow(r);

					if (row != null) {
						String cell1 = DBResultPumpSettingBasal.getCellAsString(row, 0);
						if (cell1.equals(m_SettingsActiveBasalString)) {
							result = "Program: " + DBResultPumpSettingBasal.getCellAsString(row, 1); // Result is next
							// cell
						}
					}
				}
			}
		} catch (Exception ioe) {
			m_Logger.log(Level.SEVERE, "<" + this.getClass().getName() + ">" + " Exception locating active Basal Rate "
					+ ioe.getMessage());
		}

		return result;
	}

	private void loadDBResultEntriesFromCGMTab() {
		int dbgCount = 0;
		HSSFRow row = null;
		boolean headersInitialized = false;
		
		// Reset CGM headers each time we load CGM tab
		DBResultEntryDiasend.resetCGMHeaders();
		
		try {
			HSSFSheet sheet = getNamedWorksheet(m_CGMTabName); // m_ExcelWorkBook.getSheet(m_InsulinTabName);

			int rows = sheet.getLastRowNum();

			for (int r = 0; r < rows; r++) {
				dbgCount++;

				row = sheet.getRow(r);

				if (row != null)
				{
					if (!headersInitialized)
					{
						headersInitialized = DBResultEntryDiasend.initializeCGMHeaders(row);
					}
					else 
					{
						DBResultEntryDiasend res = new DBResultEntryDiasend(row);
						addEntry(res);
						m_Logger.log(Level.FINEST, "<DataLoadDiasend>" + "Result added for " + res.toString());
					}
				}
			}
		} catch (Exception ioe) {
			m_Logger.log(Level.SEVERE,
					"<" + this.getClass().getName() + ">" + " loadDBResultEntriesFromCGMTab Exception loading Insulin tab " + ioe.getMessage()
					+ " DBG Count: " + dbgCount);
		}

		// Having loaded all the raw values from file, now need to traverse the
		// collection
		// and determine other attributes like direction, etc..

	}

	private void loadInsulinPumpSettings() {
		HSSFSheet sheet = getNamedWorksheet(m_SettingsTabName); // m_ExcelWorkBook.getSheet(m_SettingsTabName);

		if (sheet != null) {
			m_ActiveBasal = getActiveBasalRate(sheet);

			m_BasalSettings1 = loadBasalRate(1, sheet);
			m_BasalSettings2 = loadBasalRate(2, sheet);
			m_BasalSettings3 = loadBasalRate(3, sheet);
			m_BasalSettings4 = loadBasalRate(4, sheet);
			m_BasalSettings5 = loadBasalRate(5, sheet);
			m_BasalSettings6 = loadBasalRate(6, sheet);
			m_BasalSettings7 = loadBasalRate(7, sheet);

			if (m_ActiveBasal != null) {
				m_BasalSettings = (m_ActiveBasal.equals("Program: 1")) ? m_BasalSettings1 : m_BasalSettings;
				m_BasalSettings = (m_ActiveBasal.equals("Program: 2")) ? m_BasalSettings2 : m_BasalSettings;
				m_BasalSettings = (m_ActiveBasal.equals("Program: 3")) ? m_BasalSettings3 : m_BasalSettings;
				m_BasalSettings = (m_ActiveBasal.equals("Program: 4")) ? m_BasalSettings4 : m_BasalSettings;
				m_BasalSettings = (m_ActiveBasal.equals("Program: 5")) ? m_BasalSettings5 : m_BasalSettings;
				m_BasalSettings = (m_ActiveBasal.equals("Program: 6")) ? m_BasalSettings6 : m_BasalSettings;
				m_BasalSettings = (m_ActiveBasal.equals("Program: 7")) ? m_BasalSettings7 : m_BasalSettings;
			}

			m_ISFSettings = loadISFSettings(sheet);
			m_CarbRatioSettings = loadCarbRatioSettings(sheet);
		}
	}

	private void loadDBResultsFromGlucoseTab() {
		try {
			HSSFSheet sheet = getNamedWorksheet(m_GlucoseTabName); // m_ExcelWorkBook.getSheet(m_GlucoseTabName);
			HSSFRow row;

			int rows; // No of rows
			// rows = sheet.getPhysicalNumberOfRows();
			rows = sheet.getLastRowNum();

			int cols = 0; // No of columns
			int tmp = 0;

			// This trick ensures that we get the data properly even if it doesn't start
			// from first few rows
			for (int i = 0; i < 10 || i < rows; i++) {
				row = sheet.getRow(i);
				if (row != null) {
					tmp = sheet.getRow(i).getPhysicalNumberOfCells();
					if (tmp > cols)
						cols = tmp;
				}
			}

			for (int r = 0; r <= rows; r++) {
				row = sheet.getRow(r);

				if (r + 1 == m_GlucoseRowDateRange) {
					DBResultDiasend.initializeGlucoseDateRange(row);
				} else if (r + 1 == m_GlucoseRowDataHeaders) {
					DBResultDiasend.initializeGlucoseHeaders(row);
				} else if (r + 1 >= m_GlucoseRowDataStart) {
					DBResultDiasend res = new DBResultDiasend(row, false);
					if (res.isValid()) {
						rawResultsFromDB.add(res);
						m_Logger.log(Level.FINEST,
								"<" + this.getClass().getName() + ">" + "Result added for " + res.toString());
					}

				}
			}
		} catch (Exception ioe) {
			m_Logger.log(Level.SEVERE,
					"<" + this.getClass().getName() + ">" + " Exception loading Glucose tab " + ioe.getMessage());
		}

	}

	private void loadDBResultsFromInsulinTab() {

		int dbgCount = 0;

		try {
			HSSFSheet sheet = getNamedWorksheet(m_InsulinTabName); // m_ExcelWorkBook.getSheet(m_InsulinTabName);
			HSSFRow row;

			// int rows = sheet.getPhysicalNumberOfRows(); // No of rows
			int rows = sheet.getLastRowNum();

			int cols = 0; // No of columns
			int tmp = 0;

			// This trick ensures that we get the data properly even if it doesn't start
			// from first few rows
			for (int i = 0; i < 10 || i < rows; i++) {
				row = sheet.getRow(i);
				if (row != null) {
					tmp = sheet.getRow(i).getPhysicalNumberOfCells();
					if (tmp > cols)
						cols = tmp;
				}
			}

			for (int r = 0; r <= rows; r++) {
				dbgCount++;
				row = sheet.getRow(r);

				if (r + 1 == m_InsulinRowDataHeaders) {
					DBResultDiasend.initializeInsulinHeaders(row);
				} else if (r + 1 >= m_InsulinRowDataStart) {
					DBResultDiasend res = new DBResultDiasend(row, true);
					if (res.isValid()) {
						// Check if this is a Basal rate
						if (res.getM_ResultType().equals("Basal")) {
							/// Do a look up on basal rates from settings
							// If this is a temp basal then we do keep it.
						}
						rawResultsFromDB.add(res);
						m_Logger.log(Level.FINEST,
								"<" + this.getClass().getName() + ">" + "Result added for " + res.toString());
					}

				}
			}
		} catch (Exception ioe) {
			m_Logger.log(Level.SEVERE,
					"<" + this.getClass().getName() + ">" + " loadDBResultsFromInsulinTab Exception loading Insulin tab " + ioe.getMessage()
					+ " DBG Count: " + dbgCount);
		}
	}

	/*
	 * private void sortDBResults() { // Sort the Mongo Results
	 * Collections.sort(rawResultsFromDB, new ResultFromDBComparator(false)); }
	 */
	//	private DBResultDiasendBasalSetting getPrevalentBasalRate(DBResult basalResult)
	//	{
	//		DBResultDiasendBasalSetting result = null;
	//		DBResultDiasendBasalSetting prev   = null;
	//		if (m_BasalSettings != null)
	//		{
	//			for (DBResultDiasendBasalSetting c : m_BasalSettings)
	//			{
	//				try {
	//					if (result == null && CommonUtils.isTimeAfter(c.getM_Time(), basalResult.getM_Time()))
	//					{
	//						result = prev;
	//						break;
	//					}
	//					else
	//					{
	//						prev = c;
	//					}
	//				} 
	//				catch (ParseException e) 
	//				{
	//					m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + 
	//							"getPrevalentBasalRate - Unexpected error identifying basal rate: " + basalResult.toString());
	//				}
	//			}
	//		}
	//
	//		return result;
	//	}

	protected void locateTempBasals() {
		// Iterate over the raw results looking for basal rates that have changed.
		// Assume the list is ordered in time.
		// boolean tempStarted = false;
		if (PrefsNightScoutLoader.getInstance().isM_InferTempBasals()) {
			DBResult tempBasalStart = null;
			DBResult lastHourChange = null;
			//			Double lastHourChangeRate = null;

			for (DBResult res : rawResultsFromDB) {
				// Only do this for Basal Rates
				String resType = res.getM_ResultType();

				if (resType == "Basal") {
					// Different approach.
					// If the basal rate change was on the hour, then assume that it's a basal rate
					// If the basal rate change was off the hour, then assume it's a temp basal.
					Date basalTime = new Date(res.getM_EpochMillies());
					int minutes = CommonUtils.getMinutesFromDate(basalTime);

					if (minutes == 0) {
						lastHourChange = res;
						//						lastHourChangeRate = Double.parseDouble(lastHourChange.getM_Result());
						if (tempBasalStart != null) {
							Date tempBasalEndTime = new Date(tempBasalStart.getM_EpochMillies());
							Double mins = (double) CommonUtils.timeDiffInMinutes(basalTime, tempBasalEndTime);
							if (mins > 0) {
								tempBasalStart.setM_CP_Duration(mins);
								resultTreatments.add(tempBasalStart);
							}
							// tempBasalStart = null;
						}
						tempBasalStart = new DBResult(res, getDevice());
					} else {
						// If there's a last hour change and we're not in the middle of a temp basal,
						// and the last hour change basal rate was not 0, and the rate is not 100% then
						// start one
						if (lastHourChange != null && tempBasalStart == null) {
							m_Logger.log(Level.FINE, "Creating Temp Basal from : " + res.rawToString());
							tempBasalStart = new DBResult(res, getDevice());
						}

						// If there's a last hour change and we're in the middle of a temp basal,
						// Then end one
						else if (lastHourChange != null && tempBasalStart != null) {
							Date tempBasalEndTime = new Date(tempBasalStart.getM_EpochMillies());
							Double mins = (double) CommonUtils.timeDiffInMinutes(basalTime, tempBasalEndTime);
							if (mins > 0) {
								tempBasalStart.setM_CP_Duration(mins);
								resultTreatments.add(tempBasalStart);
							}

							tempBasalStart = new DBResult(res, getDevice());
						}
					}

				}

			}
			if (tempBasalStart != null) {
				m_Logger.log(Level.FINE, "Ignoring last Temp Basal from since duration cannot be calcuated : "
						+ tempBasalStart.rawToString());
			}
		}

		// Now remove all the basal entries from rawResultsFromDB
		Iterator<DBResult> it = rawResultsFromDB.iterator();
		while (it.hasNext()) {
			String resType = it.next().getM_ResultType();
			if (resType == "Basal") {
				it.remove();
				// If you know it's unique, you could `break;` here
			}
		}
	}

	public static boolean isDiasend(String fileName) {
		boolean result = false;

		try {
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(fileName));
			HSSFWorkbook wb = new HSSFWorkbook(fs);

			HSSFSheet glucoseSheet = wb.getSheet(m_GlucoseTabName);
			HSSFSheet insulinSheet = wb.getSheet(m_InsulinTabName);
			HSSFSheet cgmSheet = wb.getSheet(m_CGMTabName);

			if ((glucoseSheet != null && glucoseSheet.getLastRowNum() > 0) // Glucose tab is there with rows
					|| (insulinSheet != null && insulinSheet.getLastRowNum() > 0)// Or Insulin tab is there with rows
					|| (cgmSheet != null && cgmSheet.getLastRowNum() > 0)) // Or CGM tab is there with rows
			{
				result = true;
			}
			wb.close();
		} catch (Exception ioe) {
			m_Logger.log(Level.SEVERE,
					"isDiasend: IOException closing file. File " + fileName + " Error " + ioe.getMessage());

			ioe.printStackTrace();
		}

		return result;
	}

	static public Date parseFileDateTime(String date) {
		Date result = new Date(0);
		// Combined Date Time

		//		final String defSlashFormat = new String("dd/MM/yy HH:mm");  -- Changed with Glooko?
		// In fact, now find that it changes between dd/MM/yyyy and MM/dd/yyyy, so we
		// hold a preference
		// and set the preference during an initial file scan

		final String defSlashFormat = new String(
				PrefsNightScoutLoader.getInstance().getM_DiasendDateFormat() + " HH:mm");

		// 06 Jan 2018
		// David - don;t really want to do this.
		// Instead, allow the infer function to determine what date format probably
		// really is
		// and instead use that rather than this override
		String prefDateFormat = PrefsNightScoutLoader.getInstance().getM_InputDateFormat();
		DateFormat slashformat = new SimpleDateFormat((prefDateFormat.contains("/") ? prefDateFormat : defSlashFormat),
				Locale.ENGLISH);
		// DateFormat slashformat = new SimpleDateFormat("dd/MM/yyyy HH:mm",
		// Locale.ENGLISH);
		try {
			result = slashformat.parse(date);
		} catch (ParseException e) {
			m_Logger.log(Level.SEVERE, "<DataLoadDiasend> " + "parseFileDate - Unexpected error parsing date: " + date);
		}

		return result;
	}

	static public Date parseFileDate(String date) {
		Date result = new Date(0);
		// Combined Date Time
		DateFormat slashformat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

		//		final String defSlashFormat = new String("dd/MM/yy HH:mm");  -- Changed with Glooko?
		// In fact, now find that it changes between dd/MM/yyyy and MM/dd/yyyy, so we
		// hold a preference
		// and set the preference during an initial file scan

		//		final String defSlashFormat = new String(
		//				PrefsNightScoutLoader.getInstance().getM_DiasendDateFormat());

		try {
			result = slashformat.parse(date);
		} catch (ParseException e) {
			m_Logger.log(Level.SEVERE, "<DBResultDiasend>" + "parseDate - Unexpected error parsing date: " + date);
		}

		return result;
	}

	private static boolean isDateValidAndBeforeNow(String dateString, Date now, String dateFormat) {
		boolean result = true;

		try {
			Date d = new Date(0);
			DateFormat df = new SimpleDateFormat(dateFormat);
			df.setLenient(false);
			d = df.parse(dateString);

			// set result based on if before now or after now
			result = d.before(now) ? true : false;
		} catch (ParseException e) {
			result = false;
		}

		return result;
	}

	public static String scanFileAndInferDateFormat(String fileName) {
		// Start off with what was last used
		String result = new String(PrefsNightScoutLoader.getInstance().getM_DiasendDateFormat());

		//		boolean switched = false;

		// Traverse the entire file and check that this date format will work.
		int glucoseTimeIndex = DBResultDiasend.getM_GlucoseTimeIndex();
		int insulinTimeIndex = DBResultDiasend.getM_InsulinTimeIndex();
		int cgmTimeIndex = DBResultEntryDiasend.getM_CGMTimeIndex();

		try {
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(fileName));
			HSSFWorkbook wb = new HSSFWorkbook(fs);

			HSSFSheet glucoseSheet = wb.getSheet(m_GlucoseTabName);
			HSSFSheet insulinSheet = wb.getSheet(m_InsulinTabName);
			HSSFSheet cgmSheet = wb.getSheet(m_CGMTabName);

			String glucoseSheetFormat = scanSheetAndInferDateFormat(glucoseSheet, glucoseTimeIndex);
			String insulinSheetFormat = scanSheetAndInferDateFormat(insulinSheet, insulinTimeIndex);
			String cgmSheetFormat = scanSheetAndInferDateFormat(cgmSheet, cgmTimeIndex);

			// If any of the sheets come back with serious format issues, propagate up
			if (glucoseSheetFormat.isEmpty() || insulinSheetFormat.isEmpty() || cgmSheetFormat.isEmpty()) {
				result = "";
			} else if (!glucoseSheetFormat.equals(result)) {
				result = glucoseSheetFormat;
			} else if (!insulinSheetFormat.equals(result)) {
				result = insulinSheetFormat;
			} else if (!cgmSheetFormat.equals(result)) {
				result = cgmSheetFormat;
			}

			wb.close();
		} catch (Exception ioe) {
			m_Logger.log(Level.SEVERE,
					"isDiasend: IOException closing file. File " + fileName + " Error " + ioe.getMessage());

			ioe.printStackTrace();
		}

		return result;
	}

	public static String scanSheetAndInferDateFormat(HSSFSheet sheet, int timeIndex) {
		// Start off with what was last used
		String result = new String(PrefsNightScoutLoader.getInstance().getM_DiasendDateFormat());
		boolean switched = false;

		// Iterate the Glucose Sheet
		HSSFRow row;
		int rows; // No of rows
		rows = sheet.getLastRowNum();

		boolean valid = true;

		// Generate a date for now and use it to as another check
		// since file dates should not be later than now
		Date now = new Date();

		// This trick ensures that we get the data properly even if it doesn't start
		// from first few rows
		for (int i = 0; (valid == true) && (i < rows); i++) {
			row = sheet.getRow(i);
			if (row != null) {
				String timeStr = CommonUtils.getStringCellValue(row, timeIndex);

				// Only proceed if this looks like a date time
				if (!timeStr.isEmpty() && timeStr.contains("/")) {
					valid = isDateValidAndBeforeNow(timeStr, now, result);
					if (switched == false && valid == false) {
						switched = true;
						if (result.equals("dd/MM/yyyy") || (result.equals("dd/MM/yy"))) {
							result = "MM/dd/yyyy";
						} else {
							result = "dd/MM/yyyy";
						}

						// Try again
						valid = isDateValidAndBeforeNow(timeStr, now, result);
					}
				}
			}
		}

		if (valid == false) {
			// We really have no idea if neither formats work at this stage.
			// Return an empty string to say that file is really invalid
			result = "";
		}

		return result;
	}

	/**
	 * @return the m_BasalSettings
	 */
	public synchronized ArrayList<DBResultPumpSettingBasal> getM_BasalSettings() {
		return m_BasalSettings;
	}

	/**
	 * @return the m_ISFSettings
	 */
	public synchronized ArrayList<DBResultPumpSettingISF> getM_ISFSettings() {
		return m_ISFSettings;
	}

	/**
	 * @return the m_CarbRatioSettings
	 */
	public synchronized ArrayList<DBResultPumpSettingCarbRatio> getM_CarbRatioSettings() {
		return m_CarbRatioSettings;
	}

	/**
	 * @return the m_StartDate
	 */
	public synchronized Date getM_StartDate() {
		return m_StartDate;
	}

	/**
	 * @return the m_EndDate
	 */
	public synchronized Date getM_EndDate() {
		return m_EndDate;
	}
}