package davidRichardson;

import com.jcraft.jsch.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class RemoteLinuxServer 
{
	private static final Logger m_Logger = Logger.getLogger(MyLogger.class.getName());

	private JSch   m_JSch;
	private String m_OutputStream;
	private Date   m_StartDate;
	private Date   m_EndDate;
	private TextLineReceiverInterface m_TextLineReceiverInterface = null;
	private String m_BackupDirectory = "NightscoutLoader_Backups";


	public class RemoteLinuxServerHostDetails
	{
		private Boolean m_KeyFileAuth;
		private String  m_KeyFile;
		private String  m_Host;
		private String  m_User;
		private String  m_PassWord;
		private int     m_port;

		RemoteLinuxServerHostDetails()
		{
			String suppliedServer = new String(PrefsNightScoutLoader.getInstance().getM_AutoTuneServer());

			m_KeyFileAuth = PrefsNightScoutLoader.getInstance().isM_AutoTuneSSH2KeyLogin();
			m_KeyFile     = PrefsNightScoutLoader.getInstance().getM_AutoTuneKeyFile();
			m_Host        = new String ((suppliedServer.contains("@") ? suppliedServer.substring(suppliedServer.indexOf('@')+1) : ""));
			m_User        = new String ((suppliedServer.contains("@") ? suppliedServer.substring(0, suppliedServer.indexOf('@')) : ""));
			m_PassWord    = PrefsNightScoutLoader.getInstance().getM_AutoTunePassword();
			m_port        = 22;
		}

		/**
		 * @return the m_KeyFileAuth
		 */
		public synchronized Boolean getM_KeyFileAuth() {
			return m_KeyFileAuth;
		}

		/**
		 * @return the m_KeyFile
		 */
		public synchronized String getM_KeyFile() {
			return m_KeyFile;
		}

		/**
		 * @return the m_Host
		 */
		public synchronized String getM_Host() {
			return m_Host;
		}

		/**
		 * @return the m_User
		 */
		public synchronized String getM_User() {
			return m_User;
		}

		/**
		 * @return the m_PassWord
		 */
		public synchronized String getM_PassWord() {
			return m_PassWord;
		}

		/**
		 * @return the m_port
		 */
		public synchronized int getM_port() {
			return m_port;
		}
	}


	RemoteLinuxServer(Date startDate, Date endDate)
	{
		m_JSch = new JSch();
		m_OutputStream = new String();
		m_StartDate = new Date(startDate.getTime());
		m_EndDate   = new Date(endDate.getTime());
	}

	RemoteLinuxServer(Date startDate, Date endDate, TextLineReceiverInterface tlri)
	{
		m_JSch = new JSch();
		m_OutputStream = new String();
		m_StartDate = new Date(startDate.getTime());
		m_EndDate   = new Date(endDate.getTime());
		m_TextLineReceiverInterface = tlri;
	}

	// For file download / upload
	RemoteLinuxServer(TextLineReceiverInterface tlri)
	{
		m_JSch = new JSch();
		m_OutputStream = new String();
		m_StartDate = new Date(0);
		m_EndDate   = new Date(0);
		m_TextLineReceiverInterface = tlri;
	}


	public int runAutotuneToExcel(HSSFWorkbook wb, Sheet sheet, int rowNum)
	{
		runAutotune();

		int result = writeAutotuneOutputToExcel(wb, sheet, rowNum);
		return result;
	}

	public void runAutotune()
	{
		boolean success = false;

		addTextLine("**************************************************\n");
		addTextLineWithDate("Autotune Thread Started\n");
		addTextLine("**************************************************\n");


		String command = "oref0-autotune --dir=~/myopenaps --ns-host="; 
		command += PrefsNightScoutLoader.getInstance().getM_AutoTuneNSURL();
		command += " --start-date=";
		try 
		{
			command += CommonUtils.convertDateString(m_StartDate, "YYYY-MM-dd");
			command += " --end-date=";
			command += CommonUtils.convertDateString(m_EndDate, "YYYY-MM-dd");
		} 
		catch (ParseException e) 
		{
			m_Logger.log(Level.SEVERE, "Autotune.runAutotune() Exception caught: " + e.getMessage());
		}

		success = execRemoteCommand(command, "Autotune Started.", m_TextLineReceiverInterface, "Autotune Finished.");
		if (success == false)
		{
			addTextLine("**************************************************\n");
			addTextLineWithDate("Autotune Thread Finished with exception\n");
			addTextLine("**************************************************\n");
			addTextLine("****");
			addTextLine(" Autotune failed to run ");
			addTextLine("****");
			addTextLine("\n\n");
			addTextLine("Please check server details, authentication key file/password and that internet is accessible");
		}
		else
		{
			addTextLine("**************************************************\n");
			addTextLineWithDate("Autotune Thread Finished\n");
			addTextLine("**************************************************\n");
		}
	}

	public void listBackupDirectory()
	{
		boolean success = false;
		String command = "mkdir -p " + m_BackupDirectory + ";" + "ls -alrt " + m_BackupDirectory; 
		//		String command = "ls -alrt " + m_BackupDirectory; 

		addTextLine("**************************************************\n");
		addTextLineWithDate("List Remote Backups Thread Started\n");
		addTextLine("**************************************************\n");

		success = execRemoteCommand(command, null, m_TextLineReceiverInterface, null);

		if (success == false)
		{
			addTextLine("**************************************************\n");
			addTextLineWithDate("Autotune Thread Finished with exception\n");
			addTextLine("**************************************************\n");
			addTextLine("****");
			addTextLine(" Autotune failed to run ");
			addTextLine("****");
			addTextLine("\n\n");
			addTextLine("Please check server details, authentication key file/password and that internet is accessible");
		}
		else
		{
			addTextLine("**************************************************\n");
			addTextLineWithDate("List Remote Backups Thread Finished\n");
			addTextLine("**************************************************\n");
		}
	}



	public boolean installProfileFile(String path)
	{
		boolean result = false;

		// Copy the supplied file remotely as each of the three files that should be stored.

		uploadProfileFile("myopenaps/settings/profile.json", path);
		uploadProfileFile("myopenaps/settings/autotune.json", path);
		uploadProfileFile("myopenaps/settings/pumpprofile.json", path);

		// Now check each
		String checkProfile  = execRemoteCommand("ls -alrt myopenaps/settings/profile.json", null, null);
		String checkAutotune = execRemoteCommand("ls -alrt myopenaps/settings/autotune.json", null, null);
		String checkPump     = execRemoteCommand("ls -alrt myopenaps/settings/pumpprofile.json", null, null);



		return result;
	}

	public String execRemoteCommand(String command, String startNotification, String endNotification)
	{
		String result = "";
		TextLineAccumulator op = new TextLineAccumulator();
		execRemoteCommand(command, startNotification, op, endNotification);
		result = op.getM_Output();
		return result;
	}

	public boolean execRemoteCommand(String command, String startNotification, TextLineReceiverInterface tlri, String endNotification)
	{
		boolean result = false;
		try
		{
			RemoteLinuxServerHostDetails details = new RemoteLinuxServerHostDetails();
			
			// Either use SSH Key file or User Name and Password
			if (details.getM_KeyFileAuth())
			{
				m_JSch.addIdentity(details.getM_KeyFile());
			}
			
			String host = details.getM_Host();
			String user = details.getM_User();
			int    port = details.getM_port();
			Session session = m_JSch.getSession(user, host, port);
			
			if (!details.getM_KeyFileAuth())
			{
				session.setPassword(details.getM_PassWord());
			}

			//			m_JSch.addIdentity(PrefsNightScoutLoader.getInstance().getM_AutoTuneKeyFile());
			//			String host = PrefsNightScoutLoader.getInstance().getM_AutoTuneServer();
			//			String user=host.substring(0, host.indexOf('@'));
			//			host=host.substring(host.indexOf('@')+1);
			//			Session session = m_JSch.getSession(user, host, 22);


			// username and password will be given via UserInfo interface.
			UserInfo ui=new MyUserInfo();
			session.setUserInfo(ui);
			session.connect();
			if (tlri != null)
			{
				//				tlri.addTextLine("Connected to " +host+ "\n");
				m_Logger.log(Level.FINE, "Connected to " +host+ " - Running " +  command);
			}

			Channel channel=session.openChannel("exec");
			((ChannelExec)channel).setCommand(command);

			if (startNotification != null)
			{
				m_Logger.log( Level.INFO, startNotification);
			}

			channel.setInputStream(null);
			((ChannelExec)channel).setErrStream(System.err);

			InputStream in=channel.getInputStream();

			channel.connect();

			byte[] tmp=new byte[1024];
			while(true)
			{
				while(in.available()>0)
				{
					int i=in.read(tmp, 0, 1024);
					if(i<0)break;
					String op = new String(tmp, 0, i);
					m_OutputStream += op;
					if (tlri != null)
					{
						tlri.addTextLine(op);
					}
				}
				if(channel.isClosed())
				{
					if(in.available()>0) 
						continue; 
					break;
				}
				try
				{
					Thread.sleep(1000);
				}
				catch(Exception ee)
				{
					m_Logger.log(Level.SEVERE, "RemoteLinuxServer.execRemoteCommand() Exception caught: " + ee.getMessage());
				}
			}
			channel.disconnect();
			session.disconnect();

			if (endNotification != null)
			{
				m_Logger.log( Level.INFO, endNotification);
			}

			result = true;
		}
		catch(Exception e)
		{
			m_Logger.log(Level.SEVERE, "RemoteLinuxServer.execRemoteCommand() Exception caught: " + e.getMessage());
			result = false;
		}

		return result;
	}


	public void remoteSaveAs(String json, String localFile) throws IOException
	{
		boolean remoteBackedUp = false;
		boolean savedLocally   = false;
		boolean copiedRemotely = false;

		// 1 Backup remote file
		remoteBackedUp = backupProfileFile();
		if (remoteBackedUp == true)
		{
			addTextLine("Current Remote Autotune JSON Profile backed-up successfully.\n");
		}

		// 2 Save JSON contents to a temporary file
		if (remoteBackedUp == true)
		{
			try (FileWriter file = new FileWriter(localFile)) 
			{
				file.write(json);
				addTextLine("New Autotune JSON Parameter values from form successfully saved to local file.\n");
			}
			catch (IOException e1) 
			{
				m_Logger.log(Level.SEVERE, "Exceptioon saving Autotune JSON Parameters to file " + e1);
			}
			finally
			{
				savedLocally = true;
			}
		}

		// 5 Then copy the file over to remote server
		if (remoteBackedUp == true && savedLocally == true)
		{
			installProfileFile(localFile);
			addTextLine("New Autotune JSON Parameter in local file copied to Autotune server.\n");
		}

		// And ensure it's installed in all three locations.
	}

//	public void runAutotune_orig()
//	{
//		// Only so long as output stream is empty!
//		if (m_OutputStream.length() == 0)
//		{
//			try
//			{
//				m_JSch.addIdentity(PrefsNightScoutLoader.getInstance().getM_AutoTuneKeyFile());
//
//				String host = PrefsNightScoutLoader.getInstance().getM_AutoTuneServer();
//				String user=host.substring(0, host.indexOf('@'));
//				host=host.substring(host.indexOf('@')+1);
//				Session session = m_JSch.getSession(user, host, 22);
//
//				// username and password will be given via UserInfo interface.
//				UserInfo ui=new MyUserInfo();
//				session.setUserInfo(ui);
//				session.connect();
//				if (m_TextLineReceiverInterface != null)
//				{
//					//					addTextLine("Connected to " +host+ "\n");
//					m_Logger.log(Level.FINE, "Connected to " +host+ " - Running Autotune-Orig");
//				}
//
//				//			String command = "oref0-autotune --dir=~/myopenaps --ns-host=https://dexcom-davidr.azurewebsites.net --start-date=2017-01-29 --end-date=2017-02-12";
//				String command = "oref0-autotune --dir=~/myopenaps --ns-host="; 
//				command += PrefsNightScoutLoader.getInstance().getM_AutoTuneNSURL();
//				command += " --start-date=";
//				command += CommonUtils.convertDateString(m_StartDate, "YYYY-MM-dd");
//				command += " --end-date=";
//				command += CommonUtils.convertDateString(m_EndDate, "YYYY-MM-dd");
//
//				Channel channel=session.openChannel("exec");
//				((ChannelExec)channel).setCommand(command);
//
//				m_Logger.log( Level.INFO, "Autotune Started.");
//
//
//				// X Forwarding
//				// channel.setXForwarding(true);
//
//				//channel.setInputStream(System.in);
//				channel.setInputStream(null);
//
//				//channel.setOutputStream(System.out);
//
//				//FileOutputStream fos=new FileOutputStream("/tmp/stderr");
//				//((ChannelExec)channel).setErrStream(fos);
//				((ChannelExec)channel).setErrStream(System.err);
//
//				InputStream in=channel.getInputStream();
//
//				channel.connect();
//
//				byte[] tmp=new byte[1024];
//				while(true){
//					while(in.available()>0){
//						int i=in.read(tmp, 0, 1024);
//						if(i<0)break;
//						String op = new String(tmp, 0, i);
//						m_OutputStream += op;
//						if (m_TextLineReceiverInterface != null)
//						{
//							addTextLine(op);
//						}
//						else
//						{
//							System.out.print(op);
//						}
//					}
//					if(channel.isClosed()){
//						if(in.available()>0) continue; 
//						//						if (m_TextLineReceiverInterface != null)
//						//						{
//						//							addTextLine("\n\nexit-status: "+channel.getExitStatus());
//						//						}
//						//						else
//						//						{
//						//							System.out.println("exit-status: "+channel.getExitStatus());
//						//						}
//						break;
//					}
//					try{Thread.sleep(1000);}catch(Exception ee){}
//				}
//				channel.disconnect();
//				session.disconnect();
//
//				m_Logger.log(Level.INFO, "Autotune Finished.");
//
//				// Add a sumary line at end of stream
//				String summary = "Autotune ran between " +
//						CommonUtils.convertDateString(m_StartDate, "YYYY-MM-dd") +
//						" and " +
//						CommonUtils.convertDateString(m_EndDate, "YYYY-MM-dd") + 
//						" on host " +
//						PrefsNightScoutLoader.getInstance().getM_AutoTuneServer();
//				if (m_TextLineReceiverInterface != null)
//				{
//					addTextLine(summary);
//				}
//				else
//				{
//					System.out.print(summary);
//				}
//
//			}
//			catch(Exception e)
//			{
//				m_Logger.log(Level.SEVERE, "RemoteLinuxServer.runAutotune() Exception caught: " + e.getMessage());
//			}
//		}
//	}

	public boolean downloadProfileFile(String remoteProfileName, String localProfileName)
	{
		boolean result = false; // Not downloaded
		try
		{

			RemoteLinuxServerHostDetails details = new RemoteLinuxServerHostDetails();
			if (details.getM_KeyFileAuth())
			{
				m_JSch.addIdentity(details.getM_KeyFile());
			}
			String host = details.getM_Host();
			String user = details.getM_User();
			int    port = details.getM_port();
			Session session = m_JSch.getSession(user, host, port);

			if (!details.getM_KeyFileAuth())
			{
				session.setPassword(details.getM_PassWord());
			}

			
			//			m_JSch.addIdentity(PrefsNightScoutLoader.getInstance().getM_AutoTuneKeyFile());
			//
			//			String host = PrefsNightScoutLoader.getInstance().getM_AutoTuneServer();
			//			String user=host.substring(0, host.indexOf('@'));
			//			host=host.substring(host.indexOf('@')+1);
			//			Session session = m_JSch.getSession(user, host, 22);

			// username and password will be given via UserInfo interface.
			UserInfo ui=new MyUserInfo();
			session.setUserInfo(ui);
			session.connect();
			if (m_TextLineReceiverInterface != null)
			{
				//				addTextLine("Connected to " +host+ "\n");
				m_Logger.log(Level.FINE, "Connected to " +host+ " - Downloading Profile File");

			}

			Channel channel=session.openChannel("sftp");
			channel.connect();
			ChannelSftp channelSftp = (ChannelSftp)channel;
			channelSftp.get(remoteProfileName, localProfileName); 

			// Now check local file is here successfully
			File f = new File(localProfileName);
			result = (f.exists() && !f.isDirectory()) ? true : false;
		}
		catch(Exception e)
		{
			m_Logger.log(Level.SEVERE, "RemoteLinuxServer.downloadProfileFile() Exception caught: " + e.getMessage());
		}

		return result;
	}

	public boolean downloadAllBackupProfileFiles(String localDirectory)
	{
		boolean result = downloadAllFiles(m_BackupDirectory, localDirectory);
		return result;
	}	

	public boolean downloadAllFiles(String remoteDirectory, String localDirectory)
	{
		boolean result = false; // Not downloaded
		try
		{
			addTextLine("**************************************************\n");
			addTextLineWithDate("Copy Remote Backups Thread Started\n");
			addTextLine("Copying files from remote directory: " + remoteDirectory + "\n");
			addTextLine("Copying files to local directory: " + localDirectory + "\n");
			addTextLine("**************************************************\n");

			RemoteLinuxServerHostDetails details = new RemoteLinuxServerHostDetails();
			if (details.getM_KeyFileAuth())
			{
				m_JSch.addIdentity(details.getM_KeyFile());
			}
			String host = details.getM_Host();
			String user = details.getM_User();
			int    port = details.getM_port();
			Session session = m_JSch.getSession(user, host, port);

			if (!details.getM_KeyFileAuth())
			{
				session.setPassword(details.getM_PassWord());
			}
			
			//			m_JSch.addIdentity(PrefsNightScoutLoader.getInstance().getM_AutoTuneKeyFile());
			//
			//			String host = PrefsNightScoutLoader.getInstance().getM_AutoTuneServer();
			//			String user=host.substring(0, host.indexOf('@'));
			//			host=host.substring(host.indexOf('@')+1);
			//			Session session = m_JSch.getSession(user, host, 22);

			// username and password will be given via UserInfo interface.
			UserInfo ui=new MyUserInfo();
			session.setUserInfo(ui);
			session.connect();
			if (m_TextLineReceiverInterface != null)
			{
				//				addTextLine("Connected to " +host+ "\n");
				m_Logger.log(Level.FINE, "Connected to " +host+ " - Downloading all files.");
			}

			Channel channel=session.openChannel("sftp");
			channel.connect();
			ChannelSftp channelSftp = (ChannelSftp)channel;
			channelSftp.cd(remoteDirectory);
			channelSftp.lcd(localDirectory);

			Vector<ChannelSftp.LsEntry> list = channelSftp.ls("*");
			for(ChannelSftp.LsEntry entry : list)
			{
				channelSftp.get(entry.getFilename(), entry.getFilename());
				addTextLine(" Copied " + entry.getFilename() + "\n");
			}
			result = true;
		}
		catch(Exception e)
		{
			m_Logger.log(Level.SEVERE, "RemoteLinuxServer.downloadAllFiles() Exception caught: " + e.getMessage());
		}

		if (result == true)
		{
			addTextLine("**************************************************\n");
			addTextLineWithDate("Copy Remote Backups Thread Finished\n");					
			addTextLine("**************************************************\n");

		}
		else
		{
			addTextLine("**************************************************\n");
			addTextLineWithDate("Copy Remote Backups Thread Finished with exception\n");
			addTextLine("**************************************************\n");			
		}

		return result;
	}

	public boolean backupProfileFile()
	{
		boolean result = false;
		try
		{
			// Make the backup directory if not already there
			String backupDir  = m_BackupDirectory;
			String mkdirCmd   = "mkdir -p " + backupDir;
			execRemoteCommand(mkdirCmd, null, null, null);

			// Create date timestamp from current time for backup
			Date now = new Date();
			String timestamp  = CommonUtils.convertDateString(now, "YYYYMMdd-HHmmss");
			String backupFile = "profile.json." + timestamp + ".bak";
			String backupPath = backupDir +"/"+ backupFile;

			// Now copy the current profile file to backup directory with modified filename
			String cpyCmd = "cp myopenaps/settings/profile.json " + backupPath;
			execRemoteCommand(cpyCmd, null, null, null);

			// Now confirm we can pull the backup file over to temp
			result = downloadProfileFile(backupPath, "C:\\Temp\\" + backupFile);
		}
		catch(Exception e)
		{
			m_Logger.log(Level.SEVERE, "RemoteLinuxServer.backupProfileFile() Exception caught: " + e.getMessage());
		}
		return result;
	}

	public void uploadProfileFile(String remoteProfileName, String localProfileName)
	{
		try
		{
			RemoteLinuxServerHostDetails details = new RemoteLinuxServerHostDetails();
			if (details.getM_KeyFileAuth())
			{
				m_JSch.addIdentity(details.getM_KeyFile());
			}
			String host = details.getM_Host();
			String user = details.getM_User();
			int    port = details.getM_port();
			Session session = m_JSch.getSession(user, host, port);

			if (!details.getM_KeyFileAuth())
			{
				session.setPassword(details.getM_PassWord());
			}
			
			//			m_JSch.addIdentity(PrefsNightScoutLoader.getInstance().getM_AutoTuneKeyFile());
			//
			//			String host = PrefsNightScoutLoader.getInstance().getM_AutoTuneServer();
			//			String user=host.substring(0, host.indexOf('@'));
			//			host=host.substring(host.indexOf('@')+1);
			//			Session session = m_JSch.getSession(user, host, 22);

			// username and password will be given via UserInfo interface.
			UserInfo ui=new MyUserInfo();
			session.setUserInfo(ui);
			session.connect();
			if (m_TextLineReceiverInterface != null)
			{
				//				addTextLine("Connected to " +host+ "\n");
				m_Logger.log(Level.FINE, "Connected to " +host+ " - Uploading profile file.");				
			}

			Channel channel=session.openChannel("sftp");
			channel.connect();
			ChannelSftp channelSftp = (ChannelSftp)channel;

			channelSftp.put(localProfileName, remoteProfileName); 
		}
		catch(Exception e)
		{
			m_Logger.log(Level.SEVERE, "RemoteLinuxServer.uploadProfileFile() Exception caught: " + e.getMessage());
		}
	}


	public int writeAutotuneOutputToExcel(HSSFWorkbook wb, Sheet sheet, int rowNum)
	{
		// Key words to look for:
		String start   = "Parameter      | Pump     | Autotune";
		String ignored = "--------------------------------------\n";

		int result = 0;

		int index = m_OutputStream.lastIndexOf(start);
		if (index >= 0)
		{
			// Create a string with rest of output
			String recommendations = new String(m_OutputStream.substring(index + start.length() + ignored.length()));

			String[] lines = recommendations.split("\n");

			Row row = null;
			Cell cell = null;
			HSSFCellStyle style = null;
			// No particular format
			HSSFCellStyle regularStyle = wb.createCellStyle();

			for (int l = 0; l < lines.length; l++)
			{
				String[] rec = lines[l].split("\\|");
				row = sheet.createRow(l + rowNum);
				result = l;
				int j = 0;

				for (int r = 0; r < rec.length; r++)
				{
					style = regularStyle;

					cell = row.createCell(j++);
					String s = new String(rec[r]).trim();
					if (s.contains("."))
					{
						Double d = Double.parseDouble(s);
						cell.setCellValue(d);
					}
					else
					{
						cell.setCellValue(s);
					}
					cell.setCellStyle(style);					
				}
			}

			// Now split this based on the Pipe symbol and strip white space.

		}
		// Put an entry in the file to indicate that Autotune didn't run
		else
		{
			Row row = null;
			Cell cell = null;
			HSSFCellStyle style = null;
			// No particular format
			HSSFCellStyle regularStyle = wb.createCellStyle();

			row = sheet.createRow(rowNum);
			result = 1;
			int j = 0;
			style = regularStyle;

			cell = row.createCell(j++);
			cell.setCellValue("****");
			cell.setCellStyle(style);	

			cell = row.createCell(j++);
			String s = new String("Autotune failed to run");
			cell.setCellValue(s);
			cell.setCellStyle(style);	

			cell = row.createCell(j++);
			cell.setCellValue("****");
			cell.setCellStyle(style);
		}

		return result;

	}

//	RemoteLinuxServer(boolean forTestPurposes)
//	{
//		m_JSch = new JSch();
//		m_OutputStream = new String();
//
//		// just_connect();
//		exec_command();
//
//		identifyRecommendations();
//	}

	// Consulted http://www.jcraft.com/jsch/examples/UserAuthPubKey.java.html
	private void just_connect()
	{
		try {
			m_JSch.addIdentity("C:\\temp\\autotuneinstance1.ppk");

			String host = "ubuntu@ec2-54-186-243-97.us-west-2.compute.amazonaws.com";

			String user=host.substring(0, host.indexOf('@'));
			host=host.substring(host.indexOf('@')+1);
			Session session = m_JSch.getSession(user, host, 22);

			// username and passphrase will be given via UserInfo interface.
			UserInfo ui=new MyUserInfo();
			session.setUserInfo(ui);
			session.connect();

			Channel channel=session.openChannel("shell");

			channel.setInputStream(System.in);
			channel.setOutputStream(System.out);

			channel.connect();

		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void exec_command()
	{
		try
		{
			m_JSch.addIdentity("C:\\temp\\autotuneinstance1.ppk");

			String host = "ubuntu@ec2-54-186-243-97.us-west-2.compute.amazonaws.com";
			String user=host.substring(0, host.indexOf('@'));
			host=host.substring(host.indexOf('@')+1);
			Session session = m_JSch.getSession(user, host, 22);

			/*
	      String xhost="127.0.0.1";
	      int xport=0;
	      String display=JOptionPane.showInputDialog("Enter display name", 
	                                                 xhost+":"+xport);
	      xhost=display.substring(0, display.indexOf(':'));
	      xport=Integer.parseInt(display.substring(display.indexOf(':')+1));
	      session.setX11Host(xhost);
	      session.setX11Port(xport+6000);
			 */

			// username and password will be given via UserInfo interface.
			UserInfo ui=new MyUserInfo();
			session.setUserInfo(ui);
			session.connect();

			String command=JOptionPane.showInputDialog("Enter command", 
					"set|grep SSH");

			Channel channel=session.openChannel("exec");
			((ChannelExec)channel).setCommand(command);

			// X Forwarding
			// channel.setXForwarding(true);

			//channel.setInputStream(System.in);
			channel.setInputStream(null);

			//channel.setOutputStream(System.out);

			//FileOutputStream fos=new FileOutputStream("/tmp/stderr");
			//((ChannelExec)channel).setErrStream(fos);
			((ChannelExec)channel).setErrStream(System.err);

			InputStream in=channel.getInputStream();

			channel.connect();

			byte[] tmp=new byte[1024];
			while(true){
				while(in.available()>0){
					int i=in.read(tmp, 0, 1024);
					if(i<0)break;
					String op = new String(tmp, 0, i);
					m_OutputStream += op;
					System.out.print(op);
				}
				if(channel.isClosed()){
					if(in.available()>0) continue; 
					System.out.println("exit-status: "+channel.getExitStatus());
					break;
				}
				try{Thread.sleep(1000);}catch(Exception ee){}
			}
			channel.disconnect();
			session.disconnect();
		}
		catch(Exception e)
		{
			m_Logger.log(Level.SEVERE, "RemoteLinuxServer.exec_command() Exception caught: " + e.getMessage());
		}
	}

	private void addTextLine(String line)
	{
		if (m_TextLineReceiverInterface != null)
		{
			m_TextLineReceiverInterface.addTextLine(line);
		}
	}

	private void addTextLineWithDate(String line)
	{
		if (m_TextLineReceiverInterface != null)
		{
			// Keep everything at level INFO without the standard date time Logger provides
			// but sometimes we might want the time - eg for Sync
			final DateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
			Date now = new Date();

			String s = new String(df.format(now) + " " + line);
			m_TextLineReceiverInterface.addTextLine(s);
		}

	}

//	private void identifyRecommendations()
//	{
//
//		// Key words to look for:
//		String start   = "Parameter      | Current  | Autotune";
//		String ignored = "---------------------------------------\n";
//
//		int index = m_OutputStream.indexOf(start);
//		if (index >= 0)
//		{
//			// Create a string with rest of output
//			String recommendations = new String(m_OutputStream.substring(index + start.length() + ignored.length()));
//
//			String[] lines = recommendations.split("\n");
//
//			for (int l = 0; l < lines.length; l++)
//			{
//				String[] rec = lines[l].split("\\|");
//
//				for (int r = 0; r < rec.length; r++)
//				{
//					System.out.print(rec[r]);
//				}
//			}
//
//			// Now split this based on the Pipe symbol and strip white space.
//
//		}
//
//	}

	public class TextLineAccumulator implements TextLineReceiverInterface
	{
		private String m_Output = new String("");

		TextLineAccumulator()
		{

		}

		@Override
		public void addTextLine(String line) 
		{
			m_Output += line;
		}

		/**
		 * @return the m_Output
		 */
		public synchronized String getM_Output() {
			return m_Output;
		}

	}


	public static class MyUserInfo implements UserInfo, UIKeyboardInteractive{
		public String getPassword(){ return null; }
		public boolean promptYesNoOrig(String str){
			Object[] options={ "yes", "no" };
			int foo=JOptionPane.showOptionDialog(null, 
					str,
					"Warning", 
					JOptionPane.DEFAULT_OPTION, 
					JOptionPane.WARNING_MESSAGE,
					null, options, options[0]);
			return foo==0;
		}
		public boolean promptYesNo(String str){
			return true;
		}

		String passphrase;
		JTextField passphraseField=(JTextField)new JPasswordField(20);

		public String getPassphrase(){ return passphrase; }
		public boolean promptPassphrase(String message){
			Object[] ob={passphraseField};
			int result=
					JOptionPane.showConfirmDialog(null, ob, message,
							JOptionPane.OK_CANCEL_OPTION);
			if(result==JOptionPane.OK_OPTION){
				passphrase=passphraseField.getText();
				return true;
			}
			else{ return false; }
		}
		public boolean promptPassword(String message){ return true; }
		public void showMessage(String message){
			JOptionPane.showMessageDialog(null, message);
		}
		final GridBagConstraints gbc = 
				new GridBagConstraints(0,0,1,1,1,1,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,
						new Insets(0,0,0,0),0,0);
		private Container panel;
		public String[] promptKeyboardInteractive(String destination,
				String name,
				String instruction,
				String[] prompt,
				boolean[] echo){
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());

			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.gridx = 0;
			panel.add(new JLabel(instruction), gbc);
			gbc.gridy++;

			gbc.gridwidth = GridBagConstraints.RELATIVE;

			JTextField[] texts=new JTextField[prompt.length];
			for(int i=0; i<prompt.length; i++){
				gbc.fill = GridBagConstraints.NONE;
				gbc.gridx = 0;
				gbc.weightx = 1;
				panel.add(new JLabel(prompt[i]),gbc);

				gbc.gridx = 1;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.weighty = 1;
				if(echo[i]){
					texts[i]=new JTextField(20);
				}
				else{
					texts[i]=new JPasswordField(20);
				}
				panel.add(texts[i], gbc);
				gbc.gridy++;
			}

			if(JOptionPane.showConfirmDialog(null, panel, 
					destination+": "+name,
					JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE)
					==JOptionPane.OK_OPTION){
				String[] response=new String[prompt.length];
				for(int i=0; i<prompt.length; i++){
					response[i]=texts[i].getText();
				}
				return response;
			}
			else{
				return null;  // cancel
			}
		}
	}


	/**
	 * @return the mLogger
	 */
	public static synchronized Logger getmLogger() {
		return m_Logger;
	}

	/**
	 * @return the m_JSch
	 */
	public synchronized JSch getM_JSch() {
		return m_JSch;
	}

	/**
	 * @return the m_OutputStream
	 */
	public synchronized String getM_OutputStream() {
		return m_OutputStream;
	}

	/**
	 * @return the m_StartDate
	 */
	public synchronized Date getM_StartDate() {
		return m_StartDate;
	}

	/**
	 * @return the m_EndDate
	 */
	public synchronized Date getM_EndDate() {
		return m_EndDate;
	}

}
