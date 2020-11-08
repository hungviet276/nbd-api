package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditMenuVM {

    @NotEmpty(message = "menu id không được trống")
    private String id;

    @NotEmpty(message = "Tên menu không được trống")
    @Size(max = 200, message = "Tên menu dài tối đa 200 ký tự")
    private String name;

    @NotEmpty(message = "Độ ưu tiên không được trống")
    @Size(max = 6, message = "Độ ưu tiên không vượt quá 999999")
    private String displayOrder;

    private String pictureFile;

    private String detailFile;

    private String parentId;

    @NotEmpty(message = "publish không được trống")
    private String publish;
}
