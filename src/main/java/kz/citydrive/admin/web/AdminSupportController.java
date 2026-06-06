package kz.citydrive.admin.web;

import kz.citydrive.admin.dto.SupportUpdateRequest;
import kz.citydrive.admin.service.SupportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/support")
public class AdminSupportController {

    private final SupportService supportService;

    public AdminSupportController(SupportService supportService) {
        this.supportService = supportService;
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("support", supportService.getFormData());
        return "admin/support";
    }

    @PostMapping
    public String save(@ModelAttribute SupportUpdateRequest support, RedirectAttributes redirectAttributes) {
        supportService.updateSettings(support);
        redirectAttributes.addFlashAttribute("message", "Настройки службы поддержки сохранены");
        return "redirect:/admin/support";
    }
}
