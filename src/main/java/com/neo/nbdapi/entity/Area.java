/**
 * 
 */
package com.neo.nbdapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Area {
	private Integer areaId;
	private String areaCode;
	private String areaName;
	private Integer createById;
	private Integer updatedById;
}
