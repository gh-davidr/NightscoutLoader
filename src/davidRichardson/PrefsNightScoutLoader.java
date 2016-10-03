package davidRichardson;
import java.sql.Date;
import java.util.Calendar;
import java.util.prefs.Preferences;

public class PrefsNightScoutLoader 
{
	// Implements the Singleton Design Pattern
	private static PrefsNightScoutLoader m_Instance=null;
	
	public static PrefsNightScoutLoader getInstance()
	{
		if (m_Instance == null)
		{
			m_Instance = new PrefsNightScoutLoader();
		}
		return m_Instance;
	}
	
	// Prevent instantiation
	private PrefsNightScoutLoader()
	{
//		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
//		prefs.put("ProductKey", "NightScoutLoader");
		
		m_SelectedMeter = new String();
		m_SQLFile = new String();
		m_SQLDBServerHost = new String();
		m_SQLDBServerInstance = new String();
		m_SQLDBName = new String();
		m_MedtronicMeterPumpResultFilePath = new String();
		m_DiasendMeterPumpResultFilePath = new String();
		m_OmniPodMeterPumpResultFilePath = new String();
		m_NightscoutMongoServer = new String();
		m_NightscoutMongoDB = new String();
		m_NightscoutMongoCollection = new String();
		m_NightscoutSensorMongoCollection = new String();
		m_MongoMeterCollection = new String();
		m_LogFile = new String();
		m_InputDateFormat = new String();
		m_Timezone = new String();
		
		// Get error with below, so try above instead.
	    prefs = Preferences.userRoot().node(this.getClass().getName());

		loadPreferences();
	}

	// Not sure how much use this will be because of the need for double escaping!
	
	private Preferences prefs;
	
	
	// Found this from here
	// http://www.vogella.com/tutorials/JavaPreferences/article.html
	
	// Note the defaults
	final private int    def_M_BGUnits                           = 0;  // mmol
	final private String def_M_SelectedMeter                     = "Roche Combo";
	final private int    def_M_DaysToLoad                        = 45;
	final private String def_M_SQLFile                           = "C:\\Local_Data\\SQL_Query_For_Java.txt";
	final private String def_M_SQLDBServerHost                   = "192.168.1.123";
	final private String def_M_SQLDBServerInstance               = "ACCUCHEK360";
	final private String def_M_SQLDBName                         = "KATSERVER_FUJI_ACCUCHEK_1";
	final private String def_M_MedtronicMeterPumpResultFilePath  = "";
	final private String def_M_DiasendMeterPumpResultFilePath    = "";
	final private String def_M_OmniPodMeterPumpResultFilePath    = "";
	final private String def_M_ExportFilePath                    = "";
	final private String def_M_DownloadTreatmentFilePath         = "";
	final private String def_M_DownloadSensorFilePath            = "";
	final private String def_M_AnalysisFilePath                  = "";

//	final private String def_M_NightscoutMongoServer             = "localhost";
	final private String def_M_NightscoutMongoServer             = "";
//	m_NightscoutMongoPort               = 27017;
	final private String def_M_NightscoutMongoDB                 = "dexcom_db";
	final private String def_M_NightscoutMongoCollection         = "treatments";
	final private String def_M_NightscoutSensorMongoCollection   = "entries";
	final private String def_M_NightscoutAuditCollection         = "treatments_loads";
	final private String def_M_MongoMeterCollection              = "Roche_Results";
	
	final private Boolean def_M_AdvancedOptions                  = false;
	final private int    def_M_MaxMinsBetweenSameMealEvent       = 30; // Allow up to half an hour between BG, Carbs & Ins
	final private int    def_M_MaxMinsBetweenSameCorrectionEvent = 5;  // Allow up to 5 mins between BG & Ins	
	final private Boolean def_M_UseMongoForRocheResults          = false; 
	
	final private int     def_M_LogLevel                         = 0; // CHanged now.  It's an index onto 3 values
	final private String  def_M_LogFile                          = "C:\\temp\\NightscoutLoader_Log.txt";
	final private Boolean def_M_AttemptDiasendTempBasals         = false;
	final private Boolean def_M_AuditLogAllShown                 = true;
	
	/*Not Final!*/      
	      private String  def_M_InputDateFormat                  = "Default";
	/*Not Final!*/      
	      
	final private String  def_M_Timezone                         = "Local Timezone";
	final private int     def_M_ProximityMinutes                 = 15;
	final private boolean def_M_ProximityTypeCheck               = true;
	final private int     def_M_ProximityValueCheck              = 1;
	final private int     def_M_MongoDBAlerterCheckInterval      = 10;

	// Analyzer Controls
	final private int     def_M_AnalyzerDaysBack                 = 14;
	final private long    def_M_AnalyzerStartDateLong            = 0;
	final private long    def_M_AnalyzerEndDateLong              = 0;

	final private String  def_M_AnalyzerBreakfastTimeStart       = "06:00:00";
	final private String  def_M_AnalyzerLunchTimeStart           = "12:00:00";
	final private String  def_M_AnalyzerDinnerTimeStart          = "17:00:00";
	final private String  def_M_AnalyzerBedTimeStart             = "22:00:00";
	final private int     def_M_AnalyzerDaysBackToReview         = 14;
	final private double  def_M_AnalyzerHighThreshold            = 14.0;
	final private double  def_M_AnalyzerHighThresholdRelevanceFactor = 1.5;      // We rank results from 1 to 10. 1 being 7 or under and 10 being worst which is this factor x high thresh
	final private double  def_M_AnalyzerLowThreshold             = 3.1;
	final private double  def_M_AnalyzerLowThresholdRelevanceFactor  = 0.66666;  // We rank results from 1 to 10. 1 being 4 and 10 being worst which is this factor x low thresh
	final private double  def_M_AnalyzerIndividualTrendRatio       = 0.3333;   // For example, if set to 0.2 then more than 1 in 5 will trigger trend
	final private double  def_M_AnalyzerOvernightChangeTrendRatio  = 0.3333;   // For example, if set to 0.2 then more than 1 in 5 will trigger trend
	final private double  def_M_AnalyzerBreakfastChangeTrendRatio  = 0.3333;   // For example, if set to 0.2 then more than 1 in 5 will trigger trend
	final private double  def_M_AnalyzerLunchChangeTrendRatio      = 0.3333;   // For example, if set to 0.2 then more than 1 in 5 will trigger trend
	final private double  def_M_AnalyzerDinnerChangeTrendRatio     = 0.3333;   // For example, if set to 0.2 then more than 1 in 5 will trigger trend
	final private int     def_M_AnalyzerMinMinsForTrendResults     = 120;      // Default of 2 hour gap needed for two results to be a trend
	final private double  def_M_AnalyzerHighFrequencyPercentage    = 10.0;     // See >10% percentage frequency, then mark as high frequency
	final private double  def_M_AnalyzerMediumFrequencyPercentage  = 5.0;      // See >5% percentage frequency, then mark as medium frequency (if below high)
	final private double  def_M_AnalyzerLowRangeThreshold          = 4.0;      // Assume UK units
	final private double  def_M_AnalyzerHighRangeThreshold         = 7.0;      // Assume UK units
	
	final private String  def_M_AnalyzerBedTrendStartStartTime     = "22:00:00";
	final private String  def_M_AnalyzerBedTrendStartEndTime       = "23:59:00";
	final private String  def_M_AnalyzerBedTrendEndStartTime       = "05:00:00";
	// Make end end deliberately late to ensure that the night time trend actually ends the following day!
	final private String  def_M_AnalyzerBedTrendEndEndTime         = "23:59:00";
	
	final private String  def_M_AnalyzerBadNightStartTime          = "01:00:00";
	final private String  def_M_AnalyzerBadNightEndTime            = "05:30:00";
	final private boolean def_M_AnalyzerCompressMealTrends         = false;
	final private boolean def_M_AnalyzerTotalRecurringTrendsOnly   = true;

	
	private int     m_BGUnits;    // 0 ==> mmol/L 1 ==> mg/
	private String  m_SelectedMeter;
	private int     m_DaysToLoad;
	private String  m_SQLFile;
	private String  m_SQLDBServerHost;
	private String  m_SQLDBServerInstance;
	private String  m_SQLDBName;
	private String  m_MedtronicMeterPumpResultFilePath;
	private String  m_DiasendMeterPumpResultFilePath;
	private String  m_OmniPodMeterPumpResultFilePath;
	private String  m_ExportFilePath;
	private String  m_DownloadTreatmentFilePath;
	private String  m_DownloadSensorFilePath;
	private String  m_AnalysisFilePath;
	private String  m_NightscoutMongoServer;
	//private int     m_NightscoutMongoPort;
	private String  m_NightscoutMongoDB;
	private String  m_NightscoutMongoCollection;
	private String  m_NightscoutSensorMongoCollection;
	private String  m_NightscoutAuditCollection;
	// Items below are for more advanced use only
	private boolean m_AdvancedOptions;
	private String  m_MongoMeterServer;     // Used for development 'on the road'
	private String  m_MongoMeterDB;         // Used for development 'on the road'
	private String  m_MongoMeterCollection; // Used for development 'on the road'
	private int     m_MaxMinsBetweenSameMealEvent;
	private int     m_MaxMinsBetweenSameCorrectionEvent;	
	private boolean m_UseMongoForRocheResults;
	private int     m_LogLevel;
	private String  m_LogFile;
	private boolean m_AttemptDiasendTempBasals;
	private boolean m_AuditLogAllShown;
	private String  m_InputDateFormat;
	private String  m_Timezone;
	private int     m_ProximityMinutes            = 15;
	private boolean m_ProximityTypeCheck          = true;
	private int     m_ProximityValueCheck         = 1;

	private int     m_MongoDBAlerterCheckInterval = 10;
	
	// Analyzer controls
	private int     m_AnalyzerDaysBack;
	
	private long    m_AnalyzerStartDateLong;
	private long    m_AnalyzerEndDateLong;

	private String  m_AnalyzerBreakfastTimeStart;
	private String  m_AnalyzerLunchTimeStart;
	private String  m_AnalyzerDinnerTimeStart;
	private String  m_AnalyzerBedTimeStart;
	private int     m_AnalyzerDaysBackToReview;
	private double  m_AnalyzerHighThreshold;
	private double  m_AnalyzerHighThresholdRelevanceFactor;
	private double  m_AnalyzerLowThreshold;
	private double  m_AnalyzerLowThresholdRelevanceFactor;

	private double  m_AnalyzerIndividualTrendRatio;   // For example, if set to 0.2 then more than 1 in 5 will trigger trend
	private double  m_AnalyzerOvernightChangeTrendRatio;   // For example, if set to 0.2 then more than 1 in 5 will trigger trend
	private double  m_AnalyzerBreakfastChangeTrendRatio;   // For example, if set to 0.2 then more than 1 in 5 will trigger trend
	private double  m_AnalyzerLunchChangeTrendRatio;   // For example, if set to 0.2 then more than 1 in 5 will trigger trend
	private double  m_AnalyzerDinnerChangeTrendRatio;   // For example, if set to 0.2 then more than 1 in 5 will trigger trend
	private int     m_AnalyzerMinMinsForTrendResults;
	private double  m_AnalyzerHighFrequencyPercentage;      // For example, analyzing 10 days and see 4 days with same trend == 0.4 so colour red
	private double  m_AnalyzerMediumFrequencyPercentage;    // For example, analyzing 10 days and see 3 days with same trend == 0.3 so colour amber
	
	
	private double  m_AnalyzerLowRangeThreshold;   // Typically 4.0 mmol/L  OR  72 mg/dl
	private double  m_AnalyzerHighRangeThreshold;  // Typically 7.0 mmol/L  OR 126 mg/dl
	
	private String  m_AnalyzerBedTrendStartStartTime;
	private String  m_AnalyzerBedTrendStartEndTime;
	private String  m_AnalyzerBedTrendEndStartTime;
	private String  m_AnalyzerBedTrendEndEndTime;
	
	private String  m_AnalyzerBadNightStartTime;  // Allow us to find results for BAD Nights - when up late testing
	private String  m_AnalyzerBadNightEndTime;  // Allow us to find results for BAD Nights - when up late testing
	private boolean m_AnalyzerCompressMealTrends; // Compresses some meal trends so rises & false are grouped 
	private boolean m_AnalyzerTotalRecurringTrendsOnly;  // 100% consists of trends that recur 2 or more times if true.  Else full trend list
	
			
	// Handles to retrieve preferences
	final private String pref_BGUnits                           = "NSL_BGUnits";
	final private String pref_SelectedMeter                     = "NSL_SelectedMeter";
	final private String pref_DaysToLoad                        = "NSL_DaysToLoad";
	final private String pref_SQLFile                           = "NSL_SQLFile";
	final private String pref_SQLDBServerHost                   = "NSL_SQLDBServer_Host";
	final private String pref_SQLDBServerInstance               = "NSL_SQLDBServer_Instance";
	final private String pref_SQLDBName                         = "NSL_SQLDBName";
	final private String pref_MedtronicMeterPumpResultFilePath  = "NSL_MedtronicMeterPumpResultFilePath";
	final private String pref_DiasendMeterPumpResultFilePath    = "NSL_m_DiasendMeterPumpResultFilePath";
	final private String pref_OmniPodMeterPumpResultFilePath    = "NSL_m_OmniPodMeterPumpResultFilePath";
	final private String pref_ExportFilePath                    = "NSL_ExportFilePath";
	final private String pref_DownloadTreatmentFilePath         = "NSL_DownloadTreatmentFilePath";
	final private String pref_DownloadSensorFilePath            = "NSL_DownloadSensorFilePath";
	final private String pref_AnalysisFilePath                  = "NSL_AnalysisFilePath";

	final private String pref_NightscoutMongoServer             = "NSL_NightscoutMongoServer";
//	final private String pref_NightscoutMongoPort               = "NSL_NightscoutMongoPort";
	final private String pref_NightscoutMongoDB                 = "NSL_NightscoutMongoDB";
	final private String pref_NightscoutMongoCollection         = "NSL_NightscoutMongoCollection";
	final private String pref_NightscoutSensorMongoCollection   = "NSL_NightscoutSensorMongoCollection";
	final private String pref_NightscoutAuditCollection         = "NSL_NightscoutAuditCollection";
	// Items below are for more advanced use only
	final private String pref_AdvancedOptions                   = "NSL_AdvancedOptions";
	final private String pref_MongoMeterServer                  = "NSL_MongoMeterServer";
	final private String pref_MongoMeterDB                      = "NSL_MongoMeterDB";
	final private String pref_MongoMeterCollection              = "NSL_MongoMeterCollection";
	final private String pref_MaxMinsBetweenSameMealEvent       = "NSL_MaxMinsBetweenSameMealEvent";
	final private String pref_MaxMinsBetweenSameCorrectionEvent = "NSL_MaxMinsBetweenSameCorrectionEvent";
	final private String pref_UseMongoForRocheResults           = "NSL_UseMongoForRocheResults";
	final private String pref_LogLevel                          = "NSL_LogLevel";
	final private String pref_LogFile                           = "NSL_LogFile";
	final private String pref_AttemptDiasendTempBasals          = "NSL_AttemptDiasendTempBasals";
	final private String pref_AuditLogAllShown                  = "NSL_AuditLogAllShown";   
	final private String pref_InputDateFormat                   = "NSL_InputDateFormat";
	final private String pref_Timezone                          = "NSL_Timezone";
	final private String pref_ProximityMinutes                  = "NSL_ProximityMinutes";
	final private String pref_ProximityTypeCheck                = "NSL_ProximityTypeCheck";
	final private String pref_ProximityValueCheck               = "ProximityValueCheck";

	final private String pref_MongoDBAlerterCheckInterval       = "NSL_MongoDBAlerterCheckInterval";	

	// Analyzer controls
	final private String  pref_AnalyzerDaysBack                     = "NSL_AnalyzerDaysBack";
	final private String  pref_AnalyzerStartDateLong                = "NSL_AalyzerStartDateLong";
	final private String  pref_AnalyzerEndDateLong                  = "NSL_AalyzerEndDateLong";
	final private String  pref_AnalyzerBreakfastTimeStart           = "NSL_AnalyzerBreakfastTimeStart";
	final private String  pref_AnalyzerLunchTimeStart               = "NSL_AnalyzerLunchTimeStart";
	final private String  pref_AnalyzerDinnerTimeStart              = "NSL_AnalyzerDinnerTimeStart";
	final private String  pref_AnalyzerBedTimeStart                 = "NSL_AnalyzerBedTimeStart";
	final private String  pref_AnalyzerDaysBackToReview             = "NSL_AnalyzerDaysBackToReview";
	final private String  pref_AnalyzerHighThreshold                = "NSL_AnalyzerHighThreshold";
	final private String  pref_AnalyzerHighThresholdRelevanceFactor = "NSL_AnalyzerHighThresholdRelevanceFactor";
	final private String  pref_AnalyzerLowThreshold                 = "NSL_AnalyzerLowThreshold";
	final private String  pref_AnalyzerLowThresholdRelevanceFactor  = "NSL_AnalyzerLowThresholdRelevanceFactor";
	final private String  pref_AnalyzerIndividualTrendRatio         = "NSL_AnalyzerIndividualTrendRatio";   
	final private String  pref_AnalyzerOvernightChangeTrendRatio    = "NSL_AnalyzerOvernightChangeTrendRatio";   
	final private String  pref_AnalyzerBreakfastChangeTrendRatio    = "NSL_AnalyzerBreakfastChangeTrendRatio";   
	final private String  pref_AnalyzerLunchChangeTrendRatio        = "NSL_AnalyzerLunchChangeTrendRatio";   
	final private String  pref_AnalyzerDinnerChangeTrendRatio       = "NSL_AnalyzerDinnerChangeTrendRatio";
	final private String  pref_AnalyzerMinMinsForTrendResults       = "NSL_AnalyzerMinMinsForTrendResults";
	final private String  pref_AnalyzerHighFrequencyPercentage      = "NSL_AnalyzerHighFrequencyPercentage";
	final private String  pref_AnalyzerMediumFrequencyPercentage    = "NSL_AnalyzerMediumFrequencyPercentage";
	final private String  pref_AnalyzerLowRangeThreshold            = "NSL_AnalyzerLowRangeThreshold";
	final private String  pref_AnalyzerHighRangeThreshold           = "NSL_AnalyzerHighRangeThreshold";
	
	final private String  pref_AnalyzerBedTrendStartStartTime       = "NSL_AnalyzerBedTrendStartStartTime";
	final private String  pref_AnalyzerBedTrendStartEndTime         = "NSL_AnalyzerBedTrendStartEndTime";
	final private String  pref_AnalyzerBedTrendEndStartTime         = "NSL_AnalyzerBedTrendEndStartTime";
	final private String  pref_AnalyzerBedTrendEndEndTime           = "NSL_AnalyzerBedTrendEndEndTime";
	final private String  pref_AnalyzerBadNightStartTime            = "NSL_AnalyzerBadNightStartTime";
	final private String  pref_AnalyzerBadNightEndTime              = "NSL_AnalyzerBadNightEndTime";
	final private String  pref_AnalyzerCompressMealTrends           = "NSL_AnalyzerCompressMealTrends";
	final private String  pref_AnalyzerTotalRecurringTrendsOnly     = "NSL_AnalyzerTotalRecurringTrendsOnly";
	
	
	public static int getBGUnitMultiplier()
	{
		int result = 1;  // Default is mmol/L
		
		if (PrefsNightScoutLoader.getInstance().getM_BGUnits() != 0)
		{
			result = 18;
		}
		
		return result;
	}
	
	public void loadDefaultPreferences()
	{
		// Note the defaults
		m_BGUnits                           = def_M_BGUnits;
		m_SelectedMeter                     = def_M_SelectedMeter;
		m_DaysToLoad                        = def_M_DaysToLoad;
		m_SQLFile                           = def_M_SQLFile;
		m_SQLDBServerHost                   = def_M_SQLDBServerHost;
		m_SQLDBServerInstance               = def_M_SQLDBServerInstance;
		m_SQLDBName                         = def_M_SQLDBName;
		m_MedtronicMeterPumpResultFilePath  = def_M_MedtronicMeterPumpResultFilePath;
		m_DiasendMeterPumpResultFilePath    = def_M_DiasendMeterPumpResultFilePath;
		m_OmniPodMeterPumpResultFilePath    = def_M_OmniPodMeterPumpResultFilePath;
		
		m_ExportFilePath                    = def_M_ExportFilePath;
		m_DownloadTreatmentFilePath         = def_M_DownloadTreatmentFilePath;
		m_DownloadSensorFilePath            = def_M_DownloadSensorFilePath;
		m_AnalysisFilePath                  = def_M_AnalysisFilePath;
				
		m_NightscoutMongoServer             = def_M_NightscoutMongoServer;
//		m_NightscoutMongoPort               = 27017;
		m_NightscoutMongoDB                 = def_M_NightscoutMongoDB;
		m_NightscoutMongoCollection         = def_M_NightscoutMongoCollection;
		m_NightscoutSensorMongoCollection   = def_M_NightscoutSensorMongoCollection;
		m_NightscoutAuditCollection         = def_M_NightscoutAuditCollection;
		m_MongoMeterCollection              = def_M_MongoMeterCollection;
		
		m_AdvancedOptions                   = def_M_AdvancedOptions;
		m_MaxMinsBetweenSameMealEvent       = def_M_MaxMinsBetweenSameMealEvent;
		m_MaxMinsBetweenSameCorrectionEvent = def_M_MaxMinsBetweenSameCorrectionEvent;	
		m_UseMongoForRocheResults           = def_M_UseMongoForRocheResults; 
		
		m_LogLevel                          = def_M_LogLevel;
		m_LogFile                           = def_M_LogFile;
		m_AttemptDiasendTempBasals          = def_M_AttemptDiasendTempBasals;
		m_AuditLogAllShown                  = def_M_AuditLogAllShown;
		m_InputDateFormat                   = def_M_InputDateFormat;
		m_Timezone                          = def_M_Timezone;
		m_ProximityMinutes                  = def_M_ProximityMinutes;
		m_ProximityTypeCheck                = def_M_ProximityTypeCheck;
		m_ProximityValueCheck               = def_M_ProximityValueCheck;
		
		m_MongoDBAlerterCheckInterval       = def_M_MongoDBAlerterCheckInterval;
		
		// Analyzer controls in a separate procedure since we want to call them from
		// Analyzer window
		loadAnalyzerDefaultPreferences();		
	}
	
	/**
	 * @return the def_M_Timezone
	 */
	public synchronized String getDef_M_Timezone() {
		return def_M_Timezone;
	}

	public void loadAnalyzerDefaultPreferences()
	{		
		// Analyzer controls
		m_AnalyzerDaysBack                     = def_M_AnalyzerDaysBack;
		m_AnalyzerStartDateLong                = def_M_AnalyzerStartDateLong;
		m_AnalyzerEndDateLong                  = def_M_AnalyzerEndDateLong;
		m_AnalyzerBreakfastTimeStart           = def_M_AnalyzerBreakfastTimeStart;
		m_AnalyzerLunchTimeStart               = def_M_AnalyzerLunchTimeStart;
		m_AnalyzerDinnerTimeStart              = def_M_AnalyzerDinnerTimeStart;
		m_AnalyzerBedTimeStart                 = def_M_AnalyzerBedTimeStart;
		m_AnalyzerDaysBackToReview             = def_M_AnalyzerDaysBackToReview;
		m_AnalyzerHighThreshold                = def_M_AnalyzerHighThreshold * getBGUnitMultiplier();
		m_AnalyzerHighThresholdRelevanceFactor = def_M_AnalyzerHighThresholdRelevanceFactor;
		m_AnalyzerLowThreshold                 = def_M_AnalyzerLowThreshold * getBGUnitMultiplier();
		m_AnalyzerLowThresholdRelevanceFactor  = def_M_AnalyzerLowThresholdRelevanceFactor;
		m_AnalyzerIndividualTrendRatio         = def_M_AnalyzerIndividualTrendRatio;
		m_AnalyzerOvernightChangeTrendRatio    = def_M_AnalyzerOvernightChangeTrendRatio;  
		m_AnalyzerBreakfastChangeTrendRatio    = def_M_AnalyzerBreakfastChangeTrendRatio;
		m_AnalyzerLunchChangeTrendRatio        = def_M_AnalyzerLunchChangeTrendRatio;
		m_AnalyzerDinnerChangeTrendRatio       = def_M_AnalyzerDinnerChangeTrendRatio;
		m_AnalyzerMinMinsForTrendResults       = def_M_AnalyzerMinMinsForTrendResults;
		m_AnalyzerHighFrequencyPercentage        = def_M_AnalyzerHighFrequencyPercentage;
		m_AnalyzerMediumFrequencyPercentage      = def_M_AnalyzerMediumFrequencyPercentage;
		m_AnalyzerLowRangeThreshold            = def_M_AnalyzerLowRangeThreshold * getBGUnitMultiplier();
		m_AnalyzerHighRangeThreshold           = def_M_AnalyzerHighRangeThreshold * getBGUnitMultiplier();
		
		m_AnalyzerBedTrendStartStartTime       = def_M_AnalyzerBedTrendStartStartTime;
		m_AnalyzerBedTrendStartEndTime         = def_M_AnalyzerBedTrendStartEndTime;
		m_AnalyzerBedTrendEndStartTime         = def_M_AnalyzerBedTrendEndStartTime;
		m_AnalyzerBedTrendEndEndTime           = def_M_AnalyzerBedTrendEndEndTime;

		m_AnalyzerBadNightStartTime            = def_M_AnalyzerBadNightStartTime;
		m_AnalyzerBadNightEndTime              = def_M_AnalyzerBadNightEndTime;
		m_AnalyzerCompressMealTrends           = def_M_AnalyzerCompressMealTrends;
		m_AnalyzerTotalRecurringTrendsOnly     = def_M_AnalyzerTotalRecurringTrendsOnly;
		
	}
	
	private void resetBGValues()
	{
		m_AnalyzerHighThreshold                = def_M_AnalyzerHighThreshold * getBGUnitMultiplier();
		m_AnalyzerLowThreshold                 = def_M_AnalyzerLowThreshold * getBGUnitMultiplier();
		m_AnalyzerLowRangeThreshold            = def_M_AnalyzerLowRangeThreshold * getBGUnitMultiplier();
		m_AnalyzerHighRangeThreshold           = def_M_AnalyzerHighRangeThreshold * getBGUnitMultiplier();
	}

	public void resetAnalyzeDateRange(java.util.Date endDate2)
	{
		m_AnalyzerDaysBack                     = def_M_AnalyzerDaysBack;
		m_AnalyzerEndDateLong                  = endDate2.getTime();
		Date endDate                           = new Date(m_AnalyzerEndDateLong);
		
		Calendar c = Calendar.getInstance();
		c.setTime(endDate);
		c.add(Calendar.DATE, (int)(m_AnalyzerDaysBack * (-1))); // Subtract daysBack
		m_AnalyzerStartDateLong = c.getTime().getTime();
	}
	
	
	public void setPreferences()
	{
	    // now set the values
		prefs.putInt(pref_BGUnits, m_BGUnits);
		prefs.put(pref_SelectedMeter, m_SelectedMeter);
	    prefs.putInt(pref_DaysToLoad, m_DaysToLoad);
	    prefs.put(pref_MongoMeterCollection, m_MongoMeterCollection);
	    prefs.put(pref_SQLDBServerHost, m_SQLDBServerHost);
	    prefs.put(pref_SQLDBServerInstance, m_SQLDBServerInstance);
	    prefs.put(pref_SQLDBName, m_SQLDBName);
	    prefs.put(pref_MedtronicMeterPumpResultFilePath, m_MedtronicMeterPumpResultFilePath);
	    prefs.put(pref_DiasendMeterPumpResultFilePath, m_DiasendMeterPumpResultFilePath);
	    prefs.put(pref_OmniPodMeterPumpResultFilePath, m_OmniPodMeterPumpResultFilePath);
	    prefs.put(pref_ExportFilePath,          m_ExportFilePath);
	    prefs.put(pref_DownloadTreatmentFilePath, m_DownloadTreatmentFilePath);
	    prefs.put(pref_DownloadSensorFilePath, m_DownloadSensorFilePath);
	    prefs.put(pref_AnalysisFilePath, m_AnalysisFilePath);
	    		
	    prefs.put(pref_NightscoutMongoServer, m_NightscoutMongoServer);
//	    prefs.putInt(pref_NightscoutMongoPort, m_NightscoutMongoPort);
	    prefs.put(pref_NightscoutMongoDB, m_NightscoutMongoDB);
	    prefs.put(pref_NightscoutMongoCollection, m_NightscoutMongoCollection);
	    prefs.put(pref_NightscoutSensorMongoCollection, m_NightscoutSensorMongoCollection);
	    prefs.put(pref_NightscoutAuditCollection, m_NightscoutAuditCollection);
	    
	    prefs.putBoolean(pref_AdvancedOptions, m_AdvancedOptions);
	    prefs.put(pref_MongoMeterCollection, m_MongoMeterCollection);
	    prefs.putInt(pref_MaxMinsBetweenSameMealEvent, m_MaxMinsBetweenSameMealEvent);
	    prefs.putInt(pref_MaxMinsBetweenSameCorrectionEvent, m_MaxMinsBetweenSameCorrectionEvent);
	    prefs.putBoolean(pref_UseMongoForRocheResults, m_UseMongoForRocheResults);
	    prefs.putInt(pref_LogLevel, m_LogLevel);
	    prefs.put(pref_LogFile, m_LogFile);
	    prefs.putBoolean(pref_AttemptDiasendTempBasals, m_AttemptDiasendTempBasals);
	    prefs.putBoolean(pref_AuditLogAllShown, m_AuditLogAllShown);
	    prefs.put(pref_InputDateFormat, m_InputDateFormat);
	    prefs.put(pref_Timezone, m_Timezone);
	    prefs.putInt(pref_ProximityMinutes, m_ProximityMinutes);
	    prefs.putBoolean(pref_ProximityTypeCheck, m_ProximityTypeCheck);
	    prefs.putInt(pref_ProximityValueCheck, m_ProximityValueCheck);

	    prefs.putInt(pref_MongoDBAlerterCheckInterval, m_MongoDBAlerterCheckInterval);
	    
	    
		// Analyzer controls
	    prefs.putInt(pref_AnalyzerDaysBack, m_AnalyzerDaysBack);
	    prefs.putLong(pref_AnalyzerStartDateLong, m_AnalyzerStartDateLong);
	    prefs.putLong(pref_AnalyzerEndDateLong, m_AnalyzerEndDateLong);
		prefs.put(pref_AnalyzerBreakfastTimeStart,  m_AnalyzerBreakfastTimeStart);
		prefs.put(pref_AnalyzerLunchTimeStart, m_AnalyzerLunchTimeStart);
		prefs.put(pref_AnalyzerDinnerTimeStart, m_AnalyzerDinnerTimeStart);
		prefs.put(pref_AnalyzerBedTimeStart, m_AnalyzerBedTimeStart);
		prefs.putInt(pref_AnalyzerDaysBackToReview, m_AnalyzerDaysBackToReview);
		prefs.putFloat(pref_AnalyzerHighThreshold, (float) m_AnalyzerHighThreshold);
		prefs.putFloat(pref_AnalyzerHighThresholdRelevanceFactor, (float) m_AnalyzerHighThresholdRelevanceFactor);
		prefs.putFloat(pref_AnalyzerLowThreshold, (float) m_AnalyzerLowThreshold);
		prefs.putFloat(pref_AnalyzerLowThresholdRelevanceFactor, (float) m_AnalyzerLowThresholdRelevanceFactor);
		prefs.putFloat(pref_AnalyzerIndividualTrendRatio, (float) m_AnalyzerIndividualTrendRatio);
		prefs.putFloat(pref_AnalyzerOvernightChangeTrendRatio, (float) m_AnalyzerOvernightChangeTrendRatio);  
		prefs.putFloat(pref_AnalyzerBreakfastChangeTrendRatio, (float) m_AnalyzerBreakfastChangeTrendRatio);
		prefs.putFloat(pref_AnalyzerLunchChangeTrendRatio, (float) m_AnalyzerLunchChangeTrendRatio);
		prefs.putFloat(pref_AnalyzerDinnerChangeTrendRatio, (float) m_AnalyzerDinnerChangeTrendRatio);
		prefs.putInt(pref_AnalyzerMinMinsForTrendResults, m_AnalyzerMinMinsForTrendResults);
		prefs.putFloat(pref_AnalyzerHighFrequencyPercentage, (float)m_AnalyzerHighFrequencyPercentage);
		prefs.putFloat(pref_AnalyzerMediumFrequencyPercentage, (float)m_AnalyzerMediumFrequencyPercentage);
		
		prefs.putFloat(pref_AnalyzerLowRangeThreshold, (float)m_AnalyzerLowRangeThreshold);
		prefs.putFloat(pref_AnalyzerHighRangeThreshold, (float)m_AnalyzerHighRangeThreshold);
		
		prefs.put(pref_AnalyzerBedTrendStartStartTime, m_AnalyzerBedTrendStartStartTime);
		prefs.put(pref_AnalyzerBedTrendStartEndTime, m_AnalyzerBedTrendStartEndTime);
		prefs.put(pref_AnalyzerBedTrendEndStartTime, m_AnalyzerBedTrendEndStartTime);
		prefs.put(pref_AnalyzerBedTrendEndEndTime,   m_AnalyzerBedTrendEndEndTime);
		
		prefs.put(pref_AnalyzerBadNightStartTime,   m_AnalyzerBadNightStartTime);
		prefs.put(pref_AnalyzerBadNightEndTime,   m_AnalyzerBadNightEndTime);
		prefs.putBoolean(pref_AnalyzerCompressMealTrends, m_AnalyzerCompressMealTrends);
		prefs.putBoolean(pref_AnalyzerTotalRecurringTrendsOnly, m_AnalyzerTotalRecurringTrendsOnly);

	}
	
	private void loadPreferences()
	{
//		// Reset the mistake from before :-(
//		this.setM_AnalyzerLowRangeThreshold(4.0);
//		this.setM_AnalyzerHighRangeThreshold(7.0);
		
		// Note the defaults
		m_BGUnits                           = prefs.getInt(pref_BGUnits, def_M_BGUnits);
		m_SelectedMeter                     = prefs.get(pref_SelectedMeter, def_M_SelectedMeter);
		m_DaysToLoad                        = prefs.getInt(pref_DaysToLoad,     def_M_DaysToLoad);
		m_SQLFile                           = prefs.get(pref_SQLFile,         def_M_SQLFile); // "C:\\\\Local_Data\\\\SQL_Query_For_Java.txt");
//		DBServer   = prefs.get(pref_DBServer,      "192.168.1.123\\\\ACCUCHEK360");
		m_SQLDBServerHost                   = prefs.get(pref_SQLDBServerHost,    def_M_SQLDBServerHost);
		m_SQLDBServerInstance               = prefs.get(pref_SQLDBServerInstance,  def_M_SQLDBServerInstance);
		m_SQLDBName                         = prefs.get(pref_SQLDBName,            def_M_SQLDBName);
		m_MedtronicMeterPumpResultFilePath  = prefs.get(pref_MedtronicMeterPumpResultFilePath, def_M_MedtronicMeterPumpResultFilePath);
		m_DiasendMeterPumpResultFilePath    = prefs.get(pref_DiasendMeterPumpResultFilePath, def_M_DiasendMeterPumpResultFilePath);
		m_OmniPodMeterPumpResultFilePath    = prefs.get(pref_OmniPodMeterPumpResultFilePath, def_M_OmniPodMeterPumpResultFilePath);
		m_ExportFilePath                    = prefs.get(pref_ExportFilePath, def_M_ExportFilePath);
		m_DownloadTreatmentFilePath         = prefs.get(pref_DownloadTreatmentFilePath, def_M_DownloadTreatmentFilePath);
		m_DownloadSensorFilePath            = prefs.get(pref_DownloadSensorFilePath, def_M_DownloadSensorFilePath);
		m_AnalysisFilePath                  = prefs.get(pref_AnalysisFilePath,  def_M_AnalysisFilePath);
				
		m_NightscoutMongoServer             = prefs.get(pref_NightscoutMongoServer,  def_M_NightscoutMongoServer);
//		m_NightscoutMongoPort               = prefs.getInt(pref_NightscoutMongoPort, 27017);
		m_NightscoutMongoDB                 = prefs.get(pref_NightscoutMongoDB, def_M_NightscoutMongoDB);
		m_NightscoutMongoCollection         = prefs.get(pref_NightscoutMongoCollection, def_M_NightscoutMongoCollection);
		m_NightscoutSensorMongoCollection   = prefs.get(pref_NightscoutSensorMongoCollection, def_M_NightscoutSensorMongoCollection);
		m_NightscoutAuditCollection         = prefs.get(pref_NightscoutAuditCollection, def_M_NightscoutAuditCollection);
		
		m_AdvancedOptions                   = prefs.getBoolean(pref_AdvancedOptions, def_M_AdvancedOptions);
//		m_MongoMeterServer                  = prefs.get(pref_MongoMeterServer, "localhost");
		m_MongoMeterServer                  = prefs.get(pref_MongoMeterServer, "");
		m_MongoMeterDB                      = prefs.get(pref_MongoMeterDB, "dexcom_db");
		m_MongoMeterCollection              = prefs.get(pref_MongoMeterCollection, "Roche_Results");
		m_MaxMinsBetweenSameMealEvent       = prefs.getInt(pref_MaxMinsBetweenSameMealEvent, def_M_MaxMinsBetweenSameMealEvent);
		m_MaxMinsBetweenSameCorrectionEvent = prefs.getInt(pref_MaxMinsBetweenSameCorrectionEvent, def_M_MaxMinsBetweenSameCorrectionEvent);
		m_UseMongoForRocheResults           = prefs.getBoolean(pref_UseMongoForRocheResults, def_M_UseMongoForRocheResults);
		
		m_LogLevel                          = prefs.getInt(pref_LogLevel, def_M_LogLevel);
		m_LogFile                           = prefs.get(pref_LogFile,  def_M_LogFile); // "C:\\temp\\NightscoutLoader.log");
		m_AttemptDiasendTempBasals          = prefs.getBoolean(pref_AttemptDiasendTempBasals, def_M_AttemptDiasendTempBasals);
		m_AuditLogAllShown                  = prefs.getBoolean(pref_AuditLogAllShown, def_M_AuditLogAllShown);
		m_InputDateFormat                   = prefs.get(pref_InputDateFormat, def_M_InputDateFormat);     
		m_Timezone                          = prefs.get(pref_Timezone, def_M_Timezone);
		m_ProximityMinutes                  = prefs.getInt(pref_ProximityMinutes, def_M_ProximityMinutes);
		m_ProximityTypeCheck                = prefs.getBoolean(pref_ProximityTypeCheck, def_M_ProximityTypeCheck);
		m_ProximityValueCheck               = prefs.getInt(pref_ProximityValueCheck, def_M_ProximityValueCheck);
		
		m_MongoDBAlerterCheckInterval       = prefs.getInt(pref_MongoDBAlerterCheckInterval, def_M_MongoDBAlerterCheckInterval);
		
		// Analyzer controls
		m_AnalyzerDaysBack                  = prefs.getInt(pref_AnalyzerDaysBack, def_M_AnalyzerDaysBack);
	    m_AnalyzerStartDateLong             = prefs.getLong(pref_AnalyzerStartDateLong, def_M_AnalyzerStartDateLong);
	    m_AnalyzerEndDateLong               = prefs.getLong(pref_AnalyzerEndDateLong, def_M_AnalyzerEndDateLong);

		m_AnalyzerBreakfastTimeStart        = prefs.get(pref_AnalyzerBreakfastTimeStart,  def_M_AnalyzerBreakfastTimeStart);
		m_AnalyzerLunchTimeStart            = prefs.get(pref_AnalyzerLunchTimeStart, def_M_AnalyzerLunchTimeStart);
		m_AnalyzerDinnerTimeStart           = prefs.get(pref_AnalyzerDinnerTimeStart, def_M_AnalyzerDinnerTimeStart);
		m_AnalyzerBedTimeStart              = prefs.get(pref_AnalyzerBedTimeStart, def_M_AnalyzerBedTimeStart);
		m_AnalyzerDaysBackToReview          = prefs.getInt(pref_AnalyzerDaysBackToReview, def_M_AnalyzerDaysBackToReview);
		m_AnalyzerHighThreshold             = prefs.getFloat(pref_AnalyzerHighThreshold, (float) def_M_AnalyzerHighThreshold);
		m_AnalyzerHighThresholdRelevanceFactor = prefs.getFloat(pref_AnalyzerHighThresholdRelevanceFactor, (float) def_M_AnalyzerHighThresholdRelevanceFactor);
		m_AnalyzerLowThreshold              = prefs.getFloat(pref_AnalyzerLowThreshold, (float) def_M_AnalyzerLowThreshold);
		m_AnalyzerLowThresholdRelevanceFactor = prefs.getFloat(pref_AnalyzerLowThresholdRelevanceFactor, (float) def_M_AnalyzerLowThresholdRelevanceFactor);
		m_AnalyzerIndividualTrendRatio      = prefs.getFloat(pref_AnalyzerIndividualTrendRatio, (float) def_M_AnalyzerIndividualTrendRatio);
		m_AnalyzerOvernightChangeTrendRatio = prefs.getFloat(pref_AnalyzerOvernightChangeTrendRatio, (float) def_M_AnalyzerOvernightChangeTrendRatio);  
		m_AnalyzerBreakfastChangeTrendRatio = prefs.getFloat(pref_AnalyzerBreakfastChangeTrendRatio, (float) def_M_AnalyzerBreakfastChangeTrendRatio);
		m_AnalyzerLunchChangeTrendRatio     = prefs.getFloat(pref_AnalyzerLunchChangeTrendRatio, (float) def_M_AnalyzerLunchChangeTrendRatio);
		m_AnalyzerDinnerChangeTrendRatio    = prefs.getFloat(pref_AnalyzerDinnerChangeTrendRatio, (float) def_M_AnalyzerDinnerChangeTrendRatio);
		m_AnalyzerMinMinsForTrendResults    = prefs.getInt(pref_AnalyzerMinMinsForTrendResults, def_M_AnalyzerMinMinsForTrendResults);
		m_AnalyzerHighFrequencyPercentage     = prefs.getFloat(pref_AnalyzerHighFrequencyPercentage, (float)def_M_AnalyzerHighFrequencyPercentage);
		m_AnalyzerMediumFrequencyPercentage   = prefs.getFloat(pref_AnalyzerMediumFrequencyPercentage, (float)def_M_AnalyzerMediumFrequencyPercentage);
		m_AnalyzerLowRangeThreshold         = prefs.getFloat(pref_AnalyzerLowRangeThreshold, (float)def_M_AnalyzerLowRangeThreshold);
		m_AnalyzerHighRangeThreshold        = prefs.getFloat(pref_AnalyzerHighRangeThreshold, (float)def_M_AnalyzerHighRangeThreshold);
		// Made a mistake initially havnig same tag (NSL_HIGH) reference both low and hign.  Since thne, low is 7!!!

		
		m_AnalyzerBedTrendStartStartTime    = prefs.get(pref_AnalyzerBedTrendStartStartTime, def_M_AnalyzerBedTrendStartStartTime);
		m_AnalyzerBedTrendStartEndTime      = prefs.get(pref_AnalyzerBedTrendStartEndTime, def_M_AnalyzerBedTrendStartEndTime);
		m_AnalyzerBedTrendEndStartTime      = prefs.get(pref_AnalyzerBedTrendEndStartTime, def_M_AnalyzerBedTrendEndStartTime);
		m_AnalyzerBedTrendEndEndTime        = prefs.get(pref_AnalyzerBedTrendEndEndTime,   def_M_AnalyzerBedTrendEndEndTime);

		m_AnalyzerBadNightStartTime         = prefs.get(pref_AnalyzerBadNightStartTime,   def_M_AnalyzerBadNightStartTime);
		m_AnalyzerBadNightEndTime           = prefs.get(pref_AnalyzerBadNightEndTime,   def_M_AnalyzerBadNightEndTime);
		m_AnalyzerCompressMealTrends        = prefs.getBoolean(pref_AnalyzerCompressMealTrends, def_M_AnalyzerCompressMealTrends);
		m_AnalyzerTotalRecurringTrendsOnly  = prefs.getBoolean(pref_AnalyzerTotalRecurringTrendsOnly, def_M_AnalyzerTotalRecurringTrendsOnly);
		
		
		
	//	prefs.nodeExists(arg0)
	}

	/**
	 * @return the m_BGUnits
	 */
	public synchronized int getM_BGUnits() {
		return m_BGUnits;
	}

	/**
	 * @param m_BGUnits the m_BGUnits to set
	 */
	public synchronized void setM_BGUnits(int m_BGUnits) 
	{
		this.m_BGUnits = m_BGUnits;
		resetBGValues();
	}

	/**
	 * @return the m_SelectedMeter
	 */
	public String getM_SelectedMeter() {
		return m_SelectedMeter;
	}

	/**
	 * @param m_SelectedMeter the m_SelectedMeter to set
	 */
	public void setM_SelectedMeter(String m_SelectedMeter) {
		this.m_SelectedMeter = m_SelectedMeter;
	}

	/**
	 * @return the m_DaysToLoad
	 */
	public int getM_DaysToLoad() {
		return m_DaysToLoad;
	}

	/**
	 * @param m_DaysToLoad the m_DaysToLoad to set
	 */
	public void setM_DaysToLoad(int m_DaysToLoad) {
		this.m_DaysToLoad = m_DaysToLoad;
	}

	/**
	 * @return the m_SQLFile
	 */
	public String getM_SQLFile() {
		return m_SQLFile;
	}

	/**
	 * @param m_SQLFile the m_SQLFile to set
	 */
	public void setM_SQLFile(String m_SQLFile) {
		this.m_SQLFile = m_SQLFile;
	}

	/**
	 * @return the m_SQLDBServerHost
	 */
	public String getM_SQLDBServerHost() {
		return m_SQLDBServerHost;
	}

	/**
	 * @param m_SQLDBServerHost the m_SQLDBServerHost to set
	 */
	public void setM_SQLDBServerHost(String m_SQLDBServerHost) {
		this.m_SQLDBServerHost = m_SQLDBServerHost;
	}

	/**
	 * @return the m_SQLDBServerInstance
	 */
	public String getM_SQLDBServerInstance() {
		return m_SQLDBServerInstance;
	}

	/**
	 * @param m_SQLDBServerInstance the m_SQLDBServerInstance to set
	 */
	public void setM_SQLDBServerInstance(String m_SQLDBServerInstance) {
		this.m_SQLDBServerInstance = m_SQLDBServerInstance;
	}

	/**
	 * @return the m_SQLDBName
	 */
	public String getM_SQLDBName() {
		return m_SQLDBName;
	}

	/**
	 * @param m_SQLDBName the m_SQLDBName to set
	 */
	public void setM_SQLDBName(String m_SQLDBName) {
		this.m_SQLDBName = m_SQLDBName;
	}

	/**
	 * @return the m_MedtronicMeterPumpResultFilePath
	 */
	public String getM_MedtronicMeterPumpResultFilePath() {
		return m_MedtronicMeterPumpResultFilePath;
	}

	/**
	 * @param m_MedtronicMeterPumpResultFilePath the m_MedtronicMeterPumpResultFilePath to set
	 */
	public void setM_MedtronicMeterPumpResultFilePath(String m_MeterPumpResultFilePath) {
		this.m_MedtronicMeterPumpResultFilePath = m_MeterPumpResultFilePath;
	}

	/**
	 * @return the m_DiasendMeterPumpResultFilePath
	 */
	public synchronized String getM_DiasendMeterPumpResultFilePath() {
		return m_DiasendMeterPumpResultFilePath;
	}

	/**
	 * @param m_DiasendMeterPumpResultFilePath the m_DiasendMeterPumpResultFilePath to set
	 */
	public synchronized void setM_DiasendMeterPumpResultFilePath(String m_DiasendMeterPumpResultFilePath) {
		this.m_DiasendMeterPumpResultFilePath = m_DiasendMeterPumpResultFilePath;
	}

	/**
	 * @return the m_OmniPodMeterPumpResultFilePath
	 */
	public synchronized String getM_OmniPodMeterPumpResultFilePath() {
		return m_OmniPodMeterPumpResultFilePath;
	}

	/**
	 * @param m_OmniPodMeterPumpResultFilePath the m_OmniPodMeterPumpResultFilePath to set
	 */
	public synchronized void setM_OmniPodMeterPumpResultFilePath(String m_OmniPodMeterPumpResultFilePath) {
		this.m_OmniPodMeterPumpResultFilePath = m_OmniPodMeterPumpResultFilePath;
	}

	
	/**
	 * @return the m_ExportFilePath
	 */
	public String getM_ExportFilePath() {
		return m_ExportFilePath;
	}

	/**
	 * @param m_ExportFilePath the m_ExportFilePath to set
	 */
	public void setM_ExportFilePath(String m_ExportFilePath) {
		this.m_ExportFilePath = m_ExportFilePath;
	}

	/**
	 * @return the m_DownloadTreatmentFilePath
	 */
	public String getM_DownloadTreatmentFilePath() {
		return m_DownloadTreatmentFilePath;
	}

	/**
	 * @param m_DownloadTreatmentFilePath the m_DownloadTreatmentFilePath to set
	 */
	public void setM_DownloadTreatmentFilePath(String m_DownloadTreatmentFilePath) {
		this.m_DownloadTreatmentFilePath = m_DownloadTreatmentFilePath;
	}

	/**
	 * @return the m_DownloadSensorFilePath
	 */
	public String getM_DownloadSensorFilePath() {
		return m_DownloadSensorFilePath;
	}

	/**
	 * @param m_DownloadSensorFilePath the m_DownloadSensorFilePath to set
	 */
	public void setM_DownloadSensorFilePath(String m_DownloadSensorFilePath) {
		this.m_DownloadSensorFilePath = m_DownloadSensorFilePath;
	}

	/**
	 * @return the m_AnalysisFilePath
	 */
	public synchronized String getM_AnalysisFilePath() {
		return m_AnalysisFilePath;
	}

	/**
	 * @param m_AnalysisFilePath the m_AnalysisFilePath to set
	 */
	public synchronized void setM_AnalysisFilePath(String m_AnalysisFilePath) {
		this.m_AnalysisFilePath = m_AnalysisFilePath;
	}

	/**
	 * @return the m_MongoServer
	 */
	public String getM_NightscoutMongoServer() {
		return m_NightscoutMongoServer;
	}

	/**
	 * @param m_MongoServer the m_MongoServer to set
	 */
	public void setM_NightscoutMongoServer(String m_MongoServer) {
		this.m_NightscoutMongoServer = m_MongoServer;
	}

//	/**
//	 * @return the m_NightscoutMongoPort
//	 */
//	public int getM_NightscoutMongoPort() {
//		return m_NightscoutMongoPort;
//	}
//
//	/**
//	 * @param m_NightscoutMongoPort the m_NightscoutMongoPort to set
//	 */
//	public void setM_NightscoutMongoPort(int m_NightscoutMongoPort) {
//		this.m_NightscoutMongoPort = m_NightscoutMongoPort;
//	}

	/**
	 * @return the m_MongoDB
	 */
	public String getM_NightscoutMongoDB() {
		return m_NightscoutMongoDB;
	}

	/**
	 * @param m_MongoDB the m_MongoDB to set
	 */
	public void setM_NightscoutMongoDB(String m_MongoDB) {
		this.m_NightscoutMongoDB = m_MongoDB;
	}

	/**
	 * @return the m_MongoCollection
	 */
	public String getM_NightscoutMongoCollection() {
		return m_NightscoutMongoCollection;
	}

	/**
	 * @param m_MongoCollection the m_MongoCollection to set
	 */
	public void setM_NightscoutMongoCollection(String m_MongoCollection) {
		this.m_NightscoutMongoCollection = m_MongoCollection;
	}

	/**
	 * @return the m_NightscoutSensorMongoCollection
	 */
	public String getM_NightscoutSensorMongoCollection() {
		return m_NightscoutSensorMongoCollection;
	}

	/**
	 * @param m_NightscoutSensorMongoCollection the m_NightscoutSensorMongoCollection to set
	 */
	public void setM_NightscoutSensorMongoCollection(String m_NightscoutSensorMongoCollection) {
		this.m_NightscoutSensorMongoCollection = m_NightscoutSensorMongoCollection;
	}

	/**
	 * @return the m_NightscoutAuditCollection
	 */
	public synchronized String getM_NightscoutAuditCollection() {
		return m_NightscoutAuditCollection;
	}

	/**
	 * @param m_NightscoutAuditCollection the m_NightscoutAuditCollection to set
	 */
	public synchronized void setM_NightscoutAuditCollection(String m_NightscoutAuditCollection) {
		this.m_NightscoutAuditCollection = m_NightscoutAuditCollection;
	}

	/**
	 * @return the m_MongoMeterServer
	 */
	public String getM_MongoMeterServer() {
		return m_MongoMeterServer;
	}

	/**
	 * @param m_MongoMeterServer the m_MongoMeterServer to set
	 */
	public void setM_MongoMeterServer(String m_MongoMeterServer) {
		this.m_MongoMeterServer = m_MongoMeterServer;
	}

	/**
	 * @return the m_MongoMeterDB
	 */
	public String getM_MongoMeterDB() {
		return m_MongoMeterDB;
	}

	/**
	 * @param m_MongoMeterDB the m_MongoMeterDB to set
	 */
	public void setM_MongoMeterDB(String m_MongoMeterDB) {
		this.m_MongoMeterDB = m_MongoMeterDB;
	}

	/**
	 * @return the m_MongoMeterCollection
	 */
	public String getM_MongoMeterCollection() {
		return m_MongoMeterCollection;
	}

	/**
	 * @param m_MongoMeterCollection the m_MongoMeterCollection to set
	 */
	public void setM_MongoMeterCollection(String m_MongoMeterCollection) {
		this.m_MongoMeterCollection = m_MongoMeterCollection;
	}

	/**
	 * @return the m_AdvancedOptions
	 */
	public boolean isM_AdvancedOptions() {
		return m_AdvancedOptions;
	}

	/**
	 * @param m_AdvancedOptions the m_AdvancedOptions to set
	 */
	public void setM_AdvancedOptions(boolean m_AdvancedOptions) {
		this.m_AdvancedOptions = m_AdvancedOptions;
	}

	/**
	 * @return the m_MaxMinsBetweenSameMealEvent
	 */
	public int getM_MaxMinsBetweenSameMealEvent() {
		return m_MaxMinsBetweenSameMealEvent;
	}

	/**
	 * @param m_MaxMinsBetweenSameMealEvent the m_MaxMinsBetweenSameMealEvent to set
	 */
	public void setM_MaxMinsBetweenSameMealEvent(int m_MaxMinsBetweenSameMealEvent) {
		this.m_MaxMinsBetweenSameMealEvent = m_MaxMinsBetweenSameMealEvent;
	}

	/**
	 * @return the m_MaxMinsBetweenSameCorrectionEvent
	 */
	public int getM_MaxMinsBetweenSameCorrectionEvent() {
		return m_MaxMinsBetweenSameCorrectionEvent;
	}

	/**
	 * @param m_MaxMinsBetweenSameCorrectionEvent the m_MaxMinsBetweenSameCorrectionEvent to set
	 */
	public void setM_MaxMinsBetweenSameCorrectionEvent(int m_MaxMinsBetweenSameCorrectionEvent) {
		this.m_MaxMinsBetweenSameCorrectionEvent = m_MaxMinsBetweenSameCorrectionEvent;
	}

	/**
	 * @return the m_UseMongoForRocheResults
	 */
	public boolean isM_UseMongoForRocheResults() {
		return m_UseMongoForRocheResults;
	}

	/**
	 * @param m_UseMongoForRocheResults the m_UseMongoForRocheResults to set
	 */
	public void setM_UseMongoForRocheResults(boolean m_UseMongoForRocheResults) {
		this.m_UseMongoForRocheResults = m_UseMongoForRocheResults;
	}

	/**
	 * @return the m_LogLevel
	 */
	public int getM_LogLevel() {
		return m_LogLevel;
	}

	/**
	 * @param m_LogLevel the m_LogLevel to set
	 */
	public void setM_LogLevel(int m_LogLevel) {
		this.m_LogLevel = m_LogLevel;
	}

	/**
	 * @return the m_LogFile
	 */
	public String getM_LogFile() {
		return m_LogFile;
	}

	/**
	 * @param m_LogFile the m_LogFile to set
	 */
	public void setM_LogFile(String m_LogFile) {
		this.m_LogFile = m_LogFile;
	}

	/**
	 * @return the m_AttemptDiasendTempBasals
	 */
	public synchronized boolean isM_AttemptDiasendTempBasals() {
		return m_AttemptDiasendTempBasals;
	}

	/**
	 * @param m_AttemptDiasendTempBasals the m_AttemptDiasendTempBasals to set
	 */
	public synchronized void setM_AttemptDiasendTempBasals(boolean m_AttemptDiasendTempBasals) {
		this.m_AttemptDiasendTempBasals = m_AttemptDiasendTempBasals;
	}

	/**
	 * @return the m_AuditLogAllShown
	 */
	public synchronized boolean isM_AuditLogAllShown() {
		return m_AuditLogAllShown;
	}

	/**
	 * @param m_AuditLogAllShown the m_AuditLogAllShown to set
	 */
	public synchronized void setM_AuditLogAllShown(boolean m_AuditLogAllShown) {
		this.m_AuditLogAllShown = m_AuditLogAllShown;
	}
	
	/**
	 * @return the m_InputDateFormat
	 */
	public synchronized String getM_InputDateFormat() {
		return m_InputDateFormat;
	}

	/**
	 * @param m_InputDateFormat the m_InputDateFormat to set
	 */
	public synchronized void setM_InputDateFormat(String m_InputDateFormat) {
		this.m_InputDateFormat = m_InputDateFormat;
	}

	/**
	 * @return the m_Timezone
	 */
	public synchronized String getM_Timezone() {
		return m_Timezone;
	}

	/**
	 * @param m_Timezone the m_Timezone to set
	 */
	public synchronized void setM_Timezone(String m_Timezone) {
		this.m_Timezone = m_Timezone;
	}

	/**
	 * @return the m_ProximityMinutes
	 */
	public synchronized int getM_ProximityMinutes() {
		return m_ProximityMinutes;
	}

	/**
	 * @param m_ProximityMinutes the m_ProximityMinutes to set
	 */
	public synchronized void setM_ProximityMinutes(int m_ProximityMinutes) {
		this.m_ProximityMinutes = m_ProximityMinutes;
	}

	/**
	 * @return the m_ProximityTypeCheck
	 */
	public synchronized boolean isM_ProximityTypeCheck() {
		return m_ProximityTypeCheck;
	}

	/**
	 * @param m_ProximityTypeCheck the m_ProximityTypeCheck to set
	 */
	public synchronized void setM_ProximityTypeCheck(boolean m_ProximityTypeCheck) {
		this.m_ProximityTypeCheck = m_ProximityTypeCheck;
	}

	/**
	 * @return the m_ProximityValueCheck
	 */
	public synchronized int getM_ProximityValueCheck() {
		return m_ProximityValueCheck;
	}

	/**
	 * @param m_ProximityValueCheck the m_ProximityValueCheck to set
	 */
	public synchronized void setM_ProximityValueCheck(int m_ProximityValueCheck) {
		this.m_ProximityValueCheck = m_ProximityValueCheck;
	}

	/**
	 * @return the m_MongoDBAlerterCheckInterval
	 */
	public synchronized int getM_MongoDBAlerterCheckInterval() {
		return m_MongoDBAlerterCheckInterval;
	}

	/**
	 * @param m_MongoDBAlerterCheckInterval the m_MongoDBAlerterCheckInterval to set
	 */
	public synchronized void setM_MongoDBAlerterCheckInterval(int m_MongoDBAlerterCheckInterval) {
		this.m_MongoDBAlerterCheckInterval = m_MongoDBAlerterCheckInterval;
	}

	/**
	 * @return the def_M_InputDateFormat
	 */
	public synchronized String getDef_M_InputDateFormat() {
		return def_M_InputDateFormat;
	}

	/**
	 * @param def_M_InputDateFormat the def_M_InputDateFormat to set
	 */
	public synchronized void setDef_M_InputDateFormat(String def_M_InputDateFormat) {
		this.def_M_InputDateFormat = def_M_InputDateFormat;
	}

	/**
	 * @return the m_AnalyzerDaysBack
	 */
	public synchronized int getM_AnalyzerDaysBack() {
		return m_AnalyzerDaysBack;
	}

	/**
	 * @param m_AnalyzerDaysBack the m_AnalyzerDaysBack to set
	 */
	public synchronized void setM_AnalyzerDaysBack(int m_AnalyzerDaysBack) {
		this.m_AnalyzerDaysBack = m_AnalyzerDaysBack;
	}


	/**
	 * @return the m_AnalyzerStartDateLong
	 */
	public synchronized long getM_AnalyzerStartDateLong() {
		return m_AnalyzerStartDateLong;
	}

	/**
	 * @param m_AnalyzerStartDateLong the m_AnalyzerStartDateLong to set
	 */
	public synchronized void setM_AnalyzerStartDateLong(long m_AnalyzerStartDateLong) {
		this.m_AnalyzerStartDateLong = m_AnalyzerStartDateLong;
	}

	/**
	 * @return the m_AnalyzerEndDateLong
	 */
	public synchronized long getM_AnalyzerEndDateLong() {
		return m_AnalyzerEndDateLong;
	}

	/**
	 * @param m_AnalyzerEndDateLong the m_AnalyzerEndDateLong to set
	 */
	public synchronized void setM_AnalyzerEndDateLong(long m_AnalyzerEndDateLong) {
		this.m_AnalyzerEndDateLong = m_AnalyzerEndDateLong;
	}

	/**
	 * @return the m_AnalyzerBreakfastTimeStart
	 */
	public synchronized String getM_AnalyzerBreakfastTimeStart() {
		return m_AnalyzerBreakfastTimeStart;
	}

	/**
	 * @param m_AnalyzerBreakfastTimeStart the m_AnalyzerBreakfastTimeStart to set
	 */
	public synchronized void setM_AnalyzerBreakfastTimeStart(String m_AnalyzerBreakfastTimeStart) {
		this.m_AnalyzerBreakfastTimeStart = m_AnalyzerBreakfastTimeStart;
	}

	/**
	 * @return the m_AnalyzerLunchTimeStart
	 */
	public synchronized String getM_AnalyzerLunchTimeStart() {
		return m_AnalyzerLunchTimeStart;
	}

	/**
	 * @param m_AnalyzerLunchTimeStart the m_AnalyzerLunchTimeStart to set
	 */
	public synchronized void setM_AnalyzerLunchTimeStart(String m_AnalyzerLunchTimeStart) {
		this.m_AnalyzerLunchTimeStart = m_AnalyzerLunchTimeStart;
	}

	/**
	 * @return the m_AnalyzerDinnerTimeStart
	 */
	public synchronized String getM_AnalyzerDinnerTimeStart() {
		return m_AnalyzerDinnerTimeStart;
	}

	/**
	 * @param m_AnalyzerDinnerTimeStart the m_AnalyzerDinnerTimeStart to set
	 */
	public synchronized void setM_AnalyzerDinnerTimeStart(String m_AnalyzerDinnerTimeStart) {
		this.m_AnalyzerDinnerTimeStart = m_AnalyzerDinnerTimeStart;
	}

	/**
	 * @return the m_AnalyzerBedTimeStart
	 */
	public synchronized String getM_AnalyzerBedTimeStart() {
		return m_AnalyzerBedTimeStart;
	}

	/**
	 * @param m_AnalyzerBedTimeStart the m_AnalyzerBedTimeStart to set
	 */
	public synchronized void setM_AnalyzerBedTimeStart(String m_AnalyzerBedTimeStart) {
		this.m_AnalyzerBedTimeStart = m_AnalyzerBedTimeStart;
	}

	/**
	 * @return the m_AnalyzerDaysBackToReview
	 */
	public synchronized int getM_AnalyzerDaysBackToReview() {
		return m_AnalyzerDaysBackToReview;
	}

	/**
	 * @param m_AnalyzerDaysBackToReview the m_AnalyzerDaysBackToReview to set
	 */
	public synchronized void setM_AnalyzerDaysBackToReview(int m_AnalyzerDaysBackToReview) {
		this.m_AnalyzerDaysBackToReview = m_AnalyzerDaysBackToReview;
	}

	/**
	 * @return the m_AnalyzerHighThreshold
	 */
	public synchronized double getM_AnalyzerHighThreshold() {
		return m_AnalyzerHighThreshold;
	}

	/**
	 * @param m_AnalyzerHighThreshold the m_AnalyzerHighThreshold to set
	 */
	public synchronized void setM_AnalyzerHighThreshold(double m_AnalyzerHighThreshold) {
		this.m_AnalyzerHighThreshold = m_AnalyzerHighThreshold;
	}

	/**
	 * @return the m_AnalyzerHighThresholdRelevanceFactor
	 */
	public synchronized double getM_AnalyzerHighThresholdRelevanceFactor() {
		return m_AnalyzerHighThresholdRelevanceFactor;
	}

	/**
	 * @param m_AnalyzerHighThresholdRelevanceFactor the m_AnalyzerHighThresholdRelevanceFactor to set
	 */
	public synchronized void setM_AnalyzerHighThresholdRelevanceFactor(double m_AnalyzerHighThresholdRelevanceFactor) {
		this.m_AnalyzerHighThresholdRelevanceFactor = m_AnalyzerHighThresholdRelevanceFactor;
	}

	/**
	 * @return the m_AnalyzerLowThreshold
	 */
	public synchronized double getM_AnalyzerLowThreshold() {
		return m_AnalyzerLowThreshold;
	}

	/**
	 * @param m_AnalyzerLowThreshold the m_AnalyzerLowThreshold to set
	 */
	public synchronized void setM_AnalyzerLowThreshold(double m_AnalyzerLowThreshold) {
		this.m_AnalyzerLowThreshold = m_AnalyzerLowThreshold;
	}

	/**
	 * @return the m_AnalyzerLowThresholdRelevanceFactor
	 */
	public synchronized double getM_AnalyzerLowThresholdRelevanceFactor() {
		return m_AnalyzerLowThresholdRelevanceFactor;
	}

	/**
	 * @param m_AnalyzerLowThresholdRelevanceFactor the m_AnalyzerLowThresholdRelevanceFactor to set
	 */
	public synchronized void setM_AnalyzerLowThresholdRelevanceFactor(double m_AnalyzerLowThresholdRelevanceFactor) {
		this.m_AnalyzerLowThresholdRelevanceFactor = m_AnalyzerLowThresholdRelevanceFactor;
	}

	/**
	 * @return the m_AnalyzerIndividualTrendRatio
	 */
	public synchronized double getM_AnalyzerIndividualTrendRatio() {
		return m_AnalyzerIndividualTrendRatio;
	}

	/**
	 * @param m_AnalyzerIndividualTrendRatio the m_AnalyzerIndividualTrendRatio to set
	 */
	public synchronized void setM_AnalyzerIndividualTrendRatio(double m_AnalyzerIndividualTrendRatio) {
		this.m_AnalyzerIndividualTrendRatio = m_AnalyzerIndividualTrendRatio;
	}

	/**
	 * @return the m_AnalyzerOvernightChangeTrendRatio
	 */
	public synchronized double getM_AnalyzerOvernightChangeTrendRatio() {
		return m_AnalyzerOvernightChangeTrendRatio;
	}

	/**
	 * @param m_AnalyzerOvernightChangeTrendRatio the m_AnalyzerOvernightChangeTrendRatio to set
	 */
	public synchronized void setM_AnalyzerOvernightChangeTrendRatio(double m_AnalyzerOvernightChangeTrendRatio) {
		this.m_AnalyzerOvernightChangeTrendRatio = m_AnalyzerOvernightChangeTrendRatio;
	}

	/**
	 * @return the m_AnalyzerBreakfastChangeTrendRatio
	 */
	public synchronized double getM_AnalyzerBreakfastChangeTrendRatio() {
		return m_AnalyzerBreakfastChangeTrendRatio;
	}

	/**
	 * @param m_AnalyzerBreakfastChangeTrendRatio the m_AnalyzerBreakfastChangeTrendRatio to set
	 */
	public synchronized void setM_AnalyzerBreakfastChangeTrendRatio(double m_AnalyzerBreakfastChangeTrendRatio) {
		this.m_AnalyzerBreakfastChangeTrendRatio = m_AnalyzerBreakfastChangeTrendRatio;
	}

	/**
	 * @return the m_AnalyzerLunchChangeTrendRatio
	 */
	public synchronized double getM_AnalyzerLunchChangeTrendRatio() {
		return m_AnalyzerLunchChangeTrendRatio;
	}

	/**
	 * @param m_AnalyzerLunchChangeTrendRatio the m_AnalyzerLunchChangeTrendRatio to set
	 */
	public synchronized void setM_AnalyzerLunchChangeTrendRatio(double m_AnalyzerLunchChangeTrendRatio) {
		this.m_AnalyzerLunchChangeTrendRatio = m_AnalyzerLunchChangeTrendRatio;
	}

	/**
	 * @return the m_AnalyzerDinnerChangeTrendRatio
	 */
	public synchronized double getM_AnalyzerDinnerChangeTrendRatio() {
		return m_AnalyzerDinnerChangeTrendRatio;
	}

	/**
	 * @param m_AnalyzerDinnerChangeTrendRatio the m_AnalyzerDinnerChangeTrendRatio to set
	 */
	public synchronized void setM_AnalyzerDinnerChangeTrendRatio(double m_AnalyzerDinnerChangeTrendRatio) {
		this.m_AnalyzerDinnerChangeTrendRatio = m_AnalyzerDinnerChangeTrendRatio;
	}

	/**
	 * @return the m_AnalyzerMinMinsForTrendResults
	 */
	public synchronized int getM_AnalyzerMinMinsForTrendResults() {
		return m_AnalyzerMinMinsForTrendResults;
	}

	/**
	 * @param m_AnalyzerMinMinsForTrendResults the m_AnalyzerMinMinsForTrendResults to set
	 */
	public synchronized void setM_AnalyzerMinMinsForTrendResults(int m_AnalyzerMinMinsForTrendResults) {
		this.m_AnalyzerMinMinsForTrendResults = m_AnalyzerMinMinsForTrendResults;
	}

	/**
	 * @return the m_AnalyzerHighFrequencyPercentage
	 */
	public synchronized double getM_AnalyzerHighFrequencyPercentage() {
		return m_AnalyzerHighFrequencyPercentage;
	}

	/**
	 * @param m_AnalyzerHighFrequencyPercentage the m_AnalyzerHighFrequencyPercentage to set
	 */
	public synchronized void setM_AnalyzerHighFrequencyPercentage(double m_AnalyzerHighFrequencyPercentage) {
		this.m_AnalyzerHighFrequencyPercentage = m_AnalyzerHighFrequencyPercentage;
	}

	/**
	 * @return the m_AnalyzerMediumFrequencyPercentage
	 */
	public synchronized double getM_AnalyzerMediumFrequencyPercentage() {
		return m_AnalyzerMediumFrequencyPercentage;
	}

	/**
	 * @param m_AnalyzerMediumFrequencyPercentage the m_AnalyzerMediumFrequencyPercentage to set
	 */
	public synchronized void setM_AnalyzerMediumFrequencyPercentage(double m_AnalyzerMediumFrequencyPercentage) {
		this.m_AnalyzerMediumFrequencyPercentage = m_AnalyzerMediumFrequencyPercentage;
	}

	/**
	 * @return the m_AnalyzerLowRangeThreshold
	 */
	public synchronized double getM_AnalyzerLowRangeThreshold() {
		return m_AnalyzerLowRangeThreshold;
	}

	/**
	 * @param m_AnalyzerLowRangeThreshold the m_AnalyzerLowRangeThreshold to set
	 */
	public synchronized void setM_AnalyzerLowRangeThreshold(double m_AnalyzerLowRangeThreshold) {
		this.m_AnalyzerLowRangeThreshold = m_AnalyzerLowRangeThreshold;
	}

	/**
	 * @return the m_AnalyzerHighRangeThreshold
	 */
	public synchronized double getM_AnalyzerHighRangeThreshold() {
		return m_AnalyzerHighRangeThreshold;
	}

	/**
	 * @param m_AnalyzerHighRangeThreshold the m_AnalyzerHighRangeThreshold to set
	 */
	public synchronized void setM_AnalyzerHighRangeThreshold(double m_AnalyzerHighRangeThreshold) {
		this.m_AnalyzerHighRangeThreshold = m_AnalyzerHighRangeThreshold;
	}

	/**
	 * @return the m_AnalyzerBedTrendStartStartTime
	 */
	public synchronized String getM_AnalyzerBedTrendStartStartTime() {
		return m_AnalyzerBedTrendStartStartTime;
	}

	/**
	 * @param m_AnalyzerBedTrendStartStartTime the m_AnalyzerBedTrendStartStartTime to set
	 */
	public synchronized void setM_AnalyzerBedTrendStartStartTime(String m_AnalyzerBedTrendStartStartTime) {
		this.m_AnalyzerBedTrendStartStartTime = m_AnalyzerBedTrendStartStartTime;
	}

	/**
	 * @return the m_AnalyzerBedTrendStartEndTime
	 */
	public synchronized String getM_AnalyzerBedTrendStartEndTime() {
		return m_AnalyzerBedTrendStartEndTime;
	}

	/**
	 * @param m_AnalyzerBedTrendStartEndTime the m_AnalyzerBedTrendStartEndTime to set
	 */
	public synchronized void setM_AnalyzerBedTrendStartEndTime(String m_AnalyzerBedTrendStartEndTime) {
		this.m_AnalyzerBedTrendStartEndTime = m_AnalyzerBedTrendStartEndTime;
	}

	/**
	 * @return the m_AnalyzerBedTrendEndStartTime
	 */
	public synchronized String getM_AnalyzerBedTrendEndStartTime() {
		return m_AnalyzerBedTrendEndStartTime;
	}

	/**
	 * @param m_AnalyzerBedTrendEndStartTime the m_AnalyzerBedTrendEndStartTime to set
	 */
	public synchronized void setM_AnalyzerBedTrendEndStartTime(String m_AnalyzerBedTrendEndStartTime) {
		this.m_AnalyzerBedTrendEndStartTime = m_AnalyzerBedTrendEndStartTime;
	}

	/**
	 * @return the m_AnalyzerBedTrendEndEndTime
	 */
	public synchronized String getM_AnalyzerBedTrendEndEndTime() {
		return m_AnalyzerBedTrendEndEndTime;
	}

	/**
	 * @param m_AnalyzerBedTrendEndEndTime the m_AnalyzerBedTrendEndEndTime to set
	 */
	public synchronized void setM_AnalyzerBedTrendEndEndTime(String m_AnalyzerBedTrendEndEndTime) {
		this.m_AnalyzerBedTrendEndEndTime = m_AnalyzerBedTrendEndEndTime;
	}

	/**
	 * @return the m_AnalyzerBadNightStartTime
	 */
	public synchronized String getM_AnalyzerBadNightStartTime() {
		return m_AnalyzerBadNightStartTime;
	}

	/**
	 * @param m_AnalyzerBadNightStartTime the m_AnalyzerBadNightStartTime to set
	 */
	public synchronized void setM_AnalyzerBadNightStartTime(String m_AnalyzerBadNightStartTime) {
		this.m_AnalyzerBadNightStartTime = m_AnalyzerBadNightStartTime;
	}

	/**
	 * @return the m_AnalyzerBadNightEndTime
	 */
	public synchronized String getM_AnalyzerBadNightEndTime() {
		return m_AnalyzerBadNightEndTime;
	}

	/**
	 * @param m_AnalyzerBadNightEndTime the m_AnalyzerBadNightEndTime to set
	 */
	public synchronized void setM_AnalyzerBadNightEndTime(String m_AnalyzerBadNightEndTime) {
		this.m_AnalyzerBadNightEndTime = m_AnalyzerBadNightEndTime;
	}

	/**
	 * @return the m_AnalyzerCompressMealTrends
	 */
	public synchronized boolean isM_AnalyzerCompressMealTrends() {
		return m_AnalyzerCompressMealTrends;
	}

	/**
	 * @param m_AnalyzerCompressMealTrends the m_AnalyzerCompressMealTrends to set
	 */
	public synchronized void setM_AnalyzerCompressMealTrends(boolean m_AnalyzerCompressMealTrends) {
		this.m_AnalyzerCompressMealTrends = m_AnalyzerCompressMealTrends;
	}

	/**
	 * @return the m_AnalyzerTotalRecurringTrendsOnly
	 */
	public synchronized boolean isM_AnalyzerTotalRecurringTrendsOnly() {
		return m_AnalyzerTotalRecurringTrendsOnly;
	}

	/**
	 * @param m_AnalyzerTotalRecurringTrendsOnly the m_AnalyzerTotalRecurringTrendsOnly to set
	 */
	public synchronized void setM_AnalyzerTotalRecurringTrendsOnly(boolean m_AnalyzerTotalRecurringTrendsOnly) {
		this.m_AnalyzerTotalRecurringTrendsOnly = m_AnalyzerTotalRecurringTrendsOnly;
	}



}

