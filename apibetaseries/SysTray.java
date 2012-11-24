package apibetaseries;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SysTray {
    
    public TrayIcon trayIcon = null;
    public PopupMenu popup;
    public MenuItem IClose = IClose();
    public List<MenuItem> ITVShows;
    public DnDDummyFrame dndDummyFrame = new DnDDummyFrame();
    public APIHandler APIHandler = new APIHandler();
    
    public SysTray() {
        if (!SystemTray.isSupported())
            System.out.println("SystemTray not supported");
        
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().getImage("icon.png");

        popup = new PopupMenu();

        trayIcon = new TrayIcon(image, "Betaseries Fetcher", popup);
        
        trayIcon.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent me) {
                    if( me.getButton() == MouseEvent.BUTTON3 )
                        menuShowed();
                    else if( me.getButton() == MouseEvent.BUTTON2 )
                        dndDummyFrame.updateVisible( !dndDummyFrame.isVisible );
                }
            }
        );
        
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println(e);
        }
    }
    
    public void menuShowed(){
        popup.removeAll();

        List<String> timeline = APIHandler.getTimeline(APIHandler.Login, 5);
        for(String s: timeline)
            popup.add( ITimeline(s) );
        
        popup.addSeparator();

        ITVShows = getTVShows();
        for(MenuItem iTVShow : ITVShows)
            popup.add( iTVShow );
        
        popup.addSeparator();

        popup.add(IRenamer());
        popup.add(CreateILog());
        popup.add(ICreateCSV());
        popup.add(IClose);
    }
    
    private List<MenuItem> getTVShows(){
        List<MenuItem> LShows = new ArrayList<>();
        
        List<String> winNameList = EnumAllWindowNames.getAllWindowNames();
        for (String winName : winNameList) {
            if( Utils.containsPlayerName(winName) ){
                TVShowNameParser tvShow = new TVShowNameParser(winName);
                if( tvShow.isFound )
                    LShows.add( ITVShows( tvShow ) );
            }
        }
        
        return LShows;
    }
    
    private MenuItem ITVShows( final TVShowNameParser show ){
        MenuItem it = new MenuItem( "Add episode: '" + show.toString() + "'" );
        it.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    APIHandler.viewEpisode( show );
                }
            }
        );
        return it;        
    }
    
    private Menu CreateILog(){
        Menu it = new Menu("Read Log");
        
        List<String> entries = Utils.ReadFile( Utils.LogFile );
        for(String s: entries)
            it.add( ILogEntry( s ) );
        
        return it;
    }
    
    private MenuItem IRenamer(){
        String title = (dndDummyFrame.isVisible() ? "Hide Renamer" : "Show Renamer");
        MenuItem it = new MenuItem(title);
        it.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    dndDummyFrame.updateVisible( !dndDummyFrame.isVisible() );
                }
            }
        );
        return it;
    }
    
    private MenuItem ICreateCSV() {
        MenuItem it = new Menu("Create CSV");
        it.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    APIHandler.createCSV();
                }
            }
        );
        return it;   
    }
    
    private MenuItem ILogEntry( final String s ){
        MenuItem it = new MenuItem(s);
        return it;
    }
    
    private MenuItem IClose(){
        MenuItem it = new MenuItem("Close");
        it.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    Utils.logger.info("Closing...");
                    System.exit(0);
                }
            }
        );
        return it;
    }
    
    private MenuItem ITimeline( final String title ){
        MenuItem it = new MenuItem( title );
        it.setEnabled(false);
        return it;
    }
}
