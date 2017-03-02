package davidRichardson;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import davidRichardson.ThreadAutotune.AutotuneCompleteHandler;


public class WinAutotuneProfile extends JFrame 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	private static final String       m_RemoteProfileName = "myopenaps/settings/profile.json";
	private static final String       m_LocalProfileName  = "C:\\Temp\\profile.json";

	private WinTextWin                m_Parent;
	private JPanel                    m_ContentPane;

	private JSpinner                  m_CarbRatio;
	private JSpinner                  m_AutosensMax;
	private JSpinner                  m_AutosensMin;
	private JSpinner                  m_Min5mCarbImpact;
	private JSpinner                  m_Dia;

	private JPanel                    m_Panel;
	private AutotuneProfileControls[] m_ISFandBasalRateControls;

	protected JMenuBar                m_MenuBar;
	protected JMenu                   m_mnFile;
	protected JMenuItem               m_mntmFileLoadLocal; 
	protected JMenuItem               m_mntmFileLoadRemote; 
	protected JMenuItem               m_mntmFileLoadDiasend; 
	protected JMenuItem               m_mntmFileSaveRemote; 
	protected JMenuItem               m_mntmFileSaveAsRemote; 
	protected JMenuItem               m_mntmFileSaveAsLocal; 
	protected JMenuItem               m_mntmFileClose; 

	private  RemoteLinuxServer        m_Autotune;
	private  ThreadAutotune           m_ATThread;

	/**
	 * Create the frame.
	 */
	public WinAutotuneProfile(String title, WinTextWin parent) 
	{
		super.setTitle(title);
		m_Parent = parent;

		m_Autotune = new RemoteLinuxServer(parent);
		m_ATThread = null;

		URL url = MainNightScoutLoader.class.getResource("/Nightscout.jpg");
		ImageIcon img = new ImageIcon(url);
		setIconImage(img.getImage());

		setBounds(100, 00, 620, 730);
		m_ContentPane = new JPanel();
		m_ContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		m_ContentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(m_ContentPane);

		m_Panel = new JPanel();
		m_Panel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		m_ContentPane.add(m_Panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {82, 80, 80, 80};
		gbl_panel.rowHeights = new int[] {27, 27, 27, 27, 27, 0};
		gbl_panel.columnWeights = new double[]{4.9E-324, 1.0, 0.0, 0.0};
		gbl_panel.rowWeights = new double[]{4.9E-324, Double.MIN_VALUE, 0.0, 0.0, 0.0, 0.0};
		m_Panel.setLayout(gbl_panel);

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

		m_mntmFileLoadRemote = new JMenuItem("Re-load remote file");
		m_mntmFileLoadRemote.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				doLoadRemoteFile();
			}
		});
		m_mnFile.add(m_mntmFileLoadRemote);
		m_mnFile.add(new JSeparator()); // SEPARATOR

		m_mntmFileLoadLocal = new JMenuItem("Load local file");
		m_mntmFileLoadLocal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				doLoadLocalFile();
			}
		});
		m_mnFile.add(m_mntmFileLoadLocal);

		m_mntmFileLoadDiasend = new JMenuItem("Load from Diasend");
		m_mntmFileLoadDiasend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				doLoadDiasend();
			}
		});
		m_mnFile.add(m_mntmFileLoadDiasend);

		m_mnFile.add(new JSeparator()); // SEPARATOR

		m_mntmFileSaveAsLocal = new JMenuItem("Save As (Local)");
		m_mntmFileSaveAsLocal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				try {
					doSaveAsLocal();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		m_mnFile.add(m_mntmFileSaveAsLocal);
		m_mnFile.add(new JSeparator()); // SEPARATOR

		m_MenuBar.add(m_mnFile);
		m_mntmFileSaveAsRemote = new JMenuItem("Save As (Remote)");
		m_mntmFileSaveAsRemote.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					doSaveAsRemote();
				} 
				catch (IOException e1) 
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		m_mnFile.add(m_mntmFileSaveAsRemote);

		m_ISFandBasalRateControls = new AutotuneProfileControls[24];

		int vert = 0;

		m_AutosensMax     = createSpinnerControl("Auto Sens Max", 0, vert, 1, 0.0);
		m_AutosensMin     = createSpinnerControl("Auto Sens Min", 2, vert++, 1, 0.0);
		m_Min5mCarbImpact = createSpinnerControl("min_5m_carbimpact", 0, vert, 1, 0);
		m_Dia             = createSpinnerControl("dia", 2, vert++, 1, 0);
		m_CarbRatio       = createSpinnerControl("Carb Ratio", 0, vert, 1, 0.0);
		createWarningOnISF("ISF is in mg/dL", 3, vert++, 1, 0.0);

		for (int h = 0; h < 24; h++)
		{
			m_ISFandBasalRateControls[h] = createBasalAndISFControls(h, 0, h + vert, 1);
		}
	}

	public void retrieveAndLoadFile()
	{
		retrieveLinuxProfileFile();
		loadFile(m_LocalProfileName);
	}

	private void retrieveLinuxProfileFile()
	{
		// Use the RemoteLinuxServer class to load the file
		m_Autotune.downloadProfileFile(m_RemoteProfileName, m_LocalProfileName);
	}

	private void loadFile(String path)
	{
		JSONParser parser = new JSONParser();

		Object obj = null;
		try {
			obj = parser.parse(new FileReader(path));
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Now parse the JSON to set values in the controls
		setValuesFromJSON((JSONObject)obj);
	}

	private void setValuesFromJSON(JSONObject jsonObject)
	{	
		// Set the separate control values
		String val = null;
		val = jsonObject.get("carb_ratio").toString();
		m_CarbRatio.setValue(Double.parseDouble(val));
		val = jsonObject.get("autosens_max").toString();
		m_AutosensMax.setValue(Double.parseDouble(val));
		val = jsonObject.get("autosens_min").toString();
		m_AutosensMin.setValue(Double.parseDouble(val));
		val = jsonObject.get("autosens_min").toString();
		m_AutosensMin.setValue(Double.parseDouble(val));
		val = jsonObject.get("min_5m_carbimpact").toString();
		m_Min5mCarbImpact.setValue(Long.parseLong(val));
		val = jsonObject.get("dia").toString();
		m_Dia.setValue(Long.parseLong(val));

		setBasalValuesFromJSON(jsonObject);
		setISFValuesFromJSON(jsonObject);
	}

	private void setBasalValuesFromJSON(JSONObject jsonObject)
	{
		// Use an array to install basal rates from JSON file.
		double[] basalRates = 
				new double[] 
						{
								-1,-1,-1,-1,-1,-1,
								-1,-1,-1,-1,-1,-1,
								-1,-1,-1,-1,-1,-1,
								-1,-1,-1,-1,-1,-1,
						};

		// Now go through the array and store value into an array in case they are
		// out of sequence
		JSONArray basalProfile = (JSONArray) jsonObject.get("basalprofile");
		for (Object elem : basalProfile) 
		{
			String start   = (String)((JSONObject)elem).get("start");
			//   		long   minutes = (long)((JSONObject)elem).get("minutes");
			double rate    = (double)((JSONObject)elem).get("rate");

			int hour = Integer.parseInt(start.substring(0, 2));
			if (hour < 24)
			{
				basalRates[hour] = rate;
			}
		}

		// Having built an array, now set the values accordingly
		setBasalRateValues(basalRates);
	}

	private void setBasalRateValues(double[] basalRates)
	{
		// Having built an array, now set the values accordingly
		double prev = 0.0;
		for (int h = 0; h < 24; h++)
		{
			double rate = basalRates[h];
			if (rate != -1.0)
			{
				prev = rate;
				m_ISFandBasalRateControls[h].getM_BasalRate().setValue((double)rate);
				m_ISFandBasalRateControls[h].getM_CopyBR().setSelected(false);
			}
			else
			{
				m_ISFandBasalRateControls[h].getM_BasalRate().setValue(prev);
				m_ISFandBasalRateControls[h].getM_CopyBR().setSelected(true);	
				ActionListener[] al = m_ISFandBasalRateControls[h].getM_CopyBR().getActionListeners();
				if (al != null && al.length > 0)
				{
					al[0].actionPerformed(null);
				}
			}
		}	
	}

	private void setISFValues(double[] isfRates)
	{
		// Having built an array, now set the values accordingly
		double prev = 0.0;
		for (int h = 0; h < 24; h++)
		{
			double rate = isfRates[h];
			if (rate != -1.0)
			{
				prev = rate;
				m_ISFandBasalRateControls[h].getM_InsSensFactor().setValue((double)rate);
				m_ISFandBasalRateControls[h].getM_CopyISF().setSelected(false);
			}
			else
			{
				m_ISFandBasalRateControls[h].getM_InsSensFactor().setValue(prev);
				m_ISFandBasalRateControls[h].getM_CopyISF().setSelected(true);	
				ActionListener[] al = m_ISFandBasalRateControls[h].getM_CopyISF().getActionListeners();
				if (al != null && al.length > 0)
				{
					al[0].actionPerformed(null);
				}
			}
		}	
	}


	private void setCarbRatioRateValues(double[] isfRates)
	{

	}


	private void setISFValuesFromJSON(JSONObject jsonObject)
	{
		// Use an array to install isf rates from JSON file.
		long[] isfRates = 
				new long[] 
						{
								-1,-1,-1,-1,-1,-1,
								-1,-1,-1,-1,-1,-1,
								-1,-1,-1,-1,-1,-1,
								-1,-1,-1,-1,-1,-1,
						};

		// Now go through the array and store value into an array in case they are
		// out of sequence
		JSONObject isfProfile    = (JSONObject)jsonObject.get("isfProfile");
		JSONArray sensitivities  = (JSONArray) isfProfile.get("sensitivities");
		for (Object elem : sensitivities) 
		{
			//			int   i            = (int)((JSONObject)elem).get("i");
			String start       = (String)((JSONObject)elem).get("start");
			long   sensitivity = (long)((JSONObject)elem).get("sensitivity");
			//			int   offset      = (int)((JSONObject)elem).get("offset");
			//			int   x           = (int)((JSONObject)elem).get("x");
			//			int   endOffset   = (int)((JSONObject)elem).get("endOffset");

			int hour = Integer.parseInt(start.substring(0, 2));
			if (hour < 24)
			{
				isfRates[hour] = sensitivity;
			}
		}

		// Having built an array, now set the values accordingly
		long prev = 0;
		for (int h = 0; h < 24; h++)
		{
			long sensitivity = isfRates[h];
			if (sensitivity != -1)
			{
				prev = sensitivity;
				m_ISFandBasalRateControls[h].getM_InsSensFactor().setValue(sensitivity);
				m_ISFandBasalRateControls[h].getM_CopyISF().setSelected(false);
			}
			else
			{
				m_ISFandBasalRateControls[h].getM_InsSensFactor().setValue(prev);
				m_ISFandBasalRateControls[h].getM_CopyISF().setSelected(true);	
				ActionListener[] al = m_ISFandBasalRateControls[h].getM_CopyISF().getActionListeners();
				if (al != null && al.length > 0)
				{
					al[0].actionPerformed(null);
				}
			}
		}
	}


	private JSpinner createSpinnerControl(String label, int x, int y, int w, double initialVal)
	{
		JSpinner result   =  createDecimalSpinner(initialVal);		

		// Create a label at position X , Y
		JLabel lbl = new JLabel(label);
		lbl.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_lbl = new GridBagConstraints();
		gbc_lbl.insets = new Insets(0, 0, 5, 0);
		gbc_lbl.gridwidth = w;
		gbc_lbl.gridx     = x;
		gbc_lbl.gridy     = y;
		m_Panel.add(lbl, gbc_lbl);

		// Create a Basal Rate spinner at position X + 1, Y
		GridBagConstraints gbc_br_spn = new GridBagConstraints();
		gbc_br_spn.insets = new Insets(0, 0, 5, 0);
		gbc_br_spn.gridwidth = w;
		gbc_br_spn.gridx     = x + 1;
		gbc_br_spn.gridy     = y;
		m_Panel.add(result, gbc_br_spn);

		return result;
	}

	private void createWarningOnISF(String label, int x, int y, int w, double initialVal)
	{
		// Create a label at position X , Y
		JLabel lbl = new JLabel(label);
		lbl.setFont(new Font("Tahoma", Font.BOLD, 16));
		GridBagConstraints gbc_lbl = new GridBagConstraints();
		gbc_lbl.insets = new Insets(0, 0, 5, 0);
		gbc_lbl.gridwidth = w;
		gbc_lbl.gridx     = x;
		gbc_lbl.gridy     = y;
		m_Panel.add(lbl, gbc_lbl);
	}


	private JSpinner createSpinnerControl(String label, int x, int y, int w, long initialVal)
	{
		JSpinner result   =  createLongSpinner(initialVal);		

		// Create a label at position X , Y
		JLabel lbl = new JLabel(label);
		lbl.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_lbl = new GridBagConstraints();
		gbc_lbl.insets = new Insets(0, 0, 5, 0);
		gbc_lbl.gridwidth = w;
		gbc_lbl.gridx     = x;
		gbc_lbl.gridy     = y;
		m_Panel.add(lbl, gbc_lbl);

		// Create a Basal Rate spinner at position X + 1, Y
		GridBagConstraints gbc_br_spn = new GridBagConstraints();
		gbc_br_spn.insets = new Insets(0, 0, 5, 0);
		gbc_br_spn.gridwidth = w;
		gbc_br_spn.gridx     = x + 1;
		gbc_br_spn.gridy     = y;
		m_Panel.add(result, gbc_br_spn);

		return result;
	}


	private AutotuneProfileControls createBasalAndISFControls(int h, int x, int y, int w)
	{
		JSpinner basalSpn =  createDecimalSpinner(0.0);		
		JSpinner isfSpn   =  createLongSpinner(0);		
		//	JSpinner isfSpn   =  createDecimalSpinner(0.0);		

		// Create a label at position X , Y
		JLabel brLbl = new JLabel("Basal From " + (h < 10 ? "0" + h : h) + ":00" );
		brLbl.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_brLbl = new GridBagConstraints();
		gbc_brLbl.insets = new Insets(0, 0, 0, 0);
		gbc_brLbl.gridwidth = w;
		gbc_brLbl.gridx     = x;
		gbc_brLbl.gridy     = y;
		m_Panel.add(brLbl, gbc_brLbl);

		// Create a Basal Rate spinner at position X + 1, Y
		GridBagConstraints gbc_br_spn = new GridBagConstraints();
		gbc_br_spn.insets = new Insets(0, 0, 0, 0);
		gbc_br_spn.gridwidth = w;
		gbc_br_spn.gridx     = x + 1;
		gbc_br_spn.gridy     = y;
		m_Panel.add(basalSpn, gbc_br_spn);

		// Add a radio button that will copy previous value X + 2, Y
		JRadioButton basalRbn = new JRadioButton("BR Continues");
		GridBagConstraints gbc_br_but = new GridBagConstraints();
		gbc_br_but.anchor = GridBagConstraints.WEST;
		gbc_br_but.insets = new Insets(0, 0, 0, 0);
		gbc_br_but.gridx = x + 2;
		gbc_br_but.gridy = y;
		m_Panel.add(basalRbn, gbc_br_but);

		// Create a label at position X , Y
		JLabel isfLbl = new JLabel("ISF From " + (h < 10 ? "0" + h : h) + ":00" );
		isfLbl.setFont(new Font("Tahoma", Font.PLAIN, 12));
		GridBagConstraints gbc_isfLbl = new GridBagConstraints();
		gbc_isfLbl.insets = new Insets(0, 0, 0, 0);
		gbc_isfLbl.gridwidth = w;
		gbc_isfLbl.gridx     = x + 3;
		gbc_isfLbl.gridy     = y;
		m_Panel.add(isfLbl, gbc_isfLbl);

		// Create an ISF spinner at position X + 3, Y
		GridBagConstraints gbc_isf_spn = new GridBagConstraints();
		gbc_isf_spn.insets = new Insets(0, 0, 0, 0);
		gbc_isf_spn.gridwidth = w;
		gbc_isf_spn.gridx     = x + 4;
		gbc_isf_spn.gridy     = y;
		m_Panel.add(isfSpn, gbc_isf_spn);

		// Add a radio button that will copy previous value X + 4, Y
		JRadioButton isfRbn = new JRadioButton("ISF Continues");
		GridBagConstraints gbc_isf_but = new GridBagConstraints();
		gbc_isf_but.anchor = GridBagConstraints.WEST;
		gbc_isf_but.insets = new Insets(0, 0, 0, 0);
		gbc_isf_but.gridx = x + 5;
		gbc_isf_but.gridy = y;
		m_Panel.add(isfRbn, gbc_isf_but);

		AutotuneProfileControls result = new AutotuneProfileControls(brLbl, isfLbl, basalSpn, basalRbn, isfSpn, isfRbn);
		basalRbn.addActionListener(new AutotuneControlActionListener(result));
		isfRbn.addActionListener(new AutotuneControlActionListener(result));

		// The continue buttons for midnight make no sense, so remove them
		if (h == 0)
		{
			basalRbn.setVisible(false);
			isfRbn.setVisible(false);
		}
		// Finally, the ISF is disabled for all but Midnight
		else if (h > 0)
		{
			isfLbl.setEnabled(false);
			isfSpn.setEnabled(false);
			isfRbn.setEnabled(false);
		}

		return result;
	}

	private JSpinner createDecimalSpinner(double initialVal)
	{
		JSpinner result = null;

		SpinnerNumberModel model = new SpinnerNumberModel(1.0, 0.0, 10.0, 0.01);

		result = new JSpinner();
		result.setModel(model);
		JSpinner.NumberEditor editor = (JSpinner.NumberEditor)result.getEditor();
		DecimalFormat format = editor.getFormat();
		format.setMinimumFractionDigits(3);
		editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);
		Dimension d = result.getPreferredSize();
		d.width = 60;
		result.setPreferredSize(d);
		result.setValue(initialVal);

		return result;
	}


	private JSpinner createLongSpinner(long initialVal)
	{
		JSpinner result = null;

		SpinnerNumberModel model = new SpinnerNumberModel(1, 0, 100, 1);

		result = new JSpinner();
		result.setModel(model);
		JSpinner.NumberEditor editor = (JSpinner.NumberEditor)result.getEditor();
		//		DecimalFormat format = editor.getFormat();
		//		format.setMinimumFractionDigits(3);
		editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);
		Dimension d = result.getPreferredSize();
		d.width = 60;
		result.setPreferredSize(d);
		result.setValue(initialVal);

		return result;
	}


	private void doLoadLocalFile()
	{	
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select local Profile file to load Autotune Profile data from");
		String localFile = new String("");

		FileNameExtensionFilter filter = null;

		filter = new FileNameExtensionFilter("JSON Files", "json");

		chooser.setFileFilter(filter);

		// Was there a previously selected file?
		String prevFile = new String(PrefsNightScoutLoader.getInstance().getM_AutoTuneLocalProfileFileLoaded());
		if (!prevFile.isEmpty())
		{
			File selectedFile = new File(prevFile);
			chooser.setSelectedFile(selectedFile);
		}
		int returnVal = chooser.showOpenDialog(m_ContentPane);        	    		
		if(returnVal == JFileChooser.APPROVE_OPTION) 
		{
			localFile = chooser.getSelectedFile().getAbsolutePath();

			// Also set it in preferences too
			PrefsNightScoutLoader.getInstance().setM_AutoTuneLocalProfileFileLoaded(localFile);

			m_Parent.addTextLine("You chose to open this file: " +
					localFile);

			// Now load the file selected
			loadFile(localFile);
		}
	}

	private void doLoadRemoteFile()
	{		
		this.retrieveAndLoadFile();
	}

	private void doLoadDiasend()
	{		
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select Diasend file to load Autotune Profile data from");
		String diasendPath = new String("");

		FileNameExtensionFilter filter = null;

		filter = new FileNameExtensionFilter("XLS Files", "xls");

		chooser.setFileFilter(filter);
		File selectedFile = new File(PrefsNightScoutLoader.getInstance().getM_DiasendMeterPumpResultFilePath());
		chooser.setSelectedFile(selectedFile);
		int returnVal = chooser.showOpenDialog(m_ContentPane);        	    		
		if(returnVal == JFileChooser.APPROVE_OPTION) 
		{
			diasendPath = chooser.getSelectedFile().getAbsolutePath();

			// Also set it in preferences too
			PrefsNightScoutLoader.getInstance().setM_DiasendMeterPumpResultFilePath(diasendPath);

			m_Logger.log(Level.INFO, "You chose to open this file: " +
					diasendPath);

			// Now load the diasend Basal Rates only
			DataLoadDiasend diasendPumpSettingLoader = new DataLoadDiasend();
			try 
			{
				diasendPumpSettingLoader.loadPumpSettings(diasendPath);
				ArrayList<DBResultDiasendBasalSetting> basalValues = diasendPumpSettingLoader.getM_BasalSettings();
				initializeFromBasals(basalValues);
				ArrayList<DBResultDiasendISFSetting>   isfValues   = diasendPumpSettingLoader.getM_ISFSettings();
				initializeFromISF(isfValues);
				ArrayList<DBResultDiasendCarbRatioSetting> carbRatioValues   = diasendPumpSettingLoader.getM_CarbRatioSettings();
				initializeFromCarbRatio(carbRatioValues);
			} 
			catch (ClassNotFoundException | SQLException | IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void doSaveAsLocal() throws IOException
	{
		JSONObject obj = buildJSONObject();

		try (FileWriter file = new FileWriter("c:\\Temp\\David_JSON_Output.txt")) 
		{					
			file.write(obj.toJSONString());
			System.out.println("Successfully Copied JSON Object to File...");
			System.out.println("\nJSON Object: " + obj);
		}
	}

	private void doSaveAsRemote() throws IOException
	{
		String  localFile      = "c:\\Temp\\profile.json.txt";

		m_Parent.addTextLine("**************************************************\n");
		m_Parent.addTextLine("Remote Save As Thread Started\n");
		m_Parent.addTextLine("**************************************************\n");

		// Build JSON from form
		JSONObject obj = buildJSONObject();

		setAllMenusEnabled(false);


		m_ATThread = new ThreadAutotuneRemoteSaveAs(m_Autotune, obj.toJSONString(), localFile);
		m_ATThread.runThreadCommand(new AutotuneCompleteHandler(this) 
		{
			//		@Override
			public void exceptionRaised(String message) 
			{
				//			m_mntmActionRunAutotune.setEnabled(true);
				//			m_mntmActionListBackups.setEnabled(true);			
				m_Parent.addTextLine("**************************************************\n");
				m_Parent.addTextLine("Remote Save As Thread Finished with exception\n");
				m_Parent.addTextLine("**************************************************\n");
				setAllMenusEnabled(true);
			}

			//		@Override
			public void runAutotuneComplete(Object obj, String message) 
			{
				// m_Logger.log(Level.INFO, "RemoteLinuxServer Finished");
				//				m_mntmActionRunAutotune.setEnabled(true);
				//				m_mntmActionListBackups.setEnabled(true);
				m_Parent.addTextLine("**************************************************\n");
				m_Parent.addTextLine("Thread Finished to handle Remote Save As\n");
				m_Parent.addTextLine("**************************************************\n");
				setAllMenusEnabled(true);
			}
		});	

	}

	private void setAllMenusEnabled(boolean enabled)
	{
		setMenusEnabled(m_mnFile, enabled);
	}

	private void setMenusEnabled(JMenu m, boolean enabled)
	{
		for (int i = 0; i < m.getItemCount(); i++)
		{
			JMenuItem mi = m.getItem(i);
			if (mi != null)
			{
				mi.setEnabled(enabled);
			}
		}
	}

	//	private void doSaveAsRemote_orig() throws IOException
	//	{
	//		boolean remoteBackedUp = false;
	//		boolean savedLocally   = false;
	//		boolean copiedRemotely = false;
	//		String  localFile      = "c:\\Temp\\profile.json.txt";
	//
	//		// 1 Create RemoteLinuxServer class for remote access
	//		RemoteLinuxServer at = new RemoteLinuxServer();
	//
	//		// 2 Build JSON
	//		JSONObject obj = buildJSONObject();
	//
	//		// 3 Backup remote file
	//		remoteBackedUp = at.backupProfileFile();
	//		if (remoteBackedUp == true)
	//		{
	//			m_Parent.addTextLine("Current Remote Autotune JSON Profile backed-up successfully.\n");
	//		}
	//
	//		// 4 Save JSON contents to a temporary file
	//		if (remoteBackedUp == true)
	//		{
	//			try (FileWriter file = new FileWriter(localFile)) 
	//			{
	//				file.write(obj.toJSONString());
	//				m_Parent.addTextLine("New Autotune JSON Parameter values from form successfully saved to local file.\n");
	//			}
	//			catch (IOException e1) 
	//			{
	//				m_Logger.log(Level.SEVERE, "Exceptioon saving Autotune JSON Parameters to file " + e1);
	//			}
	//			finally
	//			{
	//				savedLocally = true;
	//			}
	//		}
	//
	//		// 5 Then copy the file over to remote server
	//		if (remoteBackedUp == true && savedLocally == true)
	//		{
	//			at.installProfileFile(localFile);
	//			m_Parent.addTextLine("New Autotune JSON Parameter in local file copied to Autotune server.\n");
	//		}
	//
	//		// And ensure it's installed in all three locations.
	//	}

	private void initializeFromBasals(ArrayList<DBResultDiasendBasalSetting> basals)
	{
		// Use an array to install basal rates from JSON file.
		double[] basalRates = 
				new double[] 
						{
								-1,-1,-1,-1,-1,-1,
								-1,-1,-1,-1,-1,-1,
								-1,-1,-1,-1,-1,-1,
								-1,-1,-1,-1,-1,-1,
						};

		// Now go through the array and store value into an array in case they are
		// out of sequence
		for (DBResultDiasendBasalSetting b : basals)
		{
			String basalTime = new String(b.getM_Time());
			double basalRate = b.getM_BasalValue();
			int hour = Integer.parseInt(basalTime.substring(0, 2));
			if (hour < 24)
			{
				basalRates[hour] = basalRate;
			}

		}

		// Having built an array, now set the values accordingly
		setBasalRateValues(basalRates);
	}

	private void initializeFromISF(ArrayList<DBResultDiasendISFSetting> isfValues)
	{
		// Use an array to install isf rates from JSON file.
		double[] isfRates = 
				new double[] 
						{
								-1,-1,-1,-1,-1,-1,
								-1,-1,-1,-1,-1,-1,
								-1,-1,-1,-1,-1,-1,
								-1,-1,-1,-1,-1,-1,
						};

		// Now go through the array and store value into an array in case they are
		// out of sequence
		for (DBResultDiasendISFSetting b : isfValues)
		{
			String isfTime = new String(b.getM_Time());
			double isfRate = b.getM_ISFValue();
			int hour = Integer.parseInt(isfTime.substring(0, 2));
			if (hour < 24)
			{
				isfRates[hour] = isfRate;
			}

		}

		// Having built an array, now set the values accordingly
		setISFValues(isfRates);

	}

	private void initializeFromCarbRatio(ArrayList<DBResultDiasendCarbRatioSetting> carbRatioValues)
	{
		// Use an array to install carbRatio rates from JSON file.
		double[] carbRatioRates = 
				new double[] 
						{
								-1,-1,-1,-1,-1,-1,
								-1,-1,-1,-1,-1,-1,
								-1,-1,-1,-1,-1,-1,
								-1,-1,-1,-1,-1,-1,
						};

		// Now go through the array and store value into an array in case they are
		// out of sequence
		for (DBResultDiasendCarbRatioSetting b : carbRatioValues)
		{
			String carbRatioTime = new String(b.getM_Time());
			double carbRatioRate = b.getM_CarbRatioValue();
			int hour = Integer.parseInt(carbRatioTime.substring(0, 2));
			if (hour < 24)
			{
				carbRatioRates[hour] = carbRatioRate;
			}

		}

		// Having built an array, now set the values accordingly
		setCarbRatioRateValues(carbRatioRates);

	}


	private JSONObject buildJSONObject()
	{
		JSONObject result = null;

		result = new JSONObject();
		result.put("min_5m_carbimpact", this.m_Min5mCarbImpact.getValue());
		result.put("dia", this.m_Dia.getValue());

		JSONArray basalprofile = new JSONArray();
		for (int i = 0; i < 24; i++)
		{
			addBasalProfile(i, basalprofile);
		}
		result.put("basalprofile", basalprofile);

		JSONObject isfProfile = new JSONObject();
		result.put("isfProfile", isfProfile);

		JSONArray sensitivities = new JSONArray();
		for (int i = 0; i < 24; i++)
		{
			addISFProfile(i, sensitivities);
		}
		isfProfile.put("sensitivities", sensitivities);

		result.put("carb_ratio",   (double)this.m_CarbRatio.getValue());
		result.put("autosens_max", (double)this.m_AutosensMax.getValue());
		result.put("autosens_min", (double)this.m_AutosensMin.getValue());

		return result;
	}

	private void addBasalProfile(int h, JSONArray arr)
	{
		AutotuneProfileControls profControl = h < m_ISFandBasalRateControls.length ? m_ISFandBasalRateControls[h] : null;
		if (profControl != null && profControl.getM_CopyBR().isSelected() == false)
		{
			String start   = "";
			long   minutes = h * 60;
			double rate    = (double)profControl.getM_BasalRate().getValue();
			start = (h < 10) ? "0" + h + ":00:00" : h + ":00:00";
			JSONObject obj = new JSONObject();
			obj.put("start",   start);
			obj.put("minutes", minutes);
			obj.put("rate",    rate);
			arr.add(obj);
		}
	}

	private void addISFProfile(int h, JSONArray arr)
	{
		AutotuneProfileControls profControl = h < m_ISFandBasalRateControls.length ? m_ISFandBasalRateControls[h] : null;

		if (profControl != null && profControl.getM_CopyISF().isSelected() == false)
		{
			String sensStr       = profControl.getM_InsSensFactor().getValue().toString();

			long   i             = 0;
			String start         = "";
			//			long   minutes       = h * 60;
			long   minutes       = 1440;  // For now, only one senstivitiy can be used.
			//			Long  sensitivity   = (Long) profControl.getM_InsSensFactor().getValue();
			long   sensitivity   = Long.parseLong(sensStr);
			long   offset        = 0;
			long   x             = 0;
			start = (h < 10) ? "0" + h + ":00:00" : h + ":00:00";
			JSONObject obj = new JSONObject();
			obj.put("i",           i);
			obj.put("x",           x);
			obj.put("start",       start);
			obj.put("sensitivity", sensitivity);
			obj.put("offset",      offset);
			obj.put("endOffset",   minutes);
			arr.add(obj);
		}

	}

	class AutotuneProfileControls
	{
		private JLabel       m_brLabel;
		private JLabel       m_isfLabel;
		private JSpinner     m_BasalRate;
		private JRadioButton m_CopyBR;
		private JSpinner     m_InsSensFactor;
		private JRadioButton m_CopyISF;

		AutotuneProfileControls(JLabel brLbl, JLabel isfLbl, JSpinner brSpn, JRadioButton brRbn, JSpinner isfSpn, JRadioButton isfRbn)
		{
			m_brLabel       = brLbl;
			m_isfLabel      = isfLbl;
			m_BasalRate     = brSpn;
			m_CopyBR        = brRbn;
			m_InsSensFactor = isfSpn;
			m_CopyISF       = isfRbn;
		}


		/**
		 * @return the m_brLabel
		 */
		public synchronized JLabel getM_brLabel() {
			return m_brLabel;
		}


		/**
		 * @param m_brLabel the m_brLabel to set
		 */
		public synchronized void setM_brLabel(JLabel m_brLabel) {
			this.m_brLabel = m_brLabel;
		}


		/**
		 * @return the m_isfLabel
		 */
		public synchronized JLabel getM_isfLabel() {
			return m_isfLabel;
		}


		/**
		 * @param m_isfLabel the m_isfLabel to set
		 */
		public synchronized void setM_isfLabel(JLabel m_isfLabel) {
			this.m_isfLabel = m_isfLabel;
		}


		/**
		 * @return the m_BasalRate
		 */
		public synchronized JSpinner getM_BasalRate() {
			return m_BasalRate;
		}

		/**
		 * @param m_BasalRate the m_BasalRate to set
		 */
		public synchronized void setM_BasalRate(JSpinner m_BasalRate) {
			this.m_BasalRate = m_BasalRate;
		}

		/**
		 * @return the m_CopyBR
		 */
		public synchronized JRadioButton getM_CopyBR() {
			return m_CopyBR;
		}

		/**
		 * @param m_CopyBR the m_CopyBR to set
		 */
		public synchronized void setM_CopyBR(JRadioButton m_CopyBR) {
			this.m_CopyBR = m_CopyBR;
		}

		/**
		 * @return the m_InsSensFactor
		 */
		public synchronized JSpinner getM_InsSensFactor() {
			return m_InsSensFactor;
		}

		/**
		 * @param m_InsSensFactor the m_InsSensFactor to set
		 */
		public synchronized void setM_InsSensFactor(JSpinner m_InsSensFactor) {
			this.m_InsSensFactor = m_InsSensFactor;
		}

		/**
		 * @return the m_CopyISF
		 */
		public synchronized JRadioButton getM_CopyISF() {
			return m_CopyISF;
		}

		/**
		 * @param m_CopyISF the m_CopyISF to set
		 */
		public synchronized void setM_CopyISF(JRadioButton m_CopyISF) {
			this.m_CopyISF = m_CopyISF;
		}

	}

	private class AutotuneControlActionListener implements ActionListener 
	{
		private AutotuneProfileControls m_Control;

		public AutotuneControlActionListener(AutotuneProfileControls control) 
		{
			this.m_Control      = control;
		}

		public void actionPerformed(ActionEvent e) 
		{
			// Set control based on whether pushed in or raised up
			boolean basalSelected = m_Control.getM_CopyBR().isSelected();
			boolean isfSelected   = m_Control.getM_CopyISF().isSelected();

			m_Control.getM_brLabel().setEnabled(basalSelected ? false : true);
			m_Control.getM_isfLabel().setEnabled(isfSelected ? false : true);
			m_Control.getM_BasalRate().setEnabled(basalSelected ? false : true);
			m_Control.getM_InsSensFactor().setEnabled(isfSelected ? false : true);
		}
	}


}
