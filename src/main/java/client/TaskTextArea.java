package client;

import javax.swing.JTextArea;
/*
 * 因为多个线程都要使用这个类，所以使用了同步
 */

public class TaskTextArea{
	
	
	
	private static JTextArea taskTextArea = new JTextArea("");
	
	public TaskTextArea(){
		
		setComponentPositionAndAttribute();
		
	}
	private synchronized void setComponentPositionAndAttribute() {
		
		taskTextArea.setEditable(false);
		taskTextArea.setAutoscrolls(true);
	}
	
	public synchronized static JTextArea getTaskTextArea() {
		return taskTextArea;
	}
	
	public synchronized static void setText(String text) {
		
		taskTextArea.setText(text);
		
	}
	
	public synchronized static void appendText(String text) {
		
		taskTextArea.append(text+"\n");
		taskTextArea.setSelectionStart(taskTextArea.getText().length());
		
		
	}
	

}
