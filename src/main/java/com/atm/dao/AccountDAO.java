package com.atm.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.atm.model.Account;
import com.atm.util.DatabaseConnection;

public class AccountDAO {
    public Optional<Account> findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToAccount(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error finding account: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Account> findByUserId(int userId) {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToAccount(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error finding account by user ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    public void updateBalance(String accountNumber, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, newBalance);
            pstmt.setString(2, accountNumber);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating balance failed, no rows affected.");
            }
        }
    }

    public Account createAccount(int userId, String accountNumber, String accountType, String status, Integer requestedBy) throws SQLException {
        String sql = "INSERT INTO accounts (user_id, account_number, account_type, balance, status, requested_by) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, accountNumber);
            pstmt.setString(3, accountType);
            pstmt.setBigDecimal(4, BigDecimal.ZERO);
            pstmt.setString(5, status);
            if (requestedBy != null) {
                pstmt.setInt(6, requestedBy);
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating account failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return findById(generatedKeys.getInt(1)).orElseThrow(() -> new SQLException("Could not retrieve created account"));
                } else {
                    throw new SQLException("Creating account failed, no ID obtained.");
                }
            }
        }
    }

    public List<Account> getPendingAccounts() {
        List<Account> pendingAccounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE status = 'PENDING_ACTIVATION'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                pendingAccounts.add(mapRowToAccount(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error getting pending accounts: " + e.getMessage());
        }
        return pendingAccounts;
    }

    public void updateAccountStatus(String accountNumber, String status) throws SQLException {
        String sql = "UPDATE accounts SET status = ? WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, accountNumber);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating account status failed, no rows affected.");
            }
        }
    }

    private Optional<Account> findById(int id) {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToAccount(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error finding account by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    private Account mapRowToAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setId(rs.getInt("id"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setUserId(rs.getInt("user_id"));
        account.setAccountType(rs.getString("account_type"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setStatus(rs.getString("status"));
        account.setRequestedBy(rs.getObject("requested_by", Integer.class));
        return account;
    }
}