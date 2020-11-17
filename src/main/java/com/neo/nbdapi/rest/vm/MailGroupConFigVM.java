package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailGroupConFigVM implements Serializable {
    private String id;
    private String code;
    private String name;
    private String description;
    private Integer status;
    private String user;
    private List<String> userInSites;
    private List<String> warningConfig;
    private List<String> userOutSite;
}
