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
//import java.util.Date;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
//import javax.swing.JEditorPane;

public class WinAuditLogForm extends JFrame 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

//	private static final Logger m_Logger = Logger.getLogger( MyLogger.class.getName() );

	/**
	 * 
	 */

	private JPanel contentPane;

	//private JTextField  tf_ID;
	private JTextField  tf_UploadID;
	private JTextField  tf_UploadStatus;
	private JTextField  tf_UploadDate;
	private JTextField  tf_UploadDevice;
	private JTextField  tf_FileName;
	private JTextField  tf_DateRange;
	private JTextField  tf_EntriesAdded;
	private JTextField  tf_TreatmentsAtStart;
	private JTextField  tf_TreatmentsByNSLAtStart;
	private JTextField  tf_ProximityMeterEntries;
	private JTextField  tf_ProximityNSEntries;

	private JButton     btn_ReverseSynchronization;
	private JButton     btn_ReverseProximityEntries;
	
	// Passed in from calling window
	private AuditLog m_result  = null;
	WinAuditHistory  m_MainWin = null;

	// Keep track of row num from grid too
	private int m_RowNum;

	// For updates
//	private AuditHistory m_AuditHistory;

	/**
	 * Create the frame.
	 */
	public WinAuditLogForm(WinAuditHistory mainWin, String title) 
	{
		super();
		URL url = MainNightScoutLoader.class.getResource("/Nightscout.jpg");
		ImageIcon img = new ImageIcon(url);
		setIconImage(img.getImage());

//		m_AuditHistory = AuditHistory.getInstance();

		m_MainWin = mainWin;

		super.setTitle(title);
		setBounds(100, 100, 600, 400);
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
		gbl_panel_2.rowHeights = new int[]{30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);

		JLabel lblEntryType = new JLabel("Upload ID");
		GridBagConstraints gbc_lblEntryType = new GridBagConstraints();
		gbc_lblEntryType.anchor = GridBagConstraints.EAST;
		gbc_lblEntryType.insets = new Insets(0, 0, 5, 5);
		gbc_lblEntryType.gridx = 0;
		gbc_lblEntryType.gridy = 0;
		panel_2.add(lblEntryType, gbc_lblEntryType);

		tf_UploadID = new JTextField();
		tf_UploadID.setEditable(false);
		tf_UploadID.setBackground(Color.WHITE);
		GridBagConstraints gbc_tf_ResultType = new GridBagConstraints();
		gbc_tf_ResultType.insets = new Insets(0, 0, 5, 0);
		gbc_tf_ResultType.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_ResultType.gridx = 1;
		gbc_tf_ResultType.gridy = 0;
		panel_2.add(tf_UploadID, gbc_tf_ResultType);

		JLabel lblNewLabel = new JLabel("Upload Status");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 1;
		panel_2.add(lblNewLabel, gbc_lblNewLabel);

		tf_UploadStatus = new JTextField();
		tf_UploadStatus.setEditable(false);
		GridBagConstraints gbc_tf_Year = new GridBagConstraints();
		gbc_tf_Year.insets = new Insets(0, 0, 5, 0);
		gbc_tf_Year.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_Year.gridx = 1;
		gbc_tf_Year.gridy = 1;
		panel_2.add(tf_UploadStatus, gbc_tf_Year);
		tf_UploadStatus.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Upload Date");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 2;
		panel_2.add(lblNewLabel_1, gbc_lblNewLabel_1);

		tf_UploadDate = new JTextField();
		tf_UploadDate.setEditable(false);
		GridBagConstraints gbc_tf_Month = new GridBagConstraints();
		gbc_tf_Month.insets = new Insets(0, 0, 5, 0);
		gbc_tf_Month.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_Month.gridx = 1;
		gbc_tf_Month.gridy = 2;
		panel_2.add(tf_UploadDate, gbc_tf_Month);
		tf_UploadDate.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Upload Device");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 3;
		panel_2.add(lblNewLabel_2, gbc_lblNewLabel_2);

		tf_UploadDevice = new JTextField();
		tf_UploadDevice.setEditable(false);
		GridBagConstraints gbc_tf_Day = new GridBagConstraints();
		gbc_tf_Day.insets = new Insets(0, 0, 5, 0);
		gbc_tf_Day.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_Day.gridx = 1;
		gbc_tf_Day.gridy = 3;
		panel_2.add(tf_UploadDevice, gbc_tf_Day);
		tf_UploadDevice.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Filename");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 4;
		panel_2.add(lblNewLabel_3, gbc_lblNewLabel_3);

		tf_FileName = new JTextField();
		tf_FileName.setEditable(false);
		GridBagConstraints gbc_tf_DayName = new GridBagConstraints();
		gbc_tf_DayName.insets = new Insets(0, 0, 5, 0);
		gbc_tf_DayName.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_DayName.gridx = 1;
		gbc_tf_DayName.gridy = 4;
		panel_2.add(tf_FileName, gbc_tf_DayName);
		tf_FileName.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("Date Range");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 0;
		gbc_lblNewLabel_4.gridy = 5;
		panel_2.add(lblNewLabel_4, gbc_lblNewLabel_4);

		tf_DateRange = new JTextField();
		tf_DateRange.setEditable(false);
		GridBagConstraints gbc_tf_DateRange = new GridBagConstraints();
		gbc_tf_DateRange.insets = new Insets(0, 0, 5, 0);
		gbc_tf_DateRange.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_DateRange.gridx = 1;
		gbc_tf_DateRange.gridy = 5;
		panel_2.add(tf_DateRange, gbc_tf_DateRange);
		tf_DateRange.setColumns(10);

		JLabel label = new JLabel("Entries Added");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 0;
		gbc_label.gridy = 6;
		panel_2.add(label, gbc_label);

		tf_EntriesAdded = new JTextField();
		tf_EntriesAdded.setEditable(false);
		tf_EntriesAdded.setColumns(10);
		GridBagConstraints gbc_tf_EntriesAdded = new GridBagConstraints();
		gbc_tf_EntriesAdded.insets = new Insets(0, 0, 5, 0);
		gbc_tf_EntriesAdded.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_EntriesAdded.gridx = 1;
		gbc_tf_EntriesAdded.gridy = 6;
		panel_2.add(tf_EntriesAdded, gbc_tf_EntriesAdded);

		JLabel label_1 = new JLabel("Treatments At Start");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.anchor = GridBagConstraints.EAST;
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		gbc_label_1.gridx = 0;
		gbc_label_1.gridy = 7;
		panel_2.add(label_1, gbc_label_1);

		tf_TreatmentsAtStart = new JTextField();
		tf_TreatmentsAtStart.setEditable(false);
		tf_TreatmentsAtStart.setColumns(10);
		GridBagConstraints gbc_tf_TreatmentsAtStart = new GridBagConstraints();
		gbc_tf_TreatmentsAtStart.insets = new Insets(0, 0, 5, 0);
		gbc_tf_TreatmentsAtStart.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_TreatmentsAtStart.gridx = 1;
		gbc_tf_TreatmentsAtStart.gridy = 7;
		panel_2.add(tf_TreatmentsAtStart, gbc_tf_TreatmentsAtStart);

		JLabel lblNewLabel_5 = new JLabel("Nightscout Loader Treatments");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_5.gridx = 0;
		gbc_lblNewLabel_5.gridy = 8;
		panel_2.add(lblNewLabel_5, gbc_lblNewLabel_5);

		tf_TreatmentsByNSLAtStart = new JTextField();
		tf_TreatmentsByNSLAtStart.setEditable(false);
		GridBagConstraints gbc_tf_TimeSlot = new GridBagConstraints();
		gbc_tf_TimeSlot.insets = new Insets(0, 0, 5, 0);
		gbc_tf_TimeSlot.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_TimeSlot.gridx = 1;
		gbc_tf_TimeSlot.gridy = 8;
		panel_2.add(tf_TreatmentsByNSLAtStart, gbc_tf_TimeSlot);
		tf_TreatmentsByNSLAtStart.setColumns(10);

		JLabel lblNewLabel_6 = new JLabel("Meter Dupes");
		GridBagConstraints gbc_lblNewLabel_6 = new GridBagConstraints();
		gbc_lblNewLabel_6.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_6.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_6.gridx = 0;
		gbc_lblNewLabel_6.gridy = 9;
		panel_2.add(lblNewLabel_6, gbc_lblNewLabel_6);
		
		tf_ProximityMeterEntries = new JTextField();
		tf_ProximityMeterEntries.setEditable(false);
		GridBagConstraints gbc_ProximityMeterEntries = new GridBagConstraints();
		gbc_ProximityMeterEntries.insets = new Insets(0, 0, 5, 0);
		gbc_ProximityMeterEntries.fill = GridBagConstraints.HORIZONTAL;
		gbc_ProximityMeterEntries.gridx = 1;
		gbc_ProximityMeterEntries.gridy = 9;
		panel_2.add(tf_ProximityMeterEntries, gbc_ProximityMeterEntries);
		tf_ProximityMeterEntries.setColumns(10);
		
		JLabel lblNightscoutDupes = new JLabel("Nightscout Dupes");
		lblNightscoutDupes.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblNightscoutDupes = new GridBagConstraints();
		gbc_lblNightscoutDupes.anchor = GridBagConstraints.EAST;
		gbc_lblNightscoutDupes.insets = new Insets(0, 0, 5, 5);
		gbc_lblNightscoutDupes.gridx = 0;
		gbc_lblNightscoutDupes.gridy = 10;
		panel_2.add(lblNightscoutDupes, gbc_lblNightscoutDupes);
		
		tf_ProximityNSEntries = new JTextField();
		tf_ProximityNSEntries.setEditable(false);
		tf_ProximityNSEntries.setColumns(10);
		GridBagConstraints gbc_tf_ProximityNSEntries = new GridBagConstraints();
		gbc_tf_ProximityNSEntries.insets = new Insets(0, 0, 5, 0);
		gbc_tf_ProximityNSEntries.fill = GridBagConstraints.HORIZONTAL;
		gbc_tf_ProximityNSEntries.gridx = 1;
		gbc_tf_ProximityNSEntries.gridy = 10;
		panel_2.add(tf_ProximityNSEntries, gbc_tf_ProximityNSEntries);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);

		btn_ReverseSynchronization = new JButton("Reverse Synchronization");
		btn_ReverseSynchronization.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				reverseSynchButtonClick();
			}
		});
		panel_1.add(btn_ReverseSynchronization);
		
		btn_ReverseProximityEntries = new JButton("Reverse Proximity Entries");
		btn_ReverseProximityEntries.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				reverseProximityEntriesButtonClick();
			}
		});
		panel_1.add(btn_ReverseProximityEntries);

		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cancelButtonClick();
			}
		});
		panel_1.add(btnCancel);
	}

	public void initialize(AuditLog result, int rowNum)
	{
		m_result = result;
		m_RowNum = rowNum;
		
		setReverseSynchronizationButtonStates();

		initialize();
	}
//	private void setTextFromDouble(JTextField tf, Double val)
//	{
//		if (val != null)
//		{
//			if (DBResult.doubleIsInteger(val))
//			{
//				tf.setText(Long.toString(val.longValue()));
//			}
//			else
//			{
//				tf.setText(val.toString());
//			}
//		}
//		else
//		{
//			tf.setText("");
//		}
//	}
	public void initialize()
	{
		tf_UploadID.setText(m_result.getM_UploadID());
		tf_UploadStatus.setText(m_result.getM_UploadStatus());
		tf_UploadDate.setText(m_result.getM_UploadDateString());
		tf_UploadDevice.setText(m_result.getM_UploadDevice());
		tf_FileName.setText(m_result.getM_FileName());
		tf_DateRange.setText(m_result.getM_DateRange());
		tf_EntriesAdded.setText(String.format("%d", m_result.getM_EntriesAdded()));
		tf_TreatmentsAtStart.setText(String.format("%d", m_result.getM_TreatmentsAtStart()));
		tf_TreatmentsByNSLAtStart.setText(String.format("%d", m_result.getM_TreatmentsByNSLAtStart()));
		
		tf_ProximityMeterEntries.setText(String.format("%d", m_result.getM_ProximityMeterEntries()));
		tf_ProximityNSEntries.setText(String.format("%d", m_result.getM_ProximityNSEntries()));
	}

	//	public boolean updateResultFromDB()
	//	{
	//		// Change to true if we need to do an update :-)
	//		boolean result = false;
	//		if (!m_result.getM_CP_Notes().equals(tf_CP_Notes.getText()))
	//		{
	//			result = true;
	//			m_result.setM_CP_Notes(tf_CP_Notes.getText());
	//
	//			// Now update the result on DB too.
	//			// FOr now, do the actual mongo update here.  Will figure out where best to move later
	//			try
	//			{
	//				m_AuditHistory.updateNotesOnly(m_result);
	//			}
	//			catch(Exception e)
	//			{
	//				m_Logger.log(Level.SEVERE, WinAuditLogForm.class.getName() + " just caught an error: " + e.getMessage() + " - " + e.getLocalizedMessage());
	//			}
	//		}
	//		return result;
	//
	//	}

	private void setReverseSynchronizationButtonStates()
	{
		// State set to enabled for active records only
		// AND if the advanced option is set.
		boolean activeRecord = false;
		Boolean advancedOptions = PrefsNightScoutLoader.getInstance().isM_AdvancedSettings();

		if (advancedOptions == true && m_result != null)
		{
			AuditLog.Status status = m_result.getStatus();
			activeRecord = (status == AuditLog.Status.Success || status == AuditLog.Status.Not_Saved) ? true : false;
		}

		btn_ReverseSynchronization.setEnabled(activeRecord);
		
		// Do extra check for proximity - only enable if there are any entries here.
		if (m_result.getM_ProximityMeterEntries() == 0 &&
				m_result.getM_ProximityNSEntries() == 0)
		{
			activeRecord = false;
		}
		btn_ReverseProximityEntries.setEnabled(activeRecord);
	}

	private void reverseSynchButtonClick()
	{
		// If we do update then inform the main window
		m_MainWin.rowUpdated(m_RowNum);
		
		// Get Core to remove required entries
		CoreNightScoutLoader.getInstance().deleteLoadedTreatment(m_result);
		
		// Challenge now is to get the main window to load nightscout again and update
		// See CoreNightScoutLoader.doDeleteLoadedTreatments()
		
		dispose();
		
		// Tell main window to refresh
		m_MainWin.reverseSynchButtonClick();
	}
	
	private void reverseProximityEntriesButtonClick()
	{
		// If we do update then inform the main window
		m_MainWin.rowUpdated(m_RowNum);
		
		// Get Core to remove required entries
		CoreNightScoutLoader.getInstance().deleteLoadedProximityTreatment(m_result);
				
		dispose();
		
		// Tell main window to refresh
		m_MainWin.reverseSynchButtonClick();
	}

	private void cancelButtonClick()
	{
		dispose();
	}


}
