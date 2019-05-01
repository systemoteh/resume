package ru.systemoteh.resume.model;

import org.apache.commons.lang.WordUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.beans.PropertyEditorSupport;

public enum LanguageType {

    ALL,

    SPOKEN,

    WRITING;

    public static PropertyEditorSupport getPropertyEditor() {
        return new PropertyEditorSupport() {
            @Override
            public void setAsText(String dbValue) throws IllegalArgumentException {
                setValue(LanguageType.valueOf(dbValue.toUpperCase()));
            }
        };
    }

    public String getCaption() {
        return WordUtils.capitalize(name());
    }

    public String getDbValue() {
        return name();
    }

    public LanguageType getReverseType() {
        if (this == SPOKEN) {
            return WRITING;
        } else if (this == WRITING) {
            return SPOKEN;
        } else {
            throw new IllegalArgumentException(this + " does not have reverse type");
        }
    }

    @Converter
    public static class PersistJPAConverter implements AttributeConverter<LanguageType, String> {
        @Override
        public String convertToDatabaseColumn(LanguageType attribute) {
            return attribute.getDbValue();
        }

        @Override
        public LanguageType convertToEntityAttribute(String dbValue) {
            return LanguageType.valueOf(dbValue.toUpperCase());
        }
    }
}