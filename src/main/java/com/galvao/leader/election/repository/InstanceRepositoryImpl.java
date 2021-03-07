package com.galvao.leader.election.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Map;

@Repository
public class InstanceRepositoryImpl implements InstanceRepository {

    public static final String INSTANCE_KEY = "INSTANCE";

    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations                hashOperations;

    @Autowired
    public InstanceRepositoryImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void save(String instanceId) {
        redisTemplate.expire(INSTANCE_KEY, Duration.ofDays(1));
        hashOperations.put(INSTANCE_KEY, instanceId, instanceId);
    }

    @Override
    public Map findAll() {
        return hashOperations.entries(INSTANCE_KEY);
    }

    @Override
    public void delete(String instanceId) {
        hashOperations.delete(INSTANCE_KEY, instanceId);
    }

}