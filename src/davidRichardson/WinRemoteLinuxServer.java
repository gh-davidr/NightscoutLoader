package davidRichardson;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import davidRichardson.ThreadAutotune.AutotuneCompleteHandler;

public class WinRemoteLinuxServer extends WinTextWin  implements WinSetDatesInterface
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	protected JMenu              m_mnEdit;
	protected JMenuItem          m_mntmEditProfileEditor; 
	protected JMenuItem          m_mntmEditCGMDates; 

	protected JMenu              m_mnAction;
	protected JMenuItem          m_mntmActionRunAutotune; 
	protected JMenuItem          m_mntmActionListBackups; 
	protected JMenuItem          m_mntmActionCopyAllBackups; 

	private   Date               m_DefaultStartDate;
	private   Date               m_DefaultEndDate;

	private  RemoteLinuxServer   m_Autotune;
	private  ThreadAutotune      m_ATThread;
	

	protected WinAutotuneProfile m_WinAutotuneProfile;

	public WinRemoteLinuxServer(String title) 
	{
		super(title);

		URL url = MainNightScoutLoader.class.getResource("/Nightscout.jpg");
		ImageIcon img = new ImageIcon(url);
		setIconImage(img.getImage());

		m_DefaultStartDate = new Date(0);
		m_DefaultEndDate   = new Date(0);
		m_Autotune         = null;
		m_ATThread         = null;

		m_mnEdit = new JMenu("Edit");
		m_mnEdit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				// Nothing needed here
			}
		});
		m_MenuBar.add(m_mnEdit);

		m_mntmEditProfileEditor = new JMenuItem("Edit Autotune Profile");
		m_mntmEditProfileEditor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				displayAutotuneProfile();
			}
		});
		m_mnEdit.add(m_mntmEditProfileEditor);
		m_mnEdit.add(new JSeparator()); // SEPARATOR

		m_mntmEditCGMDates = new JMenuItem("Select Alternate Dates");
		m_mntmEditCGMDates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				displayCGMDates();
			}
		});
		m_mnEdit.add(m_mntmEditCGMDates);


		m_mnAction = new JMenu("Action");
		m_mnAction.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				// Nothing needed here
			}
		});
		m_MenuBar.add(m_mnAction);

		m_mnAction.add(new JSeparator()); // SEPARATOR
		m_mntmActionRunAutotune = new JMenuItem("Run Autotune");
		m_mntmActionRunAutotune.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				runAutotune();
			}
		});
		m_mnAction.add(m_mntmActionRunAutotune);
		m_mnAction.add(new JSeparator()); // SEPARATOR

		m_mntmActionListBackups = new JMenuItem("List Remote Profile Backups");
		m_mntmActionListBackups.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				listRemoteBackups();
			}
		});
		m_mnAction.add(m_mntmActionListBackups);	
		m_mnAction.add(new JSeparator()); // SEPARATOR

		m_mntmActionCopyAllBackups = new JMenuItem("Download All Remote Profile Backups");
		m_mntmActionCopyAllBackups.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				copyRemoteBackups();
			}
		});
		m_mnAction.add(m_mntmActionCopyAllBackups);	

	}
	
	public void setDatesAndHandlerFromAnalyzer(Date startDate, Date endDate)
	{
		m_DefaultStartDate = startDate;
		m_DefaultEndDate   = endDate;
	}

	public void runAutotune()
	{
		m_Autotune = new RemoteLinuxServer(this.m_DefaultStartDate, this.m_DefaultEndDate, this);
		setAllMenusEnabled(false);

		m_ATThread = new ThreadAutotuneRun(m_Autotune);
		m_ATThread.runThreadCommand(new AutotuneCompleteHandler(this) 
		{
			//		@Override
			public void exceptionRaised(String message) 
			{
				setAllMenusEnabled(true);
			}

			//		@Override
			public void runAutotuneComplete(Object obj, String message) 
			{
				setAllMenusEnabled(true);
			}
		});	
	}
	
	private void displayAutotuneProfile()
	{
		m_WinAutotuneProfile = new WinAutotuneProfile("Nightscout Loader " + Version.getInstance().getM_Version() + " - Autotune - Profile Editor", 
				this) ;
		m_WinAutotuneProfile.retrieveAndLoadFile();
		m_WinAutotuneProfile.setVisible(true);
	}

	private void displayCGMDates()
	{
		WinCGMRanges cgmRange = new WinCGMRanges(this, "Nightscout Loader " + Version.getInstance().getM_Version() + " - Autotune - CGM Dates");
		cgmRange.intializeCGMRanges();
		cgmRange.setVisible(true);
	}

	private void listRemoteBackups()
	{
		m_Autotune = new RemoteLinuxServer(this.m_DefaultStartDate, this.m_DefaultEndDate, this);

		setAllMenusEnabled(false);

		m_ATThread = new ThreadAutotuneListBackups(m_Autotune);
		m_ATThread.runThreadCommand(new AutotuneCompleteHandler(this) 
		{
			//		@Override
			public void exceptionRaised(String message) 
			{
				setAllMenusEnabled(true);
			}

			//		@Override
			public void runAutotuneComplete(Object obj, String message) 
			{
				setAllMenusEnabled(true);
			}
		});	

	}

	private void copyRemoteBackups()
	{
		JFileChooser chooser = new JFileChooser(); 
		String localDirForRemBackups = PrefsNightScoutLoader.getInstance().getM_AutoTuneLocalFolderForBackups();
		// chooser.setCurrentDirectory(new java.io.File("."));
		File selectedFile = new File(localDirForRemBackups);
		chooser.setSelectedFile(selectedFile);
		chooser.setDialogTitle("Select Local Directory to download remote Backup Profiles to");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//
		// disable the "All files" option.
		//
		chooser.setAcceptAllFileFilterUsed(false);
		//    
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
		{ 
			String directory = new String(chooser.getSelectedFile().getAbsolutePath());
			PrefsNightScoutLoader.getInstance().setM_AutoTuneLocalFolderForBackups(directory);

			System.out.println("getCurrentDirectory(): " 
					+  chooser.getCurrentDirectory());
			System.out.println("getSelectedFile() : " 
					+  chooser.getSelectedFile());

			m_Autotune = new RemoteLinuxServer(this.m_DefaultStartDate, this.m_DefaultEndDate, this);

			setAllMenusEnabled(false);

			m_ATThread = new ThreadAutotuneDownloadBackups(m_Autotune, directory);
			m_ATThread.runThreadCommand(new AutotuneCompleteHandler(this) 
			{
				//		@Override
				public void exceptionRaised(String message) 
				{
					setAllMenusEnabled(true);
				}

				//		@Override
				public void runAutotuneComplete(Object obj, String message) 
				{
					setAllMenusEnabled(true);
				}
			});	
		}
	}

	private void setAllMenusEnabled(boolean enabled)
	{
		// Swing is not threadsafe, so add a request to update the grid onto the even queue
		// Found this technique here:
		// http://www.informit.com/articles/article.aspx?p=26326&seqNum=9
		m_Enabled = enabled;
		EventQueue.invokeLater(new 
				Runnable()
		{ 
			public void run()
			{ 
				setMenusEnabled(m_mnEdit);
				setMenusEnabled(m_mnAction);
			}
		});

	}

	/**
	 * @return the m_DefaultStartDate
	 */
	public synchronized Date getM_DefaultStartDate() {
		return m_DefaultStartDate;
	}

	/**
	 * @param m_DefaultStartDate the m_DefaultStartDate to set
	 */
	public synchronized void setM_DefaultStartDate(Date m_DefaultStartDate) {
		this.m_DefaultStartDate = m_DefaultStartDate;

		try 
		{
			addTextLine("Start Date set to: " + CommonUtils.convertDateString(m_DefaultStartDate, "dd-MM-YYYY") + "\n");
		} 
		catch (ParseException e) 
		{
			m_Logger.log(Level.SEVERE, "WinRemoteLinuxServer.setM_DefaultStartDate() Exception caught: " + e.getMessage());
		}
	}

	/**
	 * @return the m_DefaultEndDate
	 */
	public synchronized Date getM_DefaultEndDate() {
		return m_DefaultEndDate;
	}

	/**
	 * @param m_DefaultEndDate the m_DefaultEndDate to set
	 */
	public synchronized void setM_DefaultEndDate(Date m_DefaultEndDate) {
		this.m_DefaultEndDate = m_DefaultEndDate;
		try 
		{
			addTextLine("End Date set to: " + CommonUtils.convertDateString(m_DefaultEndDate, "dd-MM-YYYY") + "\n");
		} 
		catch (ParseException e) 
		{
			m_Logger.log(Level.SEVERE, "WinRemoteLinuxServer.setM_DefaultEndDate() Exception caught: " + e.getMessage());
		}
	}

	/**
	 * @return the m_Autotune
	 */
	public synchronized RemoteLinuxServer getM_Autotune() {
		return m_Autotune;
	}

	/**
	 * @return the m_ATThread
	 */
	public synchronized ThreadAutotune getM_ATThread() {
		return m_ATThread;
	}

	@Override
	public void setDates(Date startDate, Date endDate) 
	{
		setM_DefaultStartDate(startDate);
		setM_DefaultEndDate(endDate);
	}

}
