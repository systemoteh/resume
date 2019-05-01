package ru.systemoteh.resume.annotation;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marker annotation which groups the fields from Profile table into field list,
 * which should be used for updating profile data. This marker is used for updating profile domain by reflection.
 * <p>
 * Please look at ru.systemoteh.resume.service.impl.EditProfileServiceImpl.updateProfileData() for details
 */
@Retention(RUNTIME)
public @interface ProfileDataFieldGroup {

}