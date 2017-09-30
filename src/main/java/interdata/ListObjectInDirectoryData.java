package interdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.naming.InitialContext;

import com.amazonaws.services.clouddirectory.model.GetObjectInformationRequest;

import client.CurrentDirectoryLabel;
import s3sdk.ObjectOperations;

/*
 * 该类的操作是和当前的bucket目录相关的
 */

public class ListObjectInDirectoryData {
	
	private  HashMap<String, String[]> objectInfo = new HashMap<String, String[]>();
	private  ArrayList<String> objectName = new ArrayList<String>();
	private  HashSet<String> curListObject = null;
 	private  Stack<HashSet<String>>  oldListObjetc = new Stack<HashSet<String>>();
 	
 	private CurDirData curDirData = null;
	
	public synchronized  void init(ArrayList<String[]> listObject) {
		
		curListObject = new HashSet<String>();
		objectInfo.clear();
		objectName.clear();
		curListObject.clear();
		oldListObjetc.clear();
		
		if(listObject == null||listObject.size() == 0) {
			return;
		}
		
		
		for(int i=0;i<listObject.size();i++) {
			
			objectName.add(listObject.get(i)[0]);
			objectInfo.put(objectName.get(i), listObject.get(i));
			String tmp = objectName.get(i);
			int pos;
			if((pos=tmp.indexOf("/"))==-1) {
				//this is a file
				curListObject.add(tmp);
			}
			else {
				//this is a directory
				curListObject.add(tmp.substring(0, pos+1));
			}
		}
		Collections.sort(objectName);
	}
	public synchronized  HashMap<String, String[]> getObjectInfo() {
		
		return this.objectInfo;
		
	}
	public synchronized  HashSet<String> getCurListObject() {
		
		return this.curListObject;
		
	}
	public synchronized  void goBackToRoot() {
		oldListObjetc.clear();
		curListObject = new HashSet<String>();
		
		for(int i=0;i<objectName.size();i++) {
			
			String tmp = objectName.get(i);
			int pos;
			if((pos=tmp.indexOf("/"))==-1) {
				//this is a file
				curListObject.add(tmp);
			}
			else {
				//this is a directory
				curListObject.add(tmp.substring(0, pos+1));
			}
		}
	}

	public synchronized void addRowData(String[] rowData) {
		
		objectInfo.put(rowData[0], rowData);
		objectName.add(rowData[0]);
		Collections.sort(objectName);
		//goBackToRoot();
	}
	
	public synchronized  ArrayList<String> getFileNameInOneDirectory(String key) {
		
		//这里有一种情况，目录上显示了，但是实际云存储中并没有这个文件名，尤其是下载的时候会失败，将在objectOpearton中解决
		
		int dirPosInObjectName = binarySearchStartWith(0, objectName.size()-1, key);
		
		ArrayList<String> fileNames = new ArrayList<String>();
		if(dirPosInObjectName == -1) {
			return fileNames;
		}
		//在objectname中删除
		//向下找,并删除
		int i = dirPosInObjectName;
		
		while(objectName.isEmpty() == false && i<objectName.size()) {
			
			if(objectName.get(i).startsWith(key)) {
				fileNames.add(objectName.get(i));
				i++;
			}
			else {
				break;
			}
		}
		//向上找,并删除
		i = dirPosInObjectName-1;
		while(objectName.isEmpty() == false && i>=0) {
			
			if(objectName.get(i).startsWith(key)) {
				fileNames.add(objectName.get(i));
				i--;
			}
			else {
				break;
			}
		}
		return fileNames;
	}
	
	/*
	 * just delete one file
	 */
	public synchronized  void deleteOneFile(String key) {
		
		if(objectInfo.containsKey(key) == true) {
			objectInfo.remove(key);
		}
		int filePosInObjectName = binarySearch(0, objectName.size()-1, key);
		if(filePosInObjectName != -1) {
			objectName.remove(filePosInObjectName);
			Collections.sort(objectName);
			goBackToRoot();
		}
	}
	/*
	 * 把目录下的所有文件都删除
	 */
	public  void deleteOneDirectory(String key) {
		
		int dirPosInObjectName = binarySearchStartWith(0, objectName.size()-1, key);
		
		//System.out.println(key + "pos:"+dirPosInObjectName);
		
		if(dirPosInObjectName == -1) {
			return;
		}
		
		ArrayList<String> fileNameToDelete = new ArrayList<String>();
		
		//在objectname中删除
		//向下找,并删除
		int i = dirPosInObjectName;
		while(objectName.isEmpty() == false && i<objectName.size()) {
			//System.out.print("this is while");
			
			if(objectName.get(i).startsWith(key)) {
				fileNameToDelete.add(objectName.get(i));
				objectName.remove(i);
				
			}
			else {
				break;
			}
		}
		//向上找,并删除
		i = dirPosInObjectName-1;
		while(objectName.isEmpty() == false && i>=0) {
			
			if(objectName.get(i).startsWith(key)) {
				fileNameToDelete.add(objectName.get(i));
				objectName.remove(i);
				i--;
			}
			else {
				break;
			}
		}
		Collections.sort(objectName);
		
		//在objectInfo中删除
		for(i=0;i<fileNameToDelete.size();i++) {
			if(objectInfo.containsKey(fileNameToDelete.get(i))) {
				objectInfo.remove(fileNameToDelete.get(i));
			}
		}
		
		goBackToRoot();
	}
	
	public  void backToLastLevel() {
		if(oldListObjetc.isEmpty() == true) {
			return;
		}
		
		curListObject = oldListObjetc.peek();
		
		oldListObjetc.pop();
		String curKey = curDirData.getCurKey();
		int lastDelimiterPos = curKey.substring(0,curKey.length()-1).lastIndexOf("/");
		if(lastDelimiterPos == -1) {
			curDirData.setCurKey("");
		}
		else {
			curDirData.setCurKey(curKey.substring(0,lastDelimiterPos+1));
		}
		
	}
	public void goToNextLevel(String key) {
		
		String curKey = curDirData.getCurKey();
		if(curKey == null || curKey.equals("")) {
			curKey = key;
		}
		else {
			curKey = curKey+key;
		}
		//set curKey
		curDirData.setCurKey(curKey);
		//System.out.print("curKey:"+curKey);
		int onePos = binarySearchStartWith(0,objectName.size()-1,curKey);
		
		//construct the curListObject
		//保存上一级的显示
		oldListObjetc.push(curListObject);
		//-1，别忘了刷新显示。bug在这
		if(onePos == -1) {
			curListObject = new HashSet<String>();
		}
		else {
			setCurListObject(onePos,curKey);
		}
		
	}
	
	private  void setCurListObject(int pos,String curKey){
		
		curListObject = new HashSet<String>();
		
		int nextDelimiterPos;
		String tmpString;
		String subString;
		//向下找
		for(int i=pos;i<objectName.size();i++) {
			tmpString = objectName.get(i);
			if(tmpString.startsWith(curKey)==false) {
				break;
			}
			//下一个'/'位置
			subString = tmpString.substring(curKey.length(),tmpString.length());
			
			if(subString.equals("")) {
				continue;
			}
			
			nextDelimiterPos = subString.indexOf("/");
			if(nextDelimiterPos == -1) {
				//this is a file
				curListObject.add(subString);
				
			}
			else {
				curListObject.add(subString.substring(0,nextDelimiterPos+1));
			}
		}
		//向上找
		for(int i=pos-1;i>=0;i--) {
			tmpString = objectName.get(i);
			if(tmpString.startsWith(curKey)==false) {
				break;
			}
			//下一个'/'位置
			subString = tmpString.substring(curKey.length(),tmpString.length());
			
			if(subString.equals("")) {
				continue;
			}
			
			nextDelimiterPos = subString.indexOf("/");
			if(nextDelimiterPos == -1) {
				//this is a file
				curListObject.add(subString);
				
			}
			else {
				curListObject.add(subString.substring(0,nextDelimiterPos+1));
			}
		}
		
	}
	/*
	 * 这个不是等于curkey的搜索，只要以curkey开头就可以
	 */
	private  int binarySearchStartWith(int head,int tail,String curKey) {
		
		int middle;
		
		while(head<=tail) {
			
			middle = (head+tail)/2;

			if(objectName.get(middle).startsWith(curKey)) {
				return middle;
			}
			else if(objectName.get(middle).compareTo(curKey)<0) {
				head = middle+1;
			}
			else {
				tail = middle-1;
			}
		}
		return -1;
	}
	/*
	 * 精确搜索，必须等于curKey
	 */
	private int binarySearch(int head,int tail,String curKey) {
		
		int middle;
		
		while(head<=tail) {
			
			middle = (head+tail)/2;

			if(objectName.get(middle).startsWith(curKey)) {
				return middle;
			}
			else if(objectName.get(middle).compareTo(curKey)<0) {
				head = middle+1;
			}
			else {
				tail = middle-1;
			}
		}
		return -1;
	}
	/*
	 * 工具，SynDirService使用,删除云存储中的object
	 */
	@SuppressWarnings("unchecked")
	public  void deleteObjectInDirectory(String bucketName,String key) {
		
		ArrayList<String[]> objectsMetadata = ObjectOperations.listObject(bucketName);
		//System.out.println("end listobjects");
		Collections.sort(objectsMetadata, new MyCompartor());
		
		//for(int i=0;i<objectsMetadata.size();i++) {
		//	System.out.println(objectsMetadata.get(i)[0]);
		//}
		
		//System.out.println("end sort");
		int pos = binarySearch(objectsMetadata, key);
		//System.out.println("end binarysearch");
		//System.out.println(pos+"");
		if(pos == -1) {
			return;
		}
		//向下找,并删除
		int i = pos;
		while(objectsMetadata.isEmpty() == false && i<objectsMetadata.size()) {
			//System.out.println("while ");
			if((objectsMetadata.get(i))[0].startsWith(key)) {
				//System.out.println((objectsMetadata.get(i))[0]);
				
				ObjectOperations.deleteOneObject(bucketName, (objectsMetadata.get(i))[0]);
				i++;
			}
			else {
				break;
			}
		}
		//向上找,并删除
		i = pos-1;
		while(objectName.isEmpty() == false && i>=0) {
			//System.out.println("while ");
			if((objectsMetadata.get(i))[0].startsWith(key)) {
				//System.out.println((objectsMetadata.get(i))[0]);
				ObjectOperations.deleteOneObject(bucketName, (objectsMetadata.get(i))[0]);
				i--;
			}
			else {
				break;
			}
		}	
		
	}
	/*
	 * 只有deleteObjectInDirectory使用
	 */
	
	private  int binarySearch(ArrayList<String[]> objectsMetadata,String curKey) {
		
		int head = 0;
		int tail = objectsMetadata.size()-1;
		
		int middle = 0;
		
		while(head<=tail) {
			
			//System.out.println(head+" "+middle+" "+tail);
			middle = (head+tail)/2;

			if((objectsMetadata.get(middle))[0].startsWith(curKey)) {
				return middle;
			}
			else if((objectsMetadata.get(middle))[0].compareTo(curKey)<0) {
				head = middle+1;
			}
			else {
				tail = middle-1;
			}
		}
		return -1;
	}
	public  class MyCompartor implements Comparator {

		public int compare(Object o1, Object o2){
			
			String []
				str1 = (String[])o1,
				str2 = (String[])o2;
			return str1[0].compareTo(str2[0]);

	   }

	}
	
	public void setCurDirData(CurDirData curDirData) {
		this.curDirData = curDirData;
	}

}
