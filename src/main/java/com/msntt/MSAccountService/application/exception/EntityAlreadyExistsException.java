package com.msntt.MSAccountService.application.exception;

public class EntityAlreadyExistsException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message = "The Business Partner code not has already been registered";

	public EntityAlreadyExistsException() {

	}

	public EntityAlreadyExistsException(String optMessage) {
			this.message=optMessage;
	}

	public String getMessage() {
		return message;
	}

}