package com.narvee.ats.auth.tms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthRequest {
	private String email;
	private String password;
	private String loginType;
	private String otp;
    private Long otpId;
}
