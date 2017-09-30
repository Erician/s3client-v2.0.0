package mytest;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.apigateway.model.GetClientCertificateRequest;
import com.amazonaws.services.machinelearning.model.CreateDataSourceFromS3Request;
import com.amazonaws.services.opsworks.model.SslConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.transform.XmlResponsesSaxParser.ListBucketAnalyticsConfigurationHandler;
import com.amazonaws.services.simplesystemsmanagement.model.MaintenanceWindowExecution;

import interdata.ConfigData;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.junit.Test;
import java.nio.file.WatchEvent;

/**
 * List your Amazon S3 buckets.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class AppTest{
	
//	static String accessKeyId = "AKIAOR6R4VOXURXTMYGQ";
//	static String secretAccessKey = "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";
//	static String endPoint = "https://s3.cn-north-1.amazonaws.com.cn";
	
	 static final String endPoint = "oss-cn-shanghai.aliyuncs.com";
	    static final String accessKeyId= "LTAIaFmMAU6s2kpi";
	    static final String secretAccessKey = "fq543zFAJEjiBJhuSFB4H3WleW0Ckd";
	
	
	public static AmazonS3 getClient() {
    	
    	System.setProperty(SDKGlobalConfiguration.ENABLE_S3_SIGV4_SYSTEM_PROPERTY, "true");
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId,secretAccessKey); 
        
        ClientConfiguration config = new ClientConfiguration();
        config.setProtocol(Protocol.HTTP);
        
        AmazonS3 s3client = new AmazonS3Client(awsCredentials,config);
        s3client.setEndpoint(endPoint);
        S3ClientOptions options = new S3ClientOptions();
        options.withChunkedEncodingDisabled(true); // Must to have
        s3client.setS3ClientOptions(options);
    	return s3client;
    }
	
	@Test
	public void ListBucket() {
		AmazonS3 s3client = getClient();
		System.out.println( s3client.listBuckets());
	}
	
	@Test
	public void listKeys() {
		AmazonS3 s3client = getClient();
		try {
            System.out.println("Listing objects");
            
            final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName("jss-test").withMaxKeys(100);
            ListObjectsV2Result result;
            do {               
               result = s3client.listObjectsV2(req);
               
               for (S3ObjectSummary objectSummary : 
                   result.getObjectSummaries()) {
                   System.out.println(" - " + objectSummary.getKey() + "  " +
                           "(size = " + objectSummary.getSize() + 
                           ")");
               }
               System.out.println("Next Continuation Token : " + result.getNextContinuationToken());
               req.setContinuationToken(result.getNextContinuationToken());
            } while(result.isTruncated() == true ); 
            
         } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, " +
            		"which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, " +
            		"which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
		
		
	}
	@Test
    public void listObject() throws Exception{
//        String accesskeyId = "oKdXffdEaqhHbZrL";
//        String secretAccessKey = "n1NepGg5ZMO9x9M1zgwjnavEdBKDAcUFrsCT9bGu";
//        String endPoint = "s3.cn-north-1.jcloudcs.com";
        //String bucketName = "aws-test";
        
        
        String prefix = new String("/ab");
        
        
        
        String delimiter = "/";
        if (!prefix.endsWith(delimiter)) {
            prefix += delimiter;
        }

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName("oss")
                .withPrefix(prefix);
                //.withDelimiter(delimiter);
        ObjectListing objects = getClient().listObjects(listObjectsRequest);
        
        for (S3ObjectSummary objectSummary : 
          objects.getObjectSummaries()) {
            System.out.println(" - " + objectSummary.getKey() + "  " +
                    "(size = " + objectSummary.getSize() + 
                    ")");
        }
    }
	@Test
	public void getMD5() throws NoSuchAlgorithmException, IOException {
			File file = new File("D:\\oss\\oss\\azure-storage-paper-pdf.pdf");
			FileInputStream fis = new FileInputStream(file);
	         MessageDigest md = MessageDigest.getInstance("MD5");
	         byte[] buffer = new byte[1024];
	         int length = -1;
	         while ((length = fis.read(buffer, 0, 1024)) != -1) {
	             md.update(buffer, 0, length);
	         }
	         byte[] hash = md.digest();
	        System.out.println(DatatypeConverter.printHexBinary(hash).toLowerCase());
	}
	@Test
	public void getObjectEtag() {
		AmazonS3 s3client = getClient();
		String bucketName = "oss";
		String keyName = "azure-storage-paper-pdf.pdf";
		System.out.println(s3client.getObjectMetadata(bucketName, keyName).getETag());
	}
    @Test
    public void putObject() {
    	
    	 String bucketName     = "wangwei-storage1";
    	 String keyName        = "111.txt";
    	 String uploadFileName = "D:\\111.txt";
    	
            AmazonS3 s3client = getClient();
            try {
                System.out.println("Uploading a new object to S3 from a file\n");
                
                //this is putting a folder
                //暂定认为Etag就是MD5，对于多不上传不适用
                s3client.putObject(new PutObjectRequest(bucketName, keyName, new File(uploadFileName)));
                System.out.println(s3client.getObjectMetadata(bucketName, keyName).getETag());

             } catch (AmazonServiceException ase) {
                System.out.println("Caught an AmazonServiceException, which " +
                		"means your request made it " +
                        "to Amazon S3, but was rejected with an error response" +
                        " for some reason.");
                System.out.println("Error Message:    " + ase.getMessage());
                System.out.println("HTTP Status Code: " + ase.getStatusCode());
                System.out.println("AWS Error Code:   " + ase.getErrorCode());
                System.out.println("Error Type:       " + ase.getErrorType());
                System.out.println("Request ID:       " + ase.getRequestId());
            } catch (AmazonClientException ace) {
                System.out.println("Caught an AmazonClientException, which " +
                		"means the client encountered " +
                        "an internal error while trying to " +
                        "communicate with S3, " +
                        "such as not being able to access the network.");
                System.out.println("Error Message: " + ace.getMessage());
            }
        }
    @Test
    public void deleteObject() {
    	String bucketName     = "wangwei-storage1";
   	 	String keyName        = "新文件夹/";
   	 	
    	try {
            getClient().deleteObject(new DeleteObjectRequest(bucketName, keyName));
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
    @Test
    public void listlib() {
    	System.err.println(System.getProperty("java.library.path"));
    }
    @Test
    public void expiry() {
    	Calendar cal = Calendar.getInstance();
    	cal.set(1970, 0, 1, 0, 0, 0);
    	Calendar now = Calendar.getInstance();
    	System.out.println(now.getTimeInMillis()/1000);
    }
    @Test
    public void file() throws IOException {
    	File file = new File("D:/oss/lmy/新建文件夹");
    	BufferedOutputStream buff = new BufferedOutputStream(new FileOutputStream(file));
		buff.write(("SynchronizationDirectory=\n").getBytes());
		buff.close();
    }
    @Test
    public void dir() throws IOException {
    	File file = new File("D:/oss/lmy/mm/hehe.txt");
    	file.mkdirs();
    }
    @Test
    public void filePath() {
    	String path = "D:/23/4.txt";
    	File file = new File(path);
    	System.out.println(file.getParent());
    }
    @Test
    public void testWatchService() throws IOException {
    	final WatchService watcher = FileSystems.getDefault().newWatchService();
    	final HashMap<WatchKey, Path> keys = new HashMap<WatchKey, Path>();
    	Path dir = Paths.get("D:/oss");
    	//我们应该监测所有的文件夹
    	Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
    		 @Override
             public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
    			 //只会遍历文件夹，不会遍历文件
    			 System.out.println("正在访问"+dir);
    			 WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, 
						  StandardWatchEventKinds.ENTRY_DELETE, 
						  StandardWatchEventKinds.ENTRY_MODIFY);
    			 keys.put(key, dir);
                 return FileVisitResult.CONTINUE;
             }
         });
    	/*
    	dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, 
    						  StandardWatchEventKinds.ENTRY_DELETE, 
    						  StandardWatchEventKinds.ENTRY_MODIFY);
    	*/
    	while (true) {
    	    WatchKey key;
    	    try {
    	        // wait for a key to be available
    	        key = watcher.take();
    	    } catch (InterruptedException ex) {
    	        return;
    	    }
    	 
    	    for (WatchEvent<?> event : key.pollEvents()) {
    	        // get event type
    	        WatchEvent.Kind<?> kind = event.kind();
    	 
    	        // get file name
    	        @SuppressWarnings("unchecked")
    	        WatchEvent<Path> ev = (WatchEvent<Path>) event;
    	        Path fileName = ev.context();
    	        
    	        System.out.println(kind.name() + ": " + keys.get(key)+"\\"+fileName);
    	        String absolutePath = keys.get(key)+"\\"+fileName;
    	        File file = new File(absolutePath);
    	 
    	        if (kind == StandardWatchEventKinds.OVERFLOW) {
    	            continue;
    	        } else if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
    	 
    	            // process create event
    	        	System.out.println("create:"+fileName);
    	        	//应该把新的目录注册，监控该新目录下的变化
    	        	
    	        	if(file.isDirectory()) {
    	        		Path dir1 = Paths.get(absolutePath);
    	        		WatchKey key1 = dir1.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, 
    							  StandardWatchEventKinds.ENTRY_DELETE, 
    							  StandardWatchEventKinds.ENTRY_MODIFY);
    	    			 keys.put(key1, dir1);
    	        	}
    	 
    	        } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
    	 
    	            // process delete event
    	        	System.out.println("delete:"+fileName);
    	        	
    	        	//你无法判断删除的是文件还是文件夹
    	        	if(file.isDirectory()) {
    	        		System.out.println("this is a dir");
    	        	}
    	 
    	        } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
    	 
    	            // process modify event
    	        	System.out.println("modify:"+fileName);
    	        }
    	    }
    	 
    	    // IMPORTANT: The key must be reset after processed
    	    boolean valid = key.reset();
            if (!valid) {
                // 移除不可访问的目录
                // 因为有可能目录被移除，就会无法访问
            	System.out.println("dir:"+keys.get(key));
                keys.remove(key);
                
                // 如果待监控的目录都不存在了，就中断执行
                if (keys.isEmpty()) {
                    break;
                }
            }
    	}
    }
    	
   }