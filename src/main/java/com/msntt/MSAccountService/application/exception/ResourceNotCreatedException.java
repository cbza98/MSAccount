package com.msntt.MSAccountService.application.exception;

public class ResourceNotCreatedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String message = "Account couldn't be created";
	
	public ResourceNotCreatedException() {

	}

	public ResourceNotCreatedException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}}
