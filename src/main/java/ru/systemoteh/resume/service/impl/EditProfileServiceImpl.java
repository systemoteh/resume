package ru.systemoteh.resume.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import ru.systemoteh.resume.Constants;
import ru.systemoteh.resume.annotation.ProfileDataFieldGroup;
import ru.systemoteh.resume.component.impl.UploadCertificateLinkTempStorage;
import ru.systemoteh.resume.domain.*;
import ru.systemoteh.resume.exception.CantCompleteClientRequestException;
import ru.systemoteh.resume.exception.FormValidationException;
import ru.systemoteh.resume.form.InfoForm;
import ru.systemoteh.resume.form.PasswordForm;
import ru.systemoteh.resume.form.SignUpForm;
import ru.systemoteh.resume.model.CurrentProfile;
import ru.systemoteh.resume.model.UploadResult;
import ru.systemoteh.resume.repository.search.ProfileSearchRepository;
import ru.systemoteh.resume.repository.storage.*;
import ru.systemoteh.resume.service.*;
import ru.systemoteh.resume.util.DataUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.util.*;

@Service
public class EditProfileServiceImpl extends AbstractCreateProfileService implements EditProfileService {

    private static Logger LOGGER = LoggerFactory.getLogger(EditProfileServiceImpl.class);

    @Autowired
    private ProfileSearchRepository profileSearchRepository;

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private ImageProcessorService imageProcessorService;

    @Autowired
    private SkillCategoryRepository skillCategoryRepository;

    @Autowired
    private PracticeRepository practiceRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private HobbyRepository hobbyRepository;

    @Autowired
    private UploadCertificateLinkTempStorage uploadCertificateLinkManager;

    @Autowired
    private StaticDataService staticDataService;

    @Autowired
    private NotificationManagerService notificationManagerService;

    private Map<Class<? extends ProfileCollectionField>, AbstractProfileEntityRepository<? extends ProfileCollectionField>> profileEntityRepositoryMap;

    private Set<String> profileCollectionsToReIndex;

    @Value("${profile.hobbies.max}")
    private int maxProfileHobbies;

    @PostConstruct
    private void postConstruct() {
        profileCollectionsToReIndex = Collections.unmodifiableSet(
                new HashSet<>(Arrays.asList(
                        "languages",
                        "skills",
                        "practices",
                        "certificates",
                        "courses")));

        Map<Class<? extends ProfileCollectionField>, AbstractProfileEntityRepository<? extends ProfileCollectionField>> map = new HashMap<>();
        map.put(Practice.class, practiceRepository);
        map.put(Skill.class, skillRepository);
        map.put(Certificate.class, certificateRepository);
        map.put(Course.class, courseRepository);
        map.put(Education.class, educationRepository);
        map.put(Language.class, languageRepository);
        map.put(Hobby.class, hobbyRepository);
        profileEntityRepositoryMap = Collections.unmodifiableMap(map);
    }

    protected Profile getProfile(CurrentProfile currentProfile) {
        return profileRepository.findOne(currentProfile.getId());
    }

    @Override
    public Profile findProfileById(CurrentProfile currentProfile) {
        return getProfile(currentProfile);
    }

    @Override
    @Transactional
    public void updateProfileData(@Nonnull CurrentProfile currentProfile, @Nonnull Profile profileForm, @Nonnull MultipartFile uploadPhoto) {
        Profile loadedProfile = profileRepository.findOne(currentProfile.getId());
        List<String> oldProfilePhotos = Collections.EMPTY_LIST;     // to delete old photos
        if (!uploadPhoto.isEmpty()) {
            UploadResult uploadResult = imageProcessorService.processNewProfilePhoto(uploadPhoto);
            deleteUploadedPhotosIfTransactionFailed(uploadResult);
            oldProfilePhotos = Arrays.asList(new String[]{loadedProfile.getLargePhoto(), loadedProfile.getSmallPhoto()});
            loadedProfile.updateProfilePhotos(uploadResult.getLargeUrl(), uploadResult.getSmallUrl());
        }
        int copiedFieldsCount = DataUtil.copyFields(profileForm, loadedProfile, ProfileDataFieldGroup.class);
        boolean shouldProfileBeUpdated = !uploadPhoto.isEmpty() || copiedFieldsCount > 0;
        if (shouldProfileBeUpdated) {
            executeUpdateProfileData(currentProfile, loadedProfile, oldProfilePhotos);
        }
    }

    protected void deleteUploadedPhotosIfTransactionFailed(final UploadResult uploadResult) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCompletion(int status) {
                if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                    imageStorageService.remove(uploadResult.getLargeUrl(), uploadResult.getSmallUrl());
                }
            }
        });
    }

    @Nonnull
    @Override
    public Contact findContacts(@Nonnull CurrentProfile currentProfile) {
        return getProfile(currentProfile).getContacts();
    }

    @Override
    @Transactional
    public void updateContacts(@Nonnull CurrentProfile currentProfile, @Nonnull Contact contactsForm) {
        Profile loadedProfile = profileRepository.findOne(currentProfile.getId());
        int copiedFieldsCount = DataUtil.copyFields(contactsForm, loadedProfile.getContacts());
        boolean shouldProfileBeUpdated = copiedFieldsCount > 0;
        if (shouldProfileBeUpdated) {
            profileRepository.save(loadedProfile);
        } else {
            LOGGER.debug("Profile contacts not updated");
        }
    }

    @Nonnull
    @Override
    public Collection<Skill> findSkills(@Nonnull CurrentProfile currentProfile) {
        return getProfile(currentProfile).getSkills();
    }

    @Nonnull
    @Override
    public List<SkillCategory> findSkillCategories() {
        return skillCategoryRepository.findAll(new Sort("id"));
    }

    @Override
    @Transactional
    public void updateSkills(@Nonnull CurrentProfile currentProfile, @Nonnull Collection<Skill> skills) {
        updateProfileEntities(currentProfile, skills, Skill.class);
    }

    @Nonnull
    @Override
    public Collection<Practice> findPractices(@Nonnull CurrentProfile currentProfile) {
        return getProfile(currentProfile).getPractices();
    }

    @Override
    @Transactional
    public void updatePractices(@Nonnull CurrentProfile currentProfile, @Nonnull Collection<Practice> practices) {
        updateProfileEntities(currentProfile, practices, Practice.class);
    }

    @Nullable
    @Override
    public Collection<Certificate> findCertificates(@Nonnull CurrentProfile currentProfile) {
        return getProfile(currentProfile).getCertificates();
    }

    @Override
    @Transactional
    public void uploadCertificate(@Nonnull CurrentProfile currentProfile, @Nonnull Certificate certFromForm) {
        Collection<Certificate> certListFromDB = findCertificates(currentProfile);
        certListFromDB.add(certFromForm);
        updateProfileEntities(currentProfile, certListFromDB, Certificate.class);
    }

    @Override
    @Transactional
    public void updateCertificates(@Nonnull CurrentProfile currentProfile, @Nonnull Collection<Certificate> certListFromForm) {
        Collection<Certificate> certListFromDB = findCertificates(currentProfile);
        List<String> certListImagesFromDB = DataUtil.getCertificateImageUrls(certListFromDB);
        // for remove certificates that are not on the form now
        Iterator<Certificate> certImageFromForm = certListFromForm.iterator();
        while (certImageFromForm.hasNext()) {
            Certificate certFromForm = certImageFromForm.next();
            if (certFromForm.getLargeUrl() == null || certFromForm.getSmallUrl() == null) { // if user press 'add' without file
                certImageFromForm.remove();
            } else {
                certListImagesFromDB.remove(certFromForm.getLargeUrl()); // for remove certificates that are not on the form now
                certListImagesFromDB.remove(certFromForm.getSmallUrl());
            }
        }
        clearResourcesIfTransactionSuccess(certListImagesFromDB);  // remove files that are not in the form now
        updateProfileEntities(currentProfile, certListFromForm, Certificate.class);
    }

    protected void clearResourcesIfTransactionSuccess(final List<String> certificateImages) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                uploadCertificateLinkManager.clearImageLinks();
                imageStorageService.remove(certificateImages.toArray(Constants.EMPTY_ARRAY));
            }
        });
    }

    @Nonnull
    @Override
    public Collection<Course> findCourses(@Nonnull CurrentProfile currentProfile) {
        return getProfile(currentProfile).getCourses();
    }

    @Override
    @Transactional
    public void updateCourses(@Nonnull CurrentProfile currentProfile, @Nonnull Collection<Course> courses) {
        updateProfileEntities(currentProfile, courses, Course.class);
    }

    @Nonnull
    @Override
    public Collection<Education> findEducations(@Nonnull CurrentProfile currentProfile) {
        return getProfile(currentProfile).getEducations();
    }

    @Override
    @Transactional
    public void updateEducations(@Nonnull CurrentProfile currentProfile, @Nonnull Collection<Education> educations) {
        updateProfileEntities(currentProfile, educations, Education.class);
    }

    @Nonnull
    @Override
    public Collection<Language> findLanguages(@Nonnull CurrentProfile currentProfile) {
        return getProfile(currentProfile).getLanguages();
    }

    @Override
    @Transactional
    public void updateLanguages(@Nonnull CurrentProfile currentProfile, @Nonnull Collection<Language> languages) {
        updateProfileEntities(currentProfile, languages, Language.class);
    }

    @Nonnull
    @Override
    public Collection<Hobby> findHobbiesWithProfileSelected(@Nonnull CurrentProfile currentProfile) {
        Collection<Hobby> profileHobbies = getProfile(currentProfile).getHobbies();
        Collection<Hobby> allHobbies = new ArrayList<>(staticDataService.findAllHobbies());
        Collection<Hobby> hobbies = new ArrayList<>();
        if (profileHobbies != null) {
            for (Hobby h : allHobbies) {
                boolean selected = profileHobbies.contains(h);
                hobbies.add(new Hobby(h.getName(), selected));
            }
            return hobbies;
        } else {
            return allHobbies;
        }
    }

    @Override
    @Transactional
    public void updateHobbies(@Nonnull CurrentProfile currentProfile, @Nonnull List<String> hobbies) {
        Collection<Hobby> updatedHobbies = staticDataService.createHobbyEntitiesByNames(hobbies);
        if (updatedHobbies.size() > maxProfileHobbies) {
            throw new CantCompleteClientRequestException("Detected more than " + maxProfileHobbies + " hobbies for profile: currentProfile=" + currentProfile + ", hobbies=" + updatedHobbies);
        }
        updateProfileEntities(currentProfile, updatedHobbies, Hobby.class);
    }

    @Override
    @Transactional
    public void updateInfo(@Nonnull CurrentProfile currentProfile, @Nonnull InfoForm form) {
        Profile loadedProfile = profileRepository.findOne(currentProfile.getId());
        if (!StringUtils.equals(loadedProfile.getInfo(), form.getInfo())) {
            loadedProfile.setInfo(form.getInfo());
            profileRepository.save(loadedProfile);
        } else {
            LOGGER.debug("Profile info not updated");
        }
    }

    @Override
    @Transactional
    public Profile updateProfilePassword(CurrentProfile currentProfile, PasswordForm form) {
        Profile profile = profileRepository.findOne(currentProfile.getId());
        profile.setPassword(passwordEncoder.encode(form.getPassword()));
        profileRepository.save(profile);
        sendPasswordChangedIfTransactionSuccess(profile);
        return profile;
    }

    protected void sendPasswordChangedIfTransactionSuccess(final Profile profile) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                notificationManagerService.sendPasswordChanged(profile);
            }
        });
    }

    @Override
    @Transactional
    public Profile createNewProfile(SignUpForm signUpForm) {
        Profile profile = createNewProfile(signUpForm.getFirstName(), signUpForm.getLastName(), signUpForm.getPassword());
        profileRepository.save(profile);
        showProfileCreatedLogInfoIfTransactionSuccess(profile);
        return profile;
    }

    @Override
    @Transactional
    public void removeProfile(CurrentProfile currentProfile) {
        Profile profile = profileRepository.findOne(currentProfile.getId());
        List<String> imageLinksToRemove = getImageLinksToRemove(profile);
        profileRepository.delete(profile);
        removeProfileIndexIfTransactionSuccess(profile, imageLinksToRemove);
    }

    protected List<String> getImageLinksToRemove(Profile profile) {
        List<String> imageLinksToRemove = new ArrayList<>();
        imageLinksToRemove.add(profile.getLargePhoto());
        imageLinksToRemove.add(profile.getSmallPhoto());
        if (profile.getCertificates() != null) {
            for (Certificate certificate : profile.getCertificates()) {
                imageLinksToRemove.add(certificate.getLargeUrl());
                imageLinksToRemove.add(certificate.getSmallUrl());
            }
        }
        return imageLinksToRemove;
    }

    protected void removeProfileIndexIfTransactionSuccess(final Profile profile, final List<String> imageLinksToRemove) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                LOGGER.info("Profile by id=" + profile.getId() + " removed from storage");
                imageStorageService.remove(imageLinksToRemove.toArray(new String[imageLinksToRemove.size()]));
                profileSearchRepository.delete(profile.getId());
                LOGGER.info("Profile by id=" + profile.getId() + " removed from search index");
            }
        });
    }


    /**
     * common methods for profile data fields (photo, birthday, country... etc.)
     */

    protected void executeUpdateProfileData(CurrentProfile currentProfile, Profile loadedProfile, List<String> oldProfilePhotos) {
        loadedProfile.setCompleted(true);
        synchronized (this) {
            checkForDuplicatesEmailAndPhone(loadedProfile);
            profileRepository.save(loadedProfile);
        }
        completeUpdateProfileDataIfTransactionSuccess(currentProfile, loadedProfile, oldProfilePhotos);
    }

    protected void checkForDuplicatesEmailAndPhone(Profile profileForm) {
        Profile ProfileByEmail = profileRepository.findByEmail(profileForm.getEmail());
        if (ProfileByEmail != null && !ProfileByEmail.getId().equals(profileForm.getId())) {
            throw new FormValidationException("email", profileForm.getEmail());
        }
        Profile profileForPhone = profileRepository.findByPhone(profileForm.getPhone());
        if (profileForPhone != null && !profileForPhone.getId().equals(profileForm.getId())) {
            throw new FormValidationException("phone", profileForm.getPhone());
        }
    }

    protected void completeUpdateProfileDataIfTransactionSuccess(final CurrentProfile currentProfile, final Profile profileForm, final List<String> oldProfilePhotos) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                LOGGER.info("Profile updated");
                imageStorageService.remove(oldProfilePhotos.toArray(Constants.EMPTY_ARRAY));    // to delete old photos
                updateIndexProfileData(currentProfile, profileForm, ProfileDataFieldGroup.class);
            }
        });
    }

    protected <T extends Annotation> void updateIndexProfileData(CurrentProfile currentProfile, Profile profileForm, Class<T> annotationClass) {
        Profile p = profileSearchRepository.findOne(currentProfile.getId());
        if (p == null) {
            createNewProfileIndex(profileForm);
        } else {
            DataUtil.copyFields(profileForm, p, annotationClass);
            if (StringUtils.isNotBlank(profileForm.getLargePhoto()) || StringUtils.isNotBlank(profileForm.getSmallPhoto())) {
                p.setLargePhoto(profileForm.getLargePhoto());
                p.setSmallPhoto(profileForm.getSmallPhoto());
            }
            profileSearchRepository.save(p);
            LOGGER.info("Profile index updated");
        }
    }

    protected void createNewProfileIndex(Profile profileForm) {
        if (profileForm.getCertificates() == null) {
            profileForm.setCertificates(Collections.EMPTY_LIST);
        }
        if (profileForm.getPractices() == null) {
            profileForm.setPractices(Collections.EMPTY_LIST);
        }
        if (profileForm.getLanguages() == null) {
            profileForm.setLanguages(Collections.EMPTY_LIST);
        }
        if (profileForm.getSkills() == null) {
            profileForm.setSkills(Collections.EMPTY_LIST);
        }
        profileSearchRepository.save(profileForm);
        LOGGER.info("New profile index created: {}", profileForm.getUid());
    }

    /**
     * common methods for collection fields (skills, practices, certificates... etc.)
     */

    protected <E extends ProfileCollectionField> void updateProfileEntities(CurrentProfile currentProfile, Collection<E> updatedData, Class<E> entityClass) {
        Profile profile = getProfile(currentProfile);
        AbstractProfileEntityRepository<E> repository = findProfileEntityRepository(entityClass);
        List<E> profileData = repository.findByProfileIdOrderByIdAsc(currentProfile.getId());
        DataUtil.removeEmptyElements(updatedData); // if the user has entered a space in the field
        if (Comparable.class.isAssignableFrom(entityClass)) {
            Collections.sort((List<? extends Comparable>) updatedData);
        }
        String collections = DataUtil.getCollectionName(entityClass);
        if (DataUtil.areListsEqual((List<? extends AbstractEntity>) updatedData, profileData)) {
            LOGGER.debug("Profile {}: nothing to update", collections);
            return;
        } else {
            executeUpdateProfileEntities(profile, repository, updatedData);
            LOGGER.info("Profile {} updated", collections);
            updateIndexProfileEntitiesIfTransactionSuccess(currentProfile, updatedData, collections);
        }
    }

    protected <E extends ProfileCollectionField> AbstractProfileEntityRepository<E> findProfileEntityRepository(Class<E> entityClass) {
        AbstractProfileEntityRepository<E> repository = (AbstractProfileEntityRepository<E>) profileEntityRepositoryMap.get(entityClass);
        if (repository == null) {
            throw new IllegalArgumentException("ProfileEntityRepository not found for entityClass=" + entityClass);
        }
        return repository;
    }

    protected <E extends ProfileCollectionField> void executeUpdateProfileEntities(Profile profile, AbstractProfileEntityRepository<E> repository, Collection<E> updatedData) {
        repository.deleteByProfileId(profile.getId());
        repository.flush();
        for (E entity : updatedData) {
            entity.setId(null);
            entity.setProfile(profile);
            repository.saveAndFlush(entity);
        }
    }

    protected <E extends ProfileCollectionField> void updateIndexProfileEntitiesIfTransactionSuccess(final CurrentProfile currentProfile, final Collection<E> updatedData, final String collections) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                if (profileCollectionsToReIndex.contains(collections)) {
                    updateIndexProfileEntities(currentProfile, updatedData, collections);
                }
            }
        });
    }

    protected <E> void updateIndexProfileEntities(CurrentProfile currentProfile, Collection<E> updatedData, String collections) {
        Profile profile = profileSearchRepository.findOne(currentProfile.getId());
        if (profile != null) {
            DataUtil.writeProperty(profile, collections, updatedData);
            profileSearchRepository.save(profile);
            LOGGER.info("Profile {} index updated", collections);
        }
    }

}
