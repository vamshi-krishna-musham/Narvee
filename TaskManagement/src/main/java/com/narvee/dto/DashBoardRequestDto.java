package com.narvee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DashBoardRequestDto {
  
	
	private String timeIntervel;
	private String fromDate;
	private String toDate;
	private Integer year;
	private Long userId;
	private Long pid;
	
	
}
