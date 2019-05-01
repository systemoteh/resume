package ru.systemoteh.resume.service.impl;

import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.systemoteh.resume.component.DataBuilder;
import ru.systemoteh.resume.domain.Profile;
import ru.systemoteh.resume.domain.ProfileRestore;
import ru.systemoteh.resume.exception.CantCompleteClientRequestException;
import ru.systemoteh.resume.repository.search.ProfileSearchRepository;
import ru.systemoteh.resume.repository.storage.ProfileRepository;
import ru.systemoteh.resume.repository.storage.ProfileRestoreRepository;
import ru.systemoteh.resume.service.FindProfileService;
import ru.systemoteh.resume.service.NotificationManagerService;
import ru.systemoteh.resume.util.SecurityUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Service
public class FindProfileServiceImpl implements FindProfileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindProfileServiceImpl.class);
    @Autowired
    protected DataBuilder dataBuilder;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    private ProfileSearchRepository profileSearchRepository;
    @Autowired
    private ProfileRestoreRepository profileRestoreRepository;
    @Autowired
    private NotificationManagerService notificationManagerService;
    @Value("${application.host}")
    private String appHost;

    @Nonnull
    @Override
    public Page<Profile> findAll(@Nonnull Pageable pageable) {
        return profileRepository.findAllByCompletedTrue(pageable);
    }

    @Override
    public Profile findByUid(String uid) {
        return profileRepository.findByUid(uid.toLowerCase());
    }

    @Override
    @Transactional
    public void restoreAccess(String anyUniqueId) {
        Profile profile = profileRepository.findByUidOrEmailOrPhone(anyUniqueId, anyUniqueId, anyUniqueId);
        if (profile != null) {
            ProfileRestore restore = profileRestoreRepository.findOne(profile.getId());
            if (restore == null) {
                restore = new ProfileRestore();
                restore.setId(profile.getId());
            }
            restore.setToken(SecurityUtil.generateNewRestoreAccessToken());
            profileRestoreRepository.save(restore);
            sentRestoreLinkNotificationIfTransactionSuccess(profile, restore);
        } else {
            LOGGER.error("Profile not found by anyIdAccount:" + anyUniqueId);
        }
    }


    @Nullable
    @Override
    @Transactional
    public Profile findByRestoreToken(@Nonnull String token) {
        ProfileRestore restore = profileRestoreRepository.findByToken(token);
        if (restore == null) {
            throw new CantCompleteClientRequestException("Invalid token");
        }
        profileRestoreRepository.delete(restore);
        return restore.getProfile();
    }

    protected void sentRestoreLinkNotificationIfTransactionSuccess(final Profile profile, ProfileRestore restore) {
        final String restoreLink = dataBuilder.buildRestoreAccessLink(appHost, restore.getToken());
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                notificationManagerService.sendRestoreAccessLink(profile, restoreLink);
            }
        });
    }

    @Override
    public Page<Profile> findByFullContextSearchQuery(String query, Pageable pageable) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(query)
                        .field("objective")
                        .field("summary")
                        .field("info")
                        .field("certificates.name")
                        .field("languages.name")
                        .field("practices.company")
                        .field("practices.position")
                        .field("practices.responsibilities")
                        .field("skills.name")
                        .field("courses.name")
                        .field("courses.school")
                        .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                        .fuzziness(Fuzziness.ONE)
                        .operator(MatchQueryBuilder.Operator.AND))
                .withSort(SortBuilders.fieldSort("uid").order(SortOrder.DESC))
                .build();
        searchQuery.setPageable(pageable);
        return profileSearchRepository.search(searchQuery);
    }

    @Nonnull
    @Override
    public Page<Profile> findByStrictSearchQuery(@Nonnull String query, @Nonnull Pageable pageable) {
        return profileSearchRepository.findByObjectiveLikeOrSummaryLikeOrInfoLikeOrCertificatesNameLikeOrLanguagesNameLikeOrPracticesCompanyLikeOrPracticesPositionLikeOrPracticesResponsibilitiesLikeOrSkillsNameLikeOrCoursesNameLikeOrCoursesSchoolLike(
                query, query, query, query, query, query, query, query, query, query, query, pageable
        );
    }

    @Override
    @Transactional
    public Iterable<Profile> findAllForIndexing() {
        Iterable<Profile> allProfiles = profileRepository.findAll();
        // Load lazy collections
        for (Profile profile : allProfiles) {
            Hibernate.initialize(profile.getSkills());
            Hibernate.initialize(profile.getCertificates());
            Hibernate.initialize(profile.getLanguages());
            Hibernate.initialize(profile.getPractices());
            Hibernate.initialize(profile.getCourses());
        }
        return allProfiles;
    }
}
