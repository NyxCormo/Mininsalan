package insalan.mininsalan.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        cacheManager.setCaches(Arrays.asList(
                // Long-term cache for basic event data (changes infrequently)
                buildCache("events-dates", 1000, 60, TimeUnit.MINUTES),

                // Medium-term cache for summary data (challenge counts may change)
                buildCache("events-summary", 500, 30, TimeUnit.MINUTES),

                // Short-term cache for detailed data (includes dynamic challenge status)
                buildCache("event-details", 200, 15, TimeUnit.MINUTES),

                // Very short cache for time-sensitive active events
                buildCache("active-events", 100, 5, TimeUnit.MINUTES),

                // Medium cache for upcoming events
                buildCache("upcoming-events", 200, 20, TimeUnit.MINUTES),

                // Short cache for challenge-related data
                buildCache("challenges", 500, 10, TimeUnit.MINUTES),

                // Short cache for challenges by event
                buildCache("challenges-by-event", 200, 10, TimeUnit.MINUTES),

                // Short cache for individual challenge
                buildCache("challenge", 200, 10, TimeUnit.MINUTES),

                // Medium cache for game data
                buildCache("games", 500, 20, TimeUnit.MINUTES),

                // Medium cache for individual game
                buildCache("game", 200, 20, TimeUnit.MINUTES),

                // Medium cache for category data
                buildCache("categories", 500, 20, TimeUnit.MINUTES),

                // Medium cache for individual category
                buildCache("category", 200, 20, TimeUnit.MINUTES),

                // Cache for player-related data (if you add player features later)
                buildCache("players", 300, 15, TimeUnit.MINUTES)
        ));

        return cacheManager;
    }

    private CaffeineCache buildCache(String name, int maximumSize, long duration, TimeUnit timeUnit) {
        logger.info("Creating cache '{}' with max size {} and duration {} {}",
                name, maximumSize, duration, timeUnit.name().toLowerCase());

        return new CaffeineCache(name, Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterAccess(duration, timeUnit)
                .removalListener((key, value, cause) -> {
                    logger.debug("Cache '{}' - Removed key '{}' due to: {}", name, key, cause);
                })
                .recordStats()
                .build());
    }
}