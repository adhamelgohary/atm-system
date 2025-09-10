package com.atm.controller;

import com.atm.model.Account;
import com.atm.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employee")
public class BankEmployeeController {

    private final AccountService accountService;

    @Autowired
    public BankEmployeeController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/request-account")
    public ResponseEntity<?> requestNewAccount(@RequestBody Map<String, Object> payload) {
        Long customerId = Long.valueOf(payload.get("customerId").toString());
        Integer employeeId = Integer.valueOf(payload.get("employeeId").toString());

        try {
            accountService.requestNewAccount(customerId, employeeId);
            return ResponseEntity.ok("Account request submitted");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/pending-accounts")
    public List<Account> getPendingAccounts() {
        return accountService.getPendingAccounts();
    }

    @PostMapping("/activate-account")
    public ResponseEntity<?> activateAccount(@RequestBody Map<String, String> payload) {
        String accountNumber = payload.get("accountNumber");
        try {
            accountService.activateAccount(accountNumber);
            return ResponseEntity.ok("Account activated");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
