package ru.acron.eurekaclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * Метод принимает RedisConnectionFactory как параметр, который используется для установления соединений
     * с сервером Redis.
     * Создается экземпляр RedisTemplate<String, Object>.
     * Настраивается сериализация для ключей с использованием StringRedisSerializer.
     * Настраивается сериализация для значений с использованием GenericJackson2JsonRedisSerializer,
     * который сериализует значения в формат JSON.
     * Возвращает настроенный экземпляр RedisTemplate.
     */

    // Установка Redis с помощью
    // docker run --name redis -d -p 6379:6379 redis
    // docker exec -it redis redis-cli - подключение к Redis
    // keys * - просмотр какие запросы в базе
    // docker ps - проверка запущен ли контейнер

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // Сериализация для ключа (String)
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // Сериализация для значения (в формате JSON)
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }
}