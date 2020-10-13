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
public class District {
	private Integer districtId;
	private String districtCode;
	private String districtName;
	private Integer status;
	private Float districtLat;
	private Float districtLong;
	private Integer areaId;
	private Integer provinceId;
	private Integer createById;
	private Integer updatedById;
}
