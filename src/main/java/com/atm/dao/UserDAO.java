package com.atm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.atm.model.User;
import com.atm.util.DatabaseConnection;

public class UserDAO {
    public Optional<User> findByUserId(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error finding user: " + e.getMessage());
        }
        return Optional.empty();
    }

    public User createUser(String fullName, String userId, String pinHash, String role) throws SQLException {
        String sql = "INSERT INTO users (full_name, user_id, pin_hash, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, fullName);
            pstmt.setString(2, userId);
            pstmt.setString(3, pinHash);
            pstmt.setString(4, role);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    User newUser = new User();
                    newUser.setId(generatedKeys.getInt(1));
                    newUser.setFullName(fullName);
                    newUser.setUserId(userId);
                    newUser.setPinHash(pinHash);
                    newUser.setRole(role);
                    return newUser;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUserId(rs.getString("user_id"));
        user.setPinHash(rs.getString("pin_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(rs.getString("role"));
        return user;
    }
}