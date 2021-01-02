package ru.nightori.cc;

import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nightori.cc.model.RedirectRepository;

import java.util.Random;

@Service
public class RandomGenerator {

    @Autowired
    RedirectRepository redirectRepository;

    // generate a random URL and make sure it's unique
    public String getRandomUrl() {
        String url;
        do {
            url = RandomString.make(Config.GENERATED_URL_LENGTH);
        }
        while (redirectRepository.existsByShortUrl(url));
        return url;
    }

    // generate a random numeric password
    public String getRandomPassword() {
        Random random = new Random();
        int password = random.nextInt(9999) + 1;
        return String.format("%04d", password);
    }

}
