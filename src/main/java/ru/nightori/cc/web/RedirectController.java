package ru.nightori.cc.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.nightori.cc.Config;
import ru.nightori.cc.model.RedirectService;
import ru.nightori.cc.exceptions.IllegalHeaderException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Pattern;

import static ru.nightori.cc.Config.APP_DOMAIN;

//	this is intended to be run behind a reverse proxy
//	that's why clients IPs are obtained by reading 'x-forwarded-for' header
//	if it's not present, regular remote address is used instead

@RestController
@Validated
public class RedirectController {

	@Autowired
	RedirectService redirectService;

	@Autowired
    ClientCacheService clientCacheService;

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
			HttpServletRequest request,
			@RequestParam @Pattern(regexp = "^[A-Za-z0-9\\-_]*$") String shortUrl,
			@RequestParam @Pattern(regexp = "^https?://[^\\s]+$") String destination,
			@RequestParam @Pattern(regexp = "^[A-Za-z0-9]*$") String password
	) {
		String ip = getIpAddress(request);
		clientCacheService.tryAccess(ip);
		String url = redirectService.createRedirect(shortUrl, destination, password);
		if (Config.LOGGING_ENABLED) {
			logger.info("Created: \"" + url + "\" ["+ip+"]");
		}
		return "https://" + APP_DOMAIN + "/" + url;
	}

	// delete an existing redirect with the password
	@DeleteMapping(path="/api")
	public void deleteRedirect(
			HttpServletRequest request,
			@RequestParam @Pattern(regexp = "^[A-Za-z0-9\\-_]+$") String shortUrl,
			@RequestParam @Pattern(regexp = "^[A-Za-z0-9]+$") String password
	) {
		String ip = getIpAddress(request);
		clientCacheService.tryAccess(ip);
		redirectService.deleteRedirect(shortUrl, password);
		if (Config.LOGGING_ENABLED) {
			logger.info("Deleted: \"" + shortUrl + "\" ["+ip+"]");
		}
	}

	// all GET requests to /* are assumed to be redirects
	// this gets the destination (from DB or cache) and redirects the client to it
	@GetMapping(path="/{url}")
	public void redirect(
			@PathVariable("url") String url,
			HttpServletResponse httpServletResponse
	) {
		// reserved URLs should not be treated as redirects
		if (Config.RESERVED_URLS.contains(url)) {
			httpServletResponse.setStatus(404);
		}
		else {
			String location = redirectService.getRedirectUrl(url);
			httpServletResponse.setHeader("Location", location);
			httpServletResponse.setStatus(302);
		}
	}

	// helper function to get client's IP address
	private String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip != null) {
			String[] ips = ip.split(",");

			// this is a way to detect header tampering
			// if everything's legit, there should be only 2 IPs here
			if (ips.length != 2) {
				throw new IllegalHeaderException("Illegal ip header: "+ip);
			}

			// the first one belongs to the client
			return ips[0];
		}
		else {
			return request.getRemoteAddr();
		}
	}

}
