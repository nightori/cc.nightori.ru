package ru.nightori.cc.exceptions;

// exception for when the deletion password is wrong
public class WrongPasswordException extends RuntimeException {
	public WrongPasswordException(String errorMessage) {
		super(errorMessage);
	}
}