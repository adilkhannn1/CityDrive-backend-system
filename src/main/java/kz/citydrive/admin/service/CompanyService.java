package kz.citydrive.admin.service;

import jakarta.servlet.http.HttpServletRequest;
import kz.citydrive.admin.domain.Company;
import kz.citydrive.admin.domain.CompanyStatus;
import kz.citydrive.admin.domain.User;
import kz.citydrive.admin.domain.UserRole;
import kz.citydrive.admin.dto.CompanyAdminView;
import kz.citydrive.admin.dto.CompanyDocumentsResponse;
import kz.citydrive.admin.dto.CompanyRegistrationStateResponse;
import kz.citydrive.admin.dto.CompanyRequest;
import kz.citydrive.admin.dto.CompanyResponse;
import kz.citydrive.admin.dto.ApprovalStatusResponse;
import kz.citydrive.admin.repository.CompanyRepository;
import kz.citydrive.admin.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class CompanyService {

    private static final Pattern BIN_PATTERN = Pattern.compile("^\\d{12}$");

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CityService cityService;
    private final FileStorageService fileStorageService;

    public CompanyService(
            CompanyRepository companyRepository,
            UserRepository userRepository,
            CityService cityService,
            FileStorageService fileStorageService) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.cityService = cityService;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public CompanyResponse saveCompanyData(Long userId, CompanyRequest request) {
        User user = requireController(userId);
        validateCompanyRequest(request);

        String bin = request.getBin().trim();
        companyRepository.findByBinAndUserIdNot(bin, userId).ifPresent(existing -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "BIN already registered");
        });

        Company company = companyRepository.findByUserId(userId).orElse(null);
        if (company == null) {
            company = new Company();
            company.setUserId(userId);
            company.setStatus(CompanyStatus.DRAFT);
        } else if (company.getStatus() == CompanyStatus.APPROVED) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Company data can only be changed via admin after approval");
        } else if (company.getStatus() == CompanyStatus.PENDING_REVIEW) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Application is under review and cannot be edited");
        }

        applyRequest(company, request);
        if (company.getStatus() == CompanyStatus.REJECTED) {
            company.setStatus(CompanyStatus.DRAFT);
            company.setRejectionReason(null);
        }

        return CompanyResponse.fromEntity(companyRepository.save(company));
    }

    public CompanyResponse getCompany(Long userId, HttpServletRequest request) {
        requireController(userId);
        Company company = companyRepository
                .findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        return toResponse(company, request);
    }

    public Optional<Company> findByUserId(Long userId) {
        return companyRepository.findByUserId(userId);
    }

    @Transactional
    public CompanyDocumentsResponse uploadDocuments(
            Long userId,
            MultipartFile registrationCertificate,
            MultipartFile portfolio,
            HttpServletRequest request) {
        requireController(userId);

        Company company = companyRepository
                .findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Сначала заполните данные компании"));

        if (company.getStatus() == CompanyStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Company is already approved");
        }
        if (company.getStatus() == CompanyStatus.PENDING_REVIEW) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Application is already under review");
        }

        if (registrationCertificate == null
                || registrationCertificate.isEmpty()
                || portfolio == null
                || portfolio.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both documents are required");
        }

        String certPath =
                fileStorageService.saveCompanyDocument(userId, "registration_certificate", registrationCertificate);
        String portfolioPath = fileStorageService.saveCompanyDocument(userId, "portfolio", portfolio);

        company.setRegistrationCertificateUrl(certPath);
        company.setPortfolioUrl(portfolioPath);
        company.setStatus(CompanyStatus.PENDING_REVIEW);
        company.setRejectionReason(null);
        company.setSubmittedAt(Instant.now());

        Company saved = companyRepository.save(company);
        User user = userRepository.findById(userId).orElseThrow();
        user.setApproved(false);
        userRepository.save(user);

        return new CompanyDocumentsResponse(
                "Заявка отправлена на рассмотрение", toResponse(saved, request));
    }

    public CompanyRegistrationStateResponse getRegistrationState(Long userId) {
        User user = requireUser(userId);
        Optional<Company> companyOpt = companyRepository.findByUserId(userId);

        if (companyOpt.isEmpty()) {
            return new CompanyRegistrationStateResponse(
                    "company_data", user.isApproved(), null, null);
        }

        Company company = companyOpt.get();
        String step = switch (company.getStatus()) {
            case DRAFT -> "documents";
            case PENDING_REVIEW -> "pending_review";
            case APPROVED -> "approved";
            case REJECTED -> "rejected";
        };

        return new CompanyRegistrationStateResponse(
                step, user.isApproved(), company.getStatus(), company.getRejectionReason());
    }

    public ApprovalStatusResponse getApprovalStatus(Long userId) {
        User user = requireUser(userId);
        Optional<Company> companyOpt = companyRepository.findByUserId(userId);

        if (companyOpt.isEmpty()) {
            return new ApprovalStatusResponse(user.isApproved());
        }

        Company company = companyOpt.get();
        return new ApprovalStatusResponse(
                user.isApproved(), company.getStatus(), company.getRejectionReason());
    }

    public List<CompanyAdminView> findAllForAdmin() {
        List<CompanyAdminView> views = new ArrayList<>();
        for (Company company : companyRepository.findAllByOrderByUpdatedAtDesc()) {
            User user = userRepository.findById(company.getUserId()).orElse(null);
            if (user == null) {
                continue;
            }
            String cityName = cityService.findNameById(user.getCityId()).orElse("—");
            views.add(new CompanyAdminView(company, user, cityName));
        }
        return views;
    }

    public CompanyAdminView getAdminView(Long companyId) {
        Company company = companyRepository
                .findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        User user = userRepository
                .findById(company.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        String cityName = cityService.findNameById(user.getCityId()).orElse("—");
        return new CompanyAdminView(company, user, cityName);
    }

    @Transactional
    public void approveApplication(Long companyId) {
        Company company = requireCompany(companyId);
        if (company.getStatus() != CompanyStatus.PENDING_REVIEW) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Only pending applications can be approved");
        }

        company.setStatus(CompanyStatus.APPROVED);
        company.setRejectionReason(null);
        companyRepository.save(company);

        User user = userRepository.findById(company.getUserId()).orElseThrow();
        user.setApproved(true);
        userRepository.save(user);
    }

    @Transactional
    public void rejectApplication(Long companyId, String rejectionReason) {
        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rejection reason is required");
        }

        Company company = requireCompany(companyId);
        if (company.getStatus() != CompanyStatus.PENDING_REVIEW) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Only pending applications can be rejected");
        }

        company.setStatus(CompanyStatus.REJECTED);
        company.setRejectionReason(rejectionReason.trim());
        companyRepository.save(company);

        User user = userRepository.findById(company.getUserId()).orElseThrow();
        user.setApproved(false);
        userRepository.save(user);
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        companyRepository.findByUserId(userId).ifPresent(company -> {
            companyRepository.delete(company);
            fileStorageService.deleteCompanyDirectory(userId);
        });
    }

    public CompanyResponse toResponse(Company company, HttpServletRequest request) {
        CompanyResponse response = CompanyResponse.fromEntity(company);
        response.setRegistrationCertificateUrl(
                fileStorageService.resolvePublicUrl(company.getRegistrationCertificateUrl(), request));
        response.setPortfolioUrl(fileStorageService.resolvePublicUrl(company.getPortfolioUrl(), request));
        return response;
    }

    private Company requireCompany(Long companyId) {
        return companyRepository
                .findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
    }

    private User requireController(Long userId) {
        User user = requireUser(userId);
        if (user.getRole() != UserRole.CONTROLLER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only controllers can manage company data");
        }
        return user;
    }

    private User requireUser(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }

    private void applyRequest(Company company, CompanyRequest request) {
        company.setName(request.getName().trim());
        company.setBin(request.getBin().trim());
        company.setLegalAddress(request.getLegalAddress().trim());
        company.setFoundedYear(request.getFoundedYear());
    }

    private void validateCompanyRequest(CompanyRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "name is required");
        }
        if (request.getBin() == null || request.getBin().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "bin is required");
        }
        if (!BIN_PATTERN.matcher(request.getBin().trim()).matches()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "BIN must be 12 digits");
        }
        if (request.getLegalAddress() == null || request.getLegalAddress().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "legal_address is required");
        }
        if (request.getFoundedYear() == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "founded_year is required");
        }
        int year = request.getFoundedYear();
        int currentYear = Year.now().getValue();
        if (year < 1900 || year > currentYear) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "founded_year must be between 1900 and " + currentYear);
        }
    }
}
