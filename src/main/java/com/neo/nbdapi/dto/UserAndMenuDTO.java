package com.neo.nbdapi.dto;

import com.neo.nbdapi.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAndMenuDTO implements Serializable {
    private String userId;

    private List<MenuDTO> menus;

    private List<ApiUrlDTO> urlApi;
}
