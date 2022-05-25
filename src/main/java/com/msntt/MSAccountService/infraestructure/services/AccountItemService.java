package com.msntt.MSAccountService.infraestructure.services;

import com.msntt.MSAccountService.application.exception.EntityAlreadyExistsException;
import com.msntt.MSAccountService.application.exception.EntityNotExistsException;
import com.msntt.MSAccountService.domain.entities.AccountItem;
import com.msntt.MSAccountService.domain.repository.AccountItemRepository;
import com.msntt.MSAccountService.infraestructure.interfaces.IAccountItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccountItemService implements IAccountItemService {

    @Autowired
    private AccountItemRepository repository;

    @Override
    public Mono<AccountItem> save(AccountItem item) {
        return repository.existsById(item.getItemCode())
                .filter(exists->!exists)
                .flatMap(r-> {
                    AccountItem i = AccountItem.builder()
                            .businessPartnerAllowed(item.getBusinessPartnerAllowed())
                            .accountName(item.getAccountName())
                            .accountType(item.getAccountType())
                            .limitTransaction(item.getLimitTransaction())
                            .commission(item.getCommission())
                            .maintenanceCommission(item.getMaintenanceCommission())
                            .limitDay(item.getLimitDay())
                            .valid(item.getValid())
                            .creditCardIsRequired(item.getCreditCardIsRequired())
                            .limitAccountsAllowed(item.getLimitAccountsAllowed())
                            .moreHoldersAreAllowed(item.getMoreHoldersAreAllowed())
                            .signersAreAllowed(item.getSignersAreAllowed())
                            .hasAccountsLimit(item.getHasAccountsLimit())
                            .build();
                    return repository.save(i);
                })
                .switchIfEmpty(Mono.error(new EntityAlreadyExistsException()));
    }

    @Override
    public Mono<AccountItem> update(AccountItem item) {
        return repository.findById(item.getItemCode()).flatMap(a -> {
            AccountItem i = AccountItem.builder()
                    .itemCode(item.getItemCode())
                    .businessPartnerAllowed(item.getBusinessPartnerAllowed())
                    .accountName(item.getAccountName())
                    .accountType(item.getAccountType())
                    .limitTransaction(item.getLimitTransaction())
                    .commission(item.getCommission())
                    .maintenanceCommission(item.getMaintenanceCommission())
                    .limitDay(item.getLimitDay())
                    .valid(item.getValid())
                    .build();
            return repository.save(i);
        }).switchIfEmpty(Mono.error(new EntityNotExistsException()));
    }

    @Override
    public Mono<AccountItem> delete(String id) {
        return repository.findById(id)
                .flatMap(deleted -> repository.delete(deleted)
                        .then(Mono.just(deleted))).switchIfEmpty(Mono.error(new EntityNotExistsException()));

    }

    @Override
    public Mono<AccountItem> findById(String id) {
        return repository.findById(id)
                .flatMap(bsp-> repository.findById(id))
                .switchIfEmpty(Mono.error(new EntityNotExistsException()));

    }

    @Override
    public Flux<AccountItem> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<AccountItem> findByAccountType(String accountType) {
        return repository.findByAccountType(accountType);
    }
}
