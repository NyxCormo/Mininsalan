package insalan.mininsalan.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class defi {
    @Id
    @GeneratedValue
    private Long id;
}
