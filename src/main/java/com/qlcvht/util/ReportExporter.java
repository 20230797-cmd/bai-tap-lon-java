package com.qlcvht.util;

import com.qlcvht.model.AIRiskPrediction;
import com.qlcvht.model.CounselingProgressItem;
import com.qlcvht.service.AIPredictionService;
import com.qlcvht.service.ThongKeService;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.poi.xwpf.usermodel.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Utility xuat Bieu mau Bao cao Hanh chinh (Bien ban hop lop, Bao cao tong hop Khoa/Lop)
 * sang dinh dang Word (.docx) va PDF (.pdf).
 */
public class ReportExporter {

    private static final ThongKeService thongKeService = new ThongKeService();
    private static final AIPredictionService aiService = new AIPredictionService();

    // =========================================================================
    // 1. XUẤT BIÊN BẢN HỌP LỚP CỐ VẤN HỌC TẬP
    // =========================================================================

    public static void exportBienBanHopLopWord(String maLop) {
        File fileToSave = chooseSaveFile("Bien_Ban_Hop_Lop_" + maLop, "docx", "Word Document (*.docx)");
        if (fileToSave == null) return;

        Map<String, Integer> stats = thongKeService.getThongKeTongQuan(maLop);
        List<AIRiskPrediction> aiList = aiService.predictAllStudents(maLop, "ALL");
        String currentDateStr = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        try (XWPFDocument doc = new XWPFDocument()) {
            // Header Quoc hieu & Ten truong
            addParagraph(doc, "BỘ GIÁO DỤC VÀ ĐÀO TẠO", 10, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "TRƯỜNG ĐẠI HỌC XÂY DỰNG HÀ NỘI (HUCE)", 11, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM", 11, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "Độc lập - Tự do - Hạnh phúc", 11, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "------------------------", 10, false, ParagraphAlignment.CENTER);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            // Tieu de Bien ban
            addParagraph(doc, "BIÊN BẢN HỌP LỚP CỐ VẤN HỌC TẬP", 16, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "V/v Tình hình học tập, Cảnh báo học vụ & Tư vấn sinh viên nguy cơ", 12, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "Thời gian thực hiện: Ngày " + currentDateStr, 11, false, ParagraphAlignment.CENTER);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            // Noi dung cuoc hop
            addParagraph(doc, "I. THÔNG TIN CHUNG", 13, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "• Lớp sinh hoạt: " + ("ALL".equals(maLop) ? "Tất cả các lớp" : maLop), 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "• Tổng số sinh viên: " + stats.getOrDefault("tong_sv", 0) + " sinh viên", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "• Chủ trì cuộc họp: Cố vấn học tập phụ trách lớp", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "• Thư ký: Lớp trưởng / Đại diện lớp", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "II. THỐNG KÊ TÌNH HÌNH CẢNH BÁO HỌC VỤ", 13, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "1. Số lượng sinh viên đang học bình thường: " + stats.getOrDefault("sv_binh_thuong", 0) + " SV", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "2. Số lượng sinh viên bị Cảnh báo học vụ Mức 1: " + stats.getOrDefault("cb_muc_1", 0) + " SV", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "3. Số lượng sinh viên bị Cảnh báo học vụ Mức 2: " + stats.getOrDefault("cb_muc_2", 0) + " SV", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "4. Số lượng sinh viên bị Buộc thôi học: " + stats.getOrDefault("buoc_thoi_hoc", 0) + " SV", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "III. DANH SÁCH SINH VIÊN CÓ NGUY CƠ HỌC VỤ & ĐỀ XUẤT TƯ VẤN (AI PREDICTION)", 13, true, ParagraphAlignment.LEFT);

            // Bang sinh vien nguy co
            XWPFTable table = doc.createTable();
            XWPFTableRow headerRow = table.getRow(0);
            setHeaderCell(headerRow, 0, "STT");
            setHeaderCell(headerRow, 1, "Mã SV");
            setHeaderCell(headerRow, 2, "Họ và Tên");
            setHeaderCell(headerRow, 3, "GPA Hiện tại");
            setHeaderCell(headerRow, 4, "GPA Dự báo");
            setHeaderCell(headerRow, 5, "TC Nợ");
            setHeaderCell(headerRow, 6, "Mức nguy cơ AI");
            setHeaderCell(headerRow, 7, "Khuyến nghị tư vấn");

            int idx = 1;
            for (AIRiskPrediction ai : aiList) {
                if ("HIGH_RISK".equals(ai.getMucRuiRo()) || "MEDIUM_RISK".equals(ai.getMucRuiRo())) {
                    XWPFTableRow r = table.createRow();
                    r.getCell(0).setText(String.valueOf(idx++));
                    r.getCell(1).setText(ai.getMaSv());
                    r.getCell(2).setText(ai.getHoTen());
                    r.getCell(3).setText(String.valueOf(ai.getGpaMoiNhat()));
                    r.getCell(4).setText(String.valueOf(ai.getGpaDuBao()));
                    r.getCell(5).setText(String.valueOf(ai.getSoTinChiNo()));
                    r.getCell(6).setText("HIGH_RISK".equals(ai.getMucRuiRo()) ? "NGUY CƠ CAO" : "TRUNG BÌNH");
                    r.getCell(7).setText(ai.getKhuyenNghi());
                }
            }

            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "IV. KẾT LUẬN & CAM KẾT", 13, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "• CVHT yêu cầu tất cả sinh viên thuộc diện Cảnh báo học vụ và Nguy cơ cao liên hệ tư vấn trực tiếp.", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "• Ban cán sự lớp phối hợp theo dõi sĩ số và nhắc nhở lịch đăng ký môn học cải thiện.", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            // Chuky
            addParagraph(doc, "       LỚP TRƯỞNG                                             CỐ VẤN HỌC TẬP", 12, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "     (Ký và ghi rõ họ tên)                                   (Ký và ghi rõ họ tên)", 10, false, ParagraphAlignment.CENTER);

            try (FileOutputStream out = new FileOutputStream(fileToSave)) {
                doc.write(out);
            }
            showSuccess("Xuất Biên bản họp lớp (Word) thành công:\n" + fileToSave.getAbsolutePath());
        } catch (Exception e) {
            showError("Lỗi xuất file Word: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void exportBienBanHopLopPDF(String maLop) {
        File fileToSave = chooseSaveFile("Bien_Ban_Hop_Lop_" + maLop, "pdf", "PDF Document (*.pdf)");
        if (fileToSave == null) return;

        Map<String, Integer> stats = thongKeService.getThongKeTongQuan(maLop);
        List<AIRiskPrediction> aiList = aiService.predictAllStudents(maLop, "ALL");
        String currentDateStr = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        try (FileOutputStream out = new FileOutputStream(fileToSave)) {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fTitle = getPdfFont(15, Font.BOLD);
            Font fHeader = getPdfFont(11, Font.BOLD);
            Font fBold = getPdfFont(11, Font.BOLD);
            Font fNormal = getPdfFont(10, Font.NORMAL);
            Font fSmall = getPdfFont(9, Font.NORMAL);

            // Header
            Paragraph pHeader = new Paragraph("TRƯỜNG ĐẠI HỌC XÂY DỰNG HÀ NỘI (HUCE)\nCỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM\nĐộc lập - Tự do - Hạnh phúc\n------------------------", fHeader);
            pHeader.setAlignment(Element.ALIGN_CENTER);
            document.add(pHeader);

            document.add(new Paragraph("\n"));
            Paragraph pTitle = new Paragraph("BIÊN BẢN HỌP LỚP CỐ VẤN HỌC TẬP\nV/v Tình hình học tập, Cảnh báo học vụ & Tư vấn sinh viên nguy cơ", fTitle);
            pTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(pTitle);

            document.add(new Paragraph("Thời gian: Ngày " + currentDateStr + "   |   Lớp: " + maLop + "\n\n", fNormal));

            document.add(new Paragraph("I. THỐNG KÊ TÌNH HÌNH CẢNH BÁO HỌC VỤ", fBold));
            document.add(new Paragraph(String.format("• Tổng sinh viên: %d | Học bình thường: %d | Cảnh báo Mức 1: %d | Cảnh báo Mức 2: %d | Buộc thôi học: %d\n\n",
                stats.getOrDefault("tong_sv", 0), stats.getOrDefault("sv_binh_thuong", 0),
                stats.getOrDefault("cb_muc_1", 0), stats.getOrDefault("cb_muc_2", 0), stats.getOrDefault("buoc_thoi_hoc", 0)), fNormal));

            document.add(new Paragraph("II. DANH SÁCH SINH VIÊN DỰ BÁO NGUY CƠ HỌC VỤ (AI PREDICTION)", fBold));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 2, 3, 1.5f, 1.5f, 1.5f, 2, 4});

            addPdfHeaderCell(table, "STT", fBold);
            addPdfHeaderCell(table, "Mã SV", fBold);
            addPdfHeaderCell(table, "Họ Tên", fBold);
            addPdfHeaderCell(table, "GPA", fBold);
            addPdfHeaderCell(table, "Dự báo", fBold);
            addPdfHeaderCell(table, "Nợ TC", fBold);
            addPdfHeaderCell(table, "Nguy cơ", fBold);
            addPdfHeaderCell(table, "Khuyến nghị", fBold);

            int idx = 1;
            for (AIRiskPrediction ai : aiList) {
                if ("HIGH_RISK".equals(ai.getMucRuiRo()) || "MEDIUM_RISK".equals(ai.getMucRuiRo())) {
                    table.addCell(new Phrase(String.valueOf(idx++), fSmall));
                    table.addCell(new Phrase(ai.getMaSv(), fSmall));
                    table.addCell(new Phrase(ai.getHoTen(), fSmall));
                    table.addCell(new Phrase(String.valueOf(ai.getGpaMoiNhat()), fSmall));
                    table.addCell(new Phrase(String.valueOf(ai.getGpaDuBao()), fSmall));
                    table.addCell(new Phrase(String.valueOf(ai.getSoTinChiNo()), fSmall));
                    table.addCell(new Phrase("HIGH_RISK".equals(ai.getMucRuiRo()) ? "NGUY CƠ CAO" : "TRUNG BÌNH", fSmall));
                    table.addCell(new Phrase(ai.getKhuyenNghi(), fSmall));
                }
            }
            document.add(table);

            document.add(new Paragraph("\n\n"));
            Paragraph pSig = new Paragraph("       LỚP TRƯỞNG                                             CỐ VẤN HỌC TẬP\n     (Ký và ghi rõ họ tên)                                   (Ký và ghi rõ họ tên)", fBold);
            pSig.setAlignment(Element.ALIGN_CENTER);
            document.add(pSig);

            document.close();
            showSuccess("Xuất Biên bản họp lớp (PDF) thành công:\n" + fileToSave.getAbsolutePath());
        } catch (Exception e) {
            showError("Lỗi xuất file PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =========================================================================
    // 2. XUẤT BÁO CÁO TỔNG HỢP KHOA / LỚP TRÌNH BAN GIÁM HIỆU
    // =========================================================================

    public static void exportBaoCaoTongHopWord(String maLop) {
        File fileToSave = chooseSaveFile("Bao_Cao_Tong_Hop_Hoc_Vu_" + maLop, "docx", "Word Document (*.docx)");
        if (fileToSave == null) return;

        Map<String, Integer> stats = thongKeService.getThongKeTongQuan(maLop);
        Map<String, Object> progressStats = thongKeService.getThongKeTienDoTuVan(maLop);
        List<CounselingProgressItem> counselingItems = (List<CounselingProgressItem>) progressStats.get("items");

        String currentDateStr = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        try (XWPFDocument doc = new XWPFDocument()) {
            addParagraph(doc, "TRƯỜNG ĐẠI HỌC XÂY DỰNG HÀ NỘI (HUCE)", 11, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "ĐƠN VỊ: CỐ VẤN HỌC TẬP / QUẢN LÝ KHOA", 10, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "---------------------------------------------", 10, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "BÁO CÁO TỔNG HỢP TÌNH HÌNH HỌC VỤ & TIẾN ĐỘ TƯ VẤN", 16, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "KÍNH GỬI: BAN GIÁM HIỆU TRƯỜNG ĐẠI HỌC XÂY DỰNG HÀ NỘI", 12, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "Thời điểm báo cáo: Ngày " + currentDateStr + "   |   Phạm vi: " + ("ALL".equals(maLop) ? "Toàn Khoa" : "Lớp " + maLop), 11, false, ParagraphAlignment.CENTER);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "I. KẾT QUẢ THỐNG KÊ TỔNG QUAN HỌC VỤ", 13, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "1. Tổng số sinh viên quản lý: " + stats.getOrDefault("tong_sv", 0) + " SV", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "2. Sinh viên bị Cảnh báo Mức 1: " + stats.getOrDefault("cb_muc_1", 0) + " SV", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "3. Sinh viên bị Cảnh báo Mức 2: " + stats.getOrDefault("cb_muc_2", 0) + " SV", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "4. Sinh viên bị Buộc thôi học: " + stats.getOrDefault("buoc_thoi_hoc", 0) + " SV", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "II. BÁO CÁO TIẾN ĐỘ TƯ VẤN CỦA CỐ VẤN HỌC TẬP", 13, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "• Tổng số SV bị cảnh báo học vụ: " + progressStats.get("tongSvCanhBao") + " SV", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "• Số SV đã được CVHT gặp mặt tư vấn: " + progressStats.get("svDaTuVan") + " SV (" + progressStats.get("percentDaTuVan") + "% tổng số SV cảnh báo)", 11, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "• Số SV cải thiện điểm số sau khi tư vấn: " + progressStats.get("svCaiThienDiem") + " SV (" + progressStats.get("percentCaiThien") + "% số SV đã tư vấn)", 11, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, "III. CHI TIẾT KẾT QUẢ TƯ VẤN VÀ CẢI THIỆN ĐIỂM SỐ", 13, true, ParagraphAlignment.LEFT);

            XWPFTable table = doc.createTable();
            XWPFTableRow headerRow = table.getRow(0);
            setHeaderCell(headerRow, 0, "STT");
            setHeaderCell(headerRow, 1, "Mã SV");
            setHeaderCell(headerRow, 2, "Họ và Tên");
            setHeaderCell(headerRow, 3, "Lớp");
            setHeaderCell(headerRow, 4, "Ngày tư vấn");
            setHeaderCell(headerRow, 5, "GPA Trước TV");
            setHeaderCell(headerRow, 6, "GPA Sau TV");
            setHeaderCell(headerRow, 7, "Đánh giá Cải thiện");

            int idx = 1;
            for (CounselingProgressItem item : counselingItems) {
                XWPFTableRow r = table.createRow();
                r.getCell(0).setText(String.valueOf(idx++));
                r.getCell(1).setText(item.getMaSv());
                r.getCell(2).setText(item.getHoTen());
                r.getCell(3).setText(item.getMaLop());
                r.getCell(4).setText(item.getNgayTuVan() != null ? item.getNgayTuVan().toString() : "");
                r.getCell(5).setText(String.valueOf(item.getGpaTruocTuVan()));
                r.getCell(6).setText(String.valueOf(item.getGpaSauTuVan()));
                r.getCell(7).setText(item.getTrangThaiCaiThien());
            }

            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "IV. ĐỀ XUẤT VÀ KIẾN NGHỊ VỚI BAN GIÁM HIỆU", 13, true, ParagraphAlignment.LEFT);
            addParagraph(doc, "1. Kính đề nghị Ban Giám hiệu phê duyệt kế hoạch hỗ trợ đăng ký lớp học lại hè cho SV thuộc nhóm Cảnh báo học vụ.", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "2. Đề nghị Phòng Đào tạo hỗ trợ mở thêm các lớp học cải thiện điểm cho các môn học đại cương có số lượng nợ cao.", 11, false, ParagraphAlignment.LEFT);
            addParagraph(doc, "", 10, false, ParagraphAlignment.LEFT);

            addParagraph(doc, " CỐ VẤN HỌC TẬP / TRƯỞNG KHOA                               TRƯỞNG PHÒNG ĐÀO TẠO", 12, true, ParagraphAlignment.CENTER);
            addParagraph(doc, "    (Ký và ghi rõ họ tên)                                      (Ký và ghi rõ họ tên)", 10, false, ParagraphAlignment.CENTER);

            try (FileOutputStream out = new FileOutputStream(fileToSave)) {
                doc.write(out);
            }
            showSuccess("Xuất Báo cáo tổng hợp (Word) thành công:\n" + fileToSave.getAbsolutePath());
        } catch (Exception e) {
            showError("Lỗi xuất file Word: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void exportBaoCaoTongHopPDF(String maLop) {
        File fileToSave = chooseSaveFile("Bao_Cao_Tong_Hop_Hoc_Vu_" + maLop, "pdf", "PDF Document (*.pdf)");
        if (fileToSave == null) return;

        Map<String, Integer> stats = thongKeService.getThongKeTongQuan(maLop);
        Map<String, Object> progressStats = thongKeService.getThongKeTienDoTuVan(maLop);
        List<CounselingProgressItem> counselingItems = (List<CounselingProgressItem>) progressStats.get("items");
        String currentDateStr = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        try (FileOutputStream out = new FileOutputStream(fileToSave)) {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fTitle = getPdfFont(15, Font.BOLD);
            Font fHeader = getPdfFont(11, Font.BOLD);
            Font fBold = getPdfFont(11, Font.BOLD);
            Font fNormal = getPdfFont(10, Font.NORMAL);
            Font fSmall = getPdfFont(9, Font.NORMAL);

            Paragraph pHeader = new Paragraph("TRƯỜNG ĐẠI HỌC XÂY DỰNG HÀ NỘI (HUCE)\nCỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM\n------------------------", fHeader);
            pHeader.setAlignment(Element.ALIGN_CENTER);
            document.add(pHeader);

            document.add(new Paragraph("\n"));
            Paragraph pTitle = new Paragraph("BÁO CÁO TỔNG HỢP TÌNH HÌNH HỌC VỤ & TIẾN ĐỘ TƯ VẤN\nKÍNH GỬI: BAN GIÁM HIỆU", fTitle);
            pTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(pTitle);

            document.add(new Paragraph("Thời điểm báo cáo: " + currentDateStr + "   |   Phạm vi: " + maLop + "\n\n", fNormal));

            document.add(new Paragraph("I. TỈ LỆ TIẾN ĐỘ TƯ VẤN & CẢI THIỆN ĐIỂM SỐ", fBold));
            document.add(new Paragraph(String.format("• Tỉ lệ sinh viên bị cảnh báo đã được tư vấn: %.1f%% (%s / %s SV)\n" +
                                                     "• Tỉ lệ sinh viên cải thiện điểm số sau tư vấn: %.1f%% (%s / %s SV đã tư vấn)\n\n",
                progressStats.get("percentDaTuVan"), progressStats.get("svDaTuVan"), progressStats.get("tongSvCanhBao"),
                progressStats.get("percentCaiThien"), progressStats.get("svCaiThienDiem"), progressStats.get("svDaTuVan")), fNormal));

            document.add(new Paragraph("II. CHI TIẾT KẾT QUẢ TƯ VẤN VÀ CẢI THIỆN ĐIỂM SỐ", fBold));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 2, 3, 1.5f, 2, 1.5f, 1.5f, 3.5f});

            addPdfHeaderCell(table, "STT", fBold);
            addPdfHeaderCell(table, "Mã SV", fBold);
            addPdfHeaderCell(table, "Họ Tên", fBold);
            addPdfHeaderCell(table, "Lớp", fBold);
            addPdfHeaderCell(table, "Ngày TV", fBold);
            addPdfHeaderCell(table, "GPA Trước", fBold);
            addPdfHeaderCell(table, "GPA Sau", fBold);
            addPdfHeaderCell(table, "Đánh giá Cải thiện", fBold);

            int idx = 1;
            for (CounselingProgressItem item : counselingItems) {
                table.addCell(new Phrase(String.valueOf(idx++), fSmall));
                table.addCell(new Phrase(item.getMaSv(), fSmall));
                table.addCell(new Phrase(item.getHoTen(), fSmall));
                table.addCell(new Phrase(item.getMaLop(), fSmall));
                table.addCell(new Phrase(item.getNgayTuVan() != null ? item.getNgayTuVan().toString() : "", fSmall));
                table.addCell(new Phrase(String.valueOf(item.getGpaTruocTuVan()), fSmall));
                table.addCell(new Phrase(String.valueOf(item.getGpaSauTuVan()), fSmall));
                table.addCell(new Phrase(item.getTrangThaiCaiThien(), fSmall));
            }
            document.add(table);

            document.add(new Paragraph("\n\n"));
            Paragraph pSig = new Paragraph(" CỐ VẤN HỌC TẬP / TRƯỞNG KHOA                               TRƯỞNG PHÒNG ĐÀO TẠO\n    (Ký và ghi rõ họ tên)                                      (Ký và ghi rõ họ tên)", fBold);
            pSig.setAlignment(Element.ALIGN_CENTER);
            document.add(pSig);

            document.close();
            showSuccess("Xuất Báo cáo tổng hợp (PDF) thành công:\n" + fileToSave.getAbsolutePath());
        } catch (Exception e) {
            showError("Lỗi xuất file PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    private static File chooseSaveFile(String defaultName, String ext, String extDescription) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu file báo cáo");
        fileChooser.setFileFilter(new FileNameExtensionFilter(extDescription, ext));
        fileChooser.setSelectedFile(new File(defaultName + "." + ext));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection != JFileChooser.APPROVE_OPTION) return null;

        File file = fileChooser.getSelectedFile();
        if (!file.getAbsolutePath().endsWith("." + ext)) {
            file = new File(file.getAbsolutePath() + "." + ext);
        }
        return file;
    }

    private static void addParagraph(XWPFDocument doc, String text, int fontSize, boolean isBold, ParagraphAlignment align) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(align);
        XWPFRun r = p.createRun();
        r.setText(text);
        r.setFontSize(fontSize);
        r.setBold(isBold);
        r.setFontFamily("Times New Roman");
    }

    private static void setHeaderCell(XWPFTableRow row, int index, String text) {
        XWPFTableCell cell = row.getCell(index) != null ? row.getCell(index) : row.createCell();
        cell.setText(text);
        cell.setColor("1E40AF"); // Navy blue header
    }

    private static Font getPdfFont(int size, int style) {
        try {
            // Su dung font Windows Arial / Times New Roman de ho tro tieng Viet chuẩn
            String fontPath = "C:/Windows/Fonts/arial.ttf";
            if (!new File(fontPath).exists()) {
                fontPath = "C:/Windows/Fonts/times.ttf";
            }
            if (new File(fontPath).exists()) {
                BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                return new Font(bf, size, style, Color.BLACK);
            }
        } catch (Exception ignored) {}
        return new Font(Font.HELVETICA, size, style, Color.BLACK);
    }

    private static void addPdfHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(30, 64, 175));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static void showSuccess(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Xuất Báo Cáo Thành Công", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Lỗi Xuất File", JOptionPane.ERROR_MESSAGE);
    }
}
