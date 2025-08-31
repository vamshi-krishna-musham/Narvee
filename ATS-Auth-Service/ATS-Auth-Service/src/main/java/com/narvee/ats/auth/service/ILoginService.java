package com.narvee.ats.auth.service;

import com.narvee.ats.auth.entity.Users;

public interface ILoginService {

	public Users findByEmail(String email);

	public Users findbyuserid(long userid);

	public void updatePassword(Users user, String newPsw);

	public void updateResetPasswordToken(String token, String email);

	public boolean isOTPValid(Long optid, String enteredOTP);

	public Users registerNewUser(Users user);
}
