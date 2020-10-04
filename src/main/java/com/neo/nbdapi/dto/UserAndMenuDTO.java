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

	private static final long serialVersionUID = 1L;
	private String userId;
	private String password;

	private List<MenuDTO> menus;

	private List<ApiUrlDTO> urlApi;
}
