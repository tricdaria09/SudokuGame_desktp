package com.sudokugame.game;

import com.sudokugame.ui.MainMeniu;
import com.sudokugame.cafe.CafeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SudokuGame extends JFrame {
    private MainMeniu mainMeniu;
    private CafeManager cafeManager;
    private JTextField[][] cells;
    private int[][] board;
    private int[][] solution;

    public SudokuGame(MainMeniu mainMeniu, CafeManager cafeManager) {
        this.mainMeniu = mainMeniu;
        this.cafeManager = cafeManager;
        initializeGame();
        initializeUI();
    }

    private void initializeGame() {
        board = generatePuzzle();
        solution = generateSolution(board);
        cells = new JTextField[9][9];
    }

    private void initializeUI() {
        setTitle("Sudoku - Joacă!");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);

        // 🖼️ PANEL PRINCIPAL
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(250, 250, 250));

        // 🎯 TITLU
        JLabel titleLabel = new JLabel("JOC SUDOKU", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 🔢 GRID SUDOKU
        JPanel gridPanel = new JPanel(new GridLayout(9, 9, 1, 1));
        gridPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        gridPanel.setBackground(Color.BLACK);

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("Arial", Font.BOLD, 20));

                // Culori alternante pentru căsuțele 3x3
                if ((row / 3) % 2 == (col / 3) % 2) {
                    cells[row][col].setBackground(new Color(240, 240, 240));
                } else {
                    cells[row][col].setBackground(Color.WHITE);
                }

                // Numere precompletate (nu pot fi modificate)
                if (board[row][col] != 0) {
                    cells[row][col].setText(String.valueOf(board[row][col]));
                    cells[row][col].setEditable(false);
                    cells[row][col].setBackground(new Color(200, 200, 200));
                    cells[row][col].setForeground(Color.BLUE);
                }

                // Acceptă doar numere de la 1-9
                cells[row][col].addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!((c >= '1' && c <= '9') || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                            e.consume();
                        }
                    }
                });

                gridPanel.add(cells[row][col]);
            }
        }

        mainPanel.add(gridPanel, BorderLayout.CENTER);

        // 🎮 PANEL BUTOANE
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton checkButton = createGameButton("✅ Verifică");
        JButton solveButton = createGameButton("🔍 Rezolvă");
        JButton menuButton = createGameButton("🏠 Meniu Principal");

        checkButton.addActionListener(e -> checkSolution());
        solveButton.addActionListener(e -> showSolution());
        menuButton.addActionListener(e -> returnToMenu());

        buttonPanel.add(checkButton);
        buttonPanel.add(solveButton);
        buttonPanel.add(menuButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton createGameButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private int[][] generatePuzzle() {
        return new int[][]{
                {5, 3, 0, 0, 7, 0, 0, 0, 0},
                {6, 0, 0, 1, 9, 5, 0, 0, 0},
                {0, 9, 8, 0, 0, 0, 0, 6, 0},
                {8, 0, 0, 0, 6, 0, 0, 0, 3},
                {4, 0, 0, 8, 0, 3, 0, 0, 1},
                {7, 0, 0, 0, 2, 0, 0, 0, 6},
                {0, 6, 0, 0, 0, 0, 2, 8, 0},
                {0, 0, 0, 4, 1, 9, 0, 0, 5},
                {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
    }

    private int[][] generateSolution(int[][] puzzle) {
        return new int[][]{
                {5, 3, 4, 6, 7, 8, 9, 1, 2},
                {6, 7, 2, 1, 9, 5, 3, 4, 8},
                {1, 9, 8, 3, 4, 2, 5, 6, 7},
                {8, 5, 9, 7, 6, 1, 4, 2, 3},
                {4, 2, 6, 8, 5, 3, 7, 9, 1},
                {7, 1, 3, 9, 2, 4, 8, 5, 6},
                {9, 6, 1, 5, 3, 7, 2, 8, 4},
                {2, 8, 7, 4, 1, 9, 6, 3, 5},
                {3, 4, 5, 2, 8, 6, 1, 7, 9}
        };
    }

    private void checkSolution() {
        boolean correct = true;

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String text = cells[row][col].getText();
                if (text.isEmpty() || Integer.parseInt(text) != solution[row][col]) {
                    cells[row][col].setBackground(new Color(255, 200, 200)); // Roșu deschis
                    correct = false;
                } else {
                    cells[row][col].setBackground(new Color(200, 255, 200)); // Verde deschis
                }
            }
        }

        if (correct) {
            JOptionPane.showMessageDialog(this,
                    "🎉 Felicitări! Ai rezolvat corect Sudoku!\n\n" +
                            "💰 Ai câștigat 50€ pentru cafenea!",
                    "Victorie!",
                    JOptionPane.INFORMATION_MESSAGE);
            mainMeniu.incrementWins();
            returnToMenu();
        } else {
            JOptionPane.showMessageDialog(this,
                    "❌ Mai încearcă! Există erori în soluție.\n" +
                            "Căsuțele roșii conțin numere greșite.",
                    "Soluție incorectă",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showSolution() {
        int response = JOptionPane.showConfirmDialog(this,
                "Ești sigur că vrei să vezi soluția?",
                "Arată soluția",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    cells[row][col].setText(String.valueOf(solution[row][col]));
                    cells[row][col].setBackground(new Color(255, 255, 200)); // Galben
                    cells[row][col].setEditable(false);
                }
            }
        }
    }

    private void returnToMenu() {
        this.dispose();
        mainMeniu.setVisible(true);
    }
}