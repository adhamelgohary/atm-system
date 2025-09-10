package com.atm.service;

import com.atm.dao.AccountRepository;
import com.atm.dao.TransactionRepository;
import com.atm.model.Account;
import com.atm.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public Account createAccountForUser(Long userId) {
        Account newAccount = new Account();
        newAccount.setUserId(userId);
        newAccount.setAccountNumber(generateAccountNumber());
        newAccount.setBalance(BigDecimal.ZERO);
        newAccount.setStatus("ACTIVE");
        return accountRepository.save(newAccount);
    }

    public Optional<Account> getAccountByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public Optional<Account> getAccountDetails(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Transactional
    public void deposit(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccountId(account.getId().intValue());
        transaction.setTransactionType("DEPOSIT");
        transaction.setAmount(amount);
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    @Transactional
    public void withdraw(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccountId(account.getId().intValue());
        transaction.setTransactionType("WITHDRAWAL");
        transaction.setAmount(amount);
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    public List<Account> getPendingAccounts() {
        return accountRepository.findByStatus("PENDING");
    }

    @Transactional
    public void activateAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setStatus("ACTIVE");
        accountRepository.save(account);
    }

    @Transactional
    public void requestNewAccount(Long customerId, Integer employeeId) {
        Account newAccount = new Account();
        newAccount.setUserId(customerId);
        newAccount.setAccountNumber(generateAccountNumber());
        newAccount.setBalance(BigDecimal.ZERO);
        newAccount.setStatus("PENDING");
        newAccount.setRequestedBy(employeeId);
        accountRepository.save(newAccount);
    }

    private String generateAccountNumber() {
        // Simple account number generation logic
        return String.valueOf(System.currentTimeMillis());
    }
}
