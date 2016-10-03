package davidRichardson;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.logging.Level;

// First attempt at reading the OmniPod binary file

public class DataLoadOmniPod extends DataLoadBase
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());


	private byte[]   m_OmniPodBytes = null;
	private String   m_initFilename = null;  // Path to Binary File with OmniPod results in it.
	
	private ArrayList<OmniPodBinaryFileSection> m_OmniPodBinaryFileSection;
	
//	private String   m_IBFVersion   = new String();
//	private String   m_PDMVersion   = new String();
	
	private static boolean m_Initialized = false;

	DataLoadOmniPod()
	{
		if (!m_Initialized)
		{
			initialize();
		}
	}
	
	private void initialize()
	{
		m_Initialized = true;
		
		// Load up the FileSection list
	}

	private void initializeFileSections()
	{
		// Fixed Records
		
		m_OmniPodBinaryFileSection.add(
				new OmniPodBinaryFileSection("ibf_version",
						"6S8z8z", new String[]{"ibf_maj", "ibf_min", "ibf_patch",
					         "eng_maj", "eng_min", "eng_patch",
					         "vendorid", "productid"}));

		m_OmniPodBinaryFileSection.add(
				new OmniPodBinaryFileSection("pdm_version",
						"3S", new String[]{"pdm_maj", "pdm_min", "pdm_patch"}));

		m_OmniPodBinaryFileSection.add(
				new OmniPodBinaryFileSection("mfg_data",
						"??z", new String[]{"data"}));
		
		m_OmniPodBinaryFileSection.add(
				new OmniPodBinaryFileSection("basal_programs_hdr",
						"3s", new String[]{"num_progrs", "enabled_idx", "max_name_size"}));
		
		m_OmniPodBinaryFileSection.add(
				new OmniPodBinaryFileSection("eeprom_settings",
						"13.4i2b4.b5.b.bb8.i19.7b3sb19.bi", 
						new String[]{
						        "BOLUS_INCR",
						        "BOLUS_MAX",
						        "BASAL_MAX",
						        "LOW_VOL",
						        "AUTO_OFF",
						        "LANGUAGE",
						        "EXPIRE_ALERT",
						        "BG_REMINDER",
						        "CONF_ALERT",
						        "REMDR_ALERT",
						        "REMOTE_ID",
						        "TEMP_BAS_TYPE",
						        "EXT_BOL_TYPE",
						        "BOL_REMINDER",
						        "BOL_CALCS",
						        "BOL_CALCS_REVERSE",
						        "BG_DISPLAY",
						        "BG_SOUND",
						        "BG_MIN",
						        "BG_GOAL_LOW",
						        "BG_GOAL_UP",
						        "INSULIN_DURATION",
						        "ALARM_REPAIR_COUNT",
						        "PDM_CONFIG"
						}));
		
		m_OmniPodBinaryFileSection.add(
				new OmniPodBinaryFileSection("profile_hdr",
						"b6.Si", new String[]{"profile_idx", "error_code", "operation_time"}));

		m_OmniPodBinaryFileSection.add(
				new OmniPodBinaryFileSection("log_hdr",
						"7bS3b.S", new String[]{
							      "logs_info_revision",
							      "insulin_history_revision",
							      "alarm_history_revision",
							      "blood_glucose_revision",
							      "insulet_stats_revision",
							      "day",
							      "month",
							      "year",
							      "seconds",
							      "minutes",
							      "hours",
							      "num_log_descriptions"
						}));
		
		m_OmniPodBinaryFileSection.add(
				new OmniPodBinaryFileSection("log_description",
						"5S2N", new String[]{
							      "log_index", "backup", "location", "has_variable", "record_size",
							      "first_index", "last_index"
				}));

		m_OmniPodBinaryFileSection.add(
				new OmniPodBinaryFileSection("log_record",
						"bNSSbbsbbb.i", new String[]{
								"log_id", "log_index", "record_size", "error_code",
							      "day", "month", "year", "seconds", "minutes", "hours",
							      "secs_since_powerup"
				}));

		m_OmniPodBinaryFileSection.add(
				new OmniPodBinaryFileSection("history_record",
						"bNSSbbsbbb.ins..", new String[]{
							      "log_id", "log_index", "record_size", "error_code",
							      "day", "month", "year", "seconds", "minutes", "hours",
							      "secs_since_powerup", "rectype", "flags"
				}));

		
		// Variable Records
		
		
		// Need to think how the results can get stored back and how the validator will work.
	}
	
	@Override
	protected String getDevice() 
	{
		return "OmniPod";
	}

	public void loadDBResults() throws UnknownHostException, SQLException, ClassNotFoundException, IOException 
	{
		readSmallBinaryFile(m_initFilename); 
	}

	void initialize(String filePath)
	{
		m_initFilename = new String(filePath);
	}

	public static boolean isOmniPod(String filePath)
	{
		return true; // Return true for now until we figure out how to tell :-)
	}

	void readSmallBinaryFile(String aFileName) throws IOException 
	{
		Path path = Paths.get(aFileName);
		m_OmniPodBytes = Files.readAllBytes(path);

		m_Logger.log(Level.INFO, "in readSmallBinaryFile");
		m_Logger.log(Level.INFO, "m_OmniPodBytes - length: " + m_OmniPodBytes.length);
		m_Logger.log(Level.INFO, "m_OmniPodBytes: " + m_OmniPodBytes);
		
//		for (int c = 0; c < m_OmniPodBytes.length; c++)
//		{
//			m_Logger.log(Level.FINEST, "m_OmniPodBytes[" + c + "]: " + m_OmniPodBytes[c]);
//		}
	}
	
	void interpretBinaryFile()
	{
		int offset = 0;
		
		offset = getIBFVersion(offset);
		
		offset = getPDMVersion(offset);
		
	}
	
	void unpack(String section)
	{
		// This needs to parse the format, look for section in the list
		// For each type, call relevant binarystruct method, store results
		// then call validator
		
		// Might be better placed in binary struct -- tbd
		
		
	}
	
	private BinaryStruct parseFormat(String format)
	{
		BinaryStruct result = new BinaryStruct();
		
		int count = 0;
		String exp = new String("");
		
		Pattern formatPattern = Pattern.compile("([0-9]*)([a-zA-Z.])");
		Matcher formatMatcher = formatPattern.matcher(format);

		if (formatMatcher.find())
		{
			count = Integer.parseInt(formatMatcher.group(1));
			exp   = formatMatcher.group(2);
			
			

		}

		return result;
	}
	
	private int getIBFVersion(int offset)
	{
		int result = offset;
		
//		unpack();
		
		return result;
	}
	
	private int getPDMVersion(int offset)
	{
		int result = offset;
		
		return result;

	}

}
