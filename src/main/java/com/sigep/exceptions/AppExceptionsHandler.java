package com.sigep.exceptions;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sigep.constants.AppConstants;
import com.sigep.dto.response.ApiResponseDTO;
import com.sigep.dto.response.MetaResponseDTO;
import com.sigep.dto.response.StructuredLog;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class AppExceptionsHandler {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String ADDITIONAL_INFO_EXCEPTION_CLASS = "exceptionClass";
    private static final String LOG_TYPE_TRANSACTION = "TRANSACTION";

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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        return buildBusinessErrorResponse(
                ex,
                request,
                HttpStatus.NOT_FOUND,
                "WARN",
                "NOT_FOUND",
                "RESOURCE_NOT_FOUND"
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponseDTO> handleDuplicateResourceException(
            DuplicateResourceException ex,
            HttpServletRequest request
    ) {
        return buildBusinessErrorResponse(
                ex,
                request,
                HttpStatus.CONFLICT,
                "WARN",
                "CONFLICT",
                "DUPLICATE_RESOURCE"
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponseDTO> handleBadRequestException(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        return buildBusinessErrorResponse(
                ex,
                request,
                HttpStatus.BAD_REQUEST,
                "WARN",
                AppConstants.BAD_REQUEST,
                "BUSINESS_VALIDATION_ERROR"
        );
    }

    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<ApiResponseDTO> handleNoContentException(
            NoContentException ex,
            HttpServletRequest request
    ) {
        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put(ADDITIONAL_INFO_EXCEPTION_CLASS, ex.getClass().getName());

        StructuredLog logData = buildStructuredLog(
                "INFO",
                LOG_TYPE_TRANSACTION,
                AppConstants.SUCCESS,
                ex.getMessage(),
                "NO_CONTENT_RESULT",
                request,
                additionalInfo
        );

        log.info(logData.toJson());

        MetaResponseDTO meta = MetaResponseDTO.builder()
                .status(AppConstants.SUCCESS)
                .statusCode(HttpStatus.NO_CONTENT.value())
                .message(ex.getMessage())
                .build();

        ApiResponseDTO response = ApiResponseDTO.builder()
                .meta(meta)
                .build();

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put(ADDITIONAL_INFO_EXCEPTION_CLASS, ex.getClass().getName());
        additionalInfo.put("validationErrors", errors);

        StructuredLog logData = buildStructuredLog(
                "WARN",
                LOG_TYPE_TRANSACTION,
                AppConstants.BAD_REQUEST,
                "Validation failed for request parameters",
                "VALIDATION_ERROR",
                request,
                additionalInfo
        );

        log.warn(logData.toJson());

        MetaResponseDTO meta = MetaResponseDTO.builder()
                .status(AppConstants.FAILURE)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("La peticion contiene datos de entrada invalidos.")
                .devMessage("Validation error on request body fields.")
                .errorDetails(errors)
                .build();

        ApiResponseDTO response = ApiResponseDTO.builder()
                .meta(meta)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO> handleAllExceptions(
            Exception ex,
            HttpServletRequest request
    ) {
        String failureLocation = getFailureLocation(ex);

        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put(ADDITIONAL_INFO_EXCEPTION_CLASS, ex.getClass().getName());
        additionalInfo.put("failureLocation", failureLocation);

        StructuredLog logData = buildStructuredLog(
                "ERROR",
                "EXCEPTION",
                AppConstants.FAILURE,
                ex.getMessage() != null ? ex.getMessage() : "Unexpected system error",
                "SYSTEM_EXCEPTION",
                request,
                additionalInfo
        );

        log.error(logData.toJson(), ex);

        MetaResponseDTO meta = MetaResponseDTO.builder()
                .status(AppConstants.FAILURE)
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

    private ResponseEntity<ApiResponseDTO> buildBusinessErrorResponse(
            RuntimeException ex,
            HttpServletRequest request,
            HttpStatus httpStatus,
            String logLevel,
            String metaStatus,
            String eventType
    ) {
        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put(ADDITIONAL_INFO_EXCEPTION_CLASS, ex.getClass().getName());

        StructuredLog logData = buildStructuredLog(
                logLevel,
                LOG_TYPE_TRANSACTION,
                metaStatus,
                ex.getMessage(),
                eventType,
                request,
                additionalInfo
        );

        log.warn(logData.toJson());

        MetaResponseDTO meta = MetaResponseDTO.builder()
                .status(metaStatus)
                .statusCode(httpStatus.value())
                .message(ex.getMessage())
                .build();

        ApiResponseDTO response = ApiResponseDTO.builder()
                .meta(meta)
                .build();

        return ResponseEntity.status(httpStatus).body(response);
    }

    private StructuredLog buildStructuredLog(
            String level,
            String logType,
            String status,
            String message,
            String eventType,
            HttpServletRequest request,
            Map<String, Object> additionalInfo
    ) {
        String tracingId = MDC.get("tracingId");
        String sourceIP = MDC.get("sourceIP");

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

        return StructuredLog.builder()
                .level(level)
                .logType(logType)
                .sourceIP(sourceIP)
                .status(status)
                .message(message)
                .logOrigin("INTERNAL")
                .timestamp(LocalDateTime.now(ZoneOffset.UTC).format(DATE_FORMATTER))
                .tracingId(tracingId)
                .hostname(hostname)
                .eventType(eventType)
                .application(appMeta)
                .measurement(measurementMeta)
                .additionalInfo(additionalInfo)
                .build();
    }

    private String getFailureLocation(Throwable ex) {
        if (ex.getStackTrace() != null && ex.getStackTrace().length > 0) {
            for (StackTraceElement element : ex.getStackTrace()) {
                if (element.getClassName().startsWith("com.sigep")) {
                    return String.format(
                            "%s.%s(Line:%d)",
                            element.getClassName(),
                            element.getMethodName(),
                            element.getLineNumber()
                    );
                }
            }
            StackTraceElement first = ex.getStackTrace()[0];
            return String.format(
                    "%s.%s(Line:%d)",
                    first.getClassName(),
                    first.getMethodName(),
                    first.getLineNumber()
            );
        }
        return "unknown-location";
    }
}
