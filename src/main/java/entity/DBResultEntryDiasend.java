package entity;

import java.text.ParseException;
import java.util.Date;
import org.apache.poi.hssf.usermodel.HSSFRow;

import com.mongodb.DBObject;

import loader.AuditHistory;
import utils.CommonUtils;
import loader.DataLoadDiasend;

public class DBResultEntryDiasend extends DBResultEntry 
{
	// Collection of items to handle the CGM tab
	static private boolean m_CGMIndexesInitialized = false; 
	static private String[] m_CGMFieldNames =
		{
				"Time",
				"mmol/L mg/dl",  // We can see either string come through
				"",              // This field is empty
				"Serial number"
		};
	static private int m_CGMTimeIndex = 0;
	static private int m_CGMBGIndex = 0;


	public DBResultEntryDiasend(DBObject rs) {
		super(rs);
		// TODO Auto-generated constructor stub
	}

	public DBResultEntryDiasend(String m_ID, Double m_Unfiltered, Double m_Filtered, String m_Direction, String m_Device,
			Double m_RSSI, Double m_SGV, String m_DateString, String m_Type, Double m_Date, Integer m_Noise) throws ParseException 
	{
		super(m_ID, m_Unfiltered, m_Filtered, m_Direction, m_Device,
				m_RSSI, m_SGV, m_DateString, m_Type, m_Date, m_Noise);
	}

	public DBResultEntryDiasend(HSSFRow row) 
	{
		// Handle aa CGM record
		loadRawCGM(row);
	}
	
	public static void resetCGMHeaders()
	{
		m_CGMIndexesInitialized = false;
	}

	public static Boolean initializeCGMHeaders(HSSFRow row)
	{
		Boolean result = false;
		
		if (m_CGMIndexesInitialized == false)
		{
			m_CGMTimeIndex = -1;
			m_CGMBGIndex = -1;
			
			int maxColumns = row.getPhysicalNumberOfCells();

			// Diasend now has an empty column
			int cols = 0;
			for (int i = 0; i < m_CGMFieldNames.length; i++) cols += m_CGMFieldNames[i].length() > 0 ? 1 : 0;

			if (maxColumns >= cols)
			{
				int c = 0;

				for (c=0; c < m_CGMFieldNames.length; c++)
				{
					if (row.getCell(c) != null)
					{
						String cell = row.getCell(c).getStringCellValue();
						if (m_CGMFieldNames[c].contains(cell))
						{
							switch (c)
							{
							case 0 : m_CGMTimeIndex           = c; break;
							case 1 : m_CGMBGIndex             = c; break;
							default :                         break;
							}
						}
					}
				}
				m_CGMIndexesInitialized = true;
			}
		}
		
		result = (m_CGMTimeIndex != -1) && (m_CGMBGIndex != -1) ? true : false;
		
		return result;
	}

	private void loadRawCGM(HSSFRow row)
	{
		String timeStr  = CommonUtils.getStringCellValue(row, m_CGMTimeIndex);
		Double bgDouble = CommonUtils.getDoubleCellValue(row, m_CGMBGIndex);

		m_Device        = AuditHistory.getInstance().getM_NextUploadID();

		m_Type = "sgv";
		if (bgDouble != null)
		{
			m_SGV = bgDouble * 18;  // Values held in mgDl
			m_BG = bgDouble;
		}

		if (timeStr != null)
		{
			Date d = new Date(0);
			//			d = parseFileDate(timeStr);
			d = DataLoadDiasend.parseFileDateTime(timeStr);
			if (d.getTime() != 0)
			{
				//This isn't working, it's applying the offset twice, I suspect something to
				//do with Date.getTime() always returning results for display in local TZ
				//Date utcD = new Date(CommonUtils.toUTC(d.getTime()));
				Date utcD = new Date(d.getTime());
				this.setM_UTCDate(utcD);

				try {
					this.setM_DateString(CommonUtils.convertNSZDateString(d));
				} catch (ParseException e) {
					m_Logger.severe("Error converting Date to NSZDateString : " + timeStr);
				}


				// Essential for use in comparator
				setEpochMilliesFromUTC();				
			}
		}
	}

	/**
	 * @return the m_CGMTimeIndex
	 */
	public static synchronized int getM_CGMTimeIndex() {
		return m_CGMTimeIndex;
	}

	//	private Date parseFileDateTime(String date)
	//	{
	//		Date result = new Date(0);
	//		// Combined Date Time
	//
	////		final String defSlashFormat = new String("dd/MM/yy HH:mm");  -- Changed with Glooko?
	//		// In fact, now find that it changes between dd/MM/yyyy and MM/dd/yyyy, so we hold a preference
	//		// and set the preference during an initial file scan
	//		
	//		final String defSlashFormat = new String(
	//				PrefsNightScoutLoader.getInstance().getM_DiasendDateFormat() + " HH:mm");
	//		
	//		String prefDateFormat       = PrefsNightScoutLoader.getInstance().getM_InputDateFormat();
	//		DateFormat slashformat      = new SimpleDateFormat((prefDateFormat.contains("/")  ?  prefDateFormat : defSlashFormat), Locale.ENGLISH);
	//		//		DateFormat slashformat      = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
	//
	//		try
	//		{
	//			result = slashformat.parse(date);
	//		}
	//		catch (ParseException e) 
	//		{
	//			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+"> " + "parseFileDate - Unexpected error parsing date: " + date);
	//		}
	//
	//		return result;
	//	}


}
