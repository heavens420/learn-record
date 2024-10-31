package com.zlx.demo.tokenbucket.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DoRequest {

    @Resource
    private RedisTemplate redisTemplate;

    @Value("${redis.limt.rate:1}")
    private int rate;

    @Value("${redis.limt.capacity:3}")
    private int capacity;

    @Value("${redis.limt.tokens:3}")
    private int tokens;

    @Value("${redis.limt.maxCount:5}")
    private int maxCount;

    private static final String SCRIPT = "local tokens_key = KEYS[1]\n" +
            "local timestamp_key = KEYS[2]\n" +
            "local rate = tonumber(ARGV[1])\n" +
            "local capacity = tonumber(ARGV[2])\n" +
            "local now = tonumber(ARGV[3])\n" +
            "local requested = tonumber(ARGV[4])\n" +
            "local fill_time = capacity/rate\n" +
            "local ttl = math.floor(fill_time*2)\n" +
            "local last_tokens = tonumber(redis.call('get', tokens_key))\n" +
            "if last_tokens == nil then\n" +
            "  last_tokens = capacity\n" +
            "end\n" +
            "local last_refreshed = tonumber(redis.call('get', timestamp_key))\n" +
            "if last_refreshed == nil then\n" +
            "  last_refreshed = 0\n" +
            "end\n" +
            "local diff_time = math.max(0, now-last_refreshed)\n" +
            "local filled_tokens = math.min(capacity, last_tokens+(diff_time*rate))\n" +
            "local allowed = filled_tokens >= requested\n" +
            "local new_tokens = filled_tokens\n" +
            "local allowed_num = 0\n" +
            "if allowed then\n" +
            "  new_tokens = filled_tokens - requested\n" +
            "  allowed_num = 1\n" +
            "end\n" +
            "if ttl > 0 then\n" +
            "  redis.call('setex', tokens_key, ttl, new_tokens)\n" +
            "  redis.call('setex', timestamp_key, ttl, now)\n" +
            "end\n" +
            "return allowed_num\n";

    private final String SCRIPT2 = "\n" +
            "\t\n" +
            "local tokens_key = KEYS[1]\n" +
            "\n" +
            "local timestamp_key = KEYS[2]\n" +
            "\n" +
            "local count_key = KEYS[3]\n" +
            "\n" +
            "local rate = tonumber(ARGV[1])\n" +
            "\n" +
            "local capacity = tonumber(ARGV[2])\n" +
            "\n" +
            "local now = tonumber(ARGV[3])\n" +
            "\n" +
            "local requested = tonumber(ARGV[4])\n" +
            "\n" +
            "local min_max = tonumber(ARGV[5])\n" +
            "\n" +
            "local fill_time = capacity/rate\n" +
            "\n" +
            "local ttl = math.floor(fill_time*2)\n" +
            "\n" +
            "local has_count = tonumber(redis.call('get', count_key))\n" +
            "\n" +
            "if has_count == nil then\n" +
            "\n" +
            "  has_count = 0\n" +
            "\n" +
            "end\n" +
            "\n" +
            "if has_count >= min_max then\n" +
            "\n" +
            "return 0\n" +
            "\n" +
            "end\n" +
            "\n" +
            "local last_tokens = tonumber(redis.call('get', tokens_key))\n" +
            "\n" +
            "if last_tokens == nil then\n" +
            "\n" +
            "  last_tokens = capacity\n" +
            "\n" +
            "end\n" +
            "\n" +
            "local last_refreshed = tonumber(redis.call('get', timestamp_key))\n" +
            "\n" +
            "if last_refreshed == nil then\n" +
            "\n" +
            "  last_refreshed = 0\n" +
            "\n" +
            "end\n" +
            "\n" +
            "local diff_time = math.max(0, now-last_refreshed)\n" +
            "\n" +
            "local filled_tokens = math.min(capacity, last_tokens+(diff_time*rate))\n" +
            "\n" +
            "local allowed = filled_tokens >= requested\n" +
            "\n" +
            "local new_tokens = filled_tokens\n" +
            "\n" +
            "local allowed_num = 0\n" +
            "\n" +
            "if allowed then\n" +
            "\n" +
            "  new_tokens = filled_tokens - requested\n" +
            "\n" +
            "  allowed_num = 1\n" +
            "\n" +
            "end\n" +
            "\n" +
            "if ttl > 0 then\n" +
            "\n" +
            "  redis.call('setex', tokens_key, ttl, new_tokens)\n" +
            "\n" +
            "  redis.call('setex', timestamp_key, ttl, now)\n" +
            "\n" +
            "end\n" +
            "\n" +
            "local count_ttl = tonumber(redis.call('ttl',count_key))\n" +
            "\n" +
            "if count_ttl < 0 then\n" +
            "\n" +
            "  count_ttl = fill_time\n" +
            "\n" +
            "end\n" +
            "\n" +
            "redis.call('setex', count_key,count_ttl , has_count+1)\n" +
            "\n" +
            "return allowed_num\n";

    private final String SCRIPT3 = "-- Lua 脚本实现令牌桶算法\n" +
            "-- key 为 Redis 中用于存储桶的键\n" +
            "-- rate 为令牌填充速率（每秒填充的令牌数）\n" +
            "-- capacity 为桶的容量\n" +
            "-- now 为当前时间戳\n" +
            "-- permits 为本次请求需要的令牌数\n" +
            " \n" +
            "local key = KEYS[1]\n" +
            "local rate = tonumber(ARGV[1])\n" +
            "local capacity = tonumber(ARGV[2])\n" +
            "local now = tonumber(ARGV[3])\n" +
            "local permits = tonumber(ARGV[4])\n" +
            " \n" +
            "local bucket = redis.call('hmget', key, 'lastRefillTime', 'tokens')\n" +
            "local lastRefillTime = tonumber(bucket[1])\n" +
            "local tokens = tonumber(bucket[2])\n" +
            " \n" +
            "if lastRefillTime == nil then\n" +
            "  lastRefillTime = now\n" +
            "  tokens = capacity\n" +
            "end\n" +
            " \n" +
            "-- 计算自上次填充以来经过的时间\n" +
            "local delta = math.max(0, now - lastRefillTime)\n" +
            "-- 计算应该填充的令牌数\n" +
            "local refillTokens = math.floor(delta * rate)\n" +
            "tokens = math.min(capacity, tokens + refillTokens)\n" +
            "lastRefillTime = now\n" +
            " \n" +
            "local enoughTokens = false\n" +
            "if tokens >= permits then\n" +
            "  enoughTokens = true\n" +
            "  tokens = tokens - permits\n" +
            "end\n" +
            " \n" +
            "-- 更新桶的状态\n" +
            "redis.call('hmset', key, 'lastRefillTime', lastRefillTime, 'tokens', tokens)\n" +
            " \n" +
            "-- 设置过期时间防止无限增长\n" +
            "redis.call('expire', key, math.ceil(capacity/rate)*2)\n" +
            " \n" +
            "if enoughTokens then\n" +
            "  return 1\n" +
            "else\n" +
            "  return 0\n" +
            "end";
    public boolean isAllowed(String id) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(SCRIPT3, Long.class);
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        Object execute = redisTemplate.execute(redisScript, Collections.singletonList(id), rate, capacity, Instant.now().getEpochSecond(), tokens);
        return "1".equals(execute+"");
    }

    private List<String> getKey(String id) {
        String prefix = "limiter:" + id;
        String tokenKey = prefix + ":tokens";
        String timestampKey = prefix + ":timestamp";
        String countKey = prefix + ":count";
        return Arrays.asList(tokenKey, timestampKey, countKey);
    }
}
