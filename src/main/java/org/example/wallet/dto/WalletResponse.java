package org.example.wallet.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class WalletResponse {
    private UUID walletId;
    private long balance;

    public WalletResponse(UUID walletId, long balance) {
        this.walletId = walletId;
        this.balance = balance;
    }
}
