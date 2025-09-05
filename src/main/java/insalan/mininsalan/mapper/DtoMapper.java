package insalan.mininsalan.mapper;

import insalan.mininsalan.dto.*;
import insalan.mininsalan.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    public EventDetailsDto toEventDetailsDto(Event event, Long playerId) {
        return EventDetailsDto.builder()
                .id(event.getId())
                .eventInfo(EventDetailsDto.EventInfo.builder()
                        .name(event.getName())
                        .description(event.getDescription())
                        .startDate(event.getStartDate())
                        .endDate(event.getEndDate())
                        .duration(event.getDuration())
                        .build())
                .challenges(event.getChallenges().stream()
                        .map(challenge -> toChallengeDto(challenge, playerId))
                        .collect(Collectors.toList()))
                .build();
    }

    public EventDetailsDto.ChallengeDto toChallengeDto(Challenge challenge, Long playerId) {
        boolean isCompleted = false;
        LocalDateTime completedAt = null;

        if (playerId != null && challenge.getCompletions() != null) {
            var completion = challenge.getCompletions().stream()
                    .filter(c -> c.getPlayer().getId().equals(playerId))
                    .findFirst();

            if (completion.isPresent()) {
                isCompleted = true;
                completedAt = completion.get().getCompletedAt();
            }
        }

        return EventDetailsDto.ChallengeDto.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .points(challenge.getPoints())
                .type(challenge.getType())
                .reward(challenge.getReward())
                .releaseTime(challenge.getReleaseTime())
                .endTime(challenge.getEndTime())
                .game(challenge.getGame() != null ? toGameDto(challenge.getGame()) : null)
                .categories(challenge.getCategories().stream()
                        .map(this::toCategoryDto)
                        .collect(Collectors.toSet()))
                .isCompleted(isCompleted)
                .completedAt(completedAt)
                .build();
    }

    public EventDetailsDto.GameDto toGameDto(Game game) {
        return EventDetailsDto.GameDto.builder()
                .id(game.getId())
                .name(game.getName())
                .link(game.getLink())
                .build();
    }

    public EventDetailsDto.CategoryDto toCategoryDto(Category category) {
        return EventDetailsDto.CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public EventSummaryDto toEventSummaryDto(Event event) {
        LocalDateTime now = LocalDateTime.now();
        int availableChallenges = (int) event.getChallenges().stream()
                .filter(c -> c.getReleaseTime().isBefore(now) &&
                        (c.getEndTime() == null || c.getEndTime().isAfter(now)))
                .count();

        return EventSummaryDto.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .totalChallenges(event.getChallenges().size())
                .availableChallenges(availableChallenges)
                .isActive(event.getStartDate().isBefore(now) && event.getEndDate().isAfter(now))
                .build();
    }

    public PlayerChallengeProgressDto toPlayerProgressDto(Player player, List<ChallengeCompletion> completions) {
        int totalPoints = completions.stream()
                .mapToInt(c -> c.getChallenge().getPoints())
                .sum();

        List<PlayerChallengeProgressDto.CompletionDto> completionDtos = completions.stream()
                .map(completion -> PlayerChallengeProgressDto.CompletionDto.builder()
                        .challengeId(completion.getChallenge().getId())
                        .challengeTitle(completion.getChallenge().getTitle())
                        .points(completion.getChallenge().getPoints())
                        .completedAt(completion.getCompletedAt())
                        .reward(completion.getChallenge().getReward())
                        .build())
                .collect(Collectors.toList());

        return PlayerChallengeProgressDto.builder()
                .playerId(player.getId())
                .username(player.getUsername())
                .totalPoints(totalPoints)
                .completedChallenges(completions.size())
                .completions(completionDtos)
                .build();
    }

    public ChallengeSummaryDto toChallengeSummaryDto(Challenge challenge, Long playerId) {
        LocalDateTime now = LocalDateTime.now();
        boolean isAvailable = challenge.getReleaseTime().isBefore(now) &&
                (challenge.getEndTime() == null || challenge.getEndTime().isAfter(now));

        boolean isCompleted = false;
        if (playerId != null && challenge.getCompletions() != null) {
            isCompleted = challenge.getCompletions().stream()
                    .anyMatch(c -> c.getPlayer().getId().equals(playerId));
        }

        return ChallengeSummaryDto.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .points(challenge.getPoints())
                .type(challenge.getType())
                .reward(challenge.getReward())
                .isAvailable(isAvailable)
                .isCompleted(isCompleted)
                .gameName(challenge.getGame() != null ? challenge.getGame().getName() : null)
                .build();
    }
}