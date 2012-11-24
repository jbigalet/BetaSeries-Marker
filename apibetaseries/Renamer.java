package apibetaseries;

import it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.DefaultCheckboxTreeCellRenderer;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.QuadristateButtonModel;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.TreeCheckingModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class Renamer extends JFrame {
    List<File> files;
    CheckboxTree checkboxTree;
    DnDDummyFrame dummyFrame;

    public Renamer( final DnDDummyFrame frame, List<File> files ){
        super("Automatic Renamer");
        
        this.files = files;
        this.dummyFrame = frame;
        
        this.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e){
                frame.updateVisible(true);
            }
        });

        this.getContentPane().setLayout(new BorderLayout());
        
        checkboxTree = new CheckboxTree( getTreeNodeFromFileList(files) );
        checkboxTree.setCellRenderer( new persoCellRenderer() );
        checkboxTree.setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView( checkboxTree );
        this.getContentPane().add( scrollPane, BorderLayout.CENTER );
        
        JButton button = new JButton("Rename");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                performRename();
            }
        });
        button.setPreferredSize(new Dimension(600, 40));
        this.getContentPane().add( button, BorderLayout.PAGE_END );
        
        this.setSize(600, 900 );
        this.setLocation( 50, 50 );

        this.setVisible(true);
    }
    
    public void performRename(){
        TreePath[] tps = checkboxTree.getCheckingPaths();
        for( TreePath tp : tps ){
            DefaultMutableTreeNode tn = (DefaultMutableTreeNode)tp.getLastPathComponent();
            if( tn.isLeaf() ){
                NodeFile nf = (NodeFile) tn.getUserObject();
                if( nf.renameTo( new File( 
                        Utils.getPath( nf.getAbsolutePath() )
                        + nf.parsed.toString()
                        + Utils.getExtension( nf.getName()) ) 
                   ) )
                    Utils.logger.info( "'" + nf.getAbsolutePath() + "' renamed");
                else
                    Utils.logger.warning( "Impossible to rename '" + nf.getAbsolutePath() + "'");
            }
        }
        this.dummyFrame.updateVisible( true );
        this.dispose();
    }
    
    private TreeNode getTreeNodeFromFileList( List<File> files ){
        DefaultMutableTreeNode tn = new DefaultMutableTreeNode("Files");
        for(File f : files)
            tn.add( getTreeNodeFromFiles( f ));
        return tn;
    }
    
    private MutableTreeNode getTreeNodeFromFiles( File file ){
        DefaultMutableTreeNode tn;

        if( file.isDirectory() ){
            tn = new DefaultMutableTreeNode( file.getName() );
            for( File f : file.listFiles() )
                tn.add( getTreeNodeFromFiles( f ));
        } else 
            tn = new DefaultMutableTreeNode( new NodeFile(file.getAbsolutePath()) );

        return tn;
    }
}


class NodeFile extends File {
    TVShowNameParser parsed;
    
    public NodeFile(String s){
        super(s);
        parsed = new TVShowNameParser( this.getName() );
    }
    
    @Override
    public String toString(){
        return parsed.toString() + "   ( " + this.getName() + " )";
    }
}


class persoCellRenderer extends DefaultCheckboxTreeCellRenderer {
    
    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf,
            int row, boolean hasFocus) {

        this.label.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        this.label.setFont( new Font(label.getFont().getName(),Font.BOLD,label.getFont().getSize()) );
        
        DefaultMutableTreeNode tn = (DefaultMutableTreeNode) value;
        if( leaf ){
            NodeFile nf = (NodeFile) tn.getUserObject();

            if( !nf.parsed.isFound )
                this.label.setForeground(Color.red);
            else if( nf.parsed.TVShow.toLowerCase().equals(nf.parsed.BSShow.toLowerCase()) )
                this.label.setForeground( new Color(50, 150, 0) );
            else
                this.label.setForeground( new Color(230, 160, 0) );
        }
        
        if (tree instanceof CheckboxTree) {
            TreePath path = tree.getPathForRow(row);
            TreeCheckingModel checkingModel = ((CheckboxTree) tree).getCheckingModel();
            this.checkBox.setEnabled(checkingModel.isPathEnabled(path) && tree.isEnabled());
            boolean checked = checkingModel.isPathChecked(path);
            boolean greyed = checkingModel.isPathGreyed(path);
            if (checked && !greyed) {
                this.checkBox.setState(QuadristateButtonModel.State.CHECKED);
            }
            if (!checked && greyed) {
                this.checkBox.setState(QuadristateButtonModel.State.GREY_UNCHECKED);
            }
            if (checked && greyed) {
                this.checkBox.setState(QuadristateButtonModel.State.GREY_CHECKED);
            }
            if (!checked && !greyed) {
                this.checkBox.setState(QuadristateButtonModel.State.UNCHECKED);
            }
        }
        return this;
    }
}