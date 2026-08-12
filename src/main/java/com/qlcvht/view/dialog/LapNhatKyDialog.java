package com.qlcvht.view.dialog;

import com.qlcvht.dao.NhatKyTuVanDAO;
import com.qlcvht.model.CanhBaoHocVu;
import com.qlcvht.model.NhatKyTuVan;
import com.qlcvht.model.TaiKhoan;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class LapNhatKyDialog extends JDialog {

    private final CanhBaoHocVu canhBao;
    private final TaiKhoan currentUser;
    private boolean savedSuccess = false;

    private JTextField txtMaSv;
    private JTextField txtHoTenSv;
    private JTextField txtNgayTuVan;
    private JComboBox<String> cbHinhThuc;
    private JTextArea txtNoiDung;
    private JTextArea txtNguyenNhan;
    private JTextArea txtGiaiPhap;
    private JTextArea txtCamKet;

    public LapNhatKyDialog(Frame parent, CanhBaoHocVu canhBao, TaiKhoan currentUser) {
        super(parent, "Lập Nhật ký Tư vấn Sinh viên Bị cảnh báo", true);
        this.canhBao = canhBao;
        this.currentUser = currentUser;

        initUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setSize(550, 600);

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(24, 119, 242));
        JLabel lblTitle = new JLabel("  BIÊN BẢN TƯ VẤN CẢNH BÁO HỌC VỤ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setPreferredSize(new Dimension(500, 40));
        headerPanel.add(lblTitle);
        add(headerPanel, BorderLayout.NORTH);

        // Form Content Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Sinh vien & Ho ten
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Mã Sinh viên:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtMaSv = new JTextField(canhBao != null ? canhBao.getMaSv() : "");
        txtMaSv.setEditable(false);
        formPanel.add(txtMaSv, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Họ và Tên:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtHoTenSv = new JTextField(canhBao != null ? canhBao.getHoTenSv() : "");
        txtHoTenSv.setEditable(false);
        formPanel.add(txtHoTenSv, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Ngày tư vấn (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        txtNgayTuVan = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        formPanel.add(txtNgayTuVan, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Hình thức gặp mặt:"), gbc);
        gbc.gridx = 1;
        cbHinhThuc = new JComboBox<>(new String[]{"Trực tiếp", "Online (Teams/Zoom)", "Qua điện thoại"});
        formPanel.add(cbHinhThuc, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Nội dung trao đổi:"), gbc);
        gbc.gridx = 1;
        txtNoiDung = new JTextArea(3, 20);
        txtNoiDung.setLineWrap(true);
        txtNoiDung.setWrapStyleWord(true);
        if (canhBao != null) {
            txtNoiDung.setText("Trao đổi nguyên nhân bị cảnh báo học vụ " + canhBao.getMucCanhBaoHienThi() + " (GPA: " + canhBao.getGpaXetDuyet() + ").");
        }
        formPanel.add(new JScrollPane(txtNoiDung), gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Nguyên nhân chính:"), gbc);
        gbc.gridx = 1;
        txtNguyenNhan = new JTextArea(2, 20);
        txtNguyenNhan.setLineWrap(true);
        txtNguyenNhan.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(txtNguyenNhan), gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Giải pháp khắc phục:"), gbc);
        gbc.gridx = 1;
        txtGiaiPhap = new JTextArea(2, 20);
        txtGiaiPhap.setLineWrap(true);
        txtGiaiPhap.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(txtGiaiPhap), gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Cam kết của Sinh viên:"), gbc);
        gbc.gridx = 1;
        txtCamKet = new JTextArea(2, 20);
        txtCamKet.setLineWrap(true);
        txtCamKet.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(txtCamKet), gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnSave = new JButton("💾 Lưu Nhật ký");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBackground(new Color(24, 119, 242));
        btnSave.setForeground(Color.WHITE);

        JButton btnCancel = new JButton("Hủy bỏ");

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void onSave() {
        String maCvht = (currentUser != null && currentUser.getMaRef() != null) ? currentUser.getMaRef() : "CV001";
        String ngayStr = txtNgayTuVan.getText().trim();
        Date ngayTuVan;
        try {
            ngayTuVan = Date.valueOf(ngayStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ! Vui lòng dùng YYYY-MM-DD", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (txtNoiDung.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập nội dung trao đổi!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        NhatKyTuVan nk = new NhatKyTuVan(
            0,
            txtMaSv.getText().trim(),
            maCvht,
            canhBao != null ? canhBao.getId() : null,
            ngayTuVan,
            (String) cbHinhThuc.getSelectedItem(),
            txtNoiDung.getText().trim(),
            txtNguyenNhan.getText().trim(),
            txtGiaiPhap.getText().trim(),
            txtCamKet.getText().trim()
        );

        boolean ok = new NhatKyTuVanDAO().addNhatKy(nk);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Lưu nhật ký tư vấn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            savedSuccess = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Không thể lưu nhật ký tư vấn. Vui lòng thử lại!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSavedSuccess() {
        return savedSuccess;
    }
}
