package com.msntt.MSAccountService.infraestructure.services;

import com.msntt.MSAccountService.application.exception.ResourceNotCreatedException;
import com.msntt.MSAccountService.application.exception.EntityNotExistsException;
import com.msntt.MSAccountService.application.helpers.CardGeneratorValues;
import com.msntt.MSAccountService.domain.beans.AssociateAccountDTO;
import com.msntt.MSAccountService.domain.beans.BusinessPartnerDTO;
import com.msntt.MSAccountService.domain.beans.CreateDebitCardDTO;
import com.msntt.MSAccountService.domain.beans.DebitCardBalanceDTO;
import com.msntt.MSAccountService.domain.model.Account;
import com.msntt.MSAccountService.domain.model.AccountItem;
import com.msntt.MSAccountService.domain.model.DebitCard;
import com.msntt.MSAccountService.domain.model.LinkedAccount;
import com.msntt.MSAccountService.domain.repository.DebitCardRepository;
import com.msntt.MSAccountService.infraestructure.interfaces.IDebitCardService;
import com.msntt.MSAccountService.infraestructure.restclient.IBusinessPartnerClient;
import com.msntt.MSAccountService.infraestructure.restclient.ICreditClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple5;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class DebitCardService implements IDebitCardService {

    @Autowired
    private DebitCardRepository repository;

    @Autowired
    private IBusinessPartnerClient businessPartnerClient;

    @Autowired
    private ICreditClient creditCardClient;

    @Autowired
    private AccountService accountService;

    @Override
    public Flux<DebitCard> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<DebitCard> createDebitCard(CreateDebitCardDTO debitCardDTO) {

        Mono<BusinessPartnerDTO> bpDTO = businessPartnerClient.findById(debitCardDTO.getCodeBusinessPartner());

        Mono<Account> account = accountService.findById(debitCardDTO.getAccountNumber())
                                    .switchIfEmpty(Mono.error(new EntityNotExistsException("Account doesn't exists")));

        Mono<Long> countExpiredDebt =creditCardClient.getExpiredDebts(debitCardDTO.getCodeBusinessPartner());

        return   Mono.zip(countExpiredDebt,bpDTO,account,Mono.just(debitCardDTO))
                .filter(t-> t.getT1() ==0L)
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Business partner had expired debt")))
                .filter(a->a.getT3().getCodeBusinessPartner().equals(a.getT2().getBusinessPartnerId()))
                .switchIfEmpty(Mono.error(new EntityNotExistsException("Account Holder doesn't match with client id")))
                .flatMap(f->mapToDebitCardAndSave.apply(f.getT4()))
                .switchIfEmpty(Mono.error(ResourceNotCreatedException::new));
    }

    @Override
    public Mono<DebitCard> delete(String id) {
        return repository.findById(id).flatMap(deleted -> repository.delete(deleted).then(Mono.just(deleted)))
                .switchIfEmpty(Mono.error(new EntityNotExistsException()));
    }

    @Override
    public Mono<DebitCard> findById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotExistsException("Debit Card doesn't exists")));
    }

    @Override
    public Mono<DebitCard> associateAccount(AssociateAccountDTO associateAccountDTO) {

        Mono<Account> account = accountService.findById(associateAccountDTO.getAccountId())
                .switchIfEmpty(Mono.error(new EntityNotExistsException("Account doesn't exists")));

        Mono<DebitCard> debitCard= repository.findById(associateAccountDTO.getDebitCardId())
                .switchIfEmpty(Mono.error(new EntityNotExistsException("Debit Card doesn't exists")));

        return account.zipWith(debitCard).filter(a->a.getT1().getCodeBusinessPartner()
                                                    .equals(a.getT2().getCodeBusinessPartner()))
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Holders doesn't match")))
                .filter(a-> a.getT2().getLinkedAccountList()
                            .stream().noneMatch(h -> h.getAccountId()
                                                   .equals(associateAccountDTO.getAccountId())))
                .switchIfEmpty(Mono.error(new ResourceNotCreatedException("Account already linked to debit card")))
                .flatMap(a->saveAssociatedAccount.apply(associateAccountDTO,a.getT2()));

    }

    public Mono<DebitCardBalanceDTO> getDebitCardBalance(String debitCardNumber){

        Mono<DebitCard> debitCard = repository.findById(debitCardNumber)
                .switchIfEmpty(Mono.error(new EntityNotExistsException("Debit doesn't exists")));

        return debitCard.map(DebitCard::getLinkedAccountList)
                .flatMap(getMainAccount).map(
                        a->DebitCardBalanceDTO.builder()
                                    .debitCardNumber(debitCardNumber)
                                    .accountNumber(a.getAccountNumber())
                                    .balance(a.getBalance()).build());
    }
    private final Function<CreateDebitCardDTO, Mono<DebitCard>> mapToDebitCardAndSave = debitCardDto -> {

        LinkedAccount linkedAccount= LinkedAccount.builder()
                                        .accountId(debitCardDto.getAccountNumber())
                                        .addedDate(LocalDateTime.now())
                                        .isMainAccount(true)
                                        .build();

        DebitCard a = DebitCard.builder()
                .cardNumber(CardGeneratorValues.CardNumberGenerate())
                .valid(true)
                .expiringDate(CardGeneratorValues.CardExpiringDateGenerate())
                .codeBusinessPartner(debitCardDto.getCodeBusinessPartner())
                .cvv(CardGeneratorValues.CardCVVGenerate())
                .createdate(LocalDateTime.now())
                .cardName("Debit Card VISA")
                .linkedAccountList(List.of(linkedAccount)).build();

        return repository.save(a);

    };

    private final BiFunction<AssociateAccountDTO,DebitCard, Mono<DebitCard>> saveAssociatedAccount=
            (associateAccountDTO,debitCard) -> {
                List<LinkedAccount> lnkAcc = debitCard.getLinkedAccountList();

                lnkAcc.add(LinkedAccount.builder()
                        .accountId(associateAccountDTO.getAccountId())
                        .addedDate(LocalDateTime.now())
                        .build());

                debitCard.setLinkedAccountList(lnkAcc);
                return repository.save(debitCard);
            };

    private final Function<List<LinkedAccount>,Mono<Account>> getMainAccount = f->{

        LinkedAccount lnk = f.stream().filter(LinkedAccount::getIsMainAccount)
                .findAny().orElseThrow();

        return accountService.findById(lnk.getAccountId());
    };
}
