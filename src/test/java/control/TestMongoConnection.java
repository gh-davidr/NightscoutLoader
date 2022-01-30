package control;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoSocketWriteException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public class TestMongoConnection {

	private static String m_ValidMongoURIString = null;
	private static String m_ValidMongoDBString = null;
	private static String m_InValidMongoURIString = null;
	private static String m_InValidMongoDBString = null;


	public TestMongoConnection()
	{
		setup();
	}

	@Test
	public void test1()
	{
		// Support some level of Mongo connection testing without need to hard code sensitive
		// connection strings
		//
		// Instead, these get passed in through the mvn command as follows:
		//
		//
		//  mvn -DVALID_MONGO_URI="mongodb+srv://valid_user:pass@heroku.mongo.net/db?retryWrites=true&w=majority" ^
		//      -DVALID_MONGO_DB="db" ^
		//      -DINVALID_MONGO_URI="mongodb+srv://invalid_user:pass@heroku.mongo.net/db?retryWrites=true&w=majority" ^
		//      -DINVALID_MONGO_DB="db"

		m_ValidMongoURIString = System.getProperty("VALID_MONGO_URI");
		m_ValidMongoDBString = System.getProperty("VALID_MONGO_DB");
		m_InValidMongoURIString = System.getProperty("INVALID_MONGO_URI");
		m_InValidMongoDBString = System.getProperty("INVALID_MONGO_DB");

		testConnection(m_ValidMongoURIString, m_ValidMongoDBString, true);
		testConnection(m_InValidMongoURIString, m_InValidMongoDBString, false);

		//		testConnection(uri3, db3);
		//		testConnection(uri4, db4);
	}

	private void testConnection(String uri, String dbString, Boolean expectSucceed)
	{
		if (uri != null && uri.length() > 0 && dbString != null && dbString.length() > 0)
		{
			MongoClientURI dbURI = new MongoClientURI(uri);
			MongoClient mongo = new MongoClient(dbURI);

			Boolean connectionSuccessBoolean = true;

			try {
				MongoDatabase database = mongo.getDatabase(dbString);

				MongoIterable<String> list = database.listCollectionNames();
				for (String name : list) {
					System.out.println(uri + " has " + name);
				}
			} 
			catch (MongoSocketWriteException e)
			{
				connectionSuccessBoolean = false;
			}
			catch (MongoTimeoutException e)
			{
				connectionSuccessBoolean = false;
			}
			catch (Exception e) 
			{
				connectionSuccessBoolean = false;
			}

			Assertions.assertTrue(expectSucceed == connectionSuccessBoolean, 
					"Attempted connection to " + uri + ":" + dbString + ".  Expected " 
							+ (expectSucceed == true ? "successful connection " : "failed connection")
							+ " but got " 
							+ (connectionSuccessBoolean == true ? "successful connection " : "failed connection")
					);
			
			mongo.close();
		}
		else
		{
			System.out.println("**** Mongo Connection tests skipped.  ****"
					+ " (Test for connection success " + expectSucceed + ") "
					+ "Use mvn parameters to set valid and invalid URIs for testing: "
					+ " -DVALID_MONGO_URI & -DVALID_MONGO_DB, and  -DINVALID_MONGO_URI & -DINVALID_MONGO_DB"
					);
		}
	}

	private void setup()
	{
		try {

			// Set log level to 10 (warning).  Other new option is 20 (severe)
			// This quietens down the output significantly
			PrefsNightScoutLoader.getInstance().setM_LogLevel(10);

			MyLogger.setup(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
