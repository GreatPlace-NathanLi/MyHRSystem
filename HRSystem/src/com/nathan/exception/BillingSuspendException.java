package com.nathan.exception;

@SuppressWarnings("serial")
public class BillingSuspendException extends Exception {
	
	public BillingSuspendException() {
		super();
	}
	
	public BillingSuspendException(String message) {
		super(message);
	}
}
