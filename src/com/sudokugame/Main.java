package com.sudokugame;

import com.sudokugame.ui.MainMeniu;
import com.sudokugame.utils.AssetsLoader;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Încarcă assets-urile la început
        AssetsLoader.loadAssets();

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainMeniu().setVisible(true);
        });
    }
}