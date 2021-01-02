package ru.nightori.cc.exceptions;

// custom exception for recursive redirects
public class RecursiveRedirectException extends RuntimeException {
	public RecursiveRedirectException(String errorMessage) {
		super(errorMessage);
	}
}