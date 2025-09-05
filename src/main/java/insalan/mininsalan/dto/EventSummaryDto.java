package insalan.mininsalan.dto;

import insalan.mininsalan.enums.ChallengeType;
import insalan.mininsalan.enums.Reward;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventSummaryDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int totalChallenges;
    private int availableChallenges;
    private boolean isActive;
}

