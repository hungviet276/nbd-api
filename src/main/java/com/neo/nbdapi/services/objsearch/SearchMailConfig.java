package com.neo.nbdapi.services.objsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchMailConfig implements Serializable {

    @JsonProperty("s_id")
    String id;

    @JsonProperty("s_ip")
    String ip;

    @JsonProperty("s_port")
    String port;

    @JsonProperty("s_username")
    String username;

    @JsonProperty("s_password")
    String password;

    @JsonProperty("s_domain")
    String domain;

    @JsonProperty("s_sendername")
    String senderName;

    @JsonProperty("s_email")
    String email;

    @JsonProperty("s_protocol")
    String protocol;
}
