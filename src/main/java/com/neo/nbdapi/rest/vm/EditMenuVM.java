package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
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

    private String name;

    private String displayOrder;

    private String pictureFile;

    private String detailFile;

    private String menuLevel;

    private String parentId;

    private String publish;
}
