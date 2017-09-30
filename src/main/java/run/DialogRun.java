package run;

import java.awt.Toolkit;
import javax.swing.*;

public class DialogRun {
	public static void run(final JDialog d,final int width,final int height){
		//不再单独开线程
				// TODO Auto-generated method stub
				try{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				}
				catch(Exception e){
					throw new RuntimeException(e);
				}
				
				//d.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
				d.setSize(width, height);
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				int x = (int)(toolkit.getScreenSize().getWidth()-d.getWidth())/2;
				int y = (int)(toolkit.getScreenSize().getHeight()-d.getHeight())/2;
				//d.setTitle(d.getClass().getSimpleName());
				d.setLocation(x, y);
				//父窗口不可点击
				d.setModal(true);
				d.setVisible(true);
	}
	
}

