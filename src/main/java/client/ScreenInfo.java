package client;

public class ScreenInfo {
	
	public static int getScreenWidth(){
		return ((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().width);
	}
	public static int getScreenHeight(){
		return ((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);
	}
	public static float getWidthFactor(){
		return (float) (getScreenWidth()*1.0/1920);
	}
	public static float getHeightFactor(){
		return (float)(getScreenHeight()*1.0/1080); 
	}
	
}
