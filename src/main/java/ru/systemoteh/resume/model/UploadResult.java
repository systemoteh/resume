package ru.systemoteh.resume.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class UploadResult extends AbstractModel implements Serializable {

    private String largeUrl;
    private String smallUrl;

}

