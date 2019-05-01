package ru.systemoteh.resume.util;

import org.apache.commons.lang.WordUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;
import ru.systemoteh.resume.domain.AbstractEntity;
import ru.systemoteh.resume.domain.Certificate;
import ru.systemoteh.resume.domain.ProfileCollectionField;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;

public class DataUtil {

    @SuppressWarnings("unchecked")
    public static <T> int compareByFields(Comparable<T> firstFieldValue, Comparable<T> secondFieldValue, boolean nullFirst) {
        if (firstFieldValue == null) {
            if (secondFieldValue == null) {
                return 0;
            } else {
                return nullFirst ? 1 : -1;
            }
        } else {
            if (secondFieldValue == null) {
                return nullFirst ? -1 : 1;
            } else {
                return firstFieldValue.compareTo((T) secondFieldValue);
            }
        }
    }

    public static <T extends Annotation> int copyFields(final Object from, final Object to, Class<T> annotation) {
        final CopiedFieldsCounter copiedFieldsCounter = new CopiedFieldsCounter();
        ReflectionUtils.doWithFields(to.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                ReflectionUtils.makeAccessible(field);
                copyAccessibleField(field, from, to, copiedFieldsCounter);
            }
        }, createFieldFilter(annotation));
        return copiedFieldsCounter.counter;
    }

    public static <T extends Annotation> int copyFields(final Object from, final Object to) {
        return copyFields(from, to, null);
    }

    private static void copyAccessibleField(Field field, Object from, Object to, CopiedFieldsCounter copiedFieldsCounter) throws IllegalAccessException {
        Object fromValue = field.get(from);
        Object toValue = field.get(to);
        if (fromValue == null) {
            if (toValue != null) {
                field.set(to, null);
                copiedFieldsCounter.counter++;
            }
        } else {
            if (!fromValue.equals(toValue)) {
                field.set(to, fromValue);
                copiedFieldsCounter.counter++;
            }
        }
    }

    private static <T extends Annotation> ReflectionUtils.FieldFilter createFieldFilter(Class<T> annotation) {
        if (annotation == null) {
            return ReflectionUtils.COPYABLE_FIELDS;
        } else {
            return new org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter(annotation);
        }
    }

    public static <T extends ProfileCollectionField> String getCollectionName(Class<T> clazz) {
        String className = clazz.getSimpleName().toLowerCase();
        if (className.endsWith("y")) {
            className = className.substring(0, className.length() - 1) + "ie";
        }
        return className + "s";
    }

    public static boolean areListsEqual(final List<? extends AbstractEntity> updatedData, final List<?> profileData) {
        if (updatedData.size() != profileData.size()) {
            return false;
        }
        for (int i = 0; i < updatedData.size(); i++) {
            if (!updatedData.get(i).equalsWithoutIdAndProfile(profileData.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static void removeEmptyElements(Collection<?> collection) {
        Iterator<?> it = collection.iterator();
        while (it.hasNext()) {
            Object element = it.next();
            if (element == null || isAllFieldsNull(element)) {
                it.remove();
            }
        }
    }

    private static boolean isAllFieldsNull(Object element) {
        Field[] fields = element.getClass().getDeclaredFields();
        for (Field field : fields) {
            ReflectionUtils.makeAccessible(field);
            if (!Modifier.isStatic(field.getModifiers()) && ReflectionUtils.getField(field, element) != null) {
                return false;
            }
        }
        return true;
    }

    public static List<String> getCertificateImageUrls(Collection<Certificate> certificates) {
        List<String> imageUrls = new ArrayList<>(certificates.size()*2);
        for(Certificate certificate : certificates) {
            imageUrls.add(certificate.getLargeUrl());
            imageUrls.add(certificate.getSmallUrl());
        }
        return imageUrls;
    }

    public static Object readProperty(Object obj, String propertyName) {
        try {
            return BeanUtils.getPropertyDescriptor(obj.getClass(), propertyName).getReadMethod().invoke(obj);
        } catch (IllegalAccessException | InvocationTargetException | RuntimeException e) {
            throw new IllegalArgumentException("Can't read property: '" + propertyName + "' from object:'"
                    + obj.getClass() + "': " + e.getMessage(), e);
        }
    }

    public static String capitalizeName(String name) {
        return WordUtils.capitalize(normalizeName(name));
    }

    public static String normalizeName(String name) {
        return name.trim().toLowerCase();
    }

    public static String generateRandomString(String alphabet, int letterCount) {
        Random r = new Random();
        StringBuilder uid = new StringBuilder();
        for (int i = 0; i < letterCount; i++) {
            uid.append(alphabet.charAt(r.nextInt(alphabet.length())));
        }
        return uid.toString();
    }

    public static void writeProperty(Object obj, String propertyName, Object value) {
        try {
            BeanUtils.getPropertyDescriptor(obj.getClass(), propertyName).getWriteMethod().invoke(obj, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Can't read property: '"+propertyName+"' from object:'"+obj.getClass()+"': "+e.getMessage(), e);
        }
    }


    private static final class CopiedFieldsCounter {
        private int counter;
    }
}
