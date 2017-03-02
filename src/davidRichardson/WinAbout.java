package davidRichardson;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.net.URL;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;

public class WinAbout extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -51832866421758739L;

	private final JPanel contentPanel = new JPanel();

	private String m_AboutText;

	/**
	 * Create the dialog.
	 */
	public WinAbout(String title) 
	{
		super.setTitle(title);

		URL url = MainNightScoutLoader.class.getResource("/Nightscout.jpg");
		ImageIcon img = new ImageIcon(url);
		setIconImage(img.getImage());

		m_AboutText = new String();

		m_AboutText = Version.getInstance().getM_AboutText();

		setBounds(100, 100, 750, 600);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

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

		Font font = new Font("Courier", Font.BOLD, 12);


		JTextArea txtrNightscoutloader = new JTextArea();
		getContentPane().add(txtrNightscoutloader, BorderLayout.WEST);
		txtrNightscoutloader.setFont(font);

		txtrNightscoutloader.setText(m_AboutText);
		
		JScrollPane scrollPane = new JScrollPane(txtrNightscoutloader);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	}

}
