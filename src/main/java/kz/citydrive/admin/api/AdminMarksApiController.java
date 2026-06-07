package kz.citydrive.admin.api;

import kz.citydrive.admin.dto.AdminRejectApplicationRequest;
import kz.citydrive.admin.dto.RoadMarkDto;
import kz.citydrive.admin.service.RoadMarkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/marks")
public class AdminMarksApiController {

    private final RoadMarkService roadMarkService;

    public AdminMarksApiController(RoadMarkService roadMarkService) {
        this.roadMarkService = roadMarkService;
    }

    @GetMapping("/controller-applications")
    public List<RoadMarkDto> controllerApplications() {
        return roadMarkService.findControllerApplicationsForAdmin();
    }

    @PatchMapping("/{id:\\d+}/approve-work-start")
    public RoadMarkDto approveWorkStart(@PathVariable Long id) {
        return roadMarkService.approveWorkStart(id);
    }

    @PatchMapping("/{id:\\d+}/reject-controller-application")
    public RoadMarkDto rejectControllerApplication(
            @PathVariable Long id, @RequestBody(required = false) AdminRejectApplicationRequest request) {
        String adminNote = request != null ? request.getAdminNote() : null;
        return roadMarkService.rejectControllerApplication(id, adminNote);
    }

    @GetMapping("/work-reports")
    public List<RoadMarkDto> workReports() {
        return roadMarkService.findWorkReportsForAdmin();
    }

    @PatchMapping("/{id:\\d+}/approve-work-report")
    public RoadMarkDto approveWorkReport(@PathVariable Long id) {
        return roadMarkService.approveWorkReport(id);
    }

    @PatchMapping("/{id:\\d+}/reject-work-report")
    public RoadMarkDto rejectWorkReport(
            @PathVariable Long id, @RequestBody(required = false) AdminRejectApplicationRequest request) {
        String adminNote = request != null ? request.getAdminNote() : null;
        return roadMarkService.rejectWorkReport(id, adminNote);
    }
}
