package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import com.amazonaws.services.kms.model.DeleteImportedKeyMaterialRequest;

public class DirectoryButtons{

	private ArrayList<JButton> buttons = new ArrayList<JButton>();
	private JPanel pathAndSearchPanel = null;
	private JPanel buttonPanel = new JPanel();
	private EditableDirectory editableDirectory = null;
	//fresh button
	private JButton freshButton = new JButton();
	//button panel 位置相关
	private int UIWidth = Math.round(1300*ScreenInfo.getWidthFactor());
	private float heightFactor = ScreenInfo.getHeightFactor();
	private float widthFactor = ScreenInfo.getWidthFactor();
	private int widthPos = 0;
	private int heightPos = 0;
	//用来获取字符串所占的像素个数
	FontMetrics metrics = null;
	public DirectoryButtons(){
		
	}
	public DirectoryButtons(int widthPos,int heightPos){
		this.widthPos = widthPos;
		this.heightPos = heightPos;
	}
	public DirectoryButtons(JPanel pathAndSearchPanel) {
		// TODO Auto-generated constructor stub
		this.pathAndSearchPanel = pathAndSearchPanel;
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.setBounds(Math.round(85*widthFactor), Math.round(9*heightFactor),
				UIWidth*3/5,  Math.round(32*heightFactor));
		buttonPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(0xA9A9A9)));
		buttonPanel.setLayout(null);
		this.pathAndSearchPanel.add(buttonPanel);
		metrics = buttonPanel.getFontMetrics(buttonPanel.getFont());
		//add freshButton
		freshButton.setIcon(new ImageIcon("src/main/resources/pictures/fresh16-16.png"));
		freshButton.setFocusPainted(false);
		freshButton.setFocusable(false);
		freshButton.setMargin(new Insets(0, 0, 0, 0));
		freshButton.setBorderPainted(false);
		freshButton.setBackground(Color.WHITE);
		freshButton.setBounds(UIWidth*3/5-17, 0+1, 16, Math.round(29*heightFactor));
		freshButton.addMouseListener(new freshButtonMouseListener(freshButton));
		buttonPanel.add(freshButton);
	}
	public void setBound(int sw){
		this.UIWidth = sw;
		buttonPanel.setBounds(Math.round(85*widthFactor), Math.round(9*heightFactor),
				UIWidth*3/5,  Math.round(32*heightFactor));
		freshButton.setBounds(UIWidth*3/5-17, 0+1, 16, Math.round(29*heightFactor));
	}
	
	public void addOneDirectory(String directoryName){
		
		int directoryLength = directoryName.length();
		JButton button = new JButton(directoryName);
		button.setMargin(new Insets(0, 0, 0, 0));
		button.setBorderPainted(false);
		button.setBackground(Color.WHITE);
		button.setFocusable(false);
		button.setFocusPainted(false);
		
		button.setBounds(this.widthPos+1, this.heightPos+1, 
				metrics.stringWidth(directoryName)+10,  Math.round(29*heightFactor));
		this.widthPos = this.widthPos+metrics.stringWidth(directoryName)+10;
		
		//add listener
		button.addMouseListener(new directoryButtonMouseListener(button));
		buttonPanel.add(button);
		buttons.add(button);
		
		JButton delimiterButton = new JButton();
		delimiterButton.setIcon(new ImageIcon("src/main/resources/pictures/directory-delimiter16-16.png"));
		delimiterButton.setMargin(new Insets(0, 0, 0, 0));
		delimiterButton.setBorderPainted(false);
		delimiterButton.setBackground(Color.WHITE);
		delimiterButton.setFocusable(false);
		delimiterButton.setFocusPainted(false);
		delimiterButton.setBounds(this.widthPos+1, this.heightPos+1, 
				Math.round(16*widthFactor),  Math.round(29*heightFactor));
		this.widthPos = this.widthPos+Math.round(16*widthFactor);
		//add listener
		delimiterButton.addMouseListener(new directoryButtonMouseListener(delimiterButton));
		buttonPanel.add(delimiterButton);
		buttons.add(delimiterButton);
		
	}
	private class directoryButtonMouseListener implements MouseListener{
		
		private JButton button = null;
		public directoryButtonMouseListener(JButton button){
			this.button = button;
		}
		
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("back");
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			button.setBackground(new Color(0xCDE8FF));
		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			button.setBackground(Color.white);
		}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	private class buttonPanelMouseListener implements MouseListener{
		
		private JPanel panel = null;
		private int widthPos = 0;
		private EditableDirectory editableDirectory = null;

		public buttonPanelMouseListener(JPanel buttonPanel, 
				EditableDirectory editableDirectory, int widthPos) {
			// TODO Auto-generated constructor stub
			this.panel = buttonPanel;
			this.widthPos = widthPos;
			this.editableDirectory = editableDirectory;
		}

		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			if(e.getX()>this.widthPos){
				this.panel.show(false);
				this.editableDirectory.show(true);
			}
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	private class freshButtonMouseListener implements MouseListener{
		
		private JButton button = null;
		public freshButtonMouseListener(JButton button){
			this.button = button;
		}
		
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("back");
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			button.setBackground(new Color(0xCDE8FF));
		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			button.setBackground(Color.white);
		}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	public void setEditableDirectory(EditableDirectory editableDirectory) {
		// TODO Auto-generated method stub
		this.editableDirectory = editableDirectory;
		buttonPanel.addMouseListener(new buttonPanelMouseListener(buttonPanel,
				this.editableDirectory,this.widthPos));
	}
	public void show(boolean b) {
		// TODO Auto-generated method stub
		this.buttonPanel.show(b);
	}

}
