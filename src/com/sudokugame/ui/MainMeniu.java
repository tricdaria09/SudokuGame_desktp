package com.sudokugame.ui;

import com.sudokugame.game.Difficulty;
import com.sudokugame.game.SudokuEngine;
import com.sudokugame.cafe.CafeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class MainMeniu extends JFrame {
    private CafeManager cafeManager;
    private JLabel moneyLabel;

    public MainMeniu() {
        cafeManager = new CafeManager();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Sudoku Master");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal cu gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(74, 144, 226),
                        0, getHeight(), new Color(42, 94, 162));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Header cu bani și cafenea
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        moneyLabel = new JLabel("💰 " + cafeManager.getMoney() + " coins");
        moneyLabel.setForeground(Color.WHITE);
        moneyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        moneyLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 0));

        JButton cafeButton = createIconButton("☕", "Cafenea");
        cafeButton.addActionListener(e -> showCafe());

        headerPanel.add(moneyLabel, BorderLayout.WEST);
        headerPanel.add(cafeButton, BorderLayout.EAST);

        // Titlu central
        JLabel titleLabel = new JLabel("SUDOKU", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 72));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

        // Panel butoane principale
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 20, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 150, 100, 150));

        JButton easyButton = createMenuButton("🎮 Ușor", new Color(76, 175, 80));
        JButton mediumButton = createMenuButton("🎮 Mediu", new Color(255, 152, 0));
        JButton hardButton = createMenuButton("🎮 Greu", new Color(244, 67, 54));
        JButton statsButton = createMenuButton("📊 Statistici", new Color(156, 39, 176));

        easyButton.addActionListener(e -> startGame(Difficulty.EASY));
        mediumButton.addActionListener(e -> startGame(Difficulty.MEDIUM));
        hardButton.addActionListener(e -> startGame(Difficulty.HARD));
        statsButton.addActionListener(e -> showStatistics());

        buttonPanel.add(easyButton);
        buttonPanel.add(mediumButton);
        buttonPanel.add(hardButton);
        buttonPanel.add(statsButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createMenuButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fundal rotund cu gradient
                GradientPaint gradient = new GradientPaint(0, 0, color, 0, getHeight(), color.darker());
                g2.setPaint(gradient);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25));

                // Text
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
            }
        };

        button.setPreferredSize(new Dimension(200, 60));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private JButton createIconButton(String icon, String tooltip) {
        JButton button = new JButton(icon);
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        button.setToolTipText(tooltip);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void startGame(Difficulty difficulty) {
        GamePanel gamePanel = new GamePanel(this, cafeManager, difficulty);
        gamePanel.setVisible(true);
        this.setVisible(false);
    }

    private void showCafe() {
        cafeManager.showCafe();
        updateMoneyDisplay();
    }

    private void showStatistics() {
        JOptionPane.showMessageDialog(this,
                "📊 Statistici Cafenea:\n\n" +
                        "💰 Bani: " + cafeManager.getMoney() + " coins\n" +
                        "🏆 Nivel total: " + cafeManager.getTotalLevel() + "\n\n" +
                        "☕ Cafea: Nivel " + cafeManager.getCoffeeLevel() + "\n" +
                        "🍰 Patiserie: Nivel " + cafeManager.getPastryLevel() + "\n" +
                        "🎨 Decor: Nivel " + cafeManager.getDecorLevel() + "\n" +
                        "🎵 Muzică: Nivel " + cafeManager.getMusicLevel() + "\n" +
                        "🌟 Lumină: Nivel " + cafeManager.getLightingLevel(),
                "Statistici",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void updateMoneyDisplay() {
        moneyLabel.setText("💰 " + cafeManager.getMoney() + " coins");
    }
}