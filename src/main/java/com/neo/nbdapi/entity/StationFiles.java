/**
 * 
 */
package com.neo.nbdapi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationFiles {
	private Long stationFileId;
	private String stationFileCode;
	private String fileName;
	private String filePath;
	private Float fileSize;
	private Date fileDate;
	private Integer status;
	private Integer isDel;
	private Date createdAt;
	private Date updateAt;
	private String createdUser;
	private String updateUser;
	private Long stationId;
	private Long valueTypeId;
	private String station_file_content;

}
