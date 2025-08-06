package org.example.wallet.repository;

import jakarta.persistence.LockModeType;
import org.example.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    @Modifying
    @Query("UPDATE Wallet w SET w.balance = w.balance + :amount WHERE w.id = :walletId")
    int updateBalance(@Param("walletId") UUID walletId, @Param("amount") long amount);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Wallet> findById(UUID id);
}
