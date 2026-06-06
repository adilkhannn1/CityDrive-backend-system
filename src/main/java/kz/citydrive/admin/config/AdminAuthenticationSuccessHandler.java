package kz.citydrive.admin.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kz.citydrive.admin.domain.UserRole;
import kz.citydrive.admin.security.AdminUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AdminAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        if (authentication.getPrincipal() instanceof AdminUserDetails details
                && details.getUser().getRole() == UserRole.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/admin");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/login?error=access");
    }
}
