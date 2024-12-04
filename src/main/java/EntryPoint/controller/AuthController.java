package EntryPoint.controller;

import EntryPoint.dto.ResponseDTO;
import EntryPoint.dto.UserDTO;
import EntryPoint.service.UserService;
import EntryPoint.utils.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    ResponseDTO response;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@Valid @RequestBody UserDTO request) {
        try {
            Object registeredEmail = userService.register(request);
            response = new ResponseDTO(true, "Registration Successful", registeredEmail);
            return ResponseEntity.ok(response); // Return success response with email as data
        } catch (Exception e) {
            response = ResponseDTO.errorResponse("Registration Failed", e.getMessage());
            return ResponseEntity.status(500).body(response); // Return error response
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseDTO> verifyOTP(@RequestParam String email, @RequestParam String otp) {
        try {
            boolean isValid = userService.verifyOTP(email, otp);
            if(isValid) {
                response = new ResponseDTO(true, "OTP is verified successfully and registration is completed successfully", null);
                return ResponseEntity.ok(response);
            } else {
                response = ResponseDTO.errorResponse("Invalid or expired OTP!", null);
                return ResponseEntity.status(400).body(response);
            }

        } catch (Exception e) {
            response = ResponseDTO.errorResponse("OTP verification failed!", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@Valid @RequestBody UserDTO request) {
        try {
            Object token = userService.login(request);
            response = new ResponseDTO(true, "You are logged in!", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response = ResponseDTO.errorResponse("Login Failed", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseDTO> getProfile(@RequestHeader("Authorization") String accessToken) {
        try {
            String token = accessToken.substring(7).trim();
            String email = jwtUtil.extractEmail(token);
            Object profile = userService.getProfile(email);
            response = new ResponseDTO(true, "User Profile", profile);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response = ResponseDTO.errorResponse("Profile fetching failed!", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseDTO> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
        try {
            String token = refreshToken.substring(7).trim();
            Object newAccessToken = userService.refreshAccessToken(token);
            response = new ResponseDTO(true, "Access Token refreshed", newAccessToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response = ResponseDTO.errorResponse("Failed to refresh token!", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO> logout(@RequestHeader("Authorization") String accessToken, @RequestHeader("Refresh-Token") String refreshToken) {
        try {
            String AccessToken, RefreshToken;
            AccessToken = accessToken.substring(7).trim();
            RefreshToken = refreshToken.substring(7).trim();
            boolean isLoggedOut = userService.logout(AccessToken, RefreshToken);
            if(isLoggedOut){
                response = new ResponseDTO(true, "Logout successful", null);
                return ResponseEntity.ok(response);
            } else {
                response = ResponseDTO.errorResponse("Logout Failed!", null);
                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            response = ResponseDTO.errorResponse("Logout Failed", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
