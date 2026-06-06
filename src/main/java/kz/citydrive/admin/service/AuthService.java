package kz.citydrive.admin.service;

import kz.citydrive.admin.domain.User;
import kz.citydrive.admin.dto.LoginResponse;
import kz.citydrive.admin.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthService(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public LoginResponse login(String phone, String password) {
        User user = userService.findByPhone(phone)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (user.isBlocked()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is blocked");
        }

        if (!userService.matchesPassword(user, password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateToken(
                user.getPhone(),
                user.getId(),
                user.getRole().name());

        return new LoginResponse(token, UserDto.fromEntity(user));
    }
}
