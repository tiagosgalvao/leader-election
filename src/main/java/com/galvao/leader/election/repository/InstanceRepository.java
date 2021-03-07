package com.galvao.leader.election.repository;

import java.util.Map;

public interface InstanceRepository {

    void save(String instances);

    Map findAll();

    void delete(String instanceId);

}
