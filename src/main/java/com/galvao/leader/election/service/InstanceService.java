package com.galvao.leader.election.service;

import com.galvao.leader.election.model.AppInstanceSingleton;
import com.galvao.leader.election.repository.InstanceRepository;
import com.galvao.leader.election.utils.UUIDUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static com.galvao.leader.election.enums.LeadershipStatus.NON_LEADER;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstanceService {

    private final InstanceRepository instanceRepository;
    private final UUIDUtils          uuidUtils;

    public void delete(String instanceId) {
        instanceRepository.delete(instanceId);
    }

    public void setInstanceId() {
        String newId = null;
        while (newId == null) {
            newId = uuidUtils.generateUUIDString();
            Set<String> targetSet = new HashSet<>(instanceRepository.findAll().values());
            if (!targetSet.add(newId)) {
                newId = null;
            }
        }
        AppInstanceSingleton appInstanceSingleton = AppInstanceSingleton.getInstance();
        appInstanceSingleton.setInstanceId(newId);
        appInstanceSingleton.setLeadershipStatus(NON_LEADER);
        instanceRepository.save(newId);
    }

    @Scheduled(cron = "0 0 * * * *")
    public void updateInstanceId() {
        instanceRepository.save(AppInstanceSingleton.getInstance().getInstanceId());
    }

}
