package insalan.mininsalan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameDto {
    private Long id;
    private String name;
    private String link;
    private String imagelink;
}
