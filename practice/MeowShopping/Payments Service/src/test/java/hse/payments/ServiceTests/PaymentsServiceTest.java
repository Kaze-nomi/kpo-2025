package hse.payments.ServiceTests;

import hse.payments.domains.Account;
import hse.payments.repositories.AccountsRepository;
import hse.payments.services.PaymentsService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentsServiceTest {

    @Mock
    private AccountsRepository accountsRepository;

    @InjectMocks
    private PaymentsService paymentsService;

    @Test
    void createAccount_Success() {
        paymentsService.createAccount(123);
        verify(accountsRepository).save(argThat(account -> account.getUserId() == 123 && account.getMoney() == 0.0));
    }

    @Test
    void deposit_Success() {
        when(accountsRepository.existsById(123)).thenReturn(true);
        when(accountsRepository.depositMoney(123, 50.0)).thenReturn(1);
        paymentsService.deposit(123, 50.0);
        verify(accountsRepository).depositMoney(123, 50.0);
    }

    @Test
    void withdraw_Success() {
        when(accountsRepository.existsById(123)).thenReturn(true);
        when(accountsRepository.withdrawMoney(123, 30.0)).thenReturn(1);
        assertTrue(paymentsService.withdraw(123, 30.0));
    }

    @Test
    void withdraw_InsufficientFunds() {
        when(accountsRepository.existsById(123)).thenReturn(true);
        when(accountsRepository.withdrawMoney(123, 100.0)).thenReturn(0);
        assertFalse(paymentsService.withdraw(123, 100.0));
    }

    @Test
    void getBalance_Success() {
        Account account = new Account();
        account.setMoney(200.0);
        when(accountsRepository.findById(123)).thenReturn(Optional.of(account));

        assertEquals(200.0, paymentsService.getBalance(123));
    }
    
}