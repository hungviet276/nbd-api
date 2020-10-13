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
public class River {
	private Integer riverId;
	private String riverCode;
	private String riverName;
	private Integer status;
	private Integer createById;
	private Integer updatedById;
	
}
