package hse.payments.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import hse.payments.domains.Account;

public interface AccountsRepository extends JpaRepository<Account, Integer> {

    @Modifying
    @Query("UPDATE Account a SET a.money = a.money + :amount WHERE a.userId = :userId")
    int depositMoney(@Param("userId") Integer userId, @Param("amount") Double amount);
    
    @Modifying
    @Query("UPDATE Account a SET a.money = a.money - :amount "
           + "WHERE a.userId = :userId AND a.money >= :amount")
    int withdrawMoney(@Param("userId") Integer userId, @Param("amount") Double amount);

}