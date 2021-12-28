package control;

import java.io.IOException;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.CommonUtils;

public class ThreadMongoDBAlerterTreatments extends ThreadMongoDBAlerter 
{
	private static String m_What = new String("Treatments");

	@Override
	protected void checkDBForUpdates() throws IOException, ParseException 
	{
		String result = this.m_DataLoader.getLatestTreatmentsTimeAndWho();
		if (result != null)
		{
			String pattern = "(.*)( by )(.*)";
			Pattern r = Pattern.compile(pattern);
			Matcher m = r.matcher(result);
			if (m.find())
			{
				m_CurrentResultAt = CommonUtils.convertNSZDateString(m.group(1));
				m_CurrentResultBy = m.group(3);
			}
		}
	}

	@Override
	protected String whatIsChecked() 
	{
		return m_What;
	}

}
