package davidRichardson;

import com.mongodb.BasicDBObject;

public interface DBResultInterface 
{
	public long getM_EpochMillies();
	
	// Derived classes will implement the concept of proximity duplicates
	void setImpactOfProximity();	
	
	void determineWhetherInProximity();	

	BasicDBObject createNightScoutObject();
}
