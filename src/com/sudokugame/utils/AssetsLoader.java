package com.sudokugame.utils;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AssetsLoader {
    private static Map<String, Image> images = new HashMap<>();
    private static Map<String, Font> fonts = new HashMap<>();

    public static void loadAssets() {
        loadImages();
        loadFonts();
    }

    private static void loadImages() {
        try {
            // 🖼️ BACKGROUND-URI
            images.put("menu_bg", new ImageIcon("assets/images/background/menu_bg.jpg").getImage());
            images.put("game_bg", new ImageIcon("assets/images/background/game_bg.jpg").getImage());
            images.put("cafe_scene", new ImageIcon("assets/images/background/cafe_scene.jpg").getImage());

            // 🖼️ BUTOANE UI
            images.put("btn_normal", new ImageIcon("assets/images/ui/button_normal.png").getImage());
            images.put("btn_hover", new ImageIcon("assets/images/ui/button_hover.png").getImage());
            images.put("btn_pressed", new ImageIcon("assets/images/ui/button_pressed.png").getImage());

            // 🖼️ CAFENEA - OBIECTE
            images.put("coffee_machine", new ImageIcon("assets/images/cafe/coffee_machine.png").getImage());
            images.put("pastry_counter", new ImageIcon("assets/images/cafe/pastry_counter.png").getImage());
            images.put("register", new ImageIcon("assets/images/cafe/register.png").getImage());
            images.put("table", new ImageIcon("assets/images/cafe/table.png").getImage());
            images.put("customer", new ImageIcon("assets/images/cafe/customer_1.png").getImage());

            // 🖼️ JOC SUDOKU
            images.put("cell_normal", new ImageIcon("assets/images/game/cell_normal.png").getImage());
            images.put("cell_selected", new ImageIcon("assets/images/game/cell_selected.png").getImage());
            images.put("cell_error", new ImageIcon("assets/images/game/cell_error.png").getImage());

        } catch (Exception e) {
            System.out.println("⚠️ Unele imagini nu au putut fi încărcate: " + e.getMessage());
        }
    }

    private static void loadFonts() {
        // Poți adăuga fonturi custom aici
    }

    public static Image getImage(String key) {
        return images.get(key);
    }

    public static Font getFont(String key) {
        return fonts.get(key);
    }
}