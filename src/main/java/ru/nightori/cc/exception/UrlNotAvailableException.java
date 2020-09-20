package ru.nightori.cc.exception;

// custom exception for when custom url is not available
public class UrlNotAvailableException extends RuntimeException {
	public UrlNotAvailableException(String errorMessage) {
		super(errorMessage);
	}
}