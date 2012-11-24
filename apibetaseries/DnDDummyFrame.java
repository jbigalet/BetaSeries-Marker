package apibetaseries;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

public class DnDDummyFrame extends JDialog {

    Point dummySize = new Point(20, 20);
    boolean isVisible = false;
    
    public DnDDummyFrame(){
        super();
        
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if( isVisible )
                    toFront();
            }
        };
        new Timer(300, actionListener).start();

        
        this.setSize(65, 37);
        //Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        //this.setLocation( mouseLocation.x - dummySize.x / 2, mouseLocation.y - dummySize.y / 2 );
        this.setLocation( Utils.settings.dummyDnDPosition );
        
        
        JLabel dropLabel = new JLabel("Renamer");
        Font font = new Font("Courier", Font.BOLD, 14);
        dropLabel.setFont(font);
        dropLabel.setForeground(Color.white);
        
        DnDListener dndListener = new DnDListener( this );
        new DropTarget( dropLabel, dndListener );
        
        MoveMouseListener moveMouseListener = new MoveMouseListener( this );
        this.getContentPane().addMouseListener( moveMouseListener );
        this.getContentPane().addMouseMotionListener( moveMouseListener );
        
        this.getContentPane().add(BorderLayout.CENTER,  dropLabel);
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.setBackground(new Color(0f, 0f, 0f, 0.01f));
    
        if( Utils.settings.isDummyDnDPresent )
            updateVisible(true);
    }
    
    public void updateVisible(boolean isVisible){
        this.setVisible(isVisible);
        this.isVisible = isVisible;
    }

    
}

class DnDListener implements DropTargetListener{
    DnDDummyFrame frame;
    
    public DnDListener(DnDDummyFrame frame){
        this.frame = frame;
    }
    
    @Override
    public void drop(DropTargetDropEvent event){
        event.acceptDrop(DnDConstants.ACTION_COPY);

        Transferable transferable = event.getTransferable();
        DataFlavor[] dataFlavors = transferable.getTransferDataFlavors();
        
        for( DataFlavor flavor : dataFlavors )
            if( flavor.isFlavorJavaFileListType() ){
                try{
                    frame.updateVisible(false);
                    List<File> files = (List<File>) transferable.getTransferData( flavor );
                    new Renamer(frame, files);
                } catch (Exception e) { e.printStackTrace(); }
            }
    }
    
    @Override
    public void dragEnter(DropTargetDragEvent event) { }

    @Override
    public void dragExit(DropTargetEvent event) { }

    @Override
    public void dragOver(DropTargetDragEvent event) { }

    @Override
    public void dropActionChanged(DropTargetDragEvent event) { }
    
}

class MoveMouseListener implements MouseListener, MouseMotionListener {

    DnDDummyFrame target;
    Point start_drag;
    Point start_loc;

    public MoveMouseListener(DnDDummyFrame target) {
        this.target = target;
    }

    Point getScreenLocation(MouseEvent e) {
        Point cursor = e.getPoint();
        Point target_location = this.target.getLocationOnScreen();
        return new Point((int) (target_location.getX() + cursor.getX()),
                (int) (target_location.getY() + cursor.getY()));
    }

    @Override public void mouseClicked(MouseEvent e) {
        if( e.getButton() == MouseEvent.BUTTON2 )
            this.target.updateVisible( false );
    }

    @Override public void mouseEntered(MouseEvent e) { }

    @Override public void mouseExited(MouseEvent e) { }

    @Override public void mousePressed(MouseEvent e) {
        this.start_drag = this.getScreenLocation(e);
        this.start_loc = this.target.getLocation();
    }

    @Override public void mouseReleased(MouseEvent e) { 
        Utils.settings.dummyDnDPosition = this.target.getLocation();
        Utils.settings.update();
    }

    @Override public void mouseDragged(MouseEvent e) {
        Point current = this.getScreenLocation(e);
        Point offset = new Point((int) current.getX() - (int) start_drag.getX(),
                (int) current.getY() - (int) start_drag.getY());
        Point new_location = new Point(
                (int) (this.start_loc.getX() + offset.getX()), (int) (this.start_loc
                .getY() + offset.getY()));
        this.target.setLocation(new_location);
    }

    @Override public void mouseMoved(MouseEvent e) { }
}