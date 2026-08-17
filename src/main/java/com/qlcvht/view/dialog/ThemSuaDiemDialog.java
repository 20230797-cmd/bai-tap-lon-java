package com.qlcvht.view.dialog;

import com.qlcvht.dao.KetQuaHocTapDAO;
import com.qlcvht.dao.SinhVienDAO;
import com.qlcvht.model.KetQuaHocTap;
import com.qlcvht.model.SinhVien;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ThemSuaDiemDialog extends JDialog {

    private final KetQuaHocTap kqEdit;
    private final KetQuaHocTapDAO kqDAO = new KetQuaHocTapDAO();
    private final SinhVienDAO svDAO = new SinhVienDAO();

    private JComboBox<String> cbSinhVien;
    private JComboBox<Integer> cbHocKy;
    private JComboBox<String> cbNamHoc;
    private JTextField txtGpaHocKy;
    private JTextField txtGpaTichLuy;
    private JTextField txtSoTinChiNo;

    private boolean saved = false;

    public ThemSuaDiemDialog(Frame parent, KetQuaHocTap kq) {
        super(parent, kq == null ? "Nhập Kết quả Học tập Học kỳ" : "Cập nhật Điểm & Kết quả Học tập", true);
        this.kqEdit = kq;
        initUI();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(540, 480));
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel lbl = new JLabel(kqEdit == null ? "NHẬP BẢNG ĐIỂM HỌC KỲ" : "CẬP NHẬT KẾT QUẢ HỌC TẬP");
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

        // Sinh viên
        addLabel(form, gbc, row, "Sinh viên (*):");
        cbSinhVien = new JComboBox<>();
        List<SinhVien> listSv = svDAO.getAllSinhVien();
        for (SinhVien sv : listSv) {
            cbSinhVien.addItem(sv.getMaSv() + " - " + sv.getHoTen() + " (" + (sv.getTenLop() != null ? sv.getTenLop() : sv.getMaLop()) + ")");
        }
        if (kqEdit != null) {
            for (int i = 0; i < cbSinhVien.getItemCount(); i++) {
                if (cbSinhVien.getItemAt(i).startsWith(kqEdit.getMaSv())) {
                    cbSinhVien.setSelectedIndex(i);
                    cbSinhVien.setEnabled(false);
                    break;
                }
            }
        }
        addField(form, gbc, row, cbSinhVien); row++;

        // Học kỳ
        addLabel(form, gbc, row, "Học kỳ (*):");
        cbHocKy = new JComboBox<>(new Integer[]{1, 2, 3});
        if (kqEdit != null) cbHocKy.setSelectedItem(kqEdit.getHocKy());
        addField(form, gbc, row, cbHocKy); row++;

        // Năm học
        addLabel(form, gbc, row, "Năm học (*):");
        cbNamHoc = new JComboBox<>(new String[]{"2022-2023", "2023-2024", "2024-2025", "2025-2026", "2026-2027"});
        if (kqEdit != null) cbNamHoc.setSelectedItem(kqEdit.getNamHoc());
        else cbNamHoc.setSelectedItem("2023-2024");
        addField(form, gbc, row, cbNamHoc); row++;

        // GPA học kỳ
        addLabel(form, gbc, row, "GPA Học kỳ (Thang 4):");
        txtGpaHocKy = new JTextField(kqEdit != null ? String.valueOf(kqEdit.getGpaHocKy()) : "3.00");
        addField(form, gbc, row, txtGpaHocKy); row++;

        // GPA tích lũy
        addLabel(form, gbc, row, "GPA Tích lũy - CPA (Thang 4):");
        txtGpaTichLuy = new JTextField(kqEdit != null ? String.valueOf(kqEdit.getGpaTichLuy()) : "3.00");
        addField(form, gbc, row, txtGpaTichLuy); row++;

        // Tín chỉ nợ
        addLabel(form, gbc, row, "Số tín chỉ nợ / Chưa đạt:");
        txtSoTinChiNo = new JTextField(kqEdit != null ? String.valueOf(kqEdit.getSoTinChiNo()) : "0");
        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollForm, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT));

        JButton btnSave = UITheme.createButton("Lưu Kết Quả", UITheme.PRIMARY, Color.WHITE);
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
        String selSv = (String) cbSinhVien.getSelectedItem();
        if (selSv == null) return;
        String maSv = selSv.split(" - ")[0].trim();

        int hocKy = (Integer) cbHocKy.getSelectedItem();
        String namHoc = (String) cbNamHoc.getSelectedItem();

        double gpaHk, gpaTl;
        int noTc;

        try {
            gpaHk = Double.parseDouble(txtGpaHocKy.getText().trim());
            gpaTl = Double.parseDouble(txtGpaTichLuy.getText().trim());
            noTc = Integer.parseInt(txtSoTinChiNo.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Điểm GPA và số tín chỉ nợ phải là các con số hợp lệ!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (gpaHk < 0.0 || gpaHk > 4.0 || gpaTl < 0.0 || gpaTl > 4.0) {
            JOptionPane.showMessageDialog(this, "Điểm GPA phải nằm trong khoảng từ 0.0 đến 4.0!", "Lỗi giá trị", JOptionPane.ERROR_MESSAGE);
            return;
        }

        KetQuaHocTap kq = new KetQuaHocTap(0, maSv, hocKy, namHoc, gpaHk, gpaTl, noTc);
        boolean ok = kqDAO.saveOrUpdate(kq);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Đã lưu kết quả học tập thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu kết quả thất bại! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}
