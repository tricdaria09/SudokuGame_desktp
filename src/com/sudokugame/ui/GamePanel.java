package com.sudokugame.ui;

import com.sudokugame.game.SudokuEngine;
import com.sudokugame.game.Difficulty;
import com.sudokugame.cafe.CafeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePanel extends JFrame {
    private MainMeniu mainMeniu;
    private CafeManager cafeManager;
    private Difficulty difficulty;
    private SudokuEngine engine;
    private Timer gameTimer;
    private int secondsElapsed;
    private JLabel timerLabel;

    public GamePanel(MainMeniu mainMeniu, CafeManager cafeManager, Difficulty difficulty) {
        this.mainMeniu = mainMeniu;
        this.cafeManager = cafeManager;
        this.difficulty = difficulty;
        this.engine = new SudokuEngine(difficulty);
        initializeUI();
        startTimer();
    }

    private void initializeUI() {
        setTitle("Sudoku - " + difficulty.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 900);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));

        // Header cu timer și butoane
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Grid Sudoku
        JPanel gridPanel = createGridPanel();
        mainPanel.add(gridPanel, BorderLayout.CENTER);

        // Panel control
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(50, 50, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Timer
        timerLabel = new JLabel("00:00");
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Dificultate
        JLabel diffLabel = new JLabel(difficulty.getName());
        diffLabel.setForeground(difficulty.getColor());
        diffLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Buton meniu
        JButton menuButton = new JButton("🏠");
        menuButton.addActionListener(e -> returnToMenu());

        panel.add(timerLabel, BorderLayout.WEST);
        panel.add(diffLabel, BorderLayout.CENTER);
        panel.add(menuButton, BorderLayout.EAST);

        return panel;
    }

    private JPanel createGridPanel() {
        JPanel panel = new JPanel(new GridLayout(9, 9, 2, 2));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.BLACK);

        // Implementare grid similară cu versiunea anterioară
        // Dar cu design îmbunătățit

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JButton checkButton = createControlButton("✅ Verifică", new Color(76, 175, 80));
        JButton hintButton = createControlButton("💡 Indică", new Color(255, 152, 0));
        JButton solveButton = createControlButton("🔍 Rezolvă", new Color(156, 39, 176));

        checkButton.addActionListener(e -> checkSolution());
        hintButton.addActionListener(e -> showHint());
        solveButton.addActionListener(e -> showSolution());

        panel.add(checkButton);
        panel.add(hintButton);
        panel.add(solveButton);

        return panel;
    }

    private JButton createControlButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void startTimer() {
        secondsElapsed = 0;
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secondsElapsed++;
                updateTimerDisplay();
            }
        });
        gameTimer.start();
    }

    private void updateTimerDisplay() {
        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void checkSolution() {
        if (engine.checkSolution()) {
            gameTimer.stop();
            int reward = calculateReward();
            cafeManager.addMoney(reward);

            // Animatie victorie
            showVictoryAnimation(reward);
        } else {
            JOptionPane.showMessageDialog(this, "Mai incearca! Există erori.");
        }
    }

    private int calculateReward() {
        int baseReward = difficulty.getBaseReward();
        int timeBonus = Math.max(0, 300 - secondsElapsed) / 10; // Bonus pentru timp rapid
        return baseReward + timeBonus + cafeManager.getCafeBonus();
    }

    private void showVictoryAnimation(int reward) {
        // Implementare animatie victorie
        JOptionPane.showMessageDialog(this,
                "🎉 Felicitări! Ai câștigat!\n\n" +
                        "⏱️ Timp: " + timerLabel.getText() + "\n" +
                        "💰 Reward: " + reward + " coins\n" +
                        "☕ Bonus cafenea: " + cafeManager.getCafeBonus() + " coins",
                "Victorie!",
                JOptionPane.INFORMATION_MESSAGE);

        returnToMenu();
    }

    private void showHint() {
        if (cafeManager.canAfford(10)) {
            cafeManager.addMoney(-10);
            engine.provideHint();
            mainMeniu.updateMoneyDisplay();
        } else {
            JOptionPane.showMessageDialog(this, "Nu ai suficienți bani pentru indiciu!");
        }
    }

    private void showSolution() {
        engine.solveCompletely();
    }

    private void returnToMenu() {
        gameTimer.stop();
        this.dispose();
        mainMeniu.setVisible(true);
        mainMeniu.updateMoneyDisplay();
    }
}