package kz.citydrive.admin.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class WorkReportCreateRequest {

    private String description;

    @JsonProperty("image_urls")
    private List<String> imageUrls;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
