package ru.nightori.cc.config;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class Config {

	// URL of the deployed web-application
	// it's used to limit request origins in production
	public final static String APP_DOMAIN = "cc.nightori.ru";

	// generated URLs have fixed length set by this constant
	// 5 alphanumeric characters mean 62^5 possibilities
	// that's around a billion so should be enough
	public final static int GENERATED_URL_LENGTH = 5;

	// special URLs that are not available for redirect creation
	public final static List<String> RESERVED_URLS = Arrays.asList("home", "api");

	// allowed HTTP methods
	public final static List<String> ALLOWED_METHODS = Arrays.asList("GET", "POST", "DELETE");

	// API rate limitation
	public final static Duration API_COOLDOWN = Duration.ofSeconds(10);

	// logging for all creation and deletion operations
	public final static boolean LOGGING_ENABLED = true;

}