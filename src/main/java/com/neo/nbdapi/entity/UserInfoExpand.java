package com.neo.nbdapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoExpand {
    private Long id;
    private String name;
    private String mobile;
    private String code;
    private  String email;
    private Integer gender;
    private Integer status;
    private String cardNumber;
    private String position;
}
