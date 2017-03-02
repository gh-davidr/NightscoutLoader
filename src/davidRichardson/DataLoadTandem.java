package davidRichardson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataLoadTandem extends DataLoadCSVFile
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());
	private static final String m_TandemSplitBy = ",";

	@Override
	protected DBResult makeDBResult(String[] res) 
	{
		DBResult result = null;
		return result;
	}

	protected DBResult makeDBResult(String[] res, DBResult.ResultType resType) 
	{
		DBResultTandem result = new DBResultTandem(res, resType);
		return result;
	}

	@Override
	protected String loadStringName() 
	{
		return "Tandem";
	}

	@Override
	protected String getSplitBy() 
	{
		return m_TandemSplitBy;
	}

	// we need to override this in tabdem

	@Override
	public void loadDBRawResults(String fileName) throws  IOException
	{
		BufferedReader br = null;
		String line = "";
		String strippedLine = "";
		String nulledLine = "";

		//String cvsSplitBy = ",";
		String cvsSplitBy = getSplitBy();

		// at line 7 we see header for bgs
		int ln = 0;

		try 
		{			
			rawResultsFromDB.clear();
			br = new BufferedReader(new FileReader(fileName));
			while ((line = br.readLine()) != null) 
			{
				ln++;
				
				// Lines from Tandem all have double quotes around each field.
				// Let's strip them
				nulledLine   = line.replaceAll("\"\"","null");
				strippedLine = nulledLine.replaceAll("\"","");
				
				// use comma as separator
				String[] rs = strippedLine.split(cvsSplitBy);

				// Line 7 marks the start of BG results
				if (ln == 7)
				{
					if (rs.length == 7)
					{
						DBResultTandem.setM_WhichSectionOfFile(DBResultTandem.WhichSectionOfFile.Meter_Vals);
					}
				}
				else if (ln > 7)
				{
					// While we are reading BG results
					if (DBResultTandem.getM_WhichSectionOfFile() == DBResultTandem.WhichSectionOfFile.Meter_Vals)
					{
						// DBResultMedtronic res = new DBResultMedtronic(rs);
						DBResult res = makeDBResult(rs, DBResult.ResultType.BG);
						if (res.isValid())
						{
							rawResultsFromDB.add(res);
							m_Logger.log(Level.FINEST, "Result added for " + loadStringName() + " " + res.toString());
						}
						else
						{
							DBResultTandem.setM_WhichSectionOfFile(DBResultTandem.WhichSectionOfFile.Unknown);
						}

					}

					// We've read all the BG results and are now looking for the start of the
					// Basal and Pump data
					else if (DBResultTandem.getM_WhichSectionOfFile() == DBResultTandem.WhichSectionOfFile.Unknown)
					{
						// Basal Rate section is 3 columns wide
						if (rs.length == 3)
						{
							// We've hit a row marking the start of the Pump data 
							if (rs[0].equals("Type"))
							{
								DBResultTandem.setM_WhichSectionOfFile(DBResultTandem.WhichSectionOfFile.Basal_Vals);
							}	
						}
						
						else if (rs.length > 40)
						{
							// We've hit a row marking the start of the Pump data 
							if (rs[0].equals("Type"))
							{
								DBResultTandem.setM_WhichSectionOfFile(DBResultTandem.WhichSectionOfFile.Pump_Vals);
							}	
						}
					}

					else if (DBResultTandem.getM_WhichSectionOfFile() == DBResultTandem.WhichSectionOfFile.Basal_Vals)
					{

						// Load the Carbs data
						DBResult res = makeDBResult(rs, DBResult.ResultType.Basal);
						if (res.isValid())
						{
							rawResultsFromDB.add(res);
							m_Logger.log(Level.FINEST, "Result added for " + loadStringName() + " " + res.toString());
						}
						// Only do this once per record
						else
						{
							DBResultTandem.setM_WhichSectionOfFile(DBResultTandem.WhichSectionOfFile.Unknown);
						}
					}

					
					else if (DBResultTandem.getM_WhichSectionOfFile() == DBResultTandem.WhichSectionOfFile.Pump_Vals)
					{

						// Load the Carbs data
						if (DBResultTandem.getM_WhichSectionOfFile() == DBResultTandem.WhichSectionOfFile.Pump_Vals)
						{

							DBResult res = makeDBResult(rs, DBResult.ResultType.Carbs);
							if (res.isValid())
							{
								rawResultsFromDB.add(res);
								m_Logger.log(Level.FINEST, "Result added for " + loadStringName() + " " + res.toString());
							}
						}

						if (DBResultTandem.getM_WhichSectionOfFile() == DBResultTandem.WhichSectionOfFile.Pump_Vals)
							// Load the Insulin data
						{
							DBResult res = makeDBResult(rs, DBResult.ResultType.Insulin);
							if (res.isValid())
							{
								rawResultsFromDB.add(res);
								m_Logger.log(Level.FINEST, "Result added for " + loadStringName() + " " + res.toString());
							}
							// Only do this once per record
							else
							{
								DBResultTandem.setM_WhichSectionOfFile(DBResultTandem.WhichSectionOfFile.Unknown);
							}
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
		
		// Sort the results prior to merging them
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

	}


	public static boolean isTandem(String fileName)
	{
		boolean result = false;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		// Expected Format
		boolean ln1Col1Tandem      = false;
		boolean ln2Col1Timeline    = false;
		boolean ln3Col1Name        = false;
		boolean ln4Col1DOB         = false;
		boolean ln5Col1ReportGen   = false;

		int ln = 0;
		int maxLines = 5;   // check first 5 lines

		try 
		{
			br = new BufferedReader(new FileReader(fileName));
			while ((ln <= maxLines) && (line = br.readLine()) != null) 
			{
				ln++;
				// use comma as separator
				String[] rs = line.split(cvsSplitBy);

				if (ln == 1)
					ln1Col1Tandem  = (rs.length > 0 && rs[0].equals("Tandem Diabetes Care Inc.")) ? true : false;
				if (ln == 2)
					ln2Col1Timeline  = (rs.length > 0 && rs[0].equals("t:connect Therapy Timeline Data Export")) ? true : false;
				if (ln == 3)
					ln3Col1Name         = (rs.length > 0 && rs[0].equals("Patient Name"))         ? true : false;
				if (ln == 4)
					ln4Col1DOB    = (rs.length > 0 && rs[0].equals("Patient DOB")) ? true : false;
				if (ln == 5)
					ln5Col1ReportGen       = (rs.length > 0 && rs[0].equals("Report Generated On"))  ? true : false;
			}

			result = (ln1Col1Tandem == true && ln2Col1Timeline == true && ln3Col1Name == true && 
					ln4Col1DOB == true && ln5Col1ReportGen == true ) ? true : false;

		} 
		catch (FileNotFoundException e) 
		{
			m_Logger.log(Level.SEVERE, "<DataLoadTandem>" + "isTandem: FileNotFoundException. File " + fileName + " Error " + e.getMessage());

			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			m_Logger.log(Level.SEVERE, "<DataLoadTandem>" + "isTandem: IOException. File " + fileName + " Error " + e.getMessage());
			e.printStackTrace();
		} 
		finally 
		{
			if (br != null) 
			{
				try 
				{
					br.close();
				} 
				catch (IOException e) 
				{
					m_Logger.log(Level.SEVERE, "<DataLoadTandem>" + "isTandem: IOException closing file. File " + fileName + " Error " + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		return result;
	}


}

