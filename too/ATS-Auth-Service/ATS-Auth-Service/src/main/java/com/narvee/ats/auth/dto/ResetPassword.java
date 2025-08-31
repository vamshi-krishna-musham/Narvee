package com.narvee.ats.auth.dto;

import lombok.Data;

@Data
public class ResetPassword {
	private long userid;
	private String email;
	private String password;
	private String renewpassword;
	private String newPassword;
	private String otp;
	private String username;
	private String link;
}
