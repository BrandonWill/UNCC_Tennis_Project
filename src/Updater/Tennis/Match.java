package Updater.Tennis;

/**
 * Created by Brandon on 1/25/2017.
 */
public class Match {
    private String matchType; // 64, 32, 16, QF, SF, FR
    private String record; //W for win, L for lose
    private String partnerData; //H2H if no partner
    private String opponentName; //If doubles, both opponents here
    private String score; //ex, 6-0 6-1
    private static int matchID = 0;
    private int ID;

    public Match(String matchType, String record, String partnerData, String opponentName, String score) {
        this.matchType = matchType;
        this.record = record;
        this.partnerData = partnerData;
        this.opponentName = opponentName;
        this.score = score;
        matchID++;
        ID = matchID;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getPartnerData() {
        return partnerData;
    }

    public void setPartnerData(String partnerData) {
        this.partnerData = partnerData;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getID() {
        return ID;
    }
}
