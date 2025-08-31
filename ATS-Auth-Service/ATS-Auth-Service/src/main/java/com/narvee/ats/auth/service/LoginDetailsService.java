package com.narvee.ats.auth.service;

import com.narvee.ats.auth.dto.RegistrationRequest;
import com.narvee.ats.auth.entity.OTP;

public interface LoginDetailsService {

	public OTP emailVerification(String email);

	public boolean validateOtp(String otp, Long id);

	public Long registerConsultant(RegistrationRequest request);


}
