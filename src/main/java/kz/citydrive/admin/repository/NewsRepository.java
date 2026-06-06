package kz.citydrive.admin.repository;

import kz.citydrive.admin.domain.News;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByPublishedTrueOrderByPublishedAtDesc(Pageable pageable);
    List<News> findAllByOrderByPublishedAtDesc();
}
