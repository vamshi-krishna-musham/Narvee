package com.narvee.ats.auth.util;

import java.security.SecureRandom;

public class OTPGenerator {

	private static final String NUMBER = "0123456789";

	private static final String PASSWORD_ALLOW_BASE = NUMBER;

	private static SecureRandom random = new SecureRandom();

	public static String generateRandomPassword(int length) {
//		if (length == 4) {
//			throw new IllegalArgumentException("Otp length must be at least 4");
//		}
			

		StringBuilder password = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			int index = random.nextInt(PASSWORD_ALLOW_BASE.length());
			password.append(PASSWORD_ALLOW_BASE.charAt(index));
		}

		return password.toString();
	}

}
