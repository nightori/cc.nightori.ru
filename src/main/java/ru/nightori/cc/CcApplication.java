package ru.nightori.cc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableCaching
public class CcApplication {

    // URL of the deployed web-application
    // it's used to limit request origins in production
    public final static String APP_DOMAIN = "cc.nightori.ru";

    // declaring bCrypt here to have it available for injection when we need it
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}

	public static void main(String[] args) {
		SpringApplication.run(CcApplication.class, args);
	}

}
