package com.atm.service;

import com.atm.dao.AccountDAO;
import com.atm.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountDAO accountDAO;

    @InjectMocks
    private AccountService accountService;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(1);
        account.setUserId(1);
        account.setAccountNumber("ACC1001");
        account.setBalance(new BigDecimal("1000.00"));
    }

    @Test
    void getAccountByUserId_shouldReturnAccount_whenFound() throws Exception {
        when(accountDAO.findByUserId(1)).thenReturn(Optional.of(account));

        Account foundAccount = accountService.getAccountByUserId(1);

        assertNotNull(foundAccount);
        assertEquals("ACC1001", foundAccount.getAccountNumber());
    }

    @Test
    void getAccountByUserId_shouldThrowException_whenNotFound() {
        when(accountDAO.findByUserId(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            accountService.getAccountByUserId(1);
        });

        String expectedMessage = "Account for user ID 1 not found.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
