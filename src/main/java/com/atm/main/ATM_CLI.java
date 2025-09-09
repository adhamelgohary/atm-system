package com.atm.main;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Scanner;

import com.atm.dao.AccountDAO;
import com.atm.dao.TransactionDAO;
import com.atm.dao.UserDAO;
import com.atm.model.Account;
import com.atm.model.User;
import com.atm.service.AccountService;
import com.atm.service.UserService;

public class ATM_CLI {
    private static final UserDAO userDAO = new UserDAO();
    private static final AccountDAO accountDAO = new AccountDAO();
    private static final TransactionDAO transactionDAO = new TransactionDAO();
    private static final UserService userService = new UserService(userDAO);
    private static final AccountService accountService = new AccountService(accountDAO, transactionDAO);
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to the Scalable ATM System!");
        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> handleLogin();
                case "2" -> handleRegistration();
                case "3" -> {
                    System.out.println("Thank you for using the ATM. Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void handleLogin() {
        System.out.print("\nEnter User ID: ");
        String userId = scanner.nextLine();

        Optional<User> userOptional = userService.findUserByUserId(userId);

        if (userOptional.isEmpty()) {
            System.out.println("User ID not found. Please try again.");
            return; // Return to main menu
        }

        User user = userOptional.get();

        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine();

        if (userService.isPinCorrect(user, pin)) {
            System.out.println("\nLogin Successful! Welcome, " + user.getFullName());
            try {
                Account account = accountService.getAccountByUserId(user.getId());
                showAccountMenu(account.getAccountNumber());
            } catch (Exception e) {
                System.err.println("\nERROR: Could not retrieve account details. " + e.getMessage());
            }
        } else {
            System.out.println("Incorrect PIN. Please try again.");
        }
    }

    private static void handleRegistration() {
        System.out.println("\n--- New User Registration ---");
        System.out.print("Enter your full name: ");
        String fullName = scanner.nextLine();
        System.out.print("Enter a new user ID: ");
        String newUserId = scanner.nextLine();
        System.out.print("Enter a new PIN: ");
        String newPin = scanner.nextLine();

        try {
            User newUser = userService.registerUser(fullName, newUserId, newPin);
            System.out.println("\nRegistration successful for user: " + newUser.getFullName());

            Account newAccount = accountService.createAccountForUser(newUser.getId());
            System.out.println("Your new account has been created.");
            System.out.println("Account Number: " + newAccount.getAccountNumber());
            System.out.println("You can now log in with your new credentials.");

        } catch (Exception e) {
            System.err.println("\nERROR: Registration failed. " + e.getMessage());
        }
    }

    private static void showAccountMenu(String accountNumber) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n--- ATM Menu for Account " + accountNumber + " ---");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1" -> handleCheckBalance(accountNumber);
                    case "2" -> handleDeposit(accountNumber);
                    case "3" -> handleWithdrawal(accountNumber);
                    case "4" -> {
                        System.out.println("Logging out. Thank you for using the ATM!");
                        loggedIn = false;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.err.println("\nERROR: " + e.getMessage());
            }
        }
    }

    private static void handleCheckBalance(String accountNumber) throws Exception {
        Account account = accountService.getAccountDetails(accountNumber);
        System.out.println("---------------------------------");
        System.out.printf("Your current balance is: $%.2f%n", account.getBalance());
        System.out.println("---------------------------------");
    }

    private static void handleWithdrawal(String accountNumber) throws Exception {
        System.out.print("Enter amount to withdraw: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());
        accountService.withdraw(accountNumber, amount);
        System.out.println("Withdrawal successful. Please take your cash.");
        handleCheckBalance(accountNumber);
    }

    private static void handleDeposit(String accountNumber) throws Exception {
        System.out.print("Enter amount to deposit: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());
        accountService.deposit(accountNumber, amount);
        System.out.println("Deposit successful.");
        handleCheckBalance(accountNumber);
    }
}