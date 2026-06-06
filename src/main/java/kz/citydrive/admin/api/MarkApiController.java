package kz.citydrive.admin.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kz.citydrive.admin.domain.User;
import kz.citydrive.admin.domain.UserRole;
import kz.citydrive.admin.dto.*;
import kz.citydrive.admin.security.AdminUserDetails;
import kz.citydrive.admin.service.MarkInteractionService;
import kz.citydrive.admin.service.RoadMarkService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/marks")
public class MarkApiController {

    private final RoadMarkService roadMarkService;
    private final MarkInteractionService markInteractionService;

    @Value("${app.uploads.dir:uploads}")
    private String uploadsDir;

    public MarkApiController(RoadMarkService roadMarkService, MarkInteractionService markInteractionService) {
        this.roadMarkService = roadMarkService;
        this.markInteractionService = markInteractionService;
    }

    @GetMapping
    public List<RoadMarkDto> list(@AuthenticationPrincipal AdminUserDetails principal) {
        return roadMarkService.findAllDtos(currentUserId(principal));
    }

    @GetMapping("/pending")
    public List<RoadMarkDto> pending(
            @AuthenticationPrincipal AdminUserDetails principal,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        User user = requirePrincipal(principal);
        return roadMarkService.findPendingDtosForController(
                user.getRole() == UserRole.CONTROLLER ? user : null,
                q,
                severity,
                type,
                limit,
                offset,
                user.getId());
    }

    @GetMapping("/mine-for-controller")
    public List<RoadMarkDto> mineForController(@AuthenticationPrincipal AdminUserDetails principal) {
        return roadMarkService.findMineForControllerDtos(requirePrincipal(principal));
    }

    @GetMapping("/mine")
    public List<RoadMarkDto> mine(@AuthenticationPrincipal AdminUserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return roadMarkService.findMineDtos(principal.getUser().getId());
    }

    @GetMapping("/{id:\\d+}")
    public RoadMarkDto get(@PathVariable Long id, @AuthenticationPrincipal AdminUserDetails principal) {
        return roadMarkService.getDto(id, currentUserId(principal));
    }

    @PostMapping("/{markId:\\d+}/like")
    public MarkLikeResponse like(
            @PathVariable Long markId, @AuthenticationPrincipal AdminUserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return markInteractionService.toggleLike(markId, principal.getUser().getId());
    }

    @GetMapping("/{markId:\\d+}/comments")
    public MarkCommentPageResponse comments(
            @PathVariable Long markId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return markInteractionService.getComments(markId, page, size);
    }

    @GetMapping("/{markId:\\d+}/likes")
    public MarkLikePageResponse likes(
            @PathVariable Long markId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return markInteractionService.getLikes(markId, page, size);
    }

    @PostMapping("/{markId:\\d+}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public MarkCommentDto addComment(
            @PathVariable Long markId,
            @Valid @RequestBody MarkCommentCreateRequest request,
            @AuthenticationPrincipal AdminUserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return markInteractionService.addComment(markId, principal.getUser().getId(), request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoadMarkDto create(
            @RequestBody RoadMarkCreateRequest request,
            @AuthenticationPrincipal AdminUserDetails principal) {
        Long jwtUserId = principal != null ? principal.getUser().getId() : null;
        return roadMarkService.create(request, jwtUserId);
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> upload(
            @RequestParam("image") MultipartFile file,
            HttpServletRequest request) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only image files are allowed");
        }

        String ext = contentType.contains("png") ? ".png" : ".jpg";
        String filename = UUID.randomUUID() + ext;

        try {
            Path dir = Paths.get(uploadsDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), dir.resolve(filename));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save image");
        }

        String base = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort();
        return Map.of("image_url", base + "/uploads/" + filename);
    }

    @PatchMapping("/{id:\\d+}/status")
    public RoadMarkDto updateStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request,
            @AuthenticationPrincipal AdminUserDetails principal) {
        User user = requirePrincipal(principal);
        if (user.getRole() == UserRole.ADMIN) {
            return roadMarkService.updateStatus(id, request);
        }
        return roadMarkService.updateStatusByController(id, request, user);
    }

    @DeleteMapping("/{id:\\d+}")
    public Map<String, Boolean> delete(@PathVariable Long id) {
        roadMarkService.delete(id);
        return Map.of("deleted", true);
    }

    private Long currentUserId(AdminUserDetails principal) {
        return principal != null ? principal.getUser().getId() : null;
    }

    private User requirePrincipal(AdminUserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return principal.getUser();
    }
}
