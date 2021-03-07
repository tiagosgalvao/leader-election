package com.galvao.leader.election.service;

import com.galvao.leader.election.kafka.message.AppInstanceKafkaDto;
import com.galvao.leader.election.kafka.producer.LeadershipProducerService;
import com.galvao.leader.election.model.AppInstanceSingleton;
import com.galvao.leader.election.repository.LeaderRepository;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static com.galvao.leader.election.enums.LeadershipStatus.LEADER;
import static com.galvao.leader.election.enums.LeadershipStatus.NON_LEADER;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LeaderServiceTest {

    private static final String INSTANCE_ID = UUID.randomUUID().toString();

    @InjectMocks
    private LeaderService leaderService;

    @Mock
    private LeaderRepository leaderRepository;

    @Mock
    private LeadershipProducerService leadershipProducerService;

    @Mock
    private Gson gson;

    @Test
    public void shouldSetLeadershipNewLeader() {
        try (MockedStatic<AppInstanceSingleton> mocked = mockStatic(AppInstanceSingleton.class)) {
            // Arrange
            AppInstanceSingleton non = new AppInstanceSingleton(INSTANCE_ID, NON_LEADER);
            mocked.when(AppInstanceSingleton::getInstance).thenReturn(non);

            // Act
            leaderService.setLeadership();

            // Assert
            verify(leadershipProducerService).sendAsyncMessageLeadershipObtained();
        }
    }

    @Test
    public void shouldSetLeadershipLeadershipAvailableButWasBefore() {
        try (MockedStatic<AppInstanceSingleton> mocked = mockStatic(AppInstanceSingleton.class)) {
            // Arrange
            AppInstanceSingleton nonStatic = new AppInstanceSingleton(INSTANCE_ID, LEADER);
            mocked.when(AppInstanceSingleton::getInstance).thenReturn(nonStatic);
            when(leaderRepository.getLeader()).thenReturn(INSTANCE_ID);

            // Act
            leaderService.setLeadership();

            // Assert
            verify(leaderRepository).saveUpdateTTL(INSTANCE_ID);
        }
    }

    @Test
    public void shouldSetLeadershipExistingLeader() {
        try (MockedStatic<AppInstanceSingleton> mocked = mockStatic(AppInstanceSingleton.class)) {
            // Arrange
            AppInstanceSingleton nonStatic = new AppInstanceSingleton(INSTANCE_ID, LEADER);
            mocked.when(AppInstanceSingleton::getInstance).thenReturn(nonStatic);
            when(leaderRepository.getLeader()).thenReturn(UUID.randomUUID().toString());

            // Act
            leaderService.setLeadership();

            // Assert
            verify(leadershipProducerService).sendAsyncMessageLeadershipLost();
        }
    }

    @Test
    public void shouldCheckReleaseLeadershipInstanceIsLeader() {
        try (MockedStatic<AppInstanceSingleton> mocked = mockStatic(AppInstanceSingleton.class)) {
            // Arrange
            AppInstanceSingleton nonStatic = new AppInstanceSingleton(INSTANCE_ID, LEADER);
            mocked.when(AppInstanceSingleton::getInstance).thenReturn(nonStatic);
            when(leaderRepository.getLeader()).thenReturn(INSTANCE_ID);

            // Act
            leaderService.checkReleaseLeadership();

            // Assert
            verify(leaderRepository).clearLeader();
            verify(leadershipProducerService).sendAsyncMessageLeadershipLost();
        }
    }

    @Test
    public void shouldCheckReleaseLeadershipInstanceNonLeader() {
        try (MockedStatic<AppInstanceSingleton> mocked = mockStatic(AppInstanceSingleton.class)) {
            // Arrange
            AppInstanceSingleton nonStatic = new AppInstanceSingleton(INSTANCE_ID, LEADER);
            mocked.when(AppInstanceSingleton::getInstance).thenReturn(nonStatic);
            when(leaderRepository.getLeader()).thenReturn(UUID.randomUUID().toString());

            // Act
            leaderService.checkReleaseLeadership();

            // Assert
            verify(leaderRepository).getLeader();
        }
    }

    @Test
    public void shouldGetLeader() {
        // Arrange
        // Act
        leaderService.getLeader();

        // Assert
        verify(leaderRepository).getLeader();
    }

    @Test
    public void shouldClearLeader() {
        // Arrange
        // Act
        leaderService.clearLeader();

        // Assert
        verify(leaderRepository).clearLeader();
        verify(leadershipProducerService).sendAsyncMessageLeadershipLost();
    }

    @Test
    public void shouldListenLeadershipLost() {
        // Arrange
        // Act
        leaderService.listenLeadershipLost(INSTANCE_ID);

        // Assert
    }

    @Test
    public void shouldListenLeadershipObtainedWhenLeader() {
        try (MockedStatic<AppInstanceSingleton> mocked = mockStatic(AppInstanceSingleton.class)) {
            // Arrange
            AppInstanceSingleton nonStatic = new AppInstanceSingleton("cc7db723-3dd2-4b13-9a61-cb02297d3a82", LEADER);
            mocked.when(AppInstanceSingleton::getInstance).thenReturn(nonStatic);
            String newLeader = "{\"instanceId\":\"cc7db723-3dd2-4b13-9a61-cb02297d3a82\",\"leadershipStatus\":\"LEADER\"}";
            AppInstanceKafkaDto appInstanceKafkaDto = new AppInstanceKafkaDto("cc7db723-3dd2-4b13-9a61-cb02297d3a82", LEADER);
            when(gson.fromJson(newLeader, AppInstanceKafkaDto.class)).thenReturn(appInstanceKafkaDto);

            // Act
            leaderService.listenLeadershipObtained(newLeader);

            // Assert
            Assert.assertEquals(nonStatic.getLeadershipStatus(), LEADER);
        }
    }

    @Test
    public void shouldListenLeadershipObtainedWhenNonLeader() {
        try (MockedStatic<AppInstanceSingleton> mocked = mockStatic(AppInstanceSingleton.class)) {
            // Arrange
            AppInstanceSingleton nonStatic = new AppInstanceSingleton(INSTANCE_ID, LEADER);
            mocked.when(AppInstanceSingleton::getInstance).thenReturn(nonStatic);
            String newLeader = "{\"instanceId\":\"cc7db723-3dd2-4b13-9a61-cb02297d3a82\",\"leadershipStatus\":\"LEADER\"}";
            AppInstanceKafkaDto appInstanceKafkaDto = new AppInstanceKafkaDto("cc7db723-3dd2-4b13-9a61-cb02297d3a82", LEADER);
            when(gson.fromJson(newLeader, AppInstanceKafkaDto.class)).thenReturn(appInstanceKafkaDto);

            // Act
            leaderService.listenLeadershipObtained(newLeader);

            // Assert
            Assert.assertEquals(nonStatic.getLeadershipStatus(), NON_LEADER);
        }
    }

}
