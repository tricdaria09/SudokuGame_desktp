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

    // 🎯 SISTEM DE INIMI
    private int hearts;
    private int maxHearts;
    private JPanel heartsPanel;
    private JLabel heartsLabel;

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

        // 🎯 SETEAZĂ NUMĂRUL DE INIMI PE BAZA DIFICULTĂȚII
        this.maxHearts = getMaxHeartsForDifficulty();
        this.hearts = maxHearts;

        initializeUI();
        startTimer();
    }

    private int getMaxHeartsForDifficulty() {
        switch (difficulty) {
            case EASY: return 5;   // 5 inimi pentru ușor
            case MEDIUM: return 3; // 3 inimi pentru mediu
            case HARD: return 2;   // 2 inimi pentru greu
            default: return 3;
        }
    }

    private void initializeUI() {
        setTitle("Sudoku - " + difficulty.getName() + " ❤️" + hearts);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 1000);
        setLocationRelativeTo(null);
        setResizable(false);

        // 🖼️ BACKGROUND JOC MODERN
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // ⬇️🖼️ BACKGROUND JOC SUDOKU - SCHIMBĂ "game_bg.jpg" CU IMAGINEA TA!
                Image background = AssetsLoader.getImage("game_bg");
                if (background != null) {
                    g2d.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback gradient modern
                    GradientPaint gradient = new GradientPaint(0, 0, new Color(240, 248, 255),
                            0, getHeight(), new Color(230, 240, 250));
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        // 🎯 HEADER CU INFO ȘI INIMI
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // 🔢 GRID SUDOKU CENTRAL
        mainPanel.add(createSudokuGrid(), BorderLayout.CENTER);

        // 🎮 PANEL CONTROL
        mainPanel.add(createControlPanel(), BorderLayout.SOUTH);

        // 🔢 PANEL NUMERE (Tastatură virtuală)
        mainPanel.add(createNumberPanel(), BorderLayout.EAST);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(50, 50, 50, 200));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // ⏰ TIMER
        timerLabel = new JLabel("00:00");
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // 🎯 DIFICULTATE
        difficultyLabel = new JLabel(difficulty.getName() + " | ❤️ " + hearts + "/" + maxHearts);
        difficultyLabel.setForeground(difficulty.getColor());
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 20));
        difficultyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 💰 MONEDE
        coinsLabel = new JLabel("💰 " + cafeManager.getMoney() + " coins");
        coinsLabel.setForeground(Color.YELLOW);
        coinsLabel.setFont(new Font("Arial", Font.BOLD, 18));

        panel.add(timerLabel, BorderLayout.WEST);
        panel.add(difficultyLabel, BorderLayout.CENTER);
        panel.add(coinsLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createSudokuGrid() {
        // 🎯 GRID CU LINII ACCENTUATE
        sudokuGrid = new JPanel(new GridLayout(9, 9, 1, 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // 🎯 LINII GROASE PENTRU GRUPURILE 3x3 - ACCENTUATE!
                g2d.setColor(new Color(0, 0, 0, 200));
                g2d.setStroke(new BasicStroke(4)); // LINII MAI GROASE

                for (int i = 1; i < 3; i++) {
                    // Linii verticale groase
                    int x = i * getWidth() / 3;
                    g2d.drawLine(x, 0, x, getHeight());

                    // Linii orizontale groase
                    int y = i * getHeight() / 3;
                    g2d.drawLine(0, y, getWidth(), y);
                }

                // 🎯 LINII SUBȚIRI PENTRU CELULE
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.setStroke(new BasicStroke(1));

                for (int i = 1; i < 9; i++) {
                    if (i % 3 != 0) { // Doar liniile dintre celulele mici
                        // Linii verticale subțiri
                        int x = i * getWidth() / 9;
                        g2d.drawLine(x, 0, x, getHeight());

                        // Linii orizontale subțiri
                        int y = i * getHeight() / 9;
                        g2d.drawLine(0, y, getWidth(), y);
                    }
                }
            }
        };

        sudokuGrid.setBackground(Color.BLACK);
        sudokuGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        sudokuGrid.setPreferredSize(new Dimension(600, 600));

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

    // 🎯 CLASA INTERNĂ PENTRU CELULE SUDOKU (IMBUNĂTĂȚITĂ)
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

            setPreferredSize(new Dimension(60, 60));
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

            // 🖼️ BACKGROUND CELULĂ - SCHIMBĂ "cell_normal.png" CU IMAGINEA TA!
            Image cellImage = AssetsLoader.getImage("cell_normal");
            if (cellImage != null) {
                g2d.drawImage(cellImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                // Fallback la culori
                if (isSelected) {
                    g2d.setColor(new Color(100, 180, 255, 150));
                } else if (isError) {
                    g2d.setColor(new Color(255, 100, 100, 120));
                } else {
                    // Pattern pentru grupurile 3x3
                    boolean isLight = ((row / 3) + (col / 3)) % 2 == 0;
                    g2d.setColor(isLight ? new Color(240, 240, 240) : new Color(220, 220, 220));
                }
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

            // ✏️ TEXT
            if (value != 0) {
                g2d.setColor(isFixed ? new Color(0, 0, 139) : Color.BLACK); // Albastru închis pentru numere fixe
                g2d.setFont(new Font("Arial", isFixed ? Font.BOLD : Font.PLAIN, 22));

                FontMetrics fm = g2d.getFontMetrics();
                String text = String.valueOf(value);
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;

                g2d.drawString(text, x, y);
            }

            // 🎯 LINII DE ORIENTARE CÂND E SELECTAT
            if (isSelected) {
                g2d.setColor(new Color(0, 100, 255, 100));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRect(1, 1, getWidth()-2, getHeight()-2);

                // 🎯 HIGHLIGHT LINIE ȘI COLOANĂ
                highlightRowAndColumn(g2d);
            }
        }

        private void highlightRowAndColumn(Graphics2D g2d) {
            // 🎯 HIGHLIGHT TOATĂ LINIA (orizontal)
            g2d.setColor(new Color(100, 180, 255, 50));
            for (int c = 0; c < 9; c++) {
                if (c != col) {
                    int cellWidth = getWidth();
                    int cellHeight = getHeight();
                    g2d.fillRect(c * cellWidth, 0, cellWidth, cellHeight);
                }
            }

            // 🎯 HIGHLIGHT TOATĂ COLOANA (vertical)
            for (int r = 0; r < 9; r++) {
                if (r != row) {
                    int cellWidth = getWidth();
                    int cellHeight = getHeight();
                    g2d.fillRect(0, r * cellHeight, cellWidth, cellHeight);
                }
            }

            // 🎯 HIGHLIGHT GRUPUL 3x3
            int startRow = row - row % 3;
            int startCol = col - col % 3;
            g2d.setColor(new Color(100, 180, 255, 30));
            for (int r = startRow; r < startRow + 3; r++) {
                for (int c = startCol; c < startCol + 3; c++) {
                    if (r != row || c != col) {
                        int cellWidth = getWidth();
                        int cellHeight = getHeight();
                        g2d.fillRect(c * cellWidth, r * cellHeight, cellWidth, cellHeight);
                    }
                }
            }
        }

        private void selectCell() {
            // Deselectează celula precedentă
            if (selectedRow != -1 && selectedCol != -1) {
                cells[selectedRow][selectedCol].isSelected = false;
                cells[selectedRow][selectedCol].repaint();
            }

            // Selectează celula nouă doar dacă nu e fixă
            if (!isFixed) {
                isSelected = true;
                selectedRow = row;
                selectedCol = col;
                repaint();

                // 🎯 REPAINT TOATĂ LINIA ȘI COLOANA PENTRU HIGHLIGHT
                for (int i = 0; i < 9; i++) {
                    if (i != col) cells[row][i].repaint();
                    if (i != row) cells[i][col].repaint();
                }

                // 🎯 REPAINT GRUPUL 3x3
                int startRow = row - row % 3;
                int startCol = col - col % 3;
                for (int r = startRow; r < startRow + 3; r++) {
                    for (int c = startCol; c < startCol + 3; c++) {
                        if (r != row || c != col) {
                            cells[r][c].repaint();
                        }
                    }
                }
            }
        }

        public void setValue(int newValue) {
            if (!isFixed) {
                this.value = newValue;
                engine.setCellValue(row, col, newValue);

                // 🎯 VERIFICĂ DACA VALOAREA ESTE CORECTĂ
                if (newValue != 0) {
                    boolean isCorrect = (newValue == engine.getSolutionValue(row, col));
                    if (!isCorrect) {
                        loseHeart(); // 🎯 PIERDE O INIMĂ PENTRU GREȘEALĂ
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

    // 🎯 SISTEM DE INIMI
    private void loseHeart() {
        hearts--;
        updateHeartsDisplay();

        // 🎯 ACTUALIZEAZĂ TITLUL FEREASTREI
        setTitle("Sudoku - " + difficulty.getName() + " ❤️" + hearts + "/" + maxHearts);
        difficultyLabel.setText(difficulty.getName() + " | ❤️ " + hearts + "/" + maxHearts);

        // 🎯 VERIFICĂ DACA JOCUL S-A TERMINAT
        if (hearts <= 0) {
            gameOver();
        } else {
            // 🎯 ANIMAȚIE SCĂDERE INIMI
            showHeartLossAnimation();
        }
    }

    private void updateHeartsDisplay() {
        // 🎯 ACTUALIZEAZĂ AFIȘAJUL INIMILOR
        difficultyLabel.setText(difficulty.getName() + " | ❤️ " + hearts + "/" + maxHearts);
    }

    private void showHeartLossAnimation() {
        // 🎯 ANIMAȚIE SCĂDERE INIMI
        Timer heartTimer = new Timer(100, new ActionListener() {
            int flashCount = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                difficultyLabel.setForeground(flashCount % 2 == 0 ? Color.RED : difficulty.getColor());
                flashCount++;
                if (flashCount > 6) {
                    difficultyLabel.setForeground(difficulty.getColor());
                    ((Timer)e.getSource()).stop();
                }
            }
        });
        heartTimer.start();

        JOptionPane.showMessageDialog(this,
                "💔 Wrong number!\n" +
                        "❤️ You lost one heart!\n" +
                        "Hearts remaining: " + hearts + "/" + maxHearts,
                "Incorrect Move",
                JOptionPane.WARNING_MESSAGE);
    }

    private void gameOver() {
        gameTimer.stop();

        JOptionPane.showMessageDialog(this,
                "💔 GAME OVER!\n\n" +
                        "⏱️ Time: " + timerLabel.getText() + "\n" +
                        "❤️ Hearts: 0/" + maxHearts + "\n" +
                        "🎯 Difficulty: " + difficulty.getName() + "\n\n" +
                        "You ran out of hearts!\n" +
                        "Better luck next time!",
                "Game Over",
                JOptionPane.ERROR_MESSAGE);

        returnToMenu();
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(0, 0, 0, 150));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // 🎯 BUTOANE MODERNE
        panel.add(createControlButton("✅ Check", new Color(76, 175, 80), e -> checkSolution()));
        panel.add(createControlButton("💡 Hint (25¢)", new Color(255, 152, 0), e -> showHint()));
        panel.add(createControlButton("🔍 Solve", new Color(156, 39, 176), e -> showSolution()));
        panel.add(createControlButton("🔄 New Game", new Color(33, 150, 243), e -> newGame()));
        panel.add(createControlButton("🏠 Menu", new Color(244, 67, 54), e -> returnToMenu()));

        return panel;
    }

    private JPanel createNumberPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBackground(new Color(0, 0, 0, 100));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));
        panel.setPreferredSize(new Dimension(100, 600));

        for (int i = 1; i <= 9; i++) {
            JButton numberBtn = new JButton(String.valueOf(i));
            numberBtn.setFont(new Font("Arial", Font.BOLD, 20));
            numberBtn.setBackground(new Color(70, 130, 180));
            numberBtn.setForeground(Color.WHITE);
            numberBtn.setFocusPainted(false);
            numberBtn.addActionListener(e -> inputNumber(Integer.parseInt(numberBtn.getText())));
            panel.add(numberBtn);
        }

        JButton clearBtn = new JButton("✕");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 20));
        clearBtn.setBackground(Color.RED);
        clearBtn.setForeground(Color.WHITE);
        clearBtn.addActionListener(e -> inputNumber(0));
        panel.add(clearBtn);

        return panel;
    }

    private JButton createControlButton(String text, Color color, ActionListener action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Buton modern cu gradient
                GradientPaint gradient = new GradientPaint(0, 0, color, 0, getHeight(), color.darker());
                g2.setPaint(gradient);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));

                // Text
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2.drawString(getText(), x, y);
            }
        };

        button.setPreferredSize(new Dimension(120, 45));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
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
                    "❌ Not quite right!\n" +
                            "Check the red cells for errors.",
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
        // 🎉 ANIMAȚIE VICTORIE
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

        // 🎯 BONUS PENTRU INIMILE RĂMASE
        int heartBonus = hearts * 10;
        reward += heartBonus;

        JOptionPane.showMessageDialog(this,
                "🎉 CONGRATULATIONS!\n\n" +
                        "⏱️ Time: " + timerLabel.getText() + "\n" +
                        "❤️ Hearts remaining: " + hearts + "/" + maxHearts + " (+" + heartBonus + " coins)\n" +
                        "💰 Total Reward: " + reward + " coins\n" +
                        "🏪 Cafe Bonus: +" + cafeManager.getCafeBonus() + " coins\n" +
                        "⭐ Difficulty: " + difficulty.getName() + "\n\n" +
                        "Your cafe has earned extra income!",
                "Puzzle Complete!",
                JOptionPane.INFORMATION_MESSAGE);

        returnToMenu();
    }

    private void showHint() {
        if (cafeManager.canAfford(25)) {
            cafeManager.addMoney(-25);
            engine.provideHint();
            updateBoardDisplay();
            coinsLabel.setText("💰 " + cafeManager.getMoney() + " coins");

            JOptionPane.showMessageDialog(this,
                    "💡 Hint applied!\n" +
                            "One cell has been filled for you.",
                    "Hint Used",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "❌ Not enough coins for hint!\n" +
                            "Need: 25 coins",
                    "Insufficient Funds",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showSolution() {
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to see the solution?\n" +
                        "This will end the current game.",
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
                "Start a new game?\n" +
                        "Current progress will be lost.",
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