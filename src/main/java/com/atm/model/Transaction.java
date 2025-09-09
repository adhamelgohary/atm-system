package com.atm.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private int id;
    private int accountId;
    private String transactionType;
    private BigDecimal amount;
    private LocalDateTime transactionDate;

    // Constructors, getters, and setters

    public int getId() {
        return id;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
