package davidRichardson;

import org.apache.poi.hssf.usermodel.HSSFRow;

public class DBResultDiasendBasalSetting extends DBResultDiasendPumpSetting
{
	private String m_BasalProfileName;

	DBResultDiasendBasalSetting(String basalProfileName, HSSFRow row)
	{
		super(row);
		m_BasalProfileName = new String(basalProfileName);
	}

	public String toString()
	{
		return String.format("Profile Name: %s, Index: %d, Time: %s, Value: %g",
				m_BasalProfileName, getM_Index(), getM_Time(), getM_BasalValue());  			
	}


	/**
	 * @return the m_BasalProfileName
	 */
	public synchronized String getM_BasalProfileName() {
		return m_BasalProfileName;
	}

	/**
	 * @param m_BasalProfileName the m_BasalProfileName to set
	 */
	public synchronized void setM_BasalProfileName(String m_BasalProfileName) {
		this.m_BasalProfileName = m_BasalProfileName;
	}

	/**
	 * @return the m_BasalValue
	 */
	public synchronized Double getM_BasalValue() {
		return getM_TimeBoundValue();
	}

	/**
	 * @param m_BasalValue the m_BasalValue to set
	 */
	public synchronized void setM_BasalValue(Double m_BasalValue) {
		setM_TimeBoundValue(m_BasalValue);
	}
}


//Original class definition
//
//package davidRichardson;
//
//import org.apache.poi.hssf.usermodel.HSSFCell;
//import org.apache.poi.hssf.usermodel.HSSFRow;
//
//public class DBResultDiasendBasalSetting 
//{
//	private String m_BasalProfileName;
//	private int    m_Index;
//	private String m_Time;
//	private Double m_BasalValue;
//	
//	private final int m_SettingsFields = 3;
//
//	// Utility Function for getting a cell from row as string even if numeric
//	static String getCellAsString(HSSFRow row, int index)
//	{
//		String result;
//		// Check type of this cell first
//		if (row.getCell(index).getCellType() == HSSFCell.CELL_TYPE_STRING)
//		{
//			result = row.getCell(index).getStringCellValue();
//		}
//		else
//		{
//			Double result_num = row.getCell(index).getNumericCellValue();
//			result = result_num.toString();
//		}
//		return result;
//	}
//	
//	DBResultDiasendBasalSetting(String basalProfileName, HSSFRow row)
//	{
//		m_BasalProfileName = new String(basalProfileName);
//
//		int maxColumns = row.getPhysicalNumberOfCells();
//		if (maxColumns == m_SettingsFields)
//		{			
//			Double cell1 = row.getCell(0).getNumericCellValue();
//			String cell2 = row.getCell(1).getStringCellValue();
//			Double cell3 = row.getCell(2).getNumericCellValue();
//
//			m_Index      = cell1.intValue();
//			m_Time       = cell2;
//			m_BasalValue = cell3;
//		}
//	}
//	
//	public String toString()
//	{
//		return String.format("Profile Name: %s, Index: %d, Time: %s, Value: %g",
//				m_BasalProfileName, m_Index, m_Time, m_BasalValue);  			
//	}
//
//
//	/**
//	 * @return the m_BasalProfileName
//	 */
//	public synchronized String getM_BasalProfileName() {
//		return m_BasalProfileName;
//	}
//
//	/**
//	 * @param m_BasalProfileName the m_BasalProfileName to set
//	 */
//	public synchronized void setM_BasalProfileName(String m_BasalProfileName) {
//		this.m_BasalProfileName = m_BasalProfileName;
//	}
//
//	/**
//	 * @return the m_Index
//	 */
//	public synchronized int getM_Index() {
//		return m_Index;
//	}
//
//	/**
//	 * @param m_Index the m_Index to set
//	 */
//	public synchronized void setM_Index(int m_Index) {
//		this.m_Index = m_Index;
//	}
//
//	/**
//	 * @return the m_Time
//	 */
//	public synchronized String getM_Time() {
//		return m_Time;
//	}
//
//	/**
//	 * @param m_Time the m_Time to set
//	 */
//	public synchronized void setM_Time(String m_Time) {
//		this.m_Time = m_Time;
//	}
//
//	/**
//	 * @return the m_BasalValue
//	 */
//	public synchronized Double getM_BasalValue() {
//		return m_BasalValue;
//	}
//
//	/**
//	 * @param m_BasalValue the m_BasalValue to set
//	 */
//	public synchronized void setM_BasalValue(Double m_BasalValue) {
//		this.m_BasalValue = m_BasalValue;
//	}
//	
//	
//}