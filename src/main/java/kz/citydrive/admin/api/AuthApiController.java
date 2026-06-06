package kz.citydrive.admin.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kz.citydrive.admin.dto.ApiMessageResponse;
import kz.citydrive.admin.dto.LoginRequest;
import kz.citydrive.admin.dto.LoginResponse;
import kz.citydrive.admin.service.AuthService;
import kz.citydrive.admin.service.TokenBlacklistService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthApiController(AuthService authService, TokenBlacklistService tokenBlacklistService) {
        this.authService = authService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.getPhone(), request.getPassword());
    }

    @PostMapping("/logout")
    public ApiMessageResponse logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            tokenBlacklistService.blacklist(authHeader.substring(7));
        }
        return new ApiMessageResponse("Logged out");
    }
}
