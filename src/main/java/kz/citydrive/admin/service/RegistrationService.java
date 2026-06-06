package kz.citydrive.admin.service;

import kz.citydrive.admin.domain.PendingRegistration;
import kz.citydrive.admin.domain.User;
import kz.citydrive.admin.domain.UserRole;
import kz.citydrive.admin.dto.FlutterLoginResponse;
import kz.citydrive.admin.dto.RegisterInitResponse;
import kz.citydrive.admin.dto.RegisterRequest;
import kz.citydrive.admin.dto.RegisterVerifyRequest;
import kz.citydrive.admin.repository.PendingRegistrationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
public class RegistrationService {

    private static final String DEBUG_SMS_CODE = "1111";

    private final UserService userService;
    private final JwtService jwtService;
    private final PendingRegistrationRepository pendingRegistrationRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
            UserService userService,
            JwtService jwtService,
            PendingRegistrationRepository pendingRegistrationRepository,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.pendingRegistrationRepository = pendingRegistrationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegisterInitResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getPasswordConfirmation())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        if (userService.findByPhone(request.getPhone()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone already registered");
        }

        UserRole role = request.getRole();
        if (role == UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role for registration");
        }

        PendingRegistration pending = pendingRegistrationRepository
                .findByPhone(request.getPhone())
                .orElse(new PendingRegistration());

        pending.setPhone(request.getPhone());
        pending.setFullName(request.getFullName().trim());
        pending.setCityId(request.getCityId());
        pending.setBirthDate(request.getBirthDate().trim());
        pending.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        pending.setRole(role);
        pending.setCreatedAt(Instant.now());
        pendingRegistrationRepository.save(pending);

        return new RegisterInitResponse(
                "SMS sent (debug: use code 1111 or any 4+ digits)",
                request.getPhone(),
                pending.getFullName());
    }

    @Transactional
    public FlutterLoginResponse verify(RegisterVerifyRequest request) {
        if (!isValidSmsCode(request.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid SMS code");
        }

        PendingRegistration pending = pendingRegistrationRepository
                .findByPhone(request.getPhone())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Registration not found. Complete step 1 first"));

        if (userService.findByPhone(request.getPhone()).isPresent()) {
            pendingRegistrationRepository.deleteByPhone(request.getPhone());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone already registered");
        }

        User user = userService.createRegisteredUser(
                pending.getFullName(),
                pending.getPhone(),
                pending.getPasswordHash(),
                pending.getCityId(),
                pending.getBirthDate(),
                pending.getRole());

        pendingRegistrationRepository.deleteByPhone(request.getPhone());

        String token = jwtService.generateToken(user.getPhone(), user.getId(), user.getRole().name());
        return FlutterLoginResponse.fromUser(user, token);
    }

    private boolean isValidSmsCode(String code) {
        if (code == null || code.isBlank()) {
            return false;
        }
        if (DEBUG_SMS_CODE.equals(code)) {
            return true;
        }
        return code.matches("\\d{4,}");
    }
}
