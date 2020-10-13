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
public class Unit {
	private Integer unitId;
	private String unitCode;
	private String unitName;
	private Integer status;
	private Integer createById;
	private Integer updatedById;
}
