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
        Properties prop = new Properties();
        boolean loaded = false;

        // Ưu tiên đọc trực tiếp từ file src/main/resources/database.properties hoặc thư mục gốc để luôn cập nhật cấu hình mới nhất
        File[] candidateFiles = new File[] {
            new File("src/main/resources/database.properties"),
            new File("database.properties")
        };

        for (File file : candidateFiles) {
            if (file.exists() && file.isFile()) {
                try (java.io.FileInputStream fis = new java.io.FileInputStream(file);
                     InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
                    prop.load(isr);
                    loaded = true;
                    break;
                } catch (Exception ignored) {}
            }
        }

        if (!loaded) {
            try (InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
                if (is != null) {
                    prop.load(new InputStreamReader(is, StandardCharsets.UTF_8));
                    loaded = true;
                }
            } catch (Exception e) {
                System.err.println("[WARN] Không thể đọc database.properties: " + e.getMessage());
            }
        }

        if (loaded) {
            dbType = prop.getProperty("db.type", dbType).trim().toLowerCase();
            dbDriver = prop.getProperty("db.driver", dbDriver).trim();
            dbHost = prop.getProperty("db.host", dbHost).trim();
            dbPort = prop.getProperty("db.port", dbPort).trim();
            dbName = prop.getProperty("db.name", dbName).trim();
            dbUser = prop.getProperty("db.user", dbUser).trim();
            dbPassword = prop.getProperty("db.password", dbPassword != null ? dbPassword : "").trim();
            dbParams = prop.getProperty("db.params", dbParams).trim();
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
            boolean hasChuyenCan = false;
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM chuyen_can_mon_hoc")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    hasChuyenCan = true;
                }
            } catch (SQLException ignored) {
                // Bảng chưa tồn tại hoặc chưa có dữ liệu môn học chuyên cần
            }

            if (!hasChuyenCan) {
                System.out.println("[INFO] Đang tự động nạp cấu trúc bảng và 100% dữ liệu mẫu (môn học + cấm thi) cho " + activeEngineName + "...");
                if (activeEngineName.equalsIgnoreCase("SQL Server")) {
                    executeSqlScript(conn, "sqlserver_schema.sql");
                } else {
                    executeSqlScript(conn, "database.sql");
                }
                System.out.println("[INFO] Nạp dữ liệu mẫu " + activeEngineName + " hoàn tất!");
            }
            ensureDefaultAccounts(conn);
        } catch (Exception e) {
            System.err.println("[WARN] Lỗi khi kiểm tra/khởi tạo bảng " + activeEngineName + ": " + e.getMessage());
        }
    }

    private static void ensureDefaultAccounts(Connection conn) {
        String hash = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92"; // 123456
        String[][] defaults = {
            {"admin", hash, "Quản trị viên Hệ thống EAUT", "admin@eaut.edu.vn", "ADMIN", null},
            {"cv_phongdv", hash, "TS. Đinh Văn Phong", "phong.dv@eaut.edu.vn", "CO_VAN", "CV001"},
            {"cv001", hash, "TS. Đinh Văn Phong", "phong.dv@eaut.edu.vn", "CO_VAN", "CV001"},
            {"cv_haint", hash, "PGS.TS. Nguyễn Thanh Hải", "hai.nt@eaut.edu.vn", "CO_VAN", "CV002"},
            {"cv002", hash, "PGS.TS. Nguyễn Thanh Hải", "hai.nt@eaut.edu.vn", "CO_VAN", "CV002"},
            {"cv_maiht", hash, "ThS. Hoàng Thị Mai", "mai.ht@eaut.edu.vn", "CO_VAN", "CV003"},
            {"cv003", hash, "ThS. Hoàng Thị Mai", "mai.ht@eaut.edu.vn", "CO_VAN", "CV003"},
            {"cv_sonvt", hash, "TS. Vũ Trường Sơn", "son.vt@eaut.edu.vn", "CO_VAN", "CV004"},
            {"cv004", hash, "TS. Vũ Trường Sơn", "son.vt@eaut.edu.vn", "CO_VAN", "CV004"},
            {"20230001", hash, "Vũ Đình Anh", "sv20230001@eaut.edu.vn", "SINH_VIEN", "20230001"}
        };

        for (String[] acc : defaults) {
            try {
                boolean exists = false;
                try (java.sql.PreparedStatement psCheck = conn.prepareStatement("SELECT COUNT(*) FROM tai_khoan WHERE LOWER(ten_dang_nhap) = LOWER(?)")) {
                    psCheck.setString(1, acc[0]);
                    try (ResultSet rs = psCheck.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            exists = true;
                        }
                    }
                }
                if (!exists) {
                    try (java.sql.PreparedStatement psIns = conn.prepareStatement(
                            "INSERT INTO tai_khoan (ten_dang_nhap, mat_khau, ho_ten, email, vai_tro, ma_ref) VALUES (?, ?, ?, ?, ?, ?)")) {
                        psIns.setString(1, acc[0]);
                        psIns.setString(2, acc[1]);
                        psIns.setString(3, acc[2]);
                        psIns.setString(4, acc[3]);
                        psIns.setString(5, acc[4]);
                        psIns.setString(6, acc[5]);
                        psIns.executeUpdate();
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    /**
     * Tự động tạo bảng & dữ liệu mẫu cho SQLite nếu chưa có
     */
    private static void ensureSQLiteSchema(Connection conn) {
        try {
            boolean hasChuyenCan = false;
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='chuyen_can_mon_hoc'")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    try (ResultSet rCount = st.executeQuery("SELECT COUNT(*) FROM chuyen_can_mon_hoc")) {
                        if (rCount.next() && rCount.getInt(1) > 0) {
                            hasChuyenCan = true;
                        }
                    }
                }
            } catch (SQLException ignored) {
            }

            if (!hasChuyenCan) {
                System.out.println("[INFO] Đang tạo bảng và nạp 100% dữ liệu mẫu (16 Môn học + 480 Bản ghi Cấm thi) vào SQLite...");
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
        InputStream is = null;
        try {
            File[] candidateFiles = new File[] {
                new File(resourceName),
                new File("src/main/resources/" + resourceName)
            };
            for (File f : candidateFiles) {
                if (f.exists() && f.isFile()) {
                    is = new java.io.FileInputStream(f);
                    break;
                }
            }
            if (is == null) {
                is = DatabaseConnection.class.getClassLoader().getResourceAsStream(resourceName);
            }
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

    /**
     * Khởi tạo lại và nạp toàn bộ 100% dữ liệu mẫu EAUT vào CSDL hiện hành
     */
    public static synchronized boolean resetAndSeedDatabase() {
        try (Connection conn = getConnection()) {
            if (isSQLiteMode) {
                executeSqlScript(conn, "sqlite_schema.sql");
            } else if (isUsingSQLServer()) {
                executeSqlScript(conn, "sqlserver_schema.sql");
            } else {
                executeSqlScript(conn, "database.sql");
            }
            ensureDefaultAccounts(conn);
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] Lỗi khi nạp lại dữ liệu CSDL: " + e.getMessage());
            e.printStackTrace();
            return false;
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