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
        setSize(600, 500); // FEREASTRĂ MAI MICĂ
        setLocationRelativeTo(null);
        setResizable(true); // PERMITEM REDIMENSIONAREA

        // FUNDAL GRI ÎNCHIS PENTRU CONTRAST
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(50, 50, 50));

        // HEADER - ALBASTRU ÎNCHIS
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // MAI PUȚIN SPAȚIU
        headerPanel.setBackground(new Color(30, 70, 120));
        headerPanel.setPreferredSize(new Dimension(600, 60)); // MAI MIC

        moneyLabel = new JLabel("$ " + cafeManager.getMoney() + " coins");
        cafeLevelLabel = new JLabel("~ Level " + cafeManager.getCafeLevel());

        moneyLabel.setFont(new Font("Arial", Font.BOLD, 16)); // FONT MAI MIC
        cafeLevelLabel.setFont(new Font("Arial", Font.BOLD, 16));
        moneyLabel.setForeground(Color.YELLOW);
        cafeLevelLabel.setForeground(Color.YELLOW);

        headerPanel.add(moneyLabel);
        headerPanel.add(cafeLevelLabel);

        // TITLU - GALBEN STRĂLUCITOR
        JLabel titleLabel = new JLabel("SUDOKU CAFE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36)); // FONT MAI MIC
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0)); // MAI PUȚIN SPAȚIU

        // PANEL BUTOANE
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 8, 8)); // MAI PUȚIN SPAȚIU ÎNTRE BUTOANE
        buttonPanel.setBackground(new Color(50, 50, 50));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 80, 10, 80)); // MARGINI MAI MICI

        // BUTOANE CU CULORI PUTERNICE ȘI TEXT NEGRU
        JButton playButton = createButton("~ PLAY SUDOKU", Color.CYAN);
        JButton cafeButton = createButton("~ MANAGE CAFE", Color.GREEN);
        JButton statsButton = createButton("~ STATISTICS", Color.MAGENTA);
        JButton settingsButton = createButton("~ SETTINGS", Color.ORANGE);
        JButton exitButton = createButton(":( EXIT", Color.RED);

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

        // ASAMBLARE
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);

        // BUTON MAI MIC
        button.setFont(new Font("Arial", Font.BOLD, 16)); // FONT MAI MIC
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setPreferredSize(new Dimension(300, 45)); // BUTOANE MAI MICI

        // Efect hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.brighter());
                button.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
                button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            }
        });

        return button;
    }

    // ... restul metodelor rămân la fel ...
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
                "% Statistics:\n\n" +
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
                "* Settings\n\n" +
                        "No settings available yet.",
                "Settings",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void updateDisplay() {
        moneyLabel.setText("$ " + cafeManager.getMoney() + " coins");
        cafeLevelLabel.setText("~ Level " + cafeManager.getCafeLevel());
    }
}