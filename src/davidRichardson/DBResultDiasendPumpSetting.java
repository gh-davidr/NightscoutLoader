package davidRichardson;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

public class DBResultDiasendPumpSetting 
{
	private int    m_Index;
	private String m_Time;
	private Double m_TimeBoundValue;
	
	private final int m_SettingsFields = 3;

	// Utility Function for getting a cell from row as string even if numeric
	public static String getCellAsString(HSSFRow row, int index)
	{
		String result;
		// Check type of this cell first
		if (row.getCell(index).getCellType() == HSSFCell.CELL_TYPE_STRING)
		{
			result = row.getCell(index).getStringCellValue();
		}
		else
		{
			Double result_num = row.getCell(index).getNumericCellValue();
			result = result_num.toString();
		}
		return result;
	}
	
	DBResultDiasendPumpSetting(HSSFRow row)
	{
		int maxColumns = row.getPhysicalNumberOfCells();
		if (maxColumns == m_SettingsFields)
		{			
			Double cell1 = row.getCell(0).getNumericCellValue();
			String cell2 = row.getCell(1).getStringCellValue();
			Double cell3 = row.getCell(2).getNumericCellValue();

			m_Index      = cell1.intValue();
			m_Time       = cell2;
			m_TimeBoundValue = cell3;
		}
	}
	
	public String toString()
	{
		return String.format("Index: %d, Time: %s, Value: %g",
				m_Index, m_Time, m_TimeBoundValue);  			
	}

	/**
	 * @return the m_Index
	 */
	public synchronized int getM_Index() {
		return m_Index;
	}

	/**
	 * @param m_Index the m_Index to set
	 */
	public synchronized void setM_Index(int m_Index) {
		this.m_Index = m_Index;
	}

	/**
	 * @return the m_Time
	 */
	public synchronized String getM_Time() {
		return m_Time;
	}

	/**
	 * @param m_Time the m_Time to set
	 */
	public synchronized void setM_Time(String m_Time) {
		this.m_Time = m_Time;
	}

	/**
	 * @return the m_TimeBoundValue
	 */
	public synchronized Double getM_TimeBoundValue() {
		return m_TimeBoundValue;
	}

	/**
	 * @param m_TimeBoundValue the m_TimeBoundValue to set
	 */
	public synchronized void setM_TimeBoundValue(Double m_TimeBoundValue) {
		this.m_TimeBoundValue = m_TimeBoundValue;
	}
	
	
}
