package com.atm.service;

import java.math.BigDecimal;

import com.atm.dao.AccountDAO;
import com.atm.dao.TransactionDAO;
import com.atm.model.Account;

public class AccountService {
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;

    public AccountService(AccountDAO accountDAO, TransactionDAO transactionDAO) {
        this.accountDAO = accountDAO;
        this.transactionDAO = transactionDAO;
    }

    public Account getAccountDetails(String accountNumber) throws Exception {
        return accountDAO.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new Exception("Account " + accountNumber + " not found."));
    }

    public Account getAccountByUserId(int userId) throws Exception {
        return accountDAO.findByUserId(userId)
                .orElseThrow(() -> new Exception("Account for user ID " + userId + " not found."));
    }

    public void withdraw(String accountNumber, BigDecimal amount) throws Exception {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }

        Account account = getAccountDetails(accountNumber);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new Exception("Insufficient funds. Current balance: $" + account.getBalance());
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        accountDAO.updateBalance(accountNumber, newBalance);
        transactionDAO.logTransaction(account.getId(), "WITHDRAWAL", amount, null);
    }

    public void deposit(String accountNumber, BigDecimal amount) throws Exception {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }

        Account account = getAccountDetails(accountNumber);
        BigDecimal newBalance = account.getBalance().add(amount);
        accountDAO.updateBalance(accountNumber, newBalance);
        transactionDAO.logTransaction(account.getId(), "DEPOSIT", amount, null);
    }

    public Account createAccountForUser(int userId) throws Exception {
        // Generate a unique account number
        String accountNumber = "ACC" + (1000 + new java.util.Random().nextInt(9000));
        return accountDAO.createAccount(userId, accountNumber, "CHECKING", "ACTIVE", null);
    }

    public Account requestNewAccount(int customerId, int employeeId) throws Exception {
        // Generate a unique account number
        String accountNumber = "ACC" + (1000 + new java.util.Random().nextInt(9000));
        return accountDAO.createAccount(customerId, accountNumber, "CHECKING", "PENDING_ACTIVATION", employeeId);
    }

    public java.util.List<Account> getPendingAccounts() {
        return accountDAO.getPendingAccounts();
    }

    public void activateAccount(String accountNumber) throws Exception {
        Account account = getAccountDetails(accountNumber);
        if (!account.getStatus().equals("PENDING_ACTIVATION")) {
            throw new Exception("Account is not pending activation.");
        }
        accountDAO.updateAccountStatus(accountNumber, "ACTIVE");
    }
}