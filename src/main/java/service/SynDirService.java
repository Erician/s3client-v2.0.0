package service;

import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;
import s3sdk.ObjectOperations;
import showmessage.ErrorMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;



import utils.ComputeFileMD5;
import client.App;
import client.TaskTextArea;
import interdata.CurDirData;
import interdata.ListObjectInDirectoryData;
import threadcommunication.RequestForSDKServiceBlockingQueue;
import threadcommunication.RequestForSDKServiceData;
import interdata.SynDirData;
import interdata.InfoForUpdateObjectTable.Operation;
import threadcommunication.RequestForSDKServiceData.OperationName;
import threadcommunication.SynDirBlockingQueue;


public class SynDirService implements Runnable {
	
	private ListObjectInDirectoryData listObjectInDirectoryData = null;
	

	private WatchService watcher = null;
	private HashMap<WatchKey, Path> keys = null;
	
	public SynDirService(ListObjectInDirectoryData listObjectInDirectoryData) {
		this.listObjectInDirectoryData = listObjectInDirectoryData;
		
	}
	
	/*
	 * watchservice并不好用，换回JNotify
	public void run() {
		// TODO Auto-generated method stub
		try {
			while(!Thread.interrupted()) {
				
				System.out.println("begin");
				if(App.isSynDirChanged) {
					
					App.isSynDirChanged = false;
					//关闭上一次的监控
					if(watcher != null) {
						watcher.close();
					}
					watcher = FileSystems.getDefault().newWatchService();
					keys = new HashMap<WatchKey, Path>();
					Path start = Paths.get(SynDirData.getSynDir());
					//我们应该监测文件夹下的所有文件
			    	registerAll(start);
				}
				if(watcher == null) {
					continue;
				}
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
	    	        
	    	        String parentFolder = keys.get(key)+"";
	    	        parentFolder = parentFolder.replace('\\', '/');
	    	        String absolutePath = parentFolder+"/"+fileName;
	    	        System.out.println("absolutePath:"+absolutePath);
	    	        File file = new File(absolutePath);

	    	        		
	    	        if (kind == StandardWatchEventKinds.OVERFLOW) {
	    	            continue;
	    	        } else if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
	    	 
	    	            // process create event
	    	        	System.out.println("create:"+fileName);
	    	        	//keyname就是key
		    	        String bucketAndKey =parentFolder.substring(SynDirData.getSynDir().length()+1)+"/"+fileName;
		    	        String bucketName = bucketAndKey.substring(0,bucketAndKey.indexOf('/'));
		    	        String keyName = bucketAndKey.substring(bucketName.length()+1);
	    	        	//应该把新的目录注册，监控该新目录下的变化
	    	        	if(file.isDirectory()) { 
	    	        		System.out.println("register:"+absolutePath);
	    	        		Path dir1 = Paths.get(absolutePath);
	    	        		WatchKey key1 = dir1.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, 
	    							  StandardWatchEventKinds.ENTRY_DELETE, 
	    							  StandardWatchEventKinds.ENTRY_MODIFY);
	    	    			 keys.put(key1, dir1);
	    	    			 uploadFolder(bucketName, keyName);
	    	        	}
	    	        	else {
	    	        		uploadFile(bucketName, keyName, file);
	    	        	}
	    	 
	    	        } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
	    	 
	    	            // process delete event
	    	        	System.out.println("delete:"+fileName);
	    	        	//我没有办法区分删除的是文件还是文件夹，所以只能把以这个为前缀的全部删除
	    	        	//deleteFile(bucketName, keyName, file);
	    	 
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
			System.out.println("it is over");
			watcher.close();
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
    private void registerAll(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, 
        		StandardWatchEventKinds.ENTRY_CREATE, 
        		StandardWatchEventKinds.ENTRY_DELETE, 
        		StandardWatchEventKinds.ENTRY_MODIFY);
        keys.put(key, dir);
    }
	*/
	//判断是不是第一次触发的，例如：filecreate出发后，往往还会触发filemodify，
	int cnt = 0;
	String bucketKey = "";

	public void run() {
		// TODO Auto-generated method stub
		int watchID = -1;
		try {
			while(!Thread.interrupted()) {
				String synPath = SynDirBlockingQueue.getLinkedBlockingQueueData().take();
				//更新pathAndEtag
				removeWatch(watchID);
				//addWatch的返回值还不太清楚
				watchID = addWatch(synPath);
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	private boolean removeWatch(int watchID) {
		try {
			return JNotify.removeWatch(watchID);
		} catch (JNotifyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;  
	}
	private int addWatch(String synPath) {
		
		int mask = JNotify.FILE_CREATED | JNotify.FILE_DELETED | JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED;
		//int mask = JNotify.FILE_DELETED | JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED ;  
        // 是否监视子目录  
        boolean watchSubtree = true;   
        try{  
        	int watchID = JNotify.addWatch(synPath, mask, watchSubtree, new Listener());
        	return watchID;
        }catch(Exception e)  {  
            ;  
        }
		return -1;  
		
	}
	public  class Listener implements JNotifyListener {
        public void fileRenamed(int wd, String rootPath, String oldName, String newName)  
        {  
        	System.out.println("renamed " + rootPath + " : " + oldName + " -> " + newName);
            
        	//改名字会很耗流量，会把被修改的文件名下的所有文件和文件夹都上传
            //把老的文件名删去
            fileDeleted(wd,rootPath,oldName);
            //加上新的
            String name = newName;
            name = name.replace('\\', '/');
            String bucketName = name.substring(0,name.indexOf("/"));
            String keyName = name.substring(name.indexOf("/")+1,name.length());
            String absoluteFilePath = rootPath.replace('\\', '/')+"/"+name;
            File file = new File(absoluteFilePath);
            
            if(file.isFile()) {
            	try {
					uploadFile(bucketName, keyName, file);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            else {
            	try {
            		//这里的keyname应该是上一级的目录
            		
            		//一个/，说明直接在bucket下面
            		if(name.indexOf("/") == name.lastIndexOf("/")) {
            			keyName = "";
            		}
            		else {
            			keyName = name.substring(name.indexOf("/")+1,name.lastIndexOf("/"));
            		}
					uploadDir(bucketName,keyName,file,file.getName());
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
  
        public void fileModified(int wd, String rootPath, String name)
        {
        	//当编辑文件再保存时，这个函数会被调用两次，并且name只是文件名	
        	System.out.println("modified " + rootPath + " : " + name);  
            
            name = name.replace('\\', '/');
            String bucketName = name.substring(0,name.indexOf("/"));
            String keyName = name.substring(name.indexOf("/")+1,name.length());
            String absoluteFilePath = rootPath.replace('\\', '/')+"/"+name;
            File file = new File(absoluteFilePath);
            
            
            if(file.isFile()) {
            	try {
            		//
            		if(cnt == 0) {
            			bucketKey = bucketName+keyName;
            			cnt++;
            		}
            		else {
            			if(bucketKey.equals(bucketName+keyName)) {
            				uploadFile(bucketName, keyName, file);
                			cnt = 0;
            			}else {
            				bucketKey = bucketName+keyName;
                			cnt++;
            			}
            		}
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            else {
            	//nothing to do
            }
        	//现在采用可以直接上传
        }
  
        public void fileDeleted(int wd, String rootPath, String name)  
        {	
        	System.out.println("deleted " + rootPath + " : " + name);  
        	
            name = name.replace('\\', '/');
            String bucketName = name.substring(0,name.indexOf("/"));
            String key = name.substring(name.indexOf("/")+1,name.length());
            String absoluteFilePath = rootPath.replace('\\', '/')+"/"+name;
          
            File file = new File(absoluteFilePath);
            //RequestForSDKServiceBlockingQueue.getLinkedBlockingQueueData().put(
			//		new RequestForSDKServiceData(OperationName.deleteOneObject,bucketName,key,"",
			//				oldKey) );
			//以key开头的object都将被删除
            deleteFile(bucketName, key, file);	
			return ;
            
        } 
        
        public void fileCreated(int wd, String rootPath, String name)  
        {  
        	
        	System.out.println("created " + rootPath + " : " + name);  
            
            name = name.replace('\\', '/');
            String bucketName = name.substring(0,name.indexOf("/"));
            String keyName = name.substring(name.indexOf("/")+1,name.length());
            String absoluteFilePath = rootPath.replace('\\', '/')+"/"+name;
            File file = new File(absoluteFilePath);
            
            if(file.isDirectory()) {
                uploadFolder(bucketName, keyName,file);
            }
            else {		
            	try {
					uploadFile(bucketName, keyName, file);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            return;
        }  
  
    
       	private boolean deleteFile(String bucketName,String key,File file) {
    		
    		System.out.println("delete file or folder"+key);
    		String oldKey = key.indexOf("/")==-1 ? "":key.substring(0, key.lastIndexOf("/")+1);
    		try {
				RequestForSDKServiceBlockingQueue.getLinkedBlockingQueueData().put(
						new RequestForSDKServiceData(OperationName.deleteOneObject,bucketName,key,"",
								oldKey) );
				return true;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
    		
    	}
       	
	}
	public static boolean uploadFile(String bucketName,String key,File file) throws NoSuchAlgorithmException, IOException {
		
		//ObjectOperations.uploadObject(bucketName,key,file.getAbsolutePath());
		System.out.println("upload file:"+key);
		try {
			//这里有一个bug，当上传还没成功时就获取文件的信息是没有道理的
			
			if(ObjectOperations.doesObjectExist(bucketName, key) == true) {
				//验证md5
				//这里有个问题，可能没下载完就去计算md5，这样肯定是不对的
				//使用一段时间内长度不变来查看文件是否写完
				long old_length;  
		        do {  
		            old_length = file.length();  
		            try {  
		                Thread.sleep(300);  
		            } catch (InterruptedException e) {  
		                e.printStackTrace();  
		            }  
		        } while (old_length != file.length());
		        
				String md5 = ComputeFileMD5.generate(file);
				String etag = ObjectOperations.getObjectMetaData(bucketName, key)[5];
				System.out.println(md5);
				System.out.println(etag);
				if(md5.equals(etag)) {
					return true;
				}
			}
			
			String oldKey = key.indexOf("/")==-1 ? "":key.substring(0, key.lastIndexOf("/")+1);
			
			RequestForSDKServiceBlockingQueue.getLinkedBlockingQueueData().put(
					new RequestForSDKServiceData(OperationName.uploadObject,bucketName,key,
							file.getAbsolutePath().replace('\\', '/'),
							oldKey) );
			return true;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			TaskTextArea.appendText("    -put object failed");
			ErrorMessage.showErrorMessage("upload object failed");
		}
		return false;
	}
	
	public static boolean uploadFolder(String bucketName,String key,File file) {
		
		
		System.out.println("upload folder:"+key);
		try {
			
			//已经在云存储上存在了
			if(ObjectOperations.doesObjectExist(bucketName, key+"/") == true) {
				//使用MD5验证，是否需要重新上传
				//因为文件夹的MD5都是对“”的摘要，这里不再验证，云存储有这个文件夹就不上传了
				return true;
			}
			
			String oldKey = key.indexOf("/")==-1 ? "":key.substring(0, key.lastIndexOf("/")+1);
			RequestForSDKServiceBlockingQueue.getLinkedBlockingQueueData().put(
					new RequestForSDKServiceData(OperationName.putOneFolder,bucketName,key+"/",
							file == null ? "" : file.getAbsolutePath().replace('\\', '/'),
							oldKey));
			
			//System.out.println(key.substring(0, key.lastIndexOf("/")));
		
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;	
	}
	public static boolean uploadDir(String bucketName,String keyName,File file,String fileOrFolderName) throws NoSuchAlgorithmException, IOException {
		
		if(file.isDirectory()) {
			
			System.out.println("fileName:"+keyName+fileOrFolderName);
			
			uploadFolder(bucketName, keyName+fileOrFolderName,file);
			String[] children = file.list();
			for (int i=0; i<children.length; i++) {
				File newFile = new File(file, children[i]);
	        	boolean success = uploadDir(bucketName,keyName,newFile,fileOrFolderName+"/"+newFile.getName());
	            if (!success) {
	            	ErrorMessage.showErrorMessage("upload failed");
	            	return false;
	            }
	        }
		} 
		else {
			//System.out.println("uploadDir:"+dir.getAbsolutePath());
			uploadFile(bucketName, keyName+fileOrFolderName, file);
		}
		return true;
		
	}
	
	
}
