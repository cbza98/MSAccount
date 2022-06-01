package com.msntt.MSAccountService.application.controller;

import com.msntt.MSAccountService.domain.beans.AssociateAccountDTO;
import com.msntt.MSAccountService.domain.beans.CreateDebitCardDTO;
import com.msntt.MSAccountService.domain.beans.DebitCardBalanceDTO;
import com.msntt.MSAccountService.domain.beans.HolderDTO;
import com.msntt.MSAccountService.domain.model.Account;
import com.msntt.MSAccountService.domain.model.DebitCard;
import com.msntt.MSAccountService.infraestructure.interfaces.IDebitCardService;
import com.msntt.MSAccountService.infraestructure.services.DebitCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/Accounts/Entities/DebitCard")
public class DebitCardController {
    @Autowired
    private DebitCardService debitCardService;
    @GetMapping
    public Mono<ResponseEntity<Flux<DebitCard>>> findAll() {
        return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(debitCardService.findAll()));
    }
    @GetMapping("/{id}")
    public Mono<DebitCard> findById(@PathVariable String id) {
        return debitCardService.findById(id);
    }
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> delete(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        return debitCardService.delete(id)
                .map(c -> {
                    response.put("DebitCard", c);
                    response.put("message", "Successful debit card deleted");
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .location( URI.create("/Accounts/Entities/DebitCard".concat(c.getCardNumber())))
                            .body(response);
                });
    }
    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> createDebitCard(@Valid @RequestBody Mono<CreateDebitCardDTO> request) {

        Map<String, Object> response = new HashMap<>();

        return request.flatMap(a -> debitCardService.createDebitCard(a).map(c -> {
            response.put("Debit Card", c);
            response.put("Message", "Debit Card created successfully");
            return ResponseEntity.created(URI.create("/Accounts/Entities/DebitCard".concat(c.getCardNumber())))
                    .contentType(MediaType.APPLICATION_JSON).body(response);
        }));
    }
    @PostMapping("/AssociateAccount")
    public Mono<ResponseEntity<Map<String, Object>>> associateAccount(@Valid @RequestBody Mono<AssociateAccountDTO> request) {

        Map<String, Object> response = new HashMap<>();

        return request.flatMap(a -> debitCardService.associateAccount(a).map(c -> {
            response.put("Debit Card", c);
            response.put("Message", "Account Linked Successfully");
            return ResponseEntity.created(URI.create("/Accounts/Entities/DebitCard/AssociateAccount"
                            .concat(c.getCardNumber())))
                    .contentType(MediaType.APPLICATION_JSON).body(response);
        }));
    }
    @GetMapping("/GetDebitCardBalance/{id}")
    public Mono<DebitCardBalanceDTO>getDebitCardBalance(@PathVariable String id) {
        return debitCardService.getDebitCardBalance(id);
    }

}
