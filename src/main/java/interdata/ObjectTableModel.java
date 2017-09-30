package interdata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

public class ObjectTableModel extends AbstractTableModel{
	
	String[] columnNames =  { "File", "Owner", "Size", "Last Modified", "Storage class" }; 
    Object[][] data = new Object[0][5];
    String[] parentDir = {"..","","","",""};
    
    
    public ObjectTableModel() {
		// TODO Auto-generated constructor stub
	}
	
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnNames.length;
	}
	public int getRowCount() {
		// TODO Auto-generated method stub
		return data.length;
	}

	public Object getValueAt(int row, int col) {
		// TODO Auto-generated method stub
		//System.out.println(row+" "+col);
		return data[row][col];
	}
	
		/** 
     * 得到列名 
     */  
    @Override  
    public String getColumnName(int column)  
    {  
        return columnNames[column];  
    }  
    /** 
     * 指定设置数据单元是否可编辑 
     */  
    @Override  
    public boolean isCellEditable(int rowIndex, int columnIndex)  
    {  
        return false;
    }
    /*
    public void setData(ArrayList<String[]> listObject) {
    	if(listObject == null||listObject.size() == 0 ) {
    		data = parentDir;
    		return;
    	}
    	Object [][] tmpData = new Object[listObject.size()][listObject.get(0).length];
    	for(int i=0;i<listObject.size();i++) {
    		tmpData[i] = listObject.get(i);
    	}
    	
    }
    */
    public void setData(HashMap<String, String[]> objectInfo, HashSet<String> curListObject,String curKey) {
		// TODO Auto-generated method stub
    	if(curListObject == null||curListObject.size() == 0 ||curListObject.isEmpty()) {
    		Object [][] tmpData = new Object[1][5];
        	tmpData[0] = parentDir;
        	data = tmpData;
    		return;
    	}
    	
    	Object [][] tmpData = new Object[curListObject.size()+1][5];
    	tmpData[0] = parentDir;
    	int i = 1;
    	String tmpString;
    	for( Iterator it = curListObject.iterator(); it.hasNext();){
    		tmpString = (String) it.next();
    		
    		if(objectInfo.containsKey(curKey+tmpString)==false) {
    			tmpData[i++][0] = tmpString;
    			continue;
    		}
    		tmpData[i] = objectInfo.get(curKey+tmpString);
    		tmpData[i][0] = tmpString;
    		i++;
        }
    	
    	data = tmpData;
	}
	
}
