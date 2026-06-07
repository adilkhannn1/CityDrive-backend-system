package kz.citydrive.admin.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(basePackages = "kz.citydrive.admin.web")
public class AdminWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(AdminWebExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, RedirectAttributes redirectAttributes) {
        log.error("Admin panel error", ex);
        redirectAttributes.addFlashAttribute(
                "error", ex.getMessage() != null ? ex.getMessage() : "Unexpected error");
        return "redirect:/admin";
    }
}
