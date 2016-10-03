package davidRichardson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFormattedTextField.AbstractFormatter;

// Used to format dates in JDatePicker
public class DateLabelFormatter extends AbstractFormatter 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String datePattern = "dd-MMM-yyyy";
	private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

	@Override
	public Object stringToValue(String text) throws ParseException {
		return dateFormatter.parseObject(text);
	}

	@Override
	public String valueToString(Object value) throws ParseException {
		if (value != null) 
		{
			//			Date dateVal = (Date)value;
			//			return dateFormatter.format(dateVal.getTime());

			if (value.getClass().getName().equals("java.util.GregorianCalendar"))
			{
				// David - 30 Aug 2016
				Calendar cal = (Calendar)value;  // Get exception about casting a Date to Calendar
				// Seems to happen on analyzer when setting days back, clicking off then running analyze
				return dateFormatter.format(cal.getTime());
			}
			else if (value.getClass().getName().equals("java.util.Date"))
			{
				// David - 30 Aug 2016
				Date cal = (Date)value;  // Get exception about casting a Date to Calendar
				// Seems to happen on analyzer when setting days back, clicking off then running analyze
				return dateFormatter.format(cal.getTime());
			}
			else
			{
				System.out.println("Unexpected Class type here " + value.getClass().getName());
			}
		}

		return "";
	}

}
