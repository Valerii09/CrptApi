package org.example.wallet.dto;

import org.example.wallet.model.OperationType;

import java.util.UUID;

public class WalletOperationRequest {

    private UUID walletId;
    private OperationType operationType;
    private long amount;

    public WalletOperationRequest() {
    }

    public WalletOperationRequest(UUID walletId, OperationType operationType, long amount) {
        this.walletId = walletId;
        this.operationType = operationType;
        this.amount = amount;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "WalletOperationRequest{" +
                "walletId=" + walletId +
                ", operationType=" + operationType +
                ", amount=" + amount +
                '}';
    }
}
