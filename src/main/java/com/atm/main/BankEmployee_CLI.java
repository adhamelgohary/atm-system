package com.atm.main;

import com.atm.dao.AccountDAO;
import com.atm.dao.TransactionDAO;
import com.atm.dao.UserDAO;
import com.atm.model.User;
import com.atm.service.AccountService;
import com.atm.service.UserService;

import java.util.Optional;
import java.util.Scanner;

public class BankEmployee_CLI {
    private static final UserDAO userDAO = new UserDAO();
    private static final AccountDAO accountDAO = new AccountDAO();
    private static final TransactionDAO transactionDAO = new TransactionDAO();
    private static final UserService userService = new UserService(userDAO);
    private static final AccountService accountService = new AccountService(accountDAO, transactionDAO);
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to the Bank Employee Portal!");

        // Employee Login
        System.out.print("\nEnter Employee User ID: ");
        String userId = scanner.nextLine();
        Optional<User> userOptional = userService.findUserByUserId(userId);

        if (userOptional.isEmpty() || !isEmployee(userOptional.get())) {
            System.out.println("Invalid Employee User ID or you do not have employee access.");
            return;
        }

        User employee = userOptional.get();

        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();

        if (!userService.isPinCorrect(employee, pin)) {
            System.out.println("Incorrect PIN.");
            return;
        }

        System.out.println("\nLogin Successful! Welcome, " + employee.getFullName());

        // Show menu based on role
        if (employee.getRole().equals("MANAGER")) {
            showManagerMenu(employee);
        } else {
            showEmployeeMenu(employee);
        }
    }

    private static void showEmployeeMenu(User employee) {
        while (true) {
            System.out.println("\n--- Employee Menu ---");
            System.out.println("1. Request New Customer Account");
            System.out.println("2. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> requestNewCustomerAccount(employee);
                case "2" -> {
                    System.out.println("Logging out. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void showManagerMenu(User manager) {
        while (true) {
            System.out.println("\n--- Manager Menu ---");
            System.out.println("1. Request New Customer Account");
            System.out.println("2. List Pending Accounts");
            System.out.println("3. Activate Account");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> requestNewCustomerAccount(manager);
                case "2" -> listPendingAccounts();
                case "3" -> activateAccount();
                case "4" -> {
                    System.out.println("Logging out. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void requestNewCustomerAccount(User employee) {
        System.out.println("\n--- New Customer Account Request ---");
        System.out.print("Enter customer's full name: ");
        String fullName = scanner.nextLine();
        System.out.print("Enter a new user ID for the customer: ");
        String newUserId = scanner.nextLine();
        System.out.print("Enter a temporary PIN for the customer: ");
        String newPin = scanner.nextLine();

        try {
            User newCustomer = userService.registerUser(fullName, newUserId, newPin);
            accountService.requestNewAccount(newCustomer.getId(), employee.getId());
            System.out.println("\nAccount activation request submitted successfully for user: " + newCustomer.getFullName());
        } catch (Exception e) {
            System.err.println("\nERROR: Failed to submit account activation request. " + e.getMessage());
        }
    }

    private static void listPendingAccounts() {
        System.out.println("\n--- Pending Account Activations ---");
        java.util.List<com.atm.model.Account> pendingAccounts = accountService.getPendingAccounts();
        if (pendingAccounts.isEmpty()) {
            System.out.println("There are no pending account activations.");
        } else {
            for (com.atm.model.Account account : pendingAccounts) {
                System.out.println("Account Number: " + account.getAccountNumber() + ", User ID: " + account.getUserId());
            }
        }
    }

    private static void activateAccount() {
        System.out.print("\nEnter the account number to activate: ");
        String accountNumber = scanner.nextLine();
        try {
            accountService.activateAccount(accountNumber);
            System.out.println("Account " + accountNumber + " has been activated successfully.");
        } catch (Exception e) {
            System.err.println("\nERROR: Failed to activate account. " + e.getMessage());
        }
    }

    private static boolean isEmployee(User user) {
        return user.getRole().equals("EMPLOYEE") || user.getRole().equals("MANAGER");
    }
}
