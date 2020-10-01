package com.neo.nbdapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * class user_info
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {

    private String id;

    private String password;
}
