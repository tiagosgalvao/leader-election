package com.galvao.leader.election.service.config.shutdown;

import com.galvao.leader.election.service.InstanceService;
import com.galvao.leader.election.service.LeaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ShutdownConfig {

    private final LeaderService   leaderService;
    private final InstanceService instanceService;

    @Bean
    public ShutdownSetup getTerminateBean() {
        return new ShutdownSetup(leaderService, instanceService);
    }

}