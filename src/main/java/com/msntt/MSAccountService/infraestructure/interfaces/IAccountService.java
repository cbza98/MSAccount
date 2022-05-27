package com.msntt.MSAccountService.infraestructure.interfaces;

import com.msntt.MSAccountService.domain.beans.CreateAccountDTO;
import com.msntt.MSAccountService.domain.beans.HolderDTO;
import com.msntt.MSAccountService.domain.beans.SignerDTO;
import com.msntt.MSAccountService.domain.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public interface IAccountService {
	Flux<Account> findAll();
	Mono<Account> createAccount(CreateAccountDTO account);
	Mono<Account> delete(String id);
	Mono<Account> findById(String id);
	Flux<Account> saveAll(List<Account> a);
	Mono<Account> update(Account request);
	Mono<Account> updateBalanceDp(String id, BigDecimal balance);
	Mono<Account> updateBalanceWt(String id, BigDecimal balance);
	Mono<Account> addHolder(HolderDTO holder);
	Mono<Account> addSigner(SignerDTO signer);

}
