package com.atm.service;

import com.atm.dao.UserRepository;
import com.atm.model.User;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String fullName, String userId, String pin) {
        String pinHash = BCrypt.hashpw(pin, BCrypt.gensalt());
        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setUserId(userId);
        newUser.setPinHash(pinHash);
        newUser.setRole("CUSTOMER");
        return userRepository.save(newUser);
    }

    public Optional<User> findUserByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    public boolean isPinCorrect(User user, String pin) {
        return BCrypt.checkpw(pin, user.getPinHash());
    }
}
