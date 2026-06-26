package com.sigep.config;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sigep.dto.response.StructuredLog;
import com.sigep.utils.UUIDvs7;

import lombok.extern.slf4j.Slf4j;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class HttpLoggingFilter extends OncePerRequestFilter {

    private static final String TRACING_ID_KEY = "tracingId";
    private static final String SOURCE_IP_KEY = "sourceIP";
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

    public HttpLoggingFilter() {
        String host;
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            host = "unknown-host";
        }
        this.hostname = host;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        String tracingId = UUIDvs7.randomUUID();
        String sourceIP = getClientIp(request);

        // Registrar en el MDC para que esté disponible en todo el hilo de ejecución
        MDC.put(TRACING_ID_KEY, tracingId);
        MDC.put(SOURCE_IP_KEY, sourceIP);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            double seconds = duration / 1000.0;

            String controllerMethod = (String) request.getAttribute("controllerMethod");
            String logMethod = controllerMethod != null 
                    ? controllerMethod 
                    : request.getMethod() + " " + request.getRequestURI();

            @SuppressWarnings("unchecked")
            Map<String, Object> additionalInfo = (Map<String, Object>) request.getAttribute("additionalInfo");

            StructuredLog.ApplicationMetadata appMeta = StructuredLog.ApplicationMetadata.builder()
                    .name(appName)
                    .version(appVersion)
                    .env(appEnv)
                    .kind(appKind)
                    .build();

            StructuredLog.MeasurementMetadata measurementMeta = StructuredLog.MeasurementMetadata.builder()
                    .method(logMethod)
                    .elapsedTime(String.format("%.2fs", seconds))
                    .build();

            StructuredLog logData = StructuredLog.builder()
                    .level("INFO")
                    .logType("TRANSACTION")
                    .sourceIP(sourceIP)
                    .status(response.getStatus() >= 400 ? "ERROR" : "SUCCESS")
                    .message(response.getStatus() >= 400 ? "HTTP Error Status: " + response.getStatus() : "OK")
                    .logOrigin("INTERNAL")
                    .timestamp(LocalDateTime.now().format(DATE_FORMATTER))
                    .tracingId(tracingId)
                    .hostname(hostname)
                    .eventType("HTTP_TRANSACTION")
                    .application(appMeta)
                    .measurement(measurementMeta)
                    .additionalInfo(additionalInfo)
                    .build();

            log.info(logData.toJson());

            // Limpiar MDC para evitar fugas de memoria en el pool de hilos
            MDC.clear();
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}
