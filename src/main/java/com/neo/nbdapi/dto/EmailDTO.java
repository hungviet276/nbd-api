package com.neo.nbdapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class EmailDTO {

    private String mailFrom;

    private String mailTo;

    private String mailCc;

    private String mailBcc;

    private String mailSubject;

    private String mailContent;

    private String templateName;

    private String contentType;

    public EmailDTO() {
        this.contentType = "text/html";
    }

    @Override
    public String toString() {
        return "Email [mailFrom=" + mailFrom + ", mailTo=" + mailTo + ", mailCc=" + mailCc + ", mailBcc=" + mailBcc
                + ", mailSubject=" + mailSubject + ", mailContent=" + mailContent + ", templateName=" + templateName
                + ", contentType=" + contentType + "]";
    }
}
