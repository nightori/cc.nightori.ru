package ru.nightori.cc.exceptions;

// exception for when a short URL is not available
public class UrlNotAvailableException extends RuntimeException {
	public UrlNotAvailableException(String errorMessage) {
		super(errorMessage);
	}
}