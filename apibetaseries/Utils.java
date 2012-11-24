package apibetaseries;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import org.apache.http.client.utils.URIUtils;

public class Utils {
    public static String[] players = {"VLC", "KMPlayer"};
    public static String LogFile = "log.txt";
    public static final Logger logger = CreateLogger();
    public static SettingsHandler settings = new SettingsHandler("settings.txt");
    
    public static boolean containsPlayerName(String toCheck){
        for(String player : players)
            if( toCheck.contains(player))
                return true;
        return false;
    }
    
    public static String trailingZeroNumber(int number, int size){
        String r = Integer.toString( number );
        while( r.length() < size )
            r = "0" + r;
        return r;
    }
    
    public static RegexMatch matchRegex(String string, String regex){
        Pattern p = Pattern.compile( regex );
        Matcher m = p.matcher( string );
        if( m.find() )
            return new RegexMatch( m.start(), m.end(), m );
        return new RegexMatch();
    }
    
    public static String getWebPage( String sUrl ){
        StringBuilder page = new StringBuilder();
        try{
            URL url = new URL( sUrl.replace(" ", "%20"));
            InputStream is = url.openStream();
            DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
            
            String line;
            while((line = dis.readLine()) != null)
                page.append(line);
            
        } catch( Exception e) {}
        
        return page.toString();
    }
    
    public static String formatHour(long millis){
        return (new SimpleDateFormat("HH:mm:ss").format(new Date(millis)));
    }
    
    public static Logger CreateLogger(){
        Logger l = Logger.getLogger("Log");
        try {
            FileHandler fh = new FileHandler( LogFile );
            Formatter formatter = new Formatter(){
                @Override
                public String format(LogRecord record){
                    return  "[" + formatHour(record.getMillis())
                            + "][" + record.getLevel() + "] "
                            + record.getMessage() 
//                            + "    (" + record.getSourceClassName() 
//                            + ":" + record.getSourceMethodName()
//                            + ")"
                            + "\n";
                }
            };
            fh.setFormatter(formatter);
            l.addHandler(fh);
        } catch (SecurityException | IOException e) { }
        return l;
    }
    
    public static List<String> ReadFile( String file ){
        List<String> r = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line = br.readLine()) != null)
                r.add(line);
            br.close();
        } catch (IOException e) {}
        return r;
    }

    public static void WriteFile( String file, List<String> toWrite ){
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for(String line : toWrite)
                bw.write(line + "\n");
            bw.close();
        } catch (IOException e) {}
    }

    public static void WriteFile( String file, String toWrite ){
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(toWrite);
            bw.close();
        } catch (IOException e) {}
    }
    
    public static String getExtension( String file ){
        String ext = file.substring( file.lastIndexOf(".") );
        return ext;
    }
    
    public static String getPath( String file ){
        String ext = file.substring( 0, file.lastIndexOf("\\") + 1 );
        return ext;
    }
}