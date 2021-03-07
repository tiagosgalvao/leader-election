package com.galvao.leader.election.service;

import com.galvao.leader.election.model.AppInstanceSingleton;
import com.galvao.leader.election.repository.InstanceRepository;
import com.galvao.leader.election.utils.UUIDUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.galvao.leader.election.enums.LeadershipStatus.NON_LEADER;
import static org.mockito.AdditionalAnswers.returnsElementsOf;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InstanceServiceTest {

    private static final String INSTANCE_ID = UUID.randomUUID().toString();

    @InjectMocks
    private InstanceService instanceService;

    @Mock
    private InstanceRepository instanceRepository;

    @Mock
    private UUIDUtils uuidUtils;

    @Test
    public void shouldSetLeadershipNewLeader() {
        // Arrange
        // Act
        instanceService.delete(INSTANCE_ID);

        // Assert
        verify(instanceRepository).delete(INSTANCE_ID);
    }

    @Test
    public void shouldSetLeadershipLeadershipAvailableButWasBefore() {
        try (MockedStatic<AppInstanceSingleton> mocked = mockStatic(AppInstanceSingleton.class)) {
            // Arrange
            when(uuidUtils.generateUUIDString()).thenReturn(INSTANCE_ID);
            AppInstanceSingleton nonStatic = new AppInstanceSingleton(INSTANCE_ID, NON_LEADER);
            mocked.when(AppInstanceSingleton::getInstance).thenReturn(nonStatic);
            when(instanceRepository.findAll()).thenReturn(new HashMap());

            // Act
            instanceService.setInstanceId();

            // Assert
            verify(instanceRepository).save(INSTANCE_ID);
        }
    }

    @Test
    public void shouldSetLeadershipLeadershipAvailableExistingInstanceId() {
        try (MockedStatic<AppInstanceSingleton> mocked = mockStatic(AppInstanceSingleton.class)) {
            // Arrange
            var finalUUID = UUID.randomUUID().toString();
            doAnswer(returnsElementsOf(List.of(INSTANCE_ID, finalUUID))).when(uuidUtils).generateUUIDString();
            AppInstanceSingleton nonStatic = new AppInstanceSingleton(INSTANCE_ID, NON_LEADER);
            mocked.when(AppInstanceSingleton::getInstance).thenReturn(nonStatic);
            Map<String, String> targetSet = new HashMap<>();
            targetSet.put("INSTANCE", INSTANCE_ID);
            when(instanceRepository.findAll()).thenReturn(targetSet);

            // Act
            instanceService.setInstanceId();

            // Assert
            verify(instanceRepository).save(finalUUID);
        }
    }

    @Test
    public void shouldUpdateInstanceId() {
        try (MockedStatic<AppInstanceSingleton> mocked = mockStatic(AppInstanceSingleton.class)) {
            // Arrange
            AppInstanceSingleton nonStatic = new AppInstanceSingleton(INSTANCE_ID, NON_LEADER);
            mocked.when(AppInstanceSingleton::getInstance).thenReturn(nonStatic);

            // Act
            instanceService.updateInstanceId();

            // Assert
            verify(instanceRepository).save(INSTANCE_ID);
        }
    }

}
