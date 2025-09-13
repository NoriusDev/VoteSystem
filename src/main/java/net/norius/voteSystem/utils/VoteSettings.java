package net.norius.voteSystem.utils;

public record VoteSettings (int streakResetHours, int streakPauseHours,
                           double interestPoints, double requiredBankPoints,
                           int requiredStreak, int intervallDays) {
}
