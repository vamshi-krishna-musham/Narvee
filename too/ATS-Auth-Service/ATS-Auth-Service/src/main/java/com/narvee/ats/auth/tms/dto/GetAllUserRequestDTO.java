package com.narvee.ats.auth.tms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllUserRequestDTO {
	private int pageNumber;
	private int pageSize;
	private String sortField;
	private String sortOrder;
	private String keyword;
	private Long adminId;
	private Long profileId;
}
