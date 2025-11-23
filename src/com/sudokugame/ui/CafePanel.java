package com.sudokugame.ui;

import com.sudokugame.cafe.CafeManager;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class CafePanel extends JFrame {
    private CafeManager cafeManager;
    private MainMeniu mainMeniu;

    public CafePanel(MainMeniu mainMeniu, CafeManager cafeManager) {
        this.mainMeniu = mainMeniu;
        this.cafeManager = cafeManager;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("☕ Cafenea Ta");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 222, 179));

        // Header
        JPanel headerPanel = createHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content - Upgrade cards
        JPanel contentPanel = createContentPanel();
        mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);

        // Footer - Statistics
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(139, 69, 19));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(getWidth(), 100));

        JLabel titleLabel = new JLabel("☕ Cafenea Ta", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));

        JLabel moneyLabel = new JLabel("💰 " + cafeManager.getMoney() + " coins");
        moneyLabel.setForeground(Color.WHITE);
        moneyLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JButton backButton = new JButton("← Înapoi");
        backButton.setBackground(new Color(210, 180, 140));
        backButton.setForeground(Color.BLACK);
        backButton.addActionListener(e -> goBack());

        panel.add(moneyLabel, BorderLayout.WEST);
        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 15, 15));
        panel.setBackground(new Color(245, 222, 179));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(createUpgradeCard("☕ Cafea", "coffee",
                "Crește veniturile pasive", cafeManager.getCoffeeLevel(), 100));
        panel.add(createUpgradeCard("🍰 Patiserie", "pastry",
                "Crește bonus-ul la joc", cafeManager.getPastryLevel(), 150));
        panel.add(createUpgradeCard("🎨 Decor", "decor",
                "Îmbunătățește atmosfera", cafeManager.getDecorLevel(), 200));
        panel.add(createUpgradeCard("🎵 Muzică", "music",
                "Atrage mai mulți clienți", cafeManager.getMusicLevel(), 120));
        panel.add(createUpgradeCard("💡 Lumină", "lighting",
                "Crește nivelul de comfort", cafeManager.getLightingLevel(), 180));

        return panel;
    }

    private JPanel createUpgradeCard(String name, String type, String description, int level, int baseCost) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 180, 140), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Left side - Icon and info
        JPanel infoPanel = new JPanel(new BorderLayout(10, 10));
        infoPanel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel(name.split(" ")[0]); // Emoji icon
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(name + " - Nivel " + level);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(Color.GRAY);

        textPanel.add(nameLabel);
        textPanel.add(descLabel);

        infoPanel.add(iconLabel, BorderLayout.WEST);
        infoPanel.add(textPanel, BorderLayout.CENTER);

        // Right side - Upgrade button
        int cost = baseCost * level;
        JButton upgradeButton = new JButton("Upgrade\n" + cost + " coins");
        upgradeButton.setFont(new Font("Arial", Font.BOLD, 14));
        upgradeButton.setBackground(new Color(139, 69, 19));
        upgradeButton.setForeground(Color.WHITE);
        upgradeButton.setPreferredSize(new Dimension(120, 60));
        upgradeButton.addActionListener(e -> upgradeItem(type, cost));

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(upgradeButton, BorderLayout.EAST);

        return card;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBackground(new Color(210, 180, 140));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel incomeLabel = new JLabel("📈 Venit/h: " + cafeManager.calculateHourlyIncome() + " coins");
        JLabel bonusLabel = new JLabel("⭐ Bonus joc: +" + cafeManager.getCafeBonus() + " coins");
        JLabel levelLabel = new JLabel("🏆 Nivel total: " + cafeManager.getTotalLevel());
        JLabel customersLabel = new JLabel("👥 Clienți: " + (cafeManager.getTotalLevel() * 5));

        incomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bonusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        levelLabel.setFont(new Font("Arial", Font.BOLD, 14));
        customersLabel.setFont(new Font("Arial", Font.BOLD, 14));

        panel.add(incomeLabel);
        panel.add(bonusLabel);
        panel.add(levelLabel);
        panel.add(customersLabel);

        return panel;
    }

    private void upgradeItem(String type, int cost) {
        if (cafeManager.canAfford(cost)) {
            cafeManager.addMoney(-cost);

            // Actualizează afișajul
            refreshDisplay();
            mainMeniu.updateMoneyDisplay();

            JOptionPane.showMessageDialog(this,
                    "✅ Upgrade reușit!\n" +
                            "💰 Cost: " + cost + " coins\n" +
                            "🎯 " + getUpgradeName(type) + " a fost îmbunătățit!",
                    "Upgrade Reușit",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "❌ Fonduri insuficiente!\n" +
                            "💰 Mai ai nevoie de " + (cost - cafeManager.getMoney()) + " coins",
                    "Fonduri Insuficiente",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private String getUpgradeName(String type) {
        switch (type) {
            case "coffee": return "Cafeaua";
            case "pastry": return "Patiseria";
            case "decor": return "Decorul";
            case "music": return "Muzica";
            case "lighting": return "Lumina";
            default: return "Upgrade-ul";
        }
    }

    private void refreshDisplay() {
        // Re-creates the UI to reflect changes
        getContentPane().removeAll();
        initializeUI();
        revalidate();
        repaint();
    }

    private void goBack() {
        this.dispose();
        mainMeniu.setVisible(true);
    }
}