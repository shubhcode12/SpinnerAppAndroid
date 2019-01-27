package sol.earningapp.smtech.models;

public class Withdraw {
    private long withdrawID;
    private long userID;
    private String withdrawPoints;
    private String withdrawDate;
    private boolean isPaid;

    public Withdraw() {
    }

    public long getWithdrawID() {
        return withdrawID;
    }

    public void setWithdrawID(long withdrawID) {
        this.withdrawID = withdrawID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getWithdrawPoints() {
        return withdrawPoints;
    }

    public void setWithdrawPoints(String withdrawPoints) {
        this.withdrawPoints = withdrawPoints;
    }

    public String getWithdrawDate() {
        return withdrawDate;
    }

    public void setWithdrawDate(String withdrawDate) {
        this.withdrawDate = withdrawDate;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public boolean isPaid() {
        return isPaid;
    }
}
