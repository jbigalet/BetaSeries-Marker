package apibetaseries;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class SettingsHandler {
    
    public String settingsFile;
    public boolean isLoaded = false;
    public boolean isDummyDnDPresent;
    public Point dummyDnDPosition;
    
    public SettingsHandler( String file ){
        this.settingsFile = file;
        reload();
    }
    
    public void reload(){
        List<String> settings = Utils.ReadFile( settingsFile );
        try{
            isDummyDnDPresent = Boolean.parseBoolean( settings.remove(0) );
            dummyDnDPosition = new Point(Integer.parseInt(settings.remove(0)), Integer.parseInt(settings.remove(0)));
            
            isLoaded = true;
        } catch( Exception e ) {
            Utils.logger.warning("Impossible to parse the settings file");
        }
    }
    
    public void update(){
        List<String> settings = new ArrayList<>();
        settings.add("" + isDummyDnDPresent);
        settings.add("" + dummyDnDPosition.x);
        settings.add("" + dummyDnDPosition.y);
        Utils.WriteFile(settingsFile, settings);
    }
    
}
