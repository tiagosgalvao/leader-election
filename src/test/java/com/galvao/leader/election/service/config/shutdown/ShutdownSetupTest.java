package com.galvao.leader.election.service.config.shutdown;

import com.galvao.leader.election.model.AppInstanceSingleton;
import com.galvao.leader.election.service.InstanceService;
import com.galvao.leader.election.service.LeaderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static com.galvao.leader.election.enums.LeadershipStatus.LEADER;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ShutdownSetupTest {

    private static final String INSTANCE_ID = UUID.randomUUID().toString();

    @InjectMocks
    private ShutdownSetup shutdownSetup;

    @Mock
    private InstanceService instanceService;

    @Mock
    private LeaderService leaderService;

    @Test
    public void onDestroy() {
        try (MockedStatic<AppInstanceSingleton> mocked = mockStatic(AppInstanceSingleton.class)) {
            // Arrange
            AppInstanceSingleton nonStatic = new AppInstanceSingleton(INSTANCE_ID, LEADER);
            mocked.when(AppInstanceSingleton::getInstance).thenReturn(nonStatic);

            // Act
            shutdownSetup.onDestroy();

            // Assert
            verify(leaderService).checkReleaseLeadership();
            verify(instanceService).delete(INSTANCE_ID);
        }
    }

}
