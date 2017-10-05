package run;

import java.awt.Toolkit;

import javax.swing.*;
public class FrameRun {
	public static void run(final JFrame f,final int width,final int height){
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				
				try{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
				catch(Exception e){
					throw new RuntimeException(e);
				}
				
				
				f.setTitle(f.getClass().getSimpleName());
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(width, height);
				//
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				int x = (int)(toolkit.getScreenSize().getWidth()-f.getWidth())/2;
				int y = (int)(toolkit.getScreenSize().getHeight()-f.getHeight())/2;
				f.setLocation(x, y);
				/*
				f.getContentPane().setBackground(Color.white);
				f.getContentPane().setVisible(true);
				*/
				f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				f.setTitle("S3Client");
				f.setVisible(true);
				
			}
		});
	}
	
}


