package s3sdk;

import java.util.List;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.Bucket;

import client.TaskTextArea;
import interdata.ConfigData;

public class S3Client {
	
	private static AmazonS3 s3Client = null;
	
	public S3Client() {
		// TODO Auto-generated constructor stub
	}
	/*
	 * boolean现在并没有什么用，先留着
	 */
	public static boolean createAmazonS3Client(){
		
		TaskTextArea.appendText("create s3client");
		
		
		System.setProperty(SDKGlobalConfiguration.ENABLE_S3_SIGV4_SYSTEM_PROPERTY, "true");
        AWSCredentials awsCredentials = new BasicAWSCredentials(ConfigData.getAccessKeyId(),ConfigData.getSecretAccessKey()); 
        
        ClientConfiguration config = new ClientConfiguration();
        config.setProtocol(Protocol.HTTP);
        
        s3Client = new AmazonS3Client(awsCredentials,config);
        
//        System.out.println(ConfigData.getAccessKeyId());
//        System.out.println(ConfigData.getSecretAccessKey());
//        System.out.println(ConfigData.getEndPoint());
        
        s3Client.setEndpoint(ConfigData.getEndPoint());
       
        S3ClientOptions options = new S3ClientOptions();
        options.withChunkedEncodingDisabled(true); // Must to have
        s3Client.setS3ClientOptions(options);
        
		return true;
	}

	public static AmazonS3 getAmazonS3Client() {
		return s3Client;
	}

}
