package com.sudokugame.cafe;

public class Upgrade {
    private String name;
    private String category;
    private int currentLevel;
    private int maxLevel;
    private int baseCost;
    private double costMultiplier;
    private String[] levelDescriptions;

    public Upgrade(String name, String category, int baseCost, int maxLevel) {
        this.name = name;
        this.category = category;
        this.currentLevel = 1;
        this.maxLevel = maxLevel;
        this.baseCost = baseCost;
        this.costMultiplier = 1.5;
        this.levelDescriptions = generateLevelDescriptions();
    }

    private String[] generateLevelDescriptions() {
        String[] descriptions = new String[maxLevel];
        for (int i = 0; i < maxLevel; i++) {
            int level = i + 1;
            switch (category) {
                case "coffee":
                    descriptions[i] = "Produce " + (level * 10) + " coins/oră";
                    break;
                case "pastry":
                    descriptions[i] = "Bonus " + (level * 5) + "% la reward-uri";
                    break;
                case "decor":
                    descriptions[i] = "Atrage " + (level * 3) + " clienți/oră";
                    break;
                case "music":
                    descriptions[i] = "Crește satisfacția cu " + (level * 2) + "%";
                    break;
                case "lighting":
                    descriptions[i] = "Îmbunătățește atmosfera cu " + (level * 4) + "%";
                    break;
                default:
                    descriptions[i] = "Nivel " + level;
            }
        }
        return descriptions;
    }

    public boolean canUpgrade() {
        return currentLevel < maxLevel;
    }

    public int getUpgradeCost() {
        return (int) (baseCost * Math.pow(costMultiplier, currentLevel - 1));
    }

    public boolean upgrade() {
        if (canUpgrade()) {
            currentLevel++;
            return true;
        }
        return false;
    }

    public int getBonusValue() {
        switch (category) {
            case "coffee": return currentLevel * 10;
            case "pastry": return currentLevel * 5;
            case "decor": return currentLevel * 3;
            case "music": return currentLevel * 2;
            case "lighting": return currentLevel * 4;
            default: return currentLevel;
        }
    }

    public double getMultiplierBonus() {
        switch (category) {
            case "pastry": return 1.0 + (currentLevel * 0.05);
            case "music": return 1.0 + (currentLevel * 0.02);
            case "lighting": return 1.0 + (currentLevel * 0.03);
            default: return 1.0;
        }
    }

    // Getters
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getCurrentLevel() { return currentLevel; }
    public int getMaxLevel() { return maxLevel; }
    public String getCurrentDescription() {
        return levelDescriptions[Math.min(currentLevel - 1, maxLevel - 1)];
    }
    public String getNextLevelDescription() {
        if (currentLevel < maxLevel) {
            return levelDescriptions[currentLevel];
        }
        return "Nivel maxim atins!";
    }

    public String getProgress() {
        return currentLevel + "/" + maxLevel;
    }

    public double getProgressPercentage() {
        return (double) currentLevel / maxLevel * 100;
    }
}