package EntryPoint.service;

import EntryPoint.dto.AccessTokenResponseDTO;
import EntryPoint.dto.LoginResponseDTO;
import EntryPoint.dto.UserDTO;
import EntryPoint.dto.UserProfileDTO;
import EntryPoint.exception.GlobalExceptionHandler;
import EntryPoint.repository.UserRepository;
import EntryPoint.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import EntryPoint.model.User;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor

public class UserService {
    @Autowired
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final StringRedisTemplate redisTemplate;

    public Object register(UserDTO request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already taken!");
        }

        // Generate OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Store OTP in Redis with expiration
        redisTemplate.opsForValue().set(request.getEmail(), otp, 5, TimeUnit.MINUTES);

        // Send OTP to user's email
        emailService.sendOTP(request.getEmail(), otp);
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(false)
                .build();
        userRepository.save(user);
        return user.getEmail();
    }

    public boolean verifyOTP(String email, String otp) {
        // Retrieve OTP from Redis
        String storedOTP = redisTemplate.opsForValue().get(email);

        // Validate OTP
        if(storedOTP != null && storedOTP.equals(otp)) {
            redisTemplate.delete(email);
            User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("User not found!"));
            user.setIsActive(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public Object login(UserDTO request) {
        // find user by email
        User user = userRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new GlobalExceptionHandler.UserNotFoundException("Invalid user!"));

        if(!user.getIsActive()) {
            throw new RuntimeException("User is not active. Please validate your OTP before logging in.");
        }
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new GlobalExceptionHandler.InvalidPasswordException("Invalid password!");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return new LoginResponseDTO(user.getEmail(), accessToken, refreshToken);
    }

    public Object getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        return new UserProfileDTO(user.getEmail());
    }

    public Object refreshAccessToken(String refreshToken) {
        if(!jwtUtil.validateToken(refreshToken)){
            throw new RuntimeException("Invalid or expired refresh token!");
        }
        String email = jwtUtil.extractEmail(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(email);
        return new AccessTokenResponseDTO(newAccessToken);
    }

    public boolean logout(String accessToken, String refreshToken) {
        jwtUtil.invalidateToken(accessToken);
        jwtUtil.invalidateToken(refreshToken);
        return true;
    }
}
