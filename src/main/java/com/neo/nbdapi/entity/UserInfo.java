package com.neo.nbdapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * class user_info
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/*
Table user_info mapping
 */
public class UserInfo {

    private String id;

    private String password;

    private String name;

    private String mobile;

    private String position;

    private String email;

    private int gender;

    private int statusId;

    private int checkRole;

    private int dateRole;

    private int cardNumber;

    private String code;

    private String officeCode;

    private Date createdDate;

    private String createdBy;
}
