package com.qlcvht.view.dialog;

import com.qlcvht.dao.TaiKhoanDAO;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.util.PasswordUtil;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DoiMatKhauDialog extends JDialog {

    private final TaiKhoan currentUser;
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    private JPasswordField txtMatKhauCu;
    private JPasswordField txtMatKhauMoi;
    private JPasswordField txtXacNhan;

    public DoiMatKhauDialog(Frame parent, TaiKhoan user) {
        super(parent, "Đổi Mật Khẩu Tài Khoản", true);
        this.currentUser = user;
        initUI();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(460, 360));
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel lbl = new JLabel("THAY ĐỔI MẬT KHẨU BẢO MẬT");
        lbl.setFont(UITheme.FONT_SUBHEADER);
        lbl.setForeground(Color.WHITE);
        header.add(lbl, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        int row = 0;

        addLabel(form, gbc, row, "Mật khẩu hiện tại (*):");
        txtMatKhauCu = new JPasswordField();
        addField(form, gbc, row, txtMatKhauCu); row++;

        addLabel(form, gbc, row, "Mật khẩu mới (*):");
        txtMatKhauMoi = new JPasswordField();
        addField(form, gbc, row, txtMatKhauMoi); row++;

        addLabel(form, gbc, row, "Xác nhận mật khẩu (*):");
        txtXacNhan = new JPasswordField();
        addField(form, gbc, row, txtXacNhan); row++;

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollForm, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT));

        JButton btnSave = UITheme.createButton("Cập Nhật Mật Khẩu", UITheme.PRIMARY, Color.WHITE);
        btnSave.addActionListener(e -> onSave());

        JButton btnCancel = UITheme.createButton("Hủy Bỏ", new Color(200, 205, 215), UITheme.TEXT_PRIMARY);
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void addLabel(JPanel p, GridBagConstraints gbc, int row, String text) {
        gbc.gridy = row; gbc.gridx = 0; gbc.weightx = 0.0; gbc.gridwidth = 1;
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BODY);
        p.add(lbl, gbc);
    }

    private void addField(JPanel p, GridBagConstraints gbc, int row, JComponent field) {
        gbc.gridy = row; gbc.gridx = 1; gbc.weightx = 1.0;
        field.setFont(UITheme.FONT_BODY);
        p.add(field, gbc);
    }

    private void onSave() {
        String passCu = new String(txtMatKhauCu.getPassword()).trim();
        String passMoi = new String(txtMatKhauMoi.getPassword()).trim();
        String xacNhan = new String(txtXacNhan.getPassword()).trim();

        if (passCu.isEmpty() || passMoi.isEmpty() || xacNhan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ các trường mật khẩu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!PasswordUtil.verifyPassword(passCu, currentUser.getMatKhau())) {
            JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không chính xác!", "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (passMoi.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có tối thiểu 6 ký tự!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!passMoi.equals(xacNhan)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp với mật khẩu mới!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ok = taiKhoanDAO.doiMatKhau(currentUser.getId(), passMoi);
        if (ok) {
            currentUser.setMatKhau(PasswordUtil.hashPassword(passMoi));
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thất bại! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
