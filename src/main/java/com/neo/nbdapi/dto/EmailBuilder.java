package com.neo.nbdapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EmailBuilder {
    public static final String TEMPLATE = "./src/main/resources/mail/" + "mail-template.html";
    private String subject;
    private String username;
    private String ip;
    private String port;
    private String mailTo;
    private String mailFrom;
    private String template;
    private String content;
    private String password;
    private String protocol;
    private String senderName;

}
