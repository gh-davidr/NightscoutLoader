package davidRichardson;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Dimension;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.ActionEvent;
import javax.swing.JEditorPane;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class WinMongoForm extends JFrame 
{

	private static final Logger m_Logger = Logger.getLogger( MyLogger.class.getName() );

	/**
	 * 
	 */
	private static final long serialVersionUID = 6069119838030173232L;
	
	private JPanel contentPane;
	private JTextField  tf_CP_EventType;
	private JTextField tf_CP_Glucose;
	private JTextField tf_CP_Carbs;
	private JTextField tf_CP_Insulin;
	private JTextField tf_CP_CarbsTime;
	private JTextField tf_CP_EventDate;
	private JTextField tf_CP_EnteredBy;
	private JEditorPane tf_CP_Notes;
	
	private JLabel    lbl_PossibleDuplicate;
	private JComboBox<String> cb_Proximity;
	
	// Passed in from calling window
	private DBResult m_result;
	WinNightScoutLoader  m_MainWin;
	
	// Keep track of row num from grid too
	private int m_RowNum;
	
	// For updates
	private DataLoadNightScout m_DataLoadMongoDB;
	
	// For logging
	private JTextField tf_CP_EventTime;
	private JTextField tf_CP_EventDay;

	/**
	 * Create the frame.
	 */
	public WinMongoForm(WinNightScoutLoader mainWin, String title) 
	{
		super();
		//ImageIcon img = new ImageIcon("Images\\Nightscout.jpg");
		URL url = MainNightScoutLoader.class.getResource("/Nightscout.jpg");
		ImageIcon img = new ImageIcon(url);
		setIconImage(img.getImage());

		m_DataLoadMongoDB = new DataLoadNightScout();
		
		m_MainWin = mainWin;
		
		super.setTitle(title);
		setBounds(100, 100, 356, 432);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		JPanel panel_2 = new JPanel();
		panel_2.setMinimumSize(new Dimension(100, 100));
		panel_2.setMaximumSize(new Dimension(100, 100));
		contentPane.add(panel_2, BorderLayout.CENTER);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{82, 147, 0};
		gbl_panel_2.rowHeights = new int[]{30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		JLabel lblEntryType = new JLabel("Result Type");
		GridBagConstraints gbc_lblEntryType = new GridBagConstraints();
		gbc_lblEntryType.anchor = GridBagConstraints.EAST;
		gbc_lblEntryType.insets = new Insets(0, 0, 5, 5);
		gbc_lblEntryType.gridx = 0;
		gbc_lblEntryType.gridy = 0;
		panel_2.add(lblEntryType, gbc_lblEntryType);
		
		tf_CP_EventType = new JTextField();
		tf_CP_EventType.setEditable(false);
		tf_CP_EventType.setBackground(Color.WHITE);
		GridBagConstraints gbc_tf_ResultType = new GridBagConstraints();
		gbc_tf_ResultType.insets = new Insets(0, 0, 5, 0);
		gbc_tf_ResultType.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_ResultType.gridx = 1;
		gbc_tf_ResultType.gridy = 0;
		panel_2.add(tf_CP_EventType, gbc_tf_ResultType);
		
		JLabel lblNewLabel = new JLabel("Glucose");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		panel_2.add(lblNewLabel, gbc_lblNewLabel);
		
		tf_CP_Glucose = new JTextField();
		tf_CP_Glucose.setEditable(false);
		GridBagConstraints gbc_tf_Year = new GridBagConstraints();
		gbc_tf_Year.insets = new Insets(0, 0, 5, 0);
		gbc_tf_Year.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_Year.gridx = 1;
		gbc_tf_Year.gridy = 1;
		panel_2.add(tf_CP_Glucose, gbc_tf_Year);
		tf_CP_Glucose.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Carbs");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 2;
		panel_2.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		tf_CP_Carbs = new JTextField();
		tf_CP_Carbs.setEditable(false);
		GridBagConstraints gbc_tf_Month = new GridBagConstraints();
		gbc_tf_Month.insets = new Insets(0, 0, 5, 0);
		gbc_tf_Month.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_Month.gridx = 1;
		gbc_tf_Month.gridy = 2;
		panel_2.add(tf_CP_Carbs, gbc_tf_Month);
		tf_CP_Carbs.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Insulin");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 3;
		panel_2.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		tf_CP_Insulin = new JTextField();
		tf_CP_Insulin.setEditable(false);
		GridBagConstraints gbc_tf_Day = new GridBagConstraints();
		gbc_tf_Day.insets = new Insets(0, 0, 5, 0);
		gbc_tf_Day.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_Day.gridx = 1;
		gbc_tf_Day.gridy = 3;
		panel_2.add(tf_CP_Insulin, gbc_tf_Day);
		tf_CP_Insulin.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("Carbs Time");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 4;
		panel_2.add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		tf_CP_CarbsTime = new JTextField();
		tf_CP_CarbsTime.setEditable(false);
		GridBagConstraints gbc_tf_DayName = new GridBagConstraints();
		gbc_tf_DayName.insets = new Insets(0, 0, 5, 0);
		gbc_tf_DayName.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_DayName.gridx = 1;
		gbc_tf_DayName.gridy = 4;
		panel_2.add(tf_CP_CarbsTime, gbc_tf_DayName);
		tf_CP_CarbsTime.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("Date");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 5;
		panel_2.add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		tf_CP_EventDate = new JTextField();
		tf_CP_EventDate.setEditable(false);
		GridBagConstraints gbc_tf_CP_EventDate = new GridBagConstraints();
		gbc_tf_CP_EventDate.insets = new Insets(0, 0, 5, 0);
		gbc_tf_CP_EventDate.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_CP_EventDate.gridx = 1;
		gbc_tf_CP_EventDate.gridy = 5;
		panel_2.add(tf_CP_EventDate, gbc_tf_CP_EventDate);
		tf_CP_EventDate.setColumns(10);
		
		JLabel label = new JLabel("Time");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 0;
		gbc_label.gridy = 6;
		panel_2.add(label, gbc_label);
		
		tf_CP_EventTime = new JTextField();
		tf_CP_EventTime.setEditable(false);
		tf_CP_EventTime.setColumns(10);
		GridBagConstraints gbc_tf_CP_EventTime = new GridBagConstraints();
		gbc_tf_CP_EventTime.insets = new Insets(0, 0, 5, 0);
		gbc_tf_CP_EventTime.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_CP_EventTime.gridx = 1;
		gbc_tf_CP_EventTime.gridy = 6;
		panel_2.add(tf_CP_EventTime, gbc_tf_CP_EventTime);
		
		JLabel label_1 = new JLabel("Day");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.anchor = GridBagConstraints.EAST;
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		gbc_label_1.gridx = 0;
		gbc_label_1.gridy = 7;
		panel_2.add(label_1, gbc_label_1);
		
		tf_CP_EventDay = new JTextField();
		tf_CP_EventDay.setEditable(false);
		tf_CP_EventDay.setColumns(10);
		GridBagConstraints gbc_tf_CP_EventDay = new GridBagConstraints();
		gbc_tf_CP_EventDay.insets = new Insets(0, 0, 5, 0);
		gbc_tf_CP_EventDay.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_CP_EventDay.gridx = 1;
		gbc_tf_CP_EventDay.gridy = 7;
		panel_2.add(tf_CP_EventDay, gbc_tf_CP_EventDay);
		
		JLabel lblNewLabel_5 = new JLabel("Entered By");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 8;
		panel_2.add(lblNewLabel_5, gbc_lblNewLabel_5);
		
		tf_CP_EnteredBy = new JTextField();
		tf_CP_EnteredBy.setEditable(false);
		GridBagConstraints gbc_tf_TimeSlot = new GridBagConstraints();
		gbc_tf_TimeSlot.insets = new Insets(0, 0, 5, 0);
		gbc_tf_TimeSlot.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_TimeSlot.gridx = 1;
		gbc_tf_TimeSlot.gridy = 8;
		panel_2.add(tf_CP_EnteredBy, gbc_tf_TimeSlot);
		tf_CP_EnteredBy.setColumns(10);
		
		JLabel lblNewLabel_6 = new JLabel("Notes");
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 9;
		panel_2.add(lblNewLabel_6, gbc_lblNewLabel_6);
		
		tf_CP_Notes = new JEditorPane();
		GridBagConstraints gbc_tf_Result = new GridBagConstraints();
		gbc_tf_Result.insets = new Insets(0, 0, 5, 0);
		gbc_tf_Result.gridheight = 3;
		gbc_tf_Result.fill = GridBagConstraints.BOTH;
		gbc_tf_Result.gridx = 1;
		gbc_tf_Result.gridy = 9;
		
		panel_2.add(tf_CP_Notes, gbc_tf_Result);
		
		lbl_PossibleDuplicate = new JLabel("Possible Duplicate");
		GridBagConstraints gbc_lblPossibleDuplicate = new GridBagConstraints();
		gbc_lblPossibleDuplicate.anchor = GridBagConstraints.EAST;
		gbc_lblPossibleDuplicate.insets = new Insets(0, 0, 0, 5);
		gbc_lblPossibleDuplicate.gridx = 0;
		gbc_lblPossibleDuplicate.gridy = 12;
		panel_2.add(lbl_PossibleDuplicate, gbc_lblPossibleDuplicate);
		
		cb_Proximity = new JComboBox<String>();
		cb_Proximity.setModel(new DefaultComboBoxModel<String>(new String[] {"Confirmed Duplicate", "Confirmed NOT Duplicate"}));
	
		GridBagConstraints gbc_cbProximity = new GridBagConstraints();
		gbc_cbProximity.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbProximity.gridx = 1;
		gbc_cbProximity.gridy = 12;
		panel_2.add(cb_Proximity, gbc_cbProximity);


		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		
		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				okButtonClick();
			}
		});
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
	}
	

	public void initialize(DBResult result, int rowNum)
	{
		m_result = result;
		m_RowNum = rowNum;
		initialize();
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
}
