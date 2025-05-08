package com.narvee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {
	
	private Integer pageNumber;
	private Integer pageSize;
	private String sortOrder;
	private String sortField;
	private String projectid;
	private String status;
	private String keyword;
	private String access;
	private Long userid;
	
	private String ticketId;
	
 
}
