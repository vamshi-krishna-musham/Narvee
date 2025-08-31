package com.narvee.ats.auth.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

public class IPUtils {

	private IPUtils() {
	}

	public static String getClientIP(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		// Handle multiple IPs
		if (ip != null && ip.contains(",")) {
			ip = ip.split(",")[0].trim();
		}

		// If it's localhost, convert to real LAN IP
		if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
			try {
				InetAddress localHost = InetAddress.getLocalHost();
				ip = localHost.getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
				ip = "UNKNOWN";
			}
		}
		return ip;
	}

	// This fetches your public IP using an external service
	public static String getNetworkIP() {
		try (Scanner s = new Scanner(new URL("https://api.ipify.org").openStream(), "UTF-8")) {
			return s.useDelimiter("\\A").next();
		} catch (IOException e) {
			return "UNKNOWN";
		}
	}

}
