package insalan.mininsalan.dto;

import insalan.mininsalan.enums.ChallengeType;
import insalan.mininsalan.enums.Reward;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChallengeSummaryDto {
    private Long id;
    private String title;
    private String description;
    private int points;
    private ChallengeType type;
    private Reward reward;
    private boolean isAvailable;
    private boolean isCompleted;
    private String gameName;
}
