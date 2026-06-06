package kz.citydrive.admin.service;

import kz.citydrive.admin.domain.Company;
import kz.citydrive.admin.domain.CompanyStatus;
import kz.citydrive.admin.domain.User;
import kz.citydrive.admin.domain.UserRole;
import kz.citydrive.admin.repository.CompanyRepository;
import kz.citydrive.admin.repository.MarkCommentRepository;
import kz.citydrive.admin.repository.MarkLikeRepository;
import kz.citydrive.admin.repository.PendingRegistrationRepository;
import kz.citydrive.admin.repository.RoadMarkRepository;
import kz.citydrive.admin.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoadMarkRepository roadMarkRepository;
    private final PendingRegistrationRepository pendingRegistrationRepository;
    private final MarkLikeRepository markLikeRepository;
    private final MarkCommentRepository markCommentRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyService companyService;

    public UserService(
            UserRepository userRepository,
            RoadMarkRepository roadMarkRepository,
            PendingRegistrationRepository pendingRegistrationRepository,
            MarkLikeRepository markLikeRepository,
            MarkCommentRepository markCommentRepository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder,
            CompanyService companyService) {
        this.userRepository = userRepository;
        this.roadMarkRepository = roadMarkRepository;
        this.pendingRegistrationRepository = pendingRegistrationRepository;
        this.markLikeRepository = markLikeRepository;
        this.markCommentRepository = markCommentRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyService = companyService;
    }

    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findControllers() {
        return userRepository.findByRole(UserRole.CONTROLLER);
    }

    @Transactional
    public User createUser(String fullName, String phone, String rawPassword, UserRole role) {
        User user = new User();
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setBlocked(false);
        user.setApproved(role != UserRole.CONTROLLER);
        return userRepository.save(user);
    }

    @Transactional
    public User createRegisteredUser(
            String fullName,
            String phone,
            String passwordHash,
            Integer cityId,
            String birthDate,
            UserRole role) {
        User user = new User();
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setPasswordHash(passwordHash);
        user.setRole(role);
        user.setBlocked(false);
        user.setCityId(cityId);
        user.setBirthDate(birthDate);
        user.setApproved(role != UserRole.CONTROLLER);
        return userRepository.save(user);
    }

    @Transactional
    public User setApproval(Long userId, boolean approved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "User not found"));

        if (approved && user.getRole() == UserRole.CONTROLLER) {
            Optional<Company> company = companyRepository.findByUserId(userId);
            if (company.isEmpty() || company.get().getStatus() != CompanyStatus.APPROVED) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Контроллера можно одобрить только через раздел «Заявки контроллеров»");
            }
        }

        user.setApproved(approved);
        return userRepository.save(user);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matchesPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() == UserRole.ADMIN && userRepository.countByRole(UserRole.ADMIN) <= 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete the last admin");
        }

        pendingRegistrationRepository.deleteByPhone(user.getPhone());
        companyService.deleteByUserId(userId);
        markLikeRepository.deleteByUserId(userId);
        markCommentRepository.deleteByUserId(userId);
        roadMarkRepository.clearAssignedController(userId);
        roadMarkRepository.deleteByAuthorUserId(userId);
        userRepository.delete(user);
    }
}
