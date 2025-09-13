package net.norius.voteSystem.vote;

import java.util.Date;

public class VoteData {

    private int streak;
    private double points;
    private double bank;
    private Date lastVote1;
    private Date lastVote2;
    private Date lastInterest;
    private Date lastPause;

    public VoteData(int streak, double points, double bank, Date lastVote1, Date lastVote2, Date lastInterest, Date lastPause) {
        this.streak = streak;
        this.points = points;
        this.bank = bank;
        this.lastVote1 = lastVote1;
        this.lastVote2 = lastVote2;
        this.lastInterest = lastInterest;
        this.lastPause = lastPause;
    }

    public Date getLastPause() {
        return lastPause;
    }

    public void setLastPause(Date lastPause) {
        this.lastPause = lastPause;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public double getBank() {
        return bank;
    }

    public void setBank(double bank) {
        this.bank = bank;
    }

    public Date getLastVote1() {
        return lastVote1;
    }

    public void setLastVote1(Date lastVote1) {
        this.lastVote1 = lastVote1;
    }

    public Date getLastVote2() {
        return lastVote2;
    }

    public void setLastVote2(Date lastVote2) {
        this.lastVote2 = lastVote2;
    }

    public Date getLastInterest() {
        return lastInterest;
    }

    public void setLastInterest(Date lastInterest) {
        this.lastInterest = lastInterest;
    }
}
