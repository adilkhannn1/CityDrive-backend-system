package kz.citydrive.admin.repository;

import kz.citydrive.admin.domain.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findByActiveTrueOrderBySortOrderAsc();

    List<City> findAllByOrderBySortOrderAsc();

    Optional<City> findTopByOrderBySortOrderDesc();

    boolean existsByIdAndActiveTrue(Long id);
}
