package com.narvee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TmsUsersInfo {


	private String FullName;
	private String email;
	private String contactNumber;
	private String addedBy;
	private String password;
	private String userRole;
}
