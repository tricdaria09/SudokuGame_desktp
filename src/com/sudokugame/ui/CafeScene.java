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
        setTitle("My Cafe - Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500); // FEREASTRĂ MAI MICĂ
        setLocationRelativeTo(null);
        setResizable(true); // PERMITEM REDIMENSIONAREA

        // FUNDAL ÎNCHIS PENTRU CONTRAST
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(40, 40, 40));

        // HEADER - ALBASTRU ÎNCHIS
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        headerPanel.setBackground(new Color(30, 70, 120));
        headerPanel.setPreferredSize(new Dimension(800, 80));

        moneyLabel = new JLabel("$ Money: " + cafeManager.getMoney() + " coins");
        satisfactionLabel = new JLabel(":D Satisfaction: " + cafeManager.getSatisfaction() + "%");

        moneyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        satisfactionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        moneyLabel.setForeground(Color.YELLOW);
        satisfactionLabel.setForeground(Color.YELLOW);

        headerPanel.add(moneyLabel);
        headerPanel.add(satisfactionLabel);

        JButton backButton = createButton("← BACK", Color.ORANGE);
        backButton.addActionListener(e -> goBack());
        headerPanel.add(backButton);

        // CENTER - CUSTOMERS
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("👥 CUSTOMERS WAITING"));
        centerPanel.setBackground(new Color(60, 60, 60));

        customerArea = new JTextArea();
        customerArea.setEditable(false);
        customerArea.setFont(new Font("Arial", Font.BOLD, 14));
        customerArea.setBackground(Color.BLACK);
        customerArea.setForeground(Color.YELLOW);
        JScrollPane scrollPane = new JScrollPane(customerArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // ACTIONS - BUTOANE COLORATE
        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setBackground(new Color(60, 60, 60));

        JButton serveButton = createButton(";) SERVE ALL", Color.GREEN);
        serveButton.addActionListener(e -> serveAllCustomers());

        JButton upgradeButton = createButton("️-> UPGRADE (100¢)", Color.CYAN);
        upgradeButton.addActionListener(e -> quickUpgrade());

        actionPanel.add(serveButton);
        actionPanel.add(upgradeButton);

        centerPanel.add(actionPanel, BorderLayout.SOUTH);

        // RIGHT SIDE - INFO
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("* CAFE INFO"));
        infoPanel.setBackground(new Color(80, 80, 80));
        infoPanel.setPreferredSize(new Dimension(250, 400));

        addInfoLine(infoPanel, "~ Level: " + cafeManager.getCafeLevel());
        addInfoLine(infoPanel, "~ Games Won: " + cafeManager.getGamesWon());
        addInfoLine(infoPanel, "~ Win Rate: " + String.format("%.1f", cafeManager.getWinRate()) + "%");
        addInfoLine(infoPanel, "~ Income: " + cafeManager.getHourlyIncome() + "/h");
        addInfoLine(infoPanel, "~ Max Customers: " + cafeManager.getMaxCustomers());

        // ASAMBLARE FINALĂ
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.EAST);

        add(mainPanel);
        updateCustomerDisplay();
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.BLACK); // TEXT NEGRU
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        button.setPreferredSize(new Dimension(200, 50));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
                button.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 4));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
            }
        });

        return button;
    }

    private void addInfoLine(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.YELLOW);
        label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        panel.add(label);
    }

    private void startGameLoop() {
        gameTimer = new Timer(3000, e -> {
            // INCOME PASIV
            cafeManager.addMoney(cafeManager.getHourlyIncome() / 12);

            // SPAWN CUSTOMERS
            spawnRandomCustomer();

            // UPDATE DISPLAY
            updateCustomerDisplay();
            updateHeader();
        });
        gameTimer.start();
    }

    private void spawnRandomCustomer() {
        Random rand = new Random();
        if (rand.nextInt(100) < 50) {
            List<Customer> customers = cafeManager.getCustomersList();
            if (customers.size() < cafeManager.getMaxCustomers()) {
                Customer newCustomer = new Customer(0, 0);
                customers.add(newCustomer);
                customerArea.append("! NEW " + newCustomer.getType().toUpperCase() + " CUSTOMER!\n");
                customerArea.setCaretPosition(customerArea.getDocument().getLength());
            }
        }
    }

    private void serveAllCustomers() {
        List<Customer> customers = cafeManager.getCustomersList();
        if (customers.isEmpty()) {
            customerArea.append(":( NO CUSTOMERS TO SERVE!\n");
            return;
        }

        int totalEarned = 0;
        int servedCount = 0;

        for (Customer customer : customers) {
            if (customer.isReadyToOrder()) {
                int earned = customer.calculateSpending();
                totalEarned += earned;
                servedCount++;
            }
        }

        customers.removeIf(customer -> customer.isReadyToOrder());

        if (servedCount > 0) {
            cafeManager.addMoney(totalEarned);
            customerArea.append(":) SERVED " + servedCount + " CUSTOMERS! +" + totalEarned + " COINS!\n");
        } else {
            customerArea.append(";| CUSTOMERS ARE STILL WAITING...\n");
        }

        updateHeader();
        customerArea.setCaretPosition(customerArea.getDocument().getLength());
    }

    private void quickUpgrade() {
        int cost = 100;
        if (cafeManager.canAfford(cost)) {
            cafeManager.addMoney(-cost);
            cafeManager.addMoney(50);
            customerArea.append("-> CAFE UPGRADED! +50 COINS BONUS!\n");
            updateHeader();
        } else {
            customerArea.append(">:( NEED " + cost + " COINS FOR UPGRADE!\n");
        }
        customerArea.setCaretPosition(customerArea.getDocument().getLength());
    }

    private void updateCustomerDisplay() {
        List<Customer> customers = cafeManager.getCustomersList();

        StringBuilder sb = new StringBuilder();
        sb.append("=== CUSTOMERS WAITING ===\n\n");

        if (customers.isEmpty()) {
            sb.append("No customers...\n");
            sb.append("Waiting for new customers...\n");
        } else {
            for (int i = 0; i < customers.size(); i++) {
                Customer c = customers.get(i);
                String status = c.isReadyToOrder() ? "READY :)" : "WAITING :|";
                sb.append(String.format("%d. %s - %s\n", i + 1, c.getType().toUpperCase(), status));
                sb.append(String.format("   Happiness: %d%%\n", c.getSatisfaction()));
                sb.append(String.format("   Potential: %d coins\n\n", c.calculateSpending()));
            }
        }

        sb.append("Click 'SERVE ALL' to earn money!\n");

        customerArea.setText(sb.toString());
    }

    private void updateHeader() {
        moneyLabel.setText("$ Money: " + cafeManager.getMoney() + " coins");
        satisfactionLabel.setText(":) Satisfaction: " + cafeManager.getSatisfaction() + "%");
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