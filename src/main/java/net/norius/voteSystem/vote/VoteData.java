package net.norius.voteSystem.vote;

import java.util.Date;

public class VoteData {

    private int streak;
    private int points;
    private int bank;
    private Date lastVote1;
    private Date lastVote2;
    private int activeVoteDays;
    private int interestPoints;
    private Date lastInterest;

    public VoteData(int streak, int points, int bank, Date lastVote1, Date lastVote2, int activeVoteDays, int interestPoints, Date lastInterest) {
        this.streak = streak;
        this.points = points;
        this.bank = bank;
        this.lastVote1 = lastVote1;
        this.lastVote2 = lastVote2;
        this.activeVoteDays = activeVoteDays;
        this.interestPoints = interestPoints;
        this.lastInterest = lastInterest;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getBank() {
        return bank;
    }

    public void setBank(int bank) {
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

    public int getActiveVoteDays() {
        return activeVoteDays;
    }

    public void setActiveVoteDays(int activeVoteDays) {
        this.activeVoteDays = activeVoteDays;
    }

    public int getInterestPoints() {
        return interestPoints;
    }

    public void setInterestPoints(int interestPoints) {
        this.interestPoints = interestPoints;
    }

    public Date getLastInterest() {
        return lastInterest;
    }

    public void setLastInterest(Date lastInterest) {
        this.lastInterest = lastInterest;
    }
}
