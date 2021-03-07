package com.galvao.leader.election.kafka.producer;

import com.galvao.leader.election.kafka.message.TransactionKafkaDto;
import com.galvao.leader.election.model.AppInstanceSingleton;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.galvao.leader.election.enums.LeadershipStatus.LEADER;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublisherProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Gson                          gson;

    @Value("${kafka.topic.publisher.name}")
    private String topicName;

    @Scheduled(fixedDelayString = "${publish-interval-milliseconds}")
    public void sendAsyncMessage() {
        AppInstanceSingleton appInstanceSingleton = AppInstanceSingleton.getInstance();
        if (LEADER == appInstanceSingleton.getLeadershipStatus()) {
            Instant now = Instant.now();
            log.info("Sending server time as leader, instanceId:{} time:{} ", appInstanceSingleton.getInstanceId(), now);
            kafkaTemplate.send(topicName, gson.toJson(TransactionKafkaDto.builder().instanceId(appInstanceSingleton.getInstanceId()).time(now).build().toString()));
        } else {
            log.info("Not leader:{} ", appInstanceSingleton.getInstanceId());
        }
    }

}