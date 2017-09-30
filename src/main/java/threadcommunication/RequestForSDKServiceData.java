package threadcommunication;

public class RequestForSDKServiceData {
	
	public enum OperationName {listBucket,listObject,uploadObject,
		downloadOneObject,putOneFolder,deleteOneObject,getObjectMetaData};
	private OperationName operationName = null;
	String bucketName = "";
	String objectName = "";
	String absolutePath = "";
	//这个主要控制要不要刷新objecttable
	String oldKey = "";
	
	
	public RequestForSDKServiceData(OperationName operationName) {
		// TODO Auto-generated constructor stub
		this.operationName = operationName;
	}
	public RequestForSDKServiceData(OperationName operationName,String bucketName) {
		// TODO Auto-generated constructor stub
		this.operationName = operationName;
		this.bucketName = bucketName;
	}
	public RequestForSDKServiceData(OperationName operationName,String bucketName ,String objectName) {
		// TODO Auto-generated constructor stub
		this.operationName = operationName;
		this.bucketName = bucketName;
		this.objectName = objectName;
	}
	public RequestForSDKServiceData(OperationName operationName,String bucketName ,String objectName,String absolutePath) {
		// TODO Auto-generated constructor stub
		this.operationName = operationName;
		this.bucketName = bucketName;
		this.objectName = objectName;
		this.absolutePath = absolutePath;
	}
	public RequestForSDKServiceData(OperationName operationName,String bucketName ,String objectName,String absolutePath,String oldKey) {
		// TODO Auto-generated constructor stub
		this.operationName = operationName;
		this.bucketName = bucketName;
		this.objectName = objectName;
		this.absolutePath = absolutePath;
		this.oldKey = oldKey;
	}
	
	
	public OperationName getOperationName() {
		return operationName;
	}
	public String getBucketName() {
		return bucketName;
	}
	public String getObjectName() {
		return objectName;
	}
	public String getAbsolutePath() {
		return absolutePath;
	}
	public String getOldKey() {
		return oldKey;
	}
	
}
