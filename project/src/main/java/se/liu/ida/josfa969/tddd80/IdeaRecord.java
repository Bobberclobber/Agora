package se.liu.ida.josfa969.tddd80;

/**
 * Created by Josef on 27/07/14.
 *
 * A class used to contain info regarding ideas
 * which are displayed at different
 * places throughout the app
 */
public class IdeaRecord {

    public String ideaId;
    public String ideaText;
    public String poster;
    public String approvalNum;

    public IdeaRecord(String ideaId, String ideaText, String poster, String approvalNum) {
        this.ideaId = ideaId;
        this.ideaText = ideaText;
        this.poster = poster;
        this.approvalNum = approvalNum;
    }
}
