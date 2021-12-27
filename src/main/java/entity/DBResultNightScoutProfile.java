package entity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import control.MyLogger;
import utils.CommonUtils;

public class DBResultNightScoutProfile 
{
	protected static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	// Newest entity that holds profile from Nightscout MongoDB
	// A profile consists of:
	//  Basal rates
	//  Carb Ratios
	//  Insulin Sensitivities
	//  BG Targets
	
	protected String m_ID;
	protected String m_DefaultProfile;
	protected Long   m_Dia;
	protected Long   m_CarbHours;
	protected Long   m_Delay;
	protected String m_Timezone;
	protected Date   m_StartDate;
	protected Long   m_Mills;
	protected String m_Unit;
	protected Date   m_CreatedAt;
	
	
	// To Do
	
	// Change the Target to separate lists - one for low and one for high
	// Check that the documents can load as coded below - one document in another.
	
	
	protected ArrayList <DBResultPumpSettingBasal>     m_BasalSettings;
	protected ArrayList <DBResultPumpSettingISF>       m_ISFSettings;
	protected ArrayList <DBResultPumpSettingCarbRatio> m_CarbRatioSettings;
	protected ArrayList <DBResultPumpSettingTarget>    m_TargetSettings;
	
	
	
	public  DBResultNightScoutProfile(Document rs)
	{
		// Put all into a try-catch block since format.parse can raise exception
		try
		{
				m_ID             = CommonUtils.getIDStr(rs, "_id");
				m_DefaultProfile = CommonUtils.getFieldStr(rs, "defaultProfile");
				m_CarbHours      = CommonUtils.getFieldLong(rs, "carbs_hr");
				m_Unit           = CommonUtils.getFieldStr(rs, "units");
				

				// Nightscout times are in UTC.
				// Need to convert them to local time.
				Date utcTime      = CommonUtils.convertNSZDateString(CommonUtils.getFieldStr(rs, "created_at"));
				m_CreatedAt       = new Date(CommonUtils.toLocalTime(utcTime.getTime(), CommonUtils.locTZ));

				
				Document storeDoc = CommonUtils.getFieldDocument(rs, "store");
				Document defaultDoc = CommonUtils.getFieldDocument(storeDoc, "store");
				
				m_Dia            = CommonUtils.getFieldLong(defaultDoc, "dia");
				m_CarbHours      = CommonUtils.getFieldLong(defaultDoc, "carbs_hr");
				m_Delay          = CommonUtils.getFieldLong(defaultDoc, "delay");
				m_Timezone       = CommonUtils.getFieldStr(defaultDoc, "timezone");

				// Nightscout times are in UTC.
				// Need to convert them to local time.
				utcTime          = CommonUtils.convertNSZDateString(CommonUtils.getFieldStr(defaultDoc, "startDate"));
				m_StartDate      = new Date(CommonUtils.toLocalTime(utcTime.getTime(), CommonUtils.locTZ));
				m_Unit           = CommonUtils.getFieldStr(defaultDoc, "timezone");
				
				
			
		}
		catch (ParseException e) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "DBResultNightScout Caught Exception in MongoDB load "+e.toString());
		}
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
	 * @return the m_DefaultProfile
	 */
	public synchronized String getM_DefaultProfile() {
		return m_DefaultProfile;
	}


	/**
	 * @param m_DefaultProfile the m_DefaultProfile to set
	 */
	public synchronized void setM_DefaultProfile(String m_DefaultProfile) {
		this.m_DefaultProfile = m_DefaultProfile;
	}


	/**
	 * @return the m_Dia
	 */
	public synchronized Long getM_Dia() {
		return m_Dia;
	}
	/**
	 * @param m_Dia the m_Dia to set
	 */
	public synchronized void setM_Dia(Long m_Dia) {
		this.m_Dia = m_Dia;
	}
	/**
	 * @return the m_CarbHours
	 */
	public synchronized Long getM_CarbHours() {
		return m_CarbHours;
	}
	/**
	 * @param m_CarbHours the m_CarbHours to set
	 */
	public synchronized void setM_CarbHours(Long m_CarbHours) {
		this.m_CarbHours = m_CarbHours;
	}
	/**
	 * @return the m_Delay
	 */
	public synchronized Long getM_Delay() {
		return m_Delay;
	}
	/**
	 * @param m_Delay the m_Delay to set
	 */
	public synchronized void setM_Delay(Long m_Delay) {
		this.m_Delay = m_Delay;
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
	 * @return the m_StartDate
	 */
	public synchronized Date getM_StartDate() {
		return m_StartDate;
	}
	/**
	 * @param m_StartDate the m_StartDate to set
	 */
	public synchronized void setM_StartDate(Date m_StartDate) {
		this.m_StartDate = m_StartDate;
	}
	/**
	 * @return the m_Mills
	 */
	public synchronized Long getM_Mills() {
		return m_Mills;
	}
	/**
	 * @param m_Mills the m_Mills to set
	 */
	public synchronized void setM_Mills(Long m_Mills) {
		this.m_Mills = m_Mills;
	}
	/**
	 * @return the m_Unit
	 */
	public synchronized String getM_Unit() {
		return m_Unit;
	}
	/**
	 * @param m_Unit the m_Unit to set
	 */
	public synchronized void setM_Unit(String m_Unit) {
		this.m_Unit = m_Unit;
	}
	/**
	 * @return the m_CreatedAt
	 */
	public synchronized Date getM_CreatedAt() {
		return m_CreatedAt;
	}
	/**
	 * @param m_CreatedAt the m_CreatedAt to set
	 */
	public synchronized void setM_CreatedAt(Date m_CreatedAt) {
		this.m_CreatedAt = m_CreatedAt;
	}
	/**
	 * @return the m_BasalSettings
	 */
	public synchronized ArrayList<DBResultPumpSettingBasal> getM_BasalSettings() {
		return m_BasalSettings;
	}
	/**
	 * @param m_BasalSettings the m_BasalSettings to set
	 */
	public synchronized void setM_BasalSettings(ArrayList<DBResultPumpSettingBasal> m_BasalSettings) {
		this.m_BasalSettings = m_BasalSettings;
	}
	/**
	 * @return the m_ISFSettings
	 */
	public synchronized ArrayList<DBResultPumpSettingISF> getM_ISFSettings() {
		return m_ISFSettings;
	}
	/**
	 * @param m_ISFSettings the m_ISFSettings to set
	 */
	public synchronized void setM_ISFSettings(ArrayList<DBResultPumpSettingISF> m_ISFSettings) {
		this.m_ISFSettings = m_ISFSettings;
	}
	/**
	 * @return the m_CarbRatioSettings
	 */
	public synchronized ArrayList<DBResultPumpSettingCarbRatio> getM_CarbRatioSettings() {
		return m_CarbRatioSettings;
	}
	/**
	 * @param m_CarbRatioSettings the m_CarbRatioSettings to set
	 */
	public synchronized void setM_CarbRatioSettings(ArrayList<DBResultPumpSettingCarbRatio> m_CarbRatioSettings) {
		this.m_CarbRatioSettings = m_CarbRatioSettings;
	}
	/**
	 * @return the m_TargetSettings
	 */
	public synchronized ArrayList<DBResultPumpSettingTarget> getM_TargetSettings() {
		return m_TargetSettings;
	}
	/**
	 * @param m_TargetSettings the m_TargetSettings to set
	 */
	public synchronized void setM_TargetSettings(ArrayList<DBResultPumpSettingTarget> m_TargetSettings) {
		this.m_TargetSettings = m_TargetSettings;
	}

}
