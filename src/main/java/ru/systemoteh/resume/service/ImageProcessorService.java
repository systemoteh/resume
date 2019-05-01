package ru.systemoteh.resume.service;

import org.springframework.web.multipart.MultipartFile;
import ru.systemoteh.resume.model.CurrentProfile;
import ru.systemoteh.resume.model.UploadCertificateResult;
import ru.systemoteh.resume.model.UploadResult;

import javax.annotation.Nonnull;

public interface ImageProcessorService {

    @Nonnull
    UploadResult processNewProfilePhoto(@Nonnull MultipartFile uploadPhoto);

    @Nonnull
    UploadCertificateResult processNewCertificateImage(@Nonnull CurrentProfile currentProfile, @Nonnull MultipartFile uploadCertificate);

}
