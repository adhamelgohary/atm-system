package com.atm.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // !! IMPORTANT !!: Change the user and password to match your MySQL setup.
    // By default, the root password on a new brew installation is often empty ("").
    private static final String URL = "jdbc:mysql://localhost:3306/atm_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // CHANGE IF YOU SET A PASSWORD

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}