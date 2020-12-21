package ru.nightori.cc;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.nightori.cc.exception.RecursiveRedirectException;
import ru.nightori.cc.exception.UrlNotAvailableException;
import ru.nightori.cc.exception.WrongPasswordException;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.nightori.cc.config.Config.APP_DOMAIN;

@SpringBootTest(classes = RedirectService.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)

class RedirectServiceTests {

    @Autowired
    RedirectService service;

    @MockBean
    RedirectRepository mockedRepository;

    @MockBean
    BCryptPasswordEncoder mockedPasswordEncoder;

    @Test
    void createRedirectTestSuccess() {
        service.createRedirect("google", "https://google.com", "123");
        verify(mockedRepository).existsByShortUrl("google");
        verify(mockedPasswordEncoder).encode(anyString());
        verify(mockedRepository).save(any());
    }

    @Test
    void createRedirectTestNotAvailable() {
        when(mockedRepository.existsByShortUrl(anyString())).thenReturn(true);
        assertThrows(
                UrlNotAvailableException.class,
                () -> service.createRedirect("google", "https://google.com", "123")
        );
    }

    @Test
    void createRedirectTestRecursive() {
        assertThrows(
                RecursiveRedirectException.class,
                () -> service.createRedirect("google", APP_DOMAIN, "123")
        );
    }

    @Test
    void deleteRedirectTestSuccess() {
        Redirect redirect = new Redirect("google", "https://google.com", "123");
        when(mockedRepository.findByShortUrl(anyString())).thenReturn(Optional.of(redirect));
        when(mockedPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);

        service.deleteRedirect(redirect.getShortUrl(), redirect.getPassword());
        verify(mockedRepository).deleteById(redirect.getShortUrl());
    }

    @Test
    void deleteRedirectTestNotFound() {
        when(mockedRepository.findByShortUrl(anyString())).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> service.deleteRedirect("google", "123")
        );
    }

    @Test
    void deleteRedirectTestAccessDenied() {
        Redirect redirect = new Redirect("google", "https://google.com", null);
        when(mockedRepository.findByShortUrl(anyString())).thenReturn(Optional.of(redirect));
        when(mockedPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThrows(
                WrongPasswordException.class,
                () -> service.deleteRedirect("google", "123")
        );
    }

    @Test
    void getRedirectUrlTestSuccess() {
        Redirect redirect = new Redirect("google", "https://google.com", null);
        when(mockedRepository.findByShortUrl(anyString())).thenReturn(Optional.of(redirect));

        String destination = service.getRedirectUrl(redirect.getShortUrl());
        assertEquals(redirect.getDestination(), destination);
    }

    @Test
    void getRedirectUrlTestNotPresent() {
        when(mockedRepository.findByShortUrl(anyString())).thenReturn(Optional.empty());

        String destination = service.getRedirectUrl("google");
        assertEquals("/home", destination);
    }

    @RepeatedTest(10)
    void generateRandomUrlTests() {
        String url = service.generateRandomUrl();
        assertTrue(url.matches("\\w{5}"));
    }
}