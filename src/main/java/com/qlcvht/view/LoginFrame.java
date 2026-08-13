package com.qlcvht.view;

import com.qlcvht.dao.TaiKhoanDAO;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblStatus;
    private JCheckBox chkShowPass;

    public LoginFrame() {
        setTitle("Dang nhap - He thong Quan ly Co van Hoc tap");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(440, 540);
        setResizable(false);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        // Root panel with gradient background
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

        // === TOP BANNER ===
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(new EmptyBorder(40, 30, 30, 30));

        // University initials badge
        JLabel lblBadge = new JLabel("HUCE", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255,255,255,40));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(UITheme.fontBold(22));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("HUCE")) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 3;
                g2.drawString("HUCE", x, y);
            }
        };
        lblBadge.setPreferredSize(new Dimension(80, 80));
        lblBadge.setMaximumSize(new Dimension(80, 80));
        lblBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("QUAN LY CO VAN HOC TAP", SwingConstants.CENTER);
        lblTitle.setFont(UITheme.fontBold(17));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("& CANH BAO HOC VU - HUCE", SwingConstants.CENTER);
        lblSub.setFont(UITheme.fontPlain(13));
        lblSub.setForeground(new Color(200, 220, 255));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(lblBadge);
        topPanel.add(Box.createVerticalStrut(14));
        topPanel.add(lblTitle);
        topPanel.add(Box.createVerticalStrut(4));
        topPanel.add(lblSub);

        root.add(topPanel, BorderLayout.NORTH);

        // === FORM CARD ===
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(30, 35, 30, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.insets = new Insets(6, 0, 6, 0);

        // Username label
        gbc.gridy = 0;
        JLabel lblUser = new JLabel("Ten dang nhap");
        lblUser.setFont(UITheme.FONT_BODY_BOLD);
        lblUser.setForeground(UITheme.TEXT_SECONDARY);
        card.add(lblUser, gbc);

        // Username field
        gbc.gridy = 1;
        txtUsername = new JTextField("admin");
        txtUsername.setFont(UITheme.FONT_BODY);
        txtUsername.setPreferredSize(new Dimension(330, 42));
        styleField(txtUsername);
        card.add(txtUsername, gbc);

        // Password label
        gbc.gridy = 2;
        gbc.insets = new Insets(14, 0, 6, 0);
        JLabel lblPass = new JLabel("Mat khau");
        lblPass.setFont(UITheme.FONT_BODY_BOLD);
        lblPass.setForeground(UITheme.TEXT_SECONDARY);
        card.add(lblPass, gbc);

        // Password field
        gbc.gridy = 3;
        gbc.insets = new Insets(6, 0, 6, 0);
        txtPassword = new JPasswordField("123456");
        txtPassword.setFont(UITheme.FONT_BODY);
        txtPassword.setPreferredSize(new Dimension(330, 42));
        styleField(txtPassword);
        card.add(txtPassword, gbc);

        // Show password checkbox
        gbc.gridy = 4;
        chkShowPass = new JCheckBox("Hien mat khau");
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
        gbc.gridy = 5;
        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setFont(UITheme.FONT_SMALL);
        lblStatus.setForeground(UITheme.DANGER);
        card.add(lblStatus, gbc);

        // Login button
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 0, 6, 0);
        btnLogin = new JButton("DANG NHAP");
        btnLogin.setFont(UITheme.FONT_BTN_LARGE);
        btnLogin.setBackground(UITheme.PRIMARY);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(330, 46));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> onLogin());
        card.add(btnLogin, gbc);

        // Hint
        gbc.gridy = 7;
        gbc.insets = new Insets(14, 0, 0, 0);
        JLabel hint = new JLabel("Tai khoan mau: admin / cv_nguynvanan / quanly (MK: 123456)", SwingConstants.CENTER);
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(new Color(160, 160, 160));
        card.add(hint, gbc);

        JPanel cardWrapper = new JPanel(new BorderLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.setBorder(new EmptyBorder(0, 30, 40, 30));
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
            lblStatus.setText("Vui long nhap day du Ten dang nhap va Mat khau!");
            return;
        }

        lblStatus.setForeground(UITheme.INFO);
        lblStatus.setText("Dang kiem tra thong tin dang nhap...");
        btnLogin.setEnabled(false);

        SwingUtilities.invokeLater(() -> {
            TaiKhoan user = new TaiKhoanDAO().login(username, password);
            if (user != null) {
                dispose();
                new MainFrame(user).setVisible(true);
            } else {
                lblStatus.setForeground(UITheme.DANGER);
                lblStatus.setText("Ten dang nhap hoac mat khau khong chinh xac!");
                btnLogin.setEnabled(true);
                txtPassword.selectAll();
                txtPassword.requestFocus();
            }
        });
    }
}