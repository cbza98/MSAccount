package com.msntt.MSAccountService.application.exception;

public class AccountNotCreatedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String message = "Account couldn't be created";
	
	public AccountNotCreatedException() {

	}

	public AccountNotCreatedException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}}
