package com.galvao.leader.election.controller;

import com.galvao.leader.election.service.LeaderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Leader Election")
@RestController
@RequestMapping("v1/leaders")
@RequiredArgsConstructor
@Profile("local")
public class LeaderController {

    private final LeaderService leaderService;

    @GetMapping
    public String getLeaders() {
        return leaderService.getLeader();
    }

    @PostMapping
    public void clearCache() {
        leaderService.clearLeader();
    }

}