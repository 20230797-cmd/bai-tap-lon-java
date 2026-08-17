package com.qlcvht.view.dialog;

import com.qlcvht.util.ExcelSmartImporter;
import com.qlcvht.util.ExcelSmartImporter.ImportType;
import com.qlcvht.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * Dialog cho phép người dùng chọn file Excel và thực hiện import dữ liệu.
 */
public class ExcelImportDialog extends JDialog {

    private final ImportType importType;
    private boolean importedSuccessfully = false;

    private JLabel lblStatus;
    private JTextField txtFilePath;
    private JButton btnBrowse;
    private JButton btnImport;
    private JTextArea txtLog;

    public ExcelImportDialog(Frame owner, ImportType importType) {
        super(owner, "Import Excel — " + importType.getDisplayName(), true);
        this.importType = importType;
        buildUI();
        pack();
        setMinimumSize(new Dimension(520, 380));
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));
        getRootPane().setBorder(new EmptyBorder(16, 16, 16, 16));
        getContentPane().setBackground(UITheme.BG_MAIN);

        JLabel lblTitle = new JLabel("Import " + importType.getDisplayName() + " từ file Excel (.xlsx)");
        lblTitle.setFont(UITheme.FONT_SUBHEADER);
        lblTitle.setForeground(UITheme.TEXT_PRIMARY);
        lblTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setBackground(UITheme.BG_MAIN);

        JPanel fileRow = new JPanel(new BorderLayout(8, 0));
        fileRow.setBackground(UITheme.BG_MAIN);

        txtFilePath = new JTextField();
        txtFilePath.setEditable(false);
        txtFilePath.setFont(UITheme.FONT_BODY);
        txtFilePath.setBackground(UITheme.BG_WHITE);
        txtFilePath.setForeground(UITheme.TEXT_SECONDARY);
        txtFilePath.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT, 1),
                new EmptyBorder(6, 8, 6, 8)));
        txtFilePath.setText("Chưa chọn file...");

        btnBrowse = new JButton("📂 Chọn file");
        btnBrowse.setFont(UITheme.FONT_BTN);
        btnBrowse.setBackground(UITheme.PRIMARY);
        btnBrowse.setForeground(Color.WHITE);
        btnBrowse.setFocusPainted(false);
        btnBrowse.setBorderPainted(false);
        btnBrowse.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBrowse.addActionListener(e -> browseFile());

        fileRow.add(txtFilePath, BorderLayout.CENTER);
        fileRow.add(btnBrowse, BorderLayout.EAST);

        JLabel lblHint = new JLabel("<html><span style='color:#888;font-size:11px;'>" +
                "📌 Cột theo thứ tự: <b>Mã SV | Họ tên | Ngày sinh (yyyy-MM-dd) | Giới tính | Email | SĐT | Mã lớp | Trạng thái</b>" +
                "</span></html>");
        lblHint.setBorder(new EmptyBorder(4, 0, 4, 0));

        txtLog = new JTextArea(8, 40);
        txtLog.setEditable(false);
        txtLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtLog.setBackground(new Color(30, 30, 30));
        txtLog.setForeground(new Color(200, 255, 200));
        txtLog.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane scroll = new JScrollPane(txtLog);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_MEDIUM));

        lblStatus = new JLabel(" ");
        lblStatus.setFont(UITheme.FONT_BODY);
        lblStatus.setForeground(UITheme.TEXT_SECONDARY);

        center.add(fileRow, BorderLayout.NORTH);
        center.add(lblHint, BorderLayout.CENTER);
        center.add(scroll, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottom.setBackground(UITheme.BG_MAIN);
        bottom.setBorder(new EmptyBorder(12, 0, 0, 0));

        btnImport = new JButton("⬆ Bắt đầu Import");
        btnImport.setFont(UITheme.FONT_BTN);
        btnImport.setBackground(UITheme.SUCCESS);
        btnImport.setForeground(Color.WHITE);
        btnImport.setFocusPainted(false);
        btnImport.setBorderPainted(false);
        btnImport.setEnabled(false);
        btnImport.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnImport.addActionListener(e -> doImport());

        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(UITheme.FONT_BTN);
        btnClose.setBackground(UITheme.BG_WHITE);
        btnClose.setForeground(UITheme.TEXT_PRIMARY);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dispose());

        bottom.add(lblStatus);
        bottom.add(btnImport);
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);
    }

    private void browseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn file Excel để import");
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            txtFilePath.setText(file.getAbsolutePath());
            txtFilePath.setForeground(UITheme.TEXT_PRIMARY);
            btnImport.setEnabled(true);
            txtLog.setText("");
            lblStatus.setText("📄 File: " + file.getName());
        }
    }

    private void doImport() {
        String path = txtFilePath.getText();
        if (path == null || path.isBlank() || path.equals("Chưa chọn file...")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn file Excel trước!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        btnImport.setEnabled(false);
        btnBrowse.setEnabled(false);
        lblStatus.setText("⏳ Đang import...");
        txtLog.setText("Đang xử lý file...\n");

        SwingWorker<ExcelSmartImporter.ImportResult, String> worker = new SwingWorker<>() {
            @Override
            protected ExcelSmartImporter.ImportResult doInBackground() {
                return ExcelSmartImporter.doImport(new File(path), importType);
            }
            @Override
            protected void done() {
                try {
                    ExcelSmartImporter.ImportResult result = get();
                    txtLog.setText("");
                    txtLog.append("=== KẾT QUẢ IMPORT ===\n");
                    txtLog.append(result.getSummary() + "\n\n");
                    if (!result.errorMessages.isEmpty()) {
                        txtLog.append("=== CHI TIẾT LỖI ===\n");
                        for (String msg : result.errorMessages) txtLog.append("  ⚠ " + msg + "\n");
                    }
                    if (result.successCount > 0) {
                        importedSuccessfully = true;
                        lblStatus.setText("✅ Import xong: " + result.successCount + " bản ghi.");
                    } else {
                        lblStatus.setText("❌ Không import được bản ghi nào.");
                    }
                } catch (Exception ex) {
                    lblStatus.setText("❌ Lỗi: " + ex.getMessage());
                    txtLog.append("LỖI: " + ex.getMessage() + "\n");
                } finally {
                    btnImport.setEnabled(true);
                    btnBrowse.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    public boolean isImportedSuccessfully() {
        return importedSuccessfully;
    }
}
