/**
 *
 */
package com.neo.nbdapi.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Station {

    private Long id;

    private String stationId;

    private String stationCode;

    private String stationName;

    private String stationLongName;

    private Float elevation;

    private String image;

    private Float latitude;

    private Float longtitude;

    private Integer trans_miss;

    private String address;

    private Integer status;

    private Integer is_active;

    private Integer isDel;

    //	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date createdAt;

    private Date updatedAt;

    private Long areaId;

    private String areaName;

    private String siteName;

    private Long districtId;

    private Long provinceId;

    private Long riverId;

    private Long stationTypeId;

    private Long wardId;

    private Integer isActive;

    private Integer countryId;

    private Integer siteId;

    private String countryName;

    private String provinceName;

    private String districtName;

    private String wardName;

    private String riverName;

    private String unitName;

    private Integer projectId;

    private String projectName;

    private Integer objectTypeId;

    private String objectType;

    private String objectTypeName;

    private Integer isAuto;

    private Integer modeStationType;

    private String createById;

    private int curTsTypeId;
}
