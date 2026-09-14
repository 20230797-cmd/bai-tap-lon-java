package com.qlcvht.service;

import com.qlcvht.dao.AuditDAO;
import com.qlcvht.model.AuditLog;
import com.qlcvht.model.TaiKhoan;

public class AuditService {

    private static AuditService instance;
    private final AuditDAO auditDAO;

    private AuditService() {
        this.auditDAO = new AuditDAO();
    }

    public static synchronized AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    public void logLogin(TaiKhoan user) {
        if (user == null) return;
        new Thread(() -> {
            try {
                auditDAO.recordLogin(
                    user.getTenDangNhap(),
                    user.getHoTen() != null ? user.getHoTen() : user.getTenDangNhap(),
                    user.getVaiTro() != null ? user.getVaiTro() : "UNKNOWN",
                    "127.0.0.1 (Localhost)",
                    "Java Swing Desktop Client"
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void logLogout(TaiKhoan user) {
        if (user == null) return;
        try {
            auditDAO.recordLogout(user.getTenDangNhap());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logAction(TaiKhoan user, String actionType, String description) {
        if (user == null) return;
        new Thread(() -> {
            try {
                AuditLog log = new AuditLog(
                    user.getTenDangNhap(),
                    user.getHoTen() != null ? user.getHoTen() : user.getTenDangNhap(),
                    user.getVaiTro() != null ? user.getVaiTro() : "UNKNOWN",
                    actionType,
                    description
                );
                auditDAO.logAction(log);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void logAction(String username, String fullName, String role, String actionType, String description) {
        new Thread(() -> {
            try {
                AuditLog log = new AuditLog(username, fullName, role, actionType, description);
                auditDAO.logAction(log);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public int getOnlineUsersCount() {
        return auditDAO.countOnlineUsers();
    }

    public int getLoginsTodayCount() {
        return auditDAO.countLoginsToday();
    }

    public boolean forceLogoutUser(String username) {
        return auditDAO.forceLogout(username);
    }
}
