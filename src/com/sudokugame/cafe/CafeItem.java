package com.sudokugame.cafe;

public class CafeItem {
    private String name;
    private String type;
    private int level;
    private int baseCost;
    private String description;
    private String emoji;

    public CafeItem(String name, String type, int baseCost, String description, String emoji) {
        this.name = name;
        this.type = type;
        this.level = 1;
        this.baseCost = baseCost;
        this.description = description;
        this.emoji = emoji;
    }

    // Getters
    public String getName() { return name; }
    public String getType() { return type; }
    public int getLevel() { return level; }
    public int getCurrentCost() { return baseCost * level; }
    public int getNextLevelCost() { return baseCost * (level + 1); }
    public String getDescription() { return description; }
    public String getEmoji() { return emoji; }

    // Upgrade the item
    public void upgrade() {
        level++;
    }

    // Calculate bonus based on level
    public int getIncomeBonus() {
        switch (type) {
            case "coffee": return level * 10;
            case "pastry": return level * 5;
            case "decor": return level * 3;
            case "music": return level * 2;
            case "lighting": return level * 4;
            default: return level;
        }
    }

    public int getGameBonus() {
        switch (type) {
            case "coffee": return level * 2;
            case "pastry": return level * 3;
            case "decor": return level;
            case "music": return level;
            case "lighting": return level * 2;
            default: return 0;
        }
    }

    @Override
    public String toString() {
        return emoji + " " + name + " (Nivel " + level + ")";
    }
}