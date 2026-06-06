package kz.citydrive.admin.api;

import kz.citydrive.admin.dto.ControllerDashboardDto;
import kz.citydrive.admin.dto.RoadMarkDto;
import kz.citydrive.admin.security.AdminUserDetails;
import kz.citydrive.admin.service.RoadMarkService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/controller")
public class ControllerApiController {

    private final RoadMarkService roadMarkService;

    public ControllerApiController(RoadMarkService roadMarkService) {
        this.roadMarkService = roadMarkService;
    }

    @GetMapping("/dashboard")
    public ControllerDashboardDto dashboard(
            @AuthenticationPrincipal AdminUserDetails principal,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        return roadMarkService.getControllerDashboard(
                requirePrincipal(principal), q, severity, type, limit, offset);
    }

    @GetMapping("/marks/mine")
    public List<RoadMarkDto> myMarks(@AuthenticationPrincipal AdminUserDetails principal) {
        return roadMarkService.findMineForControllerDtos(requirePrincipal(principal));
    }

    private kz.citydrive.admin.domain.User requirePrincipal(AdminUserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return principal.getUser();
    }
}
