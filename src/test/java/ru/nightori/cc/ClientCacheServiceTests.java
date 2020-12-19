package ru.nightori.cc;

import org.junit.jupiter.api.*;
import ru.nightori.cc.exception.LimitExceededException;

import static org.junit.jupiter.api.Assertions.*;

// not annotated, we don't need Spring context for this
class ClientCacheServiceTests {
    ClientCacheService service;

    @BeforeEach
    void setUp() {
        service = new ClientCacheService();
    }

    @Test
    void grantedTest() {
        // different IPs, should complete without errors
        service.tryAccess("127.0.0.1");
        service.tryAccess("192.168.0.1");
        service.tryAccess("98.81.10.231");
    }

    @Test
    void deniedTest() {
        service.tryAccess("127.0.0.1");
        // this IP is in the cache, so an exception must be thrown
        assertThrows(LimitExceededException.class, () -> service.tryAccess("127.0.0.1"));
    }
}