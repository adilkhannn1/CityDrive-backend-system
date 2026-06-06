package kz.citydrive.admin.web;

import kz.citydrive.admin.domain.Company;
import kz.citydrive.admin.dto.CompanyAdminView;
import kz.citydrive.admin.service.CompanyService;
import kz.citydrive.admin.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/admin/companies")
public class AdminCompaniesController {

    private final CompanyService companyService;
    private final FileStorageService fileStorageService;

    public AdminCompaniesController(CompanyService companyService, FileStorageService fileStorageService) {
        this.companyService = companyService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("applications", companyService.findAllForAdmin());
        return "admin/companies";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        CompanyAdminView view = companyService.getAdminView(id);
        model.addAttribute("view", view);
        return "admin/company-detail";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes ra) {
        try {
            companyService.approveApplication(id);
            ra.addFlashAttribute("message", "Заявка одобрена");
        } catch (ResponseStatusException e) {
            ra.addFlashAttribute("error", e.getReason());
        }
        return "redirect:/admin/companies/" + id;
    }

    @PostMapping("/{id}/reject")
    public String reject(
            @PathVariable Long id,
            @RequestParam String rejectionReason,
            RedirectAttributes ra) {
        try {
            companyService.rejectApplication(id, rejectionReason);
            ra.addFlashAttribute("message", "Заявка отклонена");
        } catch (ResponseStatusException e) {
            ra.addFlashAttribute("error", e.getReason());
        }
        return "redirect:/admin/companies/" + id;
    }

    @GetMapping("/{id}/files/registration-certificate")
    public ResponseEntity<Resource> registrationCertificate(
            @PathVariable Long id, @RequestParam(defaultValue = "inline") String disposition) {
        return serveDocument(id, true, disposition);
    }

    @GetMapping("/{id}/files/portfolio")
    public ResponseEntity<Resource> portfolio(
            @PathVariable Long id, @RequestParam(defaultValue = "inline") String disposition) {
        return serveDocument(id, false, disposition);
    }

    private ResponseEntity<Resource> serveDocument(Long companyId, boolean registrationCertificate, String disposition) {
        CompanyAdminView view = companyService.getAdminView(companyId);
        Company company = view.getCompany();
        String storedPath = registrationCertificate
                ? company.getRegistrationCertificateUrl()
                : company.getPortfolioUrl();
        if (storedPath == null || storedPath.isBlank()) {
            throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "Document not uploaded");
        }

        Resource resource = fileStorageService.loadStoredFile(storedPath);
        MediaType mediaType = fileStorageService.mediaTypeFor(storedPath);
        String filename = fileStorageService.downloadFilename(
                storedPath,
                registrationCertificate ? "registration_certificate.pdf" : "portfolio.pdf");
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        String mode = "attachment".equalsIgnoreCase(disposition) ? "attachment" : "inline";

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, mode + "; filename*=UTF-8''" + encodedFilename)
                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=3600")
                .body(resource);
    }
}
