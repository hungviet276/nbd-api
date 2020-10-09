package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMailConfigVM {

    /**
     * ip cua mail
     */
    private String ip;

    /**
     * port mail
     */
    private String port;

    /**
     * ten dang nhap cua mail
     */
    private String username;

    /**
     * mat khau cua mail
     */
    private String password;

    /**
     * domain cua mail
     */
    private String domain;

    /**
     * nguoi gui
     */
    private String senderName;

    /**
     * dia chi email
     */
    private String emailAddress;

    /**
     * giao thuc
     */
    private String protocol;
}
