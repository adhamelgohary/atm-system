package com.atm.controller;

import com.atm.model.Account;
import com.atm.model.User;
import com.atm.service.AccountService;
import com.atm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/atm")
public class AtmController {

    private final UserService userService;
    private final AccountService accountService;

    @Autowired
    public AtmController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> payload) {
        String fullName = payload.get("fullName");
        String userId = payload.get("userId");
        String pin = payload.get("pin");

        if (fullName == null || userId == null || pin == null) {
            return ResponseEntity.badRequest().body("Missing required fields");
        }

        try {
            User newUser = userService.registerUser(fullName, userId, pin);
            accountService.createAccountForUser(newUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String userId = payload.get("userId");
        String pin = payload.get("pin");

        Optional<User> userOptional = userService.findUserByUserId(userId);
        if (userOptional.isEmpty() || !userService.isPinCorrect(userOptional.get(), pin)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        return ResponseEntity.ok("Login successful");
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<?> getBalance(@PathVariable String accountNumber) {
        Optional<Account> accountOptional = accountService.getAccountDetails(accountNumber);
        if (accountOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("balance", accountOptional.get().getBalance()));
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<?> deposit(@PathVariable String accountNumber, @RequestBody Map<String, BigDecimal> payload) {
        BigDecimal amount = payload.get("amount");
        try {
            accountService.deposit(accountNumber, amount);
            return ResponseEntity.ok("Deposit successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<?> withdraw(@PathVariable String accountNumber, @RequestBody Map<String, BigDecimal> payload) {
        BigDecimal amount = payload.get("amount");
        try {
            accountService.withdraw(accountNumber, amount);
            return ResponseEntity.ok("Withdrawal successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
