package ru.systemoteh.resume.model;

import org.apache.commons.lang.WordUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.beans.PropertyEditorSupport;

public enum LanguageLevel {

    BEGINNER,

    ELEMENTARY,

    PRE_INTERMEDIATE,

    INTERMEDIATE,

    UPPER_INTERMEDIATE,

    ADVANCED,

    PROFICIENCY;

    public static PropertyEditorSupport getPropertyEditor() {
        return new PropertyEditorSupport() {
            @Override
            public void setAsText(String sliderIntValue) throws IllegalArgumentException {
                setValue(LanguageLevel.values()[Integer.parseInt(sliderIntValue)]);
            }
        };
    }

    public int getSliderIntValue() {
        return ordinal();
    }

    public String getDbValue() {
        return name();
    }

    public String getCaption() {
        String caption = WordUtils.capitalize(name()).replace("_", "-");
        return caption.substring(0, 1).concat(caption.substring(1, name().length()).toLowerCase());
    }

    @Converter
    public static class PersistJPAConverter implements AttributeConverter<LanguageLevel, String> {
        @Override
        public String convertToDatabaseColumn(LanguageLevel attribute) {
            return attribute.getDbValue();
        }

        @Override
        public LanguageLevel convertToEntityAttribute(String dbValue) {
            return LanguageLevel.valueOf(dbValue.toUpperCase());
        }
    }
}