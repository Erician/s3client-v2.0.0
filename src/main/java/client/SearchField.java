package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;


public class SearchField {

	private JPanel pathAndSearchPanel = null;
	private JTextField searchField = new JTextField();
	private JPanel searchPanel = new JPanel();
	private JButton searchIcon = new JButton();
	//button panel 位置相关
	private int UIWidth = Math.round(1300*ScreenInfo.getWidthFactor());
	private float heightFactor = ScreenInfo.getHeightFactor();
	private float widthFactor = ScreenInfo.getWidthFactor();
	
	public SearchField(JPanel pathAndSearchPanel){
		this.pathAndSearchPanel = pathAndSearchPanel;
		searchPanel.setLayout(null);
		searchPanel.setBackground(Color.white);
		searchPanel.setBounds(Math.round(85*widthFactor)+UIWidth*3/5+5, Math.round(9*heightFactor),
				UIWidth-Math.round(85*widthFactor)-UIWidth*3/5-35,  Math.round(32*heightFactor));
		searchPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(0xA9A9A9)));
		this.pathAndSearchPanel.add(searchPanel);
		
		//add searchIcon
		searchIcon.setIcon(new ImageIcon("src/main/resources/pictures/search16-16.png"));
		searchIcon.setFocusPainted(false);
		searchIcon.setFocusable(false);
		searchIcon.setMargin(new Insets(0, 0, 0, 0));
		searchIcon.setBorderPainted(false);
		searchIcon.setBackground(Color.WHITE);
		searchIcon.setBounds(0+1, 0+1, 16, Math.round(29*heightFactor));
		searchPanel.add(searchIcon);
		
		searchField.setBorder(new MatteBorder(1, 0, 1, 1, new Color(0xA9A9A9)));
		searchField.setBackground(Color.WHITE);
		searchField.setBounds(16, 0, UIWidth-Math.round(85*widthFactor)-UIWidth*3/5-35-16, Math.round(32*heightFactor));
		searchField.addFocusListener(new searchFieldFocusListener());
		searchField.setText("\"search here\"");
		searchField.setForeground(new Color(0xAAAAAA)); 
		searchPanel.add(searchField);
		this.pathAndSearchPanel.add(searchPanel);
	}
	private class searchFieldFocusListener implements FocusListener{
		
		public void focusGained(FocusEvent e) {
			// TODO Auto-generated method stub
			searchField.setText("");
			searchField.setForeground(Color.BLACK);
		}
		public void focusLost(FocusEvent e) {
			// TODO Auto-generated method stub
			searchField.setText("\"search here\"");
			searchField.setForeground(new Color(0xAAAAAA)); 
		}    
    }
	
}
