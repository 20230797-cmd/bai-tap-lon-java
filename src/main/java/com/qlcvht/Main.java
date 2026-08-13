package com.qlcvht;

import com.formdev.flatlaf.FlatLightLaf;
import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.util.UITheme;
import com.qlcvht.view.LoginFrame;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Apply FlatLaf Look & Feel
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 6);
            UIManager.put("defaultFont", UITheme.FONT_BODY);
        } catch (Exception ex) {
            System.err.println("Khong the ap dung FlatLaf: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            boolean connected = DatabaseConnection.testConnection();
            if (DatabaseConnection.isUsingSQLite()) {
                System.out.println("[INFO] Dang su dung CSDL nhung SQLite (du phong).");
            } else {
                System.out.println("[INFO] Ket noi CSDL MySQL thanh cong!");
            }
            new LoginFrame().setVisible(true);
        });
    }
}