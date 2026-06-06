package kz.citydrive.admin.api;

import jakarta.validation.Valid;
import kz.citydrive.admin.dto.FlutterLoginResponse;
import kz.citydrive.admin.dto.LoginRequest;
import kz.citydrive.admin.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FlutterAuthApiController {

    private final AuthService authService;

    public FlutterAuthApiController(AuthService authService) {
        this.authService = authService;
    }

    /** Flutter вызывает POST /api/login (не /api/auth/login). */
    @PostMapping("/login")
    public FlutterLoginResponse login(@Valid @RequestBody LoginRequest request) {
        return FlutterLoginResponse.from(authService.login(request.getPhone(), request.getPassword()));
    }
}
