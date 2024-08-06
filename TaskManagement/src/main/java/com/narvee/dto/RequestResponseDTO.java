package com.narvee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestResponseDTO {
	
	private Integer pageNumber;
	private Integer pageSize;
	private String sortOrder;
	private String sortField;
	private String keyword;
 
}
