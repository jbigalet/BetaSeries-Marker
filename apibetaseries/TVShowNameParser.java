package apibetaseries;

public class TVShowNameParser {
    boolean isFound = false;
    String TVShow = "";
    String BSUrl = null;
    String BSShow = null;
    int Season = -1;
    int Episode = -1;
    
    private String[] regexes = { "[sS]([0-9]+)[eE]([0-9]+)",
                                 "([0-9]+)x([0-9]+)",
                                 "[^0-9]([0-9])([0-9][0-9])[^0-9]"
                               };
    
    public TVShowNameParser(String name){
        // Remove trailing noise
        name = name.substring( Utils.matchRegex(name, "[a-zA-Z]").startingIndex );
        System.out.println(name);
        
        // Searching for season/episode
        for(String regex : regexes){
            RegexMatch rm = Utils.matchRegex(name, regex);
            if(rm.isFound){
                Season = Integer.parseInt( rm.matches[1] );
                Episode = Integer.parseInt( rm.matches[2] );
                TVShow = name.substring(0, rm.startingIndex);
                isFound = true;
                break;
            }
        }
        
        TVShow = TVShow.replace('.', ' ');
        TVShow = TVShow.trim();
        
        if( isFound ){
            String[] apiSearch = APIHandler.searchShow(TVShow);
            if(apiSearch[0] == null || apiSearch[0].isEmpty())
                isFound = false;
            else {
                BSUrl = apiSearch[0];
                BSShow = apiSearch[1];
            }
        }
    }
    
    @Override
    public String toString(){
        if( !isFound )
            return "";
        else
            return BSShow + " - [" 
                    + Utils.trailingZeroNumber(Season, 2)
                    + "x" 
                    + Utils.trailingZeroNumber(Episode, 2)
                    + "]";
    }
    
}
