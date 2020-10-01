package com.neo.nbdapi.dto;

import com.neo.nbdapi.entity.Menu;

import java.io.Serializable;
import java.util.List;

public class UserAndMenuDTO implements Serializable {
    private String userId;

    private List<Menu> list;
}
