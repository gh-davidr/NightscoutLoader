package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestCommonUtils {


	/*
	 * 
	 * List for testing copied from utils.CommonUtils class
	 * 
	 * 
			{"^([0-9]){4,4}-([0-9]){2,2}-([0-9]){2,2}T([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}.([0-9]){3,3}Z$",  "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"},
			{"^([0-9]){4,4}-([0-9]){2,2}-([0-9]){2,2}T([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}.([0-9]){2,2}Z$",  "yyyy-MM-dd'T'HH:mm:ss.SS'Z'"},
			{"^([0-9]){4,4}-([0-9]){2,2}-([0-9]){2,2}T([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}.([0-9]){1,1}Z$",  "yyyy-MM-dd'T'HH:mm:ss.S'Z'"},

			{"^([0-9]){4,4}-([a-zA-Z]){3,3}-([0-9]){2,2} ([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",             "yyyy-MMM-dd HH:mm:ss"},
			{"^([0-9]){4,4}-([a-zA-Z]){3,3}-([0-9]){2,2} ([0-9]){2,2}:([0-9]){2,2}$",                          "yyyy-MMM-dd HH:mm"},
			{"^([0-9]){4,4}-([0-9]){2,2}-([0-9]){2,2} ([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",                "yyyy-MM-dd HH:mm:ss"},
			{"^([0-9]){4,4}-([0-9]){2,2}-([0-9]){2,2} ([0-9]){2,2}:([0-9]){2,2}$",                             "yyyy-MM-dd HH:mm"},

			{"^([0-9]){2,2}-([a-zA-Z]){3,3}-([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",             "dd-MMM-yyyy HH:mm:ss"},
			{"^([0-9]){2,2}-([a-zA-Z]){3,3}-([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}$",                          "dd-MMM-yyyy HH:mm"},
			{"^([0-9]){2,2}-([0-9]){2,2}-([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",                "dd-MM-yyyy HH:mm:ss"},
			{"^([0-9]){2,2}-([0-9]){2,2}-([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}$",                             "dd-MM-yyyy HH:mm"},

			{"^([0-9]){4,4}/([a-zA-Z]){3,3}/([0-9]){2,2}T([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",             "yyyy/MMM/dd HH:mm:ss"},
			{"^([0-9]){4,4}/([a-zA-Z]){3,3}/([0-9]){2,2}T([0-9]){2,2}:([0-9]){2,2}$",                          "yyyy/MMM/dd HH:mm"},
			{"^([0-9]){4,4}/([0-9]){2,2}/([0-9]){2,2}T([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",                "yyyy/MM/dd HH:mm:ss"},
			{"^([0-9]){4,4}/([0-9]){2,2}/([0-9]){2,2}T([0-9]){2,2}:([0-9]){2,2}$",                             "yyyy/MM/dd HH:mm"},

			{"^([0-9]){2,2}/([a-zA-Z]){3,3}/([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",             "dd/MMM/yyyy HH:mm:ss"},
			{"^([0-9]){2,2}/([a-zA-Z]){3,3}/([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}$",                          "dd/MMM/yyyy HH:mm"},
			{"^([0-9]){2,2}/([0-9]){2,2}/([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",                "dd/MM/yyyy HH:mm:ss"},
			{"^([0-9]){2,2}/([0-9]){2,2}/([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}$",                             "dd/MM/yyyy HH:mm"},

			// CellNovo
			{"^([a-zA-Z]){6,9} ([0-9]){2,2} ([a-zA-Z]){3,3} ([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}",            "EEEE dd MMM yyyy HH:mm"},

	 * 
	 */
	private static Object[][] TESTED_DATES = 
		{
				// {"^([0-9]){4,4}-([0-9]){2,2}-([0-9]){2,2}T([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}.([0-9]){3,3}Z$",  "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"},
				{"2021-12-13T12:23:34.123Z",   // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					34,                        // Second
					123						   // Milli-seconds
				},
				// {"^([0-9]){4,4}-([0-9]){2,2}-([0-9]){2,2}T([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}.([0-9]){2,2}Z$",  "yyyy-MM-dd'T'HH:mm:ss.SS'Z'"},
				{"2021-12-13T12:23:34.12Z",    // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					34,                        // Second
					120						   // Milli-seconds
				},
				// {"^([0-9]){4,4}-([0-9]){2,2}-([0-9]){2,2}T([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}.([0-9]){2,2}Z$",  "yyyy-MM-dd'T'HH:mm:ss.S'Z'"},
				{"2021-12-13T12:23:34.1Z",    // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					34,                        // Second
					100						   // Milli-seconds
				},


				// {"^([0-9]){4,4}-([a-zA-Z]){3,3}-([0-9]){2,2} ([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",             "yyyy-MMM-dd HH:mm:ss"},
				{"2021-Dec-13 12:23:34",       // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					34,                        // Second
					0  					       // Milli-seconds
				},
				// {"^([0-9]){4,4}-([a-zA-Z]){3,3}-([0-9]){2,2} ([0-9]){2,2}:([0-9]){2,2}$",                          "yyyy-MMM-dd HH:mm"},
				{"13-Dec-2021 12:23:34",        // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					34,                        // Second
					0						   // Milli-seconds
				},


				// {"^([0-9]){4,4}-([0-9]){2,2}-([0-9]){2,2} ([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",                "yyyy-MM-dd HH:mm:ss"},
				{"2021-12-13 12:23:34",       // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					34,                        // Second
					0  					       // Milli-seconds
				},
				// {"^([0-9]){2,2}-([a-zA-Z]){3,3}-([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",             "dd-MMM-yyyy HH:mm:ss"},
				{"13-12-2021 12:23:34",        // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					34,                        // Second
					0						   // Milli-seconds
				},


				// {"^([0-9]){2,2}-([a-zA-Z]){3,3}-([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",             "dd-MMM-yyyy HH:mm:ss"},
				{"13-Dec-2021 12:23:34",       // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					34,                        // Second
					0						   // Milli-seconds
				},
				// {"^([0-9]){2,2}-([a-zA-Z]){3,3}-([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}$",                          "dd-MMM-yyyy HH:mm"},
				{"13-Dec-2021 12:23",          // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					0,                         // Second
					0						   // Milli-seconds
				},

				// {"^([0-9]){2,2}-([0-9]){2,2}-([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",                "dd-MM-yyyy HH:mm:ss"},
				{"13-12-2021 12:23:34",        // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					34,                        // Second
					0						   // Milli-seconds
				},
				// {"^([0-9]){2,2}-([0-9]){2,2}-([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}$",                             "dd-MM-yyyy HH:mm"},
				{"13-12-2021 12:23",           // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					0,                         // Second
					0						   // Milli-seconds
				},

				// {"^([0-9]){4,4}/([a-zA-Z]){3,3}/([0-9]){2,2} ([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",             "yyyy/MMM/dd HH:mm:ss"},
				{"2021/Dec/13 12:23:34",       // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					34,                        // Second
					0						   // Milli-seconds
				},
				// {"^([0-9]){4,4}/([a-zA-Z]){3,3}/([0-9]){2,2} ([0-9]){2,2}:([0-9]){2,2}$",                          "yyyy/MMM/dd HH:mm"},
				{"2021/Dec/13 12:23",          // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					0,                         // Second
					0						   // Milli-seconds
				},

				// {"^([0-9]){4,4}/([0-9]){2,2}/([0-9]){2,2}T([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",                "yyyy/MM/dd HH:mm:ss"},
				{"2021/12/13 12:23:34",        // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					34,                        // Second
					0						   // Milli-seconds
				},
				// {"^([0-9]){4,4}/([0-9]){2,2}/([0-9]){2,2}T([0-9]){2,2}:([0-9]){2,2}$",                             "yyyy/MM/dd HH:mm"},
				{"2021/12/13 12:23",           // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					0,                         // Second
					0						   // Milli-seconds
				},

				// {"^([0-9]){2,2}/([a-zA-Z]){3,3}/([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",             "dd/MMM/yyyy HH:mm:ss"},
				{"13/Dec/2021 12:23:34",       // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					34,                        // Second
					0						   // Milli-seconds
				},
				// {"^([0-9]){2,2}/([a-zA-Z]){3,3}/([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}$",                          "dd/MMM/yyyy HH:mm"},
				{"13/Dec/2021 12:23",          // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					0,                         // Second
					0						   // Milli-seconds
				},

				// {"^([0-9]){2,2}/([0-9]){2,2}/([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}:([0-9]){2,2}$",                "dd/MM/yyyy HH:mm:ss"},
				{"13/12/2021 12:23:34",        // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					34,                        // Second
					0						   // Milli-seconds
				},
				// {"^([0-9]){2,2}/([0-9]){2,2}/([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}$",                             "dd/MM/yyyy HH:mm"},
				{"13/12/2021 12:23",           // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					0,                         // Second
					0						   // Milli-seconds
				},


				// {"^([a-zA-Z]){6,9} ([0-9]){2,2} ([a-zA-Z]){3,3} ([0-9]){4,4} ([0-9]){2,2}:([0-9]){2,2}",            "EEEE dd MMM yyyy HH:mm"},
				{"Monday 13 Dec 2021 12:23",   // Date String
					2021,                      // Year
					12,                        // Month
					13,                        // Date
					12,                        // Hour
					23,                        // Minute
					00,                        // Second
					000						   // Milli-seconds
				},


		};

	@Test
	public void performanceTests()
	{
		int numTests = 1000000;

		Date hashDateStart = new Date();
		Date hashDateEnd = multipleHashDateConversionDuration((String)TESTED_DATES[0][0], numTests);
		long hashDateDur = hashDateEnd.getTime() - hashDateStart.getTime();

		Date simpleDateStart = new Date();
		Date simpleDateEnd = multipleDateConversionDuration("13-12-2021 12:23:34", "dd-MM-yyyy HH:mm:ss", numTests);
		long simpleDateDur = simpleDateEnd.getTime() - simpleDateStart.getTime();

		System.out.println(numTests + " repetitive HASH Date tests took " + hashDateDur + " milliseconds.");
		System.out.println(numTests + " repetitive Simple Date tests took " + simpleDateDur + " milliseconds.");
	}

	private Date multipleHashDateConversionDuration(String dateString, int numTests)
	{
		for (int i = 0; i < numTests; i++)
		{
			try {
				CommonUtils.convertDateString(dateString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return new Date();
	}

	private Date multipleDateConversionDuration(String dateString, String format, int numTests)
	{
		for (int i = 0; i < numTests; i++)
		{
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(format);
				formatter.parse(dateString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return new Date();
	}

	@Test
	public void testDates()
	{
		for (int c = 0; c < TESTED_DATES.length; c++)
		{
			Object[] dateObject = TESTED_DATES[c];
			int i = 0;

			String dateString = (String)dateObject[i++];
			Integer year      = (Integer)dateObject[i++];
			Integer month     = (Integer)dateObject[i++];
			Integer date      = (Integer)dateObject[i++];
			Integer hour      = (Integer)dateObject[i++];
			Integer minute    = (Integer)dateObject[i++];
			Integer second    = (Integer)dateObject[i++];
			Integer millSec   = (Integer)dateObject[i++];


			Date d = null;
			try {
				d = CommonUtils.convertDateString(dateString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			checkValue("Year", dateString, year, CommonUtils.getYearFromDate(d));
			checkValue("Month", dateString, month, CommonUtils.getMonthFromDate(d));
			checkValue("Date", dateString, date, CommonUtils.getDateFromDate(d));
			checkValue("Hour", dateString, hour, CommonUtils.getHourFromDate(d));
			checkValue("Minute", dateString, minute, CommonUtils.getMinuteFromDate(d));
			checkValue("Second", dateString, second, CommonUtils.getSecondFromDate(d));
			checkValue("Milli-Second", dateString, millSec, CommonUtils.getMilliSecondFromDate(d));


			Assertions.assertEquals(minute, CommonUtils.getMinuteFromDate(d), 
					"Expect Minutes to match.  Date '" + dateString + "' "
							+ " Expect " + minute
							+ " Got " + CommonUtils.getMinuteFromDate(d)
					);
		}

	}

	private void checkValue(String what, String dateString, Integer expected, Integer received)
	{
		Assertions.assertEquals(expected, received, 
				"Expect " + what + " to match.  Date '" + dateString + "' "
						+ " Expect " + expected
						+ " Received " + received
				);
	}

}
