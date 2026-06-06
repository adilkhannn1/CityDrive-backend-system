package kz.citydrive.admin.dto;

import kz.citydrive.admin.domain.Company;
import kz.citydrive.admin.domain.User;

public class CompanyAdminView {

    private final Company company;
    private final User user;
    private final String cityName;

    public CompanyAdminView(Company company, User user, String cityName) {
        this.company = company;
        this.user = user;
        this.cityName = cityName;
    }

    public Company getCompany() {
        return company;
    }

    public User getUser() {
        return user;
    }

    public String getCityName() {
        return cityName;
    }
}
