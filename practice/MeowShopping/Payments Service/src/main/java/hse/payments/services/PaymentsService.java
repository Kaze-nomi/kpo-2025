package hse.payments.services;

import hse.payments.domains.Account;
import hse.payments.repositories.AccountsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentsService {

    private final AccountsRepository accountsRepository;

    @Transactional
    public Account createAccount(Integer userId) {
        if (accountsRepository.existsById(userId)) {
            throw new RuntimeException("Аккаунт уже существует для пользователя с ID: " + userId);
        }

        Account account = new Account();
        account.setUserId(userId);
        account.setMoney(0.0);
        
        try {
            return accountsRepository.save(account);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания аккаунта: " + e.getMessage());
        }
    }
    
    @Transactional
    @Retryable(value = { 
            ObjectOptimisticLockingFailureException.class,
            DataAccessResourceFailureException.class,
            TransientDataAccessException.class 
        }, 
        maxAttempts = 3,
        backoff = @Backoff(delay = 100, multiplier = 2.0)
    )
    public void deposit(Integer userId, double amount) {
        if (!accountsRepository.existsById(userId)) {
            throw new RuntimeException("Аккаунт не найден для пользователя с ID: " + userId);
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Количество денег должно быть положительным");
        }
        
        int updated = accountsRepository.depositMoney(userId, amount);
        if (updated == 0) {
            throw new RuntimeException("Ошибка пополнения баланса");
        }
    }

    // Нужна ли тут @Version? И как она вообще может меняться если операции в БД атомарны?
    // Exactly once ли withdraw и как вообще возможно сделать реальный exactly once? Нам же в любом случае нужно чтобы хотя бы БД или сама Кафка были живы, иначе мы потеряем запрос на снятие денег.

    @Transactional
    @Retryable(value = { 
            ObjectOptimisticLockingFailureException.class,
            DataAccessResourceFailureException.class,
            TransientDataAccessException.class 
        }, 
        maxAttempts = 3,
        backoff = @Backoff(delay = 100, multiplier = 2.0)
    )
    public boolean withdraw(Integer userId, double amount) {
        if (!accountsRepository.existsById(userId)) {
            return false;
        }

        if (amount <= 0) {
            return false;
        }
        
        int updated = accountsRepository.withdrawMoney(userId, amount);
        
        if (updated == 0) {
            return false;
        }
        
        return true;
    }

    @Transactional(readOnly = true)
    public double getBalance(Integer userId) {
        return accountsRepository.findById(userId)
                .map(Account::getMoney)
                .orElseThrow(() -> new RuntimeException("Аккаунт не найден для пользователя с ID: " + userId));
    }

}