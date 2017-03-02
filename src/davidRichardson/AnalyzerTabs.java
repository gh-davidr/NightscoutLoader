package davidRichardson;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnalyzerTabs 
{
	// Implements the Singleton Design Pattern
	private static AnalyzerTabs m_Instance=null;

	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	public static AnalyzerTabs getInstance()
	{
		if (m_Instance == null)
		{
			m_Instance = new AnalyzerTabs();
		}
		return m_Instance;
	}

	class Tab
	{
		private String m_TabName;
		private boolean m_Enabled;

		Tab(String tabName, boolean enabled)
		{
			m_TabName = new String(tabName);
			m_Enabled = enabled;
		}

		/**
		 * @return the m_TabName
		 */
		public synchronized String getM_TabName() {
			return m_TabName;
		}

		/**
		 * @param m_TabName the m_TabName to set
		 */
		public synchronized void setM_TabName(String m_TabName) {
			this.m_TabName = m_TabName;
		}

		/**
		 * @return the m_Enabled
		 */
		public synchronized boolean isM_Enabled() {
			return m_Enabled;
		}

		/**
		 * @param m_Enabled the m_Enabled to set
		 */
		public synchronized void setM_Enabled(boolean m_Enabled) {
			this.m_Enabled = m_Enabled;
		}
	}

	private ArrayList<Tab> m_ListOfTabs;
	private ArrayList<Tab> m_MinimalListOfTabs;


	private AnalyzerTabs()
	{
		m_ListOfTabs = new ArrayList<Tab>();
		m_MinimalListOfTabs = new ArrayList<Tab>();
	}

	boolean isTabEnabled(String tabName)
	{
		boolean advancedSettings = PrefsNightScoutLoader.getInstance().isM_AdvancedSettings();
		
		// Only if advanced is true do we use the set values.
		// Else ignore and use the minimal list instead.
		ArrayList<Tab> searchList = advancedSettings == true ? m_ListOfTabs : m_MinimalListOfTabs;
		
		boolean result = false;
		for (Tab t : searchList)
		{
			//			if (t.getM_TabName().equals(tabName))
			if (tabName.equals(t.getM_TabName()))
			{
				result = t.isM_Enabled();
				break;
			}
		}
		return result;
	}

	public void setupListOfTabs()
	{
		int excelOutputLevel = PrefsNightScoutLoader.getInstance().getM_AnalyzerExcelOutputLevel();
		setupListOfTabs(this.m_ListOfTabs, excelOutputLevel);
		setupListOfTabs(this.m_MinimalListOfTabs, 0);
		
		m_Logger.log(Level.FINE, "Added full list");
	}


	private void setupListOfTabs(ArrayList<Tab> searchList, int level)
	{		
		searchList.clear();
		
		// level = 0 ==> Minimal
		// level = 1 ==> Moderate
		// level = 2 ==> Maximum

		if (level >= 0) searchList.add(new Tab("Guide to Tabs", true));
		if (level >= 0) searchList.add(new Tab("Autotune", true));
		if (level >= 0) searchList.add(new Tab("Recurring Trends", true));
		if (level == 2) searchList.add(new Tab("Trends", true));
		if (level >= 1) searchList.add(new Tab("Skipped Meal Trends", true));
		if (level >= 0) searchList.add(new Tab("CGM Summary", true));
		if (level >= 0) searchList.add(new Tab("CGM Heat Map", true));
		if (level >= 1) searchList.add(new Tab("BGs Outside Range", true));
		if (level >= 1) searchList.add(new Tab("Day Summaries", true));
		if (level == 2) searchList.add(new Tab("Single Results", true));
		if (level == 2) searchList.add(new Tab("Treatment Data Analyzed", true));
		if (level == 2) searchList.add(new Tab("In Range CGM Trend Result Entries", true));
		if (level == 2) searchList.add(new Tab("In Range CGM Entry Intervals", true));
		if (level == 2) searchList.add(new Tab("In Range CGM Results", true));
		if (level == 2) searchList.add(new Tab("Full History Trends", true));
		if (level >= 1) searchList.add(new Tab("Comparison to Full History", true));
		if (level >= 0) searchList.add(new Tab("Parameters", true));
		if (level >= 0) searchList.add(new Tab("Settings", true));
		if (level >= 0) searchList.add(new Tab("Trend Explanations", true));
	}
}
