package com.sudokugame.game;

import java.awt.Color;

public enum Difficulty {
    EASY("Ușor", Color.GREEN, 50, 35),
    MEDIUM("Mediu", Color.ORANGE, 100, 28),
    HARD("Greu", Color.RED, 200, 22);

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