package com.msntt.MSAccountService.domain.repository;
import com.msntt.MSAccountService.domain.entities.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveMongoRepository<Account, String> {
	Mono<Long> countByAccountTypeAndCodeBusinessPartner(String Tipo, String Code);
	Mono<Account> findByAccountNumber(String number);

}
