package com.galvao.leader.election.repository;

public interface LeaderRepository {

    void save(String name);

    void saveUpdateTTL(String name);

    String getLeader();

    void clearLeader();

}
