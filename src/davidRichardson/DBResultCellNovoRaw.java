package davidRichardson;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class DBResultCellNovoRaw extends DBResult 
{
	static final int m_HourlyValues     = 25;
	static final int m_FinalHourlyValue = 24;

	String m_Date = new String("");
	String m_Type = new String("");

	String[] m_HourlyValuesPlusTotal = new String[m_HourlyValues];

	DBResultCellNovoRaw(String[] rs)
	{
		if (rs.length == 27)
		{
			int i = 0;
			m_Date = rs[i++];
			m_Type = rs[i++];

			for (int j = 0; j < m_HourlyValues; j++)
			{
				m_HourlyValuesPlusTotal[j] = rs[i++];
			}
		}
	}

	public ArrayList<DBResult> createDBResults()
	{
		ArrayList<DBResult> result = new ArrayList<DBResult>();

		// We're not really interested in the final total value
		for (int i = 0; i < m_FinalHourlyValue; i++)
		{
			if (m_Type.equals("Blood Glucose"))
			{
				ArrayList<Double> bgs = this.readBGValues(m_HourlyValuesPlusTotal[i]);

				// How many results do we have?
				// Distribute them across the hour evenly.
				int minGap = 60 / (bgs.size() > 0 ? bgs.size() : 1);

				int j = 0;
				for (Double d : bgs)
				{
					Date date = createDateFromString(m_Date, i, j++ * minGap);
					DBResult dbRes = createDBResult("BG", date, d);
					result.add(dbRes);
				}
			}
			else if (m_Type.equals("Food"))
			{
				if (m_HourlyValuesPlusTotal[i].length() > 0)
				{
					Date date = createDateFromString(m_Date, i);
					DBResult dbRes = createDBResult("Carbs", date, m_HourlyValuesPlusTotal[i]);
					result.add(dbRes);
				}
			} 
			else if (m_Type.equals("Bolus"))
			{
				if (m_HourlyValuesPlusTotal[i].length() > 0)
				{
					Date date = createDateFromString(m_Date, i);
					DBResult dbRes = createDBResult("Standard Bolus", date, m_HourlyValuesPlusTotal[i]);
					result.add(dbRes);
				}
			} 
		}

		return result;
	}

	public boolean isValid()
	{
		boolean result = false;

		if ( (m_Date.length() > 0 && m_Type.length() > 0) &&
				!m_Date.equals("Date") )
		{

			result = true;
		}

		return result;
	}

	private DBResult createDBResult(String type, Date date, String val)
	{
		DBResult result = new DBResult();

		Double d = readValue(val);
		result.setM_Time(date);
		result.setM_ResultType(type);
		result.setM_Result(Double.toString(d));
		
		// Set epoch millies too for later use in comparator
		result.setM_EpochMillies(date.getTime());

		return result;
	}

	private DBResult createDBResult(String type, Date date, Double val)
	{
		DBResult result = new DBResult();

		result.setM_Time(date);
		result.setM_ResultType(type);
		result.setM_Result(Double.toString(val));

		// Set epoch millies too for later use in comparator
		result.setM_EpochMillies(date.getTime());

		return result;
	}


	private Date createDateFromString(String date, int hour)
	{
		Date result = createDateFromString(date, hour, 0);
		return result;
	}

	private Date createDateFromString(String date, int hour, int mins)
	{
		Date result = new Date(0);

		String format = new String("EEEE dd MMM yyyyy HH:mm");
		try 
		{	
			String dateTime = date + " " + String.format("%02d", hour) + ":" + String.format("%02d", mins);

			result = CommonUtils.convertDateString(dateTime, format);
		}
		catch (ParseException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}


	private ArrayList<Double> readBGValues(String field)
	{
		ArrayList<Double> result = new ArrayList<Double>();

		String[] bgVals = field.split("/ ");

		for (int i = 0; i < bgVals.length; i++)
		{
			if (bgVals[i].length() > 0)
			{
				Double val = readValue(bgVals[i]);
				result.add(val);
			}
		}

		return result;
	}

	private Double readValue(String val)
	{
		Double result = 0.0;

		String[] vals = val.split(" ");

		if (vals.length > 0)
		{
			result = Double.parseDouble(vals[0]);
		}

		return result;
	}

	/**
	 * @return the m_Date
	 */
	public synchronized String getM_Date() {
		return m_Date;
	}

	/**
	 * @param m_Date the m_Date to set
	 */
	public synchronized void setM_Date(String m_Date) {
		this.m_Date = m_Date;
	}

	/**
	 * @return the m_Type
	 */
	public synchronized String getM_Type() {
		return m_Type;
	}

	/**
	 * @param m_Type the m_Type to set
	 */
	public synchronized void setM_Type(String m_Type) {
		this.m_Type = m_Type;
	}

	/**
	 * @return the m_HourlyValuesPlusTotal
	 */
	public synchronized String[] getM_HourlyValuesPlusTotal() {
		return m_HourlyValuesPlusTotal;
	}

	/**
	 * @param m_HourlyValuesPlusTotal the m_HourlyValuesPlusTotal to set
	 */
	public synchronized void setM_HourlyValuesPlusTotal(String[] m_HourlyValuesPlusTotal) {
		this.m_HourlyValuesPlusTotal = m_HourlyValuesPlusTotal;
	}






}
