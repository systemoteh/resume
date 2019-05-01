package ru.systemoteh.resume.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.systemoteh.resume.Constants;
import ru.systemoteh.resume.exception.CantCompleteClientRequestException;
import ru.systemoteh.resume.service.ImageStorageService;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileImageStorageService implements ImageStorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileImageStorageService.class);

    @Value("${media.storage.root.path}")
    protected String root;


    @Nonnull
    @Override
    public String createImageLink(@Nonnull String imageName, @Nonnull Constants.UIImageType imageType) {
        return "/media/" + imageType.getFolderName() + "/" + imageName;
    }

    @Override
    public void save(@Nonnull String imageLink, @Nonnull Path tempImageFile) {
        try {
            saveImageFile(tempImageFile, getDestinationImageFile(imageLink));
        } catch (IOException e) {
            throw new CantCompleteClientRequestException("Can't save image: " + e.getMessage(), e);
        }
    }

    protected void saveImageFile(Path srcImageFile, Path destinationImageFile) throws IOException {
        Files.move(srcImageFile, destinationImageFile);
    }

    protected Path getDestinationImageFile(String imageLink) {
        return Paths.get(root + imageLink);
    }

    @Override
    public void remove(String... imageLinks) {
        for (String imageLink : imageLinks) {
            if (StringUtils.isNotBlank(imageLink)) {
                removeImageFile(getDestinationImageFile(imageLink));
            }
        }
    }

    protected void removeImageFile(Path path) {
        try {
            if (Files.exists(path)) {
                Files.delete(path);
                LOGGER.debug("Image file {} removed successful", path);
            }
        } catch (IOException e) {
            LOGGER.error("Can't remove file: " + path, e);
        }
    }

}
