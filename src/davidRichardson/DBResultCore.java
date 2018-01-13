package davidRichardson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mongodb.BasicDBObject;

public abstract class DBResultCore implements DBResultInterface
{
	// Sep 2016
	// Proximity checks are for where we have a new Meter/Pump entry coming into an
	// existing NightScout Care Portal data set, and there's a possible duplicate
	// among them.
	protected static boolean m_ProximityCheck           = false;
	protected static boolean m_ProximityCheckSecondPass = false;

	// Proximity match with an existing record
	private boolean m_ProximityPossibleDuplicate = false;

	
	// Can only instantiate derived classes
	protected DBResultCore()
	{
		
	}
	
	public long getProximityAdjustedTime(long time)
	{
		long result = 0;
		
		if (m_ProximityCheck == true)
		{
			// How many minutes apart two entries can be before being considered proximity/duplicate
			int     proximityMinutes    = PrefsNightScoutLoader.getInstance().getM_ProximityMinutes();

			// Since we may have 2 adjacent readings either side of the mid point of proximityMinutes, we do a second
			// pass looking for proximity matches but this time slide the time forward by half the proximityMinutes
			if (m_ProximityCheckSecondPass == true)
			{
				long halfProximityMinutesMillis = proximityMinutes * 60 * 1000 / 2;

				time += halfProximityMinutesMillis;
			}

			long roundPeriodMins = proximityMinutes * 60 * 1000;
			// Adjust time by rounding up or down to nearest proximity minutes approximately.

			long timeUp   = time - (time % roundPeriodMins) + roundPeriodMins;
			long timeDown = time - (time % roundPeriodMins);

			// Are we closer to Up time or Down
			if ( (timeUp - time) > (time - timeDown) )
			{
				result = timeDown;
			}
			else
			{
				result = timeUp;
			}
		}
		else
		{
			result = time;
		}
		
		return result;
	}
	
	static public void appendToDoc(BasicDBObject doc, String label, String value)
	{
		if (value.length() > 0)
		{
			doc.append(label, value);
		}
	}

	static public void appendToDoc(BasicDBObject doc, String label, Double value)
	{
		if (value != null)
		{
			doc.append(label, doubleIsInteger(value) ? value.longValue() : value.doubleValue());
		}
	}

	static public void appendToDoc(BasicDBObject doc, String label, int value)
	{
		doc.append(label, value);
	}

	static public void appendToDoc(BasicDBObject doc, String label, Date value)
	{
		if (value != null)
		{
			// 16 Jun 2016
			// Feedback from Mel in Australia that times are shifted
			// Realise that I need to convert from local to UTC times!
			//			final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'", Locale.ENGLISH);
			//			final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH); // Try something different

			final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			//			Date utcValue = new Date(CommonUtils.toUTC(value.getTime(), CommonUtils.locTZ)); 
			//			String dVal = format.format(utcValue);

			String dVal = format.format(value);

			doc.append(label, dVal);
		}

	}

	static public boolean doubleIsInteger(double val)
	{
		boolean result = false;  // Assume not initially

		if (val == Math.floor(val))
		{
			result = true;
		}
		return result;
	}

	
	/**
	 * @return the m_ProximityCheck
	 */
	public static synchronized boolean isM_ProximityCheck() {
		return m_ProximityCheck;
	}

	/**
	 * @param m_ProximityCheck the m_ProximityCheck to set
	 */
	public static synchronized void setM_ProximityCheck(boolean proximityCheck) {
		DBResultCore.m_ProximityCheck = proximityCheck;
	}

	/**
	 * @return the m_ProximityCheckSecondPass
	 */
	public static synchronized boolean isM_ProximityCheckSecondPass() {
		return m_ProximityCheckSecondPass;
	}

	/**
	 * @param m_ProximityCheckSecondPass the m_ProximityCheckSecondPass to set
	 */
	public static synchronized void setM_ProximityCheckSecondPass(boolean proximityCheckSecondPass) {
		DBResultCore.m_ProximityCheckSecondPass = proximityCheckSecondPass;
	}

	/**
	 * @return the m_ProximityPossibleDuplicate
	 */
	public synchronized boolean isM_ProximityPossibleDuplicate() 
	{
		determineWhetherInProximity();
		return m_ProximityPossibleDuplicate;
	}

	/**
	 * @param m_ProximityPossibleDuplicate the m_ProximityPossibleDuplicate to set
	 */
	public synchronized void setM_ProximityPossibleDuplicate(boolean m_ProximityPossibleDuplicate) 
	{
		this.m_ProximityPossibleDuplicate = m_ProximityPossibleDuplicate;
		setImpactOfProximity();	
	}

}
