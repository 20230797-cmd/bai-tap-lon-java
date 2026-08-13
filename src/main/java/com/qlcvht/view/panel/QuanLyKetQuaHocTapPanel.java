package com.qlcvht.view.panel;

import com.qlcvht.dao.KetQuaHocTapDAO;
import com.qlcvht.dao.SinhVienDAO;
import com.qlcvht.model.KetQuaHocTap;
import com.qlcvht.model.SinhVien;
import com.qlcvht.model.TaiKhoan;
import com.qlcvht.service.CanhBaoService;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel quan ly va nhap ket qua hoc tap (GPA).
 */
public class QuanLyKetQuaHocTapPanel extends JPanel {

    private final TaiKhoan currentUser;
    private final KetQuaHocTapDAO ketQuaDAO = new KetQuaHocTapDAO();
    private final SinhVienDAO svDAO = new SinhVienDAO();
    private final CanhBaoService canhBaoService = new CanhBaoService();

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearchSv;
    private JComboBox<String> cbNamHoc;
    private JComboBox<String> cbHocKy;

    private static final String[] COLUMNS = {"ID", "Ma SV", "Ho va Ten SV", "Hoc ky", "Nam hoc", "GPA Hoc ky", "GPA Tich luy", "Tin chi No"};

    public QuanLyKetQuaHocTapPanel(TaiKhoan user) {
        this.currentUser = user;
        setLayout(new BorderLayout(0, 10));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(12, 14, 12, 14));
        initHeader();
        initToolbar();
        initTable();
        loadData();
    }

    private void initHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        JLabel title = new JLabel("Quan ly Ket qua Hoc tap (GPA)");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);
        h.add(title, BorderLayout.WEST);
        add(h, BorderLayout.NORTH);
    }

    private void initToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        bar.setBackground(UITheme.BG_WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT),
            new EmptyBorder(6, 10, 6, 10)
        ));

        bar.add(new JLabel("Tim SV:"));
        txtSearchSv = new JTextField(12);
        bar.add(txtSearchSv);

        bar.add(new JLabel("Nam hoc:"));
        cbNamHoc = new JComboBox<>(new String[]{"Tat ca", "2023-2024", "2024-2025", "2025-2026"});
        bar.add(cbNamHoc);

        bar.add(new JLabel("Hoc ky:"));
        cbHocKy = new JComboBox<>(new String[]{"Tat ca", "1", "2", "3"});
        bar.add(cbHocKy);

        JButton btnSearch = createBtn("Tim kiem", UITheme.PRIMARY, Color.WHITE);
        btnSearch.addActionListener(e -> loadData());
        bar.add(btnSearch);

        JButton btnReset = createBtn("Lam moi", UITheme.BORDER_MEDIUM, UITheme.TEXT_PRIMARY);
        btnReset.addActionListener(e -> { txtSearchSv.setText(""); cbNamHoc.setSelectedIndex(0); cbHocKy.setSelectedIndex(0); loadData(); });
        bar.add(btnReset);

        bar.add(new JSeparator(SwingConstants.VERTICAL));

        // Nhap GPA moi
        JButton btnNhap = createBtn("+ Nhap GPA moi", UITheme.SUCCESS, Color.WHITE);
        btnNhap.addActionListener(e -> onNhapGPA());
        bar.add(btnNhap);

        // Sua GPA
        JButton btnSua = createBtn("Sua GPA", UITheme.WARNING, Color.WHITE);
        btnSua.addActionListener(e -> onSuaGPA());
        bar.add(btnSua);

        // Xoa GPA
        if ("ADMIN".equals(currentUser != null ? currentUser.getVaiTro() : "")) {
            JButton btnXoa = createBtn("Xoa", UITheme.DANGER, Color.WHITE);
            btnXoa.addActionListener(e -> onXoaGPA());
            bar.add(btnXoa);
        }

        // Quet canh bao
        JButton btnQuet = createBtn("Quet Canh bao", new Color(140, 20, 20), Color.WHITE);
        btnQuet.setToolTipText("Tu dong phan tich GPA va tao canh bao hoc vu");
        btnQuet.addActionListener(e -> onQuetCanhBao());
        bar.add(btnQuet);

        add(bar, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        // GPA color renderer
        DefaultTableCellRenderer gpaRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) {
                    comp.setBackground(r % 2 == 0 ? Color.WHITE : UITheme.BG_TABLE_STRIPE);
                    if (v != null) {
                        try {
                            double gpa = Double.parseDouble(v.toString());
                            if (gpa < 1.0)       comp.setForeground(UITheme.DANGER_DARK);
                            else if (gpa < 1.5)  comp.setForeground(UITheme.DANGER);
                            else if (gpa < 2.0)  comp.setForeground(UITheme.WARNING);
                            else                 comp.setForeground(UITheme.SUCCESS);
                        } catch (NumberFormatException ignored) {}
                    }
                }
                return comp;
            }
        };
        table.getColumnModel().getColumn(5).setCellRenderer(gpaRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(gpaRenderer);

        int[] widths = {50, 90, 160, 70, 100, 100, 110, 90};
        for (int i = 0; i < widths.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        add(scroll, BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<KetQuaHocTap> list = ketQuaDAO.getAllKetQua();
        String kw = txtSearchSv.getText().trim().toLowerCase();
        String namHoc = (String) cbNamHoc.getSelectedItem();
        String hocKyStr = (String) cbHocKy.getSelectedItem();

        for (KetQuaHocTap kq : list) {
            // Filter
            if (!kw.isEmpty()) {
                String sv = (kq.getHoTenSv() != null ? kq.getHoTenSv() : "").toLowerCase();
                String ma = kq.getMaSv().toLowerCase();
                if (!sv.contains(kw) && !ma.contains(kw)) continue;
            }
            if (!"Tat ca".equals(namHoc) && !namHoc.equals(kq.getNamHoc())) continue;
            if (!"Tat ca".equals(hocKyStr) && !hocKyStr.equals(String.valueOf(kq.getHocKy()))) continue;

            tableModel.addRow(new Object[]{
                kq.getId(), kq.getMaSv(),
                kq.getHoTenSv() != null ? kq.getHoTenSv() : "",
                "Hoc ky " + kq.getHocKy(), kq.getNamHoc(),
                String.format("%.2f", kq.getGpaHocKy()),
                String.format("%.2f", kq.getGpaTichLuy()),
                kq.getSoTinChiNo()
            });
        }
    }

    private void onNhapGPA() {
        // Chon sinh vien
        List<SinhVien> dsSv = svDAO.getAllSinhVien();
        Object[] choices = dsSv.toArray();
        if (choices.length == 0) { JOptionPane.showMessageDialog(this, "Chua co sinh vien nao!", "Thong bao", JOptionPane.WARNING_MESSAGE); return; }
        SinhVien selected = (SinhVien) JOptionPane.showInputDialog(this,
            "Chon sinh vien de nhap GPA:", "Nhap GPA moi",
            JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);
        if (selected == null) return;
        showGpaInputDialog(selected, null);
    }

    private void onSuaGPA() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Vui long chon 1 ban ghi de sua!"); return; }
        int id   = (int) tableModel.getValueAt(row, 0);
        String maSv = (String) tableModel.getValueAt(row, 1);
        SinhVien sv = svDAO.getSinhVienById(maSv);
        // Find existing KQ
        List<KetQuaHocTap> list = ketQuaDAO.getKetQuaBySinhVien(maSv);
        KetQuaHocTap existing = list.stream().filter(k -> k.getId() == id).findFirst().orElse(null);
        if (sv != null) showGpaInputDialog(sv, existing);
    }

    private void onXoaGPA() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Vui long chon 1 ban ghi de xoa!"); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        int choice = JOptionPane.showConfirmDialog(this, "Ban co chac muon xoa ban ghi GPA nay?", "Xac nhan", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            if (ketQuaDAO.delete(id)) { loadData(); JOptionPane.showMessageDialog(this, "Xoa thanh cong!"); }
            else JOptionPane.showMessageDialog(this, "Xoa that bai!", "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onQuetCanhBao() {
        String namHocIn = JOptionPane.showInputDialog(this, "Nhap nam hoc can quet (VD: 2023-2024):", "2023-2024");
        if (namHocIn == null || namHocIn.trim().isEmpty()) return;
        String hocKyIn = JOptionPane.showInputDialog(this, "Nhap hoc ky (1, 2 hoac 3):", "2");
        if (hocKyIn == null) return;
        int hocKy;
        try { hocKy = Integer.parseInt(hocKyIn.trim()); } catch (NumberFormatException e) { warn("Hoc ky phai la so!"); return; }

        int count = canhBaoService.quetCanhBaoHocVu(hocKy, namHocIn.trim());
        JOptionPane.showMessageDialog(this,
            "Hoan thanh quet du lieu!\nPhat hien va tao: " + count + " ban ghi canh bao moi.",
            "Ket qua quet Canh bao", JOptionPane.INFORMATION_MESSAGE);
        loadData();
    }

    private void showGpaInputDialog(SinhVien sv, KetQuaHocTap existing) {
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 8, 8));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Sinh vien:")); inputPanel.add(new JLabel(sv.getHoTen() + " (" + sv.getMaSv() + ")"));
        inputPanel.add(new JLabel("Hoc ky:"));
        JComboBox<Integer> cbHK = new JComboBox<>(new Integer[]{1, 2, 3});
        if (existing != null) cbHK.setSelectedItem(existing.getHocKy());
        inputPanel.add(cbHK);
        inputPanel.add(new JLabel("Nam hoc:"));
        JTextField tfNH = new JTextField(existing != null ? existing.getNamHoc() : "2023-2024");
        inputPanel.add(tfNH);
        inputPanel.add(new JLabel("GPA Hoc ky:"));
        JTextField tfGpaHK = new JTextField(existing != null ? String.format("%.2f", existing.getGpaHocKy()) : "");
        inputPanel.add(tfGpaHK);
        inputPanel.add(new JLabel("GPA Tich luy:"));
        JTextField tfGpaTL = new JTextField(existing != null ? String.format("%.2f", existing.getGpaTichLuy()) : "");
        inputPanel.add(tfGpaTL);
        // Note: so tin chi no se tinh rieng
        // For simplicity: removed so_tin_chi_no input, default 0

        int result = JOptionPane.showConfirmDialog(this, inputPanel,
            existing == null ? "Nhap GPA moi" : "Sua GPA",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            double gpaHK = Double.parseDouble(tfGpaHK.getText().trim().replace(",", "."));
            double gpaTL = Double.parseDouble(tfGpaTL.getText().trim().replace(",", "."));
            if (gpaHK < 0 || gpaHK > 4 || gpaTL < 0 || gpaTL > 4) {
                warn("GPA phai trong khoang 0.00 - 4.00!"); return;
            }
            KetQuaHocTap kq = new KetQuaHocTap(
                existing != null ? existing.getId() : 0,
                sv.getMaSv(), (Integer) cbHK.getSelectedItem(),
                tfNH.getText().trim(), gpaHK, gpaTL, 0
            );
            if (ketQuaDAO.saveOrUpdate(kq)) {
                loadData();
                JOptionPane.showMessageDialog(this, "Luu GPA thanh cong!", "Thong bao", JOptionPane.INFORMATION_MESSAGE);
            } else {
                warn("Luu GPA that bai!");
            }
        } catch (NumberFormatException e) {
            warn("Gia tri GPA khong hop le! Vui long nhap so (VD: 3.50).");
        }
    }

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

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thong bao", JOptionPane.WARNING_MESSAGE);
    }
}