package com.msntt.MSAccountService.infraestructure.services;

import com.msntt.MSAccountService.application.exception.EntityNotExistsException;
import com.msntt.MSAccountService.application.exception.ResourceNotCreatedException;
import com.msntt.MSAccountService.domain.beans.DebitCardOperationDTO;
import com.msntt.MSAccountService.domain.enums.TransactionType;
import com.msntt.MSAccountService.domain.model.*;
import com.msntt.MSAccountService.domain.repository.DebitCardRepository;
import com.msntt.MSAccountService.domain.repository.TransactionRepository;
import com.msntt.MSAccountService.infraestructure.interfaces.IDebitCardTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
@Service
public class DebitCardTransactionService implements IDebitCardTransactionService {
    @Autowired
    private DebitCardRepository debitCardRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private YankiConsumer yankiConsumer;

    @Override
    public Mono<Transaction> doDebitCardPayment(DebitCardOperationDTO operationDTO) {

        Mono<DebitCard> debitCard = debitCardRepository
                .findByCardNumberAndExpiringDateAndCvv(operationDTO.getDebiCardNumber(),
                                                         operationDTO.getExpireDate(),
                                                         operationDTO.getCvv())
                .switchIfEmpty(Mono.error(new EntityNotExistsException("Debit doesn't exists")));

        return debitCard.map(DebitCard::getLinkedAccountList)
                .flatMap(getAccountForPayment)
                .flatMap(a->accountService.updateBalanceWithdrawal(a.getAccountNumber(),operationDTO.getAmount()))
                .flatMap(a->saveTransactionConsumption.apply(a,operationDTO))
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Transaction Error")));
    }

    @Override
    public Mono<Transaction> doDebitCardWithdrawal(DebitCardOperationDTO operationDTO) {

        Mono<DebitCard> debitCard = debitCardRepository.findById(operationDTO.getDebiCardNumber())
                .switchIfEmpty(Mono.error(new EntityNotExistsException("Debit doesn't exists")));

        return debitCard.map(DebitCard::getLinkedAccountList)
                .flatMap(getAccountForPayment)
                .flatMap(a->accountService.updateBalanceWithdrawal(a.getAccountNumber(),operationDTO.getAmount()))
                .flatMap(a->saveTransactionConsumption.apply(a,operationDTO))
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Transaction Error")));

    }

    private final Function<List<LinkedAccount>,Mono<Account>> getAccountForPayment = f->{

        LinkedAccount lnk = f.stream().filter(LinkedAccount::getIsMainAccount)
                .findAny().orElseThrow();

        return accountService.findById(lnk.getAccountId());
    };

    private final BiFunction<Account, DebitCardOperationDTO,Mono<Transaction>> saveTransactionConsumption = (a, dto) -> {

        Transaction t = Transaction.builder()
                .debit(dto.getAmount())
                .credit(BigDecimal.ZERO)
                .account(a.getAccountNumber())
                .debitCardId(dto.getDebiCardNumber())
                .transactiontype(TransactionType.DEBIT_CARD_CONSUMPTION)
                .commissionAmount(BigDecimal.ZERO)
                .createDate(LocalDateTime.now()).build();

        return accountService.updateBalanceWithdrawal(t.getAccount(), dto.getAmount())
                .doOnSuccess(r->
                {
                    Message M = Message.builder()
                            .amount(t.getDebit())
                            .referencia1(dto.getDebiCardNumber())
                            .referencia2(String.valueOf(t.getTransactiontype()))
                            .build();
                    yankiConsumer.sendMessage(M);
                })
                .then(transactionRepository.save(t));
    };

    private final BiFunction<Account, DebitCardOperationDTO,Mono<Transaction>> saveTransactionWithdrawal = (a, dto) -> {

        Transaction t = Transaction.builder()
                .debit(dto.getAmount())
                .account(a.getAccountNumber())
                .debitCardId(dto.getDebiCardNumber())
                .transactiontype(TransactionType.DEBIT_CARD_CONSUMPTION)
                .commissionAmount(BigDecimal.ZERO)
                .createDate(LocalDateTime.now()).build();

        return accountService.updateBalanceWithdrawal(t.getAccount(), dto.getAmount())
                .doOnSuccess(r->
                {
                    Message M = Message.builder()
                            .amount(t.getDebit())
                            .referencia1(dto.getDebiCardNumber())
                            .referencia2(String.valueOf(t.getTransactiontype()))
                            .build();
                    yankiConsumer.sendMessage(M);
                })
                .then(transactionRepository.save(t));
    };

}
