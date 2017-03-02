package davidRichardson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataLoadMedtronic extends DataLoadCSVFile
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());
	private static final String m_MedtronicSplitBy = ",";

	@Override
	protected DBResult makeDBResult(String[] res) 
	{
		DBResultMedtronic result = new DBResultMedtronic(res);
		return result;
	}

	@Override
	protected String loadStringName() 
	{
		return "Medtronic";
	}
	
	@Override
	protected String getSplitBy() 
	{
		return m_MedtronicSplitBy;
	}

	public static boolean isMedtronic(String fileName)
	{
		boolean result = false;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		
		// Expected Format
		boolean ln2Col1PatientInfo = false;
		boolean ln3Col1Name        = false;
		boolean ln4Col1DateRange   = false;
		boolean ln5Col1Device      = false;
		boolean ln11Col1Index      = false;  // 640
		boolean ln13Col1Index      = false;  // Veo
		
		int ln = 0;
		int maxLines = 12;
		int index    = 10;  // for 640 & +2 for Veo
		int meter    = 6;   // Increment for each line that Meter is listed

		try 
		{
			br = new BufferedReader(new FileReader(fileName));
			while ((ln <= maxLines) && (line = br.readLine()) != null) 
			{
				ln++;
				// use comma as separator
				String[] rs = line.split(cvsSplitBy);
				
				if (ln == 2)
					ln2Col1PatientInfo  = (rs.length > 0 && rs[0].equals("PATIENT INFO")) ? true : false;
				if (ln == 3)
					ln3Col1Name         = (rs.length > 0 && rs[0].equals("Name"))         ? true : false;
				if (ln == 4)
					ln4Col1DateRange    = (rs.length > 0 && rs[0].equals("Report Range")) ? true : false;
				if (ln == 5)
					ln5Col1Device       = (rs.length > 0 && rs[0].equals("DEVICE INFO"))  ? true : false;
				
				if (ln == meter)
				{
					// There could be more than one meter, so allow index to drift forward by one
					// each time we see a separate meter line
					// 
					// 19 Feb 2017
					// Seen Pump: at line 6 in file from Melanie Mason
					if ((rs.length > 0 && (rs[0].equals("Meter:") || rs[0].equals("Pump:"))))
					{
						index++;
						meter++;
						maxLines++;
					}
				}
				
				if (ln == index)
					ln11Col1Index       = (rs.length > 0 && rs[0].equals("Index"))        ? true : false;
				if (ln == index + 2)
					ln13Col1Index       = (rs.length > 0 && rs[0].equals("Index"))        ? true : false;
			}
			
			result = (ln2Col1PatientInfo == true && ln3Col1Name == true && ln4Col1DateRange == true && 
					ln5Col1Device == true && (ln11Col1Index == true || ln13Col1Index == true) ) ? true : false;

		} 
		catch (FileNotFoundException e) 
		{
	    	m_Logger.log(Level.SEVERE, "<DataLoadMedtronic>" + "isMedtronic: FileNotFoundException. File " + fileName + " Error " + e.getMessage());

			e.printStackTrace();
		} 
		catch (IOException e) 
		{
	    	m_Logger.log(Level.SEVERE, "<DataLoadMedtronic>" + "isMedtronic: IOException. File " + fileName + " Error " + e.getMessage());
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
			    	m_Logger.log(Level.SEVERE, "<DataLoadMedtronic>" + "isMedtronic: IOException closing file. File " + fileName + " Error " + e.getMessage());
			    	e.printStackTrace();
				}
			}
		}

		return result;
	}


}

