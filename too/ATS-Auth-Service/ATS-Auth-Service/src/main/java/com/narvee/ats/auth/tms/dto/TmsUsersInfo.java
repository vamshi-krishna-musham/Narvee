package com.narvee.ats.auth.tms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TmsUsersInfo {
    private Long userId;
	private String firstName;
	private String middleName;
	private String lastName;
	private String organisationName;
	private String organisationEmail;
	private String email;
	private String contactNumber;
	private Long adminId;
	private String password;
	// private String userRole;
	private Boolean isSuperAdmin;
	private String position;
	private Long addedBy;
	private Long updatedBy;
	private Long roleId;
	private String companyDomain;
	private Long companySize;
	private String industry;
	private byte[] profilepic;
	
	
}
