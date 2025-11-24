package com.sudokugame.game;

import java.awt.Color;

public enum Difficulty {
    EASY("Easy", Color.GREEN, 50, 35),
    MEDIUM("Medium", Color.ORANGE, 100, 45),
    HARD("Hard", Color.RED, 200, 55);

    private final String name;
    private final Color color;
    private final int baseReward;
    private final int cellsToRemove;

    Difficulty(String name, Color color, int baseReward, int cellsToRemove) {
        this.name = name;
        this.color = color;
        this.baseReward = baseReward;
        this.cellsToRemove = cellsToRemove;
    }

    public String getName() { return name; }
    public Color getColor() { return color; }
    public int getBaseReward() { return baseReward; }
    public int getCellsToRemove() { return cellsToRemove; }
}