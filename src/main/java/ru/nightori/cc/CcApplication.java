package ru.nightori.cc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CcApplication {

    // domain name for the web application
    public final static String APP_DOMAIN = "cc.nightori.ru";

	public static void main(String[] args) {
		SpringApplication.run(CcApplication.class, args);
	}

}
