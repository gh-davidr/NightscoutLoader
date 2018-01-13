package davidRichardson;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
//import java.awt.Desktop.Action;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

//import javax.swing.AbstractAction;
//import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
//import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

public class WinAuditHistory extends JFrame 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger m_Logger = Logger.getLogger( MyLogger.class.getName() );

	private JPanel          m_ContentPane;
	private JTable          m_AuditLogTable;
	private JScrollPane     m_AuditLogScrollPane;

	private JMenuItem       m_mntmExportResults; 
	private JMenuItem       m_mntmClose; 
	private JMenuItem       m_mntmFilterAuditLog;

	private int             m_RowUpdated = -1;
	private WinAuditLogForm m_WinAuditLog;
	private Date            m_MostRecentDate  = new Date(0);

	private WinNightScoutLoader     m_MainWin;

	public WinAuditHistory(WinNightScoutLoader mainWin, String title) 
	{
		super(title);

		m_MainWin = mainWin;

		//ImageIcon img = new ImageIcon("Images\\Nightscout.jpg");
		URL url = MainNightScoutLoader.class.getResource("/Nightscout.jpg");
		ImageIcon img = new ImageIcon(url);

		setIconImage(img.getImage());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) 
			{
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent arg0) 
			{
				// Refresh Grid when shown.
				updateGrid();
			}
		});
		initialize();
		//		updateGrid();
	}

	@Override
	public void setVisible(boolean visible)
	{
		// Keep track of the most recent date when the window becomes visible.
		// Try creating a copy to iterate over
		ArrayList <AuditLog> auditHistoryList = AuditHistory.getInstance().getM_AuditHistoryList();

		if (auditHistoryList != null)
		{
			Collections.sort(auditHistoryList, new AuditLogComparator(true));

			if (auditHistoryList.size() > 0)
			{
				AuditLog first = auditHistoryList.get(0);

				m_MostRecentDate = first.getM_UploadDate();
			}
		}

		// Also, reset the updated record too.
		m_RowUpdated = -1;

		super.setVisible(visible);
	}

	private void initialize()
	{
		setBounds(100, 100, 750, 450);
		//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setResizable(true);

		m_WinAuditLog = new WinAuditLogForm(this, "Nightscout Loader " + Version.getInstance().getM_Version() + " - Audit History - Audit Log");

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		mnFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
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


		m_mntmClose = new JMenuItem("Close");
		m_mntmClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doClose();
			}
		});
		mnFile.add(m_mntmClose);


		JMenu mnView = new JMenu("View");
		mnView.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
			}
		});

		menuBar.add(mnView);

		m_mntmFilterAuditLog = new JMenuItem("Switch to Active Only");
		m_mntmFilterAuditLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				boolean allShown = PrefsNightScoutLoader.getInstance().isM_AuditLogAllShown();
				// Toggle the setting
				PrefsNightScoutLoader.getInstance().setM_AuditLogAllShown(allShown ? false : true); 
				updateGrid();
			}
		});
		mnView.add(m_mntmFilterAuditLog);

		mnView.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				Boolean advancedOptions = PrefsNightScoutLoader.getInstance().isM_AdvancedSettings();

				m_Logger.log(Level.FINER, "NightScoutLoader.ActionMenuHandler: Adv: " + advancedOptions);

				// Disable the Delete menu item if Advanced option is not set.
				m_mntmFilterAuditLog.setEnabled(advancedOptions);

			}
		});


		m_ContentPane = new JPanel();
		setContentPane(m_ContentPane);

		m_ContentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel_3 = new JPanel();
		m_ContentPane.add(panel_3, BorderLayout.CENTER);

		m_AuditLogTable = new JTable();
		m_AuditLogTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		m_AuditLogTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int rowNum = m_AuditLogTable.getSelectedRow();
				displayAuditLogForm(getSelectedAuditLog(rowNum), rowNum);
			}
		});
		m_AuditLogTable.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		m_AuditLogTable.setModel(new DefaultTableModel(
			new Object[][] {
				{"", "", "", "", "", "", "", "", "", "", "", ""},
			},
			new String[] {
				"Date Time", "Upload ID", "Status", "Device", "Filename", "Date Range", "Entries Added", "CGM Entries Added", "Total Treatments at Start", "NSL Treatments at Start", "Meter Dupes", "NS Dupes"
			}
		)
				);

		// Try setting column widths
		TableColumn col;
		for(int i=0; i<m_AuditLogTable.getColumnCount(); i++) 
		{
			col = m_AuditLogTable.getColumnModel().getColumn(i);
			col.setPreferredWidth(200);
			col.setMaxWidth(500);
		}

		m_AuditLogTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
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

				final Date epochDate = new Date(0); 
				AuditLog auditLog = getSelectedAuditLog(row);

				// Check for modified row
				if (row == m_RowUpdated)
				{
					col = Color.YELLOW;
				}
				// Check for added rows
				else if (m_MostRecentDate.after(epochDate) && auditLog.getM_UploadDate().after(m_MostRecentDate))
				{
					col = Color.GREEN;
				}
				else
				{
					col = row % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE;
				}

				c.setBackground(col);
				return c;
			}
		});
		panel_3.setLayout(new BorderLayout(0, 0));


		m_AuditLogScrollPane = new JScrollPane();
		m_AuditLogScrollPane.setPreferredSize(new Dimension(680, 400));
		m_AuditLogScrollPane.setEnabled(false);
		m_AuditLogScrollPane.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		m_AuditLogScrollPane.setViewportView(m_AuditLogTable);

		panel_3.add(m_AuditLogScrollPane);

	}

	synchronized public void updateGrid()
	{
		boolean allShown = PrefsNightScoutLoader.getInstance().isM_AuditLogAllShown();
		Boolean advancedOptions = PrefsNightScoutLoader.getInstance().isM_AdvancedSettings();

		m_mntmFilterAuditLog.setText(allShown ? "Switch to Active Only" : "Switch to All"); 
		m_mntmFilterAuditLog.setEnabled(advancedOptions);

		updateGrid(allShown);
	}

	private AuditLog getSelectedAuditLog(int index)
	{
		// THis is needed since we might be filtering, so entry n in the view panel
		// will definitely not correspond with index being used
		AuditLog result = null;

		boolean allShown = PrefsNightScoutLoader.getInstance().isM_AuditLogAllShown();

		// Try creating a copy to iterate over
		ArrayList <AuditLog> auditHistoryList = AuditHistory.getInstance().getM_AuditHistoryList();
		Collections.sort(auditHistoryList, new AuditLogComparator(true));

		int c = -1;  // Start minus one in case we filter and first row is selected :-)

		Iterator<AuditLog> it = auditHistoryList.iterator();
		while (it.hasNext() && result == null) 
		{
			AuditLog auditLog = it.next();

			AuditLog.Status status = auditLog.getStatus();
			if (result == null && allShown == true || status == AuditLog.Status.Success)
			{
				c++;

				if (c == index)
				{
					result = auditLog;
				}
			}
		}

		return result;
	}

	public void displayAuditLogForm(AuditLog result, int rowNum)
	{
		m_RowUpdated = -1;
		m_WinAuditLog.initialize(result, rowNum);
		m_WinAuditLog.setVisible(true);
	}	
	synchronized private void updateGrid(boolean showAll)
	{
		// For the thread.
		//		m_MongoResults = m_NightScoutLoaderCore.getM_ResultsMongoDB();
		//		changeStatusText(m_NightScoutLoaderCore.getM_StatusText());

		DefaultTableModel model = (DefaultTableModel) m_AuditLogTable.getModel();
		// Clear current table model...
		model.setRowCount(0);

		// Try creating a copy to iterate over
		ArrayList <AuditLog> auditHistoryList = AuditHistory.getInstance().getM_AuditHistoryList();

		Collections.sort(auditHistoryList, new AuditLogComparator(true));

		int c = 0;

		// This isn't protecting access here...
		synchronized(auditHistoryList)
		{
			try
			{
				for (AuditLog x : auditHistoryList)
				{
					String status = x.getM_UploadStatus();
					//					String subStr = status.length() < AuditLog.m_DeletedBy.length() ? status : status.substring(0, AuditLog.m_DeletedBy.length());
					if (showAll == true || status.equals(AuditLog.m_Success))
					{
						model.addRow(x.toArray());
						c++;
					}
				}

				// Popup a message if none are there
				if (c == 0)
				{
					JOptionPane.showMessageDialog(null, 
							"Filtering for active audit records only, however there are none!\n\n" +
									"This means that Nightscout Loader has not loaded any results into your Nightscout\n\n" +
							"Please use Action->Synchronize to load data.");
				}
			}
			catch (Exception e)
			{
				m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + ".updateGrid: just caught an error: " + e.getMessage() + " - " + e.getLocalizedMessage());
			}
		}
	}

	public void rowUpdated(int rowNum)
	{
		// Now extra special ...
		// Only set this if not filtering for success
		boolean allShown = PrefsNightScoutLoader.getInstance().isM_AuditLogAllShown();

		if (allShown)
		{
			m_RowUpdated = rowNum + 1;
		}
		else
		{
			m_RowUpdated = -1;
		}

		updateGrid();
	}

	private void doExportResults()
	{
		JOptionPane.showMessageDialog(null, 
				"Not yet implemented...");

	}

	private void doClose()
	{
		this.dispose();
	}

	public void reverseSynchButtonClick()
	{
		// The form has just deleted entries.
		// Need to inform the main win to reload and refresh

		m_MainWin.reverseSynchButtonClick();

		// Must also deal with it here too.
		updateGrid();
	}

}
