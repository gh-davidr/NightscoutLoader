package davidRichardson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataLoadRocheCSV extends DataLoadCSVFile
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());
	private static final String m_RocheSplitBy = "\t";

	@Override
	protected DBResult makeDBResult(String[] res) 
	{
		DBResultRoche result = new DBResultRoche(res);
		return result;
	}

	@Override
	protected DBResult makeDBResult(String[] res, DBResult.ResultType resType) 
	{
		DBResult result = null;
		return result;
	}
	
	@Override
	protected String loadStringName() 
	{
		return "Roche";
	}

	@Override
	protected String getSplitBy() 
	{
		return m_RocheSplitBy;
	}

	public static boolean isRoche(String fileName)
	{
		boolean result = false;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = m_RocheSplitBy; 

		// Expected Format
		boolean ln1Year       = false;
		boolean ln1Month      = false;
		boolean ln1Day        = false;
		boolean ln1DayName    = false;
		boolean ln1Time       = false;  // 640
		boolean ln1TimeSlot   = false;  // Veo
		boolean ln1Result     = false;  // Veo
		boolean ln1ResultType = false;  // Veo
		boolean ln1MealType   = false;  // Veo
		boolean ln1Duration   = false;  // Veo

		int ln = 0;
		int maxLines = 1;  // Just one line

		try 
		{
			br = new BufferedReader(new FileReader(fileName));
			while ((ln <= maxLines) && (line = br.readLine()) != null) 
			{
				ln++;
				// use comma as separator
				String[] rs = line.split(cvsSplitBy);

				if (ln == 1)
				{
					ln1Year       = (rs.length > 0 && rs[0].equals("Year")) ? true : false;
					ln1Month      = (rs.length > 0 && rs[1].equals("Month")) ? true : false;
					ln1Day        = (rs.length > 0 && rs[2].equals("Day")) ? true : false;
					ln1DayName    = (rs.length > 0 && rs[3].equals("DayName")) ? true : false;
					ln1Time       = (rs.length > 0 && rs[4].equals("Time")) ? true : false;
					ln1TimeSlot   = (rs.length > 0 && rs[5].equals("TimeSlot")) ? true : false;
					ln1Result     = (rs.length > 0 && rs[6].equals("Result")) ? true : false;
					ln1ResultType = (rs.length > 0 && rs[7].equals("ResultType")) ? true : false;
					ln1MealType   = (rs.length > 0 && rs[8].equals("MealType")) ? true : false;
					ln1Duration   = (rs.length > 0 && rs[9].equals("Duration")) ? true : false;

				}

			}

			result = (ln1Year == true && ln1Month == true && ln1Day == true && 
					ln1DayName == true && ln1Time == true && ln1TimeSlot == true &&
					ln1Result == true && ln1ResultType == true && ln1MealType == true && ln1Duration == true) ? true : false;

		} 
		catch (FileNotFoundException e) 
		{
			m_Logger.log(Level.SEVERE, "<DataLoadRoche>" + "isRoche: FileNotFoundException. File " + fileName + " Error " + e.getMessage());

			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			m_Logger.log(Level.SEVERE, "<DataLoadRoche>" + "isRoche: IOException. File " + fileName + " Error " + e.getMessage());
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
					m_Logger.log(Level.SEVERE, "<DataLoadRoche>" + "isMedtronic: IOException closing file. File " + fileName + " Error " + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		return result;
	}


}

