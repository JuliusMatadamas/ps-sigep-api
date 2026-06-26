package com.sigep.exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sigep.dto.response.ApiResponseDTO;
import com.sigep.dto.response.MetaResponseDTO;
import com.sigep.dto.response.StructuredLog;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class AppExceptionsHandler {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String hostname;

    @Value("${log.application.name:ps-sigep-api}")
    private String appName;

    @Value("${log.application.version:1.0.0}")
    private String appVersion;

    @Value("${log.application.env:develop}")
    private String appEnv;

    @Value("${log.application.kind:api}")
    private String appKind;

    public AppExceptionsHandler() {
        String host;
        try {
            host = java.net.InetAddress.getLocalHost().getHostName();
        } catch (java.net.UnknownHostException e) {
            host = "unknown-host";
        }
        this.hostname = host;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO> handleAllExceptions(
            Exception ex,
            HttpServletRequest request
    ) {
        String tracingId = MDC.get("tracingId");
        String sourceIP = MDC.get("sourceIP");
        String failureLocation = getFailureLocation(ex);

        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("exceptionClass", ex.getClass().getName());
        additionalInfo.put("failureLocation", failureLocation);

        StructuredLog.ApplicationMetadata appMeta = StructuredLog.ApplicationMetadata.builder()
                .name(appName)
                .version(appVersion)
                .env(appEnv)
                .kind(appKind)
                .build();

        StructuredLog.MeasurementMetadata measurementMeta = StructuredLog.MeasurementMetadata.builder()
                .method(request.getMethod() + " " + request.getRequestURI())
                .elapsedTime("N/A")
                .build();

        StructuredLog logData = StructuredLog.builder()
                .level("ERROR")
                .logType("EXCEPTION")
                .sourceIP(sourceIP)
                .status("FAILURE")
                .message(ex.getMessage() != null ? ex.getMessage() : "Unexpected system error")
                .logOrigin("INTERNAL")
                .timestamp(LocalDateTime.now().format(DATE_FORMATTER))
                .tracingId(tracingId)
                .hostname(hostname)
                .eventType("SYSTEM_EXCEPTION")
                .application(appMeta)
                .measurement(measurementMeta)
                .additionalInfo(additionalInfo)
                .build();

        log.error(logData.toJson(), ex);

        MetaResponseDTO meta = MetaResponseDTO.builder()
                .status("FAILURE")
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Ha ocurrido un error interno en el servidor.")
                .devMessage(ex.getMessage())
                .errorDetails(Map.of("location", failureLocation))
                .build();

        ApiResponseDTO response = ApiResponseDTO.builder()
                .meta(meta)
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO> handleValidationExceptions(
            org.springframework.web.bind.MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String tracingId = MDC.get("tracingId");
        String sourceIP = MDC.get("sourceIP");

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("exceptionClass", ex.getClass().getName());
        additionalInfo.put("validationErrors", errors);

        StructuredLog.ApplicationMetadata appMeta = StructuredLog.ApplicationMetadata.builder()
                .name(appName)
                .version(appVersion)
                .env(appEnv)
                .kind(appKind)
                .build();

        StructuredLog.MeasurementMetadata measurementMeta = StructuredLog.MeasurementMetadata.builder()
                .method(request.getMethod() + " " + request.getRequestURI())
                .elapsedTime("N/A")
                .build();

        StructuredLog logData = StructuredLog.builder()
                .level("WARN")
                .logType("TRANSACTION")
                .sourceIP(sourceIP)
                .status("BAD_REQUEST")
                .message("Validation failed for request parameters")
                .logOrigin("INTERNAL")
                .timestamp(LocalDateTime.now().format(DATE_FORMATTER))
                .tracingId(tracingId)
                .hostname(hostname)
                .eventType("VALIDATION_ERROR")
                .application(appMeta)
                .measurement(measurementMeta)
                .additionalInfo(additionalInfo)
                .build();

        log.warn(logData.toJson());

        MetaResponseDTO meta = MetaResponseDTO.builder()
                .status("FAILURE")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("La petición contiene datos de entrada inválidos.")
                .devMessage("Validation error on request body fields.")
                .errorDetails(errors)
                .build();

        ApiResponseDTO response = ApiResponseDTO.builder()
                .meta(meta)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private String getFailureLocation(Throwable ex) {
        if (ex.getStackTrace() != null && ex.getStackTrace().length > 0) {
            for (StackTraceElement element : ex.getStackTrace()) {
                if (element.getClassName().startsWith("com.sigep")) {
                    return String.format("%s.%s(Line:%d)", 
                            element.getClassName(), 
                            element.getMethodName(), 
                            element.getLineNumber());
                }
            }
            StackTraceElement first = ex.getStackTrace()[0];
            return String.format("%s.%s(Line:%d)", 
                    first.getClassName(), 
                    first.getMethodName(), 
                    first.getLineNumber());
        }
        return "unknown-location";
    }
}
