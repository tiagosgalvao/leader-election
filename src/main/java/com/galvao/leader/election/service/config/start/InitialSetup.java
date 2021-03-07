package com.galvao.leader.election.service.config.start;

import com.galvao.leader.election.service.InstanceService;
import com.galvao.leader.election.service.LeaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Order(0)
public class InitialSetup {

    private final InstanceService instanceService;
    private final LeaderService   leaderService;

    @PostConstruct
    public void init() {
        instanceService.setInstanceId();
        leaderService.setLeadership();
    }

}