package com.galvao.leader.election.model;

import com.galvao.leader.election.enums.LeadershipStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;

@Data
@Scope
@AllArgsConstructor
@NoArgsConstructor
public class AppInstanceSingleton {

    private String           instanceId;
    private LeadershipStatus leadershipStatus;

    private static AppInstanceSingleton INSTANCE;

    public static AppInstanceSingleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppInstanceSingleton();
        }
        return INSTANCE;
    }

}
