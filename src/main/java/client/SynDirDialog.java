package client;

import java.awt.Color;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.amazonaws.services.dynamodbv2.xspec.AddAction;
import interdata.ConfigData;
import interdata.SynDirData;
import showmessage.ErrorMessage;

public class SynDirDialog extends JDialog{
	
	private String synDir = null;
	private JPanel synDirDialogPanel = new JPanel();
	
	private JLabel synDirLabel = new JLabel("Input the synchronization directory:");
	private JTextField synDirTextField  = new JTextField();
	
	private JButton showFileChooserButton = new JButton("...");
	
	
	private JButton saveButton = new JButton("Save");
	private JButton cancelButton = new JButton("Cancel");
	
	
	
	public SynDirDialog() {
		// TODO Auto-generated constructor stub
		synDirDialogPanel.setLayout(null);
		
		setComponentPositionAndAttribute();
		
		synDirDialogPanel.add(synDirLabel);
		synDirDialogPanel.add(synDirTextField);
		
		synDirDialogPanel.add(showFileChooserButton);
		
		synDirDialogPanel.add(saveButton);
		synDirDialogPanel.add(cancelButton);
		
		add(synDirDialogPanel);
		setTitle("set the synchronization directory");
		
	}
	
	private void setComponentPositionAndAttribute() {
		//add listener
		
		showFileChooserButton.addActionListener(new ShowFileChooserButtonListener());
		cancelButton.addActionListener(new CancelButtonListener());
		saveButton.addActionListener(new SaveButtonListener());
	
		//set pos
		synDirDialogPanel.setBounds(0, 0, 800, 350);
		
		synDirLabel.setBounds(100, 50, 500, 28);
		synDirTextField.setBounds(100, 90, 570, 28);
		
		showFileChooserButton.setBounds(670, 90, 30, 28);
		
		saveButton.setBounds(490, 200, 100, 28);
		cancelButton.setBounds(600, 200, 100, 28);
		
		//set color
		synDirDialogPanel.setBackground(Color.WHITE);
		
		//set text
		synDirTextField.setText(SynDirData.getSynDir());
	}
	private class ShowFileChooserButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			JFileChooser synDirFileChooser = new JFileChooser();
			synDirFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			synDirFileChooser.showDialog(new Label(), "choose a directory");
			synDir = synDirFileChooser.getSelectedFile().getAbsolutePath();
			synDirTextField.setText(synDir);
		}
		
	}
	private class SaveButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			synDir = synDirTextField.getText();
			if(synDir == "" || synDir == null) {
			}
			else if(new File(synDir).exists() == false){
				//如果目录后有多个'/',上面这个函数不能检测到,这个问题在下面处理
				ErrorMessage.showErrorMessage("the folder you input doesn't exist!");
			}
			else if(new File(synDir).isDirectory() == false) {
				ErrorMessage.showErrorMessage("it is not a folder!");
			}
			else {
				try {
					while(synDir.endsWith("\\")){
						synDir = synDir.substring(0, synDir.length()-1);
					}
					SynDirData.setSynDir(synDir);
					dispose();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					ErrorMessage.showErrorMessage("Can't save Synchronization Directory!");
				}
			}
		}
		
	}
	private class CancelButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			dispose();
		}
		
	}
	

}
