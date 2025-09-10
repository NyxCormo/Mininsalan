package insalan.mininsalan.dto;

import insalan.mininsalan.enums.ChallengeType;
import insalan.mininsalan.enums.Reward;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChallengeDto {
    private Long id;
    private String title;
    private String description;
    private int points;
    private ChallengeType type;
    private Reward reward;
    private LocalDateTime releaseTime;
    private LocalDateTime endTime;
    private GameDto game;
    private EventDto event;
    private Set<CategoryDto> categories;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EventDto {
        private Long id;
        private String name;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GameDto {
        private Long id;
        private String name;
        private String link;
        private String imagelink;
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