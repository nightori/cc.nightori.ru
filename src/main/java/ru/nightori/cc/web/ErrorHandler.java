package ru.nightori.cc.web;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.nightori.cc.exceptions.*;

import javax.persistence.EntityNotFoundException;
import javax.persistence.RollbackException;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

	// 400 - invalid request
	@ExceptionHandler(value = {
			RollbackException.class,
			ConstraintViolationException.class,
			IllegalHeaderException.class,
			DataIntegrityViolationException.class
	})
	protected ResponseEntity<Object> handleValidationError(RuntimeException ex, WebRequest request) {
		String msg = "invalid request";
		return handleExceptionInternal(ex, msg, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	// 403 - wrong password
	@ExceptionHandler(value = {
			WrongPasswordException.class
	})
	protected ResponseEntity<Object> handleAccessError(RuntimeException ex, WebRequest request) {
		String msg = "access denied";
		return handleExceptionInternal(ex, msg, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
	}

	// 404 - requested entry not found
	@ExceptionHandler(value = {
			EntityNotFoundException.class
	})
	protected ResponseEntity<Object> handleNotFoundError(RuntimeException ex, WebRequest request) {
		String msg = "not found";
		return handleExceptionInternal(ex, msg, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

	// 418 - recursive redirect
	@ExceptionHandler(value = {
			RecursiveRedirectException.class
	})
	protected ResponseEntity<Object> handleRecursiveError(RuntimeException ex, WebRequest request) {
		String msg = "recursive redirect";
		return handleExceptionInternal(ex, msg, new HttpHeaders(), HttpStatus.I_AM_A_TEAPOT, request);
	}

	// 422 - duplicate IDs or other SQL error
	@ExceptionHandler(value = {
			UrlNotAvailableException.class
	})
	protected ResponseEntity<Object> handleSqlError(RuntimeException ex, WebRequest request) {
		String msg = "sql error";
		return handleExceptionInternal(ex, msg, new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
	}

	// 429 - too many requests
	@ExceptionHandler(value = {
			LimitExceededException.class
	})
	protected ResponseEntity<Object> handleLimitError(RuntimeException ex, WebRequest request) {
		String msg = "too many requests";
		return handleExceptionInternal(ex, msg, new HttpHeaders(), HttpStatus.TOO_MANY_REQUESTS, request);
	}

}