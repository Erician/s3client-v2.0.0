package interdata;

public class InfoForUpdateObjectTable {
	
		public enum Operation {delete,add,update};
		Operation operation = null;
		private String objectName = "";
		private String bucketName = "";
		public InfoForUpdateObjectTable(String bucketName,String objectName,Operation operation) {
			// TODO Auto-generated constructor stub
			this.operation = operation;
			this.objectName = objectName;
			this.bucketName = bucketName;
		}
		public Operation getOperation() {
			return operation;
		}
		public String getObjectName() {
			return objectName;
		}
		public String getBucketName() {
			return bucketName;
		}
}
