package com.sudokugame.ui;

import com.sudokugame.cafe.CafeManager;
import com.sudokugame.game.Difficulty;
import com.sudokugame.utils.AssetsLoader;

import javax.swing.*;
import java.awt.*;

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
        setResizable(false);

        // Panel principal cu layout explicit
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE); // Fundal alb pentru contrast

        // HEADER - Sus
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 15));
        headerPanel.setBackground(new Color(70, 130, 180)); // Albastru închis
        headerPanel.setPreferredSize(new Dimension(800, 80));

        moneyLabel = new JLabel("💰 " + cafeManager.getMoney() + " coins");
        cafeLevelLabel = new JLabel("🏪 Level " + cafeManager.getCafeLevel());

        moneyLabel.setFont(new Font("Arial", Font.BOLD, 20));
        cafeLevelLabel.setFont(new Font("Arial", Font.BOLD, 20));
        moneyLabel.setForeground(Color.WHITE);
        cafeLevelLabel.setForeground(Color.WHITE);

        headerPanel.add(moneyLabel);
        headerPanel.add(cafeLevelLabel);

        // TITLU - Centru sus
        JLabel titleLabel = new JLabel("SUDOKU CAFE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        // PANEL pentru BUTOANE - Centru
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 150, 20, 150));

        // BUTOANE MARI și VIZIBILE
        JButton playButton = createButton("🎮 PLAY SUDOKU", new Color(70, 130, 180));
        JButton cafeButton = createButton("🏪 MANAGE CAFE", new Color(65, 105, 225));
        JButton statsButton = createButton("📊 STATISTICS", new Color(46, 139, 87));
        JButton settingsButton = createButton("⚙️ SETTINGS", new Color(169, 169, 169));
        JButton exitButton = createButton("🚪 EXIT", new Color(220, 80, 60));

        playButton.addActionListener(e -> showDifficultyMenu());
        cafeButton.addActionListener(e -> openCafeScene());
        statsButton.addActionListener(e -> showStatistics());
        settingsButton.addActionListener(e -> showSettings());
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(playButton);
        buttonPanel.add(cafeButton);
        buttonPanel.add(statsButton);
        buttonPanel.add(settingsButton);
        buttonPanel.add(exitButton);

        // Asamblare finală
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);

        // STIL BUTON - FOARTE IMPORTANT!
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setPreferredSize(new Dimension(300, 60));
        button.setMinimumSize(new Dimension(300, 60));
        button.setMaximumSize(new Dimension(300, 60));

        // Efect hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.brighter());
                button.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            }
        });

        return button;
    }

    private void openCafeScene() {
        this.setVisible(false);
        CafeScene cafeScene = new CafeScene(this, cafeManager);
        cafeScene.setVisible(true);
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
        setVisible(false);
        SudokuGamePanel gamePanel = new SudokuGamePanel(this, cafeManager, difficulty);
        gamePanel.setVisible(true);
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