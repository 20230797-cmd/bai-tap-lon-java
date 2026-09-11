package com.qlcvht.util;

import java.util.regex.Pattern;

/**
 * Tiện ích kiểm tra tính hợp lệ của dữ liệu đầu vào (Học phần Java Validation - Lab 08)
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(0|\\+84)[0-9]{9}$");
    private static final Pattern MSSV_PATTERN = Pattern.compile("^[0-9]{8,12}$");

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isValidMSSV(String mssv) {
        if (!isNotEmpty(mssv)) return false;
        return MSSV_PATTERN.matcher(mssv.trim()).matches();
    }

    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (!isNotEmpty(phone)) return true; // Cho phép để trống nếu không bắt buộc
        return PHONE_PATTERN.matcher(phone.trim().replaceAll("\\s+", "")).matches();
    }

    public static boolean isValidGPA(double gpa) {
        return gpa >= 0.0 && gpa <= 4.0;
    }

    public static boolean isValidCredits(int credits) {
        return credits >= 0 && credits <= 50;
    }
}
