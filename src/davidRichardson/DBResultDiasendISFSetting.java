package davidRichardson;

import org.apache.poi.hssf.usermodel.HSSFRow;

public class DBResultDiasendISFSetting extends DBResultDiasendPumpSetting
{
	DBResultDiasendISFSetting(HSSFRow row)
	{
		super(row);
	}

	/**
	 * @return the m_ISFValue
	 */
	public synchronized Double getM_ISFValue() {
		return getM_TimeBoundValue();
	}

	/**
	 * @param m_ISFValue the m_ISFValue to set
	 */
	public synchronized void setM_ISFValue(Double m_ISFValue) {
		setM_TimeBoundValue(m_ISFValue);
	}
}
