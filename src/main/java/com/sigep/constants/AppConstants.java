package com.sigep.constants;

import java.util.List;

public final class AppConstants {

	private AppConstants() {
		throw new AssertionError("Utility class");
	}

	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	public static final String BAD_REQUEST = "BAD REQUEST";

	public static final String MSG_OBTENIDOS = "Se obtuvieron ";
	public static final String MSG_RETORNADOS = "Se retornaron ";
	public static final String MSG_CONTINENTE_NO_ENCONTRADO_ID = "Continente no encontrado con id: ";
	public static final String MSG_CONTINENTE_NO_ENCONTRADO = "Continente no encontrado.";
	public static final String MSG_CONTINENTE_YA_REGISTRADO_PREFIX = "El continente '";
	public static final String LOG_VAR_ROWS_AFFECTED = "rowsAffected";

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
