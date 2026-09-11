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
 * Lớp quản lý kết nối Cơ sở dữ liệu Đa Nền Tảng (Microsoft SQL Server / MySQL + SQLite Fallback).
 * 
 * - Ưu tiên 1: Kết nối Microsoft SQL Server hoặc MySQL theo cấu hình database.properties.
 *              Tự động tạo Database, tạo bảng và nạp dữ liệu mẫu nếu CSDL chưa có.
 * - Ưu tiên 2: Nếu chưa bật hoặc không kết nối được CSDL máy chủ, hệ thống TỰ ĐỘNG chuyển sang
 *              SQLite Engine cục bộ (Zero-Config) với 100% dữ liệu mẫu, đảm bảo ứng dụng luôn chạy mượt mà!
 */
public class DatabaseConnection {

    private static String dbType = "sqlserver";
    private static String dbDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static String dbHost = "localhost";
    private static String dbPort = "1433";
    private static String dbName = "ql_canhbao_hocvu";
    private static String dbUser = "sa";
    private static String dbPassword = "";
    private static String dbParams = "encrypt=true;trustServerCertificate=true;characterEncoding=UTF-8;loginTimeout=5;";

    private static String serverJdbcUrl;
    private static String masterJdbcUrl;
    private static final String SQLITE_DB_PATH = "data/ql_canhbao_hocvu.db";
    private static final String SQLITE_JDBC_URL = "jdbc:sqlite:" + SQLITE_DB_PATH;

    private static boolean isSQLiteMode = false;
    private static String activeEngineName = "SQL Server";
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
                dbType = prop.getProperty("db.type", dbType).trim().toLowerCase();
                dbDriver = prop.getProperty("db.driver", dbDriver).trim();
                dbHost = prop.getProperty("db.host", dbHost).trim();
                dbPort = prop.getProperty("db.port", dbPort).trim();
                dbName = prop.getProperty("db.name", dbName).trim();
                dbUser = prop.getProperty("db.user", dbUser).trim();
                dbPassword = prop.getProperty("db.password", dbPassword != null ? dbPassword : "").trim();
                dbParams = prop.getProperty("db.params", dbParams).trim();
            }
        } catch (Exception e) {
            System.err.println("[WARN] Không thể đọc database.properties, sử dụng cấu hình mặc định: " + e.getMessage());
        }

        buildJdbcUrl();
    }

    private static void buildJdbcUrl() {
        if (dbType.contains("sqlserver") || dbDriver.contains("sqlserver")) {
            activeEngineName = "SQL Server";
            String params = dbParams;
            if (!params.isEmpty() && !params.startsWith(";")) params = ";" + params;
            if (!params.endsWith(";")) params = params + ";";
            serverJdbcUrl = "jdbc:sqlserver://" + dbHost + ":" + dbPort + ";databaseName=" + dbName + params;
            masterJdbcUrl = "jdbc:sqlserver://" + dbHost + ":" + dbPort + ";databaseName=master" + params;
        } else {
            activeEngineName = "MySQL";
            String params = dbParams;
            if (!params.isEmpty() && !params.startsWith("?")) params = "?" + params;
            serverJdbcUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + params;
            masterJdbcUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + params;
        }
    }

    /**
     * Khởi tạo và kiểm tra kết nối (Tự động chọn SQL Server/MySQL hoặc SQLite Fallback)
     */
    public static synchronized void initDatabase() {
        if (initialized) return;

        boolean serverSuccess = false;
        try {
            Class.forName(dbDriver);
            try (Connection conn = DriverManager.getConnection(serverJdbcUrl, dbUser, dbPassword)) {
                System.out.println("[INFO] Kết nối thành công tới " + activeEngineName + " Server: " + dbName + " (" + dbHost + ":" + dbPort + ")");
                ensureServerSchema(conn);
                isSQLiteMode = false;
                serverSuccess = true;
            }
        } catch (Exception e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : "";
            System.err.println("[WARN] Thử kết nối tới " + activeEngineName + " (" + dbHost + ":" + dbPort + ") báo: " + errorMsg);

            // Nếu Database chưa tồn tại trên Server -> Tự động kết nối vào master để tạo Database
            if (errorMsg.contains("Cannot open database") || errorMsg.contains("Unknown database")) {
                try {
                    System.out.println("[INFO] Database '" + dbName + "' chưa có trên " + activeEngineName + ". Đang tự động tạo Database mới...");
                    try (Connection masterConn = DriverManager.getConnection(masterJdbcUrl, dbUser, dbPassword);
                         Statement st = masterConn.createStatement()) {
                        st.executeUpdate("CREATE DATABASE " + dbName);
                        System.out.println("[INFO] Tạo Database '" + dbName + "' thành công!");
                    }
                    // Kết nối lại vào Database vừa tạo
                    try (Connection newConn = DriverManager.getConnection(serverJdbcUrl, dbUser, dbPassword)) {
                        ensureServerSchema(newConn);
                        isSQLiteMode = false;
                        serverSuccess = true;
                        System.out.println("[INFO] Sẵn sàng sử dụng CSDL " + activeEngineName + " (" + dbName + ")");
                    }
                } catch (Exception ex) {
                    System.err.println("[WARN] Không thể tự động tạo database trên server: " + ex.getMessage());
                }
            }
        }

        // 2. Nếu CSDL Server không khả dụng -> Chuyển sang SQLite
        if (!serverSuccess) {
            System.out.println("[INFO] ------------------------------------------------------------");
            System.out.println("[INFO] [MULTI-ENGINE] Tự động kích hoạt CSDL Tích Hợp SQLite (Zero-Config)");
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
                activeEngineName = "SQLite";
                System.out.println("[INFO] Sẵn sàng sử dụng CSDL SQLite: " + SQLITE_DB_PATH);
            } catch (Exception e) {
                System.err.println("[ERROR] Lỗi nghiêm trọng khi khởi tạo SQLite Fallback: " + e.getMessage());
                e.printStackTrace();
            }
        }

        initialized = true;
    }

    /**
     * Lấy kết nối CSDL hiện tại (SQL Server / MySQL hoặc SQLite)
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initDatabase();
        }
        if (isSQLiteMode) {
            return DriverManager.getConnection(SQLITE_JDBC_URL);
        } else {
            return DriverManager.getConnection(serverJdbcUrl, dbUser, dbPassword);
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
     * Tự động tạo bảng & dữ liệu mẫu cho SQL Server / MySQL nếu chưa có
     */
    private static void ensureServerSchema(Connection conn) {
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
                System.out.println("[INFO] Đang tự động nạp cấu trúc bảng và dữ liệu mẫu cho " + activeEngineName + "...");
                if (activeEngineName.equalsIgnoreCase("SQL Server")) {
                    executeSqlScript(conn, "sqlserver_schema.sql");
                } else {
                    executeSqlScript(conn, "database.sql");
                }
                System.out.println("[INFO] Nạp dữ liệu mẫu " + activeEngineName + " hoàn tất!");
            }
        } catch (Exception e) {
            System.err.println("[WARN] Lỗi khi kiểm tra/khởi tạo bảng " + activeEngineName + ": " + e.getMessage());
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
                    if (trimmed.equalsIgnoreCase("GO")) {
                        String sql = sb.toString().trim();
                        if (!sql.isEmpty()) {
                            try {
                                st.execute(sql);
                            } catch (SQLException e) {
                                // Bỏ qua lỗi DROP hoặc CREATE DATABASE
                            }
                        }
                        sb.setLength(0);
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

    public static boolean isUsingSQLServer() {
        return !isSQLiteMode && "SQL Server".equalsIgnoreCase(activeEngineName);
    }

    public static boolean isUsingMySQL() {
        return !isSQLiteMode && "MySQL".equalsIgnoreCase(activeEngineName);
    }

    public static String getDatabaseEngineName() {
        return isSQLiteMode ? "SQLite" : activeEngineName;
    }

    public static String getDatabaseDisplayStatus() {
        if (isSQLiteMode) {
            return "● SQLite (Offline)";
        }
        return "● " + activeEngineName + " (Online)";
    }

    public static String getDatabaseType() {
        return isSQLiteMode ? "SQLite (Tích hợp cục bộ)" : activeEngineName + " (" + dbName + ")";
    }

    public static String getJdbcUrl() {
        return isSQLiteMode ? SQLITE_JDBC_URL : serverJdbcUrl;
    }

    public static String getDbUser() {
        return isSQLiteMode ? "local_sqlite" : dbUser;
    }
}