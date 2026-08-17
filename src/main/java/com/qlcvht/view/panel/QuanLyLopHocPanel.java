package com.qlcvht.view.panel;

import com.qlcvht.dao.CoVanDAO;
import com.qlcvht.model.LopHoc;
import com.qlcvht.util.ExcelExporter;
import com.qlcvht.util.UITheme;
import com.qlcvht.view.dialog.ThemSuaLopDialog;

import com.qlcvht.util.WrapLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class QuanLyLopHocPanel extends JPanel {

    private final CoVanDAO coVanDAO = new CoVanDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;
    private List<LopHoc> currentList = new ArrayList<>();

    private static final String[] COLUMNS = {
        "STT", "Mã Lớp", "Tên Lớp Quản Lý", "Khoa / Bộ Môn", "Khóa Tuyển Sinh", "Sĩ Số Sinh Viên", "Mã CVHT", "Cố Vấn Học Tập Phụ Trách"
    };

    public QuanLyLopHocPanel() {
        setLayout(new BorderLayout(0, 10));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(12, 16, 12, 16));
        initTopPanel();
        initTable();
        loadData();
    }

    private void initTopPanel() {
        JPanel topContainer = new JPanel(new BorderLayout(0, 8));
        topContainer.setOpaque(false);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("🏫  QUẢN LÝ LỚP HỌC & PHÂN CÔNG CỐ VẤN HỌC TẬP");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.TEXT_PRIMARY);

        lblTotal = new JLabel("Tổng số: 0 lớp");
        lblTotal.setFont(UITheme.fontBold(13));
        lblTotal.setForeground(UITheme.PRIMARY);
        lblTotal.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.PRIMARY, 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));

        header.add(title, BorderLayout.WEST);
        header.add(lblTotal, BorderLayout.EAST);
        topContainer.add(header, BorderLayout.NORTH);

        // Toolbar with WrapLayout
        JPanel bar = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 6));
        bar.setBackground(UITheme.BG_WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));

        JButton btnThem = UITheme.createButton("➕ Thêm Lớp Mới", UITheme.SUCCESS, Color.WHITE);
        btnThem.addActionListener(e -> onThem());
        bar.add(btnThem);

        JButton btnSua = UITheme.createButton("✏️ Sửa Lớp & Phân Công", UITheme.WARNING, Color.WHITE);
        btnSua.addActionListener(e -> onSua());
        bar.add(btnSua);

        JButton btnXoa = UITheme.createButton("🗑️ Xóa Lớp", UITheme.DANGER, Color.WHITE);
        btnXoa.addActionListener(e -> onXoa());
        bar.add(btnXoa);

        JButton btnRefresh = UITheme.createButton("🔄 Làm Mới", new Color(220, 225, 235), UITheme.TEXT_PRIMARY);
        btnRefresh.addActionListener(e -> loadData());
        bar.add(btnRefresh);

        bar.add(new JSeparator(SwingConstants.VERTICAL));

        JButton btnExport = UITheme.createButton("📊 Xuất Excel", new Color(46, 125, 50), Color.WHITE);
        btnExport.addActionListener(e -> ExcelExporter.exportJTableToExcel(table, "Danh_Sach_Lop_Hoc"));
        bar.add(btnExport);

        topContainer.add(bar, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        int[] widths = {45, 95, 220, 180, 110, 110, 90, 190};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        DefaultTableCellRenderer center = UITheme.createCenterRenderer();
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(center);
        table.getColumnModel().getColumn(6).setCellRenderer(center);

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) onSua();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        currentList = coVanDAO.getAllLopHoc();
        int stt = 1;
        for (LopHoc l : currentList) {
            int siSo = coVanDAO.getSoLuongSinhVienTrongLop(l.getMaLop());
            tableModel.addRow(new Object[]{
                stt++,
                l.getMaLop(),
                l.getTenLop(),
                l.getKhoa(),
                "Khóa " + l.getKhoaHoc(),
                siSo + " SV",
                l.getMaCvht() != null ? l.getMaCvht() : "---",
                l.getTenCvht() != null ? l.getTenCvht() : "--- Chưa phân công ---"
            });
        }
        lblTotal.setText("Tổng số: " + currentList.size() + " lớp");
    }

    private LopHoc getSelectedLop() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= currentList.size()) return null;
        return currentList.get(row);
    }

    private void onThem() {
        ThemSuaLopDialog dlg = new ThemSuaLopDialog((Frame) SwingUtilities.getWindowAncestor(this), null);
        dlg.setVisible(true);
        if (dlg.isSaved()) loadData();
    }

    private void onSua() {
        LopHoc lop = getSelectedLop();
        if (lop == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp cần chỉnh sửa thông tin!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ThemSuaLopDialog dlg = new ThemSuaLopDialog((Frame) SwingUtilities.getWindowAncestor(this), lop);
        dlg.setVisible(true);
        if (dlg.isSaved()) loadData();
    }

    private void onXoa() {
        LopHoc lop = getSelectedLop();
        if (lop == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa lớp " + lop.getTenLop() + " (" + lop.getMaLop() + ")?\nLưu ý: Thao tác này có thể ảnh hưởng đến các sinh viên thuộc lớp!",
            "Xác nhận xóa lớp", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = coVanDAO.deleteLopHoc(lop.getMaLop());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã xóa lớp học thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa lớp thất bại! Có thể lớp đang chứa sinh viên.", "Lỗi ràng buộc", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}