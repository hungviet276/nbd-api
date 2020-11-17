package com.neo.nbdapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author thanglv on 11/16/2020
 * @project NBD
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserStations implements Serializable {
    private Long id;

    private String stationId;

    private String userId;

    private Date createdDate;

    private String createdBy;
}
