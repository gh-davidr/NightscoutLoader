package davidRichardson;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.text.NumberFormatter;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Dimension;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.ActionEvent;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.border.BevelBorder;
import javax.swing.JRadioButton;

public class WinMongoForm extends JFrame 
{

	private static final Logger m_Logger = Logger.getLogger( MyLogger.class.getName() );

	/**
	 * 
	 */
	private static final long serialVersionUID = 6069119838030173232L;

	private final static int VIBRATION_LENGTH = 20;
	private final static int VIBRATION_VELOCITY = 5;


	private JPanel contentPane;
	private JComboBox  tfSrchCP_EventType;


	private JTextField  tf_CP_EventType;
	private JTextField  tf_CP_Glucose;
	private JTextField  tf_CP_Carbs;
	private JTextField  tf_CP_Insulin;
	private JTextField  tf_CP_CarbsTime;
	private JTextField  tf_CP_EventDate;
	private JTextField  tf_CP_EnteredBy;
	private JEditorPane tf_CP_Notes;

	private JTextField  tfSrchGlucose;
	private JTextField  tfSrchCarbs;
	private JTextField  tfSrchInsulin;
	private JTextField  tfSrchEnteredBy;
	private JTextField  tfSrchNotes;
	JRadioButton        rdbtnSrchPossibleDuplicate;
	private JDatePickerImpl jdpStartDate;
	private JDatePickerImpl jdpEndDate;


	private NumberFormat m_NumberFormat;
	private NumberFormatter m_NumberFormatter;

	private JLabel    lbl_PossibleDuplicate;
	private JComboBox<String> cb_Proximity;

	// Passed in from calling window
	private DBResult m_result;
	private WinNightScoutLoader  m_MainWin;
	private ArrayList <DBResult> m_MongoResults = null;

	// Keep track of row num from grid too
	private int m_RowNum;

	enum LastDirection
	{
		up,
		down,
	};

	private LastDirection m_LastDirection = LastDirection.down;  // Default


	// For updates
	private DataLoadNightScoutTreatments m_DataLoadMongoDB;

	// For logging
	private JTextField tf_CP_EventTime;
	private JTextField tf_CP_EventDay;

	/**
	 * Create the frame.
	 */
	public WinMongoForm(WinNightScoutLoader mainWin, String title) 
	{
		super();

		m_NumberFormat = NumberFormat.getNumberInstance();
		m_NumberFormatter = new NumberFormatter(m_NumberFormat);
		m_NumberFormatter.setValueClass(Double.class);
		m_NumberFormatter.setAllowsInvalid(false);

		//ImageIcon img = new ImageIcon("Images\\Nightscout.jpg");
		URL url = MainNightScoutLoader.class.getResource("/Nightscout.jpg");
		ImageIcon img = new ImageIcon(url);
		setIconImage(img.getImage());

		m_DataLoadMongoDB = new DataLoadNightScoutTreatments();

		m_MainWin = mainWin;

		super.setTitle(title);
		setBounds(100, 10, 650, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		contentPane.add(panel_3, BorderLayout.NORTH);
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[] {82, 80, 80, 80};
		gbl_panel_3.rowHeights = new int[] {27, 27, 27, 27, 27, 0};
		gbl_panel_3.columnWeights = new double[]{4.9E-324, 1.0, 0.0, 0.0};
		gbl_panel_3.rowWeights = new double[]{4.9E-324, Double.MIN_VALUE, 0.0, 0.0, 0.0, 0.0};
		panel_3.setLayout(gbl_panel_3);

		JLabel lblFindByParameters = new JLabel("Find by Parameters");
		lblFindByParameters.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblFindByParameters = new GridBagConstraints();
		gbc_lblFindByParameters.insets = new Insets(0, 0, 5, 0);
		gbc_lblFindByParameters.gridwidth = 4;
		gbc_lblFindByParameters.gridx = 0;
		gbc_lblFindByParameters.gridy = 0;
		panel_3.add(lblFindByParameters, gbc_lblFindByParameters);

		JLabel lblSrchResultType = new JLabel("Result Type");
		GridBagConstraints gbc_lblSrchResultType = new GridBagConstraints();
		gbc_lblSrchResultType.anchor = GridBagConstraints.EAST;
		gbc_lblSrchResultType.insets = new Insets(0, 0, 5, 5);
		gbc_lblSrchResultType.gridx = 0;
		gbc_lblSrchResultType.gridy = 1;
		panel_3.add(lblSrchResultType, gbc_lblSrchResultType);

		tfSrchCP_EventType = new JComboBox<String>();
		tfSrchCP_EventType.setModel(new DefaultComboBoxModel(new String[] {"", "BG Check", "Correction Bolus", "Meal Bolus", "Temp Basal"}));
		tfSrchCP_EventType.setEditable(false);
		tfSrchCP_EventType.setBackground(Color.WHITE);
		GridBagConstraints gbc_tfSrch_ResultType = new GridBagConstraints();
		gbc_tfSrch_ResultType.insets = new Insets(0, 0, 5, 5);
		gbc_tfSrch_ResultType.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfSrch_ResultType.gridx = 1;
		gbc_tfSrch_ResultType.gridy = 1;
		panel_3.add(tfSrchCP_EventType, gbc_tfSrch_ResultType);

		JLabel lblSrchBGResult = new JLabel("Glucose");
		GridBagConstraints gbc_lblSrchBGResult = new GridBagConstraints();
		gbc_lblSrchBGResult.anchor = GridBagConstraints.EAST;
		gbc_lblSrchBGResult.insets = new Insets(0, 0, 5, 5);
		gbc_lblSrchBGResult.gridx = 2;
		gbc_lblSrchBGResult.gridy = 1;
		panel_3.add(lblSrchBGResult, gbc_lblSrchBGResult);

		tfSrchGlucose = new JTextField();
		//		tfSrchGlucose = new JFormattedTextField(m_NumberFormatter);
		GridBagConstraints gbc_tfSrchGlucose = new GridBagConstraints();
		gbc_tfSrchGlucose.anchor = GridBagConstraints.WEST;
		gbc_tfSrchGlucose.insets = new Insets(0, 0, 5, 0);
		gbc_tfSrchGlucose.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfSrchGlucose.gridx = 3;
		gbc_tfSrchGlucose.gridy = 1;
		panel_3.add(tfSrchGlucose, gbc_tfSrchGlucose);
		tfSrchGlucose.setColumns(10);

		JLabel lblSrchCarbs = new JLabel("Carbs");
		GridBagConstraints gbc_lblSrchCarbs = new GridBagConstraints();
		gbc_lblSrchCarbs.anchor = GridBagConstraints.EAST;
		gbc_lblSrchCarbs.insets = new Insets(0, 0, 5, 5);
		gbc_lblSrchCarbs.gridx = 2;
		gbc_lblSrchCarbs.gridy = 2;
		panel_3.add(lblSrchCarbs, gbc_lblSrchCarbs);

		tfSrchCarbs = new JTextField();
		tfSrchCarbs.setColumns(10);
		GridBagConstraints gbc_tfSrchCarbs = new GridBagConstraints();
		gbc_tfSrchCarbs.insets = new Insets(0, 0, 5, 0);
		gbc_tfSrchCarbs.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfSrchCarbs.gridx = 3;
		gbc_tfSrchCarbs.gridy = 2;
		panel_3.add(tfSrchCarbs, gbc_tfSrchCarbs);

		JLabel lblSrchInsulin = new JLabel("Insulin");
		GridBagConstraints gbc_lblSrchInsulin = new GridBagConstraints();
		gbc_lblSrchInsulin.anchor = GridBagConstraints.EAST;
		gbc_lblSrchInsulin.insets = new Insets(0, 0, 5, 5);
		gbc_lblSrchInsulin.gridx = 2;
		gbc_lblSrchInsulin.gridy = 3;
		panel_3.add(lblSrchInsulin, gbc_lblSrchInsulin);

		tfSrchInsulin = new JTextField();
		tfSrchInsulin.setColumns(10);
		GridBagConstraints gbc_tfSrchInsulin = new GridBagConstraints();
		gbc_tfSrchInsulin.insets = new Insets(0, 0, 5, 0);
		gbc_tfSrchInsulin.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfSrchInsulin.gridx = 3;
		gbc_tfSrchInsulin.gridy = 3;
		panel_3.add(tfSrchInsulin, gbc_tfSrchInsulin);

		JLabel lblSrchEnteredBy = new JLabel("Entered By");
		GridBagConstraints gbc_lblSrchEnteredBy = new GridBagConstraints();
		gbc_lblSrchEnteredBy.anchor = GridBagConstraints.EAST;
		gbc_lblSrchEnteredBy.insets = new Insets(0, 0, 5, 5);
		gbc_lblSrchEnteredBy.gridx = 0;
		gbc_lblSrchEnteredBy.gridy = 3;
		panel_3.add(lblSrchEnteredBy, gbc_lblSrchEnteredBy);

		tfSrchEnteredBy = new JTextField();
		tfSrchEnteredBy.setColumns(10);
		GridBagConstraints gbc_tfSrchEnteredBy = new GridBagConstraints();
		gbc_tfSrchEnteredBy.insets = new Insets(0, 0, 5, 5);
		gbc_tfSrchEnteredBy.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfSrchEnteredBy.gridx = 1;
		gbc_tfSrchEnteredBy.gridy = 3;
		panel_3.add(tfSrchEnteredBy, gbc_tfSrchEnteredBy);

		JLabel lblSrchNotes = new JLabel("Notes");
		GridBagConstraints gbc_lblSrchNotes = new GridBagConstraints();
		gbc_lblSrchNotes.anchor = GridBagConstraints.EAST;
		gbc_lblSrchNotes.insets = new Insets(0, 0, 5, 5);
		gbc_lblSrchNotes.gridx = 0;
		gbc_lblSrchNotes.gridy = 2;
		panel_3.add(lblSrchNotes, gbc_lblSrchNotes);

		tfSrchNotes = new JTextField();
		tfSrchNotes.setColumns(10);
		GridBagConstraints gbc_tfSrchNotes = new GridBagConstraints();
		gbc_tfSrchNotes.insets = new Insets(0, 0, 5, 5);
		gbc_tfSrchNotes.fill = GridBagConstraints.HORIZONTAL;
		gbc_tfSrchNotes.gridx = 1;
		gbc_tfSrchNotes.gridy = 2;
		panel_3.add(tfSrchNotes, gbc_tfSrchNotes);

		UtilDateModel startDateModel = new UtilDateModel();
		UtilDateModel endDateModel = new UtilDateModel();

		JLabel lblStartDate = new JLabel("Start Date");
		GridBagConstraints gbc_lblStartDate = new GridBagConstraints();
		gbc_lblStartDate.anchor = GridBagConstraints.EAST;
		gbc_lblStartDate.insets = new Insets(0, 0, 5, 5);
		gbc_lblStartDate.gridx = 0;
		gbc_lblStartDate.gridy = 4;
		panel_3.add(lblStartDate, gbc_lblStartDate);

		jdpStartDate = new JDatePickerImpl(new JDatePanelImpl(startDateModel), new DateLabelFormatter());
		jdpStartDate.setMinimumSize(new Dimension(115,30));
		GridBagConstraints gbc_jdpStartDate = new GridBagConstraints();
		gbc_jdpStartDate.anchor = GridBagConstraints.WEST;
		gbc_jdpStartDate.insets = new Insets(0, 0, 5, 5);
		gbc_jdpStartDate.gridwidth = 1;
		gbc_jdpStartDate.gridx = 1;
		gbc_jdpStartDate.gridy = 4;
		panel_3.add(jdpStartDate, gbc_jdpStartDate);

		JLabel lblEndDate = new JLabel("End Date");
		GridBagConstraints gbc_lblEndDate = new GridBagConstraints();
		gbc_lblEndDate.anchor = GridBagConstraints.EAST;
		gbc_lblEndDate.insets = new Insets(0, 0, 5, 5);
		gbc_lblEndDate.gridx = 2;
		gbc_lblEndDate.gridy = 4;
		panel_3.add(lblEndDate, gbc_lblEndDate);

		jdpEndDate = new JDatePickerImpl(new JDatePanelImpl(endDateModel), new DateLabelFormatter());
		jdpEndDate.setMinimumSize(new Dimension(115,30));		
		GridBagConstraints gbc_jdpEndDate = new GridBagConstraints();
		gbc_jdpEndDate.anchor = GridBagConstraints.WEST;
		gbc_jdpEndDate.insets = new Insets(0, 0, 5, 0);
		gbc_jdpEndDate.gridx = 3;
		gbc_jdpEndDate.gridy = 4;
		panel_3.add(jdpEndDate, gbc_jdpEndDate);



		rdbtnSrchPossibleDuplicate = new JRadioButton("Possible Duplicate");
		GridBagConstraints gbc_rdbtnPossibleDuplicate = new GridBagConstraints();
		gbc_rdbtnPossibleDuplicate.anchor = GridBagConstraints.WEST;
		gbc_rdbtnPossibleDuplicate.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnPossibleDuplicate.gridx = 1;
		gbc_rdbtnPossibleDuplicate.gridy = 5;
		panel_3.add(rdbtnSrchPossibleDuplicate, gbc_rdbtnPossibleDuplicate);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.gridx = 2;
		gbc_panel.gridy = 5;
		// contentPane.add(panel, BorderLayout.WEST);

		BasicArrowButton abSrchUp = new BasicArrowButton(1);
		abSrchUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				upSearchButtonClick();
			}
		});		
		panel.add(abSrchUp);

		JLabel btSrchFind = new JLabel("Up -Find- Down");
		panel.add(btSrchFind);
		panel_3.add(panel, gbc_panel);

		BasicArrowButton abSrchDown = new BasicArrowButton(5);
		abSrchDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				downSearchButtonClick();
			}
		});		
		panel.add(abSrchDown);



		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		panel_2.setMinimumSize(new Dimension(100, 100));
		panel_2.setMaximumSize(new Dimension(100, 100));
		contentPane.add(panel_2, BorderLayout.CENTER);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] {82, 147};
		gbl_panel_2.rowHeights = new int[] {30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30};
		gbl_panel_2.columnWeights = new double[]{0.0, 1.0};
		gbl_panel_2.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		panel_2.setLayout(gbl_panel_2);

		JLabel lblModifyPanel = new JLabel("Up/Down Navigation & Modify");
		lblModifyPanel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		GridBagConstraints gbc_lblModifyPanel = new GridBagConstraints();
		gbc_lblModifyPanel.gridwidth = 2;
		gbc_lblModifyPanel.insets = new Insets(0, 0, 5, 0);
		gbc_lblModifyPanel.gridx = 0;
		gbc_lblModifyPanel.gridy = 0;
		panel_2.add(lblModifyPanel, gbc_lblModifyPanel);

		JLabel lblEntryType = new JLabel("Result Type");
		GridBagConstraints gbc_lblEntryType = new GridBagConstraints();
		gbc_lblEntryType.anchor = GridBagConstraints.EAST;
		gbc_lblEntryType.insets = new Insets(0, 0, 5, 5);
		gbc_lblEntryType.gridx = 0;
		gbc_lblEntryType.gridy = 1;
		panel_2.add(lblEntryType, gbc_lblEntryType);

		tf_CP_EventType = new JTextField();
		tf_CP_EventType.setEditable(false);
		GridBagConstraints gbc_tf_ResultType = new GridBagConstraints();
		gbc_tf_ResultType.insets = new Insets(0, 0, 5, 0);
		gbc_tf_ResultType.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_ResultType.gridx = 1;
		gbc_tf_ResultType.gridy = 1;
		panel_2.add(tf_CP_EventType, gbc_tf_ResultType);

		JLabel lblNewLabel = new JLabel("Glucose");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 2;
		panel_2.add(lblNewLabel, gbc_lblNewLabel);

		tf_CP_Glucose = new JTextField();
		tf_CP_Glucose.setEditable(false);
		GridBagConstraints gbc_tf_Year = new GridBagConstraints();
		gbc_tf_Year.insets = new Insets(0, 0, 5, 0);
		gbc_tf_Year.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_Year.gridx = 1;
		gbc_tf_Year.gridy = 2;
		panel_2.add(tf_CP_Glucose, gbc_tf_Year);
		tf_CP_Glucose.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Carbs");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 3;
		panel_2.add(lblNewLabel_1, gbc_lblNewLabel_1);

		tf_CP_Carbs = new JTextField();
		tf_CP_Carbs.setEditable(false);
		GridBagConstraints gbc_tf_Month = new GridBagConstraints();
		gbc_tf_Month.insets = new Insets(0, 0, 5, 0);
		gbc_tf_Month.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_Month.gridx = 1;
		gbc_tf_Month.gridy = 3;
		panel_2.add(tf_CP_Carbs, gbc_tf_Month);
		tf_CP_Carbs.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Insulin");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 4;
		panel_2.add(lblNewLabel_2, gbc_lblNewLabel_2);

		tf_CP_Insulin = new JTextField();
		tf_CP_Insulin.setEditable(false);
		GridBagConstraints gbc_tf_Day = new GridBagConstraints();
		gbc_tf_Day.insets = new Insets(0, 0, 5, 0);
		gbc_tf_Day.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_Day.gridx = 1;
		gbc_tf_Day.gridy = 4;
		panel_2.add(tf_CP_Insulin, gbc_tf_Day);
		tf_CP_Insulin.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Carbs Time");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 5;
		panel_2.add(lblNewLabel_3, gbc_lblNewLabel_3);

		tf_CP_CarbsTime = new JTextField();
		tf_CP_CarbsTime.setEditable(false);
		GridBagConstraints gbc_tf_DayName = new GridBagConstraints();
		gbc_tf_DayName.insets = new Insets(0, 0, 5, 0);
		gbc_tf_DayName.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_DayName.gridx = 1;
		gbc_tf_DayName.gridy = 5;
		panel_2.add(tf_CP_CarbsTime, gbc_tf_DayName);
		tf_CP_CarbsTime.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("Date");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 6;
		panel_2.add(lblNewLabel_4, gbc_lblNewLabel_4);

		tf_CP_EventDate = new JTextField();
		tf_CP_EventDate.setEditable(false);
		GridBagConstraints gbc_tf_CP_EventDate = new GridBagConstraints();
		gbc_tf_CP_EventDate.insets = new Insets(0, 0, 5, 0);
		gbc_tf_CP_EventDate.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_CP_EventDate.gridx = 1;
		gbc_tf_CP_EventDate.gridy = 6;
		panel_2.add(tf_CP_EventDate, gbc_tf_CP_EventDate);
		tf_CP_EventDate.setColumns(10);

		JLabel label = new JLabel("Time");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 0;
		gbc_label.gridy = 7;
		panel_2.add(label, gbc_label);

		tf_CP_EventTime = new JTextField();
		tf_CP_EventTime.setEditable(false);
		tf_CP_EventTime.setColumns(10);
		GridBagConstraints gbc_tf_CP_EventTime = new GridBagConstraints();
		gbc_tf_CP_EventTime.insets = new Insets(0, 0, 5, 0);
		gbc_tf_CP_EventTime.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_CP_EventTime.gridx = 1;
		gbc_tf_CP_EventTime.gridy = 7;
		panel_2.add(tf_CP_EventTime, gbc_tf_CP_EventTime);

		JLabel label_1 = new JLabel("Day");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.anchor = GridBagConstraints.EAST;
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		gbc_label_1.gridx = 0;
		gbc_label_1.gridy = 8;
		panel_2.add(label_1, gbc_label_1);

		tf_CP_EventDay = new JTextField();
		tf_CP_EventDay.setEditable(false);
		tf_CP_EventDay.setColumns(10);
		GridBagConstraints gbc_tf_CP_EventDay = new GridBagConstraints();
		gbc_tf_CP_EventDay.insets = new Insets(0, 0, 5, 0);
		gbc_tf_CP_EventDay.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_CP_EventDay.gridx = 1;
		gbc_tf_CP_EventDay.gridy = 8;
		panel_2.add(tf_CP_EventDay, gbc_tf_CP_EventDay);

		JLabel lblNewLabel_5 = new JLabel("Entered By");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 9;
		panel_2.add(lblNewLabel_5, gbc_lblNewLabel_5);

		tf_CP_EnteredBy = new JTextField();
		tf_CP_EnteredBy.setEditable(false);
		GridBagConstraints gbc_tf_TimeSlot = new GridBagConstraints();
		gbc_tf_TimeSlot.insets = new Insets(0, 0, 5, 0);
		gbc_tf_TimeSlot.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_TimeSlot.gridx = 1;
		gbc_tf_TimeSlot.gridy = 9;
		panel_2.add(tf_CP_EnteredBy, gbc_tf_TimeSlot);
		tf_CP_EnteredBy.setColumns(10);

		JLabel lblNewLabel_6 = new JLabel("Notes");
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 10;
		panel_2.add(lblNewLabel_6, gbc_lblNewLabel_6);

		tf_CP_Notes = new JEditorPane();
		GridBagConstraints gbc_tf_Result = new GridBagConstraints();
		gbc_tf_Result.insets = new Insets(0, 0, 5, 0);
		gbc_tf_Result.gridheight = 3;
		gbc_tf_Result.fill = GridBagConstraints.BOTH;
		gbc_tf_Result.gridx = 1;
		gbc_tf_Result.gridy = 10;

		panel_2.add(tf_CP_Notes, gbc_tf_Result);

		lbl_PossibleDuplicate = new JLabel("Possible Duplicate");
		GridBagConstraints gbc_lblPossibleDuplicate = new GridBagConstraints();
		gbc_lblPossibleDuplicate.anchor = GridBagConstraints.EAST;
		gbc_lblPossibleDuplicate.insets = new Insets(0, 0, 5, 5);
		gbc_lblPossibleDuplicate.gridx = 0;
		gbc_lblPossibleDuplicate.gridy = 13;
		panel_2.add(lbl_PossibleDuplicate, gbc_lblPossibleDuplicate);

		cb_Proximity = new JComboBox<String>();
		cb_Proximity.setModel(new DefaultComboBoxModel<String>(new String[] {"Confirmed Duplicate", "Confirmed NOT Duplicate"}));

		GridBagConstraints gbc_cbProximity = new GridBagConstraints();
		gbc_cbProximity.insets = new Insets(0, 0, 5, 0);
		gbc_cbProximity.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbProximity.gridx = 1;
		gbc_cbProximity.gridy = 13;
		panel_2.add(cb_Proximity, gbc_cbProximity);


		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		contentPane.add(panel_1, BorderLayout.SOUTH);



		BasicArrowButton abUp = new BasicArrowButton(BasicArrowButton.NORTH);
		abUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				upButtonClick();
			}
		});		
		panel_1.add(abUp);

		BasicArrowButton abDown = new BasicArrowButton(BasicArrowButton.SOUTH);
		abDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				downButtonClick();
			}
		});		
		panel_1.add(abDown);

		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				okButtonClick();
			}
		});
		
		JButton button = new JButton("Reset Search");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				resetSearchPanel();
			}
		});
		panel_1.add(button);
		panel_1.add(btnOk);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cancelButtonClick();
			}
		});
		panel_1.add(btnCancel);

		JButton btnUndo = new JButton("Undo");
		btnUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				undoButtonClick();
			}
		});
		panel_1.add(btnUndo);

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				deleteButtonClick();
			}
		});
		panel_1.add(btnDelete);
	}


	public void initialize(DBResult result, int rowNum)
	{
		m_result = result;
		m_RowNum = rowNum;
		initialize();
	}


	public void initialize(ArrayList <DBResult> mongoResults, DBResult result, int rowNum)
	{
		m_result = result;
		m_MongoResults = mongoResults;
		m_RowNum = rowNum;
		initialize();
	}
	
	private void resetSearchPanel()
	{
		initializeDateSelectors(true);
		
		this.tfSrchCP_EventType.getModel().setSelectedItem("");
		this.tfSrchGlucose.setText("");
		this.tfSrchCarbs.setText("");
		this.tfSrchInsulin.setText("");
		this.tfSrchEnteredBy.setText("");
		this.tfSrchNotes.setText("");
	}

	void initializeDateSelectors()
	{
		initializeDateSelectors(false);
	}
	
	void initializeDateSelectors(boolean override)
	{
		String currStartDateStr = new String(jdpStartDate.getJFormattedTextField().getText());
		String currEndDateStr   = new String(jdpEndDate.getJFormattedTextField().getText());

		// Set min and max dates to dates based on the treatment range in memory
		if (override || currStartDateStr.isEmpty() || currEndDateStr.isEmpty())
		{
			// Set start and end dates as min and max dates for data set.
			final DateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
			ArrayList<DBResult> resultList = CoreNightScoutLoader.getInstance().getM_ResultsMongoDB();
			Date startDate = Analyzer.getFirstDateFromDBResults(resultList);
			Date endDate   = Analyzer.getLastDateFromDBResults(resultList);

			final Date epochDate = new Date(0);

			Calendar startDateCal  = Calendar.getInstance();
			Calendar endDateCal    = Calendar.getInstance();
			startDateCal.setTime(startDate);
			endDateCal.setTime(endDate);

			((UtilDateModel)jdpStartDate.getModel()).setValue(startDateCal.getTime());
			((UtilDateModel)jdpEndDate.getModel()).setValue(endDateCal.getTime());

			String startDateStr = new String(format.format(startDate.getTime()));
			String endDateStr = new String(format.format(endDate.getTime()));

			jdpStartDate.getJFormattedTextField().setText(startDateStr);
			jdpEndDate.getJFormattedTextField().setText(endDateStr);
		}
	}

	private void setTextFromDouble(JTextField tf, Double val)
	{
		if (val != null)
		{
			if (DBResult.doubleIsInteger(val))
			{
				tf.setText(Long.toString(val.longValue()));
			}
			else
			{
				tf.setText(val.toString());
			}
		}
		else
		{
			tf.setText("");
		}
	}
	public void initialize()
	{
		tf_CP_EventType.setText(m_result.getM_CP_EventType());
		setTextFromDouble(tf_CP_Glucose, m_result.getM_CP_Glucose());
		setTextFromDouble(tf_CP_Carbs, m_result.getM_CP_Carbs());
		setTextFromDouble(tf_CP_Insulin, m_result.getM_CP_Insulin());

		//		tf_CP_CarbsTime.setText(m_result.getM_CP_CarbsTime());
		tf_CP_EventDate.setText(m_result.getM_TreatmentDate());
		tf_CP_EventTime.setText(m_result.getM_TreatmentTime());
		tf_CP_EventDay.setText(m_result.getM_TreatmentDayName());
		tf_CP_EnteredBy.setText(m_result.getM_CP_EnteredBy());
		tf_CP_Notes.setText(m_result.getM_CP_Notes());

		if (m_result.getM_CP_EnteredBy().contains("PROXIMITY"))
		{
			lbl_PossibleDuplicate.setVisible(true);
			cb_Proximity.setVisible(true);
			cb_Proximity.setSelectedIndex(0);
		}
		else
		{
			lbl_PossibleDuplicate.setVisible(false);
			cb_Proximity.setVisible(false);
		}

		initializeDateSelectors();

	}

	void upButtonClick()
	{
		m_MainWin.navigateUp(m_RowNum);
	}

	void downButtonClick()
	{
		m_MainWin.navigateDown(m_RowNum);
	}

	public boolean updateResultFromDB()
	{
		// Change to true if we need to do an update :-)
		boolean result = false;
		// See if proxmity has changed
		boolean clearProximity =
				(m_result.getM_CP_EnteredBy().contains("PROXIMITY") &&
						cb_Proximity.getSelectedIndex() == 1) ? true : false;

		if (!m_result.getM_CP_Notes().equals(tf_CP_Notes.getText()) || clearProximity)
		{
			result = true;
			m_result.setM_CP_Notes(tf_CP_Notes.getText());


			// Now update the result on DB too.
			// FOr now, do the actual mongo update here.  Will figure out where best to move later
			try
			{
				m_DataLoadMongoDB.updateDBResultFromForm(m_result, clearProximity);
			}
			catch(Exception e)
			{
				m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + " just caught an error: " + e.getMessage() + " - " + e.getLocalizedMessage());
			}

			// If this worked fine, then update the text field to reflect change on Mongo
			String cp_EnteredBy = new String(tf_CP_EnteredBy.getText()); 
			cp_EnteredBy = cp_EnteredBy.replace("-PROXIMITY", "");
			tf_CP_EnteredBy.setText(cp_EnteredBy);
			m_result.setM_CP_EnteredBy(cp_EnteredBy);
		}

		return result;

	}

	private void okButtonClick()
	{
		if (updateResultFromDB())
		{
			// If we do update then inform the main window
			m_MainWin.rowUpdated(m_RowNum);
		}
		dispose();
	}

	private void cancelButtonClick()
	{
		dispose();
	}

	private void undoButtonClick()
	{
		initialize();
	}


	@Override
	public void dispose()
	{
		super.dispose();
		m_MainWin.repaint();
	}

	private void deleteButtonClick()
	{
		JDialog.setDefaultLookAndFeelDecorated(true);
		int response = JOptionPane.showConfirmDialog(null, 
				"Are you sure you really want to delete this entry?", "Delete Result Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (response == JOptionPane.YES_OPTION)
		{
			// Delete the result ...
			CoreNightScoutLoader.getInstance().deleteLoadedTreatment(m_result);
			// Reload the entire collection
			m_MainWin.reverseSynchButtonClick();

			dispose();
		}
	}

	public int getM_RowNum()
	{
		return m_RowNum;
	}

	// Enhanced Search with dates too
	private boolean doesDBResultMatch(DBResult res,
			String eventType, String glucose, String carbs, 
			String insulin,   String enteredBy, String notes,
			Date   startDate, Date endDate,
			boolean possDupe)
	{
		boolean result = false;

		boolean typeMatch = (eventType.isEmpty() ? true : eventType.equals(res.getM_CP_EventType()));

		boolean glucMatch = (glucose.isEmpty()   || (res.getM_CP_Glucose() != null && Double.parseDouble(glucose) == res.getM_CP_Glucose()) ? true : false);
		boolean carbMatch = (carbs.isEmpty()     || (res.getM_CP_Carbs()   != null && Double.parseDouble(carbs)   == res.getM_CP_Carbs())   ? true : false);	
		boolean insuMatch = (insulin.isEmpty()   || (res.getM_CP_Insulin() != null && Double.parseDouble(insulin) == res.getM_CP_Insulin()) ? true : false);

		//		boolean glucMatch = (glucose.isEmpty()   || (res.getM_CP_Glucose() != null && glucose.equals(res.getM_CP_Glucose().toString())) ? true : false);
		//		boolean carbMatch = (carbs.isEmpty()     || (res.getM_CP_Carbs()   != null && carbs.equals(res.getM_CP_Carbs().toString()))     ? true : false);	
		//		boolean insuMatch = (insulin.isEmpty()   || (res.getM_CP_Insulin() != null && insulin.equals(res.getM_CP_Insulin().toString())) ? true : false);


		boolean entrMatch = (enteredBy.isEmpty() ? true : res.getM_CP_EnteredBy().contains(enteredBy));
		boolean noteMatch = (notes.isEmpty()     ? true : res.getM_CP_Notes().contains(notes));
		boolean dateMatch = CommonUtils.isTimeBetween(startDate, endDate, res.getM_Time());

		boolean dupeMatch = (!possDupe || possDupe && res.isM_ProximityPossibleDuplicate() ? true : false);

		//		boolean glucMatch = (glucose.isEmpty()  && res.getM_CP_Glucose() == null ? true : glucose.equals(res.getM_CP_Glucose().toString()));
		//		boolean carbMatch = (carbs.isEmpty()    && res.getM_CP_Carbs() == null   ? true : carbs.equals(res.getM_CP_Carbs().toString()));	
		//		boolean insuMatch = (insulin.isEmpty()  && res.getM_CP_Insulin() == null ? true : insulin.equals(res.getM_CP_Insulin().toString()));
		//		boolean entrMatch = (enteredBy.isEmpty() ? true : res.getM_CP_EnteredBy().contains(enteredBy));
		//		boolean noteMatch =(notes.isEmpty()     ? true : res.getM_CP_Notes().contains(notes));

		result = (typeMatch == true && glucMatch == true && carbMatch == true && 
				insuMatch == true && entrMatch == true && noteMatch == true && 
				dateMatch == true && dupeMatch == true
				? true : false);

		return result;
	}


	private boolean doesDBResultMatch(DBResult res,
			String eventType, String glucose, String carbs, 
			String insulin,   String enteredBy, String notes,
			boolean possDupe)
	{
		boolean result = false;

		boolean typeMatch = (eventType.isEmpty() ? true : eventType.equals(res.getM_CP_EventType()));

		boolean glucMatch = (glucose.isEmpty()   || (res.getM_CP_Glucose() != null && Double.parseDouble(glucose) == res.getM_CP_Glucose()) ? true : false);
		boolean carbMatch = (carbs.isEmpty()     || (res.getM_CP_Carbs()   != null && Double.parseDouble(carbs)   == res.getM_CP_Carbs())   ? true : false);	
		boolean insuMatch = (insulin.isEmpty()   || (res.getM_CP_Insulin() != null && Double.parseDouble(insulin) == res.getM_CP_Insulin()) ? true : false);

		//		boolean glucMatch = (glucose.isEmpty()   || (res.getM_CP_Glucose() != null && glucose.equals(res.getM_CP_Glucose().toString())) ? true : false);
		//		boolean carbMatch = (carbs.isEmpty()     || (res.getM_CP_Carbs()   != null && carbs.equals(res.getM_CP_Carbs().toString()))     ? true : false);	
		//		boolean insuMatch = (insulin.isEmpty()   || (res.getM_CP_Insulin() != null && insulin.equals(res.getM_CP_Insulin().toString())) ? true : false);


		boolean entrMatch = (enteredBy.isEmpty() ? true : res.getM_CP_EnteredBy().contains(enteredBy));
		boolean noteMatch = (notes.isEmpty()     ? true : res.getM_CP_Notes().contains(notes));
		boolean dupeMatch = (!possDupe || possDupe && res.isM_ProximityPossibleDuplicate() ? true : false);

		//		boolean glucMatch = (glucose.isEmpty()  && res.getM_CP_Glucose() == null ? true : glucose.equals(res.getM_CP_Glucose().toString()));
		//		boolean carbMatch = (carbs.isEmpty()    && res.getM_CP_Carbs() == null   ? true : carbs.equals(res.getM_CP_Carbs().toString()));	
		//		boolean insuMatch = (insulin.isEmpty()  && res.getM_CP_Insulin() == null ? true : insulin.equals(res.getM_CP_Insulin().toString()));
		//		boolean entrMatch = (enteredBy.isEmpty() ? true : res.getM_CP_EnteredBy().contains(enteredBy));
		//		boolean noteMatch =(notes.isEmpty()     ? true : res.getM_CP_Notes().contains(notes));

		result = (typeMatch == true && glucMatch == true && carbMatch == true && 
				insuMatch == true && entrMatch == true && noteMatch == true && dupeMatch == true
				? true : false);

		return result;
	}


	private void searchAndHighlight()
	{
		int foundRow = search();
		if (foundRow != -1)
		{
			// Only store the new row if we can find one
			m_RowNum = foundRow;

			if (this.m_LastDirection == LastDirection.up)
			{
				m_MainWin.navigateUpTo(m_RowNum);
			}
			else
			{
				m_MainWin.navigateDownTo(m_RowNum);
			}
		}
		else
		{
			vibrate();
		}
	}

	private int search()
	{
		int result = -1;

		// Get the text field for search
		String eventType  = (String)this.tfSrchCP_EventType.getModel().getSelectedItem();
		String glucose    = this.tfSrchGlucose.getText();
		String carbs      = this.tfSrchCarbs.getText();
		String insulin    = this.tfSrchInsulin.getText();
		String enteredBy  = this.tfSrchEnteredBy.getText();
		String notes      = this.tfSrchNotes.getText();
		Date startDate    = (Date)jdpStartDate.getModel().getValue();
		Date endDate      = (Date)jdpEndDate.getModel().getValue();

		boolean possDupe  = this.rdbtnSrchPossibleDuplicate.isSelected();

		// We have to search on something :-)
		if (!eventType.isEmpty() || !glucose.isEmpty() || !carbs.isEmpty() || 
				!insulin.isEmpty() || !enteredBy.isEmpty() || !notes.isEmpty() || possDupe)
		{

			// Start at current row num and then go up or down
			if (m_LastDirection == LastDirection.down)
			{
				// Start at current rownum and go down the sequence
				for (int i = this.m_RowNum + 1; result == -1 && i < this.m_MongoResults.size(); i++)
				{
					if (doesDBResultMatch(m_MongoResults.get(i), 
							eventType, glucose, carbs, insulin, enteredBy, notes, 
							startDate, endDate, possDupe))
					{
						result = i;
					}
				}

				// Let's not wrap round.
				// Instead, return -1 and let the shake begin ...

				//			// If not found then start at top and continue down again
				//			for (int i = 0; result == -1 && i <= this.m_RowNum && i < this.m_MongoResults.size(); i++)
				//			{
				//				if (doesDBResultMatch(m_MongoResults.get(i),
				//						eventType, glucose, carbs, insulin, enteredBy, notes))						
				//				{
				//					result = i;
				//				}
				//			}

			}

			// 
			else
			{
				// Start at current rownum and go up the sequence
				for (int i = this.m_RowNum - 1; result == -1 && i >= 0; i--)
				{
					if (doesDBResultMatch(m_MongoResults.get(i),
							eventType, glucose, carbs, insulin, enteredBy, notes, 
							startDate, endDate, possDupe))						
					{
						result = i;
					}
				}

				// Let's not wrap round.
				// Instead, return -1 and let the shake begin ...

				//			// If not found then start at bottom and continue up again
				//			for (int i = this.m_MongoResults.size() - 1; result == -1 && i > this.m_RowNum; i--)
				//			{
				//				if (doesDBResultMatch(m_MongoResults.get(i),
				//						eventType, glucose, carbs, insulin, enteredBy, notes))						
				//				{
				//					result = i;
				//				}
				//			}

			}
		}

		return result;
	}

	void upSearchButtonClick()
	{
		m_LastDirection = LastDirection.up;
		searchAndHighlight();
	}

	void downSearchButtonClick()
	{
		m_LastDirection = LastDirection.down;
		searchAndHighlight();
	}

	private void findButtonClick()
	{
		searchAndHighlight();
	}

	public void vibrate()
	{
		vibrate(this);
	}


	// http://www.rgagnon.com/javadetails/java-0622.html
	public static void vibrate(JFrame frame) 
	{ 
		try { 
			final int originalX = frame.getLocationOnScreen().x; 
			final int originalY = frame.getLocationOnScreen().y; 
			for(int i = 0; i < VIBRATION_LENGTH; i++) { 
				Thread.sleep(10); 
				frame.setLocation(originalX, originalY + VIBRATION_VELOCITY); 
				Thread.sleep(10); 
				frame.setLocation(originalX, originalY - VIBRATION_VELOCITY);
				Thread.sleep(10); 
				frame.setLocation(originalX + VIBRATION_VELOCITY, originalY);
				Thread.sleep(10); 
				frame.setLocation(originalX, originalY); 
			} 
		} 
		catch (Exception err) { 
			err.printStackTrace(); 
		} 
	}

}
