package davidRichardson;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

public class DataLoadNightScoutTreatments extends DataLoadNightScout 
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

//	private static Integer m_FailedTests = 0;

	private ArrayList <DBResult> resultsFromDB;
//	private MongoDBServerStateEnum m_ServerState = MongoDBServerStateEnum.unknown;

//	enum MongoDBServerStateEnum
//	{
//		unknown,
//		accessible,
//		not_accessible,
//	};

	public DataLoadNightScoutTreatments()
	{
		resultsFromDB = new ArrayList<DBResult>();
	}

//	// Check DB connection for where we only have server name
//	public String testDBConnection(String mongoHost)  throws UnknownHostException
//	{
//		synchronized (mongoHost) {
//			String result = new String();
//			MongoClient dbClient;
//			dbClient = new MongoClient(mongoHost);
//			result = "Connected to " + mongoHost + " now listing available databases.\n\n";
//			for (String s : dbClient.getDatabaseNames()) {
//				result += "  " + s + "\n";
//			}
//			dbClient.close();
//			result += "\n\nSUCCESS\n";
//			m_ServerState = MongoDBServerStateEnum.accessible;
//			return result;
//		}
//	}

//	/**
//	 * @return the m_ServerState
//	 */
//	public synchronized MongoDBServerStateEnum getM_ServerState() {
//		return m_ServerState;
//	}


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
		m_ServerState = DataLoadNightScout.MongoDBServerStateEnum.accessible;

		return result;
	}

	/*	// From abstract parent
	public void loadDBResults()  throws UnknownHostException, SQLException, ClassNotFoundException, IOException
	{
		loadDBResults();
	}*/

//	public void testMongo()
//	{
//		if (m_ServerState == MongoDBServerStateEnum.unknown && m_FailedTests <= m_FailedTestLimit)
//		{
//			m_Logger.log(Level.FINE, "Checking - " + m_FailedTests + ".  Limit is " + m_FailedTestLimit);
//
//			try
//			{
//				final String mongoHost      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer();
//				//			final int    mongoPort      = NightLoaderPreferences.getInstance().getM_NightscoutMongoPort();
//				final String mongoDB        = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB();
////				final String mongoColl      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection();
//
//				if (mongoHost.equals(""))
//				{
//					m_Logger.log(Level.INFO, "Nightscout connection disabled.  Running in standalone mode.");
//					incrementTestCount();
//				}
//				else
//				{
//					m_Logger.log(Level.INFO, "Attempting to connect to Mongo at '" + mongoHost + "'  (May take a few seconds to detect failure)");
//
//					testDBConnection(mongoHost, mongoDB);
//
//					m_Logger.log(Level.INFO, "Mongo connection success!");
//					m_ServerState = MongoDBServerStateEnum.accessible;
//
//				}
//			}
//			catch (Exception e)
//			{
//				m_Logger.log(Level.INFO, "Mongo connection failed.  Please review options.");
//				m_ServerState = MongoDBServerStateEnum.not_accessible;
//				incrementTestCount();
//			}
//		}
//	}

	public void loadDBResults() throws UnknownHostException
	{
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible)
		{
			final String mongoHost      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer();
			//		final int    mongoPort      = NightLoaderPreferences.getInstance().getM_NightscoutMongoPort();
			final String mongoDB        = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB();
			final String mongoColl      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection();


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

			timeFld = "created_at";
			collFld = mongoColl /*"treatments"*/;

			DB db = dbClient.getDB(mongoDB);

			resultsFromDB = new ArrayList<DBResult>();

			// Get the players collection
			DBCollection coll = db.getCollection(collFld);
			// Retrieve all the documents

			BasicDBObject query = new BasicDBObject();
			// Load *all* results
			//		query.put(timeFld, BasicDBObjectBuilder.start("$gte", startString).add("$lte", endString).get());

			m_Logger.log(Level.FINE, "loadDBResults Mongo Query is now " + query.toString());

			DBCursor cursor = coll.find(query);
			// Sort by time
			cursor.sort(new BasicDBObject(timeFld, 1));

			for (DBObject rs: cursor)
			{			
				// Now create Result objects for each document and store into array
				//ResultFromDB res = new ResultFromDB(rs);

				// Switch to new object type
				DBResult res = new DBResultNightScout(rs, false);

				resultsFromDB.add(res);

				m_Logger.log(Level.FINEST, "Result added for Nightscout " + rs.toString());
			}

			dbClient.close();
		}

	}

	// Used to load Mongo results between date ranges
	// Either for Nightscout or for the Local Roche test data
	public void loadDBResults(Date    startDate,
			Date    endDate,
			boolean rawData) throws UnknownHostException
	{		
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible)
		{

			final String mongoHost      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer();
			final String mongoDB        = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB();
			final String mongoColl      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection();
			final String mongoMeterColl = PrefsNightScoutLoader.getInstance().getM_MongoMeterCollection();

			MongoClient dbClient;
			dbClient = new MongoClient( mongoHost /*"localhost"*/ );
			// dbClient = new MongoClient();

			String timeFld = new String();
			String collFld = new String();

			if (rawData)
			{
				timeFld = "Time";
				collFld = mongoMeterColl /*"Roche_Results"*/;
			}
			else
			{
				timeFld = "created_at";
				collFld = mongoColl /*"treatments"*/;
			}

			final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			String startString = new String(df.format(startDate));
			String endString   = new String(df.format(endDate));

			DB db = dbClient.getDB( mongoDB /*"dexcom_db"*/ );
			resultsFromDB = new ArrayList<DBResult>();

			// Get the players collection
			DBCollection coll = db.getCollection(collFld);
			// Retrieve all the documents

			BasicDBObject query = new BasicDBObject();
			query.put(timeFld, BasicDBObjectBuilder.start("$gte", startString).add("$lte", endString).get());

			m_Logger.log(Level.FINE, "loadDBResults with dates Mongo Query is now " + query.toString());

			DBCursor cursor = coll.find(query);

			// Sort by time
			cursor.sort(new BasicDBObject(timeFld, 1));

			for (DBObject rs: cursor)
			{
				// Now create Result objects for each document and store into array
				//ResultFromDB res = new ResultFromDB(rs);

				// Switch to new object type
				DBResult res = new DBResultNightScout(rs, rawData);

				resultsFromDB.add(res);
				m_Logger.log(Level.FINEST, "Result added for Nightscout " + rs.toString());
			}

			dbClient.close();
		}
	}

	public String getLatestEntriesTime() throws UnknownHostException
	{
		final String collName = new String("entries");
		final String sortName = new String("dateString");

		String result = null;

		DBObject res = getLatestMongoRecord(collName, sortName);
		if (res != null)
		{
			result = CommonUtils.getFieldStr(res, sortName);
		}

		return result;
	}

	public String getLatestTreatmentsTimeAndWho() throws UnknownHostException
	{
		final String collName = new String("treatments");
		final String sortName = new String("created_at");
		final String whoName  = new String("enteredBy");

		String result = null;

		DBObject res = getLatestMongoRecord(collName, sortName);
		if (res != null)
		{
			result = CommonUtils.getFieldStr(res, sortName);
			// Add who did it too
			result += " by " + CommonUtils.getFieldStr(res, whoName);
		}

		return result;
	}

	private DBObject getLatestMongoRecord(String collectionName,
			String sortName) throws UnknownHostException
	{
		DBObject result = null;

		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible)
		{
			final String mongoHost      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer();
			final String mongoDB        = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB();

			MongoClient dbClient;
			MongoClientURI dbURI;

			if (mongoHost.contains("@"))
			{
				// Create full URI with DB too.  This is straight from the https://mongolab.com/databases/dexcom_db page
				//		dbURI    = new MongoClientURI(mongoHost + ":" + mongoPort + "/" + mongoDB);

				// Left like below after a few days not using server but not working clearly :-(
				//dbURI    = new MongoClientURI(mongoHost + ":" + "/" + mongoDB);

				dbURI    = new MongoClientURI(mongoHost + "/" + mongoDB);
				dbClient = new MongoClient(dbURI);
			}
			else
			{
				dbClient = new MongoClient(mongoHost);
			}

			// Build DBOjects for storing into MongoDB

			// Create & get reference to our Roche Results Collection
			DB db = dbClient.getDB(mongoDB);
			DBCollection coll = db.getCollection(collectionName);
	
//			
//			
//			
//			
//			final String mongoHost      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer();
//			final String mongoDB        = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB();
//			//		final String mongoMeterColl = PrefsNightScoutLoader.getInstance().getM_MongoMeterCollection();
//
//			MongoClient dbClient;
//			dbClient = new MongoClient( mongoHost /*"localhost"*/ );
//			// dbClient = new MongoClient();
//
//			DB db = dbClient.getDB( mongoDB /*"dexcom_db"*/ );
//
//			// Get the players collection
//			DBCollection coll = db.getCollection(collectionName);
//			// Retrieve all the documents

			BasicDBObject query = new BasicDBObject();		
			//	m_Logger.log(Level.FINE, "loadDBResults with dates Mongo Query is now " + query.toString());

			DBCursor cursor = coll.find(query).sort(new BasicDBObject(sortName, -1)).limit(1);

			if (cursor.size() == 1)
			{
				result = cursor.next();  
				m_Logger.log(Level.FINER, "Mongo Entries Thread Query returned: " + result.toString());
			}

			dbClient.close();
		}
		return result;
	}

	public DBObject getLatestTreatmentsRecord()
	{
		DBObject result = null;

		return result;
	}

	public int downloadTreamentJSON(String fileName) throws IOException
	{
		final String mongoColl      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection();

		int result = downloadJSON(fileName, mongoColl, "created_at");

		return result;
	}

	public int downloadSensorJSON(String fileName) throws IOException
	{
		final String mongoColl      = PrefsNightScoutLoader.getInstance().getM_NightscoutSensorMongoCollection();

		int result = downloadJSON(fileName, mongoColl, "dateString");

		return result;
	}

	//	private int downloadJSON(String fileName, String mongoColl) throws IOException
	//	{
	//		return downloadJSON(fileName, mongoColl, null);
	//	}
	//	
	private int downloadJSON(String fileName, String mongoColl, String sortCol) throws IOException
	{
		// How many did we actually write to file
		int result = 0;

		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible)
		{

			final String mongoHost      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer();
			final String mongoDB        = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB();
			//		final String mongoColl      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection();

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

			String collFld = new String();
			collFld = mongoColl /*"treatments"*/;

			DB db = dbClient.getDB(mongoDB);

			// Get the collection
			DBCollection coll = db.getCollection(collFld);
			// Retrieve all the documents

			BasicDBObject query = new BasicDBObject();
			m_Logger.log(Level.FINE, "downloadJSON Mongo Query is now " + query.toString());
			DBCursor cursor = coll.find(query);	
			if (sortCol != null)
			{
				cursor.sort(new BasicDBObject(sortCol, 1));	
			}

			FileOutputStream out = new FileOutputStream(fileName);		
			for (DBObject rs: cursor)
			{
				// Convert to string then write out to file
				String doc = rs.toString();
				out.write(doc.getBytes());
				out.write('\n');
				result++;

				m_Logger.log(Level.FINEST, "Retrieved JSON " + rs.toString());
			}
			out.close();
			dbClient.close();
		}

		return result;	
	}

	public void updateDBResultFromForm(DBResult res) throws UnknownHostException
	{
		updateDBResultFromForm(res, false);
	}

	public void updateDBResultFromForm(DBResult res, boolean clearProximity) throws UnknownHostException
	{
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible)
		{

			final String mongoHost      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer();
			//		final int    mongoPort      = NightLoaderPreferences.getInstance().getM_NightscoutMongoPort();
			final String mongoDB        = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB();
			final String mongoColl      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection();


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

			// Build DBOjects for storing into MongoDB

			// Create & get reference to our Roche Results Collection
			DB db = dbClient.getDB(mongoDB);
			DBCollection coll = db.getCollection(mongoColl);

			BasicDBObject dbObject = new BasicDBObject();
			dbObject.put("_id", new ObjectId(res.getM_ID())); 

			DBObject newObject =  coll.find(dbObject).toArray().get(0);

			// David 23 Sep 2016
			// Need to strip out Promixity
			// Also need to tell the audit log that proximity count has gone down too.

			if (clearProximity == true)
			{
				String cp_EnteredBy = new String(res.getM_CP_EnteredBy());
				cp_EnteredBy = cp_EnteredBy.replace("-PROXIMITY", "");

				newObject.put("enteredBy", cp_EnteredBy);
			}
			newObject.put("notes", res.getM_CP_Notes());
			coll.findAndModify(dbObject, newObject);

			dbClient.close();
		}

		// Tell Audit History to update too.
		if (clearProximity == true)
		{
			AuditHistory.getInstance().clearProximity(res);
		}
	}

	public void storeResultsFromDB(Set<DBResult> resultsSet) throws UnknownHostException
	{
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible)
		{

			final String mongoHost      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer();
			//		final int    mongoPort      = NightLoaderPreferences.getInstance().getM_NightscoutMongoPort();
			final String mongoDB        = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB();
			final String mongoColl      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection();

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


			// Build DBOjects for storing into MongoDB

			// Create & get reference to our Roche Results Collection
			DB db = dbClient.getDB(mongoDB);
			DBCollection coll = db.getCollection(mongoColl);

			// In reality, the collection used will probably be treatments!

			// Performance improvement ...
			// http://stackoverflow.com/questions/18128490/how-to-insert-multiple-documents-at-once-in-mongodb-through-java
			List<DBObject> documents = new ArrayList<>();

			for (DBResult x: resultsSet)
			{
				BasicDBObject doc = x.createNightScoutObject();

				// coll.insert(doc);
				documents.add(doc);

				m_Logger.log(Level.FINEST, "Result added for Nightscout " + x.toString());
			}

			m_Logger.log(Level.FINE, "About to bulk insert to Nightscout");
			// Bulk insert instead
			coll.insert(documents);
			m_Logger.log(Level.FINE, "Bulk Insert completed.");

			dbClient.close();
		}
	}
	
	public void updateExistingResultsFromDB(Set<DBResult> resultsSet) throws UnknownHostException
	{
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible)
		{

			final String mongoHost      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer();
			//		final int    mongoPort      = NightLoaderPreferences.getInstance().getM_NightscoutMongoPort();
			final String mongoDB        = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB();
			final String mongoColl      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection();

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


			// Build DBOjects for storing into MongoDB

			// Create & get reference to our Roche Results Collection
			DB db = dbClient.getDB(mongoDB);
			DBCollection coll = db.getCollection(mongoColl);

			// In reality, the collection used will probably be treatments!

			// These are the list of potentially duplicate existing treatments that need
			// their entered by values updating
			for (DBResult x: resultsSet)
			{				
				BasicDBObject dbObject = new BasicDBObject();
				dbObject.put("_id", new ObjectId(x.getM_ID())); 

				DBObject newObject =  coll.find(dbObject).toArray().get(0);

				newObject.put("enteredBy", x.getM_CP_EnteredBy());
				coll.findAndModify(dbObject, newObject);

				m_Logger.log(Level.FINEST, "enteredBy updated for Nightscout " + x.toString());
			}

			dbClient.close();
		}
	}


	// Remove entries for all uploads
	public int deleteLoadedTreatments() throws IOException
	{
		// How many did we actually delete
		int result = 0;

		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible)
		{
			final String mongoHost      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer();
			final String mongoDB        = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB();
			final String mongoColl      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection();

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

			String collFld = new String();
			collFld = mongoColl /*"treatments"*/;

			DB db = dbClient.getDB(mongoDB);

			// Get the collection
			DBCollection coll = db.getCollection(collFld);
			// Retrieve all the documents

			BasicDBObject query = new BasicDBObject();
			//		query.append(DBResult.getM_determinantField(), DBResult.getM_determinantValue());
			// For now, delete all match regexp
			query.append(DBResult.getM_determinantField(), java.util.regex.Pattern.compile(DBResult.getM_determinantValue()));
			m_Logger.log(Level.FINE, "deleteLoadedTreatments Mongo Query is now " + query.toString());

			DBCursor cursor = coll.find(query);
			result = cursor.count();

			coll.remove(query);

			dbClient.close();
		}

		return result;	
	}

	// Remove entries for a single upload
	public int deleteLoadedTreatment(AuditLog entry) throws IOException
	{
		return deleteLoadedTreatment(entry, false);
	}

	// Remove entries for a single upload
	public int deleteLoadedTreatment(AuditLog entry, boolean proximityOnly) throws IOException
	{
		// How many did we actually delete
		int result = 0;
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible)
		{
			final String mongoHost      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer();
			final String mongoDB        = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB();
			final String mongoColl      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection();

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

			String collFld = new String();
			collFld = mongoColl /*"treatments"*/;

			DB db = dbClient.getDB(mongoDB);

			// Get the collection
			DBCollection coll = db.getCollection(collFld);
			// Retrieve all the documents

			BasicDBObject query = new BasicDBObject();
			//		query.append(DBResult.getM_determinantField(), DBResult.getM_determinantValue());
			// For now, delete all match regexp

			// Deletins by upload ID append proximity if deleting these items
			query.append(DBResult.getM_determinantField(), entry.getM_UploadID() + (
					proximityOnly ? "-PROXIMITY" : ""));
			m_Logger.log(Level.FINE, "deleteLoadedTreatment Mongo Query is now " + query.toString());

			DBCursor cursor = coll.find(query);
			result = cursor.count();

			coll.remove(query);

			dbClient.close();
		}

		return result;	
	}
	
	public int deleteLoadedTreatment(DBResult res) throws UnknownHostException
	{
		// How many did we actually delete
		int result = 0;
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible)
		{
			final String mongoHost      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer();
			final String mongoDB        = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB();
			final String mongoColl      = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection();

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

			String collFld = new String();
			collFld = mongoColl /*"treatments"*/;

			DB db = dbClient.getDB(mongoDB);

			// Get the collection
			DBCollection coll = db.getCollection(collFld);
			// Retrieve all the documents

			BasicDBObject query = new BasicDBObject();
			//		query.append(DBResult.getM_determinantField(), DBResult.getM_determinantValue());
			// For now, delete all match regexp

			// Deletins by upload ID append proximity if deleting these items
			query.append("_id", new ObjectId(res.getM_ID()));
			m_Logger.log(Level.FINE, "deleteLoadedTreatment Mongo Query is now " + query.toString());

			DBCursor cursor = coll.find(query);
			result = cursor.count();

			coll.remove(query);

			dbClient.close();
		}

		return result;	
	}



	// Useful for saving SQL Server type results into local Mongo for developing
	// while on the train :-)
	public void storeRawResultsFromDB(ArrayList <DBResult> resultsFromDB) throws UnknownHostException
	{
		final String mongoHost      = PrefsNightScoutLoader.getInstance().getM_MongoMeterServer();
		final String mongoDB        = PrefsNightScoutLoader.getInstance().getM_MongoMeterDB();
		final String mongoMeterColl = PrefsNightScoutLoader.getInstance().getM_MongoMeterCollection();

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

		// Build DBOjects for storing into MongoDB

		// Create & get reference to our Roche Results Collection
		DB db = dbClient.getDB(mongoDB);
		DBCollection coll = db.getCollection(mongoMeterColl);

		// In reality, the collection used will probably be treatments!

		for (DBResult x: resultsFromDB)
		{
			BasicDBObject doc = new BasicDBObject("Year", x.getM_Year())
					.append("Month", x.getM_Month())
					.append("Day", x.getM_Day())
					.append("DayName", x.getM_DayName())
					//					.append("Time", x.getM_Time())
					.append("Time", x.getM_Time().toString())
					.append("TimeSlot", x.getM_TimeSlot())
					.append("Result", x.getM_Result())
					.append("ResultType", x.getM_ResultType())
					.append("MealType", x.getM_MealType())
					.append("Duration", x.getM_Duration());
			coll.insert(doc);
			m_Logger.log(Level.FINEST, "Result stored for Raw Nightscout load " + x.toString());
		}

		dbClient.close();
	}

	ArrayList <DBResult> getResultsFromDB()
	{
		return resultsFromDB;
	}

	public void cloneMeterPumpOnlyResults(ArrayList <DBResult> results)
	{
		resultsFromDB = new ArrayList<DBResult>(results);
	}

	@Override
	protected String getDevice() 
	{
		return new String("");  // Not used
	}

	@Override
	protected String getRequestType() 
	{
		return new String ("Data Load Treatments ");
	}

//	/**
//	 * @return the m_FailedTests
//	 */
//	public static synchronized int getM_FailedTests() 
//	{
//		return m_FailedTests;
//	}
//
//	/**
//	 * Reset m_FailedTests to 0
//	 */
//	public static synchronized void resetFailedTests() 
//	{
//		m_Logger.log(Level.FINE, "Resetting m_FailedTests from " + m_FailedTests + " to 0");
//
//		DataLoadNightScoutTreatments.m_FailedTests = 0;
//	}
//
//	private static synchronized void incrementTestCount() 
//	{
//		synchronized(DataLoadNightScoutTreatments.m_FailedTests)
//		{
//			m_Logger.log(Level.FINE, "Incrementing m_FailedTests from " + m_FailedTests + " by 1");
//			// There are 4 calls here before the logging mechanism initializes!
//			// System.out.println("SYS OUT Incrementing m_FailedTests from " + m_FailedTests + " by 1");
//
//			DataLoadNightScoutTreatments.m_FailedTests++;
//		}
//	}
}
