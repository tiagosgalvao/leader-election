package com.galvao.leader.election.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDUtils {

    public UUID generateUUID() {
        return UUID.randomUUID();
    }

    public String generateUUIDString() {
        return UUID.randomUUID().toString();
    }

}
