package com.qlcvht.view.dialog;

import com.qlcvht.dao.CoVanDAO;
import com.qlcvht.dao.SinhVienDAO;
import com.qlcvht.model.LopHoc;
import com.qlcvht.model.SinhVien;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.util.List;

/**
 * Dialog them hoac sua thong tin sinh vien.
 * Neu sv == null => che do them moi.
 */
public class ThemSuaSinhVienDialog extends JDialog {

    private final SinhVien svEdit; // null = them moi
    private final SinhVienDAO svDAO = new SinhVienDAO();
    private final CoVanDAO coVanDAO = new CoVanDAO();

    private JTextField txtMaSv, txtHoTen, txtNgaySinh, txtEmail, txtSdt;
    private JComboBox<String> cbGioiTinh;
    private JComboBox<Object> cbLop;
    private JComboBox<String> cbTrangThai;

    private boolean saved = false;

    public ThemSuaSinhVienDialog(Frame parent, SinhVien sv) {
        super(parent, sv == null ? "Them sinh vien moi" : "Sua thong tin sinh vien", true);
        this.svEdit = sv;
        initUI();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(550, 520));
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel lbl = new JLabel(svEdit == null ? "THEM SINH VIEN MOI" : "SUA THONG TIN SINH VIEN");
        lbl.setFont(UITheme.FONT_SUBHEADER);
        lbl.setForeground(Color.WHITE);
        header.add(lbl, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_WHITE);
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 8, 7, 8);

        // Ma SV
        int row = 0;
        addLabel(form, gbc, row, "Ma sinh vien (*):");
        txtMaSv = new JTextField();
        if (svEdit != null) { txtMaSv.setText(svEdit.getMaSv()); txtMaSv.setEditable(false); txtMaSv.setBackground(new Color(245,245,245)); }
        addField(form, gbc, row, txtMaSv);

        // Ho ten
        row++;
        addLabel(form, gbc, row, "Ho va ten (*):");
        txtHoTen = new JTextField(svEdit != null ? svEdit.getHoTen() : "");
        addField(form, gbc, row, txtHoTen);

        // Ngay sinh
        row++;
        addLabel(form, gbc, row, "Ngay sinh (YYYY-MM-DD):");
        txtNgaySinh = new JTextField(svEdit != null && svEdit.getNgaySinh() != null ? svEdit.getNgaySinh().toString() : "");
        addField(form, gbc, row, txtNgaySinh);

        // Gioi tinh
        row++;
        addLabel(form, gbc, row, "Gioi tinh:");
        cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nu"});
        if (svEdit != null && "Nu".equals(svEdit.getGioiTinh())) cbGioiTinh.setSelectedItem("Nu");
        addField(form, gbc, row, cbGioiTinh);

        // Email
        row++;
        addLabel(form, gbc, row, "Email:");
        txtEmail = new JTextField(svEdit != null ? svEdit.getEmail() : "");
        addField(form, gbc, row, txtEmail);

        // SDT
        row++;
        addLabel(form, gbc, row, "So dien thoai:");
        txtSdt = new JTextField(svEdit != null ? svEdit.getSoDienThoai() : "");
        addField(form, gbc, row, txtSdt);

        // Lop hoc
        row++;
        addLabel(form, gbc, row, "Lop hoc (*):");
        cbLop = new JComboBox<>();
        List<LopHoc> listLop = coVanDAO.getAllLopHoc();
        for (LopHoc l : listLop) cbLop.addItem(l);
        if (svEdit != null) {
            for (int i = 0; i < cbLop.getItemCount(); i++) {
                Object item = cbLop.getItemAt(i);
                if (item instanceof LopHoc && ((LopHoc) item).getMaLop().equals(svEdit.getMaLop())) {
                    cbLop.setSelectedIndex(i); break;
                }
            }
        }
        addField(form, gbc, row, cbLop);

        // Trang thai
        row++;
        addLabel(form, gbc, row, "Trang thai hoc vu:");
        cbTrangThai = new JComboBox<>(new String[]{"DANG_HOC", "CANH_BAO_1", "CANH_BAO_2", "BUOC_THOI_HOC", "DA_TOT_NGHIEP"});
        if (svEdit != null) cbTrangThai.setSelectedItem(svEdit.getTrangThai());
        addField(form, gbc, row, cbTrangThai);

        // Required note
        row++;
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 2; gbc.weightx = 0;
        JLabel note = new JLabel("(*) Cac truong bat buoc");
        note.setFont(UITheme.FONT_SMALL);
        note.setForeground(UITheme.TEXT_SECONDARY);
        form.add(note, gbc);

        add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnPanel.setBackground(UITheme.BG_WHITE);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT));

        JButton btnSave = createBtn("Luu", UITheme.PRIMARY, Color.WHITE);
        btnSave.addActionListener(e -> onSave());

        JButton btnCancel = createBtn("Huy", UITheme.BORDER_MEDIUM, UITheme.TEXT_PRIMARY);
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
        String maSv   = txtMaSv.getText().trim();
        String hoTen  = txtHoTen.getText().trim();
        String ngayStr= txtNgaySinh.getText().trim();
        String email  = txtEmail.getText().trim();
        String sdt    = txtSdt.getText().trim();
        String gt     = (String) cbGioiTinh.getSelectedItem();
        String tt     = (String) cbTrangThai.getSelectedItem();

        if (maSv.isEmpty() || hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui long nhap Ma sinh vien va Ho ten!", "Canh bao", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LopHoc lop = null;
        Object selLop = cbLop.getSelectedItem();
        if (selLop instanceof LopHoc) lop = (LopHoc) selLop;
        if (lop == null) {
            JOptionPane.showMessageDialog(this, "Vui long chon Lop hoc!", "Canh bao", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date ngaySinh = null;
        if (!ngayStr.isEmpty()) {
            try { ngaySinh = Date.valueOf(ngayStr); }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dinh dang ngay sinh khong hop le! Dung YYYY-MM-DD", "Loi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Dinh dang email khong hop le!", "Loi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!sdt.isEmpty() && !sdt.matches("^\\d{9,11}$")) {
            JOptionPane.showMessageDialog(this, "So dien thoai phai tu 9-11 chu so!", "Loi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SinhVien sv = new SinhVien(maSv, hoTen, ngaySinh, gt, email, sdt, lop.getMaLop(), tt);
        boolean ok;
        if (svEdit == null) {
            ok = svDAO.addSinhVien(sv);
        } else {
            ok = svDAO.updateSinhVien(sv);
        }

        if (ok) {
            JOptionPane.showMessageDialog(this, (svEdit == null ? "Them" : "Cap nhat") + " sinh vien thanh cong!", "Thong bao", JOptionPane.INFORMATION_MESSAGE);
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Luu that bai! Ma sinh vien co the da ton tai.", "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }

    private JButton createBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BTN);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}