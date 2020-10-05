package com.neo.nbdapi.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu {
	private int id;

	private String name;

	private int displayOrder;

	private String pictureFile;

	private String detailFile;

	private int menuLevel;

	private int parentId;

	private int publish;

	private int sysId;

	private String createdUser;

	private String modifiedUser;

	private Date createdDate;

	private Date modifiedDate;
}
