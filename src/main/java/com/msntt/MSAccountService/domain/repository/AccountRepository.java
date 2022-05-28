package com.msntt.MSAccountService.domain.repository;
import com.msntt.MSAccountService.domain.model.Account;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collection;

public interface AccountRepository extends ReactiveMongoRepository<Account, String> {
	Mono<Long> countByAccountItemIdAndCodeBusinessPartner(String Tipo, String Code);

	Mono<Account> findFirstByAccountNumberInAndBalanceGreaterThanEqualOrderByDebitCardLinkDateAsc
					(Collection<String> accountNumberList, BigDecimal Balance);

}
