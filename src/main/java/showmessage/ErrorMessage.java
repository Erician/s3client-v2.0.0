package showmessage;

import javax.swing.JOptionPane;

public class ErrorMessage {
	
	public static void showErrorMessage(String message) {
		
		JOptionPane.showConfirmDialog(null, message, "错误",JOptionPane.CLOSED_OPTION);  
		 
	}

}
