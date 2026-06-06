package kz.citydrive.admin.service;

import kz.citydrive.admin.domain.City;
import kz.citydrive.admin.dto.CityCreateRequest;
import kz.citydrive.admin.dto.CityDto;
import kz.citydrive.admin.repository.CityRepository;
import kz.citydrive.admin.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CityService {

    private final CityRepository cityRepository;
    private final UserRepository userRepository;

    public CityService(CityRepository cityRepository, UserRepository userRepository) {
        this.cityRepository = cityRepository;
        this.userRepository = userRepository;
    }

    public List<CityDto> findAll() {
        return cityRepository.findByActiveTrueOrderBySortOrderAsc().stream()
                .map(this::toDto)
                .toList();
    }

    public List<City> findAllEntities() {
        return cityRepository.findAllByOrderBySortOrderAsc();
    }

    public City getEntity(Long id) {
        return cityRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found"));
    }

    public int nextSortOrder() {
        return cityRepository.findTopByOrderBySortOrderDesc()
                .map(city -> city.getSortOrder() + 1)
                .orElse(1);
    }

    public Optional<String> findNameById(Integer cityId) {
        if (cityId == null) {
            return Optional.empty();
        }
        return cityRepository.findById(cityId.longValue()).map(City::getName);
    }

    public boolean exists(Integer cityId) {
        if (cityId == null) {
            return false;
        }
        return cityRepository.existsByIdAndActiveTrue(cityId.longValue());
    }

    @Transactional
    public City create(CityCreateRequest request) {
        validateRequest(request);
        City city = new City();
        applyRequest(city, request);
        return cityRepository.save(city);
    }

    @Transactional
    public City update(Long id, CityCreateRequest request) {
        validateRequest(request);
        City city = getEntity(id);
        applyRequest(city, request);
        return cityRepository.save(city);
    }

    @Transactional
    public void delete(Long id) {
        City city = getEntity(id);
        if (userRepository.existsByCityId(id.intValue())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Нельзя удалить город: есть пользователи с этим городом");
        }
        cityRepository.delete(city);
    }

    private void applyRequest(City city, CityCreateRequest request) {
        city.setName(request.getName().trim());
        city.setSortOrder(request.getSortOrder());
        city.setActive(request.isActive());
    }

    private void validateRequest(CityCreateRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
    }

    private CityDto toDto(City city) {
        return new CityDto(city.getId().intValue(), city.getName());
    }
}
