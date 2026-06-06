package kz.citydrive.admin.repository;

import kz.citydrive.admin.domain.User;
import kz.citydrive.admin.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phone);

    List<User> findByRole(UserRole role);

    long countByRole(UserRole role);

    boolean existsByPhone(String phone);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    boolean existsByCityId(Integer cityId);
}
