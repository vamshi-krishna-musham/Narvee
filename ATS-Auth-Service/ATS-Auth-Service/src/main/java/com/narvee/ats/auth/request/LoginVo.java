package com.narvee.ats.auth.request;

import lombok.Data;

@Data
public class LoginVo {
	private String email;
	private String password;
	private String loginAs;
	private Long otpId;
	private String otp;
}
