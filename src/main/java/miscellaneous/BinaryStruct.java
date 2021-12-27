package miscellaneous;
//package org.DavidRichardson.NightscoutLoaderMaven;
//
//import java.util.ArrayList;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class BinaryStruct 
//{
//
//	enum VarType
//	{
//		vt_Unknown,
//		
//		vt_short,
//		vt_signed_short,
//		vt_int,
//		vt_signed_int,
//		vt_byte,
//		vt_signed_byte,
//		vt_float,
//		vt_BE_short,
//		vt_BE_signed_short,
//		vt_BE_float,
//		vt_BE_int,
//		vt_BE_signed_int,
//		vt_ZString,
//		vt_String,
//	};
//
//	private static FieldType[] m_FieldType   = new FieldType[15];
//	private static boolean     m_Initialized = false;
//	
//	private int                m_Count = 0;
//	private String             m_Expression = "";
//
//	private class FieldType
//	{
//		private char    m_fieldType;
//		private int     m_length;
//		private VarType m_VarType;
//		
//		FieldType(char f, int l, VarType v)
//		{
//			m_fieldType = f;
//			m_length    = l;
//			m_VarType   = v;
//		}
//
//		/**
//		 * @return the m_fieldType
//		 */
//		public synchronized char getM_fieldType() {
//			return m_fieldType;
//		}
//
//		/**
//		 * @param m_fieldType the m_fieldType to set
//		 */
//		public synchronized void setM_fieldType(char m_fieldType) {
//			this.m_fieldType = m_fieldType;
//		}
//
//		/**
//		 * @return the m_length
//		 */
//		public synchronized int getM_length() {
//			return m_length;
//		}
//
//		/**
//		 * @param m_length the m_length to set
//		 */
//		public synchronized void setM_length(int m_length) {
//			this.m_length = m_length;
//		}
//
//		/**
//		 * @return the m_VarType
//		 */
//		public synchronized VarType getM_VarType() {
//			return m_VarType;
//		}
//
//		/**
//		 * @param m_VarType the m_VarType to set
//		 */
//		public synchronized void setM_VarType(VarType m_VarType) {
//			this.m_VarType = m_VarType;
//		}
//	}
//
//	// See parseformat in struct.js from tidepool on which this is modeled
//	public static ArrayList<BinaryStruct> parseFormat(String format)
//	{
//		ArrayList<BinaryStruct> result = new ArrayList<BinaryStruct>();
//		
//		int count = 0;
//		String exp = new String("");
//		
//		Pattern formatPattern = Pattern.compile("(([0-9]*)([a-zA-Z.]))+");
//		Matcher formatMatcher = formatPattern.matcher(format);
//
//		while (formatMatcher.find())
//		{
//			count = Integer.parseInt(formatMatcher.group(1));
//			exp   = formatMatcher.group(2);
//			BinaryStruct entry = new BinaryStruct(count, exp);
//			result.add(entry);
//		}
//
//		return result;
//	}
//	
//	public BinaryStruct(int count, String exp)
//	{
//		initialize();
//		m_Count      = count;
//		m_Expression = new String(exp);
//	}
//	
//	public BinaryStruct()
//	{
//		initialize();
//	}
//	
//	private void initialize()
//	{
//		if (m_Initialized == false)
//		{
//			m_Initialized = true;
//			
//			int i = 0;
//			
//			m_FieldType[i++] = new FieldType('i', 4, VarType.vt_int);
//			m_FieldType[i++] = new FieldType('n', 4, VarType.vt_signed_int);
//			m_FieldType[i++] = new FieldType('s', 2, VarType.vt_short);
//			m_FieldType[i++] = new FieldType('h', 2, VarType.vt_signed_short);
//			m_FieldType[i++] = new FieldType('b', 1, VarType.vt_byte);
//			m_FieldType[i++] = new FieldType('y', 1, VarType.vt_signed_byte);
//			m_FieldType[i++] = new FieldType('B', 0, VarType.vt_String); // was bytes ...
//			m_FieldType[i++] = new FieldType('f', 4, VarType.vt_float);
//			m_FieldType[i++] = new FieldType('F', 4, VarType.vt_BE_float);
//			m_FieldType[i++] = new FieldType('I', 4, VarType.vt_BE_int);
//			m_FieldType[i++] = new FieldType('N', 4, VarType.vt_BE_signed_int);
//			m_FieldType[i++] = new FieldType('S', 2, VarType.vt_BE_short);
//			m_FieldType[i++] = new FieldType('H', 2, VarType.vt_BE_signed_short);
//			m_FieldType[i++] = new FieldType('z', 0, VarType.vt_ZString);
//			m_FieldType[i++] = new FieldType('Z', 0, VarType.vt_String);
//
//		}
//	}
//}
//
//
