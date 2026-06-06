package kz.citydrive.admin.api;

import kz.citydrive.admin.dto.CitiesListResponse;
import kz.citydrive.admin.service.CityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cities")
public class CitiesApiController {

    private final CityService cityService;

    public CitiesApiController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping
    public CitiesListResponse list() {
        return new CitiesListResponse(cityService.findAll());
    }
}
