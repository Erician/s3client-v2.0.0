package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class ComputeFileMD5{
	
	 public static String generate(File file) throws NoSuchAlgorithmException,IOException {
		 
		 FileInputStream fis = new FileInputStream(file);
         MessageDigest md = MessageDigest.getInstance("MD5");
         byte[] buffer = new byte[1024];
         int length = -1;
         while ((length = fis.read(buffer, 0, 1024)) != -1) {
             md.update(buffer, 0, length);
         }
         byte[] hash = md.digest();
	     //小写
	     return DatatypeConverter.printHexBinary(hash).toLowerCase();
	}

	
}