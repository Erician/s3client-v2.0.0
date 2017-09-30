package mytest;  
  
import java.awt.Color;  
import java.awt.Container;  
import java.awt.Dimension;  
import java.awt.GridLayout;  
  
import javax.swing.BorderFactory;  
import javax.swing.Box;  
import javax.swing.BoxLayout;  
import javax.swing.JFrame;  
import javax.swing.JLabel;  
import javax.swing.JPanel;  
import javax.swing.JTabbedPane;  
import javax.swing.border.Border;  
import javax.swing.border.TitledBorder;  
  
public class TitleBorder extends JFrame {  
    public TitleBorder() {  
        initUI();  
    }  
  
    private void initUI() {  
        Border emptyPanl,lineBorder,etchedBorder  
            ,titleBorder1,titleBorder2  
            ,titleBorderCenter_Left , titleBorderCenter_CENTER  
            ,titleBorderBottom_center,titleBorderBelowBottom_Center;  
        emptyPanl = BorderFactory.createEmptyBorder(10, 10, 10, 10);  
        lineBorder = BorderFactory.createLineBorder(Color.red);  
        etchedBorder = BorderFactory.createEtchedBorder();  
          
        titleBorder1 = BorderFactory.createTitledBorder(lineBorder,"title");  
        titleBorder2 = BorderFactory.createTitledBorder(etchedBorder,"title");  
        titleBorderCenter_Left = BorderFactory  
                .createTitledBorder(lineBorder   
                       , "title" , TitledBorder.LEFT   
                       , TitledBorder.CENTER );  
          
        titleBorderCenter_CENTER = BorderFactory  
                .createTitledBorder(lineBorder  
                    , "title", TitledBorder.CENTER  
                    , TitledBorder.CENTER );  
  
        titleBorderBottom_center = BorderFactory  
                .createTitledBorder(etchedBorder  
                    , "title", TitledBorder.CENTER  
                    , TitledBorder.BOTTOM);  
        titleBorderBelowBottom_Center = BorderFactory  
                .createTitledBorder(etchedBorder  
                    , "title", TitledBorder.CENTER  
                    , TitledBorder.BELOW_BOTTOM);  
  
        JPanel simoleTitleBorder = new JPanel();  
        simoleTitleBorder.setBorder(emptyPanl);  
        simoleTitleBorder.setLayout(new BoxLayout(simoleTitleBorder, BoxLayout.Y_AXIS));  
        addCompForBorder(titleBorder1,"title Border with line Border",simoleTitleBorder);  
        addCompForBorder(titleBorder2,"title Border with etche Border",simoleTitleBorder);  
          
        JPanel customTitleBorder = new JPanel();  
        customTitleBorder.setBorder(emptyPanl);  
        customTitleBorder.setLayout(  
                                new BoxLayout(customTitleBorder  
                                       , BoxLayout.Y_AXIS));  
        addCompForBorder(titleBorderCenter_Left  
                                       ,"标题在左上边且在边框里"  
                                       ,customTitleBorder);  
        addCompForBorder(titleBorderCenter_CENTER  
                                       ,"标题在上边框中间且在边框里"  
                                       ,customTitleBorder);  
        addCompForBorder(titleBorderBottom_center  
                                       ,"标题下边框中间且在边框里"  
                                       ,customTitleBorder);  
        addCompForBorder(titleBorderBelowBottom_Center  
                                       ,"标题在下边框外且在中间"  
                                       ,customTitleBorder);  
          
          
        JTabbedPane jTabbedPane = new JTabbedPane();  
        jTabbedPane.setToolTipText("simple");  
        jTabbedPane.addTab("simpleTitleBorde", simoleTitleBorder);  
        jTabbedPane.addTab("customTitleBorde", customTitleBorder);  
        this.add(jTabbedPane);  
        //this.getContentPane().add(jTabbedPane);  
    }  
      
    private void addCompForBorder(Border border,String lable,Container container) {  
        JPanel comp = new JPanel(false);  
        JLabel label = new JLabel(lable, JLabel.CENTER);  
        comp.setLayout(new GridLayout(1, 1));  
        comp.add(label);  
        comp.setBorder(border);  
  
        container.add(Box.createRigidArea(new Dimension(0, 10)));  
        container.add(comp);  
    }  
  
    public static void main(String[] args) {
        JFrame frame = new TitleBorder();  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        frame.setPreferredSize(new Dimension(500,500));  
        frame.pack();  
        frame.setVisible(true);  
    }  
}  