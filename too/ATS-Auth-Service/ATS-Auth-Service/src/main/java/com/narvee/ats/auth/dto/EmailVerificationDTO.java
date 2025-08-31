package com.narvee.ats.auth.dto;

import lombok.Data;

@Data
public class EmailVerificationDTO {
	private String userName;
	private String email;
	private String otp;

}
