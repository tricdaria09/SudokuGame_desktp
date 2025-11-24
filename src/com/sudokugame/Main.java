package com.sudokugame;

import com.sudokugame.ui.MainMeniu;
import com.sudokugame.utils.AssetsLoader;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            AssetsLoader.loadAssets();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainMeniu mainMenu = new MainMeniu();
            mainMenu.setVisible(true);
        });
    }
}