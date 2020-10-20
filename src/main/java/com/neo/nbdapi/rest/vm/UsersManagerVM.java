package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersManagerVM {
    private String id;

    private String id_dub;

    private String password;

    private String name;

    private String mobile;

    private String position;

    private String email;

    private int gender;

    private int status_id;

    private int check_roke;

    private String card_number;

    private String code;

    private int group_user_id;

    private String check_download_time;

    private String thread_id;

    private String user_login;

    private String check_edit_pass;
}
