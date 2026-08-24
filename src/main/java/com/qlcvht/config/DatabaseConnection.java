package com.qlcvht.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Lớp quản lý kết nối Cơ sở dữ liệu Dual-Engine (MySQL + SQLite Fallback).
 * 
 * - Ưu tiên 1: Kết nối MySQL Server theo cấu hình database.properties.
 *              Tự động khởi tạo bảng và nạp dữ liệu mẫu nếu MySQL chưa có database/bảng.
 * - Ưu tiên 2: Nếu máy chưa cài hoặc chưa bật MySQL, hệ thống TỰ ĐỘNG chuyển sang
 *              SQLite Engine cục bộ (Zero-Config) với 100% dữ liệu mẫu và tài khoản,
 *              đảm bảo bất kỳ ai kéo code về từ GitHub cũng chạy được ngay lập tức!
 */
public class DatabaseConnection {

    private static String dbDriver = "com.mysql.cj.jdbc.Driver";
    private static String dbHost = "localhost";
    private static String dbPort = "3306";
    private static String dbName = "ql_canhbao_hocvu";
    private static String dbUser = "root";
    private static String dbPassword = "";
    private static String dbParams = "useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true&useSSL=false";

    private static String mysqlJdbcUrl;
    private static final String SQLITE_DB_PATH = "data/ql_canhbao_hocvu.db";
    private static final String SQLITE_JDBC_URL = "jdbc:sqlite:" + SQLITE_DB_PATH;

    private static boolean isSQLiteMode = false;
    private static boolean initialized = false;

    static {
        loadConfiguration();
    }

    /**
     * Nạp cấu hình từ file database.properties
     */
    private static void loadConfiguration() {
        try (InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (is != null) {
                Properties prop = new Properties();
                prop.load(new InputStreamReader(is, StandardCharsets.UTF_8));
                dbDriver = prop.getProperty("db.driver", dbDriver);
                dbHost = prop.getProperty("db.host", dbHost);
                dbPort = prop.getProperty("db.port", dbPort);
                dbName = prop.getProperty("db.name", dbName);
                dbUser = prop.getProperty("db.user", dbUser);
                dbPassword = prop.getProperty("db.password", dbPassword);
                dbParams = prop.getProperty("db.params", dbParams);
            }
        } catch (Exception e) {
            System.err.println("[WARN] Không thể đọc database.properties, sử dụng cấu hình mặc định: " + e.getMessage());
        }

        mysqlJdbcUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?" + dbParams;
    }

    /**
     * Khởi tạo và kiểm tra kết nối (Tự động chọn MySQL hoặc SQLite Fallback)
     */
    public static synchronized void initDatabase() {
        if (initialized) return;

        // 1. Thử kết nối MySQL
        boolean mysqlSuccess = false;
        try {
            Class.forName(dbDriver);
            try (Connection conn = DriverManager.getConnection(mysqlJdbcUrl, dbUser, dbPassword)) {
                System.out.println("[INFO] Kết nối thành công tới MySQL Server: " + dbName + " (" + dbHost + ":" + dbPort + ")");
                ensureMySQLSchema(conn);
                isSQLiteMode = false;
                mysqlSuccess = true;
            }
        } catch (Exception e) {
            System.err.println("[WARN] Không thể kết nối tới MySQL Server (" + dbHost + ":" + dbPort + "): " + e.getMessage());
        }

        // 2. Nếu MySQL không khả dụng -> Chuyển sang SQLite
        if (!mysqlSuccess) {
            System.out.println("[INFO] ------------------------------------------------------------");
            System.out.println("[INFO] [DUAL-ENGINE] Tự động kích hoạt CSDL Tích Hợp SQLite (Zero-Config)");
            System.out.println("[INFO] Hệ thống sẽ tự tạo file database và nạp sẵn 100% dữ liệu mẫu.");
            System.out.println("[INFO] ------------------------------------------------------------");
            try {
                Class.forName("org.sqlite.JDBC");
                File dataDir = new File("data");
                if (!dataDir.exists()) {
                    dataDir.mkdirs();
                }
                try (Connection conn = DriverManager.getConnection(SQLITE_JDBC_URL)) {
                    ensureSQLiteSchema(conn);
                }
                isSQLiteMode = true;
                System.out.println("[INFO] Sẵn sàng sử dụng CSDL SQLite: " + SQLITE_DB_PATH);
            } catch (Exception e) {
                System.err.println("[ERROR] Lỗi nghiêm trọng khi khởi tạo SQLite Fallback: " + e.getMessage());
                e.printStackTrace();
            }
        }

        initialized = true;
    }

    /**
     * Lấy kết nối CSDL hiện tại (MySQL hoặc SQLite)
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initDatabase();
        }
        if (isSQLiteMode) {
            return DriverManager.getConnection(SQLITE_JDBC_URL);
        } else {
            return DriverManager.getConnection(mysqlJdbcUrl, dbUser, dbPassword);
        }
    }

    /**
     * Kiểm tra trạng thái kết nối
     */
    public static boolean testConnection() {
        try {
            if (!initialized) {
                initDatabase();
            }
            try (Connection conn = getConnection()) {
                return conn != null && !conn.isClosed();
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Lỗi kiểm tra kết nối: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tự động tạo bảng & dữ liệu mẫu cho MySQL nếu chưa có
     */
    private static void ensureMySQLSchema(Connection conn) {
        try {
            boolean hasAccounts = false;
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tai_khoan")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    hasAccounts = true;
                }
            } catch (SQLException ignored) {
                // Bảng chưa tồn tại
            }

            if (!hasAccounts) {
                System.out.println("[INFO] Đang tự động nạp cấu trúc bảng và dữ liệu mẫu cho MySQL...");
                executeSqlScript(conn, "database.sql");
                System.out.println("[INFO] Nạp dữ liệu mẫu MySQL hoàn tất!");
            }
        } catch (Exception e) {
            System.err.println("[WARN] Lỗi khi kiểm tra/khởi tạo bảng MySQL: " + e.getMessage());
        }
    }

    /**
     * Tự động tạo bảng & dữ liệu mẫu cho SQLite nếu chưa có
     */
    private static void ensureSQLiteSchema(Connection conn) {
        try {
            boolean hasAccounts = false;
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='tai_khoan'")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    try (ResultSet rCount = st.executeQuery("SELECT COUNT(*) FROM tai_khoan")) {
                        if (rCount.next() && rCount.getInt(1) > 0) {
                            hasAccounts = true;
                        }
                    }
                }
            } catch (SQLException ignored) {
            }

            if (!hasAccounts) {
                System.out.println("[INFO] Đang tạo bảng và nạp 100% dữ liệu mẫu vào SQLite...");
                executeSqlScript(conn, "sqlite_schema.sql");
                System.out.println("[INFO] Tạo dữ liệu SQLite thành công!");
            }
        } catch (Exception e) {
            System.err.println("[WARN] Lỗi khi nạp dữ liệu vào SQLite: " + e.getMessage());
        }
    }

    /**
     * Đọc và thực thi file SQL từ resources
     */
    private static void executeSqlScript(Connection conn, String resourceName) {
        try (InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                System.err.println("[WARN] Không tìm thấy resource file SQL: " + resourceName);
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                 Statement st = conn.createStatement()) {
                
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("--") || trimmed.startsWith("#") || trimmed.startsWith("/*")) {
                        continue;
                    }
                    sb.append(line).append("\n");
                    if (trimmed.endsWith(";")) {
                        String sql = sb.toString().trim();
                        if (sql.endsWith(";")) {
                            sql = sql.substring(0, sql.length() - 1).trim();
                        }
                        if (!sql.isEmpty()) {
                            try {
                                st.execute(sql);
                            } catch (SQLException e) {
                                // Bỏ qua lỗi DROP TABLE IF EXISTS hoặc CREATE DATABASE nếu không đủ quyền
                                if (!sql.toUpperCase().startsWith("DROP") && !sql.toUpperCase().startsWith("CREATE DATABASE") && !sql.toUpperCase().startsWith("USE")) {
                                    System.err.println("[WARN] Lỗi thực thi câu lệnh SQL: " + e.getMessage() + " | SQL: " + sql.substring(0, Math.min(80, sql.length())));
                                }
                            }
                        }
                        sb.setLength(0);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Lỗi khi đọc script SQL: " + e.getMessage());
        }
    }

    public static boolean isUsingSQLite() {
        return isSQLiteMode;
    }

    public static String getDatabaseType() {
        return isSQLiteMode ? "SQLite (Tích hợp cục bộ)" : "MySQL (" + dbName + ")";
    }

    public static String getJdbcUrl() {
        return isSQLiteMode ? SQLITE_JDBC_URL : mysqlJdbcUrl;
    }

    public static String getDbUser() {
        return isSQLiteMode ? "local_sqlite" : dbUser;
    }
}