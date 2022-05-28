package com.msntt.MSAccountService.infraestructure.interfaces;

import com.msntt.MSAccountService.domain.beans.DebitCardOperationDTO;
import com.msntt.MSAccountService.domain.model.Transaction;
import reactor.core.publisher.Mono;

public interface IDebitCardTransactionService {
    Mono<Transaction> doDebitCardPayment(DebitCardOperationDTO operationDTO);

    Mono<Transaction> doDebitCardWithdrawal(DebitCardOperationDTO operationDTO);

}
