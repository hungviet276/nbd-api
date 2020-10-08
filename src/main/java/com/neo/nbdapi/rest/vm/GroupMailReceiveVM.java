package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMailReceiveVM {
    private String name;
    private String code;
    private String status;
    private String description;
}
