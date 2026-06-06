package kz.citydrive.admin.repository;

import kz.citydrive.admin.domain.Company;
import kz.citydrive.admin.domain.CompanyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByUserId(Long userId);

    Optional<Company> findByBin(String bin);

    Optional<Company> findByBinAndUserIdNot(String bin, Long userId);

    List<Company> findAllByOrderByUpdatedAtDesc();

    List<Company> findByStatusOrderBySubmittedAtDesc(CompanyStatus status);

    void deleteByUserId(Long userId);
}
