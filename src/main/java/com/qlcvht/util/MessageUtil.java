package com.qlcvht.util;

import javax.swing.*;
import java.awt.*;

/**
 * Tiện ích hiển thị thông báo chuẩn hóa giao diện người dùng (Học phần Java Swing - Lab 05)
 */
public class MessageUtil {

    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Thông Báo Hệ Thống",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Thành Công",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Cảnh Báo",
            JOptionPane.WARNING_MESSAGE
        );
    }

    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Lỗi Hệ Thống",
            JOptionPane.ERROR_MESSAGE
        );
    }

    public static boolean confirm(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(
            parent,
            message,
            "Xác Nhận Thao Tác",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
}
