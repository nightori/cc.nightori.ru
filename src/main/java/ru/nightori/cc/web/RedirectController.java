package ru.nightori.cc.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.nightori.cc.service.RedirectService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Pattern;

import static ru.nightori.cc.CcApplication.APP_DOMAIN;

// this is intended to be run behind a reverse proxy
// that's why clients' IP addresses are obtained from 'x-forwarded-for' header
// if it's not present, regular remote address is used instead

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class RedirectController {

	private final RedirectService redirectService;

	// logging for all creation and deletion operations
	@Value("${config.logging-enabled}")
	private boolean loggingEnabled;

	// redirect from main page to /home
	// /home is served statically by web-server and contains the frontend
	@GetMapping(path="/")
	public void homeRedirect(HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader("Location", "/home");
		httpServletResponse.setStatus(HttpStatus.MOVED_PERMANENTLY.value());
	}

	// create a new redirect
	@PostMapping(path="/api")
	public String createRedirect(
			@RequestParam @Pattern(regexp = "^[A-Za-z0-9\\-_]*$") String shortUrl,
			@RequestParam @Pattern(regexp = "^https?://[^\\s]+$") String destination,
			@RequestParam @Pattern(regexp = "^[A-Za-z0-9]*$") String password)
	{
		String url = redirectService.createRedirect(shortUrl, destination, password);
		if (loggingEnabled) log.info("Created: \"" + url + "\"");
		return "https://" + APP_DOMAIN + "/" + url;
	}

	// delete an existing redirect with a password
	@DeleteMapping(path="/api")
	public void deleteRedirect(
			@RequestParam @Pattern(regexp = "^[A-Za-z0-9\\-_]+$") String shortUrl,
			@RequestParam @Pattern(regexp = "^[A-Za-z0-9]+$") String password)
	{
		redirectService.deleteRedirect(shortUrl, password);
		if (loggingEnabled) log.info("Deleted: \"" + shortUrl + "\"");
	}

	// all GET requests to /* are assumed to be redirects
	// this gets the destination (from DB or cache) and redirects the client to it
	@GetMapping(path="/{url}")
	public void redirect(
			@PathVariable("url") String url,
			HttpServletResponse httpServletResponse)
	{
		String location = redirectService.getRedirectUrl(url);
		httpServletResponse.setHeader("Location", location);
		httpServletResponse.setStatus(HttpStatus.MOVED_PERMANENTLY.value());
	}

}
