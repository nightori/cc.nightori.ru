package ru.nightori.cc.exceptions;

// exception for recursive redirects
public class RecursiveRedirectException extends RuntimeException {
	public RecursiveRedirectException(String errorMessage) {
		super(errorMessage);
	}
}