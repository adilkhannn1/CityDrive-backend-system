package kz.citydrive.admin.api;

import kz.citydrive.admin.dto.ApprovalStatusResponse;
import kz.citydrive.admin.dto.DeleteResponse;
import kz.citydrive.admin.dto.UserApprovalUpdateRequest;
import kz.citydrive.admin.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserApiController {

    private final UserService userService;

    public AdminUserApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}/approval-status")
    public ApprovalStatusResponse getApprovalStatus(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> new ApprovalStatusResponse(user.isApproved()))
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "User not found"));
    }

    @PatchMapping("/{id}/approval")
    public ApprovalStatusResponse updateApproval(
            @PathVariable Long id,
            @RequestBody UserApprovalUpdateRequest request) {
        return new ApprovalStatusResponse(userService.setApproval(id, request.isApproved()).isApproved());
    }

    @DeleteMapping("/{id}")
    public DeleteResponse delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return new DeleteResponse("User deleted");
    }
}
