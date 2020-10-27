package com.neo.nbdapi.dto;

import com.neo.nbdapi.entity.UserGroupDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroupDTO {
    private long id;
    private String name;
    private long groupParent;
    private String groupParentName;
    private int groupLevel;
    private long stationId;
    private String stationsName;
    private String description;
    private int status;
    private String createBy;
    private Date createAt;
    private String modifyBy;
    private Date modifyAt;
    private List<UserGroupDetail> users;
}
