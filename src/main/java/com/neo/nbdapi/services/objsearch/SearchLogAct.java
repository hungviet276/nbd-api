package com.neo.nbdapi.services.objsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchLogAct {
    private String id;

    private String menuId;

    private String act;

    private String createdBy;

    private String fromDate;

    private String toDate;
}
