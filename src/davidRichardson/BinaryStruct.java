package davidRichardson;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BinaryStruct {

	enum VarType
	{
		vt_short,
		vt_signed_short,
		vt_int,
		vt_signed_int,
		vt_byte,
		vt_signed_byte,
		vt_float,
		vt_BE_short,
		vt_BE_signed_short,
		vt_BE_float,
		vt_BE_int,
		vt_BE_signed_int,
		vt_ZString,
		vt_String,
	};

	private static FieldType[] m_FieldType   = new FieldType[15];
	private static boolean     m_Initialized = false;

	private class FieldType
	{
		private char    m_fieldType;
		private int     m_length;
		private VarType m_VarType;
		
		FieldType(char f, int l, VarType v)
		{
			m_fieldType = f;
			m_length    = l;
			m_VarType   = v;
		}

		/**
		 * @return the m_fieldType
		 */
		public synchronized char getM_fieldType() {
			return m_fieldType;
		}

		/**
		 * @param m_fieldType the m_fieldType to set
		 */
		public synchronized void setM_fieldType(char m_fieldType) {
			this.m_fieldType = m_fieldType;
		}

		/**
		 * @return the m_length
		 */
		public synchronized int getM_length() {
			return m_length;
		}

		/**
		 * @param m_length the m_length to set
		 */
		public synchronized void setM_length(int m_length) {
			this.m_length = m_length;
		}

		/**
		 * @return the m_VarType
		 */
		public synchronized VarType getM_VarType() {
			return m_VarType;
		}

		/**
		 * @param m_VarType the m_VarType to set
		 */
		public synchronized void setM_VarType(VarType m_VarType) {
			this.m_VarType = m_VarType;
		}
	}


	public class FieldAccess 
	{
		private byte[] m_RawData = null;		

		FieldAccess(byte[] rawdata)
		{
			m_RawData = rawdata;
		}

		public int extractInt(byte[] rawdata, int offset)
		{
			int result =  ByteBuffer.wrap(rawdata,offset,4).order(ByteOrder.LITTLE_ENDIAN).getInt();
			return result;
		}
		public int extractSignedInt(byte[] rawdata, int offset)
		{
			int result =  extractInt(rawdata, offset);
			return result;
		}

		public short extractShort(byte[] rawdata, int offset)
		{
			short result =  ByteBuffer.wrap(rawdata,offset,4).order(ByteOrder.LITTLE_ENDIAN).getShort();
			return result;
		}
		public short extractSignedShort(byte[] rawdata, int offset)
		{
			short result = extractShort(rawdata, offset);
			return result;
		}

		public byte extractbyte(byte[] rawdata, int offset)
		{
			return rawdata[offset];
		}

		public byte extractSignedbyte(byte[] rawdata, int offset)
		{
			byte result = extractbyte(rawdata, offset);

			result = (byte) ((result & 127) - (result & 128));
			return result;
		}

		public String  extractbytes(byte[] rawdata, int offset, int len)
		{
			String result = new String();
			for (int c = offset; c < offset + len; c++)
			{
				result += rawdata[c];	
			}
						
			return result;
		}
		
		public float extractFloat(byte[] rawdata, int offset)
		{
			float result =  ByteBuffer.wrap(rawdata,offset,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
			return result;
		}
		
		public int extractBEInt(byte[] rawdata, int offset)
		{
			int result =  ByteBuffer.wrap(rawdata,offset,4).order(ByteOrder.BIG_ENDIAN).getInt();
			return result;	
		}
		
		public int extractBESignedInt(byte[] rawdata, int offset)
		{
			int result =  extractBEInt(rawdata, offset);
			return result;
		}
		
		public short extractBEShort(byte[] rawdata, int offset)
		{
			short result =  ByteBuffer.wrap(rawdata,offset,4).order(ByteOrder.BIG_ENDIAN).getShort();
			return result;
		}
		
		public short extractBESignedShort(byte[] rawdata, int offset)
		{
			short result = extractBEShort(rawdata, offset);
			return result;
		}
		
		public String  extractZString(byte[] rawdata, int offset, int len)
		{
			 String result = extractbytes(rawdata, offset, len);
			 return result;
		}
		public String  extractString(byte[] rawdata, int offset, int len)
		{
			 String result = extractbytes(rawdata, offset, len);
			 return result;
		}
	}
	
	BinaryStruct()
	{
		if (m_Initialized == false)
		{
			m_Initialized = true;
			
			int i = 0;
			
			m_FieldType[i++] = new FieldType('i', 4, VarType.vt_int);
			m_FieldType[i++] = new FieldType('n', 4, VarType.vt_signed_int);
			m_FieldType[i++] = new FieldType('s', 2, VarType.vt_short);
			m_FieldType[i++] = new FieldType('h', 2, VarType.vt_signed_short);
			m_FieldType[i++] = new FieldType('b', 1, VarType.vt_byte);
			m_FieldType[i++] = new FieldType('y', 1, VarType.vt_signed_byte);
			m_FieldType[i++] = new FieldType('B', 0, VarType.vt_String); // was bytes ...
			m_FieldType[i++] = new FieldType('f', 4, VarType.vt_float);
			m_FieldType[i++] = new FieldType('F', 4, VarType.vt_BE_float);
			m_FieldType[i++] = new FieldType('I', 4, VarType.vt_BE_int);
			m_FieldType[i++] = new FieldType('N', 4, VarType.vt_BE_signed_int);
			m_FieldType[i++] = new FieldType('S', 2, VarType.vt_BE_short);
			m_FieldType[i++] = new FieldType('H', 2, VarType.vt_BE_signed_short);
			m_FieldType[i++] = new FieldType('z', 0, VarType.vt_ZString);
			m_FieldType[i++] = new FieldType('Z', 0, VarType.vt_String);

		}
	}
}


