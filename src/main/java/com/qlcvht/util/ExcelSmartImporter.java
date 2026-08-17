package com.qlcvht.util;

import com.qlcvht.config.DatabaseConnection;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

/**
 * ExcelSmartImporter - Import dữ liệu từ file Excel (.xlsx) vào database.
 */
public class ExcelSmartImporter {

    public enum ImportType {
        SINH_VIEN("Sinh viên"),
        KET_QUA_HOC_TAP("Kết quả học tập"),
        LOP_HOC("Lớp học");

        private final String displayName;

        ImportType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public static class ImportResult {
        public int successCount = 0;
        public int skipCount = 0;
        public int errorCount = 0;
        public List<String> errorMessages = new ArrayList<>();

        public boolean isSuccess() {
            return errorCount == 0 && successCount > 0;
        }

        public String getSummary() {
            return String.format("✅ Thành công: %d | ⏭ Bỏ qua: %d | ❌ Lỗi: %d",
                    successCount, skipCount, errorCount);
        }
    }

    public static ImportResult importSinhVien(File file) {
        ImportResult result = new ImportResult();
        String sql = "INSERT INTO sinh_vien (ma_sv, ho_ten, ngay_sinh, gioi_tinh, email, so_dien_thoai, ma_lop, trang_thai) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE ho_ten=VALUES(ho_ten), email=VALUES(email), so_dien_thoai=VALUES(so_dien_thoai)";

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis);
             Connection conn = DatabaseConnection.getConnection()) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) { result.skipCount++; continue; }
                try {
                    String maSv      = getCellString(row, 0);
                    String hoTen     = getCellString(row, 1);
                    String ngaySinhStr = getCellString(row, 2);
                    String gioiTinh  = getCellString(row, 3);
                    String email     = getCellString(row, 4);
                    String sdt       = getCellString(row, 5);
                    String maLop     = getCellString(row, 6);
                    String trangThai = getCellString(row, 7);

                    if (maSv.isBlank() || hoTen.isBlank()) { result.skipCount++; continue; }

                    Date ngaySinh = null;
                    if (!ngaySinhStr.isBlank()) {
                        try { ngaySinh = Date.valueOf(ngaySinhStr.substring(0, 10)); } catch (Exception ignored) {}
                    }

                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, maSv);
                        ps.setString(2, hoTen);
                        ps.setDate(3, ngaySinh);
                        ps.setString(4, gioiTinh.isBlank() ? "Nam" : gioiTinh);
                        ps.setString(5, email);
                        ps.setString(6, sdt);
                        ps.setString(7, maLop);
                        ps.setString(8, trangThai.isBlank() ? "DANG_HOC" : trangThai);
                        ps.executeUpdate();
                        result.successCount++;
                    }
                } catch (Exception e) {
                    result.errorCount++;
                    result.errorMessages.add("Dòng " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            result.errorCount++;
            result.errorMessages.add("Không thể đọc file: " + e.getMessage());
        }
        return result;
    }

    public static ImportResult doImport(File file, ImportType type) {
        return switch (type) {
            case SINH_VIEN -> importSinhVien(file);
            default -> {
                ImportResult r = new ImportResult();
                r.errorCount = 1;
                r.errorMessages.add("Loại import '" + type.getDisplayName() + "' chưa được hỗ trợ.");
                yield r;
            }
        };
    }

    private static String getCellString(Row row, int col) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield new java.text.SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                }
                double d = cell.getNumericCellValue();
                yield (d == Math.floor(d)) ? String.valueOf((long) d) : String.valueOf(d);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
}
