package com.neo.nbdapi.services.objsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchMenu {

    @JsonProperty("s_id")
    private String id;

    @JsonProperty("s_name")
    private String name;

    @JsonProperty("s_display_order")
    private String displayOrder;

    @JsonProperty("s_picture_file")
    private String pictureFile;

    @JsonProperty("s_detail_file")
    private String detailFile;

    @JsonProperty("s_menu_level")
    private String menuLevel;

    @JsonProperty("s_parent_id")
    private String parentId;

    @JsonProperty("s_publish")
    private String publish;

    @JsonProperty("s_sys_id")
    private String sysId;

    @JsonProperty("s_created_user")
    private String createdUser;

    @JsonProperty("s_modified_user")
    private String modifiedUser;
}
