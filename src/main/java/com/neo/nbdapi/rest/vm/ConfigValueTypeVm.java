package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigValueTypeVm implements Serializable {
    private Long station;
    private Long valueType;
    private Date startDate;
    private Date endDate;
}
