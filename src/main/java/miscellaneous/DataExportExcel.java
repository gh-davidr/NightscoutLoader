package miscellaneous;

import java.io.FileNotFoundException;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
//import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddress;

import control.MyLogger;
import control.PrefsNightScoutLoader;
import control.Version;
import entity.DBResult;
import utils.CommonUtils;

import org.apache.poi.hssf.util.HSSFColor;


// Newer ones (14 Sep 2016)
//import org.apache.poi.ss.usermodel.CellStyle;
//import org.apache.poi.ss.usermodel.Font;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.ss.usermodel.IndexedColors;


import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class DataExportExcel 
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	// Idea from:
	// https://gugiaji.wordpress.com/2013/12/24/exporting-java-defaulttablemodel-to-excel-example/

	private String[]                  m_Settings_ColNames    = {"Parameter", "Value", "Notes" };

	
	public DataExportExcel()
	{
	}

	public void exportToExcel(DefaultTableModel dtm, String filename) 
			throws FileNotFoundException, IOException
	{
		HSSFWorkbook wb = new HSSFWorkbook();

		writeTreatmentDataToExcel(dtm, wb);
		writeSettingsToExcel(wb);

		FileOutputStream out = new FileOutputStream(filename);
		wb.write(out);
		out.close();
		wb.close();
	}
	
	protected void writeTreatmentDataToExcel(DefaultTableModel dtm, HSSFWorkbook wb)
	{
		Sheet sheet = wb.createSheet("Nightscout Loader");
		sheet.createFreezePane(0,1);
		String[] columNames = DBResult.getColNames();

		writeColumnHeaderRow(wb, sheet, columNames);
		Row row = null;
		Cell cell = null;
				
		for (int i=0;i<dtm.getRowCount();i++) 
		{
			// Row always one more since we add the title
			row = sheet.createRow(i+1);			
			for (int j=0;j<dtm.getColumnCount();j++) 
			{
				cell = row.createCell(j);
				cell.setCellValue((String) dtm.getValueAt(i, j));
			}
		}
		
		autoSizeColumns(sheet, columNames);
	}
	
	
	protected void writeSettingsToExcel(HSSFWorkbook wb)
	{
		Sheet sheet = wb.createSheet("Settings");
		sheet.createFreezePane(0,1);

		writeColumnHeaderRow(wb, sheet, m_Settings_ColNames);

		// Now just add the rows one at a time...

		//	 m_Parameter_ColNames  = {"Parameter", "Value", "Notes" };
		int rowNum = 1;

		// Add details on the version of application being used..
		Date now = new Date();
		String nowStr = new String("");
		final String nowStrFrmt = new String("dd-MM-yyyy HH:mm:ss");
		try 
		{
			nowStr = CommonUtils.convertDateString(now, nowStrFrmt);
		} 
		catch (ParseException e) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+"> writeSettingsToExcel " + ". Unable to convert current time for output");
		}

		rowNum += addParameterValue("Nightscout Loader Version Number", Version.getInstance().getM_Version(), "Author: " + Version.getInstance().getM_Author(), sheet, rowNum);
		rowNum += addParameterValue("Contact Details", Version.getInstance().getM_ContactUs(), "", sheet, rowNum);
		rowNum += addParameterValue("Date / Time of extract", nowStr, "Date and Time the extract was actually run", sheet, rowNum);

		rowNum += addParameterValue("", "", "", sheet, rowNum);		

		rowNum += addParameterValue("Roche SQL Server Host", PrefsNightScoutLoader.getInstance().getM_SQLDBServerHost(), "Server host hosting Roche Combo SQL Server database", sheet, rowNum);
		rowNum += addParameterValue("Roche SQL Server Instance", PrefsNightScoutLoader.getInstance().getM_SQLDBServerInstance(), "Server instance of Roche Combo SQL Server database", sheet, rowNum);
		rowNum += addParameterValue("Roche SQL Server DBName", PrefsNightScoutLoader.getInstance().getM_SQLDBName(), "Database name of Roche Combo SQL Server database", sheet, rowNum);
		rowNum += addParameterValue("Days To Load", PrefsNightScoutLoader.getInstance().getM_DaysToLoad(), "For Roche Meter - default number of days back to query", sheet, rowNum);
		rowNum += addParameterValue("Roche SQL File", PrefsNightScoutLoader.getInstance().getM_SQLFile(), "File with SQL commands that can read Roche Combo SQL Server database", sheet, rowNum);
		rowNum += addParameterValue("MongoDB Server Host", PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer(), "Nightscout MongoDB Server Host", sheet, rowNum);
		rowNum += addParameterValue("BG UNITS", PrefsNightScoutLoader.getInstance().getM_BGUnits() == 0 ? "mmol/L" : "mg/dL", "KEY parameter in determining how your results are interpreted.  Please set accordingly!", sheet, rowNum);
		rowNum += addParameterValue("MongoDB Database", PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB(), "Nightscout MongoDB Database Name", sheet, rowNum);
		rowNum += addParameterValue("MongoDB Meter Collection", PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection(), "Nightscout MongoDB Collection Name", sheet, rowNum);
		rowNum += addParameterValue("Advanced Settings", PrefsNightScoutLoader.getInstance().isM_AdvancedSettings() ? "True" : "False", "Advanced Settings in the Settings tab.  If set to false, then all parameters are either default or unchanged from before", sheet, rowNum);

		if (PrefsNightScoutLoader.getInstance().isM_AdvancedSettings() == true)
		{
			rowNum += addParameterValue("", "", "", sheet, rowNum);		
			rowNum += addParameterValue("** Advanced Features **", "", "The next block are the advanced features on Settings panel", sheet, rowNum);		
			rowNum += addParameterValue("Timezone", PrefsNightScoutLoader.getInstance().getM_Timezone(), "Application knows which timezone it runs from.  This parameter can force it to think it is somewhere else.", sheet, rowNum);
			rowNum += addParameterValue("Input Date Format", PrefsNightScoutLoader.getInstance().getM_InputDateFormat(), "BEWARE anything other than Default - Setting incorrectly can lead to unexpected behaviour.  Allows customizations for input files where day/month are reversed.", sheet, rowNum);

			rowNum += addParameterValue("Use Mongo DB for Roche Results (Development only)", PrefsNightScoutLoader.getInstance().isM_UseMongoForRocheResults() ? "True" : "False", "Allows access to SQL Server data via Mongo during development on laptop", sheet, rowNum);
			rowNum += addParameterValue("MongoDB Meter Server Host (Development only)", PrefsNightScoutLoader.getInstance().getM_MongoMeterServer(), "Nightscout MongoDB Server Host used to hold SQL Server data during development", sheet, rowNum);
			rowNum += addParameterValue("MongoDB Meter Collection (Development only)", PrefsNightScoutLoader.getInstance().getM_MongoMeterCollection(), "Name of MongoDB collection used to hold SQL Server data during development", sheet, rowNum);
			rowNum += addParameterValue("Log Level", PrefsNightScoutLoader.getInstance().getM_LogLevel(), "Log Level for application", sheet, rowNum);
			rowNum += addParameterValue("Log File", PrefsNightScoutLoader.getInstance().getM_LogFile(), "Log File for application", sheet, rowNum);
			rowNum += addParameterValue("Max minutes between same meal event", PrefsNightScoutLoader.getInstance().getM_MaxMinsBetweenSameMealEvent(), "Results for BG, Insulin and Carbs must be within this time interval to be grouped together", sheet, rowNum);
			rowNum += addParameterValue("Max minutes between same correction event", PrefsNightScoutLoader.getInstance().getM_MaxMinsBetweenSameCorrectionEvent(), "Results for BG and Insulin must be within this time interval to be grouped together", sheet, rowNum);
			rowNum += addParameterValue("Proxmity Minutes", PrefsNightScoutLoader.getInstance().getM_ProximityMinutes(), "An existing Care Portal entry and a reading from meter/pump of same type within this time frame are considered proximity.  Such meter entries are identified and can be deleted", sheet, rowNum);
		}

		rowNum += addParameterValue("", "", "", sheet, rowNum);		
		rowNum += addParameterValue("** Last Files Used **", "", "The next block are internal references to last various files used ", sheet, rowNum);		
		rowNum += addParameterValue("Last Selected Meter", PrefsNightScoutLoader.getInstance().getM_SelectedMeter(), "Last Meter selected for data upload", sheet, rowNum);
		rowNum += addParameterValue("Last Medtronic File", PrefsNightScoutLoader.getInstance().getM_MedtronicMeterPumpResultFilePath(), "(If used) Path to last file uploaded with Medtronic data", sheet, rowNum);
		rowNum += addParameterValue("Last Diasend File", PrefsNightScoutLoader.getInstance().getM_DiasendMeterPumpResultFilePath(), "(If used) Path to last file uploaded with Diasend data", sheet, rowNum);
		rowNum += addParameterValue("Last Roche SQL Export File", PrefsNightScoutLoader.getInstance().getM_RocheExtractMeterPumpResultFilePath(), "(If used) Path to last file uploaded with Roche SQL Extract data", sheet, rowNum);
		rowNum += addParameterValue("Last Tandem File", PrefsNightScoutLoader.getInstance().getM_TandemMeterPumpResultFilePath(), "(If used) Path to last file uploaded with Tandem data", sheet, rowNum);
		rowNum += addParameterValue("Last Export File", PrefsNightScoutLoader.getInstance().getM_ExportFilePath(), "(If used) Path to last Export of results", sheet, rowNum);
		rowNum += addParameterValue("Last Care Portal Download File", PrefsNightScoutLoader.getInstance().getM_DownloadTreatmentFilePath(), "(If used) Path to last JSON download of Care Portal Treatment data", sheet, rowNum);
		rowNum += addParameterValue("Last CGMS Download File", PrefsNightScoutLoader.getInstance().getM_DownloadSensorFilePath(), "(If used) Path to last JSON download of CGMS Sensor data", sheet, rowNum);
		rowNum += addParameterValue("Last Analysis File", PrefsNightScoutLoader.getInstance().getM_AnalysisFilePath(), "Path to last used Analysis Results File.  (Probably this file!)", sheet, rowNum);

		rowNum += addParameterValue("", "", "", sheet, rowNum);		
		rowNum += addParameterValue("** Adhoc internal values **", "", "The next block are internal references to last various parameters not exposed on Settings panel", sheet, rowNum);		
		rowNum += addParameterValue("MongoDB Sensor Collection", PrefsNightScoutLoader.getInstance().getM_NightscoutSensorMongoCollection(), "Not exposed.  Internally used to identify name of collection for sensor heartbeats", sheet, rowNum);
		rowNum += addParameterValue("MongoDB Database (Development only)", PrefsNightScoutLoader.getInstance().getM_MongoMeterDB(), "Nightscout MongoDB Database Name used to hold SQL Server data during development", sheet, rowNum);
		rowNum += addParameterValue("MongoDB Audit Collection", PrefsNightScoutLoader.getInstance().getM_NightscoutAuditCollection(), "Not exposed.  Internally used to identify name of new collection for Nightscout Loader historic loads", sheet, rowNum);
		rowNum += addParameterValue("Infer Temp Basals", PrefsNightScoutLoader.getInstance().isM_InferTempBasals() ? "True" : "False", "Diasend & now Tandem temp basals have to be inferred so there's a risk they could be wrong..  This disables until I can persuade Diased/Tandem to improve their exports", sheet, rowNum);
		rowNum += addParameterValue("Show All results in Audit History Window", PrefsNightScoutLoader.getInstance().isM_AuditLogAllShown() ? "True" : "False", "The simple Audit view hides all activity.  This flag controls this", sheet, rowNum);

		autoSizeColumns(sheet, m_Settings_ColNames);
	}
	
	// Used by Analyzer
	protected int addParameterValue(String parName, Double parValue, String notes, Sheet sheet, int rowNum)
	{
		int result = 0;

		Row row = null;
		Cell cell = null;

		// Row always one more since we add the title
		row = sheet.createRow(result + rowNum);

		int j = 0;
		
		Font defaultFont= sheet.getWorkbook().createFont();
		defaultFont.setFontHeightInPoints((short)10);
		defaultFont.setFontName("Arial");
		defaultFont.setColor(IndexedColors.BLACK.getIndex());
		defaultFont.setBold(true);
		defaultFont.setItalic(true);

		// All Good Colouring / Font
		CellStyle defaultStyle = sheet.getWorkbook().createCellStyle();
		defaultStyle.setAlignment(CellStyle.ALIGN_RIGHT);
		defaultStyle.setFont(defaultFont);

		
		// 	 m_Highs_Lows_ColNames = {"Date", "Day Name", "TimeSlot", "Type", "Relevance", "BG", "Time"};

		cell = row.createCell(j++);
		cell.setCellStyle(defaultStyle);
		cell.setCellValue(parName);
		//		sheet.autoSizeColumn(j++); // Set auto adjust on column widths
		cell = row.createCell(j++);
		cell.setCellValue(parValue);
		//		sheet.autoSizeColumn(j++); // Set auto adjust on column widths
		cell = row.createCell(j++);
		cell.setCellValue(notes);
		//		sheet.autoSizeColumn(j++); // Set auto adjust on column widths

		result++;

		return result;
	}

	protected int addParameterValue(String parName, Integer parValue, String notes, Sheet sheet, int rowNum)
	{
		int result = 0;

		Row row = null;
		Cell cell = null;

		// Row always one more since we add the title
		row = sheet.createRow(result + rowNum);

		int j = 0;

		Font defaultFont= sheet.getWorkbook().createFont();
		defaultFont.setFontHeightInPoints((short)10);
		defaultFont.setFontName("Arial");
		defaultFont.setColor(IndexedColors.BLACK.getIndex());
		defaultFont.setBold(true);
		defaultFont.setItalic(true);

		// All Good Colouring / Font
		CellStyle defaultStyle = sheet.getWorkbook().createCellStyle();
		defaultStyle.setAlignment(CellStyle.ALIGN_RIGHT);
		defaultStyle.setFont(defaultFont);

		// 	 m_Highs_Lows_ColNames = {"Date", "Day Name", "TimeSlot", "Type", "Relevance", "BG", "Time"};

		cell = row.createCell(j++);
		cell.setCellStyle(defaultStyle);
		cell.setCellValue(parName);
		//		sheet.autoSizeColumn(j++); // Set auto adjust on column widths
		cell = row.createCell(j++);
		cell.setCellValue(parValue);
		//		sheet.autoSizeColumn(j++); // Set auto adjust on column widths
		cell = row.createCell(j++);
		cell.setCellValue(notes);
		//		sheet.autoSizeColumn(j++); // Set auto adjust on column widths

		result++;

		return result;
	}

	protected int addParameterValue(String parName, String parValue, String notes, Sheet sheet, int rowNum)
	{
		int result = 0;

		Row row = null;
		Cell cell = null;

		// Row always one more since we add the title
		row = sheet.createRow(result + rowNum);

		int j = 0;

		Font defaultFont= sheet.getWorkbook().createFont();
		defaultFont.setFontHeightInPoints((short)10);
		defaultFont.setFontName("Arial");
		defaultFont.setColor(IndexedColors.BLACK.getIndex());
		defaultFont.setBold(true);
		defaultFont.setItalic(true);

		// All Good Colouring / Font
		CellStyle defaultStyle = sheet.getWorkbook().createCellStyle();
		defaultStyle.setAlignment(CellStyle.ALIGN_RIGHT);
		defaultStyle.setFont(defaultFont);

		// 	 m_Highs_Lows_ColNames = {"Date", "Day Name", "TimeSlot", "Type", "Relevance", "BG", "Time"};

		cell = row.createCell(j++);
		cell.setCellStyle(defaultStyle);
		cell.setCellValue(parName);
		//		sheet.autoSizeColumn(j++); // Set auto adjust on column widths
		cell = row.createCell(j++);
		cell.setCellValue(parValue);
		//		sheet.autoSizeColumn(j++); // Set auto adjust on column widths
		cell = row.createCell(j++);
		cell.setCellValue(notes);
		//		sheet.autoSizeColumn(j++); // Set auto adjust on column widths

		result++;

		return result;
	}

	
	protected int addParameterValue(String parName, String parValue, String notes, Sheet sheet, int rowNum,
			HSSFCellStyle style)
	{
		int result = 0;

		Row row = null;
		Cell cell = null;

		// Row always one more since we add the title
		row = sheet.createRow(result + rowNum);

		int j = 0;

		// 	 m_Highs_Lows_ColNames = {"Date", "Day Name", "TimeSlot", "Type", "Relevance", "BG", "Time"};

		cell = row.createCell(j++);
		cell.setCellValue(parName);
		cell.setCellStyle(style);

		//		sheet.autoSizeColumn(j++); // Set auto adjust on column widths
		cell = row.createCell(j++);
		cell.setCellValue(parValue);
		cell.setCellStyle(style);

		//		sheet.autoSizeColumn(j++); // Set auto adjust on column widths
		cell = row.createCell(j++);
		cell.setCellValue(notes);
		//		sheet.autoSizeColumn(j++); // Set auto adjust on column widths

		result++;

		return result;
	}

	protected void autoSizeColumns(Sheet sheet, String[] colNames)
	{
		for (int c=0; c < colNames.length; c++) 
		{
			sheet.autoSizeColumn(c); // Set auto adjust on column widths
		}

		if (sheet.getLastRowNum() > 0)
		{
			String start = "A1";
			String end   = getExcelColRefFromNumber(colNames.length) + sheet.getLastRowNum();
//			sheet.setAutoFilter(CellRangeAddress.valueOf("a1:" + getExcelColRefFromNumber(colNames.length) + sheet.getLastRowNum()));
			sheet.setAutoFilter(CellRangeAddress.valueOf(start + ":" + end));
			
			// 15 Sep 2016
			// Looked into why above is not consistent.
			// For example on most recent worksheet (compare full history) filter is always first 3 columns
			// Even when using function 2 below and hardcoding col counts
			// Could be related to use of HSSF perhaps and ought to switch to newer library...
		}
		else
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+"> autoSizeColumns " + ".  Sheet " + sheet.getSheetName() + " has " +
					sheet.getLastRowNum() + " as last row.");
		}
	}

//	protected void autoSizeColumns2(HSSFSheet sheet, String[] colNames)
//	{
//		for (int c=0; c < colNames.length; c++) 
//		{
//			sheet.autoSizeColumn(c); // Set auto adjust on column widths
//		}
//
//		if (sheet.getLastRowNum() > 0)
//		{
//			String start = "A1";
//			String end   = getExcelColRefFromNumber(colNames.length) + sheet.getLastRowNum();
//			//CellRangeAddress cra = new CellRangeAddress(1,sheet.getLastRowNum(),1,colNames.length);
//			CellRangeAddress cra = new CellRangeAddress(1,sheet.getLastRowNum(),1,6);
//			
////			sheet.setAutoFilter(CellRangeAddress.valueOf("a1:" + getExcelColRefFromNumber(colNames.length) + sheet.getLastRowNum()));
////			sheet.setAutoFilter(CellRangeAddress.valueOf(start + ":" + end));
//			sheet.setAutoFilter(cra);
//		}
//		else
//		{
//			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+"> autoSizeColumns " + ".  Sheet " + sheet.getSheetName() + " has " +
//					sheet.getLastRowNum() + " as last row.");
//		}
//	}
	
	protected void writeColumnHeaderRow(HSSFWorkbook wb, Sheet sheet, String[] colNames)
	{
		sheet.createFreezePane(0,1);

		Row row = null;
		Cell cell = null;

		// Create a new font and alter it.
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short)12);
		font.setFontName("Arial");
		font.setBold(true);
		font.setColor(HSSFColor.WHITE.index);

		// Fonts are set into a style so create a new one to use.
		CellStyle style = wb.createCellStyle();
		style.setFillForegroundColor(HSSFColor.BLUE.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setFont(font);

		row = sheet.createRow(0);
		for (int c=0; c < colNames.length; c++) 
		{
			cell = row.createCell(c);
			cell.setCellValue(colNames[c]);
			cell.setCellStyle(style);
			//			sheet.autoSizeColumn(c); // Set auto adjust on column widths
		}
	}

	protected String getExcelColRefFromNumber(int columnNumber)
	{
		int dividend = columnNumber;
		String columnName = new String();
		int modulo;

		while (dividend > 0)
		{
			modulo = (dividend - 1) % 26;
			columnName = (char)(65 + modulo) + columnName;
			dividend = (int)((dividend - modulo) / 26);
		} 

		return columnName;
	}

}
