package win;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
//import java.awt.Graphics;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import control.MainNightScoutLoader;
import control.MyLogger;

import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.event.ActionEvent;

public class WinWhy extends JDialog 
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	// THIS IS A DELIBERATE CHANGE

	/**
	 * 
	 */
	private static final long serialVersionUID = -51832866421758739L;

	private final JPanel contentPanel = new JPanel();
	
//	private final ImagePanel imagePanel = new ImagePanel();

	private String m_WhyText;

	/**
	 * Create the dialog.
	 */
	public WinWhy(String title) 
	{
		URL url = MainNightScoutLoader.class.getResource("/Nightscout.jpg");
		ImageIcon img = new ImageIcon(url);
		setIconImage(img.getImage());

		super.setTitle(title);

		m_WhyText = new String();

		m_WhyText += "NightScoutLoader\r\n\r\nThe reason behind this application:\r\n\r\n";
		m_WhyText += "  Dawn was 19 months 21st March 2003 when blue-light rushed from Doctor's surgery to hospital.\r\n";
		m_WhyText += "  Since then (as every parent with a T1 child knows) life has never been the same.\r\n";
		m_WhyText += "  She was in hospital for 10 straight days, advanced stage ketoacidosis and in a really bad way.\r\n\r\n";
		m_WhyText += "  In hindsight, the virus we thought she had was more serious than we realised.\r\n";
		m_WhyText += "  \r\n";
		m_WhyText += "  She's pictured left on her 2nd Birthday August 2003.\r\n";
		m_WhyText += "  \r\n";
		m_WhyText += "  Since then Debbie & I did what we could to ensure her health was fine.\r\n";
		m_WhyText += "  In the early days, Dawn was on combination insulin injections - twice a day.\r\n";
		m_WhyText += "  We soon exhausted all available combinations and within a year were moving to 3 then 4 injections.\r\n";
		m_WhyText += "  Realising the importance of data, I immediately got on top of meter downloads and \r\n";
		m_WhyText += "  with my technical skills, I established the process of annotating results with what she ate\r\n";
		m_WhyText += "  \r\n";
		m_WhyText += "  In 2008, Dawn went onto a Medtronic pump for the first time.  \r\n";
		m_WhyText += "  Leading up to that date, we needed to firmly establish carb counting.  \r\n";
		m_WhyText += "  Looking around for a mobile device that could help & finding none, I wrote one on Windows Mobile \r\n";
		m_WhyText += "  that we used for several years until gaining expertise in carb counting. \r\n";
		m_WhyText += "  I developed a PC tool that would link historical food information to the Medtronic Care Link site too. \r\n";
		m_WhyText += "  \r\n";
		m_WhyText += "  In 2012, Dawn moved to Roche. \r\n";
		m_WhyText += "  Later that year, I reverse engineered the Roche SQL Server Database and developed some spreadsheets \r\n";
		m_WhyText += "  that could query directly pulling data down into my own tables & graphs.\r\n";
		m_WhyText += "  \r\n";
		m_WhyText += "  Once I discovered NightScout in 2015, I realised this early Roche work could be of use\r\n";
		m_WhyText += "  and so began the project Nightscout Loader in December 2015.\r\n";
		m_WhyText += "  \r\n";
		m_WhyText += "  Since then, Dawn moved to OmniPod and with only real access to data being through Diasend,\r\n";
		m_WhyText += "  I strengthened support for this file type.\r\n";
		m_WhyText += "  Most recently, I've added the audit and analytical capabililties.  I think they're useful.  Hope you do too.\r\n";

		setBounds(50, 50, 1250, 650);
		getContentPane().setLayout(new BorderLayout());
//		getContentPane().setLayout(new FlowLayout());
		
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		
		// This code scales the picture but it the app doesn't launch
		// when exported

		// Using http://www.mkyong.com/java/how-to-read-an-image-from-file-or-url/
		BufferedImage myPicture = null;
		try {
			URL url2 = MainNightScoutLoader.class.getResource("/Dawn.JPG");			
			myPicture = ImageIO.read(url2);
		} catch (IOException e) {
			e.printStackTrace();
			m_Logger.log(Level.SEVERE, "<"+this.getClass().getName()+">" + "deeperAnalyseResults: Just caught an exception " + e.getMessage());
		}
		
		Image dimg = myPicture.getScaledInstance(600, 400, Image.SCALE_SMOOTH);
		JLabel picLabel = new JLabel(new ImageIcon(dimg));
		
		add(picLabel);
		getContentPane().add(picLabel, BorderLayout.WEST);


		
//		getContentPane().add(imagePanel, BorderLayout.NORTH);

		getContentPane().add(contentPanel, BorderLayout.EAST);
		{
			JTextArea txtrNightscoutloader = new JTextArea();
			txtrNightscoutloader.setText(m_WhyText);
			contentPanel.add(txtrNightscoutloader);
			
			JScrollPane scrollPane = new JScrollPane(txtrNightscoutloader);
			getContentPane().add(scrollPane, BorderLayout.CENTER);
		}

		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
