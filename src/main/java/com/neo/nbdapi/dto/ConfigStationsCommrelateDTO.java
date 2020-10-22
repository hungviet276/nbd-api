package com.neo.nbdapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigStationsCommrelateDTO implements Serializable {
    private Long id;
    private Long configValueTypeId;
    private Long configValueTypeParent;
}
