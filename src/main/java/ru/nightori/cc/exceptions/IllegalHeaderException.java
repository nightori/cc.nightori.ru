package ru.nightori.cc.exceptions;

// custom exception for when IP header is tampered with
public class IllegalHeaderException extends RuntimeException {
	public IllegalHeaderException(String errorMessage) {
		super(errorMessage);
	}
}