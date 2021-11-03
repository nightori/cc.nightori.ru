package ru.nightori.cc.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedirectRepository extends CrudRepository<Redirect, String> {
	Optional<Redirect> findByShortUrl(String shortUrl);
	boolean existsByShortUrl(String shortUrl);
}
