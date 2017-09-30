package showmessage;

import javax.swing.JOptionPane;

public class WarningMessage {
	
	public static void showWarningMessage(String message) {
		
		JOptionPane.showMessageDialog(null, message, "warning",JOptionPane.WARNING_MESSAGE);  
	}

}
