package ru.nightori.cc.exceptions;

// custom exception for when the password is wrong
public class WrongPasswordException extends RuntimeException {
	public WrongPasswordException(String errorMessage) {
		super(errorMessage);
	}
}