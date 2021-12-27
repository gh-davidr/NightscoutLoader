package loader;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.logging.Logger;

import control.MyLogger;
import miscellaneous.OmniPodBinaryFileSection;
import miscellaneous.OmniPodBinaryLogFileSection;
import miscellaneous.OmniPodValidator;

import java.util.ArrayList;
import java.util.logging.Level;

// First attempt at reading the OmniPod binary file

public class DataLoadOmniPod extends DataLoadBase
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());


	private byte[]   m_OmniPodBytes = null;
	private String   m_initFilename = null;  // Path to Binary File with OmniPod results in it.
	
	private ArrayList<OmniPodBinaryFileSection>     m_OmniPodBinaryFileSectionList;
	private ArrayList<OmniPodBinaryLogFileSection>  m_OmniPodBinaryLogFileSectionList;
	
//	private String   m_IBFVersion   = new String();
//	private String   m_PDMVersion   = new String();
	
	private static boolean m_Initialized = false;
	
	enum BinaryFileValueType
	{
		Unknown,
		String,
		Integer,
		Double,
	}
	
	public class BinaryFileValue
	{
		private BinaryFileValueType m_Type       = BinaryFileValueType.Unknown;
		private String              m_StringVal  = new String();
		private Integer             m_IntegerVal = Integer.valueOf(0);
		private Double              m_DoubleVal  = Double.valueOf(0.0);
	//	private int                 m_AddOffset  = 0;
		
		BinaryFileValue()
		{
			
		}

		/**
		 * @return the m_Type
		 */
		public synchronized BinaryFileValueType getM_Type() {
			return m_Type;
		}

		/**
		 * @param m_Type the m_Type to set
		 */
		public synchronized void setM_Type(BinaryFileValueType m_Type) {
			this.m_Type = m_Type;
		}

		/**
		 * @return the m_StringVal
		 */
		public synchronized String getM_StringVal() {
			return m_StringVal;
		}

		/**
		 * @param m_StringVal the m_StringVal to set
		 */
		public synchronized void setM_StringVal(String m_StringVal) {
			this.m_StringVal = m_StringVal;
			setM_Type(BinaryFileValueType.String);

		}

		/**
		 * @return the m_IntegerVal
		 */
		public synchronized Integer getM_IntegerVal() {
			return m_IntegerVal;
		}

		/**
		 * @param m_IntegerVal the m_IntegerVal to set
		 */
		public synchronized void setM_IntegerVal(Integer m_IntegerVal) {
			this.m_IntegerVal = m_IntegerVal;
			setM_Type(BinaryFileValueType.Integer);
		}

		/**
		 * @return the m_DoubleVal
		 */
		public synchronized Double getM_DoubleVal() {
			return m_DoubleVal;
		}

		/**
		 * @param m_DoubleVal the m_DoubleVal to set
		 */
		public synchronized void setM_DoubleVal(Double m_DoubleVal) {
			this.m_DoubleVal = m_DoubleVal;
			setM_Type(BinaryFileValueType.Double);
		}
	}

	public DataLoadOmniPod()
	{
		m_OmniPodBinaryFileSectionList    = new ArrayList<OmniPodBinaryFileSection>();
		m_OmniPodBinaryLogFileSectionList = new ArrayList<OmniPodBinaryLogFileSection>();
		
		if (!m_Initialized)
		{
			initialize();
		}
	}
	
	private void initialize()
	{
		m_Initialized = true;
		
		// Load up the FileSection list
		initializeFileSections();
	}

	private void initializeFileSections()
	{
		// Fixed Records
		
		m_OmniPodBinaryFileSectionList.add(
				new OmniPodBinaryFileSection("ibf_version",
						"6S8z8z", new String[]{"ibf_maj", "ibf_min", "ibf_patch",
					         "eng_maj", "eng_min", "eng_patch",
					         "vendorid", "productid"}));

		m_OmniPodBinaryFileSectionList.add(
				new OmniPodBinaryFileSection("pdm_version",
						"3S", new String[]{"pdm_maj", "pdm_min", "pdm_patch"}));

		m_OmniPodBinaryFileSectionList.add(
				new OmniPodBinaryFileSection("mfg_data",
						"??z", new String[]{"data"}));
		
		m_OmniPodBinaryFileSectionList.add(
				new OmniPodBinaryFileSection("basal_programs_hdr",
						"3s", new String[]{"num_progrs", "enabled_idx", "max_name_size"}));

		m_OmniPodBinaryFileSectionList.add(
				new OmniPodBinaryFileSection("basal_programs_name",
						"S??z", new String[]{"index", "name"}));
		
		m_OmniPodBinaryFileSectionList.add(
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
		
		m_OmniPodBinaryFileSectionList.add(
				new OmniPodBinaryFileSection("profile_hdr",
						"b6.Si", new String[]{"profile_idx", "error_code", "operation_time"}));

		m_OmniPodBinaryFileSectionList.add(
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
		
		m_OmniPodBinaryFileSectionList.add(
				new OmniPodBinaryFileSection("log_description",
						"5S2N", new String[]{
							      "log_index", "backup", "location", "has_variable", "record_size",
							      "first_index", "last_index"
				}));

		m_OmniPodBinaryFileSectionList.add(
				new OmniPodBinaryFileSection("log_record",
						"bNSSbbsbbb.i", new String[]{
								"log_id", "log_index", "record_size", "error_code",
							      "day", "month", "year", "seconds", "minutes", "hours",
							      "secs_since_powerup"
				}));

		m_OmniPodBinaryFileSectionList.add(
				new OmniPodBinaryFileSection("history_record",
						"bNSSbbsbbb.ins..", new String[]{
							      "log_id", "log_index", "record_size", "error_code",
							      "day", "month", "year", "seconds", "minutes", "hours",
							      "secs_since_powerup", "rectype", "flags"
				}));

		
		// Variable Records
		
		m_OmniPodBinaryFileSectionList.add(
				new OmniPodBinaryFileSection("history_record",
						"bNSSbbsbbb.ins..", new String[]{
							      "log_id", "log_index", "record_size", "error_code",
							      "day", "month", "year", "seconds", "minutes", "hours",
							      "secs_since_powerup", "rectype", "flags"
				}));
		
		
		// Now the Log Records section
		
 		m_OmniPodBinaryLogFileSectionList.add(
				new OmniPodBinaryLogFileSection(
						0x000, new OmniPodBinaryFileSection("End_Marker", "", null)));

		m_OmniPodBinaryLogFileSectionList.add(
				new OmniPodBinaryLogFileSection(
						0x0001, new OmniPodBinaryFileSection("Deactivate", "", null)));

		m_OmniPodBinaryLogFileSectionList.add(
				new OmniPodBinaryLogFileSection(
						0x0002, new OmniPodBinaryFileSection("Time_Change", "3b.",
								new String[]{ "seconds", "minutes", "hours"})));

		// Next one requires use of validator to update values!!
		// Need to see how field values are set and inferred in a general
		// class - perhaps casting from Object will do it.
		
		m_OmniPodBinaryLogFileSectionList.add(
				new OmniPodBinaryLogFileSection(
						0x0002, new OmniPodBinaryFileSection("Bolus", "isss",
								new String[]{ "volume", "extended_duration_minutes", 
											  "calculation_record_offset", "immediate_duration_seconds"},
								new OmniPodValidator()
						{
							private OmniPodDetailsBolus m_OmniPodDetailsBolus = new OmniPodDetailsBolus(); 
							
							public boolean valid()
							{
								boolean result = true;
								return result;
							}
							
							public OmniPodDetails getDetails()
							{
								return m_OmniPodDetailsBolus;
							}
						}
								)));
		
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
		interpretBinaryFile();
	}

	public void initialize(String filePath)
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
		
		offset = getManufacturingData(offset);
		
		offset = getBasalProgramNames(offset);
		
		offset = getEEPromSettings(offset);

		offset = getLogDescriptions(offset);

	}
	
	// See unpack in struct.js from tidepool on which this is modeled
	void unpack(String section)
	{
		// This needs to parse the format, look for section in the list
		// For each type, call relevant binarystruct method, store results
		// then call validator
		
		// Might be better placed in binary struct -- tbd
		
		
	}
	
//	// See parseformat in struct.js from tidepool on which this is modeled
//	private ArrayList<BinaryStruct> parseFormat(String format)
//	{
//		ArrayList<BinaryStruct> result = new ArrayList<BinaryStruct>();
//		
//		int count = 0;
//		String exp = new String("");
//		
//		Pattern formatPattern = Pattern.compile("(([0-9]*)([a-zA-Z.]))+");
//		Matcher formatMatcher = formatPattern.matcher(format);
//
//		while (formatMatcher.find())
//		{
//			count = Integer.parseInt(formatMatcher.group(1));
//			exp   = formatMatcher.group(2);
//			BinaryStruct entry = new BinaryStruct(count, exp);
//			result.add(entry);
//		}
//
//		return result;
//	}
	
	private int getIBFVersion(int offset)
	{
		int result = offset;
		
		// 1 Locate the section in header
		// 2 Unpack bytes according to specification
		// 3 move offset ahead
		
//		OmniPodBinaryFileSection section = locateFileSection("ibf_version");
		
//		unpack();
		
		return result;
	}
	
//	private OmniPodBinaryFileSection locateFileSection(String header)
//	{
//		OmniPodBinaryFileSection result = null;
//		
//		for (OmniPodBinaryFileSection c : m_OmniPodBinaryFileSectionList)
//		{
//			if (c.getM_SectionHeader().equals(header))
//			{
//				result = c;
//				break;
//			}
//		}
//		
//		return result;
//	}
	
	private int getPDMVersion(int offset)
	{
		int result = offset;
		
		return result;

	}
	
	private int getManufacturingData(int offset)
	{
		int result = offset;
		
		return result;

	}
	
	private int getBasalProgramNames(int offset)
	{
		int result = offset;
		
		return result;

	}
	
	private int getEEPromSettings(int offset)
	{
		int result = offset;
		
		return result;

	}

	private int getLogDescriptions(int offset)
	{
		int result = offset;
		
		return result;

	}


}
