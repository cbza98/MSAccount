package com.msntt.MSAccountService.application.controller;

import com.msntt.MSAccountService.domain.beans.CreateAccountDTO;
import com.msntt.MSAccountService.domain.beans.HolderDTO;
import com.msntt.MSAccountService.domain.beans.SignerDTO;
import com.msntt.MSAccountService.domain.model.Account;
import com.msntt.MSAccountService.infraestructure.services.AccountService;
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
@RequestMapping("/Accounts/Entities/Account")
public class AccountController {
	@Autowired
	private AccountService service;
	@GetMapping
	public Mono<ResponseEntity<Flux<Account>>> findAll() {
		return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(service.findAll()));
	}
	@GetMapping("/{id}")
	public Mono<Account> findById(@PathVariable String id) {
		return service.findById(id);
	}
	@PostMapping
	public Mono<ResponseEntity<Map<String, Object>>> createAccount(@Valid @RequestBody Mono<CreateAccountDTO> request) {

		Map<String, Object> response = new HashMap<>();

		return request.flatMap(a -> service.createAccount(a).map(c -> {
			response.put("Account", c);
			response.put("Message", "Account created Successfully");
			return ResponseEntity.created(URI.create("/Accounts/Entities/Account/".concat(c.getAccountNumber())))
					.contentType(MediaType.APPLICATION_JSON).body(response);
		}));
	}
	@PostMapping("/SaveAll")
	public Mono<ResponseEntity<Map<String, Object>>> saveAll(@RequestBody Flux<Account> businessPartnerList) {

		Map<String, Object> response = new HashMap<>();

		return businessPartnerList.collectList().flatMap(a -> service.saveAll(a).collectList()).map(c -> {
			response.put("Accounts", c);
			response.put("Message", "Accounts Created Successfully");
			return ResponseEntity.created(URI.create("/Accounts/Entities/Account/SaveAll")).contentType(MediaType.APPLICATION_JSON)
					.body(response);
		});
	}
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Map<String, Object> >> delete(@PathVariable String id) {
		 Map<String, Object> response = new HashMap<>();

		return service.delete(id)
				.map(c -> {
					response.put("Account", c);
					response.put("Message", "Accounts deleted successfully");
					return ResponseEntity.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.location( URI.create("/Accounts/Entities/Account".concat(c.getAccountNumber())))
							.body(response);
				});
	}
	@PostMapping("/AddHolder")
	public Mono<ResponseEntity<Map<String, Object>>> addHolder(@Valid @RequestBody Mono<HolderDTO> request) {

		Map<String, Object> response = new HashMap<>();

		return request.flatMap(a -> service.addHolder(a).map(c -> {
			response.put("Account", c);
			response.put("Message", "Holder added successfully");
			return ResponseEntity.created(URI.create("/Accounts/Entities/Account/AddHolder".concat(c.getAccountNumber())))
					.contentType(MediaType.APPLICATION_JSON).body(response);
		}));
	}
	@PostMapping("/AddSigner")
	public Mono<ResponseEntity<Map<String, Object>>> addSigner(@Valid @RequestBody Mono<SignerDTO> request) {

		Map<String, Object> response = new HashMap<>();

		return request.flatMap(a -> service.addSigner(a).map(c -> {
			response.put("Account", c);
			response.put("Message", "Signer added successfully");
			return ResponseEntity.created(URI.create("/Accounts/Entities/Account/AddSigner".concat(c.getAccountNumber())))
					.contentType(MediaType.APPLICATION_JSON).body(response);
		}));
	}
	
}
