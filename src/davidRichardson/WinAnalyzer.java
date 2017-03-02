package davidRichardson;

import javax.swing.JButton;
//import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.JTextField;
//import javax.swing.JTextArea;
//import javax.swing.JScrollPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.EventQueue;
//import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import davidRichardson.ThreadAnalyzer.AnalyzerCompleteHander;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

//import javax.swing.JComboBox;
//import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerDateModel;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Calendar;
//import javax.swing.JToggleButton;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;


//public class WinAnalyzer  extends JDialog implements WinSetDatesInterface
public class WinAnalyzer  extends JFrame implements WinSetDatesInterface
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6089944380592193437L;

	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	private WinNightScoutLoader     m_MainWin;

	private JTextField tf_ExcelFile;
	private JTextField tf_LowThreshold;
	private JTextField tf_LunchTrendRatio;
	private JTextField tf_MinMinsForTrend;
	private JSpinner tf_MediumPriorityRecurrRatio;
	private JTextField tf_LowRangeThreshold;
	private JTextField tf_HighRangeThreshold;

	private JLabel lblHighPriorityRecurrRatio;
	private JLabel lblMediumPriorityRecurrRatio;
	private JLabel lblLowRangeThreshold;
	private JLabel lblHighRangeThreshold;

	//	private DataLoadRoche m_SQLServerLoader;
	//	private DataLoadNightScoutTreatments   m_MongoDBLoader;

	private JLabel lblDaysBack;
	private JLabel lblHighThreshold;
	private JTextField tf_HighThreshold;
	private JLabel lblHighThresholdRelevance;
	private JLabel lblLowThresholdRelevanceFactor;
	private JTextField tf_HighThresholdRelevanceFactor;
	private JTextField tf_LowThresholdRelevanceFactor;
	private JLabel lblOvernightTrendRatio;
	private JTextField tf_OvernightTrendRatio;
	private JLabel lblBreakfastTrendRatio;
	private JTextField tf_BreakfastTrendRatio;
	private JLabel lblDinnerTrendRatio;
	private JTextField tf_DinnerTrendRatio;
	private JSpinner tf_HighPriorityRecurrRatio;
	private GridBagConstraints gbc_lblDaysBack;
	private JLabel lblBedTrendStartStartTime;
	private JLabel lblBedTrendStartEndTime;
	private JLabel lblBedTrendEndStartTime;
	private JLabel lblBedTrendEndEndTime;
	private JLabel lblBadNightStartTime;
	private JButton btnResetToDefaults;
	private JLabel lblBadNightEndTime;
	private JRadioButton rdbtnCompressMealTrends;
	private JRadioButton rdbtnTotalRecurringTrendsOnly;
	private JRadioButton rdbtnIncludeBreakfast;
	private JRadioButton rdbtnIncludeLunch;
	private JRadioButton rdbtnIncludeDinner;
	private JRadioButton rdbtnIncludeOvernight;

	private JRadioButton rdbtnAutoTuneInvoked;
	private JLabel lblAutoTuneServer;
	private JTextField tf_AutoTuneServer;
	private JLabel lblAutoTuneNSURL;
	private JTextField tf_AutoTuneNSURL;
	private JRadioButton rdbtnAutoTuneSSH2KeyLogin;
	private JLabel lblAutoTuneKeyFile;
	private JTextField tf_AutoTuneKeyFile;
	
	private JButton m_AnaylzeButton;
	private JTextField tf_BGUnits;
	private JSpinner sp_breakfastStart;
	private JSpinner sp_lunchStart;
	private JSpinner sp_dinnerStart;
	private JSpinner sp_bedStart;
	private JSpinner sp_BadnightStart;
	private JSpinner sp_BadnightEnd;
	private JSpinner sp_bedTrendStartStart;
	private JSpinner sp_bedTrendStartEnd;
	private JSpinner sp_bedTrendEndStart;
	private JSpinner sp_bedTrendEndEnd;
	private JSpinner sp_DaysBack;
	private JSpinner sp_StartDate;
	private JSpinner sp_EndDate;
	private JSpinner sp_CGMTrendIntervalDuration;

	private JDatePickerImpl jp_StartDate;
	private JDatePickerImpl jp_EndDate;

	private JLabel lblStartDate;
	private JLabel lblEndDate;

	private boolean m_SettingDatesOrDays = false;

	private Date    m_MostRecentResultDate = new Date(0);
	private Date    m_OldestResultDate     = new Date(0);
	private String  m_MostRecentResultDateStr = new String("");
	private String  m_OldestResultDateStr     = new String("");
	private JButton btnCGMDates;

	private WinCGMRanges m_WinCGMRanges = null;
	private JComboBox<String> cb_ExcelOutputLevel;
	private JLabel lblCgmTrendHour;

	/**
	 * Launch the application.
	 */
	/**
	 * @param args
	 */

	private SpinnerListModel getTimeListModel()
	{
		SpinnerListModel result = new SpinnerListModel(new String[] {"00:00:00", "00:01:00", "00:02:00", "00:03:00", "00:04:00", "00:05:00", "00:06:00", "00:07:00", "00:08:00", "00:09:00", "00:10:00", "00:11:00", "00:12:00", "00:13:00", "00:14:00", "00:15:00", "00:16:00", "00:17:00", "00:18:00", "00:19:00", "00:20:00", "00:21:00", "00:22:00", "00:23:00", "00:24:00", "00:25:00", "00:26:00", "00:27:00", "00:28:00", "00:29:00", "00:30:00", "00:31:00", "00:32:00", "00:33:00", "00:34:00", "00:35:00", "00:36:00", "00:37:00", "00:38:00", "00:39:00", "00:40:00", "00:41:00", "00:42:00", "00:43:00", "00:44:00", "00:45:00", "00:46:00", "00:47:00", "00:48:00", "00:49:00", "00:50:00", "00:51:00", "00:52:00", "00:53:00", "00:54:00", "00:55:00", "00:56:00", "00:57:00", "00:58:00", "00:59:00", "01:00:00", "01:01:00", "01:02:00", "01:03:00", "01:04:00", "01:05:00", "01:06:00", "01:07:00", "01:08:00", "01:09:00", "01:10:00", "01:11:00", "01:12:00", "01:13:00", "01:14:00", "01:15:00", "01:16:00", "01:17:00", "01:18:00", "01:19:00", "01:20:00", "01:21:00", "01:22:00", "01:23:00", "01:24:00", "01:25:00", "01:26:00", "01:27:00", "01:28:00", "01:29:00", "01:30:00", "01:31:00", "01:32:00", "01:33:00", "01:34:00", "01:35:00", "01:36:00", "01:37:00", "01:38:00", "01:39:00", "01:40:00", "01:41:00", "01:42:00", "01:43:00", "01:44:00", "01:45:00", "01:46:00", "01:47:00", "01:48:00", "01:49:00", "01:50:00", "01:51:00", "01:52:00", "01:53:00", "01:54:00", "01:55:00", "01:56:00", "01:57:00", "01:58:00", "01:59:00", "02:00:00", "02:01:00", "02:02:00", "02:03:00", "02:04:00", "02:05:00", "02:06:00", "02:07:00", "02:08:00", "02:09:00", "02:10:00", "02:11:00", "02:12:00", "02:13:00", "02:14:00", "02:15:00", "02:16:00", "02:17:00", "02:18:00", "02:19:00", "02:20:00", "02:21:00", "02:22:00", "02:23:00", "02:24:00", "02:25:00", "02:26:00", "02:27:00", "02:28:00", "02:29:00", "02:30:00", "02:31:00", "02:32:00", "02:33:00", "02:34:00", "02:35:00", "02:36:00", "02:37:00", "02:38:00", "02:39:00", "02:40:00", "02:41:00", "02:42:00", "02:43:00", "02:44:00", "02:45:00", "02:46:00", "02:47:00", "02:48:00", "02:49:00", "02:50:00", "02:51:00", "02:52:00", "02:53:00", "02:54:00", "02:55:00", "02:56:00", "02:57:00", "02:58:00", "02:59:00", "03:00:00", "03:01:00", "03:02:00", "03:03:00", "03:04:00", "03:05:00", "03:06:00", "03:07:00", "03:08:00", "03:09:00", "03:10:00", "03:11:00", "03:12:00", "03:13:00", "03:14:00", "03:15:00", "03:16:00", "03:17:00", "03:18:00", "03:19:00", "03:20:00", "03:21:00", "03:22:00", "03:23:00", "03:24:00", "03:25:00", "03:26:00", "03:27:00", "03:28:00", "03:29:00", "03:30:00", "03:31:00", "03:32:00", "03:33:00", "03:34:00", "03:35:00", "03:36:00", "03:37:00", "03:38:00", "03:39:00", "03:40:00", "03:41:00", "03:42:00", "03:43:00", "03:44:00", "03:45:00", "03:46:00", "03:47:00", "03:48:00", "03:49:00", "03:50:00", "03:51:00", "03:52:00", "03:53:00", "03:54:00", "03:55:00", "03:56:00", "03:57:00", "03:58:00", "03:59:00", "04:00:00", "04:01:00", "04:02:00", "04:03:00", "04:04:00", "04:05:00", "04:06:00", "04:07:00", "04:08:00", "04:09:00", "04:10:00", "04:11:00", "04:12:00", "04:13:00", "04:14:00", "04:15:00", "04:16:00", "04:17:00", "04:18:00", "04:19:00", "04:20:00", "04:21:00", "04:22:00", "04:23:00", "04:24:00", "04:25:00", "04:26:00", "04:27:00", "04:28:00", "04:29:00", "04:30:00", "04:31:00", "04:32:00", "04:33:00", "04:34:00", "04:35:00", "04:36:00", "04:37:00", "04:38:00", "04:39:00", "04:40:00", "04:41:00", "04:42:00", "04:43:00", "04:44:00", "04:45:00", "04:46:00", "04:47:00", "04:48:00", "04:49:00", "04:50:00", "04:51:00", "04:52:00", "04:53:00", "04:54:00", "04:55:00", "04:56:00", "04:57:00", "04:58:00", "04:59:00", "05:00:00", "05:01:00", "05:02:00", "05:03:00", "05:04:00", "05:05:00", "05:06:00", "05:07:00", "05:08:00", "05:09:00", "05:10:00", "05:11:00", "05:12:00", "05:13:00", "05:14:00", "05:15:00", "05:16:00", "05:17:00", "05:18:00", "05:19:00", "05:20:00", "05:21:00", "05:22:00", "05:23:00", "05:24:00", "05:25:00", "05:26:00", "05:27:00", "05:28:00", "05:29:00", "05:30:00", "05:31:00", "05:32:00", "05:33:00", "05:34:00", "05:35:00", "05:36:00", "05:37:00", "05:38:00", "05:39:00", "05:40:00", "05:41:00", "05:42:00", "05:43:00", "05:44:00", "05:45:00", "05:46:00", "05:47:00", "05:48:00", "05:49:00", "05:50:00", "05:51:00", "05:52:00", "05:53:00", "05:54:00", "05:55:00", "05:56:00", "05:57:00", "05:58:00", "05:59:00", "06:00:00", "06:01:00", "06:02:00", "06:03:00", "06:04:00", "06:05:00", "06:06:00", "06:07:00", "06:08:00", "06:09:00", "06:10:00", "06:11:00", "06:12:00", "06:13:00", "06:14:00", "06:15:00", "06:16:00", "06:17:00", "06:18:00", "06:19:00", "06:20:00", "06:21:00", "06:22:00", "06:23:00", "06:24:00", "06:25:00", "06:26:00", "06:27:00", "06:28:00", "06:29:00", "06:30:00", "06:31:00", "06:32:00", "06:33:00", "06:34:00", "06:35:00", "06:36:00", "06:37:00", "06:38:00", "06:39:00", "06:40:00", "06:41:00", "06:42:00", "06:43:00", "06:44:00", "06:45:00", "06:46:00", "06:47:00", "06:48:00", "06:49:00", "06:50:00", "06:51:00", "06:52:00", "06:53:00", "06:54:00", "06:55:00", "06:56:00", "06:57:00", "06:58:00", "06:59:00", "07:00:00", "07:01:00", "07:02:00", "07:03:00", "07:04:00", "07:05:00", "07:06:00", "07:07:00", "07:08:00", "07:09:00", "07:10:00", "07:11:00", "07:12:00", "07:13:00", "07:14:00", "07:15:00", "07:16:00", "07:17:00", "07:18:00", "07:19:00", "07:20:00", "07:21:00", "07:22:00", "07:23:00", "07:24:00", "07:25:00", "07:26:00", "07:27:00", "07:28:00", "07:29:00", "07:30:00", "07:31:00", "07:32:00", "07:33:00", "07:34:00", "07:35:00", "07:36:00", "07:37:00", "07:38:00", "07:39:00", "07:40:00", "07:41:00", "07:42:00", "07:43:00", "07:44:00", "07:45:00", "07:46:00", "07:47:00", "07:48:00", "07:49:00", "07:50:00", "07:51:00", "07:52:00", "07:53:00", "07:54:00", "07:55:00", "07:56:00", "07:57:00", "07:58:00", "07:59:00", "08:00:00", "08:01:00", "08:02:00", "08:03:00", "08:04:00", "08:05:00", "08:06:00", "08:07:00", "08:08:00", "08:09:00", "08:10:00", "08:11:00", "08:12:00", "08:13:00", "08:14:00", "08:15:00", "08:16:00", "08:17:00", "08:18:00", "08:19:00", "08:20:00", "08:21:00", "08:22:00", "08:23:00", "08:24:00", "08:25:00", "08:26:00", "08:27:00", "08:28:00", "08:29:00", "08:30:00", "08:31:00", "08:32:00", "08:33:00", "08:34:00", "08:35:00", "08:36:00", "08:37:00", "08:38:00", "08:39:00", "08:40:00", "08:41:00", "08:42:00", "08:43:00", "08:44:00", "08:45:00", "08:46:00", "08:47:00", "08:48:00", "08:49:00", "08:50:00", "08:51:00", "08:52:00", "08:53:00", "08:54:00", "08:55:00", "08:56:00", "08:57:00", "08:58:00", "08:59:00", "09:00:00", "09:01:00", "09:02:00", "09:03:00", "09:04:00", "09:05:00", "09:06:00", "09:07:00", "09:08:00", "09:09:00", "09:10:00", "09:11:00", "09:12:00", "09:13:00", "09:14:00", "09:15:00", "09:16:00", "09:17:00", "09:18:00", "09:19:00", "09:20:00", "09:21:00", "09:22:00", "09:23:00", "09:24:00", "09:25:00", "09:26:00", "09:27:00", "09:28:00", "09:29:00", "09:30:00", "09:31:00", "09:32:00", "09:33:00", "09:34:00", "09:35:00", "09:36:00", "09:37:00", "09:38:00", "09:39:00", "09:40:00", "09:41:00", "09:42:00", "09:43:00", "09:44:00", "09:45:00", "09:46:00", "09:47:00", "09:48:00", "09:49:00", "09:50:00", "09:51:00", "09:52:00", "09:53:00", "09:54:00", "09:55:00", "09:56:00", "09:57:00", "09:58:00", "09:59:00", "10:00:00", "10:01:00", "10:02:00", "10:03:00", "10:04:00", "10:05:00", "10:06:00", "10:07:00", "10:08:00", "10:09:00", "10:10:00", "10:11:00", "10:12:00", "10:13:00", "10:14:00", "10:15:00", "10:16:00", "10:17:00", "10:18:00", "10:19:00", "10:20:00", "10:21:00", "10:22:00", "10:23:00", "10:24:00", "10:25:00", "10:26:00", "10:27:00", "10:28:00", "10:29:00", "10:30:00", "10:31:00", "10:32:00", "10:33:00", "10:34:00", "10:35:00", "10:36:00", "10:37:00", "10:38:00", "10:39:00", "10:40:00", "10:41:00", "10:42:00", "10:43:00", "10:44:00", "10:45:00", "10:46:00", "10:47:00", "10:48:00", "10:49:00", "10:50:00", "10:51:00", "10:52:00", "10:53:00", "10:54:00", "10:55:00", "10:56:00", "10:57:00", "10:58:00", "10:59:00", "11:00:00", "11:01:00", "11:02:00", "11:03:00", "11:04:00", "11:05:00", "11:06:00", "11:07:00", "11:08:00", "11:09:00", "11:10:00", "11:11:00", "11:12:00", "11:13:00", "11:14:00", "11:15:00", "11:16:00", "11:17:00", "11:18:00", "11:19:00", "11:20:00", "11:21:00", "11:22:00", "11:23:00", "11:24:00", "11:25:00", "11:26:00", "11:27:00", "11:28:00", "11:29:00", "11:30:00", "11:31:00", "11:32:00", "11:33:00", "11:34:00", "11:35:00", "11:36:00", "11:37:00", "11:38:00", "11:39:00", "11:40:00", "11:41:00", "11:42:00", "11:43:00", "11:44:00", "11:45:00", "11:46:00", "11:47:00", "11:48:00", "11:49:00", "11:50:00", "11:51:00", "11:52:00", "11:53:00", "11:54:00", "11:55:00", "11:56:00", "11:57:00", "11:58:00", "11:59:00", "12:00:00", "12:01:00", "12:02:00", "12:03:00", "12:04:00", "12:05:00", "12:06:00", "12:07:00", "12:08:00", "12:09:00", "12:10:00", "12:11:00", "12:12:00", "12:13:00", "12:14:00", "12:15:00", "12:16:00", "12:17:00", "12:18:00", "12:19:00", "12:20:00", "12:21:00", "12:22:00", "12:23:00", "12:24:00", "12:25:00", "12:26:00", "12:27:00", "12:28:00", "12:29:00", "12:30:00", "12:31:00", "12:32:00", "12:33:00", "12:34:00", "12:35:00", "12:36:00", "12:37:00", "12:38:00", "12:39:00", "12:40:00", "12:41:00", "12:42:00", "12:43:00", "12:44:00", "12:45:00", "12:46:00", "12:47:00", "12:48:00", "12:49:00", "12:50:00", "12:51:00", "12:52:00", "12:53:00", "12:54:00", "12:55:00", "12:56:00", "12:57:00", "12:58:00", "12:59:00", "13:00:00", "13:01:00", "13:02:00", "13:03:00", "13:04:00", "13:05:00", "13:06:00", "13:07:00", "13:08:00", "13:09:00", "13:10:00", "13:11:00", "13:12:00", "13:13:00", "13:14:00", "13:15:00", "13:16:00", "13:17:00", "13:18:00", "13:19:00", "13:20:00", "13:21:00", "13:22:00", "13:23:00", "13:24:00", "13:25:00", "13:26:00", "13:27:00", "13:28:00", "13:29:00", "13:30:00", "13:31:00", "13:32:00", "13:33:00", "13:34:00", "13:35:00", "13:36:00", "13:37:00", "13:38:00", "13:39:00", "13:40:00", "13:41:00", "13:42:00", "13:43:00", "13:44:00", "13:45:00", "13:46:00", "13:47:00", "13:48:00", "13:49:00", "13:50:00", "13:51:00", "13:52:00", "13:53:00", "13:54:00", "13:55:00", "13:56:00", "13:57:00", "13:58:00", "13:59:00", "14:00:00", "14:01:00", "14:02:00", "14:03:00", "14:04:00", "14:05:00", "14:06:00", "14:07:00", "14:08:00", "14:09:00", "14:10:00", "14:11:00", "14:12:00", "14:13:00", "14:14:00", "14:15:00", "14:16:00", "14:17:00", "14:18:00", "14:19:00", "14:20:00", "14:21:00", "14:22:00", "14:23:00", "14:24:00", "14:25:00", "14:26:00", "14:27:00", "14:28:00", "14:29:00", "14:30:00", "14:31:00", "14:32:00", "14:33:00", "14:34:00", "14:35:00", "14:36:00", "14:37:00", "14:38:00", "14:39:00", "14:40:00", "14:41:00", "14:42:00", "14:43:00", "14:44:00", "14:45:00", "14:46:00", "14:47:00", "14:48:00", "14:49:00", "14:50:00", "14:51:00", "14:52:00", "14:53:00", "14:54:00", "14:55:00", "14:56:00", "14:57:00", "14:58:00", "14:59:00", "15:00:00", "15:01:00", "15:02:00", "15:03:00", "15:04:00", "15:05:00", "15:06:00", "15:07:00", "15:08:00", "15:09:00", "15:10:00", "15:11:00", "15:12:00", "15:13:00", "15:14:00", "15:15:00", "15:16:00", "15:17:00", "15:18:00", "15:19:00", "15:20:00", "15:21:00", "15:22:00", "15:23:00", "15:24:00", "15:25:00", "15:26:00", "15:27:00", "15:28:00", "15:29:00", "15:30:00", "15:31:00", "15:32:00", "15:33:00", "15:34:00", "15:35:00", "15:36:00", "15:37:00", "15:38:00", "15:39:00", "15:40:00", "15:41:00", "15:42:00", "15:43:00", "15:44:00", "15:45:00", "15:46:00", "15:47:00", "15:48:00", "15:49:00", "15:50:00", "15:51:00", "15:52:00", "15:53:00", "15:54:00", "15:55:00", "15:56:00", "15:57:00", "15:58:00", "15:59:00", "16:00:00", "16:01:00", "16:02:00", "16:03:00", "16:04:00", "16:05:00", "16:06:00", "16:07:00", "16:08:00", "16:09:00", "16:10:00", "16:11:00", "16:12:00", "16:13:00", "16:14:00", "16:15:00", "16:16:00", "16:17:00", "16:18:00", "16:19:00", "16:20:00", "16:21:00", "16:22:00", "16:23:00", "16:24:00", "16:25:00", "16:26:00", "16:27:00", "16:28:00", "16:29:00", "16:30:00", "16:31:00", "16:32:00", "16:33:00", "16:34:00", "16:35:00", "16:36:00", "16:37:00", "16:38:00", "16:39:00", "16:40:00", "16:41:00", "16:42:00", "16:43:00", "16:44:00", "16:45:00", "16:46:00", "16:47:00", "16:48:00", "16:49:00", "16:50:00", "16:51:00", "16:52:00", "16:53:00", "16:54:00", "16:55:00", "16:56:00", "16:57:00", "16:58:00", "16:59:00", "17:00:00", "17:01:00", "17:02:00", "17:03:00", "17:04:00", "17:05:00", "17:06:00", "17:07:00", "17:08:00", "17:09:00", "17:10:00", "17:11:00", "17:12:00", "17:13:00", "17:14:00", "17:15:00", "17:16:00", "17:17:00", "17:18:00", "17:19:00", "17:20:00", "17:21:00", "17:22:00", "17:23:00", "17:24:00", "17:25:00", "17:26:00", "17:27:00", "17:28:00", "17:29:00", "17:30:00", "17:31:00", "17:32:00", "17:33:00", "17:34:00", "17:35:00", "17:36:00", "17:37:00", "17:38:00", "17:39:00", "17:40:00", "17:41:00", "17:42:00", "17:43:00", "17:44:00", "17:45:00", "17:46:00", "17:47:00", "17:48:00", "17:49:00", "17:50:00", "17:51:00", "17:52:00", "17:53:00", "17:54:00", "17:55:00", "17:56:00", "17:57:00", "17:58:00", "17:59:00", "18:00:00", "18:01:00", "18:02:00", "18:03:00", "18:04:00", "18:05:00", "18:06:00", "18:07:00", "18:08:00", "18:09:00", "18:10:00", "18:11:00", "18:12:00", "18:13:00", "18:14:00", "18:15:00", "18:16:00", "18:17:00", "18:18:00", "18:19:00", "18:20:00", "18:21:00", "18:22:00", "18:23:00", "18:24:00", "18:25:00", "18:26:00", "18:27:00", "18:28:00", "18:29:00", "18:30:00", "18:31:00", "18:32:00", "18:33:00", "18:34:00", "18:35:00", "18:36:00", "18:37:00", "18:38:00", "18:39:00", "18:40:00", "18:41:00", "18:42:00", "18:43:00", "18:44:00", "18:45:00", "18:46:00", "18:47:00", "18:48:00", "18:49:00", "18:50:00", "18:51:00", "18:52:00", "18:53:00", "18:54:00", "18:55:00", "18:56:00", "18:57:00", "18:58:00", "18:59:00", "19:00:00", "19:01:00", "19:02:00", "19:03:00", "19:04:00", "19:05:00", "19:06:00", "19:07:00", "19:08:00", "19:09:00", "19:10:00", "19:11:00", "19:12:00", "19:13:00", "19:14:00", "19:15:00", "19:16:00", "19:17:00", "19:18:00", "19:19:00", "19:20:00", "19:21:00", "19:22:00", "19:23:00", "19:24:00", "19:25:00", "19:26:00", "19:27:00", "19:28:00", "19:29:00", "19:30:00", "19:31:00", "19:32:00", "19:33:00", "19:34:00", "19:35:00", "19:36:00", "19:37:00", "19:38:00", "19:39:00", "19:40:00", "19:41:00", "19:42:00", "19:43:00", "19:44:00", "19:45:00", "19:46:00", "19:47:00", "19:48:00", "19:49:00", "19:50:00", "19:51:00", "19:52:00", "19:53:00", "19:54:00", "19:55:00", "19:56:00", "19:57:00", "19:58:00", "19:59:00", "20:00:00", "20:01:00", "20:02:00", "20:03:00", "20:04:00", "20:05:00", "20:06:00", "20:07:00", "20:08:00", "20:09:00", "20:10:00", "20:11:00", "20:12:00", "20:13:00", "20:14:00", "20:15:00", "20:16:00", "20:17:00", "20:18:00", "20:19:00", "20:20:00", "20:21:00", "20:22:00", "20:23:00", "20:24:00", "20:25:00", "20:26:00", "20:27:00", "20:28:00", "20:29:00", "20:30:00", "20:31:00", "20:32:00", "20:33:00", "20:34:00", "20:35:00", "20:36:00", "20:37:00", "20:38:00", "20:39:00", "20:40:00", "20:41:00", "20:42:00", "20:43:00", "20:44:00", "20:45:00", "20:46:00", "20:47:00", "20:48:00", "20:49:00", "20:50:00", "20:51:00", "20:52:00", "20:53:00", "20:54:00", "20:55:00", "20:56:00", "20:57:00", "20:58:00", "20:59:00", "21:00:00", "21:01:00", "21:02:00", "21:03:00", "21:04:00", "21:05:00", "21:06:00", "21:07:00", "21:08:00", "21:09:00", "21:10:00", "21:11:00", "21:12:00", "21:13:00", "21:14:00", "21:15:00", "21:16:00", "21:17:00", "21:18:00", "21:19:00", "21:20:00", "21:21:00", "21:22:00", "21:23:00", "21:24:00", "21:25:00", "21:26:00", "21:27:00", "21:28:00", "21:29:00", "21:30:00", "21:31:00", "21:32:00", "21:33:00", "21:34:00", "21:35:00", "21:36:00", "21:37:00", "21:38:00", "21:39:00", "21:40:00", "21:41:00", "21:42:00", "21:43:00", "21:44:00", "21:45:00", "21:46:00", "21:47:00", "21:48:00", "21:49:00", "21:50:00", "21:51:00", "21:52:00", "21:53:00", "21:54:00", "21:55:00", "21:56:00", "21:57:00", "21:58:00", "21:59:00", "22:00:00", "22:01:00", "22:02:00", "22:03:00", "22:04:00", "22:05:00", "22:06:00", "22:07:00", "22:08:00", "22:09:00", "22:10:00", "22:11:00", "22:12:00", "22:13:00", "22:14:00", "22:15:00", "22:16:00", "22:17:00", "22:18:00", "22:19:00", "22:20:00", "22:21:00", "22:22:00", "22:23:00", "22:24:00", "22:25:00", "22:26:00", "22:27:00", "22:28:00", "22:29:00", "22:30:00", "22:31:00", "22:32:00", "22:33:00", "22:34:00", "22:35:00", "22:36:00", "22:37:00", "22:38:00", "22:39:00", "22:40:00", "22:41:00", "22:42:00", "22:43:00", "22:44:00", "22:45:00", "22:46:00", "22:47:00", "22:48:00", "22:49:00", "22:50:00", "22:51:00", "22:52:00", "22:53:00", "22:54:00", "22:55:00", "22:56:00", "22:57:00", "22:58:00", "22:59:00", "23:00:00", "23:01:00", "23:02:00", "23:03:00", "23:04:00", "23:05:00", "23:06:00", "23:07:00", "23:08:00", "23:09:00", "23:10:00", "23:11:00", "23:12:00", "23:13:00", "23:14:00", "23:15:00", "23:16:00", "23:17:00", "23:18:00", "23:19:00", "23:20:00", "23:21:00", "23:22:00", "23:23:00", "23:24:00", "23:25:00", "23:26:00", "23:27:00", "23:28:00", "23:29:00", "23:30:00", "23:31:00", "23:32:00", "23:33:00", "23:34:00", "23:35:00", "23:36:00", "23:37:00", "23:38:00", "23:39:00", "23:40:00", "23:41:00", "23:42:00", "23:43:00", "23:44:00", "23:45:00", "23:46:00", "23:47:00", "23:48:00", "23:49:00", "23:50:00", "23:51:00", "23:52:00", "23:53:00", "23:54:00", "23:55:00", "23:56:00", "23:57:00", "23:58:00", "23:59:00"});
		return result;
	}

	/**
	 * Create the dialog.
	 */
	public WinAnalyzer(WinNightScoutLoader mainWin, String title) 
	{
		super();

		m_MainWin = mainWin;


		URL url = MainNightScoutLoader.class.getResource("/Nightscout.jpg");
		ImageIcon img = new ImageIcon(url);
		setIconImage(img.getImage());

		//		m_SQLServerLoader = new DataLoadRoche();
		//		m_MongoDBLoader   = new DataLoadNightScoutTreatments();

		super.setTitle(title);
		setBounds(100, 100, 780, 650);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {300, 0};
		gridBagLayout.rowHeights = new int[] {300, 25};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0};
		getContentPane().setLayout(gridBagLayout);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		getContentPane().add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] {50, 147, 0, 70, 0, 0, 0, 0, 0, 0};
		gbl_panel_1.rowHeights = new int[] {30, 0, 0, 0, 0, 0, 0, 0, 30, 30, 0, 30, 0, 0, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 1.0, 1.0};
		gbl_panel_1.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		panel_1.setLayout(gbl_panel_1);

		JLabel lblExcelFile = new JLabel("Excel file for results");
		GridBagConstraints gbc_lblExcelFile = new GridBagConstraints();
		gbc_lblExcelFile.anchor = GridBagConstraints.EAST;
		gbc_lblExcelFile.insets = new Insets(0, 0, 5, 5);
		gbc_lblExcelFile.gridx = 1;
		gbc_lblExcelFile.gridy = 0;
		panel_1.add(lblExcelFile, gbc_lblExcelFile);

		tf_ExcelFile = new JTextField(PrefsNightScoutLoader.getInstance().getM_AnalysisFilePath());
		tf_ExcelFile.setToolTipText("<html>Analysis results are stored in Excel file for convenience and archiving.  <br>Select the file to use for this analysis here.</html>");
		GridBagConstraints gbc_tf_ExcelFile = new GridBagConstraints();
		gbc_tf_ExcelFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_ExcelFile.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_ExcelFile.insets = new Insets(0, 0, 5, 5);
		gbc_tf_ExcelFile.gridwidth = 6;
		gbc_tf_ExcelFile.gridx = 3;
		gbc_tf_ExcelFile.gridy = 0;
		panel_1.add(tf_ExcelFile, gbc_tf_ExcelFile);

		JButton btnSelect = new JButton("Select");
		btnSelect.setToolTipText("<html>Provides a means of selecting the Excel file for analysis output.</html>");
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"Excel Files", "xls");
				chooser.setFileFilter(filter);
				File selectedFile = new File(PrefsNightScoutLoader.getInstance().getM_AnalysisFilePath());
				chooser.setSelectedFile(selectedFile);
				int returnVal = chooser.showOpenDialog(getContentPane());        	    		
				if(returnVal == JFileChooser.APPROVE_OPTION) 
				{
					tf_ExcelFile.setText(chooser.getSelectedFile().getAbsolutePath());
					m_Logger.log( Level.INFO, "You chose to open this file for SQL File Contents: " +
							chooser.getSelectedFile().getAbsolutePath());
				}				
			}
		});
		GridBagConstraints gbc_btnSelect = new GridBagConstraints();
		gbc_btnSelect.insets = new Insets(0, 0, 5, 5);
		gbc_btnSelect.gridx = 9;
		gbc_btnSelect.gridy = 0;
		panel_1.add(btnSelect, gbc_btnSelect);

		lblDaysBack = new JLabel("Daysback to Review");
		gbc_lblDaysBack = new GridBagConstraints();
		gbc_lblDaysBack.anchor = GridBagConstraints.EAST;
		gbc_lblDaysBack.insets = new Insets(0, 0, 5, 5);
		gbc_lblDaysBack.gridx = 1;
		gbc_lblDaysBack.gridy = 1;
		panel_1.add(lblDaysBack, gbc_lblDaysBack);

		sp_DaysBack = new JSpinner();
		sp_DaysBack.setToolTipText("<html>This is a convenience parameter to help set the start date for analysis.  <br>It counts back from the most recent result this many days.</html>");
		sp_DaysBack.setModel(new SpinnerNumberModel(14, 1, 1000, 1));
		sp_DaysBack.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerDaysBack());
		sp_DaysBack.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setDatesFromDaysBack();
			}
		});

		GridBagConstraints gbc_sp_DaysBack = new GridBagConstraints();
		gbc_sp_DaysBack.anchor = GridBagConstraints.WEST;
		gbc_sp_DaysBack.insets = new Insets(0, 0, 5, 5);
		gbc_sp_DaysBack.gridx = 3;
		gbc_sp_DaysBack.gridy = 1;
		panel_1.add(sp_DaysBack, gbc_sp_DaysBack);

		sp_breakfastStart = new JSpinner();
		sp_breakfastStart.setToolTipText("<html>Start time for breakfast period.  <br>Used for meal or correction trends that start in one period and could end in another.  <br>(Overnight trends use different periods.)</html>");
		sp_breakfastStart.setModel(getTimeListModel());
		sp_breakfastStart.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerBreakfastTimeStart());

		cb_ExcelOutputLevel = new JComboBox<String>();
		cb_ExcelOutputLevel.setToolTipText("<html>Determines how much detail is included in Excel report.<br>Full Detail includes extra tabs that show the progression of underlying<br>raw data into intermeidate forms that are then aggregated for trend reports" +
		"<br><br>It is easy to get lost in a sea of too much data here, <br>so keep at minimal level unless an advanced user with need to access the detail.</html>");

		cb_ExcelOutputLevel.setModel(new DefaultComboBoxModel<String>(new String[] 
				{"Minimal Detail Excel Summary",  "Moderate Detail Excel Summary", "Full Detail Excel Summary",}));
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 5;
		gbc_comboBox.gridy = 1;
		panel_1.add(cb_ExcelOutputLevel, gbc_comboBox);

		tf_BGUnits = new JTextField();
		tf_BGUnits.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
		tf_BGUnits.setBackground(Color.YELLOW);
		tf_BGUnits.setToolTipText("Go to Settings panel to change between mmol/L and mg/dL");
		tf_BGUnits.setHorizontalAlignment(SwingConstants.CENTER);
		tf_BGUnits.setEditable(false);
		GridBagConstraints gbc_tf_BGUnits = new GridBagConstraints();
		gbc_tf_BGUnits.insets = new Insets(0, 0, 5, 5);
		gbc_tf_BGUnits.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_BGUnits.gridx = 9;
		gbc_tf_BGUnits.gridy = 1;
		panel_1.add(tf_BGUnits, gbc_tf_BGUnits);
		tf_BGUnits.setColumns(10);

		lblStartDate = new JLabel("Start Date");
		GridBagConstraints gbc_lblStartDate = new GridBagConstraints();
		gbc_lblStartDate.anchor = GridBagConstraints.EAST;
		gbc_lblStartDate.insets = new Insets(0, 0, 5, 5);
		gbc_lblStartDate.gridx = 1;
		gbc_lblStartDate.gridy = 2;
		panel_1.add(lblStartDate, gbc_lblStartDate);

		sp_StartDate = new JSpinner();
		sp_StartDate.setToolTipText("<html>Start date for analysis.  <br>Results on this date and after and before the End Date are considered in the analysis.</html>");
		sp_StartDate.setModel(new SpinnerDateModel(new Date(1465513200000L), null, null, Calendar.HOUR));
		sp_StartDate.getModel().setValue(new Date(PrefsNightScoutLoader.getInstance().getM_AnalyzerStartDateLong()));

		sp_StartDate.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setDaysBackFromDates();
			}
		});

		jp_StartDate = new JDatePickerImpl(new JDatePanelImpl(new UtilDateModel()), new DateLabelFormatter());
		jp_StartDate.setToolTipText("<html>Start date for analysis.  <br>Results on this date and after and before the End Date are considered in the analysis.</html>");

		jp_EndDate = new JDatePickerImpl(new JDatePanelImpl(new UtilDateModel()), new DateLabelFormatter());
		jp_EndDate.setToolTipText("<html>End date for analysis.  <br>Results on the Start date and after and before this Date are considered in the analysis.</html>");

		jp_StartDate.setMinimumSize(new Dimension(115,30));
		jp_EndDate.setMinimumSize(new Dimension(115,30));

		// Initialise start & end dates to what was used last
		// End date will be changed to the most recent date in result set once known.
		((UtilDateModel)jp_StartDate.getModel()).setValue(new Date(PrefsNightScoutLoader.getInstance().getM_AnalyzerStartDateLong()));
		((UtilDateModel)jp_EndDate.getModel()).setValue(new Date(PrefsNightScoutLoader.getInstance().getM_AnalyzerEndDateLong()));

		// Put an action handler on end date to catch when values go later than most recent result
		jp_EndDate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				checkAndSetDates(jp_EndDate);

				/*
//				Date startDate = (Date)jp_StartDate.getModel().getValue();
				Date endDate   = (Date)jp_EndDate.getModel().getValue();
				if (endDate.after(m_MostRecentResultDate))
				{
//					String startDateStr = new String("");
					String endDateStr = new String("");
					try {
//						startDateStr = CommonUtils.convertDateString(startDate, "dd-MMM-yyyy");
						endDateStr = CommonUtils.convertDateString(endDate, "dd-MMM-yyyy");
					} catch (ParseException e) {
						m_Logger.log( Level.SEVERE, "Unexpected error converting dates to check end date");
					}

					// Add a warning
					warningMessage("Please note the following:\n\n" + "The selected date (" + 
					endDateStr + ") is later than the date of last result loaded (" 
					+ m_MostRecentResultDateStr + ")\n\n" +
					"Analysis can only therefore run on results between (" + m_OldestResultDateStr + ") and (" + m_MostRecentResultDateStr + ")");
				}
				setDaysBackFromDates();
				 */
			}
		});

		// Put an action handler on start date to catch when values go earlier than oldest result
		jp_StartDate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				checkAndSetDates(jp_StartDate);

				/*
//				Date startDate = (Date)jp_StartDate.getModel().getValue();
				Date endDate   = (Date)jp_EndDate.getModel().getValue();
				if (endDate.before(m_OldestResultDate))
				{
//					String startDateStr = new String("");
					String endDateStr = new String("");
					try {
//						startDateStr = CommonUtils.convertDateString(startDate, "dd-MMM-yyyy");
						endDateStr = CommonUtils.convertDateString(endDate, "dd-MMM-yyyy");
					} catch (ParseException e) {
						m_Logger.log( Level.SEVERE, "Unexpected error converting dates to check end date");
					}

					// Add a warning
					warningMessage("Please note the following:\n\n" + "The selected date (" + 
					endDateStr + ") is earlier than the date of the oldest result loaded (" 
					+ m_OldestResultDateStr + ")\n\n" +
					"Analysis can only therefore run on results between (" + m_OldestResultDateStr + ") and (" + m_MostRecentResultDateStr + ")");
				}
				setDaysBackFromDates();
				 */
			}
		});


		GridBagConstraints gbc_sp_StartDate = new GridBagConstraints();
		gbc_sp_StartDate.anchor = GridBagConstraints.WEST;
		gbc_sp_StartDate.insets = new Insets(0, 0, 5, 5);
		gbc_sp_StartDate.gridx = 3;
		gbc_sp_StartDate.gridy = 2;
		//		panel_1.add(sp_StartDate, gbc_sp_StartDate);
		panel_1.add(jp_StartDate, gbc_sp_StartDate);

		m_WinCGMRanges = new WinCGMRanges(this, "Nightscout Loader " + Version.getInstance().getM_Version() + " - Analyzer - CGM Dates");

		lblEndDate = new JLabel("End Date");
		GridBagConstraints gbc_lblEndDate = new GridBagConstraints();
		gbc_lblEndDate.anchor = GridBagConstraints.EAST;
		gbc_lblEndDate.insets = new Insets(0, 0, 5, 5);
		gbc_lblEndDate.gridx = 5;
		gbc_lblEndDate.gridy = 2;
		panel_1.add(lblEndDate, gbc_lblEndDate);

		sp_EndDate = new JSpinner();
		sp_EndDate.setToolTipText("<html>End date for analysis.  <br>Results on this date and before and on or after the Start Date are considered in the analysis.</html>");
		sp_EndDate.setModel(new SpinnerDateModel(new Date(1465513200000L), null, null, Calendar.DAY_OF_YEAR));
		sp_EndDate.getModel().setValue(new Date(PrefsNightScoutLoader.getInstance().getM_AnalyzerEndDateLong()));
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.anchor = GridBagConstraints.WEST;
		gbc_spinner.insets = new Insets(0, 0, 5, 5);
		gbc_spinner.gridx = 9;
		gbc_spinner.gridy = 2;
		//		panel_1.add(sp_EndDate, gbc_spinner);
		panel_1.add(jp_EndDate, gbc_spinner);

		JLabel lblBreakfastStart = new JLabel("Breakfast Start Time");
		GridBagConstraints gbc_lblBreakfastStart = new GridBagConstraints();
		gbc_lblBreakfastStart.anchor = GridBagConstraints.EAST;
		gbc_lblBreakfastStart.insets = new Insets(0, 0, 5, 5);
		gbc_lblBreakfastStart.gridx = 1;
		gbc_lblBreakfastStart.gridy = 3;
		panel_1.add(lblBreakfastStart, gbc_lblBreakfastStart);
		GridBagConstraints gbc_sp_breakfastStart = new GridBagConstraints();
		gbc_sp_breakfastStart.anchor = GridBagConstraints.WEST;
		gbc_sp_breakfastStart.insets = new Insets(0, 0, 5, 5);
		gbc_sp_breakfastStart.gridx = 3;
		gbc_sp_breakfastStart.gridy = 3;
		panel_1.add(sp_breakfastStart, gbc_sp_breakfastStart);

		sp_dinnerStart = new JSpinner();
		sp_dinnerStart.setToolTipText("<html>Start time for dinner period.  <br>Used for meal or correction trends that start in one period and could end in another.  <br>(Overnight trends use different periods.)</html>");
		sp_dinnerStart.setModel(getTimeListModel());
		sp_dinnerStart.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerDinnerTimeStart());

		JLabel lblLunchStart = new JLabel("Lunch Start Time");
		GridBagConstraints gbc_lblLunchStart = new GridBagConstraints();
		gbc_lblLunchStart.anchor = GridBagConstraints.EAST;
		gbc_lblLunchStart.insets = new Insets(0, 0, 5, 5);
		gbc_lblLunchStart.gridx = 5;
		gbc_lblLunchStart.gridy = 3;
		panel_1.add(lblLunchStart, gbc_lblLunchStart);

		sp_lunchStart = new JSpinner();
		sp_lunchStart.setToolTipText("<html>Start time for lunch period.  <br>Used for meal or correction trends that start in one period and could end in another.  <br>(Overnight trends use different periods.)</html>");
		sp_lunchStart.setModel(getTimeListModel());
		sp_lunchStart.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerLunchTimeStart());
		GridBagConstraints gbc_sp_lunchStart = new GridBagConstraints();
		gbc_sp_lunchStart.anchor = GridBagConstraints.WEST;
		gbc_sp_lunchStart.insets = new Insets(0, 0, 5, 5);
		gbc_sp_lunchStart.gridx = 9;
		gbc_sp_lunchStart.gridy = 3;
		panel_1.add(sp_lunchStart, gbc_sp_lunchStart);
		//	tf_DBName.setColumns(25);

		JLabel lblDinnerStart = new JLabel("Dinner Start Time");
		GridBagConstraints gbc_lblDinnerStart = new GridBagConstraints();
		gbc_lblDinnerStart.anchor = GridBagConstraints.EAST;
		gbc_lblDinnerStart.insets = new Insets(0, 0, 5, 5);
		gbc_lblDinnerStart.gridx = 1;
		gbc_lblDinnerStart.gridy = 4;
		panel_1.add(lblDinnerStart, gbc_lblDinnerStart);
		lblDinnerStart.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_sp_dinnerStart = new GridBagConstraints();
		gbc_sp_dinnerStart.anchor = GridBagConstraints.WEST;
		gbc_sp_dinnerStart.insets = new Insets(0, 0, 5, 5);
		gbc_sp_dinnerStart.gridx = 3;
		gbc_sp_dinnerStart.gridy = 4;
		panel_1.add(sp_dinnerStart, gbc_sp_dinnerStart);

		JLabel lblBedStart = new JLabel("Bed Start Time");
		GridBagConstraints gbc_lblBedStart = new GridBagConstraints();
		gbc_lblBedStart.anchor = GridBagConstraints.EAST;
		gbc_lblBedStart.insets = new Insets(0, 0, 5, 5);
		//gbc_lblSqlFile.gridwidth = 2;
		gbc_lblBedStart.gridx = 5;
		gbc_lblBedStart.gridy = 4;
		panel_1.add(lblBedStart, gbc_lblBedStart);

		sp_bedStart = new JSpinner();
		sp_bedStart.setToolTipText("<html>Start time for bed period.  <br>Used for meal or correction trends that start in one period and could end in another.  <br>(Overnight trends use different periods.)</html>");
		sp_bedStart.setModel(getTimeListModel());
		sp_bedStart.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTimeStart());
		GridBagConstraints gbc_sp_bedStart = new GridBagConstraints();
		gbc_sp_bedStart.anchor = GridBagConstraints.WEST;
		gbc_sp_bedStart.insets = new Insets(0, 0, 5, 5);
		gbc_sp_bedStart.gridx = 9;
		gbc_sp_bedStart.gridy = 4;
		panel_1.add(sp_bedStart, gbc_sp_bedStart);

		lblHighThreshold = new JLabel("BG High Threshold");
		GridBagConstraints gbc_lblHighThreshold = new GridBagConstraints();
		gbc_lblHighThreshold.anchor = GridBagConstraints.EAST;
		gbc_lblHighThreshold.insets = new Insets(0, 0, 5, 5);
		gbc_lblHighThreshold.gridx = 1;
		gbc_lblHighThreshold.gridy = 5;
		panel_1.add(lblHighThreshold, gbc_lblHighThreshold);

		tf_HighThreshold = new JTextField(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerHighThreshold()));
		tf_HighThreshold.setToolTipText("<html>Hyper threshold.  <br>BG Values above this are considered HYPER.</html>");
		tf_HighThreshold.setHorizontalAlignment(SwingConstants.LEFT);
		tf_HighThreshold.setColumns(5);
		GridBagConstraints gbc_tf_HighThreshold = new GridBagConstraints();
		gbc_tf_HighThreshold.gridwidth = 2;
		gbc_tf_HighThreshold.insets = new Insets(0, 0, 5, 5);
		gbc_tf_HighThreshold.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_HighThreshold.gridx = 3;
		gbc_tf_HighThreshold.gridy = 5;
		panel_1.add(tf_HighThreshold, gbc_tf_HighThreshold);

		lblHighThresholdRelevance = new JLabel("BG High Thresh Relevance Factor");
		GridBagConstraints gbc_lblHighThresholdRelevance = new GridBagConstraints();
		gbc_lblHighThresholdRelevance.anchor = GridBagConstraints.EAST;
		gbc_lblHighThresholdRelevance.insets = new Insets(0, 0, 5, 5);
		gbc_lblHighThresholdRelevance.gridx = 5;
		gbc_lblHighThresholdRelevance.gridy = 5;
		panel_1.add(lblHighThresholdRelevance, gbc_lblHighThresholdRelevance);

		tf_HighThresholdRelevanceFactor = new JTextField(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerHighThresholdRelevanceFactor()));
		tf_HighThresholdRelevanceFactor.setToolTipText("<html>BG values at this value and above are considered high and scored from 1 to 10 for severity purposes.  <br>1 would be this lower level, and increase as the BG levels go higher.</html>");
		tf_HighThresholdRelevanceFactor.setHorizontalAlignment(SwingConstants.LEFT);
		tf_HighThresholdRelevanceFactor.setColumns(5);
		GridBagConstraints gbc_tfHighThresholdRelevanceFactor = new GridBagConstraints();
		gbc_tfHighThresholdRelevanceFactor.insets = new Insets(0, 0, 5, 5);
		gbc_tfHighThresholdRelevanceFactor.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfHighThresholdRelevanceFactor.gridx = 9;
		gbc_tfHighThresholdRelevanceFactor.gridy = 5;
		panel_1.add(tf_HighThresholdRelevanceFactor, gbc_tfHighThresholdRelevanceFactor);

		JLabel lblLowThreshold = new JLabel("BG Low Threshold");
		GridBagConstraints gbc_lblLowThreshold = new GridBagConstraints();
		gbc_lblLowThreshold.anchor = GridBagConstraints.EAST;
		gbc_lblLowThreshold.insets = new Insets(0, 0, 5, 5);
		//gbc_label.gridwidth = 3;
		gbc_lblLowThreshold.gridx = 1;
		gbc_lblLowThreshold.gridy = 6;
		panel_1.add(lblLowThreshold, gbc_lblLowThreshold);

		tf_LowThreshold = new JTextField(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerLowThreshold()));
		tf_LowThreshold.setToolTipText("<html>Hyper threshold.  <br>BG Values below this are considered HYPO.</html>");
		GridBagConstraints gbc_tf_LowThreshold = new GridBagConstraints();
		gbc_tf_LowThreshold.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_LowThreshold.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_LowThreshold.insets = new Insets(0, 0, 5, 5);
		gbc_tf_LowThreshold.gridwidth = 2;
		gbc_tf_LowThreshold.gridx = 3;
		gbc_tf_LowThreshold.gridy = 6;
		panel_1.add(tf_LowThreshold, gbc_tf_LowThreshold);
		tf_LowThreshold.setColumns(10);

		lblLowThresholdRelevanceFactor = new JLabel("BG Low Thresh Relevance Factor");
		GridBagConstraints gbc_lblLowThresholdRelevanceFactor = new GridBagConstraints();
		gbc_lblLowThresholdRelevanceFactor.insets = new Insets(0, 0, 5, 5);
		gbc_lblLowThresholdRelevanceFactor.gridx = 5;
		gbc_lblLowThresholdRelevanceFactor.gridy = 6;
		panel_1.add(lblLowThresholdRelevanceFactor, gbc_lblLowThresholdRelevanceFactor);

		tf_LowThresholdRelevanceFactor = new JTextField(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerLowThresholdRelevanceFactor()));
		tf_LowThresholdRelevanceFactor.setToolTipText("<html>BG values at this value and below are considered low and scored from 1 to 10 for severity purposes.  <br>1 would be this higherrlevel, and increase as the BG levels go lower.</html>");
		tf_LowThresholdRelevanceFactor.setHorizontalAlignment(SwingConstants.LEFT);
		tf_LowThresholdRelevanceFactor.setColumns(5);
		GridBagConstraints gbc_tfLowThresholdRelevanceFactor = new GridBagConstraints();
		gbc_tfLowThresholdRelevanceFactor.insets = new Insets(0, 0, 5, 5);
		gbc_tfLowThresholdRelevanceFactor.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfLowThresholdRelevanceFactor.gridx = 9;
		gbc_tfLowThresholdRelevanceFactor.gridy = 6;
		panel_1.add(tf_LowThresholdRelevanceFactor, gbc_tfLowThresholdRelevanceFactor);

		lblOvernightTrendRatio = new JLabel("Overnight Trend Ratio");
		GridBagConstraints gbc_lblOvernightTrendRatio = new GridBagConstraints();
		gbc_lblOvernightTrendRatio.anchor = GridBagConstraints.EAST;
		gbc_lblOvernightTrendRatio.insets = new Insets(0, 0, 5, 5);
		gbc_lblOvernightTrendRatio.gridx = 1;
		gbc_lblOvernightTrendRatio.gridy = 7;
		panel_1.add(lblOvernightTrendRatio, gbc_lblOvernightTrendRatio);

		tf_OvernightTrendRatio = new JTextField(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerOvernightChangeTrendRatio()));
		tf_OvernightTrendRatio.setToolTipText("<html>The severity of a trend is calculated by dividing the difference by the start BG.  <br>If the fraction is this ratio or higher, then it's considered severity 10.  <br>The analyzer calculates severity proportonally right down to 1.  <br>The ratio can be set by meal time.</html>");
		tf_OvernightTrendRatio.setColumns(10);
		GridBagConstraints gbc_tf_OvernightTrendRatio = new GridBagConstraints();
		gbc_tf_OvernightTrendRatio.gridwidth = 2;
		gbc_tf_OvernightTrendRatio.insets = new Insets(0, 0, 5, 5);
		gbc_tf_OvernightTrendRatio.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_OvernightTrendRatio.gridx = 3;
		gbc_tf_OvernightTrendRatio.gridy = 7;
		panel_1.add(tf_OvernightTrendRatio, gbc_tf_OvernightTrendRatio);

		lblBreakfastTrendRatio = new JLabel("Breakfast Trend Ratio");
		GridBagConstraints gbc_lblBreakfastTrendRatio = new GridBagConstraints();
		gbc_lblBreakfastTrendRatio.anchor = GridBagConstraints.EAST;
		gbc_lblBreakfastTrendRatio.insets = new Insets(0, 0, 5, 5);
		gbc_lblBreakfastTrendRatio.gridx = 5;
		gbc_lblBreakfastTrendRatio.gridy = 7;
		panel_1.add(lblBreakfastTrendRatio, gbc_lblBreakfastTrendRatio);

		tf_BreakfastTrendRatio = new JTextField(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerBreakfastChangeTrendRatio()));
		tf_BreakfastTrendRatio.setToolTipText("<html>The severity of a trend is calculated by dividing the difference by the start BG.  <br>If the fraction is this ratio or higher, then it's considered severity 10.  <br>The analyzer calculates severity proportonally right down to 1.  <br>The ratio can be set by meal time.</html>");
		tf_BreakfastTrendRatio.setColumns(10);
		GridBagConstraints gbc_tf_BreakfastTrendRatio = new GridBagConstraints();
		gbc_tf_BreakfastTrendRatio.insets = new Insets(0, 0, 5, 5);
		gbc_tf_BreakfastTrendRatio.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_BreakfastTrendRatio.gridx = 9;
		gbc_tf_BreakfastTrendRatio.gridy = 7;
		panel_1.add(tf_BreakfastTrendRatio, gbc_tf_BreakfastTrendRatio);

		JLabel lblMinMinsForTrend = new JLabel("Minimum Minutes for Trend");
		GridBagConstraints gbc_lblMinMinsForTrend = new GridBagConstraints();
		gbc_lblMinMinsForTrend.anchor = GridBagConstraints.EAST;
		gbc_lblMinMinsForTrend.insets = new Insets(0, 0, 5, 5);
		//gbc_label_2.gridwidth = 4;
		gbc_lblMinMinsForTrend.gridx = 1;
		gbc_lblMinMinsForTrend.gridy = 8;
		panel_1.add(lblMinMinsForTrend, gbc_lblMinMinsForTrend);

		tf_MinMinsForTrend = new JTextField(String.format("%d", PrefsNightScoutLoader.getInstance().getM_AnalyzerMinMinsForTrendResults()));
		tf_MinMinsForTrend.setToolTipText("<html>A trend BG requires two results at least this many minutes apart.</html>");
		GridBagConstraints gbc_tf_MinMinsForTrend = new GridBagConstraints();
		gbc_tf_MinMinsForTrend.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_MinMinsForTrend.anchor = GridBagConstraints.EAST;
		gbc_tf_MinMinsForTrend.insets = new Insets(0, 0, 5, 5);
		gbc_tf_MinMinsForTrend.gridwidth = 2;
		gbc_tf_MinMinsForTrend.gridx = 3;
		gbc_tf_MinMinsForTrend.gridy = 8;
		panel_1.add(tf_MinMinsForTrend, gbc_tf_MinMinsForTrend);
		tf_MinMinsForTrend.setColumns(25);


		JLabel lblLunchTrendRatio = new JLabel("Lunch Trend Ratio");
		GridBagConstraints gbc_lblLunchTrendRatio = new GridBagConstraints();
		gbc_lblLunchTrendRatio.anchor = GridBagConstraints.EAST;
		gbc_lblLunchTrendRatio.insets = new Insets(0, 0, 5, 5);
		//gbc_label_1.gridwidth = 3;
		gbc_lblLunchTrendRatio.gridx = 5;
		gbc_lblLunchTrendRatio.gridy = 8;
		panel_1.add(lblLunchTrendRatio, gbc_lblLunchTrendRatio);

		tf_LunchTrendRatio = new JTextField(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerLunchChangeTrendRatio()));
		tf_LunchTrendRatio.setToolTipText("<html>The severity of a trend is calculated by dividing the difference by the start BG.  <br>If the fraction is this ratio or higher, then it's considered severity 10.  <br>The analyzer calculates severity proportonally right down to 1.  <br>The ratio can be set by meal time.</html>");
		GridBagConstraints gbc_tf_LunchTrendRatio = new GridBagConstraints();
		gbc_tf_LunchTrendRatio.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_LunchTrendRatio.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_LunchTrendRatio.insets = new Insets(0, 0, 5, 0);
		gbc_tf_LunchTrendRatio.gridwidth = 2;
		gbc_tf_LunchTrendRatio.gridx = 9;
		gbc_tf_LunchTrendRatio.gridy = 8;
		panel_1.add(tf_LunchTrendRatio, gbc_tf_LunchTrendRatio);
		tf_LunchTrendRatio.setColumns(10);

		lblDinnerTrendRatio = new JLabel("Dinner Trend Ratio");
		GridBagConstraints gbc_lblDinnerTrendRatio = new GridBagConstraints();
		gbc_lblDinnerTrendRatio.anchor = GridBagConstraints.EAST;
		gbc_lblDinnerTrendRatio.insets = new Insets(0, 0, 5, 5);
		gbc_lblDinnerTrendRatio.gridx = 5;
		gbc_lblDinnerTrendRatio.gridy = 9;
		panel_1.add(lblDinnerTrendRatio, gbc_lblDinnerTrendRatio);

		tf_DinnerTrendRatio = new JTextField(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerDinnerChangeTrendRatio()));
		tf_DinnerTrendRatio.setToolTipText("<html>The severity of a trend is calculated by dividing the difference by the start BG.  <br>If the fraction is this ratio or higher, then it's considered severity 10.  <br>The analyzer calculates severity proportonally right down to 1.  <br>The ratio can be set by meal time.</html>");
		tf_DinnerTrendRatio.setColumns(10);
		GridBagConstraints gbc_tf_DinnerTrendRatio = new GridBagConstraints();
		gbc_tf_DinnerTrendRatio.insets = new Insets(0, 0, 5, 5);
		gbc_tf_DinnerTrendRatio.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_DinnerTrendRatio.gridx = 9;
		gbc_tf_DinnerTrendRatio.gridy = 9;
		panel_1.add(tf_DinnerTrendRatio, gbc_tf_DinnerTrendRatio);

		rdbtnIncludeBreakfast = new JRadioButton("Include Breakfast");
		rdbtnIncludeBreakfast.setToolTipText("<html>Control that determines whether Breakfast meals are considered for Trend Analysis</html>");
		rdbtnIncludeBreakfast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				checkIncludeOptions(rdbtnIncludeBreakfast);
			}
		});
		rdbtnIncludeBreakfast.setSelected(PrefsNightScoutLoader.getInstance().isM_AnalyzerIncludeBreakfast());
		GridBagConstraints gbc_rdbtnIncludeBreakfast = new GridBagConstraints();
		gbc_rdbtnIncludeBreakfast.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnIncludeBreakfast.gridx = 1;
		gbc_rdbtnIncludeBreakfast.gridy = 10;
		panel_1.add(rdbtnIncludeBreakfast, gbc_rdbtnIncludeBreakfast);

		rdbtnIncludeLunch = new JRadioButton("Include Lunch");
		rdbtnIncludeLunch.setToolTipText("<html>Control that determines whether Lunch meals are considered for Trend Analysis</html>");
		rdbtnIncludeLunch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				checkIncludeOptions(rdbtnIncludeLunch);
			}
		});
		rdbtnIncludeLunch.setSelected(PrefsNightScoutLoader.getInstance().isM_AnalyzerIncludeLunch());
		GridBagConstraints gbc_rdbtnIncludeLunch = new GridBagConstraints();
		gbc_rdbtnIncludeLunch.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnIncludeLunch.gridx = 3;
		gbc_rdbtnIncludeLunch.gridy = 10;
		panel_1.add(rdbtnIncludeLunch, gbc_rdbtnIncludeLunch);

		rdbtnIncludeDinner = new JRadioButton("Include Dinner");
		rdbtnIncludeDinner.setToolTipText("<html>Control that determines whether Dinner meals are considered for Trend Analysis</html>");
		rdbtnIncludeDinner.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				checkIncludeOptions(rdbtnIncludeDinner);
			}
		});
		rdbtnIncludeDinner.setSelected(PrefsNightScoutLoader.getInstance().isM_AnalyzerIncludeDinner());
		GridBagConstraints gbc_rdbtnIncludeDinner = new GridBagConstraints();
		gbc_rdbtnIncludeDinner.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnIncludeDinner.gridx = 5;
		gbc_rdbtnIncludeDinner.gridy = 10;
		panel_1.add(rdbtnIncludeDinner, gbc_rdbtnIncludeDinner);

		rdbtnIncludeOvernight = new JRadioButton("Include Overnight");
		rdbtnIncludeOvernight.setToolTipText("<html>Control that determines whether Overnight events are considered for Trend Analysis</html>");
		rdbtnIncludeOvernight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				checkIncludeOptions(rdbtnIncludeOvernight);
			}
		});
		rdbtnIncludeOvernight.setSelected(PrefsNightScoutLoader.getInstance().isM_AnalyzerIncludeOvernight());
		GridBagConstraints gbc_rdbtnIncludeOvernight = new GridBagConstraints();
		gbc_rdbtnIncludeOvernight.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnIncludeOvernight.gridx = 9;
		gbc_rdbtnIncludeOvernight.gridy = 10;
		panel_1.add(rdbtnIncludeOvernight, gbc_rdbtnIncludeOvernight);

		lblBedTrendStartStartTime = new JLabel("Bedtime Trend Start Start Time");
		GridBagConstraints gbc_lblBedTrendStartStartTime = new GridBagConstraints();
		gbc_lblBedTrendStartStartTime.anchor = GridBagConstraints.EAST;
		gbc_lblBedTrendStartStartTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblBedTrendStartStartTime.gridx = 1;
		gbc_lblBedTrendStartStartTime.gridy = 11;
		panel_1.add(lblBedTrendStartStartTime, gbc_lblBedTrendStartStartTime);

		sp_bedTrendStartStart = new JSpinner();
		sp_bedTrendStartStart.setToolTipText("<html>The anlayzer looks for overnight trends that start with in a start/end start time and end within a start/end end time.  <br>This paramter sets the start of the start time range.</html>");
		sp_bedTrendStartStart.setModel(getTimeListModel());
		sp_bedTrendStartStart.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendStartStartTime());
		GridBagConstraints gbc_sp_bedTrendStartStart = new GridBagConstraints();
		gbc_sp_bedTrendStartStart.anchor = GridBagConstraints.WEST;
		gbc_sp_bedTrendStartStart.insets = new Insets(0, 0, 5, 5);
		gbc_sp_bedTrendStartStart.gridx = 3;
		gbc_sp_bedTrendStartStart.gridy = 11;
		panel_1.add(sp_bedTrendStartStart, gbc_sp_bedTrendStartStart);

		lblBedTrendEndStartTime = new JLabel("Bedtime Trend End Start Time");
		GridBagConstraints gbc_lblBedTrendEndStartTime = new GridBagConstraints();
		gbc_lblBedTrendEndStartTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblBedTrendEndStartTime.gridx = 5;
		gbc_lblBedTrendEndStartTime.gridy = 11;
		panel_1.add(lblBedTrendEndStartTime, gbc_lblBedTrendEndStartTime);

		sp_bedTrendEndStart = new JSpinner();
		sp_bedTrendEndStart.setToolTipText("<html>The anlayzer looks for overnight trends that start with in a start/end start time and end within a start/end end time.  <br>This paramter sets the start of the end time range.</html>");
		sp_bedTrendEndStart.setModel(getTimeListModel());
		sp_bedTrendEndStart.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendEndStartTime());
		GridBagConstraints gbc_sp_bedTrendEndStart = new GridBagConstraints();
		gbc_sp_bedTrendEndStart.anchor = GridBagConstraints.WEST;
		gbc_sp_bedTrendEndStart.insets = new Insets(0, 0, 5, 5);
		gbc_sp_bedTrendEndStart.gridx = 9;
		gbc_sp_bedTrendEndStart.gridy = 11;
		panel_1.add(sp_bedTrendEndStart, gbc_sp_bedTrendEndStart);

		lblBedTrendStartEndTime = new JLabel("Bedtime Trend Start End Time");
		GridBagConstraints gbc_lblBedTrendStartEndTime = new GridBagConstraints();
		gbc_lblBedTrendStartEndTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblBedTrendStartEndTime.gridx = 1;
		gbc_lblBedTrendStartEndTime.gridy = 12;
		panel_1.add(lblBedTrendStartEndTime, gbc_lblBedTrendStartEndTime);

		sp_bedTrendStartEnd = new JSpinner();
		sp_bedTrendStartEnd.setToolTipText("<html>The anlayzer looks for overnight trends that start with in a start/end start time and end within a start/end end time.  <br>This paramter sets the end of the start time range.</html>");
		sp_bedTrendStartEnd.setModel(getTimeListModel());
		sp_bedTrendStartEnd.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendStartEndTime());
		GridBagConstraints gbc_sp_bedTrendStartEnd = new GridBagConstraints();
		gbc_sp_bedTrendStartEnd.anchor = GridBagConstraints.WEST;
		gbc_sp_bedTrendStartEnd.insets = new Insets(0, 0, 5, 5);
		gbc_sp_bedTrendStartEnd.gridx = 3;
		gbc_sp_bedTrendStartEnd.gridy = 12;
		panel_1.add(sp_bedTrendStartEnd, gbc_sp_bedTrendStartEnd);

		lblBedTrendEndEndTime = new JLabel("Bedtime Trend End End Time");
		GridBagConstraints gbc_lblBedTrendEndEndTime = new GridBagConstraints();
		gbc_lblBedTrendEndEndTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblBedTrendEndEndTime.gridx = 5;
		gbc_lblBedTrendEndEndTime.gridy = 12;
		panel_1.add(lblBedTrendEndEndTime, gbc_lblBedTrendEndEndTime);

		sp_bedTrendEndEnd = new JSpinner();
		sp_bedTrendEndEnd.setToolTipText("<html>The anlayzer looks for overnight trends that start with in a start/end start time and end within a start/end end time.  <br>This paramter sets the end of the end time range.</html>");
		sp_bedTrendEndEnd.setModel(getTimeListModel());
		sp_bedTrendEndEnd.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendEndEndTime());
		GridBagConstraints gbc_sp_bedTrendEndEnd = new GridBagConstraints();
		gbc_sp_bedTrendEndEnd.anchor = GridBagConstraints.WEST;
		gbc_sp_bedTrendEndEnd.insets = new Insets(0, 0, 5, 5);
		gbc_sp_bedTrendEndEnd.gridx = 9;
		gbc_sp_bedTrendEndEnd.gridy = 12;
		panel_1.add(sp_bedTrendEndEnd, gbc_sp_bedTrendEndEnd);


		lblHighPriorityRecurrRatio = new JLabel("High Priority Recurring %");
		GridBagConstraints gbc_lblHighPriorityRecurrRatio = new GridBagConstraints();
		gbc_lblHighPriorityRecurrRatio.anchor = GridBagConstraints.EAST;
		gbc_lblHighPriorityRecurrRatio.insets = new Insets(0, 0, 5, 5);
		//	gbc_label_3.gridwidth = 5;
		gbc_lblHighPriorityRecurrRatio.gridx = 1;
		gbc_lblHighPriorityRecurrRatio.gridy = 13;
		panel_1.add(lblHighPriorityRecurrRatio, gbc_lblHighPriorityRecurrRatio);

		tf_HighPriorityRecurrRatio = new JSpinner();
		tf_HighPriorityRecurrRatio.setModel(new SpinnerNumberModel(10.0, 1.0, 100.0, 1.0));
		tf_HighPriorityRecurrRatio.setToolTipText("<html>Recurring trends happening more often than this ratio of times during analytical period are flagged as red <br>since the trend is very strong and needs most attention.</html>");
		GridBagConstraints gbc_tf_HighPriorityRecurrRatio = new GridBagConstraints();
		gbc_tf_HighPriorityRecurrRatio.anchor = GridBagConstraints.WEST;
		gbc_tf_HighPriorityRecurrRatio.insets = new Insets(0, 0, 5, 5);
		gbc_tf_HighPriorityRecurrRatio.gridx = 3;
		gbc_tf_HighPriorityRecurrRatio.gridy = 13;
		panel_1.add(tf_HighPriorityRecurrRatio, gbc_tf_HighPriorityRecurrRatio);
		tf_HighPriorityRecurrRatio.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerHighFrequencyPercentage());

		lblMediumPriorityRecurrRatio = new JLabel("Medium Priority Recurring %");
		GridBagConstraints gbc_lblMediumPriorityRecurrRatio = new GridBagConstraints();
		gbc_lblMediumPriorityRecurrRatio.anchor = GridBagConstraints.EAST;
		gbc_lblMediumPriorityRecurrRatio.insets = new Insets(0, 0, 5, 5);
		//	gbc_label_4.gridwidth = 4;
		gbc_lblMediumPriorityRecurrRatio.gridx = 5;
		gbc_lblMediumPriorityRecurrRatio.gridy = 13;
		panel_1.add(lblMediumPriorityRecurrRatio, gbc_lblMediumPriorityRecurrRatio);

		tf_MediumPriorityRecurrRatio = new JSpinner();
		tf_MediumPriorityRecurrRatio.setModel(new SpinnerNumberModel(5.0, 1.0, 100.0, 1.0));
		tf_MediumPriorityRecurrRatio.setToolTipText("<html>Recurring trends happening more often than this ratio of times <br>(and less often than high priority ratio) <br>during analytical period are flagged as amber since the trend is strong and needs attention.</html>");
		GridBagConstraints gbc_tf_MeterMongoServer = new GridBagConstraints();
		gbc_tf_MeterMongoServer.anchor = GridBagConstraints.WEST;
		gbc_tf_MeterMongoServer.insets = new Insets(0, 0, 5, 0);
		gbc_tf_MeterMongoServer.gridwidth = 2;
		gbc_tf_MeterMongoServer.gridx = 9;
		gbc_tf_MeterMongoServer.gridy = 13;
		panel_1.add(tf_MediumPriorityRecurrRatio, gbc_tf_MeterMongoServer);
		tf_MediumPriorityRecurrRatio.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerMediumFrequencyPercentage());

		lblLowRangeThreshold = new JLabel("BG Low Range Threshold");
		GridBagConstraints gbc_lblLowRangeThreshold = new GridBagConstraints();
		gbc_lblLowRangeThreshold.anchor = GridBagConstraints.EAST;
		gbc_lblLowRangeThreshold.insets = new Insets(0, 0, 5, 5);
		//	gbc_label_5.gridwidth = 6;
		gbc_lblLowRangeThreshold.gridx = 1;
		gbc_lblLowRangeThreshold.gridy = 14;
		panel_1.add(lblLowRangeThreshold, gbc_lblLowRangeThreshold);

		tf_LowRangeThreshold = new JTextField(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerLowRangeThreshold()));
		tf_LowRangeThreshold.setToolTipText("BG low level value for considering BGs in range.");
		GridBagConstraints gbc_tf_LowRangeThreshold = new GridBagConstraints();
		gbc_tf_LowRangeThreshold.insets = new Insets(0, 0, 5, 5);
		gbc_tf_LowRangeThreshold.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_LowRangeThreshold.anchor = GridBagConstraints.EAST;
		gbc_tf_LowRangeThreshold.gridwidth = 2;
		gbc_tf_LowRangeThreshold.gridx = 3;
		gbc_tf_LowRangeThreshold.gridy = 14;
		panel_1.add(tf_LowRangeThreshold, gbc_tf_LowRangeThreshold);
		tf_LowRangeThreshold.setColumns(25);

		lblHighRangeThreshold = new JLabel("BG High Range Threshold");
		GridBagConstraints gbc_lblHighRangeThreshold = new GridBagConstraints();
		gbc_lblHighRangeThreshold.anchor = GridBagConstraints.EAST;
		gbc_lblHighRangeThreshold.insets = new Insets(0, 0, 5, 5);
		//	gbc_label_6.gridwidth = 6;
		gbc_lblHighRangeThreshold.gridx = 5;
		gbc_lblHighRangeThreshold.gridy = 14;
		panel_1.add(lblHighRangeThreshold, gbc_lblHighRangeThreshold);

		tf_HighRangeThreshold = new JTextField(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerHighRangeThreshold()));
		tf_HighRangeThreshold.setToolTipText("BG high level value for considering BGs in range.");
		GridBagConstraints gbc_tf_HighRangeThreshold = new GridBagConstraints();
		gbc_tf_HighRangeThreshold.insets = new Insets(0, 0, 5, 0);
		gbc_tf_HighRangeThreshold.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_HighRangeThreshold.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_HighRangeThreshold.gridwidth = 2;
		gbc_tf_HighRangeThreshold.gridx = 9;
		gbc_tf_HighRangeThreshold.gridy = 14;
		panel_1.add(tf_HighRangeThreshold, gbc_tf_HighRangeThreshold);
		tf_HighRangeThreshold.setPreferredSize(new Dimension(7, 20));
		tf_HighRangeThreshold.setColumns(25);

		lblBadNightStartTime = new JLabel("Bad Night Start Time");
		GridBagConstraints gbc_lblBadNightStartTime = new GridBagConstraints();
		gbc_lblBadNightStartTime.anchor = GridBagConstraints.EAST;
		gbc_lblBadNightStartTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblBadNightStartTime.gridx = 1;
		gbc_lblBadNightStartTime.gridy = 15;
		panel_1.add(lblBadNightStartTime, gbc_lblBadNightStartTime);

		sp_BadnightStart = new JSpinner();
		sp_BadnightStart.setToolTipText("<html>The analyzer locates events (either BG, Insulin or Carbs) at bad times of night.  <br>This parameter determines the start time of a 'bad night'</html>");
		sp_BadnightStart.setModel(getTimeListModel());
		sp_BadnightStart.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerBadNightStartTime());
		GridBagConstraints gbc_sp_BadnightStart = new GridBagConstraints();
		gbc_sp_BadnightStart.anchor = GridBagConstraints.WEST;
		gbc_sp_BadnightStart.insets = new Insets(0, 0, 5, 5);
		gbc_sp_BadnightStart.gridx = 3;
		gbc_sp_BadnightStart.gridy = 15;
		panel_1.add(sp_BadnightStart, gbc_sp_BadnightStart);

		lblBadNightEndTime = new JLabel("Bad Night End Time");
		GridBagConstraints gbc_lblBadNightEndTime = new GridBagConstraints();
		gbc_lblBadNightEndTime.anchor = GridBagConstraints.EAST;
		gbc_lblBadNightEndTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblBadNightEndTime.gridx = 5;
		gbc_lblBadNightEndTime.gridy = 15;
		panel_1.add(lblBadNightEndTime, gbc_lblBadNightEndTime);

		sp_BadnightEnd = new JSpinner();
		sp_BadnightEnd.setToolTipText("<html>The analyzer locates events (either BG, Insulin or Carbs) at bad times of night.  <br>This parameter determines the end time of a 'bad night'</html>");
		sp_BadnightEnd.setModel(getTimeListModel());
		sp_BadnightEnd.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerBadNightEndTime());
		GridBagConstraints gbc_sp_BadnightEnd = new GridBagConstraints();
		gbc_sp_BadnightEnd.anchor = GridBagConstraints.WEST;
		gbc_sp_BadnightEnd.insets = new Insets(0, 0, 5, 5);
		gbc_sp_BadnightEnd.gridx = 9;
		gbc_sp_BadnightEnd.gridy = 15;
		panel_1.add(sp_BadnightEnd, gbc_sp_BadnightEnd);
		
		lblCgmTrendHour = new JLabel("CGM Trend Hour Intervals");
		lblCgmTrendHour.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblCgmTrendHour = new GridBagConstraints();
		gbc_lblCgmTrendHour.anchor = GridBagConstraints.EAST;
		gbc_lblCgmTrendHour.insets = new Insets(0, 0, 0, 5);
		gbc_lblCgmTrendHour.gridx = 1;
		gbc_lblCgmTrendHour.gridy = 16;
		panel_1.add(lblCgmTrendHour, gbc_lblCgmTrendHour);

		
		
		sp_CGMTrendIntervalDuration = new JSpinner();
		sp_CGMTrendIntervalDuration.setFont(new Font("Tahoma", Font.BOLD, 11));
		sp_CGMTrendIntervalDuration.setToolTipText("<html>This parameter sets the number of hours to group CGM entries in for trend analysis.  <br>Groupings can either be 1 hour, 2 hours or 3 hours </hmtl>");
		sp_CGMTrendIntervalDuration.setModel(new SpinnerNumberModel(1, 1, 3, 1));
		sp_CGMTrendIntervalDuration.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_EntryAnalyzerIntervalHours());

		GridBagConstraints gbc_spCGMTrendIntervalDuration = new GridBagConstraints();
		gbc_spCGMTrendIntervalDuration.anchor = GridBagConstraints.WEST;
		gbc_spCGMTrendIntervalDuration.insets = new Insets(0, 0, 0, 5);
		gbc_spCGMTrendIntervalDuration.gridx = 3;
		gbc_spCGMTrendIntervalDuration.gridy = 16;
		panel_1.add(sp_CGMTrendIntervalDuration, gbc_spCGMTrendIntervalDuration);


		
		rdbtnCompressMealTrends = new JRadioButton("Compress Meal Trends");
		rdbtnCompressMealTrends.setToolTipText("<html>The analyzer can separate out rises from in range to out of range as well as rises from out of range to further out of range.  <br>Compressing the meal trends will just consider all rises to out of range together (and the opposite for falls too).</html>");
		GridBagConstraints gbc_rdbtnCompressMealTrends = new GridBagConstraints();
		gbc_rdbtnCompressMealTrends.anchor = GridBagConstraints.WEST;
		gbc_rdbtnCompressMealTrends.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnCompressMealTrends.gridx = 5;
		gbc_rdbtnCompressMealTrends.gridy = 16;
		panel_1.add(rdbtnCompressMealTrends, gbc_rdbtnCompressMealTrends);

		rdbtnCompressMealTrends.setSelected(PrefsNightScoutLoader.getInstance().isM_AnalyzerCompressMealTrends());

		rdbtnTotalRecurringTrendsOnly = new JRadioButton("Total Recurring Trends Only");
		rdbtnTotalRecurringTrendsOnly.setToolTipText("<html>When determining the %ge for recurring results, the analyzer can consider 100% to be either recurring trend results only or ALL trend results</html>");
		GridBagConstraints gbc_rdbtnTotalRecurringTrendsOnly = new GridBagConstraints();
		gbc_rdbtnTotalRecurringTrendsOnly.anchor = GridBagConstraints.WEST;
		gbc_rdbtnTotalRecurringTrendsOnly.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnTotalRecurringTrendsOnly.gridx = 9;
		gbc_rdbtnTotalRecurringTrendsOnly.gridy = 16;
		panel_1.add(rdbtnTotalRecurringTrendsOnly, gbc_rdbtnTotalRecurringTrendsOnly);
		setBGUnitsText();

		
		rdbtnAutoTuneInvoked = new JRadioButton("Run Autotune");
		rdbtnAutoTuneInvoked.setToolTipText("<html>Control that determines whether Autotune is run as part of analysis</html>");
		rdbtnAutoTuneInvoked.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				checkAutoTuneInvokedOptions(rdbtnAutoTuneInvoked);
			}
		});
		rdbtnAutoTuneInvoked.setSelected(PrefsNightScoutLoader.getInstance().isM_AutoTuneInvoked());
		GridBagConstraints gbc_rdbtnAutoTuneInvoked = new GridBagConstraints();
		gbc_rdbtnAutoTuneInvoked.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnAutoTuneInvoked.gridx = 9;
		gbc_rdbtnAutoTuneInvoked.gridy = 17;
		panel_1.add(rdbtnAutoTuneInvoked, gbc_rdbtnAutoTuneInvoked);

		
		lblAutoTuneNSURL = new JLabel("Nightscout URL");
		lblAutoTuneNSURL.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblAutoTuneNSURL = new GridBagConstraints();
		gbc_lblAutoTuneNSURL.anchor = GridBagConstraints.EAST;
		gbc_lblAutoTuneNSURL.insets = new Insets(0, 0, 0, 5);
		gbc_lblAutoTuneNSURL.gridx = 1;
		gbc_lblAutoTuneNSURL.gridy = 17;
		panel_1.add(lblAutoTuneNSURL, gbc_lblAutoTuneNSURL);

		tf_AutoTuneNSURL = new JTextField(PrefsNightScoutLoader.getInstance().getM_AutoTuneNSURL());
		tf_AutoTuneNSURL.setToolTipText("Nightscout URL used by Autotune");
		GridBagConstraints gbc_tf_AutoTuneNSURL = new GridBagConstraints();
		gbc_tf_AutoTuneNSURL.insets = new Insets(0, 0, 5, 0);
		gbc_tf_AutoTuneNSURL.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_AutoTuneNSURL.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_AutoTuneNSURL.gridwidth = 6;
		gbc_tf_AutoTuneNSURL.gridx = 3;
		gbc_tf_AutoTuneNSURL.gridy = 17;
		panel_1.add(tf_AutoTuneNSURL, gbc_tf_AutoTuneNSURL);
		tf_AutoTuneNSURL.setPreferredSize(new Dimension(7, 20));
		tf_AutoTuneNSURL.setColumns(25);

		
		lblAutoTuneServer = new JLabel("Autotune Linux Server");
		lblAutoTuneServer.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblAutoTuneServer = new GridBagConstraints();
		gbc_lblAutoTuneServer.anchor = GridBagConstraints.EAST;
		gbc_lblAutoTuneServer.insets = new Insets(0, 0, 0, 5);
		gbc_lblAutoTuneServer.gridx = 1;
		gbc_lblAutoTuneServer.gridy = 18;
		panel_1.add(lblAutoTuneServer, gbc_lblAutoTuneServer);

		tf_AutoTuneServer = new JTextField(PrefsNightScoutLoader.getInstance().getM_AutoTuneServer());
		tf_AutoTuneServer.setToolTipText("Linux Server that can run Autotune");
		GridBagConstraints gbc_tf_AutoTuneServer = new GridBagConstraints();
		gbc_tf_AutoTuneServer.insets = new Insets(0, 0, 5, 0);
		gbc_tf_AutoTuneServer.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_AutoTuneServer.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_AutoTuneServer.gridwidth = 6;
		gbc_tf_AutoTuneServer.gridx = 3;
		gbc_tf_AutoTuneServer.gridy = 18;
		panel_1.add(tf_AutoTuneServer, gbc_tf_AutoTuneServer);
		tf_AutoTuneServer.setPreferredSize(new Dimension(7, 20));
		tf_AutoTuneServer.setColumns(25);

		lblAutoTuneKeyFile = new JLabel("Autotune SSH Key File");
		lblAutoTuneKeyFile.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblAutoTuneKeyFile = new GridBagConstraints();
		gbc_lblAutoTuneKeyFile.anchor = GridBagConstraints.EAST;
		gbc_lblAutoTuneKeyFile.insets = new Insets(0, 0, 0, 5);
		gbc_lblAutoTuneKeyFile.gridx = 1;
		gbc_lblAutoTuneKeyFile.gridy = 19;
		panel_1.add(lblAutoTuneKeyFile, gbc_lblAutoTuneKeyFile);

		tf_AutoTuneKeyFile = new JTextField(PrefsNightScoutLoader.getInstance().getM_AutoTuneKeyFile());
		tf_AutoTuneKeyFile.setToolTipText("SSH Key file for authentication on Aututune server");
		GridBagConstraints gbc_tf_AutoTuneKeyFile = new GridBagConstraints();
		gbc_tf_AutoTuneKeyFile.insets = new Insets(0, 0, 5, 0);
		gbc_tf_AutoTuneKeyFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_AutoTuneKeyFile.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_AutoTuneKeyFile.gridwidth = 6;
		gbc_tf_AutoTuneKeyFile.gridx = 3;
		gbc_tf_AutoTuneKeyFile.gridy = 19;
		panel_1.add(tf_AutoTuneKeyFile, gbc_tf_AutoTuneKeyFile);
		tf_AutoTuneKeyFile.setPreferredSize(new Dimension(7, 20));
		tf_AutoTuneKeyFile.setColumns(25);
		
		rdbtnAutoTuneSSH2KeyLogin = new JRadioButton("Autotune Key Auth");
		rdbtnAutoTuneSSH2KeyLogin.setToolTipText("<html>Future Control.  At present not used, since authorization performed by encrypted key file.</html>");
		rdbtnAutoTuneSSH2KeyLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				checkAutoTuneKeyOptions(rdbtnAutoTuneSSH2KeyLogin);
			}
		});
		rdbtnAutoTuneSSH2KeyLogin.setSelected(PrefsNightScoutLoader.getInstance().isM_AutoTuneSSH2KeyLogin());
		GridBagConstraints gbc_rdbtnAutoTuneSSH2KeyLogin = new GridBagConstraints();
		gbc_rdbtnAutoTuneSSH2KeyLogin.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnAutoTuneSSH2KeyLogin.gridx = 9;
		gbc_rdbtnAutoTuneSSH2KeyLogin.gridy = 19;
		panel_1.add(rdbtnAutoTuneSSH2KeyLogin, gbc_rdbtnAutoTuneSSH2KeyLogin);

		
	/*
	 * 
	 
	 	private JLabel lblAutoTuneNSURL;
	private JTextField tf_AutoTuneNSURL;
	private JRadioButton rdbtnAutoTuneSSH2KeyLogin;
	private JLabel lblAutoTuneKeyFile;
	private JTextField tf_AutoTuneKeyFile;

	 */
		

		//		private JTextField tf_LogLevel;
		//		private JTextField tf_LogFile;


		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.SOUTH;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		getContentPane().add(panel, gbc_panel);

		m_AnaylzeButton = new JButton("Analyze!");
		m_AnaylzeButton.setToolTipText("<html><b>Starts the Analysis<br>Once complete, Excel will launch with results.</b></html>");
		m_AnaylzeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//analyze();
				threadAnalyze();
				dispose();
			}
		});
		panel.add(m_AnaylzeButton);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setToolTipText("<html><b>Cancels the Analysis and closes this window</b></html>");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		panel.add(btnCancel);

		btnResetToDefaults = new JButton("Reset to Defaults");
		btnResetToDefaults.setToolTipText("<html><b>Resets the Analysis settings to Factory Defaults</b></html>");
		btnResetToDefaults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				resetDefaults();
			}
		});

		btnCGMDates = new JButton("CGM Dates");
		btnCGMDates.setToolTipText("<html>Click here to see a range of dates that have CGM results.  <br>This offers a convenient way of selecting Start and End dates to analyze a period of CGM results.  <br>Look at the dates in the popup and double click a row to make the selection.</html>");
		panel.add(btnCGMDates);
		btnCGMDates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				// Display a popup window that displays the CGM date ranges
				// and allows one to be selected to set start and end dates 

				if (CoreNightScoutLoader.getInstance().getM_NightScoutArrayListDBResultEntries().size() == 0)
				{
					JOptionPane.showMessageDialog(null, 
							"No CGM Date Ranges found.  Is CGM loading enabled?");
				}
				else
				{
					m_WinCGMRanges.intializeCGMRanges();
					m_WinCGMRanges.setVisible(true);
				}
			}
		});
		panel.add(btnResetToDefaults);	

		toggleAdvancedOptions(PrefsNightScoutLoader.getInstance().isM_AdvancedSettings());

		// Set values based on the restore method
		loadOptions();
	}

	void 	warningMessage(String message)
	{
		JOptionPane.showMessageDialog(this, message); 
	}

	void checkAndSetDates(JDatePickerImpl jdpJustSet)
	{
		Date jdpJustSetDate = (Date)jdpJustSet.getModel().getValue();
		Date startDate      = (Date)jp_StartDate.getModel().getValue();
		Date endDate        = (Date)jp_EndDate.getModel().getValue();

		// Check if end is before start
		if (endDate.before(startDate))
		{
			String jdpJustSetDateStr = new String("");
			try {
				jdpJustSetDateStr = CommonUtils.convertDateString(jdpJustSetDate, "dd-MMM-yyyy");
			} catch (ParseException e) {
				m_Logger.log( Level.SEVERE, "Unexpected error converting dates to check end date");
			}

			// Add a warning
			warningMessage("Please note the following:\n\n" + "The selected date (" + 
					jdpJustSetDateStr + ") means the end date is before the start date.\n\nPlease change.");
		}

		else if (jdpJustSetDate.after(m_MostRecentResultDate))
		{
			String jdpJustSetDateStr = new String("");
			try {
				jdpJustSetDateStr = CommonUtils.convertDateString(jdpJustSetDate, "dd-MMM-yyyy");
			} catch (ParseException e) {
				m_Logger.log( Level.SEVERE, "Unexpected error converting dates to check end date");
			}

			// Add a warning
			warningMessage("Please note the following:\n\n" + "The selected date (" + 
					jdpJustSetDateStr + ") is later than the date of last result loaded (" 
					+ m_MostRecentResultDateStr + ")\n\n" +
					"Analysis can only therefore run on results between (" + m_OldestResultDateStr + ") and (" + m_MostRecentResultDateStr + ")");
		}
		else if (jdpJustSetDate.before(m_OldestResultDate))
		{
			String jdpJustSetDateStr = new String("");
			try {
				jdpJustSetDateStr = CommonUtils.convertDateString(jdpJustSetDate, "dd-MMM-yyyy");
			} catch (ParseException e) {
				m_Logger.log( Level.SEVERE, "Unexpected error converting dates to check end date");
			}

			// Add a warning
			warningMessage("Please note the following:\n\n" + "The selected date (" + 
					jdpJustSetDateStr + ") is earlier than the date of earliest result loaded (" 
					+ m_OldestResultDateStr + ")\n\n" +
					"Analysis can only therefore run on results between (" + m_OldestResultDateStr + ") and (" + m_MostRecentResultDateStr + ")");
		}

		setDaysBackFromDates();
	}

	void checkIncludeOptions(JRadioButton rbJustSet)
	{
		// Check whether all RBs are disabled.
		// If so and this is the last one, then reset to enabled.

		boolean atLeastOneEnabled = 
				rdbtnIncludeBreakfast.isSelected() || rdbtnIncludeLunch.isSelected() || 
				rdbtnIncludeDinner.isSelected(   ) || rdbtnIncludeOvernight.isSelected();

		if (atLeastOneEnabled == false)
		{
			rbJustSet.setSelected(true);

			// Add a warning
			warningMessage("Analyzer has nothing to do if no meals are included!\n\n" +
					"Therefore the option to '" + rbJustSet.getText() + "' cannot be disabled\n"+
					"unless the option to include one of the other meals is enabled.");
		}
	}

	void checkAutoTuneInvokedOptions(JRadioButton rbJustSet)
	{
		boolean selected = rbJustSet.isSelected();

		lblAutoTuneServer.setEnabled(selected);
		tf_AutoTuneServer.setEnabled(selected);
		lblAutoTuneNSURL.setEnabled(selected);
		tf_AutoTuneNSURL.setEnabled(selected);
		rdbtnAutoTuneSSH2KeyLogin.setEnabled(selected);
		lblAutoTuneKeyFile.setEnabled(selected);
		tf_AutoTuneKeyFile.setEnabled(selected);
	}

	
	void checkAutoTuneKeyOptions(JRadioButton rbJustSet)
	{
		boolean selected = rbJustSet.isSelected() ;
		lblAutoTuneKeyFile.setEnabled(selected);
		tf_AutoTuneKeyFile.setEnabled(selected);
	}


	public void resetDefaults()
	{
		PrefsNightScoutLoader.getInstance().loadAnalyzerDefaultPreferences();
		loadOptions();
	}

	private void setBGUnitsText()
	{
		tf_BGUnits.setText( (PrefsNightScoutLoader.getBGUnitMultiplier() == 1) ? 
				"BG Units are all in mmol/L" : "BG Units are all in mg/dL");
	}

	@Override
	public void setVisible(boolean visible)
	{
		// Check units used...
		setBGUnitsText();

		// Set maximum dates based on last result
		setMaximumDates();

		// Days back drives it initially.
		//		setDatesFromDaysBack();

		// Set date based on latest value
		// Use date diff to work backwards

		// Finally, set visible
		super.setVisible(visible);

	}

	private void setMaximumDates()
	{
		// Setting min max dates seems to break the spinners.
		// Not essential, so comment all out

		//		ArrayList<DBResult> resultList = CoreNightScoutLoader.getInstance().getM_ResultsMongoDB();
		//		if (resultList.size() > 0)
		//		{
		//			Date endDate   = Analyzer.getLastDateFromDBResults(resultList);
		//			Date firstDate = Analyzer.getFirstDateFromDBResults(resultList);
		//			SpinnerDateModel startDateModel = (SpinnerDateModel)sp_StartDate.getModel();
		//			SpinnerDateModel endDateModel   = (SpinnerDateModel)sp_EndDate.getModel();
		//			startDateModel.setEnd(endDate.getTime());
		//			startDateModel.setStart(firstDate.getTime());
		//			endDateModel.setEnd(endDate.getTime());
		//			endDateModel.setStart(firstDate.getTime());
		//		}

		ArrayList<DBResult> resultList = CoreNightScoutLoader.getInstance().getM_ResultsMongoDB();
		m_MostRecentResultDate = Analyzer.getLastDateFromDBResults(resultList);
		m_OldestResultDate     = Analyzer.getFirstDateFromDBResults(resultList);

		try {
			m_MostRecentResultDateStr = CommonUtils.convertDateString(m_MostRecentResultDate, "dd-MMM-yyyy");
			m_OldestResultDateStr     = CommonUtils.convertDateString(m_OldestResultDate, "dd-MMM-yyyy");
		} catch (ParseException e) {
			m_Logger.log( Level.SEVERE, "Unexpected error converting most recent result date to string");
		}


		// Set end date as the last result in the system
		((UtilDateModel)jp_EndDate.getModel()).setValue(m_MostRecentResultDate);
		setDaysBackFromDates();
	}

	private void setDatesFromDaysBack()
	{
		ArrayList<DBResult> resultList = CoreNightScoutLoader.getInstance().getM_ResultsMongoDB();
		if (resultList.size() > 0 && m_SettingDatesOrDays == false)
		{
			m_SettingDatesOrDays = true;
			int daysBack   = (int)sp_DaysBack.getModel().getValue();
			Date endDate   = Analyzer.getLastDateFromDBResults(resultList);
			Date startDate = Analyzer.getDateOffsetBy(endDate, daysBack);

			//			sp_StartDate.getModel().setValue(startDate);
			//			sp_EndDate.getModel().setValue(endDate);
			m_SettingDatesOrDays = false;

			final DateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

			((UtilDateModel)jp_StartDate.getModel()).setValue(startDate);
			((UtilDateModel)jp_EndDate.getModel()).setValue(endDate);

			String startDateTxt = new String(format.format(startDate.getTime()));
			String endDateTxt   = new String(format.format(endDate.getTime()));

			jp_StartDate.getJFormattedTextField().setText(startDateTxt);
			jp_EndDate.getJFormattedTextField().setText(endDateTxt);

		}
	}

	@Override
	public void setDates(Date startDate, Date endDate)
	{
		final DateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

		((UtilDateModel)jp_StartDate.getModel()).setValue(startDate);
		((UtilDateModel)jp_EndDate.getModel()).setValue(endDate);

		String startDateTxt = new String(format.format(startDate.getTime()));
		String endDateTxt   = new String(format.format(endDate.getTime()));

		jp_StartDate.getJFormattedTextField().setText(startDateTxt);
		jp_EndDate.getJFormattedTextField().setText(endDateTxt);
		
		setDaysBackFromDates();
	}

	private void setDaysBackFromDates()
	{
		if (m_SettingDatesOrDays == false)
		{
			m_SettingDatesOrDays = true;

			Date startDate = (Date)jp_StartDate.getModel().getValue();
			Date endDate = (Date)jp_EndDate.getModel().getValue();
			//			Date startDate = (Date)sp_StartDate.getModel().getValue();
			//			Date endDate = (Date)sp_EndDate.getModel().getValue();

			long diff = endDate.getTime() - startDate.getTime();

			long daysBack =  TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
			sp_DaysBack.getModel().setValue((int)daysBack);
			m_SettingDatesOrDays = false;
		}
	}


	private void toggleAdvancedOptions(boolean advancedOptions)
	{
		tf_LowRangeThreshold.setVisible(advancedOptions);
		tf_HighRangeThreshold.setVisible(advancedOptions);
		lblHighPriorityRecurrRatio.setVisible(advancedOptions);
		lblMediumPriorityRecurrRatio.setVisible(advancedOptions);
		lblLowRangeThreshold.setVisible(advancedOptions);
		lblHighRangeThreshold.setVisible(advancedOptions);
		loadOptions();
	}

	public void loadOptions()
	{
		sp_DaysBack.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerDaysBack());
		this.setDatesFromDaysBack();

		PrefsNightScoutLoader.getInstance().setM_AnalyzerBedTrendStartStartTime((String)sp_bedTrendStartStart.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerBedTrendEndStartTime((String)sp_bedTrendEndStart.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerBedTrendStartEndTime((String)sp_bedTrendStartEnd.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerBedTrendEndEndTime((String)sp_bedTrendEndEnd.getModel().getValue());

		PrefsNightScoutLoader.getInstance().setM_AnalyzerBadNightStartTime((String)sp_BadnightStart.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerBadNightEndTime((String)sp_BadnightEnd.getModel().getValue());

		PrefsNightScoutLoader.getInstance().setM_AnalyzerCompressMealTrends(rdbtnCompressMealTrends.isSelected());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerTotalRecurringTrendsOnly(rdbtnTotalRecurringTrendsOnly.isSelected());

		PrefsNightScoutLoader.getInstance().setPreferences();

		//		PrefsNightScoutLoader.getInstance().setM_AnalyzerIncludeBreakfast(rdbtnIncludeBreakfast.isSelected());
		//		PrefsNightScoutLoader.getInstance().setM_AnalyzerIncludeLunch(rdbtnIncludeLunch.isSelected());
		//		PrefsNightScoutLoader.getInstance().setM_AnalyzerIncludeDinner(rdbtnIncludeDinner.isSelected());
		//		PrefsNightScoutLoader.getInstance().setM_AnalyzerIncludeOvernight(rdbtnIncludeOvernight.isSelected());

		tf_ExcelFile.setText(PrefsNightScoutLoader.getInstance().getM_AnalysisFilePath());

		sp_breakfastStart.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerBreakfastTimeStart());
		sp_lunchStart.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerLunchTimeStart());
		sp_dinnerStart.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerDinnerTimeStart());
		sp_bedStart.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTimeStart());

		tf_MinMinsForTrend.setText(String.format("%d", PrefsNightScoutLoader.getInstance().getM_AnalyzerMinMinsForTrendResults()));
		tf_HighThreshold.setText(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerHighThreshold()));
		tf_LowThreshold.setText(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerLowThreshold()));

		tf_BreakfastTrendRatio.setText(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerBreakfastChangeTrendRatio()));
		tf_LunchTrendRatio.setText(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerLunchChangeTrendRatio()));
		tf_DinnerTrendRatio.setText(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerDinnerChangeTrendRatio()));
		tf_OvernightTrendRatio.setText(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerOvernightChangeTrendRatio()));
		tf_LowRangeThreshold.setText(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerLowRangeThreshold()));
		tf_HighRangeThreshold.setText(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerHighRangeThreshold()));
		tf_HighThresholdRelevanceFactor.setText(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerHighThresholdRelevanceFactor()));
		tf_LowThresholdRelevanceFactor.setText(String.format("%g", PrefsNightScoutLoader.getInstance().getM_AnalyzerLowThresholdRelevanceFactor()));

		sp_bedTrendStartStart.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendStartStartTime());
		sp_bedTrendEndStart.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendEndStartTime());
		sp_bedTrendStartEnd.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendStartEndTime());
		sp_bedTrendEndEnd.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerBedTrendEndEndTime());
		sp_BadnightStart.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerBadNightStartTime());
		sp_BadnightEnd.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_AnalyzerBadNightEndTime());

		sp_CGMTrendIntervalDuration.getModel().setValue(PrefsNightScoutLoader.getInstance().getM_EntryAnalyzerIntervalHours());
		
		rdbtnCompressMealTrends.setSelected(PrefsNightScoutLoader.getInstance().isM_AnalyzerCompressMealTrends());		
		rdbtnTotalRecurringTrendsOnly.setSelected(PrefsNightScoutLoader.getInstance().isM_AnalyzerTotalRecurringTrendsOnly());	

		rdbtnIncludeBreakfast.setSelected(PrefsNightScoutLoader.getInstance().isM_AnalyzerIncludeBreakfast());		
		rdbtnIncludeLunch.setSelected(PrefsNightScoutLoader.getInstance().isM_AnalyzerIncludeLunch());		
		rdbtnIncludeDinner.setSelected(PrefsNightScoutLoader.getInstance().isM_AnalyzerIncludeDinner());		
		rdbtnIncludeOvernight.setSelected(PrefsNightScoutLoader.getInstance().isM_AnalyzerIncludeOvernight());	

		cb_ExcelOutputLevel.setSelectedIndex(PrefsNightScoutLoader.getInstance().getM_AnalyzerExcelOutputLevel());

		rdbtnAutoTuneInvoked.setSelected(PrefsNightScoutLoader.getInstance().isM_AutoTuneInvoked());
		tf_AutoTuneServer.setText(PrefsNightScoutLoader.getInstance().getM_AutoTuneServer());
		tf_AutoTuneNSURL.setText(PrefsNightScoutLoader.getInstance().getM_AutoTuneNSURL());
		rdbtnAutoTuneSSH2KeyLogin.setSelected(PrefsNightScoutLoader.getInstance().isM_AutoTuneSSH2KeyLogin());
		tf_AutoTuneKeyFile.setText(PrefsNightScoutLoader.getInstance().getM_AutoTuneKeyFile());

		
		setBGUnitsText();
	}

	private void savePreferences()
	{
		// Take what's on the panel first
		PrefsNightScoutLoader.getInstance().setM_AnalysisFilePath(tf_ExcelFile.getText());
		//		String excelFile = m_MainWin.getSelectedExcelFileForOutput(PrefsNightScoutLoader.getInstance().getM_AnalysisFilePath(), 
		//				"Results Analysis", 
		//				"You chose to generate Analysis to this file: ");

		// Update the text field
		//		tf_ExcelFile.setText(excelFile);

		// Save options first since analyzer retrieves them from Prefs
		//		Date startDate = (Date)sp_StartDate.getModel().getValue();
		//		Date endDate   = (Date)sp_EndDate.getModel().getValue();

		Date startDate = (Date)jp_StartDate.getModel().getValue();
		Date endDate   = (Date)jp_EndDate.getModel().getValue();


		PrefsNightScoutLoader.getInstance().setM_AnalyzerDaysBack((int)sp_DaysBack.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerStartDateLong(startDate.getTime());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerEndDateLong(endDate.getTime());

		PrefsNightScoutLoader.getInstance().setM_AnalysisFilePath(tf_ExcelFile.getText());

		PrefsNightScoutLoader.getInstance().setM_AnalyzerBreakfastTimeStart((String)sp_breakfastStart.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerLunchTimeStart((String)sp_lunchStart.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerDinnerTimeStart((String)sp_dinnerStart.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerBedTimeStart((String)sp_bedStart.getModel().getValue());

		PrefsNightScoutLoader.getInstance().setM_AnalyzerMinMinsForTrendResults(Integer.parseInt(tf_MinMinsForTrend.getText()));

		PrefsNightScoutLoader.getInstance().setM_AnalyzerHighThreshold(Double.parseDouble(tf_HighThreshold.getText()));
		PrefsNightScoutLoader.getInstance().setM_AnalyzerLowThreshold(Double.parseDouble(tf_LowThreshold.getText()));

		PrefsNightScoutLoader.getInstance().setM_AnalyzerBreakfastChangeTrendRatio(Double.parseDouble(tf_BreakfastTrendRatio.getText()));
		PrefsNightScoutLoader.getInstance().setM_AnalyzerLunchChangeTrendRatio(Double.parseDouble(tf_LunchTrendRatio.getText()));
		PrefsNightScoutLoader.getInstance().setM_AnalyzerDinnerChangeTrendRatio(Double.parseDouble(tf_DinnerTrendRatio.getText()));
		PrefsNightScoutLoader.getInstance().setM_AnalyzerOvernightChangeTrendRatio(Double.parseDouble(tf_OvernightTrendRatio.getText()));

		PrefsNightScoutLoader.getInstance().setM_AnalyzerHighFrequencyPercentage((double)tf_HighPriorityRecurrRatio.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerMediumFrequencyPercentage((double)tf_MediumPriorityRecurrRatio.getModel().getValue());

		PrefsNightScoutLoader.getInstance().setM_AnalyzerLowRangeThreshold(Double.parseDouble(tf_LowRangeThreshold.getText()));
		PrefsNightScoutLoader.getInstance().setM_AnalyzerHighRangeThreshold(Double.parseDouble(tf_HighRangeThreshold.getText()));

		PrefsNightScoutLoader.getInstance().setM_AnalyzerHighThresholdRelevanceFactor(Double.parseDouble(tf_HighThresholdRelevanceFactor.getText()));
		PrefsNightScoutLoader.getInstance().setM_AnalyzerLowThresholdRelevanceFactor(Double.parseDouble(tf_LowThresholdRelevanceFactor.getText()));

		PrefsNightScoutLoader.getInstance().setM_AnalyzerBedTrendStartStartTime((String)sp_bedTrendStartStart.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerBedTrendEndStartTime((String)sp_bedTrendEndStart.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerBedTrendStartEndTime((String)sp_bedTrendStartEnd.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerBedTrendEndEndTime((String)sp_bedTrendEndEnd.getModel().getValue());

		PrefsNightScoutLoader.getInstance().setM_AnalyzerBadNightStartTime((String)sp_BadnightStart.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerBadNightEndTime((String)sp_BadnightEnd.getModel().getValue());

		PrefsNightScoutLoader.getInstance().setM_EntryAnalyzerIntervalHours((int)sp_CGMTrendIntervalDuration.getModel().getValue());
		
		PrefsNightScoutLoader.getInstance().setM_AnalyzerCompressMealTrends(rdbtnCompressMealTrends.isSelected());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerTotalRecurringTrendsOnly(rdbtnTotalRecurringTrendsOnly.isSelected());

		PrefsNightScoutLoader.getInstance().setM_AnalyzerIncludeBreakfast(rdbtnIncludeBreakfast.isSelected());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerIncludeLunch(rdbtnIncludeLunch.isSelected());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerIncludeDinner(rdbtnIncludeDinner.isSelected());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerIncludeOvernight(rdbtnIncludeOvernight.isSelected());
		PrefsNightScoutLoader.getInstance().setM_AnalyzerExcelOutputLevel(cb_ExcelOutputLevel.getSelectedIndex());

		PrefsNightScoutLoader.getInstance().setM_AutoTuneInvoked(rdbtnAutoTuneInvoked.isSelected());
		PrefsNightScoutLoader.getInstance().setM_AutoTuneServer(tf_AutoTuneServer.getText());
		PrefsNightScoutLoader.getInstance().setM_AutoTuneNSURL(tf_AutoTuneNSURL.getText());
		PrefsNightScoutLoader.getInstance().setM_AutoTuneSSH2KeyLogin(rdbtnAutoTuneSSH2KeyLogin.isSelected());
		PrefsNightScoutLoader.getInstance().setM_AutoTuneKeyFile(tf_AutoTuneKeyFile.getText());
		
		PrefsNightScoutLoader.getInstance().setPreferences();
	}

	private void setExcelOutputLevel()
	{
		AnalyzerTabs.getInstance().setupListOfTabs();
	}

	public void setSelectedExcelFile(String file)
	{
		tf_ExcelFile.setText(file);
	}

	//	private void analyze()
	//	{
	//		savePreferences();
	//
	//		// Do the analysis
	//		this.m_MainWin.deeperAnalyseResults(tf_ExcelFile.getText());
	//		
	//		// David up to here
	//		//
	//		// doThreadAnalyzeResults(String excelFilename, AnalyzerCompleteHander handler)
	//	}
	//	
	public void threadAnalyze()
	{
		// Now insist that a filename is selected at top panel		
		savePreferences();

		// Now set Excel level based on selection
		setExcelOutputLevel();

		// Do the analysis
		boolean analysisRan = this.m_MainWin.threadDeeperAnalyseResults(
				new AnalyzerCompleteHander(this)
				{
					//		@Override
					public void exceptionRaised(String message) 
					{
						// We want to do this UI change in the main thread, and not the DB worker thread that's just
						// notified back
						EventQueue.invokeLater(new 
								Runnable()
						{ 
							public void run()
							{ 
								// Re-enable the analyze button in GUI thread
								m_AnaylzeButton.setEnabled(true);
							}
						});
					}

					//		@Override
					public void analyzeResultsComplete(Object obj) 
					{
						// We want to do this UI change in the main thread, and not the DB worker thread that's just
						// notified back
						EventQueue.invokeLater(new 
								Runnable()
						{ 
							public void run()
							{ 
								// Re-enable the analyze button in GUI thread
								m_AnaylzeButton.setEnabled(true);
								m_MainWin.openExcelFile(tf_ExcelFile.getText());

								// Experimental - display a chart
								//								m_MainWin.displayCGMChart();
							}
						});

					}
				}
				);

		// Disable the Analyze! button from allowing analyze to run again - if analysis actually ran
		m_AnaylzeButton.setEnabled(analysisRan ? false : true);
	}

}
