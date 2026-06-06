package kz.citydrive.admin.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kz.citydrive.admin.dto.ApiMessageResponse;
import kz.citydrive.admin.dto.ProfileLangRequest;
import kz.citydrive.admin.dto.ProfileLangResponse;
import kz.citydrive.admin.dto.ProfileResponse;
import kz.citydrive.admin.security.AdminUserDetails;
import kz.citydrive.admin.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/profile")
public class ProfileApiController {

    private final ProfileService profileService;

    public ProfileApiController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ProfileResponse getProfile(
            @AuthenticationPrincipal AdminUserDetails principal, HttpServletRequest request) {
        return profileService.getProfile(requireUserId(principal), request);
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ProfileResponse updateProfile(
            @AuthenticationPrincipal AdminUserDetails principal,
            @RequestParam(required = false) String full_name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String birth_date,
            @RequestParam(required = false) String city_id,
            @RequestParam(required = false) String lang,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String password_confirmation,
            @RequestParam(required = false) String device_token,
            @RequestParam(required = false) String device_type,
            @RequestParam(value = "avatar_url", required = false) MultipartFile avatar_url,
            HttpServletRequest request) {
        return profileService.updateProfile(
                requireUserId(principal),
                full_name,
                phone,
                birth_date,
                city_id,
                lang,
                password,
                password_confirmation,
                device_token,
                device_type,
                avatar_url,
                request);
    }

    @PatchMapping("/lang")
    public ProfileLangResponse updateLang(
            @AuthenticationPrincipal AdminUserDetails principal,
            @Valid @RequestBody ProfileLangRequest body) {
        return profileService.updateLang(requireUserId(principal), body.getLang());
    }

    @DeleteMapping
    public ApiMessageResponse deleteProfile(
            @AuthenticationPrincipal AdminUserDetails principal, HttpServletRequest request) {
        String token = extractBearerToken(request);
        profileService.deleteProfile(requireUserId(principal), token);
        return new ApiMessageResponse("Account deleted", HttpStatus.OK.value());
    }

    private Long requireUserId(AdminUserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return principal.getUser().getId();
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
