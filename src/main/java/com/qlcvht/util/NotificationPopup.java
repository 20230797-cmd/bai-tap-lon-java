package com.qlcvht.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NotificationPopup {

    public static void showInfo(String title, String message) {
        showPopup(null, title, message, null);
    }

    public static void showPopup(Window parent, String title, String message, Runnable onClickAction) {
        SwingUtilities.invokeLater(() -> {
            try {
                Toolkit.getDefaultToolkit().beep();
            } catch (Exception ignored) {}

            JWindow window = new JWindow(parent);
            window.setLayout(new BorderLayout());
            window.setAlwaysOnTop(true);

            JPanel container = new JPanel(new BorderLayout(10, 8)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // Background shadow effect
                    g2.setColor(new Color(255, 255, 255));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                    // Left primary accent indicator
                    g2.setColor(new Color(37, 99, 235));
                    g2.fillRoundRect(0, 0, 6, getHeight(), 16, 16);

                    // Soft sleek border
                    g2.setColor(new Color(203, 213, 225));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                    g2.dispose();
                }
            };
            container.setOpaque(false);
            container.setBorder(new EmptyBorder(12, 16, 12, 16));
            container.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Top Header: Title + Close Button
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setOpaque(false);

            JLabel lblTitle = new JLabel("🔔 " + (title != null ? title : "Thông Báo Mới"));
            lblTitle.setFont(UITheme.fontBold(13));
            lblTitle.setForeground(UITheme.PRIMARY_DARK);

            JLabel btnClose = new JLabel("✕", SwingConstants.CENTER);
            btnClose.setFont(UITheme.fontBold(14));
            btnClose.setForeground(UITheme.TEXT_SECONDARY);
            btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnClose.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    window.dispose();
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    btnClose.setForeground(UITheme.DANGER);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    btnClose.setForeground(UITheme.TEXT_SECONDARY);
                }
            });

            topPanel.add(lblTitle, BorderLayout.WEST);
            topPanel.add(btnClose, BorderLayout.EAST);
            container.add(topPanel, BorderLayout.NORTH);

            // Center: Message body
            String cleanMsg = message != null ? message : "";
            if (cleanMsg.length() > 140) {
                cleanMsg = cleanMsg.substring(0, 137) + "...";
            }
            JLabel lblMsg = new JLabel("<html><body style='width: 250px; font-family: Segoe UI, sans-serif; font-size: 11px; color: #334155; line-height: 1.3;'>" 
                + escapeHtml(cleanMsg) + "</body></html>");
            container.add(lblMsg, BorderLayout.CENTER);

            // Bottom: Action button bar
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.setOpaque(false);

            JLabel lblHint = new JLabel("Nhấn để xem chi tiết", SwingConstants.LEFT);
            lblHint.setFont(UITheme.fontPlain(10));
            lblHint.setForeground(new Color(100, 116, 139));
            bottomPanel.add(lblHint, BorderLayout.WEST);

            JButton btnView = UITheme.createButton("Đọc ngay ➜", new Color(37, 99, 235), Color.WHITE);
            btnView.setFont(UITheme.fontBold(11));
            btnView.setPreferredSize(new Dimension(100, 26));
            btnView.addActionListener(e -> {
                window.dispose();
                if (onClickAction != null) {
                    onClickAction.run();
                }
            });
            bottomPanel.add(btnView, BorderLayout.EAST);

            container.add(bottomPanel, BorderLayout.SOUTH);

            // Click anywhere on container to open and dismiss
            container.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    window.dispose();
                    if (onClickAction != null) {
                        onClickAction.run();
                    }
                }
            });

            window.getContentPane().add(container);
            window.setSize(340, 135);

            // Calculate bottom-right position on screen
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle screenBounds = ge.getMaximumWindowBounds();
            int x = screenBounds.x + screenBounds.width - window.getWidth() - 25;
            int y = screenBounds.y + screenBounds.height - window.getHeight() - 25;
            window.setLocation(x, y);

            window.setVisible(true);

            // Auto-dispose timer after 8 seconds if not clicked
            Timer timer = new Timer(8000, e -> window.dispose());
            timer.setRepeats(false);
            timer.start();
        });
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}
