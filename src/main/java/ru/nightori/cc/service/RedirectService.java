package ru.nightori.cc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nightori.cc.exceptions.*;
import ru.nightori.cc.model.Redirect;
import ru.nightori.cc.model.RedirectRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.Set;

import static ru.nightori.cc.CcApplication.APP_DOMAIN;

@Service
@RequiredArgsConstructor
public class RedirectService {

	// special URLs that are not available for redirect creation
	public final static Set<String> RESERVED_URLS = Set.of("home", "api");

	private final RedirectRepository redirectRepository;
	private final BCryptPasswordEncoder encoder;
	private final RandomGeneratorService generator;

	public String createRedirect(String shortUrl, String destination, String password) {
		// redirects to redirects are not allowed
		if (destination.contains(APP_DOMAIN)) {
			throw new RecursiveRedirectException("Recursive redirect to \"" + destination + "\n");
		}

		// generate a random URL if it wasn't set
		if (shortUrl.isEmpty()) {
			shortUrl = generator.getRandomUrl();
		}
		// if it was set but it's unavailable, throw an error
		else if (RESERVED_URLS.contains(shortUrl) || redirectRepository.existsByShortUrl(shortUrl)) {
			throw new UrlNotAvailableException("\"" + shortUrl + "\" is not available");
		}

		// result will be either "shortUrl" or "shortUrl;generatedPassword"
		StringBuilder result = new StringBuilder(shortUrl);

		// generate a password if it wasn't set
		if (password.isEmpty()) {
			password = generator.getRandomPassword();
			result.append(";").append(password);
		}

		// create a redirect and store it
		Redirect redirect = new Redirect(shortUrl, destination, encoder.encode(password));
		redirectRepository.save(redirect);
		return result.toString();
	}

	public void deleteRedirect(String shortUrl, String password) {
		// get the target redirect (and if it doesn't exist, throw an error)
		Optional<Redirect> redirectOptional = redirectRepository.findByShortUrl(shortUrl);
		Redirect redirect = redirectOptional.orElseThrow(EntityNotFoundException::new);

		// check the password
		if (encoder.matches(password, redirect.getPassword())) {
			redirectRepository.deleteById(shortUrl);
		}
		else {
			throw new WrongPasswordException("Wrong password for \"" + shortUrl + "\"");
		}
	}

	// get a destination URL from a redirect URL
	@Cacheable("redirectCache")
	public String getRedirectUrl(String shortUrl) {
		// reserved URLs get redirected to the main page
		if (RESERVED_URLS.contains(shortUrl)) return "/home";

		// nonexistent URLs get redirected to the main page as well
		Optional<Redirect> redirectOpt = redirectRepository.findByShortUrl(shortUrl);
		return redirectOpt.isPresent() ? redirectOpt.get().getDestination() : "/home";
	}

}
