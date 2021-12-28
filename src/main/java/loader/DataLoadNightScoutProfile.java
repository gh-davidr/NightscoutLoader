package loader;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import control.MyLogger;
import entity.DBResult;
import entity.DBResultNightScout;
import mongo.NightscoutMongoDB;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import java.net.UnknownHostException;

public class DataLoadNightScoutProfile extends DataLoadNightScout {
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	private ArrayList<DBResult> resultsFromDB;

	public DataLoadNightScoutProfile() {
		resultsFromDB = new ArrayList<DBResult>();
	}

	public void loadDBResults() throws UnknownHostException {
		// loadDBResults(DataLoadNightScoutEntries.getEntryLoadStartDate());
		
		// Test out the V3 interface :-)
		loadDBResultsV3(DataLoadNightScoutEntries.getEntryLoadStartDate());
	}

	public void loadDBResults(Date startDate) throws UnknownHostException 
	{
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible)
		{			
			NightscoutMongoDB nsMongoDB = new NightscoutMongoDB();
			DBCollection coll = nsMongoDB.getProfileCollectionV2xCollection();
			String timeFld = new String( "created_at");
			resultsFromDB = new ArrayList<DBResult>();

			BasicDBObject query = new BasicDBObject();
			// Load *all* results
			// query.put(timeFld, BasicDBObjectBuilder.start("$gte",
			// startString).add("$lte", endString).get());
			final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			String startString = new String(df.format(startDate));
			query.put(timeFld, BasicDBObjectBuilder.start("$gte", startString).get());

			m_Logger.log(Level.FINE, "loadDBResults Mongo Query is now " + query.toString());

			DBCursor cursor = coll.find(query);
			// Sort by time
			cursor.sort(new BasicDBObject(timeFld, 1));

			for (DBObject rs : cursor) {
				// Now create Result objects for each document and store into array
				// ResultFromDB res = new ResultFromDB(rs);

				// Switch to new object type
				DBResult res = new DBResultNightScout(rs, false);

				resultsFromDB.add(res);

				m_Logger.log(Level.FINEST, "Result added for Nightscout " + rs.toString());
			}

			nsMongoDB.close();
		}

	}

	// Used to load Mongo results between date ranges
	// Either for Nightscout or for the Local Roche test data
	public void loadDBResults(Date startDate, Date endDate, boolean rawData) throws UnknownHostException {
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible) {

			NightscoutMongoDB nsMongoDB = new NightscoutMongoDB();
			DBCollection coll = rawData ? nsMongoDB.getRocheDebugMeterV2xCollection() : nsMongoDB.getTreatmentsV2xCollection();
			String timeFld = new String(rawData ? "Time" : "created_at");
			resultsFromDB = new ArrayList<DBResult>();

			final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			String startString = new String(df.format(startDate));
			String endString = new String(df.format(endDate));
		
			BasicDBObject query = new BasicDBObject();
			query.put(timeFld, BasicDBObjectBuilder.start("$gte", startString).add("$lte", endString).get());

			m_Logger.log(Level.FINE, "loadDBResults with dates Mongo Query is now " + query.toString());

			DBCursor cursor = coll.find(query);

			// Sort by time
			cursor.sort(new BasicDBObject(timeFld, 1));

			for (DBObject rs : cursor) {
				// Now create Result objects for each document and store into array
				// ResultFromDB res = new ResultFromDB(rs);

				// Switch to new object type
				DBResult res = new DBResultNightScout(rs, rawData);

				resultsFromDB.add(res);
				m_Logger.log(Level.FINEST, "Result added for Nightscout " + rs.toString());
			}

			nsMongoDB.close();
		}
	}

	
	public void loadDBResultsV3(Date startDate) throws UnknownHostException 
	{
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible)
		{			
			NightscoutMongoDB nsMongoDB = new NightscoutMongoDB();
			MongoCollection<Document>  coll = nsMongoDB.getTreatmentsCollection();
			String timeFld = new String( "created_at");
			resultsFromDB = new ArrayList<DBResult>();

			BasicDBObject query = new BasicDBObject();
			// Load *all* results
			// query.put(timeFld, BasicDBObjectBuilder.start("$gte",
			// startString).add("$lte", endString).get());
			final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			String startString = new String(df.format(startDate));
			query.put(timeFld, BasicDBObjectBuilder.start("$gte", startString).get());
			// Sort by time
			BasicDBObject sort = new BasicDBObject(timeFld, 1);

			m_Logger.log(Level.FINE, "loadDBResults Mongo Query is now " + query.toString());

			MongoCursor<Document> cursor = coll.find(query)
					.sort(sort)
					.iterator();

			while (cursor.hasNext()) {
//			for (DBObject rs : cursor) {
				Document rs = cursor.next();
				// Now create Result objects for each document and store into array
				// ResultFromDB res = new ResultFromDB(rs);

				// Switch to new object type
				DBResult res = new DBResultNightScout(rs, false);

				resultsFromDB.add(res);

				m_Logger.log(Level.FINEST, "Result added for Nightscout " + rs.toString());
			}

			nsMongoDB.close();
		}

	}

	
	// Used to load Mongo results between date ranges
	// Either for Nightscout or for the Local Roche test data
	public void loadDBResultsV3(Date startDate, Date endDate, boolean rawData) throws UnknownHostException {
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible) {

			NightscoutMongoDB nsMongoDB = new NightscoutMongoDB();
			
			MongoCollection<Document>  coll = rawData ? nsMongoDB.getRocheDebugMeterCollection() : nsMongoDB.getTreatmentsCollection();
			String timeFld = new String(rawData ? "Time" : "created_at");
			resultsFromDB = new ArrayList<DBResult>();

			final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			String startString = new String(df.format(startDate));
			String endString = new String(df.format(endDate));
		
			BasicDBObject query = new BasicDBObject();
			query.put(timeFld, BasicDBObjectBuilder.start("$gte", startString).add("$lte", endString).get());
			// Sort by time
			BasicDBObject sort = new BasicDBObject(timeFld, 1);
			
			m_Logger.log(Level.FINE, "loadDBResults with dates Mongo Query is now " + query.toString());

			MongoCursor<Document> cursor = coll.find(query)
					.sort(sort)
					.iterator();

			while (cursor.hasNext()) {
//			for (DBObject rs : cursor) {
				Document rs = cursor.next();
				
				// Now create Result objects for each document and store into array
				// ResultFromDB res = new ResultFromDB(rs);

				// Switch to new object type
				DBResult res = new DBResultNightScout(rs, rawData);

				resultsFromDB.add(res);
				m_Logger.log(Level.FINEST, "Result added for Nightscout " + rs.toString());
			}

			nsMongoDB.close();
		}
	}
	
	public ArrayList<DBResult> getResultsFromDB() {
		return resultsFromDB;
	}

	public void cloneMeterPumpOnlyResults(ArrayList<DBResult> results) {
		resultsFromDB = new ArrayList<DBResult>(results);
	}

	@Override
	protected String getRequestType() {
		return new String("Data Load Treatments ");
	}
}
