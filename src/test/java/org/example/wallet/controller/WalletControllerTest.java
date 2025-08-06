package org.example.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.wallet.dto.WalletOperationRequest;
import org.example.wallet.model.OperationType;
import org.example.wallet.model.Wallet;
import org.example.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID walletId;

    @BeforeEach
    public void setup() {
        walletRepository.deleteAll();
        walletId = UUID.randomUUID();
    }

    @Test
    public void testDeposit() throws Exception {
        WalletOperationRequest request = new WalletOperationRequest(walletId, OperationType.DEPOSIT, 1000);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(walletId.toString()))
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    public void testWithdrawSuccess() throws Exception {
        // сначала пополним
        walletRepository.save(new Wallet() {{
            setId(walletId);
            setBalance(1500L);
        }});

        WalletOperationRequest withdrawRequest = new WalletOperationRequest(walletId, OperationType.WITHDRAW, 500);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    public void testWithdrawInsufficientFunds() throws Exception {
        walletRepository.save(new Wallet() {{
            setId(walletId);
            setBalance(100L);
        }});

        WalletOperationRequest withdrawRequest = new WalletOperationRequest(walletId, OperationType.WITHDRAW, 500);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not enough funds"));
    }

    @Test
    public void testGetBalance() throws Exception {
        walletRepository.save(new Wallet() {{
            setId(walletId);
            setBalance(2500L);
        }});

        mockMvc.perform(get("/api/v1/wallets/" + walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(walletId.toString()))
                .andExpect(jsonPath("$.balance", is(2500)));
    }

    @Test
    public void testGetBalance_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/wallets/" + UUID.randomUUID()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Wallet not found"));
    }
}
