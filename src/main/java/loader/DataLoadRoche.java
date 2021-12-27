package loader;
import java.sql.DriverManager;
//import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.io.*;
//import java.lang.*;
//import java.util.*;
import java.net.UnknownHostException;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import control.MyLogger;
import control.PrefsNightScoutLoader;
import entity.DBResult;
import entity.DBResultNightScout;
import entity.DBResultRoche;

import java.util.ArrayList;
//import java.util.Scanner;
import java.util.Date;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.text.DateFormat;


public class DataLoadRoche extends DataLoadBase
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());
	
	// Used for 2 separate calls to load
	// One to initialize other the abstract load
	private Date m_initStartDate;
	private Date m_initEndDate;

	public DataLoadRoche()
	{
		super();
		m_initStartDate = new Date(0);
		m_initEndDate   = new Date(0);
	}

	// New initialize routine
	public void initialize(Date startDate, Date endDate)
	{
		m_initStartDate = startDate;
		m_initEndDate   = endDate;
		
		// Also clear out any stored results
		clearLists();
	}

	
	// From abstract parent
	public void loadDBResults()  throws SQLException, ClassNotFoundException, IOException
	{
		loadDBResults(
				PrefsNightScoutLoader.getInstance().getM_SQLDBServerHost(),
				PrefsNightScoutLoader.getInstance().getM_SQLDBServerInstance(), 
				PrefsNightScoutLoader.getInstance().getM_SQLDBName(), 
				PrefsNightScoutLoader.getInstance().getM_SQLFile(), 
				m_initStartDate, m_initEndDate);
	}
	
	// Check DB connection for where we only have server name
	public String testDBConnection(String srv, String inst) throws SQLException, ClassNotFoundException, IOException
	{
		String result = new String();
		String l_Name = new String();

		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");	
		//Connection conn = DriverManager.getConnection("jdbc:sqlserver://192.168.1.123\\ACCUCHEK360;integratedSecurity=true;databaseName=KATSERVER_FUJI_ACCUCHEK_1");
		String connString = "jdbc:sqlserver://" + srv + "\\" + inst + ";integratedSecurity=true;databaseName=master";
		Connection conn = DriverManager.getConnection(connString);
		Statement sta = conn.createStatement();
		
    	m_Logger.log(Level.FINE, "Test Connection: Successfully connected to SQL Server: " + srv + "/" + inst);
				
		// Get a list of Database Names
		String Sql = "SELECT name FROM master.dbo.sysdatabases";		
    	m_Logger.log(Level.FINER, "Test Connection: SQL Contents modified as follows " + Sql);

		ResultSet rs = sta.executeQuery(Sql);
    	m_Logger.log(Level.FINE, "Test Connection: Successfully executed query");
    	
		rawResultsFromDB.clear();
		while (rs.next()) 
		{
			l_Name = rs.getString("name");

			result = result
					+ " nm:" + l_Name
					+ "\n";		
	    	m_Logger.log(Level.FINER, "Test Connection: Name - " + l_Name);
		}

		result += "\n\nSUCCESS\n";

		return result;
	}

	
	// Check DB connection for where we have server & DB name
	public String testDBConnection(String srv,
								   String inst,
			                       String db)  throws SQLException, ClassNotFoundException, IOException
	{
		String result = new String();
		
		String l_Catalog = new String();
		String l_Schema  = new String();
		String l_Table   = new String();
		String l_Column  = new String();
		String l_DatType = new String();
		
		// TO DO
		// When testing the built app on other machines, got error reported as here:
		// https://blogs.msdn.microsoft.com/psssql/2015/01/09/jdbc-this-driver-is-not-configured-for-integrated-authentication/
		
		// Actually....
		// http://blogs.msdn.com/b/jdbcteam/archive/2007/06/18/com-microsoft-sqlserver-jdbc-sqlserverexception-this-driver-is-not-configured-for-integrated-authentication.aspx
		// suggests that I need to copy ..
		/*
		 * 
On a Windows platform, when trying to connect to SQL Server using Integrated Authentication, you may see this exception:
    com.microsoft.sqlserver.jdbc.SQLServerException: This driver is not configured for integrated authentication.
along with the following trace message:
    com.microsoft.sqlserver.jdbc.AuthenticationJNI <clinit>
    WARNING: Failed to load the sqljdbc_auth.dll

This generally indicates that the driver can not find the appropriate sqljdbc_auth.dll in the JVM library path.  To correct the problem, please use the java.exe -D option to specify the "java.library.path" system property value.  You will want to specify the full path to the directory contain the sqljdbc_auth.dll.
    For example:  java -cp .;"c:\jdbcv1_2\sqljdbc.jar" -Djava.library.path="c:\jdbcv1_2\auth\x86" myApp

Alternatively, you can copy the sqljdbc_auth.dll to a directory in the search path (example: the local directory where you are executing your application).
		 * 
		 */
		
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");	
		//Connection conn = DriverManager.getConnection("jdbc:sqlserver://192.168.1.123\\ACCUCHEK360;integratedSecurity=true;databaseName=KATSERVER_FUJI_ACCUCHEK_1");
		String connString = "jdbc:sqlserver://" + srv + "\\" + inst + ";integratedSecurity=true;databaseName=" + db +";user=sa;";
		Connection conn = DriverManager.getConnection(connString);
		Statement sta = conn.createStatement();
    	m_Logger.log(Level.FINE, "Test Connection: Successfully connected to SQL Server: " + srv + "/" + inst);
    	
		// Get a list of Tables/Columns from this Database
		String Sql = "SELECT * FROM information_schema.columns";
    	m_Logger.log(Level.FINE, "Test Connection: SQL Contents modified as follows " + Sql);

		ResultSet rs = sta.executeQuery(Sql);
    	m_Logger.log(Level.FINE, "Test Connection: Successfully executed query");

		rawResultsFromDB.clear();
		while (rs.next()) 
		{
			result += " ";
			l_Catalog = rs.getString("TABLE_CATALOG");
			l_Schema  = rs.getString("TABLE_SCHEMA");
			l_Table   = rs.getString("TABLE_NAME");
			l_Column  = rs.getString("COLUMN_NAME");
			l_DatType = rs.getString("DATA_TYPE");

			result += 
					  " cat:" + l_Catalog
					+ " sch:" + l_Schema
					+ " tbl:" + l_Table
					+ " col:" + l_Column
					+ " typ:" + l_DatType
					+ "\n";
			
	    	m_Logger.log(Level.FINE, "Test Connection: " +
					"TABLE_CATALOG(" + l_Catalog + ") " +
					"TABLE_SCHEMA("  + l_Schema  + ") " +
					"TABLE_NAME("    + l_Table   + ") " +
					"COLUMN_NAME("   + l_Column  + ") " +
					"DATA_TYPE("     + l_DatType + ") "
					);

		}

		result += "\n\nSUCCESS\n";

		return result;

	}
			
	public void loadDBResults(String srv,
							  String inst,
						      String db,
						      String fileName,
						      Date   startDate,
						      Date   endDate) throws SQLException, ClassNotFoundException, IOException
	{
		if (PrefsNightScoutLoader.getInstance().isM_UseMongoForRocheResults())
		{
			loadDBRawResultsForTest(startDate, endDate);
		}
		else
		{
			loadDBRawResults(srv, inst, db, fileName, startDate, endDate);
		}
		convertDBResultsToTreatments();
	}
	
	protected String getDevice()
	{
		return "RocheCombo";
	}

	
	private void loadDBRawResults(String srv,
								  String inst,
								  String db,
								  String fileName,
								  Date   startDate,
								  Date   endDate) throws SQLException, ClassNotFoundException, IOException
	{
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");	
		//Connection conn = DriverManager.getConnection("jdbc:sqlserver://192.168.1.123\\ACCUCHEK360;integratedSecurity=true;databaseName=KATSERVER_FUJI_ACCUCHEK_1");
		String connString = "jdbc:sqlserver://" + srv  + "\\" + inst + ";integratedSecurity=true;databaseName=" + db;
		Connection conn = DriverManager.getConnection(connString);
		Statement sta = conn.createStatement();
    	m_Logger.log(Level.FINE, "loadDBRawResults: Successfully connected to SQL Server: " + srv + "/" + inst);
		String Sql_raw = readFile(fileName);
    	m_Logger.log(Level.FINE, "loadDBRawResults: SQL Contents loaded as follows " + Sql_raw);

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");

		// Now replace the date strings...
		String Sql = Sql_raw.replace("XXX_START_DATE_XXX", 
				dateFormat.format(startDate)).replace("YYY_END_DATE_YYY", 
						dateFormat.format(endDate).replace("ZZZ_DB_ZZZ", db));  
    	m_Logger.log(Level.FINER, "loadDBRawResults: SQL Contents modified as follows " + Sql);
		
		ResultSet rs = sta.executeQuery(Sql);
    	m_Logger.log(Level.FINE, "loadDBRawResults: Successfully executed query");

		rawResultsFromDB.clear();
		while (rs.next()) 
		{
			// 1 Feb 2016
			// Now switch to subclass instead
			// ResultFromDB res = new ResultFromDB(rs);
			
			DBResult res = new DBResultRoche(rs);
			rawResultsFromDB.add(res);
			//System.out.println(res.toString());
	    	m_Logger.log(Level.FINEST, "loadDBRawResults: Read Result from DB: " + res.toString());
		}
	}
	
	@SuppressWarnings("deprecation")
	public void loadDBRawResultsForTest(Date   startDate,
				Date   endDate) throws UnknownHostException
	{
		MongoClient dbClient;
		dbClient = new MongoClient( "localhost" );
		// dbClient = new MongoClient();
    	m_Logger.log(Level.FINE, "loadDBRawResultsForTest: Local MongoDB connection successfull ");

		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		String startString = new String(df.format(startDate));
		String endString   = new String(df.format(endDate));
		
		// Get a list of databases.
    	m_Logger.log(Level.FINE, "loadDBRawResultsForTest: Printing a list of databases... ");
		for (String s : dbClient.getDatabaseNames()) 
		{
	    	m_Logger.log(Level.FINE, "loadDBRawResultsForTest: Database - " + s);
		}	
		
		DB db = dbClient.getDB( "dexcom_db" );
		// 
		// Print out all the collections in the users database
    	m_Logger.log(Level.FINE, "loadDBRawResultsForTest: Printing all collections in DB dexcom_db ... - ");
		Set<String> colls = db.getCollectionNames();
		for (String s : colls) 
		{
	    	m_Logger.log(Level.FINE, "loadDBRawResultsForTest: Collection - " + s);
		} 
		
		resultTreatments = new ArrayList<DBResult>();
		
		// Get the players collection
		DBCollection coll = db.getCollection("Roche_Results");
		// Retrieve all the documents
		
		BasicDBObject query = new BasicDBObject();
		query.put("Time", BasicDBObjectBuilder.start("$gte", startString).add("$lte", endString).get());
		
		/*
		* Tried this to avoid seeing ISODate in MongoDB.
		* However, see this prevents resuts coming back as date correctly - get exception caught
		* 
		* 		// Create an instance of SimpleDateFormat used for formatting 
		// the string representation of date (month/day/year)
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startString = new String(df.format(startDate));
		String endString   = new String(df.format(endDate));
		
		query.put("Time", BasicDBObjectBuilder.start("$gte", startString).add("$lte", endString).get());
		*/
		
    	m_Logger.log(Level.FINE, "loadDBRawResultsForTest: Roche Data Mongo Query is now - " + query.toString());

		DBCursor cursor = coll.find(query);

		// Sort by time
		cursor.sort(new BasicDBObject("Time", 1));
		
		rawResultsFromDB.clear();
		for (DBObject rs: cursor)
		{
			//System.out.println(cursor.next());
			
			// Now create Result objects for each document and store into array
			//ResultFromDB res = new ResultFromDB(rs);
			
			// Switch to new object type
			DBResult res = new DBResultNightScout(rs, true);
			
			rawResultsFromDB.add(res);
	    	m_Logger.log(Level.FINEST, "loadDBRawResultsForTest: Read Result from DB: " + res.toString());

		}
		
		dbClient.close();
		
	}


	private String readFile( String file ) throws IOException 
	{
	    BufferedReader reader = new BufferedReader( new FileReader (file));
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");
    	m_Logger.log(Level.FINE, "readFile: Reading contents of file - " + file);

	    try 
	    {
	        while( ( line = reader.readLine() ) != null ) 
	        {
	            stringBuilder.append( line );
	            stringBuilder.append( ls );
	        }

	        return stringBuilder.toString();
	    } 
	    finally 
	    {
	        reader.close();
	    }
	}
}
