package com.msntt.MSAccountService.infraestructure.interfaces;

import com.msntt.MSAccountService.domain.beans.DebitCardPayDTO;
import com.msntt.MSAccountService.domain.model.Transaction;
import reactor.core.publisher.Mono;

public interface IDebitCardTransactionService {
    Mono<Transaction> doDebitCardPaymentOrWithdrawal(DebitCardPayDTO dbCardPayDTO);

}
