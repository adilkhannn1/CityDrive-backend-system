package kz.citydrive.admin.api;

import jakarta.validation.Valid;
import kz.citydrive.admin.dto.FlutterLoginResponse;
import kz.citydrive.admin.dto.RegisterInitResponse;
import kz.citydrive.admin.dto.RegisterRequest;
import kz.citydrive.admin.dto.RegisterVerifyRequest;
import kz.citydrive.admin.service.RegistrationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RegisterApiController {

    private final RegistrationService registrationService;

    public RegisterApiController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public RegisterInitResponse register(@Valid @RequestBody RegisterRequest request) {
        return registrationService.register(request);
    }

    @PostMapping("/register/verify")
    public FlutterLoginResponse verify(@Valid @RequestBody RegisterVerifyRequest request) {
        return registrationService.verify(request);
    }
}
