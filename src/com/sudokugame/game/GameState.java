package com.sudokugame.game;

public class GameState {
    private int gamesPlayed;
    private int gamesWon;
    private int totalCoinsEarned;
    private long totalPlayTime;

    public GameState() {
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.totalCoinsEarned = 0;
        this.totalPlayTime = 0;
    }

    // Getters and setters
    public int getGamesPlayed() { return gamesPlayed; }
    public void incrementGamesPlayed() { gamesPlayed++; }

    public int getGamesWon() { return gamesWon; }
    public void incrementGamesWon() { gamesWon++; }

    public int getTotalCoinsEarned() { return totalCoinsEarned; }
    public void addCoins(int coins) { totalCoinsEarned += coins; }

    public long getTotalPlayTime() { return totalPlayTime; }
    public void addPlayTime(long seconds) { totalPlayTime += seconds; }

    public double getWinRate() {
        return gamesPlayed == 0 ? 0 : (double) gamesWon / gamesPlayed * 100;
    }
}