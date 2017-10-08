package client;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/*
 * 暂时没有用到这个类，先保留着
 */
public class ShortcutPanel extends JPanel{
	
	private JButton 
		uploadButton = new JButton("Upload"),
		downloadButton = new JButton("DownLoad"),
		newfolderButton = new JButton("New Folder"),
		newbucketButton = new JButton("New Bucket");
	
	public ShortcutPanel(){
		//set attribute
		setButtonsAttribute();
		//add listener
		addButtonsListener();
		//set bounds
		setButtonsBounds();
		//add
		this.add(uploadButton);
		this.add(downloadButton);
		this.add(newfolderButton);
		this.add(newbucketButton);
	}
	private void setButtonsAttribute(){
		//background
		uploadButton.setBackground(new Color(0xF2F2F2));
		downloadButton.setBackground(new Color(0xF2F2F2));
		newfolderButton.setBackground(new Color(0xF2F2F2));
		newbucketButton.setBackground(new Color(0xF2F2F2));
		//foreground
		uploadButton.setForeground(new Color(0xAAAAAA));
		downloadButton.setForeground(new Color(0xAAAAAA));
		newfolderButton.setForeground(new Color(0xAAAAAA));
		newbucketButton.setForeground(new Color(0xAAAAAA));
		//focusable
		uploadButton.setFocusable(false);
		downloadButton.setFocusable(false);
		newfolderButton.setFocusable(false);
		newbucketButton.setFocusable(false);
		//focus painted
		uploadButton.setFocusPainted(false);
		downloadButton.setFocusPainted(false);
		newfolderButton.setFocusPainted(false);
		newbucketButton.setFocusPainted(false);
		//border
		uploadButton.setBorderPainted(false);
		downloadButton.setBorderPainted(false);
		newfolderButton.setBorderPainted(false);
		newbucketButton.setBorderPainted(false);
	}
	private void addButtonsListener(){
		uploadButton.addMouseListener(new shortcutButtonMouseListener(uploadButton));
		downloadButton.addMouseListener(new shortcutButtonMouseListener(downloadButton));
		newfolderButton.addMouseListener(new shortcutButtonMouseListener(newfolderButton));
		newbucketButton.addMouseListener(new shortcutButtonMouseListener(newbucketButton));
	}
	private class shortcutButtonMouseListener implements MouseListener{
		
		private JButton button = null;
		public shortcutButtonMouseListener(JButton button){
			this.button = button;
		}
		
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("back");
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			button.setForeground(new Color(0x09A3DC));
		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			button.setForeground(new Color(0xAAAAAA));
		}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	private void setButtonsBounds(){
		uploadButton.setBounds(0, 0, 40, 32);
		
	}
	
}
