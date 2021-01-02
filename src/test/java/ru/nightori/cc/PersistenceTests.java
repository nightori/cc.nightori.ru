package ru.nightori.cc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.nightori.cc.model.Redirect;
import ru.nightori.cc.model.RedirectRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PersistenceTests {

    final String TEST_SHORT_URL = "google";
    final String TEST_DESTINATION = "https://google.com";

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    RedirectRepository redirectRepository;

    @BeforeEach
    void setUp() {
        Redirect redirect = new Redirect(TEST_SHORT_URL, TEST_DESTINATION,null);
        entityManager.persist(redirect);
        entityManager.flush();
    }

    @Test
    void findByShortUrlTest() {
        Optional<Redirect> opt = redirectRepository.findByShortUrl(TEST_SHORT_URL);
        assert opt.isPresent();
        assertEquals(TEST_DESTINATION, opt.get().getDestination());
    }

    @Test
    void existsByShortUrlTest() {
        boolean exists = redirectRepository.existsByShortUrl(TEST_SHORT_URL);
        assertTrue(exists);
    }
}