package client;

import java.awt.Font;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import interdata.CurDirData;
import interdata.ListObjectInDirectoryData;
import interdata.ObjectTableModel;
import service.SDKService;
import service.SynDirService;
import showmessage.ErrorMessage;

public class App {
	
	//isSynDirChanged用于控制同步本地数据和云存储数据，只有当同步目录改变的，才被设为true
	//SynDirService会判断这个变量的值，对目录增加监视
	//此参数不再使用
	//public volatile static boolean isSynDirChanged = false;
	
	public static void main(String[] args) throws IOException {
		
		
		final int SIZE = 1;
		InitGlobalFont(new Font("TimesRoman", Font.PLAIN, (int) Math.floor(18*ScreenInfo.getHeightFactor()))); 
		//
		//countDownLatch这个用于关闭该主窗口时，通知线程池关闭
		CountDownLatch countDownLatch = new CountDownLatch(SIZE);
		
		ExecutorService executorService = Executors.newCachedThreadPool();
		
		
		//用于自动刷新的数据
		CurDirData curDirData = new CurDirData();
		ListObjectInDirectoryData listObjectInDirectoryData = new ListObjectInDirectoryData();
		ObjectTable objectTable = new ObjectTable(new ObjectTableModel());
		//
		
		executorService.execute((Runnable) new SDKService(curDirData,listObjectInDirectoryData,
				objectTable));
		executorService.execute(new SynDirService(listObjectInDirectoryData));
		executorService.execute(new MainFrame(countDownLatch,curDirData,listObjectInDirectoryData,
				objectTable));
		try {
			countDownLatch.await();
			executorService.shutdown();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			ErrorMessage.showErrorMessage("close thread failed");
		}
	}
	private static void InitGlobalFont(Font font) {  
		  FontUIResource fontRes = new FontUIResource(font);  
		  for (Enumeration<Object> keys = UIManager.getDefaults().keys();keys.hasMoreElements(); ) { 
		      Object key = keys.nextElement();  
		      Object value = UIManager.get(key);  
		      if (value instanceof FontUIResource) {  
			      UIManager.put(key, fontRes);  
		      }  
		  } 
	}
}
