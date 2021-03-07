package com.galvao.leader.election.kafka.message;

import com.galvao.leader.election.enums.LeadershipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AppInstanceKafkaDto {

    private String           instanceId;
    private LeadershipStatus leadershipStatus;

}