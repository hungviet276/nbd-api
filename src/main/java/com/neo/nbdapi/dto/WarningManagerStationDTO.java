package com.neo.nbdapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarningManagerStationDTO implements Serializable {

    private Long id;

    @NotEmpty(message = "Không được để trống mã cảnh báo")
    @JsonProperty("codeWarning")
    @Size(min = 1, max = 50, message = "Độ dài code trong khoảng 1 đến 50")
    @Pattern(regexp = "^[A-Z,0-9]+$", message = "Mã cảnh báo phải thuộc bảng chữ cái tiếng anh hoặc là số, phải được viết hoa không có ký tự đặc biệt")
    private String code;

    @NotEmpty(message = "Không được để trống tên cảnh báo")
    @JsonProperty("nameWarning")
    @Size(min = 0, max = 100, message = "tên cảnh báo trong khoảng 1 đến 50")
    private String name;

    @JsonProperty("descriptionWarning")
    @Size(min = 0, max = 500, message = "tên cảnh báo trong khoảng 1 đến 50")
    private String description;

    @JsonProperty("contentWarning")
    private String content;

    @Size(min = 0, max = 100, message = "độ dài của màu sắc trong khoảng 0 đến 100")
    @JsonProperty("color")
    private String color;

    @Size(min = 0, max = 100, message = "độ dài của icon trong khoảng 0 đến 100")
    @JsonProperty("iconWarning")
    private String icon;

    @NotEmpty(message = "Không được để trống mã trạm")
    @JsonProperty("stationWarning")
    private String stationId;

    @NotEmpty(message = "Không được để trống loại cảnh báo")
    @JsonProperty("typeWarning")
    private String suffixesTable;

    @JsonProperty("createBy")
    private String createBy;

    private List<WarningManagerDetailDTO> dataWarning;

}
