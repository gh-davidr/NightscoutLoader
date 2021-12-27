package utils;

import java.util.regex.Pattern;

public class DatePatternFormat
{
	private String  m_PatternString = null;
	private String  m_FormatString = null;
	private Pattern m_Pattern = null;
	
	public DatePatternFormat(String m_PatternString, String m_FormatString) {
		super();
		this.m_PatternString = m_PatternString;
		this.m_FormatString = m_FormatString;
		
		m_Pattern = Pattern.compile(m_PatternString);
	}

	/**
	 * @return the m_PatternString
	 */
	public synchronized String getM_PatternString() {
		return m_PatternString;
	}

	/**
	 * @return the m_FormatString
	 */
	public synchronized String getM_FormatString() {
		return m_FormatString;
	}

	/**
	 * @return the m_Pattern
	 */
	public synchronized Pattern getM_Pattern() {
		return m_Pattern;
	}
}