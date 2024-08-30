package com.shinhantime.tweione.Transaction.Service;


import com.shinhantime.tweione.Transaction.Repository.TransactionEntity;
import com.shinhantime.tweione.Transaction.Repository.TransactionRepository;
import com.shinhantime.tweione.Transaction.Repository.dto.TransactionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<TransactionDTO> getTransactionsByUserId(Long userId) {
        List<TransactionEntity> transactions = transactionRepository.findAllTransactionsByUserId(userId);

        // Convert entities to DTOs
        return transactions.stream()
                .map(TransactionDTO::fromEntity)
                .collect(Collectors.toList());
    }
}