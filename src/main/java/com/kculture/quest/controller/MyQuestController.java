package com.kculture.quest.controller;

import com.kculture.quest.dto.QuestProgressResponse;
import com.kculture.quest.dto.StampResponse;
import com.kculture.quest.service.QuestProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}")
public class MyQuestController {

    private final QuestProgressService progressService;

    @GetMapping("/quests")
    public List<QuestProgressResponse> findUserQuests(@PathVariable Long userId) {
        return progressService.findUserQuests(userId);
    }

    @GetMapping("/stamps")
    public List<StampResponse> findUserStamps(@PathVariable Long userId) {
        return progressService.findUserStamps(userId);
    }
}
