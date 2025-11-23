package com.sudokugame.ui;

import com.sudokugame.game.Difficulty;
import com.sudokugame.cafe.CafeManager;
import com.sudokugame.utils.AssetsLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class MainMeniu extends JFrame {
    private CafeManager cafeManager;
    private JLabel moneyLabel;
    private JLabel cafeLevelLabel;

    public MainMeniu() {
        cafeManager = new CafeManager();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Sudoku Cafe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setResizable(false);

        // 🖼️ PANEL CU BACKGROUND IMAGINE
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // ⬇️🖼️ BACKGROUND MENIU - SCHIMBĂ CU IMAGINEA TA!
                Image background = AssetsLoader.getImage("menu_bg");
                if (background != null) {
                    g2d.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback gradient dacă imaginea nu există
                    GradientPaint gradient = new GradientPaint(0, 0, new Color(74, 134, 232),
                            0, getHeight(), new Color(142, 94, 242));
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }

                // Overlay semi-transparent pentru text readability
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // 🎯 HEADER CU STATS
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 🎮 PANEL BUTOANE PRINCIPALE
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // 🏪 INFO CAFENEA
        JPanel cafeInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        cafeInfoPanel.setOpaque(false);

        moneyLabel = new JLabel("💰 " + cafeManager.getMoney() + " coins");
        moneyLabel.setForeground(Color.WHITE);
        moneyLabel.setFont(new Font("Arial", Font.BOLD, 20));

        cafeLevelLabel = new JLabel("🏪 Level " + cafeManager.getCafeLevel());
        cafeLevelLabel.setForeground(Color.YELLOW);
        cafeLevelLabel.setFont(new Font("Arial", Font.BOLD, 20));

        cafeInfoPanel.add(moneyLabel);
        cafeInfoPanel.add(cafeLevelLabel);

        // ⏰ STATS JOC
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statsPanel.setOpaque(false);

        JLabel gamesLabel = new JLabel("🎯 Games: " + cafeManager.getGamesPlayed());
        JLabel winsLabel = new JLabel("🏆 Wins: " + cafeManager.getGamesWon());

        gamesLabel.setForeground(Color.WHITE);
        winsLabel.setForeground(Color.WHITE);
        gamesLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        winsLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        statsPanel.add(gamesLabel);
        statsPanel.add(winsLabel);

        panel.add(cafeInfoPanel, BorderLayout.WEST);
        panel.add(statsPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 100, 15, 100);

        // 🎮 TITLU
        JLabel titleLabel = new JLabel("SUDOKU CAFE", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 64));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        panel.add(titleLabel, gbc);

        // 🎯 BUTOANE PRINCIPALE
        panel.add(createMenuButton("🎮 PLAY SUDOKU", new Color(76, 175, 80), e -> showGameMenu()), gbc);
        panel.add(createMenuButton("🏪 MANAGE CAFE", new Color(255, 152, 0), e -> showCafeScene()), gbc);
        panel.add(createMenuButton("📊 STATISTICS", new Color(156, 39, 176), e -> showStatistics()), gbc);
        panel.add(createMenuButton("⚙️ SETTINGS", new Color(33, 150, 243), e -> showSettings()), gbc);
        panel.add(createMenuButton("🚪 EXIT", new Color(244, 67, 54), e -> exitGame()), gbc);

        return panel;
    }

    private JButton createMenuButton(String text, Color color, java.awt.event.ActionListener action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Buton modern cu gradient
                GradientPaint gradient = new GradientPaint(0, 0, color, 0, getHeight(), color.darker());
                g2.setPaint(gradient);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25));

                // Border
                g2.setColor(new Color(255, 255, 255, 100));
                g2.setStroke(new BasicStroke(2));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 25, 25));

                // Text
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 24));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
            }
        };

        button.setPreferredSize(new Dimension(350, 70));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecte hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });

        button.addActionListener(action);
        return button;
    }

    private void showGameMenu() {
        JPopupMenu difficultyMenu = new JPopupMenu();

        JMenuItem easy = new JMenuItem("🎮 Easy - 25 coins");
        JMenuItem medium = new JMenuItem("🎮 Medium - 50 coins");
        JMenuItem hard = new JMenuItem("🎮 Hard - 100 coins");

        easy.addActionListener(e -> startGame(Difficulty.EASY));
        medium.addActionListener(e -> startGame(Difficulty.MEDIUM));
        hard.addActionListener(e -> startGame(Difficulty.HARD));

        difficultyMenu.add(easy);
        difficultyMenu.add(medium);
        difficultyMenu.add(hard);

        // 🎯 AFIȘEAZĂ MENIUL ÎN CENTRU
        difficultyMenu.show(this, getWidth()/2 - 100, getHeight()/2);
    }

    private void startGame(Difficulty difficulty) {
        // Verifică dacă player-ul are suficienți bani
        int cost = 0;
        switch (difficulty) {
            case EASY: cost = 25; break;
            case MEDIUM: cost = 50; break;
            case HARD: cost = 100; break;
        }

        if (cafeManager.canAfford(cost)) {
            cafeManager.addMoney(-cost);

            // 🎮 DESCHIDE JOCUL SUDOKU
            SudokuGamePanel gamePanel = new SudokuGamePanel(this, cafeManager, difficulty);
            gamePanel.setVisible(true);
            this.setVisible(false);
            updateDisplay();
        } else {
            JOptionPane.showMessageDialog(this,
                    "❌ Not enough coins to play!\n" +
                            "💰 Need: " + cost + " coins\n" +
                            "💡 Earn more coins by managing your cafe!",
                    "Insufficient Funds",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showCafeScene() {
        CafeScene cafeScene = new CafeScene(this, cafeManager);
        cafeScene.setVisible(true);
        this.setVisible(false);
    }

    private void showStatistics() {
        JOptionPane.showMessageDialog(this,
                "📊 Cafe Statistics:\n\n" +
                        "💰 Total Money: " + cafeManager.getMoney() + " coins\n" +
                        "🏪 Cafe Level: " + cafeManager.getCafeLevel() + "\n" +
                        "😊 Satisfaction: " + cafeManager.getSatisfaction() + "%\n" +
                        "👥 Customers: " + cafeManager.getCustomers() + "\n" +
                        "📈 Hourly Income: " + cafeManager.getHourlyIncome() + " coins\n" +
                        "🎯 Games Played: " + cafeManager.getGamesPlayed() + "\n" +
                        "🏆 Games Won: " + cafeManager.getGamesWon() + "\n" +
                        "⭐ Win Rate: " + String.format("%.1f", cafeManager.getWinRate()) + "%\n\n" +
                        "☕ Upgrades:\n" +
                        "  • Coffee Quality: Level " + cafeManager.getCoffeeLevel() + "\n" +
                        "  • Pastry Variety: Level " + cafeManager.getPastryLevel() + "\n" +
                        "  • Service Speed: Level " + cafeManager.getServiceLevel() + "\n" +
                        "  • Decor: Level " + cafeManager.getDecorLevel() + "\n" +
                        "  • Marketing: Level " + cafeManager.getMarketingLevel(),
                "Cafe Statistics",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSettings() {
        JOptionPane.showMessageDialog(this,
                "⚙️ Settings\n\n" +
                        "Game settings will be available\n" +
                        "in a future update!",
                "Settings",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exitGame() {
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Exit Game",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public void updateDisplay() {
        moneyLabel.setText("💰 " + cafeManager.getMoney() + " coins");
        cafeLevelLabel.setText("🏪 Level " + cafeManager.getCafeLevel());
    }
}