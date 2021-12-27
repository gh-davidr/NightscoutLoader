package loader;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import control.MyLogger;
import control.PrefsNightScoutLoader;
import entity.DBResultEntry;
import mongo.NightscoutMongoDB;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.UnknownHostException;

public class DataLoadNightScoutEntries extends DataLoadNightScout 
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());
//	private static final int m_FailedTestLimit = 2;

//	private static Integer m_FailedTests = 0;

	private ArrayList <DBResultEntry> resultsFromDB;
	
	public DataLoadNightScoutEntries()
	{
		super();
		resultsFromDB = new ArrayList<DBResultEntry>();
	}
	
	static public Date getEntryLoadStartDate() 
	{
		int weeksBack = PrefsNightScoutLoader.getInstance().getM_WeeksBackToLoadEntries();
		Date now = new Date();
		long nowLong = now.getTime();
		long thenLong = nowLong - ((long)weeksBack * 7 * 24 * 3600 * 1000); // Arrghh - need to cast this weeks back else get garbage future date!
		Date result = new Date(thenLong);
	
		return result;
	}

	public void loadDBResults() throws UnknownHostException
	{
/*
 * 		int weeksBack = PrefsNightScoutLoader.getInstance().getM_WeeksBackToLoadEntries();
		Date now = new Date();
		long nowLong = now.getTime();
		long thenLong = nowLong - ((long)weeksBack * 7 * 24 * 3600 * 1000); // Arrghh - need to cast this weeks back else get garbage future date!
		Date startDate = new Date(thenLong);
		
//		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
//		String nowString = new String(df.format(now));
//		String startDatetring = new String(df.format(startDate));
		
		loadDBResults(startDate);
		*/
		
		loadDBResults(getEntryLoadStartDate());
	}

	public void loadDBResults(Date startDate) throws UnknownHostException
	{
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible)
		{
			String timeFld = new String("date");
			
			NightscoutMongoDB nsMongoDB = new NightscoutMongoDB();
			DBCollection coll = nsMongoDB.getSensorV2xCollection();
				
			resultsFromDB = new ArrayList<DBResultEntry>();

			BasicDBObject query = new BasicDBObject();
			// Load *all* results
			query.put(timeFld, BasicDBObjectBuilder.start("$gte", startDate.getTime()).get());

			m_Logger.log(Level.FINE, "loadDBResults Mongo Query is now " + query.toString());
			
			DBCursor cursor = coll.find(query);
			// Sort by time
			cursor.sort(new BasicDBObject(timeFld, 1));
			
			for (DBObject rs: cursor)
			{			
				// Now create Result objects for each document and store into array
				//ResultFromDB res = new ResultFromDB(rs);

				// Switch to new object type
				DBResultEntry res = new DBResultEntry(rs);

				resultsFromDB.add(res);

				m_Logger.log(Level.FINEST, "Result added for Nightscout Sensor Result " + rs.toString());
			}

			nsMongoDB.close();
		}

	}

	public ArrayList <DBResultEntry> getResultsFromDB()
	{
		return resultsFromDB;
	}


	@Override
	protected String getDevice() 
	{
		return new String("");  // Not used
	}

	@Override
	protected String getRequestType() 
	{
		return new String ("Data Load Entries ");
	}

}
