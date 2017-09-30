package showmessage;

import java.awt.Color;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import junit.awtui.ProgressBar;

public class ProgressMessage extends JDialog{
	
	static private ProgressBar progressBar = new ProgressBar();
	public ProgressMessage() {
		// TODO Auto-generated constructor stub
		super();
		setComponentPositionAndAttribute();
		add(progressBar);
		
		
	}
	private void setComponentPositionAndAttribute() {
		
		progressBar.setBounds(10, 10, 250, 30);
		
	}

}
