package davidRichardson;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

// public class WinTextWin extends JDialog implements TextLineReceiverInterface
public class WinTextWin extends JFrame implements TextLineReceiverInterface
{

	/**
	 * 
	 */
	private static final long    serialVersionUID = -51832866421758739L;
	private static final Logger  m_Logger = Logger.getLogger(MyLogger.class.getName());

	private final JPanel         contentPanel = new JPanel();
	private JTextArea            txtrNightscoutloader = new JTextArea();

	private String               m_Text;
	private ArrayList<String>    m_AppendedLines;

	protected JMenuBar           m_MenuBar;
	protected JMenu              m_mnFile;
	protected JMenuItem          m_mntmFileSave; 
	protected JMenuItem          m_mntmFileClose; 
	protected boolean            m_Enabled;


	/**
	 * Create the dialog.
	 */
	public WinTextWin(String title) 
	{
		super.setTitle(title);

//		URL url = MainNightScoutLoader.class.getResource("/Nightscout.jpg");
//		ImageIcon img = new ImageIcon(url);
//		setIconImage(img.getImage());

		m_MenuBar = new JMenuBar();
		setJMenuBar(m_MenuBar);

		m_mnFile = new JMenu("File");
		m_mnFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				// Nothing needed here
			}
		});

		m_MenuBar.add(m_mnFile);

		m_mntmFileSave = new JMenuItem("Save Text");
		m_mntmFileSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				doSaveText();
			}
		});
		m_mnFile.add(m_mntmFileSave);
		m_mnFile.add(new JSeparator()); // SEPARATOR

		m_mntmFileClose = new JMenuItem("Close");
		m_mntmFileClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				setVisible(false);
			}
		});
		m_mnFile.add(m_mntmFileClose);

		
		m_Text = new String();
		m_AppendedLines = new ArrayList<String>();

		setBounds(100, 100, 750, 620);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		Font font = new Font("Courier", Font.BOLD, 12);

		getContentPane().add(txtrNightscoutloader, BorderLayout.WEST);
		txtrNightscoutloader.setFont(font);

		JScrollPane scrollPane = new JScrollPane(txtrNightscoutloader);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	}

	public void setText(String text)
	{
		m_Text = text;
		txtrNightscoutloader.setText(m_Text);
	}

	@Override
	public void addTextLine(String line) 
	{
		m_Text += line;
		m_AppendedLines.add(line);

		// Do this in the GUI thread
		EventQueue.invokeLater(new 
				Runnable()
		{ 
			public void run()
			{ 
				String line = null;
				if (m_AppendedLines.size() > 0)
				{
					line = m_AppendedLines.get(0);
					txtrNightscoutloader.append(line);
					m_AppendedLines.remove(0);
				}
			}
		});
	}
	
	protected void setMenusEnabled(JMenu m)
	{
		for (int i = 0; i < m.getItemCount(); i++)
		{
			JMenuItem mi = m.getItem(i);
			if (mi != null)
			{
				mi.setEnabled(m_Enabled);
			}
		}
	}


	private void doSaveText()
	{
		// Popup a dialog to select the file for saving contents to
		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Text Files", "txt");
		chooser.setFileFilter(filter);
//		File selectedFile = new File(PrefsNightScoutLoader.getInstance().getM_AnalysisFilePath());
//		chooser.setSelectedFile(selectedFile);
		int returnVal = chooser.showOpenDialog(getContentPane());        	    		
		if(returnVal == JFileChooser.APPROVE_OPTION) 
		{
			m_Logger.log(Level.INFO, "You chose to Save to this file: " +
					chooser.getSelectedFile().getAbsolutePath());
			
			try
			{
			    PrintWriter writer = new PrintWriter(chooser.getSelectedFile().getAbsolutePath(), "UTF-8");
			    writer.print(m_Text);

			    writer.close();
			} 
			catch (IOException e) 
			{
			   // do something
			}
		}

	}
}
