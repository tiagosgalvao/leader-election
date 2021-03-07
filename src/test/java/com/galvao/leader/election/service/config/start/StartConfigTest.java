package com.galvao.leader.election.service.config.start;

import com.galvao.leader.election.service.InstanceService;
import com.galvao.leader.election.service.LeaderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StartConfigTest {

    @InjectMocks
    private InitialSetup initialSetup;

    @Mock
    private InstanceService instanceService;

    @Mock
    private LeaderService leaderService;

    @Test
    public void init() {
        // Arrange
        // Act
        initialSetup.init();

        // Assert
        verify(instanceService).setInstanceId();
        verify(leaderService).setLeadership();
    }

}
