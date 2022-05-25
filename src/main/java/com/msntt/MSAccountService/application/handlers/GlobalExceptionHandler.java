package com.msntt.MSAccountService.application.handlers;


import com.msntt.MSAccountService.application.exception.AccountNotCreatedException;
import com.msntt.MSAccountService.application.exception.EntityAlreadyExistsException;
import com.msntt.MSAccountService.application.exception.EntityNotExistsException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MarkerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(WebExchangeBindException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handlerException(WebExchangeBindException ex) {

		Map<String, Object> response = new HashMap<>();

		log.warn(MarkerFactory.getMarker("VALID"), ex.getMessage());
		return Mono.just(ex).flatMap(e -> Mono.just(e.getFieldErrors())).flatMapMany(Flux::fromIterable)
				.map(fieldError -> "The field:" + fieldError.getField() + " " + fieldError.getDefaultMessage())
				.collectList().flatMap(list -> {

					response.put("errors", list);
					response.put("timestamp", new Date());
					response.put("status", HttpStatus.BAD_REQUEST.value());

					return Mono.just(ResponseEntity.badRequest().body(response));

				});
	}

	@ExceptionHandler(EntityAlreadyExistsException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handlerException(EntityAlreadyExistsException ex) {

		Map<String, Object> response = new HashMap<>();

		log.warn(MarkerFactory.getMarker("VALID"), ex.getMessage());
		return Mono.just(ex).map(EntityAlreadyExistsException::getMessage).flatMap(msg -> {

			response.put("errors", msg);
			response.put("timestamp", new Date());
			response.put("status", HttpStatus.BAD_REQUEST.value());

			return Mono.just(ResponseEntity.badRequest().body(response));

		});
	}
	
	@ExceptionHandler(AccountNotCreatedException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handlerException(AccountNotCreatedException ex) {

		Map<String, Object> response = new HashMap<>();

		log.warn(MarkerFactory.getMarker("VALID"), ex.getMessage());
		return Mono.just(ex).map(AccountNotCreatedException::getMessage).flatMap(msg -> {

			response.put("errors", msg);
			response.put("timestamp", new Date());
			response.put("status", HttpStatus.BAD_REQUEST.value());

			return Mono.just(ResponseEntity.badRequest().body(response));

		});
	}

	@ExceptionHandler(EntityNotExistsException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handlerException(EntityNotExistsException ex) {

		Map<String, Object> response = new HashMap<>();

		log.warn(MarkerFactory.getMarker("VALID"), ex.getMessage());
		return Mono.just(ex).map(EntityNotExistsException::getMessage).flatMap(msg -> {

			response.put("errors", msg);
			response.put("timestamp", new Date());
			response.put("status", HttpStatus.BAD_REQUEST.value());

			return Mono.just(ResponseEntity.badRequest().body(response));

		});

	}

	@ExceptionHandler(FeignException.class)
	public Mono<ResponseEntity<Map<String, Object>>> handleFeignStatusException(FeignException ex) {

		Map<String, Object> response = new HashMap<>();

		log.warn(MarkerFactory.getMarker("VALID"), ex.getMessage());
		return Mono.just(ex).map(e-> {

			response.put("errors", "business partner doesn't exists");
			response.put("timestamp", new Date());
			response.put("status", e.status());

			return ResponseEntity.badRequest().body(response);

		});
	}
}
