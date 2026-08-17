package com.qlcvht.util;

import com.qlcvht.model.SinhVien;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ExcelExporter {

    public static boolean exportJTableToExcel(JTable table, String sheetTitle) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        fileChooser.setSelectedFile(new File(sheetTitle.replaceAll("[^a-zA-Z0-9_]", "_") + ".xlsx"));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getAbsolutePath().endsWith(".xlsx")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetTitle);
            TableModel model = table.getModel();

            // Font & Style cho Header
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Ghi tiêu đề cột (Header Row)
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < model.getColumnCount(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(model.getColumnName(col));
                cell.setCellStyle(headerStyle);
            }

            // Ghi dữ liệu từng dòng
            for (int row = 0; row < model.getRowCount(); row++) {
                Row dataRow = sheet.createRow(row + 1);
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Cell cell = dataRow.createCell(col);
                    Object val = model.getValueAt(row, col);
                    if (val != null) {
                        if (val instanceof Number) {
                            cell.setCellValue(((Number) val).doubleValue());
                        } else {
                            cell.setCellValue(val.toString());
                        }
                    } else {
                        cell.setCellValue("");
                    }
                }
            }

            // Tự động căn chỉnh độ rộng cột
            for (int col = 0; col < model.getColumnCount(); col++) {
                sheet.autoSizeColumn(col);
            }

            try (FileOutputStream out = new FileOutputStream(fileToSave)) {
                workbook.write(out);
            }

            JOptionPane.showMessageDialog(null, 
                "Xuất file Excel thành công:\n" + fileToSave.getAbsolutePath(), 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Lỗi khi xuất file Excel: " + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    public static List<SinhVien> importSinhVienFromExcel(File file) throws Exception {
        List<SinhVien> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            // Bỏ qua dòng tiêu đề (row 0)
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                String maSv = formatter.formatCellValue(row.getCell(0)).trim();
                String hoTen = formatter.formatCellValue(row.getCell(1)).trim();
                String ngaySinhStr = formatter.formatCellValue(row.getCell(2)).trim();
                String gioiTinh = formatter.formatCellValue(row.getCell(3)).trim();
                String email = formatter.formatCellValue(row.getCell(4)).trim();
                String sdt = formatter.formatCellValue(row.getCell(5)).trim();
                String maLop = formatter.formatCellValue(row.getCell(6)).trim();
                String trangThai = formatter.formatCellValue(row.getCell(7)).trim();

                if (maSv.isEmpty() || hoTen.isEmpty()) continue;

                Date ngaySinh = null;
                try {
                    if (!ngaySinhStr.isEmpty()) {
                        ngaySinh = new Date(sdf.parse(ngaySinhStr).getTime());
                    }
                } catch (Exception ignored) {
                    try {
                        ngaySinh = Date.valueOf(ngaySinhStr);
                    } catch (Exception ignored2) {}
                }

                if (trangThai.isEmpty()) trangThai = "DANG_HOC";
                if (gioiTinh.isEmpty()) gioiTinh = "Nam";

                SinhVien sv = new SinhVien(maSv, hoTen, ngaySinh, gioiTinh, email, sdt, maLop, trangThai);
                list.add(sv);
            }
        }
        return list;
    }
}

