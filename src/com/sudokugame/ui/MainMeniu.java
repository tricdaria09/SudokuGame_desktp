package com.sudokugame.ui;

import com.sudokugame.cafe.CafeManager;
import com.sudokugame.game.Difficulty;
import com.sudokugame.utils.AssetsLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                GradientPaint gradient = new GradientPaint(
                        0, 0, AssetsLoader.getColor("menu_bg"),
                        getWidth(), getHeight(), Color.WHITE
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout());
        headerPanel.setBackground(AssetsLoader.getColor("header_bg"));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        moneyLabel = new JLabel("💰 " + cafeManager.getMoney() + " coins");
        cafeLevelLabel = new JLabel("🏪 Level " + cafeManager.getCafeLevel());

        moneyLabel.setFont(AssetsLoader.getFont("header"));
        cafeLevelLabel.setFont(AssetsLoader.getFont("header"));

        headerPanel.add(moneyLabel);
        headerPanel.add(cafeLevelLabel);

        // Butoane
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));

        JButton playButton = createButton("🎮 PLAY SUDOKU", AssetsLoader.getColor("button_primary"));
        JButton cafeButton = createButton("🏪 MANAGE CAFE", Color.ORANGE);
        JButton statsButton = createButton("📊 STATISTICS", Color.MAGENTA);
        JButton settingsButton = createButton("⚙️ SETTINGS", Color.BLUE);
        JButton exitButton = createButton("🚪 EXIT", Color.RED);

        playButton.addActionListener(e -> showDifficultyMenu());
        cafeButton.addActionListener(e -> showCafeMessage());
        statsButton.addActionListener(e -> showStatistics());
        settingsButton.addActionListener(e -> showSettings());
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(playButton);
        buttonPanel.add(cafeButton);
        buttonPanel.add(statsButton);
        buttonPanel.add(settingsButton);
        buttonPanel.add(exitButton);

        // Titlu
        JLabel titleLabel = new JLabel("SUDOKU CAFE", SwingConstants.CENTER);
        titleLabel.setFont(AssetsLoader.getFont("title"));
        titleLabel.setForeground(new Color(139, 69, 19));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(AssetsLoader.getFont("button"));
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efect hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void showDifficultyMenu() {
        String[] options = {"Easy (25 coins)", "Medium (50 coins)", "Hard (100 coins)"};
        String choice = (String) JOptionPane.showInputDialog(
                this,
                "Choose difficulty:",
                "Start Game",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice != null) {
            Difficulty difficulty = null;
            int cost = 0;

            if (choice.contains("Easy")) {
                difficulty = Difficulty.EASY;
                cost = 25;
            } else if (choice.contains("Medium")) {
                difficulty = Difficulty.MEDIUM;
                cost = 50;
            } else if (choice.contains("Hard")) {
                difficulty = Difficulty.HARD;
                cost = 100;
            }

            if (difficulty != null) {
                startSudokuGame(difficulty, cost);
            }
        }
    }

    private void startSudokuGame(Difficulty difficulty, int cost) {
        if (!cafeManager.canAfford(cost)) {
            JOptionPane.showMessageDialog(this,
                    "Not enough coins! Need: " + cost + " coins",
                    "Insufficient Funds",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        cafeManager.addMoney(-cost);
        updateDisplay();

        // Ascunde meniul principal
        setVisible(false);

        // Creează fereastra de joc Sudoku
        SudokuGamePanel gamePanel = new SudokuGamePanel(this, cafeManager, difficulty);
        gamePanel.setVisible(true);
    }

    private void showCafeMessage() {
        JOptionPane.showMessageDialog(this,
                "🏪 Cafe Management\n\n" +
                        "This feature will be available\n" +
                        "in the next update!",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showStatistics() {
        JOptionPane.showMessageDialog(this,
                "📊 Statistics:\n\n" +
                        "Money: " + cafeManager.getMoney() + " coins\n" +
                        "Cafe Level: " + cafeManager.getCafeLevel() + "\n" +
                        "Games Played: " + cafeManager.getGamesPlayed() + "\n" +
                        "Games Won: " + cafeManager.getGamesWon() + "\n" +
                        "Win Rate: " + String.format("%.1f", cafeManager.getWinRate()) + "%",
                "Game Statistics",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSettings() {
        JOptionPane.showMessageDialog(this,
                "⚙️ Settings\n\n" +
                        "No settings available yet.",
                "Settings",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void updateDisplay() {
        moneyLabel.setText("💰 " + cafeManager.getMoney() + " coins");
        cafeLevelLabel.setText("🏪 Level " + cafeManager.getCafeLevel());
    }
}