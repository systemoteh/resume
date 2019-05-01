package ru.systemoteh.resume.component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface DataBuilder {

    @Nonnull
    String buildCertificateName(@Nullable String fileName);

    @Nonnull String buildProfileUid(@Nonnull String firstName, @Nonnull String lastName);

    @Nonnull String rebuildUidWithRandomSuffix(@Nonnull String baseUid, @Nonnull String alphabet, int letterCount);

    @Nonnull String buildRestoreAccessLink(@Nonnull String appHost, @Nonnull String token);
}
