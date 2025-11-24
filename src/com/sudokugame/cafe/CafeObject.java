package com.sudokugame.cafe;

import com.sudokugame.utils.AssetsLoader;

import java.awt.*;
import java.awt.image.ImageObserver;

public class CafeObject {
    private String name;
    private int x, y, width, height;
    private int level;
    private int baseIncome;
    private String imageKey;
    private boolean isHighlighted;

    public CafeObject(String name, int x, int y, int width, int height, String imageKey) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.level = 1;
        this.baseIncome = calculateBaseIncome(name);
        this.imageKey = imageKey;
        this.isHighlighted = false;
    }

    private int calculateBaseIncome(String name) {
        switch (name.toLowerCase()) {
            case "coffee machine": return 20;
            case "pastry counter": return 15;
            case "register": return 10;
            case "table": return 5;
            default: return 10;
        }
    }

    public void draw(Graphics2D g2d) {
        Image image = AssetsLoader.getImage(imageKey);
        if (image != null) {
            g2d.drawImage(image, x, y, width, height, (ImageObserver) null);
        } else {
            if (isHighlighted) {
                g2d.setColor(new Color(255, 255, 0, 100));
            } else {
                g2d.setColor(new Color(139, 69, 19, 200));
            }
            g2d.fillRect(x, y, width, height);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString(name, x + 5, y + height/2);
        }

        if (isHighlighted) {
            g2d.setColor(new Color(255, 255, 0, 80));
            g2d.fillRect(x, y, width, height);
        }

        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Lv." + level, x + width - 30, y + 20);
    }

    public boolean contains(int pointX, int pointY) {
        return pointX >= x && pointX <= x + width &&
                pointY >= y && pointY <= y + height;
    }

    public void upgrade() {
        level++;
    }

    public int getIncome() {
        return baseIncome * level;
    }

    public int getUpgradeCost() {
        return (int) (100 * Math.pow(1.5, level - 1));
    }

    public int getSatisfactionBonus() {
        return level * 2;
    }

    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void setHighlighted(boolean highlighted) { isHighlighted = highlighted; }
}