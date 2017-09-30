package client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.JTextField;



import interdata.ConfigData;
import interdata.CurDirData;
import s3sdk.BucketOperations;
import s3sdk.S3Client;
import showmessage.ErrorMessage;
import showmessage.WarningMessage;

public class AccountDialog extends JDialog{
	
	String accessKeyId = null;
	String secretAccessKey = null;
	String region = null;
	
	JPanel accountDialogPanel = new JPanel();
	private JLabel accessKeyIdLabel = new JLabel("AccessKeyId");
	private JTextField accessKeyIdTextField  = new JTextField();
	
	private JLabel secretAccessKeyLabel = new JLabel("SecretAccessKey");
	private JTextField secretAccessKeyTextField = new JTextField();
	
	private JLabel endPointLabel = new JLabel("EndPoint");
	private JTextField endPointTextField  = new JTextField();
	
	private JButton saveButton = new JButton("Save");
	private JButton cancelButton = new JButton("Cancel");
	
	private BucketTree bucketTree = null;
	
	public AccountDialog() {
		// TODO Auto-generated constructor stub
		addComponents();
	}
	public AccountDialog(BucketTree bucketTree) {
		// TODO Auto-generated constructor stub
		this.bucketTree = bucketTree;
		addComponents();
	}
	private void addComponents() {
		
		accountDialogPanel.setLayout(null);
		
		setComponentPositionAndAttribute();
		accountDialogPanel.add(accessKeyIdLabel);
		accountDialogPanel.add(accessKeyIdTextField);
		
		accountDialogPanel.add(secretAccessKeyLabel);
		accountDialogPanel.add(secretAccessKeyTextField);
		
		accountDialogPanel.add(endPointLabel);
		accountDialogPanel.add(endPointTextField);
		
		accountDialogPanel.add(saveButton);
		accountDialogPanel.add(cancelButton);
		add(accountDialogPanel);
		
		setTitle("add an account");
	}
	private void setComponentPositionAndAttribute() {
		//add listener
		saveButton.addActionListener(new SaveButtonListener());
		cancelButton.addActionListener(new CancelButtonListener());
		//set pos
		accountDialogPanel.setBounds(0, 0, 800, 400);
		
		accessKeyIdLabel.setBounds(100, 70, 150, 28);
		accessKeyIdTextField.setBounds(260, 70, 440, 28);
		
		secretAccessKeyLabel.setBounds(100, 105, 150, 28);
		secretAccessKeyTextField.setBounds(260, 105, 440, 28);
		
		endPointLabel.setBounds(100, 140, 150, 28);
		endPointTextField.setBounds(260, 140, 440, 28);
		
		saveButton.setBounds(490, 250, 100, 28);
		cancelButton.setBounds(600, 250, 100, 28);
		
		//set color
		accountDialogPanel.setBackground(Color.WHITE);
		
		//set text
		accessKeyIdTextField.setText(ConfigData.getAccessKeyId());
		secretAccessKeyTextField.setText(ConfigData.getSecretAccessKey());
		endPointTextField.setText(ConfigData.getEndPoint());

	}
	private class SaveButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
			ConfigData.setAccessKeyId(accessKeyIdTextField.getText());
			
			if(ConfigData.getAccessKeyId()==null||ConfigData.getAccessKeyId().equals("")) {
				WarningMessage.showWarningMessage("AccessKeyId can not be empty!");
				return;
			}
			ConfigData.setSecretAccessKey(secretAccessKeyTextField.getText());
			if(ConfigData.getSecretAccessKey()==null||ConfigData.getSecretAccessKey().equals("")) {
				WarningMessage.showWarningMessage("SecretAccessKey can not be empty!");
				return;
			}
			ConfigData.setEndPoint(endPointTextField.getText());
			if(ConfigData.getEndPoint()==null||ConfigData.getEndPoint().equals("")) {
				WarningMessage.showWarningMessage("EndPoint can not be empty!");
				return;
			}
			dispose();
			/*
			 * 不要在这里检查account对不对了
			try {
				//create client and save account info
				if(S3Client.createAmazonS3Client()) {
					BucketTree.init(BucketOperations.listBucket());
					bucketTree.expandBucketTree();
					//如果上面的能正常运行，没有异常，说明configure都正确，进行保存
					ConfigData.saveConfig();
				}
				dispose();
			} catch (Exception e) {
				// TODO: handle exception
				ErrorMessage.showErrorMessage("We can't list your buckets.Please check you account!");
			}
			*/
		}
	}
	private class CancelButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			dispose();
		}
		
	}

}
