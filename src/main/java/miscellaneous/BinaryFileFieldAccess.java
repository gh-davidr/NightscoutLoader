package miscellaneous;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BinaryFileFieldAccess 
{
//	private byte[] m_RawData = null;		

	BinaryFileFieldAccess(byte[] rawdata)
	{
//		m_RawData = rawdata;
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


