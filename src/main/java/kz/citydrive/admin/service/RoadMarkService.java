package kz.citydrive.admin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.citydrive.admin.domain.Company;
import kz.citydrive.admin.domain.MarkStatus;
import kz.citydrive.admin.domain.RoadMark;
import kz.citydrive.admin.domain.User;
import kz.citydrive.admin.domain.UserRole;
import kz.citydrive.admin.dto.AssignedControllerDto;
import kz.citydrive.admin.dto.ControllerDashboardDto;
import kz.citydrive.admin.dto.ControllerDashboardStatsDto;
import kz.citydrive.admin.dto.RoadMarkCreateRequest;
import kz.citydrive.admin.dto.RoadMarkDto;
import kz.citydrive.admin.dto.StatusUpdateRequest;
import kz.citydrive.admin.repository.CompanyRepository;
import kz.citydrive.admin.repository.RoadMarkRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoadMarkService {

    private static final List<MarkStatus> CONTROLLER_PENDING_STATUSES =
            List.of(MarkStatus.NEW, MarkStatus.PENDING);
    private static final List<MarkStatus> CONTROLLER_ASSIGNED_STATUSES =
            List.of(MarkStatus.CONFIRMED, MarkStatus.IN_PROGRESS, MarkStatus.FIXED, MarkStatus.REJECTED);
    private static final List<MarkStatus> CONTROLLER_IN_WORK_STATUSES =
            List.of(MarkStatus.CONFIRMED, MarkStatus.IN_PROGRESS);

    private final RoadMarkRepository roadMarkRepository;
    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final MarkInteractionService markInteractionService;

    public RoadMarkService(
            RoadMarkRepository roadMarkRepository,
            CompanyRepository companyRepository,
            UserService userService,
            ObjectMapper objectMapper,
            MarkInteractionService markInteractionService) {
        this.roadMarkRepository = roadMarkRepository;
        this.companyRepository = companyRepository;
        this.userService = userService;
        this.objectMapper = objectMapper;
        this.markInteractionService = markInteractionService;
    }

    /** Только подтверждённые отметки — для карты в мобильном приложении */
    public List<RoadMarkDto> findAllDtos(Long currentUserId) {
        Set<Long> likedIds = markInteractionService.findLikedMarkIds(currentUserId);
        return roadMarkRepository.findByStatusIn(
                        List.of(MarkStatus.CONFIRMED, MarkStatus.IN_PROGRESS, MarkStatus.FIXED))
                .stream()
                .map(m -> toDto(m, likedIds, false, currentUserId))
                .collect(Collectors.toList());
    }

    /** Все отметки — для таблицы в админ-панели */
    public List<RoadMarkDto> findAllDtosForAdmin() {
        return roadMarkRepository.findAll().stream()
                .map(m -> RoadMarkDto.fromEntity(m, objectMapper))
                .collect(Collectors.toList());
    }

    public List<RoadMarkDto> findByStatusFilter(String statusFilter) {
        List<RoadMark> marks;
        if (statusFilter == null || statusFilter.isBlank()) {
            marks = roadMarkRepository.findAll();
        } else {
            marks = roadMarkRepository.findByStatus(MarkStatus.fromValue(statusFilter));
        }
        return marks.stream()
                .map(m -> RoadMarkDto.fromEntity(m, objectMapper))
                .collect(Collectors.toList());
    }

    public List<RoadMark> findAllEntities() {
        return roadMarkRepository.findAll();
    }

    public List<RoadMark> findByStatusFilterEntities(String statusFilter) {
        if (statusFilter == null || statusFilter.isBlank()) {
            return roadMarkRepository.findAll();
        }
        return roadMarkRepository.findByStatus(MarkStatus.fromValue(statusFilter));
    }

    public RoadMarkDto getDto(Long id, Long currentUserId) {
        RoadMark mark = getEntity(id);
        Set<Long> likedIds = markInteractionService.findLikedMarkIds(currentUserId);
        return toDto(mark, likedIds, true, currentUserId);
    }

    public RoadMark getEntity(Long id) {
        return roadMarkRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mark not found"));
    }

    public List<RoadMarkDto> findPendingDtos(Long currentUserId) {
        return findPendingDtosForController(null, null, null, null, 100, 0, currentUserId);
    }

    public List<RoadMarkDto> findPendingDtosForController(
            User controller,
            String query,
            String severity,
            String type,
            Integer limit,
            Integer offset,
            Long currentUserId) {
        if (controller != null) {
            requireApprovedController(controller);
        }

        int pageSize = limit == null || limit <= 0 ? 100 : Math.min(limit, 200);
        int pageNumber = offset == null || offset <= 0 ? 0 : offset / pageSize;

        List<RoadMark> marks = roadMarkRepository.searchPendingForController(
                CONTROLLER_PENDING_STATUSES,
                blankToNull(query),
                blankToNull(severity),
                blankToNull(type),
                PageRequest.of(pageNumber, pageSize));

        Set<Long> likedIds = markInteractionService.findLikedMarkIds(currentUserId);
        return marks.stream().map(m -> toDto(m, likedIds, false, currentUserId)).collect(Collectors.toList());
    }

    public List<RoadMarkDto> findMineForControllerDtos(User controller) {
        requireApprovedController(controller);
        Set<Long> likedIds = markInteractionService.findLikedMarkIds(controller.getId());
        return roadMarkRepository
                .findByAssignedControllerIdAndStatusInOrderByReportedDateDesc(
                        controller.getId(), CONTROLLER_ASSIGNED_STATUSES)
                .stream()
                .map(m -> toDto(m, likedIds, true, controller.getId()))
                .collect(Collectors.toList());
    }

    public ControllerDashboardDto getControllerDashboard(
            User controller, String query, String severity, String type, Integer limit, Integer offset) {
        requireApprovedController(controller);

        long newCount =
                roadMarkRepository.countByStatusInAndAssignedControllerIdIsNull(CONTROLLER_PENDING_STATUSES);
        long inWorkCount = roadMarkRepository.countByAssignedControllerIdAndStatusIn(
                controller.getId(), CONTROLLER_IN_WORK_STATUSES);
        long doneCount =
                roadMarkRepository.countByAssignedControllerIdAndStatus(controller.getId(), MarkStatus.FIXED);

        ControllerDashboardStatsDto stats =
                new ControllerDashboardStatsDto(newCount, newCount, inWorkCount, doneCount);

        List<RoadMarkDto> pendingMarks =
                findPendingDtosForController(controller, query, severity, type, limit, offset, controller.getId());
        List<RoadMarkDto> myMarks = findMineForControllerDtos(controller);

        return new ControllerDashboardDto(stats, pendingMarks, myMarks);
    }

    @Transactional
    public RoadMarkDto create(RoadMarkCreateRequest request, Long jwtUserId) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title is required");
        }
        if (request.getLatitude() == null || request.getLongitude() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "latitude and longitude are required");
        }

        // JWT userId takes priority over body to prevent spoofing
        Long authorId = jwtUserId != null ? jwtUserId : request.getAuthorUserId();
        if (authorId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "author_user_id is required");
        }

        User author = userService.findById(authorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author not found"));

        if (author.isBlocked()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is blocked");
        }

        // Filter out local file:// paths — only real URLs accepted
        List<String> images = request.getImages() != null
                ? request.getImages().stream()
                        .filter(url -> url != null && url.startsWith("http"))
                        .collect(Collectors.toList())
                : Collections.emptyList();

        RoadMark mark = new RoadMark();
        mark.setAuthorUserId(authorId);
        mark.setTitle(request.getTitle());
        mark.setDescription(request.getDescription());
        mark.setAddress(request.getAddress());
        mark.setLat(request.getLatitude());
        mark.setLng(request.getLongitude());
        mark.setType(request.getType());
        mark.setSeverity(request.getSeverity());
        mark.setStatus(MarkStatus.NEW);
        mark.setReportedDate(Instant.now());
        mark.setImagesJson(toImagesJson(images));
        mark.setAuthor(request.getAuthor() != null && !request.getAuthor().isBlank()
                ? request.getAuthor() : author.getFullName());
        mark.setLikes(0);
        mark.setCommentsCount(0);

        return toDto(roadMarkRepository.save(mark), Set.of(), false, jwtUserId);
    }

    @Transactional
    public RoadMarkDto updateStatus(Long id, StatusUpdateRequest request) {
        RoadMark mark = getEntity(id);
        applyStatusUpdate(mark, request, null, false);
        return toDto(roadMarkRepository.save(mark), Set.of(), false, null);
    }

    @Transactional
    public RoadMarkDto updateStatusByController(Long id, StatusUpdateRequest request, User controller) {
        requireApprovedController(controller);

        if (request.getStatus() == null || request.getStatus().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status is required");
        }

        RoadMark mark = getEntity(id);
        MarkStatus targetStatus = MarkStatus.fromValue(request.getStatus());

        if (targetStatus == MarkStatus.CONFIRMED || targetStatus == MarkStatus.REJECTED) {
            acceptOrRejectPendingMark(mark, controller, targetStatus, request);
        } else if (targetStatus == MarkStatus.IN_PROGRESS || targetStatus == MarkStatus.FIXED) {
            requireAssignedController(mark, controller.getId());
            mark.setStatus(targetStatus);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Controllers can only set confirmed, rejected, in_progress or fixed");
        }

        RoadMark saved = roadMarkRepository.save(mark);
        return toDto(saved, markInteractionService.findLikedMarkIds(controller.getId()), true, controller.getId());
    }

    private void acceptOrRejectPendingMark(
            RoadMark mark, User controller, MarkStatus targetStatus, StatusUpdateRequest request) {
        Long controllerId = controller.getId();

        if (mark.getAssignedControllerId() != null && !mark.getAssignedControllerId().equals(controllerId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Заявка уже принята другим контроллером");
        }

        if (targetStatus == MarkStatus.CONFIRMED
                && (mark.getStatus() == MarkStatus.CONFIRMED
                        || mark.getStatus() == MarkStatus.IN_PROGRESS
                        || mark.getStatus() == MarkStatus.FIXED)) {
            if (mark.getAssignedControllerId() == null) {
                mark.setAssignedControllerId(controllerId);
            }
            applyControllerComment(mark, targetStatus, request);
            return;
        }

        if (targetStatus == MarkStatus.REJECTED && mark.getStatus() == MarkStatus.REJECTED) {
            applyControllerComment(mark, targetStatus, request);
            return;
        }

        if (!CONTROLLER_PENDING_STATUSES.contains(mark.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Нельзя изменить статус: заявка не ожидает действия контроллера");
        }

        if (request.getAssignedControllerId() != null && !request.getAssignedControllerId().equals(controllerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Нельзя назначить заявку другому контроллеру");
        }

        mark.setStatus(targetStatus);
        mark.setAssignedControllerId(controllerId);
        applyControllerComment(mark, targetStatus, request);
    }

    private void applyControllerComment(RoadMark mark, MarkStatus targetStatus, StatusUpdateRequest request) {
        String comment = resolveComment(request);
        if (comment != null) {
            mark.setControllerComment(comment);
        }
        if (targetStatus == MarkStatus.CONFIRMED && mark.getAcceptedAt() == null) {
            mark.setAcceptedAt(Instant.now());
        }
    }

    private String resolveComment(StatusUpdateRequest request) {
        if (request.getComment() != null && !request.getComment().isBlank()) {
            return request.getComment().trim();
        }
        if (request.getAdminNote() != null && !request.getAdminNote().isBlank()) {
            return request.getAdminNote().trim();
        }
        return null;
    }

    private void requireAssignedController(RoadMark mark, Long controllerId) {
        if (mark.getAssignedControllerId() == null || !mark.getAssignedControllerId().equals(controllerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Mark is not assigned to you");
        }
    }

    private void applyStatusUpdate(RoadMark mark, StatusUpdateRequest request, User controller, boolean controllerOnly) {
        mark.setStatus(MarkStatus.fromValue(request.getStatus()));

        if (request.getAssignedControllerId() != null) {
            Long controllerId = request.getAssignedControllerId();
            userService.findById(controllerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Controller not found"));
            if (controllerOnly
                    && controller != null
                    && !controllerId.equals(controller.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot assign mark to another controller");
            }
            mark.setAssignedControllerId(controllerId);
        }

        if (request.getAdminNote() != null) {
            mark.setAdminNote(request.getAdminNote());
        }
    }

    public void requireApprovedController(User user) {
        if (user.getRole() != UserRole.CONTROLLER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only controllers can access this resource");
        }
        if (!user.isApproved()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Controller is not approved");
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    @Transactional
    public void delete(Long id) {
        if (!roadMarkRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mark not found");
        }
        markInteractionService.deleteByMarkId(id);
        roadMarkRepository.deleteById(id);
    }

    public Map<String, Long> dashboardStats() {
        return Map.of(
                "total", roadMarkRepository.count(),
                "new", roadMarkRepository.countByStatus(MarkStatus.NEW),
                "pending", roadMarkRepository.countByStatus(MarkStatus.PENDING),
                "confirmed", roadMarkRepository.countByStatus(MarkStatus.CONFIRMED),
                "in_progress", roadMarkRepository.countByStatus(MarkStatus.IN_PROGRESS),
                "fixed", roadMarkRepository.countByStatus(MarkStatus.FIXED),
                "rejected", roadMarkRepository.countByStatus(MarkStatus.REJECTED));
    }

    public List<RoadMarkDto> findMineDtos(Long authorId) {
        Set<Long> likedIds = markInteractionService.findLikedMarkIds(authorId);
        return roadMarkRepository.findByAuthorUserIdOrderByReportedDateDesc(authorId)
                .stream()
                .map(m -> toDto(m, likedIds, false, authorId))
                .collect(Collectors.toList());
    }

    private RoadMarkDto toDto(
            RoadMark mark, Set<Long> likedMarkIds, boolean includeLatestComment, Long currentUserId) {
        RoadMarkDto dto = RoadMarkDto.fromEntity(mark, objectMapper);
        dto.setLikedByMe(currentUserId != null && likedMarkIds.contains(mark.getId()));
        if (includeLatestComment) {
            dto.setLatestComment(markInteractionService.findLatestComment(mark.getId()));
        }
        if (mark.getAssignedControllerId() != null) {
            dto.setAssignedController(buildAssignedController(mark.getAssignedControllerId()));
        }
        return dto;
    }

    private AssignedControllerDto buildAssignedController(Long controllerUserId) {
        return userService
                .findById(controllerUserId)
                .map(user -> {
                    String companyName = companyRepository
                            .findByUserId(user.getId())
                            .map(Company::getName)
                            .orElse(null);
                    return new AssignedControllerDto(user.getId(), user.getFullName(), companyName);
                })
                .orElse(new AssignedControllerDto(controllerUserId, null, null));
    }

    private String toImagesJson(List<String> images) {
        try {
            return objectMapper.writeValueAsString(images != null ? images : Collections.emptyList());
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}
