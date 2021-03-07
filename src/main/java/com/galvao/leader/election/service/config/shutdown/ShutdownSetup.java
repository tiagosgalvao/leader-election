package com.galvao.leader.election.service.config.shutdown;

import com.galvao.leader.election.model.AppInstanceSingleton;
import com.galvao.leader.election.service.InstanceService;
import com.galvao.leader.election.service.LeaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShutdownSetup {

    private final LeaderService   leaderService;
    private final InstanceService instanceService;

    @PreDestroy
    public void onDestroy() {
        log.info("Spring Container is destroyed!");
        leaderService.checkReleaseLeadership();
        instanceService.delete(AppInstanceSingleton.getInstance().getInstanceId());
    }

}