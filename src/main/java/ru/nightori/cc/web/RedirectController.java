package ru.nightori.cc.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.nightori.cc.model.RedirectService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Pattern;

import static ru.nightori.cc.CcApplication.APP_DOMAIN;

//	this is intended to be run behind a reverse proxy
//	that's why clients IPs are obtained by reading 'x-forwarded-for' header
//	if it's not present, regular remote address is used instead

@RestController
@Validated
public class RedirectController {

	@Autowired
	RedirectService redirectService;

	// logging for all creation and deletion operations
	@Value("${config.logging-enabled}")
	boolean LOGGING_ENABLED;

	static final Logger logger = LoggerFactory.getLogger(RedirectController.class);

	// redirect from main page to /home
	// /home is served statically by web-server and contains the frontend
	@GetMapping(path="/")
	public void homeRedirect(HttpServletResponse httpServletResponse) {
		httpServletResponse.setHeader("Location", "/home");
		httpServletResponse.setStatus(302);
	}

	// create a new redirect
	@PostMapping(path="/api")
	public String createRedirect(
			@RequestParam @Pattern(regexp = "^[A-Za-z0-9\\-_]*$") String shortUrl,
			@RequestParam @Pattern(regexp = "^https?://[^\\s]+$") String destination,
			@RequestParam @Pattern(regexp = "^[A-Za-z0-9]*$") String password)
	{
		String url = redirectService.createRedirect(shortUrl, destination, password);
		if (LOGGING_ENABLED) logger.info("Created: \"" + url + "\"");
		return "https://" + APP_DOMAIN + "/" + url;
	}

	// delete an existing redirect with the password
	@DeleteMapping(path="/api")
	public void deleteRedirect(
			@RequestParam @Pattern(regexp = "^[A-Za-z0-9\\-_]+$") String shortUrl,
			@RequestParam @Pattern(regexp = "^[A-Za-z0-9]+$") String password)
	{
		redirectService.deleteRedirect(shortUrl, password);
		if (LOGGING_ENABLED) logger.info("Deleted: \"" + shortUrl + "\"");
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
		httpServletResponse.setStatus(302);
	}

}
