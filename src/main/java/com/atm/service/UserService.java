package com.atm.service;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

import com.atm.dao.UserDAO;
import com.atm.model.User;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Finds a user by their user ID.
     * This is the first step of authentication.
     * @param userId The user ID to search for.
     * @return An Optional containing the User if found, otherwise an empty Optional.
     */
    public Optional<User> findUserByUserId(String userId) {
        return userDAO.findByUserId(userId);
    }

    /**
     * Verifies if the provided plain text PIN matches the stored hash for a given user.
     * This is the second step of authentication.
     * @param user The User object (which contains the hash).
     * @param plainTextPin The PIN entered by the user.
     * @return true if the PIN is correct, false otherwise.
     */
    public boolean isPinCorrect(User user, String plainTextPin) {
        if (user == null || user.getPinHash() == null || plainTextPin == null) {
            return false;
        }
        return BCrypt.checkpw(plainTextPin, user.getPinHash());
    }
}