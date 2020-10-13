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
public class Ward {
	private Integer wardId;
	private String wardCode;
	private String wardName;
	private Integer status;
	private Float wardLat;
	private Float wardLong;
	private Integer districtId;
	private Integer provinceId;
	private Integer createById;
	private Integer updatedById;
}
