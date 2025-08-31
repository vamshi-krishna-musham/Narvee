package com.narvee.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskReportsDTO {
	
	private Integer pageNumber;
	private Integer pageSize;
	private String sortOrder;
	private String sortField;
	private LocalDate fromDate;
	private LocalDate toDate;
	private String department;
	private String keyword;
	
	

}
