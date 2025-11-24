package com.sudokugame.ui;

import com.sudokugame.game.SudokuEngine;
import com.sudokugame.game.Difficulty;
import com.sudokugame.cafe.CafeManager;
import com.sudokugame.utils.AssetsLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class SudokuGamePanel extends JFrame {
    private MainMeniu mainMenu;
    private CafeManager cafeManager;
    private Difficulty difficulty;
    private SudokuEngine engine;
    private Timer gameTimer;
    private int secondsElapsed;

    private int hearts;
    private int maxHearts;
    private JPanel heartsPanel;

    private JLabel timerLabel;
    private JLabel difficultyLabel;
    private JLabel coinsLabel;
    private SudokuCell[][] cells;
    private JPanel sudokuGrid;
    private int selectedRow = -1, selectedCol = -1;

    public SudokuGamePanel(MainMeniu mainMenu, CafeManager cafeManager, Difficulty difficulty) {
        this.mainMenu = mainMenu;
        this.cafeManager = cafeManager;
        this.difficulty = difficulty;
        this.engine = new SudokuEngine(difficulty);

        this.maxHearts = getMaxHeartsForDifficulty();
        this.hearts = maxHearts;

        initializeUI();
        startTimer();
    }

    private int getMaxHeartsForDifficulty() {
        switch (difficulty) {
            case EASY: return 5;
            case MEDIUM: return 3;
            case HARD: return 2;
            default: return 3;
        }
    }

    private void initializeUI() {
        setTitle("Sudoku - " + difficulty.getName());
        setSize(700, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Image background = AssetsLoader.getImage("game_bg");
                if (background != null) {
                    g2d.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                } else {
                    GradientPaint gradient = new GradientPaint(
                            0, 0, AssetsLoader.getColor("menu_bg"),
                            getWidth(), getHeight(), AssetsLoader.getColor("game_bg")
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createSudokuGrid(), BorderLayout.CENTER);
        mainPanel.add(createControlPanel(), BorderLayout.SOUTH);
        mainPanel.add(createNumberPanel(), BorderLayout.EAST);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AssetsLoader.getColor("header_bg"));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        panel.setPreferredSize(new Dimension(getWidth(), 70));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        timerLabel = new JLabel("00:00");
        timerLabel.setForeground(AssetsLoader.getColor("header_text"));
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));

        heartsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        heartsPanel.setOpaque(false);
        heartsPanel.setPreferredSize(new Dimension(150, 25));
        updateHeartsDisplay();

        coinsLabel = new JLabel("💰 " + cafeManager.getMoney());
        coinsLabel.setForeground(AssetsLoader.getColor("header_text"));
        coinsLabel.setFont(new Font("Arial", Font.BOLD, 16));

        topRow.add(timerLabel, BorderLayout.WEST);
        topRow.add(heartsPanel, BorderLayout.CENTER);
        topRow.add(coinsLabel, BorderLayout.EAST);

        difficultyLabel = new JLabel(difficulty.getName(), SwingConstants.CENTER);
        difficultyLabel.setForeground(AssetsLoader.getColor("header_text"));
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 16));

        panel.add(topRow, BorderLayout.NORTH);
        panel.add(difficultyLabel, BorderLayout.SOUTH);

        return panel;
    }

    private void updateHeartsDisplay() {
        if (heartsPanel == null) return;

        heartsPanel.removeAll();

        for (int i = 0; i < hearts; i++) {
            JLabel heart = new JLabel("❤️");
            heart.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            heartsPanel.add(heart);
        }

        for (int i = hearts; i < maxHearts; i++) {
            JLabel heart = new JLabel("🤍");
            heart.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            heartsPanel.add(heart);
        }

        heartsPanel.revalidate();
        heartsPanel.repaint();
    }

    private JPanel createSudokuGrid() {
        sudokuGrid = new JPanel(new GridLayout(9, 9, 0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                g2d.setColor(new Color(0, 0, 0));
                g2d.setStroke(new BasicStroke(3));

                for (int i = 1; i < 3; i++) {
                    int x = i * getWidth() / 3;
                    g2d.drawLine(x, 0, x, getHeight());

                    int y = i * getHeight() / 3;
                    g2d.drawLine(0, y, getWidth(), y);
                }

                g2d.setColor(new Color(200, 200, 200));
                g2d.setStroke(new BasicStroke(1));

                for (int i = 1; i < 9; i++) {
                    if (i % 3 != 0) {
                        int x = i * getWidth() / 9;
                        g2d.drawLine(x, 0, x, getHeight());

                        int y = i * getHeight() / 9;
                        g2d.drawLine(0, y, getWidth(), y);
                    }
                }
            }
        };

        sudokuGrid.setBackground(Color.WHITE);
        sudokuGrid.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        sudokuGrid.setPreferredSize(new Dimension(400, 400));

        cells = new SudokuCell[9][9];
        int[][] board = engine.getBoard();

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col] = new SudokuCell(row, col, board[row][col]);
                sudokuGrid.add(cells[row][col]);
            }
        }

        return sudokuGrid;
    }

    private class SudokuCell extends JPanel {
        private int row, col, value;
        private boolean isFixed, isSelected, isError;

        public SudokuCell(int row, int col, int value) {
            this.row = row;
            this.col = col;
            this.value = value;
            this.isFixed = (value != 0);
            this.isSelected = false;
            this.isError = false;

            setPreferredSize(new Dimension(40, 40));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectCell();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            if (isSelected) {
                g2d.setColor(AssetsLoader.getColor("cell_selected"));
                for (int c = 0; c < 9; c++) {
                    if (c != col) {
                        g2d.fillRect(c * getWidth(), 0, getWidth(), getHeight());
                    }
                }

                for (int r = 0; r < 9; r++) {
                    if (r != row) {
                        g2d.fillRect(0, r * getHeight(), getWidth(), getHeight());
                    }
                }

                int startRow = row - row % 3;
                int startCol = col - col % 3;
                g2d.setColor(new Color(220, 240, 255));
                for (int r = startRow; r < startRow + 3; r++) {
                    for (int c = startCol; c < startCol + 3; c++) {
                        if (r != row || c != col) {
                            g2d.fillRect(c * getWidth(), r * getHeight(), getWidth(), getHeight());
                        }
                    }
                }

                g2d.setColor(new Color(0, 100, 255));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(1, 1, getWidth()-2, getHeight()-2);
            }

            if (isFixed) {
                g2d.setColor(new Color(240, 240, 240));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

            if (isError) {
                g2d.setColor(AssetsLoader.getColor("cell_error"));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

            if (value != 0) {
                if (isError) {
                    g2d.setColor(Color.RED);
                } else if (isFixed) {
                    g2d.setColor(Color.BLACK);
                } else {
                    g2d.setColor(new Color(0, 100, 200));
                }

                g2d.setFont(new Font("Arial", Font.BOLD, 18));

                FontMetrics fm = g2d.getFontMetrics();
                String text = String.valueOf(value);
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;

                g2d.drawString(text, x, y);
            }

            g2d.setColor(new Color(200, 200, 200));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(0, 0, getWidth()-1, getHeight()-1);
        }

        private void selectCell() {
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    cells[r][c].isSelected = false;
                }
            }

            if (!isFixed) {
                isSelected = true;
                selectedRow = row;
                selectedCol = col;
            }

            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    cells[r][c].repaint();
                }
            }
        }

        public void setValue(int newValue) {
            if (!isFixed) {
                this.value = newValue;
                engine.setCellValue(row, col, newValue);

                if (newValue != 0) {
                    boolean isCorrect = (newValue == engine.getSolutionValue(row, col));
                    if (!isCorrect) {
                        loseHeart();
                    }
                    isError = !isCorrect;
                } else {
                    isError = false;
                }
                repaint();
            }
        }

        public void setError(boolean error) {
            this.isError = error;
            repaint();
        }
    }

    private void loseHeart() {
        hearts--;
        updateHeartsDisplay();

        if (hearts <= 0) {
            gameOver();
        } else {
            showHeartLossAnimation();
        }
    }

    private void showHeartLossAnimation() {
        Timer heartTimer = new Timer(100, new ActionListener() {
            int flashCount = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                difficultyLabel.setForeground(flashCount % 2 == 0 ? Color.RED : AssetsLoader.getColor("header_text"));
                flashCount++;
                if (flashCount > 6) {
                    difficultyLabel.setForeground(AssetsLoader.getColor("header_text"));
                    ((Timer)e.getSource()).stop();

                    JOptionPane.showMessageDialog(SudokuGamePanel.this,
                            "💔 Wrong number!\n❤️ You lost one heart!\nHearts remaining: " + hearts + "/" + maxHearts,
                            "Incorrect Move",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        heartTimer.start();
    }

    private void gameOver() {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        JOptionPane.showMessageDialog(this,
                "💔 GAME OVER!\n\n⏱️ Time: " + timerLabel.getText() + "\n❤️ Hearts: 0/" + maxHearts + "\n🎯 Difficulty: " + difficulty.getName(),
                "Game Over",
                JOptionPane.ERROR_MESSAGE);

        returnToMenu();
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(AssetsLoader.getColor("control_bg"));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        panel.add(createControlButton("Check", new Color(76, 175, 80), e -> checkSolution()));
        panel.add(createControlButton("Hint (25¢)", new Color(255, 152, 0), e -> showHint()));
        panel.add(createControlButton("Solve", new Color(156, 39, 176), e -> showSolution()));
        panel.add(createControlButton("New Game", new Color(33, 150, 243), e -> newGame()));
        panel.add(createControlButton("Menu", new Color(244, 67, 54), e -> returnToMenu()));

        return panel;
    }

    private JPanel createNumberPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 2, 2));
        panel.setBackground(AssetsLoader.getColor("control_bg"));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        panel.setPreferredSize(new Dimension(60, 400));

        for (int i = 1; i <= 9; i++) {
            JButton numberBtn = new JButton(String.valueOf(i));
            numberBtn.setFont(new Font("Arial", Font.BOLD, 16));
            numberBtn.setBackground(AssetsLoader.getColor("button_primary"));
            numberBtn.setForeground(AssetsLoader.getColor("button_text"));
            numberBtn.setFocusPainted(false);
            numberBtn.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            numberBtn.addActionListener(e -> inputNumber(Integer.parseInt(numberBtn.getText())));
            panel.add(numberBtn);
        }

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 14));
        clearBtn.setBackground(Color.RED);
        clearBtn.setForeground(Color.WHITE);
        clearBtn.addActionListener(e -> inputNumber(0));
        panel.add(clearBtn);

        return panel;
    }

    private JButton createControlButton(String text, Color color, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);

        return button;
    }

    private void inputNumber(int number) {
        if (selectedRow != -1 && selectedCol != -1) {
            cells[selectedRow][selectedCol].setValue(number);
        }
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
            int baseReward = difficulty.getBaseReward();
            cafeManager.addGameResult(true, baseReward);

            showVictoryAnimation();
        } else {
            highlightErrors();
            JOptionPane.showMessageDialog(this,
                    "❌ Not quite right!\nCheck the red cells for errors.",
                    "Solution Check",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void highlightErrors() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int userValue = engine.getCellValue(row, col);
                int solutionValue = engine.getSolutionValue(row, col);
                cells[row][col].setError(userValue != 0 && userValue != solutionValue);
            }
        }
    }

    private void showVictoryAnimation() {
        Timer victoryTimer = new Timer(100, new ActionListener() {
            int flashCount = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int row = 0; row < 9; row++) {
                    for (int col = 0; col < 9; col++) {
                        cells[row][col].setBackground(flashCount % 2 == 0 ? Color.GREEN : Color.YELLOW);
                    }
                }
                flashCount++;
                if (flashCount > 6) {
                    ((Timer)e.getSource()).stop();
                    showVictoryMessage();
                }
            }
        });
        victoryTimer.start();
    }

    private void showVictoryMessage() {
        int reward = difficulty.getBaseReward() + cafeManager.getCafeBonus();
        int heartBonus = hearts * 10;
        reward += heartBonus;

        JOptionPane.showMessageDialog(this,
                "🎉 CONGRATULATIONS!\n\n⏱️ Time: " + timerLabel.getText() + "\n❤️ Hearts remaining: " + hearts + "/" + maxHearts + " (+" + heartBonus + " coins)\n💰 Total Reward: " + reward + " coins",
                "Puzzle Complete!",
                JOptionPane.INFORMATION_MESSAGE);

        returnToMenu();
    }

    private void showHint() {
        if (cafeManager.canAfford(25)) {
            cafeManager.addMoney(-25);
            engine.provideHint();
            updateBoardDisplay();
            coinsLabel.setText("💰 " + cafeManager.getMoney());

            JOptionPane.showMessageDialog(this,
                    "💡 Hint applied!\nOne cell has been filled for you.",
                    "Hint Used",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "❌ Not enough coins for hint!\nNeed: 25 coins",
                    "Insufficient Funds",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showSolution() {
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to see the solution?\nThis will end the current game.",
                "Show Solution",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            engine.solveCompletely();
            updateBoardDisplay();
            gameTimer.stop();
        }
    }

    private void newGame() {
        int response = JOptionPane.showConfirmDialog(this,
                "Start a new game?\nCurrent progress will be lost.",
                "New Game",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            this.dispose();
            SudokuGamePanel newGame = new SudokuGamePanel(mainMenu, cafeManager, difficulty);
            newGame.setVisible(true);
        }
    }

    private void updateBoardDisplay() {
        int[][] board = engine.getBoard();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col].setValue(board[row][col]);
            }
        }
    }

    private void returnToMenu() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        this.dispose();
        mainMenu.setVisible(true);
        mainMenu.updateDisplay();
    }
}