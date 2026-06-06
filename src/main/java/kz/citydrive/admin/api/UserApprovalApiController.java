package kz.citydrive.admin.api;

import kz.citydrive.admin.dto.ApprovalStatusResponse;
import kz.citydrive.admin.dto.DeleteResponse;
import kz.citydrive.admin.security.AdminUserDetails;
import kz.citydrive.admin.service.CompanyService;
import kz.citydrive.admin.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserApprovalApiController {

    private final UserService userService;
    private final CompanyService companyService;

    public UserApprovalApiController(UserService userService, CompanyService companyService) {
        this.userService = userService;
        this.companyService = companyService;
    }

    @GetMapping("/approval-status")
    public ApprovalStatusResponse approvalStatus(@AuthenticationPrincipal AdminUserDetails principal) {
        if (principal == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return companyService.getApprovalStatus(principal.getUser().getId());
    }

    @DeleteMapping("/account")
    public DeleteResponse deleteAccount(@AuthenticationPrincipal AdminUserDetails principal) {
        if (principal == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        Long userId = principal.getUser().getId();
        userService.deleteUser(userId);
        return new DeleteResponse("Account deleted");
    }
}
