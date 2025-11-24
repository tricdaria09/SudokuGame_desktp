package com.sudokugame.utils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AssetsLoader {
    private static Map<String, Color> colors = new HashMap<>();
    private static Map<String, Font> fonts = new HashMap<>();
    private static Map<String, Image> images = new HashMap<>();

    public static void loadAssets() {
        loadColors();
        loadFonts();
        loadImages();
    }

    private static void loadColors() {
        // 🎨 PALETA DE CULORI: alb, roz, bej deschis
        colors.put("menu_bg", new Color(255, 240, 245));     // Roz foarte deschis
        colors.put("game_bg", new Color(245, 245, 240));     // Bej deschis
        colors.put("cafe_bg", new Color(255, 250, 250));     // Alb cu nuanță caldă
        colors.put("button_primary", new Color(255, 200, 200)); // Roz deschis
        colors.put("button_hover", new Color(255, 150, 150));   // Roz mediu
        colors.put("cell_normal", Color.WHITE);
        colors.put("cell_selected", new Color(255, 240, 245));  // Roz foarte deschis
        colors.put("cell_error", new Color(255, 220, 220));     // Roz pentru erori
        colors.put("header_bg", new Color(210, 180, 140));      // Bej
        colors.put("grid_bg", Color.WHITE);
        colors.put("control_bg", new Color(200, 180, 160));     // Bej mai închis
    }

    private static void loadFonts() {
        // Fonturi de bază
        fonts.put("title", new Font("Arial", Font.BOLD, 48));
        fonts.put("header", new Font("Arial", Font.BOLD, 20));
        fonts.put("normal", new Font("Arial", Font.PLAIN, 14));
        fonts.put("button", new Font("Arial", Font.BOLD, 18));
        fonts.put("cell", new Font("Arial", Font.BOLD, 20));
    }

    private static void loadImages() {
        try {
            // Poți adăuga imagini aici dacă vrei
            // images.put("customer", new ImageIcon("assets/customer.png").getImage());
        } catch (Exception e) {
            System.out.println("⚠️ Unele imagini nu au putut fi încărcate: " + e.getMessage());
        }
    }

    public static Color getColor(String key) {
        return colors.getOrDefault(key, Color.WHITE);
    }

    public static Font getFont(String key) {
        return fonts.getOrDefault(key, new Font("Arial", Font.PLAIN, 12));
    }

    public static Image getImage(String key) {
        return images.get(key);
    }
}