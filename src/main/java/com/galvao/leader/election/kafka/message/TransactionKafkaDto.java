package com.galvao.leader.election.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class TransactionKafkaDto {

    private String  instanceId;
    private Instant time;

}