package ru.nightori.cc;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
public class Redirect {

	@Id
	@NotNull
	@Pattern(regexp = "^[A-Za-z0-9\\-_]+$")
	private String shortUrl;

	@NotNull
	@Pattern(regexp = "^https?://[^\\s]+$")
	private String destination;

	// can be null and store anything, it's encoded anyway
	private String password;

	// empty default constructor to let Spring magic work
	public Redirect() {}

	// constructor to set all fields quickly because setters are lame
	public Redirect(String shortUrl, String destination, String password) {
		this.shortUrl = shortUrl;
		this.destination = destination;
		this.password = password;
	}

	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
