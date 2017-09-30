package s3sdk;

import java.util.List;
import com.amazonaws.services.s3.model.Bucket;

import client.TaskTextArea;

public class BucketOperations{
	
	public static List<Bucket> listBucket() {
		
		TaskTextArea.appendText("list buckets");
		return (S3Client.getAmazonS3Client()).listBuckets();
	}

}
