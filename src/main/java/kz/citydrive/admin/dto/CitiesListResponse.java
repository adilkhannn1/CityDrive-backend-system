package kz.citydrive.admin.dto;

import java.util.List;

public class CitiesListResponse {

    private List<CityDto> data;

    public CitiesListResponse(List<CityDto> data) {
        this.data = data;
    }

    public List<CityDto> getData() {
        return data;
    }

    public void setData(List<CityDto> data) {
        this.data = data;
    }
}
