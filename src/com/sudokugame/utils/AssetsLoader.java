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
        colors.put("menu_bg", new Color(255, 240, 245));
        colors.put("game_bg", new Color(245, 245, 240));
        colors.put("cafe_bg", new Color(255, 250, 250));

        colors.put("button_primary", new Color(70, 130, 180));
        colors.put("button_hover", new Color(100, 160, 210));
        colors.put("button_text", Color.WHITE);

        colors.put("cell_normal", Color.WHITE);
        colors.put("cell_selected", new Color(173, 216, 230));
        colors.put("cell_error", new Color(255, 182, 193));
        colors.put("header_bg", new Color(70, 130, 180));
        colors.put("header_text", Color.WHITE);
        colors.put("grid_bg", Color.WHITE);
        colors.put("control_bg", new Color(70, 130, 180));
        colors.put("control_text", Color.WHITE);
    }

    private static void loadFonts() {
        fonts.put("title", new Font("Arial", Font.BOLD, 36));
        fonts.put("header", new Font("Arial", Font.BOLD, 16));
        fonts.put("normal", new Font("Arial", Font.PLAIN, 12));
        fonts.put("button", new Font("Arial", Font.BOLD, 14));
        fonts.put("cell", new Font("Arial", Font.BOLD, 18));
    }

    private static void loadImages() {
        try {
            images.put("menu_bg", new ImageIcon("assets/images/menu_bg.jpg").getImage());
            images.put("game_bg", new ImageIcon("assets/images/game_bg.jpg").getImage());
            images.put("cafe_scene", new ImageIcon("assets/images/cafe_scene.jpg").getImage());

            if (images.get("menu_bg") == null) {
                images.put("menu_bg", new ImageIcon("assets/images/menu_bg.jpeg").getImage());
            }
            if (images.get("game_bg") == null) {
                images.put("game_bg", new ImageIcon("assets/images/game_bg.jpeg").getImage());
            }
            if (images.get("cafe_scene") == null) {
                images.put("cafe_scene", new ImageIcon("assets/images/cafe_scene.jpeg").getImage());
            }

            System.out.println(":) Imagini încărcate cu succes!");
        } catch (Exception e) {
            System.out.println(":( Nu s-au putut încărca imaginile: " + e.getMessage());
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