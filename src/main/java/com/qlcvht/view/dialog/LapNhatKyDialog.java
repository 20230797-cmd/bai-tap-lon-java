package com.qlcvht.view.dialog;

import com.qlcvht.dao.NhatKyTuVanDAO;
import com.qlcvht.model.CanhBaoHocVu;
import com.qlcvht.model.NhatKyTuVan;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class LapNhatKyDialog extends JDialog {

    private final CanhBaoHocVu canhBao;
    private final TaiKhoan currentUser;
    private boolean savedSuccess = false;

    private JTextField txtMaSv, txtHoTenSv, txtNgayTuVan;
    private JComboBox<String> cbHinhThuc;
    private JTextArea txtNoiDung, txtNguyenNhan, txtGiaiPhap, txtCamKet;

    public LapNhatKyDialog(Frame parent, CanhBaoHocVu canhBao, TaiKhoan currentUser) {
        super(parent, "Lap Nhat ky Tu van Sinh vien Bi canh bao", true);
        this.canhBao = canhBao;
        this.currentUser = currentUser;
        initUI();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(560, 620));
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UITheme.PRIMARY);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel lbl = new JLabel("BIEN BAN TU VAN CANH BAO HOC VU");
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

        addLabel(form, gbc, row, "Ma Sinh vien:");
        txtMaSv = new JTextField(canhBao != null ? canhBao.getMaSv() : "");
        txtMaSv.setEditable(false); txtMaSv.setBackground(new Color(245, 245, 245));
        addField(form, gbc, row, txtMaSv); row++;

        addLabel(form, gbc, row, "Ho va Ten SV:");
        txtHoTenSv = new JTextField(canhBao != null ? (canhBao.getHoTenSv() != null ? canhBao.getHoTenSv() : "") : "");
        txtHoTenSv.setEditable(false); txtHoTenSv.setBackground(new Color(245, 245, 245));
        addField(form, gbc, row, txtHoTenSv); row++;

        addLabel(form, gbc, row, "Ngay tu van (YYYY-MM-DD):");
        txtNgayTuVan = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        addField(form, gbc, row, txtNgayTuVan); row++;

        addLabel(form, gbc, row, "Hinh thuc gap mat:");
        cbHinhThuc = new JComboBox<>(new String[]{"Truc tiep", "Online (Teams/Zoom)", "Qua dien thoai"});
        addField(form, gbc, row, cbHinhThuc); row++;

        addLabel(form, gbc, row, "Noi dung trao doi:");
        gbc.gridy = row; gbc.gridx = 1; gbc.anchor = GridBagConstraints.NORTHWEST;
        txtNoiDung = new JTextArea(3, 22);
        txtNoiDung.setLineWrap(true); txtNoiDung.setWrapStyleWord(true);
        txtNoiDung.setFont(UITheme.FONT_BODY);
        if (canhBao != null) txtNoiDung.setText("Trao doi nguyen nhan bi canh bao hoc vu " + canhBao.getMucCanhBaoHienThi() + " (GPA: " + String.format("%.2f", canhBao.getGpaXetDuyet()) + ").");
        form.add(new JScrollPane(txtNoiDung), gbc); row++;

        addLabel(form, gbc, row, "Nguyen nhan chinh:");
        gbc.gridy = row; gbc.gridx = 1;
        txtNguyenNhan = new JTextArea(2, 22);
        txtNguyenNhan.setLineWrap(true); txtNguyenNhan.setWrapStyleWord(true);
        txtNguyenNhan.setFont(UITheme.FONT_BODY);
        form.add(new JScrollPane(txtNguyenNhan), gbc); row++;

        addLabel(form, gbc, row, "Giai phap khac phuc:");
        gbc.gridy = row; gbc.gridx = 1;
        txtGiaiPhap = new JTextArea(2, 22);
        txtGiaiPhap.setLineWrap(true); txtGiaiPhap.setWrapStyleWord(true);
        txtGiaiPhap.setFont(UITheme.FONT_BODY);
        form.add(new JScrollPane(txtGiaiPhap), gbc); row++;

        addLabel(form, gbc, row, "Cam ket cua Sinh vien:");
        gbc.gridy = row; gbc.gridx = 1;
        txtCamKet = new JTextArea(2, 22);
        txtCamKet.setLineWrap(true); txtCamKet.setWrapStyleWord(true);
        txtCamKet.setFont(UITheme.FONT_BODY);
        form.add(new JScrollPane(txtCamKet), gbc);

        add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        btns.setBackground(Color.WHITE);
        btns.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT));
        JButton btnSave = createBtn("Luu Nhat ky", UITheme.PRIMARY, Color.WHITE);
        btnSave.addActionListener(e -> onSave());
        JButton btnCancel = createBtn("Huy", UITheme.BORDER_MEDIUM, UITheme.TEXT_PRIMARY);
        btnCancel.addActionListener(e -> dispose());
        btns.add(btnSave);
        btns.add(btnCancel);
        add(btns, BorderLayout.SOUTH);
    }

    private void addLabel(JPanel p, GridBagConstraints gbc, int row, String text) {
        gbc.gridy = row; gbc.gridx = 0; gbc.weightx = 0.32; gbc.anchor = GridBagConstraints.WEST;
        JLabel lbl = new JLabel(text);
        lbl.setFont(UITheme.FONT_BODY);
        p.add(lbl, gbc);
    }

    private void addField(JPanel p, GridBagConstraints gbc, int row, JComponent field) {
        gbc.gridy = row; gbc.gridx = 1; gbc.weightx = 0.68;
        field.setFont(UITheme.FONT_BODY);
        p.add(field, gbc);
    }

    private void onSave() {
        String ngayStr = txtNgayTuVan.getText().trim();
        Date ngayTuVan;
        try { ngayTuVan = Date.valueOf(ngayStr); }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dinh dang ngay khong hop le! Dung YYYY-MM-DD", "Loi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (txtNoiDung.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui long nhap noi dung trao doi!", "Canh bao", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maCvht = (currentUser != null && currentUser.getMaRef() != null) ? currentUser.getMaRef() : "CV001";
        NhatKyTuVan nk = new NhatKyTuVan(
            0, txtMaSv.getText().trim(), maCvht,
            canhBao != null ? canhBao.getId() : null,
            ngayTuVan, (String) cbHinhThuc.getSelectedItem(),
            txtNoiDung.getText().trim(), txtNguyenNhan.getText().trim(),
            txtGiaiPhap.getText().trim(), txtCamKet.getText().trim()
        );
        boolean ok = new NhatKyTuVanDAO().addNhatKy(nk);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Luu nhat ky tu van thanh cong!", "Thong bao", JOptionPane.INFORMATION_MESSAGE);
            savedSuccess = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Luu that bai! Vui long thu lai.", "Loi CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSavedSuccess() { return savedSuccess; }

    private JButton createBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BTN);
        btn.setBackground(bg); btn.setForeground(fg);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}