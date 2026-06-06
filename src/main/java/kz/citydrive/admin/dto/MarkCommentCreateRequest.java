package kz.citydrive.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MarkCommentCreateRequest {

    @NotBlank(message = "text is required")
    @Size(min = 1, max = 1000, message = "text must be 1-1000 characters")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
