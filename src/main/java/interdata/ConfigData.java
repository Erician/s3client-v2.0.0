package interdata;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import com.amazonaws.services.kinesisfirehose.model.transform.RedshiftRetryOptionsJsonUnmarshaller;

import s3sdk.S3Client;


public class ConfigData {
	//AccessKeyId,SecretAccessKey(secretkey),Region(EndPoint)
	static String accessKeyId = "";
	static String secretAccessKey = "";
	//region可以不用
	static String region = "";
	static String endPoint = "";
	
	
	
	//Credentials和Region会保存在文件里
	static File configureFile = new File("configure");
	

	public ConfigData() {
		// TODO Auto-generated constructor stub
	}
	
	public static void setAccessKeyId(String accessKeyId) {
		ConfigData.accessKeyId = accessKeyId;
	}
	public static void setSecretAccessKey(String secretAccessKey) {
		ConfigData.secretAccessKey = secretAccessKey;
	}
	public static void setEndPoint(String endPoint) {
		ConfigData.endPoint = endPoint;
	}
	
	
	
	public static String getAccessKeyId() {
		return ConfigData.accessKeyId;
	}
	public static String getSecretAccessKey() {
		return ConfigData.secretAccessKey;
	}
	public static String getEndPoint() {
		return ConfigData.endPoint;
	}
	
	
	public static Boolean readDataFromFile() throws IOException {
		
		if(configureFile.exists()==false) {
			return false;
		}
		BufferedReader bufferedReader = new BufferedReader(new FileReader(configureFile));
        String str = null;
        int line = 0;
        while ((str = bufferedReader.readLine()) != null && line != 3) {
        	switch (line) {
			case 0:
				ConfigData.accessKeyId = str.substring(str.indexOf("=")+1).trim();
				line++;
				break;
			case 1:
				ConfigData.secretAccessKey = str.substring(str.indexOf("=")+1).trim();
				line++;
				break;
			case 2:
				ConfigData.endPoint = str.substring(str.indexOf("=")+1).trim();
				line++;
				break;
			default:
				break;
			}
        }
        bufferedReader.close();
        return true;
        /*现在觉得这个检查是没有必要的，因为只要不是null就行了。那就都初始化为“”
         * 只是如果没有读取不知道会是什么结果，可以在创建S3Client的时候进行检查
         * S3Client不会在这里创建了
        //创建client必须要检查
        if(isConfigureOk()) {
			if(S3Client.createAmazonS3Client()==false) {
				return false;
			}
			else {
				isS3ClientOk = true;
				return true;
			}
        }
		return false;
		*/
	}
	
	/*
	 * 返回值确定创建客户端是否成功
	 */
	public static void saveConfig() throws IOException {
		BufferedOutputStream buff = new BufferedOutputStream(new FileOutputStream(configureFile));
		buff.write(("AccessKeyId="+accessKeyId+"\n").getBytes());
		buff.write(("SecretAccessKey="+secretAccessKey+"\n").getBytes());
		buff.write(("EndPoint="+endPoint+"\n").getBytes());
		buff.close();
	}
	public static boolean isConfigureOk() {
		if(accessKeyId==null||accessKeyId.equals("")
			||secretAccessKey==null||secretAccessKey.equals("")
			||endPoint==null||endPoint.equals("")) {
			return false;
		}
		return true;
	}
}
