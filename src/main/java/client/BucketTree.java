package client;


import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;


import com.amazonaws.services.s3.model.Bucket;
import interdata.CurDirData;

public class BucketTree extends JTree{
	
	private DefaultMutableTreeNode root = null;
	private DefaultTreeModel treeModel = null; 
	private TreePath treePath = null;
	private CurrentDirectoryLabel currentDirectoryLabel = null;
	private CurDirData curDirData = null;
	private ObjectTable objectTable = null;
	
	public BucketTree(DefaultTreeModel treeModel,DefaultMutableTreeNode root) {
		// TODO Auto-generated constructor stub
		super(treeModel);
		this.treeModel = treeModel;
		this.root = root;
		setComponentPositionAndAttribute();
	}
	
	private void setComponentPositionAndAttribute() {
		
		setRowHeight(35);
		setAutoscrolls(true);
		//创建节点绘制对象  
        DefaultTreeCellRenderer cellRenderer = (DefaultTreeCellRenderer)getCellRenderer();  
        //设置颜色
        //cellRenderer.setIcon(arg0);
        cellRenderer.setBackground(Color.YELLOW);
        //cellRenderer.setBorderSelectionColor(Color.red);
        
        //addTreeSelectionListener(new listObjectTreeSelectionListener());
        addTreeSelectionListener(new listObjectInDirectoryTreeSelectionListener());
        addMouseListener(new popMenuMouseAdapter());
        
	}
	/*
	private class listObjectTreeSelectionListener implements TreeSelectionListener{

		public void valueChanged(TreeSelectionEvent e) {
			// TODO Auto-generated method stub
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();  
			if(node == null) {
				return ;
			}  
			if(node.isLeaf() == true) {
				
				ObjectTable.setObjectTableData(node.getUserObject().toString());

			}	
		}
	}
	*/
	private class listObjectInDirectoryTreeSelectionListener implements TreeSelectionListener{

		public void valueChanged(TreeSelectionEvent e) {
			// TODO Auto-generated method stub
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();  
			if(node == null) {
				return ;
			}  
			//使用isRoot是比较准确地，不要用isNode
			if(node.isRoot()==false) {
				//获取所点击的名字
				/*
				 * progress bar 暂缓
				 */
				curDirData.setCurBucket(node.getUserObject().toString());
				curDirData.setCurKey("");
				currentDirectoryLabel.setText(curDirData.getCurDir());
				objectTable.setObjectTableDataInDirectory(node.getUserObject().toString());
			}	
		}
	}
	private class popMenuMouseAdapter extends MouseAdapter{
		//参考
		//https://stackoverflow.com/questions/10101428/showing-popup-box-on-right-click-at-jtree-node-swing
		public void mousePressed ( MouseEvent e ){
            if ( SwingUtilities.isRightMouseButton ( e ) ){
            	
                TreePath path = BucketTree.this.getPathForLocation ( e.getX (), e.getY () );
                Rectangle pathBounds = BucketTree.this.getUI ().getPathBounds ( BucketTree.this, path );
                if ( pathBounds != null && pathBounds.contains ( e.getX (), e.getY () ) ){
                	if("buckets".equals(path.getLastPathComponent()) == false) {
                		JPopupMenu menu = new JPopupMenu ();
                        menu.add ( new JMenuItem ( "Test" ) );
                        menu.show ( BucketTree.this, pathBounds.x, pathBounds.y + pathBounds.height );
                	}
                	else {
                		
                	}
                    
                }
            }
        }
	}
	public void init(List<Bucket> buckets) {
		
		for(Bucket b: buckets){
			treeModel.insertNodeInto(new DefaultMutableTreeNode(b.getName()), root, root.getChildCount()); 
        }
	}
	public void clear() {
		
		DefaultMutableTreeNode devices = (DefaultMutableTreeNode)treeModel.getRoot();
		devices.removeAllChildren();
		treeModel.reload();
		
	}

	
	public void setCurrentDirectoryLabel(CurrentDirectoryLabel currentDirectoryLabel) {
		this.currentDirectoryLabel = currentDirectoryLabel;
		
	}
	public void setCurDirData(CurDirData curDirData) {
		this.curDirData = curDirData;
	}
	public void setObjectTable(ObjectTable objectTable) {
		this.objectTable = objectTable;
	}
	public void expandBucketTree() {
		treePath = new TreePath(root);
		expandPath(treePath);
	}
	

}
