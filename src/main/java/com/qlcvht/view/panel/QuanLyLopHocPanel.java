package com.qlcvht.view.panel;

import com.qlcvht.dao.CoVanDAO;
import com.qlcvht.model.CoVanHocTap;
import com.qlcvht.model.LopHoc;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.List;
import com.qlcvht.config.DatabaseConnection;

/**
 * Panel quan ly lop hoc (Admin / Quan ly).
 */
public class QuanLyLopHocPanel extends JPanel {

    private final CoVanDAO coVanDAO = new CoVanDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private List<LopHoc> currentList;

    private static final String[] COLUMNS = {"Ma Lop", "Ten Lop", "Khoa", "Khoa Hoc", "Ma CVHT", "Ten Co Van"};

    public QuanLyLopHocPanel() {
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
        JLabel title = new JLabel("Quan ly Lop hoc & Phan cong Co van");
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

        JButton btnThem = createBtn("+ Them lop", UITheme.SUCCESS, Color.WHITE);
        btnThem.addActionListener(e -> onThem());
        bar.add(btnThem);

        JButton btnSua = createBtn("Sua lop", UITheme.WARNING, Color.WHITE);
        btnSua.addActionListener(e -> onSua());
        bar.add(btnSua);

        JButton btnXoa = createBtn("Xoa lop", UITheme.DANGER, Color.WHITE);
        btnXoa.addActionListener(e -> onXoa());
        bar.add(btnXoa);

        JButton btnRefresh = createBtn("Lam moi", UITheme.BORDER_MEDIUM, UITheme.TEXT_PRIMARY);
        btnRefresh.addActionListener(e -> loadData());
        bar.add(btnRefresh);

        add(bar, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        int[] widths = {80, 250, 150, 80, 90, 180};
        for (int i = 0; i < widths.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        add(scroll, BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        currentList = coVanDAO.getAllLopHoc();
        for (LopHoc l : currentList) {
            tableModel.addRow(new Object[]{l.getMaLop(), l.getTenLop(), l.getKhoa(), l.getKhoaHoc(), l.getMaCvht(), l.getTenCvht()});
        }
    }

    private void onThem() {
        LopHoc lop = showLopDialog(null);
        if (lop != null) {
            if (saveLop(lop, true)) {
                JOptionPane.showMessageDialog(this, "Them lop thanh cong!", "Thong bao", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Them lop that bai! Ma lop co the da ton tai.", "Loi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onSua() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Vui long chon 1 lop de sua!"); return; }
        LopHoc existing = currentList.get(row);
        LopHoc edited = showLopDialog(existing);
        if (edited != null) {
            if (saveLop(edited, false)) {
                JOptionPane.showMessageDialog(this, "Cap nhat lop thanh cong!", "Thong bao", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Cap nhat lop that bai!", "Loi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onXoa() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Vui long chon 1 lop de xoa!"); return; }
        LopHoc lop = currentList.get(row);
        int choice = JOptionPane.showConfirmDialog(this,
            "Ban co chac muon xoa lop: " + lop.getMaLop() + " - " + lop.getTenLop() + "?\nLuu y: Se khong xoa duoc neu lop con sinh vien!",
            "Xac nhan xoa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM lop_hoc WHERE ma_lop=?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, lop.getMaLop());
                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Xoa lop thanh cong!", "Thong bao", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    warn("Xoa that bai!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Khong the xoa! Lop co the con sinh vien.", "Loi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private LopHoc showLopDialog(LopHoc existing) {
        List<CoVanHocTap> listCv = coVanDAO.getAllCoVan();
        Object[] cvChoices = listCv.toArray();

        JPanel panel = new JPanel(new GridLayout(5, 2, 8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Ma lop (*):"));
        JTextField tfMa = new JTextField(existing != null ? existing.getMaLop() : "");
        if (existing != null) { tfMa.setEditable(false); tfMa.setBackground(new Color(245,245,245)); }
        panel.add(tfMa);

        panel.add(new JLabel("Ten lop (*):"));
        JTextField tfTen = new JTextField(existing != null ? existing.getTenLop() : "");
        panel.add(tfTen);

        panel.add(new JLabel("Khoa:"));
        JTextField tfKhoa = new JTextField(existing != null ? existing.getKhoa() : "");
        panel.add(tfKhoa);

        panel.add(new JLabel("Khoa hoc (nam):"));
        JTextField tfKH = new JTextField(existing != null ? String.valueOf(existing.getKhoaHoc()) : "2024");
        panel.add(tfKH);

        panel.add(new JLabel("Co van hoc tap:"));
        JComboBox<Object> cbCv = new JComboBox<>(cvChoices);
        if (existing != null) {
            for (int i = 0; i < cbCv.getItemCount(); i++) {
                Object it = cbCv.getItemAt(i);
                if (it instanceof CoVanHocTap && ((CoVanHocTap) it).getMaCvht().equals(existing.getMaCvht())) {
                    cbCv.setSelectedIndex(i); break;
                }
            }
        }
        panel.add(cbCv);

        int result = JOptionPane.showConfirmDialog(this, panel,
            existing == null ? "Them lop moi" : "Sua thong tin lop",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return null;

        String maLop = tfMa.getText().trim();
        String tenLop = tfTen.getText().trim();
        if (maLop.isEmpty() || tenLop.isEmpty()) { warn("Ma lop va Ten lop khong duoc de trong!"); return null; }

        int khoaHoc = 2024;
        try { khoaHoc = Integer.parseInt(tfKH.getText().trim()); } catch (NumberFormatException ignored) {}

        CoVanHocTap cv = (CoVanHocTap) cbCv.getSelectedItem();
        LopHoc lop = new LopHoc(maLop, tenLop, tfKhoa.getText().trim(), khoaHoc, cv != null ? cv.getMaCvht() : null);
        return lop;
    }

    private boolean saveLop(LopHoc lop, boolean isNew) {
        String sql = isNew
            ? "INSERT INTO lop_hoc (ma_lop,ten_lop,khoa,khoa_hoc,ma_cvht) VALUES (?,?,?,?,?)"
            : "UPDATE lop_hoc SET ten_lop=?,khoa=?,khoa_hoc=?,ma_cvht=? WHERE ma_lop=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (isNew) {
                ps.setString(1, lop.getMaLop()); ps.setString(2, lop.getTenLop());
                ps.setString(3, lop.getKhoa()); ps.setInt(4, lop.getKhoaHoc());
                ps.setString(5, lop.getMaCvht());
            } else {
                ps.setString(1, lop.getTenLop()); ps.setString(2, lop.getKhoa());
                ps.setInt(3, lop.getKhoaHoc()); ps.setString(4, lop.getMaCvht());
                ps.setString(5, lop.getMaLop());
            }
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
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