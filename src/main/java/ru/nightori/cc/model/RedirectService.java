package ru.nightori.cc.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nightori.cc.Config;
import ru.nightori.cc.RandomGenerator;
import ru.nightori.cc.exceptions.*;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static ru.nightori.cc.Config.APP_DOMAIN;

@Service
public class RedirectService {

	@Autowired
	RedirectRepository redirectRepository;

	@Autowired
	BCryptPasswordEncoder encoder;

	@Autowired
	RandomGenerator generator;

	public String createRedirect(String shortUrl, String destination, String password) {
		// redirects to redirects are not allowed
		if (destination.contains(APP_DOMAIN)) {
			throw new RecursiveRedirectException("Recursive redirect to \""+destination+"\n");
		}

		// generate a random URL if it wasn't set
		// if it was set but it's not available, throw an error
		if (shortUrl.isEmpty()) shortUrl = generator.getRandomUrl();
		else if (Config.RESERVED_URLS.contains(shortUrl) || redirectRepository.existsByShortUrl(shortUrl)) {
			throw new UrlNotAvailableException("\"" + shortUrl + "\" is not available");
		}

		// result is either "shortUrl" or "shortUrl;generatedPassword"
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
		// get the requested redirect and if it doesn't exist, throw an error
		Optional<Redirect> redirectOptional = redirectRepository.findByShortUrl(shortUrl);
		Redirect redirect = redirectOptional.orElseThrow(EntityNotFoundException::new);

		// if the password hashes don't match, throw an error
		if (!encoder.matches(password, redirect.getPassword())) {
			throw new WrongPasswordException("Wrong password for \""+shortUrl+"\"");
		}
		else {
			redirectRepository.deleteById(shortUrl);
		}
	}

	// get destination URL from redirect url
	@Cacheable("redirectCache")
	public String getRedirectUrl(String shortUrl) {
		// if it doesn't exist, redirect to main page instead
		Optional<Redirect> redirectOpt = redirectRepository.findByShortUrl(shortUrl);
		return redirectOpt.isPresent() ? redirectOpt.get().getDestination() : "/home";
	}

}
