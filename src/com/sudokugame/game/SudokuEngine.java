package com.sudokugame.game;

import java.util.Random;

public class SudokuEngine {
    private int[][] board;
    private int[][] solution;
    private int[][] userBoard;
    private Difficulty difficulty;
    private Random random;

    public SudokuEngine(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.random = new Random();
        generateNewPuzzle();
    }

    private void generateNewPuzzle() {
        // Generate a complete solution
        solution = generateCompleteSolution();
        board = copyArray(solution);

        // Remove numbers based on difficulty
        removeNumbers(difficulty.getCellsToRemove());
        userBoard = copyArray(board);
    }

    private int[][] generateCompleteSolution() {
        int[][] grid = new int[9][9];
        solve(grid);
        return grid;
    }

    private boolean solve(int[][] grid) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (grid[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isValid(grid, row, col, num)) {
                            grid[row][col] = num;
                            if (solve(grid)) {
                                return true;
                            }
                            grid[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValid(int[][] grid, int row, int col, int num) {
        // Check row
        for (int x = 0; x < 9; x++) {
            if (grid[row][x] == num) return false;
        }

        // Check column
        for (int x = 0; x < 9; x++) {
            if (grid[x][col] == num) return false;
        }

        // Check 3x3 box
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i + startRow][j + startCol] == num) return false;
            }
        }

        return true;
    }

    private void removeNumbers(int cellsToRemove) {
        int removed = 0;
        while (removed < cellsToRemove) {
            int row = random.nextInt(9);
            int col = random.nextInt(9);
            if (board[row][col] != 0) {
                board[row][col] = 0;
                removed++;
            }
        }
    }

    private int[][] copyArray(int[][] original) {
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, 9);
        }
        return copy;
    }

    public boolean checkSolution() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (userBoard[i][j] != solution[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public void setCellValue(int row, int col, int value) {
        if (board[row][col] == 0) { // Only allow setting empty cells
            userBoard[row][col] = value;
        }
    }

    public int getCellValue(int row, int col) {
        return userBoard[row][col];
    }

    public int getSolutionValue(int row, int col) {
        return solution[row][col];
    }

    public boolean isFixedCell(int row, int col) {
        return board[row][col] != 0;
    }

    public void provideHint() {
        // Find an empty cell and fill it with solution
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (userBoard[i][j] == 0) {
                    userBoard[i][j] = solution[i][j];
                    return;
                }
            }
        }
    }

    public void solveCompletely() {
        userBoard = copyArray(solution);
    }

    public int[][] getBoard() {
        return userBoard;
    }
}