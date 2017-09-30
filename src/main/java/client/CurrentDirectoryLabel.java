package client;

import java.awt.Color;

import javax.swing.JLabel;

import com.amazonaws.services.ec2.model.VolumeDetail;
import com.amazonaws.services.elastictranscoder.model.Thumbnails;



public class CurrentDirectoryLabel extends JLabel{
	
	private static String constName = new String("current directory:");
	
	public CurrentDirectoryLabel() {
		// TODO Auto-generated constructor stub
		setText(constName);
		setComponentPositionAndAttribute();
		
	}
	private void setComponentPositionAndAttribute() {
		//不透明
		setOpaque(true);
		setBackground(new Color(0xE1E6F6));
	}
	
	public void setText(String directory) {
		super.setText(constName+directory);
	}

}
