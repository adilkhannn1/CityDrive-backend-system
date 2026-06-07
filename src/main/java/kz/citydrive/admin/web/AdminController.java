package kz.citydrive.admin.web;

import kz.citydrive.admin.domain.RoadMark;
import kz.citydrive.admin.domain.User;
import kz.citydrive.admin.dto.RoadMarkDto;
import kz.citydrive.admin.dto.StatusUpdateRequest;
import kz.citydrive.admin.service.MarkInteractionService;
import kz.citydrive.admin.service.RoadMarkService;
import kz.citydrive.admin.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RoadMarkService roadMarkService;
    private final UserService userService;
    private final MarkInteractionService markInteractionService;

    public AdminController(
            RoadMarkService roadMarkService,
            UserService userService,
            MarkInteractionService markInteractionService) {
        this.roadMarkService = roadMarkService;
        this.userService = userService;
        this.markInteractionService = markInteractionService;
    }

    @GetMapping
    public String dashboard(Model model) {
        Map<String, Long> stats = roadMarkService.dashboardStats();
        model.addAttribute("stats", stats);
        model.addAttribute("userCount", userService.findAll().size());
        return "admin/dashboard";
    }

    @GetMapping("/marks")
    public String marks(@RequestParam(required = false) String status, Model model) {
        try {
            List<RoadMark> marks = roadMarkService.findForAdminPanel(status);
            model.addAttribute("marks", marks);
            model.addAttribute("controllerLabels", roadMarkService.buildControllerLabels(marks));
            model.addAttribute("statusFilter", status != null ? status : "");
        } catch (Exception ex) {
            model.addAttribute("marks", List.of());
            model.addAttribute("controllerLabels", Map.of());
            model.addAttribute("statusFilter", status != null ? status : "");
            model.addAttribute("error", "Не удалось загрузить отметки: " + ex.getMessage());
        }
        return "admin/marks";
    }

    @GetMapping("/marks/{id}")
    public String markDetail(@PathVariable Long id, Model model) {
        RoadMark mark = roadMarkService.getEntity(id);
        RoadMarkDto dto = roadMarkService.getDto(id, null);
        model.addAttribute("mark", mark);
        model.addAttribute("images", dto.getImages());
        model.addAttribute("assignedControllerLabel", roadMarkService.describeAssignedController(mark.getAssignedControllerId()));
        model.addAttribute("controllers", userService.findControllers());
        model.addAttribute("comments", markInteractionService.findAllCommentsForAdmin(id));
        model.addAttribute("likesList", markInteractionService.findAllLikesForAdmin(id));
        return "admin/mark-detail";
    }

    @PostMapping("/marks/{id}/status")
    public String updateMarkStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) Long assignedControllerId,
            @RequestParam(required = false) String adminNote,
            RedirectAttributes redirectAttributes) {
        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setStatus(status);
        request.setAssignedControllerId(assignedControllerId);
        request.setAdminNote(adminNote);
        roadMarkService.updateStatus(id, request);
        redirectAttributes.addFlashAttribute("message", "Статус обновлён");
        return "redirect:/admin/marks/" + id;
    }

    @GetMapping("/mark-applications")
    public String markApplications(Model model) {
        List<RoadMark> marks = roadMarkService.findControllerApplicationEntities();
        model.addAttribute("marks", marks);
        model.addAttribute("controllerLabels", roadMarkService.buildControllerLabels(marks));
        return "admin/mark-applications";
    }

    @PostMapping("/mark-applications/{id}/approve")
    public String approveWorkStart(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        roadMarkService.approveWorkStart(id);
        redirectAttributes.addFlashAttribute("message", "Начало работы одобрено");
        return "redirect:/admin/in-progress";
    }

    @PostMapping("/mark-applications/{id}/reject")
    public String rejectMarkApplication(
            @PathVariable Long id,
            @RequestParam(required = false) String adminNote,
            RedirectAttributes redirectAttributes) {
        roadMarkService.rejectControllerApplication(id, adminNote);
        redirectAttributes.addFlashAttribute("message", "Заявка контроллера отклонена, отметка снова доступна контроллерам");
        return "redirect:/admin/mark-applications";
    }

    @GetMapping("/in-progress")
    public String inProgressMarks(Model model) {
        List<RoadMark> marks = roadMarkService.findInProgressEntities();
        model.addAttribute("marks", marks);
        model.addAttribute("controllerLabels", roadMarkService.buildControllerLabels(marks));
        return "admin/in-progress";
    }

    @GetMapping("/users")
    public String users(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/users/{id}/approve")
    public String approveUser(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean approved,
            RedirectAttributes redirectAttributes) {
        userService.setApproval(id, approved);
        redirectAttributes.addFlashAttribute(
                "message", approved ? "Пользователь подтверждён" : "Подтверждение снято");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("message", "Пользователь удалён");
        } catch (org.springframework.web.server.ResponseStatusException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getReason());
        }
        return "redirect:/admin/users";
    }
}
