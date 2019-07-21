package davidRichardson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataLoadCellNovo extends DataLoadCSVFile 
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());
	private static final String m_CellNovoSplitBy = ";";


	@Override
	protected DBResult makeDBResult(String[] res) 
	{
		return null;
	}

	@Override
	protected ArrayList<DBResult> makeDBResultList(String[] res) 
	{
		ArrayList<DBResult> result = null;
		DBResultCellNovoRaw rawRes = new DBResultCellNovoRaw(res);
		if (rawRes.isValid())
		{
			result = rawRes.createDBResults();
		}
		return result;
	}
	
	@Override
	protected void orderRawResults()
	{
		Collections.sort(rawResultsFromDB, new ResultFromDBComparator(false));
	}

	@Override
	protected String loadStringName() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getSplitBy() 
	{
		return m_CellNovoSplitBy;
	}

	public static boolean isCellNovo(String fileName)
	{
		boolean result = false;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = m_CellNovoSplitBy; 

		// Expected Format

		boolean ln9Date       = false;
		boolean ln9Type       = false;
		boolean ln9Hour0      = false;
		boolean ln9Hour1      = false;
		boolean ln9Hour2      = false;
		boolean ln9Hour3      = false;
		boolean ln9Hour4      = false;
		boolean ln9Hour5      = false;
		boolean ln9Hour6      = false;
		boolean ln9Hour7      = false;
		boolean ln9Hour8      = false;
		boolean ln9Hour9      = false;
		boolean ln9Hour10     = false;
		boolean ln9Hour11     = false;
		boolean ln9Hour12     = false;
		boolean ln9Hour13     = false;
		boolean ln9Hour14     = false;
		boolean ln9Hour15     = false;
		boolean ln9Hour16     = false;
		boolean ln9Hour17     = false;
		boolean ln9Hour18     = false;
		boolean ln9Hour19     = false;
		boolean ln9Hour20     = false;
		boolean ln9Hour21     = false;
		boolean ln9Hour22     = false;
		boolean ln9Hour23     = false;
		boolean ln9Total      = false;


		int ln = 0;
		int maxLines = 10; 
		int lineCheck = 9; // Check line 9 as that's where the data really starts

		try 
		{
			br = new BufferedReader(new FileReader(fileName));
			while ((ln <= maxLines) && (line = br.readLine()) != null) 
			{
				ln++;
				// use comma as separator
				String[] rs = line.split(cvsSplitBy);

				if (ln == lineCheck)
				{
					int i = 0;
					ln9Date       = (rs.length > i && rs[i++].equals("Date")) ? true : false;
					ln9Type       = (rs.length > i && rs[i++].equals(""))     ? true : false;
					ln9Hour0      = (rs.length > i && rs[i++].equals("00"))    ? true : false;
					ln9Hour1      = (rs.length > i && rs[i++].equals("01"))    ? true : false;
					ln9Hour2      = (rs.length > i && rs[i++].equals("02"))    ? true : false;
					ln9Hour3      = (rs.length > i && rs[i++].equals("03"))    ? true : false;
					ln9Hour4      = (rs.length > i && rs[i++].equals("04"))    ? true : false;
					ln9Hour5      = (rs.length > i && rs[i++].equals("05"))    ? true : false;
					ln9Hour6      = (rs.length > i && rs[i++].equals("06"))    ? true : false;
					ln9Hour7      = (rs.length > i && rs[i++].equals("07"))    ? true : false;
					ln9Hour8      = (rs.length > i && rs[i++].equals("08"))    ? true : false;
					ln9Hour9      = (rs.length > i && rs[i++].equals("09"))    ? true : false;
					ln9Hour10     = (rs.length > i && rs[i++].equals("10"))   ? true : false;
					ln9Hour11     = (rs.length > i && rs[i++].equals("11"))   ? true : false;
					ln9Hour12     = (rs.length > i && rs[i++].equals("12"))   ? true : false;
					ln9Hour13     = (rs.length > i && rs[i++].equals("13"))   ? true : false;
					ln9Hour14     = (rs.length > i && rs[i++].equals("14"))   ? true : false;
					ln9Hour15     = (rs.length > i && rs[i++].equals("15"))   ? true : false;
					ln9Hour16     = (rs.length > i && rs[i++].equals("16"))   ? true : false;
					ln9Hour17     = (rs.length > i && rs[i++].equals("17"))   ? true : false;
					ln9Hour18     = (rs.length > i && rs[i++].equals("18"))   ? true : false;
					ln9Hour19     = (rs.length > i && rs[i++].equals("19"))   ? true : false;
					ln9Hour20     = (rs.length > i && rs[i++].equals("20"))   ? true : false;
					ln9Hour21     = (rs.length > i && rs[i++].equals("21"))   ? true : false;
					ln9Hour22     = (rs.length > i && rs[i++].equals("22"))   ? true : false;
					ln9Hour23     = (rs.length > i && rs[i++].equals("23"))   ? true : false;
					ln9Total      = (rs.length > i && rs[i++].equals("Total"))? true : false;

				}

			}

			result = (ln9Date == true && 
					ln9Hour0  == true && ln9Hour1  == true && ln9Hour2  == true && ln9Hour3  == true &&
					ln9Hour4  == true && ln9Hour5  == true && ln9Hour6  == true && ln9Hour7  == true &&
					ln9Hour8  == true && ln9Hour9  == true && ln9Hour10 == true && ln9Hour11 == true &&
					ln9Hour12 == true && ln9Hour13 == true && ln9Hour14 == true && ln9Hour15 == true &&
					ln9Hour16 == true && ln9Hour17 == true && ln9Hour18 == true && ln9Hour19 == true &&
					ln9Hour20 == true && ln9Hour21 == true && ln9Hour22 == true && ln9Hour23 == true &&
					ln9Total == true) ? true : false;

		} 
		catch (FileNotFoundException e) 
		{
			m_Logger.log(Level.SEVERE, "<DataLoadCellNovo>" + "isCellNovo: FileNotFoundException. File " + fileName + " Error " + e.getMessage());

			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			m_Logger.log(Level.SEVERE, "<DataLoadCellNovo>" + "isCellNovo: IOException. File " + fileName + " Error " + e.getMessage());
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
					m_Logger.log(Level.SEVERE, "<DataLoadCellNovo>" + "isMedtronic: IOException closing file. File " + fileName + " Error " + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		return result;
	}

}
