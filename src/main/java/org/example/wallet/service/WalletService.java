package org.example.wallet.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.wallet.dto.WalletOperationRequest;
import org.example.wallet.dto.WalletResponse;
import org.example.wallet.model.OperationType;
import org.example.wallet.model.Wallet;
import org.example.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public WalletResponse processOperation(WalletOperationRequest request) {
        validateRequest(request);

        UUID walletId = request.getWalletId();

        // Создать кошелек, если его нет
        walletRepository.findById(walletId).orElseGet(() -> createNewWallet(walletId));

        int updated = 0;
        if (request.getOperationType() == OperationType.DEPOSIT) {
            updated = walletRepository.updateBalance(walletId, request.getAmount());
        } else if (request.getOperationType() == OperationType.WITHDRAW) {
            Wallet wallet = walletRepository.findById(walletId)
                    .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

            if (wallet.getBalance() < request.getAmount()) {
                throw new IllegalArgumentException("Not enough funds");
            }

            updated = walletRepository.updateBalance(walletId, -request.getAmount());
        }

        if (updated == 0) {
            throw new IllegalStateException("Failed to update wallet");
        }

        Wallet updatedWallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        return new WalletResponse(updatedWallet.getId(), updatedWallet.getBalance());
    }

    @Transactional(readOnly = true)
    public WalletResponse getBalance(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));
        return new WalletResponse(wallet.getId(), wallet.getBalance());
    }

    private void validateRequest(WalletOperationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request must not be null");
        }

        if (Objects.isNull(request.getWalletId())) {
            throw new IllegalArgumentException("Wallet ID must not be null");
        }

        if (Objects.isNull(request.getOperationType())) {
            throw new IllegalArgumentException("Operation type must not be null");
        }

        if (request.getAmount() < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
    }

    private Wallet createNewWallet(UUID walletId) {
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(0L);
        return walletRepository.save(wallet);
    }
}
