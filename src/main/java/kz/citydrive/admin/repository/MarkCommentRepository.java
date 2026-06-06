package kz.citydrive.admin.repository;

import kz.citydrive.admin.domain.MarkComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarkCommentRepository extends JpaRepository<MarkComment, Long> {

    Page<MarkComment> findByMarkIdOrderByCreatedAtAsc(Long markId, Pageable pageable);

    List<MarkComment> findByMarkIdOrderByCreatedAtAsc(Long markId);

    Optional<MarkComment> findTopByMarkIdOrderByCreatedAtDesc(Long markId);

    long countByMarkId(Long markId);

    void deleteByMarkId(Long markId);

    void deleteByUserId(Long userId);
}
