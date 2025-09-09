package com.atm.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.atm.util.DatabaseConnection;

public class TransactionDAO {
    public void logTransaction(int accountId, String type, BigDecimal amount, String relatedAccount) {
        String sql = "INSERT INTO transactions (account_id, transaction_type, amount, related_account_number) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, accountId);
            pstmt.setString(2, type);
            pstmt.setBigDecimal(3, amount);
            pstmt.setString(4, relatedAccount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // In a real app, use a proper logging framework
            System.err.println("Error logging transaction: " + e.getMessage());
        }
    }
}