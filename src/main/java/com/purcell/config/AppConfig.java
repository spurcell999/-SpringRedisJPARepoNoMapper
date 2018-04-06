package com.purcell.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ComponentScan("com.purcell")
public class AppConfig {
    @Bean
    @Qualifier("cloud")
    public RedisConnectionFactory getConnectionFactory() {
        JedisConnectionFactory jRedisConnectionFactory = new JedisConnectionFactory(new JedisPoolConfig());
        jRedisConnectionFactory.setPort(6379);
        jRedisConnectionFactory.setHostName("10.157.138.26");
        return jRedisConnectionFactory;
    }

    @Bean(name="stringRedisTemplate")
    public StringRedisTemplate getStringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(getConnectionFactory());
        return stringRedisTemplate;
    }

    @Bean(name = "redisTemplate")
    public <String,V> RedisTemplate<String,V> getRedisTemplate(){
        RedisTemplate<String,V> redisTemplate =  new RedisTemplate<String, V>();
        redisTemplate.setConnectionFactory(getConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
