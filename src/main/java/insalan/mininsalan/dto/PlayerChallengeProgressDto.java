package insalan.mininsalan.dto;

import insalan.mininsalan.enums.Reward;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerChallengeProgressDto {
    private Long playerId;
    private String username;
    private int totalPoints;
    private int completedChallenges;
    private List<CompletionDto> completions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CompletionDto {
        private Long challengeId;
        private String challengeTitle;
        private int points;
        private LocalDateTime completedAt;
        private Reward reward;
    }
}