package ru.nightori.cc;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedirectRepository extends CrudRepository<Redirect, String> {
	Optional<Redirect> findByShortUrl(String shortUrl);
	boolean existsByShortUrl(String shortUrl);
}
