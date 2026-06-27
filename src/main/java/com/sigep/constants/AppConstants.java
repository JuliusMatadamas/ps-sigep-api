package com.sigep.constants;

import java.util.List;

public final class AppConstants {

	private AppConstants() {
		throw new AssertionError("Utility class");
	}

	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	public static final String BAD_REQUEST = "BAD REQUEST";

	private static final List<String> SQL_INJECTION_PATTERNS = List.of(
		";--", "'--", "/*", "*/", "DROP TABLE", "DELETE FROM",
		"INSERT INTO", "UPDATE", "EXEC", "UNION", "SELECT"
	);

	private static final List<String> XSS_PATTERNS = List.of(
		"<script", "javascript:", "onerror=", "onload=", "onclick=",
		"onmouseover=", "onfocus=", "onblur=", "eval(", "expression("
	);

	private static final List<String> CONTROL_CHARS = List.of(
		"\0", "\b", "\f", "\r"
	);

	public static List<String> getSqlInjectionPatterns() {
		return SQL_INJECTION_PATTERNS;
	}

	public static List<String> getXssPatterns() {
		return XSS_PATTERNS;
	}

	public static List<String> getControlChars() {
		return CONTROL_CHARS;
	}

}
