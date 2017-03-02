package davidRichardson;

import java.text.ParseException;
import java.util.Date;
import java.util.logging.Logger;

import com.mongodb.DBObject;

public class DBResultEntry implements DBResultInterface
{
	protected static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	// Entry Data for Nightscout
	protected String   m_ID;
	protected Double   m_Unfiltered;
	protected Double   m_Filtered;
	protected String   m_Direction;
	protected String   m_Device;
	protected Double   m_RSSI;   
	protected Double   m_SGV;
	protected String   m_DateString;
	protected String   m_Type;
	protected Double   m_Date;
	protected Integer  m_Noise;

	protected Date     m_UTCDate;
	protected long     m_EpochMillies = 0;
	protected int      m_Hour = 0;

	protected Double   m_BG;  // Hold BG reading separately in mmol/L


	private AnalyzerResultEntryInterval m_AnalyzerResultEntryInterval;

	/**
	 * @param m_ID
	 * @param m_Unfiltered
	 * @param m_Filtered
	 * @param m_Direction
	 * @param m_Device
	 * @param m_RSSI
	 * @param m_SGV
	 * @param m_DateString
	 * @param m_Type
	 * @param m_Date
	 * @param m_Noise
	 * @throws ParseException 
	 */
	public DBResultEntry(String m_ID, Double m_Unfiltered, Double m_Filtered, String m_Direction, String m_Device,
			Double m_RSSI, Double m_SGV, String m_DateString, String m_Type, Double m_Date, Integer m_Noise) throws ParseException 
	{
		super();
		this.m_ID = m_ID;
		this.m_Unfiltered = m_Unfiltered;
		this.m_Filtered = m_Filtered;
		this.m_Direction = m_Direction;
		this.m_Device = m_Device;
		this.m_RSSI = m_RSSI;
//		this.m_SGV = m_SGV;
		setM_SGV(m_SGV);
		this.m_DateString = m_DateString;
		this.m_Type = m_Type;
		this.m_Date = m_Date;
		this.m_Noise = m_Noise;

		m_UTCDate = CommonUtils.convertNSZDateString(m_DateString);
		m_EpochMillies = m_UTCDate.getTime();
		m_Hour = CommonUtils.get24Hour(m_UTCDate);
		
		setM_BG(m_SGV / 18.0);
	}

	public  DBResultEntry(DBObject rs)
	{
//		final DateFormat format     = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.ENGLISH);
//		//		final DateFormat nsformat   = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
//		final DateFormat dayformat  = new SimpleDateFormat("EEEE", Locale.ENGLISH);
//		final DateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
//		final DateFormat timeformat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

		m_ID            = CommonUtils.getIDStr(rs, "_id");
		m_Unfiltered    = CommonUtils.getFieldDouble(rs, "unfiltered");
		m_Direction     = CommonUtils.getFieldStr(rs, "direction");
		m_Device        = CommonUtils.getFieldStr(rs, "device");
		m_RSSI          = CommonUtils.getFieldDouble(rs, "rssi");
//		m_SGV           = CommonUtils.getFieldDouble(rs, "sgv");
		setM_SGV(CommonUtils.getFieldDouble(rs, "sgv"));
		m_DateString    = CommonUtils.getFieldStr(rs, "dateString");
		m_Type          = CommonUtils.getFieldStr(rs, "type");
		//		m_Date          = CommonUtils.getFieldLong(rs, "date");
		m_Date          = CommonUtils.getFieldDouble(rs, "date");
		m_Noise         = CommonUtils.getFieldInt(rs, "noise");

		try {
			m_UTCDate = CommonUtils.convertNSZDateString(m_DateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			m_UTCDate = new Date(0);
		}
	

	}


	// override the equal method
	@Override
	public boolean equals(Object obj) 
	{ 
		if (obj == this) 
		{ 
			return true; 
		} 
		if (obj == null)
		{
			return false; 
		} 
		DBResultEntry guest = (DBResultEntry) obj;
		boolean result = false;

		String id       = getIdentity();
		String guest_id = guest.getIdentity();

		result = id.equals(guest_id);
		return result; 
	}

	@Override 
	public int hashCode() 
	{ 
		//		final int prime = 31; 
		int result = 1; 
		String id       = getIdentity();

		result = id.hashCode();
		return result; 
	}

	public String getIdentity()
	{
		return m_ID;
	}

	/**
	 * @return the m_ID
	 */
	public synchronized String getM_ID() {
		return m_ID;
	}

	/**
	 * @param m_ID the m_ID to set
	 */
	public synchronized void setM_ID(String m_ID) {
		this.m_ID = m_ID;
	}

	/**
	 * @return the m_Unfiltered
	 */
	public synchronized Double getM_Unfiltered() {
		return m_Unfiltered;
	}

	/**
	 * @param m_Unfiltered the m_Unfiltered to set
	 */
	public synchronized void setM_Unfiltered(Double m_Unfiltered) {
		this.m_Unfiltered = m_Unfiltered;
	}

	/**
	 * @return the m_Filtered
	 */
	public synchronized Double getM_Filtered() {
		return m_Filtered;
	}

	/**
	 * @param m_Filtered the m_Filtered to set
	 */
	public synchronized void setM_Filtered(Double m_Filtered) {
		this.m_Filtered = m_Filtered;
	}

	/**
	 * @return the m_Direction
	 */
	public synchronized String getM_Direction() {
		return m_Direction;
	}

	/**
	 * @param m_Direction the m_Direction to set
	 */
	public synchronized void setM_Direction(String m_Direction) {
		this.m_Direction = m_Direction;
	}

	/**
	 * @return the m_Device
	 */
	public synchronized String getM_Device() {
		return m_Device;
	}

	/**
	 * @param m_Device the m_Device to set
	 */
	public synchronized void setM_Device(String m_Device) {
		this.m_Device = m_Device;
	}

	/**
	 * @return the m_RSSI
	 */
	public synchronized Double getM_RSSI() {
		return m_RSSI;
	}

	/**
	 * @param m_RSSI the m_RSSI to set
	 */
	public synchronized void setM_RSSI(Double m_RSSI) {
		this.m_RSSI = m_RSSI;
	}

	/**
	 * @return the m_SGV
	 */
	public synchronized Double getM_SGV() {
		return m_SGV;
	}


	/**
	 * @param m_SGV the m_SGV to set
	 */
	public synchronized void setM_SGV(Double m_SGV) 
	{
		this.m_SGV = m_SGV;
		// Only do this if SGV is not null
		if (m_SGV != null)
		{
			this.setM_BG(m_SGV / 18.0);
		}
	}

	/**
	 * @return the m_DateString
	 */
	public synchronized String getM_DateString() {
		return m_DateString;
	}

	/**
	 * @param m_DateString the m_DateString to set
	 */
	public synchronized void setM_DateString(String m_DateString) {
		this.m_DateString = m_DateString;
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
	 * @return the m_Date
	 */
	public synchronized Double getM_Date() {
		return m_Date;
	}

	/**
	 * @param m_Date the m_Date to set
	 */
	public synchronized void setM_Date(Double m_Date) {
		this.m_Date = m_Date;
	}

	/**
	 * @return the m_Noise
	 */
	public synchronized Integer getM_Noise() {
		return m_Noise;
	}

	/**
	 * @param m_Noise the m_Noise to set
	 */
	public synchronized void setM_Noise(Integer m_Noise) {
		this.m_Noise = m_Noise;
	}

	/**
	 * @return the m_UTCDate
	 */
	public synchronized Date getM_UTCDate() {
		return m_UTCDate;
	}

	/**
	 * @param m_UTCDate the m_UTCDate to set
	 */
	public synchronized void setM_UTCDate(Date m_UTCDate) {
		this.m_UTCDate = m_UTCDate;
	}

	@Override
	public long getM_EpochMillies() 
	{
		return m_EpochMillies;
	}

	/**
	 * @return the m_Hour
	 */
	public synchronized int getM_Hour() {
		return m_Hour;
	}

	/**
	 * @return the m_BG
	 */
	public synchronized Double getM_BG() {
		return m_BG;
	}

	/**
	 * @param m_BG the m_BG to set
	 */
	public synchronized void setM_BG(Double m_BG) 
	{
		this.m_BG = m_BG;
	}

	/**
	 * @return the m_AnalyzerResultEntryInterval
	 */
	public synchronized AnalyzerResultEntryInterval getM_AnalyzerResultEntryInterval() {
		return m_AnalyzerResultEntryInterval;
	}

	/**
	 * @param m_AnalyzerResultEntryInterval the m_AnalyzerResultEntryInterval to set
	 */
	public synchronized void setM_AnalyzerResultEntryInterval(AnalyzerResultEntryInterval m_AnalyzerResultEntryInterval) {
		this.m_AnalyzerResultEntryInterval = m_AnalyzerResultEntryInterval;
	}


}
