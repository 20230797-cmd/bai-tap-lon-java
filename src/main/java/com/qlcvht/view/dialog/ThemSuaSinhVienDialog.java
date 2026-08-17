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
 * Dialog thêm hoặc sửa thông tin sinh viên với giao diện Tiếng Việt chuẩn và validate chi tiết.
 */
public class ThemSuaSinhVienDialog extends JDialog {

    private final SinhVien svEdit; // null = thêm mới
    private final SinhVienDAO svDAO = new SinhVienDAO();
    private final CoVanDAO coVanDAO = new CoVanDAO();

    private JTextField txtMaSv, txtHoTen, txtNgaySinh, txtEmail, txtSdt;
    private JComboBox<String> cbGioiTinh;
    private JComboBox<Object> cbLop;
    private JComboBox<String> cbTrangThai;

    private boolean saved = false;

    public ThemSuaSinhVienDialog(Frame parent, SinhVien sv) {
        super(parent, sv == null ? "Thêm Sinh viên Mới" : "Chỉnh sửa Thông tin Sinh viên", true);
        this.svEdit = sv;
        initUI();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(560, 530));
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel lbl = new JLabel(svEdit == null ? "THÊM SINH VIÊN MỚI" : "CẬP NHẬT THÔNG TIN SINH VIÊN");
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

        // Mã SV
        int row = 0;
        addLabel(form, gbc, row, "Mã sinh viên (*):");
        txtMaSv = new JTextField();
        if (svEdit != null) { 
            txtMaSv.setText(svEdit.getMaSv()); 
            txtMaSv.setEditable(false); 
            txtMaSv.setBackground(new Color(245, 245, 245)); 
        }
        addField(form, gbc, row, txtMaSv);

        // Họ tên
        row++;
        addLabel(form, gbc, row, "Họ và tên (*):");
        txtHoTen = new JTextField(svEdit != null ? svEdit.getHoTen() : "");
        addField(form, gbc, row, txtHoTen);

        // Ngày sinh
        row++;
        addLabel(form, gbc, row, "Ngày sinh (YYYY-MM-DD):");
        txtNgaySinh = new JTextField(svEdit != null && svEdit.getNgaySinh() != null ? svEdit.getNgaySinh().toString() : "");
        addField(form, gbc, row, txtNgaySinh);

        // Giới tính
        row++;
        addLabel(form, gbc, row, "Giới tính:");
        cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        if (svEdit != null && "Nữ".equalsIgnoreCase(svEdit.getGioiTinh())) {
            cbGioiTinh.setSelectedItem("Nữ");
        }
        addField(form, gbc, row, cbGioiTinh);

        // Email
        row++;
        addLabel(form, gbc, row, "Địa chỉ Email:");
        txtEmail = new JTextField(svEdit != null ? svEdit.getEmail() : "");
        addField(form, gbc, row, txtEmail);

        // SĐT
        row++;
        addLabel(form, gbc, row, "Số điện thoại:");
        txtSdt = new JTextField(svEdit != null ? svEdit.getSoDienThoai() : "");
        addField(form, gbc, row, txtSdt);

        // Lớp học
        row++;
        addLabel(form, gbc, row, "Lớp quản lý (*):");
        cbLop = new JComboBox<>();
        List<LopHoc> listLop = coVanDAO.getAllLopHoc();
        for (LopHoc l : listLop) cbLop.addItem(l);
        if (svEdit != null) {
            for (int i = 0; i < cbLop.getItemCount(); i++) {
                Object item = cbLop.getItemAt(i);
                if (item instanceof LopHoc && ((LopHoc) item).getMaLop().equals(svEdit.getMaLop())) {
                    cbLop.setSelectedIndex(i); 
                    break;
                }
            }
        }
        addField(form, gbc, row, cbLop);

        // Trạng thái học vụ
        row++;
        addLabel(form, gbc, row, "Trạng thái học vụ:");
        cbTrangThai = new JComboBox<>(new String[]{
            "DANG_HOC - Đang học", 
            "CANH_BAO_1 - Cảnh báo mức 1", 
            "CANH_BAO_2 - Cảnh báo mức 2", 
            "BUOC_THOI_HOC - Buộc thôi học", 
            "DA_TOT_NGHIEP - Đã tốt nghiệp"
        });
        if (svEdit != null && svEdit.getTrangThai() != null) {
            for (int i = 0; i < cbTrangThai.getItemCount(); i++) {
                if (cbTrangThai.getItemAt(i).startsWith(svEdit.getTrangThai())) {
                    cbTrangThai.setSelectedIndex(i);
                    break;
                }
            }
        }
        addField(form, gbc, row, cbTrangThai);

        // Ghi chú bắt buộc
        row++;
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 2; gbc.weightx = 0;
        JLabel note = new JLabel("(*) Các trường thông tin bắt buộc phải điền");
        note.setFont(UITheme.FONT_SMALL);
        note.setForeground(UITheme.DANGER);
        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollForm, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnPanel.setBackground(UITheme.BG_WHITE);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT));

        JButton btnSave = UITheme.createButton("Lưu Thông Tin", UITheme.PRIMARY, Color.WHITE);
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
        String maSv   = txtMaSv.getText().trim();
        String hoTen  = txtHoTen.getText().trim();
        String ngayStr= txtNgaySinh.getText().trim();
        String email  = txtEmail.getText().trim();
        String sdt    = txtSdt.getText().trim();
        String gt     = (String) cbGioiTinh.getSelectedItem();
        
        String rawTt = (String) cbTrangThai.getSelectedItem();
        String tt = "DANG_HOC";
        if (rawTt != null && rawTt.contains(" - ")) {
            tt = rawTt.split(" - ")[0];
        }

        if (maSv.isEmpty() || hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã sinh viên và Họ tên!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LopHoc lop = null;
        Object selLop = cbLop.getSelectedItem();
        if (selLop instanceof LopHoc) lop = (LopHoc) selLop;
        if (lop == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Lớp học cho sinh viên!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date ngaySinh = null;
        if (!ngayStr.isEmpty()) {
            try { 
                ngaySinh = Date.valueOf(ngayStr); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Định dạng ngày sinh không hợp lệ! Định dạng chuẩn: YYYY-MM-DD (VD: 2005-08-15)", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Định dạng email không hợp lệ (VD: sv@huce.edu.vn)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!sdt.isEmpty() && !sdt.matches("^\\d{9,11}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải từ 9 đến 11 chữ số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, (svEdit == null ? "Thêm" : "Cập nhật") + " sinh viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu thất bại! Mã sinh viên có thể đã tồn tại trong hệ thống.", "Lỗi cơ sở dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}