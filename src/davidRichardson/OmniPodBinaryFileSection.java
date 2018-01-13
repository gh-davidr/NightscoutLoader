package davidRichardson;

import java.util.ArrayList;

public class OmniPodBinaryFileSection 
{
	protected String            m_SectionHeader;
	protected String            m_Encoding;
	protected ArrayList<String> m_FieldNames;
	protected ArrayList<String> m_FieldValues;
	protected OmniPodValidator  m_Validator    = null;

	OmniPodBinaryFileSection(String sectionHeader, String encoding, String[] fieldNames)
	{
		m_SectionHeader = new String(sectionHeader);
		m_Encoding      = new String(encoding);
		m_FieldNames    = new ArrayList<String>();
		m_FieldValues   = new ArrayList<String>();

		if (fieldNames != null)
		{
			for (String f : fieldNames)
			{
				m_FieldNames.add(f);
			}
		}
	}

	OmniPodBinaryFileSection(String sectionHeader, String encoding, 
			String[] fieldNames, 
			OmniPodValidator validator)
	{
		m_SectionHeader = new String(sectionHeader);
		m_Encoding      = new String(encoding);
		m_FieldNames    = new ArrayList<String>();
		m_FieldValues   = new ArrayList<String>();

		for (String f : fieldNames)
		{
			m_FieldNames.add(f);
		}
		m_Validator = validator;
	}

	OmniPodBinaryFileSection(String sectionHeader, String encoding, 
			String[] fieldNames, String[] fieldValues)
	{
		m_SectionHeader = new String(sectionHeader);
		m_Encoding      = new String(encoding);
		m_FieldNames    = new ArrayList<String>();
		m_FieldValues   = new ArrayList<String>();

		for (String f : fieldNames)
		{
			m_FieldNames.add(f);
		}
		for (String f : fieldValues)
		{
			m_FieldValues.add(f);
		}
	}

	OmniPodBinaryFileSection(String sectionHeader, String encoding, 
			String[] fieldNames, String[] fieldValues,
			OmniPodValidator validator)
	{
		m_SectionHeader = new String(sectionHeader);
		m_Encoding      = new String(encoding);
		m_FieldNames    = new ArrayList<String>();
		m_FieldValues   = new ArrayList<String>();

		for (String f : fieldNames)
		{
			m_FieldNames.add(f);
		}
		for (String f : fieldValues)
		{
			m_FieldValues.add(f);
		}
		m_Validator = validator;
	}

	/**
	 * @return the m_SectionHeader
	 */
	public synchronized String getM_SectionHeader() {
		return m_SectionHeader;
	}

	/**
	 * @param m_SectionHeader the m_SectionHeader to set
	 */
	public synchronized void setM_SectionHeader(String m_SectionHeader) {
		this.m_SectionHeader = m_SectionHeader;
	}

	/**
	 * @return the m_Encoding
	 */
	public synchronized String getM_Encoding() {
		return m_Encoding;
	}

	/**
	 * @param m_Encoding the m_Encoding to set
	 */
	public synchronized void setM_Encoding(String m_Encoding) {
		this.m_Encoding = m_Encoding;
	}

}
