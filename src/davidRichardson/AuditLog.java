package davidRichardson;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class AuditLog 
{
	protected static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	private String  m_ID;
	private String  m_UploadID;
	private String  m_UploadStatus;
	private Date    m_UploadDate;
	private String  m_UploadDevice;
	private String  m_FileName;
	private String  m_DateRange;
	private int     m_EntriesAdded;
	private int     m_TreatmentsAtStart;
	private int     m_TreatmentsByNSLAtStart;
	private int     m_ProximityEntries;

	public static final String    m_Success             = "Success";               // Audit log of successful synch
	public static final String    m_NotSaved            = "Not Saved";             // Audit log of unsuccessful synch
	public static final String    m_DeletedBy           = "Deleted by ";           // Previous success or not saved that gets backed out
	public static final String    m_Delete              = "Delete";                // Audit log of back out requests
	public static final String    m_ProximityDeletedBy  = "Proximity Deleted by";  // Previous success or not saved with proximities that gets backed out


	public enum Status
	{
		Success,
		Not_Saved,
		Deleted_By,
		Delete_Request,
		Proximity_Deleted_By
	}

	private static String[] m_ColNames = {
			"Date Time",
			"Upload ID",
			"Status",
			"Device",
			"Filename", 
			"Date Range", 
			"Entries Added", 
			"Total Treatments at Start", 
			"NSL Treatments at Start",
			"Proximity Entries"
	};
	private static int[] m_ColWidths = {250, 250, 250, 250, 250, 400, 700,
			450, 250, 250, };

	private static Object[][] m_Initializer = {{"","","","","",
		"","","","", "" }};


	AuditLog(String id, String uploadID, String uploadStatus, Date uploadDate, 
			String uploadDevice, String fileName, String dateRange, int entriesAdded, 
			int treatmentsAtStart, int treatmentsByNSLAtStart, int proximityEntries)
	{
		m_ID                     = new String(id);
		m_UploadID               = new String(uploadID);
		m_UploadStatus           = uploadStatus;
		m_UploadDate             = new Date(0);
		m_UploadDate             = uploadDate;
		m_UploadDevice           = new String(uploadDevice);
		m_FileName               = new String(fileName);
		m_DateRange              = new String(dateRange);
		m_EntriesAdded           = entriesAdded;
		m_TreatmentsAtStart      = treatmentsAtStart;
		m_TreatmentsByNSLAtStart = treatmentsByNSLAtStart;
		m_ProximityEntries       = proximityEntries;
	}

	public  AuditLog(DBObject rs, boolean rawData)
	{
		// Put all into a try-catch block since format.parse can raise exception

		m_ID                     = CommonUtils.getIDStr(rs,     "_id");
		m_UploadID               = CommonUtils.getFieldStr(rs,  "uploadID");
		m_UploadStatus           = CommonUtils.getFieldStr(rs,  "uploadStatus");
		m_UploadDate             = CommonUtils.getFieldDate(rs, "uploadDate");
		m_UploadDevice           = CommonUtils.getFieldStr(rs,  "uploadDevice");
		m_FileName               = CommonUtils.getFieldStr(rs,  "fileName");
		m_DateRange              = CommonUtils.getFieldStr(rs,  "dateRange");
		m_EntriesAdded           = CommonUtils.getFieldInt(rs,  "entriesAdded");
		m_TreatmentsAtStart      = CommonUtils.getFieldInt(rs,  "treatmentsAtStart"); 
		m_TreatmentsByNSLAtStart = CommonUtils.getFieldInt(rs,  "treatmentsByNSLAtStart"); 
		m_ProximityEntries       = CommonUtils.getFieldInt(rs,  "proximityEntries"); 
	}

	public BasicDBObject createNightScoutObject()
	{
		BasicDBObject result = new BasicDBObject("eventType", "Nightscout Upload");

		DBResult.appendToDoc(result, "uploadID",               m_UploadID);
		DBResult.appendToDoc(result, "uploadStatus",           m_UploadStatus);
		DBResult.appendToDoc(result, "uploadDate",             m_UploadDate);
		DBResult.appendToDoc(result, "uploadDevice",           m_UploadDevice);
		DBResult.appendToDoc(result, "fileName",               m_FileName);
		DBResult.appendToDoc(result, "dateRange",              m_DateRange);
		DBResult.appendToDoc(result, "entriesAdded",           m_EntriesAdded);
		DBResult.appendToDoc(result, "treatmentsAtStart",      m_TreatmentsAtStart);
		DBResult.appendToDoc(result, "treatmentsByNSLAtStart", m_TreatmentsByNSLAtStart);
		DBResult.appendToDoc(result, "proximityEntries",       m_ProximityEntries);

		return result;
	}

	public String[] toArray()
	{
		// m_Time used to populate event time too

		int arrSize = 10;

		String[] res = new String[arrSize];

		m_Logger.log(Level.FINEST, toString());

		int i = 0;
		//			res[i++] = m_CP_EventTime;
		res[i++] = getM_UploadDateString();
		res[i++] = m_UploadID;
		res[i++] = m_UploadStatus;
		res[i++] = m_UploadDevice;
		res[i++] = CommonUtils.truncatedFileName(m_FileName);
		res[i++] = m_DateRange;
		res[i++] = String.format("%d", m_EntriesAdded);
		res[i++] = String.format("%d", m_TreatmentsAtStart);
		res[i++] = String.format("%d", m_TreatmentsByNSLAtStart);
		res[i++] = String.format("%d", m_ProximityEntries);

		return res;
	}

	public synchronized Status getStatus()
	{
		Status result = AuditLog.Status.Success;

		String status = getM_UploadStatus();

		if (status.equals(AuditLog.m_Success))
		{
			result = AuditLog.Status.Success;
		}
		else if (status.equals(AuditLog.m_NotSaved))
		{
			result = AuditLog.Status.Not_Saved;
		}
		else if (status.equals(AuditLog.m_Delete))
		{
			result = AuditLog.Status.Delete_Request;
		}
		else if (status.substring(0, AuditLog.m_DeletedBy.length()).equals(AuditLog.m_DeletedBy))
		{
			result = AuditLog.Status.Deleted_By;
		}
		else if (status.substring(0, AuditLog.m_ProximityDeletedBy.length()).equals(AuditLog.m_ProximityDeletedBy))
		{
			result = AuditLog.Status.Proximity_Deleted_By;
		}

		return result;
	}


	/**
	 * @return the m_ID
	 */
	public synchronized String getM_ID() {
		return m_ID;
	}



	/**
	 * @param m_ID the m_ID to set
	 */
	public synchronized void setM_ID(String m_ID) {
		this.m_ID = m_ID;
	}



	/**
	 * @return the m_UploadID
	 */
	public synchronized String getM_UploadID() {
		return m_UploadID;
	}

	/**
	 * @param m_UploadID the m_UploadID to set
	 */
	public synchronized void setM_UploadID(String m_UploadID) {
		this.m_UploadID = m_UploadID;
	}

	/**
	 * @return the m_UploadStatus
	 */
	public synchronized String getM_UploadStatus() {
		return m_UploadStatus;
	}

	/**
	 * @param m_UploadStatus the m_UploadStatus to set
	 */
	public synchronized void setM_UploadStatus(String m_UploadStatus) {
		this.m_UploadStatus = m_UploadStatus;
	}

	public synchronized String getM_UploadDateString()
	{
		final DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
		String timeString = new String(format.format(m_UploadDate));

		return timeString;
	}

	/**
	 * @return the m_UploadDate
	 */
	public synchronized Date getM_UploadDate() {
		return m_UploadDate;
	}

	/**
	 * @param m_UploadDate the m_UploadDate to set
	 */
	public synchronized void setM_UploadDate(Date m_UploadDate) {
		this.m_UploadDate = m_UploadDate;
	}

	/**
	 * @return the m_UploadDevice
	 */
	public synchronized String getM_UploadDevice() {
		return m_UploadDevice;
	}

	/**
	 * @param m_UploadDevice the m_UploadDevice to set
	 */
	public synchronized void setM_UploadDevice(String m_UploadDevice) {
		this.m_UploadDevice = m_UploadDevice;
	}

	/**
	 * @return the m_FileName
	 */
	public synchronized String getM_FileName() {
		return m_FileName;
	}



	/**
	 * @param m_FileName the m_FileName to set
	 */
	public synchronized void setM_FileName(String m_FileName) {
		this.m_FileName = m_FileName;
	}



	/**
	 * @return the m_DateRange
	 */
	public synchronized String getM_DateRange() {
		return m_DateRange;
	}



	/**
	 * @param m_DateRange the m_DateRange to set
	 */
	public synchronized void setM_DateRange(String m_DateRange) {
		this.m_DateRange = m_DateRange;
	}



	/**
	 * @return the m_EntriesAdded
	 */
	public synchronized int getM_EntriesAdded() {
		return m_EntriesAdded;
	}

	/**
	 * @param m_EntriesAdded the m_EntriesAdded to set
	 */
	public synchronized void setM_EntriesAdded(int m_EntriesAdded) {
		this.m_EntriesAdded = m_EntriesAdded;
	}

	/**
	 * @return the m_TreatmentsAtStart
	 */
	public synchronized int getM_TreatmentsAtStart() {
		return m_TreatmentsAtStart;
	}

	/**
	 * @param m_TreatmentsAtStart the m_TreatmentsAtStart to set
	 */
	public synchronized void setM_TreatmentsAtStart(int m_TreatmentsAtStart) {
		this.m_TreatmentsAtStart = m_TreatmentsAtStart;
	}

	/**
	 * @return the m_TreatmentsByNSLAtStart
	 */
	public synchronized int getM_TreatmentsByNSLAtStart() {
		return m_TreatmentsByNSLAtStart;
	}

	/**
	 * @param m_TreatmentsByNSLAtStart the m_TreatmentsByNSLAtStart to set
	 */
	public synchronized void setM_TreatmentsByNSLAtStart(int m_TreatmentsByNSLAtStart) {
		this.m_TreatmentsByNSLAtStart = m_TreatmentsByNSLAtStart;
	}



	/**
	 * @return the m_ProximityEntries
	 */
	public synchronized int getM_ProximityEntries() {
		return m_ProximityEntries;
	}

	/**
	 * @param m_ProximityEntries the m_ProximityEntries to set
	 */
	public synchronized void setM_ProximityEntries(int m_ProximityEntries) {
		this.m_ProximityEntries = m_ProximityEntries;
	}

	/**
	 * @return the m_ColNames
	 */
	public static synchronized String[] getM_ColNames () {
		return m_ColNames;
	}

	/**
	 * @param m_ColNames the m_ColNames to set
	 */
	public static synchronized void setM_ColNames(String[] m_ColNames) {
		AuditLog.m_ColNames = m_ColNames;
	}

	/**
	 * @return the m_ColWidths
	 */
	public static synchronized int[] getM_ColWidths() {
		return m_ColWidths;
	}

	/**
	 * @param m_ColWidths the m_ColWidths to set
	 */
	public static synchronized void setM_ColWidths(int[] m_ColWidths) {
		AuditLog.m_ColWidths = m_ColWidths;
	}

	/**
	 * @return the m_Initializer
	 */
	public static synchronized Object[][] getM_Initializer() {
		return m_Initializer;
	}



	/**
	 * @param m_Initializer the m_Initializer to set
	 */
	public static synchronized void setM_Initializer(Object[][] m_Initializer) {
		AuditLog.m_Initializer = m_Initializer;
	}
}
