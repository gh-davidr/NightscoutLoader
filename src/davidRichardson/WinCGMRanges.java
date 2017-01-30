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
import javax.swing.JButton;
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

public class WinCGMRanges extends JFrame 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger m_Logger = Logger.getLogger( MyLogger.class.getName() );

	private JPanel          m_ContentPane;
	private JTable          m_CGMRangesTable;
	private JScrollPane     m_CGMRangesScrollPane;

	private JMenuItem       m_mntmExportResults; 
	private JMenuItem       m_mntmClose; 

	private int             m_RowSelected = -1;
	private Date            m_MostRecentDate  = new Date(0);

	private WinAnalyzer     m_WinAnalyzer = null;

	private AnalyzerEntries                            m_AnalyzerEntries = null;
	private ArrayList<AnalyzerEntriesCGMRange>         m_CGMRanges = null;

	private Date            m_JTableDoubleClickTime = new Date(0);

	public WinCGMRanges(WinAnalyzer mainWin, String title) 
	{
		super(title);

		m_WinAnalyzer = mainWin;

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
		// Also, reset the updated record too.
		m_RowSelected = -1;

		// Only set visible if we have records to display
		if (this.m_CGMRanges.size() > 0)
		{
			super.setVisible(visible);
		}
	}

	public void intializeCGMRanges()
	{
		if (m_AnalyzerEntries == null)
		{
			m_AnalyzerEntries = new AnalyzerEntries(CoreNightScoutLoader.getInstance().getM_NightScoutArrayListDBResultEntries(),
					CoreNightScoutLoader.getInstance().getM_ResultsMongoDB());
			m_AnalyzerEntries.initialize(CoreNightScoutLoader.getInstance().getM_NightScoutArrayListDBResultEntries());		
			m_CGMRanges   = m_AnalyzerEntries.getM_CGMRanges();
		}
	}

	private void initialize()
	{
		setBounds(110, 110, 700, 350);
		//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		this.setResizable(true);

		m_ContentPane = new JPanel();
		setContentPane(m_ContentPane);

		m_ContentPane.setLayout(new BorderLayout(0, 0));


		JPanel panel_3 = new JPanel();
		m_ContentPane.add(panel_3, BorderLayout.NORTH);
		panel_3.setLayout(new BorderLayout(0, 0));


		m_CGMRangesTable = new JTable();
		m_CGMRangesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		m_CGMRangesTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				Date now = new Date();

				// 250 ms for double click
				if (now.getTime() - m_JTableDoubleClickTime.getTime() < 250)
				{
					okButtonClick();
				}
				else
				{
					m_JTableDoubleClickTime = now;
				}

			}
		});



		m_CGMRangesTable.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		m_CGMRangesTable.setModel(new DefaultTableModel(
				AnalyzerEntriesCGMRange.getM_Initializer(),
				AnalyzerEntriesCGMRange.getM_ColNames()
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
				return false;
			}}
				);

		// Try setting column widths
		TableColumn col;
		int[] colWidths = AnalyzerEntriesCGMRange.getM_ColWidths();

		for(int i=0; i<m_CGMRangesTable.getColumnCount(); i++) 
		{
			col = m_CGMRangesTable.getColumnModel().getColumn(i);
			//			col.setPreferredWidth(200);
			//			col.setMaxWidth(500);
			col.setPreferredWidth(colWidths[i]);
			col.setMaxWidth(colWidths[i] * 2);
		}

		m_CGMRangesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
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
				AnalyzerEntriesCGMRange cgmRange = getSelectedAnalyzerEntriesCGMRange(row);
				int rowNum = m_CGMRangesTable.getSelectedRow();
				AnalyzerEntriesCGMRange.DateOverlap dateOverlap = cgmRange.getM_DateOverlap();

				// Could do something here like check min and max entries dates
				// and colour differently accordingly.

				// Check for modified row
				if (row == rowNum)
				{
					col = Color.YELLOW;
				}

				else if (dateOverlap == AnalyzerEntriesCGMRange.DateOverlap.StartsAndEndsAfterTreatments ||
						dateOverlap == AnalyzerEntriesCGMRange.DateOverlap.StartsAndEndsBeforeTreatments)
				{
					col = Color.RED;
				}

				else if (dateOverlap == AnalyzerEntriesCGMRange.DateOverlap.StartsBeforeTreatments ||
						dateOverlap == AnalyzerEntriesCGMRange.DateOverlap.EndsAfterTreatments)
				{
					col = Color.ORANGE;
				}
				/*
				// Check for added rows
				else if (m_MostRecentDate.after(epochDate) && auditLog.getM_UploadDate().after(m_MostRecentDate))
				{
					col = Color.GREEN;
				}
				 */
				else
				{
					col = row % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE;
				}

				c.setBackground(col);
				return c;
			}
		});
		panel_3.setLayout(new BorderLayout(0, 0));


		m_CGMRangesScrollPane = new JScrollPane();
		m_CGMRangesScrollPane.setPreferredSize(new Dimension(500, 200));
		m_CGMRangesScrollPane.setEnabled(false);
		m_CGMRangesScrollPane.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		m_CGMRangesScrollPane.setViewportView(m_CGMRangesTable);
		m_CGMRangesScrollPane.setAutoscrolls(true);
		m_CGMRangesScrollPane.setToolTipText("These date ranges we find CGM values");

		panel_3.add(m_CGMRangesScrollPane);



		JPanel panel_1 = new JPanel();
		m_ContentPane.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));

		JPanel panel_2 = new JPanel();
		m_ContentPane.add(panel_2, BorderLayout.SOUTH);
		//		panel_2.setLayout(new BorderLayout(0, 0));

		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				okButtonClick();
			}
		});
		panel_2.add(btnOK);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cancelButtonClick();
			}
		});
		panel_2.add(btnCancel);

	}

	private AnalyzerEntriesCGMRange getSelectedAnalyzerEntriesCGMRange(int index)
	{
		// THis is needed since we might be filtering, so entry n in the view panel
		// will definitely not correspond with index being used
		AnalyzerEntriesCGMRange result = null;

		// Try creating a copy to iterate over
		int c = -1;  // Start minus one in case we filter and first row is selected :-)

		Iterator<AnalyzerEntriesCGMRange> it = m_CGMRanges.iterator();
		while (it.hasNext() && result == null) 
		{
			AnalyzerEntriesCGMRange cgmRange = it.next();
			c++;

			if (c == index)
			{
				result = cgmRange;
			}
		}

		return result;
	}

	synchronized private void updateGrid()
	{
		// For the thread.
		//		m_MongoResults = m_NightScoutLoaderCore.getM_ResultsMongoDB();
		//		changeStatusText(m_NightScoutLoaderCore.getM_StatusText());

		DefaultTableModel model = (DefaultTableModel) m_CGMRangesTable.getModel();
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
				for (AnalyzerEntriesCGMRange x : m_CGMRanges)
				{
					model.addRow(x.toArray());
					c++;
				}

				// Popup a message if none are there
				if (c == 0)
				{
					JOptionPane.showMessageDialog(null, 
							"No CGM Date Ranges found.  Is CGM loading enabled?");
				}
			}
			catch (Exception e)
			{
				m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + ".updateGrid: just caught an error: " + e.getMessage() + " - " + e.getLocalizedMessage());
			}
		}
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

	private void okButtonClick()
	{
		AnalyzerEntriesCGMRange selectedCGM = getSelectedAnalyzerEntriesCGMRange(m_CGMRangesTable.getSelectedRow());

		if (selectedCGM != null)
		{
			// Get date ranges
			// Set them in the analyzer window
			Date startDate = selectedCGM.getM_StartDate();
			Date endDate   = selectedCGM.getM_EndDate();

			m_WinAnalyzer.setDates(startDate, endDate);
		}

		dispose();
	}

	private void cancelButtonClick()
	{
		dispose();
	}


}
