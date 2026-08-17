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
    private JButton btnLogin;
    private JLabel lblStatus;
    private JCheckBox chkShowPass;
    private JComboBox<String> cbQuickLogin;

    public LoginFrame() {
        setTitle("Đăng nhập Hệ thống - Quản lý Cố vấn Học tập & Cảnh báo Học vụ (CNJ09)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 620);
        setResizable(false);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(13, 71, 161), 0, getHeight(), new Color(25, 118, 210));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setLayout(new BorderLayout());

        // Top Banner
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(new EmptyBorder(30, 30, 20, 30));

        JLabel lblBadge = new JLabel("🎓", SwingConstants.CENTER);
        lblBadge.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 46));
        lblBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("HỆ THỐNG CỐ VẤN HỌC TẬP", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.fontBold(18));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("VÀ QUẢN LÝ CẢNH BÁO HỌC VỤ", SwingConstants.CENTER);
        lblSub.setFont(UITheme.fontBold(13));
        lblSub.setForeground(new Color(210, 230, 255));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(lblBadge);
        topPanel.add(Box.createVerticalStrut(8));
        topPanel.add(lblTitle);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(lblSub);

        root.add(topPanel, BorderLayout.NORTH);

        // Form Card
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(24, 30, 24, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.insets = new Insets(4, 0, 4, 0);

        // Quick login selector
        gbc.gridy = 0;
        JLabel lblQuick = new JLabel("Chọn tài khoản mẫu đăng nhập nhanh:");
        lblQuick.setFont(UITheme.fontBold(11));
        lblQuick.setForeground(UITheme.PRIMARY_DARK);
        card.add(lblQuick, gbc);

        gbc.gridy = 1;
        cbQuickLogin = new JComboBox<>(new String[]{
            "admin (Quản trị viên)",
            "cv_nguynvanan (TS. Nguyễn Văn An - Cố vấn)",
            "cv_tranthibinh (ThS. Trần Thị Bình - Cố vấn)",
            "quanly (Trưởng khoa CNTT)"
        });
        cbQuickLogin.setFont(UITheme.FONT_BODY);
        cbQuickLogin.addActionListener(e -> {
            int idx = cbQuickLogin.getSelectedIndex();
            if (idx == 0) txtUsername.setText("admin");
            else if (idx == 1) txtUsername.setText("cv_nguynvanan");
            else if (idx == 2) txtUsername.setText("cv_tranthibinh");
            else if (idx == 3) txtUsername.setText("quanly");
            txtPassword.setText("123456");
        });
        card.add(cbQuickLogin, gbc);

        // Username
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 4, 0);
        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(UITheme.FONT_BODY_BOLD);
        lblUser.setForeground(UITheme.TEXT_SECONDARY);
        card.add(lblUser, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(2, 0, 4, 0);
        txtUsername = new JTextField("admin");
        txtUsername.setFont(UITheme.FONT_BODY);
        txtUsername.setPreferredSize(new Dimension(340, 38));
        styleField(txtUsername);
        card.add(txtUsername, gbc);

        // Password
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 4, 0);
        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(UITheme.FONT_BODY_BOLD);
        lblPass.setForeground(UITheme.TEXT_SECONDARY);
        card.add(lblPass, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(2, 0, 4, 0);
        txtPassword = new JPasswordField("123456");
        txtPassword.setFont(UITheme.FONT_BODY);
        txtPassword.setPreferredSize(new Dimension(340, 38));
        styleField(txtPassword);
        card.add(txtPassword, gbc);

        // Show password checkbox
        gbc.gridy = 6;
        chkShowPass = new JCheckBox("Hiển thị mật khẩu");
        chkShowPass.setFont(UITheme.FONT_SMALL);
        chkShowPass.setOpaque(false);
        chkShowPass.setForeground(UITheme.TEXT_SECONDARY);
        chkShowPass.addActionListener(e -> {
            if (chkShowPass.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('\u2022');
            }
        });
        card.add(chkShowPass, gbc);

        // Status label
        gbc.gridy = 7;
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(UITheme.FONT_SMALL);
        lblStatus.setForeground(UITheme.DANGER);
        card.add(lblStatus, gbc);

        // Login button
        gbc.gridy = 8;
        gbc.insets = new Insets(8, 0, 4, 0);
        btnLogin = UITheme.createButton("ĐĂNG NHẬP HỆ THỐNG", UITheme.PRIMARY, Color.WHITE);
        btnLogin.setFont(UITheme.FONT_BTN_LARGE);
        btnLogin.setPreferredSize(new Dimension(340, 44));
        btnLogin.addActionListener(e -> onLogin());
        card.add(btnLogin, gbc);

        // DB Status hint
        gbc.gridy = 9;
        gbc.insets = new Insets(10, 0, 0, 0);
        JLabel hint = new JLabel("Trạng thái CSDL: " + DatabaseConnection.getDatabaseType(), SwingConstants.CENTER);
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(new Color(130, 140, 150));
        card.add(hint, gbc);

        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.setBorder(new EmptyBorder(0, 30, 30, 30));
        cardWrapper.add(card, BorderLayout.CENTER);

        root.add(cardWrapper, BorderLayout.CENTER);
        add(root);

        txtPassword.addActionListener(e -> onLogin());
        txtUsername.addActionListener(e -> txtPassword.requestFocus());
    }

    private void styleField(JComponent field) {
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_MEDIUM, 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        field.setBackground(Color.WHITE);
    }

    private void onLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setForeground(UITheme.DANGER);
            lblStatus.setText("Vui lòng nhập đầy đủ Tên đăng nhập và Mật khẩu!");
            return;
        }

        lblStatus.setForeground(UITheme.INFO);
        lblStatus.setText("Đang kiểm tra xác thực tài khoản...");
        btnLogin.setEnabled(false);

        SwingUtilities.invokeLater(() -> {
            TaiKhoan user = new TaiKhoanDAO().login(username, password);
            if (user != null) {
                dispose();
                new MainFrame(user).setVisible(true);
            } else {
                lblStatus.setForeground(UITheme.DANGER);
                lblStatus.setText("Tên đăng nhập hoặc mật khẩu không chính xác!");
                btnLogin.setEnabled(true);
                txtPassword.selectAll();
                txtPassword.requestFocus();
            }
        });
    }
}