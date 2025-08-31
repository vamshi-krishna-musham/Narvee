
package com.narvee.ats.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortingRequestDTO {
	
	private Integer pageNumber;
	private Integer pageSize;
	private String sortField;
	private String sortOrder;
	private String keyword;
	private String status;

}

