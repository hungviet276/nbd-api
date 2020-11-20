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
public class NoficationHistory {
	private Long warningManageStationsId;
	private Long stationId;
	private String stationNo;
	private String stationName;
	private String warningNo;
	private String warningName;
	private String description;
	private String pushTimestampStr;
	private String stationCode;
	private String status;
	private String groupReMailName;
}
