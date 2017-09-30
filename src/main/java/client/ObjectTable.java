package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import interdata.CurDirData;
import interdata.ListObjectInDirectoryData;
import interdata.ObjectTableModel;
import s3sdk.ObjectOperations;

public class ObjectTable extends JTable{
	
	private  ObjectTableModel objectTableModel= null;
	private  MyCellRenderer myCellRenderer = null;
	
	private CurrentDirectoryLabel currentDirectoryLabel = null;
	private CurDirData curDirData = null;
	private ListObjectInDirectoryData listObjectInDirectoryData = null;
	
	public ObjectTable(ObjectTableModel objectTableModel2) {
		super(objectTableModel2);
		this.objectTableModel = objectTableModel2;
		
		myCellRenderer = new MyCellRenderer();
		setComponentPositionAndAttribute();
	}
	
	private void setComponentPositionAndAttribute() {
		
		//add listener 
		addMouseListener(new MyMouseAdapter());
		
		setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
		setRowHeight(30);
		setAutoscrolls(true);
		setGridColor(Color.BLACK);
		setSelectionBackground(new Color(0x999999)); 
		setSelectionForeground(Color.WHITE);
		setShowVerticalLines(false);
	}
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		    return myCellRenderer;
	}
	/*
	public static void setObjectTableData(String bucketName) {
		ArrayList<String[]> list = ObjectOperations.listObject(bucketName);
		objectTableModel.setData(list);
		objectTableModel.fireTableDataChanged();
	}
	*/
	public void setObjectTableDataInDirectory(String bucketName) {
		TaskTextArea.appendText("list objects -bucketname "+bucketName);
		ArrayList<String[]> list = ObjectOperations.listObject(bucketName);
		listObjectInDirectoryData.init(list);
		
		objectTableModel.setData(listObjectInDirectoryData.getObjectInfo(),listObjectInDirectoryData.getCurListObject(),
				curDirData.getCurKey());
		objectTableModel.fireTableDataChanged();
	}
	
	private class MyMouseAdapter extends MouseAdapter{
		
		public void mouseClicked(MouseEvent e) { 
			if(e.getClickCount() == 2){ 
				//获得行位置 
				int row =((JTable)e.getSource()).rowAtPoint(e.getPoint()); 
				//获得列位置 ,这个没有用
				//int  col=((JTable)e.getSource()).columnAtPoint(e.getPoint());
				
				String objectName = objectTableModel.getValueAt(row, 0).toString();
				if(objectName.equals("..")) {
					//返回上一级目录
					listObjectInDirectoryData.backToLastLevel();
					currentDirectoryLabel.setText(curDirData.getCurDir());
					
					objectTableModel.setData(listObjectInDirectoryData.getObjectInfo(),listObjectInDirectoryData.getCurListObject(),
							curDirData.getCurKey());
					objectTableModel.fireTableDataChanged();
					
				}
				else if(objectName.indexOf("/") != -1) {
					//继续向下
					listObjectInDirectoryData.goToNextLevel(objectName);
					//set the curDir
					currentDirectoryLabel.setText(curDirData.getCurDir());
					
					objectTableModel.setData(listObjectInDirectoryData.getObjectInfo(),listObjectInDirectoryData.getCurListObject(),
							curDirData.getCurKey());
					objectTableModel.fireTableDataChanged();
				}
				else {
					//文件不响应
					return;
				}
				
			}
			else {
				return; 
			}
        }
	}
	public void setObjectTableModeDataAndUpdateUI() {
		objectTableModel.setData(listObjectInDirectoryData.getObjectInfo(),listObjectInDirectoryData.getCurListObject(),
				curDirData.getCurKey());
		objectTableModel.fireTableDataChanged();
	}
	public void setCurrentDirectoryLabel(CurrentDirectoryLabel currentDirectoryLabel) {
		this.currentDirectoryLabel = currentDirectoryLabel;
	}
	public void setCurDirData(CurDirData curDirData) {
		this.curDirData = curDirData;
	}
	public void setListObjectInDirectoryData(ListObjectInDirectoryData listObjectInDirectoryData) {
		this.listObjectInDirectoryData = listObjectInDirectoryData;;
	}
	public Object getValueAt(int row,int col){
		return objectTableModel.getValueAt(row, col);
	}
	private class MyCellRenderer extends DefaultTableCellRenderer {
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				  										 boolean hasFocus, int row, int column) {
			 
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		    setHorizontalAlignment(SwingConstants.CENTER);
		    return this;
		}
	}
	

}
