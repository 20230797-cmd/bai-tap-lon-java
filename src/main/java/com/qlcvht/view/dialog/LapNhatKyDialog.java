package com.qlcvht.view.dialog;

import com.qlcvht.dao.CanhBaoDAO;
import com.qlcvht.dao.NhatKyTuVanDAO;
import com.qlcvht.dao.SinhVienDAO;
import com.qlcvht.model.CanhBaoHocVu;
import com.qlcvht.model.NhatKyTuVan;
import com.qlcvht.model.SinhVien;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class LapNhatKyDialog extends JDialog {

    private final CanhBaoHocVu canhBao;
    private final TaiKhoan currentUser;
    private boolean savedSuccess = false;

    private JComboBox<String> cbSinhVien;
    private JTextField txtNgayTuVan;
    private JComboBox<String> cbHinhThuc;
    private JTextArea txtNoiDung, txtNguyenNhan, txtGiaiPhap, txtCamKet;

    private List<SinhVien> listSv;

    public LapNhatKyDialog(Frame parent, CanhBaoHocVu canhBao, TaiKhoan currentUser) {
        super(parent, "Biên bản Tư vấn Cố vấn Học tập", true);
        this.canhBao = canhBao;
        this.currentUser = currentUser;
        initUI();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(600, 650));
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel lbl = new JLabel("BIÊN BẢN TƯ VẤN CỐ VẤN HỌC TẬP & CẢNH BÁO HỌC VỤ");
        lbl.setFont(UITheme.FONT_SUBHEADER);
        lbl.setForeground(Color.WHITE);
        header.add(lbl, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(18, 22, 10, 22));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        int row = 0;

        // Chọn sinh viên
        addLabel(form, gbc, row, "Sinh viên tư vấn (*):");
        cbSinhVien = new JComboBox<>();
        listSv = new SinhVienDAO().getAllSinhVien();
        for (SinhVien sv : listSv) {
            cbSinhVien.addItem(sv.getMaSv() + " - " + sv.getHoTen() + " (" + (sv.getTenLop() != null ? sv.getTenLop() : sv.getMaLop()) + ")");
        }
        if (canhBao != null) {
            for (int i = 0; i < cbSinhVien.getItemCount(); i++) {
                if (cbSinhVien.getItemAt(i).startsWith(canhBao.getMaSv())) {
                    cbSinhVien.setSelectedIndex(i);
                    cbSinhVien.setEnabled(false);
                    break;
                }
            }
        }
        addField(form, gbc, row, cbSinhVien); row++;

        addLabel(form, gbc, row, "Ngày tư vấn (YYYY-MM-DD):");
        txtNgayTuVan = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        addField(form, gbc, row, txtNgayTuVan); row++;

        addLabel(form, gbc, row, "Hình thức gặp mặt:");
        cbHinhThuc = new JComboBox<>(new String[]{
            "Trực tiếp tại văn phòng bộ môn",
            "Trực tuyến (MS Teams / Zoom)",
            "Qua điện thoại",
            "Gặp mặt cùng phụ huynh",
            "Email trao đổi"
        });
        addField(form, gbc, row, cbHinhThuc); row++;

        addLabel(form, gbc, row, "Nội dung trao đổi (*):");
        gbc.gridy = row; gbc.gridx = 1; gbc.anchor = GridBagConstraints.NORTHWEST;
        txtNoiDung = new JTextArea(3, 24);
        txtNoiDung.setLineWrap(true); 
        txtNoiDung.setWrapStyleWord(true);
        txtNoiDung.setFont(UITheme.FONT_BODY);
        if (canhBao != null) {
            txtNoiDung.setText("Gặp mặt trao đổi và phân tích nguyên nhân bị " + UITheme.formatMucCanhBao(canhBao.getMucCanhBao()) 
                + " (GPA xét duyệt: " + String.format("%.2f", canhBao.getGpaXetDuyet()) + "). Hướng dẫn kế hoạch học kỳ tới.");
        } else {
            txtNoiDung.setText("Tư vấn định hướng học tập, hỗ trợ đăng ký học phần và tháo gỡ khó khăn.");
        }
        form.add(new JScrollPane(txtNoiDung), gbc); row++;

        addLabel(form, gbc, row, "Nguyên nhân chính:");
        gbc.gridy = row; gbc.gridx = 1;
        txtNguyenNhan = new JTextArea(2, 24);
        txtNguyenNhan.setLineWrap(true); 
        txtNguyenNhan.setWrapStyleWord(true);
        txtNguyenNhan.setFont(UITheme.FONT_BODY);
        txtNguyenNhan.setText("Chưa phân bổ thời gian hợp lý, đi làm thêm nhiều, gặp khó khăn với các môn đại cương/chuyên ngành.");
        form.add(new JScrollPane(txtNguyenNhan), gbc); row++;

        addLabel(form, gbc, row, "Giải pháp & Lộ trình:");
        gbc.gridy = row; gbc.gridx = 1;
        txtGiaiPhap = new JTextArea(2, 24);
        txtGiaiPhap.setLineWrap(true); 
        txtGiaiPhap.setWrapStyleWord(true);
        txtGiaiPhap.setFont(UITheme.FONT_BODY);
        txtGiaiPhap.setText("Đăng ký học lại/học cải thiện vào kỳ phụ, giảm giờ làm thêm, tham gia nhóm học tập của lớp.");
        form.add(new JScrollPane(txtGiaiPhap), gbc); row++;

        addLabel(form, gbc, row, "Cam kết của Sinh viên:");
        gbc.gridy = row; gbc.gridx = 1;
        txtCamKet = new JTextArea(2, 24);
        txtCamKet.setLineWrap(true); 
        txtCamKet.setWrapStyleWord(true);
        txtCamKet.setFont(UITheme.FONT_BODY);
        txtCamKet.setText("Cam kết đi học chuyên cần đầy đủ, nộp bài tập đúng hạn và đạt GPA >= 2.5 trong học kỳ tiếp theo.");
        form.add(new JScrollPane(txtCamKet), gbc); row++;

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollForm, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 12));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT));

        JButton btnSave = UITheme.createButton("Lưu Biên Bản Tư Vấn", UITheme.PRIMARY, Color.WHITE);
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

        String ngayStr = txtNgayTuVan.getText().trim();
        String hinhThuc = (String) cbHinhThuc.getSelectedItem();
        String noiDung = txtNoiDung.getText().trim();
        String nguyenNhan = txtNguyenNhan.getText().trim();
        String giaiPhap = txtGiaiPhap.getText().trim();
        String camKet = txtCamKet.getText().trim();

        if (noiDung.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập nội dung cuộc trao đổi tư vấn!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date ngayTv;
        try {
            ngayTv = Date.valueOf(ngayStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày tư vấn không đúng! Dùng chuẩn YYYY-MM-DD (VD: 2026-08-17)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String maCvht = "CV001";
        if (currentUser != null && currentUser.getMaRef() != null && !currentUser.getMaRef().isEmpty()) {
            maCvht = currentUser.getMaRef();
        }

        Integer idCanhBao = (canhBao != null) ? canhBao.getId() : null;

        NhatKyTuVan nk = new NhatKyTuVan(
            0, maSv, maCvht, idCanhBao, ngayTv, hinhThuc, noiDung, nguyenNhan, giaiPhap, camKet
        );

        boolean ok = new NhatKyTuVanDAO().addNhatKy(nk);
        if (ok) {
            if (idCanhBao != null) {
                new CanhBaoDAO().updateTrangThaiTuVan(idCanhBao, "DA_TU_VAN");
            }
            JOptionPane.showMessageDialog(this, "Đã lưu biên bản tư vấn thành công và cập nhật trạng thái học vụ!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            savedSuccess = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu biên bản thất bại! Vui lòng kiểm tra kết nối CSDL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSavedSuccess() { return savedSuccess; }
}