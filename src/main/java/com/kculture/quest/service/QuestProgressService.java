package com.kculture.quest.service;

import com.kculture.quest.domain.*;
import com.kculture.quest.dto.*;
import com.kculture.quest.exception.QuestNotFoundException;
import com.kculture.quest.repository.*;
import com.kculture.user.domain.User;
import com.kculture.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestProgressService {

    private final QuestService questService;
    private final QuestStepRepository stepRepository;
    private final MissionRepository missionRepository;
    private final UserQuestProgressRepository progressRepository;
    private final UserStepStatusRepository stepStatusRepository;
    private final StampRepository stampRepository;
    private final UserRepository userRepository;

    @Transactional
    public QuestProgressResponse startQuest(Long userId, Long questId) {
        User user = findUser(userId);
        Quest quest = questService.findQuestEntity(questId);

        // 이미 시작한 퀘스트라면 새 기록을 만들지 않고 기존 진행 상황을 반환한다.
        return progressRepository.findByUserIdAndQuestId(userId, questId)
                .map(this::toResponse)
                .orElseGet(() -> createProgress(user, quest));
    }

    public QuestProgressResponse findProgress(Long userId, Long questId) {
        UserQuestProgress progress = progressRepository.findByUserIdAndQuestId(userId, questId)
                .orElseThrow(() -> new QuestNotFoundException("퀘스트 진행 기록을 찾을 수 없습니다."));
        return toResponse(progress);
    }

    public List<QuestProgressResponse> findUserQuests(Long userId) {
        findUser(userId);
        return progressRepository.findByUserIdOrderByStartedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<StampResponse> findUserStamps(Long userId) {
        findUser(userId);
        return stampRepository.findByUserIdOrderByAcquiredAtDesc(userId).stream()
                .map(StampResponse::from)
                .toList();
    }

    public ArrivalResponse checkArrival(Long userId, Long questId, Long stepId, LocationRequest request) {
        UserStepStatus stepStatus = findStepStatus(userId, questId, stepId);
        if (stepStatus.getStatus() != StepStatus.UNLOCKED) {
            throw new IllegalStateException("현재 진행 가능한 단계가 아닙니다.");
        }

        QuestStep step = stepStatus.getQuestStep();
        double distance = calculateDistance(step, request);
        boolean arrived = distance <= step.getArrivalRadius();
        MissionResponse mission = arrived
                ? MissionResponse.from(findMission(stepId))
                : null;

        return new ArrivalResponse(
                arrived, distance, step.getArrivalRadius(),
                arrived ? step.getStory() : null, mission
        );
    }

    @Transactional
    public QuestProgressResponse completeMission(
            Long userId, Long questId, Long stepId, MissionCompleteRequest request
    ) {
        User user = findUser(userId);
        UserQuestProgress progress = progressRepository.findByUserIdAndQuestId(userId, questId)
                .orElseThrow(() -> new QuestNotFoundException("퀘스트를 먼저 시작하세요."));
        UserStepStatus currentStatus = findStepStatus(userId, questId, stepId);

        // 같은 완료 요청이 다시 들어오면 스탬프를 추가하지 않고 현재 상태를 반환한다.
        if (currentStatus.getStatus() == StepStatus.DONE) {
            return toResponse(progress);
        }
        if (currentStatus.getStatus() != StepStatus.UNLOCKED
                || currentStatus.getQuestStep().getStepOrder() != progress.getCurrentStep()) {
            throw new IllegalStateException("현재 진행 중인 단계만 완료할 수 있습니다.");
        }

        QuestStep currentStep = currentStatus.getQuestStep();
        if (calculateDistance(currentStep, request.location()) > currentStep.getArrivalRadius()) {
            throw new IllegalStateException("장소의 도착 반경 안에서 미션을 완료하세요.");
        }

        Mission mission = findMission(stepId);
        String photoUrl = null;
        if (mission.getMissionType() == MissionType.QUIZ) {
            if (request.answer() == null
                    || !mission.getAnswer().strip().equalsIgnoreCase(request.answer().strip())) {
                throw new IllegalArgumentException("퀴즈 정답이 아닙니다.");
            }
        } else {
            if (request.photoUrl() == null || request.photoUrl().isBlank()) {
                throw new IllegalArgumentException("사진 주소를 입력하세요.");
            }
            photoUrl = request.photoUrl();
        }

        currentStatus.complete();
        if (!stampRepository.existsByUserIdAndStepId(userId, stepId)) {
            stampRepository.save(new Stamp(user, currentStep, photoUrl));
        }

        stepRepository.findByQuestIdAndStepOrder(questId, currentStep.getStepOrder() + 1)
                .ifPresentOrElse(nextStep -> {
                    UserStepStatus nextStatus = stepStatusRepository
                            .findByUserIdAndQuestStepId(userId, nextStep.getId())
                            .orElseThrow(() -> new QuestNotFoundException("다음 단계 상태를 찾을 수 없습니다."));
                    nextStatus.unlock();
                    progress.moveTo(nextStep.getStepOrder());
                }, progress::complete);

        return toResponse(progress);
    }

    private QuestProgressResponse createProgress(User user, Quest quest) {
        List<QuestStep> steps = stepRepository.findByQuestIdOrderByStepOrderAsc(quest.getId());
        if (steps.isEmpty()) {
            throw new IllegalStateException("방문 단계가 없는 퀘스트입니다.");
        }

        UserQuestProgress progress = progressRepository.save(new UserQuestProgress(user, quest));
        for (int index = 0; index < steps.size(); index++) {
            UserStepStatus status = new UserStepStatus(user, steps.get(index));
            if (index == 0) {
                status.unlock();
            }
            stepStatusRepository.save(status);
        }
        return toResponse(progress);
    }

    private QuestProgressResponse toResponse(UserQuestProgress progress) {
        List<StepProgressResponse> steps = stepStatusRepository
                .findByUserIdAndQuestStepQuestIdOrderByQuestStepStepOrderAsc(
                        progress.getUser().getId(), progress.getQuest().getId()
                )
                .stream()
                .map(status -> new StepProgressResponse(
                        status.getQuestStep().getId(),
                        status.getQuestStep().getStepOrder(),
                        status.getStatus(),
                        status.getStatus() == StepStatus.LOCKED
                                ? null : status.getQuestStep().getPlace().getName()
                ))
                .toList();

        return new QuestProgressResponse(
                progress.getQuest().getId(), progress.getQuest().getTitle(),
                progress.getCurrentStep(), progress.getStatus(), steps
        );
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new QuestNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private UserStepStatus findStepStatus(Long userId, Long questId, Long stepId) {
        UserStepStatus status = stepStatusRepository.findByUserIdAndQuestStepId(userId, stepId)
                .orElseThrow(() -> new QuestNotFoundException("단계 진행 기록을 찾을 수 없습니다."));
        if (!status.getQuestStep().getQuest().getId().equals(questId)) {
            throw new QuestNotFoundException("이 퀘스트에 속한 단계가 아닙니다.");
        }
        return status;
    }

    private Mission findMission(Long stepId) {
        return missionRepository.findByStepId(stepId)
                .orElseThrow(() -> new QuestNotFoundException("미션을 찾을 수 없습니다."));
    }

    private double calculateDistance(QuestStep step, LocationRequest request) {
        if (step.getPlace().getLatitude() == null || step.getPlace().getLongitude() == null) {
            throw new IllegalStateException("장소의 GPS 좌표가 없습니다.");
        }
        return GeoDistance.meters(
                request.latitude().doubleValue(), request.longitude().doubleValue(),
                step.getPlace().getLatitude().doubleValue(), step.getPlace().getLongitude().doubleValue()
        );
    }
}
