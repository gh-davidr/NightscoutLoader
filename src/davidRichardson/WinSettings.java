package davidRichardson;

import javax.swing.JButton;
import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.awt.Color;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class WinSettings extends JDialog 
//public class WinSettings extends JFrame   -- Interesting.  Doesn't launch when a JFrame
{

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
	private JTextField tf_RocheSQLFile;
	private JTextField tf_RocheDBServer;
	private JTextField tf_RocheDBInstance;
	private JTextField tf_RocheDBName;
	private JTextField tf_MongoNightscoutServer;
	private JTextField tf_MongoNightscoutDB;
	private JTextField tf_MongoNightscoutCollection;
	private JTextField tf_MeterMongoServer;
	private JTextField tf_MeterMongoDB;
	private JTextField tf_MeterMongoCollection;
	private JRadioButton rb_UseMongoForRoche;
	private JRadioButton rb_DiasendTempBasals;
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
	private DataLoadNightScoutTreatments   m_MongoDBLoader;

	// Our own private logger
	private JComboBox<String> cb_LogLevel;
	private JComboBox<String> cb_MongoBGUnitList;

	private JLabel lblMongoBGUnits;
	private JSpinner sp_RocheDaysToLoad;
	private JSpinner sp_MaxMinsForMealBolus;
	private JSpinner sp_MaxMinsForCorrectionBolus;
	private JLabel lblDateFormat;
	private JLabel lblTimezone;
	private JComboBox<String> cb_Timezone;
	private JComboBox<String> cb_InputDateFormat;
	private JSpinner sp_ProximityMinutes;
	private JLabel lbl_MongoCheckMinutes;
	private JSpinner sp_MongoDBAlertMinutes;
	private JRadioButton rb_DupeBGCheck;
	private JRadioButton rb_DupeCarbCheck;
	private JRadioButton rb_DupeInsulinCheck;
	private JRadioButton rb_LoadEntries;
	private JSpinner sp_DupeBGDecPlace;
	private JSpinner sp_DupeCarbDecPlace;
	private JSpinner sp_DupeInsulinDecPlace;
	private JSpinner sp_WeeksBackToLoadEntries;
	private JComboBox<String> cb_DuplicateCheckType;

	/**
	 * Launch the application.
	 */
	/**
	 * @param args
	 */


	/**
	 * Create the dialog.
	 */
	public WinSettings(WinNightScoutLoader winMain, String title) 
	{
		super();
		getContentPane().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {

				// David 30 Sep 2016
				// Catch when set visible and call loadSettings
				// Problem is constructor is called before Prefs is initialized.
			}
		});

		m_WinMain = winMain;

		URL url = MainNightScoutLoader.class.getResource("/Nightscout.jpg");
		ImageIcon img = new ImageIcon(url);
		setIconImage(img.getImage());

		m_SQLServerLoader = new DataLoadRoche();
		m_MongoDBLoader   = new DataLoadNightScoutTreatments();

		super.setTitle(title);
		setBounds(100, 20, 790, 530);
		GridBagLayout gridBagLayout = new GridBagLayout();
		//		gridBagLayout.columnWidths = new int[] {650, 0};

		//		gridBagLayout.rowHeights = new int[] {300, 0, 0};
		//		gridBagLayout.rowHeights = new int[] {100, 100, 100};
		//		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		//		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0};
		getContentPane().setLayout(gridBagLayout);

		JPanel topPanel = new JPanel();
		topPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_topPanel = new GridBagConstraints();
		gbc_topPanel.fill = GridBagConstraints.BOTH;

		GridBagLayout gbl_topPanel = new GridBagLayout();
		gbl_topPanel.preferredLayoutSize(getParent());
		//	gbl_topPanel.columnWidths = new int[] {300,300};
		//	gbc_topPanel.insets = new Insets(0, 0, 5, 0);
		//	gbc_topPanel.gridwidth = 2;
		gbc_topPanel.gridx = 0;
		gbc_topPanel.gridy = 0;
		topPanel.setLayout(gbl_topPanel);

		getContentPane().add(topPanel, gbc_topPanel);

		//		JPanel centrePanel = new JPanel();
		GridBagConstraints gbc_centrePanel = new GridBagConstraints();
		final GridBagLayout gbl_centrePanel = new GridBagLayout();
		//gbl_centrePanel.preferredLayoutSize(getParent());

		//	gbc_centrePanel.insets = new Insets(0, 0, 5, 0);
		//	gbc_centrePanel.gridwidth = 1;
		gbc_centrePanel.gridx = 0;
		gbc_centrePanel.gridy = 1;

		// http://stackoverflow.com/questions/27097200/gridbaglayout-not-positioning-correctly
		//		gbc_centrePanel.weightx = 1;
		//		gbc_centrePanel.weighty = 1;
		//		gbc_centrePanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_centrePanel.fill = GridBagConstraints.BOTH;

		JPanel centrePanel = new JPanel(gbl_centrePanel)
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			// Useful trick I found to display grid lines
			// http://stackoverflow.com/questions/2444004/how-do-we-show-the-gridline-in-gridlayout
			@Override
			public void paint(Graphics g)
			{
				super.paint(g);
				/*	            int[][] dims = gbl_centrePanel.getLayoutDimensions();
	            g.setColor(Color.RED);
	            int x = 0;
	            for (int add : dims[0])
	            {
	                x += add;
	                g.drawLine(x, 0, x, getHeight());
	            }
	            int y = 0;
	            for (int add : dims[1])
	            {
	                y += add;
	                g.drawLine(0, y, getWidth(), y);
	            }
				 */	        }

		};
		centrePanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));

		getContentPane().add(centrePanel, gbc_centrePanel);

		/*		JPanel lowerPanel = new JPanel();
		lowerPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_lowerPanel = new GridBagConstraints();
		GridBagLayout gbl_lowerPanel = new GridBagLayout();
		gbl_lowerPanel.preferredLayoutSize(getParent());

	//	gbc_lowerPanel.insets = new Insets(0, 0, 5, 0);
		gbc_lowerPanel.gridx = 0;
		gbc_lowerPanel.gridy = 2;
		topPanel.setLayout(gbl_lowerPanel);

		getContentPane().add(lowerPanel, gbc_lowerPanel);
		 */

		JPanel mongoPanel = new JPanel();
		//		mongoPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_mongoPanel = new GridBagConstraints();
		//		gbc_mongoPanel.insets = new Insets(0, 0, 5, 0);
		gbc_mongoPanel.gridx = 0;
		gbc_mongoPanel.gridy = 0;
		//getContentPane().add(mongoPanel, gbc_mongoPanel);
		GridBagLayout gbl_mongoPanel = new GridBagLayout();
		//		gbl_mongoPanel.columnWidths = new int[] {50, 147, 30};
		//		gbl_mongoPanel.rowHeights = new int[] {30, 0, 0, 0, 0, 0, 0, 0, 30, 30, 0, 30, 0, 0, 0, 0, 0, 0, 0, 0};
		//		gbl_mongoPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 1.0, 1.0};
		//		gbl_mongoPanel.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		mongoPanel.setLayout(gbl_mongoPanel);

		//		rochePanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		GridBagConstraints gbc_rochePanel = new GridBagConstraints();
		//		GridBagConstraints gbc_rochePanelComponents = new GridBagConstraints();
		//		gbc_rochePanelComponents.insets = new Insets(5, 5, 5, 5);
		//gbc_rochePanel.insets = new Insets(0, 0, 5, 0);
		gbc_rochePanel.gridx = 0;
		gbc_rochePanel.gridy = 0;

		/*		// http://stackoverflow.com/questions/27097200/gridbaglayout-not-positioning-correctly
		gbc_rochePanel.weightx = 1;
		gbc_rochePanel.weighty = 1;
		gbc_rochePanel.fill = GridBagConstraints.HORIZONTAL;
		 */

		//getContentPane().add(rochePanel, gbc_rochePanel);
		final GridBagLayout gbl_rochePanel = new GridBagLayout();
		//		gbl_rochePanel.preferredLayoutSize(centrePanel);  No difference
		JPanel rochePanel = new JPanel(gbl_rochePanel)
		{
			// Useful trick I found to display grid lines
			// http://stackoverflow.com/questions/2444004/how-do-we-show-the-gridline-in-gridlayout
			@Override
			public void paint(Graphics g)
			{
				super.paint(g);
				/*		            int[][] dims = gbl_rochePanel.getLayoutDimensions();
		            g.setColor(Color.BLUE);
		            int x = 0;
		            for (int add : dims[0])
		            {
		                x += add;
		                g.drawLine(x, 0, x, getHeight());
		            }
		            int y = 0;
		            for (int add : dims[1])
		            {
		                y += add;
		                g.drawLine(0, y, getWidth(), y);
		            }
				 */		        }

		};
		//		    gbl_rochePanel.preferredLayoutSize(centrePanel);

		// 28 Nov 2016
		// Forcing column widths seems to be the trick needed
		//		    gbl_rochePanel.columnWidths = new int[] {10, 50, 10, 120, 20, 10};
		gbl_rochePanel.columnWidths = new int[] {175, 50, 50, 50,  50, 50, 60};

		//		gbl_rochePanel.columnWidths = new int[] {50, 147, 30};
		//		gbl_rochePanel.columnWidths = new int[] {10, 50, 10, 120, 20, 10};
		//		gbl_rochePanel.rowHeights = new int[] {30, 0, 0, 0, 0, 0, 0, 0, 30, 30, 0, 30, 0, 0, 0, 0, 0, 0, 0, 0};
		//		gbl_rochePanel.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 1.0, 1.0};
		//		gbl_rochePanel.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		//		rochePanel.setLayout(gbl_rochePanel);

		/*		JPanel advancedPanel = new JPanel();
		GridBagConstraints gbc_advancedPanel = new GridBagConstraints();
//		gbc_advancedPanel.insets = new Insets(0, 0, 5, 0);
		gbc_advancedPanel.gridx = 0;
		gbc_advancedPanel.gridy = 0;
		//getContentPane().add(advancedPanel, gbc_advancedPanel);
		GridBagLayout gbl_advancedPanel = new GridBagLayout();
//		gbl_advancedPanel.columnWidths = new int[] {50, 147, 30};
//		gbl_advancedPanel.rowHeights = new int[] {30, 0, 0, 0, 0, 0, 0, 0, 30, 30, 0, 30, 0, 0, 0, 0, 0, 0, 0, 0};
//		gbl_advancedPanel.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 1.0, 1.0};
//		gbl_advancedPanel.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		advancedPanel.setLayout(gbl_advancedPanel);
		 */		
		topPanel.add(mongoPanel);
		//	topPanel.add(rochePanel);
		//	centrePanel.add(rochePanel, gbc_rochePanel);
		centrePanel.add(rochePanel);
		//	centrePanel.add(mongoPanel);
		//topPanel.add(mongoPanel);
		//lowerPanel.add(rochePanel);


		JLabel lblMongoServer = new JLabel("Nightscout Server");
		lblMongoServer.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_MongoServer = new GridBagConstraints();
		gbc_MongoServer.anchor = GridBagConstraints.EAST;
		gbc_MongoServer.insets = new Insets(0, 0, 5, 5);
		//gbc_label.gridwidth = 3;
		//		gbc_MongoServer.gridx = 1;
		gbc_MongoServer.gridx = 0;
		gbc_MongoServer.gridy = 0;
		//panel_1.add(lblMongoServer, gbc_MongoServer);
		mongoPanel.add(lblMongoServer, gbc_MongoServer);

		tf_MongoNightscoutServer = new JTextField(PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer());
		tf_MongoNightscoutServer.setToolTipText("<html>Key Parameter to identify the Nightscout MongoDB server.  <br>Consult your MongoLabs DB setup for correct value. <br>Set this to an empty string to then force Nightscout Loader to run in stand-alone mode where it will not attempt a server connection.</html>");
		tf_MongoNightscoutServer.setBackground(Color.YELLOW);
		tf_MongoNightscoutServer.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_tf_MongoNightscoutServer = new GridBagConstraints();
		gbc_tf_MongoNightscoutServer.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_MongoNightscoutServer.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_MongoNightscoutServer.insets = new Insets(0, 0, 5, 5);
		gbc_tf_MongoNightscoutServer.gridwidth = 8;
		//		gbc_tf_MongoNightscoutServer.gridx = 2;
		gbc_tf_MongoNightscoutServer.gridx = 1;
		gbc_tf_MongoNightscoutServer.gridy = 0;
		//panel_1.add(tf_MongoNightscoutServer, gbc_tf_MongoNightscoutServer);
		mongoPanel.add(tf_MongoNightscoutServer, gbc_tf_MongoNightscoutServer);
		tf_MongoNightscoutServer.setColumns(10);

		lblMongoBGUnits = new JLabel("BG Units");
		lblMongoBGUnits.setBackground(Color.YELLOW);
		lblMongoBGUnits.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblMongoBGUnits;
		gbc_lblMongoBGUnits = new GridBagConstraints();
		gbc_lblMongoBGUnits.anchor = GridBagConstraints.EAST;
		gbc_lblMongoBGUnits.insets = new Insets(0, 0, 5, 5);
		gbc_lblMongoBGUnits.gridx = 0;
		gbc_lblMongoBGUnits.gridy = 1;
		//panel_1.add(lblMongoBGUnits, gbc_lblMongoBGUnits);
		mongoPanel.add(lblMongoBGUnits, gbc_lblMongoBGUnits);

		cb_MongoBGUnitList = new JComboBox<String>();
		cb_MongoBGUnitList.setModel(new DefaultComboBoxModel<String>(new String[] {"mmol/L", "mg/dL"}));
		cb_MongoBGUnitList.setToolTipText("Choose units of mmol/L (Europe) or mg/dL based on what the numbers you import actually mean.");
		cb_MongoBGUnitList.setBackground(Color.YELLOW);
		GridBagConstraints gbc_MongoBGUnitList = new GridBagConstraints();
		gbc_MongoBGUnitList.gridwidth = 1;
		gbc_MongoBGUnitList.insets = new Insets(0, 0, 5, 5);
		gbc_MongoBGUnitList.fill = GridBagConstraints.HORIZONTAL;
		gbc_MongoBGUnitList.gridx = 1;
		gbc_MongoBGUnitList.gridy = 1;
		//panel_1.add(cb_MongoBGUnitList, gbc_MongoBGUnitList);
		mongoPanel.add(cb_MongoBGUnitList, gbc_MongoBGUnitList);

		lblDateFormat = new JLabel("Date Format");
		GridBagConstraints gbc_lblDateFormat_1 = new GridBagConstraints();
		gbc_lblDateFormat_1.anchor = GridBagConstraints.EAST;
		gbc_lblDateFormat_1.insets = new Insets(0, 0, 5, 5);
		//		gbc_lblDateFormat_1.gridx = 3;
		//		gbc_lblDateFormat_1.gridy = 10;
		gbc_lblDateFormat_1.gridx = 2;
		gbc_lblDateFormat_1.gridy = 1;
		//		panel_1.add(lblDateFormat, gbc_lblDateFormat_1);
		//		advancedPanel.add(lblDateFormat, gbc_lblDateFormat_1);
		mongoPanel.add(lblDateFormat, gbc_lblDateFormat_1);

		cb_InputDateFormat = new JComboBox<String>();
		cb_InputDateFormat.setToolTipText("<html>Use to override inbuilt format for reading dates from files (Medtronic/Diasend).  <br>Useful in some cases where regional variations have been seen.  <br>Keep as Default for most cases.</html>");
		for (String c : m_InputDateFormatList)
		{
			cb_InputDateFormat.addItem(c);
		}
		GridBagConstraints gbc_cb_DateFormat = new GridBagConstraints();
		gbc_cb_DateFormat.insets = new Insets(0, 0, 5, 5);
		gbc_cb_DateFormat.fill = GridBagConstraints.HORIZONTAL;
		//		gbc_cb_DateFormat.gridx = 4;
		//		gbc_cb_DateFormat.gridy = 10;
		gbc_cb_DateFormat.gridx = 3;
		gbc_cb_DateFormat.gridy = 1;
		//		panel_1.add(cb_InputDateFormat, gbc_cb_DateFormat);
		//advancedPanel.add(cb_InputDateFormat, gbc_cb_DateFormat);
		mongoPanel.add(cb_InputDateFormat, gbc_cb_DateFormat);


		JLabel lblMongoDBLabel = new JLabel("Nightscout DB");
		GridBagConstraints gbc_MongoDBLabel = new GridBagConstraints();
		gbc_MongoDBLabel.anchor = GridBagConstraints.EAST;
		gbc_MongoDBLabel.insets = new Insets(0, 0, 5, 5);
		//gbc_label_1.gridwidth = 3;
		gbc_MongoDBLabel.gridx = 0;
		gbc_MongoDBLabel.gridy = 2;
		//panel_1.add(lblMongoDBLabel, gbc_MongoDBLabel);
		mongoPanel.add(lblMongoDBLabel, gbc_MongoDBLabel);

		tf_MongoNightscoutDB = new JTextField(PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB());
		tf_MongoNightscoutDB.setToolTipText("<html>Key Parameter to identify the Nightscout MongoDB.  <br>Consult your MongoLabs DB setup for correct value.");
		GridBagConstraints gbc_tf_MongoNightscoutDB = new GridBagConstraints();
		gbc_tf_MongoNightscoutDB.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_MongoNightscoutDB.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_MongoNightscoutDB.insets = new Insets(0, 0, 5, 5);
		gbc_tf_MongoNightscoutDB.gridwidth = 1;
		gbc_tf_MongoNightscoutDB.gridx = 1;
		gbc_tf_MongoNightscoutDB.gridy = 2;
		//panel_1.add(tf_MongoNightscoutDB, gbc_tf_MongoNightscoutDB);
		mongoPanel.add(tf_MongoNightscoutDB, gbc_tf_MongoNightscoutDB);
		tf_MongoNightscoutDB.setColumns(10);

		JButton btnMongoTest = new JButton("Test NS Connection");
		btnMongoTest.setToolTipText("Attempts a connection to Mongo based on parameters and displays results of attempt in separate window.");
		btnMongoTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				testMongo();
			}
		});
		GridBagConstraints gbc_btnMongoTest = new GridBagConstraints();
		gbc_btnMongoTest.insets = new Insets(0, 0, 5, 5);
		gbc_btnMongoTest.gridx = 4;
		gbc_btnMongoTest.gridy = 1;
		//panel_1.add(btnMongoTest, gbc_btnMongoTest);
		mongoPanel.add(btnMongoTest, gbc_btnMongoTest);

		JLabel lblMongoCollLabel = new JLabel("Nightscout Collection");
		GridBagConstraints gbc_lblMongoCollLabel = new GridBagConstraints();
		gbc_lblMongoCollLabel.anchor = GridBagConstraints.EAST;
		gbc_lblMongoCollLabel.insets = new Insets(0, 0, 5, 5);
		//gbc_label_2.gridwidth = 4;
		gbc_lblMongoCollLabel.gridx = 2;
		gbc_lblMongoCollLabel.gridy = 2;
		//panel_1.add(lblMongoCollLabel, gbc_lblMongoCollLabel);
		mongoPanel.add(lblMongoCollLabel, gbc_lblMongoCollLabel);

		tf_MongoNightscoutCollection = new JTextField(PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection());
		tf_MongoNightscoutCollection.setToolTipText("<html>Key Parameter to identify the Nightscout MongoDB collection for Care Portal entries.  <br>Almost always 'treatments', so no need to change.</html>");
		GridBagConstraints gbc_tf_MongoNightscoutCollection = new GridBagConstraints();
		gbc_tf_MongoNightscoutCollection.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_MongoNightscoutCollection.anchor = GridBagConstraints.EAST;
		gbc_tf_MongoNightscoutCollection.insets = new Insets(0, 0, 5, 5);
		gbc_tf_MongoNightscoutCollection.gridwidth  = 1;
		gbc_tf_MongoNightscoutCollection.gridx = 3;
		gbc_tf_MongoNightscoutCollection.gridy = 2;
		//panel_1.add(tf_MongoNightscoutCollection, gbc_tf_MongoNightscoutCollection);
		mongoPanel.add(tf_MongoNightscoutCollection, gbc_tf_MongoNightscoutCollection);
		tf_MongoNightscoutCollection.setColumns(25);

		lbl_MongoCheckMinutes = new JLabel("Mongo Heartbeat Mins");
		lbl_MongoCheckMinutes.setEnabled(true);
		GridBagConstraints gbc_lblMongoCheckMinutes = new GridBagConstraints();
		gbc_lblMongoCheckMinutes.insets = new Insets(0, 0, 5, 5);
		gbc_lblMongoCheckMinutes.gridx = 4;
		gbc_lblMongoCheckMinutes.gridy = 2;
		//panel_1.add(lbl_MongoCheckMinutes, gbc_lblMongoCheckMinutes);
		mongoPanel.add(lbl_MongoCheckMinutes, gbc_lblMongoCheckMinutes);

		sp_MongoDBAlertMinutes = new JSpinner();
		sp_MongoDBAlertMinutes.setModel(new SpinnerNumberModel(5, 1, 60, 1));
		sp_MongoDBAlertMinutes.setToolTipText("<html>Number of minutes between background MongoDB update check alerts.  <br>0 turns feature off.</html>");
		sp_MongoDBAlertMinutes.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_MongoDBAlerterCheckInterval());
		sp_MongoDBAlertMinutes.setEnabled(true);
		GridBagConstraints gbc_sp_MongoDBAlertMinutes = new GridBagConstraints();
		gbc_sp_MongoDBAlertMinutes.insets = new Insets(0, 0, 5, 5);
		gbc_sp_MongoDBAlertMinutes.gridx = 4;
		gbc_sp_MongoDBAlertMinutes.gridy = 3;
		//panel_1.add(sp_MongoDBAlertMinutes, gbc_sp_MongoDBAlertMinutes);
		mongoPanel.add(sp_MongoDBAlertMinutes, gbc_sp_MongoDBAlertMinutes);




		lbl_MaxMinsForMealBolus = new JLabel("Max Mins Meal Bolus");
		GridBagConstraints gbc_label_7 = new GridBagConstraints();
		gbc_label_7.anchor = GridBagConstraints.EAST;
		gbc_label_7.insets = new Insets(0, 0, 5, 5);
		//	gbc_label_7.gridwidth = 6;
		gbc_label_7.gridx = 0;
		gbc_label_7.gridy = 3;
		//panel_1.add(lbl_MaxMinsForMealBolus, gbc_label_7);
		mongoPanel.add(lbl_MaxMinsForMealBolus, gbc_label_7);

		sp_MaxMinsForMealBolus = new JSpinner();
		sp_MaxMinsForMealBolus.setToolTipText("<html>How many minutes apart the three components of Meal Bolus can be apart: <ul><li>a BG test</li><li>Carbs</li><li>Insulin</li></ul></html>");
		sp_MaxMinsForMealBolus.setModel(new SpinnerNumberModel(10, 1, 90, 1));
		GridBagConstraints gbc_sp_MaxMinsForMealBolus = new GridBagConstraints();
		gbc_sp_MaxMinsForMealBolus.insets = new Insets(0, 0, 5, 5);
		gbc_sp_MaxMinsForMealBolus.gridx = 1;
		gbc_sp_MaxMinsForMealBolus.gridy = 3;
		//panel_1.add(sp_MaxMinsForMealBolus, gbc_sp_MaxMinsForMealBolus);
		mongoPanel.add(sp_MaxMinsForMealBolus, gbc_sp_MaxMinsForMealBolus);



		lbl_MaxMinsForCorrectionBolus = new JLabel("Max Mins Correction Bolus");
		GridBagConstraints gbc_label_8 = new GridBagConstraints();
		gbc_label_8.anchor = GridBagConstraints.EAST;
		gbc_label_8.insets = new Insets(0, 0, 5, 5);
		//	gbc_label_8.gridwidth = 6;
		gbc_label_8.gridx = 2;
		gbc_label_8.gridy = 3;
		//panel_1.add(lbl_MaxMinsForCorrectionBolus, gbc_label_8);
		mongoPanel.add(lbl_MaxMinsForCorrectionBolus, gbc_label_8);

		sp_MaxMinsForCorrectionBolus = new JSpinner();
		sp_MaxMinsForCorrectionBolus.setModel(new SpinnerNumberModel(5, 1, 90, 1));
		sp_MaxMinsForCorrectionBolus.setToolTipText("How many minutes apart a BG test can be from an Insulin Correction.");
		sp_MaxMinsForCorrectionBolus.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_MaxMinsBetweenSameCorrectionEvent());

		GridBagConstraints gbc_sp_MaxMinsForCorrectionBolus = new GridBagConstraints();
		gbc_sp_MaxMinsForCorrectionBolus.insets = new Insets(0, 0, 5, 5);
		gbc_sp_MaxMinsForCorrectionBolus.gridx = 3;
		gbc_sp_MaxMinsForCorrectionBolus.gridy = 3;
		//panel_1.add(sp_MaxMinsForCorrectionBolus, gbc_sp_MaxMinsForCorrectionBolus);
		mongoPanel.add(sp_MaxMinsForCorrectionBolus, gbc_sp_MaxMinsForCorrectionBolus);		





		lbl_LogLevel = new JLabel("Log Level");
		GridBagConstraints gbc_label_6 = new GridBagConstraints();
		gbc_label_6.anchor = GridBagConstraints.EAST;
		gbc_label_6.insets = new Insets(0, 0, 5, 5);
		//	gbc_label_6.gridwidth = 6;
		//		gbc_label_6.gridx = 1;
		//		gbc_label_6.gridy = 14;
		gbc_label_6.gridx = 0;
		gbc_label_6.gridy = 4;
		//panel_1.add(lbl_LogLevel, gbc_label_6);
		//		advancedPanel.add(lbl_LogLevel, gbc_label_6);
		mongoPanel.add(lbl_LogLevel, gbc_label_6);

		cb_LogLevel = new JComboBox<String>();
		cb_LogLevel.setToolTipText("<html>Logging is enabled througout the application.  <br>Values are: <ul><li>Regular</li><li>Detailed</li><li>More Detailed</li><li>Most Detailed</li></ul>Set values to enable greater insight in the event of issues with the application.</html>");
		cb_LogLevel.setModel(new DefaultComboBoxModel<String>(new String[] {"Regular", "Detailed", "More Detailed", "Most Detailed"}));
		GridBagConstraints gbc_cb_LogLevel = new GridBagConstraints();
		gbc_cb_LogLevel.gridwidth = 1;
		gbc_cb_LogLevel.insets = new Insets(0, 0, 5, 5);
		gbc_cb_LogLevel.fill = GridBagConstraints.HORIZONTAL;
		//		gbc_cb_LogLevel.gridx = 2;
		//		gbc_cb_LogLevel.gridy = 14;
		gbc_cb_LogLevel.gridx = 1;
		gbc_cb_LogLevel.gridy = 4;
		//panel_1.add(cb_LogLevel, gbc_cb_LogLevel);
		//		advancedPanel.add(cb_LogLevel, gbc_cb_LogLevel);
		mongoPanel.add(cb_LogLevel, gbc_cb_LogLevel);

		JRadioButton rdbtnAdvancedSettings = new JRadioButton("Advanced Settings");
		rdbtnAdvancedSettings.setToolTipText("<html>Disabling Advanced Settings hides the complexity of the Analyze window parameters.  <br>It also hides all the features from Settings window below this selector.</html>");
		rdbtnAdvancedSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton aButton = (AbstractButton) actionEvent.getSource();
				PrefsNightScoutLoader.getInstance().setM_AdvancedSettings(aButton.isSelected());
				toggleAdvancedSettings(aButton.isSelected());
			}
		});
		rdbtnAdvancedSettings.setBackground(Color.YELLOW);
		GridBagConstraints gbc_rdbtnAdvancedSettings = new GridBagConstraints();
		gbc_rdbtnAdvancedSettings.anchor = GridBagConstraints.EAST;
		gbc_rdbtnAdvancedSettings.insets = new Insets(0, 0, 5, 5);
		// gbc_rdbtnAdvancedSettings.gridwidth = 5;
		gbc_rdbtnAdvancedSettings.gridx = 2;
		gbc_rdbtnAdvancedSettings.gridy = 4;
		//		panel_1.add(rdbtnAdvancedSettings, gbc_rdbtnAdvancedSettings);
		mongoPanel.add(rdbtnAdvancedSettings, gbc_rdbtnAdvancedSettings);
		rdbtnAdvancedSettings.setSelected(PrefsNightScoutLoader.getInstance().isM_AdvancedSettings());


		lblTimezone = new JLabel("Timezone");
		GridBagConstraints gbc_lblTimezone = new GridBagConstraints();
		gbc_lblTimezone.anchor = GridBagConstraints.EAST;
		gbc_lblTimezone.insets = new Insets(0, 0, 5, 5);
		//		gbc_lblTimezone.gridx = 3;
		//		gbc_lblTimezone.gridy = 9;
		gbc_lblTimezone.gridx = 3;
		gbc_lblTimezone.gridy = 4;
		//panel_1.add(lblTimezone, gbc_lblTimezone);
		//		advancedPanel.add(lblTimezone, gbc_lblTimezone);
		mongoPanel.add(lblTimezone, gbc_lblTimezone);

		cb_Timezone = new JComboBox<String>();
		cb_Timezone.setToolTipText("<html>Times held in the Mongo DB are converted to UTC. <br> The application will determine local timezone automatically when set to \"Default\". <br>Override this setting when in a different timezone to home and handling files with dates / times in local timezone.  <br>Therefore override in exceptional cases only.  <br>Leave as Default for all other occassions</html>");
		//		cb_Timezone.setModel(new DefaultComboBoxModel(new String[] {"Local Timezone", "GMT +1", "GMT +2", "GMT +3", "GMT +4", "GMT +5", "GMT +6", "GMT +7", "GMT +8", "GMT +9", "GMT +10", "GMT +11", "GMT +12", "GMT +13", "GMT -1", "GMT -2", "GMT -3", "GMT -4", "GMT -5", "GMT -6", "GMT -7", "GMT -8", "GMT -9", "GMT -10", "GMT -11", "GMT -12", "GMT -13"}));
		for (String c : m_TimezoneList)
		{
			cb_Timezone.addItem(c);
		}



		GridBagConstraints gbc_cb_Timezone = new GridBagConstraints();
		gbc_cb_Timezone.insets = new Insets(0, 0, 5, 5);
		gbc_cb_Timezone.fill = GridBagConstraints.HORIZONTAL;
		//		gbc_cb_Timezone.gridx = 4;
		//		gbc_cb_Timezone.gridy = 9;
		gbc_cb_Timezone.gridx = 4;
		gbc_cb_Timezone.gridy = 4;
		//		panel_1.add(cb_Timezone, gbc_cb_Timezone);
		//		advancedPanel.add(cb_Timezone, gbc_cb_Timezone);
		mongoPanel.add(cb_Timezone, gbc_cb_Timezone);



		lbl_LogFile = new JLabel("Log File");
		GridBagConstraints gbc_lblLogFile = new GridBagConstraints();
		gbc_lblLogFile.anchor = GridBagConstraints.EAST;
		gbc_lblLogFile.insets = new Insets(0, 0, 5, 5);
		//gbc_lblLogFile.gridwidth = 2;
		//		gbc_lblLogFile.gridx = 1;
		//		gbc_lblLogFile.gridy = 15;
		gbc_lblLogFile.gridx = 0;
		gbc_lblLogFile.gridy = 5;
		//panel_1.add(lbl_LogFile, gbc_lblLogFile);
		//		advancedPanel.add(lbl_LogFile, gbc_lblLogFile);
		mongoPanel.add(lbl_LogFile, gbc_lblLogFile);

		tf_LogFile = new JTextField(PrefsNightScoutLoader.getInstance().getM_LogFile());
		tf_LogFile.setToolTipText("<html>All messages are stored in the log file (depending on the Log Level set).  <br>Only regular messages are displayed in the panel at the bottom of the main window.</html>");
		GridBagConstraints gbc_tf_LogFile = new GridBagConstraints();
		gbc_tf_LogFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_LogFile.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_LogFile.insets = new Insets(0, 0, 5, 5);
		gbc_tf_LogFile.gridwidth = 3;
		//		gbc_tf_LogFile.gridx = 2;
		//		gbc_tf_LogFile.gridy = 15;
		gbc_tf_LogFile.gridx = 1;
		gbc_tf_LogFile.gridy = 5;
		//panel_1.add(tf_LogFile, gbc_tf_LogFile);
		//		advancedPanel.add(tf_LogFile, gbc_tf_LogFile);
		mongoPanel.add(tf_LogFile, gbc_tf_LogFile);
		tf_LogFile.setPreferredSize(new Dimension(7, 20));
		tf_LogFile.setColumns(25);

		btn_SelectLogFile = new JButton("Select Log File");
		btn_SelectLogFile.setToolTipText("Displays a file selector to change the log file");
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
		//		gbc_btnSelectLogFile.gridx = 6;
		//		gbc_btnSelectLogFile.gridy = 15;
		gbc_btnSelectLogFile.gridx = 4;
		gbc_btnSelectLogFile.gridy = 5;
		//panel_1.add(btn_SelectLogFile, gbc_btnSelectLogFile);
		//		advancedPanel.add(btn_SelectLogFile, gbc_btnSelectLogFile);
		mongoPanel.add(btn_SelectLogFile, gbc_btnSelectLogFile);



		cb_DuplicateCheckType = new JComboBox<String>();
		cb_DuplicateCheckType.setToolTipText("<html>For duplicate detection.  <br>Controls Duplicate checking.  <br>Duplicates are either existing entries or new entries.  <br>Spinner below controls time difference for dupes.</html>");
		cb_DuplicateCheckType.setModel(new DefaultComboBoxModel<String>(new String[] {"No Duplicate Checking", "Existing Dupe in mins", "New Dupe in mins"}));
		cb_DuplicateCheckType.setSelectedIndex(PrefsNightScoutLoader.getInstance().getM_ProximityCheckType());
		cb_DuplicateCheckType.setEnabled(true);
		GridBagConstraints gbc_cbDuplicateChecking = new GridBagConstraints();
		gbc_cbDuplicateChecking.insets = new Insets(0, 0, 5, 5);
		gbc_cbDuplicateChecking.fill = GridBagConstraints.HORIZONTAL;
		//		gbc_cbDuplicateChecking.gridx = 6;
		//		gbc_cbDuplicateChecking.gridy = 16;
		gbc_cbDuplicateChecking.gridx = 0;
		gbc_cbDuplicateChecking.gridy = 6;
		//panel_1.add(cb_DuplicateCheckType, gbc_cbDuplicateChecking);
		//		advancedPanel.add(cb_DuplicateCheckType, gbc_cbDuplicateChecking);
		mongoPanel.add(cb_DuplicateCheckType, gbc_cbDuplicateChecking);

		rb_DupeBGCheck = new JRadioButton("Compare BG dp");
		rb_DupeBGCheck.setToolTipText("<html>For duplicate detection.  <br>Determines whether BGs are considered fur duplicate checks and how many decimal places to compare.</html>");
		rb_DupeBGCheck.setSelected(PrefsNightScoutLoader.getInstance().isM_CompareBGInProximityCheck());
		rb_DupeBGCheck.setEnabled(true);
		rb_DupeBGCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Disable the spinner
				sp_DupeBGDecPlace.setEnabled(rb_DupeBGCheck.isSelected());
			}
		});		

		GridBagConstraints gbc_rbDupeBGCheck = new GridBagConstraints();
		gbc_rbDupeBGCheck.gridwidth = 2;
		gbc_rbDupeBGCheck.anchor = GridBagConstraints.WEST;
		gbc_rbDupeBGCheck.insets = new Insets(0, 0, 5, 5);
		//		gbc_rbDupeBGCheck.gridx = 3;
		//		gbc_rbDupeBGCheck.gridy = 17;
		gbc_rbDupeBGCheck.gridx = 1;
		gbc_rbDupeBGCheck.gridy = 6;
		//panel_1.add(rb_DupeBGCheck, gbc_rbDupeBGCheck);
		//		advancedPanel.add(rb_DupeBGCheck, gbc_rbDupeBGCheck);
		mongoPanel.add(rb_DupeBGCheck, gbc_rbDupeBGCheck);

		rb_DupeInsulinCheck = new JRadioButton("Compare Insulin DP");
		rb_DupeInsulinCheck.setToolTipText("<html>For duplicate detection.  <br>Determines whether Insulin Units are considered fur duplicate checks and how many decimal places to compare.</html>");
		rb_DupeInsulinCheck.setSelected(PrefsNightScoutLoader.getInstance().isM_CompareInsulinInProximityCheck());
		rb_DupeInsulinCheck.setEnabled(true);
		rb_DupeInsulinCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Disable the spinner
				sp_DupeInsulinDecPlace.setEnabled(rb_DupeInsulinCheck.isSelected());
			}
		});				
		GridBagConstraints gbc_rbDupeInsulinCheck = new GridBagConstraints();
		gbc_rbDupeInsulinCheck.gridwidth = 2;
		gbc_rbDupeInsulinCheck.anchor = GridBagConstraints.WEST;
		gbc_rbDupeInsulinCheck.insets = new Insets(0, 0, 5, 5);
		//		gbc_rbDupeInsulinCheck.gridx = 3;
		//		gbc_rbDupeInsulinCheck.gridy = 19;
		gbc_rbDupeInsulinCheck.gridx = 2;
		gbc_rbDupeInsulinCheck.gridy = 6;
		//panel_1.add(rb_DupeInsulinCheck, gbc_rbDupeInsulinCheck);
		//		advancedPanel.add(rb_DupeInsulinCheck, gbc_rbDupeInsulinCheck);
		mongoPanel.add(rb_DupeInsulinCheck, gbc_rbDupeInsulinCheck);

		rb_DupeCarbCheck = new JRadioButton("Compare Carb DP");
		rb_DupeCarbCheck.setToolTipText("<html>For duplicate detection.  <br>Determines whether Carb Amounts are considered fur duplicate checks and how many decimal places to compare.</html>");
		rb_DupeCarbCheck.setSelected(PrefsNightScoutLoader.getInstance().isM_CompareCarbInProximityCheck());
		rb_DupeCarbCheck.setEnabled(true);
		rb_DupeCarbCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Disable the spinner
				sp_DupeCarbDecPlace.setEnabled(rb_DupeCarbCheck.isSelected());
			}
		});						
		GridBagConstraints gbc_rbDupeCarbCheck = new GridBagConstraints();
		gbc_rbDupeCarbCheck.gridwidth = 2;
		gbc_rbDupeCarbCheck.anchor = GridBagConstraints.WEST;
		gbc_rbDupeCarbCheck.insets = new Insets(0, 0, 5, 5);
		//		gbc_rbDupeCarbCheck.gridx = 3;
		//		gbc_rbDupeCarbCheck.gridy = 18;
		gbc_rbDupeCarbCheck.gridx = 3;
		gbc_rbDupeCarbCheck.gridy = 6;
		//panel_1.add(rb_DupeCarbCheck, gbc_rbDupeCarbCheck);
		//		advancedPanel.add(rb_DupeCarbCheck, gbc_rbDupeCarbCheck);
		mongoPanel.add(rb_DupeCarbCheck, gbc_rbDupeCarbCheck);


		rb_LoadEntries = new JRadioButton("Weeks Entries to Load");
		rb_LoadEntries.setToolTipText("<html>For startup control.  <br>Determines whether CGM entries are loaded at start up.<br>Only enable if CGM ananlysis is to be performed.</html>");
		rb_LoadEntries.setSelected(PrefsNightScoutLoader.getInstance().getM_LoadNightscoutEntries());
		rb_LoadEntries.setEnabled(true);
		rb_LoadEntries.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Disable the spinner
				sp_WeeksBackToLoadEntries.setEnabled(rb_LoadEntries.isSelected());
				// Reset weeksback to non-zero if we are now enabled
				int weeksBack   = (int)sp_WeeksBackToLoadEntries.getModel().getValue();
				if (rb_LoadEntries.isSelected() && weeksBack == 0)
				{
					sp_WeeksBackToLoadEntries.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getDef_M_WeeksBackToLoadEntries());
				}
			}
		});
		GridBagConstraints gbc_rbLoadEntries = new GridBagConstraints();
		gbc_rbLoadEntries.gridwidth = 2;
		gbc_rbLoadEntries.anchor = GridBagConstraints.WEST;
		gbc_rbLoadEntries.insets = new Insets(0, 0, 5, 5);
		//		gbc_rbLoadEntries.gridx = 3;
		//		gbc_rbLoadEntries.gridy = 18;
		gbc_rbLoadEntries.gridx = 4;
		gbc_rbLoadEntries.gridy = 6;
		//panel_1.add(rb_LoadEntries, gbc_rbLoadEntries);
		//		advancedPanel.add(rb_LoadEntries, gbc_rbLoadEntries);
		mongoPanel.add(rb_LoadEntries, gbc_rbLoadEntries);

		
		sp_ProximityMinutes = new JSpinner();
		sp_ProximityMinutes.setModel(new SpinnerNumberModel(5, 1, 60, 1));
		sp_ProximityMinutes.setToolTipText("<html>For duplicate detection.  <br>How many minutes apart manual Care Portal entries can be from loaded meter readings before being marked as proximity.</html>");
		sp_ProximityMinutes.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_ProximityMinutes());
		GridBagConstraints gbc_sp_ProximityMinutes = new GridBagConstraints();
		gbc_sp_ProximityMinutes.fill = GridBagConstraints.WEST;
		gbc_sp_ProximityMinutes.insets = new Insets(0, 0, 5, 5);
		gbc_sp_ProximityMinutes.gridx = 0;
		//		gbc_sp_ProximityMinutes.gridy = 16;
		gbc_sp_ProximityMinutes.gridy = 7;
		//panel_1.add(sp_ProximityMinutes, gbc_sp_ProximityMinutes);
		//		advancedPanel.add(sp_ProximityMinutes, gbc_sp_ProximityMinutes);
		mongoPanel.add(sp_ProximityMinutes, gbc_sp_ProximityMinutes);

		sp_DupeBGDecPlace = new JSpinner();
		sp_DupeBGDecPlace.setModel(new SpinnerNumberModel(0, 0, 3, 1));
		sp_DupeBGDecPlace.setToolTipText("<html>For duplicate detection.  <br>How many decimal places on BGs are compared to detect duplicates.</html>");
		sp_DupeBGDecPlace.setEnabled(true);
		sp_DupeBGDecPlace.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_BGDecPlacesProximityCheck());
		GridBagConstraints gbc_spDupeBGDecPlace = new GridBagConstraints();
		gbc_spDupeBGDecPlace.insets = new Insets(0, 0, 5, 5);
		gbc_spDupeBGDecPlace.gridx = 1;
		gbc_spDupeBGDecPlace.gridy = 7;
		//panel_1.add(sp_DupeBGDecPlace, gbc_spDupeBGDecPlace);
		mongoPanel.add(sp_DupeBGDecPlace, gbc_spDupeBGDecPlace);


		sp_DupeInsulinDecPlace = new JSpinner();
		sp_DupeInsulinDecPlace.setModel(new SpinnerNumberModel(0, 0, 3, 1));
		sp_DupeInsulinDecPlace.setToolTipText("<html>For duplicate detection.  <br>How many decimal places on Insulin units are compared to detect duplicates.</html>");
		sp_DupeInsulinDecPlace.setEnabled(true);
		sp_DupeInsulinDecPlace.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_InsulinDecPlacesProximityCheck());
		GridBagConstraints gbc_spDupeInsulinDecPlace = new GridBagConstraints();
		gbc_spDupeInsulinDecPlace.insets = new Insets(0, 0, 0, 5);
		gbc_spDupeInsulinDecPlace.gridx = 2;
		gbc_spDupeInsulinDecPlace.gridy = 7;
		//panel_1.add(sp_DupeInsulinDecPlace, gbc_spDupeInsulinDecPlace);
		mongoPanel.add(sp_DupeInsulinDecPlace, gbc_spDupeInsulinDecPlace);


		sp_DupeCarbDecPlace = new JSpinner();
		sp_DupeCarbDecPlace.setModel(new SpinnerNumberModel(0, 0, 3, 1));
		sp_DupeCarbDecPlace.setToolTipText("<html>For duplicate detection.  <br>How many decimal places on Carb grams are compared to detect duplicates.</html>");
		sp_DupeCarbDecPlace.setEnabled(true);
		sp_DupeCarbDecPlace.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_CarbDecPlacesProximityCheck());
		GridBagConstraints gbc_spDupeCarbDecPlace = new GridBagConstraints();
		gbc_spDupeCarbDecPlace.insets = new Insets(0, 0, 5, 5);
		gbc_spDupeCarbDecPlace.gridx = 3;
		gbc_spDupeCarbDecPlace.gridy = 7;
		//panel_1.add(sp_DupeCarbDecPlace, gbc_spDupeCarbDecPlace);
		mongoPanel.add(sp_DupeCarbDecPlace, gbc_spDupeCarbDecPlace);

		sp_WeeksBackToLoadEntries = new JSpinner();
		sp_WeeksBackToLoadEntries.setModel(new SpinnerNumberModel(4, 0, 104, 1));
		sp_WeeksBackToLoadEntries.setToolTipText("<html>For startup control.  <br>How many months' worth of CGM entries data to load at startup.</html>");
		sp_WeeksBackToLoadEntries.setEnabled(true);
		sp_WeeksBackToLoadEntries.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_WeeksBackToLoadEntries());
		sp_WeeksBackToLoadEntries.addChangeListener(new ChangeListener() 
		{
			public void stateChanged(ChangeEvent e) 
			{
				int weeksBack   = (int)sp_WeeksBackToLoadEntries.getModel().getValue();
				rb_LoadEntries.setSelected(weeksBack > 0 ? true : false);
				sp_WeeksBackToLoadEntries.setEnabled(weeksBack > 0 ? true : false);
			}
		}
				);

		GridBagConstraints gbc_spWeeksBackToLoadEntries = new GridBagConstraints();
		gbc_spWeeksBackToLoadEntries.insets = new Insets(0, 0, 5, 5);
		gbc_spWeeksBackToLoadEntries.gridx = 4;
		gbc_spWeeksBackToLoadEntries.gridy = 7;
		//panel_1.add(sp_WeeksBackToLoadEntries, gbc_spWeeksBackToLoadEntries);
		mongoPanel.add(sp_WeeksBackToLoadEntries, gbc_spWeeksBackToLoadEntries);










		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		//		getContentPane().add(panel_1, gbc_panel_1);
		//		topPanel.add(panel_1, gbc_panel_1);
		//		centrePanel.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] {50, 147, 30};
		gbl_panel_1.rowHeights = new int[] {30, 0, 0, 0, 0, 0, 0, 0, 30, 30, 0, 30, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 1.0, 1.0};
		gbl_panel_1.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		panel_1.setLayout(gbl_panel_1);

		JLabel lblRocheDbServerName = new JLabel("ROCHE DB Server Name");
		GridBagConstraints gbc_lblRocheDbServerName = new GridBagConstraints();
		gbc_lblRocheDbServerName.anchor = GridBagConstraints.EAST;
		gbc_lblRocheDbServerName.insets = new Insets(0, 0, 5, 5);
		gbc_lblRocheDbServerName.gridx = 0;
		gbc_lblRocheDbServerName.gridy = 0;
		rochePanel.add(lblRocheDbServerName, gbc_lblRocheDbServerName);
		//		gbc_rochePanelComponents.anchor = GridBagConstraints.EAST;
		//		gbc_rochePanelComponents.gridx = 0;
		//		gbc_rochePanelComponents.gridy = 0;
		//		rochePanel.add(lblRocheDbServerName, gbc_rochePanelComponents);
		//		panel_1.add(lblRocheDbServerName, gbc_lblRocheDbServerName);

		tf_RocheDBServer = new JTextField(PrefsNightScoutLoader.getInstance().getM_SQLDBServerHost());
		tf_RocheDBServer.setToolTipText("<html>Roche Meter/Pump.  <br>Key Parameter to identify the Roche SQL server.  <br>This will be the machine that AccuChek is installed on.  <br>Use localhost for same machine.</html>");
		GridBagConstraints gbc_tf_RocheDBServer = new GridBagConstraints();
		gbc_tf_RocheDBServer.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_RocheDBServer.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_RocheDBServer.insets = new Insets(0, 0, 5, 5);
		gbc_tf_RocheDBServer.gridwidth = 3;
		gbc_tf_RocheDBServer.gridx = 1;
		gbc_tf_RocheDBServer.gridy = 0;
		rochePanel.add(tf_RocheDBServer, gbc_tf_RocheDBServer);
		//		gbc_rochePanelComponents.fill = GridBagConstraints.HORIZONTAL;
		//		gbc_rochePanelComponents.anchor = GridBagConstraints.NORTHWEST;
		//		gbc_rochePanelComponents.gridwidth = 6;
		//		gbc_rochePanelComponents.gridx = 1;
		//		gbc_rochePanelComponents.gridy = 0;
		//		rochePanel.add(tf_RocheDBServer, gbc_rochePanelComponents);
		//		panel_1.add(tf_RocheDBServer, gbc_tf_RocheDBServer);
		//		tf_RocheDBServer.setColumns(15);


		JLabel lblRocheDbServerInstance = new JLabel("ROCHE DB Server Instance");
		GridBagConstraints gbc_lblRocheDbServerInstance = new GridBagConstraints();
		gbc_lblRocheDbServerInstance.anchor = GridBagConstraints.EAST;
		gbc_lblRocheDbServerInstance.insets = new Insets(0, 0, 5, 5);
		gbc_lblRocheDbServerInstance.gridx = 0;
		gbc_lblRocheDbServerInstance.gridy = 1;
		rochePanel.add(lblRocheDbServerInstance, gbc_lblRocheDbServerInstance);
		//		gbc_rochePanelComponents.anchor = GridBagConstraints.EAST;
		//		gbc_rochePanelComponents.gridwidth = 1;
		//		gbc_rochePanelComponents.gridx = 0;
		//		gbc_rochePanelComponents.gridy = 1;
		//		rochePanel.add(lblRocheDbServerInstance, gbc_rochePanelComponents);

		//panel_1.add(lblRocheDbServerInstance, gbc_lblRocheDbServerInstance);

		tf_RocheDBInstance = new JTextField(PrefsNightScoutLoader.getInstance().getM_SQLDBServerInstance());
		tf_RocheDBInstance.setToolTipText("<html>Roche Meter/Pump.  <br>Key Parameter to identify the Roche Database Instance.</html>");
		GridBagConstraints gbc_tf_RocheDBInstance = new GridBagConstraints();
		gbc_tf_RocheDBInstance.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_RocheDBInstance.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_RocheDBInstance.insets = new Insets(0, 0, 5, 5);
		gbc_tf_RocheDBInstance.gridwidth = 2;
		gbc_tf_RocheDBInstance.gridx = 1;
		gbc_tf_RocheDBInstance.gridy = 1;
		rochePanel.add(tf_RocheDBInstance, gbc_tf_RocheDBInstance);
		//		gbc_rochePanelComponents.fill = GridBagConstraints.HORIZONTAL;
		////		gbc_rochePanelComponents.anchor = GridBagConstraints.NORTHWEST;
		//		gbc_rochePanelComponents.gridwidth = 3;
		//		gbc_rochePanelComponents.gridx = 1;
		//		gbc_rochePanelComponents.gridy = 1;
		//		rochePanel.add(tf_RocheDBInstance, gbc_rochePanelComponents);
		//	panel_1.add(tf_RocheDBInstance, gbc_tf_RocheDBInstance);
		// tf_RocheDBInstance.setColumns(15);

		JButton btnRocheTest = new JButton("Test Roche Connection");
		tf_RocheDBInstance.setToolTipText("<html>Roche Meter/Pump.  <br>Tests connectivity to the Roche Database and displays the results.</html>");
		btnRocheTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				testSQLServer();
			}
		});	
		GridBagConstraints gbc_btnRocheTest = new GridBagConstraints();
		gbc_btnRocheTest.anchor = GridBagConstraints.WEST;
		gbc_btnRocheTest.insets = new Insets(0, 0, 5, 5);
		gbc_btnRocheTest.gridx = 6;
		gbc_btnRocheTest.gridy = 1;
		rochePanel.add(btnRocheTest, gbc_btnRocheTest);
		//		gbc_rochePanelComponents.gridwidth = 1;
		//		gbc_rochePanelComponents.gridx = 6;
		//		gbc_rochePanelComponents.gridy = 1;
		//		rochePanel.add(btnRocheTest, gbc_rochePanelComponents);

		//	panel_1.add(btnRocheTest, gbc_btnRocheTest);

		JLabel lblRocheDbName = new JLabel("ROCHE DB Name");
		GridBagConstraints gbc_lblRocheDbName = new GridBagConstraints();
		gbc_lblRocheDbName.anchor = GridBagConstraints.EAST;
		gbc_lblRocheDbName.insets = new Insets(0, 0, 5, 5);
		gbc_lblRocheDbName.gridx = 0;
		gbc_lblRocheDbName.gridy = 2;
		rochePanel.add(lblRocheDbName, gbc_lblRocheDbName);
		//		gbc_rochePanelComponents.gridwidth = 1;
		//		gbc_rochePanelComponents.anchor = GridBagConstraints.EAST;
		//		gbc_rochePanelComponents.gridx = 0;
		//		gbc_rochePanelComponents.gridy = 2;
		//		rochePanel.add(lblRocheDbName, gbc_rochePanelComponents);

		//	panel_1.add(lblRocheDbName, gbc_lblRocheDbName);

		tf_RocheDBName = new JTextField(PrefsNightScoutLoader.getInstance().getM_SQLDBName());
		tf_RocheDBName.setToolTipText("<html>Roche Meter/Pump.  <br>Key Parameter to identify the Roche Database Name.</html>");
		GridBagConstraints gbc_tf_RocheDBName = new GridBagConstraints();
		gbc_tf_RocheDBName.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_RocheDBName.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_RocheDBName.insets = new Insets(0, 0, 5, 5);
		gbc_tf_RocheDBName.gridwidth = 2;
		gbc_tf_RocheDBName.gridx = 1;
		gbc_tf_RocheDBName.gridy = 2;
		rochePanel.add(tf_RocheDBName, gbc_tf_RocheDBName);
		//		gbc_rochePanelComponents.fill = GridBagConstraints.HORIZONTAL;
		//		gbc_rochePanelComponents.anchor = GridBagConstraints.NORTHWEST;
		//		gbc_rochePanelComponents.gridwidth = 4;
		//		gbc_rochePanelComponents.gridx = 1;
		//		gbc_rochePanelComponents.gridy = 2;
		//		rochePanel.add(tf_RocheDBName, gbc_rochePanelComponents);

		//		panel_1.add(tf_RocheDBName, gbc_tf_RocheDBName);
		//	tf_RocheDBName.setColumns(25);

		JLabel lblRocheSqlFile = new JLabel("ROCHE SQL File");
		GridBagConstraints gbc_lblRocheSqlFile = new GridBagConstraints();
		gbc_lblRocheSqlFile.anchor = GridBagConstraints.EAST;
		gbc_lblRocheSqlFile.insets = new Insets(0, 0, 5, 5);
		//gbc_lblSqlFile.gridwidth = 2;
		gbc_lblRocheSqlFile.gridx = 0;
		gbc_lblRocheSqlFile.gridy = 3;
		//panel_1.add(lblRocheSqlFile, gbc_lblRocheSqlFile);
		rochePanel.add(lblRocheSqlFile, gbc_lblRocheSqlFile);
		//		gbc_rochePanelComponents.anchor = GridBagConstraints.EAST;
		//		//gbc_lblSqlFile.gridwidth = 2;
		//		gbc_rochePanelComponents.gridwidth = 1;
		//		gbc_rochePanelComponents.gridx = 0;
		//		gbc_rochePanelComponents.gridy = 3;
		//		rochePanel.add(lblRocheSqlFile, gbc_rochePanelComponents);

		tf_RocheSQLFile = new JTextField(PrefsNightScoutLoader.getInstance().getM_SQLFile());
		tf_RocheSQLFile.setToolTipText("<html>Roche Meter/Pump.  <br>Key Parameter to locate the SQL Server SQL Commands File.  <br>This file is part of the NightscoutLoader installation.</html>");
		GridBagConstraints gbc_tf_RocheSQLFile = new GridBagConstraints();
		gbc_tf_RocheSQLFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_RocheSQLFile.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_RocheSQLFile.insets = new Insets(0, 0, 5, 5);
		gbc_tf_RocheSQLFile.gridwidth = 4;
		gbc_tf_RocheSQLFile.gridx = 1;
		gbc_tf_RocheSQLFile.gridy = 3;
		//panel_1.add(tf_RocheSQLFile, gbc_tf_RocheSQLFile);
		rochePanel.add(tf_RocheSQLFile, gbc_tf_RocheSQLFile);
		//		gbc_rochePanelComponents.fill = GridBagConstraints.HORIZONTAL;
		//		gbc_rochePanelComponents.anchor = GridBagConstraints.NORTHWEST;
		//		gbc_rochePanelComponents.gridwidth = 5;
		//		gbc_rochePanelComponents.gridx = 1;
		//		gbc_rochePanelComponents.gridy = 3;
		//		rochePanel.add(tf_RocheSQLFile, gbc_rochePanelComponents);
		//		tf_RocheSQLFile.setPreferredSize(new Dimension(7, 20));
		//		tf_RocheSQLFile.setColumns(25);

		JButton btnRocheSelect = new JButton("Select Roche SQL File");
		btnRocheSelect.setToolTipText("<html>Roche Meter/Pump.  <br>Provides a way of seleting the SQL Server SQL Commands File.</html>");
		btnRocheSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"SQL Files", "sql");
				chooser.setFileFilter(filter);
				File selectedFile = new File(tf_RocheSQLFile.getText());
				chooser.setSelectedFile(selectedFile);
				int returnVal = chooser.showOpenDialog(getContentPane());        	    		
				if(returnVal == JFileChooser.APPROVE_OPTION) 
				{
					tf_RocheSQLFile.setText(chooser.getSelectedFile().getAbsolutePath());
					m_Logger.log( Level.INFO, "You chose to open this file for SQL File Contents: " +
							chooser.getSelectedFile().getAbsolutePath());
				}				
			}
		});
		GridBagConstraints gbc_btnRocheSelect = new GridBagConstraints();
		gbc_btnRocheSelect.anchor = GridBagConstraints.WEST;
		gbc_btnRocheSelect.insets = new Insets(0, 0, 5, 5);
		gbc_btnRocheSelect.gridx = 6;
		gbc_btnRocheSelect.gridy = 3;
		//panel_1.add(btnRocheSelect, gbc_btnRocheSelect);
		rochePanel.add(btnRocheSelect, gbc_btnRocheSelect);
		//		gbc_rochePanelComponents.gridx = 8;
		//		gbc_rochePanelComponents.gridy = 3;
		//		rochePanel.add(btnRocheSelect, gbc_rochePanelComponents);

		JLabel lblRocheDaysToLoad = new JLabel("ROCHE Days to Load");
		GridBagConstraints gbc_lblRocheDaysToLoad = new GridBagConstraints();
		gbc_lblRocheDaysToLoad.anchor = GridBagConstraints.EAST;
		gbc_lblRocheDaysToLoad.insets = new Insets(0, 0, 5, 5);
		gbc_lblRocheDaysToLoad.gridx = 0;
		gbc_lblRocheDaysToLoad.gridy = 4;
		//panel_1.add(lblRocheDaysToLoad, gbc_lblRocheDaysToLoad);
		rochePanel.add(lblRocheDaysToLoad, gbc_lblRocheDaysToLoad);
		//		gbc_rochePanelComponents.anchor = GridBagConstraints.EAST;
		//		gbc_rochePanelComponents.gridwidth = 1;
		//		gbc_rochePanelComponents.gridx = 0;
		//		gbc_rochePanelComponents.gridy = 4;
		//		//panel_1.add(lblRocheDaysToLoad, gbc_rochePanelComponents);
		//		rochePanel.add(lblRocheDaysToLoad, gbc_rochePanelComponents);
		lblRocheDaysToLoad.setHorizontalAlignment(SwingConstants.LEFT);

		sp_RocheDaysToLoad = new JSpinner();
		sp_RocheDaysToLoad.setModel(new SpinnerNumberModel(45, 1, 1000, 1));
		sp_RocheDaysToLoad.setToolTipText("<html>Roche Meter/Pump.  <br>How many days' history to load from Roche SQL Server and compare with NS. </html>");
		sp_RocheDaysToLoad.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_DaysToLoad());
		GridBagConstraints gbc_sp_RocheDaysToLoad = new GridBagConstraints();
		gbc_sp_RocheDaysToLoad.insets = new Insets(0, 0, 5, 5);
		gbc_sp_RocheDaysToLoad.gridx = 1;
		gbc_sp_RocheDaysToLoad.gridy = 4;
		//panel_1.add(sp_RocheDaysToLoad, gbc_sp_RocheDaysToLoad);
		rochePanel.add(sp_RocheDaysToLoad, gbc_sp_RocheDaysToLoad);
		//		gbc_rochePanelComponents.insets = new Insets(0, 0, 5, 5);
		//		gbc_rochePanelComponents.gridx = 1;
		//		gbc_rochePanelComponents.gridy = 4;
		//		//panel_1.add(sp_RocheDaysToLoad, gbc_rochePanelComponents);
		//		rochePanel.add(sp_RocheDaysToLoad, gbc_rochePanelComponents);

		
		rb_DiasendTempBasals = new JRadioButton("Infer Temp Basals (Dias/Tand)");
		rb_DiasendTempBasals.setToolTipText("<html>Diasend & Tandem exports don't provide temp basals <br><b>Instead, they can be inferred by comparing basal rate change times.<br>A Basal rate change on the hour is assumed a usual change.  Any other time is assumed to be a temp basal.<br>Disable this in real use if temp basals appear wrong.</html>");
		rb_DiasendTempBasals.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton aButton = (AbstractButton) actionEvent.getSource();
				PrefsNightScoutLoader.getInstance().setM_InferTempBasals(aButton.isSelected());
			}
		});
		GridBagConstraints gbc_rdbtnInferTempBasals = new GridBagConstraints();
		gbc_rdbtnInferTempBasals.anchor = GridBagConstraints.WEST;
		gbc_rdbtnInferTempBasals.insets = new Insets(0, 0, 5, 5);
		//	gbc_rdbtnInferTempBasals.gridwidth = 6;
		//		gbc_rdbtnInferTempBasals.gridx = 1;
		gbc_rdbtnInferTempBasals.gridx = 4;
		//		gbc_rdbtnInferTempBasals.gridy = 10;
		gbc_rdbtnInferTempBasals.gridy = 4;
		//		panel_1.add(rb_DiasendTempBasals, gbc_rdbtnInferTempBasals);
		//		advancedPanel.add(rb_DiasendTempBasals, gbc_rdbtnInferTempBasals);
		rochePanel.add(rb_DiasendTempBasals, gbc_rdbtnInferTempBasals);
		//		gbc_rochePanelComponents.anchor = GridBagConstraints.WEST;
		//		gbc_rochePanelComponents.insets = new Insets(0, 0, 5, 5);
		//		gbc_rochePanelComponents.gridx = 2;
		//		gbc_rochePanelComponents.gridy = 4;
		//		rochePanel.add(rb_DiasendTempBasals, gbc_rochePanelComponents);

		rb_DiasendTempBasals.setSelected(PrefsNightScoutLoader.getInstance().isM_InferTempBasals());

		

		rb_UseMongoForRoche = new JRadioButton("Use Mongo for Roche");
		rb_UseMongoForRoche.setToolTipText("<html>Roche Meter/Pump.  <br><b>Used for Development purposes only.</b>  <br>Roche SQL Server data is encoded in MongoDB as a raw document to allow development on stand alone laptop.   <br>Disable this in real use.</html>");
		rb_UseMongoForRoche.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton aButton = (AbstractButton) actionEvent.getSource();
				PrefsNightScoutLoader.getInstance().setM_UseMongoForRocheResults(aButton.isSelected());
			}
		});
		GridBagConstraints gbc_rdbtnUseMongoForRoche = new GridBagConstraints();
		gbc_rdbtnUseMongoForRoche.anchor = GridBagConstraints.WEST;
		gbc_rdbtnUseMongoForRoche.insets = new Insets(0, 0, 5, 5);
		//	gbc_rdbtnUseMongoForRoche.gridwidth = 6;
		//		gbc_rdbtnUseMongoForRoche.gridx = 1;
		gbc_rdbtnUseMongoForRoche.gridx = 4;
		//		gbc_rdbtnUseMongoForRoche.gridy = 10;
		gbc_rdbtnUseMongoForRoche.gridy = 5;
		//		panel_1.add(rb_UseMongoForRoche, gbc_rdbtnUseMongoForRoche);
		//		advancedPanel.add(rb_UseMongoForRoche, gbc_rdbtnUseMongoForRoche);
		rochePanel.add(rb_UseMongoForRoche, gbc_rdbtnUseMongoForRoche);
		//		gbc_rochePanelComponents.anchor = GridBagConstraints.WEST;
		//		gbc_rochePanelComponents.insets = new Insets(0, 0, 5, 5);
		//		gbc_rochePanelComponents.gridx = 2;
		//		gbc_rochePanelComponents.gridy = 4;
		//		rochePanel.add(rb_UseMongoForRoche, gbc_rochePanelComponents);

		rb_UseMongoForRoche.setSelected(PrefsNightScoutLoader.getInstance().isM_UseMongoForRocheResults());
		
		lbl_MeterMongoServer = new JLabel("Meter Mongo Server");
		GridBagConstraints gbc_lblMeterMongoServer = new GridBagConstraints();
		gbc_lblMeterMongoServer.anchor = GridBagConstraints.EAST;
		gbc_lblMeterMongoServer.insets = new Insets(0, 0, 5, 5);
		//	gbc_label_3.gridwidth = 5;
		gbc_lblMeterMongoServer.gridx = 0;
		//		gbc_lblMeterMongoServer.gridy = 11;
		gbc_lblMeterMongoServer.gridy = 5;
		//		panel_1.add(lbl_MeterMongoServer, gbc_lblMeterMongoServer);
		//		advancedPanel.add(lbl_MeterMongoServer, gbc_lblMeterMongoServer);
		rochePanel.add(lbl_MeterMongoServer, gbc_lblMeterMongoServer);
		//		gbc_rochePanelComponents.anchor = GridBagConstraints.EAST;
		//		gbc_rochePanelComponents.gridx = 0;
		//		gbc_rochePanelComponents.gridy = 6;
		//		rochePanel.add(lbl_MeterMongoServer, gbc_rochePanelComponents);

		tf_MeterMongoServer = new JTextField(PrefsNightScoutLoader.getInstance().getM_MongoMeterServer());
		tf_MeterMongoServer.setToolTipText("<html>Roche Meter/Pump.  <br><b>Used for Development only</b>.  <br>Simulates SQL Server results in a MongoDB when Use Mongo for Roche is enabled and data loaded from Roche Combo.</html>");
		GridBagConstraints gbc_tf_MeterMongoServer = new GridBagConstraints();
		gbc_tf_MeterMongoServer.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_MeterMongoServer.anchor = GridBagConstraints.EAST;
		gbc_tf_MeterMongoServer.insets = new Insets(0, 0, 5, 5);
		gbc_tf_MeterMongoServer.gridwidth = 3;
		gbc_tf_MeterMongoServer.gridx = 1;
		//		gbc_tf_MeterMongoServer.gridy = 11;
		gbc_tf_MeterMongoServer.gridy = 5;
		//		panel_1.add(tf_MeterMongoServer, gbc_tf_MeterMongoServer);
		//		advancedPanel.add(tf_MeterMongoServer, gbc_tf_MeterMongoServer);
		rochePanel.add(tf_MeterMongoServer, gbc_tf_MeterMongoServer);
		//		gbc_rochePanelComponents.fill = GridBagConstraints.HORIZONTAL;
		//		gbc_rochePanelComponents.anchor = GridBagConstraints.EAST;
		//		gbc_rochePanelComponents.gridwidth = 4;
		//		gbc_rochePanelComponents.gridx = 1;
		//		gbc_rochePanelComponents.gridy = 6;
		//		rochePanel.add(tf_MeterMongoServer, gbc_rochePanelComponents);

		//		tf_MeterMongoServer.setColumns(10);

		lbl_MeterMongoDB = new JLabel("Meter Mongo DB");
		GridBagConstraints gbc_lbl_MeterMongoDB = new GridBagConstraints();
		gbc_lbl_MeterMongoDB.anchor = GridBagConstraints.EAST;
		gbc_lbl_MeterMongoDB.insets = new Insets(0, 0, 5, 5);
		//	gbc_label_4.gridwidth = 4;
		gbc_lbl_MeterMongoDB.gridx = 0;
		//		gbc_lbl_MeterMongoDB.gridy = 12;
		gbc_lbl_MeterMongoDB.gridy = 6;
		//		panel_1.add(lbl_MeterMongoDB, gbc_lbl_MeterMongoDB);
		//		advancedPanel.add(lbl_MeterMongoDB, gbc_lbl_MeterMongoDB);
		rochePanel.add(lbl_MeterMongoDB, gbc_lbl_MeterMongoDB);
		//		gbc_rochePanelComponents.anchor = GridBagConstraints.EAST;
		//		gbc_rochePanelComponents.gridx = 0;
		//		gbc_rochePanelComponents.gridy = 7;
		//		rochePanel.add(lbl_MeterMongoDB, gbc_rochePanelComponents);

		tf_MeterMongoDB = new JTextField(PrefsNightScoutLoader.getInstance().getM_MongoMeterDB());
		tf_MeterMongoDB.setToolTipText("<html>Roche Meter/Pump.  <br><b>Used for Development only.</b>  <br>Simulates SQL Server results in a MongoDB when Use Mongo for Roche is enabled and data loaded from Roche Combo.</html>");
		GridBagConstraints gbc_tf_MeterMongoDB = new GridBagConstraints();
		gbc_tf_MeterMongoDB.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_MeterMongoDB.anchor = GridBagConstraints.EAST;
		gbc_tf_MeterMongoDB.insets = new Insets(0, 0, 5, 5);
		gbc_tf_MeterMongoDB.gridwidth = 2;
		gbc_tf_MeterMongoDB.gridx = 1;
		//		gbc_tf_MeterMongoDB.gridy = 12;
		gbc_tf_MeterMongoDB.gridy = 6;
		//panel_1.add(tf_MeterMongoDB, gbc_tf_MeterMongoDB);
		//		advancedPanel.add(tf_MeterMongoDB, gbc_tf_MeterMongoDB);
		rochePanel.add(tf_MeterMongoDB, gbc_tf_MeterMongoDB);
		//		gbc_rochePanelComponents.fill = GridBagConstraints.HORIZONTAL;
		//		gbc_rochePanelComponents.anchor = GridBagConstraints.EAST;
		//		gbc_rochePanelComponents.gridwidth = 4;
		//		gbc_rochePanelComponents.gridx = 1;
		//		gbc_rochePanelComponents.gridy = 7;
		//		rochePanel.add(tf_MeterMongoDB, gbc_rochePanelComponents);
		//		tf_MeterMongoDB.setColumns(10);

		lbl_MeterMongoCollection = new JLabel("Meter Mongo Collection");
		GridBagConstraints gbc_label_5 = new GridBagConstraints();
		gbc_label_5.anchor = GridBagConstraints.EAST;
		gbc_label_5.insets = new Insets(0, 0, 5, 5);
		//	gbc_label_5.gridwidth = 6;
		gbc_label_5.gridx = 0;
		//		gbc_label_5.gridy = 13;
		gbc_label_5.gridy = 7;
		//panel_1.add(lbl_MeterMongoCollection, gbc_label_5);
		//		advancedPanel.add(lbl_MeterMongoCollection, gbc_label_5);
		rochePanel.add(lbl_MeterMongoCollection, gbc_label_5);
		//		gbc_rochePanelComponents.anchor = GridBagConstraints.EAST;
		//		gbc_rochePanelComponents.gridx = 0;
		//		gbc_rochePanelComponents.gridy = 8;
		//		rochePanel.add(lbl_MeterMongoCollection, gbc_rochePanelComponents);



		tf_MeterMongoCollection = new JTextField(PrefsNightScoutLoader.getInstance().getM_MongoMeterCollection());
		tf_MeterMongoCollection.setToolTipText("<html>Roche Meter/Pump.  <br><b>Used for Development only.</b>  <br>Simulates SQL Server results in a MongoDB when Use Mongo for Roche is enabled and data loaded from Roche Combo.</html>");
		GridBagConstraints gbc_tf_MeterMongoCollection = new GridBagConstraints();
		gbc_tf_MeterMongoCollection.insets = new Insets(0, 0, 5, 5);
		gbc_tf_MeterMongoCollection.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_MeterMongoCollection.anchor = GridBagConstraints.EAST;
		gbc_tf_MeterMongoCollection.gridwidth = 2;
		gbc_tf_MeterMongoCollection.gridx = 1;
		//		gbc_tf_MeterMongoCollection.gridy = 13;
		gbc_tf_MeterMongoCollection.gridy = 7;
		//panel_1.add(tf_MeterMongoCollection, gbc_tf_MeterMongoCollection);
		//		advancedPanel.add(tf_MeterMongoCollection, gbc_tf_MeterMongoCollection);
		rochePanel.add(tf_MeterMongoCollection, gbc_tf_MeterMongoCollection);
		//		gbc_rochePanelComponents.fill = GridBagConstraints.HORIZONTAL;
		//		gbc_rochePanelComponents.anchor = GridBagConstraints.EAST;
		//		gbc_rochePanelComponents.gridwidth = 4;
		//		gbc_rochePanelComponents.gridx = 1;
		//		gbc_rochePanelComponents.gridy = 8;
		//		rochePanel.add(tf_MeterMongoCollection, gbc_rochePanelComponents);
		//		tf_MeterMongoCollection.setColumns(25);



		//		private JTextField tf_LogLevel;
		//		private JTextField tf_LogFile;


		JPanel buttonPanel = new JPanel();
		GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
		gbc_buttonPanel.anchor = GridBagConstraints.SOUTH;
		gbc_buttonPanel.insets = new Insets(0, 0, 5, 0);
		gbc_buttonPanel.gridx = 0;
		gbc_buttonPanel.gridy = 3;
		getContentPane().add(buttonPanel, gbc_buttonPanel);

		JButton btnOK = new JButton("Ok");
		btnOK.setToolTipText("<html><b>Saves changes back to Settings store</b>.</html>");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveSettings();
				dispose();
			}
		});
		buttonPanel.add(btnOK);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setToolTipText("<html><b>Closes window without saving changes back to Settings store</b>.</html>");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		buttonPanel.add(btnCancel);

		JButton btnResetToDefaults = new JButton("Reset to Defaults");
		btnResetToDefaults.setToolTipText("<html><b>Resets values back to Factory defaults.<br>Any changes will be lost.</b>.</html>");
		buttonPanel.add(btnResetToDefaults);		

		btnResetToDefaults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				restoreSettings();
			}
		});

		JButton btnExport = new JButton("Export Settings");
		btnExport.setToolTipText("<html><b>Exports Settings to a file.<br>Useful to then import on another machine.</b>.</html>");
		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"Preferences", "xml");
				chooser.setFileFilter(filter);
				File selectedFile = new File("NightscoutLoaderPreferences.xml");
				chooser.setSelectedFile(selectedFile);
				int returnVal = chooser.showSaveDialog(getContentPane());        	    		
				if(returnVal == JFileChooser.APPROVE_OPTION) 
				{
					String chosenFile = new String(chooser.getSelectedFile().getAbsolutePath());
					// Handle if file exists
					if (m_WinMain.confirmWriteFile(chosenFile, "Preferences XML Export"))
					{
						try {
							PrefsNightScoutLoader.getInstance().exportPreferences(chooser.getSelectedFile().getAbsolutePath());
							m_WinMain.openExcelFile(chosenFile);

						} catch (IOException | BackingStoreException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}				
			}
		});
		buttonPanel.add(btnExport);	

		JButton btnImport = new JButton("Import Settings");
		btnImport.setToolTipText("<html><b>Imports Settings from an exported Settings file.</b>.</html>");
		btnImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"Preferences", "xml");
				chooser.setFileFilter(filter);
				File selectedFile = new File("NightscoutLoaderPreferences.xml");
				chooser.setSelectedFile(selectedFile);
				int returnVal = chooser.showOpenDialog(getContentPane());        	    		
				if(returnVal == JFileChooser.APPROVE_OPTION) 
				{
					String chosenFile = new String(chooser.getSelectedFile().getAbsolutePath());
					try {
						PrefsNightScoutLoader.getInstance().importPreferences(chooser.getSelectedFile().getAbsolutePath());
					} 
					
					catch (InvalidPreferencesFormatException e) {
						JOptionPane.showMessageDialog(null, 
								"The selected file appears to be in wrong format.\n\n" + chosenFile);
					}

					catch (IOException | BackingStoreException  e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}				
			}
		});
		buttonPanel.add(btnImport);	



		cb_DuplicateCheckType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				checkDuplicateCheckingEnabled();
			}
		});

		toggleAdvancedSettings(PrefsNightScoutLoader.getInstance().isM_AdvancedSettings());
		
		// Set values based on the restore method
		loadSettings();
	}


	/*	// Copy of previous constructor - not used
	public void previous_WinSettings(WinNightScoutLoader winMain, String title) 
	{
		// super();  Copy of previous constructor can't call this

		getContentPane().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {

				// David 30 Sep 2016
				// Catch when set visible and call loadSettings
				// Problem is constructor is called before Prefs is initialized.
			}
		});

		m_WinMain = winMain;

		URL url = MainNightScoutLoader.class.getResource("/Nightscout.jpg");
		ImageIcon img = new ImageIcon(url);
		setIconImage(img.getImage());

		m_SQLServerLoader = new DataLoadRoche();
		m_MongoDBLoader   = new DataLoadNightScoutTreatments();

		super.setTitle(title);
		setBounds(100, 50, 650, 650);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {300, 0};
		gridBagLayout.rowHeights = new int[] {300, 20, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0};
		getContentPane().setLayout(gridBagLayout);


//		JPanel topPanel = new JPanel();
//		JPanel centrePanel = new JPanel();
//		JPanel lowerPanel = new JPanel();
//		
//		topPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
//		centrePanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
//		lowerPanel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
//
//		getContentPane().add(topPanel, BorderLayout.NORTH);
//		getContentPane().add(centrePanel, BorderLayout.CENTER);
//		getContentPane().add(lowerPanel, BorderLayout.CENTER);





		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		getContentPane().add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] {50, 147, 30};
		gbl_panel_1.rowHeights = new int[] {30, 0, 0, 0, 0, 0, 0, 0, 30, 30, 0, 30, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 1.0, 1.0};
		gbl_panel_1.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		panel_1.setLayout(gbl_panel_1);


		JPanel rochePanel = new JPanel();
		GridBagConstraints gbc_rochePanel = new GridBagConstraints();
		gbc_rochePanel.fill = GridBagConstraints.BOTH;
		gbc_rochePanel.gridx = 0;
		gbc_rochePanel.gridy = 1;
		getContentPane().add(rochePanel, gbc_rochePanel);
		GridBagLayout gbl_rochePanel = new GridBagLayout();
		gbl_rochePanel.columnWidths = new int[] {50, 147, 30};
		gbl_rochePanel.rowHeights = new int[] {30, 0, 0, 0, 0, 0, 0, 0, 30, 30, 0, 30, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_rochePanel.columnWeights = new double[]{0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 1.0, 1.0};
		gbl_rochePanel.rowWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		rochePanel.setLayout(gbl_rochePanel);


		JLabel lblDbServerName = new JLabel("DB Server Name");
		GridBagConstraints gbc_lblDbServerName = new GridBagConstraints();
		gbc_lblDbServerName.anchor = GridBagConstraints.EAST;
		gbc_lblDbServerName.insets = new Insets(0, 0, 5, 5);
		gbc_lblDbServerName.gridx = 1;
		gbc_lblDbServerName.gridy = 0;
		rochePanel.add(lblDbServerName, gbc_lblDbServerName);

		tf_RocheDBServer = new JTextField(PrefsNightScoutLoader.getInstance().getM_SQLDBServerHost());
		GridBagConstraints gbc_tf_DBServer = new GridBagConstraints();
		gbc_tf_DBServer.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_DBServer.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_DBServer.insets = new Insets(0, 0, 5, 5);
		gbc_tf_DBServer.gridwidth = 4;
		gbc_tf_DBServer.gridx = 2;
		gbc_tf_DBServer.gridy = 0;
		rochePanel.add(tf_RocheDBServer, gbc_tf_DBServer);
		//		tf_RocheDBServer.setColumns(15);

		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 7;
		gbc_panel_2.gridy = 0;
		rochePanel.add(panel_2, gbc_panel_2);

		JLabel lblDbServerInstance = new JLabel("DB Server Instance");
		GridBagConstraints gbc_lblDbServerInstance = new GridBagConstraints();
		gbc_lblDbServerInstance.anchor = GridBagConstraints.EAST;
		gbc_lblDbServerInstance.insets = new Insets(0, 0, 5, 5);
		gbc_lblDbServerInstance.gridx = 1;
		gbc_lblDbServerInstance.gridy = 1;
		rochePanel.add(lblDbServerInstance, gbc_lblDbServerInstance);

		tf_RocheDBInstance = new JTextField(PrefsNightScoutLoader.getInstance().getM_SQLDBServerInstance());
		GridBagConstraints gbc_tf_DBInstance = new GridBagConstraints();
		gbc_tf_DBInstance.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_DBInstance.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_DBInstance.insets = new Insets(0, 0, 5, 5);
		gbc_tf_DBInstance.gridwidth = 4;
		gbc_tf_DBInstance.gridx = 2;
		gbc_tf_DBInstance.gridy = 1;
		rochePanel.add(tf_RocheDBInstance, gbc_tf_DBInstance);
		tf_RocheDBInstance.setColumns(15);

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
		rochePanel.add(btnTest, gbc_btnTest);

		JLabel lblDbName = new JLabel("DB Name");
		GridBagConstraints gbc_lblDbName = new GridBagConstraints();
		gbc_lblDbName.anchor = GridBagConstraints.EAST;
		gbc_lblDbName.insets = new Insets(0, 0, 5, 5);
		gbc_lblDbName.gridx = 1;
		gbc_lblDbName.gridy = 2;
		rochePanel.add(lblDbName, gbc_lblDbName);

		tf_RocheDBName = new JTextField(PrefsNightScoutLoader.getInstance().getM_SQLDBName());
		GridBagConstraints gbc_tf_DBName = new GridBagConstraints();
		gbc_tf_DBName.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_DBName.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_DBName.insets = new Insets(0, 0, 5, 5);
		gbc_tf_DBName.gridwidth = 4;
		gbc_tf_DBName.gridx = 2;
		gbc_tf_DBName.gridy = 2;
		rochePanel.add(tf_RocheDBName, gbc_tf_DBName);
		//	tf_RocheDBName.setColumns(25);

		JLabel lblDaysToLoad = new JLabel("Days to Load");
		GridBagConstraints gbc_lblDaysToLoad = new GridBagConstraints();
		gbc_lblDaysToLoad.anchor = GridBagConstraints.EAST;
		gbc_lblDaysToLoad.insets = new Insets(0, 0, 5, 5);
		gbc_lblDaysToLoad.gridx = 1;
		gbc_lblDaysToLoad.gridy = 3;
		panel_1.add(lblDaysToLoad, gbc_lblDaysToLoad);
		lblDaysToLoad.setHorizontalAlignment(SwingConstants.LEFT);

		sp_RocheDaysToLoad = new JSpinner();
		sp_RocheDaysToLoad.setModel(new SpinnerNumberModel(45, 1, 1000, 1));
		sp_RocheDaysToLoad.setToolTipText("How many days' history to load from Roche SQL Server and compare with NS");
		sp_RocheDaysToLoad.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_DaysToLoad());
		GridBagConstraints gbc_sp_DaysToLoad = new GridBagConstraints();
		gbc_sp_DaysToLoad.insets = new Insets(0, 0, 5, 5);
		gbc_sp_DaysToLoad.gridx = 2;
		gbc_sp_DaysToLoad.gridy = 3;
		panel_1.add(sp_RocheDaysToLoad, gbc_sp_DaysToLoad);

		JLabel lblSqlFile = new JLabel("SQL File");
		GridBagConstraints gbc_lblSqlFile = new GridBagConstraints();
		gbc_lblSqlFile.anchor = GridBagConstraints.EAST;
		gbc_lblSqlFile.insets = new Insets(0, 0, 5, 5);
		//gbc_lblSqlFile.gridwidth = 2;
		gbc_lblSqlFile.gridx = 1;
		gbc_lblSqlFile.gridy = 4;
		panel_1.add(lblSqlFile, gbc_lblSqlFile);

		tf_RocheSQLFile = new JTextField(PrefsNightScoutLoader.getInstance().getM_SQLFile());
		GridBagConstraints gbc_tf_SQLFile = new GridBagConstraints();
		gbc_tf_SQLFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_SQLFile.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_SQLFile.insets = new Insets(0, 0, 5, 5);
		gbc_tf_SQLFile.gridwidth = 4;
		gbc_tf_SQLFile.gridx = 2;
		gbc_tf_SQLFile.gridy = 4;
		panel_1.add(tf_RocheSQLFile, gbc_tf_SQLFile);
		tf_RocheSQLFile.setPreferredSize(new Dimension(7, 20));
		tf_RocheSQLFile.setColumns(25);

		JButton btnSelect = new JButton("Select");
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"SQL Files", "sql");
				chooser.setFileFilter(filter);
				File selectedFile = new File(tf_RocheSQLFile.getText());
				chooser.setSelectedFile(selectedFile);
				int returnVal = chooser.showOpenDialog(getContentPane());        	    		
				if(returnVal == JFileChooser.APPROVE_OPTION) 
				{
					tf_RocheSQLFile.setText(chooser.getSelectedFile().getAbsolutePath());
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

		tf_MongoNightscoutServer = new JTextField(PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer());
		tf_MongoNightscoutServer.setToolTipText("Key Parameter to identify the Nightscout MongoDB server.  Set this to an empty string to then force Nightscout Loader to run in stand-alone mode where it will not attempt a server connection.");
		tf_MongoNightscoutServer.setBackground(Color.YELLOW);
		tf_MongoNightscoutServer.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_tf_NightscoutServer = new GridBagConstraints();
		gbc_tf_NightscoutServer.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_NightscoutServer.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_NightscoutServer.insets = new Insets(0, 0, 5, 5);
		gbc_tf_NightscoutServer.gridwidth = 4;
		gbc_tf_NightscoutServer.gridx = 2;
		gbc_tf_NightscoutServer.gridy = 5;
		panel_1.add(tf_MongoNightscoutServer, gbc_tf_NightscoutServer);
		tf_MongoNightscoutServer.setColumns(10);

		lblMongoBGUnits = new JLabel("BG Units");
		lblMongoBGUnits.setBackground(Color.YELLOW);
		lblMongoBGUnits.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_lblBGUnits;
		gbc_lblBGUnits = new GridBagConstraints();
		gbc_lblBGUnits.anchor = GridBagConstraints.EAST;
		gbc_lblBGUnits.insets = new Insets(0, 0, 5, 5);
		gbc_lblBGUnits.gridx = 1;
		gbc_lblBGUnits.gridy = 6;
		panel_1.add(lblMongoBGUnits, gbc_lblBGUnits);


		cb_MongoBGUnitList = new JComboBox<String>();
		cb_MongoBGUnitList.setModel(new DefaultComboBoxModel<String>(new String[] {"mmol/L", "mg/dL"}));
		cb_MongoBGUnitList.setBackground(Color.YELLOW);
		GridBagConstraints gbc_BGUnitList = new GridBagConstraints();
		gbc_BGUnitList.gridwidth = 2;
		gbc_BGUnitList.insets = new Insets(0, 0, 5, 5);
		gbc_BGUnitList.fill = GridBagConstraints.HORIZONTAL;
		gbc_BGUnitList.gridx = 3;
		gbc_BGUnitList.gridy = 6;
		panel_1.add(cb_MongoBGUnitList, gbc_BGUnitList);

		JLabel label_1 = new JLabel("Nightscout DB");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.anchor = GridBagConstraints.EAST;
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		//gbc_label_1.gridwidth = 3;
		gbc_label_1.gridx = 1;
		gbc_label_1.gridy = 7;
		panel_1.add(label_1, gbc_label_1);

		tf_MongoNightscoutDB = new JTextField(PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB());
		GridBagConstraints gbc_tf_NightscoutDB = new GridBagConstraints();
		gbc_tf_NightscoutDB.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_NightscoutDB.anchor = GridBagConstraints.NORTHWEST;
		gbc_tf_NightscoutDB.insets = new Insets(0, 0, 5, 5);
		gbc_tf_NightscoutDB.gridwidth = 4;
		gbc_tf_NightscoutDB.gridx = 2;
		gbc_tf_NightscoutDB.gridy = 7;
		panel_1.add(tf_MongoNightscoutDB, gbc_tf_NightscoutDB);
		tf_MongoNightscoutDB.setColumns(10);

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

		tf_MongoNightscoutCollection = new JTextField(PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection());
		GridBagConstraints gbc_tf_NightscoutCollection = new GridBagConstraints();
		gbc_tf_NightscoutCollection.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_NightscoutCollection.anchor = GridBagConstraints.EAST;
		gbc_tf_NightscoutCollection.insets = new Insets(0, 0, 5, 5);
		gbc_tf_NightscoutCollection.gridwidth = 4;
		gbc_tf_NightscoutCollection.gridx = 2;
		gbc_tf_NightscoutCollection.gridy = 8;
		panel_1.add(tf_MongoNightscoutCollection, gbc_tf_NightscoutCollection);
		tf_MongoNightscoutCollection.setColumns(25);

		JRadioButton rdbtnAdvancedSettings = new JRadioButton("Advanced Settings");
		rdbtnAdvancedSettings.setToolTipText("Disabling Advanced Settings hides the complexity of the Analyze window parameters.  It also hides all the features from Settings window below this selector.");
		rdbtnAdvancedSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton aButton = (AbstractButton) actionEvent.getSource();
				PrefsNightScoutLoader.getInstance().setM_AdvancedSettings(aButton.isSelected());
				toggleAdvancedSettings(aButton.isSelected());
			}
		});
		GridBagConstraints gbc_rdbtnAdvancedSettings = new GridBagConstraints();
		gbc_rdbtnAdvancedSettings.anchor = GridBagConstraints.WEST;
		gbc_rdbtnAdvancedSettings.insets = new Insets(0, 0, 5, 5);
		// gbc_rdbtnAdvancedSettings.gridwidth = 5;
		gbc_rdbtnAdvancedSettings.gridx = 1;
		gbc_rdbtnAdvancedSettings.gridy = 9;
		panel_1.add(rdbtnAdvancedSettings, gbc_rdbtnAdvancedSettings);
		rdbtnAdvancedSettings.setSelected(PrefsNightScoutLoader.getInstance().isM_AdvancedSettings());

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
		GridBagConstraints gbc_lbl_MeterMongoDB = new GridBagConstraints();
		gbc_lbl_MeterMongoDB.anchor = GridBagConstraints.EAST;
		gbc_lbl_MeterMongoDB.insets = new Insets(0, 0, 5, 5);
		//	gbc_label_4.gridwidth = 4;
		gbc_lbl_MeterMongoDB.gridx = 1;
		gbc_lbl_MeterMongoDB.gridy = 12;
		panel_1.add(lbl_MeterMongoDB, gbc_lbl_MeterMongoDB);

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

		cb_DuplicateCheckType = new JComboBox();

		cb_DuplicateCheckType.setModel(new DefaultComboBoxModel(new String[] {"No Duplicate Checking", "Mark Existing as Dupe", "Mark New as Dupe"}));
		cb_DuplicateCheckType.setSelectedIndex(PrefsNightScoutLoader.getInstance().getM_ProximityCheckType());
		cb_DuplicateCheckType.setEnabled(true);
		GridBagConstraints gbc_cbDuplicateChecking = new GridBagConstraints();
		gbc_cbDuplicateChecking.insets = new Insets(0, 0, 5, 5);
		gbc_cbDuplicateChecking.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbDuplicateChecking.gridx = 6;
		gbc_cbDuplicateChecking.gridy = 16;
		panel_1.add(cb_DuplicateCheckType, gbc_cbDuplicateChecking);

		lbl_MaxMinsForCorrectionBolus = new JLabel("Max Mins Correction Bolus");
		GridBagConstraints gbc_label_8 = new GridBagConstraints();
		gbc_label_8.anchor = GridBagConstraints.EAST;
		gbc_label_8.insets = new Insets(0, 0, 5, 5);
		//	gbc_label_8.gridwidth = 6;
		gbc_label_8.gridx = 1;
		gbc_label_8.gridy = 17;
		panel_1.add(lbl_MaxMinsForCorrectionBolus, gbc_label_8);

		sp_MaxMinsForCorrectionBolus = new JSpinner();
		sp_MaxMinsForCorrectionBolus.setModel(new SpinnerNumberModel(5, 1, 90, 1));
		sp_MaxMinsForCorrectionBolus.setToolTipText("How many minutes apart a BG test can be from an Insulin Correction.");
		sp_MaxMinsForCorrectionBolus.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_MaxMinsBetweenSameCorrectionEvent());

		GridBagConstraints gbc_sp_MaxMinsForCorrectionBolus = new GridBagConstraints();
		gbc_sp_MaxMinsForCorrectionBolus.insets = new Insets(0, 0, 5, 5);
		gbc_sp_MaxMinsForCorrectionBolus.gridx = 2;
		gbc_sp_MaxMinsForCorrectionBolus.gridy = 17;
		panel_1.add(sp_MaxMinsForCorrectionBolus, gbc_sp_MaxMinsForCorrectionBolus);

		rb_DupeBGCheck = new JRadioButton("Compare BG");
		rb_DupeBGCheck.setSelected(PrefsNightScoutLoader.getInstance().isM_CompareBGInProximityCheck());
		rb_DupeBGCheck.setEnabled(true);
		GridBagConstraints gbc_rbDupeBGCheck = new GridBagConstraints();
		gbc_rbDupeBGCheck.gridwidth = 2;
		gbc_rbDupeBGCheck.anchor = GridBagConstraints.WEST;
		gbc_rbDupeBGCheck.insets = new Insets(0, 0, 5, 5);
		gbc_rbDupeBGCheck.gridx = 3;
		gbc_rbDupeBGCheck.gridy = 17;
		panel_1.add(rb_DupeBGCheck, gbc_rbDupeBGCheck);

		sp_DupeBGDecPlace = new JSpinner();
		sp_DupeBGDecPlace.setModel(new SpinnerNumberModel(0, 0, 3, 1));
		sp_DupeBGDecPlace.setToolTipText("For duplicate detection.  How many minutes apart manual Care Portal entries can be from loaded meter readings before being marked as proximity.");
		sp_DupeBGDecPlace.setEnabled(true);
		sp_DupeBGDecPlace.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_BGDecPlacesProximityCheck());
		GridBagConstraints gbc_spDupeBGDecPlace = new GridBagConstraints();
		gbc_spDupeBGDecPlace.insets = new Insets(0, 0, 5, 5);
		gbc_spDupeBGDecPlace.gridx = 5;
		gbc_spDupeBGDecPlace.gridy = 17;
		panel_1.add(sp_DupeBGDecPlace, gbc_spDupeBGDecPlace);

		lbl_DupeBGDecPlace = new JLabel("BG Decimal Places");
		lbl_DupeBGDecPlace.setEnabled(true);
		GridBagConstraints gbc_lblDupeBGDecPlace = new GridBagConstraints();
		gbc_lblDupeBGDecPlace.anchor = GridBagConstraints.WEST;
		gbc_lblDupeBGDecPlace.insets = new Insets(0, 0, 5, 5);
		gbc_lblDupeBGDecPlace.gridx = 6;
		gbc_lblDupeBGDecPlace.gridy = 17;
		panel_1.add(lbl_DupeBGDecPlace, gbc_lblDupeBGDecPlace);

		lbl_MongoCheckMinutes = new JLabel("Mins Mongo Check");
		lbl_MongoCheckMinutes.setEnabled(true);
		GridBagConstraints gbc_lblMongoCheckMinutes = new GridBagConstraints();
		gbc_lblMongoCheckMinutes.insets = new Insets(0, 0, 5, 5);
		gbc_lblMongoCheckMinutes.gridx = 1;
		gbc_lblMongoCheckMinutes.gridy = 18;
		panel_1.add(lbl_MongoCheckMinutes, gbc_lblMongoCheckMinutes);

		sp_MongoDBAlertMinutes = new JSpinner();
		sp_MongoDBAlertMinutes.setModel(new SpinnerNumberModel(5, 1, 60, 1));
		sp_MongoDBAlertMinutes.setToolTipText("Number of minutes between background MongoDB update check alerts.  0 turns feature off.");
		sp_MongoDBAlertMinutes.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_MongoDBAlerterCheckInterval());
		sp_MongoDBAlertMinutes.setEnabled(true);
		GridBagConstraints gbc_sp_MongoDBAlertMinutes = new GridBagConstraints();
		gbc_sp_MongoDBAlertMinutes.insets = new Insets(0, 0, 5, 5);
		gbc_sp_MongoDBAlertMinutes.gridx = 2;
		gbc_sp_MongoDBAlertMinutes.gridy = 18;
		panel_1.add(sp_MongoDBAlertMinutes, gbc_sp_MongoDBAlertMinutes);

		rb_DupeCarbCheck = new JRadioButton("Compare Carb");
		rb_DupeCarbCheck.setSelected(PrefsNightScoutLoader.getInstance().isM_CompareCarbInProximityCheck());
		rb_DupeCarbCheck.setEnabled(true);
		GridBagConstraints gbc_rbDupeCarbCheck = new GridBagConstraints();
		gbc_rbDupeCarbCheck.gridwidth = 2;
		gbc_rbDupeCarbCheck.anchor = GridBagConstraints.WEST;
		gbc_rbDupeCarbCheck.insets = new Insets(0, 0, 5, 5);
		gbc_rbDupeCarbCheck.gridx = 3;
		gbc_rbDupeCarbCheck.gridy = 18;
		panel_1.add(rb_DupeCarbCheck, gbc_rbDupeCarbCheck);

		sp_DupeCarbDecPlace = new JSpinner();
		sp_DupeCarbDecPlace.setModel(new SpinnerNumberModel(0, 0, 3, 1));
		sp_DupeCarbDecPlace.setToolTipText("For duplicate detection.  How many minutes apart manual Care Portal entries can be from loaded meter readings before being marked as proximity.");
		sp_DupeCarbDecPlace.setEnabled(true);
		sp_DupeCarbDecPlace.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_CarbDecPlacesProximityCheck());

		GridBagConstraints gbc_spDupeCarbDecPlace = new GridBagConstraints();
		gbc_spDupeCarbDecPlace.insets = new Insets(0, 0, 5, 5);
		gbc_spDupeCarbDecPlace.gridx = 5;
		gbc_spDupeCarbDecPlace.gridy = 18;
		panel_1.add(sp_DupeCarbDecPlace, gbc_spDupeCarbDecPlace);

		lbl_DupeCarbDecPlace = new JLabel("Carb Decimal Places");
		lbl_DupeCarbDecPlace.setEnabled(true);
		GridBagConstraints gbc_lbl_CarbDupeDecPlace = new GridBagConstraints();
		gbc_lbl_CarbDupeDecPlace.anchor = GridBagConstraints.WEST;
		gbc_lbl_CarbDupeDecPlace.insets = new Insets(0, 0, 5, 5);
		gbc_lbl_CarbDupeDecPlace.gridx = 6;
		gbc_lbl_CarbDupeDecPlace.gridy = 18;
		panel_1.add(lbl_DupeCarbDecPlace, gbc_lbl_CarbDupeDecPlace);

		rb_ProximityTypeCheck = new JRadioButton("Dupe Type Check");
		rb_ProximityTypeCheck.setSelected(
				PrefsNightScoutLoader.getInstance().getM_ProximityCheckType() == 0 ?
						false : true);
		GridBagConstraints gbc_rbProximityTypeCheck = new GridBagConstraints();
		gbc_rbProximityTypeCheck.insets = new Insets(0, 0, 0, 5);
		gbc_rbProximityTypeCheck.gridx = 1;
		gbc_rbProximityTypeCheck.gridy = 19;
		panel_1.add(rb_ProximityTypeCheck, gbc_rbProximityTypeCheck);

		rb_DupeInsulinCheck = new JRadioButton("Compare Insulin");
		rb_DupeInsulinCheck.setSelected(PrefsNightScoutLoader.getInstance().isM_CompareInsulinInProximityCheck());
		rb_DupeInsulinCheck.setEnabled(true);
		GridBagConstraints gbc_rbDupeInsulinCheck = new GridBagConstraints();
		gbc_rbDupeInsulinCheck.gridwidth = 2;
		gbc_rbDupeInsulinCheck.anchor = GridBagConstraints.WEST;
		gbc_rbDupeInsulinCheck.insets = new Insets(0, 0, 0, 5);
		gbc_rbDupeInsulinCheck.gridx = 3;
		gbc_rbDupeInsulinCheck.gridy = 19;
		panel_1.add(rb_DupeInsulinCheck, gbc_rbDupeInsulinCheck);

		sp_DupeInsulinDecPlace = new JSpinner();
		sp_DupeInsulinDecPlace.setModel(new SpinnerNumberModel(0, 0, 3, 1));
		sp_DupeInsulinDecPlace.setToolTipText("For duplicate detection.  How many minutes apart manual Care Portal entries can be from loaded meter readings before being marked as proximity.");
		sp_DupeInsulinDecPlace.setEnabled(true);
		sp_DupeInsulinDecPlace.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_InsulinDecPlacesProximityCheck());
		GridBagConstraints gbc_spDupeInsulinDecPlace = new GridBagConstraints();
		gbc_spDupeInsulinDecPlace.insets = new Insets(0, 0, 0, 5);
		gbc_spDupeInsulinDecPlace.gridx = 5;
		gbc_spDupeInsulinDecPlace.gridy = 19;
		panel_1.add(sp_DupeInsulinDecPlace, gbc_spDupeInsulinDecPlace);

		lbl_DupeInsulinDecPlace = new JLabel("Insulin Decimal Places");
		lbl_DupeInsulinDecPlace.setEnabled(true);
		GridBagConstraints gbc_lbl_InsulinDupeDecPlace = new GridBagConstraints();
		gbc_lbl_InsulinDupeDecPlace.anchor = GridBagConstraints.WEST;
		gbc_lbl_InsulinDupeDecPlace.insets = new Insets(0, 0, 0, 5);
		gbc_lbl_InsulinDupeDecPlace.gridx = 6;
		gbc_lbl_InsulinDupeDecPlace.gridy = 19;
		panel_1.add(lbl_DupeInsulinDecPlace, gbc_lbl_InsulinDupeDecPlace);



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
				saveSettings();
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
				restoreSettings();
			}
		});


		cb_DuplicateCheckType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				checkDuplicateCheckingEnabled();
			}
		});

		toggleAdvancedSettings(PrefsNightScoutLoader.getInstance().isM_AdvancedSettings());

		// Set values based on the restore method
		loadSettings();
	}
	// Copy of previous constructor - not used

	 */	


	private void checkDuplicateCheckingEnabled()
	{
		// Set enabled if advaced Settings are set too
		boolean enabled = cb_DuplicateCheckType.getSelectedIndex() == 0 ? false : 
			PrefsNightScoutLoader.getInstance().isM_AdvancedSettings() == true ? true : false;

		sp_ProximityMinutes.setEnabled(enabled);

		//		cb_DuplicateCheckType.setEnabled(enabled); No!!!
		rb_DupeBGCheck.setEnabled(enabled);
		rb_DupeCarbCheck.setEnabled(enabled);	
		rb_DupeInsulinCheck.setEnabled(enabled);
		rb_LoadEntries.setEnabled(enabled);

		sp_DupeBGDecPlace.setEnabled(enabled);
		sp_DupeCarbDecPlace.setEnabled(enabled);
		sp_DupeInsulinDecPlace.setEnabled(enabled);	
		sp_WeeksBackToLoadEntries.setEnabled(enabled);	
	}

	private void toggleAdvancedSettings(boolean advancedSettings)
	{
		//		tf_MeterMongoServer.setVisible(advancedSettings);
		//		tf_MeterMongoDB.setVisible(advancedSettings);
		//		tf_MeterMongoCollection.setVisible(advancedSettings);
		//		tf_LogFile.setVisible(advancedSettings);
		//		sp_MaxMinsForCorrectionBolus.setVisible(advancedSettings);
		//		sp_MaxMinsForMealBolus.setVisible(advancedSettings);
		//		cb_Timezone.setVisible(advancedSettings);
		//		cb_InputDateFormat.setVisible(advancedSettings);
		//
		//
		//		rb_UseMongoForRoche.setVisible(advancedSettings);
		//		lbl_MeterMongoServer.setVisible(advancedSettings);
		//		lbl_MeterMongoDB.setVisible(advancedSettings);
		//		lbl_MeterMongoCollection.setVisible(advancedSettings);
		//		lbl_LogFile.setVisible(advancedSettings);
		//		lbl_LogLevel.setVisible(advancedSettings);
		//		cb_LogLevel.setVisible(advancedSettings);
		//		lbl_MaxMinsForMealBolus.setVisible(advancedSettings);
		//		lbl_MaxMinsForCorrectionBolus.setVisible(advancedSettings);
		//		lblDateFormat.setVisible(advancedSettings);
		//		lblTimezone.setVisible(advancedSettings);
		//
		//		btn_SelectLogFile.setVisible(advancedSettings);

		tf_MeterMongoServer.setEnabled(advancedSettings);
		tf_MeterMongoDB.setEnabled(advancedSettings);
		tf_MeterMongoCollection.setEnabled(advancedSettings);
		tf_LogFile.setEnabled(advancedSettings);
		sp_MaxMinsForCorrectionBolus.setEnabled(advancedSettings);
		sp_MaxMinsForMealBolus.setEnabled(advancedSettings);
		cb_Timezone.setEnabled(advancedSettings);
		cb_InputDateFormat.setEnabled(advancedSettings);


		rb_UseMongoForRoche.setEnabled(advancedSettings);
		lbl_MeterMongoServer.setEnabled(advancedSettings);
		lbl_MeterMongoDB.setEnabled(advancedSettings);
		lbl_MeterMongoCollection.setEnabled(advancedSettings);
		lbl_LogFile.setEnabled(advancedSettings);
		lbl_LogLevel.setEnabled(advancedSettings);
		cb_LogLevel.setEnabled(advancedSettings);
		lbl_MaxMinsForMealBolus.setEnabled(advancedSettings);
		lbl_MaxMinsForCorrectionBolus.setEnabled(advancedSettings);
		lblDateFormat.setEnabled(advancedSettings);
		lblTimezone.setEnabled(advancedSettings);

		cb_DuplicateCheckType.setEnabled(advancedSettings);

		// Allow this routine to own setting enabled/disabled for these items
		checkDuplicateCheckingEnabled();

		/*		sp_ProximityMinutes.setEnabled(advancedSettings);		
		rb_DupeBGCheck.setEnabled(advancedSettings);
		rb_DupeCarbCheck.setEnabled(advancedSettings);	
		rb_DupeInsulinCheck.setEnabled(advancedSettings);

		sp_DupeBGDecPlace.setEnabled(advancedSettings);
		sp_DupeCarbDecPlace.setEnabled(advancedSettings);
		sp_DupeInsulinDecPlace.setEnabled(advancedSettings);
		 */		
		sp_MongoDBAlertMinutes.setEnabled(advancedSettings);
		lbl_MongoCheckMinutes.setEnabled(advancedSettings);

		btn_SelectLogFile.setEnabled(advancedSettings);
		
		// Override settings for meter parameters
		toggleDavidsSettings();

	}
	
	private void toggleDavidsSettings()
	{
		// Only enable the parameters that use MongoDB for Roche if it's Davids laptop
		boolean davidsLaptop = PrefsNightScoutLoader.isItDavidsLaptop();
		
		rb_UseMongoForRoche.setEnabled(davidsLaptop);
		lbl_MeterMongoServer.setEnabled(davidsLaptop);
		lbl_MeterMongoDB.setEnabled(davidsLaptop);
		lbl_MeterMongoCollection.setEnabled(davidsLaptop);
		
		tf_MeterMongoCollection.setEnabled(davidsLaptop);
		tf_MeterMongoDB.setEnabled(davidsLaptop);
		tf_MeterMongoServer.setEnabled(davidsLaptop);
	}

	public void restoreSettings()
	{
		JDialog.setDefaultLookAndFeelDecorated(true);
		int response = JOptionPane.showConfirmDialog(null, 
				"Do you want to reset Settings back to factory defaults?", 
				"Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (response == JOptionPane.YES_OPTION)
		{
			PrefsNightScoutLoader.getInstance().loadDefaultPreferences();
			loadSettings();
		}
	}

	public void loadSettings()
	{
		cb_MongoBGUnitList.setSelectedIndex(PrefsNightScoutLoader.getInstance().getM_BGUnits());
		tf_RocheSQLFile.setText(PrefsNightScoutLoader.getInstance().getM_SQLFile());
		tf_RocheDBServer.setText(PrefsNightScoutLoader.getInstance().getM_SQLDBServerHost());
		tf_RocheDBInstance.setText(PrefsNightScoutLoader.getInstance().getM_SQLDBServerInstance());
		tf_RocheDBName.setText(PrefsNightScoutLoader.getInstance().getM_SQLDBName());
		tf_MongoNightscoutServer.setText(PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer());
		tf_MongoNightscoutDB.setText(PrefsNightScoutLoader.getInstance().getM_NightscoutMongoDB());
		tf_MongoNightscoutCollection.setText(PrefsNightScoutLoader.getInstance().getM_NightscoutMongoCollection());
		tf_MeterMongoServer.setText(PrefsNightScoutLoader.getInstance().getM_MongoMeterServer());
		tf_MeterMongoDB.setText(PrefsNightScoutLoader.getInstance().getM_MongoMeterDB());
		tf_MeterMongoCollection.setText(PrefsNightScoutLoader.getInstance().getM_MongoMeterCollection());
		//		tf_LogLevel.setText(String.format("%d", PrefsNightScoutLoader.getInstance().getM_LogLevel()));
		cb_LogLevel.setSelectedIndex(PrefsNightScoutLoader.getInstance().getM_LogLevel());
		tf_LogFile.setText(PrefsNightScoutLoader.getInstance().getM_LogFile());
		sp_MaxMinsForCorrectionBolus.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_MaxMinsBetweenSameCorrectionEvent());
		sp_ProximityMinutes.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_ProximityMinutes());
		sp_RocheDaysToLoad.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_DaysToLoad());
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

		cb_DuplicateCheckType.setSelectedIndex(PrefsNightScoutLoader.getInstance().getM_ProximityCheckType());
		rb_DupeBGCheck.setSelected(PrefsNightScoutLoader.getInstance().isM_CompareBGInProximityCheck());
		rb_DupeCarbCheck.setSelected(PrefsNightScoutLoader.getInstance().isM_CompareCarbInProximityCheck());	
		rb_DupeInsulinCheck.setSelected(PrefsNightScoutLoader.getInstance().isM_CompareInsulinInProximityCheck());
		rb_LoadEntries.setSelected(PrefsNightScoutLoader.getInstance().getM_LoadNightscoutEntries());

		sp_DupeBGDecPlace.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_BGDecPlacesProximityCheck());
		sp_DupeCarbDecPlace.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_CarbDecPlacesProximityCheck());
		sp_DupeInsulinDecPlace.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_InsulinDecPlacesProximityCheck());
		sp_WeeksBackToLoadEntries.getModel().setValue((int)PrefsNightScoutLoader.getInstance().getM_WeeksBackToLoadEntries());
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

	public void saveSettings()
	{
		boolean bgUnitsChanging = PrefsNightScoutLoader.getInstance().getM_BGUnits() == cb_MongoBGUnitList.getSelectedIndex() ? false : true;

		// Check if Mongo Server is changing.  If so, then reset the test count
		String currentMongoServer = PrefsNightScoutLoader.getInstance().getM_NightscoutMongoServer();
		String newMongoServer = tf_MongoNightscoutServer.getText();
		if (!currentMongoServer.equals(newMongoServer))
		{
			m_Logger.log( Level.INFO, "Resetting Nightscout server from '" + currentMongoServer +
					"' to '" + newMongoServer + "' so will attempt connection.");
			DataLoadNightScoutTreatments.resetFailedTests();
		}

		PrefsNightScoutLoader.getInstance().setM_BGUnits(cb_MongoBGUnitList.getSelectedIndex());
		PrefsNightScoutLoader.getInstance().setM_DaysToLoad((int)sp_RocheDaysToLoad.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_SQLFile(tf_RocheSQLFile.getText());
		PrefsNightScoutLoader.getInstance().setM_SQLDBServerHost(tf_RocheDBServer.getText());
		PrefsNightScoutLoader.getInstance().setM_SQLDBServerInstance(tf_RocheDBInstance.getText());
		PrefsNightScoutLoader.getInstance().setM_SQLDBName(tf_RocheDBName.getText());
		PrefsNightScoutLoader.getInstance().setM_NightscoutMongoServer(tf_MongoNightscoutServer.getText());
		//		NightLoaderPreferences.getInstance().setM_NightscoutMongoPort(Integer.parseInt(tf_NightscoutPort.getText()));
		PrefsNightScoutLoader.getInstance().setM_NightscoutMongoDB(tf_MongoNightscoutDB.getText());
		PrefsNightScoutLoader.getInstance().setM_NightscoutMongoCollection(tf_MongoNightscoutCollection.getText());
		PrefsNightScoutLoader.getInstance().setM_MongoMeterServer(tf_MeterMongoServer.getText());
		PrefsNightScoutLoader.getInstance().setM_MongoMeterDB(tf_MeterMongoDB.getText());
		PrefsNightScoutLoader.getInstance().setM_MongoMeterCollection(tf_MeterMongoCollection.getText());
		//PrefsNightScoutLoader.getInstance().setM_LogLevel(Integer.parseInt(tf_LogLevel.getText()));
		PrefsNightScoutLoader.getInstance().setM_LogLevel(cb_LogLevel.getSelectedIndex());
		PrefsNightScoutLoader.getInstance().setM_LogFile(tf_LogFile.getText());
		PrefsNightScoutLoader.getInstance().setM_MaxMinsBetweenSameCorrectionEvent((int)sp_MaxMinsForCorrectionBolus.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_MaxMinsBetweenSameMealEvent((int)sp_MaxMinsForMealBolus.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_ProximityMinutes((int)sp_ProximityMinutes.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_MongoDBAlerterCheckInterval((int)sp_MongoDBAlertMinutes.getModel().getValue());	
		PrefsNightScoutLoader.getInstance().setM_ProximityCheckType(cb_DuplicateCheckType.getSelectedIndex());
		PrefsNightScoutLoader.getInstance().setM_CompareBGInProximityCheck(rb_DupeBGCheck.isSelected());
		PrefsNightScoutLoader.getInstance().setM_CompareCarbInProximityCheck(rb_DupeCarbCheck.isSelected());	
		PrefsNightScoutLoader.getInstance().setM_CompareInsulinInProximityCheck(rb_DupeInsulinCheck.isSelected());
		PrefsNightScoutLoader.getInstance().setM_LoadNightscoutEntries(rb_LoadEntries.isSelected());		
		
		PrefsNightScoutLoader.getInstance().setM_BGDecPlacesProximityCheck((int)sp_DupeBGDecPlace.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_CarbDecPlacesProximityCheck((int)sp_DupeCarbDecPlace.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_InsulinDecPlacesProximityCheck((int)sp_DupeInsulinDecPlace.getModel().getValue());
		PrefsNightScoutLoader.getInstance().setM_WeeksBackToLoadEntries((int)sp_WeeksBackToLoadEntries.getModel().getValue());

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
			if (tf_RocheDBName.getText().length() > 0)
			{
				result = m_SQLServerLoader.testDBConnection(tf_RocheDBServer.getText(), 
						tf_RocheDBInstance.getText(),
						tf_RocheDBName.getText());
				message += result;

				final JDialog dialog = new JDialog();
				dialog.setSize(700, 600);

				JTextArea textArea = new JTextArea(message);
				textArea.setFont(new Font("Courier", Font.PLAIN, 14));
				JScrollPane scrollPane = new JScrollPane(textArea);
				scrollPane.setBounds(10,60,750,500);

				dialog.setModal(true);
				dialog.getContentPane().add(scrollPane);
				dialog.setVisible(true);
			}
			else
			{
				result = m_SQLServerLoader.testDBConnection(tf_RocheDBServer.getText(),
						tf_RocheDBInstance.getText());
				message += result;

				final JDialog dialog = new JDialog();
				dialog.setSize(700, 600);

				JTextArea textArea = new JTextArea(message);
				textArea.setFont(new Font("Courier", Font.PLAIN, 14));
				JScrollPane scrollPane = new JScrollPane(textArea);
				scrollPane.setBounds(10,60,790,500);

				dialog.setModal(true);
				dialog.getContentPane().add(scrollPane);
				dialog.setVisible(true);
			}
		}
		catch (Exception e)
		{
			result = "Test SQL Server Failed.\n" + "Unable to connect to SQL Server\n\n"  + 
					"Server(" + tf_RocheDBServer.getText() + ") " +
					"DB Instance(" + tf_RocheDBInstance.getText()  + ") " +
					"DB Name(" + tf_RocheDBName.getText() + ") " +
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
			if (tf_MongoNightscoutDB.getText().length() > 0)
			{
				result = m_MongoDBLoader.testDBConnection(tf_MongoNightscoutServer.getText(), 
						tf_MongoNightscoutDB.getText());
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
				result = m_MongoDBLoader.testDBConnection(tf_MongoNightscoutServer.getText());
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
					"Nightscout Server(" + tf_MongoNightscoutServer.getText() + ") " +
					"DB Instance(" + tf_MongoNightscoutDB.getText()  + ") " +
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
