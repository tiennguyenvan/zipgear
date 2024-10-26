package com.example.demo.service;

public class Lib {
	public static String ValidationCode() {
		// we will use this for testing purposes
		return "123456";
	}

	public static String ValidationCode(int length) {
		// please develop this with a real implementation
		return "123456";
	}

	public static boolean isValidAddress(String address) {
		if (address == null || address.trim().isEmpty()) {
			return false;
		}

		String[] parts = address.trim().split(",");
		return parts.length >= 3 &&
				parts[0].contains(" ") && // Check if the first part has at least one space
				parts[1].trim().length() >= 3 && // City name >= 3 chars
				parts[2].trim().length() == 2; // Province name = 2 chars

	}
}
