package com.qlcvht.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NotificationPopup {

    public static void showPopup(Window parent, String title, String message, Runnable onClickAction) {
        SwingUtilities.invokeLater(() -> {
            try {
                Toolkit.getDefaultToolkit().beep();
            } catch (Exception ignored) {}

            JWindow window = new JWindow(parent);
            window.setLayout(new BorderLayout());
            window.setAlwaysOnTop(true);

            JPanel container = new JPanel(new BorderLayout(10, 10)) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                    
                    // Left primary accent bar
                    g2.setColor(UITheme.PRIMARY);
                    g2.fillRoundRect(0, 0, 6, getHeight(), 16, 16);

                    // Soft border
                    g2.setColor(new Color(203, 213, 225));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                }
            };
            container.setOpaque(false);
            container.setBorder(new EmptyBorder(12, 16, 12, 16));

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
            JLabel lblMsg = new JLabel("<html><body style='width: 250px; font-family: Segoe UI, sans-serif; font-size: 11px; color: #334155;'>" 
                + (message != null ? escapeHtml(message) : "") + "</body></html>");
            container.add(lblMsg, BorderLayout.CENTER);

            // Bottom: Action button
            if (onClickAction != null) {
                JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
                bottomPanel.setOpaque(false);

                JButton btnView = UITheme.createButton("Xem ngay ➜", UITheme.PRIMARY_LIGHT, UITheme.PRIMARY);
                btnView.setFont(UITheme.fontBold(11));
                btnView.setPreferredSize(new Dimension(100, 26));
                btnView.addActionListener(e -> {
                    window.dispose();
                    onClickAction.run();
                });
                bottomPanel.add(btnView);
                container.add(bottomPanel, BorderLayout.SOUTH);
            }

            window.getContentPane().add(container);
            window.setSize(330, onClickAction != null ? 125 : 95);

            // Calculate bottom-right position on screen
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle screenBounds = ge.getMaximumWindowBounds();
            int x = screenBounds.x + screenBounds.width - window.getWidth() - 25;
            int y = screenBounds.y + screenBounds.height - window.getHeight() - 25;
            window.setLocation(x, y);

            window.setVisible(true);

            // Auto-dispose timer after 6 seconds
            Timer timer = new Timer(6000, e -> window.dispose());
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
