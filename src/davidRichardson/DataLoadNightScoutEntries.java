package davidRichardson;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.UnknownHostException;

public class DataLoadNightScoutEntries extends DataLoadNightScout 
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());
//	private static final int m_FailedTestLimit = 2;

//	private static Integer m_FailedTests = 0;

	private ArrayList <DBResultEntry> resultsFromDB;
	private MongoDBServerStateEnum m_ServerState = MongoDBServerStateEnum.unknown;

	enum MongoDBServerStateEnum
	{
		unknown,
		accessible,
		not_accessible,
	};
	
	public DataLoadNightScoutEntries()
	{
		super();
		resultsFromDB = new ArrayList<DBResultEntry>();
	}

	// Check DB connection for where we only have server name
	public String testDBConnection(String mongoHost)  throws UnknownHostException
	{
		synchronized (mongoHost) {
			String result = new String();
			MongoClient dbClient;
			dbClient = new MongoClient(mongoHost);
			result = "Connected to " + mongoHost + " now listing available databases.\n\n";
			for (String s : dbClient.getDatabaseNames()) {
				result += "  " + s + "\n";
			}
			dbClient.close();
			result += "\n\nSUCCESS\n";
			m_ServerState = MongoDBServerStateEnum.accessible;
			return result;
		}
	}


	// Check DB connection for where we have server name & db name
	public String testDBConnection(String mongoHost, String mongoDB)  throws UnknownHostException
	{
		String result = new String();

		MongoClient dbClient;
		MongoClientURI dbURI;

		if (mongoHost.contains("@"))
		{
			// Create full URI with DB too.  This is straight from the https://mongolab.com/databases/dexcom_db page
			//			dbURI    = new MongoClientURI(mongoHost + ":" + mongoPort + "/" + mongoDB);

			// Left like below after a few days not using server but not working clearly :-(
			//dbURI    = new MongoClientURI(mongoHost + ":" + "/" + mongoDB);

			dbURI    = new MongoClientURI(mongoHost + "/" + mongoDB);
			dbClient = new MongoClient(dbURI);
		}
		else
		{
			dbClient = new MongoClient(mongoHost);
		}

		DB db = dbClient.getDB(mongoDB);

		result += "\nNow listing collection contents of DB " + mongoDB + "\n\n";

		// Print out all the collections in the users database
		Set<String> colls = db.getCollectionNames();
		for (String s : colls) 
		{ 
			result += "  " + s + "\n";
		}

		dbClient.close();

		result += "\n\nSUCCESS\n";
		m_ServerState = MongoDBServerStateEnum.accessible;

		return result;
	}

	/*	// From abstract parent
	public void loadDBResults()  throws UnknownHostException, SQLException, ClassNotFoundException, IOException
	{
		loadDBResults();
	}*/

	public void loadDBResults() throws UnknownHostException
	{
		int weeksBack = PrefsNightScoutLoader.getInstance().getM_WeeksBackToLoadEntries();
		Date now = new Date();
		long nowLong = now.getTime();
		long thenLong = nowLong - ((long)weeksBack * 7 * 24 * 3600 * 1000); // Arrghh - need to cast this weeks back else get garbage future date!
		Date startDate = new Date(thenLong);
		
//		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
//		String nowString = new String(df.format(now));
//		String startDatetring = new String(df.format(startDate));
		
		loadDBResults(startDate);
	}

	public void loadDBResults(Date startDate) throws UnknownHostException
	{
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible)
		{
			final String mongoHost      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer();
			//		final int    mongoPort      = NightLoaderPreferences.getInstance().getM_NightscoutMongoPort();
			final String mongoDB        = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB();
			final String mongoColl      = PrefsNightScoutLoader.getInstance().getM_NightscoutSensorMongoCollection();


			MongoClient dbClient;
			MongoClientURI dbURI;
			
			if (mongoHost.contains("@"))
			{
				// Create full URI with DB too.  This is straight from the https://mongolab.com/databases/dexcom_db page
				//			dbURI    = new MongoClientURI(mongoHost + ":" + mongoPort + "/" + mongoDB);

				// Left like below after a few days not using server but not working clearly :-(
				//dbURI    = new MongoClientURI(mongoHost + ":" + "/" + mongoDB);

				dbURI    = new MongoClientURI(mongoHost + "/" + mongoDB);
				dbClient = new MongoClient(dbURI);
			}
			else
			{
				dbClient = new MongoClient(mongoHost);
			}

			String timeFld = new String();
			String collFld = new String();

			timeFld = "date";
			collFld = mongoColl /*"treatments"*/;

			DB db = dbClient.getDB(mongoDB);

			resultsFromDB = new ArrayList<DBResultEntry>();

			// Get the players collection
			DBCollection coll = db.getCollection(collFld);
			// Retrieve all the documents

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

			dbClient.close();
		}

	}

	ArrayList <DBResultEntry> getResultsFromDB()
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
