package com.qlcvht.view;

import com.qlcvht.dao.TaiKhoanDAO;
import com.qlcvht.model.TaiKhoan;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblStatus;

    public LoginFrame() {
        setTitle("Đăng nhập - Hệ thống Quản lý Cố vấn Học tập & Cảnh báo Học vụ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 480);
        setResizable(false);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header Logo / Banner
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(new Color(24, 119, 242));
        headerPanel.setPreferredSize(new Dimension(420, 120));

        JLabel lblTitle = new JLabel("QUẢN LÝ CỐ VẤN HỌC TẬP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("VÀ CẢNH BÁO HỌC VỤ", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSub.setForeground(new Color(220, 235, 252));

        JPanel titleContainer = new JPanel(new GridLayout(2, 1, 0, 5));
        titleContainer.setOpaque(false);
        titleContainer.add(lblTitle);
        titleContainer.add(lblSub);

        headerPanel.add(titleContainer);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Input Panel
        JPanel formPanel = new JPanel(null);
        formPanel.setBackground(Color.WHITE);

        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setBounds(45, 25, 330, 25);
        formPanel.add(lblUser);

        txtUsername = new JTextField("admin"); // Mặc định tài khoản mẫu
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBounds(45, 55, 330, 38);
        formPanel.add(txtUsername);

        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setBounds(45, 105, 330, 25);
        formPanel.add(lblPass);

        txtPassword = new JPasswordField("123456"); // Mặc định mật khẩu mẫu
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBounds(45, 135, 330, 38);
        formPanel.add(txtPassword);

        lblStatus = new JLabel("", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(Color.RED);
        lblStatus.setBounds(45, 180, 330, 25);
        formPanel.add(lblStatus);

        btnLogin = new JButton("ĐĂNG NHẬP");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(24, 119, 242));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBounds(45, 210, 330, 42);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> onLogin());
        formPanel.add(btnLogin);

        // Hint Footer
        JLabel lblHint = new JLabel("Tài khoản mẫu: admin / cv_nguynvanan (MK: 123456)", SwingConstants.CENTER);
        lblHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHint.setForeground(Color.GRAY);
        lblHint.setBounds(10, 270, 400, 20);
        formPanel.add(lblHint);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Accept ENTER key on password field
        txtPassword.addActionListener(e -> onLogin());
    }

    private void onLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Vui lòng nhập đầy đủ Tên đăng nhập và Mật khẩu!");
            return;
        }

        lblStatus.setForeground(new Color(24, 119, 242));
        lblStatus.setText("Đang kiểm tra thông tin đăng nhập...");
        btnLogin.setEnabled(false);

        SwingUtilities.invokeLater(() -> {
            TaiKhoan user = new TaiKhoanDAO().login(username, password);
            if (user != null) {
                dispose();
                MainFrame mainFrame = new MainFrame(user);
                mainFrame.setVisible(true);
            } else {
                lblStatus.setForeground(Color.RED);
                lblStatus.setText("Tên đăng nhập hoặc mật khẩu không chính xác!");
                btnLogin.setEnabled(true);
            }
        });
    }
}
