<<<<<<< HEAD:src/main/java/loader/DataLoadCSVFile.java
package loader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import control.MyLogger;
import entity.DBResult;
import entity.DBResultEntry;
import entity.DBResultEntryDiasend;

//public abstract class DataLoadCSVFile extends DataLoadBase 
public abstract class DataLoadCSVFile extends DataLoadFile
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	//	private ArrayList <ResultFromDB> resultTreatments;
	private DBResult    m_ReportDateRange;

	// Used for 2 separate calls to load
	// One to initialize other the abstract load
	private String              m_initFilename;

	protected int       m_SkipLines  = 0;
	protected int       m_HeaderLine = 0;

	// Base class that must know how to create a DBResult derived instance
	// with String array
	protected abstract DBResult makeDBResult(String[] res);    

	// Now also attempt to create an entry while reading the file
	// THis is used for LibreView file
	protected DBResultEntry makeDBResultEntry(String[] res)
	{
		return null;
	}
	// Now also attempt to create an entry while reading the file
	// THis is used for LibreView file
	protected void initializeHeaders(String[] res)
	{
		;
	}

	// Allow loaders to reset CGM headers each time a file is loaded
	protected void resetCGMHeaders()
	{
		;
	}

	// Optional alternative for CellNovo - a single line can result in multiple DBResults
	protected ArrayList<DBResult> makeDBResultList(String[] res)
	{
		return null;
	}




	// Optional function to order raw results - necessary for CellNovo
	protected void orderRawResults()
	{

	}


	// Similarly, base class must know what types it creates for logging
	protected abstract String loadStringName();

	// Derived class must know what separates the fields
	protected abstract String getSplitBy();

	public DBResult getReportDateRange()
	{
		return m_ReportDateRange;
	}

	/*	public DataLoadMedtronic()
	{
		resultTreatments = new ArrayList<ResultFromDB>();
	}
	 */
	// New initialize routine
	public void initialize(String filename)
	{
		m_initFilename = new String(filename);

		// Also clear out any stored results
		clearLists();
	}

	// From abstract parent
	public void loadDBResults()  throws UnknownHostException, SQLException, ClassNotFoundException, IOException
	{
		loadDBResults(m_initFilename);
	}

	public void loadDBResults(String fileName) throws  IOException
	{
		loadDBRawResults(fileName);
		try
		{
			orderRawResults();
			sortDBResultEntries();
			convertDBResultsToTreatments();
		}
		catch (Exception e) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "loadDBResults - Unexpected error Converting raw data to Treatments ");
		}
		inferTrendsFromCGMResultEntries();
	}

	protected String getDevice()
	{
		return "Roche";
	}

	public void loadDBRawResults(String fileName) throws  IOException
	{
		BufferedReader br = null;
		String line = "";
		//String cvsSplitBy = ",";
		String cvsSplitBy = getSplitBy();

		try 
		{			
			rawResultsFromDB.clear();
			br = new BufferedReader(new FileReader(fileName));
			int i = 1;
			while ((line = br.readLine()) != null) 
			{
				// Allow derived classes to determine how we split (could be regex)
				// Also, to ensure we get trailing empty strings use 2nd form with large limit val
				String[] rs = line.split(cvsSplitBy, 1000);

				if (i == m_HeaderLine)
				{
					initializeHeaders(rs);
				}

				if (i > m_SkipLines)
				{
					// DBResultMedtronic res = new DBResultMedtronic(rs);
					DBResult res = makeDBResult(rs);
					if (res != null)
					{
						if (res.isValid())
						{
							rawResultsFromDB.add(res);
							m_Logger.log(Level.FINEST, "Result added for " + loadStringName() + " " + res.toString());
						}
						else if (res.isReportRange())
						{
							m_ReportDateRange = res;
						}
					}

					DBResultEntry resEntry = makeDBResultEntry(rs);
					if (resEntry != null)
					{
						if (resEntry.isValid())
						{
							addEntry(resEntry);
							m_Logger.log(Level.FINEST, "ResultEntry added for " + loadStringName() + " " + resEntry.toString());
						}
					}

					ArrayList<DBResult> resList = res == null ? makeDBResultList(rs) : null;
					if (resList != null)
					{
						for (DBResult r : resList)
						{
							if (r.isValid())
							{
								rawResultsFromDB.add(r);
								m_Logger.log(Level.FINEST, "Result added for " + loadStringName() + " " + r.toString());
							}
							else if (r.isReportRange())
							{
								m_ReportDateRange = r;
							}
						}
					}
				}
				
				i++;
			}

		} catch (FileNotFoundException e) {
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DataLoadCSVFile: FileNotFoundException trying to open file " + e.getLocalizedMessage());
		} catch (IOException e) {
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DataLoadCSVFile: IOException trying to open file " + e.getLocalizedMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DataLoadCSVFile: IOException trying to close file " + e.getLocalizedMessage());
				}
			}
		}

	}

	public void loadDBRawResultsFrom(String fileName) throws  IOException
	{
		BufferedReader br = null;
		String line = "";
		//String cvsSplitBy = ",";
		String cvsSplitBy = getSplitBy();

		try 
		{			
			rawResultsFromDB.clear();
			br = new BufferedReader(new FileReader(fileName));
			while ((line = br.readLine()) != null) 
			{
				// use comma as separator
				String[] rs = line.split(cvsSplitBy);

				// DBResultMedtronic res = new DBResultMedtronic(rs);
				DBResult res = makeDBResult(rs);
				if (res.isValid())
				{
					rawResultsFromDB.add(res);
					m_Logger.log(Level.FINEST, "Result added for " + loadStringName() + " " + res.toString());
				}
				else if (res.isReportRange())
				{
					m_ReportDateRange = res;
				}
			}

		} catch (FileNotFoundException e) {
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DataLoadCSVFile: FileNotFoundException trying to open file " + e.getLocalizedMessage());
		} catch (IOException e) {
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DataLoadCSVFile: IOException trying to open file " + e.getLocalizedMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DataLoadCSVFile: IOException trying to close file " + e.getLocalizedMessage());
				}
			}
		}

	}

	protected static Boolean doesLineMatch(String[] line, String[] expectedStrings, String ignoreString) {
		Boolean resultBoolean = false;

		if (line != null && expectedStrings != null && line.length == expectedStrings.length)
		{
			resultBoolean = true;
			for (int i = 0; resultBoolean && i < line.length; i++)
			{
				String a = line[i];
				String b = expectedStrings[i];

				// a == null && b == null : TRUE
				// a != null BB b != null : a==b or ingoreString = (a or b)
				// a or b == null         : FALSE

				resultBoolean = (a == null && b == null);
				resultBoolean = (a != null && b != null) ? a.equals(b) : resultBoolean;
				resultBoolean = (a != null && b != null && (a.equals(a != null && b != null) ) || b.equals(ignoreString)) ? true : resultBoolean;
			}
		}

		return resultBoolean;
	}
	
}
=======
package davidRichardson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DataLoadCSVFile extends DataLoadBase 
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	//	private ArrayList <ResultFromDB> resultTreatments;
	private DBResult    m_ReportDateRange;

	// Used for 2 separate calls to load
	// One to initialize other the abstract load
	private String              m_initFilename;
	
	// Base class that must know how to create a DBResult derived instance
	// with String array
	protected abstract DBResult makeDBResult(String[] res);    

	// Optional alternative for CellNovo - a single line can result in multiple DBResults
	protected ArrayList<DBResult> makeDBResultList(String[] res)
	{
		return null;
	}
	
	// Optional function to order raw results - necessary for CellNovo
	protected void orderRawResults()
	{
		
	}


	// Similarly, base class must know what types it creates for logging
	protected abstract String loadStringName();
	
	// Derived class must know what separates the fields
	protected abstract String getSplitBy();
	
	public DBResult getReportDateRange()
	{
		return m_ReportDateRange;
	}

	/*	public DataLoadMedtronic()
	{
		resultTreatments = new ArrayList<ResultFromDB>();
	}
	 */
	// New initialize routine
	public void initialize(String filename)
	{
		m_initFilename = new String(filename);
		
		// Also clear out any stored results
		clearLists();
	}
	
	// From abstract parent
	public void loadDBResults()  throws UnknownHostException, SQLException, ClassNotFoundException, IOException
	{
		loadDBResults(m_initFilename);
	}

	public void loadDBResults(String fileName) throws  IOException
	{
		loadDBRawResults(fileName);
		try
		{
			orderRawResults();
			convertDBResultsToTreatments();
		}
		catch (Exception e) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "loadDBResults - Unexpected error Converting raw data to Treatments ");
		}
	
	}

	protected String getDevice()
	{
		return "Roche";
	}

	public void loadDBRawResults(String fileName) throws  IOException
	{
		BufferedReader br = null;
		String line = "";
		//String cvsSplitBy = ",";
		String cvsSplitBy = getSplitBy();

		try 
		{			
			rawResultsFromDB.clear();
			br = new BufferedReader(new FileReader(fileName));
			while ((line = br.readLine()) != null) 
			{
				// Allow derived classes to determine how we split (could be regex)
				// Also, to ensure we get trailing empty strings use 2nd form with large limit val
				String[] rs = line.split(cvsSplitBy, 1000);

				// DBResultMedtronic res = new DBResultMedtronic(rs);
				DBResult res = makeDBResult(rs);
				if (res != null)
				{
					if (res.isValid())
					{
						rawResultsFromDB.add(res);
						m_Logger.log(Level.FINEST, "Result added for " + loadStringName() + " " + res.toString());
					}
					else if (res.isReportRange())
					{
						m_ReportDateRange = res;
					}
				}

				ArrayList<DBResult> resList = makeDBResultList(rs);
				if (resList != null)
				{
					for (DBResult r : resList)
					{
						if (r.isValid())
						{
							rawResultsFromDB.add(r);
							m_Logger.log(Level.FINEST, "Result added for " + loadStringName() + " " + r.toString());
						}
						else if (r.isReportRange())
						{
							m_ReportDateRange = r;
						}
					}
				}
			}

		} catch (FileNotFoundException e) {
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DataLoadCSVFile: FileNotFoundException trying to open file " + e.getLocalizedMessage());
		} catch (IOException e) {
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DataLoadCSVFile: IOException trying to open file " + e.getLocalizedMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DataLoadCSVFile: IOException trying to close file " + e.getLocalizedMessage());
				}
			}
		}

	}
	
	public void loadDBRawResultsFrom(String fileName) throws  IOException
	{
		BufferedReader br = null;
		String line = "";
		//String cvsSplitBy = ",";
		String cvsSplitBy = getSplitBy();

		try 
		{			
			rawResultsFromDB.clear();
			br = new BufferedReader(new FileReader(fileName));
			while ((line = br.readLine()) != null) 
			{
				// use comma as separator
				String[] rs = line.split(cvsSplitBy);

				// DBResultMedtronic res = new DBResultMedtronic(rs);
				DBResult res = makeDBResult(rs);
				if (res.isValid())
				{
					rawResultsFromDB.add(res);
	       	    	m_Logger.log(Level.FINEST, "Result added for " + loadStringName() + " " + res.toString());
				}
				else if (res.isReportRange())
				{
					m_ReportDateRange = res;
				}
			}

		} catch (FileNotFoundException e) {
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DataLoadCSVFile: FileNotFoundException trying to open file " + e.getLocalizedMessage());
		} catch (IOException e) {
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DataLoadCSVFile: IOException trying to open file " + e.getLocalizedMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DataLoadCSVFile: IOException trying to close file " + e.getLocalizedMessage());
				}
			}
		}

	}
	
}
>>>>>>> master:src/davidRichardson/DataLoadCSVFile.java
