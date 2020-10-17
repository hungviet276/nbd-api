package com.neo.nbdapi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
/*
Table menu mapping
 */
public class Menu {
    private long id;

    private String name;

    private int displayOrder;

    private String pictureFile;

    private String detailFile;

    private int menuLevel;

    private long parentId;

    private int publish;

    private int sysId;

    private String createdUser;

    private String modifiedUser;

    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private Date createdDate;

    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private Date modifiedDate;

    @JsonIgnore
    private int isLeaf;

    @JsonIgnore
    private String path;
}
