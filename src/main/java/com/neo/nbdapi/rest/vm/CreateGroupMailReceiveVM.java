package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGroupMailReceiveVM {
    @NotNull(message = "Tên không được để trống")
    private String name;

    @NotNull(message = "Code không được để trống")
    private String code;

    @NotNull(message = "status không được để trống")
    private String status;

    private String description;
}
