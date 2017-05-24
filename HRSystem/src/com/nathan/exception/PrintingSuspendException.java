package com.nathan.exception;

@SuppressWarnings("serial")
public class PrintingSuspendException extends Exception {

	public PrintingSuspendException() {
		super();
	}
	
	public PrintingSuspendException(String message) {
		super(message);
	}
	
}
