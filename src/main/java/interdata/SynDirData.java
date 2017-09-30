package interdata;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class SynDirData {
	
	//synDir
	static String synDir = "";
	//Credentials和Region会保存在文件里
	static File synchronizationFile = new File("syn");
	
	
	public SynDirData() {
		// TODO Auto-generated constructor stub
	}
	
	public static boolean readDataFromFile() throws IOException {
		
		if(synchronizationFile.exists()==false) {
			
			return false;
		}
		BufferedReader bufferedReader = new BufferedReader(new FileReader(synchronizationFile));
        String str = null;
        int line = 0;
        while ((str = bufferedReader.readLine()) != null && line != 1) {
        	switch (line) {
			case 0:
				SynDirData.synDir = str.substring(str.indexOf("=")+1).trim();
				line++;
				break;
			}
        }
        bufferedReader.close();
        return (new File(synDir)).isDirectory();
	}
	
	public static void setSynDir(String synDir) throws IOException{
		SynDirData.synDir = synDir;
		saveSynDir(synDir);
		
	}
	
	private static void saveSynDir(String synDir) throws IOException {
		
		BufferedOutputStream buff = new BufferedOutputStream(new FileOutputStream(synchronizationFile));
		buff.write(("SynchronizationDirectory="+synDir+"\n").getBytes());
		buff.close();
		
	}
	
	public static String getSynDir() {
		return SynDirData.synDir;
	}
	public static boolean isSynDirOk() {
		return (new File(synDir)).isDirectory();
	}
	
	

}
