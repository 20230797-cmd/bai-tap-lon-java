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
        setTitle("\u0110\u0103ng Nh\u1EADp - H\u1EC7 Th\u1ED1ng Qu\u1EA3n L\u00FD C\u1ED1 V\u1EA5n H\u1ECDc T\u1EADp & C\u1EA3nh B\u00E1o H\u1ECDc V\u1EE5");
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

        JLabel lblBadge = new JLabel("HỌC VIỆN / ĐẠI HỌC", SwingConstants.CENTER);
        lblBadge.setFont(UITheme.fontBold(13));
        lblBadge.setForeground(new Color(147, 197, 253));
        lblBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("QUẢN LÝ CỐ VẤN HỌC TẬP", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.fontBold(18));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("VÀ CẢNH BÁO HỌC VỤ", SwingConstants.CENTER);
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
        JLabel lblQuick = new JLabel("Đăng nhập mẫu nhanh:");
        lblQuick.setFont(UITheme.fontBold(11));
        lblQuick.setForeground(UITheme.PRIMARY);
        card.add(lblQuick, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        cbQuickLogin = new JComboBox<>(new String[]{
            "admin (Quản trị viên)",
            "cv_nguynvanan (TS. Nguyễn Văn An)",
            "cv_tranthibinh (ThS. Trần Thị Bình)",
            "quanly (Trưởng khoa CNTT)",
            "20230001 (Sinh viên: Nguyễn Văn Nam - Tier 1 Xuất sắc)",
            "20230009 (Sinh viên: Phạm Minh Tuấn - Tier 3 Cảnh báo 1)",
            "20230010 (Sinh viên: Vũ Đức Hải - Tier 3 Cảnh báo 2)"
        });
        cbQuickLogin.setFont(UITheme.fontPlain(13));
        cbQuickLogin.addActionListener(e -> {
            int idx = cbQuickLogin.getSelectedIndex();
            if (idx == 0) txtUsername.setText("admin");
            else if (idx == 1) txtUsername.setText("cv_nguynvanan");
            else if (idx == 2) txtUsername.setText("cv_tranthibinh");
            else if (idx == 3) txtUsername.setText("quanly");
            else if (idx == 4) txtUsername.setText("20230001");
            else if (idx == 5) txtUsername.setText("20230009");
            else if (idx == 6) txtUsername.setText("20230010");
            txtPassword.setText("123456");
        });
        card.add(cbQuickLogin, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(4, 0, 4, 0);
        JLabel lblUser = new JLabel("Tên đăng nhập / Mã Sinh Viên (MSSV):");
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
        JLabel lblPass = new JLabel("Mật khẩu (Mặc định: 123456):");
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
        chkShowPass = new JCheckBox("Hiển thị mật khẩu");
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
        btnLogin = UITheme.createButton("ĐĂNG NHẬP", UITheme.PRIMARY, Color.WHITE);
        btnLogin.setFont(UITheme.fontBold(13));
        btnLogin.setPreferredSize(new Dimension(340, 42));
        btnLogin.addActionListener(e -> onLogin());
        card.add(btnLogin, gbc);

        gbc.gridy = 9;
        gbc.insets = new Insets(4, 0, 0, 0);
        String dbInfo = "● CSDL: " + DatabaseConnection.getDatabaseEngineName() + (DatabaseConnection.isUsingSQLite() ? " (Offline)" : " (Online)");
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
            lblStatus.setText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
            return;
        }

        lblStatus.setForeground(UITheme.PRIMARY);
        lblStatus.setText("Đang xác thực thông tin...");
        btnLogin.setEnabled(false);

        SwingUtilities.invokeLater(() -> {
            TaiKhoan user = new TaiKhoanDAO().login(username, password);
            if (user != null) {
                dispose();
                if ("SINH_VIEN".equalsIgnoreCase(user.getVaiTro())) {
                    new StudentMainFrame(user).setVisible(true);
                } else {
                    new MainFrame(user).setVisible(true);
                }
            } else {
                lblStatus.setForeground(UITheme.DANGER);
                lblStatus.setText("Tên đăng nhập hoặc mật khẩu không đúng!");
                btnLogin.setEnabled(true);
                txtPassword.selectAll();
                txtPassword.requestFocus();
            }
        });
    }
}
