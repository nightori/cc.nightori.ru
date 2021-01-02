package ru.nightori.cc.web;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.nightori.cc.exceptions.LimitExceededException;

import java.time.Duration;

// this service is used to limit API requests per second
// default limitation: 1 request per second from one IP

@Service
public class ClientCacheService {

	// cache with all clients' IP addresses
	private final Cache<String, Boolean> cache;

	// initialize cache
	public ClientCacheService(@Value("${config.api-cooldown}") int cooldown) {
		Duration ttlExpiration = Duration.ofSeconds(cooldown);
		CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
				.withCache(
						"clientCache",
						CacheConfigurationBuilder.newCacheConfigurationBuilder(
								String.class,
								Boolean.class,
								ResourcePoolsBuilder.newResourcePoolsBuilder()
										.heap(10000, EntryUnit.ENTRIES)
										.offheap(10, MemoryUnit.MB)
						)
						.withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(ttlExpiration))
				)
				.build(true);
		cache = cacheManager.getCache("clientCache", String.class, Boolean.class);
	}

	// when a client sends a request
	public void tryAccess(String ip) {
		// check if the client's ip is in the cache
		if (cache.containsKey(ip)) {
			// it is: the limit is exceeded, error 492 is thrown
			throw new LimitExceededException("Too many requests from " + ip);
		}
		// it isn't: access granted, put it in the cache
		else cache.put(ip, true);
	}

}