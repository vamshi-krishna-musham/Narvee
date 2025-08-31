package com.narvee.ats.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

	private String email;

	private String firstname;

	private String lastname;

	private String password;

	private String consultantflg = "presales";

	private String personalcontactnumber;

	private String currentlocation;

	private String position;

	private Long experience;
	
	private Long technology;
	
	private String skills;

	private Long addedby;

	private String consultantemail;
	
	private String contactnumber;
	
	private Long consultantid;
	
	private Long qualification;
	
	private Long visa;
	
	private Long userid;
	
	private Long conRegistrationId;

}
