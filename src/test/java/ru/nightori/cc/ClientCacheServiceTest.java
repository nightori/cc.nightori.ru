package ru.nightori.cc;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.nightori.cc.exceptions.LimitExceededException;
import ru.nightori.cc.service.ClientCacheService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ClientCacheService.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)

class ClientCacheServiceTest {

    @Autowired
    ClientCacheService service;

    @Test
    void grantedTest() {
        // different IP addresses, should complete without errors
        service.tryAccess("127.0.0.1");
        service.tryAccess("192.168.0.1");
        service.tryAccess("98.81.10.231");
    }

    @Test
    void deniedTest() {
        // add IP address to the cache
        service.tryAccess("1.2.3.4");

        // it's still in the cache, an exception should be thrown
        assertThrows(LimitExceededException.class, () -> service.tryAccess("1.2.3.4"));
    }
}