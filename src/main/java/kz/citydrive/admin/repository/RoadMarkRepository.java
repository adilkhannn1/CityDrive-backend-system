package kz.citydrive.admin.repository;

import kz.citydrive.admin.domain.MarkStatus;
import kz.citydrive.admin.domain.RoadMark;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoadMarkRepository extends JpaRepository<RoadMark, Long> {

    List<RoadMark> findByStatus(MarkStatus status);

    List<RoadMark> findByStatusIn(List<MarkStatus> statuses);

    List<RoadMark> findByStatusInAndAssignedControllerIdIsNullOrderByReportedDateDesc(List<MarkStatus> statuses);

    List<RoadMark> findByAssignedControllerIdAndStatusInOrderByReportedDateDesc(
            Long assignedControllerId, List<MarkStatus> statuses);

    long countByStatusInAndAssignedControllerIdIsNull(List<MarkStatus> statuses);

    long countByAssignedControllerIdAndStatusIn(Long assignedControllerId, List<MarkStatus> statuses);

    long countByAssignedControllerIdAndStatus(Long assignedControllerId, MarkStatus status);

    @Query(
            """
            SELECT m FROM RoadMark m
            WHERE m.status IN :statuses
            AND m.assignedControllerId IS NULL
            AND (:query IS NULL OR :query = '' OR LOWER(m.title) LIKE LOWER(CONCAT('%', :query, '%'))
                 OR LOWER(COALESCE(m.address, '')) LIKE LOWER(CONCAT('%', :query, '%')))
            AND (:severity IS NULL OR :severity = '' OR m.severity = :severity)
            AND (:type IS NULL OR :type = '' OR m.type = :type)
            ORDER BY m.reportedDate DESC
            """)
    List<RoadMark> searchPendingForController(
            @Param("statuses") List<MarkStatus> statuses,
            @Param("query") String query,
            @Param("severity") String severity,
            @Param("type") String type,
            Pageable pageable);

    List<RoadMark> findByAuthorUserIdOrderByReportedDateDesc(Long authorUserId);

    long countByStatus(MarkStatus status);

    void deleteByAuthorUserId(Long authorUserId);

    @Modifying
    @Query("UPDATE RoadMark m SET m.assignedControllerId = null WHERE m.assignedControllerId = :userId")
    void clearAssignedController(@Param("userId") Long userId);
}
