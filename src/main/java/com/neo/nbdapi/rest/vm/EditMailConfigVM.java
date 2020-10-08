package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditMailConfigVM {

    @NotEmpty(message = "id không được trống")
    private String id;

    private String ip;

    private String port;

    private String username;

    private String password;

    private String domain;

    private String senderName;

    private String email;

    private String protocol;
}
