package service;

import threadcommunication.RequestForSDKServiceData;

import com.amazonaws.services.support.model.Communication;

import client.ObjectTable;
import interdata.CurDirData;
import interdata.ListObjectInDirectoryData;
import threadcommunication.RequestForSDKServiceBlockingQueue;
import s3sdk.BucketOperations;
import s3sdk.ObjectOperations;

/*
 * service接受上层的调用，目前仅支持上传和下载object
 */
public class SDKService implements Runnable{
	
	//用于自动刷新的数据
	CurDirData curDirData = null;
	ListObjectInDirectoryData listObjectInDirectoryData = null;
	ObjectTable objectTable = null;
	
	public SDKService(CurDirData curDirData,
			ListObjectInDirectoryData listObjectInDirectoryData, ObjectTable objectTable) {
		
		this.curDirData = curDirData;
		this.listObjectInDirectoryData = listObjectInDirectoryData;
		this.objectTable = objectTable;
		
	}
	public void run() {
		// TODO Auto-generated method stub
		try {
			while(!Thread.interrupted()) {
				
				RequestForSDKServiceData communicateData = RequestForSDKServiceBlockingQueue.getLinkedBlockingQueueData().take();
				switch(communicateData.getOperationName()) {
				case listBucket:
					break;
					
				case listObject:
					break;
					
				case uploadObject:
					ObjectOperations.uploadObject(communicateData.getBucketName(), 
							communicateData.getObjectName(),
							communicateData.getAbsolutePath());
					//自动刷新
					addFresh(communicateData,communicateData.getOldKey());
					break;
					
				case downloadOneObject:
					ObjectOperations.downloadOneObject(communicateData.getBucketName(), 
							communicateData.getObjectName(), communicateData.getAbsolutePath());
					break;
					
				case putOneFolder:
					ObjectOperations.putOneFolder(communicateData.getBucketName(), 
							communicateData.getObjectName(),
							communicateData.getAbsolutePath());
					addFresh(communicateData,communicateData.getOldKey());
					
					break;
				
				case deleteOneObject:
					//System.out.println("deleteoneobject "+communicateData.getObjectName());
					ObjectOperations.deleteOneObject(communicateData.getBucketName(), communicateData.getObjectName());
					deleteFresh(communicateData,communicateData.getOldKey());
					
					break;
					
				case getObjectMetaData:
					
					break;
					
				default:
					break;
					
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void deleteFresh(RequestForSDKServiceData communicateData,String oldKey) {
		//把以communicateData.getobjectname开头的全部删除
		synchronized (curDirData) {
			synchronized (listObjectInDirectoryData) {
			
			//System.out.println("start deleteObjectInDirectory");
			
			listObjectInDirectoryData.deleteObjectInDirectory(communicateData.getBucketName(),
					communicateData.getObjectName());
				
			//System.out.println("end deleteObjectInDirectory");
			if(communicateData.getBucketName().equals(curDirData.getCurBucket())) {
				
				//更新objecttable
				//System.out.println("curKey:"+curDirData.getCurKey()+","+"oldKey:"+oldKey);
				listObjectInDirectoryData.deleteOneDirectory(communicateData.getObjectName());
				//System.out.println("in");
				//这里最好是原子操作，以后需要修改
				//目录相同才有必要刷新显示
				if(curDirData.getCurKey().equals(oldKey)) {
					
					//更新显示
					updateObjectTable();
				}
				
			}
			}
		}

	}
	
	private void addFresh(RequestForSDKServiceData communicateData,String oldKey) {
		
		synchronized (curDirData) {
			synchronized (listObjectInDirectoryData) {
				
			if(communicateData.getBucketName().equals(curDirData.getCurBucket())) {
				String [] rowData = ObjectOperations.getObjectMetaData(
						communicateData.getBucketName(), communicateData.getObjectName());
				
				if(rowData == null ) {
					return;
				}
				listObjectInDirectoryData.addRowData(rowData);
				//这里最好是原子操作，以后需要修改
				//目录相同才有必要刷新显示
				
				//if(curDirData.getCurKey().equals(oldKey)) {
				//直接刷新到当前的key应该也没有问题
				updateObjectTable();
				}
			}
		}
	}
	
	private void updateObjectTable() {
		
		//先要go back to root
		listObjectInDirectoryData.goBackToRoot();
		String curKey = curDirData.getCurKey();
		//System.out.println(curKey);
		String subCurKey = curKey;
		String key = "";
		int delimiterPos = 0;
		
		curDirData.setCurKey("");
		while((delimiterPos = subCurKey.indexOf("/")) != -1) {
			
			key = subCurKey.substring(0, delimiterPos+1);
			subCurKey = subCurKey.substring(delimiterPos+1,subCurKey.length());
			listObjectInDirectoryData.goToNextLevel(key);
		}
		curDirData.setCurKey(curKey);
		objectTable.setObjectTableModeDataAndUpdateUI();	
	}

}
