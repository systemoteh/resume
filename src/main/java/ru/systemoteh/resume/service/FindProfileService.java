package ru.systemoteh.resume.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.systemoteh.resume.domain.Profile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface FindProfileService {

    @Nonnull
    Page<Profile> findAll(@Nonnull Pageable pageable);

    @Nullable
    Profile findByUid(@Nonnull String uid);

    void restoreAccess(@Nonnull String anyUniqueId);

    @Nullable Profile findByRestoreToken(@Nonnull String token);

    @Nonnull Page<Profile> findByFullContextSearchQuery(@Nonnull String query, @Nonnull Pageable pageable);

    @Nonnull Page<Profile> findByStrictSearchQuery(@Nonnull String query, @Nonnull Pageable pageable);

    @Nonnull Iterable<Profile> findAllForIndexing();

}
