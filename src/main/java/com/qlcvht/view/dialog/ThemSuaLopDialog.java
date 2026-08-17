package com.qlcvht.view.dialog;

import com.qlcvht.dao.CoVanDAO;
import com.qlcvht.model.CoVanHocTap;
import com.qlcvht.model.LopHoc;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ThemSuaLopDialog extends JDialog {

    private final LopHoc lopEdit;
    private final CoVanDAO coVanDAO = new CoVanDAO();

    private JTextField txtMaLop;
    private JTextField txtTenLop;
    private JTextField txtKhoa;
    private JTextField txtKhoaHoc;
    private JComboBox<Object> cbCoVan;

    private boolean saved = false;

    public ThemSuaLopDialog(Frame parent, LopHoc lop) {
        super(parent, lop == null ? "Thêm Lớp Học Mới" : "Chỉnh sửa Thông tin Lớp Học", true);
        this.lopEdit = lop;
        initUI();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(520, 440));
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel lbl = new JLabel(lopEdit == null ? "THÊM LỚP HỌC MỚI" : "CẬP NHẬT THÔNG TIN LỚP HỌC");
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

        // Mã lớp
        addLabel(form, gbc, row, "Mã lớp (*):");
        txtMaLop = new JTextField();
        if (lopEdit != null) {
            txtMaLop.setText(lopEdit.getMaLop());
            txtMaLop.setEditable(false);
            txtMaLop.setBackground(new Color(245, 245, 245));
        }
        addField(form, gbc, row, txtMaLop); row++;

        // Tên lớp
        addLabel(form, gbc, row, "Tên lớp (*):");
        txtTenLop = new JTextField(lopEdit != null ? lopEdit.getTenLop() : "");
        addField(form, gbc, row, txtTenLop); row++;

        // Khoa
        addLabel(form, gbc, row, "Khoa / Bộ môn:");
        txtKhoa = new JTextField(lopEdit != null ? lopEdit.getKhoa() : "Công nghệ thông tin");
        addField(form, gbc, row, txtKhoa); row++;

        // Khóa học
        addLabel(form, gbc, row, "Khóa tuyển sinh (Năm):");
        txtKhoaHoc = new JTextField(lopEdit != null ? String.valueOf(lopEdit.getKhoaHoc()) : "2023");
        addField(form, gbc, row, txtKhoaHoc); row++;

        // CVHT phụ trách
        addLabel(form, gbc, row, "Cố vấn học tập phụ trách:");
        cbCoVan = new JComboBox<>();
        cbCoVan.addItem("-- Chưa phân công --");
        List<CoVanHocTap> listCv = coVanDAO.getAllCoVan();
        for (CoVanHocTap cv : listCv) {
            cbCoVan.addItem(cv.getMaCvht() + " - " + cv.getHoTen());
        }
        if (lopEdit != null && lopEdit.getMaCvht() != null) {
            for (int i = 0; i < cbCoVan.getItemCount(); i++) {
                Object it = cbCoVan.getItemAt(i);
                if (it.toString().startsWith(lopEdit.getMaCvht())) {
                    cbCoVan.setSelectedIndex(i);
                    break;
                }
            }
        }
        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollForm, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT));

        JButton btnSave = UITheme.createButton("Lưu Lớp Học", UITheme.PRIMARY, Color.WHITE);
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
        String maLop = txtMaLop.getText().trim();
        String tenLop = txtTenLop.getText().trim();
        String khoa = txtKhoa.getText().trim();
        String khStr = txtKhoaHoc.getText().trim();

        if (maLop.isEmpty() || tenLop.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã lớp và Tên lớp!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int khoaHoc = 2023;
        try {
            if (!khStr.isEmpty()) khoaHoc = Integer.parseInt(khStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Khóa tuyển sinh phải là năm hợp lệ (VD: 2023)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String maCvht = null;
        Object selCv = cbCoVan.getSelectedItem();
        if (selCv != null && !selCv.toString().startsWith("--")) {
            maCvht = selCv.toString().split(" - ")[0].trim();
        }

        LopHoc lop = new LopHoc(maLop, tenLop, khoa, khoaHoc, maCvht);
        boolean ok;
        if (lopEdit == null) {
            ok = coVanDAO.addLopHoc(lop);
        } else {
            ok = coVanDAO.updateLopHoc(lop);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, (lopEdit == null ? "Thêm" : "Cập nhật") + " lớp học thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu thất bại! Mã lớp có thể đã tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}
