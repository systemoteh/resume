package ru.systemoteh.resume.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {

    private String destinationAddress;
    private String destinationName;
    private String subject;
    private String content;

    public NotificationMessage(String subject, String content) {
        super();
        this.subject = subject;
        this.content = content;
    }

}
