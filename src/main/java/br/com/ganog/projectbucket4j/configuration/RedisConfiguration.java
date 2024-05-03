package br.com.ganog.projectbucket4j.configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

import static io.github.bucket4j.distributed.ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax;
import static io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager.builderFor;
import static io.lettuce.core.codec.ByteArrayCodec.INSTANCE;
import static io.lettuce.core.codec.RedisCodec.of;
import static io.lettuce.core.codec.StringCodec.UTF8;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;

@Configuration
public class RedisConfiguration {

    private RedisClient redisClient() {
        return RedisClient.create(RedisURI.builder()
                .withHost("localhost")
                .withPort(6379)
                .withSsl(false)
                .build());
    }

    @Bean
    public ProxyManager<String> lettuceBasedProxyManager() {
        RedisClient redisClient = redisClient();

        StatefulRedisConnection<String, byte[]> redisConnection = redisClient
                .connect(of(UTF8, INSTANCE));

        return builderFor(redisConnection)
                .withExpirationStrategy(
                        basedOnTimeForRefillingBucketUpToMax(ofSeconds(300)))
                .build();
    }

    @Bean
    public Supplier<BucketConfiguration> bucketConfiguration() {
        return () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.simple(3, ofMinutes(5)))
                .build();
    }
}