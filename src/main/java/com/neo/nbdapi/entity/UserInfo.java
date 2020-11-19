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

    private Integer gender;

    private String genders;

    private Integer statusId;

    private String statusIds;

    private Integer checkRole;

    private String dateRole;


    private String dateRoles;

    private int cardNumber;

    private String cardNumbers;

    private String code;

    private String officeCode;

    private Integer group_id;

    private Date createdDate;

    private String createdDates;

    private String createdBy;

    private String text;

    private Integer isDelete;
}
