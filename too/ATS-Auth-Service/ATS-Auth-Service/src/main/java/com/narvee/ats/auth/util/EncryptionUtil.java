package com.narvee.ats.auth.util;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {

	private static final String ALGORITHM = "AES";
	private static final String SECRET_KEY = "encryptionNarveePayload";

	private static SecretKeySpec generateSecretKeySpec(String key) throws Exception {
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		byte[] keyBytes = key.getBytes("UTF-8");
		keyBytes = sha.digest(keyBytes);
		keyBytes = Arrays.copyOf(keyBytes, 16);
		return new SecretKeySpec(keyBytes, ALGORITHM);
	}

	public static String encrypt(long data) throws Exception {
		SecretKeySpec secretKey = generateSecretKeySpec(SECRET_KEY);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedBytes = cipher.doFinal(Long.toString(data).getBytes());
		return Base64.getUrlEncoder().encodeToString(encryptedBytes); // URL-safe encoding
	}

	public static long decrypt(String encryptedData) throws Exception {
		SecretKeySpec secretKey = generateSecretKeySpec(SECRET_KEY);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedBytes = cipher.doFinal(Base64.getUrlDecoder().decode(encryptedData)); // URL-safe decoding
		return Long.parseLong(new String(decryptedBytes));
	}

	public static Long checkCidType(String cid) throws Exception {
		Long num = null;
		try {
			num = Long.parseLong(cid);
		} catch (NumberFormatException e) {
			num = decrypt(cid);
		}
		return num;
	}
}
