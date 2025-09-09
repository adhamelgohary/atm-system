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
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUserId(rs.getString("user_id"));
                user.setPinHash(rs.getString("pin_hash"));
                user.setFullName(rs.getString("full_name"));
                return Optional.of(user);
            }
        } catch (SQLException e) {
            System.err.println("Database error finding user: " + e.getMessage());
        }
        return Optional.empty();
    }
}