package kz.citydrive.admin.api;

import kz.citydrive.admin.dto.SupportInfoDto;
import kz.citydrive.admin.service.SupportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/support")
public class SupportApiController {

    private final SupportService supportService;

    public SupportApiController(SupportService supportService) {
        this.supportService = supportService;
    }

    @GetMapping
    public SupportInfoDto support() {
        return supportService.getSupportInfo();
    }
}
