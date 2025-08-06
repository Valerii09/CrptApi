package org.example.wallet.controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.wallet.dto.WalletOperationRequest;
import org.example.wallet.dto.WalletResponse;
import org.example.wallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> operate(@RequestBody WalletOperationRequest request) {
        WalletResponse response = walletService.processOperation(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> getBalance(@PathVariable UUID walletId) {
        WalletResponse response = walletService.getBalance(walletId);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler({IllegalArgumentException.class, EntityNotFoundException.class})
    public ResponseEntity<String> handleBadRequest(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
