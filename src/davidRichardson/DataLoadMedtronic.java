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
	private static final String m_MedtronicSplitBy = "[,;]";
	
	private static Boolean      m_OldFileFormat = false;
	private static Boolean      m_NewFileFormat = false;
	

	@Override
	protected DBResult makeDBResult(String[] res) 
	{
		DBResultMedtronic result = null;
		
		if (m_OldFileFormat == true) result = result == null ? new DBResultMedtronicOld(res) : result;
		if (m_NewFileFormat == true) result = result == null ? new DBResultMedtronicNew(res) : result;
		
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
		
		result = (result == true ? result : isMedtronicOld(fileName));
		result = (result == true ? result : isMedtronicNew(fileName));
		
		return result;
	}

	
	public static boolean isMedtronicOld(String fileName)
	{
		boolean result = false;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "[,;]";

		// Expected Format
		boolean ln2Col1PatientInfo = false;
		boolean ln3Col1Name        = false;
		boolean ln4Col1DateRange   = false;
		boolean ln5Col1Device      = false;
		boolean ln11Col1Index      = false;  // 640
		boolean ln13Col1Index      = false;  // Veo
		boolean pump               = false;
		boolean meter              = false;
		boolean pump_and_meter     = false;

		int ln = 0;
		int maxLines   = 12;
		int index      = 10;  // for 640 & +2 for Veo
		int meterpump1 = 6;   // Increment index if both meter and pump seen
		int meterpump2 = 7;   // Increment for each line that Meter is listed

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

				if (ln == meterpump1)
				{
					pump       = (pump  || (rs.length > 0 && rs[0].equals("Pump:")))  ? true : false;
					meter      = (meter || (rs.length > 0 && rs[0].equals("Meter:")))  ? true : false;
				}

				if (ln == meterpump2)
				{
					pump       = (pump  || (rs.length > 0 && rs[0].equals("Pump:")))  ? true : false;
					meter      = (meter || (rs.length > 0 && rs[0].equals("Meter:")))  ? true : false;
				}


				if (pump && meter && !pump_and_meter)
				{
					// There could be more than one meter, so allow index to drift forward by one
					// each time we see a separate meter line
					// 
					// 19 Feb 2017
					// Seen Pump: at line 6 in file from Melanie Mason
					index++;
					maxLines++;
					
					pump_and_meter = true;
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

		m_OldFileFormat = result;
		return result;
	}


	public static boolean isMedtronicNew(String fileName)
	{
		boolean result = false;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "[,;]";

		// Expected Format
		boolean ln1Col1PatientInfo = false;
		boolean ln6Col1Device      = false;
		boolean ln7Index           = false;
		
		int ln = 0;
		int maxLines   = 10;
		int index      = 10;  // for 640 & +2 for Veo
		int meterpump1 = 6;   // Increment index if both meter and pump seen
		int meterpump2 = 7;   // Increment for each line that Meter is listed

		try 
		{
			br = new BufferedReader(new FileReader(fileName));
			while ((ln <= maxLines) && (line = br.readLine()) != null) 
			{
				ln++;
				// use comma as separator
				String[] rs = line.split(cvsSplitBy);

				if (ln == 1)
					ln1Col1PatientInfo  = (rs.length > 0 && rs[0].equals("Last Name")) ? true : false;
				if (ln == 6)
					ln6Col1Device       = (rs.length > 0 && rs[0].equals("-------"))  ? true : false;
				if (ln == 7)
					ln7Index            = (rs.length > 0 && rs[0].equals("Index"))  ? true : false;

			}

			result = (ln1Col1PatientInfo == true && ln6Col1Device == true && ln7Index == true ) ? true : false;

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

		m_NewFileFormat = result;
		return result;
	}

}

