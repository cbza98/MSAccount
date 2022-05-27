package com.msntt.MSAccountService.application.controller;
import com.msntt.MSAccountService.domain.model.AccountItem;
import com.msntt.MSAccountService.infraestructure.interfaces.IAccountItemService;
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
@RequestMapping("/Accounts/Entities/AccountItem")
public class AccountItemController {
    @Autowired
    private IAccountItemService service;
    @GetMapping
    public Mono<ResponseEntity<Flux<AccountItem>>> findAll() {
        return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(service.findAll()));
    }
    @GetMapping("/{id}")
    public Mono<AccountItem> findById(@PathVariable String id) {
        return service.findById(id);
    }
    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> save(@Valid @RequestBody Mono<AccountItem> request) {

        Map<String, Object> response = new HashMap<>();

        return request.flatMap(a -> service.save(a).map(c -> {
            response.put("Item", c);
            response.put("message", "item successfully created");
            return ResponseEntity.created(URI.create("/Accounts/Entities/AccountItem".concat(c.getItemCode())))
                    .contentType(MediaType.APPLICATION_JSON).body(response);
        }));
    }
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> delete(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        return service.delete(id)
                .map(c -> {
                    response.put("Item", c);
                    response.put("message", "item successfully deleted");
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .location( URI.create("/Accounts/Entities/AccountItem".concat(c.getItemCode())))
                            .body(response);
                });
    }
}
