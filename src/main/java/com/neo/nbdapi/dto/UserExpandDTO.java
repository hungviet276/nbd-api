package com.neo.nbdapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserExpandDTO implements Serializable {

    private Long id;

    private String nameOutSite;

    private String phoneOutSite;

    private String codeUserOutSite;

    private String emailOutSite;

    private String sexOutSite;

    private Integer statusOutSite;

    private String idOutSite;

    private String positionOutSite;
}
