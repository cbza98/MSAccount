package com.msntt.MSAccountService.application.controller;


import com.msntt.MSAccountService.domain.beans.DebitCardOperationDTO;
import com.msntt.MSAccountService.infraestructure.interfaces.IDebitCardTransactionService;
import com.msntt.MSAccountService.infraestructure.services.DebitCardTransactionService;
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
@RequestMapping("/Accounts/Actions/DebitCardTransactionService")
public class DebitCardTransactionController {

    @Autowired
    private DebitCardTransactionService service;
    @PostMapping("/Payment")
    public Mono<ResponseEntity<Map<String, Object>>> payment(@Valid @RequestBody Mono<DebitCardOperationDTO> request) {

        Map<String, Object> response = new HashMap<>();

        return request.flatMap(a -> service.doDebitCardPayment(a).map(c -> {
            response.put("Deposit", c);
            response.put("message", "Successful Deposit Transaction ");
            return ResponseEntity.created(URI.create("/Accounts/Actions/DebitCardTransactionService"
                            .concat(c.getTransactionId())))
                    .contentType(MediaType.APPLICATION_JSON).body(response);
        }));
    }
    @PostMapping("/Withdrawal")
    public Mono<ResponseEntity<Map<String, Object>>> withdrawal(@Valid @RequestBody Mono<DebitCardOperationDTO> request) {

        Map<String, Object> response = new HashMap<>();

        return request.flatMap(a -> service.doDebitCardWithdrawal(a).map(c -> {
            response.put("Withdrawal", c);
            response.put("message", "Successful Withdrawal Transaction");
            return ResponseEntity.created(URI.create("/Accounts/Actions/DebitCardTransactionService"
                            .concat(c.getTransactionId())))
                    .contentType(MediaType.APPLICATION_JSON).body(response);
        }));
    }

}
