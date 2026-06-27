package com.sigep.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.slf4j.MDC;

import com.sigep.dto.response.StructuredLog;

public class LogUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LogUtil() {
        throw new IllegalStateException("Clase de utilidad, no debe ser instanciada.");
    }

    public static void info(String message) {
        print("INFO", message, "SUCCESS", null);
    }

    public static void info(String message, Map<String, Object> additionalInfo) {
        print("INFO", message, "SUCCESS", additionalInfo);
    }

    public static void success(String message) {
        print("INFO", message, "SUCCESS", null);
    }

    public static void warn(String message) {
        print("WARN", message, "WARNING", null);
    }

    public static void warn(String message, Map<String, Object> additionalInfo) {
        print("WARN", message, "WARNING", additionalInfo);
    }

    public static void error(String message) {
        print("ERROR", message, "FAILURE", null);
    }

    public static void error(String message, Throwable throwable) {
        Map<String, Object> additionalInfo = Map.of(
                "exceptionClass", throwable.getClass().getName(),
                "exceptionMessage", throwable.getMessage()
        );
        print("ERROR", message, "FAILURE", additionalInfo);
    }

    public static void var(String varName, Object value) {
        Map<String, Object> additionalInfo = Map.of(varName, value);
        print("INFO", varName + " = " + value, "SUCCESS", additionalInfo);
    }

    private static void print(String level, String message, String status, Map<String, Object> additionalInfo) {
        String tracingId = MDC.get("tracingId");
        String sourceIP = MDC.get("sourceIP");

        StructuredLog.ApplicationMetadata appMeta = StructuredLog.ApplicationMetadata.builder()
                .name("ps-sigep-api")
                .version("1.0.0")
                .env("develop")
                .kind("api")
                .build();

        StructuredLog.MeasurementMetadata measurementMeta = StructuredLog.MeasurementMetadata.builder()
                .method("DEV_LOG")
                .elapsedTime("N/A")
                .build();

        StructuredLog logData = StructuredLog.builder()
                .level(level)
                .logType("DEV_LOG")
                .sourceIP(sourceIP != null ? sourceIP : "localhost")
                .status(status)
                .message(message)
                .logOrigin("INTERNAL")
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .tracingId(tracingId != null ? tracingId : "N/A")
                .hostname(getHostname())
                .eventType("DEVELOPMENT_LOG")
                .application(appMeta)
                .measurement(measurementMeta)
                .additionalInfo(additionalInfo)
                .build();

        System.out.println("[DEV-LOG] " + logData.toJson());
    }

    private static String getHostname() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (java.net.UnknownHostException e) {
            return "unknown-host";
        }
    }
}
