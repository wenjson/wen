package com.vin.core.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;

/**
 * Redis 配置类
 * @author Vincent
 *
 */
public class RedisConfig extends CachingConfigurerSupport {

	// private PropertyResolver propertyResolver;
	
	@Value("${spring.redis.host}")
    private String host;
	
    @Value("${spring.redis.port}")
    private int port;
    
    @Value("${spring.redis.password}")
    private String password;
    
    @Value("${spring.redis.timeout}")
    private int timeout;
    
    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;
    
    @Value("${spring.redis.jedis.pool.max-wait}")
    private long maxWaitMillis;
  
	
}
