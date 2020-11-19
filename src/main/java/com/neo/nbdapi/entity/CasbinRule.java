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

    @Override
    public String toString() {
        return "CasbinRule : pType : " + pType + ",v0 : " + v0 + ",v1 : " + v1 + ",v2 : " + v2;
    }
}
