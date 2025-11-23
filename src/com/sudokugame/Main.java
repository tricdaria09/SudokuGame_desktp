package com.sudokugame;

import com.sudokugame.ui.MainMeniu;  // Cu iu

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainMeniu().setVisible(true);  // Cu iu
        });
    }
}