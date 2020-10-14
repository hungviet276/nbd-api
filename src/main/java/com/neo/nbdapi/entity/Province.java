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
public class Province {
	private Integer provinceId;
	private String provinceCode;
	private String provinceName;
	private Integer status;
	private Float provinceLat;
	private Float provinceLong;
	private Integer areaId;
	private Integer createById;
	private Integer updatedById;
}
