package davidRichardson;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.UnknownHostException;

public abstract class DataLoadBase 
{

	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	// Loaded from raw device such as meter, pump DB or file
	protected ArrayList <DBResult> rawResultsFromDB;

	// Merge results into treatments ready for comparison/loading to Nightscout
	protected ArrayList <DBResult> resultTreatments;

	
	public void clearLists()
	{
		rawResultsFromDB.clear();
		resultTreatments.clear();
	}
	
	public DataLoadBase()
	{
		rawResultsFromDB = new ArrayList<DBResult>();
		resultTreatments = new ArrayList<DBResult>();
	}
	
	public ArrayList <DBResult> getResultsTreatments()
	{
		return resultTreatments;
	}
	
	// Derived classes must return a String that gets used in storing treatments.
	protected abstract String getDevice();
	
	// Derived classes must be able to support a load.
	// This is used in the thread class
	public abstract void loadDBResults() throws UnknownHostException, SQLException, ClassNotFoundException, IOException;
	
	// Provide a common way of converting 
	protected void convertDBResultsToTreatments()
	{
		// Iterate over the raw results and convert results into NightScout Treatments
		// according to this specification:
		//
		// BG, Carbs etc in close proximity 

		DBResult pendingRes = new DBResult();
		boolean readyForNextRes = true;
		boolean pendingResAdded = false;
		
		// Assume the list is ordered in time.
		for (DBResult res : rawResultsFromDB)
		{
			m_Logger.log(Level.FINEST, "<"+this.getClass().getName()+">" + "--------------------------------------------");
			m_Logger.log(Level.FINEST, "<"+this.getClass().getName()+">" + "Pending Res " + pendingRes.rawToString());
			m_Logger.log(Level.FINEST, "<"+this.getClass().getName()+">" + "Res " + res.rawToString());

			pendingResAdded = false;
			
			if (readyForNextRes)
			{
				pendingRes = new DBResult(res, getDevice());
				readyForNextRes = false;				
			}
			else
			{
				readyForNextRes = false;
				
				if (!pendingRes.discard())
				{
					if (pendingRes.pending())
					{
						boolean merged = pendingRes.merge(res);
						
						// Is the merge complete?
						if (!pendingRes.pending())
						{
							// Store current pending item
							resultTreatments.add(pendingRes);
							readyForNextRes = true;
							pendingResAdded = true;
						}
						
						if (!merged)
						{
							// Res is potentially of interest so keep it & check
							pendingRes = new DBResult(res, getDevice());
							readyForNextRes = false;
						}
					}
					else if (pendingRes != null && pendingResAdded == false)
					{
						resultTreatments.add(pendingRes);
						readyForNextRes = true;
					}					
				}
				else
				{
					// Discard it and get the next result
					pendingRes = new DBResult(res, getDevice());
					readyForNextRes = false;					
				}
			}
		}
		
		// Store last result
		if (pendingRes != null /*&& pendingResAdded == false*/)
		{
			resultTreatments.add(pendingRes);
		}

	}

}
