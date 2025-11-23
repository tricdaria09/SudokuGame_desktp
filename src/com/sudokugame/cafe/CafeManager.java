package com.sudokugame.cafe;

import javax.swing.*;
import java.awt.*;

public class CafeManager {
    private int money;
    private int coffeeLevel;
    private int pastryLevel;
    private int decorLevel;

    public CafeManager() {
        this.money = 100; // Bani inițiali
        this.coffeeLevel = 1;
        this.pastryLevel = 1;
        this.decorLevel = 1;
    }

    public void showCafe() {
        JFrame cafeFrame = new JFrame("Cafenea Ta ☕");
        cafeFrame.setSize(500, 400);
        cafeFrame.setLocationRelativeTo(null);
        cafeFrame.setResizable(false);

        // 🖼️ PANEL PRINCIPAL CU FUNDAL
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 🖼️ Pentru imagine de fundal cafenea, de-comentează:
                // ImageIcon background = new ImageIcon("images/cafe_background.jpg");
                // g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setBackground(new Color(245, 222, 179));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 🎯 TITLU
        JLabel titleLabel = new JLabel("☕ Cafenea Ta", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(139, 69, 19));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 💰 INFORMATII BANI SI NIVELURI
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        infoPanel.setBackground(new Color(245, 222, 179, 200));
        infoPanel.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 2));

        JLabel moneyLabel = new JLabel("💰 Bani: " + money + "€", SwingConstants.CENTER);
        moneyLabel.setFont(new Font("Arial", Font.BOLD, 20));
        moneyLabel.setForeground(new Color(0, 100, 0));

        JLabel statsLabel = new JLabel(
                "☕ Nivel " + coffeeLevel +
                        "   🍰 Nivel " + pastryLevel +
                        "   🎨 Nivel " + decorLevel,
                SwingConstants.CENTER
        );
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        infoPanel.add(moneyLabel);
        infoPanel.add(statsLabel);
        mainPanel.add(infoPanel, BorderLayout.CENTER);

        // ⬆️ PANEL UPGRADE-URI
        JPanel upgradePanel = new JPanel(new GridLayout(3, 1, 15, 15));
        upgradePanel.setBackground(new Color(245, 222, 179, 200));
        upgradePanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton upgradeCoffee = createUpgradeButton("☕ Upgrade Cafea - 100€");
        JButton upgradePastry = createUpgradeButton("🍰 Upgrade Patiserie - 150€");
        JButton upgradeDecor = createUpgradeButton("🎨 Upgrade Decor - 200€");

        upgradeCoffee.addActionListener(e -> upgradeCoffee(moneyLabel, statsLabel));
        upgradePastry.addActionListener(e -> upgradePastry(moneyLabel, statsLabel));
        upgradeDecor.addActionListener(e -> upgradeDecor(moneyLabel, statsLabel));

        upgradePanel.add(upgradeCoffee);
        upgradePanel.add(upgradePastry);
        upgradePanel.add(upgradeDecor);

        mainPanel.add(upgradePanel, BorderLayout.SOUTH);

        cafeFrame.add(mainPanel);
        cafeFrame.setVisible(true);
    }

    private JButton createUpgradeButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(139, 69, 19));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(160, 82, 45));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(139, 69, 19));
            }
        });

        return button;
    }

    private void upgradeCoffee(JLabel moneyLabel, JLabel statsLabel) {
        int cost = 100;
        if (money >= cost) {
            money -= cost;
            coffeeLevel++;
            updateLabels(moneyLabel, statsLabel);
            JOptionPane.showMessageDialog(null,
                    "☕ Cafea upgradată la nivelul " + coffeeLevel + "!\n" +
                            "Cafeaua ta este acum și mai delicioasă!",
                    "Upgrade Reușit",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    "❌ Fonduri insuficiente!\n" +
                            "Mai ai nevoie de " + (cost - money) + "€",
                    "Fonduri insuficiente",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void upgradePastry(JLabel moneyLabel, JLabel statsLabel) {
        int cost = 150;
        if (money >= cost) {
            money -= cost;
            pastryLevel++;
            updateLabels(moneyLabel, statsLabel);
            JOptionPane.showMessageDialog(null,
                    "🍰 Patiserie upgradată la nivelul " + pastryLevel + "!\n" +
                            "Prăjiturile tale sunt acum și mai gustoase!",
                    "Upgrade Reușit",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    "❌ Fonduri insuficiente!\n" +
                            "Mai ai nevoie de " + (cost - money) + "€",
                    "Fonduri insuficiente",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void upgradeDecor(JLabel moneyLabel, JLabel statsLabel) {
        int cost = 200;
        if (money >= cost) {
            money -= cost;
            decorLevel++;
            updateLabels(moneyLabel, statsLabel);
            JOptionPane.showMessageDialog(null,
                    "🎨 Decor upgradat la nivelul " + decorLevel + "!\n" +
                            "Cafeneaua ta arată acum fantastic!",
                    "Upgrade Reușit",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    "❌ Fonduri insuficiente!\n" +
                            "Mai ai nevoie de " + (cost - money) + "€",
                    "Fonduri insuficiente",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateLabels(JLabel moneyLabel, JLabel statsLabel) {
        moneyLabel.setText("💰 Bani: " + money + "€");
        statsLabel.setText(
                "☕ Nivel " + coffeeLevel +
                        "   🍰 Nivel " + pastryLevel +
                        "   🎨 Nivel " + decorLevel
        );
    }

    public void addMoney(int amount) {
        money += amount;
    }

    public int getMoney() {
        return money;
    }

    public int getCoffeeLevel() {
        return coffeeLevel;
    }

    public int getPastryLevel() {
        return pastryLevel;
    }

    public int getDecorLevel() {
        return decorLevel;
    }
}