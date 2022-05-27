package com.msntt.MSAccountService.domain.repository;


import com.msntt.MSAccountService.domain.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {
}
