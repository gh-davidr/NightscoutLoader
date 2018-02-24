package davidRichardson;

import org.apache.poi.hssf.usermodel.HSSFRow;

import com.mongodb.BasicDBObject;

public class DBResultDiasendISFSetting extends DBResultDiasendPumpSetting
{
	private String m_BGUnits;
	
	DBResultDiasendISFSetting(HSSFRow row, String bgUnits)
	{
		super(row);
		
		m_BGUnits = new String(bgUnits);
	}

	DBResultDiasendISFSetting(BasicDBObject rs, String bgUnits)
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
}
