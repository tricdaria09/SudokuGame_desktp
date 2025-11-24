package com.sudokugame.ui;

import com.sudokugame.cafe.CafeManager;
import com.sudokugame.cafe.CafeObject;
import com.sudokugame.cafe.Customer;
import com.sudokugame.utils.AssetsLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CafeScene extends JFrame {
    private MainMeniu mainMeniu;
    private CafeManager cafeManager;
    private List<CafeObject> cafeObjects;
    private JLabel moneyLabel;
    private JLabel satisfactionLabel;
    private JLabel customersLabel;

    public CafeScene(MainMeniu mainMeniu, CafeManager cafeManager) {
        this.mainMeniu = mainMeniu;
        this.cafeManager = cafeManager;
        this.cafeObjects = new ArrayList<>();
        initializeCafeObjects();
        initializeUI();
    }

    private void initializeCafeObjects() {
        cafeObjects.add(new CafeObject("Coffee Machine", 100, 200, 150, 200, "coffee_machine"));
        cafeObjects.add(new CafeObject("Pastry Counter", 300, 200, 150, 150, "pastry_counter"));
        cafeObjects.add(new CafeObject("Register", 500, 250, 120, 120, "register"));
        cafeObjects.add(new CafeObject("Table 1", 200, 400, 100, 100, "table"));
        cafeObjects.add(new CafeObject("Table 2", 400, 400, 100, 100, "table"));
        cafeObjects.add(new CafeObject("Table 3", 600, 400, 100, 100, "table"));

        for (CafeObject obj : cafeObjects) {
            cafeManager.addCafeObject(obj);
        }
    }

    private void initializeUI() {
        setTitle("My Cafe - Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel scenePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                Image background = AssetsLoader.getImage("cafe_scene");
                if (background != null) {
                    g2d.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                } else {
                    GradientPaint gradient = new GradientPaint(
                            0, 0, AssetsLoader.getColor("cafe_bg"),
                            getWidth(), getHeight(), AssetsLoader.getColor("menu_bg")
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(160, 120, 80));
                    g2d.fillRect(0, 500, getWidth(), getHeight() - 500);
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.fillRect(0, 0, getWidth(), 100);
                }

                for (CafeObject obj : cafeObjects) {
                    obj.draw(g2d);
                }

                drawCustomers(g2d);
                drawCustomerInteractions(g2d);
            }
        };

        scenePanel.setLayout(null);

        JPanel headerPanel = createHeaderPanel();
        scenePanel.add(headerPanel);
        headerPanel.setBounds(0, 0, getWidth(), 60);

        JPanel sidebarPanel = createSidebarPanel();
        scenePanel.add(sidebarPanel);
        sidebarPanel.setBounds(getWidth() - 250, 60, 250, getHeight() - 60);

        scenePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleObjectClick(e.getX(), e.getY());
            }
        });

        javax.swing.Timer refreshTimer = new javax.swing.Timer(100, e -> scenePanel.repaint());
        refreshTimer.start();

        add(scenePanel);
    }

    private void drawCustomers(Graphics2D g2d) {
        for (Customer customer : cafeManager.getCustomersList()) {
            customer.draw(g2d);
        }
    }

    private void drawCustomerInteractions(Graphics2D g2d) {
        for (Customer customer : cafeManager.getCustomersList()) {
            if (customer.isReadyToOrder()) {
                g2d.setColor(new Color(0, 200, 0, 200));
                g2d.fillOval(customer.getX() + customer.getWidth() - 15,
                        customer.getY() - 25, 25, 25);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.drawString("📝", customer.getX() + customer.getWidth() - 12,
                        customer.getY() - 10);
            }

            if (customer.isAngry()) {
                g2d.setColor(new Color(255, 0, 0, 200));
                g2d.fillOval(customer.getX() + customer.getWidth() - 15,
                        customer.getY() - 25, 25, 25);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.drawString("😠", customer.getX() + customer.getWidth() - 12,
                        customer.getY() - 10);
            }
        }
    }

    private void handleObjectClick(int x, int y) {
        for (Customer customer : cafeManager.getCustomersList()) {
            if (x >= customer.getX() && x <= customer.getX() + customer.getWidth() &&
                    y >= customer.getY() && y <= customer.getY() + customer.getHeight()) {
                showCustomerMenu(customer, x, y);
                return;
            }
        }

        for (CafeObject obj : cafeObjects) {
            if (obj.contains(x, y)) {
                showObjectMenu(obj, x, y);
                break;
            }
        }
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(139, 69, 19, 220));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        infoPanel.setOpaque(false);

        moneyLabel = new JLabel("💰 " + cafeManager.getFormattedMoney() + " coins");
        moneyLabel.setForeground(Color.WHITE);
        moneyLabel.setFont(new Font("Arial", Font.BOLD, 16));

        satisfactionLabel = new JLabel("😊 " + cafeManager.getSatisfaction() + "%");
        satisfactionLabel.setForeground(getSatisfactionColor(cafeManager.getSatisfaction()));
        satisfactionLabel.setFont(new Font("Arial", Font.BOLD, 16));

        customersLabel = new JLabel("👥 " + cafeManager.getCustomerCount() + "/" + cafeManager.getMaxCustomers());
        customersLabel.setForeground(Color.YELLOW);
        customersLabel.setFont(new Font("Arial", Font.BOLD, 16));

        infoPanel.add(moneyLabel);
        infoPanel.add(satisfactionLabel);
        infoPanel.add(customersLabel);

        JButton backButton = new JButton("← Back to Menu");
        backButton.setBackground(new Color(210, 180, 140));
        backButton.setForeground(Color.BLACK);
        backButton.setFont(new Font("Arial", Font.BOLD, 12));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> goBack());

        panel.add(infoPanel, BorderLayout.WEST);
        panel.add(backButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createSidebarPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(0, 0, 0, 200));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 12, 15, 12));

        JLabel titleLabel = new JLabel("🏪 Cafe Manager");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        addStat(panel, "Cafe Level", "⭐ " + cafeManager.getCafeLevel());
        addStat(panel, "Total Income", "💰 " + cafeManager.getTotalIncome() + " coins");
        addStat(panel, "Hourly Income", "📈 " + cafeManager.getHourlyIncome() + "/h");
        addStat(panel, "Games Won", "🏆 " + cafeManager.getGamesWon() + "/" + cafeManager.getGamesPlayed());
        addStat(panel, "Win Rate", "🎯 " + String.format("%.1f", cafeManager.getWinRate()) + "%");

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel upgradeTitle = new JLabel("⚡ Quick Upgrades");
        upgradeTitle.setForeground(Color.YELLOW);
        upgradeTitle.setFont(new Font("Arial", Font.BOLD, 16));
        upgradeTitle.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(upgradeTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        addUpgradeButton(panel, "☕ Coffee Quality", "coffee_quality");
        addUpgradeButton(panel, "🍰 Pastry Variety", "pastry_variety");
        addUpgradeButton(panel, "⚡ Service Speed", "service_speed");
        addUpgradeButton(panel, "🎨 Decor", "decor");
        addUpgradeButton(panel, "📢 Marketing", "marketing");

        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton upgradeAllButton = new JButton("🚀 Upgrade All Possible");
        upgradeAllButton.setAlignmentX(CENTER_ALIGNMENT);
        upgradeAllButton.setBackground(new Color(76, 175, 80));
        upgradeAllButton.setForeground(Color.WHITE);
        upgradeAllButton.setFocusPainted(false);
        upgradeAllButton.addActionListener(e -> upgradeAllPossible());
        panel.add(upgradeAllButton);

        return panel;
    }

    private void addStat(JPanel panel, String label, String value) {
        JPanel statPanel = new JPanel(new BorderLayout());
        statPanel.setOpaque(false);
        statPanel.setBorder(BorderFactory.createEmptyBorder(6, 5, 6, 5));

        JLabel labelL = new JLabel(label);
        JLabel valueL = new JLabel(value);

        labelL.setForeground(Color.LIGHT_GRAY);
        valueL.setForeground(Color.WHITE);
        labelL.setFont(new Font("Arial", Font.PLAIN, 12));
        valueL.setFont(new Font("Arial", Font.BOLD, 12));

        statPanel.add(labelL, BorderLayout.WEST);
        statPanel.add(valueL, BorderLayout.EAST);

        panel.add(statPanel);
    }

    private void addUpgradeButton(JPanel panel, String name, String upgradeKey) {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        int currentLevel = cafeManager.getUpgrades().get(upgradeKey);
        int cost = cafeManager.getUpgradeCosts().get(upgradeKey);

        JButton upgradeButton = new JButton("Lv." + currentLevel + " - " + cost + "¢");
        upgradeButton.setFont(new Font("Arial", Font.BOLD, 11));
        upgradeButton.setBackground(cafeManager.canAfford(cost) ? new Color(70, 130, 180) : Color.GRAY);
        upgradeButton.setForeground(Color.WHITE);
        upgradeButton.setFocusPainted(false);
        upgradeButton.addActionListener(e -> upgradeItem(upgradeKey, cost, upgradeButton));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        buttonPanel.add(nameLabel, BorderLayout.WEST);
        buttonPanel.add(upgradeButton, BorderLayout.EAST);

        panel.add(buttonPanel);
    }

    private void showObjectMenu(CafeObject obj, int x, int y) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem upgradeItem = new JMenuItem("⬆️ Upgrade (" + obj.getUpgradeCost() + " coins)");
        JMenuItem infoItem = new JMenuItem("ℹ️ Info");

        upgradeItem.addActionListener(e -> upgradeObject(obj));
        infoItem.addActionListener(e -> showObjectInfo(obj));

        menu.add(upgradeItem);
        menu.add(infoItem);

        menu.show(this, x, y);
    }

    private void showCustomerMenu(Customer customer, int x, int y) {
        JPopupMenu menu = new JPopupMenu();

        if (customer.isReadyToOrder()) {
            JMenuItem serveItem = new JMenuItem("☕ Take Order");
            serveItem.addActionListener(e -> serveCustomer(customer));
            menu.add(serveItem);
        }

        JMenuItem infoItem = new JMenuItem("ℹ️ Customer Info");
        infoItem.addActionListener(e -> showCustomerInfo(customer));
        menu.add(infoItem);

        menu.show(this, x, y);
    }

    private void upgradeObject(CafeObject obj) {
        if (cafeManager.canAfford(obj.getUpgradeCost())) {
            cafeManager.addMoney(-obj.getUpgradeCost());
            obj.upgrade();
            updateDisplay();
            repaint();

            JOptionPane.showMessageDialog(this,
                    "✅ " + obj.getName() + " upgraded to Level " + obj.getLevel() + "!\n" +
                            "💰 Income: +" + obj.getIncome() + " coins/hour\n" +
                            "😊 Satisfaction: +" + obj.getSatisfactionBonus() + "%",
                    "Upgrade Successful",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "❌ Not enough coins!\nNeed: " + obj.getUpgradeCost() + " coins",
                    "Insufficient Funds",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void upgradeItem(String upgradeKey, int cost, JButton button) {
        if (cafeManager.upgradeItem(upgradeKey)) {
            updateDisplay();
            updateButton(button, upgradeKey);

            JOptionPane.showMessageDialog(this,
                    "✅ " + getUpgradeName(upgradeKey) + " upgraded!\n" +
                            "💰 Cost: " + cost + " coins\n" +
                            "⭐ New level: " + cafeManager.getUpgrades().get(upgradeKey),
                    "Upgrade Successful",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "❌ Not enough coins!\nNeed: " + cost + " coins",
                    "Insufficient Funds",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void upgradeAllPossible() {
        int upgraded = 0;
        int totalCost = 0;

        for (String upgradeKey : cafeManager.getUpgrades().keySet()) {
            int cost = cafeManager.getUpgradeCosts().get(upgradeKey);
            if (cafeManager.canAfford(cost)) {
                if (cafeManager.upgradeItem(upgradeKey)) {
                    upgraded++;
                    totalCost += cost;
                }
            }
        }

        if (upgraded > 0) {
            updateDisplay();
            JOptionPane.showMessageDialog(this,
                    "✅ Upgraded " + upgraded + " items!\n" +
                            "💰 Total cost: " + totalCost + " coins",
                    "Bulk Upgrade Complete",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "❌ Cannot afford any upgrades!",
                    "No Upgrades Possible",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void serveCustomer(Customer customer) {
        cafeManager.serveCustomer(customer);
        updateDisplay();

        JOptionPane.showMessageDialog(this,
                "✅ Order taken!\n" +
                        "😊 Customer satisfaction: " + customer.getSatisfaction() + "%\n" +
                        "💰 Earned: " + (customer.calculateSpending() / 2) + " coins",
                "Order Taken",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showObjectInfo(CafeObject obj) {
        JOptionPane.showMessageDialog(this,
                "🏪 " + obj.getName() + "\n\n" +
                        "⭐ Level: " + obj.getLevel() + "\n" +
                        "💰 Income: " + obj.getIncome() + " coins/hour\n" +
                        "⬆️ Next Upgrade: " + obj.getUpgradeCost() + " coins\n" +
                        "🎯 Satisfaction: +" + obj.getSatisfactionBonus() + "%",
                "Object Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showCustomerInfo(Customer customer) {
        JOptionPane.showMessageDialog(this,
                "👤 Customer Information:\n\n" +
                        "🎯 Type: " + customer.getType() + "\n" +
                        "😊 Satisfaction: " + customer.getSatisfaction() + "%\n" +
                        "⏳ Patience: " + customer.getPatience() + "/" + customer.getMaxPatience() + "\n" +
                        "💰 Potential: " + customer.calculateSpending() + " coins\n" +
                        "🎭 State: " + customer.getState(),
                "Customer Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private String getUpgradeName(String upgradeKey) {
        switch (upgradeKey) {
            case "coffee_quality": return "Coffee Quality";
            case "pastry_variety": return "Pastry Variety";
            case "service_speed": return "Service Speed";
            case "decor": return "Decor";
            case "marketing": return "Marketing";
            default: return "Upgrade";
        }
    }

    private void updateButton(JButton button, String upgradeKey) {
        int currentLevel = cafeManager.getUpgrades().get(upgradeKey);
        int cost = cafeManager.getUpgradeCosts().get(upgradeKey);

        button.setText("Lv." + currentLevel + " - " + cost + "¢");
        button.setBackground(cafeManager.canAfford(cost) ? new Color(70, 130, 180) : Color.GRAY);
    }

    private Color getSatisfactionColor(int satisfaction) {
        if (satisfaction >= 80) return Color.GREEN;
        if (satisfaction >= 60) return Color.YELLOW;
        if (satisfaction >= 40) return Color.ORANGE;
        return Color.RED;
    }

    private void updateDisplay() {
        moneyLabel.setText("💰 " + cafeManager.getFormattedMoney() + " coins");
        satisfactionLabel.setText("😊 " + cafeManager.getSatisfaction() + "%");
        satisfactionLabel.setForeground(getSatisfactionColor(cafeManager.getSatisfaction()));
        customersLabel.setText("👥 " + cafeManager.getCustomerCount() + "/" + cafeManager.getMaxCustomers());
    }

    private void goBack() {
        this.dispose();
        mainMeniu.setVisible(true);
        mainMeniu.updateDisplay();
    }
}