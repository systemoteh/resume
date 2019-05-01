package ru.systemoteh.resume.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

public abstract class AbstractEntity<T> implements Serializable {

    private static Logger LOGGER = LoggerFactory.getLogger(AbstractEntity.class);

    public abstract T getId();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractEntity<T> other = (AbstractEntity<T>) obj;
        if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

    public boolean equalsWithoutIdAndProfile(Object obj) {
        /* this = updatedData, obj = profileData */
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Field[] entityFields = getClass().getDeclaredFields();
        for (int i = 0; i < entityFields.length; i++) {
            Field field = ReflectionUtils.findField(getClass(), entityFields[i].getName());
            ReflectionUtils.makeAccessible(field);
            try {
                if (!entityFields[i].getName().equals("id") && !entityFields[i].getName().equals("profile")) {
                    if (field.get(obj) != null) {
                        if (!field.get(obj).equals(field.get(this))) {
                            LOGGER.debug(String.format("field [%s]: [updatedData = %s] != [profileData = %s]",
                                    entityFields[i].getName(), field.get(this), field.get(obj)));
                            return false;
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return String.format("%s[id=%s]", getClass().getSimpleName(), getId());
    }
}
