package com.kculture.quest.controller;

import com.kculture.quest.dto.QuestProgressResponse;
import com.kculture.quest.dto.StampResponse;
import com.kculture.quest.service.QuestProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me")
public class MyQuestController {

    private final QuestProgressService progressService;

    @GetMapping("/quests")
    public List<QuestProgressResponse> findUserQuests(@AuthenticationPrincipal Long userId) {
        return progressService.findUserQuests(userId);
    }

    @GetMapping("/stamps")
    public List<StampResponse> findUserStamps(@AuthenticationPrincipal Long userId) {
        return progressService.findUserStamps(userId);
    }
}
