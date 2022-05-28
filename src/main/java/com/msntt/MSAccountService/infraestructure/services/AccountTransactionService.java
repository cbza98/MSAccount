package com.msntt.MSAccountService.infraestructure.services;
import com.msntt.MSAccountService.application.exception.EntityNotExistsException;
import com.msntt.MSAccountService.application.exception.ResourceNotCreatedException;
import com.msntt.MSAccountService.domain.beans.AccountOperationDTO;
import com.msntt.MSAccountService.domain.beans.AccountTransferDTO;
import com.msntt.MSAccountService.domain.model.Account;
import com.msntt.MSAccountService.domain.model.Transaction;
import com.msntt.MSAccountService.domain.enums.TransactionType;
import com.msntt.MSAccountService.domain.repository.AccountRepository;
import com.msntt.MSAccountService.domain.repository.TransactionRepository;
import com.msntt.MSAccountService.infraestructure.interfaces.IAccountService;
import com.msntt.MSAccountService.infraestructure.interfaces.IAccountTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;

@Service
public class AccountTransactionService implements IAccountTransactionService {

    //Services and Repositories
    @Autowired
    TransactionRepository tRepository;
    @Autowired
    AccountRepository aRepository;
    @Autowired
    IAccountService accountService;

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
        Mono<Long> countTransactions = countByAccountAndTransactionType(dto.getAccount());
        Mono<Account> account = accountService.findById(dto.getAccount())
                .switchIfEmpty(Mono.error(new EntityNotExistsException("Account doesn't exists")));

        return  Mono.zip(account,countTransactions,Mono.just(dto))
                .flatMap(saveDeposit)
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException()));
    }
    @Override
    public Mono<Transaction> doAccountWithdrawal(AccountOperationDTO dto) {
        Mono<Long> countTransactions = countByAccountAndTransactionType(dto.getAccount());
        Mono<Account> account = accountService.findById(dto.getAccount())
                .switchIfEmpty(Mono.error(new EntityNotExistsException("Account doesn't exists")));

        return Mono.zip(account,countTransactions,Mono.just(dto))
                .flatMap(saveWithdrawal)
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException()));
    }
    @Override
    public Mono<Transaction> TransferBetweenAccounts(AccountTransferDTO dto) {
        Mono<Account> fromAccount = aRepository.findById(dto.getFaccount())
                .switchIfEmpty(Mono.error(new EntityNotExistsException("Origin account doesn't exists")));
        Mono<Account> toAccount = aRepository.findById(dto.getTaccount())
                .switchIfEmpty(Mono.error(new EntityNotExistsException("Destiny account doesn't exists")));

        return  Mono.zip(fromAccount,toAccount)
                .filter(a-> !(dto.getFaccount().equals(dto.getTaccount())))
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Account cannot be the same")))
                .filter(a->a.getT1().getCodeBusinessPartner().equals(a.getT2().getCodeBusinessPartner()))
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Holders are distinct")))
                .flatMap(a-> accountService.updateBalanceWithdrawal(a.getT1().getAccountNumber(), dto.getAmount())
                                .thenReturn(a))
                .flatMap(a-> accountService.updateBalanceDeposit(a.getT2().getAccountNumber(), dto.getAmount())
                                .thenReturn(a))
                .then(Mono.just(dto)).flatMap(saveTransactionSameHolder)
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Transaction error")));
    }
    @Override
    public Mono<Transaction> doTransferToThirdParty(AccountTransferDTO dto) {
        Mono<Account> fromAccount = aRepository.findById(dto.getFaccount())
                .switchIfEmpty(Mono.error(new EntityNotExistsException("Origin account doesn't exists")));
        Mono<Account> toAccount = aRepository.findById(dto.getTaccount())
                .switchIfEmpty(Mono.error(new EntityNotExistsException("Destiny account doesn't exists")));

        return  Mono.zip(fromAccount,toAccount)
                .filter(a-> !(dto.getFaccount().equals(dto.getTaccount())))
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Account cannot be the same")))
                .flatMap(a-> accountService.updateBalanceWithdrawal(a.getT1().getAccountNumber(), dto.getAmount())
                        .thenReturn(a))
                .flatMap(a-> accountService.updateBalanceDeposit(a.getT2().getAccountNumber(), dto.getAmount())
                        .thenReturn(a))
                .then(Mono.just(dto)).flatMap(saveTransactionThirdParty)
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Transaction error")));

    }
    @Override
    public Mono<Long> countByAccountAndTransactionType(String accountNumber) {

        LocalDate start = YearMonth.now().atDay(1);
        LocalDate end = YearMonth.now().atEndOfMonth();

        Collection<String> operations = new ArrayList<>();
        operations.add("DEPOSIT");
        operations.add("WITHDRAWAL");

        return tRepository.countByAccountAndTransactiontypeInAndCreateDateBetween(accountNumber,
                                                                                  operations,
                                                                                  start,
                                                                                  end);
    }

    //Functions
    private final Function<Tuple3<Account,Long,AccountOperationDTO>, Mono<Transaction>> saveDeposit = tuple3 -> {
        BigDecimal c = (tuple3.getT2()>=tuple3.getT1().getLimitTransaction())
                ? tuple3.getT1().getCommission(): BigDecimal.ZERO;

        Transaction t = Transaction.builder()
                        .amount(tuple3.getT3().getAmount())
                        .account(tuple3.getT3().getAccount())
                        .transactiontype(TransactionType.DEPOSIT)
                        .commissionAmount(c)
                        .createDate(LocalDateTime.now()).build();

        return accountService.updateBalanceDeposit(t.getAccount(), t.getAmount().subtract(c))
                .then(tRepository.save(t));
    };
    private final Function<Tuple3<Account,Long,AccountOperationDTO>, Mono<Transaction>> saveWithdrawal = tuple3 -> {
        BigDecimal c = (tuple3.getT2()>=tuple3.getT1().getLimitTransaction())
                        ? tuple3.getT1().getCommission(): BigDecimal.ZERO;

        Transaction t = Transaction.builder()
                        .amount(tuple3.getT3().getAmount())
                        .account(tuple3.getT3().getAccount())
                        .transactiontype(TransactionType.WITHDRAWAL)
                        .commissionAmount(c)
                        .createDate(LocalDateTime.now()).build();

        return accountService.updateBalanceWithdrawal(t.getAccount(), t.getAmount().subtract(c))
                    .then(tRepository.save(t));
    };
    private final Function<AccountTransferDTO, Mono<Transaction>> saveTransactionSameHolder = transfer -> {

        Transaction t = Transaction.builder()
                        .amount(transfer.getAmount())
                        .debit(transfer.getAmount())
                        .credit(transfer.getAmount())
                        .toaccount(transfer.getFaccount())
                        .fromaccount(transfer.getTaccount())
                        .transactiontype(TransactionType.SAME_HOLDER_TRANSFER)
                        .createDate(LocalDateTime.now()).build();

        return tRepository.save(t);
    };
    private final Function<AccountTransferDTO, Mono<Transaction>> saveTransactionThirdParty = transfer -> {

        Transaction t = Transaction.builder()
                .amount(transfer.getAmount())
                .debit(transfer.getAmount())
                .credit(transfer.getAmount())
                .toaccount(transfer.getFaccount())
                .fromaccount(transfer.getTaccount())
                .transactiontype(TransactionType.THIRD_PARTY_TRANSFER)
                .createDate(LocalDateTime.now()).build();

        return tRepository.save(t);
    };

}






