package entity;

import org.apache.poi.hssf.usermodel.HSSFRow;

public class DBResultPumpSettingCarbRatio extends DBResultPumpSetting
{
	public DBResultPumpSettingCarbRatio(HSSFRow row)
	{
		super(row);
	}

	/**
	 * @return the m_CarbRatioValue
	 */
	public synchronized Double getM_CarbRatioValue() {
		return getM_TimeBoundValue();
	}

	/**
	 * @param m_CarbRatioValue the m_CarbRatioValue to set
	 */
	public synchronized void setM_CarbRatioValue(Double m_CarbRatioValue) {
		setM_TimeBoundValue(m_CarbRatioValue);
	}
}
