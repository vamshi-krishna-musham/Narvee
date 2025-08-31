package com.narvee.ats.auth.service;

import com.narvee.ats.auth.dto.EmailVerificationDTO;
import com.narvee.ats.auth.dto.ResetPassword;

public interface EmailLoginService {
	
	public void sendAtsLoginOtp(EmailVerificationDTO emailVerificationDTO);

	public void sendTmsLoginOtp(EmailVerificationDTO emailVerificationDTO);
	
	public void sendAtsForgotPassword(ResetPassword ResetPassword );
	
	public void sendTmsForgotPassword(ResetPassword ResetPassword );
	
	public void sendAtsChangePasswordEmail(ResetPassword resetPassword);
	
	public void sendTmsChangePassword(ResetPassword resetPassword);
	
	public void ChangePasswordforTmsLogin(String Username);
}
