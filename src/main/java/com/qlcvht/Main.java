package com.qlcvht;

import com.formdev.flatlaf.FlatIntelliJLaf;
import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.view.LoginFrame;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Thiết lập FlatLaf Look & Feel hiện đại cho Java Swing
        try {
            FlatIntelliJLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception ex) {
            System.err.println("Không thể áp dụng FlatLaf Look & Feel: " + ex.getMessage());
        }

        // Chạy giao diện Swing trên Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            boolean isConnected = DatabaseConnection.testConnection();
            if (DatabaseConnection.isUsingSQLite()) {
                System.out.println("ℹ️ Đang sử dụng CSDL nhúng SQLite dự phòng tự động.");
            } else {
                System.out.println("✅ Kết nối CSDL MySQL thành công!");
            }

            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
