package ru.nightori.cc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.nightori.cc.exceptions.RecursiveRedirectException;
import ru.nightori.cc.exceptions.UrlNotAvailableException;
import ru.nightori.cc.exceptions.WrongPasswordException;
import ru.nightori.cc.model.Redirect;
import ru.nightori.cc.model.RedirectRepository;
import ru.nightori.cc.service.RandomGeneratorService;
import ru.nightori.cc.service.RedirectService;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.nightori.cc.CcApplication.APP_DOMAIN;

@SpringBootTest(classes = RedirectService.class,
		webEnvironment = SpringBootTest.WebEnvironment.NONE)

class RedirectServiceTest {

	private final String TEST_SHORT_URL = "google";
	private final String TEST_DESTINATION = "https://google.com";
	private final String TEST_PASSWORD = "123";

	@Autowired
	RedirectService service;

	@MockBean
	RedirectRepository mockRepository;

	@MockBean
	BCryptPasswordEncoder mockPasswordEncoder;

	@MockBean
	RandomGeneratorService generator;

	@Test
	void createRedirectTestSuccess() {
		service.createRedirect(TEST_SHORT_URL, TEST_DESTINATION, TEST_PASSWORD);
		verify(mockRepository).existsByShortUrl(TEST_SHORT_URL);
		verify(mockPasswordEncoder).encode(TEST_PASSWORD);
		verify(mockRepository).save(any());
	}

	@Test
	void createRedirectTestNotAvailable() {
		when(mockRepository.existsByShortUrl(TEST_SHORT_URL)).thenReturn(true);
		assertThrows(
				UrlNotAvailableException.class,
				() -> service.createRedirect(TEST_SHORT_URL, TEST_DESTINATION, TEST_PASSWORD)
		);
	}

	@Test
	void createRedirectTestRecursive() {
		assertThrows(
				RecursiveRedirectException.class,
				() -> service.createRedirect(TEST_SHORT_URL, APP_DOMAIN, TEST_PASSWORD)
		);
	}

	@Test
	void deleteRedirectTestSuccess() {
		Redirect redirect = new Redirect(TEST_SHORT_URL, TEST_DESTINATION, TEST_PASSWORD);
		when(mockRepository.findByShortUrl(TEST_SHORT_URL)).thenReturn(Optional.of(redirect));
		when(mockPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);

		service.deleteRedirect(redirect.getShortUrl(), redirect.getPassword());
		verify(mockRepository).deleteById(redirect.getShortUrl());
	}

	@Test
	void deleteRedirectTestNotFound() {
		when(mockRepository.findByShortUrl(TEST_SHORT_URL)).thenReturn(Optional.empty());

		assertThrows(
				EntityNotFoundException.class,
				() -> service.deleteRedirect(TEST_SHORT_URL, TEST_PASSWORD)
		);
	}

	@Test
	void deleteRedirectTestAccessDenied() {
		Redirect redirect = new Redirect(TEST_SHORT_URL, TEST_DESTINATION, TEST_PASSWORD);
		when(mockRepository.findByShortUrl(TEST_SHORT_URL)).thenReturn(Optional.of(redirect));
		when(mockPasswordEncoder.matches(anyString(), anyString())).thenReturn(false);

		assertThrows(
				WrongPasswordException.class,
				() -> service.deleteRedirect(TEST_SHORT_URL, "456")
		);
	}

	@Test
	void getRedirectUrlTestSuccess() {
		Redirect redirect = new Redirect(TEST_SHORT_URL, TEST_DESTINATION, TEST_PASSWORD);
		when(mockRepository.findByShortUrl(TEST_SHORT_URL)).thenReturn(Optional.of(redirect));

		String destination = service.getRedirectUrl(redirect.getShortUrl());
		assertEquals(redirect.getDestination(), destination);
	}

	@Test
	void getRedirectUrlTestNotPresent() {
		when(mockRepository.findByShortUrl(TEST_SHORT_URL)).thenReturn(Optional.empty());

		String destination = service.getRedirectUrl(TEST_SHORT_URL);
		assertEquals("/home", destination);
	}

	@Test
	void getRedirectUrlTestReserved() {
		String destination = service.getRedirectUrl("api");
		assertEquals("/home", destination);
	}

}