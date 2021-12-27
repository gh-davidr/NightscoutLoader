package loader;

import java.io.IOException;
import java.sql.SQLException;

public abstract class DataLoadFile extends DataLoadBase 
{
	public abstract void loadDBResults(String fileName) throws  IOException, SQLException, ClassNotFoundException;
	
	public abstract void initialize(String filename);
}
