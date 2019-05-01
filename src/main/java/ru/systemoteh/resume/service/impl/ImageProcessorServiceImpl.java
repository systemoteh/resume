package ru.systemoteh.resume.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.systemoteh.resume.Constants;
import ru.systemoteh.resume.annotation.EnableUploadImageTempStorage;
import ru.systemoteh.resume.component.DataBuilder;
import ru.systemoteh.resume.component.ImageFormatConverter;
import ru.systemoteh.resume.component.ImageOptimizator;
import ru.systemoteh.resume.component.ImageResizer;
import ru.systemoteh.resume.component.impl.UploadCertificateLinkTempStorage;
import ru.systemoteh.resume.component.impl.UploadImageTempStorage;
import ru.systemoteh.resume.domain.Certificate;
import ru.systemoteh.resume.exception.CantCompleteClientRequestException;
import ru.systemoteh.resume.model.CurrentProfile;
import ru.systemoteh.resume.model.UploadCertificateResult;
import ru.systemoteh.resume.model.UploadResult;
import ru.systemoteh.resume.model.UploadTempPath;
import ru.systemoteh.resume.service.EditProfileService;
import ru.systemoteh.resume.service.ImageProcessorService;
import ru.systemoteh.resume.service.ImageStorageService;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.UUID;

import static ru.systemoteh.resume.Constants.UIImageType.AVATAR;
import static ru.systemoteh.resume.Constants.UIImageType.CERTIFICATE;

@Service
public class ImageProcessorServiceImpl implements ImageProcessorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageProcessorServiceImpl.class);

    @Autowired
    @Qualifier("pngToJpegImageFormatConverter")
    private ImageFormatConverter pngToJpegImageFormatConverter;

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private UploadImageTempStorage uploadImageTempStorage;

    @Autowired
    private ImageResizer imageResizer;

    @Autowired
    private ImageOptimizator imageOptimizator;

    @Autowired
    private UploadCertificateLinkTempStorage uploadCertificateLinkManager;

    @Autowired
    protected DataBuilder dataBuilder;

    @Autowired
    private EditProfileService editProfileService;


    @Nonnull
    @Override
    @EnableUploadImageTempStorage
    public UploadResult processNewProfilePhoto(@Nonnull MultipartFile uploadPhoto) {
        try {
            return processUpload(uploadPhoto, AVATAR);
        } catch (IOException e) {
            throw new CantCompleteClientRequestException("Can't save profile photo upload: " + e.getMessage(), e);
        }
    }

    protected UploadResult processUpload(MultipartFile multipartFile, Constants.UIImageType imageType) throws IOException {
        String largePhoto = generateNewFileName();
        String smallPhoto = getSmallImageName(largePhoto);
        UploadTempPath uploadTempPath = getCurrentUploadTempPath();
        transferUploadToFile(multipartFile, uploadTempPath.getLargeImagePath());
        resizeAndOptimizeUpload(uploadTempPath, imageType);
        String largePhotoLink = imageStorageService.createImageLink(largePhoto, imageType);
        imageStorageService.save(largePhotoLink, uploadTempPath.getLargeImagePath());
        String smallPhotoLink = imageStorageService.createImageLink(smallPhoto, imageType);
        imageStorageService.save(smallPhotoLink, uploadTempPath.getSmallImagePath());
        return new UploadResult(largePhotoLink, smallPhotoLink);
    }

    protected String generateNewFileName() {
        return UUID.randomUUID().toString() + ".jpg";
    }

    protected String getSmallImageName(String largePhoto) {
        return largePhoto.replace(".jpg", "-sm.jpg");
    }

    protected UploadTempPath getCurrentUploadTempPath(){
        return uploadImageTempStorage.getCurrentUploadTempPath();
    }

    protected void transferUploadToFile(MultipartFile uploadPhoto, Path destPath) throws IOException {
        String contentType = uploadPhoto.getContentType();
        LOGGER.debug("Content type for upload {}", contentType);
        uploadPhoto.transferTo(destPath.toFile());
        if (contentType.contains("png")) {
            pngToJpegImageFormatConverter.convert(destPath, destPath);
        } else if (!contentType.contains("jpg") && !contentType.contains("jpeg")) {
            throw new CantCompleteClientRequestException("Only png and jpg image formats are supported: Current content type=" + contentType);
        }
    }

    protected void resizeAndOptimizeUpload(UploadTempPath uploadTempPath, Constants.UIImageType imageType) throws IOException {
        imageResizer.resize(uploadTempPath.getLargeImagePath(), uploadTempPath.getSmallImagePath(), imageType.getSmallWidth(), imageType.getSmallHeight());
        imageOptimizator.optimize(uploadTempPath.getSmallImagePath());
        imageResizer.resize(uploadTempPath.getLargeImagePath(), uploadTempPath.getLargeImagePath(), imageType.getLargeWidth(), imageType.getLargeHeight());
        imageOptimizator.optimize(uploadTempPath.getLargeImagePath());
    }

    @Nonnull
    @Override
    @EnableUploadImageTempStorage
    public UploadCertificateResult processNewCertificateImage(@Nonnull CurrentProfile currentProfile, @Nonnull MultipartFile uploadCertificate) {
        try {
            UploadResult photoLinks = processUpload(uploadCertificate, CERTIFICATE);
            uploadCertificateLinkManager.addImageLinks(photoLinks.getLargeUrl(), photoLinks.getSmallUrl());
            String certificateName = dataBuilder.buildCertificateName(uploadCertificate.getOriginalFilename());
            UploadCertificateResult uploadCertificateResult = new UploadCertificateResult(certificateName, photoLinks.getLargeUrl(), photoLinks.getSmallUrl());
            Certificate certificate = uploadCertificateResult.castToCertificate();
            editProfileService.uploadCertificate(currentProfile, certificate);
            LOGGER.info("Certificate image {} uploaded", certificateName);
            return uploadCertificateResult;
        } catch (IOException e) {
            throw new CantCompleteClientRequestException("Can't save certificate image upload: " + e.getMessage(), e);
        }
    }
}
