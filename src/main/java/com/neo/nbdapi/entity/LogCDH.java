/**
 * 
 */
package com.neo.nbdapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogCDH {
	private Long id;
	private Long stationFileId;
	private String stationFileIdName;
	private Date startPushTime;
	private Date endPustTime;
	private Integer status;
	private String statusStr;
	private String description;
	private String valueTypeName;
	private String stationCode;
	private String stationName;
	private String createdUser;
	private String endPustTimeStr;

}
