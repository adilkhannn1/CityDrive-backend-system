package kz.citydrive.admin.repository;

import kz.citydrive.admin.domain.PendingRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PendingRegistrationRepository extends JpaRepository<PendingRegistration, Long> {

    Optional<PendingRegistration> findByPhone(String phone);

    void deleteByPhone(String phone);
}
