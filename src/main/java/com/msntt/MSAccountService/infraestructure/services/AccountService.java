package com.msntt.MSAccountService.infraestructure.services;
import com.msntt.MSAccountService.application.exception.AccountNotCreatedException;
import com.msntt.MSAccountService.application.exception.EntityNotExistsException;
import com.msntt.MSAccountService.application.helpers.AccountGeneratorValues;
import com.msntt.MSAccountService.domain.beans.BusinessPartnerDTO;
import com.msntt.MSAccountService.domain.beans.CreateAccountDTO;
import com.msntt.MSAccountService.domain.beans.HolderDTO;
import com.msntt.MSAccountService.domain.beans.SignerDTO;
import com.msntt.MSAccountService.domain.entities.Holder;
import com.msntt.MSAccountService.domain.entities.Signer;
import com.msntt.MSAccountService.infraestructure.restclient.IBusinessPartnerClient;
import com.msntt.MSAccountService.domain.entities.Account;
import com.msntt.MSAccountService.domain.entities.AccountItem;
import com.msntt.MSAccountService.domain.repository.AccountRepository;
import com.msntt.MSAccountService.infraestructure.interfaces.IAccountItemService;
import com.msntt.MSAccountService.infraestructure.interfaces.IAccountService;
import com.msntt.MSAccountService.infraestructure.restclient.ICreditCardClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AccountService implements IAccountService {

    @Autowired
    private AccountRepository repository;

    @Autowired
    private IAccountItemService itemService;
    //methods

    @Autowired
    private IBusinessPartnerClient businessPartnerClient;

    @Autowired
    private ICreditCardClient creditCardClient;

    // Cruds
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
    public Mono<Account> findById(String Id) {
        return repository.findById(Id);
    }

    @Override
    public Mono<Account> findByAccountNumber(String id) {
        return repository.findByAccountNumber(id);
    }
    public Flux<Account> saveAll(List<Account> a) {

        return repository.saveAll(a);
    }

    @Override
    public Mono<Account> addHolder(HolderDTO holderDTO) {
        return existsHolderOrSigner.apply(holderDTO.getHolderId())
                .then(findByAccountNumber(holderDTO.getAccountNumber()))
                .filter(Account::getMoreHoldersAreAllowed)
                .switchIfEmpty(Mono.error(AccountNotCreatedException::new))
                .filter(account-> account.getHolders()
                        .stream().noneMatch(h -> h.getHolderId().equals(holderDTO.getHolderId())))
                .flatMap(account-> {
                    List<Holder> h = account.getHolders();

                    h.add(Holder.builder()
                            .holderId(holderDTO.getHolderId())
                            .addedDate(new Date()).build());

                    account.setHolders(h);
                    return repository.save(account);
                }).switchIfEmpty(Mono.error(AccountNotCreatedException::new));
    }
    @Override
    public Mono<Account> addSigner(SignerDTO signerDTO){
        return existsHolderOrSigner.apply(signerDTO.getSignerId())
                .then(findByAccountNumber(signerDTO.getAccountNumber()))
                .filter(Account::getSignersAreAllowed)
                .filter(account-> account.getSigners()
                        .stream().noneMatch(s -> s.getSignerId().equals(signerDTO.getSignerId()))
                ).flatMap(account->{
                    account.getHolders().add(Holder.builder()
                            .holderId(signerDTO.getSignerId())
                            .addedDate(new Date()).build());
                    return repository.save(account);
                }).switchIfEmpty(Mono.error(AccountNotCreatedException::new));
    }
    @Override
    public Mono<Account> update(String id, Account _request) {

        return repository.findById(id).flatMap(a -> {
                    a.setAccountName(_request.getAccountName());
                    a.setAccountNumber(_request.getAccountNumber());
                    a.setAccountType(_request.getAccountType());
                    a.setCodeBusinessPartner(_request.getCodeBusinessPartner());
                    a.setDate_Opened(_request.getDate_Opened());
                    a.setValid(_request.getValid());
                    return repository.save(a);
                }).switchIfEmpty(Mono.error(new EntityNotExistsException()));
    }
    // Balance
    @Override
    public Mono<Account> updateBalanceDp(String id, BigDecimal balance) {
        return repository.findById(id).flatMap(a ->
                {
                    a.setBalance(a.getBalance().add(balance));
                    return repository.save(a);
                });
    }
    
    @Override
    public Mono<Account> updateBalanceWt(String id, BigDecimal balance) {
        return repository.findById(id).flatMap(a ->
                {
                    a.setBalance(a.getBalance().subtract(balance));
                    return repository.save(a);
                });
    }
    //Create

    @Override
    public Mono<Account> createAccount(CreateAccountDTO account) {
        return getAccountItem.apply(account)
                .flatMap(ai->existsBusinessPartner.apply(account,ai))
                .filter(ai->isBusinessPartnerAllowed.test(account,ai))
                .flatMap(ai->validateLimitCreation.apply(account,ai))
                //.flatMap(ai->creditCardValidation.apply(account,ai))
                .flatMap(ai->mapToAccountAndSave.apply(account,ai))
                .switchIfEmpty(Mono.error(AccountNotCreatedException::new));
    }

    //Functions
    private final BiFunction<CreateAccountDTO, AccountItem, Mono<AccountItem>> existsBusinessPartner = (account, accountItem)->
            businessPartnerClient.findById(account.getCodeBusinessPartner())
            .map(r -> accountItem);

    private final Function<String, Mono<String>> existsHolderOrSigner = (holderSignerId)->
            businessPartnerClient.findById(holderSignerId).map(r ->holderSignerId);

    private final Function<CreateAccountDTO,Mono<AccountItem>> getAccountItem= createAccountDTO->
            itemService.findById(createAccountDTO.getAccountCode())
            .switchIfEmpty(Mono.error(EntityNotExistsException::new));

    private final BiPredicate<CreateAccountDTO,AccountItem> isBusinessPartnerAllowed= (createAccountDTO, accountItem)->
            accountItem.getBusinessPartnerAllowed()
                    .contains(createAccountDTO.getCodeBusinessPartner().substring(0,1));

    private final BiFunction<CreateAccountDTO,AccountItem, Mono<AccountItem>> creditCardValidation= (createAccountDTO, accountItem)->
            creditCardClient.countCreditCardsByBusinessPartner(createAccountDTO.getCodeBusinessPartner())
                    .filter(dto->accountItem.getCreditCardIsRequired().equals(false)
                                ||(accountItem.getCreditCardIsRequired().equals(true) &&
                                    dto.getCreditCardCount()>0))
                    .map(dto->accountItem)
                    .switchIfEmpty(Mono.error(AccountNotCreatedException::new));


    private final BiFunction<CreateAccountDTO,AccountItem, Mono<AccountItem>> validateLimitCreation = (account,accountItem) ->

            repository.countByAccountTypeAndCodeBusinessPartner(account.getAccountCode(),
                    account.getCodeBusinessPartner())
                    .filter(count->accountItem.getLimitAccountsAllowed()>count||
                            accountItem.getHasAccountsLimit().equals(false))
                    .map(count->accountItem)
                    .switchIfEmpty(Mono.error(AccountNotCreatedException::new));
    private final BiFunction<CreateAccountDTO,AccountItem, Mono<Account>> mapToAccountAndSave = (account,accountItem) -> {

        Account a = Account.builder()
                .accountNumber(AccountGeneratorValues.NumberGenerate())
                .valid(true)
                .balance(new BigDecimal("0.00"))
                .codeBusinessPartner(account.getCodeBusinessPartner())
                .date_Opened(new Date())
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
