package com.msntt.MSAccountService.infraestructure.services;
import com.msntt.MSAccountService.application.exception.ResourceNotCreatedException;
import com.msntt.MSAccountService.domain.beans.AccountOperationDTO;
import com.msntt.MSAccountService.domain.beans.AccountTransferDTO;
import com.msntt.MSAccountService.domain.model.Transaction;
import com.msntt.MSAccountService.domain.enums.TransactionType;
import com.msntt.MSAccountService.domain.repository.AccountRepository;
import com.msntt.MSAccountService.domain.repository.TransactionRepository;
import com.msntt.MSAccountService.infraestructure.interfaces.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class TransactionService implements ITransactionService {

    //Services and Repositories
    @Autowired
    TransactionRepository trepository;
    @Autowired
    AccountRepository arepository;
    @Autowired
    AccountService accountService;

    //Crud
    @Override
    public Flux<Transaction> findAll() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Mono<Transaction> delete(String id) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Mono<Transaction> findById(String id) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Mono<ResponseEntity<Transaction>> update(String id, Transaction request) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Flux<Transaction> saveAll(List<Transaction> a) {
        // TODO Auto-generated method stub
        return null;
    }

    //Business Logic
    @Override
    public Mono<Transaction> doAccountDeposit(AccountOperationDTO dto) {

        return accountService.findById(dto.getAccount()).flatMap (a -> {
            a.setBalance(a.getBalance().add(dto.getAmount()));
            return arepository.save(a);
        })
        .then(Mono.just(dto)
                .flatMap(savedeposit))
        .switchIfEmpty(Mono.error(new ResourceNotCreatedException()));

    }
    @Override
    public Mono<Transaction> doAccountWithdrawal(AccountOperationDTO dto) {

        return accountService.findById(dto.getAccount()).then(Mono.just(dto)
                        .flatMap(savewithdrawal))
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException()));

    }
    @Override
    public Mono<Transaction> TransferBetweenAccounts(AccountTransferDTO dto) {
        return accountService.findById(dto.getFaccount())
                .filter(r -> r.getCodeBusinessPartner().equals(dto.getFbp()))
                .flatMap (a -> {
                    a.setBalance(a.getBalance().subtract(dto.getAmount()));
                    return arepository.save(a);})
                .then(accountService.findById(dto.getTaccount())
                        .flatMap( a -> {
                                    a.setBalance(a.getBalance().add(dto.getAmount()));
                                    return arepository.save(a);})
                .then(Mono.just(dto).flatMap(Savetransfertbtweenaccount)

                        .switchIfEmpty(Mono.error(new ResourceNotCreatedException()))));

    }
    @Override
    public Mono<Transaction> doTransferToThirdParty(AccountTransferDTO dto) {
        return accountService.findById(dto.getFaccount())
                .filter(r -> r.getCodeBusinessPartner().equals(dto.getFbp()))
                .flatMap (a -> {
                    a.setBalance(a.getBalance().subtract(dto.getAmount()));
                    return arepository.save(a);})
                .then(accountService.findById(dto.getTaccount())
                        .flatMap( a -> {
                            a.setBalance(a.getBalance().add(dto.getAmount()));
                            return arepository.save(a);})


                        .then(Mono.just(dto).flatMap(saveTransferToThirdParty)
                                .switchIfEmpty(Mono.error(new ResourceNotCreatedException()))));


    }

    //Functions
    private final Function<AccountOperationDTO, Mono<Transaction>> savedeposit = deposit -> {

        Transaction t;
        Mono<Transaction> _t;
        t = Transaction.builder()
                .amount(deposit.getAmount())
                .toaccount(deposit.getAccount())
                .transactiontype(TransactionType.DEPOSIT)
                .createDate(new Date()).build();

        _t = trepository.save(t);
      accountService.updateBalanceDp(t.getToaccount(), t.getAmount());
        return _t;
    };
    private final Function<AccountOperationDTO, Mono<Transaction>> savewithdrawal = withdrawal -> {

        Transaction t;
        Mono<Transaction> _t;
        t = Transaction.builder()
                .amount(withdrawal.getAmount())
                .toaccount(withdrawal.getAccount())
                .transactiontype(TransactionType.DEPOSIT)
                .createDate(new Date()).build();

        _t = trepository.save(t);
        accountService.updateBalanceWt(t.getToaccount(), t.getAmount());
        return _t;
    };
    private final Function<AccountTransferDTO, Mono<Transaction>> Savetransfertbtweenaccount = transfer -> {

        Transaction t;
        String a = transfer.getFaccount();
        String b = transfer.getTaccount();
        if (a.equals(b))
        {
            Mono.error(new ResourceNotCreatedException());
        }

        Mono<Transaction> _t;
        t = Transaction.builder()
                .amount(transfer.getAmount())
                .debit(transfer.getAmount())
                .credit(transfer.getAmount())
                .toaccount(b)
                .fromaccount(a)
                .transactiontype(TransactionType.SAME_HOLDER_TRANSFER)
                .createDate(new Date()).build();
        _t = trepository.save(t);
        return _t;
    };
    private final Function<AccountTransferDTO, Mono<Transaction>> saveTransferToThirdParty = transfer -> {

        Transaction t;
        String a = transfer.getFaccount();
        String b = transfer.getTaccount();
        if (a.equals(b))
        {
            Mono.error(new ResourceNotCreatedException());
        }

        Mono<Transaction> _t;
        t = Transaction.builder()
                .amount(transfer.getAmount())
                .debit(transfer.getAmount())
                .credit(transfer.getAmount())
                .toaccount(b)
                .fromaccount(a)
                .transactiontype(TransactionType.THIRD_PARTY_TRANSFER)
                .createDate(new Date()).build();
        _t = trepository.save(t);
        return _t;
    };

}






