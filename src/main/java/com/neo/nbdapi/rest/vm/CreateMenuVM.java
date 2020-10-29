package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

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
    @NotEmpty(message = "Tên menu không được trống")
    @Size(max = 200, message = "Tên menu dài tối đa 200 ký tự")
    private String name;

    /**
     * do uu tien
     */
    @NotEmpty(message = "Độ ưu tiên không được trống")
    @Size(max = 6, message = "Độ ưu tiên không quá 999999")
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
    @NotEmpty(message = "publish không được trống")
    private String publish;
}
