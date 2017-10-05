package client;

import static run.DialogRun.run;
import static run.FrameRun.run;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Label;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import com.amazonaws.services.ec2.model.EgressOnlyInternetGateway;
import com.amazonaws.services.support.model.Service;

import threadcommunication.RequestForSDKServiceData;
import threadcommunication.SynDirBlockingQueue;
import threadcommunication.RequestForSDKServiceData.OperationName;
import utils.ComputeFileMD5;
import interdata.ConfigData;
import interdata.CurDirData;
import interdata.InfoForUpdateObjectTable;
import threadcommunication.RequestForSDKServiceBlockingQueue;
import interdata.ListObjectInDirectoryData;
import interdata.SynDirData;
import s3sdk.BucketOperations;
import s3sdk.ObjectOperations;
import s3sdk.S3Client;
import service.SynDirService;
import showmessage.ErrorMessage;
import showmessage.WarningMessage;

 
public class MainFrame extends JFrame implements Runnable{
	
	private JMenuBar 
		jmb = new JMenuBar();
	
	private JMenu 
		fileMenu = new JMenu("File"),
		editMenu = new JMenu("Edit"),
		synchMenu = new JMenu("Synch"),
		helpMenu = new JMenu("Help");
	//fileMenu's items
	private JMenuItem
		addAnAccountItem= new JMenuItem("Add an Account"),
		editAccountsItem = new JMenuItem("Edit Accounts"),
		switchAccountItem = new JMenuItem("Switch Account"),
		exitMenuItem = new JMenuItem("Exit");
	//editMenu's items
	
	//synchMenu's items
	private JMenuItem
		addAFolderItem = new JMenuItem("Add a Folder"),
		editFoldersItem = new JMenuItem("Edit Folders");
	//helpMenu's items
	private JMenuItem
		aboutS3ClientMenuItem = new JMenuItem("About S3Client");
	
	
	
	private JPanel
		mainPanel = new JPanel(),
		pathAndSearchPanel = new JPanel(true);
	
	private DefaultMutableTreeNode 
		root = new DefaultMutableTreeNode("buckets");
	
	private BucketTree
		bucketTree = new BucketTree(new DefaultTreeModel(root),root);
	
	private JScrollPane
		bucketTreeScrollPane = new JScrollPane(bucketTree);
	
	//private ObjectTable
		//objectTable = new ObjectTable();
	
	private JScrollPane
		objectTableScrollPane = null;
	
	private CurrentDirectoryLabel
		currentDirectoryLabel = new CurrentDirectoryLabel();
	
	private JButton
		backButton = new JButton(""),
		forwardButton = new JButton(""),
		uploadButton = new JButton("Upload"),
		newFolderButton = new JButton("NewFolder"),
		downloadButton = new JButton("Download"),
		deleteButton = new JButton("Delete"), 
		freshButton = new JButton("Fresh");
	//task is useless,just for init
	private TaskTextArea
		taskTextArea = new TaskTextArea();
	
	private JScrollPane
		taskTextAreaScrollPane = new JScrollPane(TaskTextArea.getTaskTextArea());
	private JTabbedPane 
		tabbedPane = new JTabbedPane();  
	
	//用于自动刷新的数据
	CurDirData curDirData = null;
	ListObjectInDirectoryData listObjectInDirectoryData = null;
	ObjectTable objectTable = null;
	//countDownLatch这个用于关闭该主窗口时，通知线程池关闭
	private final CountDownLatch countDownLatch;
	//只有账户正确时，才可以将isSynDirok设为true
	private boolean isAccountOk = false;
	
	
	public MainFrame(CountDownLatch latch,CurDirData curDirData,
			ListObjectInDirectoryData listObjectInDirectoryData, ObjectTable objectTable
			) throws IOException {
		//checkCredentialsRegionFile这个函数还没有okay，应该检查是否有效
		countDownLatch = latch;
		this.curDirData = curDirData;
		this.listObjectInDirectoryData = listObjectInDirectoryData;
		this.objectTable = objectTable;
		
		objectTableScrollPane = new JScrollPane(objectTable);
		
		setLayout(null);
		mainPanel.setLayout(null);
		setComponentPositionAndAttribute();
		fileMenu.add(addAnAccountItem);
		fileMenu.add(editAccountsItem);
		fileMenu.add(switchAccountItem);
		fileMenu.add(exitMenuItem);
		
		synchMenu.add(addAFolderItem);
		synchMenu.add(editFoldersItem);
		
		helpMenu.add(aboutS3ClientMenuItem);
		
		jmb.add(fileMenu);
		jmb.add(editMenu);
		jmb.add(synchMenu);
		jmb.add(helpMenu);
		setJMenuBar(jmb);
		//
		mainPanel.add(bucketTreeScrollPane);
		mainPanel.add(objectTableScrollPane);
		
		//add button
		mainPanel.add(uploadButton);
		mainPanel.add(newFolderButton);
		mainPanel.add(downloadButton);
		mainPanel.add(deleteButton);
		//mainPanel.add(freshButton);
		//
		tabbedPane.add("task",taskTextAreaScrollPane);
		mainPanel.add(tabbedPane);
		//
		mainPanel.add(currentDirectoryLabel);
		
		//pathAndSearchPanel
		pathAndSearchPanel.add(backButton);
		pathAndSearchPanel.add(forwardButton);
		
		add(pathAndSearchPanel);
		add(mainPanel);
		//程序打开时进行初始化
		init();
	}
	
	private class MainFrameWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e){
			int n = JOptionPane.showConfirmDialog(null,"Do you want to exit thie S3Client?","提示",JOptionPane.YES_NO_OPTION);
			if(n==JOptionPane.YES_OPTION && e.getWindow() == MainFrame.this) {
				countDownLatch.countDown();
				System.exit(0);
			}
			else
				return;
		}
	}
	private void setBound() {
		int sw = getWidth();
		int sh = getHeight();
		
		
		float heightFactor = ScreenInfo.getHeightFactor();
		float widthFactor = ScreenInfo.getWidthFactor();
		
		pathAndSearchPanel.setBounds(0,0,sw,Math.round(50*heightFactor));
		//先在我的机器上测试一下图片
		backButton.setBounds(Math.round(9*widthFactor), Math.round(9*heightFactor), 
				Math.round(32*widthFactor), Math.round(32*heightFactor));
		//像素的简单调整
		forwardButton.setBounds(Math.round(45*widthFactor), 
				ScreenInfo.getScreenWidth()>=1900?Math.round(9*heightFactor)+1:Math.round(9*heightFactor), 
				Math.round(32*widthFactor), Math.round(32*heightFactor));
		
		
		mainPanel.setBounds(0, Math.round(50*heightFactor), sw, sh);
		
		bucketTreeScrollPane.setBounds(0, 0, sw/5, sh/8*5);
		objectTableScrollPane.setBounds(sw/5, 0, sw-sw/5-10-10, sh/8*5-50);
		currentDirectoryLabel.setBounds(0, sh-110, sw, 28);
		
		uploadButton.setBounds(sw/5+10, sh/8*5-50+10, 130, 28);
		newFolderButton.setBounds(sw/5+10+140, sh/8*5-50+10, 130, 28);
		downloadButton.setBounds(sw/5+10+140+140, sh/8*5-50+10, 130, 28);
		deleteButton.setBounds(sw/5+10+140+140+140, sh/8*5-50+10, 130, 28);
		freshButton.setBounds(sw/5+10+140+140+140+140, sh/8*5-50+10, 130, 28);
		
		tabbedPane.setBounds(0,sh/8*5,sw-20,sh/8*3-110);
		
		bucketTreeScrollPane.updateUI();
		objectTableScrollPane.updateUI();
	}
	class MainFrameComponentListener implements ComponentListener{
		public void componentShown(ComponentEvent state) {
			;
		}
		public void componentHidden(ComponentEvent state){
			;
		}
		public void componentMoved(ComponentEvent state){
			;
		}
		public void componentResized(ComponentEvent state){
			setBound();
		}
	}
	private class MainFrameWindowStateListener implements WindowStateListener{
		 public void windowStateChanged(WindowEvent e){
			 /*
			  * 暂时还没有用到
			 int oldState=e.getOldState(); 
			 int newState=e.getNewState();
			 if(newState == Frame.MAXIMIZED_BOTH || newState == Frame.NORMAL) {
				 //setBound();
			 }
			 
			 String from=""; //标识窗口以前状态的中文字符串.
			 String to=""; //标识窗口现在状态的中文字符串.
		 
			 switch(oldState){ //判断窗口以前的状态.
			 case Frame.NORMAL: //窗口正常化.
				 from="正常化"; 
				 break;
			 case Frame.MAXIMIZED_BOTH:
				 from="最大化";
				 break;
			 default:
				 from="最小化"; //窗口最小化.
			 }
			 */
		 }
	}
	private void setComponentPositionAndAttribute() {
		//add listener
		this.addComponentListener(new MainFrameComponentListener());
		this.addWindowListener(new MainFrameWindowListener());
		this.addWindowStateListener(new MainFrameWindowStateListener());
		//accountMenuItem.addActionListener(new AccountMenuItemListener());
		//synMenuItem.addActionListener(new SynMenuItemListener());
		exitMenuItem.addActionListener(new ExitMenuItemListener());
		
		uploadButton.addActionListener(new UploadActionListener());
		newFolderButton.addActionListener(new NewFolderActionListener());
		deleteButton.addActionListener(new DeleteActionListener());
		downloadButton.addActionListener(new DownLoadActionListener());
		
		backButton.addMouseListener(new backButtonMouseListener());
		forwardButton.addMouseListener(new forwardButtonMouseListener());
		
		//focus painted
		uploadButton.setFocusPainted(false);
		newFolderButton.setFocusPainted(false);
		downloadButton.setFocusPainted(false);
		deleteButton.setFocusPainted(false);
		freshButton.setFocusPainted(false);
		backButton.setFocusPainted(false);
		forwardButton.setFocusPainted(false);
		
		//focusable
		uploadButton.setFocusable(false);
		newFolderButton.setFocusable(false);
		downloadButton.setFocusable(false);
		deleteButton.setFocusable(false);
		freshButton.setFocusable(false);
		backButton.setFocusable(false);
		forwardButton.setFocusable(false);
		
		//border
		Border border = BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0x828790));
		pathAndSearchPanel.setBorder(border);
		backButton.setBorderPainted(false);
		forwardButton.setBorderPainted(false);
		
		//color
		jmb.setBackground(new Color(0xF2F2F2));
		pathAndSearchPanel.setBackground(new Color(0xF2F2F2));
		backButton.setBackground(new Color(0xF2F2F2));
		forwardButton.setBackground(new Color(0xF2F2F2));
		
		mainPanel.setBackground(Color.WHITE);
		objectTableScrollPane.getViewport().setBackground(Color.WHITE);
		//picture
		if(ScreenInfo.getScreenWidth()>=1900) {
			backButton.setIcon(new ImageIcon("src/main/resources/pictures/back32-32.png"));
			forwardButton.setIcon(new ImageIcon("src/main/resources/pictures/forward32-32.png"));
		}
		else {
			backButton.setIcon(new ImageIcon("src/main/resources/pictures/back22-22.png"));
			forwardButton.setIcon(new ImageIcon("src/main/resources/pictures/forward22-22.png"));
		}
		
	}
	private void init() throws IOException {
		//先读取文件
		if(ConfigData.readDataFromFile()==true&&ConfigData.isConfigureOk()==true) {
			//可能会出现不成功的情况
			try {
				if(S3Client.createAmazonS3Client()) {
					bucketTree.init(BucketOperations.listBucket());
					bucketTree.expandBucketTree();
					//运行到这里，说明账户正确
					isAccountOk = true;
				}
				
			}catch (Exception e) {
				// TODO: handle exception
				ErrorMessage.showErrorMessage("We can't list your buckets.Please check you account!");
				isAccountOk = false;
			}
		}
		if(SynDirData.readDataFromFile()==true && isAccountOk == true) {
			//不再使用isSynDirChanged来同步
			//App.isSynDirChanged = true;
			try {
				SynDirBlockingQueue.getLinkedBlockingQueueData().put(SynDirData.getSynDir());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				ErrorMessage.showErrorMessage("synchronize local file failed!");
			}
		}

		currentDirectoryLabel.setText(curDirData.getCurDir());
		
		//将currentDirectoryLabel传给buckettree
		bucketTree.setCurrentDirectoryLabel(currentDirectoryLabel);
		bucketTree.setObjectTable(objectTable);
		bucketTree.setCurDirData(curDirData);
		
		objectTable.setCurrentDirectoryLabel(currentDirectoryLabel);
		objectTable.setCurDirData(curDirData);
		objectTable.setListObjectInDirectoryData(listObjectInDirectoryData);
		
		listObjectInDirectoryData.setCurDirData(curDirData);
		
		
	}
	private class ExitMenuItemListener implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			int n = JOptionPane.showConfirmDialog(null,"Do you want to exit thie S3Client?","提示",JOptionPane.YES_NO_OPTION);
			if(n==JOptionPane.YES_OPTION)
				dispose();
		}
	}
	private class AccountMenuItemListener implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			try {
				//在dialog内设置了config
				run.DialogRun.run(new AccountDialog(),800,400);
				
				//这里改一下，第一次演示的bug是在这里引发的
				if(ConfigData.isConfigureOk()) {
					try {
						if(S3Client.createAmazonS3Client()) {
							//清空bucketTree和table
							bucketTree.clear();
							listObjectInDirectoryData.init(null);
							objectTable.setObjectTableModeDataAndUpdateUI();
							bucketTree.init(BucketOperations.listBucket());
							bucketTree.expandBucketTree();
							//运行到这里，说明账户正确 
							ConfigData.saveConfig();
							isAccountOk = true;
							if(SynDirData.isSynDirOk()==true) {
								//不再使用isSynDirChanged来同步
								//App.isSynDirChanged = true;
								try {
									SynDirBlockingQueue.getLinkedBlockingQueueData().put(SynDirData.getSynDir());
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									ErrorMessage.showErrorMessage("synchronize local file failed!");
								}
							}
						}
						
					}catch (Exception e) {
						// TODO: handle exception
						
						ErrorMessage.showErrorMessage("We can't list your buckets.Please check you account!");
						isAccountOk = false;
					}
				}
				
			}catch (Exception e) {
				// TODO: handle exception
				ErrorMessage.showErrorMessage("We can't list your buckets.Please check you account!");
			}
		}
	}
	private class SynMenuItemListener implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			try {
				run.DialogRun.run(new SynDirDialog(),800,350);
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	private class backButtonMouseListener implements MouseListener{

		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("back");
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			backButton.setBackground(new Color(0xDFDFDF));
		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			backButton.setBackground(new Color(0xF2F2F2));
		}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	private class forwardButtonMouseListener implements MouseListener{

		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println("forward");
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			forwardButton.setBackground(new Color(0xDFDFDF));
		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			forwardButton.setBackground(new Color(0xF2F2F2));
		}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class UploadActionListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(curDirData.getCurBucket() == null || curDirData.getCurBucket().equals("")) {
				ErrorMessage.showErrorMessage("Please choose a bucket first!");
				return;
	        }
			
			String absoluteFilePath = "";
			String folderOrFileName = "";
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int value = fileChooser.showDialog(new Label(), "choose a file or directory to upload");
			//判断窗口是否点的是打开或保存
			if(value==JFileChooser.APPROVE_OPTION){ 
				absoluteFilePath = fileChooser.getSelectedFile().getAbsolutePath();
				folderOrFileName = fileChooser.getSelectedFile().getName();
			}
			else{
				return;
			}
			//the delimiter is '\'
			//System.out.println(fileChooser.getSelectedFile().getName());
			
			File file = new File(absoluteFilePath);
			String bucketName = curDirData.getCurBucket();
			
			String keyName = "";
			
			
			if(file.isFile()) {
				//this is a file
				keyName = curDirData.getCurKey()+file.getName();
				//这里使用文件的名字，而不使用file对象的引用
				try {
					SynDirService.uploadFile(bucketName, keyName, file);
				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				/*
				 * 废弃的更新方式，将使用按钮更新
				//更新显示
				//updateObjectTable();
				 */
			}
			else {
				//this is a directory
				
				try {
					keyName = curDirData.getCurKey();
					SynDirService.uploadDir(bucketName,keyName,file,folderOrFileName);
				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			
		}
	}
	
	private class NewFolderActionListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(curDirData.getCurBucket()==null||curDirData.getCurBucket().equals("")) {
				ErrorMessage.showErrorMessage("Please select a bucket first!");
				return;
			}
			String folderName = "";
			do {
				folderName = JOptionPane.showInputDialog(
						null,"folder name:\n","Input the folder name",JOptionPane.PLAIN_MESSAGE);
				if(folderName == null) {
					return;
				}
				if(folderName.equals("")) {
					WarningMessage.showWarningMessage("Please input a folder name!");
				}
				else if(folderName.indexOf("/") !=-1) {
					WarningMessage.showWarningMessage("the folder's name can't include '/'!");
				}
				else {
					break;
				}
			}while(true);
			String newKey = curDirData.getCurKey()+folderName;

			SynDirService.uploadFolder(curDirData.getCurBucket(), newKey,null);
			//更新显示
			updateObjectTable();
			
		}
		
	}
	
	/*
	 * 当从云存储删除时，如果本地存在，那么也会从本地删除
	 */
	private class DeleteActionListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			int row = objectTable.getSelectedRow();
			if(row == -1) {
				ErrorMessage.showErrorMessage("Please select one file before you execute delete!");
				return;
			}
			//规定一下，文件列表显示的叫subKey,真正的叫key
			String subKey = (String) objectTable.getValueAt(row, 0);
			String key =  curDirData.getCurKey()+subKey;
			String absolutePath = SynDirData.getSynDir()+curDirData.getCurDir()+subKey;
			File file = new File(absolutePath);
			if(file.exists()) {
				deleteDir(file);
			}
			
			
			if(subKey.indexOf("/")==-1) {
				//this is a file
				//TaskTextArea.appendText("delete object -bucketname "+curDirData.getCurBucket()+" -key "+key);
				ObjectOperations.deleteOneObject(curDirData.getCurBucket(), key);
				//在objecttable删除这个文件的信息
				listObjectInDirectoryData.deleteOneDirectory(key);
				//更新显示
				updateObjectTable();
			}
			else {
				//this is a directory
				
				ArrayList<String> fileNameInOneDirectory = listObjectInDirectoryData.getFileNameInOneDirectory(key);
				//循环删除
				for(int i=0;i<fileNameInOneDirectory.size();i++) {
					
					ObjectOperations.deleteOneObject(curDirData.getCurBucket(), fileNameInOneDirectory.get(i));
				}
				//更新objecttable
				listObjectInDirectoryData.deleteOneDirectory(key);
				//更新显示
				updateObjectTable();
			}
		}
	}
	private class DownLoadActionListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
			//判断是否选择
			int row = objectTable.getSelectedRow();
			if(row == -1) {
				ErrorMessage.showErrorMessage("Please select one file before you execute delete!");
				return;
			}
			//判断是否设置了同步目录（synchronization directory）
			if(SynDirData.getSynDir() == null || SynDirData.getSynDir().equals("")) {
				try {
					run.DialogRun.run(new SynDirDialog(),800,350);
					if(SynDirData.isSynDirOk()&&isAccountOk) {
						try {
							SynDirBlockingQueue.getLinkedBlockingQueueData().put(SynDirData.getSynDir());
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							ErrorMessage.showErrorMessage("synchronize local file failed!");
						}
					}
					
				}catch (Exception e1) {
					// TODO: handle exception
					ErrorMessage.showErrorMessage("input the synchronization folder failed!");
				}
			}
			
			//如果仍然没有设置，将取消下载
			if(SynDirData.getSynDir() == null || SynDirData.getSynDir().equals("")) {
				return;
			}
			//规定一下，文件列表显示的叫subKey,真正的叫key
			String subKey = (String) objectTable.getValueAt(row, 0);
			String key =  curDirData.getCurKey()+subKey;

			//windows里synDir 默认是以'\'为分隔符，这里要把他们替换为'/'
			String synDir = SynDirData.getSynDir().replace('\\', '/');

			File file = null;
			
			
			if(subKey.indexOf("/")==-1) {
				//this is a file
				//ObjectOperations.downloadOneObject(curDirData.getCurBucket(),key,synDir+curDirData.getCurDir()+subKey);
				try {
					//TaskTextArea.appendText("get object -bucketname "+curDirData.getCurBucket()+" -key "+key
					//		+" -o " + synDir+curDirData.getCurDir()+subKey);
					RequestForSDKServiceBlockingQueue.getLinkedBlockingQueueData().put(
							new RequestForSDKServiceData(OperationName.downloadOneObject,
									curDirData.getCurBucket(),key,synDir+curDirData.getCurDir()+subKey));
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch blockf
					//TaskTextArea.appendText("    -get object failed");
					ErrorMessage.showErrorMessage("download object failed");
				}
			}
			else {
				//this is a directory
				ArrayList<String> list = listObjectInDirectoryData.getFileNameInOneDirectory(key);
				//删除老的目录以及目录下的文件及目录
				//if((file = new File(synDir+curDirData.getCurDir()+subKey)).exists() == true) {
				//	deleteDir(file);
				//}
				
				for(int i=0;i<list.size();i++) {
					//System.out.println(list.get(i));
					if(list.get(i).endsWith("/")) {
						
						TaskTextArea.appendText("mkdirs"+" -o "+synDir+"/"+curDirData.getCurBucket()+"/"+list.get(i));	
						//this is a folder
						(new File(synDir+"/"+curDirData.getCurBucket()+"/"+list.get(i))).mkdirs();
						continue;
							
					}
					//ObjectOperations.downloadOneObject(curDirData.getCurBucket(),list.get(i),synDir+"/"+list.get(i));
					try {
						//TaskTextArea.appendText("get object -bucketname "+curDirData.getCurBucket()+" -key "+list.get(i)+
						//		" -o "+synDir+"/"+curDirData.getCurBucket()+"/"+list.get(i));	
						//System.out.println(synDir+"/"+curDirData.getCurBucket()+"/"+list.get(i));
						RequestForSDKServiceBlockingQueue.getLinkedBlockingQueueData().put(
								new RequestForSDKServiceData(OperationName.downloadOneObject,
										curDirData.getCurBucket(),list.get(i),synDir+"/"+curDirData.getCurBucket()+"/"+list.get(i)));
						
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						//TaskTextArea.appendText("    -get object failed");
						ErrorMessage.showErrorMessage("download object failed");
					}
					
				}
			}
		}
	}
	
	
	private boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			//递归删除目录中的子目录下
	        for (int i=0; i<children.length; i++) {
	        	boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	            	return false;
	            }
	        }
		}
	    // 目录此时为空，可以删除
	    return dir.delete();
	}
	
	private void updateObjectTable() {
		
		//先要go back to root
		listObjectInDirectoryData.goBackToRoot();
		String curKey = curDirData.getCurKey();
		//System.out.println(curKey);
		String subCurKey = curKey;
		String key = "";
		int delimiterPos = 0;
		
		curDirData.setCurKey("");
		while((delimiterPos = subCurKey.indexOf("/")) != -1) {
			
			key = subCurKey.substring(0, delimiterPos+1);
			subCurKey = subCurKey.substring(delimiterPos+1,subCurKey.length());
			listObjectInDirectoryData.goToNextLevel(key);
		}
		curDirData.setCurKey(curKey);
		currentDirectoryLabel.setText(curDirData.getCurDir());
		objectTable.setObjectTableModeDataAndUpdateUI();	
	}
	public void run() {
		// TODO Auto-generated method stub
		try {
			run.FrameRun.run(this,Math.round(1300*ScreenInfo.getWidthFactor()),Math.round(850*ScreenInfo.getHeightFactor()));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
