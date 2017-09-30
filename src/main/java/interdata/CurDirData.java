package interdata;

import org.omg.CORBA.Current;

import client.CurrentDirectoryLabel;
import client.MainFrame;

public class CurDirData {
	
	private  String curBucket = "";
	private  String curKey = "";
	
	public synchronized  void setCurBucket(String curBucket) {
			
		this.curBucket = curBucket;
		
	}
	
	public synchronized  void setCurKey(String curKey) {
		this.curKey = curKey;
	}
	
	public synchronized  String getCurBucket() {
		return this.curBucket;
	}
	public synchronized  String getCurKey() {
		return this.curKey;
	}
	
	public synchronized  String getCurDir() {
		
		if(curBucket == null || curBucket.equals("")) {
			return "/";
		}
		else if(curKey ==  null||curKey.equals("")) {
			return "/"+curBucket+"/";
		}
		else {
			return "/"+curBucket+"/"+curKey;
		}
		
	}
	
	

}
