package com.qlcvht;

import com.formdev.flatlaf.FlatLightLaf;
import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.util.UITheme;
import com.qlcvht.view.LoginFrame;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;

public class Main {

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");

        try {
            FlatLightLaf.setup();
            initGlobalTypography();
        } catch (Exception ex) {
            System.err.println("[WARN] FlatLaf init error: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            boolean connected = DatabaseConnection.testConnection();
            if (connected) {
                System.out.println("[INFO] K?t n?i CSDL th?nh c?ng: " + DatabaseConnection.getDatabaseType());
            } else {
                System.err.println("[WARN] Kh?ng th? kh?i t?o CSDL!");
            }
            new LoginFrame().setVisible(true);
        });
    }

    private static void initGlobalTypography() {
        Font defaultFont = new Font("Segoe UI", Font.PLAIN, 13);

        UIManager.put("defaultFont", defaultFont);
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 8);
        UIManager.put("ScrollBar.trackArc", 8);

        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                Font f = (Font) value;
                UIManager.put(key, new FontUIResource("Segoe UI", f.getStyle(), f.getSize() > 0 ? f.getSize() : 13));
            }
        }
    }
}
