package com.narvee.exception;

public class CustomAuthenticationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CustomAuthenticationException(String message) {
        super(message);
    }
}
