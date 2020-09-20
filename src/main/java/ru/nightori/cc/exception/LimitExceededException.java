package ru.nightori.cc.exception;

// custom exception for API rate limit exceeding
public class LimitExceededException extends RuntimeException {
	public LimitExceededException(String errorMessage) {
		super(errorMessage);
	}
}