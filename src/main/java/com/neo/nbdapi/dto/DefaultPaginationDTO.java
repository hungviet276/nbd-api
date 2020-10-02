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
public class DefaultPaginationDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int draw;

    private long recordTotal;

    private long recordFiltered;

    private Object content;
}

