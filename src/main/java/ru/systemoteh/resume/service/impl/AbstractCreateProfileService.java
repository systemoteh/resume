package ru.systemoteh.resume.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.systemoteh.resume.component.DataBuilder;
import ru.systemoteh.resume.domain.Profile;
import ru.systemoteh.resume.exception.CantCompleteClientRequestException;
import ru.systemoteh.resume.repository.storage.ProfileRepository;
import ru.systemoteh.resume.util.DataUtil;

public abstract class AbstractCreateProfileService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Value("${generate.uid.suffix.length}")
    private int generateUidSuffixLength;

    @Value("${generate.uid.digit}")
    private String generateUidDigit;

    @Value("${generate.uid.max.try.count}")
    private int maxTryCountToGenerateUid;

    @Autowired
    protected ProfileRepository profileRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected DataBuilder dataBuilder;


    protected Profile createNewProfile(String firstName, String lastName, String password) {
        Profile profile = new Profile();
        profile.setUid(buildProfileUid(firstName, lastName));
        profile.setFirstName(DataUtil.capitalizeName(firstName));
        profile.setLastName(DataUtil.capitalizeName(lastName));
        profile.setPassword(passwordEncoder.encode(password));
        profile.setCompleted(false);
        return profile;
    }

    protected String buildProfileUid(String firstName, String lastName) throws CantCompleteClientRequestException {
        String baseUid = dataBuilder.buildProfileUid(firstName, lastName);
        String uid = baseUid;
        //  if a new user with the same firstName and lastName as in the database, uid must be generated a new one
        for (int i = 0; profileRepository.countByUid(uid) > 0; i++) {
            uid = dataBuilder.rebuildUidWithRandomSuffix(baseUid, generateUidDigit, generateUidSuffixLength);
            if (i >= maxTryCountToGenerateUid) {
                throw new CantCompleteClientRequestException("Can't generate unique uid for profile: " + uid);
            }
        }
        return uid;
    }

    protected void showProfileCreatedLogInfoIfTransactionSuccess(final Profile profile) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                LOGGER.info("New profile created: {}", profile.getUid());
            }
        });
    }
}
