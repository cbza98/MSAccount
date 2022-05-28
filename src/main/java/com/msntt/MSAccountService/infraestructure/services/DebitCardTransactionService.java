package com.msntt.MSAccountService.infraestructure.services;

import com.msntt.MSAccountService.application.exception.EntityNotExistsException;
import com.msntt.MSAccountService.application.exception.ResourceNotCreatedException;
import com.msntt.MSAccountService.domain.beans.DebitCardPayDTO;
import com.msntt.MSAccountService.domain.enums.TransactionType;
import com.msntt.MSAccountService.domain.model.Account;
import com.msntt.MSAccountService.domain.model.DebitCard;
import com.msntt.MSAccountService.domain.model.LinkedAccount;
import com.msntt.MSAccountService.domain.model.Transaction;
import com.msntt.MSAccountService.domain.repository.DebitCardRepository;
import com.msntt.MSAccountService.domain.repository.TransactionRepository;
import com.msntt.MSAccountService.infraestructure.interfaces.IDebitCardTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DebitCardTransactionService implements IDebitCardTransactionService {
    @Autowired
    private DebitCardRepository debitCardRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountService accountService;

    @Override
    public Mono<Transaction> doDebitCardPaymentOrWithdrawal(DebitCardPayDTO dbCardPayDTO) {

        Mono<DebitCard> debitCard = debitCardRepository.findById(dbCardPayDTO.getDebiCardNumber())
                .switchIfEmpty(Mono.error(new EntityNotExistsException("Debit doesn't exists")));

        return debitCard.map(DebitCard::getLinkedAccountList)
                .flatMap(getAccountForPayment)
                .flatMap(a->accountService.updateBalanceWithdrawal(a.getAccountNumber(),dbCardPayDTO.getPayAmount()))
                .flatMap(a->saveTransaction.apply(a,dbCardPayDTO))
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Transaction Error")));
    }
    private final Function<List<LinkedAccount>,Mono<Account>> getAccountForPayment = f->{

        LinkedAccount lnk = f.stream().filter(LinkedAccount::getIsMainAccount)
                .findAny().orElseThrow();

        return accountService.findById(lnk.getAccountId());
    };

    private final BiFunction<Account,DebitCardPayDTO,Mono<Transaction>> saveTransaction = (a,dto) -> {

        Transaction t = Transaction.builder()
                .debit(dto.getPayAmount())
                .account(a.getAccountNumber())
                .debitCardId(dto.getDebiCardNumber())
                .transactiontype(TransactionType.DEBIT_CARD_CONSUMPTION)
                .commissionAmount(BigDecimal.ZERO)
                .createDate(LocalDateTime.now()).build();

        return accountService.updateBalanceWithdrawal(t.getAccount(), t.getAmount())
                .then(transactionRepository.save(t));
    };
}
