package com.neo.nbdapi.rest.vm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SelectWarningManagerStrVM implements Serializable {

    private String term;


    @JsonProperty("_type")
    private String type;

    @NotEmpty(message = "Thiếu id trạm")
    private String id;
}
