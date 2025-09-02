package insalan.mininsalan.entity;

import insalan.mininsalan.enums.ChallengeType;
import insalan.mininsalan.enums.Reward;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Challenge {
    @Id @GeneratedValue
    private Long id;

    private String title;
    private String description;

    private int points;

    @Enumerated(EnumType.STRING)
    private ChallengeType type; // TEMPORARY, PERMANENT, RACE

    @Enumerated(EnumType.STRING)
    private Reward reward; // REDBULL, PIZZA, SNACK

    private LocalDateTime releaseTime;
    private LocalDateTime endTime;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Game game;

    @ManyToMany
    private Set<Category> categories;

    @OneToMany(mappedBy = "challenge")
    private List<ChallengeCompletion> completions;
}
