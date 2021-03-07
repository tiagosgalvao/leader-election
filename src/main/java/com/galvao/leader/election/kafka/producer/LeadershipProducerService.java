package com.galvao.leader.election.kafka.producer;

import com.galvao.leader.election.kafka.message.AppInstanceKafkaDto;
import com.galvao.leader.election.model.AppInstanceSingleton;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeadershipProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Gson                          gson;

    @Value("${kafka.topic.leadership-lost.name}")
    private String leadershipLostTopicName;

    @Value("${kafka.topic.leadership-obtained.name}")
    private String leadershipObtainedTopicName;

    public void sendAsyncMessageLeadershipLost() {
        AppInstanceSingleton appInstanceSingleton = AppInstanceSingleton.getInstance();
        log.info("Leadership lost, instanceId:{} time:{} ", appInstanceSingleton.getInstanceId(), Instant.now());
        AppInstanceKafkaDto dto = AppInstanceKafkaDto.builder()
                                                     .instanceId(appInstanceSingleton.getInstanceId())
                                                     .leadershipStatus(appInstanceSingleton.getLeadershipStatus())
                                                     .build();
        kafkaTemplate.send(leadershipLostTopicName, gson.toJson(dto));
    }

    public void sendAsyncMessageLeadershipObtained() {
        AppInstanceSingleton appInstanceSingleton = AppInstanceSingleton.getInstance();
        log.info("Leadership obtained, instanceId:{} time:{} ", appInstanceSingleton.getInstanceId(), Instant.now());
        AppInstanceKafkaDto dto = AppInstanceKafkaDto.builder()
                                                     .instanceId(appInstanceSingleton.getInstanceId())
                                                     .leadershipStatus(appInstanceSingleton.getLeadershipStatus())
                                                     .build();
        kafkaTemplate.send(leadershipObtainedTopicName, gson.toJson(dto));
    }

}