package com.msntt.MSAccountService.infraestructure.services;

import com.msntt.MSAccountService.application.exception.ResourceNotCreatedException;
import com.msntt.MSAccountService.application.exception.EntityNotExistsException;
import com.msntt.MSAccountService.application.helpers.CardGeneratorValues;
import com.msntt.MSAccountService.domain.beans.AssociateAccountDTO;
import com.msntt.MSAccountService.domain.beans.BusinessPartnerDTO;
import com.msntt.MSAccountService.domain.beans.CreateDebitCardDTO;
import com.msntt.MSAccountService.domain.model.Account;
import com.msntt.MSAccountService.domain.model.DebitCard;
import com.msntt.MSAccountService.domain.model.LinkedAccount;
import com.msntt.MSAccountService.domain.repository.DebitCardRepository;
import com.msntt.MSAccountService.infraestructure.interfaces.IDebitCardService;
import com.msntt.MSAccountService.infraestructure.restclient.IBusinessPartnerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class DebitCardService implements IDebitCardService {

    @Autowired
    private DebitCardRepository repository;

    @Autowired
    private IBusinessPartnerClient businessPartnerClient;

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

        return  bpDTO.zipWith(account)
                .filter(a->a.getT2().getCodeBusinessPartner().equals(a.getT1().getBusinessPartnerId()))
                .switchIfEmpty(Mono.error(new EntityNotExistsException("Account Holder doesn't match with client id")))
                .then(Mono.just(debitCardDTO))
                //.flatMap(ai->validateLimitCreation.apply(account,ai)) puede crear algun producto? por si tiene deuda
                .flatMap(mapToDebitCardAndSave)
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


    private final Function<CreateDebitCardDTO, Mono<DebitCard>> mapToDebitCardAndSave = debitCardDto -> {

        LinkedAccount linkedAccount= LinkedAccount.builder()
                                        .accountId(debitCardDto.getAccountNumber())
                                        .addedDate(new Date())
                                        .build();

        DebitCard a = DebitCard.builder()
                .cardNumber(CardGeneratorValues.CardNumberGenerate())
                .valid(true)
                .expiringDate(CardGeneratorValues.CardExpiringDateGenerate())
                .codeBusinessPartner(debitCardDto.getCodeBusinessPartner())
                .cvv(CardGeneratorValues.CardCVVGenerate())
                .createdate(new Date())
                .cardName("Debit Card VISA")
                .linkedAccountList(List.of(linkedAccount)).build();

        return repository.save(a);

    };

    private final BiFunction<AssociateAccountDTO,DebitCard, Mono<DebitCard>> saveAssociatedAccount=
            (associateAccountDTO,debitCard) -> {
                List<LinkedAccount> lnkAcc = debitCard.getLinkedAccountList();

                lnkAcc.add(LinkedAccount.builder()
                        .accountId(associateAccountDTO.getAccountId())
                        .addedDate(new Date())
                        .build());

                debitCard.setLinkedAccountList(lnkAcc);
                return repository.save(debitCard);
            };
}
