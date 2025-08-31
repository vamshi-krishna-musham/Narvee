package com.narvee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketTrackerSortDTO {

	private Integer pageNumber;
	private Integer pageSize;
	private String sortOrder;
	private String sortField;
	private Long taskid;
	private String keyword;
}
