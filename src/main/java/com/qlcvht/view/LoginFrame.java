package com.qlcvht.view;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.dao.TaiKhoanDAO;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPass;
    private JComboBox<String> cbQuickLogin;
    private JButton btnLogin;
    private JLabel lblStatus;

    public LoginFrame() {
        setTitle("??ng Nh?p - H? Th?ng Qu?n L? C? V?n H?c T?p & C?nh B?o H?c V?");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 640);
        setMinimumSize(new Dimension(440, 600));
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(15, 23, 42),
                    0, getHeight(), new Color(30, 41, 59)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(new EmptyBorder(32, 28, 16, 28));

        JLabel lblBadge = new JLabel("??", SwingConstants.CENTER);
        lblBadge.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        lblBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("QU?N L? C? V?N H?C T?P", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.fontBold(18));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("V? C?NH B?O H?C V?", SwingConstants.CENTER);
        lblSub.setFont(UITheme.fontBold(14));
        lblSub.setForeground(new Color(147, 197, 253));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(lblBadge);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(lblTitle);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(lblSub);

        root.add(topPanel, BorderLayout.NORTH);

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(22, 26, 22, 26));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        JLabel lblQuick = new JLabel("??ng nh?p m?u nhanh:");
        lblQuick.setFont(UITheme.fontBold(11));
        lblQuick.setForeground(UITheme.PRIMARY);
        card.add(lblQuick, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        cbQuickLogin = new JComboBox<>(new String[]{
            "?? admin (Qu?n tr? vi?n)",
            "?? cv_nguynvanan (TS. Nguy?n V?n An)",
            "?? cv_tranthibinh (ThS. Tr?n Th? B?nh)",
            "??? quanly (Tr??ng khoa CNTT)"
        });
        cbQuickLogin.setFont(UITheme.fontPlain(13));
        cbQuickLogin.addActionListener(e -> {
            int idx = cbQuickLogin.getSelectedIndex();
            if (idx == 0) txtUsername.setText("admin");
            else if (idx == 1) txtUsername.setText("cv_nguynvanan");
            else if (idx == 2) txtUsername.setText("cv_tranthibinh");
            else if (idx == 3) txtUsername.setText("quanly");
            txtPassword.setText("123456");
        });
        card.add(cbQuickLogin, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(4, 0, 4, 0);
        JLabel lblUser = new JLabel("T?n ??ng nh?p:");
        lblUser.setFont(UITheme.fontBold(12));
        lblUser.setForeground(new Color(51, 65, 85));
        card.add(lblUser, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        txtUsername = new JTextField("admin");
        txtUsername.setFont(UITheme.fontPlain(13));
        txtUsername.setPreferredSize(new Dimension(340, 38));
        card.add(txtUsername, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(4, 0, 4, 0);
        JLabel lblPass = new JLabel("M?t kh?u:");
        lblPass.setFont(UITheme.fontBold(12));
        lblPass.setForeground(new Color(51, 65, 85));
        card.add(lblPass, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 6, 0);
        txtPassword = new JPasswordField("123456");
        txtPassword.setFont(UITheme.fontPlain(13));
        txtPassword.setPreferredSize(new Dimension(340, 38));
        card.add(txtPassword, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 6, 0);
        chkShowPass = new JCheckBox("Hi?n th? m?t kh?u");
        chkShowPass.setFont(UITheme.fontPlain(12));
        chkShowPass.setOpaque(false);
        chkShowPass.setForeground(UITheme.TEXT_SECONDARY);
        chkShowPass.addActionListener(e -> {
            txtPassword.setEchoChar(chkShowPass.isSelected() ? (char) 0 : '•');
        });
        card.add(chkShowPass, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(2, 0, 6, 0);
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(UITheme.fontBold(11));
        lblStatus.setForeground(UITheme.DANGER);
        card.add(lblStatus, gbc);

        gbc.gridy = 8;
        gbc.insets = new Insets(4, 0, 8, 0);
        btnLogin = UITheme.createButton("??NG NH?P", UITheme.PRIMARY, Color.WHITE);
        btnLogin.setFont(UITheme.fontBold(13));
        btnLogin.setPreferredSize(new Dimension(340, 42));
        btnLogin.addActionListener(e -> onLogin());
        card.add(btnLogin, gbc);

        gbc.gridy = 9;
        gbc.insets = new Insets(4, 0, 0, 0);
        String dbInfo = DatabaseConnection.isUsingSQLite() ? "? ?ang d?ng CSDL SQLite Offline" : "? ?ang d?ng CSDL MySQL Online";
        JLabel hint = new JLabel(dbInfo, SwingConstants.CENTER);
        hint.setFont(UITheme.fontPlain(11));
        hint.setForeground(new Color(100, 116, 139));
        card.add(hint, gbc);

        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.setBorder(new EmptyBorder(0, 28, 28, 28));
        cardWrapper.add(card, BorderLayout.CENTER);

        root.add(cardWrapper, BorderLayout.CENTER);
        add(root);

        txtPassword.addActionListener(e -> onLogin());
        txtUsername.addActionListener(e -> txtPassword.requestFocus());
    }

    private void onLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setForeground(UITheme.DANGER);
            lblStatus.setText("Vui l?ng nh?p ??y ?? t?n ??ng nh?p v? m?t kh?u!");
            return;
        }

        lblStatus.setForeground(UITheme.PRIMARY);
        lblStatus.setText("?ang x?c th?c th?ng tin...");
        btnLogin.setEnabled(false);

        SwingUtilities.invokeLater(() -> {
            TaiKhoan user = new TaiKhoanDAO().login(username, password);
            if (user != null) {
                dispose();
                new MainFrame(user).setVisible(true);
            } else {
                lblStatus.setForeground(UITheme.DANGER);
                lblStatus.setText("T?n ??ng nh?p ho?c m?t kh?u kh?ng ??ng!");
                btnLogin.setEnabled(true);
                txtPassword.selectAll();
                txtPassword.requestFocus();
            }
        });
    }
}
