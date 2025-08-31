package com.narvee.ats.auth.tms.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthResponse {
	 private String token;
	 private String email;
	 private Long userId;
	 private String firstName;
	 private String middleName;
	 private String lastName;
	 private String organizationName;
	 private String userRole;
	 private Long adminId;
	 private byte[] profilePic;
	 private Long companySize;
	 private String companyDomain;
	 private String industry;
		private String contactNumber;
		private String position;
	 
	 
 	 
	 private List<String> rolePrivileges;
}
