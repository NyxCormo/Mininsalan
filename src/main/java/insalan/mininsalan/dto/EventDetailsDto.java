package insalan.mininsalan.dto;

import insalan.mininsalan.enums.ChallengeType;
import insalan.mininsalan.enums.Reward;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDetailsDto {
    private Long id;
    private EventInfo eventInfo;
    private List<ChallengeDto> challenges;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EventInfo {
        private String name;
        private String description;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private int duration;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ChallengeDto {
        private Long id;
        private String title;
        private String description;
        private int points;
        private ChallengeType type;
        private Reward reward;
        private LocalDateTime releaseTime;
        private LocalDateTime endTime;
        private GameDto game;
        private Set<CategoryDto> categories;
        private boolean isCompleted;
        private LocalDateTime completedAt;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GameDto {
        private Long id;
        private String name;
        private String link;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CategoryDto {
        private Long id;
        private String name;
    }
}