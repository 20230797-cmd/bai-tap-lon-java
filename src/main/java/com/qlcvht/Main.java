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
            if (connected) {
                System.out.println("[INFO] Kết nối CSDL thành công: " + DatabaseConnection.getDatabaseType());
                // Tự động kiểm tra và tạo dữ liệu ảo điểm danh nếu chưa có nhiều
                try {
                    com.qlcvht.dao.DiemDanhDAO ddDao = new com.qlcvht.dao.DiemDanhDAO();
                    java.util.Map<String, Integer> stats = ddDao.getAttendanceStats("ALL");
                    int totalAtt = stats.values().stream().mapToInt(Integer::intValue).sum();
                    if (totalAtt < 20) {
                        int mockCnt = ddDao.generateMockAttendance(null);
                        System.out.println("[INFO] Đã tự động tạo " + mockCnt + " bản ghi điểm danh mẫu cho hệ thống.");
                    }
                } catch (Exception ignored) {}
            } else {
                System.err.println("[WARN] Không thể khởi tạo CSDL!");
            }
            new LoginFrame().setVisible(true);
        });
    }
}