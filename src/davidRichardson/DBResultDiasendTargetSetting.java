package davidRichardson;

import org.apache.poi.hssf.usermodel.HSSFRow;

import com.mongodb.BasicDBObject;

public class DBResultDiasendTargetSetting extends DBResultDiasendPumpSetting
{	
	DBResultDiasendTargetSetting(HSSFRow row, String bgUnits)
	{
		super(row);
	}

	DBResultDiasendTargetSetting(BasicDBObject rs)
	{
		super(rs);
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
