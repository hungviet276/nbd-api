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

    private String genders;

    private int statusId;

    private String statusIds;

    private int checkRole;

    private String dateRole;

    private String dateRoles;

    private int cardNumber;

    private String cardNumbers;

    private String code;

    private String officeCode;

    private int group_id;

    private Date createdDate;

    private String createdDates;

    private String createdBy;


}
