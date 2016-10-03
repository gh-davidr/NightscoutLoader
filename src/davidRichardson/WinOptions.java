package davidRichardson;

import javax.swing.JButton;
import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.net.URL;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Color;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class WinOptions extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6089944380592193437L;

	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	private String[] m_TimezoneList = 
		{
				"Local Timezone",
				"GMT +1",
				"GMT +2",
				"GMT +3",
				"GMT +4",
				"GMT +5",
				"GMT +6",
				"GMT +7",
				"GMT +8",
				"GMT +9",
				"GMT +10",
				"GMT +11",
				"GMT +12",
				"GMT +13",
				"GMT -1",
				"GMT -2",
				"GMT -3",
				"GMT -4",
				"GMT -5",
				"GMT -6",
				"GMT -7",
				"GMT -8",
				"GMT -9",
				"GMT -10",
				"GMT -11",
				"GMT -12",
				"GMT -13",
		};

	private String[] m_InputDateFormatList = {"Default", "dd/MM/yy hh:mm", "MM/dd/yy hh:mm", "dd/MM/yy", "MM/dd/yy"};

	private WinNightScoutLoader m_WinMain;
	private JTextField tf_SQLFile;
	private JTextField tf_DBServer;
	private JTextField tf_DBInstance;
	private JTextField tf_DBName;
	private JTextField tf_NightscoutServer;
	private JTextField tf_NightscoutDB;
	private JTextField tf_NightscoutCollection;
	private JTextField tf_MeterMongoServer;
	private JTextField tf_MeterMongoDB;
	private JTextField tf_MeterMongoCollection;
	private JRadioButton rb_UseMongoForRoche;
	private JLabel lbl_MeterMongoServer;
	private JLabel lbl_MeterMongoDB;
	private JLabel lbl_MeterMongoCollection;
	private JLabel lbl_LogLevel;
	private JLabel lbl_LogFile;
	private JButton btn_SelectLogFile;
	private JLabel lbl_MaxMinsForMealBolus;
	private JLabel lbl_MaxMinsForCorrectionBolus;
	private JTextField tf_LogFile;

	private DataLoadRoche m_SQLServerLoader;
	private DataLoadNightScout   m_MongoDBLoader;

	// Our own private logger
	private JComboBox<String> cb_LogLevel;
	private JComboBox<String> cb_BGUnitList;

	private JLabel lblBGUnits;
	private JSpinner sp_DaysToLoad;
	private JSpinner sp_MaxMinsForMealBolus;
	private JSpinner sp_MaxMinsForCorrectionBolus;
	private JLabel lblDateFormat;
	private JLabel lblTimezone;
	private JComboBox<String> cb_Timezone;
	private JComboBox<String> cb_InputDateFormat;
	private JLabel lblProximityMinutes;
	private JSpinner sp_ProximityMinutes;
	private JLabel lblMongoCheckMinutes;
	private JSpinner sp_MongoDBAlertMinutes;
	private JRadioButton rbProximityTypeCheck;
	private JComboBox cbProximityValueCheck;

	/**
	 * Launch the application.
	 */
	/**
	 * @param args
	 */


	/**
	 * Create the dialog.
	 */
	public WinOptions(WinNightScoutLoader winMain, String title) 
	{
		super();
		getContentPane().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				
				// David 30 Sep 2016
				// Catch when set visible and call loadOptions
				// Problem is constructor is called before Prefs is initialized.
			}
		});

		m_WinMain = winMain;

		URL url = MainNightScoutLoader.class.getResource("/Nightscout.jpg");
		ImageIcon img = new ImageIcon(url);
		setIconImage(img.getImage());

		m_SQLServerLoader = new DataLoadRoche();
		m_MongoDBLoader   = new DataLoadNightScout();

		super.setTitle(title);
		setBounds(100, 100, 650, 600);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {300, 0};
		gridBagLayout.rowHeights = new int[] {300, 20};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0};
		getContentPane().setLayout(gridBagLayout);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		getContentPane().add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] {50, 147, 30};
		gbl_panel_1.rowHeights = new int[] {30, 0, 0, 0, 0, 0, 0, 0, 30, 30, 0, 30, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 1.0, 1.0};
		gbl_panel_1.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);

		JLabel lblDbServerName = new JLabel("DB Server Name");
		GridBagConstraints gbc_lblDbServerName = new GridBagConstraints();
		gbc_lblDbServerName.anchor = GridBagConstraints.EAST;
		gbc_lblDbServerName.insets = new Insets(0, 0, 5, 5);
		gbc_lblDbServerName.gridx = 1;
		gbc_lblDbServerName.gridy = 0;
		panel_1.add(lblDbServerName, gbc_lblDbServerName);

		tf_DBServer = new JTextField(PrefsNightScoutLoader.getInstance().getM_SQLDBServerHost());
		GridBagConstraints gbc_tf_DBServer = new GridBagConstraints();
		gbc_tf_DBServer.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_DBServer.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_DBServer.insets = new Insets(0, 0, 5, 5);
		gbc_tf_DBServer.gridwidth = 4;
		gbc_tf_DBServer.gridx = 2;
		gbc_tf_DBServer.gridy = 0;
		panel_1.add(tf_DBServer, gbc_tf_DBServer);
		//		tf_DBServer.setColumns(15);

		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 7;
		gbc_panel_2.gridy = 0;
		panel_1.add(panel_2, gbc_panel_2);

		JLabel lblDbServerInstance = new JLabel("DB Server Instance");
		GridBagConstraints gbc_lblDbServerInstance = new GridBagConstraints();
		gbc_lblDbServerInstance.anchor = GridBagConstraints.EAST;
		gbc_lblDbServerInstance.insets = new Insets(0, 0, 5, 5);
		gbc_lblDbServerInstance.gridx = 1;
		gbc_lblDbServerInstance.gridy = 1;
		panel_1.add(lblDbServerInstance, gbc_lblDbServerInstance);

		tf_DBInstance = new JTextField(PrefsNightScoutLoader.getInstance().getM_SQLDBServerInstance());
		GridBagConstraints gbc_tf_DBInstance = new GridBagConstraints();
		gbc_tf_DBInstance.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_DBInstance.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_DBInstance.insets = new Insets(0, 0, 5, 5);
		gbc_tf_DBInstance.gridwidth = 4;
		gbc_tf_DBInstance.gridx = 2;
		gbc_tf_DBInstance.gridy = 1;
		panel_1.add(tf_DBInstance, gbc_tf_DBInstance);
		tf_DBInstance.setColumns(15);

		JButton btnTest = new JButton("Test");
		btnTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				testSQLServer();
			}
		});		
		GridBagConstraints gbc_btnTest = new GridBagConstraints();
		gbc_btnTest.insets = new Insets(0, 0, 5, 5);
		gbc_btnTest.gridx = 6;
		gbc_btnTest.gridy = 1;
		panel_1.add(btnTest, gbc_btnTest);

		JLabel lblDbName = new JLabel("DB Name");
		GridBagConstraints gbc_lblDbName = new GridBagConstraints();
		gbc_lblDbName.anchor = GridBagConstraints.EAST;
		gbc_lblDbName.insets = new Insets(0, 0, 5, 5);
		gbc_lblDbName.gridx = 1;
		gbc_lblDbName.gridy = 2;
		panel_1.add(lblDbName, gbc_lblDbName);

		tf_DBName = new JTextField(PrefsNightScoutLoader.getInstance().getM_SQLDBName());
		GridBagConstraints gbc_tf_DBName = new GridBagConstraints();
		gbc_tf_DBName.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_DBName.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_DBName.insets = new Insets(0, 0, 5, 5);
		gbc_tf_DBName.gridwidth = 4;
		gbc_tf_DBName.gridx = 2;
		gbc_tf_DBName.gridy = 2;
		panel_1.add(tf_DBName, gbc_tf_DBName);
		//	tf_DBName.setColumns(25);

		JLabel lblDaysToLoad = new JLabel("Days to Load");
		GridBagConstraints gbc_lblDaysToLoad = new GridBagConstraints();
		gbc_lblDaysToLoad.anchor = GridBagConstraints.EAST;
		gbc_lblDaysToLoad.insets = new Insets(0, 0, 5, 5);
		gbc_lblDaysToLoad.gridx = 1;
		gbc_lblDaysToLoad.gridy = 3;
		panel_1.add(lblDaysToLoad, gbc_lblDaysToLoad);
		lblDaysToLoad.setHorizontalAlignment(SwingConstants.LEFT);

		sp_DaysToLoad = new JSpinner();
		sp_DaysToLoad.setModel(new SpinnerNumberModel(45, 1, 1000, 1));
		sp_DaysToLoad.setToolTipText("How many days' history to load from Roche SQL Server and compare with NS");
		sp_DaysToLoad.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_DaysToLoad());
		GridBagConstraints gbc_sp_DaysToLoad = new GridBagConstraints();
		gbc_sp_DaysToLoad.insets = new Insets(0, 0, 5, 5);
		gbc_sp_DaysToLoad.gridx = 2;
		gbc_sp_DaysToLoad.gridy = 3;
		panel_1.add(sp_DaysToLoad, gbc_sp_DaysToLoad);

		JLabel lblSqlFile = new JLabel("SQL File");
		GridBagConstraints gbc_lblSqlFile = new GridBagConstraints();
		gbc_lblSqlFile.anchor = GridBagConstraints.EAST;
		gbc_lblSqlFile.insets = new Insets(0, 0, 5, 5);
		//gbc_lblSqlFile.gridwidth = 2;
		gbc_lblSqlFile.gridx = 1;
		gbc_lblSqlFile.gridy = 4;
		panel_1.add(lblSqlFile, gbc_lblSqlFile);

		tf_SQLFile = new JTextField(PrefsNightScoutLoader.getInstance().getM_SQLFile());
		GridBagConstraints gbc_tf_SQLFile = new GridBagConstraints();
		gbc_tf_SQLFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_SQLFile.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_SQLFile.insets = new Insets(0, 0, 5, 5);
		gbc_tf_SQLFile.gridwidth = 4;
		gbc_tf_SQLFile.gridx = 2;
		gbc_tf_SQLFile.gridy = 4;
		panel_1.add(tf_SQLFile, gbc_tf_SQLFile);
		tf_SQLFile.setPreferredSize(new Dimension(7, 20));
		tf_SQLFile.setColumns(25);

		JButton btnSelect = new JButton("Select");
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"SQL Files", "sql");
				chooser.setFileFilter(filter);
				File selectedFile = new File(tf_SQLFile.getText());
				chooser.setSelectedFile(selectedFile);
				int returnVal = chooser.showOpenDialog(getContentPane());        	    		
				if(returnVal == JFileChooser.APPROVE_OPTION) 
				{
					tf_SQLFile.setText(chooser.getSelectedFile().getAbsolutePath());
					m_Logger.log( Level.INFO, "You chose to open this file for SQL File Contents: " +
							chooser.getSelectedFile().getAbsolutePath());
				}				
			}
		});
		GridBagConstraints gbc_btnSelect = new GridBagConstraints();
		gbc_btnSelect.insets = new Insets(0, 0, 5, 5);
		gbc_btnSelect.gridx = 6;
		gbc_btnSelect.gridy = 4;
		panel_1.add(btnSelect, gbc_btnSelect);

		JLabel label = new JLabel("Nightscout Server");
		label.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		//gbc_label.gridwidth = 3;
		gbc_label.gridx = 1;
		gbc_label.gridy = 5;
		panel_1.add(label, gbc_label);

		tf_NightscoutServer = new JTextField(PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer());
		tf_NightscoutServer.setToolTipText("Key Parameter to identify the Nightscout MongoDB server.  Set this to an empty string to then force Nightscout Loader to run in stand-alone mode where it will not attempt a server connection.");
		tf_NightscoutServer.setBackground(Color.YELLOW);
		tf_NightscoutServer.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_tf_NightscoutServer = new GridBagConstraints();
		gbc_tf_NightscoutServer.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_NightscoutServer.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_NightscoutServer.insets = new Insets(0, 0, 5, 5);
		gbc_tf_NightscoutServer.gridwidth = 4;
		gbc_tf_NightscoutServer.gridx = 2;
		gbc_tf_NightscoutServer.gridy = 5;
		panel_1.add(tf_NightscoutServer, gbc_tf_NightscoutServer);
		tf_NightscoutServer.setColumns(10);

		lblBGUnits = new JLabel("BG Units");
		lblBGUnits.setBackground(Color.YELLOW);
		lblBGUnits.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblBGUnits;
		gbc_lblBGUnits = new GridBagConstraints();
		gbc_lblBGUnits.anchor = GridBagConstraints.EAST;
		gbc_lblBGUnits.insets = new Insets(0, 0, 5, 5);
		gbc_lblBGUnits.gridx = 1;
		gbc_lblBGUnits.gridy = 6;
		panel_1.add(lblBGUnits, gbc_lblBGUnits);


		cb_BGUnitList = new JComboBox<String>();
		cb_BGUnitList.setModel(new DefaultComboBoxModel<String>(new String[] {"mmol/L", "mg/dL"}));
		cb_BGUnitList.setBackground(Color.YELLOW);
		GridBagConstraints gbc_BGUnitList = new GridBagConstraints();
		gbc_BGUnitList.gridwidth = 2;
		gbc_BGUnitList.insets = new Insets(0, 0, 5, 5);
		gbc_BGUnitList.fill = GridBagConstraints.HORIZONTAL;
		gbc_BGUnitList.gridx = 3;
		gbc_BGUnitList.gridy = 6;
		panel_1.add(cb_BGUnitList, gbc_BGUnitList);

		JLabel label_1 = new JLabel("Nightscout DB");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.anchor = GridBagConstraints.EAST;
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		//gbc_label_1.gridwidth = 3;
		gbc_label_1.gridx = 1;
		gbc_label_1.gridy = 7;
		panel_1.add(label_1, gbc_label_1);

		tf_NightscoutDB = new JTextField(PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB());
		GridBagConstraints gbc_tf_NightscoutDB = new GridBagConstraints();
		gbc_tf_NightscoutDB.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_NightscoutDB.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_NightscoutDB.insets = new Insets(0, 0, 5, 5);
		gbc_tf_NightscoutDB.gridwidth = 4;
		gbc_tf_NightscoutDB.gridx = 2;
		gbc_tf_NightscoutDB.gridy = 7;
		panel_1.add(tf_NightscoutDB, gbc_tf_NightscoutDB);
		tf_NightscoutDB.setColumns(10);

		JButton btnTest_1 = new JButton("Test");
		btnTest_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				testMongo();
			}
		});

		GridBagConstraints gbc_btnTest_1 = new GridBagConstraints();
		gbc_btnTest_1.insets = new Insets(0, 0, 5, 5);
		gbc_btnTest_1.gridx = 6;
		gbc_btnTest_1.gridy = 7;
		panel_1.add(btnTest_1, gbc_btnTest_1);

		JLabel label_2 = new JLabel("Nightscout Collection");
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
		gbc_label_2.anchor = GridBagConstraints.EAST;
		gbc_label_2.insets = new Insets(0, 0, 5, 5);
		//gbc_label_2.gridwidth = 4;
		gbc_label_2.gridx = 1;
		gbc_label_2.gridy = 8;
		panel_1.add(label_2, gbc_label_2);

		tf_NightscoutCollection = new JTextField(PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection());
		GridBagConstraints gbc_tf_NightscoutCollection = new GridBagConstraints();
		gbc_tf_NightscoutCollection.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_NightscoutCollection.anchor = GridBagConstraints.EAST;
		gbc_tf_NightscoutCollection.insets = new Insets(0, 0, 5, 5);
		gbc_tf_NightscoutCollection.gridwidth = 4;
		gbc_tf_NightscoutCollection.gridx = 2;
		gbc_tf_NightscoutCollection.gridy = 8;
		panel_1.add(tf_NightscoutCollection, gbc_tf_NightscoutCollection);
		tf_NightscoutCollection.setColumns(25);

		JRadioButton rdbtnAdvancedOptions = new JRadioButton("Advanced Options");
		rdbtnAdvancedOptions.setToolTipText("Disabling Advanced Options hides the complexity of the Analyze window parameters.  It also hides all the features from Options window below this selector.");
		rdbtnAdvancedOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton aButton = (AbstractButton) actionEvent.getSource();
				PrefsNightScoutLoader.getInstance().setM_AdvancedOptions(aButton.isSelected());
				toggleAdvancedOptions(aButton.isSelected());
			}
		});
		GridBagConstraints gbc_rdbtnAdvancedOptions = new GridBagConstraints();
		gbc_rdbtnAdvancedOptions.anchor = GridBagConstraints.WEST;
		gbc_rdbtnAdvancedOptions.insets = new Insets(0, 0, 5, 5);
		// gbc_rdbtnAdvancedOptions.gridwidth = 5;
		gbc_rdbtnAdvancedOptions.gridx = 1;
		gbc_rdbtnAdvancedOptions.gridy = 9;
		panel_1.add(rdbtnAdvancedOptions, gbc_rdbtnAdvancedOptions);
		rdbtnAdvancedOptions.setSelected(PrefsNightScoutLoader.getInstance().isM_AdvancedOptions());

		rb_UseMongoForRoche = new JRadioButton("Use Mongo for Roche");
		rb_UseMongoForRoche.setToolTipText("Used for Development purposes.  Roche SQL Server data is encoded in MongoDB as a raw document to allow development on stand alone laptop.   Disable this in real use.");
		rb_UseMongoForRoche.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton aButton = (AbstractButton) actionEvent.getSource();
				PrefsNightScoutLoader.getInstance().setM_UseMongoForRocheResults(aButton.isSelected());
			}
		});

		lblTimezone = new JLabel("Timezone");
		GridBagConstraints gbc_lblTimezone = new GridBagConstraints();
		gbc_lblTimezone.anchor = GridBagConstraints.EAST;
		gbc_lblTimezone.insets = new Insets(0, 0, 5, 5);
		gbc_lblTimezone.gridx = 3;
		gbc_lblTimezone.gridy = 9;
		panel_1.add(lblTimezone, gbc_lblTimezone);

		cb_Timezone = new JComboBox<String>();
		cb_Timezone.setToolTipText("Times held in the Mongo DB are converted to UTC.  The application will determine local timezone automatically when set to \"Default\".  Override this setting when in a different timezone to home and handling files with dates / times in local timezone.  Therefor override in exceptional cases only.  Leave as Default for all other occassions");
		//		cb_Timezone.setModel(new DefaultComboBoxModel(new String[] {"Local Timezone", "GMT +1", "GMT +2", "GMT +3", "GMT +4", "GMT +5", "GMT +6", "GMT +7", "GMT +8", "GMT +9", "GMT +10", "GMT +11", "GMT +12", "GMT +13", "GMT -1", "GMT -2", "GMT -3", "GMT -4", "GMT -5", "GMT -6", "GMT -7", "GMT -8", "GMT -9", "GMT -10", "GMT -11", "GMT -12", "GMT -13"}));
		for (String c : m_TimezoneList)
		{
			cb_Timezone.addItem(c);
		}

		GridBagConstraints gbc_cb_Timezone = new GridBagConstraints();
		gbc_cb_Timezone.insets = new Insets(0, 0, 5, 5);
		gbc_cb_Timezone.fill = GridBagConstraints.HORIZONTAL;
		gbc_cb_Timezone.gridx = 4;
		gbc_cb_Timezone.gridy = 9;
		panel_1.add(cb_Timezone, gbc_cb_Timezone);


		GridBagConstraints gbc_rdbtnUseMongoForRoche = new GridBagConstraints();
		gbc_rdbtnUseMongoForRoche.anchor = GridBagConstraints.WEST;
		gbc_rdbtnUseMongoForRoche.insets = new Insets(0, 0, 5, 5);
		//	gbc_rdbtnUseMongoForRoche.gridwidth = 6;
		gbc_rdbtnUseMongoForRoche.gridx = 1;
		gbc_rdbtnUseMongoForRoche.gridy = 10;
		panel_1.add(rb_UseMongoForRoche, gbc_rdbtnUseMongoForRoche);
		rb_UseMongoForRoche.setSelected(PrefsNightScoutLoader.getInstance().isM_UseMongoForRocheResults());

		lblDateFormat = new JLabel("Date Format");
		GridBagConstraints gbc_lblDateFormat_1 = new GridBagConstraints();
		gbc_lblDateFormat_1.anchor = GridBagConstraints.EAST;
		gbc_lblDateFormat_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblDateFormat_1.gridx = 3;
		gbc_lblDateFormat_1.gridy = 10;
		panel_1.add(lblDateFormat, gbc_lblDateFormat_1);

		cb_InputDateFormat = new JComboBox<String>();
		cb_InputDateFormat.setToolTipText("Use to override inbuilt format for reading dates from files (Medtronic/Diasend).  Useful in some cases where regional variations have been seen.  Keep as Default for most cases.");
		for (String c : m_InputDateFormatList)
		{
			cb_InputDateFormat.addItem(c);
		}
		GridBagConstraints gbc_cb_DateFormat = new GridBagConstraints();
		gbc_cb_DateFormat.insets = new Insets(0, 0, 5, 5);
		gbc_cb_DateFormat.fill = GridBagConstraints.HORIZONTAL;
		gbc_cb_DateFormat.gridx = 4;
		gbc_cb_DateFormat.gridy = 10;
		panel_1.add(cb_InputDateFormat, gbc_cb_DateFormat);

		lbl_MeterMongoServer = new JLabel("Meter Mongo Server");
		GridBagConstraints gbc_lblMeterMongoServer = new GridBagConstraints();
		gbc_lblMeterMongoServer.anchor = GridBagConstraints.EAST;
		gbc_lblMeterMongoServer.insets = new Insets(0, 0, 5, 5);
		//	gbc_label_3.gridwidth = 5;
		gbc_lblMeterMongoServer.gridx = 1;
		gbc_lblMeterMongoServer.gridy = 11;
		panel_1.add(lbl_MeterMongoServer, gbc_lblMeterMongoServer);

		tf_MeterMongoServer = new JTextField(PrefsNightScoutLoader.getInstance().getM_MongoMeterServer());
		tf_MeterMongoServer.setToolTipText("Used for Development only.  Simulates SQL Server results in a MongoDB when Use Mongo for Roche is enabled and data loaded from Roche Combo.");
		GridBagConstraints gbc_tf_MeterMongoServer = new GridBagConstraints();
		gbc_tf_MeterMongoServer.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_MeterMongoServer.anchor = GridBagConstraints.EAST;
		gbc_tf_MeterMongoServer.insets = new Insets(0, 0, 5, 5);
		gbc_tf_MeterMongoServer.gridwidth = 4;
		gbc_tf_MeterMongoServer.gridx = 2;
		gbc_tf_MeterMongoServer.gridy = 11;
		panel_1.add(tf_MeterMongoServer, gbc_tf_MeterMongoServer);
		tf_MeterMongoServer.setColumns(10);

		lbl_MeterMongoDB = new JLabel("Meter Mongo DB");
		GridBagConstraints gbc_label_4 = new GridBagConstraints();
		gbc_label_4.anchor = GridBagConstraints.EAST;
		gbc_label_4.insets = new Insets(0, 0, 5, 5);
		//	gbc_label_4.gridwidth = 4;
		gbc_label_4.gridx = 1;
		gbc_label_4.gridy = 12;
		panel_1.add(lbl_MeterMongoDB, gbc_label_4);

		tf_MeterMongoDB = new JTextField(PrefsNightScoutLoader.getInstance().getM_MongoMeterDB());
		tf_MeterMongoDB.setToolTipText("Used for Development only.  Simulates SQL Server results in a MongoDB when Use Mongo for Roche is enabled and data loaded from Roche Combo.");
		GridBagConstraints gbc_tf_MeterMongoDB = new GridBagConstraints();
		gbc_tf_MeterMongoDB.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_MeterMongoDB.anchor = GridBagConstraints.EAST;
		gbc_tf_MeterMongoDB.insets = new Insets(0, 0, 5, 5);
		gbc_tf_MeterMongoDB.gridwidth = 4;
		gbc_tf_MeterMongoDB.gridx = 2;
		gbc_tf_MeterMongoDB.gridy = 12;
		panel_1.add(tf_MeterMongoDB, gbc_tf_MeterMongoDB);
		tf_MeterMongoDB.setColumns(10);

		lbl_MeterMongoCollection = new JLabel("Meter Mongo Collection");
		GridBagConstraints gbc_label_5 = new GridBagConstraints();
		gbc_label_5.anchor = GridBagConstraints.EAST;
		gbc_label_5.insets = new Insets(0, 0, 5, 5);
		//	gbc_label_5.gridwidth = 6;
		gbc_label_5.gridx = 1;
		gbc_label_5.gridy = 13;
		panel_1.add(lbl_MeterMongoCollection, gbc_label_5);

		tf_MeterMongoCollection = new JTextField(PrefsNightScoutLoader.getInstance().getM_MongoMeterCollection());
		tf_MeterMongoCollection.setToolTipText("Used for Development only.  Simulates SQL Server results in a MongoDB when Use Mongo for Roche is enabled and data loaded from Roche Combo.");
		GridBagConstraints gbc_tf_MeterMongoCollection = new GridBagConstraints();
		gbc_tf_MeterMongoCollection.insets = new Insets(0, 0, 5, 5);
		gbc_tf_MeterMongoCollection.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_MeterMongoCollection.anchor = GridBagConstraints.EAST;
		gbc_tf_MeterMongoCollection.gridwidth = 4;
		gbc_tf_MeterMongoCollection.gridx = 2;
		gbc_tf_MeterMongoCollection.gridy = 13;
		panel_1.add(tf_MeterMongoCollection, gbc_tf_MeterMongoCollection);
		tf_MeterMongoCollection.setColumns(25);

		lbl_LogLevel = new JLabel("Log Level");
		GridBagConstraints gbc_label_6 = new GridBagConstraints();
		gbc_label_6.anchor = GridBagConstraints.EAST;
		gbc_label_6.insets = new Insets(0, 0, 5, 5);
		//	gbc_label_6.gridwidth = 6;
		gbc_label_6.gridx = 1;
		gbc_label_6.gridy = 14;
		panel_1.add(lbl_LogLevel, gbc_label_6);

		cb_LogLevel = new JComboBox<String>();
		cb_LogLevel.setToolTipText("Logging is enabled througout the application.  Values are Regular, Detailed, More Detailed, Most Detailed to enable greater insight in the event of issues with the application.");
		cb_LogLevel.setModel(new DefaultComboBoxModel<String>(new String[] {"Regular", "Detailed", "More Detailed", "Most Detailed"}));
		GridBagConstraints gbc_cb_LogLevel = new GridBagConstraints();
		gbc_cb_LogLevel.gridwidth = 2;
		gbc_cb_LogLevel.insets = new Insets(0, 0, 5, 5);
		gbc_cb_LogLevel.fill = GridBagConstraints.HORIZONTAL;
		gbc_cb_LogLevel.gridx = 2;
		gbc_cb_LogLevel.gridy = 14;
		panel_1.add(cb_LogLevel, gbc_cb_LogLevel);

		lbl_LogFile = new JLabel("Log File");
		GridBagConstraints gbc_lblLogFile = new GridBagConstraints();
		gbc_lblLogFile.anchor = GridBagConstraints.EAST;
		gbc_lblLogFile.insets = new Insets(0, 0, 5, 5);
		//gbc_lblLogFile.gridwidth = 2;
		gbc_lblLogFile.gridx = 1;
		gbc_lblLogFile.gridy = 15;
		panel_1.add(lbl_LogFile, gbc_lblLogFile);

		tf_LogFile = new JTextField(PrefsNightScoutLoader.getInstance().getM_LogFile());
		tf_LogFile.setToolTipText("All messages are stored in the log file (depending on the Log Level set).  Only regular messages are displayed in the panel at the bottom of the main window.");
		GridBagConstraints gbc_tf_LogFile = new GridBagConstraints();
		gbc_tf_LogFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_LogFile.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_LogFile.insets = new Insets(0, 0, 5, 5);
		gbc_tf_LogFile.gridwidth = 4;
		gbc_tf_LogFile.gridx = 2;
		gbc_tf_LogFile.gridy = 15;
		panel_1.add(tf_LogFile, gbc_tf_LogFile);
		tf_LogFile.setPreferredSize(new Dimension(7, 20));
		tf_LogFile.setColumns(25);

		btn_SelectLogFile = new JButton("Select");
		btn_SelectLogFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"Log Files", "txt");
				chooser.setFileFilter(filter);
				File selectedFile = new File(tf_LogFile.getText());
				chooser.setSelectedFile(selectedFile);
				int returnVal = chooser.showSaveDialog(getContentPane());        	    		
				if(returnVal == JFileChooser.APPROVE_OPTION) 
				{
					tf_LogFile.setText(chooser.getSelectedFile().getAbsolutePath()); 
					m_Logger.log( Level.INFO, "You chose to open this file for logging: " +
							chooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		GridBagConstraints gbc_btnSelectLogFile = new GridBagConstraints();
		gbc_btnSelectLogFile.insets = new Insets(0, 0, 5, 5);
		gbc_btnSelectLogFile.gridx = 6;
		gbc_btnSelectLogFile.gridy = 15;
		panel_1.add(btn_SelectLogFile, gbc_btnSelectLogFile);

		lbl_MaxMinsForMealBolus = new JLabel("Max Mins Meal Bolus");
		GridBagConstraints gbc_label_7 = new GridBagConstraints();
		gbc_label_7.anchor = GridBagConstraints.EAST;
		gbc_label_7.insets = new Insets(0, 0, 5, 5);
		//	gbc_label_7.gridwidth = 6;
		gbc_label_7.gridx = 1;
		gbc_label_7.gridy = 16;
		panel_1.add(lbl_MaxMinsForMealBolus, gbc_label_7);

		sp_MaxMinsForMealBolus = new JSpinner();
		sp_MaxMinsForMealBolus.setToolTipText("How many minutes apart the three components of Meal Bolus can be apart - a BG test, Carbs and Insulin.");
		sp_MaxMinsForMealBolus.setModel(new SpinnerNumberModel(10, 1, 90, 1));
		GridBagConstraints gbc_sp_MaxMinsForMealBolus = new GridBagConstraints();
		gbc_sp_MaxMinsForMealBolus.insets = new Insets(0, 0, 5, 5);
		gbc_sp_MaxMinsForMealBolus.gridx = 2;
		gbc_sp_MaxMinsForMealBolus.gridy = 16;
		panel_1.add(sp_MaxMinsForMealBolus, gbc_sp_MaxMinsForMealBolus);
		
		lblProximityMinutes = new JLabel("Mins Dupe Check");
		GridBagConstraints gbc_lblProximityMinutes = new GridBagConstraints();
		gbc_lblProximityMinutes.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblProximityMinutes.insets = new Insets(0, 0, 5, 5);
		gbc_lblProximityMinutes.gridx = 3;
		gbc_lblProximityMinutes.gridy = 16;
		panel_1.add(lblProximityMinutes, gbc_lblProximityMinutes);
		
		sp_ProximityMinutes = new JSpinner();
		sp_ProximityMinutes.setModel(new SpinnerNumberModel(5, 1, 60, 1));
		sp_ProximityMinutes.setToolTipText("For duplicate detection.  How many minutes apart manual Care Portal entries can be from loaded meter readings before being marked as proximity.");
		sp_ProximityMinutes.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_ProximityMinutes());
		GridBagConstraints gbc_sp_ProximityMinutes = new GridBagConstraints();
		gbc_sp_ProximityMinutes.insets = new Insets(0, 0, 5, 5);
		gbc_sp_ProximityMinutes.gridx = 4;
		gbc_sp_ProximityMinutes.gridy = 16;
		panel_1.add(sp_ProximityMinutes, gbc_sp_ProximityMinutes);
		
		rbProximityTypeCheck = new JRadioButton("Dupe Type Check");
		rbProximityTypeCheck.setSelected(PrefsNightScoutLoader.getInstance().isM_ProximityTypeCheck());
		GridBagConstraints gbc_rbProximityTypeCheck = new GridBagConstraints();
		gbc_rbProximityTypeCheck.insets = new Insets(0, 0, 5, 5);
		gbc_rbProximityTypeCheck.gridx = 6;
		gbc_rbProximityTypeCheck.gridy = 16;
		panel_1.add(rbProximityTypeCheck, gbc_rbProximityTypeCheck);

		lbl_MaxMinsForCorrectionBolus = new JLabel("Max Mins Correction Bolus");
		GridBagConstraints gbc_label_8 = new GridBagConstraints();
		gbc_label_8.anchor = GridBagConstraints.EAST;
		gbc_label_8.insets = new Insets(0, 0, 0, 5);
		//	gbc_label_8.gridwidth = 6;
		gbc_label_8.gridx = 1;
		gbc_label_8.gridy = 17;
		panel_1.add(lbl_MaxMinsForCorrectionBolus, gbc_label_8);

		sp_MaxMinsForCorrectionBolus = new JSpinner();
		sp_MaxMinsForCorrectionBolus.setModel(new SpinnerNumberModel(5, 1, 90, 1));
		sp_MaxMinsForCorrectionBolus.setToolTipText("How many minutes apart a BG test can be from an Insulin Correction.");
		sp_MaxMinsForCorrectionBolus.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_MaxMinsBetweenSameCorrectionEvent());

		GridBagConstraints gbc_sp_MaxMinsForCorrectionBolus = new GridBagConstraints();
		gbc_sp_MaxMinsForCorrectionBolus.insets = new Insets(0, 0, 0, 5);
		gbc_sp_MaxMinsForCorrectionBolus.gridx = 2;
		gbc_sp_MaxMinsForCorrectionBolus.gridy = 17;
		panel_1.add(sp_MaxMinsForCorrectionBolus, gbc_sp_MaxMinsForCorrectionBolus);
		
		lblMongoCheckMinutes = new JLabel("Mins Mongo Check");
		lblMongoCheckMinutes.setEnabled(true);
		GridBagConstraints gbc_lblMongoCheckMinutes = new GridBagConstraints();
		gbc_lblMongoCheckMinutes.insets = new Insets(0, 0, 0, 5);
		gbc_lblMongoCheckMinutes.gridx = 3;
		gbc_lblMongoCheckMinutes.gridy = 17;
		panel_1.add(lblMongoCheckMinutes, gbc_lblMongoCheckMinutes);
		
		sp_MongoDBAlertMinutes = new JSpinner();
		sp_MongoDBAlertMinutes.setModel(new SpinnerNumberModel(5, 1, 60, 1));
		sp_MongoDBAlertMinutes.setToolTipText("Number of minutes between background MongoDB update check alerts.  0 turns feature off.");
		sp_MongoDBAlertMinutes.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_MongoDBAlerterCheckInterval());
		sp_MongoDBAlertMinutes.setEnabled(true);
		GridBagConstraints gbc_sp_MongoDBAlertMinutes = new GridBagConstraints();
		gbc_sp_MongoDBAlertMinutes.insets = new Insets(0, 0, 0, 5);
		gbc_sp_MongoDBAlertMinutes.gridx = 4;
		gbc_sp_MongoDBAlertMinutes.gridy = 17;
		panel_1.add(sp_MongoDBAlertMinutes, gbc_sp_MongoDBAlertMinutes);
		
		cbProximityValueCheck = new JComboBox();
		cbProximityValueCheck.setModel(new DefaultComboBoxModel(new String[] {"Don't check values", "Check BG Value", "Check Carb Value", "Check Insulin Value"}));
		GridBagConstraints gbc_cbProximityValueCheck = new GridBagConstraints();
		gbc_cbProximityValueCheck.insets = new Insets(0, 0, 0, 5);
		gbc_cbProximityValueCheck.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbProximityValueCheck.gridx = 6;
		gbc_cbProximityValueCheck.gridy = 17;
		panel_1.add(cbProximityValueCheck, gbc_cbProximityValueCheck);



		//		private JTextField tf_LogLevel;
		//		private JTextField tf_LogFile;


		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.SOUTH;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		getContentPane().add(panel, gbc_panel);

		JButton button = new JButton("Ok");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveOptions();
				dispose();
			}
		});
		panel.add(button);

		JButton button_1 = new JButton("Cancel");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		panel.add(button_1);

		JButton button_2 = new JButton("Reset to Defaults");
		panel.add(button_2);
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				restoreOptions();
			}
		});

		toggleAdvancedOptions(PrefsNightScoutLoader.getInstance().isM_AdvancedOptions());

		// Set values based on the restore method
		loadOptions();
	}

	private void toggleAdvancedOptions(boolean advancedOptions)
	{
//		tf_MeterMongoServer.setVisible(advancedOptions);
//		tf_MeterMongoDB.setVisible(advancedOptions);
//		tf_MeterMongoCollection.setVisible(advancedOptions);
//		tf_LogFile.setVisible(advancedOptions);
//		sp_MaxMinsForCorrectionBolus.setVisible(advancedOptions);
//		sp_MaxMinsForMealBolus.setVisible(advancedOptions);
//		cb_Timezone.setVisible(advancedOptions);
//		cb_InputDateFormat.setVisible(advancedOptions);
//
//
//		rb_UseMongoForRoche.setVisible(advancedOptions);
//		lbl_MeterMongoServer.setVisible(advancedOptions);
//		lbl_MeterMongoDB.setVisible(advancedOptions);
//		lbl_MeterMongoCollection.setVisible(advancedOptions);
//		lbl_LogFile.setVisible(advancedOptions);
//		lbl_LogLevel.setVisible(advancedOptions);
//		cb_LogLevel.setVisible(advancedOptions);
//		lbl_MaxMinsForMealBolus.setVisible(advancedOptions);
//		lbl_MaxMinsForCorrectionBolus.setVisible(advancedOptions);
//		lblDateFormat.setVisible(advancedOptions);
//		lblTimezone.setVisible(advancedOptions);
//
//		btn_SelectLogFile.setVisible(advancedOptions);
		
		tf_MeterMongoServer.setEnabled(advancedOptions);
		tf_MeterMongoDB.setEnabled(advancedOptions);
		tf_MeterMongoCollection.setEnabled(advancedOptions);
		tf_LogFile.setEnabled(advancedOptions);
		sp_MaxMinsForCorrectionBolus.setEnabled(advancedOptions);
		sp_MaxMinsForMealBolus.setEnabled(advancedOptions);
		cb_Timezone.setEnabled(advancedOptions);
		cb_InputDateFormat.setEnabled(advancedOptions);


		rb_UseMongoForRoche.setEnabled(advancedOptions);
		lbl_MeterMongoServer.setEnabled(advancedOptions);
		lbl_MeterMongoDB.setEnabled(advancedOptions);
		lbl_MeterMongoCollection.setEnabled(advancedOptions);
		lbl_LogFile.setEnabled(advancedOptions);
		lbl_LogLevel.setEnabled(advancedOptions);
		cb_LogLevel.setEnabled(advancedOptions);
		lbl_MaxMinsForMealBolus.setEnabled(advancedOptions);
		lbl_MaxMinsForCorrectionBolus.setEnabled(advancedOptions);
		lblDateFormat.setEnabled(advancedOptions);
		lblTimezone.setEnabled(advancedOptions);
		
		sp_ProximityMinutes.setEnabled(advancedOptions);
		lblProximityMinutes.setEnabled(advancedOptions);
		cbProximityValueCheck.setEnabled(advancedOptions);
		rbProximityTypeCheck.setEnabled(advancedOptions);
		
		sp_MongoDBAlertMinutes.setEnabled(advancedOptions);
		lblMongoCheckMinutes.setEnabled(advancedOptions);

		btn_SelectLogFile.setEnabled(advancedOptions);

	}

	public void restoreOptions()
	{
		PrefsNightScoutLoader.getInstance().loadDefaultPreferences();
		loadOptions();
	}

	public void loadOptions()
	{
		cb_BGUnitList.setSelectedIndex(PrefsNightScoutLoader.getInstance().getM_BGUnits());
		tf_SQLFile.setText(PrefsNightScoutLoader.getInstance().getM_SQLFile());
		tf_DBServer.setText(PrefsNightScoutLoader.getInstance().getM_SQLDBServerHost());
		tf_DBInstance.setText(PrefsNightScoutLoader.getInstance().getM_SQLDBServerInstance());
		tf_DBName.setText(PrefsNightScoutLoader.getInstance().getM_SQLDBName());
		tf_NightscoutServer.setText(PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer());
		tf_NightscoutDB.setText(PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB());
		tf_NightscoutCollection.setText(PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection());
		tf_MeterMongoServer.setText(PrefsNightScoutLoader.getInstance().getM_MongoMeterServer());
		tf_MeterMongoDB.setText(PrefsNightScoutLoader.getInstance().getM_MongoMeterDB());
		tf_MeterMongoCollection.setText(PrefsNightScoutLoader.getInstance().getM_MongoMeterCollection());
		//		tf_LogLevel.setText(String.format("%d", PrefsNightScoutLoader.getInstance().getM_LogLevel()));
		cb_LogLevel.setSelectedIndex(PrefsNightScoutLoader.getInstance().getM_LogLevel());
		tf_LogFile.setText(PrefsNightScoutLoader.getInstance().getM_LogFile());
		sp_MaxMinsForCorrectionBolus.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_MaxMinsBetweenSameCorrectionEvent());
		sp_ProximityMinutes.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_ProximityMinutes());
		sp_DaysToLoad.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_DaysToLoad());
		sp_MaxMinsForMealBolus.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_MaxMinsBetweenSameMealEvent());
		sp_MongoDBAlertMinutes.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_MongoDBAlerterCheckInterval());
		
		String prefTimezone = PrefsNightScoutLoader.getInstance().getM_Timezone();		
		// Odd, no output from logger
		// APp fails to launch when setting vale.
		// Wed had the full list of timezones,  Thu 23 changed to GMT +1, +2 etc and still issues.

		//	sp_Timezone.getModel().setValue((String)prefTimezone);
		cb_Timezone.setSelectedIndex(getTimeZoneIndex(prefTimezone));

		String prefInputDateFormat = PrefsNightScoutLoader.getInstance().getM_InputDateFormat();
		cb_InputDateFormat.setSelectedIndex(getInputDateIndex(prefInputDateFormat));
		
		sp_ProximityMinutes.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_ProximityMinutes());
		cbProximityValueCheck.setSelectedIndex(PrefsNightScoutLoader.getInstance().getM_ProximityValueCheck());
		rbProximityTypeCheck.setSelected(PrefsNightScoutLoader.getInstance().isM_ProximityTypeCheck());
	}

	private int getTimeZoneIndex(String timezone)
	{
		int result = -1;
		int x = 0;
		for (String c : m_TimezoneList)
		{
			if (c.equals(timezone))
			{
				result = x;
			}
			x++;
		}
		return result;
	}

	private int getInputDateIndex(String inputDateFormat)
	{
		int result = -1;
		int x = 0;
		for (String c : m_InputDateFormatList)
		{
			if (c.equals(inputDateFormat))
			{
				result = x;
			}
			x++;
		}
		return result;
	}

	public void saveOptions()
	{
		boolean bgUnitsChanging = PrefsNightScoutLoader.getInstance().getM_BGUnits() == cb_BGUnitList.getSelectedIndex() ? false : true;

		// Check if Mongo Server is changing.  If so, then reset the test count
		String currentMongoServer = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer();
		String newMongoServer = tf_NightscoutServer.getText();
		if (!currentMongoServer.equals(newMongoServer))
		{
			m_Logger.log( Level.INFO, "Resetting Nightscout server from '" + currentMongoServer +
					"' to '" + newMongoServer + "' so will attempt connection.");
			DataLoadNightScout.resetFailedTests();
		}

		PrefsNightScoutLoader.getInstance().setM_BGUnits(cb_BGUnitList.getSelectedIndex());
		PrefsNightScoutLoader.getInstance().setM_DaysToLoad((int)sp_DaysToLoad.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_SQLFile(tf_SQLFile.getText());
		PrefsNightScoutLoader.getInstance().setM_SQLDBServerHost(tf_DBServer.getText());
		PrefsNightScoutLoader.getInstance().setM_SQLDBServerInstance(tf_DBInstance.getText());
		PrefsNightScoutLoader.getInstance().setM_SQLDBName(tf_DBName.getText());
		PrefsNightScoutLoader.getInstance().setM_NightscoutMongoServer(tf_NightscoutServer.getText());
		//		NightLoaderPreferences.getInstance().setM_NightscoutMongoPort(Integer.parseInt(tf_NightscoutPort.getText()));
		PrefsNightScoutLoader.getInstance().setM_NightscoutMongoDB(tf_NightscoutDB.getText());
		PrefsNightScoutLoader.getInstance().setM_NightscoutMongoCollection(tf_NightscoutCollection.getText());
		PrefsNightScoutLoader.getInstance().setM_MongoMeterServer(tf_MeterMongoServer.getText());
		PrefsNightScoutLoader.getInstance().setM_MongoMeterDB(tf_MeterMongoDB.getText());
		PrefsNightScoutLoader.getInstance().setM_MongoMeterCollection(tf_MeterMongoCollection.getText());
		//PrefsNightScoutLoader.getInstance().setM_LogLevel(Integer.parseInt(tf_LogLevel.getText()));
		PrefsNightScoutLoader.getInstance().setM_LogLevel(cb_LogLevel.getSelectedIndex());
		PrefsNightScoutLoader.getInstance().setM_LogFile(tf_LogFile.getText());
		PrefsNightScoutLoader.getInstance().setM_MaxMinsBetweenSameCorrectionEvent((int)sp_MaxMinsForCorrectionBolus.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_MaxMinsBetweenSameMealEvent((int)sp_MaxMinsForMealBolus.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_ProximityMinutes((int)sp_ProximityMinutes.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_ProximityValueCheck(cbProximityValueCheck.getSelectedIndex());
		PrefsNightScoutLoader.getInstance().setM_ProximityTypeCheck(rbProximityTypeCheck.isSelected());
		
		PrefsNightScoutLoader.getInstance().setM_MongoDBAlerterCheckInterval((int)sp_MongoDBAlertMinutes.getModel().getValue());
		
		
		//		PrefsNightScoutLoader.getInstance().setM_InputDateFormat((String)sp_InputDateFormat.getModel().getValue());
		//		PrefsNightScoutLoader.getInstance().setM_Timezone((String)sp_Timezone.getModel().getValue());

		boolean timezoneChanged = false;
		int selectedTimezone = cb_Timezone.getSelectedIndex();
		String currTimezone = PrefsNightScoutLoader.getInstance().getM_Timezone();
		if (selectedTimezone <= m_TimezoneList.length)
		{
			timezoneChanged = m_TimezoneList[selectedTimezone].equals(currTimezone) ? false : true;
			PrefsNightScoutLoader.getInstance().setM_Timezone(m_TimezoneList[selectedTimezone]);
		}		

		int selectedInputDate = cb_InputDateFormat.getSelectedIndex();
		if (selectedInputDate <= m_InputDateFormatList.length)
		{
			PrefsNightScoutLoader.getInstance().setM_InputDateFormat(m_InputDateFormatList[selectedInputDate]);
		}

		PrefsNightScoutLoader.getInstance().setPreferences();

		if (bgUnitsChanging)
		{
			m_WinMain.bgUnitsChanged();
		}

		if (timezoneChanged)
		{
			m_Logger.info("Reload Nightscout results since Timezone changed manually from: " + currTimezone + " to: " + m_TimezoneList[selectedTimezone]);
			// Set timezone on main screen and re-load nightscout
			m_WinMain.checkTimeZone();
			m_WinMain.doLoadNightScout(false);
		}
	}

	private void testSQLServer()
	{
		String result = new String();
		String message = new String("**************************************************\n");
		message                  += "* Testing connectivity to MS SQLServer for Roche *\n";
		message                  += "**************************************************\n\n";

		// Popup a new window with output from tests
		try
		{
			if (tf_DBName.getText().length() > 0)
			{
				result = m_SQLServerLoader.testDBConnection(tf_DBServer.getText(), 
						tf_DBInstance.getText(),
						tf_DBName.getText());
				message += result;

				final JDialog dialog = new JDialog();
				dialog.setSize(700, 600);

				JTextArea textArea = new JTextArea(message);
				textArea.setFont(new Font("Courier", Font.PLAIN, 14));
				JScrollPane scrollPane = new JScrollPane(textArea);
				scrollPane.setBounds(10,60,780,500);

				dialog.setModal(true);
				dialog.getContentPane().add(scrollPane);
				dialog.setVisible(true);
			}
			else
			{
				result = m_SQLServerLoader.testDBConnection(tf_DBServer.getText(),
						tf_DBInstance.getText());
				message += result;

				final JDialog dialog = new JDialog();
				dialog.setSize(700, 600);

				JTextArea textArea = new JTextArea(message);
				textArea.setFont(new Font("Courier", Font.PLAIN, 14));
				JScrollPane scrollPane = new JScrollPane(textArea);
				scrollPane.setBounds(10,60,780,500);

				dialog.setModal(true);
				dialog.getContentPane().add(scrollPane);
				dialog.setVisible(true);
			}
		}
		catch (Exception e)
		{
			result = "Test SQL Server Failed.\n" + "Unable to connect to SQL Server\n\n"  + 
					"Server(" + tf_DBServer.getText() + ") " +
					"DB Instance(" + tf_DBInstance.getText()  + ") " +
					"DB Name(" + tf_DBName.getText() + ") " +
					"Received the following error message:\n\n" +
					e.getMessage() + "-" + e.getLocalizedMessage();
			message += result;

			m_Logger.log(Level.INFO, "Test SQL Server: " + message);
			final JDialog dialog = new JDialog();
			dialog.setSize(700, 600);

			JTextArea textArea = new JTextArea(message);
			textArea.setFont(new Font("Courier", Font.PLAIN, 14));
			JScrollPane scrollPane = new JScrollPane(textArea);
			scrollPane.setBounds(10,60,780,500);

			dialog.setModal(true);
			dialog.getContentPane().add(scrollPane);
			dialog.setVisible(true);
		}

	}

	private void testMongo()
	{
		// Popup a new window with output from tests
		String result = new String();
		String message = new String("**************************************************\n");
		message                  += "* Testing connectivity to MongoDB for Nightscout *\n";
		message                  += "**************************************************\n\n";
		// Popup a new window with output from tests
		try
		{
			if (tf_NightscoutDB.getText().length() > 0)
			{
				result = m_MongoDBLoader.testDBConnection(tf_NightscoutServer.getText(), 
						tf_NightscoutDB.getText());
				message += result;

				final JDialog dialog = new JDialog();
				dialog.setSize(700, 600);

				JTextArea textArea = new JTextArea(message);
				textArea.setFont(new Font("Courier", Font.PLAIN, 14));
				JScrollPane scrollPane = new JScrollPane(textArea);
				scrollPane.setBounds(10,60,780,500);

				dialog.setModal(true);
				dialog.getContentPane().add(scrollPane);
				dialog.setVisible(true);
			}
			else
			{
				result = m_MongoDBLoader.testDBConnection(tf_NightscoutServer.getText());
				message += result;

				final JDialog dialog = new JDialog();
				dialog.setSize(700, 600);

				JTextArea textArea = new JTextArea(message);
				textArea.setFont(new Font("Courier", Font.PLAIN, 14));
				JScrollPane scrollPane = new JScrollPane(textArea);
				scrollPane.setBounds(10,60,780,500);

				dialog.setModal(true);
				dialog.getContentPane().add(scrollPane);
				dialog.setVisible(true);
			}
		}
		catch (Exception e)
		{
			result = "Test Nightscout Failed.\n" + "Unable to connect to MongoDB\n\n"  + 
					"Nightscout Server(" + tf_NightscoutServer.getText() + ") " +
					"DB Instance(" + tf_NightscoutDB.getText()  + ") " +
					"Received the following error message:\n\n" +
					e.getMessage() + "-" + e.getLocalizedMessage();
			message += result;
			m_Logger.log(Level.INFO, "Test Mongo Connection: " + message);

			final JDialog dialog = new JDialog();
			dialog.setSize(700, 600);

			JTextArea textArea = new JTextArea(message);
			textArea.setFont(new Font("Courier", Font.PLAIN, 14));
			JScrollPane scrollPane = new JScrollPane(textArea);
			scrollPane.setBounds(10,60,780,500);

			dialog.setModal(true);
			dialog.getContentPane().add(scrollPane);
			dialog.setVisible(true);
		}

	}
}
