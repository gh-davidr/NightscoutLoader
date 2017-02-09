package davidRichardson;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Font;
import java.awt.Point;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


import javax.swing.table.DefaultTableModel;

import davidRichardson.ThreadAnalyzer.AnalyzerCompleteHander;

import javax.swing.border.EtchedBorder;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Component;
import java.awt.Desktop;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

//import src.com.toedter.calendar.*;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar; 
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;

import javax.swing.JLabel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComboBox;

import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.awt.FlowLayout;
import javax.swing.border.BevelBorder;
import java.awt.Rectangle;
import java.awt.Graphics;



public class WinNightScoutLoader extends JFrame {

	private static final Logger m_Logger = Logger.getLogger( MyLogger.class.getName() );

	/**
	 * 
	 */
	private static final long serialVersionUID = -4452654082212680074L;

	private MyLogger m_LoggerClass;

	private JPanel contentPane;

	/*
	 * David Re-design, just one grid,
	 */

	/*	private JTable m_MeterTable;
	private JScrollPane m_MeterScrollPane;
	 */	
	private WinAbout         aboutDialog;
	private WinWhy           whyDialog;
	private WinSettings       SettingsDialog;
	private WinMongoForm     mongoForm;
	private WinAuditHistory  auditHistory;
	//	private WinFind          find;
	private WinAnalyzer      analyzer;
	private ThreadHelpLauncher m_ThreadHelpLauncher = null;

	private JTable m_NightScoutTable;
	private JScrollPane m_NightscoutScrollPane;
	private int m_RowUpdated;

	private JDatePickerImpl startDatePicker;
	private JDatePickerImpl endDatePicker;

	private JLabel lbl_TimeZone;

	private JTextArea m_StatusText;

	private final int m_StatusTextPaneDepth  = 100;
	private final int m_bigScrollPaneDepth   = 430;

	// soon replace all below with core
	private CoreNightScoutLoader  m_NightScoutLoaderCore;

	// Hold the list of objects back from Nightscout
	private ArrayList <DBResult> m_MongoResults;

	// Hold the list of CGM Entries back from Nightscout
	private ArrayList <DBResultEntry> m_MongoResultEntries;

	/*    Set<String> set1 = new HashSet<String>();
    set1.addAll(ls1);

    Set<String> set2 = new HashSet<String>();
    set2.addAll(ls2);

    set2.removeAll(set1);
	 */	

	// private String[] m_SupportedMeters = {"Roche Combo", "Medtronic", "Diasend", "OmniPod", "Roche SQL Extract", "Tandem"};
	private String[] m_SupportedMeters = {"Roche Combo", "Medtronic", "Diasend", "OmniPod", "Roche SQL Extract", };
	//	private String[] m_SupportedMeters = {"Roche Combo", "Medtronic"};
	private JComboBox<String> m_ComboBox;

	private JLabel m_StartDateLbl;
	private JLabel m_EndDateLbl;
	private JTextField m_FileNameTxtFld;
	private JLabel m_FileNameLbl;
	private JButton m_FileSelectBtn;
	private JLabel m_CGMEntriesLoadedLbl;

	private JMenuItem m_mntmExportResults; 

	private JMenuItem m_mntmSychronize;
	private JMenuItem m_mntmLoadMeterPumpOnly;
	private JMenuItem m_mntmLoadNightscout;
	private JMenuItem m_mntmAnalyzeResults;
	private JMenuItem m_mntmDeleteLoadedTreatments;

	private String    m_SaveDiffMessage = null;

	private Graphics  m_EntriesGraphic  = null;

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public WinNightScoutLoader() throws IOException 
	{
		super("Nightscout Loader " + Version.getInstance().getM_Version());

		//		ImageIcon img = new ImageIcon("C:\\Local_Data\\Primary Dataserver\\David\\Android Projects\\Java Coding\\Eclipse Workspace\\NightScoutLoader2\\Images\\Nightscout.jpg");
		URL url = MainNightScoutLoader.class.getResource("/Nightscout.jpg");
		ImageIcon img = new ImageIcon(url);

		setIconImage(img.getImage());

		m_NightScoutLoaderCore = CoreNightScoutLoader.getInstance();
		m_LoggerClass = new MyLogger(true);

		// Record Application Start up
		m_Logger.log(Level.INFO, "*** Application Started. ***");

		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				close();
			}
		});

		initialize();
		applyConfig();

		// Load Night Scout results immediately & do some level of analysis
		// doLoadNightScout(true);

		// test out the multi-threaded piece
		doThreadLoadNightScout(true);

		// Launch a separate thread to load the CGM entries - if configured
		doThreadLoadNightScoutEntries(true);

		// Tested Fri 4 Mar
		// Not sure this is working correctly.
		// Grid does not populate
	}

	private void addStatusLine()
	{
		// Also no longer needed
		//		m_NightScoutLoaderCore.addStatusLine();
		//		changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
	}

	private void changeStatusText(String text)
	{
		// No longer needed as core uses logger which writes to the status text
		//	m_StatusText.setText(text);
	}

	public void close()
	{
		// Save preferences (which includes the filename last used)
		PrefsNightScoutLoader.getInstance().setPreferences();

		dispose();

		m_Logger.log(Level.INFO, "*** Application Closed. ***");

		try
		{
			m_LoggerClass.release();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void rowUpdated(int rowNum)
	{
		m_RowUpdated = rowNum;
		// Called when result from DB gets modified
		DefaultTableModel model = (DefaultTableModel) m_NightScoutTable.getModel();
		// Clear current table model & re-add them all...
		model.setRowCount(0);
		for (DBResult x : m_MongoResults)
		{
			model.addRow(x.toArray(false));
		}
		model.fireTableRowsUpdated(rowNum, rowNum);
		m_NightScoutTable.repaint();
		// m_RowUpdated = -1;
	}

	public void bgUnitsChanged()
	{
		// BG Units have changed.
		// Write to the panel and reset the analyzer Settings

		m_Logger.log(Level.INFO, "BG Units have changed. Resetting Analyzer Defaults.");
		this.analyzer.resetDefaults();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() 
	{		
		m_RowUpdated = -1;

		setBounds(100, 50, 1000, 650);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// http://stackoverflow.com/questions/15258877/how-to-have-flowlayout-reposition-components-upon-resizing
		/*        frame.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
            	Main2.this.frame.remove(flowPanel); //this statement is really optional.
            	Main2.this.frame.add(flowPanel);
            }
        });*/


		aboutDialog = new WinAbout("Nightscout Loader " + Version.getInstance().getM_Version() + " - About");
		whyDialog   = new WinWhy("Nightscout Loader " + Version.getInstance().getM_Version() + " - Why");
		SettingsDialog = new WinSettings(this, "Nightscout Loader " + Version.getInstance().getM_Version() + " - Settings");
		mongoForm = new WinMongoForm(this, "Nightscout Loader " + Version.getInstance().getM_Version() + " - Find / Details");
		auditHistory = new WinAuditHistory(this, "Nightscout Loader " + Version.getInstance().getM_Version() + " - Audit History");
		//		find = new WinFind(this, "Nightscout Loader " + Version.getInstance().getM_Version() + " - Details");
		analyzer = new WinAnalyzer(this, "Nightscout Loader " + Version.getInstance().getM_Version() + " - Analyzer");


		UtilDateModel startDateModel = new UtilDateModel();
		UtilDateModel endDateModel = new UtilDateModel();

		this.setResizable(true);

		contentPane = new JPanel();
		setContentPane(contentPane);

		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel_3 = new JPanel();
		contentPane.add(panel_3, BorderLayout.NORTH);

		m_ComboBox = new JComboBox<String>();
		for (String c : m_SupportedMeters)
		{
			m_ComboBox.addItem(c);
		}

		//		m_ComboBox = new JComboBox<String>(m_SupportedMeters);  // After new Ecliplse install 28 Feb, this breaks Window Builder parsing
		m_ComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				meterSelected();
			}
		});
		panel_3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel_3.add(m_ComboBox);

		m_StartDateLbl = new JLabel("Start Date");
		panel_3.add(m_StartDateLbl);

		startDatePicker = new JDatePickerImpl(new JDatePanelImpl(startDateModel), new DateLabelFormatter());
		panel_3.add(startDatePicker);
		//        startDatePicker.getJFormattedTextField().setText(startDate);
		startDatePicker.setToolTipText("Start date range to load results.  Typically, this is 6 months in the past but can be changed.");
		// Try formatting in different way

		m_EndDateLbl = new JLabel("End Date");
		panel_3.add(m_EndDateLbl);
		endDatePicker = new JDatePickerImpl(new JDatePanelImpl(endDateModel), new DateLabelFormatter());
		panel_3.add(endDatePicker);

		//        endDatePicker.getJFormattedTextField().setText(endDate);
		endDatePicker.setToolTipText("End date range to load results.  Typically, this is 'today' but can be changed.");

		m_FileNameLbl = new JLabel("File Name");
		panel_3.add(m_FileNameLbl);

		m_FileNameTxtFld = new JTextField("", 200);
		m_FileNameTxtFld.setMinimumSize(new Dimension(50, 20));
		m_FileNameTxtFld.setPreferredSize(new Dimension(50, 20));
		panel_3.add(m_FileNameTxtFld);
		m_FileNameTxtFld.setColumns(50);

		m_FileSelectBtn = new JButton("Select");
		m_FileSelectBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				/* 		m_MeterFileChooser.setVisible(true);
        		m_FileNameTxtFld.setText(m_MeterFileChooser.getSelectedFile());
				 */ 

				String meter = new String((String)m_ComboBox.getSelectedItem());
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Select " + meter + " file.  Action ==> Synchronize will then load");

				FileNameExtensionFilter filter = null;
				
				if (meter.equals("Diasend"))
				{
					filter = new FileNameExtensionFilter("XLS Files", "xls");
				}
				else if (meter.equals("Medtronic") || meter.equals("Tandem") || meter.equals("Roche SQL Extract"))
				{
					filter = new FileNameExtensionFilter("CSV Files", "csv");
				}
				else if (meter.equals("OmniPod"))
				{
					filter = new FileNameExtensionFilter("OmniPod Files", "ibf");
				}
				
//				if (m_ComboBox.getSelectedIndex() == 1)
//				{
//					// CSV for Medtronic and XLS for Diasend
//					filter = new FileNameExtensionFilter(
//							"CSV Files", "csv");
//				}
//				else if (m_ComboBox.getSelectedIndex() == 2)
//				{
//					// CSV for Medtronic and XLS for Diasend
//					filter = new FileNameExtensionFilter(
//							"XLS Files", "xls");
//				}
//				else if (m_ComboBox.getSelectedIndex() == 3)
//				{
//					// IBF file format for OmniPod binary file
//					filter = new FileNameExtensionFilter(
//							"OmniPod Files", "ibf");
//				}


				chooser.setFileFilter(filter);
				File selectedFile = new File(m_FileNameTxtFld.getText());
				chooser.setSelectedFile(selectedFile);
				int returnVal = chooser.showOpenDialog(contentPane);        	    		
				if(returnVal == JFileChooser.APPROVE_OPTION) 
				{
					m_FileNameTxtFld.setText(chooser.getSelectedFile().getAbsolutePath());

					// Also set it in preferences too
					if (meter.equals("Medtronic"))
					{
						PrefsNightScoutLoader.getInstance().setM_MedtronicMeterPumpResultFilePath(chooser.getSelectedFile().getAbsolutePath());
					}
					else if (meter.equals("Diasend"))
					{
						PrefsNightScoutLoader.getInstance().setM_DiasendMeterPumpResultFilePath(chooser.getSelectedFile().getAbsolutePath());
					}
					else if (meter.equals("OmniPod"))
					{
						PrefsNightScoutLoader.getInstance().setM_OmniPodMeterPumpResultFilePath(chooser.getSelectedFile().getAbsolutePath());
					}
					else if (meter.equals("Roche SQL Extract"))
					{
						PrefsNightScoutLoader.getInstance().setM_RocheExtractMeterPumpResultFilePath(chooser.getSelectedFile().getAbsolutePath());
					}
					else if (meter.equals("Tandem"))
					{
						PrefsNightScoutLoader.getInstance().setM_TandemMeterPumpResultFilePath(chooser.getSelectedFile().getAbsolutePath());
					}
					
//					// Also set it in preferences too
//					if (m_ComboBox.getSelectedIndex() == 1)
//					{
//						PrefsNightScoutLoader.getInstance().setM_MedtronicMeterPumpResultFilePath(chooser.getSelectedFile().getAbsolutePath());
//					}
//					else if (m_ComboBox.getSelectedIndex() == 2)
//					{
//						PrefsNightScoutLoader.getInstance().setM_DiasendMeterPumpResultFilePath(chooser.getSelectedFile().getAbsolutePath());
//					}
//					else if (m_ComboBox.getSelectedIndex() == 3)
//					{
//						PrefsNightScoutLoader.getInstance().setM_OmniPodMeterPumpResultFilePath(chooser.getSelectedFile().getAbsolutePath());
//					}
//					else if (m_ComboBox.getSelectedIndex() == 4)
//					{
//						PrefsNightScoutLoader.getInstance().setM_RocheExtractMeterPumpResultFilePath(chooser.getSelectedFile().getAbsolutePath());
//					}

					m_Logger.log(Level.INFO, "You chose to open this file: " +
							chooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		panel_3.add(m_FileSelectBtn);

		m_CGMEntriesLoadedLbl = new JLabel("");
		m_CGMEntriesLoadedLbl.setVisible(false);
		panel_3.add(m_CGMEntriesLoadedLbl);

		lbl_TimeZone = new JLabel("");
		panel_3.add(lbl_TimeZone);
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
		panel_1.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panel_1.setBackground(Color.BLACK);

		m_NightScoutTable = new JTable();
		m_NightScoutTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		m_NightScoutTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int rowNum = m_NightScoutTable.getSelectedRow();

				if (isRowJustLoaded(rowNum))
				{
					doLoadNightScout(false);
				}

				displayMongoForm(m_MongoResults.get(rowNum), rowNum);
			}
		});
		m_NightScoutTable.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		m_NightScoutTable.setModel(new DefaultTableModel(
				DBResult.getInitializer(),
				DBResult.getColNames()
				/*

				new Object[][] {
				{null, null, null, null, null, null, null, null, null, null},
				},
				new String[] {
				"Year", "Month", "Day", "Name of Day", "Time", "Time Slot", "Result", "Result Type", "Meal Type", "Duration"
				}*/
				)

				// http://stackoverflow.com/questions/1990817/how-to-make-a-jtable-non-editable
				{

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				//Only the third column
				return column == 3;
			}}

				);

		m_NightScoutTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 6159960776850281022L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				Color col = Color.WHITE;

				// Check for modified row
				if (row == m_RowUpdated)
				{
					col = Color.YELLOW;
				}

				// Do something special here.
				// Show the progression up down the grid with new up/down buttons by highlighting the selected row
				else if (mongoForm.isVisible() == true && row == mongoForm.getM_RowNum())
				{
					col = Color.YELLOW;
				}


				// Check for added row
				/*		        else if (m_StartRowAdded != -1 && row > m_StartRowAdded)
																																								        {
																																								        	col = Color.RED;
																																								        }
				 */		
				// We found a proximity row - hightlight in Orange
				else if (isRowProximity(row))
				{
					col = Color.ORANGE;
				}

				else if (isRowJustLoaded(row))
				{
					col = Color.GREEN;
				}
				// Else stripe the rows - grey/white
				else
				{
					col = row % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE;
				}
				c.setBackground(col);
				return c;
			}
		});
		panel_1.setLayout(new BorderLayout(0, 0));

		// 17 Mar 2016
		// Saw this: http://boards.straightdope.com/sdmb/showthread.php?t=255490
		// So removed from content pane & also added to viewport
		//		contentPane/*.contentPane*/.add(m_NightScoutTable);

		//		m_NightscoutScrollPane = new JScrollPane(m_NightScoutTable);
		m_NightscoutScrollPane = new JScrollPane();
		m_NightscoutScrollPane.setEnabled(false);
		m_NightscoutScrollPane.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		m_NightscoutScrollPane.setViewportView(m_NightScoutTable);

		m_NightscoutScrollPane.setAutoscrolls(true);
		panel_1.add(m_NightscoutScrollPane);
		m_NightscoutScrollPane.setToolTipText("These are the NightScout results loaded in from the CGM in the Cloud");
		//		m_NightscoutScrollPane.setPreferredSize(new Dimension(850, m_smallScrollPaneDepth));
		m_NightscoutScrollPane.setPreferredSize(new Dimension(850, m_bigScrollPaneDepth));

		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.SOUTH);
		panel_2.setLayout(new BorderLayout(0, 0));

		m_StatusText = new JTextArea(6, 25);
		m_StatusText.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		m_StatusText.setEditable(false);
		m_StatusText.setFont(new Font("Courier", Font.PLAIN, 14));
		//		m_StatusText.setPreferredSize(new Dimension(850, m_StatusTextPaneDepth));
		//panel_2.add(textArea);

		// Register the text area for log output
		m_LoggerClass.addJtextAreaOutput(m_StatusText);

		JScrollPane scrollPane = new JScrollPane(m_StatusText);
		scrollPane.setPreferredSize(new Dimension(850, m_StatusTextPaneDepth));
		panel_2.add(scrollPane);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		mnFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				Boolean synchThreadsRunning = m_NightScoutLoaderCore.isLoadOrDiffThreadRunning();

				// Also disable the DB access menu if threads are running
				m_mntmExportResults.setEnabled(synchThreadsRunning ? false : true);
			}
		});

		menuBar.add(mnFile);

		m_mntmExportResults = new JMenuItem("Export Results");
		m_mntmExportResults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doExportResults();
			}
		});
		mnFile.add(m_mntmExportResults);

		JMenuItem mntmDownloadTreatments = new JMenuItem("Download Treatment Data");
		mntmDownloadTreatments.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doDownloadTreatmentData();
			}
		});
		mnFile.add(mntmDownloadTreatments);

		JMenuItem mntmDownloadSensor = new JMenuItem("Download Sensor Data");
		mntmDownloadSensor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doDownloadSensorData();
			}
		});
		mnFile.add(mntmDownloadSensor);


		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close();
			}
		});
		/*		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.action(EX, arg1)
			}
		});*/
		mnFile.add(mntmExit);

		JMenu mnAction = new JMenu("Action");
		mnAction.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				Boolean advancedSettings = PrefsNightScoutLoader.getInstance().isM_AdvancedSettings();
				Boolean synchThreadsRunning  = m_NightScoutLoaderCore.isLoadOrDiffThreadRunning();
				Boolean analyzeThreadsRunning = m_NightScoutLoaderCore.isAnalyzeThreadRunning();

				m_Logger.log(Level.FINER, "NightScoutLoader.ActionMenuHandler: Adv: " + advancedSettings + " Sync: " + synchThreadsRunning);

				// Disable the Delete menu item if Advanced option is not set.
				m_mntmDeleteLoadedTreatments.setEnabled(advancedSettings && ((synchThreadsRunning  || analyzeThreadsRunning) ? false : true));

				// Also disable the DB access menu if threads are running
				m_mntmSychronize.setEnabled(synchThreadsRunning ? false : true);
				m_mntmLoadMeterPumpOnly.setEnabled(synchThreadsRunning ? false : true);
				m_mntmLoadNightscout.setEnabled(synchThreadsRunning ? false : true);
				m_mntmAnalyzeResults.setEnabled(synchThreadsRunning ? false : true);
			}
		});
		mnAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				// Disable the Delete menu item if Advanced option is not set.
				Boolean advancedSettings = PrefsNightScoutLoader.getInstance().isM_AdvancedSettings();
				m_mntmDeleteLoadedTreatments.setEnabled(advancedSettings);
			}
		});
		menuBar.add(mnAction);

		m_mntmSychronize = new JMenuItem("Synchronize");
		m_mntmSychronize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doSynchronize();
			}
		});
		mnAction.add(m_mntmSychronize);

		m_mntmLoadMeterPumpOnly = new JMenuItem("Load Meter/Pump Only");
		m_mntmLoadMeterPumpOnly.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doLoadMeterPumpOnly();
			}
		});
		mnAction.add(m_mntmLoadMeterPumpOnly);

		m_mntmLoadNightscout = new JMenuItem("Load Nightscout");
		m_mntmLoadNightscout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doLoadNightScout(false);
			}
		});
		mnAction.add(m_mntmLoadNightscout);

		m_mntmAnalyzeResults = new JMenuItem("Analyze Results");
		m_mntmAnalyzeResults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doAnalyseResults();
			}
		});
		mnAction.add(m_mntmAnalyzeResults);

		m_mntmDeleteLoadedTreatments = new JMenuItem("Delete Loaded Treatments");
		Boolean advancedSettings = PrefsNightScoutLoader.getInstance().isM_AdvancedSettings();
		m_mntmDeleteLoadedTreatments.setEnabled(advancedSettings);
		m_mntmDeleteLoadedTreatments.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doDeleteLoadedTreatments();
			}
		});
		mnAction.add(m_mntmDeleteLoadedTreatments);

		JMenuItem mntmFind = new JMenuItem("Find");
		mntmFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mongoForm.initialize(m_MongoResults, m_MongoResults.get(0), m_RowUpdated < 0 ? 0 : m_RowUpdated);
				mongoForm.setVisible(true);
			}
		});
		mnAction.add(mntmFind);


		JMenu mnView = new JMenu("View");
		JMenuItem mntmAuditHistory = new JMenuItem("Audit History");
		mntmAuditHistory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				auditHistory.setVisible(true);
			}
		});
		mnView.add(mntmAuditHistory);
		menuBar.add(mnView);

		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);

		JMenuItem mntmSettings = new JMenuItem("Settings");
		mntmSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SettingsDialog.setVisible(true);
			}
		});
		mnTools.add(mntmSettings);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmOnlineHelp = new JMenuItem("Online Help");
		mntmOnlineHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				URI onlineHelpURI = null;
				try 
				{
					onlineHelpURI = new URI(Version.getInstance().getM_GoogleDriveHelpURI());
				} 
				catch (URISyntaxException e1) 
				{
					m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + " Help: Exception caught.  Unable to construct URI from parameters. " + e1.getMessage());
				}
				try 
				{
					Desktop desktop;
					if (Desktop.isDesktopSupported() 
							&& (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.BROWSE)) 
					{
						desktop.browse(onlineHelpURI);
					}
					else
					{
						m_Logger.log(Level.WARNING, "<"+this.getClass().getName()+">" + " Help: Browser not supported by Desktop ");
						JOptionPane.showMessageDialog(null, "Unable to launch browser with online help");	
					}
				} 
				catch (IOException e) 
				{
					m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + " Help: Exception caught.  Unable to launch native Email client for feedback message. " + e.getMessage());
				}
			}
		});
		mnHelp.add(mntmOnlineHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				aboutDialog.setVisible(true);
			}
		});
		mnHelp.add(mntmAbout);

		JMenuItem mntmWhy = new JMenuItem("Why");
		mntmWhy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				whyDialog.setVisible(true);
			}
		});
		mnHelp.add(mntmWhy);

		JMenuItem mntmFeedback = new JMenuItem("Feedback");
		mntmFeedback.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				// http://stackoverflow.com/questions/2357895/java-open-default-mail-application-and-create-new-mail-and-populate-to-and-subj
				Desktop desktop;
				if (Desktop.isDesktopSupported() 
						&& (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.MAIL)) 
				{
					URI mailto = null;
					try 
					{
						mailto = new URI("mailto:nightscoutloader@gmail.com?subject=NightScoutloader%20" 
								+ Version.getInstance().getM_Version() + "%20Feedback&body=Hi%20David,");
					} 
					catch (URISyntaxException e1) 
					{
						m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + " Feedback: Exception caught.  Unable to construct URI from parameters. " + e1.getMessage());
					}
					try 
					{
						desktop.mail(mailto);
					} 
					catch (IOException e) 
					{
						m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + " Feedback: Exception caught.  Unable to launch native Email client for feedback message. " + e.getMessage());
					}
				} 
				else
				{
					m_Logger.log(Level.WARNING, "<"+this.getClass().getName()+">" + " Help: Browser not supported by Desktop ");
					JOptionPane.showMessageDialog(null, "Unable to launch mail client for support help.\nPlease contact nightscoutloader@gmail.com manually");	
				}
			}
		});
		mnHelp.add(mntmFeedback);

		JMenuItem mntmDetailedHelp = new JMenuItem("Offline Help");
		mntmDetailedHelp.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				if (m_ThreadHelpLauncher == null)
				{
					m_ThreadHelpLauncher = new ThreadHelpLauncher();
				}
				m_ThreadHelpLauncher.addHelpRequest("/NightscoutLoader.pdf");
			}
		});
		mnHelp.add(mntmDetailedHelp);

		
		// When fully initialized, come back and set the timezone label
		EventQueue.invokeLater(new 
				Runnable()
		{ 
			public void run()
			{ 
				checkTimeZone();
			}
		});

		//	m_EntriesGraphic = new Graphics();
	}

	public void helperLaunched()
	{

	}

	private int getPrefMeterIndex()
	{
		int result = -1;
		int x = 0;
		for (String c : m_SupportedMeters)
		{
			if (c.equals(PrefsNightScoutLoader.getInstance().getM_SelectedMeter()))
			{
				result = x;
			}
			x++;
		}
		return result;
	}
	public void applyConfig()
	{
		// Set the meter last used
		int prefMeter = getPrefMeterIndex();
		if (prefMeter > -1)
		{
			m_ComboBox.setSelectedIndex(prefMeter);
		}

		// Set start & end dates for SQL Query
		Calendar now  = Calendar.getInstance();
		Calendar then = Calendar.getInstance();
		then.add(Calendar.DATE, (-1) * PrefsNightScoutLoader.getInstance().getM_DaysToLoad());
		//		final DateFormat format = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
		final DateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

		((UtilDateModel)startDatePicker.getModel()).setValue(then.getTime());
		((UtilDateModel)endDatePicker.getModel()).setValue(now.getTime());

		String startDate = new String(format.format(then.getTime()));
		String endDate = new String(format.format(now.getTime()));

		startDatePicker.getJFormattedTextField().setText(startDate);
		endDatePicker.getJFormattedTextField().setText(endDate);

		//		// Set the text filename if Medtronic is used.
		//		// Initialise text field from preferences
		//		m_FileNameTxtFld.setText(PrefsNightScoutLoader.getInstance().getM_MedtronicMeterPumpResultFilePath());

	}


	private void doThreadLoadNightScout(Boolean initialRun)
	{
		reShapeWindowForNightScoutOnly();

		try
		{
			Object obj = new Boolean(initialRun);
			m_NightScoutLoaderCore.threadLoadNightScout(
					new ThreadDataLoad.DataLoadCompleteHander(obj) 
					{

						//		@Override
						public void exceptionRaised(String message) 
						{
							m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "doThreadLoadMeterPump: Just caught an exception" + message);
						}

						//		@Override
						public void dataLoadComplete(Object obj, String message) 
						{
							Boolean initialRun = (Boolean)obj;
							m_MongoResults = m_NightScoutLoaderCore.getM_DataLoadNightScout().getResultsFromDB();
							//m_MongoResults = m_NightScoutLoaderCore.getM_ResultsMongoDB();

							// Swing is not threadsafe, so add a request to update the grid onto the even queue
							// Found this technique here:
							// http://www.informit.com/articles/article.aspx?p=26326&seqNum=9
							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									updateGrid();
								}
							});

							// Do a background analysis check at this point only for initial run
							// Synchs (which also use this thread Mongo Load) will perform check after
							// differences are determined.
							if (initialRun == true && m_MongoResults != null && m_MongoResults.size() > 0)
							{
								EventQueue.invokeLater(new 
										Runnable()
								{ 
									public void run()
									{
										doBackgroundAnalysis();										
										// analyseResults();
									}
								});								
							}

							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									//									addStatusLine();
								}
							});
						}
					});	
		}

		catch(Exception e)
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + " doThreadLoadNightScout: just caught an error: " + e.getMessage() + "-" + e.getLocalizedMessage());		
		}
	}


	private void doThreadLoadNightScoutEntries(Boolean initialRun)
	{
		// We only attempt a CGM load if the preferences are enabled
		if (PrefsNightScoutLoader.getInstance().getM_LoadNightscoutEntries() == true)
		{
			try
			{
				Object obj = new Boolean(initialRun);
				m_NightScoutLoaderCore.threadLoadNightScoutEntries(
						new ThreadDataLoad.DataLoadCompleteHander(obj) 
						{

							//		@Override
							public void exceptionRaised(String message) 
							{
								m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "doThreadLoadMeterPump: Just caught an exception" + message);
							}

							//		@Override
							public void dataLoadComplete(Object obj, String message) 
							{
								Boolean initialRun = (Boolean)obj;
								m_MongoResultEntries = m_NightScoutLoaderCore.getM_DataLoadNightScoutEntries().getResultsFromDB();
								//m_MongoResults = m_NightScoutLoaderCore.getM_ResultsMongoDB();

								// Swing is not threadsafe, so add a request to update the grid onto the even queue
								// Found this technique here:
								// http://www.informit.com/articles/article.aspx?p=26326&seqNum=9
								EventQueue.invokeLater(new 
										Runnable()
								{ 
									public void run()
									{ 
										m_CGMEntriesLoadedLbl.setText("CGM : " + NumberFormat.getIntegerInstance().format(m_MongoResultEntries.size()));
										m_CGMEntriesLoadedLbl.setVisible(true);
										m_CGMEntriesLoadedLbl.setForeground(m_MongoResultEntries.size() == 0 ? Color.RED : Color.BLUE); // setBackground(Color.YELLOW);
									}
								});

								EventQueue.invokeLater(new 
										Runnable()
								{ 
									public void run()
									{ 
										//									addStatusLine();
									}
								});
							}
						});	
			}

			catch(Exception e)
			{
				m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + " doThreadLoadNightScout: just caught an error: " + e.getMessage() + "-" + e.getLocalizedMessage());		
			}
		}
	}

	public void doLoadNightScout(Boolean initialRun)
	{
		reShapeWindowForNightScoutOnly();
		addStatusLine();
		loadNightScout();
		updateGrid();

		if (initialRun == true && m_MongoResults != null && m_MongoResults.size() > 0)
		{
			// David 28 Jul
			//			analyseResults();
		}

		addStatusLine();
	}

	private void doThreadLoadRoche()
	{
		try
		{
			// Increment the end date by one since it will assume midnight of current day hence omitting
			// today's values
			Date startDate = (Date)startDatePicker.getModel().getValue();
			Date endDate   = (Date)endDatePicker.getModel().getValue();
			Calendar c = Calendar.getInstance();
			c.setTime(endDate);
			c.add(Calendar.DATE, 1); // Add one day

			// Threaded load instead
			m_NightScoutLoaderCore.threadLoadRocheMeterPump(startDate, c.getTime(),
					new ThreadDataLoad.DataLoadCompleteHander(null) 
			{
				public void exceptionRaised(String message) { }
				public void dataLoadComplete(Object obj, String message) 
				{ 
					// Refresh grid if meter/pump load only
					if (m_NightScoutLoaderCore.isM_MeterPumpLoadOnly() == true)
					{
						m_MongoResults = m_NightScoutLoaderCore.getM_DataLoadNightScout().getResultsFromDB();
						m_SaveDiffMessage = message;

						EventQueue.invokeLater(new 
								Runnable()
						{ 
							public void run()
							{ 
								// 1 kick off background analysis check
								// 2 refresh grid
								// 3 Generate a message
								// Do in this order since grid refresh resets the m_MeterPumpLoadOnly flag
								doBackgroundAnalysis();										
								updateGrid();
								JOptionPane.showMessageDialog(null, m_SaveDiffMessage);									
							}
						});
					}
				}
			});
		}
		catch (Exception e)
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "doThreadLoadMeterPump: Just caught an exception" + e.getMessage() + "-" + e.getLocalizedMessage());		
		}

	}
	//
	//	private void doThreadLoadMedtronic(String file)
	//	{
	//		try
	//		{
	//			// Threaded load instead
	//			m_NightScoutLoaderCore.threadLoadMedtronicMeterPump(m_FileNameTxtFld.getText(),
	//					new ThreadDataLoad.DataLoadCompleteHander(null) 
	//			{
	//				public void exceptionRaised(String message) { }
	//				public void dataLoadComplete(Object obj, String message) 
	//				{
	//					// Refresh grid if meter/pump load only
	//					if (m_NightScoutLoaderCore.isM_MeterPumpLoadOnly() == true)
	//					{
	//						m_MongoResults = m_NightScoutLoaderCore.getM_DataLoadMongoDB().getResultsFromDB();
	//						m_SaveDiffMessage = message;
	//
	//						EventQueue.invokeLater(new 
	//								Runnable()
	//						{ 
	//							public void run()
	//							{ 
	//								// 1 kick off background analysis check
	//								// 2 refresh grid
	//								// 3 Generate a message
	//								// Do in this order since grid refresh resets the m_MeterPumpLoadOnly flag
	//								doBackgroundAnalysis();										
	//								updateGrid();
	//								JOptionPane.showMessageDialog(null, m_SaveDiffMessage);									
	//							}
	//						});
	//					}
	//
	//				}
	//			});
	//
	//		}
	//		catch (Exception e)
	//		{
	//			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "doThreadLoadMedtronic: Just caught an exception" + e.getMessage() + "-" + e.getLocalizedMessage());		
	//		}
	//
	//	}
	//


	//	private void doThreadLoadDiasend(String file)
	//	{
	//		try
	//		{
	//			m_NightScoutLoaderCore.threadLoadDiasendMeterPump(m_FileNameTxtFld.getText(),
	//					new ThreadDataLoad.DataLoadCompleteHander(null) 
	//			{
	//				public void exceptionRaised(String message) { }
	//				public void dataLoadComplete(Object obj, String message) 
	//				{
	//					// Refresh grid if meter/pump load only
	//					if (m_NightScoutLoaderCore.isM_MeterPumpLoadOnly() == true)
	//					{
	//						m_MongoResults = m_NightScoutLoaderCore.getM_DataLoadMongoDB().getResultsFromDB();
	//						m_SaveDiffMessage = message;
	//
	//						EventQueue.invokeLater(new 
	//								Runnable()
	//						{ 
	//							public void run()
	//							{ 
	//								// 1 kick off background analysis check
	//								// 2 refresh grid
	//								// 3 Generate a message
	//								// Do in this order since grid refresh resets the m_MeterPumpLoadOnly flag
	//								doBackgroundAnalysis();										
	//								updateGrid();
	//								JOptionPane.showMessageDialog(null, m_SaveDiffMessage);									
	//							}
	//						});
	//					}
	//
	//				}
	//			});	
	//
	//		}
	//		catch (Exception e)
	//		{
	//			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "doThreadLoadDiasend: Just caught an exception" + e.getMessage() + "-" + e.getLocalizedMessage());		
	//		}
	//	}

	private void doThreadLoadFile(String file, FileChecker.FileCheckType fileType)
	{
		try
		{
			if (fileType == FileChecker.FileCheckType.Medtronic)
			{
				/*
				m_NightScoutLoaderCore.loadMedtronicMeterPump(m_FileNameTxtFld.getText());
				changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
				 */

				// Threaded load instead
				m_NightScoutLoaderCore.threadLoadMedtronicMeterPump(m_FileNameTxtFld.getText(),
						new ThreadDataLoad.DataLoadCompleteHander(null) 
				{
					public void exceptionRaised(String message) { }
					public void dataLoadComplete(Object obj, String message) 
					{
						// Refresh grid if meter/pump load only
						if (m_NightScoutLoaderCore.isM_MeterPumpLoadOnly() == true)
						{
							m_MongoResults = m_NightScoutLoaderCore.getM_DataLoadNightScout().getResultsFromDB();
							m_SaveDiffMessage = message;

							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									// 1 kick off background analysis check
									// 2 refresh grid
									// 3 Generate a message
									// Do in this order since grid refresh resets the m_MeterPumpLoadOnly flag
									doBackgroundAnalysis();										
									updateGrid();
									JOptionPane.showMessageDialog(null, m_SaveDiffMessage);									
								}
							});
						}

					}
				});
			}
			else if (fileType == FileChecker.FileCheckType.Diasend)
			{
				/*
					m_NightScoutLoaderCore.loadDiasendMeterPump(m_FileNameTxtFld.getText());
					changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
				 */

				// Threaded load instead
				m_NightScoutLoaderCore.threadLoadDiasendMeterPump(m_FileNameTxtFld.getText(),
						new ThreadDataLoad.DataLoadCompleteHander(null) 
				{
					public void exceptionRaised(String message) { }
					public void dataLoadComplete(Object obj, String message) 
					{ 
						// Refresh grid if meter/pump load only
						if (m_NightScoutLoaderCore.isM_MeterPumpLoadOnly() == true)
						{
							m_MongoResults = m_NightScoutLoaderCore.getM_DataLoadNightScout().getResultsFromDB();
							m_SaveDiffMessage = message;

							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									// 1 kick off background analysis check
									// 2 refresh grid
									// 3 Generate a message
									// Do in this order since grid refresh resets the m_MeterPumpLoadOnly flag
									doBackgroundAnalysis();										
									updateGrid();
									JOptionPane.showMessageDialog(null, m_SaveDiffMessage);									
								}
							});
						}

					}
				});
			}

			else if (fileType == FileChecker.FileCheckType.OmniPod)
			{
				m_NightScoutLoaderCore.threadLoadOmniPodMeterPump(m_FileNameTxtFld.getText(),
						new ThreadDataLoad.DataLoadCompleteHander(null) 
				{
					public void exceptionRaised(String message) { }
					public void dataLoadComplete(Object obj, String message) 
					{
						// Refresh grid if meter/pump load only
						if (m_NightScoutLoaderCore.isM_MeterPumpLoadOnly() == true)
						{
							m_MongoResults = m_NightScoutLoaderCore.getM_DataLoadNightScout().getResultsFromDB();
							m_SaveDiffMessage = message;

							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									// 1 kick off background analysis check
									// 2 refresh grid
									// 3 Generate a message
									// Do in this order since grid refresh resets the m_MeterPumpLoadOnly flag
									doBackgroundAnalysis();										
									updateGrid();
									JOptionPane.showMessageDialog(null, m_SaveDiffMessage);									
								}
							});
						}

					}
				});	
			}

			else if (fileType == FileChecker.FileCheckType.RocheSQLExtract)
			{
				m_NightScoutLoaderCore.threadLoadRocheMeterPump(m_FileNameTxtFld.getText(),
						new ThreadDataLoad.DataLoadCompleteHander(null) 
				{
					public void exceptionRaised(String message) { }
					public void dataLoadComplete(Object obj, String message) 
					{
						// Refresh grid if meter/pump load only
						if (m_NightScoutLoaderCore.isM_MeterPumpLoadOnly() == true)
						{
							m_MongoResults = m_NightScoutLoaderCore.getM_DataLoadNightScout().getResultsFromDB();
							m_SaveDiffMessage = message;

							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									// 1 kick off background analysis check
									// 2 refresh grid
									// 3 Generate a message
									// Do in this order since grid refresh resets the m_MeterPumpLoadOnly flag
									doBackgroundAnalysis();										
									updateGrid();
									JOptionPane.showMessageDialog(null, m_SaveDiffMessage);									
								}
							});
						}

					}
				});	
			}

		}
		catch (Exception e)
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "doThreadFile: Just caught an exception" + e.getMessage() + "-" + e.getLocalizedMessage());		
		}

	}

	//	private void doThreadLoadOmniPod(String file)
	//	{
	//		try
	//		{
	//			m_NightScoutLoaderCore.threadLoadOmniPodMeterPump(m_FileNameTxtFld.getText(),
	//					new ThreadDataLoad.DataLoadCompleteHander(null) 
	//			{
	//				public void exceptionRaised(String message) { }
	//				public void dataLoadComplete(Object obj, String message) 
	//				{
	//					// Refresh grid if meter/pump load only
	//					if (m_NightScoutLoaderCore.isM_MeterPumpLoadOnly() == true)
	//					{
	//						m_MongoResults = m_NightScoutLoaderCore.getM_DataLoadMongoDB().getResultsFromDB();
	//						m_SaveDiffMessage = message;
	//
	//						EventQueue.invokeLater(new 
	//								Runnable()
	//						{ 
	//							public void run()
	//							{ 
	//								// 1 kick off background analysis check
	//								// 2 refresh grid
	//								// 3 Generate a message
	//								// Do in this order since grid refresh resets the m_MeterPumpLoadOnly flag
	//								doBackgroundAnalysis();										
	//								updateGrid();
	//								JOptionPane.showMessageDialog(null, m_SaveDiffMessage);									
	//							}
	//						});
	//					}
	//
	//				}
	//			});	
	//
	//		}
	//		catch (Exception e)
	//		{
	//			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "doThreadLoadOmniPod: Just caught an exception" + e.getMessage() + "-" + e.getLocalizedMessage());		
	//		}
	//	}

	private boolean offerToLoadCorrectFileFormat(String file, FileChecker.FileCheckType desiredFileType)
	{
		boolean result = true;  // thread started 

		try
		{
			// Check file type presented
			FileChecker.FileCheckType fileType = FileChecker.checkFile(m_FileNameTxtFld.getText());

			if ( (fileType == FileChecker.FileCheckType.Medtronic &&
					desiredFileType == FileChecker.FileCheckType.Medtronic) ||
					(fileType == FileChecker.FileCheckType.Diasend &&
					desiredFileType == FileChecker.FileCheckType.Diasend) ||
					(fileType == FileChecker.FileCheckType.OmniPod &&
					desiredFileType == FileChecker.FileCheckType.OmniPod) ||
					(fileType == FileChecker.FileCheckType.RocheSQLExtract &&
					desiredFileType == FileChecker.FileCheckType.RocheSQLExtract) )
			{
				doThreadLoadFile(file, fileType);
			}

			else if (fileType == FileChecker.FileCheckType.INVALID)
			{
				JDialog.setDefaultLookAndFeelDecorated(true);
				JOptionPane.showMessageDialog(null, 
						"Option selected to load " + FileChecker.getFileTypeStr(desiredFileType) + " file :\n\n" + m_FileNameTxtFld.getText() + 
						"\n\n" +
						"However this file is in an unrecognised format.\n" +
						"Please export results from " + FileChecker.getFileTypeStr(desiredFileType) + " software in correct format.");
				result = false;
			}
			else 
			{
				JDialog.setDefaultLookAndFeelDecorated(true);
				int response = JOptionPane.showConfirmDialog(null, 
						"Option selected to load " + FileChecker.getFileTypeStr(desiredFileType) + " file :\n\n" + m_FileNameTxtFld.getText() + 
						"\n\n" +
						"However this file appears to hold " + FileChecker.getFileTypeStr(fileType) + " content" +
						"\n\n" +
						"Do you want to load " + FileChecker.getFileTypeStr(fileType) + " information instead?", 
						"Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.YES_OPTION)
				{
					doThreadLoadFile(file, fileType);
				}
				else
				{
					result = false;
				}
			}
		}

		catch (Exception e)
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "doThreadLoadMeterPump: Just caught an exception" + e.getMessage() + "-" + e.getLocalizedMessage());		
		}

		return result;

	}

	private boolean doThreadLoadMeterPump()
	{
		boolean result = true;  // Thread started
		// Now try resizing the components we want to hide eventually

		/*
		 * David Re-design, just one grid,
		 */
		//        m_MeterTable.setVisible(true);
		//        m_MeterScrollPane.setVisible(true);
		//        m_NightscoutScrollPane.setPreferredSize(new Dimension(850, m_smallScrollPaneDepth));
		//        m_NightscoutScrollPane.revalidate();

		revalidate();
		//        JOptionPane.showMessageDialog(null, "Load Meter Pump selected", "InfoBox: " + " Action Selected", JOptionPane.INFORMATION_MESSAGE);

		// If Roche is selected, then enable the date ranges
		if (m_ComboBox.getSelectedIndex() == 0)
		{			
			doThreadLoadRoche();
		}

		// In all other cases, we load from a file
		else if (m_ComboBox.getSelectedIndex() == 1)
		{
			result = offerToLoadCorrectFileFormat(m_FileNameTxtFld.getText(), FileChecker.FileCheckType.Medtronic);
		}
		else if (m_ComboBox.getSelectedIndex() == 2)
		{
			result = offerToLoadCorrectFileFormat(m_FileNameTxtFld.getText(), FileChecker.FileCheckType.Diasend);
		}
		else if (m_ComboBox.getSelectedIndex() == 3)
		{
			// Only enable the parameters that use MongoDB for Roche if it's Davids laptop
			boolean davidsLaptop = PrefsNightScoutLoader.isItDavidsLaptop();

			// For now, only I can do this on the laptop development machine...
			if (davidsLaptop)
			{
			result = offerToLoadCorrectFileFormat(m_FileNameTxtFld.getText(), FileChecker.FileCheckType.OmniPod);
			}
			else
			{
				JDialog.setDefaultLookAndFeelDecorated(true);
				JOptionPane.showMessageDialog(null, 
						"Please note that OmniPod loads are not available yet.  This menu is here to allow development to proceed.");
				result = false;
			}
		}
		else if (m_ComboBox.getSelectedIndex() == 4)
		{
			result = offerToLoadCorrectFileFormat(m_FileNameTxtFld.getText(), FileChecker.FileCheckType.RocheSQLExtract);
		}

		// Update Status on window
		changeStatusText(m_NightScoutLoaderCore.getM_StatusText());

		return result;
	}


	private void doThreadDetermineSaveDifferences()
	{
		try
		{
			Object obj = new Boolean(false);
			//			Date startDate = (Date)startDatePicker.getModel().getValue();
			//			Date endDate   = (Date)endDatePicker.getModel().getValue();

			m_NightScoutLoaderCore.threadDetermineSaveDifferences(
					new ThreadDetermineSaveDifferences.DataLoadCompleteHander(obj) 
					{

						//		@Override
						public void exceptionRaised(String message) 
						{
							m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "doThreadDetermineSaveDifferences: Just caught an exception" + message);
						}

						//		@Override
						public void operationComplete(Object obj, String message) 
						{
							//							Boolean initialRun = (Boolean)obj;
							m_MongoResults = m_NightScoutLoaderCore.getM_DataLoadNightScout().getResultsFromDB();
							//m_MongoResults = m_NightScoutLoaderCore.getM_ResultsMongoDB();

							m_SaveDiffMessage = message;

							// Swing is not threadsafe, so add a request to update the grid onto the even queue
							// Found this technique here:
							// http://www.informit.com/articles/article.aspx?p=26326&seqNum=9
							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									JOptionPane.showMessageDialog(null, m_SaveDiffMessage);

									updateGrid();		

									// Update the Audit History Grid too
									auditHistory.updateGrid();
								}
							});

							// Can't pass a parameter through to an interface
							//							if (initialRun == true && m_MongoResults != null && m_MongoResults.size() > 0)
							if (m_MongoResults != null && m_MongoResults.size() > 0)
							{
								EventQueue.invokeLater(new 
										Runnable()
								{ 
									public void run()
									{ 
										// No need to analyze results
										// analyseResults();

										// Yes!!
										// Now launch a thread to do summarised analysis
										doBackgroundAnalysis();
									}
								});	
							}

							EventQueue.invokeLater(new 
									Runnable()
							{ 
								public void run()
								{ 
									//									addStatusLine();
								}
							});
						}
					});	
		}
		catch(Exception e)
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "doThreadDetermineSaveDifferences: Just caught an exception" + e.getMessage() + "-" + e.getLocalizedMessage());		
		}

	}

	private void doBackgroundAnalysis()
	{
		// We kick off 2 analysis threads
		// One summarising the last 2 weeks and the other across all results
		// The seond full range one is used for comparison by an actively selected analysis
		m_NightScoutLoaderCore.doSummarisedThreadAnalyzeResults(
				new AnalyzerCompleteHander(this)
				{
					//		@Override
					public void exceptionRaised(String message) 
					{
						;
					}

					//		@Override
					public void analyzeResultsComplete(Object obj) 
					{
					}
				}
				);

		// Kick off the full history analysis in parallel too
		doFullHistoryBackgroundAnalysis();
	}

	private void doFullHistoryBackgroundAnalysis()
	{
		// We kick off 2 analysis threads
		// One summarising the last 2 weeks and the other across all results
		// The seond full range one is used for comparison by an actively selected analysis
		m_NightScoutLoaderCore.doFullHistoryThreadAnalyzeResults(
				new AnalyzerCompleteHander(this)
				{
					//		@Override
					public void exceptionRaised(String message) 
					{
						;
					}

					//		@Override
					public void analyzeResultsComplete(Object obj) 
					{
						;
						// Nothing to do here since results are written to INFO and hence appear on the panel.
					}
				}
				);
	}


	private void doThreadedSynchronize()
	{
		// doThreadLoadRocheMeterPump(); Obsolete already - used to get Roche loader working in own thread

		// 1 Tell core nightscout that this is NOT meter/pump only load
		this.m_NightScoutLoaderCore.setM_MeterPumpLoadOnly(false);

		// Start thread 1 loading meter/pump data
		if (doThreadLoadMeterPump())
		{
			// Start thread 2 loading nightscout data
			doThreadLoadNightScout(false);
			// Start thread 3 that will compare results and save differences
			doThreadDetermineSaveDifferences();
		}
	}

	private void doThreadedLoadMeterPumpOnly()
	{
		// 1 Tell core nightscout that this is meter/pump only load
		this.m_NightScoutLoaderCore.setM_MeterPumpLoadOnly(true);

		// Start thread 1 loading meter/pump data
		doThreadLoadMeterPump();

		// Need some way on completion of thread above to prepare grid for display
		// That's what the threadDetermineSaveDifferences() does!
	}

	private void doSynchronize()
	{
		// Always do threaded synchronize
		doThreadedSynchronize();

		// David 25 Apr 2016 commented out below
		// While debugging odd behavior
		//   Same file presenting extra rows
		//   Delete then synch not storing anything

		//		// If Roche, then use multi-threads..
		//		if (m_ComboBox.getSelectedIndex() == 0)
		//		{		
		//			// Check if threads already running.
		//			if (m_NightScoutLoaderCore.getM_ThreadDetermineSaveDifferences() == null)
		//			{
		//				doThreadedSynchronize();
		//			}
		//		}
		//		else
		//		{
		//			addStatusLine();
		//			loadMeterPump();
		//			loadNightScout();
		//			determineDifferences();
		//			saveDifferences();
		//			updateGrid();
		//			addStatusLine();
		//		}
	}

	private void doLoadMeterPumpOnly()
	{
		// Always do threaded synchronize
		doThreadedLoadMeterPumpOnly();
	}


	private void doAnalyseResults()
	{
		// New way - now drive through new window

		// However, only show this scarily complicated window if advanced features is set
		// otherwise, just do the analysis for users.
		if (PrefsNightScoutLoader.getInstance().isM_AdvancedSettings() == true)
		{
			analyzer.setVisible(true);
		}
		else
		{
			// Only do analysis if there are results
			if (m_MongoResults != null && m_MongoResults.size() > 0)
			{
				// First off, ensure that the dates are set correctly.
				resetAnalyzeDateRange();

				analyzer.threadAnalyze();
			}
		}
	}

	private void doDeleteLoadedTreatments()
	{
		// Popup a confirmation dialog first ...
		JDialog.setDefaultLookAndFeelDecorated(true);
		int beforeTotalEntryCount = m_NightScoutLoaderCore.getM_DataLoadNightScout().getResultsFromDB().size();
		int beforeNSLEntryCount   = m_NightScoutLoaderCore.countNightScoutTreatments();

		if (beforeNSLEntryCount > 0)
		{
			int response = JOptionPane.showConfirmDialog(null, "Please confirm:\nThere are " + beforeTotalEntryCount +
					" total Treatment entries in Nightscout\n\nDo you want to delete ALL " + beforeNSLEntryCount +
					" Treatment entries loaded by this application?", "Confirm",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.YES_OPTION)
			{
				// Hide the edit form if displayed since it will become invalid with the delete ... potentially :-)			
				mongoForm.setVisible(false);

				addStatusLine();
				deleteLoadedTreatments();
				doThreadLoadNightScout(false);
				//				loadNightScout();
				//				updateGrid();
				//				addStatusLine();
			}
		}
		else
		{
			// Check if connected first
			if (m_NightScoutLoaderCore.getM_DataLoadNightScout().getM_ServerState() == DataLoadNightScoutTreatments.MongoDBServerStateEnum.not_accessible)
			{
				JOptionPane.showMessageDialog(null, "Currently not connected to MongoDB and so can't delete");

			}
			else
			{
				JOptionPane.showMessageDialog(null, "There are " + beforeTotalEntryCount +
						" total Treatment entries in Nightscout and none loaded by this application.\n\nNothing to delete!");
			}
		}
	}

	private void doExportResults()
	{
		// Extra check as menu might not have refreshed
		if (!m_NightScoutLoaderCore.isLoadOrDiffThreadRunning())
		{
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"Excel 97-2003 Workbook", "xls");
			chooser.setFileFilter(filter);
			File selectedFile = new File(PrefsNightScoutLoader.getInstance().getM_ExportFilePath());
			chooser.setSelectedFile(selectedFile);
			int returnVal = chooser.showSaveDialog(contentPane);        	    		
			if(returnVal == JFileChooser.APPROVE_OPTION) 
			{
				String chosenFile = new String(chooser.getSelectedFile().getAbsolutePath());
				// Always append '.xls' to filename if not there already!
				String endName = new String(chosenFile.substring(chosenFile.length() - 4).toLowerCase());
				if (!endName.equals(".xls"))
				{
					chosenFile += ".xls";
				}

				// Handle if file exists
				if (confirmWriteFile(chosenFile, "Excel Export"))
				{
					m_Logger.log(Level.INFO, "You chose to export Treatments to this file: " +
							chooser.getSelectedFile().getAbsolutePath());

					// Also set it in preferences too
					PrefsNightScoutLoader.getInstance().setM_ExportFilePath(chooser.getSelectedFile().getAbsolutePath());

					addStatusLine();
					try
					{
						m_NightScoutLoaderCore.exportResults((DefaultTableModel) m_NightScoutTable.getModel(), chosenFile);

						// Now launch Excel!
						// http://www.coderanch.com/t/446476/java/java/open-excel-file-java-program
						//						Runtime.getRuntime().exec("cmd /c start " + "\"" + chosenFile + "\"");

						// http://www.coderanch.com/t/446476/java/java/open-excel-file-java-program
						// (Scroll down a little more!!)
						Desktop.getDesktop().open(new File(chosenFile));
					}
					catch (IOException e) 
					{
						m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + ".doExportResults: just caught an error exporting grid: " + e.getMessage() + " - " + e.getLocalizedMessage());
					}
					addStatusLine();
					// Update Status on window
					changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
				}
			}
		}
	}

	private void doDownloadTreatmentData()
	{
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"JSON", "json");
		chooser.setFileFilter(filter);
		File selectedFile = new File(PrefsNightScoutLoader.getInstance().getM_DownloadTreatmentFilePath());
		chooser.setSelectedFile(selectedFile);
		int returnVal = chooser.showSaveDialog(contentPane);        	    		
		if(returnVal == JFileChooser.APPROVE_OPTION) 
		{
			String chosenFile = new String(chooser.getSelectedFile().getAbsolutePath());
			// Always append '.xls' to filename if not there already!
			String endName = new String(chosenFile.substring(chosenFile.length() - 5).toLowerCase());
			if (!endName.equals(".json"))
			{
				chosenFile += ".json";
			}

			// Handle if file exists
			if (confirmWriteFile(chosenFile, "treatment JSON Download"))
			{
				m_Logger.log(Level.INFO, "You chose to download Treatments to this file: " +
						chooser.getSelectedFile().getAbsolutePath());

				// Also set it in preferences too
				PrefsNightScoutLoader.getInstance().setM_DownloadTreatmentFilePath(chooser.getSelectedFile().getAbsolutePath());

				addStatusLine();
				try
				{
					m_NightScoutLoaderCore.downloadTreamentJSON(chosenFile);

					// Now open Explorer on the directory!
					// http://stackoverflow.com/questions/7357969/how-to-use-java-code-to-open-windows-file-explorer-and-highlight-the-specified-f

					Runtime.getRuntime().exec("explorer.exe /select," + chosenFile);
				}
				catch (IOException e) 
				{
					m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + ". just caught an error downloading treatments: " + e.getMessage() + " - " + e.getLocalizedMessage());
				}
				addStatusLine();
				// Update Status on window
				changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
			}
		}
	}

	private void doDownloadSensorData()
	{
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"JSON", "json");
		chooser.setFileFilter(filter);
		File selectedFile = new File(PrefsNightScoutLoader.getInstance().getM_DownloadSensorFilePath());
		chooser.setSelectedFile(selectedFile);
		int returnVal = chooser.showSaveDialog(contentPane);        	    		
		if(returnVal == JFileChooser.APPROVE_OPTION) 
		{
			String chosenFile = new String(chooser.getSelectedFile().getAbsolutePath());
			// Always append '.xls' to filename if not there already!
			String endName = new String(chosenFile.substring(chosenFile.length() - 5).toLowerCase());
			if (!endName.equals(".json"))
			{
				chosenFile += ".json";
			}
			// Handle if file exists
			if (confirmWriteFile(chosenFile, "sensor entries JSON Download"))
			{
				m_Logger.log(Level.INFO, "You chose to download Sensors to this file: " +
						chooser.getSelectedFile().getAbsolutePath());

				// Also set it in preferences too
				PrefsNightScoutLoader.getInstance().setM_DownloadSensorFilePath(chooser.getSelectedFile().getAbsolutePath());

				addStatusLine();
				try
				{
					m_NightScoutLoaderCore.downloadSensorJSON(chosenFile);
					// Now open Explorer on the directory!
					// http://stackoverflow.com/questions/7357969/how-to-use-java-code-to-open-windows-file-explorer-and-highlight-the-specified-f

					Runtime.getRuntime().exec("explorer.exe /select," + chosenFile);					
				}
				catch (IOException e) 
				{
					m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + ".doDownloadSensorData: just caught an error downloading sensor entries: " + e.getMessage() + " - " + e.getLocalizedMessage());
				}
				addStatusLine();
				// Update Status on window
				changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
			}
		}
	}

	public void loadMeterPump()
	{
		// Now try resizing the components we want to hide eventually

		/*
		 * David Re-design, just one grid,
		 */
		//        m_MeterTable.setVisible(true);
		//        m_MeterScrollPane.setVisible(true);
		//        m_NightscoutScrollPane.setPreferredSize(new Dimension(850, m_smallScrollPaneDepth));
		//        m_NightscoutScrollPane.revalidate();

		revalidate();
		//        JOptionPane.showMessageDialog(null, "Load Meter Pump selected", "InfoBox: " + " Action Selected", JOptionPane.INFORMATION_MESSAGE);

		// If Roche is selected, then enable the date ranges
		if (m_ComboBox.getSelectedIndex() == 0)
		{			
			try
			{
				// Increment the end date by one since it will assume midnight of current day hence omitting
				// today's values
				Date startDate = (Date)startDatePicker.getModel().getValue();
				Date endDate   = (Date)endDatePicker.getModel().getValue();
				Calendar c = Calendar.getInstance();
				c.setTime(endDate);
				c.add(Calendar.DATE, 1); // Add one day
				m_NightScoutLoaderCore.loadRocheMeterPump(startDate, c.getTime()); 
				changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
			}
			catch (Exception e)
			{
				m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + ".loadMeterPump: just caught an error: " + e.getMessage() + " - " + e.getLocalizedMessage());
			}
		}

		// Medtronic file load
		else if (m_ComboBox.getSelectedIndex() == 1)
		{
			try
			{
				// Check file type presented
				FileChecker.FileCheckType fileType = FileChecker.checkFile(m_FileNameTxtFld.getText());

				if (fileType == FileChecker.FileCheckType.Medtronic)
				{
					m_NightScoutLoaderCore.loadMedtronicMeterPump(m_FileNameTxtFld.getText());
					changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
				}
				else if (fileType == FileChecker.FileCheckType.Diasend)
				{
					JDialog.setDefaultLookAndFeelDecorated(true);
					int response = JOptionPane.showConfirmDialog(null, 
							"Option selected to load Medtronic file :\n\n" + m_FileNameTxtFld.getText() + 
							"\n\n" +
							"However this file appears to hold DIASEND content" +
							"\n\n" +
							"Do you want to load DIASEND information instead?", 
							"Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (response == JOptionPane.YES_OPTION)
					{
						m_NightScoutLoaderCore.loadDiasendMeterPump(m_FileNameTxtFld.getText());
						changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
					}
				}
				else
				{
					JDialog.setDefaultLookAndFeelDecorated(true);
					JOptionPane.showMessageDialog(null, 
							"Option selected to load Medtronic file :\n\n" + m_FileNameTxtFld.getText() + 
							"\n\n" +
							"However this file is in an unrecognised format.\n" +
							"Please export results from Medtronic software in CSV format.");
				}
			}


			catch (Exception e)
			{
				m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + ".loadMeterPump: just caught an error: " + e.getMessage() + " - " + e.getLocalizedMessage());
			}
		}

		// Diasend file load
		else if (m_ComboBox.getSelectedIndex() == 2)
		{
			try
			{
				// Check file type presented
				FileChecker.FileCheckType fileType = FileChecker.checkFile(m_FileNameTxtFld.getText());

				if (fileType == FileChecker.FileCheckType.Diasend)
				{
					m_NightScoutLoaderCore.loadDiasendMeterPump(m_FileNameTxtFld.getText());
					changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
				}
				else if (fileType == FileChecker.FileCheckType.Medtronic)
				{
					JDialog.setDefaultLookAndFeelDecorated(true);
					int response = JOptionPane.showConfirmDialog(null, 
							"Option selected to load Diasend file :\n\n" + m_FileNameTxtFld.getText() + 
							"\n\n" +
							"However this file appears to hold MEDTRONIC content" +
							"\n\n" +
							"Do you want to load MEDTRONIC information instead?", 
							"Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (response == JOptionPane.YES_OPTION)
					{
						m_NightScoutLoaderCore.loadMedtronicMeterPump(m_FileNameTxtFld.getText());
						changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
					}
				}
				else
				{
					JDialog.setDefaultLookAndFeelDecorated(true);
					JOptionPane.showMessageDialog(null, 
							"Option selected to load Diasend file :\n\n" + m_FileNameTxtFld.getText() + 
							"\n\n" +
							"However this file is in an unrecognised format.\n" +
							"Please export results from Medtronic software in CSV format.");
				}
			}
			catch (Exception e)
			{
				m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + ".loadMeterPump: just caught an error: " + e.getMessage() + " - " + e.getLocalizedMessage());
			}
		}

		// Update Status on window
		changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
	}




	private void reShapeWindowForNightScoutOnly()
	{
		// Now try resizing the components we want to hide eventually

		/*
		 * David Re-design, just one grid,
		 * 
		 * This is probably not needed now
		 */

		/*        m_MeterTable.setVisible(false);
        m_MeterScrollPane.setVisible(false);
        m_NightscoutScrollPane.setPreferredSize(new Dimension(850, m_bigScrollPaneDepth));
        m_NightscoutScrollPane.revalidate();
		 */
		revalidate();
		/*        JOptionPane.showMessageDialog(null, "Load NightScout selected. Start Date is: " + 
        		((Date)startDatePicker.getModel().getValue()).toString() + " End Date is: " +
        		((Date)endDatePicker.getModel().getValue()).toString(), "InfoBox: " + " Action Selected", 
        		JOptionPane.INFORMATION_MESSAGE);
		 */        
	}
	public void loadNightScout()
	{
		try
		{
			// Load treatments
			m_NightScoutLoaderCore.loadNightScout();
			m_MongoResults = m_NightScoutLoaderCore.getM_ResultsMongoDB();
			
			// Load entries
			// DAVID 8 Dec 2016
//			m_NightScoutLoaderCore.loadNightScoutEntries();
//			m_MongoResultsEntries = m_NightScoutLoaderCore.getM_DataLoadNightScoutEntries();
			
			doBackgroundAnalysis();
		}

		catch(Exception e)
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + ".loadNightScout: just caught an error: " + e.getMessage() + " - " + e.getLocalizedMessage());
		}

		// Update Status on window
		changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
	}

	synchronized private void updateGrid()
	{
		// For the thread.
		//		m_MongoResults = m_NightScoutLoaderCore.getM_ResultsMongoDB();
		changeStatusText(m_NightScoutLoaderCore.getM_StatusText());

		DefaultTableModel model = (DefaultTableModel) m_NightScoutTable.getModel();
		// Clear current table model...
		model.setRowCount(0);

		// Try creating a copy to iterate over
		ArrayList <DBResult> m_MongoResults2 = new ArrayList <DBResult>(m_MongoResults);

		// Reset meterpump only to false once we update the grid
		m_NightScoutLoaderCore.setM_MeterPumpLoadOnly(false);

		// This isn't protecting access here...
		synchronized(m_MongoResults2)
		{
			try
			{
				for (DBResult x : m_MongoResults2)
				{
					model.addRow(x.toArray(false));
				}
			}
			catch (Exception e)
			{
				m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + ".updateGrid: just caught an error: " + e.getMessage() + " - " + e.getLocalizedMessage());
			}
		}
	}

	/*	public class ResultFromDBComparator implements Comparator<ResultFromDB> {
	    public int compare(ResultFromDB p1, ResultFromDB p2) {
	        return p1.compareTo(p2);
	    }
	}*/

	//	private void determineDifferences()
	//	{
	//		m_NightScoutLoaderCore.determineDifferences();
	//		// Update Status on window
	//		changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
	//	}
	//
	//	private void saveDifferences()
	//	{
	//		m_NightScoutLoaderCore.saveDifferences();
	//		// Update Status on window
	//		changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
	//	}

	public Boolean confirmWriteFile(String filePathString, String purpose)
	{
		// Assume no, don't go ahead and write to the file
		Boolean result = false;

		File f = new File(filePathString);
		if(f.exists() && !f.isDirectory()) 
		{ 
			JDialog.setDefaultLookAndFeelDecorated(true);
			int response = JOptionPane.showConfirmDialog(null, 
					"The file already exists:\n\n" + filePathString + 
					"\n\n" +
					"Do you want to overwrite contents for " + purpose + "?", 
					"Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.YES_OPTION)
			{
				// Do if they say 'YES'
				result = true;
			}
		}
		else
		{
			// Or if the file doesn't already exist
			result = true;
		}

		return result;
	}

	public String getSelectedExcelFileForOutput(String prevFile, String dialogTitle, String dialogMessage)
	{
		String result = null;

		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Excel 97-2003 Workbook", "xls");
		chooser.setFileFilter(filter);
		File selectedFile = new File(prevFile);
		chooser.setSelectedFile(selectedFile);
		int returnVal = chooser.showSaveDialog(contentPane);        	    		
		if(returnVal == JFileChooser.APPROVE_OPTION) 
		{
			String chosenFile = new String(chooser.getSelectedFile().getAbsolutePath());
			// Always append '.xls' to filename if not there already!
			String endName = new String(chosenFile.substring(chosenFile.length() - 4).toLowerCase());
			if (!endName.equals(".xls"))
			{
				chosenFile += ".xls";
			}

			// Handle if file exists
			if (confirmWriteFile(chosenFile, dialogTitle /*"Excel Export"*/))
			{
				m_Logger.log(Level.INFO, dialogMessage /*"You chose to export Treatments to this file: "*/ +
						chooser.getSelectedFile().getAbsolutePath());

				result = chooser.getSelectedFile().getAbsolutePath();
			}
		}

		return result;
		// Remember to set file in preferences too
	}

	public void resetAnalyzeDateRange()
	{
		// This ensures that if going from advanced to not advanced, we keep the
		// date range making sense.

		m_NightScoutLoaderCore.resetAnalyzeDateRange();  

	}

	public void deeperAnalyseResults()
	{
		String excelFile = getSelectedExcelFileForOutput(PrefsNightScoutLoader.getInstance().getM_AnalysisFilePath(), 
				"Results Analysis", 
				"You chose to generate Analysis to this file: ");

		if (excelFile != null)
		{
			//			m_NightScoutLoaderCore.analyseResults(excelFile);  

			// Store file back in preferences
			PrefsNightScoutLoader.getInstance().setM_AnalysisFilePath(excelFile);

			// Open file finally
			try {
				Desktop.getDesktop().open(new File(excelFile));
			} 
			catch (IOException e) 
			{
				m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "deeperAnalyseResults: Just caught an exception " + e.getMessage());
			}
		}
	}

	public void setAnalyzeMethodEnabled(boolean enabled)
	{

	}
	
	public void displayCGMChart()
	{
		// Experimental - create a chart
		CGMChart chart = new CGMChart(this.m_NightScoutLoaderCore.getM_NightScoutArrayListDBResultEntries());
		chart.setVisible(true);
	}
	

	public boolean threadDeeperAnalyseResults(ThreadAnalyzer.AnalyzerCompleteHander handler)
	{
		boolean result = false; // did we do analysis?

		String excelFile = getSelectedExcelFileForOutput(PrefsNightScoutLoader.getInstance().getM_AnalysisFilePath(), 
				"Results Analysis", 
				"You chose to generate Analysis to this file: ");

		if (excelFile != null)
		{
			// Store file back in preferences
			PrefsNightScoutLoader.getInstance().setM_AnalysisFilePath(excelFile);

			// Also set the filename in the Analyzer window too
			// This is essential to be able to then load Excel with the file
			this.analyzer.setSelectedExcelFile(excelFile);

			m_NightScoutLoaderCore.doThreadAnalyzeResults(excelFile, handler);		
			result = true;
		}

		return result;
	}

	public void openExcelFile(String excelFile)
	{
		// Open file finally
		try 
		{
			Desktop.getDesktop().open(new File(excelFile));
		} 
		catch (IOException e) 
		{
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "openExceFile: Just caught an exception " + e.getMessage());
		}

	}


	public void deeperAnalyseResults(String excelFile)
	{	
		if (excelFile != null)
		{
			//			m_NightScoutLoaderCore.analyseResults(excelFile);			
			// Open file finally
			try {
				Desktop.getDesktop().open(new File(excelFile));
			} catch (IOException e) {
				m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "deeperAnalyseResults: Just caught an exception " + e.getMessage());
			}
		}
	}


	public void analyseResults()
	{
		//		m_NightScoutLoaderCore.analyseResults();      
		// Update Status on window
		changeStatusText(m_NightScoutLoaderCore.getM_StatusText());
	}

	public void deleteLoadedTreatments()
	{
		m_NightScoutLoaderCore.deleteLoadedTreatments();      
		// Update Status on window
		changeStatusText(m_NightScoutLoaderCore.getM_StatusText());		
	}

	public void reverseSynchButtonClick()
	{
		// The Audit Log form has just deleted entries.
		// Need to reload and refresh

		doThreadLoadNightScout(false);
	}

	private boolean isRowProximity(int rowNum)
	{
		boolean result = false;

		if (m_MongoResults != null && 
				m_MongoResults.get(rowNum).getM_DataSource() != null && 
				m_MongoResults.get(rowNum).getM_CP_EnteredBy().contains("PROXIMITY"))
		{
			result = true;
		}

		return result;
	}


	private boolean isRowJustLoaded(int rowNum)
	{
		boolean result = false;

		if (m_MongoResults != null && 
				m_MongoResults.get(rowNum).getM_DataSource() != null && 
				m_MongoResults.get(rowNum).getM_DataSource().equals("Meter"))
		{
			result = true;
		}

		return result;
	}

	// http://stackoverflow.com/questions/2749977/checking-if-a-row-appears-on-screen-before-force-scrolling-to-it
	public boolean isRowVisible(JTable table, int rowIndex) 
	{ 
		if (!(table.getParent() instanceof JViewport)) { 
			return true; 
		} 

		JViewport viewport = (JViewport)table.getParent(); 
		// This rectangle is relative to the table where the 
		// northwest corner of cell (0,0) is always (0,0) 

		Rectangle rect = table.getCellRect(rowIndex, 1, true); 

		// The location of the viewport relative to the table     
		Point pt = viewport.getViewPosition(); 
		// Translate the cell location so that it is relative 
		// to the view, assuming the northwest corner of the 
		// view is (0,0) 
		rect.setLocation(rect.x-pt.x, rect.y-pt.y);
		//	    rect.setLeft(0);
		//	    rect.setWidth(1);
		// Check if view completely contains the row
		return new Rectangle(viewport.getExtentSize()).contains(rect); 
	} 

	private int getRowsToKeepVisible(JTable table)
	{
		int result;

		// http://stackoverflow.com/questions/11887642/how-many-rows-is-a-jtable-currently-displaying
		// How many rows visible
		Rectangle vr = table.getVisibleRect ();
		int first = table.rowAtPoint(vr.getLocation());
		vr.translate(0, vr.height);
		int visibleRows = table.rowAtPoint(vr.getLocation()) - first;

		// Let's go for a quarter
		result = visibleRows / 4;

		return result;
	}

	public void displayMongoFormUp(DBResult result, int rowNum)
	{
		displayMongoForm(result, rowNum, -getRowsToKeepVisible(m_NightScoutTable));
	}

	public void displayMongoFormDown(DBResult result, int rowNum)
	{
		displayMongoForm(result, rowNum, getRowsToKeepVisible(m_NightScoutTable));
	}

	public void displayMongoForm(DBResult result, int rowNum)
	{
		// If the selected row is an updated result, it doesn't have an _ID yet.
		// Need to therefore reload the entire collection first.
		m_RowUpdated = -1;
		mongoForm.initialize(m_MongoResults, result, rowNum);

		mongoForm.setVisible(true);
		this.repaint();
	}

	public void displayMongoForm(DBResult result, int rowNum, int rowsToKeepInView)
	{
		displayMongoForm(result, rowNum);

		// Check if the row is out of view.  If so, then scroll too.
		if (!isRowVisible(m_NightScoutTable, rowNum))
		{	
			Rectangle aRect = m_NightScoutTable.getCellRect(rowNum + rowsToKeepInView, 0, true); 
			m_NightScoutTable.scrollRectToVisible(aRect);		
		}
	}

	public void meterSelected()
	{
		// If Roche is selected, then enable the date ranges
		if (m_ComboBox.getSelectedIndex() == 0)
		{			
			m_StartDateLbl.setVisible(true);	
			startDatePicker.setVisible(true);
			m_EndDateLbl.setVisible(true);	
			endDatePicker.setVisible(true);

			m_FileNameTxtFld.setVisible(false);
			m_FileNameLbl.setVisible(false);
			m_FileSelectBtn.setVisible(false);
		}
		else
		{
			if (m_ComboBox.getSelectedIndex() == 1)
			{
				// Set the text filename if Medtronic is used.
				// Initialise text field from preferences
				m_FileNameTxtFld.setText(PrefsNightScoutLoader.getInstance().getM_MedtronicMeterPumpResultFilePath());
			}
			else if(m_ComboBox.getSelectedIndex() == 2)
			{
				// Set the text filename if Medtronic is used.
				// Initialise text field from preferences
				m_FileNameTxtFld.setText(PrefsNightScoutLoader.getInstance().getM_DiasendMeterPumpResultFilePath());			
			}
			else if(m_ComboBox.getSelectedIndex() == 3)
			{
				// Set the text filename if Medtronic is used.
				// Initialise text field from preferences
				m_FileNameTxtFld.setText(PrefsNightScoutLoader.getInstance().getM_OmniPodMeterPumpResultFilePath());			
			}
			else if(m_ComboBox.getSelectedIndex() == 4)
			{
				// Set the text filename if Medtronic is used.
				// Initialise text field from preferences
				m_FileNameTxtFld.setText(PrefsNightScoutLoader.getInstance().getM_RocheExtractMeterPumpResultFilePath());			
			}

			// If Medtronic or Diasend is selected, then disable the date ranges
			m_StartDateLbl.setVisible(false);	
			startDatePicker.setVisible(false);
			m_EndDateLbl.setVisible(false);	
			endDatePicker.setVisible(false);

			m_FileNameTxtFld.setVisible(true);
			m_FileNameLbl.setVisible(true);
			m_FileSelectBtn.setVisible(true);
		}

		// Finally, update preferences so we store this down
		String meter = (String)m_ComboBox.getSelectedItem();
		PrefsNightScoutLoader.getInstance().setM_SelectedMeter(meter);
	}

	public void setTimeZoneLabel(String label)
	{
		lbl_TimeZone.setText(label);
	}

	public void checkTimeZone()
	{
		// Set timezone label accordingly.
		String timezone = PrefsNightScoutLoader.getInstance().getM_Timezone();
		if (PrefsNightScoutLoader.getInstance().getDef_M_Timezone().equals(timezone))
		{
			setTimeZoneLabel("");
		}
		else
		{
			setTimeZoneLabel(timezone);
		}
	}

	public DBResult navigateTo(int rowNum)
	{
		DBResult result = null;

		// From selected rowNum, decrease by 1 and return DBResult
		// If at the top then return null

		if (rowNum > 0 && rowNum > this.m_RowUpdated)
		{
			displayMongoFormDown(m_MongoResults.get(rowNum), rowNum);	
		}
		else
		{
			displayMongoFormUp(m_MongoResults.get(rowNum), rowNum);	
		}

		return result;
	}

	public DBResult navigateDownTo(int rowNum)
	{
		DBResult result = null;

		// From selected rowNum, decrease by 1 and return DBResult
		// If at the top then return null

		if (rowNum > 0)
		{
			displayMongoFormDown(m_MongoResults.get(rowNum), rowNum);	
		}

		return result;
	}

	public DBResult navigateUpTo(int rowNum)
	{
		DBResult result = null;

		// From selected rowNum, decrease by 1 and return DBResult
		// If at the top then return null

		if (rowNum >= 0)
		{
			displayMongoFormUp(m_MongoResults.get(rowNum), rowNum);	
		}

		return result;
	}

	public DBResult navigateUp(int rowNum)
	{
		DBResult result = null;

		// From selected rowNum, decrease by 1 and return DBResult
		// If at the top then return null

		if (rowNum > 0)
		{
			displayMongoFormUp(m_MongoResults.get(rowNum - 1), rowNum - 1);	
		}
		else
		{
			mongoForm.vibrate();
		}

		return result;
	}

	public DBResult navigateDown(int rowNum)
	{
		DBResult result = null;

		// From selected rowNum, decrease by 1 and return DBResult
		// If at the top then return null

		if (rowNum >= 0 && rowNum < m_MongoResults.size())
		{
			displayMongoFormDown(m_MongoResults.get(rowNum + 1), rowNum + 1);	
		}
		else
		{
			mongoForm.vibrate();
		}

		return result;
	}

}
