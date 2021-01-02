package ru.nightori.cc;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.nightori.cc.exceptions.LimitExceededException;
import ru.nightori.cc.web.ClientCacheService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ClientCacheService.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)

class ClientCacheServiceTests {

    @Autowired
    ClientCacheService service;

    @Test
    void grantedTest() {
        // different IPs, should complete without errors
        service.tryAccess("127.0.0.1");
        service.tryAccess("192.168.0.1");
        service.tryAccess("98.81.10.231");
    }

    @Test
    void deniedTest() {
        service.tryAccess("1.2.3.4");
        // this IP is in the cache, so an exception must be thrown
        assertThrows(LimitExceededException.class, () -> service.tryAccess("1.2.3.4"));
    }
}