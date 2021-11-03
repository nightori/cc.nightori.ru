package ru.nightori.cc.exceptions;

// exception for when the IP header is tampered with
public class IllegalHeaderException extends RuntimeException {
	public IllegalHeaderException(String errorMessage) {
		super(errorMessage);
	}
}