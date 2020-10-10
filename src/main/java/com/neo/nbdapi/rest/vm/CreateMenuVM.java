package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMenuVM {

    /*
    * Ten menu
    */
    private String name;

    /**
     * do uu tien
     */
    @NotEmpty(message = "Độ ưu tiên không được trống")
    private String displayOrder;

    /**
     * icon
     */
    private String pictureFile;

    /**
     * url cua menu
     */
    private String detailFile;

    /**
     * menu cha
     */
    private String parentId;

    /**
     * trang thai
     */
    private String publish;
}
