package miscellaneous;

public abstract class OmniPodValidator 
{
//	private OmniPodBinaryFileSection m_OmniPodBinaryFileSection;
	
	public class OmniPodDetails
	{
		
	};
	
	// --------------------------------
	// Class for handling Bolus details
	// --------------------------------
	public class OmniPodDetailsBolus extends OmniPodDetails
	{
		private int m_VolumeUnits           = 0;
		private int m_ExtendedDurationMSec  = 0;
		private int m_ImmediateDurationMSec = 0;
		/**
		 * @return the m_VolumeUnits
		 */
		public synchronized int getM_VolumeUnits() {
			return m_VolumeUnits;
		}
		/**
		 * @param m_VolumeUnits the m_VolumeUnits to set
		 */
		public synchronized void setM_VolumeUnits(int m_VolumeUnits) {
			this.m_VolumeUnits = m_VolumeUnits;
		}
		/**
		 * @return the m_ExtendedDurationMSec
		 */
		public synchronized int getM_ExtendedDurationMSec() {
			return m_ExtendedDurationMSec;
		}
		/**
		 * @param m_ExtendedDurationMSec the m_ExtendedDurationMSec to set
		 */
		public synchronized void setM_ExtendedDurationMSec(int m_ExtendedDurationMSec) {
			this.m_ExtendedDurationMSec = m_ExtendedDurationMSec;
		}
		/**
		 * @return the m_ImmediateDurationMSec
		 */
		public synchronized int getM_ImmediateDurationMSec() {
			return m_ImmediateDurationMSec;
		}
		/**
		 * @param m_ImmediateDurationMSec the m_ImmediateDurationMSec to set
		 */
		public synchronized void setM_ImmediateDurationMSec(int m_ImmediateDurationMSec) {
			this.m_ImmediateDurationMSec = m_ImmediateDurationMSec;
		}
	};
	
	public abstract boolean         valid();
	public abstract OmniPodDetails  getDetails();
}
