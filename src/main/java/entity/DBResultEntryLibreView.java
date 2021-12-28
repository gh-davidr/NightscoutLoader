package entity;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import com.mongodb.DBObject;
import loader.AuditHistory;
import utils.CommonUtils;
import loader.DataLoadLibreView;

public class DBResultEntryLibreView extends DBResultEntry 
{

	// Collection of items to handle the CGM tab
	static private boolean m_CGMIndexesInitialized = false; 
	static private String[] m_CGMFieldNames =
		{
				// These field names are stripped of space
				"Device Timestamp",
				"Historic Glucose mmol/L",
				"Scan Glucose mmol/L"
		};
	static private int m_CGMTimeIndex = 0;
	static private int m_HistoricCGMBGIndex = 0;
	static private int m_ScanCGMBGIndex = 0;

	protected boolean  m_Valid = false;


	public DBResultEntryLibreView(DBObject rs) {
		super(rs);
		// TODO Auto-generated constructor stub
	}

	public DBResultEntryLibreView(String m_ID, Double m_Unfiltered, Double m_Filtered, String m_Direction, String m_Device,
			Double m_RSSI, Double m_SGV, String m_DateString, String m_Type, Double m_Date, Integer m_Noise) throws ParseException 
	{
		super(m_ID, m_Unfiltered, m_Filtered, m_Direction, m_Device,
				m_RSSI, m_SGV, m_DateString, m_Type, m_Date, m_Noise);
	}

	public DBResultEntryLibreView(String[] row) 
	{
		// Handle aa CGM record
		loadRawCGM(row);
	}
	
	public static void resetCGMHeaders()
	{
		m_CGMIndexesInitialized = false;
	}

	public static Boolean initializeCGMHeaders(String[] row)
	{
		Boolean result = false;

		if (m_CGMIndexesInitialized == false)
		{
			m_CGMTimeIndex = -1;
			m_HistoricCGMBGIndex = -1;
			m_ScanCGMBGIndex = -1;
			
			if (row.length >= m_CGMFieldNames.length)
			{
				HashMap<String, Integer> fieldPosHashMap = new HashMap<String, Integer>();

				for (int i = 0; i < row.length; i++)
				{
					fieldPosHashMap.put(row[i], Integer.valueOf(i));
				}

				for (int i = 0; i < m_CGMFieldNames.length; i++)
				{
					Integer posInteger = fieldPosHashMap.get(m_CGMFieldNames[i]);
					if (posInteger != null)
					{
						switch (i)
						{
						case 0 : m_CGMTimeIndex           = posInteger.intValue(); break;
						case 1 : m_HistoricCGMBGIndex     = posInteger.intValue(); break;
						case 2 : m_ScanCGMBGIndex         = posInteger.intValue(); break;
						default :                              break;
						}
					}
				}

				m_CGMIndexesInitialized = true;
			}
		}
		result = (m_CGMTimeIndex != -1) && (m_HistoricCGMBGIndex != -1) && (m_ScanCGMBGIndex != -1) ? true : false;

		return result;
	}

	private void loadRawCGM(String[] row)
	{
		String timeStr  = row[m_CGMTimeIndex];
		Double historicBGDouble = getBGValue(m_HistoricCGMBGIndex, row);
		Double scanBGDouble = getBGValue(m_ScanCGMBGIndex, row);
		Double bgValueDouble = historicBGDouble == null ? scanBGDouble : historicBGDouble;

		m_Device        = AuditHistory.getInstance().getM_NextUploadID();
		//		Boolean scannedValBoolean = scanBGDouble == null ? false : true;  // Would like to mark this as a scan but have nowhere to put it

		m_Type = "sgv";
		if (bgValueDouble != null)
		{
			m_SGV = bgValueDouble * 18;  // Values held in mgDl
			m_BG = bgValueDouble;
		}

		if (timeStr != null)
		{
			setM_UTCDateTime(DataLoadLibreView.parseFileLocalDateTime(timeStr));
			
			Date d = new Date(0);
			//			d = parseFileDate(timeStr);
			d = DataLoadLibreView.parseFileDateTime(timeStr);
			if (d.getTime() != 0)
			{
				//This isn't working, it's applying the offset twice, I suspect something to
				//do with Date.getTime() always returning results for display in local TZ
				//Date utcD = new Date(CommonUtils.toUTC(d.getTime()));
				Date utcD = new Date(d.getTime());
				this.setM_UTCDate(utcD);

				try {
					this.setM_DateString(CommonUtils.convertDateString(d, "dd/MM/yyyy HH:mm:ss"));
				} catch (ParseException e) {
					m_Logger.severe("Error converting Date to NSZDateString : " + timeStr);

					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				// Essential for use in comparator
				setEpochMilliesFromUTC();				
			}
		}

		m_Valid = (getM_BG() == null || getM_SGV() == null || getM_UTCDate() == null) ? false : true;
	}

	private Double getBGValue(int index, String[] rowStrings) 
	{
		Double resultDouble = null;
		if (rowStrings.length >= index)
		{
			if (rowStrings[index] != null && rowStrings[index].length() > 0)
			{
				resultDouble = Double.parseDouble(rowStrings[index]);
			}
		}
		return resultDouble;
	}

	public boolean isValid()
	{
		return m_Valid;
	}

	/**
	 * @return the m_CGMTimeIndex
	 */
	public static synchronized int getM_CGMTimeIndex() {
		return m_CGMTimeIndex;
	}

}
