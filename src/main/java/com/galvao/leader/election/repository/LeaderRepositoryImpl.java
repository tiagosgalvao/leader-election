package com.galvao.leader.election.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class LeaderRepositoryImpl implements LeaderRepository {

    public static final String LEADER_KEY = "LEADER";

    @Value("${default-caching-ttl-seconds}")
    private Integer defaultTtlSeconds;

    private RedisTemplate<String, String> redisTemplate;
    private HashOperations                hashOperations;

    @Autowired
    public LeaderRepositoryImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void save(String instanceId) {
        redisTemplate.expire(LEADER_KEY, Duration.ofSeconds(defaultTtlSeconds));
        hashOperations.put(LEADER_KEY, LEADER_KEY, instanceId);
    }

    @Override
    public void saveUpdateTTL(String instanceId) {
        redisTemplate.expire(LEADER_KEY, Duration.ofSeconds(defaultTtlSeconds * 2));
        hashOperations.put(LEADER_KEY, LEADER_KEY, instanceId);
    }

    @Override
    public String getLeader() {
        return (String) hashOperations.get(LEADER_KEY, LEADER_KEY);
    }

    @Override
    public void clearLeader() {
        hashOperations.delete(LEADER_KEY, LEADER_KEY);
    }

}