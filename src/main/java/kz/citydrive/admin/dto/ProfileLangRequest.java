package kz.citydrive.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ProfileLangRequest {

    @NotBlank
    @Pattern(regexp = "kk|ru|en")
    private String lang;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
