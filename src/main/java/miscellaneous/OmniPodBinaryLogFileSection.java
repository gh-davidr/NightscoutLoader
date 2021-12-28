package miscellaneous;

public class OmniPodBinaryLogFileSection 
{
	private int                      m_FileOffset;
	private OmniPodBinaryFileSection m_OmniPodBinaryFileSection;

	public OmniPodBinaryLogFileSection(int offset,
			OmniPodBinaryFileSection fileSection)
	{
		m_FileOffset = offset;
		m_OmniPodBinaryFileSection = fileSection;
	}

	/**
	 * @return the m_FileOffset
	 */
	public synchronized int getM_FileOffset() {
		return m_FileOffset;
	}

	/**
	 * @return the m_OmniPodBinaryFileSection
	 */
	public synchronized OmniPodBinaryFileSection getM_OmniPodBinaryLogFileSection() {
		return m_OmniPodBinaryFileSection;
	}
}
