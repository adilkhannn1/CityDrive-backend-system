package kz.citydrive.admin.repository;

import kz.citydrive.admin.domain.MarkLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MarkLikeRepository extends JpaRepository<MarkLike, Long> {

    Optional<MarkLike> findByMarkIdAndUserId(Long markId, Long userId);

    boolean existsByMarkIdAndUserId(Long markId, Long userId);

    long countByMarkId(Long markId);

    void deleteByMarkId(Long markId);

    void deleteByUserId(Long userId);

    Page<MarkLike> findByMarkIdOrderByCreatedAtDesc(Long markId, Pageable pageable);

    List<MarkLike> findByMarkIdOrderByCreatedAtDesc(Long markId);

    @Query("SELECT ml.markId FROM MarkLike ml WHERE ml.userId = :userId")
    Set<Long> findMarkIdsByUserId(@Param("userId") Long userId);
}
