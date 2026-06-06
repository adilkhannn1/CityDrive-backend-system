package kz.citydrive.admin.api;

import jakarta.servlet.http.HttpServletRequest;
import kz.citydrive.admin.dto.CompanyDocumentsResponse;
import kz.citydrive.admin.dto.CompanyRegistrationStateResponse;
import kz.citydrive.admin.dto.CompanyRequest;
import kz.citydrive.admin.dto.CompanyResponse;
import kz.citydrive.admin.security.AdminUserDetails;
import kz.citydrive.admin.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/company")
public class CompanyApiController {

    private final CompanyService companyService;

    public CompanyApiController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public CompanyResponse create(
            @AuthenticationPrincipal AdminUserDetails principal,
            @RequestBody CompanyRequest request,
            HttpServletRequest httpRequest) {
        CompanyResponse response = companyService.saveCompanyData(requireUserId(principal), request);
        return enrichUrls(response, requireUserId(principal), httpRequest);
    }

    @PutMapping
    public CompanyResponse update(
            @AuthenticationPrincipal AdminUserDetails principal,
            @RequestBody CompanyRequest request,
            HttpServletRequest httpRequest) {
        CompanyResponse response = companyService.saveCompanyData(requireUserId(principal), request);
        return enrichUrls(response, requireUserId(principal), httpRequest);
    }

    @GetMapping
    public CompanyResponse get(
            @AuthenticationPrincipal AdminUserDetails principal, HttpServletRequest httpRequest) {
        return companyService.getCompany(requireUserId(principal), httpRequest);
    }

    @PostMapping("/documents")
    public CompanyDocumentsResponse uploadDocuments(
            @AuthenticationPrincipal AdminUserDetails principal,
            @RequestParam("registration_certificate") MultipartFile registrationCertificate,
            @RequestParam("portfolio") MultipartFile portfolio,
            HttpServletRequest httpRequest) {
        return companyService.uploadDocuments(
                requireUserId(principal), registrationCertificate, portfolio, httpRequest);
    }

    @GetMapping("/registration-state")
    public CompanyRegistrationStateResponse registrationState(
            @AuthenticationPrincipal AdminUserDetails principal) {
        return companyService.getRegistrationState(requireUserId(principal));
    }

    private CompanyResponse enrichUrls(CompanyResponse response, Long userId, HttpServletRequest httpRequest) {
        return companyService.getCompany(userId, httpRequest);
    }

    private Long requireUserId(AdminUserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return principal.getUser().getId();
    }
}
