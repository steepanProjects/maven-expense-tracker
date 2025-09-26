package com.todo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public  static final String driver = "com.mysql.cj.jdbc.Driver";
    public  static final String url = "jdbc:mysql://localhost:3306/todo";
    public  static final String username = "root";
    public  static final String password = "1234";
    static {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found");
        }
    }
    public static Connection getDBConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
