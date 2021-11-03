package ru.nightori.cc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Redirect {

	@Id
	@NotNull
	@Pattern(regexp = "^[A-Za-z0-9\\-_]+$")
	private String shortUrl;

	@NotNull
	@Pattern(regexp = "^https?://[^\\s]+$")
	private String destination;

	@NotNull
	private String password;

}
