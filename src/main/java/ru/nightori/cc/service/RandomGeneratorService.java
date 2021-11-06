package ru.nightori.cc.service;

import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.nightori.cc.model.RedirectRepository;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class RandomGeneratorService {

	private final RedirectRepository redirectRepository;

	@Value("${config.generator.url.length}")
	private int generatedUrlLength;

	@Value("${config.generator.password.length}")
	private int generatedPasswordLength;

	// generate a random URL and make sure it's unique
	public String getRandomUrl() {
		String url;
		do {
			url = RandomString.make(generatedUrlLength);
		}
		while (redirectRepository.existsByShortUrl(url));
		return url;
	}

	// generate a random numeric password
	public String getRandomPassword() {
		Random random = new Random();
		int bound = (int) (Math.pow(10, generatedPasswordLength) - 1);
		int password = random.nextInt(bound) + 1;
		return String.format("%04d", password);
	}

}
