package davidRichardson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
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
