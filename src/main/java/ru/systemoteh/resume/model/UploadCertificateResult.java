package ru.systemoteh.resume.model;

import lombok.Getter;
import lombok.Setter;
import ru.systemoteh.resume.domain.Certificate;

import java.io.Serializable;

@Getter
@Setter
public class UploadCertificateResult extends UploadResult implements Serializable {

    private String certificateName;

    public UploadCertificateResult(String certificateName, String largeUrl, String smallUrl) {
        super(largeUrl, smallUrl);
        this.certificateName = certificateName;
    }

    public Certificate castToCertificate() {
        return new Certificate(null, certificateName, getLargeUrl(), getSmallUrl(), null);
    }
}

