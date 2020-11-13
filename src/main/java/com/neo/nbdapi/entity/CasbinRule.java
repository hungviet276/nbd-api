package com.neo.nbdapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CasbinRule {
    private String pType;
    private String v0;
    private String v1;
    private String v2;
    private String v3;
    private String v4;
    private String v5;
}
