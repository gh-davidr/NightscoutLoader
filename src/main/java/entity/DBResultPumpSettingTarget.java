package entity;

import org.apache.poi.hssf.usermodel.HSSFRow;

public class DBResultPumpSettingTarget extends DBResultPumpSetting
{
	private String m_BGUnits;
	private Double m_TargetLowValue;
	private Double m_TargetHighValue;
	
	public DBResultPumpSettingTarget(HSSFRow row, String bgUnits)
	{
		super(row);
		
		m_BGUnits = new String(bgUnits);
	}

	/**
	 * @return the m_BGUnits
	 */
	public synchronized String getM_BGUnits() {
		return m_BGUnits;
	}

	/**
	 * @param m_BGUnits the m_BGUnits to set
	 */
	public synchronized void setM_BGUnits(String m_BGUnits) {
		this.m_BGUnits = m_BGUnits;
	}

	/**
	 * @return the m_TargetLowValue
	 */
	public synchronized Double getM_TargetLowValue() {
		return m_TargetLowValue;
	}

	/**
	 * @param m_TargetLowValue the m_TargetLowValue to set
	 */
	public synchronized void setM_TargetLowValue(Double m_TargetLowValue) {
		this.m_TargetLowValue = m_TargetLowValue;
	}

	/**
	 * @return the m_TargetHighValue
	 */
	public synchronized Double getM_TargetHighValue() {
		return m_TargetHighValue;
	}

	/**
	 * @param m_TargetHighValue the m_TargetHighValue to set
	 */
	public synchronized void setM_TargetHighValue(Double m_TargetHighValue) {
		this.m_TargetHighValue = m_TargetHighValue;
	}
	
}
