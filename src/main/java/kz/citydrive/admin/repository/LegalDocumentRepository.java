package kz.citydrive.admin.repository;

import kz.citydrive.admin.domain.LegalDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LegalDocumentRepository extends JpaRepository<LegalDocument, Long> {

    List<LegalDocument> findByActiveTrueOrderBySortOrderAsc();

    List<LegalDocument> findAllByOrderBySortOrderAsc();

    Optional<LegalDocument> findTopByOrderBySortOrderDesc();
}
