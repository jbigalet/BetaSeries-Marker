package apibetaseries;

import java.util.regex.Matcher;

public class RegexMatch {
    boolean isFound;
    int startingIndex;
    int endingIndex;
    String[] matches;
    Matcher matcher;
    
    public RegexMatch(){
        this.isFound = false;
    }
    
    public RegexMatch(int sI, int eI, Matcher m){
        this.isFound = true;
        this.startingIndex = sI;
        this.endingIndex = eI;
        matches = new String[m.groupCount()+1];
        for(int i=0; i<matches.length; i++)
            matches[i] = m.group(i);
        matcher = m;
    }
}
