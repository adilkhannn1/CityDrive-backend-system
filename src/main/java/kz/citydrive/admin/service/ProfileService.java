package kz.citydrive.admin.service;

import jakarta.servlet.http.HttpServletRequest;
import kz.citydrive.admin.domain.User;
import kz.citydrive.admin.dto.ProfileLangResponse;
import kz.citydrive.admin.dto.ProfileResponse;
import kz.citydrive.admin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;

@Service
public class ProfileService {

    private static final Set<String> ALLOWED_LANGS = Set.of("kk", "ru", "en");

    private final UserRepository userRepository;
    private final UserService userService;
    private final CityService cityService;
    private final TokenBlacklistService tokenBlacklistService;

    @Value("${app.uploads.dir:uploads}")
    private String uploadsDir;

    public ProfileService(
            UserRepository userRepository,
            UserService userService,
            CityService cityService,
            TokenBlacklistService tokenBlacklistService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.cityService = cityService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public ProfileResponse getProfile(Long userId, HttpServletRequest request) {
        User user = requireUser(userId);
        return toResponse(user, request);
    }

    @Transactional
    public ProfileResponse updateProfile(
            Long userId,
            String fullName,
            String phone,
            String birthDate,
            String cityIdRaw,
            String lang,
            String password,
            String passwordConfirmation,
            String deviceToken,
            String deviceType,
            MultipartFile avatarFile,
            HttpServletRequest request) {
        User user = requireUser(userId);

        if (fullName != null && !fullName.isBlank()) {
            user.setFullName(fullName.trim());
        }

        if (phone != null && !phone.isBlank()) {
            String normalizedPhone = phone.trim();
            if (userRepository.existsByPhoneAndIdNot(normalizedPhone, userId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone already in use");
            }
            user.setPhone(normalizedPhone);
        }

        if (birthDate != null) {
            user.setBirthDate(birthDate.isBlank() ? null : birthDate.trim());
        }

        if (cityIdRaw != null && !cityIdRaw.isBlank()) {
            int cityId = parseCityId(cityIdRaw);
            user.setCityId(cityId);
        }

        if (lang != null && !lang.isBlank()) {
            validateLang(lang.trim());
            user.setLang(lang.trim().toLowerCase(Locale.ROOT));
        }

        String trimmedPassword = password != null ? password.trim() : null;
        String trimmedPasswordConfirmation =
                passwordConfirmation != null ? passwordConfirmation.trim() : null;
        if (trimmedPassword != null && !trimmedPassword.isEmpty()) {
            if (trimmedPasswordConfirmation == null
                    || trimmedPasswordConfirmation.isEmpty()
                    || !trimmedPassword.equals(trimmedPasswordConfirmation)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
            }
            user.setPasswordHash(userService.encodePassword(trimmedPassword));
        }

        if (deviceToken != null) {
            user.setDeviceToken(deviceToken.isBlank() ? null : deviceToken.trim());
        }

        if (deviceType != null) {
            user.setDeviceType(deviceType.isBlank() ? null : deviceType.trim());
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            user.setAvatarUrl(saveAvatar(userId, avatarFile, request));
        }

        return toResponse(userRepository.save(user), request);
    }

    @Transactional
    public ProfileLangResponse updateLang(Long userId, String lang) {
        validateLang(lang);
        User user = requireUser(userId);
        user.setLang(lang.toLowerCase(Locale.ROOT));
        userRepository.save(user);
        return new ProfileLangResponse(user.getLang());
    }

    @Transactional
    public void deleteProfile(Long userId, String token) {
        userService.deleteUser(userId);
        tokenBlacklistService.blacklist(token);
    }

    private User requireUser(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }

    private ProfileResponse toResponse(User user, HttpServletRequest request) {
        String cityName = cityService.findNameById(user.getCityId()).orElse(null);
        String avatarUrl = resolvePublicUrl(user.getAvatarUrl(), request);
        return ProfileResponse.fromUser(user, cityName, avatarUrl);
    }

    private String resolvePublicUrl(String storedUrl, HttpServletRequest request) {
        if (storedUrl == null || storedUrl.isBlank()) {
            return null;
        }
        if (storedUrl.startsWith("http")) {
            return storedUrl;
        }
        String base = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        return storedUrl.startsWith("/") ? base + storedUrl : base + "/" + storedUrl;
    }

    private String saveAvatar(Long userId, MultipartFile file, HttpServletRequest request) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only image files are allowed");
        }

        String ext = contentType.contains("png") ? ".png" : ".jpg";
        String filename = userId + ext;

        try {
            Path dir = Paths.get(uploadsDir, "avatars").toAbsolutePath().normalize();
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save avatar");
        }

        String base = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        return base + "/uploads/avatars/" + filename;
    }

    private int parseCityId(String cityIdRaw) {
        try {
            int cityId = Integer.parseInt(cityIdRaw.trim());
            if (!cityService.exists(cityId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid city_id");
            }
            return cityId;
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid city_id");
        }
    }

    private void validateLang(String lang) {
        String normalized = lang.toLowerCase(Locale.ROOT);
        if (!ALLOWED_LANGS.contains(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lang must be kk, ru or en");
        }
    }
}
