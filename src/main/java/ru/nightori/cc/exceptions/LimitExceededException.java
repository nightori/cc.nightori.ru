package ru.nightori.cc.exceptions;

// exception for API rate limit exceeding
public class LimitExceededException extends RuntimeException {
	public LimitExceededException(String errorMessage) {
		super(errorMessage);
	}
}