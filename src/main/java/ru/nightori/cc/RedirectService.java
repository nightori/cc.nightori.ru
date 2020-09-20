package ru.nightori.cc;

import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nightori.cc.config.Config;
import ru.nightori.cc.exception.*;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static ru.nightori.cc.config.Config.APP_DOMAIN;

@Service
public class RedirectService {

	@Autowired
	RedirectRepository redirectRepository;

	@Autowired
	BCryptPasswordEncoder encoder;

	public String createRedirect(String shortUrl, String destination, String password) {
		// redirects to redirects are not allowed
		if (destination.contains(APP_DOMAIN)) {
			throw new RecursiveRedirectException("Recursive redirect to \""+destination+"\n");
		}

		// generate a random URL if the custom one was not provided
		// if it was provided but it's not available, throw an error
		if (shortUrl.isEmpty()) shortUrl = generateRandomUrl();
		else if (Config.RESERVED_URLS.contains(shortUrl) || redirectRepository.existsByShortUrl(shortUrl)) {
			throw new UrlNotAvailableException("\"" + shortUrl + "\" is not available");
		}

		// make a hash and store it instead of the password (obviously)
		String encoded = password.isEmpty() ? null : encoder.encode(password);
		Redirect redirect = new Redirect(shortUrl, destination, encoded);
		redirectRepository.save(redirect);
		return shortUrl;
	}

	public void deleteRedirect(String shortUrl, String password) {
		// get the requested redirect and if it doesn't exist, throw an error
		Optional<Redirect> redirectOptional = redirectRepository.findByShortUrl(shortUrl);
		Redirect redirect = redirectOptional.orElseThrow(EntityNotFoundException::new);
		// if the passwords don't match or if it wasn't set in the first place...
		// yeah, you guessed it - throw an error
		if (redirect.getPassword() == null || !encoder.matches(password, redirect.getPassword())) {
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

	// generate a random URL and make sure it's unique
	public String generateRandomUrl() {
		String url;
		do {
			url = RandomString.make(Config.GENERATED_URL_LENGTH);
		}
		while (redirectRepository.existsByShortUrl(url));
		return url;
	}

}
