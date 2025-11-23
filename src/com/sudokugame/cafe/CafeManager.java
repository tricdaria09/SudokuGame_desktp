package com.sudokugame.cafe;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.Timer;

public class CafeManager {
    private int money;
    private Map<String, CafeItem> items;
    private Map<String, Upgrade> upgrades;
    private int totalEarnings;
    private int customers;
    private Timer incomeTimer;

    public CafeManager() {
        this.money = 150; // Bani de start
        this.totalEarnings = 0;
        this.customers = 10;
        this.items = new HashMap<>();
        this.upgrades = new HashMap<>();

        initializeItems();
        initializeUpgrades();
        startPassiveIncome();
    }

    private void initializeItems() {
        items.put("coffee", new CafeItem("Cafea Premium", "coffee", 100,
                "Crește veniturile pasive", "☕"));
        items.put("pastry", new CafeItem("Patiserie Artizanală", "pastry", 150,
                "Crește bonus-ul la joc", "🍰"));
        items.put("decor", new CafeItem("Decor Vintage", "decor", 200,
                "Atrage mai mulți clienți", "🎨"));
        items.put("music", new CafeItem("Sistem Audio", "music", 120,
                "Îmbunătățește experiența", "🎵"));
        items.put("lighting", new CafeItem("Iluminat Ambient", "lighting", 180,
                "Crește nivelul de comfort", "💡"));
    }

    private void initializeUpgrades() {
        upgrades.put("coffee", new Upgrade("Cafea de Specialitate", "coffee", 100, 10));
        upgrades.put("pastry", new Upgrade("Brusc Artizanal", "pastry", 150, 8));
        upgrades.put("decor", new Upgrade("Design Interior", "decor", 200, 5));
        upgrades.put("music", new Upgrade("Playlist Premium", "music", 120, 6));
        upgrades.put("lighting", new Upgrade("Iluminat LED", "lighting", 180, 7));
    }

    private void startPassiveIncome() {
        incomeTimer = new Timer();
        incomeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                generatePassiveIncome();
            }
        }, 60000, 60000); // La fiecare 60 de secunde (1 minut)
    }

    private void generatePassiveIncome() {
        int hourlyIncome = calculateHourlyIncome();
        int income = hourlyIncome / 60; // Income per minute
        money += income;
        totalEarnings += income;
        customers += getCustomersPerMinute();

        // Actualizează UI-ul dacă este deschis
        SwingUtilities.invokeLater(() -> {
            // Aici poți actualiza UI-ul dacă este deschis
        });
    }

    public void showCafe() {
        JFrame cafeFrame = new JFrame("☕ Cafenea Ta - Manager");
        cafeFrame.setSize(800, 900);
        cafeFrame.setLocationRelativeTo(null);
        cafeFrame.setResizable(false);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab-ul pentru Upgrade-uri
        tabbedPane.addTab("🛠️ Upgrade-uri", createUpgradesTab());

        // Tab-ul pentru Statistici
        tabbedPane.addTab("📊 Statistici", createStatsTab());

        // Tab-ul pentru Achievements
        tabbedPane.addTab("🏆 Achievements", createAchievementsTab());

        cafeFrame.add(tabbedPane);
        cafeFrame.setVisible(true);
    }

    private JPanel createUpgradesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 222, 179));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(139, 69, 19));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel moneyLabel = new JLabel("💰 " + money + " coins");
        moneyLabel.setForeground(Color.WHITE);
        moneyLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel incomeLabel = new JLabel("📈 " + calculateHourlyIncome() + " coins/h");
        incomeLabel.setForeground(Color.WHITE);
        incomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        headerPanel.add(moneyLabel, BorderLayout.WEST);
        headerPanel.add(incomeLabel, BorderLayout.EAST);

        // Content - Upgrade cards
        JPanel contentPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        contentPanel.setBackground(new Color(245, 222, 179));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (Upgrade upgrade : upgrades.values()) {
            contentPanel.add(createUpgradeCard(upgrade));
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUpgradeCard(Upgrade upgrade) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 180, 140), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Left side - Info
        JPanel infoPanel = new JPanel(new BorderLayout(10, 10));
        infoPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(upgrade.getName() + " - Nivel " + upgrade.getCurrentLevel());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel descLabel = new JLabel(upgrade.getCurrentDescription());
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(Color.GRAY);

        // Progress bar
        JProgressBar progressBar = new JProgressBar(0, upgrade.getMaxLevel());
        progressBar.setValue(upgrade.getCurrentLevel());
        progressBar.setString(upgrade.getProgress());
        progressBar.setStringPainted(true);
        progressBar.setForeground(getUpgradeColor(upgrade.getCategory()));

        JPanel textPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(nameLabel);
        textPanel.add(descLabel);
        textPanel.add(progressBar);

        infoPanel.add(textPanel, BorderLayout.CENTER);

        // Right side - Upgrade button
        int cost = upgrade.getUpgradeCost();
        JButton upgradeButton = new JButton("<html><center>Upgrade<br>" + cost + " coins</center></html>");
        upgradeButton.setFont(new Font("Arial", Font.BOLD, 14));
        upgradeButton.setBackground(canAfford(cost) ? new Color(139, 69, 19) : Color.GRAY);
        upgradeButton.setForeground(Color.WHITE);
        upgradeButton.setPreferredSize(new Dimension(120, 60));
        upgradeButton.setEnabled(upgrade.canUpgrade() && canAfford(cost));
        upgradeButton.addActionListener(e -> purchaseUpgrade(upgrade, cost, upgradeButton));

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(upgradeButton, BorderLayout.EAST);

        return card;
    }

    private JPanel createStatsTab() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 15, 15));
        panel.setBackground(new Color(245, 222, 179));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Statistici
        addStat(panel, "💰 Bani total câștigați:", totalEarnings + " coins");
        addStat(panel, "📈 Venit curent/oră:", calculateHourlyIncome() + " coins");
        addStat(panel, "👥 Clienți curenti:", customers + " clienți");
        addStat(panel, "⭐ Bonus total joc:", getTotalGameBonus() + "%");
        addStat(panel, "🏆 Nivel cafenea:", calculateCafeLevel() + "");
        addStat(panel, "🎯 Eficiență:", calculateEfficiency() + "%");

        // Info upgrade-uri
        addStat(panel, "☕ Nivel cafea:", upgrades.get("coffee").getCurrentLevel() + "");
        addStat(panel, "🍰 Nivel patiserie:", upgrades.get("pastry").getCurrentLevel() + "");
        addStat(panel, "🎨 Nivel decor:", upgrades.get("decor").getCurrentLevel() + "");
        addStat(panel, "🎵 Nivel muzică:", upgrades.get("music").getCurrentLevel() + "");
        addStat(panel, "💡 Nivel lumină:", upgrades.get("lighting").getCurrentLevel() + "");

        return panel;
    }

    private JScrollPane createAchievementsTab() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBackground(new Color(245, 222, 179));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Achievements
        panel.add(createAchievementCard("🏪 Deschide Cafeneaua", "Ai deschis prima ta cafenea", true));
        panel.add(createAchievementCard("💰 Primul Milion", "Câștigă 1,000,000 coins", money >= 1000000));
        panel.add(createAchievementCard("⭐ Master Barista", "Upgradează cafeaua la nivel maxim",
                upgrades.get("coffee").getCurrentLevel() >= 10));
        panel.add(createAchievementCard("🎨 Designer Expert", "Upgradează decorul la nivel maxim",
                upgrades.get("decor").getCurrentLevel() >= 5));
        panel.add(createAchievementCard("👑 Împăratul Cafelei", "Ai toate upgrade-urile la nivel maxim",
                isEverythingMaxLevel()));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        return scrollPane;
    }

    private JPanel createAchievementCard(String title, String description, boolean unlocked) {
        JPanel card = new JPanel(new BorderLayout(15, 15));
        card.setBackground(unlocked ? new Color(220, 255, 220) : new Color(255, 220, 220));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(unlocked ? Color.GREEN : Color.RED, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(unlocked ? Color.GREEN.darker() : Color.RED.darker());

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel statusLabel = new JLabel(unlocked ? "✅ DEBLOCAT" : "🔒 BLOCAT");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(unlocked ? Color.GREEN.darker() : Color.RED.darker());

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(card.getBackground());
        textPanel.add(titleLabel);
        textPanel.add(descLabel);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(statusLabel, BorderLayout.EAST);

        return card;
    }

    private void addStat(JPanel panel, String label, String value) {
        JLabel statLabel = new JLabel(label);
        statLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        valueLabel.setForeground(Color.BLUE.darker());

        panel.add(statLabel);
        panel.add(valueLabel);
    }

    private void purchaseUpgrade(Upgrade upgrade, int cost, JButton button) {
        if (canAfford(cost) && upgrade.canUpgrade()) {
            money -= cost;
            upgrade.upgrade();

            // Actualizează UI
            button.setBackground(canAfford(upgrade.getUpgradeCost()) ?
                    new Color(139, 69, 19) : Color.GRAY);
            button.setEnabled(upgrade.canUpgrade() && canAfford(upgrade.getUpgradeCost()));
            button.setText("<html><center>Upgrade<br>" + upgrade.getUpgradeCost() + " coins</center></html>");

            JOptionPane.showMessageDialog(null,
                    "✅ " + upgrade.getName() + " upgradat la nivelul " + upgrade.getCurrentLevel() + "!\n" +
                            "💰 Cost: " + cost + " coins\n" +
                            "🎯 " + upgrade.getNextLevelDescription(),
                    "Upgrade Reușit",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private Color getUpgradeColor(String category) {
        switch (category) {
            case "coffee": return new Color(139, 69, 19);
            case "pastry": return new Color(210, 105, 30);
            case "decor": return new Color(106, 90, 205);
            case "music": return new Color(30, 144, 255);
            case "lighting": return new Color(255, 215, 0);
            default: return Color.GRAY;
        }
    }

    // Calculatoare
    public int calculateHourlyIncome() {
        int income = 0;
        for (Upgrade upgrade : upgrades.values()) {
            income += upgrade.getBonusValue();
        }
        return income;
    }

    public int getTotalGameBonus() {
        int bonus = 0;
        for (Upgrade upgrade : upgrades.values()) {
            bonus += (int)((upgrade.getMultiplierBonus() - 1.0) * 100);
        }
        return bonus;
    }

    public int getCafeBonus() {
        return getTotalGameBonus();
    }

    public int calculateCafeLevel() {
        int level = 0;
        for (Upgrade upgrade : upgrades.values()) {
            level += upgrade.getCurrentLevel();
        }
        return level;
    }

    public int calculateEfficiency() {
        int maxPossible = 0;
        for (Upgrade upgrade : upgrades.values()) {
            maxPossible += upgrade.getMaxLevel();
        }
        return (int)((double) calculateCafeLevel() / maxPossible * 100);
    }

    public int getCustomersPerMinute() {
        return upgrades.get("decor").getBonusValue() / 10;
    }

    private boolean isEverythingMaxLevel() {
        for (Upgrade upgrade : upgrades.values()) {
            if (upgrade.canUpgrade()) return false;
        }
        return true;
    }

    // Getters
    public int getMoney() { return money; }
    public boolean canAfford(int amount) { return money >= amount; }
    public void addMoney(int amount) { money += amount; }
    public int getCoffeeLevel() { return upgrades.get("coffee").getCurrentLevel(); }
    public int getPastryLevel() { return upgrades.get("pastry").getCurrentLevel(); }
    public int getDecorLevel() { return upgrades.get("decor").getCurrentLevel(); }
    public int getMusicLevel() { return upgrades.get("music").getCurrentLevel(); }
    public int getLightingLevel() { return upgrades.get("lighting").getCurrentLevel(); }
    public int getTotalLevel() { return calculateCafeLevel(); }
}