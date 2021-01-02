package ru.nightori.cc;

import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.nightori.cc.model.RedirectRepository;

import java.util.Random;

@Service
public class RandomGenerator {

    // generated URLs have fixed length set by this constant
    // 5 alphanumeric characters mean 62^5 possibilities
    // that's around a billion so should be enough
    public static final int GENERATED_URL_LENGTH = 5;

    // the length of auto-generated numeric password
    public static final int GENERATED_PASSWORD_LENGTH = 4;

    @Autowired
    RedirectRepository redirectRepository;

    // generate a random URL and make sure it's unique
    public String getRandomUrl() {
        String url;
        do {
            url = RandomString.make(GENERATED_URL_LENGTH);
        }
        while (redirectRepository.existsByShortUrl(url));
        return url;
    }

    // generate a random numeric password
    public String getRandomPassword() {
        Random random = new Random();
        int bound = (int) (Math.pow(10, GENERATED_PASSWORD_LENGTH) - 1);
        int password = random.nextInt(bound) + 1;
        return String.format("%04d", password);
    }

}
