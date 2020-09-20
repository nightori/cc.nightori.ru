package ru.nightori.cc.exception;

// custom exception for when the password is wrong
public class WrongPasswordException extends RuntimeException {
	public WrongPasswordException(String errorMessage) {
		super(errorMessage);
	}
}