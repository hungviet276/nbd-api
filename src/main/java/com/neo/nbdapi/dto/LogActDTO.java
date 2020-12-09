package com.neo.nbdapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author thanglv on 10/12/2020
 * @project NBD
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogActDTO {
    private long id;

    private String menuName;

    private String act;

    private String createdBy;

    private String createdAt;
}
