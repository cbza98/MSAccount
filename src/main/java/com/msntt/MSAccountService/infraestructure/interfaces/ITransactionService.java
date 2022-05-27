package com.msntt.MSAccountService.infraestructure.interfaces;

import com.msntt.MSAccountService.domain.beans.AccountOperationDTO;
import com.msntt.MSAccountService.domain.beans.AccountTransferDTO;
import com.msntt.MSAccountService.domain.model.Transaction;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ITransactionService {
	
	Flux<Transaction> findAll();
	
	Mono<Transaction> delete(String id);

	Mono<Transaction> findById(String id);
	
	Mono<ResponseEntity<Transaction>> update(String id, Transaction request);
	
	Flux<Transaction> saveAll(List<Transaction> a);

    Mono<Transaction> doAccountWithdrawal(AccountOperationDTO dto);
    
    Mono<Transaction> doAccountDeposit(AccountOperationDTO dto);
	
	Mono<Transaction> TransferBetweenAccounts(AccountTransferDTO dto);
	
	Mono<Transaction> doTransferToThirdParty(AccountTransferDTO dto);
}
