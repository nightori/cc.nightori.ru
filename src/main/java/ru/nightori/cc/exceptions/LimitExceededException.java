package ru.nightori.cc.exceptions;

// custom exception for API rate limit exceeding
public class LimitExceededException extends RuntimeException {
	public LimitExceededException(String errorMessage) {
		super(errorMessage);
	}
}