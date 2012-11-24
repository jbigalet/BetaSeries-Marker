package apibetaseries;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class APIHandler {
    
	// to fill with your informations
	
    public static String Key = "xxx";
    public static String Login = "xxx";
    public static String Pwd = "xxx";
    public static String Token = identification();

    private static JSONParser parser = new JSONParser();

    public APIHandler() {
    }
    
    public static void createCSV() {
        try {
            String episodesList = Utils.getWebPage("http://api.betaseries.com/episodes/list?limit=1&token=" + Token + "&key=" + Key);
            JSONArray episodes = (JSONArray) ((JSONObject) parser.parse(episodesList)).get("shows");
            
            String ids = "";
            for(Object episode : episodes)
                ids += (Long)((JSONObject)episode).get("id") + ",";
            ids = ids.substring(0, ids.length()-1);
            
            
            String showsList = Utils.getWebPage("http://api.betaseries.com/shows/display?id=" + ids + "&user=true&token=" + Token + "&key=" + Key);
            JSONArray shows = (JSONArray) ((JSONObject) parser.parse(showsList)).get("shows");
            
            List<String> csv = new ArrayList<String>();
            csv.add("Title;Seasons;Episodes;Year;Network;Status;Remaining;Perc;Last Seen;IMDB Rating");
            for(Object show : shows){
                JSONObject oshow = (JSONObject) show;
                JSONObject ouser = (JSONObject) oshow.get("user");
                
                String tvdb = Utils.getWebPage("http://www.thetvdb.com/data/series/" + oshow.get("thetvdb_id") + "/");
                RegexMatch rm = Utils.matchRegex(tvdb, "IMDB_ID>([^<]+)<");
                String imdbRating = "0";
                if( rm.isFound ){
                    String imdb = Utils.getWebPage("http://mymovieapi.com/?id=" + rm.matches[1] + "&type=json&plot=simple&episode=0&lang=en-US&aka=simple&release=simple&business=0&tech=0");
                    RegexMatch rm2 = Utils.matchRegex(imdb, "rating\\\": ([^,]+),");
                    if( rm2.isFound )
                        imdbRating = rm2.matches[1];
                }
                System.out.println("Fetched imdb rating for '" + oshow.get("title") + "': " + imdbRating);
                
                csv.add( 
                     "\"" + oshow.get("title") + "\""
                    + ";" + oshow.get("seasons")
                    + ";" + oshow.get("episodes")
                    + ";" + oshow.get("creation")
                    + ";" + oshow.get("network")
                    + ";" + oshow.get("status")
                        
                    + ";" + ouser.get("remaining")
                    + ";" + ouser.get("status").toString().replace('.', ',')
                    + ";" + ouser.get("last")
                    
                    + ";" + imdbRating.replace('.', ',')
                );
            }
            Utils.WriteFile("C:\\betaseries.csv", csv);
            
            System.out.println("CSV successfully exported");
            
        } catch (ParseException ex) {
            System.out.println("Impossible to parse json: " + ex.toString());
        }
    }
    
    public static void viewEpisode( TVShowNameParser toView ){
        if(toView.BSUrl.isEmpty())
            Utils.logger.warning("Unable to find " + toView.TVShow);
        else{
            Utils.logger.info("Sending to BetaSeries: " + toView.toString());
            
            // Add show to user
            Utils.getWebPage("http://api.betaseries.com/shows/add/" 
                    + toView.BSUrl + ".json"
                    + "?token=" + Token
                    + "&key=" + Key);
            
            // Mark episode as watched
            Utils.getWebPage("http://api.betaseries.com/members/watched/" 
                    + toView.BSUrl + ".json"
                    + "?season=" + toView.Season
                    + "&episode=" + toView.Episode
                    + "&token=" + Token
                    + "&key=" + Key);
            Utils.logger.info("Sended.");
        }
    }
    
    public static String identification(){
        String json = Utils.getWebPage( "http://api.betaseries.com/members/auth.json?login=" + Login + "&password=" + Pwd + "&key=" + Key);
        RegexMatch rm = Utils.matchRegex(json, "\\\"token\\\":\\\"([^\\\"]+)\\\"");
        if( rm.isFound ){
            Utils.logger.info("Logging as " + Login + " successful");
            return rm.matches[1];
        }
        else{
            Utils.logger.warning("Impossible to log as " + Login);
            return "";
        }
    }
    
    public static String[] searchShow( String show ){
        String json = Utils.getWebPage( "http://api.betaseries.com/shows/search.json?title=" + show + "&key=" + Key);
        RegexMatch rm = Utils.matchRegex(json, "\\\"url\\\":\\\"([^\\\"]+)\\\",\\\"title\\\":\\\"([^\\\"]+)\\\"");
        if( rm.isFound )
            return new String[] {rm.matches[1], rm.matches[2]};
        else{
            Utils.logger.info("Impossible to find the show '" + show + "'");
            return new String[] {null, null};
        }
    }
    
    public static List<String> getTimeline( String member, int size ){
        String json = Utils.getWebPage( "http://api.betaseries.com/timeline/member/" + member + ".json?number=" + size + "&key=" + Key);
        Pattern p = Pattern.compile(  "\\\"type\\\":\\\"([^\\\"]+)\\\","
                                    + "\\\"ref\\\":\\\"([^\\\"]+)\\\","
                                    + "\\\"login\\\":\\\"([^\\\"]+)\\\","
                                    + "\\\"data\\\":\\{([^\\}]+)\\}" 
                                   );
        Matcher m = p.matcher( json );
        
        List<String> result = new ArrayList<>();
        while( m.find() )
            switch (m.group(1)) {
                case "add_serie":
                    result.add("Add show: '" + m.group(2) + "'");
                    break;
                case "markas":
                    RegexMatch rmNumber = Utils.matchRegex( m.group(4), "\\\"number\\\":\\\"([^\\\"]+)\\\"" );
                    if( rmNumber.isFound )
                        result.add("Add episode: '" + m.group(2) + " - " + rmNumber.matches[1] + "'");
                    break;
            }
        return result;
    }
    
}
