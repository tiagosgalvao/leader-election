package com.galvao.leader.election.kafka.producer;

import com.galvao.leader.election.model.AppInstanceSingleton;
import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static com.galvao.leader.election.enums.LeadershipStatus.LEADER;
import static com.galvao.leader.election.enums.LeadershipStatus.NON_LEADER;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PublisherProducerServiceTest {

    private static final String INSTANCE_ID = UUID.randomUUID().toString();

    @InjectMocks
    private PublisherProducerService publisherProducerService;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private Gson gson;

    @Test
    public void sendAsyncMessageAsLeader() {
        try (MockedStatic<AppInstanceSingleton> mocked = mockStatic(AppInstanceSingleton.class)) {
            // Arrange
            AppInstanceSingleton nonStatic = new AppInstanceSingleton(INSTANCE_ID, LEADER);
            mocked.when(AppInstanceSingleton::getInstance).thenReturn(nonStatic);

            // Act
            publisherProducerService.sendAsyncMessage();

            // Assert
            verify(kafkaTemplate).send(any(), any());
        }
    }

    @Test
    public void sendAsyncMessageAsNonLeader() {
        try (MockedStatic<AppInstanceSingleton> mocked = mockStatic(AppInstanceSingleton.class)) {
            // Arrange
            AppInstanceSingleton nonStatic = new AppInstanceSingleton(INSTANCE_ID, NON_LEADER);
            mocked.when(AppInstanceSingleton::getInstance).thenReturn(nonStatic);

            // Act
            publisherProducerService.sendAsyncMessage();

            // Assert
        }
    }

}
