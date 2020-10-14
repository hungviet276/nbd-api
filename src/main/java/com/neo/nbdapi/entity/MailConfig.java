package com.neo.nbdapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
/*
Table mail_config mapping
 */
public class MailConfig {
    private long id;

    private String ip;

    private String port;

    private String username;

    private String password;

    private String domain;

    private String senderName;

    private String emailAddress;

    private String protocol;
}
