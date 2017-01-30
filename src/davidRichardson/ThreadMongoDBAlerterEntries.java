package davidRichardson;

import java.io.IOException;
import java.text.ParseException;

public class ThreadMongoDBAlerterEntries extends ThreadMongoDBAlerter 
{
	private static String m_What = new String("Entries");

	@Override
	protected void checkDBForUpdates() throws IOException, ParseException 
	{
		String result = this.m_DataLoader.getLatestEntriesTime();

		if (result != null)
		{
			m_CurrentResultAt = CommonUtils.convertNSZDateString(result);
		}
	}

	@Override
	protected String whatIsChecked() 
	{
		return m_What;
	}

}
