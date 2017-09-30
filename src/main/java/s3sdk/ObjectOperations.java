package s3sdk;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.applicationdiscovery.model.GetDiscoverySummaryRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import client.TaskTextArea;
import interdata.CurDirData;
import showmessage.ErrorMessage;

public class ObjectOperations {
	
	public static  ArrayList<String[]> listObject(String bucketName) {
		
		//TaskTextArea.appendText("list objects -bucketname "+bucketName);
		
		try {
			
			ArrayList<String[]> list = new ArrayList<String[]>();
            final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withMaxKeys(10000);
            ListObjectsV2Result result;
            do {               
               result = S3Client.getAmazonS3Client().listObjectsV2(req);
             
               for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
            	  
            	   String[] rowData = {objectSummary.getKey(),
            			   objectSummary.getOwner()==null?"not defined":objectSummary.getOwner()+"",
            			   String.valueOf(objectSummary.getSize()),
            			   objectSummary.getLastModified()+"",
            			   objectSummary.getStorageClass()};
                   list.add(rowData);
                   //System.out.println(rowData[0]);
               }
               req.setContinuationToken(result.getNextContinuationToken());
            } while(result.isTruncated() == true ); 
			return list;
            
         } catch (AmazonServiceException ase) {
        	 ErrorMessage.showErrorMessage("Listing object failed!");
            
        } catch (AmazonClientException ace) {
        	ErrorMessage.showErrorMessage("Listing object failed!");
        }
		
		//TaskTextArea.appendText("    -list objects failed");
		
		return null;	
	}
	public static void uploadObject(String bucketName,String keyName,String absolutePath){
		
		
		File file = new File(absolutePath);
		
		
		
		TaskTextArea.appendText("put object -bucketname "+bucketName+" -key "+keyName+" -file "+absolutePath);
		
		AmazonS3 s3client = S3Client.getAmazonS3Client();
        try {
        	//这里要加一个原子操作
        	
        	if(file.exists()) {
        		s3client.putObject(new PutObjectRequest(bucketName, keyName, file));
        	}
        	return ;

         } catch (AmazonServiceException ase) {
        	 ErrorMessage.showErrorMessage("Upload object failed!");
        } catch (AmazonClientException ace) {
        	ErrorMessage.showErrorMessage("Upload object failed!");
        }
        
        TaskTextArea.appendText("    -put object failed");
        
		return ;
	}
	public static void downloadOneObject(String bucketName,String key,String fileName) throws IOException {
		
		TaskTextArea.appendText("get object -bucketname "+bucketName+" -key "+key
				+" -o " + fileName);
		
		AmazonS3 s3Client = S3Client.getAmazonS3Client();
        try {
            File file = new File(fileName);
            File parent = new File(file.getParent());
            
    		if(parent.exists()== false) {
    			parent.mkdirs();
    		}
        
            S3Object s3object = s3Client.getObject(new GetObjectRequest(bucketName, key));
            BufferedReader reader = new BufferedReader(new InputStreamReader(s3object.getObjectContent()));
            
            BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
            int read = -1;
            while ( ( read = reader.read() ) != -1 ) {
                writer.write(read);
            }
            writer.flush();
            writer.close();
            reader.close();
            
            return;

        } catch (AmazonServiceException ase) {
        	ErrorMessage.showErrorMessage("download object failed!");
        } catch (AmazonClientException ace) {
        	ErrorMessage.showErrorMessage("download object failed!");
        }
        
        TaskTextArea.appendText("    -get object failed");

	}
	public static void putOneFolder(String bucketName,String keyName,String absolutePath) {
		
		if(absolutePath == null||absolutePath.equals("")) {
			TaskTextArea.appendText("put folder -bucketname "+bucketName+" -key "+keyName);
		}
		else {
			TaskTextArea.appendText("put folder -bucketname "+bucketName+" -key "+keyName+
					" -folder "+ absolutePath);
		}
		
		AmazonS3 s3client = S3Client.getAmazonS3Client();
		try {
			InputStream input = new ByteArrayInputStream(new byte[0]);
	        ObjectMetadata metadata = new ObjectMetadata();
	        metadata.setContentLength(0);
	        s3client.putObject(new PutObjectRequest(bucketName, keyName, input,metadata));
	        
	        return;
		}catch (Exception e) {
			// TODO: handle exception
			ErrorMessage.showErrorMessage("put folder failed!");
		}
		TaskTextArea.appendText("    -put folder failed");
	}
	/*
	 * 强调一下，一次删除一个文件
	 */
	public static void deleteOneObject(String bucketName,String keyName) {
		
		TaskTextArea.appendText("delete object -bucketname "+bucketName+" -key "+keyName);
		
		AmazonS3 s3Client = S3Client.getAmazonS3Client();
		try {
			if(doesObjectExist(bucketName, keyName)) {
				s3Client.deleteObject(new DeleteObjectRequest(bucketName, keyName));
			}
            return;
        } catch (AmazonServiceException ase) {
        	ErrorMessage.showErrorMessage("delete object failed!");
        } catch (AmazonClientException ace) {
        	ErrorMessage.showErrorMessage("delete object failed!");
        }

		//TaskTextArea.appendText("    -delete object failed");
		
	}
	public static String[] getObjectMetaData(String bucketName,String keyName){
		
		//TaskTextArea.appendText("get metadata -bucketname "+bucketName+" -key "+keyName);
		AmazonS3 s3client = S3Client.getAmazonS3Client();
		
        try {
         
        	ObjectMetadata objectMetadata = s3client.getObjectMetadata(bucketName, keyName);
        	
        	//增加MD5
        	String[] rowData = {keyName,
     			   "not known",
     			   String.valueOf(objectMetadata.getContentLength()),
     			   objectMetadata.getLastModified()+"",
     			   objectMetadata.getStorageClass()==null?"not known":objectMetadata.getStorageClass(),
     			   objectMetadata.getETag()};
        	return rowData;
         } catch (AmazonServiceException ase) {
        	 ErrorMessage.showErrorMessage("get object metadata failed!");
        } catch (AmazonClientException ace) {
        	ErrorMessage.showErrorMessage("get object metadata failed!");
        }
		return null;
	}
	
	public static boolean doesObjectExist(String bucketName,String keyName){
		AmazonS3 s3client = S3Client.getAmazonS3Client();
		
        try { 
        	return s3client.doesObjectExist(bucketName, keyName);

         } catch (AmazonServiceException ase) {
        	 ErrorMessage.showErrorMessage("head object  failed!");
        } catch (AmazonClientException ace) {
        	ErrorMessage.showErrorMessage("head object  failed!");
        }
		return false;
	}
}
