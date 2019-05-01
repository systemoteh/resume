package ru.systemoteh.resume.service;

import org.springframework.web.multipart.MultipartFile;
import ru.systemoteh.resume.domain.*;
import ru.systemoteh.resume.form.InfoForm;
import ru.systemoteh.resume.form.PasswordForm;
import ru.systemoteh.resume.form.SignUpForm;
import ru.systemoteh.resume.model.CurrentProfile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public interface EditProfileService {

    @Nonnull
    Profile findProfileById(@Nonnull CurrentProfile currentProfile);

    void updateProfileData(@Nonnull CurrentProfile currentProfile, @Nonnull Profile profileForm, @Nonnull MultipartFile uploadPhoto);

    @Nonnull
    Contact findContacts(@Nonnull CurrentProfile currentProfile);

    void updateContacts(@Nonnull CurrentProfile currentProfile, @Nonnull Contact contactsForm);

    @Nonnull
    Collection<Skill> findSkills(@Nonnull CurrentProfile currentProfile);

    @Nonnull
    List<SkillCategory> findSkillCategories();

    void updateSkills(@Nonnull CurrentProfile currentProfile, @Nonnull Collection<Skill> skills);

    @Nonnull
    Collection<Practice> findPractices(@Nonnull CurrentProfile currentProfile);

    void updatePractices(@Nonnull CurrentProfile currentProfile, @Nonnull Collection<Practice> practices);

    @Nullable
    Collection<Certificate> findCertificates(@Nonnull CurrentProfile currentProfile);

    void uploadCertificate(@Nonnull CurrentProfile currentProfile, @Nonnull Certificate certificate);

    void updateCertificates(@Nonnull CurrentProfile currentProfile, @Nonnull Collection<Certificate> certificates);

    @Nonnull
    Collection<Course> findCourses(@Nonnull CurrentProfile currentProfile);

    void updateCourses(@Nonnull CurrentProfile currentProfile, @Nonnull Collection<Course> courses);

    @Nonnull
    Collection<Education> findEducations(@Nonnull CurrentProfile currentProfile);

    void updateEducations(@Nonnull CurrentProfile currentProfile, @Nonnull Collection<Education> educations);

    @Nonnull
    Collection<Language> findLanguages(@Nonnull CurrentProfile currentProfile);

    void updateLanguages(@Nonnull CurrentProfile currentProfile, @Nonnull Collection<Language> languages);

    @Nonnull
    Collection<Hobby> findHobbiesWithProfileSelected(@Nonnull CurrentProfile currentProfile);

    void updateHobbies(@Nonnull CurrentProfile currentProfile, @Nonnull List<String> hobbies);

    void updateInfo(@Nonnull CurrentProfile currentProfile, @Nonnull InfoForm form);

    @Nonnull Profile updateProfilePassword(@Nonnull CurrentProfile currentProfile, @Nonnull PasswordForm form);

    @Nonnull Profile createNewProfile(@Nonnull SignUpForm signUpForm);

    void removeProfile(@Nonnull CurrentProfile currentProfile);
}
