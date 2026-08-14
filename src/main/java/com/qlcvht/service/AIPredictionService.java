package com.qlcvht.service;

import com.qlcvht.config.DatabaseConnection;
import com.qlcvht.model.AIRiskPrediction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service Du bao nguy co hoc vu AI bang thuat toan Hoi quy tuyen tinh & Ma tran trong so rui ro.
 */
public class AIPredictionService {

    public List<AIRiskPrediction> predictAllStudents(String maLopFilter, String riskLevelFilter) {
        List<AIRiskPrediction> predictions = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT s.ma_sv, s.ho_ten, s.ma_lop, s.trang_thai " +
            "FROM sinh_vien s WHERE 1=1 "
        );

        if (maLopFilter != null && !maLopFilter.isEmpty() && !"ALL".equals(maLopFilter)) {
            sql.append("AND s.ma_lop = ? ");
        }
        sql.append("ORDER BY s.ma_lop, s.ma_sv");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (maLopFilter != null && !maLopFilter.isEmpty() && !"ALL".equals(maLopFilter)) {
                ps.setString(1, maLopFilter);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String maSv = rs.getString("ma_sv");
                    String hoTen = rs.getString("ho_ten");
                    String maLop = rs.getString("ma_lop");
                    String trangThai = rs.getString("trang_thai");

                    AIRiskPrediction pred = predictForStudent(conn, maSv, hoTen, maLop, trangThai);
                    
                    if (riskLevelFilter == null || "ALL".equals(riskLevelFilter) || riskLevelFilter.equals(pred.getMucRuiRo())) {
                        predictions.add(pred);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return predictions;
    }

    private AIRiskPrediction predictForStudent(Connection conn, String maSv, String hoTen, String maLop, String trangThai) {
        String sql = "SELECT hoc_ky, nam_hoc, gpa_hoc_ky, gpa_tich_luy, so_tin_chi_no " +
                     "FROM ket_qua_hoc_tap WHERE ma_sv = ? " +
                     "ORDER BY nam_hoc ASC, hoc_ky ASC";

        List<Double> gpaList = new ArrayList<>();
        int latestDebtCredits = 0;
        double latestGpa = 0.0;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSv);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double gpaHk = rs.getDouble("gpa_hoc_ky");
                    gpaList.add(gpaHk);
                    latestGpa = gpaHk;
                    latestDebtCredits = rs.getInt("so_tin_chi_no");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Mat dinh neu chua co ket qua hoc tap
        if (gpaList.isEmpty()) {
            return new AIRiskPrediction(
                maSv, hoTen, maLop, 0.0, 0.0, "CHUA_CO_DU_LIEU",
                0, "LOW_RISK", trangThai,
                "Chưa có dữ liệu kết quả học tập các học kỳ.",
                "Cần bổ sung kết quả học tập.", gpaList
            );
        }

        int n = gpaList.size();
        double slope = 0.0;
        double predictedGpa = latestGpa;

        if (n >= 2) {
            // Thuat toan Hoi quy tuyen tinh (Linear Regression) y = a*x + b
            double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
            for (int i = 0; i < n; i++) {
                double x = i + 1;
                double y = gpaList.get(i);
                sumX += x;
                sumY += y;
                sumXY += x * y;
                sumX2 += x * x;
            }

            double denominator = (n * sumX2 - sumX * sumX);
            if (denominator != 0) {
                slope = (n * sumXY - sumX * sumY) / denominator;
                double intercept = (sumY - slope * sumX) / n;
                predictedGpa = slope * (n + 1) + intercept;
            }
        } else {
            // Chi co 1 hoc ky
            predictedGpa = latestGpa;
        }

        // Gioi han GPA du bao trong khoang [0.0, 4.0]
        if (predictedGpa > 4.0) predictedGpa = 4.0;
        if (predictedGpa < 0.0) predictedGpa = 0.0;

        // Lam tron 2 chu so thap phan
        predictedGpa = Math.round(predictedGpa * 100.0) / 100.0;

        // Xac dinh Trend (Xu huong)
        String trend;
        if (slope <= -0.25) trend = "GIAM_MANH";
        else if (slope < -0.05) trend = "GIAM_NHE";
        else if (slope > 0.05) trend = "TANG";
        else trend = "ON_DINH";

        // Xac dinh Muc Rui Ro & Khuyen Nghi
        String mucRuiRo;
        String lyDo;
        String khuyenNghi;

        if (predictedGpa < 1.6 || (latestGpa < 1.8 && slope < -0.15) || latestDebtCredits >= 10 || "CANH_BAO_2".equals(trangThai) || "BUOC_THOI_HOC".equals(trangThai)) {
            mucRuiRo = "HIGH_RISK";
            lyDo = String.format("GPA suy giảm (dự báo kỳ tới %.2f), nợ %d tín chỉ. Nguy cơ rơi vào Cảnh báo Mức 2 / Buộc thôi học.", predictedGpa, latestDebtCredits);
            khuyenNghi = "CẦN GẶP MẶT TƯ VẤN KHẨN CẤP: Lập lộ trình học cải thiện, giảm số tín chỉ đăng ký kỳ tới.";
        } else if (predictedGpa < 2.0 || slope <= -0.20 || latestDebtCredits >= 4 || "CANH_BAO_1".equals(trangThai)) {
            mucRuiRo = "MEDIUM_RISK";
            lyDo = String.format("GPA có xu hướng giảm (dự báo kỳ tới %.2f), nợ %d tín chỉ. Nguy cơ bị Cảnh báo Mức 1.", predictedGpa, latestDebtCredits);
            khuyenNghi = "NHẮC NHỞ & HỖ TRỢ: Gửi thông báo cảnh báo sớm, hướng dẫn phương pháp học tập và đăng ký thi lại.";
        } else {
            mucRuiRo = "LOW_RISK";
            lyDo = String.format("Kết quả học tập ổn định (dự báo kỳ tới %.2f), nợ %d tín chỉ.", predictedGpa, latestDebtCredits);
            khuyenNghi = "AN TOÀN: Khuyến khích duy trì phong độ học tập và đăng ký học bổng.";
        }

        return new AIRiskPrediction(
            maSv, hoTen, maLop, latestGpa, predictedGpa,
            trend, latestDebtCredits, mucRuiRo, trangThai,
            lyDo, khuyenNghi, gpaList
        );
    }
}
