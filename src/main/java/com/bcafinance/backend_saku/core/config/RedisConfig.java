package com.bcafinance.backend_saku.core.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${app.redis.host:localhost}")
    private String redisHost;

    @Value("${app.redis.port:6379}")
    private int redisPort;

    @Value("${app.redis.password:}")
    private String redisPassword;

    @Value("${app.redis.username:}")
    private String redisUsername;

    @Value("${app.redis.timeout:2000ms}")
    private Duration redisTimeout;

    @Value("${app.redis.key-prefix:saku}")
    private String keyPrefix;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration();
        serverConfig.setHostName(redisHost != null && !redisHost.isBlank() ? redisHost : "localhost");
        serverConfig.setPort(redisPort > 0 ? redisPort : 6379);

        if (redisPassword != null && !redisPassword.isBlank()) {
            serverConfig.setPassword(redisPassword);
        }
        if (redisUsername != null && !redisUsername.isBlank()) {
            serverConfig.setUsername(redisUsername);
        }

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(redisTimeout != null ? redisTimeout : Duration.ofMillis(2000))
                .build();

        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisSerializer<Object> jsonSerializer = RedisSerializer.json();

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        String prefix = (keyPrefix != null && !keyPrefix.isBlank() ? keyPrefix : "saku") + "::";

        RedisSerializer<Object> jsonSerializer = RedisSerializer.json();

        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(15))
                .disableCachingNullValues()
                .prefixCacheNameWith(prefix)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 1. Dashboard Statistics Cache (TTL 3 Menit)
        cacheConfigurations.put("superadminDashboardStats", defaultCacheConfig.entryTtl(Duration.ofMinutes(3)));
        cacheConfigurations.put("bmDashboardStats", defaultCacheConfig.entryTtl(Duration.ofMinutes(3)));
        cacheConfigurations.put("marketingDashboardStats", defaultCacheConfig.entryTtl(Duration.ofMinutes(3)));
        cacheConfigurations.put("backofficeDashboardStats", defaultCacheConfig.entryTtl(Duration.ofMinutes(3)));

        // 2. Master Data Cache (TTL 60 Menit)
        cacheConfigurations.put("cabangList", defaultCacheConfig.entryTtl(Duration.ofMinutes(60)));
        cacheConfigurations.put("plafondList", defaultCacheConfig.entryTtl(Duration.ofMinutes(60)));
        cacheConfigurations.put("roleList", defaultCacheConfig.entryTtl(Duration.ofMinutes(60)));
        cacheConfigurations.put("menuList", defaultCacheConfig.entryTtl(Duration.ofMinutes(60)));

        // 3. Recent Audit Log Cache (TTL 1 Menit)
        cacheConfigurations.put("recentAuditLogs", defaultCacheConfig.entryTtl(Duration.ofMinutes(1)));

        // 4. Public Endpoints Cache
        cacheConfigurations.put("publicPlafonds", defaultCacheConfig.entryTtl(Duration.ofMinutes(60)));
        cacheConfigurations.put("simulasiPinjaman", defaultCacheConfig.entryTtl(Duration.ofMinutes(30)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

}
