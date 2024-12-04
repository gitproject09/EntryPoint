package EntryPoint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class ResponseDTO {
    private LocalDateTime timestamp;
    private boolean status;
    private String message;
    private Object data;
    private Object errors;

    public ResponseDTO(boolean status, String message, Object data) {
        this.timestamp = LocalDateTime.now(); // Automatically set the timestamp
        this.status = status;
        this.message = message;
        this.data = data;
        this.errors = null;
    }

    // Method to create an error response
    public static ResponseDTO errorResponse(String message, Object errors) {
        return new ResponseDTO(LocalDateTime.now(), false, message, null, errors);
    }
}
