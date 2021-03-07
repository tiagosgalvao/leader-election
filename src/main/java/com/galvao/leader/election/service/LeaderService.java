package com.galvao.leader.election.service;

import com.galvao.leader.election.kafka.message.AppInstanceKafkaDto;
import com.galvao.leader.election.kafka.producer.LeadershipProducerService;
import com.galvao.leader.election.model.AppInstanceSingleton;
import com.galvao.leader.election.repository.LeaderRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Random;

import static com.galvao.leader.election.enums.LeadershipStatus.LEADER;
import static com.galvao.leader.election.enums.LeadershipStatus.NON_LEADER;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderService {

    private final LeadershipProducerService leadershipProducerService;
    private final LeaderRepository          leaderRepository;
    private final Gson                      gson;

    public String getLeader() {
        return leaderRepository.getLeader();
    }

    public void clearLeader() {
        leaderRepository.clearLeader();
        leadershipProducerService.sendAsyncMessageLeadershipLost();
    }

    public void setLeadership() {
        AppInstanceSingleton appInstanceSingleton = AppInstanceSingleton.getInstance();
        var instanceId = appInstanceSingleton.getInstanceId();
        log.debug("new instanceId:{}", instanceId);
        String leader = leaderRepository.getLeader();
        if (leader == null) {
            log.info("new leader elected:{}", instanceId);
            appInstanceSingleton.setLeadershipStatus(LEADER);
            leaderRepository.save(instanceId);
            waitRandomInterval();
            leadershipProducerService.sendAsyncMessageLeadershipObtained();
        } else if (LEADER == appInstanceSingleton.getLeadershipStatus()) {
            if (leader.equals(instanceId)) {
                log.info("update redis leader KEY TTL to N*2 seconds");
                leaderRepository.saveUpdateTTL(instanceId);
            } else {
                log.info("trigger LeadershipLost callbacks");
                leadershipProducerService.sendAsyncMessageLeadershipLost();
            }
        }
    }

    private void waitRandomInterval() {
        Random random = new Random();
        int low = 1;
        int high = 3;
        try {
            Thread.sleep(random.nextInt(high - low) + low);
        } catch (InterruptedException e) {
            log.error("error waiting random interval, detail:{}", e.getMessage());
        }
    }

    public void checkReleaseLeadership() {
        AppInstanceSingleton appInstanceSingleton = AppInstanceSingleton.getInstance();
        String leader = leaderRepository.getLeader();
        log.debug("actual leader: " + leader);
        log.debug("current instance: " + appInstanceSingleton.getInstanceId());
        log.debug("Check to remove leader");
        if (appInstanceSingleton.getInstanceId().equals(leader)) {
            log.info("This instance was leader... leadership is free now");
            appInstanceSingleton.setLeadershipStatus(NON_LEADER);
            leaderRepository.clearLeader();
            leadershipProducerService.sendAsyncMessageLeadershipLost();
        } else {
            log.info("Didn't remove from leadership because this instance is not the leader");
        }
    }

    @KafkaListener(topics = "${kafka.topic.leadership-lost.name}", groupId = "${kafka.topic.leadership-lost.consumer.group}")
    public void listenLeadershipLost(@RequestBody String previous) {
        log.info("Received Message previous leader:{}: ", previous);
        setLeadership();
    }

    @KafkaListener(topics = "${kafka.topic.leadership-obtained.name}", groupId = "${kafka.topic.leadership-obtained.consumer.group}")
    public void listenLeadershipObtained(@RequestBody String newLeader) {
        log.info("Received Message new leader{}: ", newLeader);
        AppInstanceSingleton appInstanceSingleton = AppInstanceSingleton.getInstance();
        AppInstanceKafkaDto previousApp = gson.fromJson(newLeader, AppInstanceKafkaDto.class);
        if (previousApp.getInstanceId().equals(appInstanceSingleton.getInstanceId())) {
            appInstanceSingleton.setLeadershipStatus(LEADER);
        } else {
            appInstanceSingleton.setLeadershipStatus(NON_LEADER);
        }
    }

}
