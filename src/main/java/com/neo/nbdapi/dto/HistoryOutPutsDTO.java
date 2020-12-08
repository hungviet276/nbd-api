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
public class HistoryOutPutsDTO {
    private String stationTYpeName;
    private String stationName;
    private String paramerterName;
    private String valueOld;
    private String unitOld;
    private String valueNews;
    private String unitNews;
}
