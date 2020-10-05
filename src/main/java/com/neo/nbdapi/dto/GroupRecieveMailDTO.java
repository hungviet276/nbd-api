package com.neo.nbdapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupRecieveMailDTO {
	
	private String name;
	
	private String description;
	
	private Long page;
	
	private Long maxPageItem;
	
	private String sortName;
	
	private String sortBy;
	
}
