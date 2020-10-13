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
public class Parameter {
	private Integer stationParamterId;
	private Integer paramterTypeId;
	private String parameterName;
	private Integer stationId;
	private Integer timeFrequency;
	private String uuid;
	private String unitName;
	private String note;
}