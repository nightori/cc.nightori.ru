package ru.nightori.cc;

import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.nightori.cc.model.RedirectRepository;
import ru.nightori.cc.service.RandomGeneratorService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = RandomGeneratorService.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)

public class RandomGeneratorServiceTest {

    @Autowired
	RandomGeneratorService generator;

    @MockBean
    RedirectRepository mockRepository;

    @Value("${config.generator.url.length}")
    private int generatedUrlLength;

    @Value("${config.generator.password.length}")
    private int generatedPasswordLength;

    @RepeatedTest(5)
    void generateRandomUrlTests() {
        when(mockRepository.existsByShortUrl(anyString())).thenReturn(false);
        String url = generator.getRandomUrl();
        assertTrue(url.matches("\\w{" + generatedUrlLength + "}"));
    }

    @RepeatedTest(5)
    void generateRandomPasswordTests() {
        String password = generator.getRandomPassword();
        assertTrue(password.matches("\\d{" + generatedPasswordLength + "}"));
    }

}
