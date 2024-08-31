package com.shinhantime.tweione.Transaction.Repository;

import com.shinhantime.tweione.Transaction.Repository.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    // 특정 사용자가 보낸 또는 받은 모든 거래 내역을 시간순으로 가져오기
    @Query("SELECT t FROM transaction t WHERE t.fromUser.id = :userId OR t.toUser.id = :userId ORDER BY t.transactionDate DESC")
    List<TransactionEntity> findAllTransactionsByUserId(@Param("userId") Long userId);
}