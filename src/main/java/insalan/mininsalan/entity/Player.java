package insalan.mininsalan.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Player {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String username;

    @OneToMany(mappedBy = "player")
    private List<ChallengeCompletion> completions;
}
