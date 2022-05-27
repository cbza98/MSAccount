package com.msntt.MSAccountService.domain.repository;

import com.msntt.MSAccountService.domain.model.DebitCard;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
public interface DebitCardRepository extends ReactiveMongoRepository<DebitCard,String> {
}
