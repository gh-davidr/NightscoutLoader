package mongo;

import org.bson.Document;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import control.PrefsNightScoutLoader;

public class NightscoutMongoDB {

	private final String  m_MongoHost = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer();
	private final String  m_MongoDB   = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB();
	private final String  m_TreatmentsCollection = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection();
	private final String  m_RocheDebugCollection = PrefsNightScoutLoader.getInstance().getM_MongoMeterCollection();
	private final String  m_AuditCollection      = PrefsNightScoutLoader.getInstance().getM_NightscoutAuditCollection();
	private final String  m_SensorCollection     = PrefsNightScoutLoader.getInstance().getM_NightscoutSensorMongoCollection();
	private final String  m_ProfileCollection    = PrefsNightScoutLoader.getInstance().getM_NightscoutProfileCollection();

	private MongoClient   m_DbClient  = null;
	private DB            m_Db        = null;   
	private MongoDatabase m_MongoDatabase = null; 

	public NightscoutMongoDB()
	{
		initialise();
	}

	public NightscoutMongoDB(String mongoHost)
	{
		initialise(mongoHost);
	}

	public NightscoutMongoDB(String mongoHost, String mongoDB)
	{
		initialise(mongoHost, mongoDB);
	}

	// Original V2.x interface used when NightscoutLoader was first written 2015
	public DBCollection getTreatmentsV2xCollection()
	{
		return m_Db.getCollection(m_TreatmentsCollection);
	}

	public DBCollection getAuditV2xCollection()
	{
		return m_Db.getCollection(m_AuditCollection);
	}
	
	public DBCollection getSensorV2xCollection()
	{
		return m_Db.getCollection(m_SensorCollection);
	}

	public DBCollection getRocheDebugMeterV2xCollection()
	{
		return m_Db.getCollection(m_RocheDebugCollection);
	}
	
	public DBCollection getProfileCollectionV2xCollection()
	{
		return m_Db.getCollection(m_ProfileCollection);
	}


	
	// V3.0.0 interface
	public MongoCollection<Document> getTreatmentsCollection()
	{
		return m_MongoDatabase.getCollection(m_TreatmentsCollection);
	}

	public MongoCollection<Document> getAuditCollection()
	{
		return m_MongoDatabase.getCollection(m_AuditCollection);
	}
	
	public MongoCollection<Document> getSensorCollection()
	{
		return m_MongoDatabase.getCollection(m_SensorCollection);
	}

	public MongoCollection<Document> getRocheDebugMeterCollection()
	{
		return m_MongoDatabase.getCollection(m_RocheDebugCollection);
	}
	
	public MongoCollection<Document> getProfileCollectionCollection()
	{
		return m_MongoDatabase.getCollection(m_ProfileCollection);
	}
	
	public void close()
	{
		m_DbClient.close();
	}

	private void initialise()
	{
		initialise(m_MongoHost, m_MongoDB);
	}

	private void initialise(String mongoHost)
	{
		initialise(mongoHost, null);
	}


	@SuppressWarnings("deprecation")
	private void initialise(String mongoHost, String mongoDB)
	{
		MongoClientURI dbURI;

		if (mongoHost.contains("@"))
		{
			// Create full URI with DB too.  This is straight from the https://mongolab.com/databases/dexcom_db page
			//		dbURI    = new MongoClientURI(m_MongoHost + ":" + mongoPort + "/" + mongoDB);

			// Left like below after a few days not using server but not working clearly :-(
			//dbURI    = new MongoClientURI(m_MongoHost + ":" + "/" + mongoDB);

			dbURI    = new MongoClientURI(mongoHost);
		 	m_DbClient = new MongoClient(dbURI);		 	
		}
		else
		{
			m_DbClient = new MongoClient(mongoHost);
		}

		// Build DBOjects for storing into MongoDB

		// Create & get reference to our Roche Results Collection
		if (mongoDB != null)
		{
			m_Db = m_DbClient.getDB(mongoDB);
			
			m_MongoDatabase = m_DbClient.getDatabase(mongoDB).withWriteConcern(WriteConcern.MAJORITY);
		}
	}



	/**
	 * @return the m_DbClient
	 */
	public synchronized MongoClient getM_DbClient() {
		return m_DbClient;
	}

	/**
	 * @return the m_Db
	 */
	public synchronized DB getM_Db() {
		return m_Db;
	}
}
