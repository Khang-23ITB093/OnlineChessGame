package org.example.onlinechessgame.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    private static String DB_URL = "jdbc:mysql://localhost:3306/onlinechessgame";
    private static String USER = "root";
    private static String PASSWORD = "";
    static Connection conn = null;

    public static Connection getConnection() {
        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to database
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return conn;
    }
    public static void dbDisconnect() throws SQLException {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
