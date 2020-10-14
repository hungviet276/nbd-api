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

    private String password;

    private String name;

    private String mobile;

    private String position;

    private String email;

    private Long gender;

    private Long status_id;

    private Long check_roke;

    private Long card_number;

}
