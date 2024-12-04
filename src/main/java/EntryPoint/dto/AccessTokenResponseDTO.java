package EntryPoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
public class AccessTokenResponseDTO {
    private String accessToken;
}
