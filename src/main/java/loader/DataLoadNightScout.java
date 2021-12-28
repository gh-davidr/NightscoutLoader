package loader;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import control.MyLogger;
import control.PrefsNightScoutLoader;
import entity.DBResult;
import entity.DBResultEntry;
import mongo.NightscoutMongoDB;
import utils.CommonUtils;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;

public abstract class DataLoadNightScout extends DataLoadBase {
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());
	private static final int m_FailedTestLimit = 2;

	private static Integer m_FailedTests = 0;
	protected MongoDBServerStateEnum m_ServerState = MongoDBServerStateEnum.unknown;

	public enum MongoDBServerStateEnum {
		unknown, accessible, not_accessible,
	};

	public DataLoadNightScout() {
		;
	}

	// Abstract method that returns the request type for messaging out
	protected abstract String getRequestType();

	// Check DB connection for where we only have server name
	@SuppressWarnings("deprecation")
	public String testDBConnection(String mongoHost) throws UnknownHostException {
		synchronized (mongoHost) {
			String result = new String();

			NightscoutMongoDB mongoDB = new NightscoutMongoDB(mongoHost);
			result = "Connected to " + mongoHost + " now listing available databases.\n\n";
			for (String s : mongoDB.getM_DbClient().getDatabaseNames()) {
				result += "  " + s + "\n";
			}
			mongoDB.close();

			result += "\n\nSUCCESS\n";
			m_ServerState = MongoDBServerStateEnum.accessible;
			return result;
		}
	}

	/**
	 * @return the m_ServerState
	 */
	public synchronized MongoDBServerStateEnum getM_ServerState() {
		return m_ServerState;
	}

	// Check DB connection for where we have server name & db name
	public String testDBConnection(String mongoHost, String mongoDB) throws UnknownHostException {
		String result = new String();

		NightscoutMongoDB nsMongoDB = new NightscoutMongoDB(mongoHost, mongoDB);
		result += "\nNow listing collection contents of DB " + mongoDB + "\n\n";

		// Print out all the collections in the users database
		Set<String> colls = nsMongoDB.getM_Db().getCollectionNames();
		for (String s : colls) {
			result += "  " + s + "\n";
		}

		nsMongoDB.close();

		result += "\n\nSUCCESS\n";
		m_ServerState = MongoDBServerStateEnum.accessible;

		return result;
	}

	/*
	 * // From abstract parent public void loadDBResults() throws
	 * UnknownHostException, SQLException, ClassNotFoundException, IOException {
	 * loadDBResults(); }
	 */

	public void testMongo() {
		if (m_ServerState == MongoDBServerStateEnum.unknown && m_FailedTests <= m_FailedTestLimit) {
			m_Logger.log(Level.FINE, "Checking - " + m_FailedTests + ".  Limit is " + m_FailedTestLimit);

			try {
				final String mongoHost = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer();
				// final int mongoPort =
				// NightLoaderPreferences.getInstance().getM_NightscoutMongoPort();
				final String mongoDB = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB();
				// final String mongoColl =
				// PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection();

				if (mongoHost.equals("")) {
					m_Logger.log(Level.INFO,
							getRequestType() + "Nightscout connection disabled.  Running in standalone mode.");
					incrementTestCount();
				} else {
					m_Logger.log(Level.INFO, getRequestType() + "Attempting to connect to Mongo at '" + mongoHost
							+ "'  (May take a few seconds to detect failure)");

					testDBConnection(mongoHost, mongoDB);

					m_Logger.log(Level.INFO, getRequestType() + "Mongo connection success!");
					m_ServerState = MongoDBServerStateEnum.accessible;

				}
			} catch (Exception e) {
				m_Logger.log(Level.INFO, getRequestType() + "Mongo connection failed.  Please review options.");
				m_ServerState = MongoDBServerStateEnum.not_accessible;
				incrementTestCount();
			}
		}
	}

	public String getLatestEntriesTime() throws UnknownHostException {
		NightscoutMongoDB nsMongoDB = new NightscoutMongoDB();
		DBCollection coll = nsMongoDB.getSensorV2xCollection();

		final String sortName = new String("dateString");

		String result = null;

		DBObject res = getLatestMongoRecord(nsMongoDB, coll, sortName);
		if (res != null) {
			result = CommonUtils.getFieldStr(res, sortName);
		}
		nsMongoDB.close();

		return result;
	}

	public String getLatestTreatmentsTimeAndWho() throws UnknownHostException 
	{
		NightscoutMongoDB nsMongoDB = new NightscoutMongoDB();
		DBCollection coll = nsMongoDB.getTreatmentsV2xCollection();

		final String sortName = new String("created_at");
		final String whoName = new String("enteredBy");

		String result = null;

		DBObject res = getLatestMongoRecord(nsMongoDB, coll, sortName);
		if (res != null) {
			result = CommonUtils.getFieldStr(res, sortName);
			// Add who did it too
			result += " by " + CommonUtils.getFieldStr(res, whoName);
		}
		nsMongoDB.close();

		return result;
	}

	protected DBObject getLatestMongoRecord(NightscoutMongoDB nsMongoDB, DBCollection collection, String sortName)
			throws UnknownHostException 
	{
		DBObject result = null;
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible) {

			BasicDBObject query = new BasicDBObject();

			DBCursor cursor = collection.find(query).sort(new BasicDBObject(sortName, -1)).limit(1);

			if (cursor.size() == 1) {
				result = cursor.next();
				m_Logger.log(Level.FINER, "Mongo Entries Thread Query returned: " + result.toString());
			}
		}

		return result;
	}

	public DBObject getLatestTreatmentsRecord() {
		DBObject result = null;

		return result;
	}

	public int downloadTreamentJSON(String fileName) throws IOException {
		NightscoutMongoDB nsMongoDB = new NightscoutMongoDB();
		DBCollection coll = nsMongoDB.getTreatmentsV2xCollection();

		final String sortName = new String("created_at");
		int result = downloadJSON(nsMongoDB, coll, fileName, sortName);

		nsMongoDB.close();
		return result;
	}

	public int downloadSensorJSON(String fileName) throws IOException {
		NightscoutMongoDB nsMongoDB = new NightscoutMongoDB();
		DBCollection coll = nsMongoDB.getSensorV2xCollection();

		final String sortName = new String("dateString");
		int result = downloadJSON(nsMongoDB, coll, fileName, sortName);

		nsMongoDB.close();
		return result;
	}

	private int downloadJSON(NightscoutMongoDB nsMongoDB, DBCollection collection, String fileName, String sortName)
			throws IOException {
		// How many did we actually write to file
		int result = 0;

		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible) {
			BasicDBObject query = new BasicDBObject();
			m_Logger.log(Level.FINE, "downloadJSON Mongo Query is now " + query.toString());
			DBCursor cursor = collection.find(query);
			if (sortName != null) {
				cursor.sort(new BasicDBObject(sortName, 1));
			}

			FileOutputStream out = new FileOutputStream(fileName);
			for (DBObject rs : cursor) {
				// Convert to string then write out to file
				String doc = rs.toString();
				out.write(doc.getBytes());
				out.write('\n');
				result++;

				m_Logger.log(Level.FINEST, "Retrieved JSON " + rs.toString());
			}
			out.close();
		}

		return result;
	}

	public void updateDBResultFromForm(DBResult res) throws UnknownHostException {
		updateDBResultFromForm(res, false);
	}

	public void updateDBResultFromForm(DBResult res, boolean clearProximity) throws UnknownHostException {
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible) {
			
			NightscoutMongoDB nsMongoDB = new NightscoutMongoDB();
			DBCollection coll = nsMongoDB.getTreatmentsV2xCollection();

			BasicDBObject dbObject = new BasicDBObject();
			dbObject.put("_id", new ObjectId(res.getM_ID()));

			DBObject newObject = coll.find(dbObject).toArray().get(0);

			// David 23 Sep 2016
			// Need to strip out Promixity
			// Also need to tell the audit log that proximity count has gone down too.

			if (clearProximity == true) {
				String cp_EnteredBy = new String(res.getM_CP_EnteredBy());
				cp_EnteredBy = cp_EnteredBy.replace("-PROXIMITY", "");

				newObject.put("enteredBy", cp_EnteredBy);
			}
			newObject.put("notes", res.getM_CP_Notes());
			coll.findAndModify(dbObject, newObject);

			nsMongoDB.close();
		}

		// Tell Audit History to update too.
		if (clearProximity == true) {
			AuditHistory.getInstance().clearProximity(res);
		}
	}

	public void storeResultsFromDB(Set<DBResult> resultsSet) throws UnknownHostException {
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible) {

			NightscoutMongoDB nsMongoDB = new NightscoutMongoDB();
			DBCollection coll = nsMongoDB.getTreatmentsV2xCollection();

			// In reality, the collection used will probably be treatments!

			// Performance improvement ...
			// http://stackoverflow.com/questions/18128490/how-to-insert-multiple-documents-at-once-in-mongodb-through-java
			List<DBObject> documents = new ArrayList<>();

			for (DBResult x : resultsSet) {
				BasicDBObject doc = x.createNightScoutObject();

				// coll.insert(doc);
				documents.add(doc);

				m_Logger.log(Level.FINEST, "Result added for Nightscout " + x.toString());
			}

			m_Logger.log(Level.FINE, "About to bulk insert to Nightscout");
			// Bulk insert instead
			coll.insert(documents);
			m_Logger.log(Level.FINE, "Bulk Insert completed.");

			nsMongoDB.close();
		}
	}

	public void storeResultEntriesFromDB(Set<DBResultEntry> resultsSet) throws UnknownHostException {
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible) {

			NightscoutMongoDB nsMongoDB = new NightscoutMongoDB();
			DBCollection coll = nsMongoDB.getSensorV2xCollection();

			// In reality, the collection used will probably be treatments!

			// Performance improvement ...
			// http://stackoverflow.com/questions/18128490/how-to-insert-multiple-documents-at-once-in-mongodb-through-java
			List<DBObject> documents = new ArrayList<>();

			for (DBResultEntry x : resultsSet) {
				BasicDBObject doc = x.createNightScoutObject();
				// coll.insert(doc);
				documents.add(doc);
				m_Logger.log(Level.FINEST, "Result added for Nightscout " + x.toString());
			}

			m_Logger.log(Level.FINE, "About to bulk insert to Nightscout");
			// Bulk insert instead
			coll.insert(documents);
			m_Logger.log(Level.FINE, "Bulk Insert completed.");

			nsMongoDB.close();
		}
	}

	public void updateExistingResultsFromDB(Set<DBResult> resultsSet) throws UnknownHostException {
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible) {
			NightscoutMongoDB nsMongoDB = new NightscoutMongoDB();
			DBCollection coll = nsMongoDB.getTreatmentsV2xCollection();

			// In reality, the collection used will probably be treatments!

			// These are the list of potentially duplicate existing treatments that need
			// their entered by values updating
			for (DBResult x : resultsSet) {
				BasicDBObject dbObject = new BasicDBObject();
				dbObject.put("_id", new ObjectId(x.getM_ID()));

				DBObject newObject = coll.find(dbObject).toArray().get(0);

				x.setImpactOfProximity();
				newObject.put("enteredBy", x.getM_CP_EnteredBy());
				coll.findAndModify(dbObject, newObject);

				m_Logger.log(Level.FINEST, "enteredBy updated for Nightscout " + x.toString());
			}

			nsMongoDB.close();
		}
	}

	// Remove entries for all uploads
	public int deleteLoadedTreatments() throws IOException {
		// How many did we actually delete
		int result = 0;

		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible) {
			NightscoutMongoDB nsMongoDB = new NightscoutMongoDB();
			DBCollection coll = nsMongoDB.getTreatmentsV2xCollection();

			BasicDBObject query = new BasicDBObject();
			// query.append(DBResult.getM_determinantField(),
			// DBResult.getM_determinantValue());
			// For now, delete all match regexp
			query.append(DBResult.getM_determinantField(),
					java.util.regex.Pattern.compile(DBResult.getM_determinantValue()));
			m_Logger.log(Level.FINE, "deleteLoadedTreatments Mongo Query is now " + query.toString());

			DBCursor cursor = coll.find(query);
			result = cursor.count();

			coll.remove(query);

			nsMongoDB.close();
		}

		return result;
	}

	// Remove entries for a single upload
	public int deleteLoadedTreatment(AuditLog entry) throws IOException {
		int treatmentResult = deleteLoadedTreatment(entry, false);
		/*int entriesResult =*/ deleteLoadedEntries(entry, false);

		return treatmentResult;
	}

	// Remove entries for a single upload
	public int deleteLoadedTreatment(AuditLog entry, boolean proximityOnly) throws IOException {
		// How many did we actually delete
		int result = 0;
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible) {
			NightscoutMongoDB nsMongoDB = new NightscoutMongoDB();
			DBCollection coll = nsMongoDB.getTreatmentsV2xCollection();

			// Retrieve all the documents

			BasicDBObject query = new BasicDBObject();
			// query.append(DBResult.getM_determinantField(),
			// DBResult.getM_determinantValue());
			// For now, delete all match regexp

			// Deletins by upload ID append proximity if deleting these items
			query.append(DBResult.getM_determinantField(), entry.getM_UploadID() + (proximityOnly ? "-PROXIMITY" : ""));
			m_Logger.log(Level.FINE, "deleteLoadedTreatment Mongo Query is now " + query.toString());

			DBCursor cursor = coll.find(query);
			result = cursor.count();

			coll.remove(query);

			nsMongoDB.close();
		}

		return result;
	}

	// Remove entries for a single upload
	public int deleteLoadedEntries(AuditLog entry, boolean proximityOnly) throws IOException {
		// How many did we actually delete
		int result = 0;
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible) {
			NightscoutMongoDB nsMongoDB = new NightscoutMongoDB();
			DBCollection coll = nsMongoDB.getSensorV2xCollection();

			BasicDBObject query = new BasicDBObject();
			// query.append(DBResult.getM_determinantField(),
			// DBResult.getM_determinantValue());
			// For now, delete all match regexp

			// Deletins by upload ID append proximity if deleting these items
			query.append(DBResultEntry.getM_determinantField(),
					entry.getM_UploadID() + (proximityOnly ? "-PROXIMITY" : ""));
			m_Logger.log(Level.FINE, "deleteLoadedTreatment Mongo Query is now " + query.toString());

			DBCursor cursor = coll.find(query);
			result = cursor.count();

			coll.remove(query);

			nsMongoDB.close();
		}

		return result;
	}

	public int deleteLoadedTreatment(DBResult res) throws UnknownHostException {
		// How many did we actually delete
		int result = 0;
		testMongo();

		if (m_ServerState == MongoDBServerStateEnum.accessible) {
			NightscoutMongoDB nsMongoDB = new NightscoutMongoDB();
			DBCollection coll = nsMongoDB.getTreatmentsV2xCollection();

			BasicDBObject query = new BasicDBObject();
			// query.append(DBResult.getM_determinantField(),
			// DBResult.getM_determinantValue());
			// For now, delete all match regexp

			// Deletins by upload ID append proximity if deleting these items
			query.append("_id", new ObjectId(res.getM_ID()));
			m_Logger.log(Level.FINE, "deleteLoadedTreatment Mongo Query is now " + query.toString());

			DBCursor cursor = coll.find(query);
			result = cursor.count();

			coll.remove(query);

			nsMongoDB.close();
		}

		return result;
	}

	// Useful for saving SQL Server type results into local Mongo for developing
	// while on the train :-)
	public void storeRawResultsFromDB(ArrayList<DBResult> resultsFromDB) throws UnknownHostException {
		NightscoutMongoDB nsMongoDB = new NightscoutMongoDB();
		DBCollection coll = nsMongoDB.getRocheDebugMeterV2xCollection();

		// In reality, the collection used will probably be treatments!

		for (DBResult x : resultsFromDB) {
			BasicDBObject doc = new BasicDBObject("Year", x.getM_Year()).append("Month", x.getM_Month())
					.append("Day", x.getM_Day()).append("DayName", x.getM_DayName())
					// .append("Time", x.getM_Time())
					.append("Time", x.getM_Time().toString()).append("TimeSlot", x.getM_TimeSlot())
					.append("Result", x.getM_Result()).append("ResultType", x.getM_ResultType())
					.append("MealType", x.getM_MealType()).append("Duration", x.getM_Duration());
			coll.insert(doc);
			m_Logger.log(Level.FINEST, "Result stored for Raw Nightscout load " + x.toString());
		}

		nsMongoDB.close();
	}

	@Override
	protected String getDevice() {
		return new String(""); // Not used
	}

	/**
	 * @return the m_FailedTests
	 */
	public static synchronized int getM_FailedTests() {
		return m_FailedTests;
	}

	/**
	 * Reset m_FailedTests to 0
	 */
	public static synchronized void resetFailedTests() {
		m_Logger.log(Level.FINE, "Resetting m_FailedTests from " + m_FailedTests + " to 0");

		DataLoadNightScout.m_FailedTests = 0;
	}

	private static synchronized void incrementTestCount() {
		synchronized (DataLoadNightScout.m_FailedTests) {
			m_Logger.log(Level.FINE, "Incrementing m_FailedTests from " + m_FailedTests + " by 1");
			// There are 4 calls here before the logging mechanism initializes!
			// System.out.println("SYS OUT Incrementing m_FailedTests from " + m_FailedTests
			// + " by 1");

			DataLoadNightScout.m_FailedTests++;
		}
	}

	@Override
	public void loadDBResults() throws UnknownHostException, SQLException, ClassNotFoundException, IOException {
		// TODO Auto-generated method stub

	}
}
