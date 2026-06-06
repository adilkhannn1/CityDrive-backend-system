package kz.citydrive.admin.service;

import kz.citydrive.admin.domain.SupportSettings;
import kz.citydrive.admin.dto.SupportInfoDto;
import kz.citydrive.admin.dto.SupportUpdateRequest;
import kz.citydrive.admin.repository.SupportSettingsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SupportService {

    private static final long SETTINGS_ID = 1L;

    private final SupportSettingsRepository supportSettingsRepository;
    private final String defaultWhatsappPhone;
    private final String defaultWhatsappMessage;
    private final String defaultEmail;
    private final String defaultPhone;

    public SupportService(
            SupportSettingsRepository supportSettingsRepository,
            @Value("${app.support.whatsapp-phone:77001234567}") String defaultWhatsappPhone,
            @Value("${app.support.whatsapp-message:Здравствуйте! Нужна помощь по City Drive}")
                    String defaultWhatsappMessage,
            @Value("${app.support.email:support@citydrive.kz}") String defaultEmail,
            @Value("${app.support.phone:+77001234567}") String defaultPhone) {
        this.supportSettingsRepository = supportSettingsRepository;
        this.defaultWhatsappPhone = defaultWhatsappPhone;
        this.defaultWhatsappMessage = defaultWhatsappMessage;
        this.defaultEmail = defaultEmail;
        this.defaultPhone = defaultPhone;
    }

    public SupportInfoDto getSupportInfo() {
        return toDto(getOrCreateSettings());
    }

    public SupportUpdateRequest getFormData() {
        SupportSettings settings = getOrCreateSettings();
        SupportUpdateRequest form = new SupportUpdateRequest();
        form.setWhatsappPhone(settings.getWhatsappPhone());
        form.setWhatsappMessage(settings.getWhatsappMessage());
        form.setEmail(settings.getEmail());
        form.setPhone(settings.getPhone());
        return form;
    }

    @Transactional
    public SupportInfoDto updateSettings(SupportUpdateRequest request) {
        if (request.getWhatsappMessage() == null || request.getWhatsappMessage().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "whatsapp_message is required");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is required");
        }
        if (request.getPhone() == null || request.getPhone().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "phone is required");
        }

        String whatsappPhone = normalizeWhatsappPhone(request.getWhatsappPhone());
        if (whatsappPhone.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "whatsapp_phone is required");
        }

        SupportSettings settings = getOrCreateSettings();
        settings.setWhatsappPhone(whatsappPhone);
        settings.setWhatsappMessage(request.getWhatsappMessage().trim());
        settings.setEmail(request.getEmail().trim());
        settings.setPhone(request.getPhone().trim());
        return toDto(supportSettingsRepository.save(settings));
    }

    private SupportSettings getOrCreateSettings() {
        return supportSettingsRepository
                .findById(SETTINGS_ID)
                .orElseGet(() -> supportSettingsRepository.save(createDefaultSettings()));
    }

    private SupportSettings createDefaultSettings() {
        SupportSettings settings = new SupportSettings();
        settings.setId(SETTINGS_ID);
        settings.setWhatsappPhone(normalizeWhatsappPhone(defaultWhatsappPhone));
        settings.setWhatsappMessage(defaultWhatsappMessage);
        settings.setEmail(defaultEmail);
        settings.setPhone(defaultPhone);
        return settings;
    }

    private SupportInfoDto toDto(SupportSettings settings) {
        return new SupportInfoDto(
                settings.getWhatsappPhone(),
                settings.getWhatsappMessage(),
                settings.getEmail(),
                settings.getPhone());
    }

    private String normalizeWhatsappPhone(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replaceAll("\\D", "");
    }
}
