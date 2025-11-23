package com.sudokugame.ui;

import com.sudokugame.game.SudokuGame;
import com.sudokugame.cafe.CafeManager;

import javax.swing.*;
import java.awt.*;

public class MainMeniu extends JFrame {  // Nume corect
    private int gamesPlayed = 0;
    private int totalWins = 0;
    private CafeManager cafeManager;

    public MainMeniu() {  // Constructor cu nume corect
        cafeManager = new CafeManager();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Sudoku Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // 🖼️ PANEL CU FUNDAL - poți adăuga imagine aici
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Pentru imagine de fundal, de-comentează:
                // ImageIcon background = new ImageIcon("images/background.jpg");
                // g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setBackground(new Color(240, 240, 240));

        // 🎯 TITLUL JOCULUI
        JLabel titleLabel = new JLabel("SUDOKU", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 🎮 PANEL PENTRU BUTOANE
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240, 200)); // Semi-transparent
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        // 🔘 BUTOANELE PRINCIPALE
        JButton playButton = createMenuButton("🎮 Joacă Sudoku");
        JButton cafeButton = createMenuButton("☕ Cafenea");
        JButton statsButton = createMenuButton("📊 Statistici");
        JButton exitButton = createMenuButton("🚪 Ieșire");

        playButton.addActionListener(e -> startNewGame());
        cafeButton.addActionListener(e -> openCafe());
        statsButton.addActionListener(e -> showStatistics());
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(playButton);
        buttonPanel.add(cafeButton);
        buttonPanel.add(statsButton);
        buttonPanel.add(exitButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // 📊 STATISTICI JOS
        JPanel statsPanel = new JPanel(new FlowLayout());
        statsPanel.setBackground(new Color(220, 220, 220, 200));
        JLabel statsLabel = new JLabel("🎯 Jocuri jucate: " + gamesPlayed + " | 🏆 Victorii: " + totalWins);
        statsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statsPanel.add(statsLabel);
        mainPanel.add(statsPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efect hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 150, 200));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
            }
        });

        return button;
    }

    private void startNewGame() {
        gamesPlayed++;
        SudokuGame sudokuGame = new SudokuGame(this, cafeManager);
        sudokuGame.setVisible(true);
        this.setVisible(false);
    }

    private void openCafe() {
        cafeManager.showCafe();
    }

    private void showStatistics() {
        JOptionPane.showMessageDialog(this,
                "📊 Statistici:\n\n" +
                        "🎯 Jocuri jucate: " + gamesPlayed + "\n" +
                        "🏆 Victorii: " + totalWins + "\n" +
                        "💰 Bani cafenea: " + cafeManager.getMoney() + "€\n\n" +
                        "☕ Nivel Cafea: " + cafeManager.getCoffeeLevel() + "\n" +
                        "🍰 Nivel Patiserie: " + cafeManager.getPastryLevel() + "\n" +
                        "🎨 Nivel Decor: " + cafeManager.getDecorLevel(),
                "Statistici Joc",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void incrementWins() {
        totalWins++;
        cafeManager.addMoney(50); // 50 bani pentru fiecare victorie
    }
}