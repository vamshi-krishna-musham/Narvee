package com.narvee.ats.auth.tms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TmsUsersDropDown {
	private Long userId;
	 private String userName;
}
