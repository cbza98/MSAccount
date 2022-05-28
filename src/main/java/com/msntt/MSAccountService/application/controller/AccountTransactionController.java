package com.msntt.MSAccountService.application.controller;

import com.msntt.MSAccountService.domain.beans.AccountOperationDTO;
import com.msntt.MSAccountService.domain.beans.AccountTransferDTO;
import com.msntt.MSAccountService.infraestructure.services.AccountTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/Accounts/Actions/AccountTransactionService")
public class AccountTransactionController {
    @Autowired
    private AccountTransactionService service;
    @PostMapping("/Deposit")
    public Mono<ResponseEntity<Map<String, Object>>> deposit(@Valid @RequestBody Mono<AccountOperationDTO> request) {

        Map<String, Object> response = new HashMap<>();

        return request.flatMap(a -> service.doAccountDeposit(a).map(c -> {
            response.put("Deposit", c);
            response.put("message", "Successful Deposit Transaction ");
            return ResponseEntity.created(URI.create("/MsFundTransact/Entities/Transaction/".concat(c.getTransactionId())))
                    .contentType(MediaType.APPLICATION_JSON).body(response);
        }));
    }
    @PostMapping("/Withdrawal")
    public Mono<ResponseEntity<Map<String, Object>>> withdrawal(@Valid @RequestBody Mono<AccountOperationDTO> request) {

        Map<String, Object> response = new HashMap<>();

        return request.flatMap(a -> service.doAccountWithdrawal(a).map(c -> {
            response.put("Withdrawal", c);
            response.put("message", "Successful Withdrawal Transaction");
            return ResponseEntity.created(URI.create("/MsFundTransact/Entities/Transaction/".concat(c.getTransactionId())))
                    .contentType(MediaType.APPLICATION_JSON).body(response);
        }));
    }
    @PostMapping("/TransferToSameHolder")
    public Mono<ResponseEntity<Map<String, Object>>> TransferSameHolder(@Valid @RequestBody Mono<AccountTransferDTO> request) {

        Map<String, Object> response = new HashMap<>();

        return request.flatMap(a -> service.TransferBetweenAccounts(a).map(c -> {
            response.put("Transfer Same Holder", c);
            response.put("message", "Successful Transfer Same Holder");
            return ResponseEntity.created(URI.create("/MsFundTransact/Entities/Transaction/".concat(c.getTransactionId())))
                    .contentType(MediaType.APPLICATION_JSON).body(response);
        }));
    }
    @PostMapping("/TransferToThirdParty")
    public Mono<ResponseEntity<Map<String, Object>>> TransferToThirdParty(@Valid @RequestBody Mono<AccountTransferDTO> request) {

        Map<String, Object> response = new HashMap<>();

        return request.flatMap(a -> service.doTransferToThirdParty(a).map(c -> {
            response.put("Transfer Same Holder", c);
            response.put("message", "Successful Transfer Same Holder");
            return ResponseEntity.created(URI.create("/MsFundTransact/Entities/Transaction/".concat(c.getTransactionId())))
                    .contentType(MediaType.APPLICATION_JSON).body(response);
        }));
    }


}
