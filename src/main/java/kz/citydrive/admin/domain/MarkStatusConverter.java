package kz.citydrive.admin.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MarkStatusConverter implements AttributeConverter<MarkStatus, String> {

    @Override
    public String convertToDatabaseColumn(MarkStatus attribute) {
        return attribute == null ? MarkStatus.NEW.getValue() : attribute.getValue();
    }

    @Override
    public MarkStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? MarkStatus.NEW : MarkStatus.fromValue(dbData);
    }
}
