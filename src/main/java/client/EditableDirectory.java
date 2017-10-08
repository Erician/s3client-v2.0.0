package client;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

import interdata.CurDirData;

public class EditableDirectory {
	
	private JPanel pathAndSearchPanel = null; 
	private CurDirData curDirData  = null;
	private JTextField textField = new JTextField();
	private DirectoryButtons directoryButtons = null;
	//test field大小
	private int UIWidth = Math.round(1300*ScreenInfo.getWidthFactor());
	private float heightFactor = ScreenInfo.getHeightFactor();
	private float widthFactor = ScreenInfo.getWidthFactor();
	
	public EditableDirectory(JPanel pathAndSearchPanel){
		this.pathAndSearchPanel = pathAndSearchPanel;
		textField.setBackground(Color.WHITE);
		textField.setBounds(Math.round(85*widthFactor), Math.round(9*heightFactor),
				UIWidth*3/5,  Math.round(32*heightFactor));
		textField.setBorder(new MatteBorder(1, 1, 1, 1, new Color(0xA9A9A9)));
		
		this.pathAndSearchPanel.add(textField);
		textField.show(false);
	}
	public void setCurDirDate(CurDirData curDirData){
		this.curDirData = curDirData;
	}
	public void setDirectoryButtons(DirectoryButtons directoryButtons){
		this.directoryButtons = directoryButtons;
		textField.addFocusListener(new textFieldFocusListener(directoryButtons,textField));
		textField.addKeyListener(new textFieldKeyListener());
	}
	public void setBound(int sw){
		this.UIWidth = sw;
		textField.setBounds(Math.round(85*widthFactor), Math.round(9*heightFactor),
				UIWidth*3/5,  Math.round(32*heightFactor));
	}
	public void show(boolean b) {
		// TODO Auto-generated method stub
		this.textField.show(b);
		String curDir = "s3:/"+curDirData.getCurDir();
		this.textField.setText(curDir);
		
		this.textField.requestFocus();
		this.textField.selectAll();
	}
	private class textFieldKeyListener implements KeyListener{  
		public void keyPressed(KeyEvent arg0) {
			// TODO Auto-generated method stub
			if(arg0.getKeyCode() == KeyEvent.VK_ENTER){
				//
			}
		}
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}  
	}
	private class textFieldFocusListener implements FocusListener{
		private DirectoryButtons directoryButtons = null;
		private JTextField textField = null;
		public textFieldFocusListener(DirectoryButtons directoryButtons,JTextField textField) {
			// TODO Auto-generated constructor stub
			this.directoryButtons = directoryButtons;
			this.textField = textField;
		}
		public void focusGained(FocusEvent e) {
			// TODO Auto-generated method stub
			
		}
		public void focusLost(FocusEvent e) {
			// TODO Auto-generated method stub
			this.textField.show(false);
			this.directoryButtons.show(true);
		}    
    }

}
