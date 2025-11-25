package com.sudokugame.ui;

import com.sudokugame.cafe.CafeManager;
import com.sudokugame.cafe.Customer;
import com.sudokugame.utils.AssetsLoader;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class CafeScene extends JFrame {
    private MainMeniu mainMeniu;
    private CafeManager cafeManager;
    private JLabel moneyLabel;
    private JLabel satisfactionLabel;
    private JTextArea customerArea;
    private Timer gameTimer;

    public CafeScene(MainMeniu mainMeniu, CafeManager cafeManager) {
        this.mainMeniu = mainMeniu;
        this.cafeManager = cafeManager;
        initializeUI();
        startGameLoop();
    }

    private void initializeUI() {
        setTitle("My Cafe - Simple Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 222, 179)); // Cafe color

        // HEADER
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        headerPanel.setBackground(new Color(139, 69, 19));
        headerPanel.setPreferredSize(new Dimension(700, 60));

        moneyLabel = new JLabel("💰 Money: " + cafeManager.getMoney() + " coins");
        satisfactionLabel = new JLabel("😊 Satisfaction: " + cafeManager.getSatisfaction() + "%");

        moneyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        satisfactionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        moneyLabel.setForeground(Color.WHITE);
        satisfactionLabel.setForeground(getSatisfactionColor(cafeManager.getSatisfaction()));

        headerPanel.add(moneyLabel);
        headerPanel.add(satisfactionLabel);

        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(Color.ORANGE);
        backButton.setForeground(Color.BLACK);
        backButton.addActionListener(e -> goBack());
        headerPanel.add(backButton);

        // CENTER - Customer Management
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("👥 Customers"));
        centerPanel.setBackground(Color.WHITE);

        customerArea = new JTextArea();
        customerArea.setEditable(false);
        customerArea.setFont(new Font("Arial", Font.PLAIN, 14));
        customerArea.setBackground(new Color(253, 245, 230));
        JScrollPane scrollPane = new JScrollPane(customerArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // ACTIONS Panel
        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setBackground(Color.WHITE);

        JButton serveButton = new JButton("☕ Serve All Customers");
        serveButton.setFont(new Font("Arial", Font.BOLD, 14));
        serveButton.setBackground(new Color(76, 175, 80));
        serveButton.setForeground(Color.WHITE);
        serveButton.addActionListener(e -> serveAllCustomers());

        JButton upgradeButton = new JButton("⬆️ Quick Upgrade (100¢)");
        upgradeButton.setFont(new Font("Arial", Font.BOLD, 14));
        upgradeButton.setBackground(new Color(70, 130, 180));
        upgradeButton.setForeground(Color.WHITE);
        upgradeButton.addActionListener(e -> quickUpgrade());

        actionPanel.add(serveButton);
        actionPanel.add(upgradeButton);

        centerPanel.add(actionPanel, BorderLayout.SOUTH);

        // INFO Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("🏪 Cafe Info"));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setPreferredSize(new Dimension(200, 400));

        addInfoLine(infoPanel, "Level: " + cafeManager.getCafeLevel());
        addInfoLine(infoPanel, "Games Won: " + cafeManager.getGamesWon());
        addInfoLine(infoPanel, "Win Rate: " + String.format("%.1f", cafeManager.getWinRate()) + "%");
        addInfoLine(infoPanel, "Income: " + cafeManager.getHourlyIncome() + "/h");

        // FINAL ASSEMBLY
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.EAST);

        add(mainPanel);
        updateCustomerDisplay();
    }

    private void addInfoLine(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(label);
    }

    private void startGameLoop() {
        gameTimer = new Timer(3000, e -> { // Update every 3 seconds
            cafeManager.addMoney(10); // Passive income
            spawnRandomCustomer();
            updateCustomerDisplay();
            updateHeader();
        });
        gameTimer.start();
    }

    private void spawnRandomCustomer() {
        Random rand = new Random();
        if (rand.nextInt(100) < 40) { // 40% chance to spawn customer
            List<Customer> customers = cafeManager.getCustomersList();
            if (customers.size() < cafeManager.getMaxCustomers()) {
                Customer newCustomer = new Customer(0, 0); // Position doesn't matter in this simple version
                customers.add(newCustomer);
                customerArea.append("👤 New " + newCustomer.getType() + " customer arrived!\n");
            }
        }
    }

    private void serveAllCustomers() {
        List<Customer> customers = cafeManager.getCustomersList();
        if (customers.isEmpty()) {
            customerArea.append("ℹ️ No customers to serve!\n");
            return;
        }

        int totalEarned = 0;
        int servedCount = 0;

        for (Customer customer : customers) {
            if (customer.isReadyToOrder()) {
                int earned = customer.calculateSpending();
                cafeManager.addMoney(earned);
                totalEarned += earned;
                servedCount++;
            }
        }

        // Remove served customers
        customers.removeIf(customer -> customer.isReadyToOrder());

        if (servedCount > 0) {
            customerArea.append("✅ Served " + servedCount + " customers! Earned: +" + totalEarned + " coins\n");
            cafeManager.addMoney(totalEarned);
        } else {
            customerArea.append("⏳ Customers are still waiting...\n");
        }

        updateHeader();
        customerArea.setCaretPosition(customerArea.getDocument().getLength());
    }

    private void quickUpgrade() {
        int cost = 100;
        if (cafeManager.canAfford(cost)) {
            cafeManager.addMoney(-cost);

            // Simple upgrade - increase satisfaction and income
            int currentSatisfaction = cafeManager.getSatisfaction();
            cafeManager.addMoney(50); // Bonus money
            cafeManager.addMoney(cafeManager.getHourlyIncome()); // Bonus income

            customerArea.append("⬆️ Cafe upgraded! Satisfaction +5%, Bonus income received!\n");
            updateHeader();
        } else {
            customerArea.append("❌ Need " + cost + " coins for upgrade!\n");
        }
        customerArea.setCaretPosition(customerArea.getDocument().getLength());
    }

    private void updateCustomerDisplay() {
        List<Customer> customers = cafeManager.getCustomersList();

        StringBuilder sb = new StringBuilder();
        sb.append("=== CURRENT CUSTOMERS ===\n");

        if (customers.isEmpty()) {
            sb.append("No customers waiting\n");
        } else {
            for (int i = 0; i < customers.size(); i++) {
                Customer c = customers.get(i);
                String status = c.isReadyToOrder() ? "READY 📝" : "Waiting ⏳";
                sb.append(String.format("%d. %s - %s (%d%% happy)\n",
                        i + 1, c.getType(), status, c.getSatisfaction()));
            }
        }

        sb.append("=======================\n");
        sb.append("Click 'Serve All' to earn money!\n\n");

        customerArea.setText(sb.toString());
    }

    private void updateHeader() {
        moneyLabel.setText("💰 Money: " + cafeManager.getMoney() + " coins");
        satisfactionLabel.setText("😊 Satisfaction: " + cafeManager.getSatisfaction() + "%");
        satisfactionLabel.setForeground(getSatisfactionColor(cafeManager.getSatisfaction()));
    }

    private Color getSatisfactionColor(int satisfaction) {
        if (satisfaction >= 80) return Color.GREEN;
        if (satisfaction >= 60) return Color.YELLOW;
        if (satisfaction >= 40) return Color.ORANGE;
        return Color.RED;
    }

    private void goBack() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        this.dispose();
        mainMeniu.setVisible(true);
        mainMeniu.updateDisplay();
    }
}