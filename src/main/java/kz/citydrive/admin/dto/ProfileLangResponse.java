package kz.citydrive.admin.dto;

public class ProfileLangResponse {

    private String lang;

    public ProfileLangResponse(String lang) {
        this.lang = lang;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
