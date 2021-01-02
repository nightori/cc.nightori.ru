package ru.nightori.cc;

import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.nightori.cc.model.RedirectRepository;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static ru.nightori.cc.RandomGenerator.GENERATED_PASSWORD_LENGTH;
import static ru.nightori.cc.RandomGenerator.GENERATED_URL_LENGTH;

@SpringBootTest(classes = RandomGenerator.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)

public class RandomGeneratorTests {

    @Autowired
    RandomGenerator generator;

    @MockBean
    RedirectRepository mockedRepository;

    @RepeatedTest(10)
    void generateRandomUrlTests() {
        when(mockedRepository.existsByShortUrl(anyString())).thenReturn(false);
        String url = generator.getRandomUrl();
        assertTrue(url.matches("\\w{" + GENERATED_URL_LENGTH + "}"));
    }

    @RepeatedTest(10)
    void generateRandomPasswordTests() {
        String password = generator.getRandomPassword();
        assertTrue(password.matches("\\d{" + GENERATED_PASSWORD_LENGTH + "}"));
    }

}
