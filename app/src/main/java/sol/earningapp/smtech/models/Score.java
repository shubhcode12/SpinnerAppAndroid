package sol.earningapp.smtech.models;

public class Score {
    private long scoreID;
    private long userID;
    private int spinRemain;
    private String currentDate;
    private String points;

    public Score() {
    }

    public long getScoreID() {
        return scoreID;
    }

    public void setScoreID(long scoreID) {
        this.scoreID = scoreID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public int getSpinRemain() {
        return spinRemain;
    }

    public void setSpinRemain(int spinRemain) {
        this.spinRemain = spinRemain;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
