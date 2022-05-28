package com.msntt.MSAccountService.infraestructure.services;
import com.msntt.MSAccountService.application.exception.ResourceNotCreatedException;
import com.msntt.MSAccountService.application.exception.EntityNotExistsException;
import com.msntt.MSAccountService.application.helpers.AccountGeneratorValues;
import com.msntt.MSAccountService.domain.beans.*;
import com.msntt.MSAccountService.domain.model.Holder;
import com.msntt.MSAccountService.domain.model.Signer;
import com.msntt.MSAccountService.infraestructure.restclient.IBusinessPartnerClient;
import com.msntt.MSAccountService.domain.model.Account;
import com.msntt.MSAccountService.domain.model.AccountItem;
import com.msntt.MSAccountService.domain.repository.AccountRepository;
import com.msntt.MSAccountService.infraestructure.interfaces.IAccountItemService;
import com.msntt.MSAccountService.infraestructure.interfaces.IAccountService;
import com.msntt.MSAccountService.infraestructure.restclient.ICreditClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuple5;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Service
public class AccountService implements IAccountService {

    //Repositories and Services
    @Autowired
    private AccountRepository repository;
    @Autowired
    private IAccountItemService itemService;
    //methods
    @Autowired
    private IBusinessPartnerClient businessPartnerClient;
    @Autowired
    private ICreditClient creditCardClient;

    // Crud
    @Override
    public Flux<Account> findAll() {
        return repository.findAll();
    }
    @Override
    public Mono<Account> delete(String Id) {
        return repository.findById(Id).flatMap(deleted -> repository.delete(deleted).then(Mono.just(deleted)))
        				 .switchIfEmpty(Mono.error(new EntityNotExistsException()));
    }
    @Override
    public Mono<Account> findById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotExistsException("Account doesn't exists")));
    }
    @Override
    public Flux<Account> saveAll(List<Account> a) {

        return repository.saveAll(a);
    }
    @Override
    public Mono<Account> update(Account _request) {

        return repository.findById(_request.getAccountNumber()).flatMap(a -> {
            a.setAccountName(_request.getAccountName());
            a.setAccountNumber(_request.getAccountNumber());
            a.setAccountType(_request.getAccountType());
            a.setCodeBusinessPartner(_request.getCodeBusinessPartner());
            a.setDate_Opened(_request.getDate_Opened());
            a.setValid(_request.getValid());
            return repository.save(a);
        }).switchIfEmpty(Mono.error(new EntityNotExistsException()));
    }

    //Business Logic
    @Override
    public Mono<Account> addHolder(HolderDTO holderDTO) {
        return   businessPartnerClient.findById(holderDTO.getHolderId())
                .then(findById(holderDTO.getAccountNumber()))
                .filter(Account::getMoreHoldersAreAllowed)
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Account doesn't allow holders")))
                .filter(account-> account.getHolders()
                        .stream().noneMatch(h -> h.getHolderId().equals(holderDTO.getHolderId())))
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Holder already exists")))
                .flatMap(account-> {
                    List<Holder> h = account.getHolders();

                    h.add(Holder.builder()
                            .holderId(holderDTO.getHolderId())
                            .addedDate(new Date()).build());

                    account.setHolders(h);
                    return repository.save(account);
                }).switchIfEmpty(Mono.error(ResourceNotCreatedException::new));
    }
    @Override
    public Mono<Account> addSigner(SignerDTO signerDTO){
        return   businessPartnerClient.findById(signerDTO.getSignerId())
                .then(findById(signerDTO.getAccountNumber()))
                .filter(Account::getSignersAreAllowed)
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Account doesn't allow signers")))
                .filter(account-> account.getSigners()
                        .stream().noneMatch(s -> s.getSignerId().equals(signerDTO.getSignerId()))
                )
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Signer already exists")))
                .flatMap(account->{
                    List<Signer> s = account.getSigners();

                    account.getSigners().add(Signer.builder()
                            .signerId(signerDTO.getSignerId())
                            .addedDate(new Date()).build());

                    return repository.save(account);
                }).switchIfEmpty(Mono.error(ResourceNotCreatedException::new));
    }

    @Override
    public Mono<AvailableAmountDTO> getAvailableAmount(String accountNumber) {
      return repository.findById(accountNumber)
                .switchIfEmpty(Mono.error(new EntityNotExistsException("Account doesn't exists")))
                .map(a->AvailableAmountDTO.builder()
                            .businessPartnerId(a.getCodeBusinessPartner())
                            .accountNumber(a.getAccountNumber())
                            .availableAmount(a.getBalance()).build());
    }

    @Override
    public Mono<Account> updateBalanceDeposit(String id, BigDecimal balance) {
        return repository.findById(id).flatMap(a ->
                {   BigDecimal bigDecimal=a.getBalance().add(balance);
                    a.setBalance(bigDecimal);
                    return repository.save(a);
                });
    }
    @Override
    public Mono<Account> updateBalanceWithdrawal(String id, BigDecimal balance) {
        return repository.findById(id).filter(a->balance.compareTo(a.getBalance())<=0)
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Withdrawal is more than actual balance")))
                .flatMap(a -> { a.setBalance(a.getBalance().subtract(balance));
                                return repository.save(a);
                });

    }


    @Override
    public Mono<Account> createAccount(CreateAccountDTO account) {

        Mono<Long> count = repository.countByAccountItemIdAndCodeBusinessPartner(
                account.getAccountCode(), account.getCodeBusinessPartner());

        Mono<Long> creditCardCount= creditCardClient.countCreditCardsByBusinessPartner(account
                .getCodeBusinessPartner());

        Mono<BusinessPartnerDTO> bsPartner = businessPartnerClient.findById(account.getCodeBusinessPartner());

        Mono<AccountItem> accountItem = itemService.findById(account.getAccountCode())
                .switchIfEmpty(Mono.error(new EntityNotExistsException("Item doesn't exists")));

        Mono<Long> countExpiredDebts = creditCardClient.getExpiredDebts(account.getCodeBusinessPartner());

        return Mono.zip(bsPartner, count, accountItem,creditCardCount,countExpiredDebts)
                .filter(hasExpiredDebt)
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Business partner had expired debt")))
                .filter(isBusinessPartnerAllowed)
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Business partner not allowed for this account")))
                .filter(creditCardValidation)
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Credit Card required")))
                .filter(validateLimitCreation)
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("out of Limit of creation of accounts")))
                .flatMap(t -> mapToAccountAndSave.apply(account, t.getT3()))
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Account couldn't be created")));

    }
    //Functions
    private final Predicate<Tuple3<BusinessPartnerDTO,Long,AccountItem>> isBusinessPartnerAllowed = t->
            t.getT3().getBusinessPartnerAllowed().contains(t.getT1().getBusinessPartnerId().substring(0,1));

    private final Predicate<Tuple4<BusinessPartnerDTO,Long,AccountItem,Long>> creditCardValidation= t->
           t.getT3().getCreditCardIsRequired().equals(false)
               ||(t.getT3().getCreditCardIsRequired().equals(true) && t.getT4()>0);
    private final Predicate<Tuple5<BusinessPartnerDTO,Long,AccountItem,Long,Long>> hasExpiredDebt= t->
            t.getT5()==0L;
    private final Predicate<Tuple3<BusinessPartnerDTO,Long,AccountItem>> validateLimitCreation = t ->
            t.getT3().getLimitAccountsAllowed()>t.getT2()||t.getT3().getHasAccountsLimit().equals(false);
    private final BiFunction<CreateAccountDTO,AccountItem, Mono<Account>> mapToAccountAndSave = (account,accountItem) -> {

        Account a = Account.builder()
                .accountNumber(AccountGeneratorValues.NumberGenerate())
                .valid(true)
                .balance(new BigDecimal("0.00"))
                .codeBusinessPartner(account.getCodeBusinessPartner())
                .date_Opened(LocalDateTime.now())
                .accountItemId(accountItem.getItemCode())
                .accountName(accountItem.getAccountName())
                .accountType(accountItem.getAccountType())
                .minDiaryAmount(accountItem.getMinDiaryAmount())
                .maintenanceCommission(accountItem.getMaintenanceCommission())
                .commission(accountItem.getCommission())
                .limitTransaction(accountItem.getLimitTransaction())
                .limitDay(accountItem.getLimitDay())
                .creditCardIsRequired(accountItem.getCreditCardIsRequired())
                .businessPartnerAllowed(accountItem.getBusinessPartnerAllowed())
                .limitAccountsAllowed(accountItem.getLimitAccountsAllowed())
                .moreHoldersAreAllowed(accountItem.getMoreHoldersAreAllowed())
                .signersAreAllowed(accountItem.getSignersAreAllowed())
                .hasAccountsLimit(accountItem.getHasAccountsLimit())
                .holders(new ArrayList<>())
                .signers(new ArrayList<>())
                .build();
        return repository.save(a);
    };
}
